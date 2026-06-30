package com.ems.hr.assets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByAssetTag(String assetTag);

    @Query(value = """
        SELECT a.* FROM company_assets a
        LEFT JOIN employee_assets ea ON ea.company_asset_id = a.id
        LEFT JOIN employee_profiles ep ON ep.employee_code = ea.employee_code
        WHERE (:search IS NULL OR :search = '' OR 
               LOWER(a.asset_tag) LIKE LOWER(CONCAT('%', :search, '%')) OR 
               LOWER(a.asset_name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
               LOWER(ep.full_name) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:category IS NULL OR :category = '' OR LOWER(a.category) = LOWER(:category))
        AND (:status IS NULL OR :status = '' OR a.status = :status)
        ORDER BY a.id DESC
        """, nativeQuery = true)
    List<Asset> findByFilters(
            @Param("search") String search,
            @Param("category") String category,
            @Param("status") String status
    );

    @Query(value = "SELECT status, COUNT(*) FROM company_assets GROUP BY status", nativeQuery = true)
    List<Object[]> countByStatus();

    @Query(value = "SELECT MAX(a.asset_tag) FROM company_assets a WHERE a.asset_tag LIKE CONCAT(:prefix, '%')", nativeQuery = true)
    String findMaxAssetTagByPrefix(@Param("prefix") String prefix);
}
