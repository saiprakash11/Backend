package com.ems.management.approvals;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    
    List<Approval> findByStatus(String status);
    
    List<Approval> findByRequestedBy(String requestedBy);
    
    List<Approval> findByApprover(String approver);
    
    List<Approval> findByRequestType(String requestType);
    
    Optional<Approval> findByRequestId(String requestId);
    
    List<Approval> findByStatusAndApprover(String status, String approver);
}
