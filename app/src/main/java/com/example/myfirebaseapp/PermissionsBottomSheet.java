package com.example.myfirebaseapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PermissionsBottomSheet extends BottomSheetDialogFragment {
    private static final int REQUEST_CALL_LOG = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_permissions, container, false);

        TextView prompt = view.findViewById(R.id.permissionPrompt);
        Button accessibilityButton = view.findViewById(R.id.accessibilityButton);
        Button callLogButton = view.findViewById(R.id.callLogButton);
        Button continueButton = view.findViewById(R.id.continueButton);

        prompt.setText("Enable Accessibility Service\nand Call Log Permission\n");
        prompt.setGravity(Gravity.CENTER);
        prompt.setTextSize(20);
        prompt.setLineSpacing(0, 1f);
        prompt.setTypeface(null, Typeface.BOLD);


        accessibilityButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);//Opens accessibility settings.
            startActivity(intent);
        });

        callLogButton.setOnClickListener(v -> {
            ActivityCompat.requestPermissions(requireActivity(),//Requests call log permission.
                    new String[]{android.Manifest.permission.READ_CALL_LOG},
                    REQUEST_CALL_LOG);
        });

        continueButton.setOnClickListener(v -> {
            if (isAccessibilityServiceEnabled() && isCallLogPermissionGranted()) {
                dismiss();
                startActivity(new Intent(requireContext(), DashboardActivity.class));
            } else {
                Toast.makeText(requireContext(), "Please enable both permissions", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private boolean isAccessibilityServiceEnabled() {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(requireContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return accessibilityEnabled == 1;
    }

    private boolean isCallLogPermissionGranted() {
        return ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_LOG && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Call Log permission granted", Toast.LENGTH_SHORT).show();
        }
    }
}