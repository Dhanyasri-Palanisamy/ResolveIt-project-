package com.resolveit.repository;
//import com.resolveit.model.Complaint; // or wherever it's located

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.resolveit.model.Complaint;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // You can add custom query methods later if needed
    // Example: List<Complaint> findByStatus(String status);
}
