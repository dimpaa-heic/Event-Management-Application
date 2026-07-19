package com.example.groupfive.helpers;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;

public class FirestoreHelper {

    private final FirebaseFirestore db;
    private static final String REGISTRATIONS_COLLECTION = "registrations";

    // Definisikan sebuah interface untuk menjadi "pemberi kabar"
    // apakah proses simpan data berhasil atau gagal.
    public interface FirestoreCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    // Konstruktor ini akan dipanggil saat kita membuat 'new FirestoreHelper()'
    public FirestoreHelper() {
        // Inisialisasi koneksi ke database Firestore
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Metode utama untuk menyimpan data pendaftaran ke koleksi "registrations".
     *
     * @param registrationData Data yang akan disimpan.
     * @param callback         Objek callback untuk menangani hasil.
     */
    public void saveRegistration(Map<String, Object> registrationData, final FirestoreCallback callback) {
        // Perintah untuk menambahkan dokumen baru ke koleksi "registrations"
        db.collection(REGISTRATIONS_COLLECTION)
                .add(registrationData) // .add() membuat ID dokumen acak secara otomatis
                .addOnSuccessListener(documentReference -> {
                    // Jika SUKSES, panggil metode onSuccess dari callback
                    callback.onSuccess("Pendaftaran berhasil disimpan dengan ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Jika GAGAL, panggil metode onFailure dari callback
                    callback.onFailure("Error: " + e.getMessage());
                });
    }
}
