package com.example.groupfive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPass;
    private Button btnLogin, btnRegister;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPass.getText().toString().trim();
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Lengkapi data", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(authResult -> {
                        Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Gagal login: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPass.getText().toString().trim();
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Lengkapi data", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(authResult -> {
                        Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
