package com.ems.management.approvals;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {
    
    private final ApprovalService approvalService;
    
    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }
    
    /**
     * GET /api/approvals
     * Returns all approvals
     */
    @GetMapping
    public ResponseEntity<List<Approval>> getAllApprovals() {
        return ResponseEntity.ok(approvalService.getAllApprovals());
    }
    
    /**
     * GET /api/approvals/{id}
     * Returns approval by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Approval> getApprovalById(@PathVariable Long id) {
        return ResponseEntity.ok(approvalService.getApprovalById(id));
    }
    
    /**
     * POST /api/approvals
     * Creates a new approval request
     */
    @PostMapping
    public ResponseEntity<Approval> createApproval(@RequestBody ApprovalDTO approvalDTO) {
        Approval approval = approvalService.createApproval(approvalDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(approval);
    }
    
    /**
     * GET /api/approvals/status/{status}
     * Returns approvals by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Approval>> getApprovalsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(approvalService.getApprovalsByStatus(status));
    }
    
    /**
     * GET /api/approvals/pending
     * Returns all pending approvals
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Approval>> getPendingApprovals() {
        return ResponseEntity.ok(approvalService.getPendingApprovals());
    }
    
    /**
     * GET /api/approvals/pending/{approver}
     * Returns pending approvals for specific approver
     */
    @GetMapping("/pending/{approver}")
    public ResponseEntity<List<Approval>> getPendingApprovalsForUser(@PathVariable String approver) {
        return ResponseEntity.ok(approvalService.getPendingApprovalsForUser(approver));
    }
    
    /**
     * GET /api/approvals/requested-by/{requestedBy}
     * Returns approvals by who requested them
     */
    @GetMapping("/requested-by/{requestedBy}")
    public ResponseEntity<List<Approval>> getApprovalsByRequestedBy(@PathVariable String requestedBy) {
        return ResponseEntity.ok(approvalService.getApprovalsByRequestedBy(requestedBy));
    }
    
    /**
     * GET /api/approvals/approver/{approver}
     * Returns approvals for a specific approver
     */
    @GetMapping("/approver/{approver}")
    public ResponseEntity<List<Approval>> getApprovalsByApprover(@PathVariable String approver) {
        return ResponseEntity.ok(approvalService.getApprovalsByApprover(approver));
    }
    
    /**
     * PUT /api/approvals/{id}/approve
     * Approves a request
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String comments = request.getOrDefault("comments", "");
        Approval approval = approvalService.approveRequest(id, comments);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Approval request approved successfully");
        response.put("approval", approval);
        return ResponseEntity.ok(response);
    }
    
    /**
     * PUT /api/approvals/{id}/reject
     * Rejects a request
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String comments = request.getOrDefault("comments", "Rejected");
        Approval approval = approvalService.rejectRequest(id, comments);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Approval request rejected");
        response.put("approval", approval);
        return ResponseEntity.ok(response);
    }
    
    /**
     * PUT /api/approvals/{id}
     * Updates an approval
     */
    @PutMapping("/{id}")
    public ResponseEntity<Approval> updateApproval(
            @PathVariable Long id,
            @RequestBody ApprovalDTO approvalDTO) {
        
        Approval approval = approvalService.updateApproval(id, approvalDTO);
        return ResponseEntity.ok(approval);
    }
    
    /**
     * DELETE /api/approvals/{id}
     * Deletes an approval
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteApproval(@PathVariable Long id) {
        approvalService.deleteApproval(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Approval deleted successfully");
        return ResponseEntity.ok(response);
    }
}
