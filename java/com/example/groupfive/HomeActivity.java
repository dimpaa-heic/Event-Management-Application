package com.example.groupfive;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements EventAdapter.OnEventClickListener {

    private RecyclerView recyclerEvents;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventList = new ArrayList<>();

    private EditText etSearch;
    private ProgressBar progressBar;

    private TextView filterSeminar, filterWorkshop, filterLomba, filterUKM;
    private TextView tvEventTotal, tvEventAktif, tvEventIkut;
    private TextView tvWelcome;

    private LinearLayout navHome, navEvent, navOrg, navProfile;

    private FirebaseFirestore db;

    private String currentUserName = "";
    private String currentUserEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        initViews();
        setupRecyclerView();
        setupFiltering();
        setupBottomNav();

        refreshUserAndWelcome();
        loadEventsFromFirebase();
        setActiveNav(navHome);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUserAndWelcome();
    }

    private void refreshUserAndWelcome() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            tvWelcome.setText("Welcome, Guest");
            return;
        }

        currentUserEmail = user.getEmail();
        loadUserName(user);
    }

    private void initViews() {
        recyclerEvents = findViewById(R.id.recyclerEvents);
        etSearch = findViewById(R.id.etSearch);
        progressBar = findViewById(R.id.progressBar);

        filterSeminar = findViewById(R.id.filterSeminar);
        filterWorkshop = findViewById(R.id.filterWorkshop);
        filterLomba = findViewById(R.id.filterLomba);
        filterUKM = findViewById(R.id.filterUKM);

        tvEventTotal = findViewById(R.id.tvEventTotal);
        tvEventAktif = findViewById(R.id.tvEventAktif);
        tvEventIkut = findViewById(R.id.tvEventIkut);
        tvWelcome = findViewById(R.id.tvWelcome);

        navHome = findViewById(R.id.navHome);
        navEvent = findViewById(R.id.navEvent);
        navOrg = findViewById(R.id.navOrg);
        navProfile = findViewById(R.id.navProfile);
    }

    private void loadUserName(FirebaseUser user) {

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {

                    currentUserName = doc.getString("name");

                    if (currentUserName == null || currentUserName.isEmpty()) {
                        currentUserName = "User";
                    }

                    tvWelcome.setText("Welcome, " + currentUserName + "!");
                })
                .addOnFailureListener(e ->
                        tvWelcome.setText("Welcome")
                );
    }

    private void setupRecyclerView() {
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

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Event event = doc.toObject(Event.class);
                            event.setId(doc.getId());
                            eventList.add(event);
                        }

                        eventAdapter.updateFullList(new ArrayList<>(eventList));
                        eventAdapter.notifyDataSetChanged();
                        updateStatistics(eventList);
                    }
                });
    }

    private void setupFiltering() {

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                eventAdapter.getFilter().filter(s.toString());
            }
        });

        filterSeminar.setOnClickListener(v -> eventAdapter.getFilter().filter("Seminar"));
        filterWorkshop.setOnClickListener(v -> eventAdapter.getFilter().filter("Workshop"));
        filterLomba.setOnClickListener(v -> eventAdapter.getFilter().filter("Lomba"));
        filterUKM.setOnClickListener(v -> eventAdapter.getFilter().filter("UKM"));
    }

    private void setupBottomNav() {

        navHome.setOnClickListener(v -> setActiveNav(navHome));

        navEvent.setOnClickListener(v -> {
            setActiveNav(navEvent);
            startActivity(new Intent(this, MyEventActivity.class));
        });

        navOrg.setOnClickListener(v ->
                Toast.makeText(this, "Organization - Coming Soon", Toast.LENGTH_SHORT).show()
        );

        navProfile.setOnClickListener(v -> {

            // 🔹 SELALU ambil ulang current user (jangan pakai cache)
            FirebaseUser freshUser = FirebaseAuth.getInstance().getCurrentUser();

            if (freshUser == null) {
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }

            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("uid", freshUser.getUid());
            intent.putExtra("name", currentUserName);
            intent.putExtra("email", currentUserEmail);

            startActivity(intent);
        });
    }

    private void setActiveNav(View active) {
        navHome.setSelected(false);
        navEvent.setSelected(false);
        navOrg.setSelected(false);
        navProfile.setSelected(false);
        active.setSelected(true);
    }

    private void updateStatistics(ArrayList<Event> events) {
        tvEventTotal.setText(String.valueOf(events.size()));
        tvEventAktif.setText(String.valueOf(events.size()));
        tvEventIkut.setText("0");
    }

    @Override
    public void onClick(Event event) {

        Intent intent = new Intent(this, EventDetailActivity.class);

        intent.putExtra("eventId", event.getId());
        intent.putExtra("title", event.getTitle());
        intent.putExtra("kategori", event.getKategori());
        intent.putExtra("date", event.getDate());
        intent.putExtra("description", event.getDescription());
        intent.putExtra("location", event.getLocation());
        intent.putExtra("time", event.getTime());
        intent.putExtra("posterUrl", event.getPosterUrl());
        intent.putExtra("registerUrl", event.getRegisterUrl());

        startActivity(intent);
    }
}
