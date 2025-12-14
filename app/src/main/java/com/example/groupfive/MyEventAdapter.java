package com.example.groupfive;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat; // Import untuk warna yang lebih aman
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList; // Gunakan ArrayList agar konsisten

// ✅ PERBAIKAN: Hapus semua impor yang salah seperti 'com.example.eventapp.R' dan 'MyEventModel'

public class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.ViewHolder> {

    private final Context context;
    // ✅ PERBAIKAN 1: Gunakan kelas 'Event' yang benar dan konsisten
    private final ArrayList<Event> eventList;

    public MyEventAdapter(Context context, ArrayList<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Impor R akan otomatis benar setelah impor yang salah dihapus
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // ✅ PERBAIKAN 2: Gunakan objek 'Event'
        final Event event = eventList.get(position);

        holder.tvTitleItem.setText(event.getTitle());
        // Gunakan getKategori() dan getDate() dari kelas Event
        holder.tvCategoryItem.setText(event.getKategori() + " • " + event.getDate());
        holder.tvStatusItem.setText(event.getStatus());

        // Muat gambar menggunakan Glide, panggil getPosterUrl() dari kelas Event
        Glide.with(context)
                .load(event.getPosterUrl())
                .placeholder(R.drawable.ic_image) // Gunakan placeholder standar kita
                .error(R.drawable.ic_image)
                .centerCrop()
                .into(holder.imgThumb);

        // Atur warna status berdasarkan teks status dari objek Event
        if ("Finished".equalsIgnoreCase(event.getStatus())) {
            // Gunakan resource drawable yang benar (jika ada) dan warna yang aman
            holder.tvStatusItem.setBackgroundResource(R.drawable.badge_finished);
            holder.tvStatusItem.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else { // "On Going" atau status lainnya
            holder.tvStatusItem.setBackgroundResource(R.drawable.badge_ongoing);
            holder.tvStatusItem.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        }

        // Click listener untuk pindah ke detail, kirim data dari objek Event
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            // Gunakan kunci Intent yang konsisten dengan yang diterima EventDetailActivity
            intent.putExtra("eventId", event.getId());
            intent.putExtra("title", event.getTitle());
            intent.putExtra("description", event.getDescription());
            intent.putExtra("location", event.getLocation());
            intent.putExtra("date", event.getDate());
            intent.putExtra("time", event.getTime());
            intent.putExtra("posterUrl", event.getPosterUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // ViewHolder tidak perlu diubah, selama ID di item_my_event.xml sudah benar
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvTitleItem, tvCategoryItem, tvStatusItem;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            tvTitleItem = itemView.findViewById(R.id.tvTitleItem);
            tvCategoryItem = itemView.findViewById(R.id.tvCategoryItem);
            tvStatusItem = itemView.findViewById(R.id.tvStatusItem);
        }
    }
}
