package com.example.groupfive;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth; // <-- IMPORT ini
import com.google.firebase.auth.FirebaseUser; // <-- IMPORT ini
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MyEventActivity extends AppCompatActivity {

    private RecyclerView recyclerMyEvents;
    private MyEventAdapter myEventAdapter;
    private ArrayList<Event> myEventList = new ArrayList<>();
    private TextView tvNoEvents;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth; // <-- Tambahkan FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);

        // === Inisialisasi Views ===
        initializeViews();

        // === Inisialisasi Firebase ===
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance(); // <-- Inisialisasi FirebaseAuth

        // === Setup RecyclerView dan Adapter ===
        setupRecyclerView();

        // === Muat data event yang diikuti dari Firebase ===
        loadMyRegisteredEvents();
    }

    private void initializeViews() {
        recyclerMyEvents = findViewById(R.id.recyclerMyEvent);
        tvNoEvents = findViewById(R.id.tvNoEvents);
        progressBar = findViewById(R.id.progressBarMyEvent);
    }

    private void setupRecyclerView() {
        myEventAdapter = new MyEventAdapter(this, myEventList);
        recyclerMyEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerMyEvents.setAdapter(myEventAdapter);
    }

    private void loadMyRegisteredEvents() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerMyEvents.setVisibility(View.GONE);
        tvNoEvents.setVisibility(View.GONE);
        myEventList.clear();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Anda harus login untuk melihat event Anda", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            tvNoEvents.setVisibility(View.VISIBLE);
            return;
        }
        String userId = currentUser.getUid();

        // Query untuk mengambil event yang ID user-nya ada di dalam array 'registeredUsers'
        db.collection("registrations")
                .whereEqualTo("userId", userId) // Ambil semua registrasi milik user ini
                .get()
                .addOnSuccessListener(registrationSnapshots -> {
                    if (registrationSnapshots.isEmpty()) {
                        // Jika user belum pernah mendaftar event apapun
                        progressBar.setVisibility(View.GONE);
                        tvNoEvents.setVisibility(View.VISIBLE);
                        return;
                    }

                    ArrayList<String> registeredEventIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : registrationSnapshots) {
                        String eventId = doc.getString("eventId");
                        if (eventId != null) {
                            registeredEventIds.add(eventId);
                        }
                    }

                    // Sekarang ambil detail dari setiap event yang sudah didaftarkan
                    if (!registeredEventIds.isEmpty()) {
                        db.collection("events")
                                .whereIn("id", registeredEventIds) // Ambil event yang ID-nya ada di daftar
                                .get()
                                .addOnCompleteListener(eventTask -> {
                                    progressBar.setVisibility(View.GONE);
                                    if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                                        for (QueryDocumentSnapshot document : eventTask.getResult()) {
                                            Event event = document.toObject(Event.class);
                                            event.setId(document.getId());

                                            // ✅ PERBAIKAN: HAPUS logika status manual.
                                            // Biarkan adapter yang menentukan status berdasarkan tanggal.

                                            myEventList.add(event);
                                        }

                                        if (myEventList.isEmpty()) {
                                            tvNoEvents.setVisibility(View.VISIBLE);
                                        } else {
                                            recyclerMyEvents.setVisibility(View.VISIBLE);
                                            myEventAdapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        Log.e("MyEventActivity", "Error loading event details: ", eventTask.getException());
                                        Toast.makeText(MyEventActivity.this, "Gagal memuat detail event.", Toast.LENGTH_SHORT).show();
                                        tvNoEvents.setVisibility(View.VISIBLE);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    tvNoEvents.setVisibility(View.VISIBLE);
                    Log.e("MyEventActivity", "Error loading registrations: ", e);
                    Toast.makeText(MyEventActivity.this, "Gagal memuat registrasi.", Toast.LENGTH_SHORT).show();
                });
    }
}
