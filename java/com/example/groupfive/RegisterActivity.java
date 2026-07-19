package com.example.groupfive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword, etConfirm;
    Button btnRegister;
    TextView tvLogin;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> register());

        tvLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
    }

    private void register() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirm = etConfirm.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user == null) return;

                    Map<String, Object> data = new HashMap<>();
                    data.put("uid", user.getUid());
                    data.put("name", name);
                    data.put("email", email);
                    data.put("phone", ""); // Default phone number

                    db.collection("users")
                            .document(user.getUid())
                            .set(data)
                            .addOnSuccessListener(unused -> {
                                // === INI PERBAIKANNYA ===
                                Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                                // Langsung arahkan ke HomeActivity, bukan LoginActivity
                                Intent intent = new Intent(this, HomeActivity.class);
                                // Hapus semua activity sebelumnya agar pengguna tidak bisa kembali ke halaman login/register
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Registrasi Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
