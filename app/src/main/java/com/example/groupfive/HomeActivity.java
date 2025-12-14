package com.example.groupfive;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

// ✅ PERBAIKAN 1: Implementasikan OnEventClickListener dari EventAdapter
public class HomeActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private RecyclerView recyclerEvents;
    private EventAdapter eventAdapter;
    // ✅ PERBAIKAN 2: Gunakan kelas 'Event' yang benar, bukan 'EventModel'
    private ArrayList<Event> eventList = new ArrayList<>();
    private EditText etSearch;
    private ProgressBar progressBar;

    private TextView filterSeminar, filterWorkshop, filterLomba, filterUKM;
    private TextView tvEventTotal, tvEventAktif, tvEventIkut;

    // Bottom nav
    private ImageView navHome, navEvent, navOrg, navProfile;

    // Firebase
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // === Inisialisasi Views dari XML ===
        initializeViews();

        // === Inisialisasi Firebase ===
        db = FirebaseFirestore.getInstance();

        // === Setup RecyclerView dan Adapter-nya ===
        setupRecyclerView();

        // === Muat data awal dari Firebase ===
        loadEventsFromFirebase();

        // === Setup logika untuk filter dan search ===
        setupFiltering();

        // === Setup logika untuk bottom navigation ===
        setupBottomNav();

        // Pilih item Home sebagai default
        navHome.setSelected(true);
    }

    private void initializeViews() {
        recyclerEvents = findViewById(R.id.recyclerEvents);
        etSearch = findViewById(R.id.etSearch);

        filterSeminar = findViewById(R.id.filterSeminar);
        filterWorkshop = findViewById(R.id.filterWorkshop);
        filterLomba = findViewById(R.id.filterLomba);
        filterUKM = findViewById(R.id.filterUKM);

        tvEventTotal = findViewById(R.id.tvEventTotal);
        tvEventAktif = findViewById(R.id.tvEventAktif);
        tvEventIkut = findViewById(R.id.tvEventIkut);

        navHome = findViewById(R.id.navHome);
        navEvent = findViewById(R.id.navEvent);
        navOrg = findViewById(R.id.navOrg);
        navProfile = findViewById(R.id.navProfile);
    }

    private void setupRecyclerView() {
        // ✅ PERBAIKAN 3: Kirim 'this' sebagai listener karena class ini sudah mengimplementasikan OnEventClickListener
        eventAdapter = new EventAdapter(this, eventList, this);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerEvents.setAdapter(eventAdapter);
    }

    private void loadEventsFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerEvents.setVisibility(View.GONE);
        eventList.clear();

        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    recyclerEvents.setVisibility(View.VISIBLE);

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            event.setId(document.getId());
                            eventList.add(event);
                        }
                        // Beri tahu adapter bahwa data telah berubah
                        eventAdapter.notifyDataSetChanged();
                        // ✅ PERBAIKAN 4: Perbarui daftar lengkap di adapter agar filter berfungsi
                        eventAdapter.updateFullList(new ArrayList<>(eventList));

                        // Update statistik
                        updateStatistics(eventList);
                    } else {
                        Log.w("HomeActivity", "Error getting documents.", task.getException());
                        Toast.makeText(HomeActivity.this, "Gagal memuat data event.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupFiltering() {
        // Filter berdasarkan pencarian teks
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (eventAdapter != null) {
                    eventAdapter.getFilter().filter(s.toString());
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Filter berdasarkan klik kategori
        filterSeminar.setOnClickListener(v -> eventAdapter.getFilter().filter("Seminar"));
        filterWorkshop.setOnClickListener(v -> eventAdapter.getFilter().filter("Workshop"));
        filterLomba.setOnClickListener(v -> eventAdapter.getFilter().filter("Lomba"));
        filterUKM.setOnClickListener(v -> eventAdapter.getFilter().filter("UKM"));
    }

    private void setupBottomNav() {
        navHome.setOnClickListener(v -> selectNav(navHome));
        navEvent.setOnClickListener(v -> selectNav(navEvent));
        navOrg.setOnClickListener(v -> selectNav(navOrg));
        navProfile.setOnClickListener(v -> selectNav(navProfile));
    }

    private void selectNav(View selectedView) {
        // Reset semua
        navHome.setSelected(false);
        navEvent.setSelected(false);
        navOrg.setSelected(false);
        navProfile.setSelected(false);
        // Set yang terpilih
        selectedView.setSelected(true);

        // Logika perpindahan halaman
        if (selectedView.getId() == R.id.navEvent) {
            startActivity(new Intent(this, MyEventActivity.class));
        } else if (selectedView.getId() == R.id.navOrg) {
            Toast.makeText(this, "Organization - Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (selectedView.getId() == R.id.navProfile) {
            Toast.makeText(this, "Profile - Coming Soon", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStatistics(ArrayList<Event> events) {
        tvEventTotal.setText(String.valueOf(events.size()));
        // Logika lain untuk event aktif dan diikuti bisa ditambahkan di sini
        tvEventAktif.setText(String.valueOf(events.size()));
        tvEventIkut.setText("0"); // Placeholder
    }

    // ✅ PERBAIKAN 5: Implementasikan metode dari OnEventClickListener
    @Override
    public void onClick(Event event) {
        // Logika untuk pindah ke halaman detail saat item di-klik
        Intent intent = new Intent(HomeActivity.this, EventDetailActivity.class);
        intent.putExtra("eventId", event.getId());
        intent.putExtra("title", event.getTitle());
        intent.putExtra("kategori", event.getKategori());
        intent.putExtra("date", event.getDate());
        intent.putExtra("description", event.getDescription());
        intent.putExtra("location", event.getLocation());
        intent.putExtra("time", event.getTime());
        intent.putExtra("posterUrl", event.getPosterUrl());
        startActivity(intent);
    }
}
