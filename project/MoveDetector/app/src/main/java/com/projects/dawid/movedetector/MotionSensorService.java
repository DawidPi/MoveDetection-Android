package com.projects.dawid.movedetector;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class MotionSensorService extends Service implements SensorEventListener {

    private static final String TAG = "MotionService";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    public static final String IN_TEL_NUM = "com.projects.dawid.movedetector.IN_TEL_NUM";
    public static final String IN_SENSITIVITY = "com.projects.dawid.movedetector.IN_SENSITIVITY";
    public static final String IN_CONTINUOUS = "com.projects.dawid.movedetector.IN_CONTINUOUS";

    private String mTelephoneNumber;
    private int mSensitivity;
    private boolean mOneshotMode;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getText(R.string.NotificationTitle))
                .setContentText(getText(R.string.NotificationText))
                .setOngoing(true)
                .build();

        mTelephoneNumber = intent.getStringExtra(IN_TEL_NUM);
        mSensitivity = intent.getIntExtra(IN_SENSITIVITY, 0);
        mOneshotMode = !intent.getBooleanExtra(IN_CONTINUOUS, false);

        if (mSensorManager == null) {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        Intent notificationIntent = new Intent(this, Settings.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        startForeground(1234, notification);

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double xAcc = Math.abs(sensorEvent.values[0]);
        double yAcc = Math.abs(sensorEvent.values[1]);
        double zAcc = Math.abs(sensorEvent.values[2]);

        double acceleration = Math.sqrt(xAcc * xAcc + yAcc * yAcc + zAcc * zAcc);
        int compareValue = 10 - ((mSensitivity / 10) -1);

        if (acceleration >= compareValue) {
            makeACall();

            if(mOneshotMode)
                stopSelf();
        }

    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    private void makeACall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        callIntent.setData(Uri.parse("tel:" + mTelephoneNumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.ErrorCallPriviledges, Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(callIntent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.v(TAG, "newAccuracy: " +i);
    }
}
