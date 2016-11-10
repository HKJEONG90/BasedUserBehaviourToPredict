package com.logiclab.varlab.servicedemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "MusicService";

    Button start, stop, check;
    TextView checkView;

    boolean GPS_Flag = false;

    private final int MY_PERMISSION_REQUEST_STORAGE = 100;

    SharedArea sharedArea;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkView = (TextView)findViewById(R.id.checknum);

        sharedArea = new SharedArea();

        start = (Button)findViewById(R.id.startBtn);
        stop = (Button)findViewById(R.id.stopBtn);
        check = (Button)findViewById(R.id.checkBtn);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        check.setOnClickListener(this);

        checkPermission();
    }

    private void checkPermission()
    {
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
            {
                Log.v("[Activity Permission]", "ACCESS_FINE_LOCATION");
            }
            sharedArea.GPS_FLAG = false;

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_STORAGE);
        }
        else
        {
            sharedArea.GPS_FLAG = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch(requestCode)
        {
            case MY_PERMISSION_REQUEST_STORAGE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    sharedArea.GPS_FLAG = true;
                }
                else
                {
                    sharedArea.GPS_FLAG = false;
                }
                break;
        }
    }

    public void onClick(View src)
    {
        switch(src.getId())
        {
            case R.id.startBtn:
                startService(new Intent(this, MusicService.class));
                break;
            case R.id.stopBtn:
                stopService(new Intent(this, MusicService.class));
                break;
            case R.id.checkBtn:
                //checkView.setText(String.valueOf(sharedArea.num));
                Toast.makeText(this,"Checkingnow",Toast.LENGTH_LONG).show();
                Log.v("==MainActivity==","SharedData = " + String.valueOf(sharedArea.num));
                break;
        }
    }
}
