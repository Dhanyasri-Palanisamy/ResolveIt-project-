package com.resolveit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;
    
    private Long complaintId;
    private String userEmail;
    private int rating; // 1-5 stars
    
    @Column(length = 1000)
    private String comment;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Constructors
    public Feedback() {}
    
    public Feedback(Long complaintId, String userEmail, int rating, String comment) {
        this.complaintId = complaintId;
        this.userEmail = userEmail;
        this.rating = rating;
        this.comment = comment;
    }
    
    // Getters and Setters
    public Long getFeedbackId() { return feedbackId; }
    public void setFeedbackId(Long feedbackId) { this.feedbackId = feedbackId; }
    
    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}