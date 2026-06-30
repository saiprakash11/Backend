package com.ems.hr.documents;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("/api/documents")
@PreAuthorize("hasAnyRole('ADMIN','HR')")
public class AdminDocumentController {

    private final AdminDocumentService service;

    public AdminDocumentController(AdminDocumentService service) {
        this.service = service;
    }

    /**
     * GET /api/documents - List all documents with optional filters
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllDocuments(
            @RequestParam(required = false) String employeeCode,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(service.getAllDocuments(employeeCode, type, search));
    }

    /**
     * POST /api/documents - Upload a new document
     * Multipart form data: file, documentType, employeeCode, documentName
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam("employeeCode") String employeeCode,
            @RequestParam("documentName") String documentName,
            @RequestParam(required = false) String notes) {
        
        Map<String, Object> result = service.uploadDocument(file, documentType, employeeCode, documentName, notes);
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/documents/{id} - Delete a document
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        service.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/documents/stats - Get document statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(service.getDocumentStatistics());
    }
}