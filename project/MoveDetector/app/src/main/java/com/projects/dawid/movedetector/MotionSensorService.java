package com.projects.dawid.movedetector;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MotionSensorService extends IntentService {

    private static final String TAG = "MotionService";

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.projects.dawid.movedetector.action.FOO";
    private static final String ACTION_BAZ = "com.projects.dawid.movedetector.action.BAZ";

    // TODO: Rename parameters
    public static final String IN_TEL_NUM = "com.projects.dawid.movedetector.extra.IN_TEL_NUM";
    public static final String IN_SENSITIVITY = "com.projects.dawid.movedetector.extra.IN_SENSITIVITY";

    public MotionSensorService() {
        super("MotionSensorService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MotionSensorService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(IN_TEL_NUM, param1);
        intent.putExtra(IN_SENSITIVITY, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MotionSensorService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(IN_TEL_NUM, param1);
        intent.putExtra(IN_SENSITIVITY, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String telephoneNumber = intent.getStringExtra(IN_TEL_NUM);
            int sensitivity = intent.getIntExtra(IN_SENSITIVITY, 0);

            Log.v(TAG, "Telephone number: " + telephoneNumber);
            Log.v(TAG, "Sensitivity: " + sensitivity);
            Toast.makeText(this, "Service successfully started", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
