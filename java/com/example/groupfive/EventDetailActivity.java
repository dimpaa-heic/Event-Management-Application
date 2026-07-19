package com.example.groupfive;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class EventDetailActivity extends AppCompatActivity {

    private ImageView ivEventPoster;
    private TextView tvEventTitle, tvEventDate, tvEventTime, tvEventLocation, tvEventDescription;
    private Button btnRegisterEvent;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();
        loadEventDataFromIntent();
        setupRegisterButton();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeViews() {
        ivEventPoster = findViewById(R.id.ivEventPoster);
        tvEventTitle = findViewById(R.id.tvEventTitle);
        tvEventDate = findViewById(R.id.tvEventDate);
        tvEventTime = findViewById(R.id.tvEventTime);
        tvEventLocation = findViewById(R.id.tvEventLocation);
        tvEventDescription = findViewById(R.id.tvEventDescription);
        btnRegisterEvent = findViewById(R.id.btnRegisterEvent);
    }

    private void loadEventDataFromIntent() {
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("eventId")) {
            event = new Event();
            event.setId(intent.getStringExtra("eventId"));
            event.setTitle(intent.getStringExtra("title"));
            event.setDescription(intent.getStringExtra("description"));
            event.setLocation(intent.getStringExtra("location"));
            event.setDate(intent.getStringExtra("date"));
            event.setTime(intent.getStringExtra("time"));
            event.setPosterUrl(intent.getStringExtra("posterUrl"));
            event.setKategori(intent.getStringExtra("kategori"));

            // 🔥 BARU
            event.setRegisterUrl(intent.getStringExtra("registerUrl"));

            displayEventDetails();
        } else {
            Toast.makeText(this, "Gagal memuat detail event", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void displayEventDetails() {
        tvEventTitle.setText(event.getTitle());
        tvEventDescription.setText(event.getDescription());
        tvEventLocation.setText("Lokasi: " + event.getLocation());
        tvEventDate.setText("Tanggal: " + event.getDate());
        tvEventTime.setText("Waktu: " + event.getTime());

        Glide.with(this)
                .load(event.getPosterUrl())
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(ivEventPoster);

        // 🔥 UBAH TEKS TOMBOL
        btnRegisterEvent.setText("Daftar via Penyelenggara");
    }

    private void setupRegisterButton() {
        btnRegisterEvent.setOnClickListener(v -> {
            if (event.getRegisterUrl() != null && !event.getRegisterUrl().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(event.getRegisterUrl()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Link pendaftaran tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
