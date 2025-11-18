package com.resolveit.repository;

import com.resolveit.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByComplaintId(Long complaintId);
    Optional<Feedback> findByComplaintIdAndUserEmail(Long complaintId, String userEmail);
    boolean existsByComplaintIdAndUserEmail(Long complaintId, String userEmail);
}