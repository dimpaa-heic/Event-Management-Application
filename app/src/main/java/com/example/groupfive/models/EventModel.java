package com.example.groupfive.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

public class EventModel {

    @DocumentId
    private String eventId;

    @PropertyName("title")
    private String title;

    @PropertyName("description")
    private String description;

    @PropertyName("location")
    private String location;

    @PropertyName("date")
    private String date;

    @PropertyName("time")
    private String time;

    @PropertyName("imageUrl")
    private String imageUrl;

    @PropertyName("category")
    private String category;

    @PropertyName("organizerId")
    private String organizerId;

    @PropertyName("participantCount")
    private int participantCount;

    // Empty constructor required for Firestore
    public EventModel() {
    }

    public EventModel(String eventId, String title, String description,
                      String location, String date, String time, String imageUrl) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.time = time;
        this.imageUrl = imageUrl;
        this.participantCount = 0;
    }

    // Getters
    public String getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPosterUrl() {
        return imageUrl; // Alias for compatibility
    }

    public String getKategory() {
        return category;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    // Setters
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }
}