package com.ems.management.approvals;

import com.ems.management.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ApprovalService {

    private final ApprovalRepository approvalRepository;

    public ApprovalService(ApprovalRepository approvalRepository) {
        this.approvalRepository = approvalRepository;
    }

    public Approval createApproval(ApprovalDTO dto) {
        Approval approval = new Approval();
        approval.setRequestId(UUID.randomUUID().toString());
        approval.setRequestType(dto.getRequestType());
        approval.setRequestedBy(dto.getRequestedBy());
        approval.setApprover(dto.getApprover());
        approval.setDescription(dto.getDescription());
        approval.setAmount(dto.getAmount());
        approval.setStatus("Pending");
        // createdAt and updatedAt are set by @PrePersist in the entity
        return approvalRepository.save(approval);
    }

    public Approval getApprovalById(Long id) {
        return approvalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Approval not found with id: " + id));
    }

    public Approval getApprovalByRequestId(String requestId) {
        return approvalRepository.findByRequestId(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Approval not found with requestId: " + requestId));
    }

    public List<Approval> getAllApprovals() {
        return approvalRepository.findAll();
    }

    public List<Approval> getApprovalsByStatus(String status) {
        return approvalRepository.findByStatus(status);
    }

    public List<Approval> getApprovalsByRequestedBy(String requestedBy) {
        return approvalRepository.findByRequestedBy(requestedBy);
    }

    public List<Approval> getApprovalsByApprover(String approver) {
        return approvalRepository.findByApprover(approver);
    }

    public List<Approval> getPendingApprovals() {
        return approvalRepository.findByStatus("Pending");
    }

    public List<Approval> getPendingApprovalsForUser(String approver) {
        return approvalRepository.findByStatusAndApprover("Pending", approver);
    }

    public Approval approveRequest(Long id, String comments) {
        Approval approval = getApprovalById(id);
        approval.setStatus("Approved");
        approval.setApprovedAt(LocalDateTime.now());
        approval.setComments(comments);
        // updatedAt handled by @PreUpdate
        return approvalRepository.save(approval);
    }

    public Approval rejectRequest(Long id, String comments) {
        Approval approval = getApprovalById(id);
        approval.setStatus("Rejected");
        approval.setComments(comments);
        // updatedAt handled by @PreUpdate
        return approvalRepository.save(approval);
    }

    public Approval updateApproval(Long id, ApprovalDTO dto) {
        Approval approval = getApprovalById(id);

        if (dto.getRequestType() != null) {
            approval.setRequestType(dto.getRequestType());
        }
        if (dto.getDescription() != null) {
            approval.setDescription(dto.getDescription());
        }
        if (dto.getAmount() != null) {
            approval.setAmount(dto.getAmount());
        }
        if (dto.getApprover() != null) {
            approval.setApprover(dto.getApprover());
        }

        // updatedAt handled by @PreUpdate
        return approvalRepository.save(approval);
    }

    public void deleteApproval(Long id) {
        Approval approval = getApprovalById(id);
        approvalRepository.delete(approval);
    }
}
