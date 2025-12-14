package com.example.groupfive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class EventDetailActivity extends AppCompatActivity {

    private ImageView ivEventPoster;
    private TextView tvEventTitle, tvEventDate, tvEventTime, tvEventLocation, tvEventDescription;
    private Button btnRegisterEvent;

    // Objek untuk menampung data event yang sedang ditampilkan
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        initializeViews();
        loadEventDataFromIntent();
        setupRegisterButton();
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

        if (intent != null && intent.getExtras() != null) {
            // Ambil semua data dari Intent menggunakan kunci yang konsisten
            String eventId = intent.getStringExtra("eventId");
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            String location = intent.getStringExtra("location");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");
            String posterUrl = intent.getStringExtra("posterUrl");
            String kategori = intent.getStringExtra("kategori");
            String status = intent.getStringExtra("status");

            // Buat objek Event baru untuk menampung semua data
            event = new Event();

            // ✅ PERBAIKAN: Gunakan setId() untuk menyimpan ID, bukan getTime()
            event.setId(eventId);
            event.setTitle(title);
            event.setDescription(description);
            event.setLocation(location);
            event.setDate(date);
            event.setTime(time);
            event.setPosterUrl(posterUrl);
            event.setKategori(kategori);
            event.setStatus(status);

            // Setelah data dimuat ke objek 'event', tampilkan di UI
            displayEventDetails();
        } else {
            // Jika tidak ada data yang dikirim, tampilkan pesan error dan tutup halaman
            Toast.makeText(this, "Error: Tidak ada data event yang diterima.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void displayEventDetails() {
        if (event != null) {
            tvEventTitle.setText(event.getTitle());
            tvEventDescription.setText(event.getDescription());
            // Menambahkan label agar lebih mudah dibaca oleh pengguna
            tvEventLocation.setText("Lokasi: " + event.getLocation());
            tvEventDate.setText("Date: " + event.getDate());
            tvEventTime.setText("Waktu: " + event.getTime());

            // Memuat gambar poster menggunakan Glide, dengan placeholder
            Glide.with(this)
                    .load(event.getPosterUrl())
                    .placeholder(R.drawable.ic_image) // Gambar yang ditampilkan saat loading
                    .error(R.drawable.ic_image)       // Gambar jika URL salah atau tidak ada internet
                    .into(ivEventPoster);
        }
    }

    private void setupRegisterButton() {
        btnRegisterEvent.setOnClickListener(v -> {
            // Pastikan objek event tidak null sebelum melanjutkan
            if (event != null) {
                // Buat intent untuk pindah ke halaman registrasi
                Intent registerIntent = new Intent(EventDetailActivity.this, RegisterEventActivity.class);

                // Kirim data penting (ID dan Judul) ke RegisterEventActivity
                registerIntent.putExtra("eventId", event.getId());
                registerIntent.putExtra("title", event.getTitle());

                startActivity(registerIntent);
            } else {
                Toast.makeText(this, "Data event tidak tersedia untuk registrasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
