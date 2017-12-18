package com.ahsibal.foolish.hachahaja;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by 10210손승용 on 2017-11-10.
 */

public class GoodByeBusStop extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        MyHostApduService.HceCheck = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_bus);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Blue: {
                Uri uri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
                if (ringtone != null) {
                    ringtone.stop();
                    /*
                    moveTaskToBack(true);
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    /*
                    ActivityManager am  = (ActivityManager)getSystemService(Activity.ACTIVITY_SERVICE);
                    am.killBackgroundProcesses(getPackageName());*/
                    moveTaskToBack(true);
                    finish();
                    int pid = android.os.Process.myPid();
                    android.os.Process.killProcess(pid);
                }
                break;
            }
        }
    }
}
