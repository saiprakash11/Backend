package com.ems.hr.documents;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class AdminDocumentService {

    private final EmployeeDocumentRepository documentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private static final String UPLOAD_DIR = "uploads/documents/admin/";
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "image/png", "image/jpeg", "image/webp"
    );

    public AdminDocumentService(EmployeeDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Map<String, Object>> getAllDocuments(String employeeCode, String type, String search) {
        StringBuilder sql = new StringBuilder(
                "SELECT ed.id, ed.employee_code, ed.document_name, ed.document_type, " +
                "ed.file_path, ed.uploaded_at, ed.status, ep.full_name " +
                "FROM employee_documents ed " +
                "LEFT JOIN employee_profiles ep ON ep.employee_code = ed.employee_code WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (employeeCode != null && !employeeCode.isBlank()) {
            sql.append(" AND ed.employee_code = ?");
            params.add(employeeCode);
        }
        if (type != null && !type.isBlank()) {
            sql.append(" AND LOWER(ed.document_type) = LOWER(?)");
            params.add(type);
        }
        if (search != null && !search.isBlank()) {
            sql.append(" AND (LOWER(ed.document_name) LIKE ? OR LOWER(ep.full_name) LIKE ? OR LOWER(ed.document_type) LIKE ?)");
            String sp = "%" + search.toLowerCase() + "%";
            params.add(sp); params.add(sp); params.add(sp);
        }
        sql.append(" ORDER BY ed.uploaded_at DESC");

        var query = entityManager.createNativeQuery(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }
        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> doc = new LinkedHashMap<>();
            doc.put("id", row[0]);
            doc.put("employeeCode", row[1]);
            doc.put("documentName", row[2]);
            doc.put("documentType", mapDocumentType(row[3]));
            doc.put("filePath", row[4]);
            doc.put("uploadedAt", row[5]);
            doc.put("status", row[6] != null ? row[6] : "pending");
            doc.put("employeeName", row[7] != null ? row[7] : "General");
            result.add(doc);
        }
        return result;
    }

    @Transactional
    public Map<String, Object> uploadDocument(MultipartFile file, String documentType,
            String employeeCode, String documentName, String notes) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Allowed: PDF, DOC, DOCX, PNG, JPG, JPEG, WEBP");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String originalName = file.getOriginalFilename();
        String storedFileName = timestamp + "_" + originalName;

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        Path filePath = Paths.get(UPLOAD_DIR + storedFileName);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }

        EmployeeDocument doc = new EmployeeDocument();
        doc.setEmployeeCode(employeeCode != null && !employeeCode.isBlank() ? employeeCode : null);
        doc.setDocumentName(documentName);
        doc.setDocumentType(documentType);
        doc.setFilePath("/uploads/documents/admin/" + storedFileName);
        documentRepository.save(doc);

        return Map.of("success", true, "message", "Document uploaded successfully");
    }

    @Transactional
    public void deleteDocument(Long id) {
        var opt = documentRepository.findById(id);
        if (opt.isEmpty()) {
            throw new NoSuchElementException("Document not found with id: " + id);
        }
        EmployeeDocument doc = opt.get();
        String filePath = doc.getFilePath();
        if (filePath != null) {
            String fsPath = filePath.replace("/uploads/documents/admin/", UPLOAD_DIR);
            File file = new File(fsPath);
            if (file.exists()) file.delete();
        }
        documentRepository.delete(doc);
    }

    public Map<String, Object> getDocumentStatistics() {
        List<EmployeeDocument> all = documentRepository.findAll();
        long total = all.size();
        long verified = all.stream().filter(d -> "verified".equalsIgnoreCase(d.getStatus())).count();
        long pending = all.stream().filter(d -> "pending".equalsIgnoreCase(d.getStatus())).count();

        long expiring = all.stream()
                .filter(d -> "verified".equalsIgnoreCase(d.getStatus())
                        && d.getUploadedAt() != null
                        && d.getUploadedAt().isAfter(java.time.LocalDateTime.now().minusDays(30)))
                .count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalDocuments", total);
        stats.put("verified", verified);
        stats.put("pending", pending);
        stats.put("expiring", expiring);
        return stats;
    }

    private String mapDocumentType(Object type) {
        if (type == null) return "other";
        String t = type.toString().toLowerCase();
        if (t.contains("policy")) return "policy";
        if (t.contains("contract")) return "contract";
        if (t.contains("certificate") || t.contains("cert")) return "certificate";
        if (t.contains("id") || t.contains("passport") || t.contains("license")) return "id";
        return "other";
    }
}