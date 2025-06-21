package com.example.myfirebaseapp;

import android.accessibilityservice.AccessibilityService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.content.ContextCompat;

public class AccessibilityServiceTracker extends AccessibilityService {
    private static final String PREFS_NAME = "TrackerPrefs";
    private static final String KEY_SCROLL_COUNT = "scrollCount";
    private static final String KEY_TAP_COUNT = "tapCount";
    private int scrollCount = 0;
    private int tapCount = 0;
    private CallLogObserver callLogObserver;
    private boolean isObserverRegistered = false;
    private Handler permissionCheckHandler;
    private final long PERMISSION_CHECK_INTERVAL = 5000; // Check every 5 seconds

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        scrollCount = prefs.getInt(KEY_SCROLL_COUNT, 0);
        tapCount = prefs.getInt(KEY_TAP_COUNT, 0);

        // Set up the CallLogObserver
        callLogObserver = new CallLogObserver(new Handler(Looper.getMainLooper()));

        // Check if we have permission to register the ContentObserver
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED) {
            getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, callLogObserver);
            isObserverRegistered = true;
        } else {
            // Start periodically checking for permission
            startPermissionCheck();
        }

        // Initial call to saveActivityLog to ensure counts are updated on service start
        saveActivityLog();
    }

    private void startPermissionCheck() {
        permissionCheckHandler = new Handler(Looper.getMainLooper());
        permissionCheckHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isObserverRegistered && ContextCompat.checkSelfPermission(
                        AccessibilityServiceTracker.this, android.Manifest.permission.READ_CALL_LOG)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, register the ContentObserver
                    getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, callLogObserver);
                    isObserverRegistered = true;
                    saveActivityLog(); // Update Call Logs now that we have permission
                } else if (!isObserverRegistered) {
                    // Permission still not granted, check again after delay
                    permissionCheckHandler.postDelayed(this, PERMISSION_CHECK_INTERVAL);
                }
            }
        }, PERMISSION_CHECK_INTERVAL);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            scrollCount++;
            saveCounts();
            saveActivityLog();
        } else if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            tapCount++;
            saveCounts();
            saveActivityLog();
        }
    }

    private void saveCounts() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SCROLL_COUNT, scrollCount);
        editor.putInt(KEY_TAP_COUNT, tapCount);
        editor.apply();
    }

    private void saveActivityLog() {
        int incoming = 0, outgoing = 0, missed = 0;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED) {

            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = null;

            try {
                cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                        switch (type) {
                            case CallLog.Calls.INCOMING_TYPE:
                                incoming++;
                                break;
                            case CallLog.Calls.OUTGOING_TYPE:
                                outgoing++;
                                break;
                            case CallLog.Calls.MISSED_TYPE:
                                missed++;
                                break;
                        }
                    }
                }
            } catch (SecurityException e) {
                android.util.Log.e("AccessibilityTracker", "Call log permission denied", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            android.util.Log.w("AccessibilityTracker", "Call log permission not granted, skipping call log data");
        }

        FirebaseHelper.getInstance().saveActivityLog(getApplicationContext(), scrollCount, tapCount, incoming, outgoing, missed);
    }

    @Override
    public void onInterrupt() {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the permission check loop
        if (permissionCheckHandler != null) {
            permissionCheckHandler.removeCallbacksAndMessages(null);
        }
        // Unregister the ContentObserver when the service is destroyed
        if (isObserverRegistered && callLogObserver != null) {
            getContentResolver().unregisterContentObserver(callLogObserver);
        }
    }

    public static int getScrollCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_SCROLL_COUNT, 0);
    }

    public static int getTapCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_TAP_COUNT, 0);
    }

    // ContentObserver to watch for changes in Call Logs
    private class CallLogObserver extends ContentObserver {
        public CallLogObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            // When the Call Log changes (e.g., a new call is received), update the counts
            saveActivityLog();
        }
    }
}


