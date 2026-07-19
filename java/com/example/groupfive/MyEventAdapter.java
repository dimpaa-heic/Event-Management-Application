package com.example.groupfive;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

// ✅ IMPORT BARU yang lebih akurat
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.Locale;

public class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Event> eventList;
    private static final String TAG = "MyEventAdapter"; // Untuk logging

    public MyEventAdapter(Context context, ArrayList<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Ambil objek event saat ini
        final Event event = eventList.get(position);

        // Set data dasar
        holder.tvTitleItem.setText(event.getTitle());
        holder.tvCategoryItem.setText(event.getKategori() + " • " + event.getDate());

        // Muat gambar dengan Glide
        Glide.with(context)
                .load(event.getPosterUrl())
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .centerCrop()
                .into(holder.imgThumb);

        // --- ✅ LOGIKA PERBANDINGAN TANGGAL YANG DIPERBAIKI (LEBIH AKURAT) ---
        try {
            // 1. Tentukan format tanggal yang Anda gunakan (contoh: "January 25, 2026")
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

            // 2. Ubah string tanggal dari event menjadi objek LocalDate
            LocalDate eventDate = LocalDate.parse(event.getDate(), formatter);

            // 3. Dapatkan tanggal hari ini
            LocalDate today = LocalDate.now();

            // 4. Bandingkan tanggal
            if (eventDate.isBefore(today)) {
                // Jika tanggal event SUDAH LEWAT
                holder.tvStatusItem.setText("Finished");
                holder.tvStatusItem.setBackgroundResource(R.drawable.badge_finished);
            } else {
                // Jika tanggal event HARI INI atau DI MASA DEPAN
                holder.tvStatusItem.setText("On Going");
                holder.tvStatusItem.setBackgroundResource(R.drawable.badge_ongoing);
            }
            // Set warna teks setelah background di-set
            holder.tvStatusItem.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            holder.tvStatusItem.setVisibility(View.VISIBLE);

        } catch (DateTimeParseException e) {
            // Jika format tanggal salah (misal: "25-Januari-2026" bukan "January 25, 2026")
            // Sembunyikan status dan cetak error untuk debugging
            Log.e(TAG, "Gagal mem-parsing tanggal: '" + event.getDate() + "'. Error: " + e.getMessage());
            holder.tvStatusItem.setVisibility(View.GONE);
        } catch (Exception e) {
            // Untuk error lainnya
            Log.e(TAG, "Error tidak terduga di onBindViewHolder: " + e.getMessage());
            holder.tvStatusItem.setVisibility(View.GONE);
        }

        // --- CLICK LISTENER (SUDAH BENAR) ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);

            // Kirim SEMUA data event yang dibutuhkan oleh EventDetailActivity
            intent.putExtra("eventId", event.getId());
            intent.putExtra("title", event.getTitle());
            intent.putExtra("description", event.getDescription());
            intent.putExtra("location", event.getLocation());
            intent.putExtra("date", event.getDate());
            intent.putExtra("time", event.getTime());
            intent.putExtra("posterUrl", event.getPosterUrl());
            // Tambahkan data lain jika ada dan dibutuhkan
            // intent.putExtra("kategori", event.getKategori());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvTitleItem, tvCategoryItem, tvStatusItem;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Pastikan ID ini cocok dengan file item_my_event.xml
            imgThumb = itemView.findViewById(R.id.imgPoster);
            tvTitleItem = itemView.findViewById(R.id.tvTitle);
            tvCategoryItem = itemView.findViewById(R.id.tvKategori);
            tvStatusItem = itemView.findViewById(R.id.tvDate);
        }
    }
}
