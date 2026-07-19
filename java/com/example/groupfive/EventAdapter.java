package com.example.groupfive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> implements Filterable {

    private final Context context;
    private final ArrayList<Event> eventList;
    private final ArrayList<Event> eventListFull;
    private final OnEventClickListener listener;

    public interface OnEventClickListener {
        void onClick(Event event);
    }

    public EventAdapter(Context context, ArrayList<Event> eventList, OnEventClickListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.listener = listener;
        this.eventListFull = new ArrayList<>(eventList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ✅ PERBAIKAN 1: Gunakan layout 'item_event.xml' yang benar.
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_my_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.tvTitle.setText(event.getTitle() != null ? event.getTitle() : "Judul Tidak Tersedia");
        holder.tvKategori.setText(event.getKategori() != null ? event.getKategori() : "-");
        holder.tvDate.setText(event.getDate() != null ? event.getDate() : "-");

        if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
            Glide.with(context)
                    .load(event.getPosterUrl())
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .into(holder.imgPoster);
        } else {
            holder.imgPoster.setImageResource(R.drawable.ic_image);
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView tvTitle, tvKategori, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvKategori = itemView.findViewById(R.id.tvKategori);
            tvDate = itemView.findViewById(R.id.tvDate);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onClick(eventList.get(position));
                }
            });
        }
    }

    public void updateFullList(ArrayList<Event> newEvents) {
        eventListFull.clear();
        eventListFull.addAll(newEvents);
    }

    @Override
    public Filter getFilter() {
        return eventFilter;
    }

    private final Filter eventFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Event> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(eventListFull);
            } else {
                String pattern = constraint.toString().toLowerCase().trim();
                for (Event item : eventListFull) {
                    String title = (item.getTitle() != null) ? item.getTitle().toLowerCase() : "";
                    String kategori = (item.getKategori() != null) ? item.getKategori().toLowerCase() : "";
                    if (title.contains(pattern) || kategori.equals(pattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            eventList.clear();
            if (results.values != null) {
                eventList.addAll((List<Event>) results.values);
            }
            notifyDataSetChanged();
        }
    };
}
