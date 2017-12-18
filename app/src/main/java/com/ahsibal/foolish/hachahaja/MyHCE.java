package com.ahsibal.foolish.hachahaja;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 10210손승용 on 2017-10-14.
 */

public class MyHCE extends AppCompatActivity {
    String BusStopName, BusStopContent, myNumber;
    static String FullInfo;
    int BusStopNum;

    @Override
    protected void onStart() {
        super.onStart();
        MyHostApduService.HceCheck = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stop_bus);

        Intent intent = getIntent() ;
        BusStopNum = intent.getIntExtra("BusStop_num", 0) ;
        BusStopName =intent.getStringExtra("BusStop_name") ;
        BusStopContent =intent.getStringExtra("BusStop_content") ;
        TextView BusStopL = (TextView) findViewById(R.id.BusStopL);
        myNumber = getNumber();

        FullInfo = BusStopNum + "," + myNumber;
        BusStopL.setText("목적지 - " + BusStopName + " , " + BusStopContent + " , " + BusStopNum + " , " + myNumber);
        //BusStopL.setText("목적지 -  " + BusStopName + "  ,  " + BusStopContent);

        MyHostApduService.HceCheck = true;
    }

    private long pressedTime;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if ( pressedTime == 0 ) {
            Toast.makeText(getApplicationContext(), "한번 더 누르면 현재 작업이 취소됩니다", Toast.LENGTH_LONG).show();
            pressedTime = System.currentTimeMillis();
        }
        else {
            int seconds = (int) (System.currentTimeMillis() - pressedTime);

            if ( seconds > 2000 ) {
                pressedTime = 0;
            }
            else {
                finish();
            }
        }
    }

    public String getNumber()
    {
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView: {
                Intent intent2 = new Intent(getApplicationContext(),ConnectBusStop.class);
                intent2.putExtra("BusStop_num", BusStopNum) ;
                intent2.putExtra("BusStop_name", BusStopName) ;
                intent2.putExtra("BusStop_content", BusStopContent) ;
                intent2.putExtra("BusStop_userid", myNumber) ;
                startActivity(intent2);
                break;
            }
        }
    }
}
