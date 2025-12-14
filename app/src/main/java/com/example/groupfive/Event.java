package com.example.groupfive;

import com.google.firebase.firestore.DocumentId;

// ✅ PASTIKAN KATA 'public' ADA DI SINI
public class Event {

    @DocumentId
    private String id;
    private String title;
    private String description;
    private String location;
    private String date;
    private String time;
    private String posterUrl;
    private String kategori;
    private String status;

    // Konstruktor kosong ini wajib ada untuk konversi dari Firestore
    public Event() {
    }

    // --- GETTERS (Untuk mengambil data) ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getPosterUrl() { return posterUrl; }
    public String getKategori() { return kategori; }
    public String getStatus() { return status; }

    // --- SETTERS (Untuk mengatur data) ---
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setStatus(String status) { this.status = status; }
}
