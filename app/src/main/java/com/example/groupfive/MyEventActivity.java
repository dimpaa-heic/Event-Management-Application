package com.example.groupfive;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MyEventActivity extends AppCompatActivity {

    private RecyclerView recyclerMyEvents;
    private MyEventAdapter myEventAdapter; // ✅ Gunakan MyEventAdapter yang sudah kita perbaiki
    private ArrayList<Event> myEventList = new ArrayList<>();
    private TextView tvNoEvents;
    private ProgressBar progressBar;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);

        // === Inisialisasi Views ===
        initializeViews();

        // === Inisialisasi Firebase ===
        db = FirebaseFirestore.getInstance();

        // === Setup RecyclerView dan Adapter ===
        setupRecyclerView();

        // === Muat data event yang diikuti dari Firebase ===
        loadMyRegisteredEvents();
    }

    private void initializeViews() {
        // Pastikan ID ini ada di file layout 'activity_my_event.xml' Anda
        recyclerMyEvents = findViewById(R.id.recyclerMyEvent);
        tvNoEvents = findViewById(R.id.tvNoEvents);
        progressBar = findViewById(R.id.progressBarMyEvent);
    }

    private void setupRecyclerView() {
        // Buat instance dari MyEventAdapter yang sudah benar
        myEventAdapter = new MyEventAdapter(this, myEventList);
        recyclerMyEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerMyEvents.setAdapter(myEventAdapter);
    }

    private void loadMyRegisteredEvents() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerMyEvents.setVisibility(View.GONE);
        tvNoEvents.setVisibility(View.GONE);
        myEventList.clear();

        // Query ini adalah contoh. Anda harus menyesuaikannya dengan struktur data Anda.
        // Misal: kita mengambil semua event yang field 'registeredUsers' nya
        // berisi ID pengguna yang sedang login.
        // Untuk sekarang, kita muat semua event sebagai placeholder.
        db.collection("events")
                // .whereArrayContains("registeredUsers", "USER_ID_ANDA") // -> Ini cara yang benar nanti
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            event.setId(document.getId());
                            // Sebagai contoh, kita set statusnya secara manual
                            if(myEventList.size() % 2 == 0) {
                                event.setStatus("Finished");
                            } else {
                                event.setStatus("On Going");
                            }
                            myEventList.add(event);
                        }

                        if (myEventList.isEmpty()) {
                            // Tampilkan pesan jika tidak ada event yang diikuti
                            tvNoEvents.setVisibility(View.VISIBLE);
                        } else {
                            // Tampilkan RecyclerView jika ada data
                            recyclerMyEvents.setVisibility(View.VISIBLE);
                            myEventAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Log.e("MyEventActivity", "Error loading events: ", task.getException());
                        Toast.makeText(MyEventActivity.this, "Gagal memuat event.", Toast.LENGTH_SHORT).show();
                        tvNoEvents.setVisibility(View.VISIBLE); // Tampilkan pesan error juga
                    }
                });
    }
}
