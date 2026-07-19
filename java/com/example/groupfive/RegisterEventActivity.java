package com.example.groupfive;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.groupfive.helpers.FirestoreHelper;

import java.util.HashMap;
import java.util.Map;

public class RegisterEventActivity extends AppCompatActivity {

    private TextView tvEventNameHeader;
    private EditText etFullName;
    private EditText etStudentId;
    private EditText etMajor;
    private EditText etPhoneNumber;
    private EditText etEmail;
    private Button btnSubmitRegistration;
    private ProgressBar progressBar;

    private String eventId;
    private String eventTitle;
    private FirestoreHelper firestoreHelper;

    private static final String TAG = "EventDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);

        initializeViews();
        loadEventInfo();
        setupSubmitButton();

        firestoreHelper = new FirestoreHelper();
    }

    private void initializeViews() {
        tvEventNameHeader = findViewById(R.id.tvEventNameHeader);
        etFullName = findViewById(R.id.etFullName);
        etStudentId = findViewById(R.id.etStudentId);
        etMajor = findViewById(R.id.etMajor);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmail = findViewById(R.id.etEmail);
        btnSubmitRegistration = findViewById(R.id.btnSubmitRegistration);
        progressBar = findViewById(R.id.progressBar);
    }


    private void loadEventInfo() {
        Intent intent = getIntent();

        if (intent != null) {
            // ✅ PERBAIKAN: Gunakan kunci yang sama dengan yang dikirim
            eventId = intent.getStringExtra("eventId");       // Ganti dari "EVENT_ID"
            eventTitle = intent.getStringExtra("title");      // Ganti dari "EVENT_TITLE"

            if (eventTitle != null && !eventTitle.isEmpty()) {
                tvEventNameHeader.setText("Register for: " + eventTitle);
            }
        }
    }

    private void setupSubmitButton() {
        btnSubmitRegistration.setOnClickListener(v -> {
            if (validateForm()) {
                submitRegistration();
            }
        });
    }

    private boolean validateForm() {
        String name = etFullName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String major = etMajor.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Check if all fields are filled
        if (TextUtils.isEmpty(name)) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(studentId)) {
            etStudentId.setError("Student ID is required");
            etStudentId.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(major)) {
            etMajor.setError("Major is required");
            etMajor.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhoneNumber.setError("Phone number is required");
            etPhoneNumber.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        return true;
    }

    private void submitRegistration() {
        // Show progress bar and disable button
        showLoading(true);

        // Get form data
        String name = etFullName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String major = etMajor.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Create registration data map
        Map<String, Object> registrationData = new HashMap<>();
        registrationData.put("eventId", eventId);
        registrationData.put("name", name);
        registrationData.put("studentId", studentId);
        registrationData.put("major", major);
        registrationData.put("phone", phone);
        registrationData.put("email", email);

        // Save to Firestore
        firestoreHelper.saveRegistration(registrationData, new FirestoreHelper.FirestoreCallback() {
            @Override
            public void onSuccess(String message) {
                showLoading(false);
                Toast.makeText(RegisterEventActivity.this,
                        "Registration successful!", Toast.LENGTH_LONG).show();

                // Clear form
                clearForm();

                // Go back to previous activity
                finish();
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                Toast.makeText(RegisterEventActivity.this,
                        "Registration failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSubmitRegistration.setEnabled(false);
            btnSubmitRegistration.setAlpha(0.5f);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSubmitRegistration.setEnabled(true);
            btnSubmitRegistration.setAlpha(1.0f);
        }
    }

    private void clearForm() {
        etFullName.setText("");
        etStudentId.setText("");
        etMajor.setText("");
        etPhoneNumber.setText("");
        etEmail.setText("");
    }
}