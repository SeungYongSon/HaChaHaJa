package com.ahsibal.foolish.hachahaja;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

/**
 * Created by 10210손승용 on 2017-10-13.
 */

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        MyHostApduService.HceCheck = false;

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (nfcAdapter.isEnabled()) {
            if(mBluetoothAdapter != null) {
                if (mBluetoothAdapter.isEnabled()) {
                } else {
                    mBluetoothAdapter.enable();
                }
            }else{
                Toast.makeText(getApplicationContext(),"이 단말은 블루투스를 지원하지 않습니다!!!", Toast.LENGTH_SHORT).show();
                moveTaskToBack(true);
                finish();
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
            }
            setContentView(R.layout.choose_busstop);

            RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
            LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);

            /*
            *정류장 데이터를 받아와 리사이클뷰로 정류장 목록을 보여주는 부분이다. 하지만 현재 임의의 데이터이다.
            *추후 블루투스통신으로 버스가 가는 정류장 데이터를 받아와
            *리스트를 보여줄 계획이다.
            */
            List<Recycler_item> items=new ArrayList<>();
            Recycler_item[] item=new Recycler_item[5];
            item[0]=new Recycler_item("한국연구재단","43-460", 1, R.drawable.d);
            item[1]=new Recycler_item("대덕대학","43-430", 2, R.drawable.a);
            item[2]=new Recycler_item("중소기업청","43-390", 3, R.drawable.b);
            item[3]=new Recycler_item("한국기계연구원","43-370",4,  R.drawable.c);
            for(int i=0;i<4;i++) items.add(item[i]);
            recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(),items,R.layout.activity_main));

    }else{
            if(mBluetoothAdapter.isEnabled()) {}
            else{
                mBluetoothAdapter.enable();
            }
            setContentView(R.layout.psbc_setting);
        }
        MyHostApduService.HceCheck = false;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNFCSetup: {
                // NFC 어댑터가 null 이라면 칩이 존재하지 않는 것으로 간주
                // NFC 어댑터를 구한다
                NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
                if( mNfcAdapter == null ) {
                    Toast.makeText(getApplicationContext(), "이 단말은 NFC를 지원하지 안합니다. :(", Toast.LENGTH_SHORT).show();
                    moveTaskToBack(true);
                    finish();
                    int pid = android.os.Process.myPid();
                    android.os.Process.killProcess(pid);
                }else{
                    // NFC 환경설정 화면 호출
                    // 4.2.2 (API 17) 부터 NFC 설정 환경이 변경됨.
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                        startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                    }
                    else {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }
                break;
            }
        }
    }
}
