package com.resolveit.service;

import com.resolveit.model.Feedback;
import com.resolveit.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }
    
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }
    
    public List<Feedback> getFeedbackByComplaintId(Long complaintId) {
        return feedbackRepository.findByComplaintId(complaintId);
    }
    
    public boolean hasUserSubmittedFeedback(Long complaintId, String userEmail) {
        return feedbackRepository.existsByComplaintIdAndUserEmail(complaintId, userEmail);
    }
    
    public double getAverageRating() {
        List<Feedback> allFeedback = feedbackRepository.findAll();
        if (allFeedback.isEmpty()) return 0.0;
        
        return allFeedback.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
    }
}