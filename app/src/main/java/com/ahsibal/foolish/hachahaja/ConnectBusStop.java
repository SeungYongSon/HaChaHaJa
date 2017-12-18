package com.ahsibal.foolish.hachahaja;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by 10210손승용 on 2017-10-15.
 */

public class ConnectBusStop extends AppCompatActivity {
    // Debugging
    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;

    private boolean SucessBus = false;
    private boolean ConnetedSusc = false;

    String BusStopContent, myNumber;
    static public String BusStopName;
    static public int BusStopNum;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    //블루투스 Service 에서 핸들러에 보내온 메세지의 종류
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBluetoothAdapter;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    public static Context context;

    ImageView imageview1 ,imageview2;
    TextView suc, con, war;

    @Override
    protected void onStart() {
        super.onStart();
        MyHostApduService.HceCheck = false;
        if(!mBluetoothAdapter.isEnabled()){
            //Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

        }
        else{
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_bus);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Intent intent = getIntent() ;
        BusStopNum = intent.getIntExtra("BusStop_num", 0) ;
        BusStopName =intent.getStringExtra("BusStop_name") ;
        BusStopContent =intent.getStringExtra("BusStop_content") ;
        myNumber = intent.getStringExtra("BusStop_userid");
        //TextView BusStopL = (TextView) findViewById(R.id.BusStopL);
        //BusStopL.setText("목적지 - " + BusStopName + " , " + BusStopContent + " , " + BusStopNum + " , " + myNumber);

        context = getApplicationContext();

        suc = (TextView) findViewById(R.id.FailorSucess);
        con = (TextView) findViewById(R.id.Wwwcontents);
        war = (TextView) findViewById(R.id.war);

        imageview1 = (ImageView)findViewById(R.id.Blue);
        imageview2 = (ImageView)findViewById(R.id.Suceess);
        imageview1.setVisibility(VISIBLE);
        imageview2.setVisibility(INVISIBLE);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Blue: {
                if(!ConnetedSusc) {
                    Intent serverIntent = new Intent(this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                    //BluetoothChatService.showNotification("PSBC", "일어나세요!! " + ConnectBusStop.BusStopName + "에 거의 도착했습니다.");
                }else{
                    //sendMessage(BusStopNum + " , " + myNumber);
                }
                break;
            }
        }
    }

    private void setupChat() {
        mChatService = new BluetoothChatService(this, mHandler);
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        //onPause 상태에서 사용자가 블루투스 켜기를 승인했다면 다시 onResume() 메소드가 호출된다.
        if (mChatService != null) {
            Log.e("#####","mChatService != null");
            //처음 시작했다면 상태가 STATE_NONE 이다.
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {

                Log.e("#####","mChatService.start()");
                //블루투스 쳇팅서비스 시작하는 메소드
                mChatService.start();
            }
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(D) Log.d(TAG, "onActivityResult " + resultCode + " requestCode " + requestCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "연결 대기중...",
                            Toast.LENGTH_SHORT).show();
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE: //메세지 서비스의 상태가 바뀌었을때
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        //연결된 상태
                        case BluetoothChatService.STATE_CONNECTED:
                            //Toast.makeText(getApplicationContext(), "연결되었습니다!!",
                            //        Toast.LENGTH_SHORT).show();
                            break;
                        //연결을 시도하고 있는 상태
                        case BluetoothChatService.STATE_CONNECTING:
                            Toast.makeText(getApplicationContext(), "연결중....",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            //아무런 상태도 아닐때
                        case BluetoothChatService.STATE_NONE:
                            break;
                    }
                    break;
                //메세지를 전송하는 상태
                case MESSAGE_WRITE:
                    //전달된 문자열을 byte 배열
                    byte[] writeBuf = (byte[]) msg.obj;
                    //바이트 배열을 이용해서 String 객체를 생성한후
                    String writeMessage = new String(writeBuf);
                    //대화창에 자신이 보낸 메세지도 표시를 해준다.
                    break;
                //원격 디바이스에서 전송한 메세지를 읽어오는 상태
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    //읽어온 메세지를 화면에 출력하기 위해서 String 으로 변환한다.
                    /*
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Intent intent = new Intent(getApplicationContext(), GoodByeBusStop.class);;
                    startActivity(intent);
                    SucessBus = true;*/
                    break;
                //연결한 장치명을 표시한다.
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), mConnectedDeviceName + " 에 연결 되었습니다!", Toast.LENGTH_SHORT).show();
                    imageview1.setVisibility(INVISIBLE);
                    imageview2.setVisibility(VISIBLE);
                    suc.setText("성공!");
                    con.setText("이제 내리실 정류장 바로 전 정류장에서\n 깨워주겠습니다. 이앱을 나가셔도 좋습니다. :)");
                    war.setVisibility(VISIBLE);
                    ConnetedSusc = true;
                    sendMessage(BusStopNum + "," + myNumber);
                    break;
                //토스트 메세지를 띄우기 위해서.
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        //메세지 보내기.
        private void sendMessage(String message) {
            //서비스의 상태가 연결 상태가 아니라면
            if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
                //연결되지 않았다는 메세지를 띄우고
                Toast.makeText(context, "블루투스 연결이 안됨!  " + mChatService.getState(), Toast.LENGTH_SHORT).show();
                //메소드를 끝낸다.
                return;
            }else {
                //메세지를 byte[] 형태로 얻어온다.
                byte[] send = message.getBytes();
                //서비스 객체를 이용해서 전송한다.
                mChatService.write(send);
                Toast.makeText(context, "블루투스 연결이 됨! " + send, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private long pressedTime;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(ConnetedSusc) {
            if (pressedTime == 0) {
                Toast.makeText(getApplicationContext(), "한번 더 누르면 앱에서 나가집니다.", Toast.LENGTH_LONG).show();
                pressedTime = System.currentTimeMillis();
            } else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);

                if (seconds > 2000) {
                    pressedTime = 0;
                } else {
                    if (!SucessBus) {
                        moveTaskToBack(true);
                    } else {
                        /*
                        moveTaskToBack(true);
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());*/
                    }
                }
            }
        }
    }
}
