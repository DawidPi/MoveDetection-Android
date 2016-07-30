package com.projects.dawid.movedetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;

public class Settings extends Activity {

    private static final String TAG = "Settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.i(TAG, "Activity Settings created");

        Log.i(TAG, "Before restoringSavedSettings");
        restoreSavedSettings();
    }

    private void restoreSavedSettings() {

        SharedPreferences savedSettings = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsHandle = savedSettings.edit();

        Log.i(TAG, "SharedPreferences created in private mode");

        int sensitivity = savedSettings.getInt(getString(R.string.keySensitivity),
                getResources().getInteger(R.integer.defaultSensitivity));
        String phoneNumber = savedSettings.getString(getString(R.string.keyTelNum),
                getString(R.string.defaultPhoneNumber));

        Log.v(TAG, "Sensitivity read from Preferences: " + sensitivity);
        Log.v(TAG, "Phone number read from Preferences: " + phoneNumber);

        SeekBar sensitivityBar = (SeekBar) findViewById(R.id.SettingsSensitivity);
        sensitivityBar.setProgress(sensitivity);

        if(!phoneNumber.equals("0")) {
            Log.i(TAG, "No information about phone number read. Fallback to default");
            EditText telephoneTextView = (EditText) findViewById(R.id.SettingsTelephoneNumber);
            telephoneTextView.setText(phoneNumber, TextView.BufferType.EDITABLE);
        }

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
        preferencesSaver.commit();
        Log.i(TAG, "Telephone and sensitivity saved.");
    }

    private boolean validateSettings() {
        EditText telephoneTextView = (EditText) findViewById(R.id.SettingsTelephoneNumber);
        String telephoneNumber = telephoneTextView.getText().toString();
        PhoneNumberUtils phoneNumbersUtility = new PhoneNumberUtils();
        Log.v(TAG, "Number to check is: " + telephoneNumber);

        if (phoneNumbersUtility.isGlobalPhoneNumber(telephoneNumber))
            return true;
        else {
            Toast.makeText(this, getString(R.string.validationWrongNumber), Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
