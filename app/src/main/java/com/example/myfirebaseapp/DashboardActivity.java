package com.example.myfirebaseapp;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    private TextView incomingText, outgoingText, missedText, scrollText, tapText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        incomingText = findViewById(R.id.incomingText);
        outgoingText = findViewById(R.id.outgoingText);
        missedText = findViewById(R.id.missedText);
        scrollText = findViewById(R.id.scrollText);
        tapText = findViewById(R.id.tapText);
        FloatingActionButton refreshFab = findViewById(R.id.refreshFab);

        refreshFab.setOnClickListener(v -> refreshData());
        refreshData();
    }

    private void refreshData() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int incoming = 0, outgoing = 0, missed = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                switch (type) {
                    case CallLog.Calls.INCOMING_TYPE: incoming++; break;
                    case CallLog.Calls.OUTGOING_TYPE: outgoing++; break;
                    case CallLog.Calls.MISSED_TYPE: missed++; break;
                }
            }
            cursor.close();
        }
        incomingText.setText(String.valueOf(incoming));
        outgoingText.setText(String.valueOf(outgoing));
        missedText.setText(String.valueOf(missed));

        int scrollCount = AccessibilityServiceTracker.getScrollCount(this);
        int tapCount = AccessibilityServiceTracker.getTapCount(this);

        scrollText.setText(String.valueOf(scrollCount));
        tapText.setText(String.valueOf(tapCount));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> log = new HashMap<>();
        log.put("timestamp", System.currentTimeMillis());
        log.put("scrollCount", scrollCount);
        log.put("tapCount", tapCount);
        Map<String, Integer> callLogStats = new HashMap<>();
        callLogStats.put("incoming", incoming);
        callLogStats.put("outgoing", outgoing);
        callLogStats.put("missed", missed);
        log.put("callLogStats", callLogStats);
        log.put("userId", uid);
        db.collection("users")
                .document(uid)
                .collection("activityLogs")
                .add(log)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving to Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}