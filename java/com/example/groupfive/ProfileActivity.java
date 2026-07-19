package com.example.groupfive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.groupfive.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebase();
        if (currentUser == null) return;

        setupJurusanDropdown();
        loadUserData();
        setupListeners();
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Sesi tidak valid, silakan login kembali.", Toast.LENGTH_SHORT).show();
            goToLogin();
            finish();
        }
    }

    private void setupJurusanDropdown() {
        String[] jurusans = new String[]{"Teknik Informatika", "Sistem Informasi", "Desain Komunikasi Visual", "Manajemen Bisnis"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, jurusans);
        binding.actvJurusan.setAdapter(adapter);
    }

    private void loadUserData() {
        DocumentReference userDoc = db.collection("users").document(currentUser.getUid());
        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String phone = documentSnapshot.getString("phone");
                String jurusan = documentSnapshot.getString("jurusan");

                binding.etName.setText(name);
                binding.etEmail.setText(email);
                binding.etPhone.setText(phone);
                binding.actvJurusan.setText(jurusan, false); 
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Gagal memuat data profil.", Toast.LENGTH_SHORT).show());
    }

    private void setupListeners() {
        binding.btnSave.setOnClickListener(v -> saveProfile());
        binding.btnLogout.setOnClickListener(v -> logout());
        binding.btnDeleteAccount.setOnClickListener(v -> confirmDeleteAccount());
    }

    private void saveProfile() {
        binding.btnSave.setEnabled(false);
        binding.btnSave.setText("Menyimpan...");

        String name = Objects.requireNonNull(binding.etName.getText()).toString().trim();
        String phone = Objects.requireNonNull(binding.etPhone.getText()).toString().trim();
        String jurusan = binding.actvJurusan.getText().toString().trim();

        DocumentReference userDoc = db.collection("users").document(currentUser.getUid());

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("jurusan", jurusan);

        userDoc.update(userData)
                .addOnSuccessListener(aVoid -> onSaveSuccess())
                .addOnFailureListener(e -> onSaveFailed());
    }

    private void onSaveSuccess() {
        Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
        binding.btnSave.setEnabled(true);
        binding.btnSave.setText("Simpan Perubahan");
    }

    private void onSaveFailed() {
        Toast.makeText(this, "Gagal memperbarui profil.", Toast.LENGTH_SHORT).show();
        binding.btnSave.setEnabled(true);
        binding.btnSave.setText("Simpan Perubahan");
    }

    private void logout() {
        auth.signOut();
        goToLogin();
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Akun")
                .setMessage("Apakah Anda yakin ingin menghapus akun Anda secara permanen? Tindakan ini tidak dapat dibatalkan.")
                .setPositiveButton("Hapus", (dialog, which) -> deleteAccount())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteAccount() {
        // 1. Hapus data dari Firestore
        db.collection("users").document(currentUser.getUid()).delete()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Gagal menghapus data profil.", Toast.LENGTH_SHORT).show();
                    }
                });

        // 2. Hapus akun dari Authentication (Paling akhir)
        currentUser.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Akun berhasil dihapus.", Toast.LENGTH_SHORT).show();
                        goToLogin();
                    } else {
                        Toast.makeText(this, "Gagal menghapus akun. Coba login ulang.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
