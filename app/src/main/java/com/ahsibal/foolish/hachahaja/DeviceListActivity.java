package com.ahsibal.foolish.hachahaja;
// DeviceListActivity.java

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 *
 * 디바이스 페어링은 정상적으로 되지만 디바이스 검색에 문제가 있다.
 * 문제 원인은 디바이스 보안 때문인 거 같다.
 * 그래서 블루투스 설정에서 먼저 디바이스와 디바이스끼리 처음으로 페어링을 시키고 앱에서 다시 패어링 시켜줘야한다.
 * 추후 디바이스 검색 불가 문제를 해결할 것이다.
 */

public class DeviceListActivity extends Activity {
    //디버깅
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;
    public static final int MESSAGE_DEVICE_NAME = 4;
    //연결할 장치의 MAC 어드레스
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    //블루투스 아답타
    private BluetoothAdapter mBtAdapter;
    //페어링된 기기
    private ArrayAdapter mPairedDevicesArrayAdapter;
    //새로 발견한 기기
    private ArrayAdapter mNewDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //윈도우 셋업
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);

        // Set result  사용자가 나갔을때
        setResult(Activity.RESULT_CANCELED);

        // device discovery 를 하기위한 버튼
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //device discovery 메소드
                doDiscovery();
                //버튼을 보이지 않게 한다.
                v.setVisibility(View.GONE);
            }
        });

        //페어링된 디바이스를 표시할 ArrayAdapter 객체생성하기.
        mPairedDevicesArrayAdapter = new ArrayAdapter(this, R.layout.device_name);
        //새로 발견된 디바이스를 표시할 ArrayAdapter 객체 생성하기.
        mNewDevicesArrayAdapter = new ArrayAdapter(this, R.layout.device_name);

        //페어링된 장비를 출력할 리스트 뷰
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        //새로 찾은 장비를 출력할 리스트뷰
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        //디바이스가 Discover 되었을때 방송을 수신할 방송수신자 등록하기.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        //디바이스 Discovering 이 끝났을때 방송을 수신할 방송수신자 등록하기.
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        //블루투스 아답타 객체 얻어오기.
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        //이미 페어링된 디바이스를 얻어온다.
        Set <BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        //페어링된 디바이스가 존재한다면 출력하기위해  ArrayAdapter 에 추가한다.
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            //페어링된 디바이스가 없다면  없다고 출력하기 위해서
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Discovering 작업을 취소한다.
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        //방송수신자를 등록해제한다.
        this.unregisterReceiver(mReceiver);
    }

    //디바이스 Discovering 를 하는 메소드
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()"); //디버깅 하기 위해서.

        //제목에 스케닝 상태 출력하기.
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        //이미 Discovering 하고 있었다면 취소한다.
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        //Discovering 시작하기.
        mBtAdapter.startDiscovery();
    }

    //리스트뷰에 등록할 아이템 클릭 리스너 객체.
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            //연결하기 위해서 현재 Discovering 을 취소한다.
            mBtAdapter.cancelDiscovery();

            //장비의 MAC 어드레스를 얻어온다. (마지막 17 글짜이다.)
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            //인텐트 객체를 생성하고 연결할 장치의 MAC 어드레스정보를 넣어준다.
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            //Request 코드를 등록 지정하고 인텐트를 전달한다.
            setResult(Activity.RESULT_OK, intent);

            finish();//액티비티를 종료한다.
        }
    };


    //방송 수신자
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //어떤 방송이 수신되었는지 알아온다.
            String action = intent.getAction();

            //Discovering 결과 장비를 찾았을때
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                //BluetoothDevice 객체를 인텐트로 부터 얻어온다.
                BluetoothDevice device =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //이미 페어링된 장비라면 무시한다.
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    //새로운 장비라면 출력하기 위해서.
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                //Discovering 이 끝났다면 타이틀을 바꿔준다.
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //프로그래스 바를 정지 하기 위해서.
                setProgressBarIndeterminateVisibility(false);
                //제목도 바꿔준다.
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    //String noDevices = getResources().getText(R.string.none_found).toString();
                    String noDevices = "기기를 찾지 못했습니다!";
                    mNewDevicesArrayAdapter.add(noDevices);
                    Button scanButton = (Button) findViewById(R.id.button_scan);
                    scanButton.setVisibility(View.VISIBLE);
                }
            }
        }
    };

}