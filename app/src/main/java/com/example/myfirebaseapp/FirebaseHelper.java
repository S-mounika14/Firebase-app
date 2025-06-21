package com.example.myfirebaseapp;

import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {//This is a utility class that handles saving data to Firebase Realtime Database.
    private static FirebaseHelper instance;
    private DatabaseReference mDatabase;

    private FirebaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public void saveActivityLog(Context context, int scrollCount, int tapCount, int incoming, int outgoing, int missed) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference logRef = mDatabase.child("users").child(userId).child("activityLogs").push();
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", System.currentTimeMillis());
        data.put("scrollCount", scrollCount);
        data.put("tapCount", tapCount);
        Map<String, Integer> callLogStats = new HashMap<>();
        callLogStats.put("incoming", incoming);
        callLogStats.put("outgoing", outgoing);
        callLogStats.put("missed", missed);
        data.put("callLogStats", callLogStats);
        data.put("userId", userId);
        logRef.setValue(data);
    }
}