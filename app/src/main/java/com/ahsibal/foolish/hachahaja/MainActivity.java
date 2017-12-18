package com.ahsibal.foolish.hachahaja;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        MyHostApduService.HceCheck = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button StBtn = (Button) findViewById(R.id.button);
        StBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class );
                startActivity(intent);
            }
        });
        MyHostApduService.HceCheck = false;
    }
    private long pressedTime;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if ( pressedTime == 0 ) {
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료됩니다", Toast.LENGTH_LONG).show();
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
}
