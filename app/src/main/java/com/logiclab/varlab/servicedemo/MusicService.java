package com.logiclab.varlab.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service
{
    private static final String TAG = "MusicService";

    Timer timer;
    int i=0;

    LocationManager locationManager;
    double latitude = 0;
    double longitude = 0;

    SharedArea sharedArea;

    public MusicService()
    {

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate()
    {
        sharedArea = new SharedArea();
        if(sharedArea.GPS_FLAG )
        {
            locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, locationListener);
        }
        Log.d(TAG,"onCreate()");
    }

    @Override
    public void onDestroy()
    {
        Toast.makeText(this, "서비스가 중지되었습니다.", Toast.LENGTH_LONG).show();
        Log.d(TAG,"onDestroy()");
        timer.cancel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG,"onStartCommand()");

        timer = new Timer();
        timer.schedule(adTask, 0, 10000);
        return super.onStartCommand(intent, flags, startId);
    }

    TimerTask adTask = new TimerTask()
    {
        @Override
        public void run()
        {
            i++;
            SharedArea sharedArea = new SharedArea();
            sharedArea.num = i;

            URLTask urlTask = new URLTask();
            Log.v("==Service==","Timer = " + String.valueOf(sharedArea.num));
            urlTask.execute(String.valueOf(sharedArea.num));
        }
    };

    public class URLTask extends AsyncTask<String, Void, Void>
    {
        private final String LOG_TAG = "URLTask";

        @Override
        protected Void doInBackground(String... params)
        {
            HttpURLConnection urlConnection = null;

            try
            {
                String day = null;
                String hour = null;

                Time dayTime = new Time();
                dayTime.setToNow();

                int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

                long dateTime;
                dateTime = dayTime.setJulianDay(julianStartDay);
                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE");
                day = shortenedDateFormat.format(dateTime);

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat hourDateFormat = new SimpleDateFormat("kk.mm");
                hour = hourDateFormat.format(date);

                if(sharedArea.GPS_FLAG)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, locationListener);
                final String BASE_URL = "http://192.168.205.16:52273/id="+String.valueOf(longitude)+"&time="+hour+"&day="+day+"&accel=1992";

                URL url = new URL(BASE_URL);

                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                urlConnection.getInputStream();
            }
            catch(IOException e)
            {
                Log.e(LOG_TAG,"Error",e);
                return null;
            }
            finally
            {
                if(urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }
    }
    LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle)
        {

        }

        @Override
        public void onProviderEnabled(String s)
        {

        }

        @Override
        public void onProviderDisabled(String s)
        {

        }
    };
}