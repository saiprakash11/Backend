package com.ems.hr.assets;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/assets")
@PreAuthorize("hasAnyRole('ADMIN','HR')")
public class AssetController {

    private final AssetService service;

    public AssetController(AssetService service) {
        this.service = service;
    }

    /**
     * GET /api/assets - List all assets with optional filters
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllAssets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(service.getAllAssets(search, category, status));
    }

    /**
     * GET /api/assets/{id} - Get asset by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAsset(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAssetById(id));
    }

    /**
     * POST /api/assets - Create new asset
     * Body: { "assetName": "...", "category": "...", "purchaseDate": "...", "cost": 0, "status": "..." }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAsset(@RequestBody Map<String, Object> body) {
        Asset asset = new Asset();
        asset.setAssetName((String) body.get("assetName"));
        asset.setCategory((String) body.get("category"));
        asset.setAssetTag((String) body.get("assetTag"));
        
        // Parse purchaseDate
        if (body.get("purchaseDate") != null) {
            asset.setPurchaseDate(java.time.LocalDate.parse(body.get("purchaseDate").toString()));
        }
        
        // Parse cost
        if (body.get("cost") != null) {
            asset.setCost(new java.math.BigDecimal(body.get("cost").toString()));
        }
        
        // Parse status
        if (body.get("status") != null) {
            asset.setStatus(Asset.AssetStatus.valueOf(body.get("status").toString()));
        }
        
        // Parse currencyCode
        if (body.get("currencyCode") != null) {
            asset.setCurrencyCode(body.get("currencyCode").toString());
        }

        Asset created = service.createAsset(asset);
        Map<String, Object> result = service.getAssetById(created.getId());
        return ResponseEntity.ok(result);
    }

    /**
     * PUT /api/assets/{id} - Update asset
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAsset(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Asset asset = new Asset();
        asset.setAssetName((String) body.get("assetName"));
        asset.setCategory((String) body.get("category"));
        
        if (body.get("purchaseDate") != null) {
            asset.setPurchaseDate(java.time.LocalDate.parse(body.get("purchaseDate").toString()));
        }
        
        if (body.get("cost") != null) {
            asset.setCost(new java.math.BigDecimal(body.get("cost").toString()));
        }
        
        if (body.get("status") != null) {
            asset.setStatus(Asset.AssetStatus.valueOf(body.get("status").toString()));
        }
        
        if (body.get("currencyCode") != null) {
            asset.setCurrencyCode(body.get("currencyCode").toString());
        }

        Asset updated = service.updateAsset(id, asset);
        Map<String, Object> result = service.getAssetById(updated.getId());
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/assets/{id} - Delete asset
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        service.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/assets/{id}/assign - Assign asset to employee
     * Body: { "employeeCode": "..." }
     */
    @PutMapping("/{id}/assign")
    public ResponseEntity<Map<String, Object>> assignAsset(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String employeeCode = body.get("employeeCode");
        if (employeeCode == null || employeeCode.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "employeeCode is required"));
        }
        return ResponseEntity.ok(service.assignAsset(id, employeeCode));
    }

    /**
     * PUT /api/assets/{id}/unassign - Unassign asset from employee
     */
    @PutMapping("/{id}/unassign")
    public ResponseEntity<Map<String, Object>> unassignAsset(@PathVariable Long id) {
        return ResponseEntity.ok(service.unassignAsset(id));
    }

    /**
     * GET /api/assets/stats - Get asset statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(service.getAssetStatistics());
    }
}