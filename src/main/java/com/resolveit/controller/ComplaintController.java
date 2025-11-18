package com.resolveit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.resolveit.model.Complaint;
import com.resolveit.service.ComplaintService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:3000")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    // Use absolute path to avoid Tomcat temp directory issues
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

    @PostMapping("/register")
    public ResponseEntity<?> registerComplaint(
            @RequestParam("userName") String userName,
            @RequestParam("userEmail") String userEmail,
            @RequestParam("complaintType") String complaintType,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        System.out.println("=== COMPLAINT REGISTRATION ===");
        System.out.println("Upload directory: " + UPLOAD_DIR);
        System.out.println("User: " + userName);
        System.out.println("Email: " + userEmail);
        System.out.println("Type: " + complaintType);
        System.out.println("File attached: " + (image != null && !image.isEmpty()));
        
        try {
            Complaint complaint = new Complaint();
            complaint.setUserName(userName);
            complaint.setUserEmail(userEmail);
            complaint.setComplaintType(complaintType);
            complaint.setDescription(description);

            if (image != null && !image.isEmpty()) {
                System.out.println("Processing file: " + image.getOriginalFilename());
                System.out.println("File size: " + image.getSize() + " bytes");
                
                try {
                    // Create uploads directory if it doesn't exist
                    File uploadDir = new File(UPLOAD_DIR);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                        System.out.println("Created directory: " + UPLOAD_DIR);
                    }

                    // Generate unique filename
                    String originalFilename = image.getOriginalFilename();
                    String extension = "";
                    if (originalFilename != null && originalFilename.contains(".")) {
                        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    }
                    String filename = System.currentTimeMillis() + extension;
                    
                    // Create full file path
                    File destFile = new File(UPLOAD_DIR + filename);
                    
                    // Save file
                    image.transferTo(destFile);
                    
                    // Store relative path in database
                    String relativePath = "uploads/" + filename;
                    complaint.setImagePath(relativePath);
                    
                    System.out.println("✅ File saved to: " + destFile.getAbsolutePath());
                    System.out.println("✅ Database path: " + relativePath);
                    
                } catch (IOException e) {
                    System.err.println("❌ Error saving file: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("No file attached");
            }

            Complaint savedComplaint = complaintService.submitComplaint(complaint);
            System.out.println("✅ Complaint saved with ID: " + savedComplaint.getComplaintId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("complaintId", savedComplaint.getComplaintId());
            response.put("complaintType", savedComplaint.getComplaintType());
            response.put("status", savedComplaint.getStatus());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllComplaints() {
        try {
            List<Complaint> complaints = complaintService.getAllComplaints();
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaintById(@PathVariable Long id) {
        try {
            Optional<Complaint> complaint = complaintService.getComplaintById(id);
            if (complaint.isPresent()) {
                return ResponseEntity.ok(complaint.get());
            } else {
                return ResponseEntity.status(404).body(Map.of("message", "Complaint not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateComplaint(
            @PathVariable Long id,
            @RequestParam("status") String status,
            @RequestParam(value = "resolutionNote", required = false) String resolutionNote,
            @RequestParam(value = "resolvedImage", required = false) MultipartFile resolvedImage
    ) {
        System.out.println("=== UPDATE COMPLAINT ===");
        System.out.println("Complaint ID: " + id);
        System.out.println("New status: " + status);
        System.out.println("Resolution image: " + (resolvedImage != null && !resolvedImage.isEmpty()));
        
        try {
            String resolvedImagePath = null;

            if (resolvedImage != null && !resolvedImage.isEmpty()) {
                System.out.println("Processing resolution image: " + resolvedImage.getOriginalFilename());
                
                // Create uploads directory if needed
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                    System.out.println("Created directory: " + UPLOAD_DIR);
                }

                String filename = "resolved_" + System.currentTimeMillis() + "_" + resolvedImage.getOriginalFilename();
                File destFile = new File(UPLOAD_DIR + filename);
                
                resolvedImage.transferTo(destFile);
                resolvedImagePath = "uploads/" + filename;
                
                System.out.println("✅ Resolution image saved: " + destFile.getAbsolutePath());
            }

            Complaint updated = complaintService.updateComplaint(id, status, resolutionNote, resolvedImagePath);
            System.out.println("✅ Complaint updated successfully");
            
            return ResponseEntity.ok(updated);
            
        } catch (Exception e) {
            System.err.println("❌ Error updating complaint: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/escalate")
    public ResponseEntity<?> escalateOverdueComplaints() {
        try {
            complaintService.escalateOverdueComplaints();
            return ResponseEntity.ok(Map.of("success", true, "message", "Escalation check completed"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComplaint(@PathVariable Long id) {
        try {
            complaintService.deleteComplaint(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Complaint deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    // Export complaints as CSV
    @GetMapping("/export/csv")
    public ResponseEntity<String> exportToCSV() {
        try {
            List<Complaint> complaints = complaintService.getAllComplaints();
            StringBuilder csv = new StringBuilder();
            
            // Header
            csv.append("ID,User Name,Email,Type,Description,Status,Created Date,Deadline,Escalated\n");
            
            // Data
            for (Complaint c : complaints) {
                csv.append(c.getComplaintId()).append(",")
                   .append(escapeCSV(c.getUserName())).append(",")
                   .append(escapeCSV(c.getUserEmail())).append(",")
                   .append(escapeCSV(c.getComplaintType())).append(",")
                   .append(escapeCSV(c.getDescription())).append(",")
                   .append(c.getStatus()).append(",")
                   .append(c.getCreatedAt()).append(",")
                   .append(c.getDeadline()).append(",")
                   .append(c.isEscalated()).append("\n");
            }
            
            return ResponseEntity.ok()
                    .header("Content-Type", "text/csv")
                    .header("Content-Disposition", "attachment; filename=complaints.csv")
                    .body(csv.toString());
                    
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating CSV: " + e.getMessage());
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}