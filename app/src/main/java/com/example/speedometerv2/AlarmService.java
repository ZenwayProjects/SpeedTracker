package com.example.speedometerv2;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;

public class AlarmService extends Service {
    private MediaPlayer player;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
        player.setLooping(true);
        player.start();
        //LEVANTAR EL SERVICIO Y QUE ESCUCHE LA VELOCIDAD Y NO PARARLO
        //SEPARAR EL SERVICIO Y LA FUNCIONALIDAD
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        player.stop();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
