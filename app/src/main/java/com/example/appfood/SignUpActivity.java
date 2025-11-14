package com.example.appfood;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText fullNameEt, emailEt, passwordEt, confirmPasswordEt;
    private Button createAccountButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase
        mAuth = FirebaseAuth.getInstance();

        // Views
        fullNameEt = findViewById(R.id.fullNameEditText);
        emailEt = findViewById(R.id.emailSignupEditText);
        passwordEt = findViewById(R.id.passwordSignupEditText);
        confirmPasswordEt = findViewById(R.id.confirmPasswordEditText);
        createAccountButton = findViewById(R.id.createAccountButton);
        progressBar = findViewById(R.id.progressBar);

        TextView backToSignIn = findViewById(R.id.backToSignIn);
        backToSignIn.setOnClickListener(v -> finish());

        createAccountButton.setOnClickListener(v -> attemptSignUp());
    }

    private void attemptSignUp() {
        String name = fullNameEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString();
        String confirm = confirmPasswordEt.getText().toString();

        // Basic validation
        if (TextUtils.isEmpty(name)) {
            fullNameEt.setError("Enter your full name");
            fullNameEt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Enter a valid email");
            emailEt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordEt.setError("Password must be at least 6 characters");
            passwordEt.requestFocus();
            return;
        }
        if (!password.equals(confirm)) {
            confirmPasswordEt.setError("Passwords do not match");
            confirmPasswordEt.requestFocus();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        createAccountButton.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    createAccountButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        // Sign up success
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(SignUpActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                        // Optionally update profile with display name
                        // Navigate to ThirdActivity (home)
                        Intent intent = new Intent(SignUpActivity.this, ThirdActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign up fails, display a message to the user.
                        String message = task.getException() != null ? task.getException().getMessage() : "Sign up failed";
                        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
