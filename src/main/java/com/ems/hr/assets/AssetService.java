package com.ems.hr.assets;

import com.ems.employee.repository.EmployeeProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final EmployeeAssetRepository employeeAssetRepository;
    private final EmployeeProfileRepository employeeProfileRepository;

    public AssetService(AssetRepository assetRepository,
                        EmployeeAssetRepository employeeAssetRepository,
                        EmployeeProfileRepository employeeProfileRepository) {
        this.assetRepository = assetRepository;
        this.employeeAssetRepository = employeeAssetRepository;
        this.employeeProfileRepository = employeeProfileRepository;
    }

    public List<Map<String, Object>> getAllAssets(String search, String category, String status) {
        List<Asset> assets;
        if (search != null || category != null || status != null) {
            assets = assetRepository.findByFilters(search, category, status);
        } else {
            assets = assetRepository.findAll();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Asset asset : assets) {
            Map<String, Object> map = assetToMap(asset);
            String employeeName = getAssignedEmployeeName(asset.getId());
            map.put("assignedTo", employeeName != null ? employeeName : "-");
            result.add(map);
        }
        return result;
    }

    public Map<String, Object> getAssetById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Asset not found with id: " + id));
        Map<String, Object> map = assetToMap(asset);
        String employeeName = getAssignedEmployeeName(asset.getId());
        map.put("assignedTo", employeeName != null ? employeeName : "-");
        return map;
    }

    @Transactional
    public Asset createAsset(Asset asset) {
        if (asset.getAssetTag() == null || asset.getAssetTag().isBlank()) {
            asset.setAssetTag(generateAssetTag());
        }
        if (asset.getStatus() == null) {
            asset.setStatus(Asset.AssetStatus.Available);
        }
        if (asset.getCurrencyCode() == null || asset.getCurrencyCode().isBlank()) {
            asset.setCurrencyCode("INR");
        }
        return assetRepository.save(asset);
    }

    @Transactional
    public Asset updateAsset(Long id, Asset updatedAsset) {
        Asset existing = assetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Asset not found with id: " + id));
        existing.setAssetName(updatedAsset.getAssetName());
        existing.setCategory(updatedAsset.getCategory());
        existing.setPurchaseDate(updatedAsset.getPurchaseDate());
        existing.setCost(updatedAsset.getCost());
        existing.setCurrencyCode(updatedAsset.getCurrencyCode());
        existing.setStatus(updatedAsset.getStatus());
        return assetRepository.save(existing);
    }

    @Transactional
    public void deleteAsset(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Asset not found with id: " + id));
        employeeAssetRepository.deleteByCompanyAssetId(id);
        assetRepository.delete(asset);
    }

    @Transactional
    public Map<String, Object> assignAsset(Long id, String employeeCode) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Asset not found with id: " + id));

        String empName = employeeProfileRepository.findFullNameByEmployeeCode(employeeCode);
        if (empName == null) {
            throw new IllegalArgumentException("Employee not found with code: " + employeeCode);
        }

        EmployeeAsset ea = new EmployeeAsset();
        ea.setEmployeeCode(employeeCode);
        ea.setCompanyAssetId(id);
        employeeAssetRepository.save(ea);

        asset.setStatus(Asset.AssetStatus.Assigned);
        assetRepository.save(asset);

        Map<String, Object> result = assetToMap(asset);
        result.put("assignedTo", empName);
        return result;
    }

    @Transactional
    public Map<String, Object> unassignAsset(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Asset not found with id: " + id));

        employeeAssetRepository.deleteByCompanyAssetId(id);

        asset.setStatus(Asset.AssetStatus.Available);
        assetRepository.save(asset);

        Map<String, Object> result = assetToMap(asset);
        result.put("assignedTo", "-");
        return result;
    }

    public Map<String, Object> getAssetStatistics() {
        List<Object[]> counts = assetRepository.countByStatus();
        Map<String, Long> statusCounts = new HashMap<>();
        long total = 0;
        for (Object[] row : counts) {
            String status = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            statusCounts.put(status, count);
            total += count;
        }
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalAssets", total);
        stats.put("available", statusCounts.getOrDefault("Available", 0L));
        stats.put("assigned", statusCounts.getOrDefault("Assigned", 0L));
        stats.put("maintenance", statusCounts.getOrDefault("Maintenance", 0L));
        stats.put("retired", statusCounts.getOrDefault("Retired", 0L));
        return stats;
    }

    private String getAssignedEmployeeName(Long assetId) {
        return employeeAssetRepository.findByCompanyAssetId(assetId)
                .map(ea -> employeeProfileRepository.findFullNameByEmployeeCode(ea.getEmployeeCode()))
                .orElse(null);
    }

    private String generateAssetTag() {
        String prefix = "AST-";
        String maxTag = assetRepository.findMaxAssetTagByPrefix(prefix);
        if (maxTag == null) return prefix + "001";
        String numericPart = maxTag.substring(prefix.length());
        int nextNum = Integer.parseInt(numericPart) + 1;
        return prefix + String.format("%03d", nextNum);
    }

    private Map<String, Object> assetToMap(Asset asset) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", asset.getId());
        map.put("assetTag", asset.getAssetTag());
        map.put("assetName", asset.getAssetName());
        map.put("category", asset.getCategory());
        map.put("purchaseDate", asset.getPurchaseDate());
        map.put("cost", asset.getCost());
        map.put("currencyCode", asset.getCurrencyCode());
        map.put("status", asset.getStatus() != null ? asset.getStatus().name() : "Available");
        map.put("createdAt", asset.getCreatedAt());
        return map;
    }
}