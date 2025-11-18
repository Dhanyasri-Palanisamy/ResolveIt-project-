package com.resolveit.controller;

import com.resolveit.model.Feedback;
import com.resolveit.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "http://localhost:3000")
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;
    
    @PostMapping("/submit")
    public ResponseEntity<?> submitFeedback(@RequestBody Feedback feedback) {
        try {
            // Check if user already submitted feedback
            if (feedbackService.hasUserSubmittedFeedback(
                    feedback.getComplaintId(), 
                    feedback.getUserEmail())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "You have already submitted feedback for this complaint"));
            }
            
            Feedback saved = feedbackService.saveFeedback(feedback);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("feedbackId", saved.getFeedbackId());
            response.put("message", "Thank you for your feedback!");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllFeedback() {
        try {
            List<Feedback> feedback = feedbackService.getAllFeedback();
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/complaint/{complaintId}")
    public ResponseEntity<?> getFeedbackByComplaint(@PathVariable Long complaintId) {
        try {
            List<Feedback> feedback = feedbackService.getFeedbackByComplaintId(complaintId);
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/average-rating")
    public ResponseEntity<?> getAverageRating() {
        try {
            double avgRating = feedbackService.getAverageRating();
            return ResponseEntity.ok(Map.of("averageRating", avgRating));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/check/{complaintId}/{userEmail}")
    public ResponseEntity<?> checkIfFeedbackExists(
            @PathVariable Long complaintId,
            @PathVariable String userEmail) {
        try {
            boolean exists = feedbackService.hasUserSubmittedFeedback(complaintId, userEmail);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }
}
