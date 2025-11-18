package com.resolveit.service;

import com.resolveit.model.Complaint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Send email when complaint is registered
    public void sendComplaintRegistrationEmail(Complaint complaint) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(complaint.getUserEmail());
            message.setSubject("ResolveIT - Complaint Registered Successfully");
            message.setText(String.format(
                "Dear %s,\n\n" +
                "Your complaint has been registered successfully.\n\n" +
                "Complaint Details:\n" +
                "Complaint ID: CMP-%d\n" +
                "Type: %s\n" +
                "Description: %s\n" +
                "Status: %s\n" +
                "Created Date: %s\n\n" +
                "You can track your complaint status using the Complaint ID.\n\n" +
                "Thank you for using ResolveIT.\n\n" +
                "Best regards,\n" +
                "ResolveIT Team",
                complaint.getUserName(),
                complaint.getComplaintId(),
                complaint.getComplaintType(),
                complaint.getDescription(),
                complaint.getStatus(),
                complaint.getCreatedAt()
            ));
            message.setFrom("noreply@resolveit.com");

            mailSender.send(message);
            System.out.println("Registration email sent to: " + complaint.getUserEmail());
        } catch (Exception e) {
            System.err.println("Failed to send registration email: " + e.getMessage());
        }
    }

    // Send email when complaint is escalated
    public void sendEscalationEmail(Complaint complaint) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(complaint.getUserEmail());
            message.setSubject("ResolveIT - Your Complaint Has Been Escalated");
            message.setText(String.format(
                "Dear %s,\n\n" +
                "Your complaint (ID: CMP-%d) has been escalated to our admin team for priority attention.\n\n" +
                "This means your issue is being reviewed at a higher level to ensure faster resolution.\n\n" +
                "Complaint Details:\n" +
                "Type: %s\n" +
                "Description: %s\n" +
                "Escalated Date: %s\n\n" +
                "We apologize for any inconvenience and are working to resolve this as quickly as possible.\n\n" +
                "Best regards,\n" +
                "ResolveIT Team",
                complaint.getUserName(),
                complaint.getComplaintId(),
                complaint.getComplaintType(),
                complaint.getDescription(),
                complaint.getUpdatedAt()
            ));
            message.setFrom("noreply@resolveit.com");

            mailSender.send(message);
            System.out.println("Escalation email sent to: " + complaint.getUserEmail());
        } catch (Exception e) {
            System.err.println("Failed to send escalation email: " + e.getMessage());
        }
    }

    // Send email when complaint is resolved
    public void sendResolutionEmail(Complaint complaint) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(complaint.getUserEmail());
            message.setSubject("ResolveIT - Your Complaint Has Been Resolved");
            message.setText(String.format(
                "Dear %s,\n\n" +
                "Good news! Your complaint (ID: CMP-%d) has been resolved.\n\n" +
                "Complaint Details:\n" +
                "Type: %s\n" +
                "Description: %s\n" +
                "Resolution Note: %s\n" +
                "Resolved Date: %s\n\n" +
                "If you're satisfied with the resolution, no further action is needed.\n" +
                "If you have any concerns, please feel free to contact us.\n\n" +
                "Thank you for your patience.\n\n" +
                "Best regards,\n" +
                "ResolveIT Team",
                complaint.getUserName(),
                complaint.getComplaintId(),
                complaint.getComplaintType(),
                complaint.getDescription(),
                complaint.getResolutionNote() != null ? complaint.getResolutionNote() : "No notes provided",
                complaint.getUpdatedAt()
            ));
            message.setFrom("noreply@resolveit.com");

            mailSender.send(message);
            System.out.println("Resolution email sent to: " + complaint.getUserEmail());
        } catch (Exception e) {
            System.err.println("Failed to send resolution email: " + e.getMessage());
        }
    }

    // Send SMS notification (using Twilio or similar service)
    // For now, this is a placeholder - you'll need to integrate with SMS provider
    public void sendSMS(String phoneNumber, String message) {
        try {
            // TODO: Integrate with SMS service like Twilio
            // Example with Twilio:
            // Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            // Message.creator(
            //     new PhoneNumber(phoneNumber),
            //     new PhoneNumber(TWILIO_NUMBER),
            //     message
            // ).create();
            
            System.out.println("SMS would be sent to: " + phoneNumber);
            System.out.println("Message: " + message);
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
    }

    // Send status update SMS
    public void sendStatusUpdateSMS(Complaint complaint, String phoneNumber) {
        String message = String.format(
            "ResolveIT Update: Your complaint CMP-%d is now %s. Check details at http://resolveit.com/track",
            complaint.getComplaintId(),
            complaint.getStatus()
        );
        sendSMS(phoneNumber, message);
    }
}