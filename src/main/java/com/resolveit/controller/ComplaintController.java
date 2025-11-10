package com.resolveit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.resolveit.model.Complaint;
import com.resolveit.service.ComplaintService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:3000")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    private static final String UPLOAD_DIR = "uploads/";

    // Register a new complaint
    @PostMapping("/register")
    public Complaint registerComplaint(
            @RequestParam("userName") String userName,
            @RequestParam("userEmail") String userEmail,
            @RequestParam("complaintType") String complaintType,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        Complaint complaint = new Complaint();
        complaint.setUserName(userName);
        complaint.setUserEmail(userEmail);
        complaint.setComplaintType(complaintType);
        complaint.setDescription(description);

        // Save image to folder if uploaded
        if (image != null && !image.isEmpty()) {
            String imagePath = UPLOAD_DIR + System.currentTimeMillis() + "_" + image.getOriginalFilename();
            File dest = new File(imagePath);
            dest.getParentFile().mkdirs();
            image.transferTo(dest);
            complaint.setImagePath(imagePath);
        }

        return complaintService.submitComplaint(complaint);
    }

    // Get all complaints
    @GetMapping("/all")
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    // Get complaint by ID
    @GetMapping("/{id}")
    public Optional<Complaint> getComplaintById(@PathVariable Long id) {
        return complaintService.getComplaintById(id);
    }

    // Update complaint (for officer to mark resolved)
    @PutMapping("/update/{id}")
    public Complaint updateComplaint(
            @PathVariable Long id,
            @RequestParam("status") String status,
            @RequestParam(value = "resolutionNote", required = false) String resolutionNote,
            @RequestParam(value = "resolvedImage", required = false) MultipartFile resolvedImage
    ) throws IOException {
        String resolvedImagePath = null;

        if (resolvedImage != null && !resolvedImage.isEmpty()) {
            String filePath = UPLOAD_DIR + "resolved_" + System.currentTimeMillis() + "_" + resolvedImage.getOriginalFilename();
            File dest = new File(filePath);
            dest.getParentFile().mkdirs();
            resolvedImage.transferTo(dest);
            resolvedImagePath = filePath;
        }

        return complaintService.updateComplaint(id, status, resolutionNote, resolvedImagePath);
    }

    // Manually trigger escalation check
    @PostMapping("/escalate")
    public String escalateOverdueComplaints() {
        complaintService.escalateOverdueComplaints();
        return "Checked and escalated overdue complaints successfully!";
    }

    // Delete complaint (Admin only)
    @DeleteMapping("/{id}")
    public String deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return "Complaint " + id + " deleted successfully";
    }
}