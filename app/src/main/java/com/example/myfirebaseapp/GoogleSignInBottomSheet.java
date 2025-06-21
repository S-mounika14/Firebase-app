package com.example.myfirebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleSignInBottomSheet extends BottomSheetDialogFragment {
    private FirebaseAuth mAuth;//Links the Google account to Firebase.
    private GoogleSignInClient mGoogleSignInClient;//Handles Google Sign-In.
    private static final int RC_SIGN_IN = 9001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_google_sign_in, container, false);

        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign-In to always prompt for account selection
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        Button googleSignInButton = view.findViewById(R.id.googleSignInConfirmButton);
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        return view;
    }

    private void signInWithGoogle() {
        // Sign out to clear the previously selected account
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener(account -> {
                        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null))
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        dismiss();
                                        PermissionsBottomSheet bottomSheet = new PermissionsBottomSheet();
                                        bottomSheet.show(requireActivity().getSupportFragmentManager(), bottomSheet.getTag());
                                    } else {
                                        Toast.makeText(requireContext(), "Google Sign-In failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}