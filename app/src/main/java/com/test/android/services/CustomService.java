package com.test.android.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import com.test.android.R;

/**
 * Created by Manish on 20/2/17.
 */

public class CustomService extends Service {

    private MediaPlayer mp;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_LONG).show();
        //setting up music player
        mp = MediaPlayer.create(getApplicationContext(), R.raw.sample_song);
        mp.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //relesing music player
        mp.stop();
        mp.release();
        Toast.makeText(getApplicationContext(), "Service Destroyed", Toast.LENGTH_LONG).show();

    }
}
