package com.projects.dawid.movedetector;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;

public class Settings extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "Settings";
    private boolean mIsContinuousEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.i(TAG, "Creating Settings activity");

        restoreSavedSettings();
        registerListener();

        setOnOffButtonState();
        saveButtonEnable(false);

    }

    private void registerListener() {
        TextView telephoneView = (TextView) findViewById(R.id.SettingsTelephoneNumber);
        SeekBar sensitivityBar = (SeekBar) findViewById(R.id.SettingsSensitivity);

        telephoneView.setOnClickListener(this);
        sensitivityBar.setOnSeekBarChangeListener(this);
    }

    private boolean isServiceActive(){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)){
            if(MotionSensorService.class.getName().equals(serviceInfo.service.getClassName())){
                Log.i(TAG, "Service is active");
                return true;
            }
        }

        Log.i(TAG, "Service is inactive");
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Settings resumed");
        setOnOffButtonState();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "Settings restarted");
        setOnOffButtonState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "Settings started");
        setOnOffButtonState();
    }

    private void setOnOffButtonState() {
        ToggleButton onOffButton = (ToggleButton) findViewById(R.id.SettingsEnableService);

        if(isServiceActive()){
            onOffButton.setChecked(true);
        }
        else onOffButton.setChecked(false);
    }

    private void restoreSavedSettings() {

        SharedPreferences savedSettings = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsHandle = savedSettings.edit();

        Log.i(TAG, "SharedPreferences created in private mode");

        int sensitivity = savedSettings.getInt(getString(R.string.keySensitivity),
                getResources().getInteger(R.integer.defaultSensitivity));
        String phoneNumber = savedSettings.getString(getString(R.string.keyTelNum),
                getString(R.string.defaultPhoneNumber));
        mIsContinuousEnabled = savedSettings.getBoolean(getString(R.string.keyContinuousMode), false);

        Log.v(TAG, "Sensitivity read from Preferences: " + sensitivity);
        Log.v(TAG, "Phone number read from Preferences: " + phoneNumber);

        SeekBar sensitivityBar = (SeekBar) findViewById(R.id.SettingsSensitivity);
        sensitivityBar.setProgress(sensitivity);

        if(!phoneNumber.equals("0")) {
            Log.i(TAG, "No information about phone number read. Fallback to default");
            EditText telephoneTextView = (EditText) findViewById(R.id.SettingsTelephoneNumber);
            telephoneTextView.setText(phoneNumber, TextView.BufferType.EDITABLE);
        }

        ToggleButton continuousModeButton = (ToggleButton) findViewById(R.id.ModeSetting);
        if(mIsContinuousEnabled) continuousModeButton.setChecked(true);
        else continuousModeButton.setChecked(false);

    }

    public void serviceToggle(View view) {
        ToggleButton onOffButton = (ToggleButton) view;

        if (onOffButton.isChecked()) {
            if(!validateSettings()){
                onOffButton.setChecked(false);
                Toast.makeText(this, getString(R.string.validationWrongNumber), Toast.LENGTH_LONG).show();
            }
            startService();
        } else {
            stopService();
        }
    }

    private void stopService() {
        Intent serviceIntent = new Intent(this, MotionSensorService.class);
        stopService(serviceIntent);
    }

    private void startService() {
        EditText telephoneTextView = (EditText) findViewById(R.id.SettingsTelephoneNumber);
        String telephoneNumber = telephoneTextView.getText().toString();
        SeekBar sensitivityBar = (SeekBar) findViewById(R.id.SettingsSensitivity);
        int progress = sensitivityBar.getProgress();


        Intent serviceIntent = new Intent(this, MotionSensorService.class);
        serviceIntent.putExtra(MotionSensorService.IN_TEL_NUM, telephoneNumber);
        serviceIntent.putExtra(MotionSensorService.IN_SENSITIVITY, progress);
        serviceIntent.putExtra(MotionSensorService.IN_CONTINUOUS, mIsContinuousEnabled);

        startService(serviceIntent);
    }

    public void saveSettings(View view) {

        if (validateSettings()) {
            Log.i(TAG, "Validation passed.");
            saveSettingsAction();
            Toast.makeText(this, getString(R.string.validationOK), Toast.LENGTH_LONG).show();
        }

    }

    private void saveSettingsAction() {
        EditText telephoneTextView = (EditText) findViewById(R.id.SettingsTelephoneNumber);
        String telephoneNumber = telephoneTextView.getText().toString();
        Log.v(TAG, "Telephone number to save: " + telephoneNumber);

        SeekBar sensitivityBar = (SeekBar) findViewById(R.id.SettingsSensitivity);
        int progress = sensitivityBar.getProgress();
        Log.v(TAG, "Sensitivity to save: " + progress);

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesSaver = preferences.edit();

        preferencesSaver.putString(getString(R.string.keyTelNum), telephoneNumber);
        preferencesSaver.putInt(getString(R.string.keySensitivity), progress);
        preferencesSaver.putBoolean(getString(R.string.keyContinuousMode), mIsContinuousEnabled);
        preferencesSaver.apply();
        Log.i(TAG, "Telephone and sensitivity saved.");
        saveButtonEnable(false);

    }

    private boolean validateSettings() {
        EditText telephoneTextView = (EditText) findViewById(R.id.SettingsTelephoneNumber);
        String telephoneNumber = telephoneTextView.getText().toString();
        Log.v(TAG, "Number to check is: " + telephoneNumber);

        if (PhoneNumberUtils.isGlobalPhoneNumber(telephoneNumber))
            return true;
        else {
            Toast.makeText(this, getString(R.string.validationWrongNumber), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onClick(View view) {
            saveButtonEnable(true);
    }

    private void saveButtonEnable(boolean state) {
        Button saveButton =  (Button) findViewById(R.id.ButtonSave);
        saveButton.setEnabled(state);
    }

    public void modeToggle(View view){
        ToggleButton modeToggle = (ToggleButton) view;

        if(modeToggle.isChecked())
            mIsContinuousEnabled = true;
        else
            mIsContinuousEnabled = false;

        saveButtonEnable(true);

        Log.v(TAG, "mode continuous changed to: " + mIsContinuousEnabled);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        saveButtonEnable(true);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
