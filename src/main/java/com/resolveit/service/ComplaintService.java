package com.resolveit.service;

import com.resolveit.model.Complaint;
import com.resolveit.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    // Save a new complaint
    public Complaint submitComplaint(Complaint complaint) {
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setUpdatedAt(LocalDateTime.now());
        // Set deadline to 48 hours from creation
        complaint.setDeadline(LocalDateTime.now().plusHours(48));
        return complaintRepository.save(complaint);
    }

    // Get all complaints
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    // Get complaint by ID
    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }

    // Update complaint status and resolution info
    public Complaint updateComplaint(Long id, String status, String resolutionNote, String resolvedImagePath) {
        Optional<Complaint> existingComplaint = complaintRepository.findById(id);
        if (existingComplaint.isPresent()) {
            Complaint complaint = existingComplaint.get();
            complaint.setStatus(status);
            complaint.setResolutionNote(resolutionNote);
            complaint.setResolvedImagePath(resolvedImagePath);
            complaint.setUpdatedAt(LocalDateTime.now());
            return complaintRepository.save(complaint);
        } else {
            throw new RuntimeException("Complaint not found with ID: " + id);
        }
    }

    // Check and escalate overdue complaints
    public void escalateOverdueComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Complaint complaint : complaints) {
            if (complaint.getDeadline() != null &&
                now.isAfter(complaint.getDeadline()) &&
                !complaint.isEscalated() &&
                !"Resolved".equalsIgnoreCase(complaint.getStatus())) {
                
                complaint.setEscalated(true);
                complaint.setStatus("Escalated");
                complaint.setUpdatedAt(now);
                complaintRepository.save(complaint);
            }
        }
    }
    
    // Delete complaint
    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }
}