package com.ems.hr.assets;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmployeeAssetRepository extends JpaRepository<EmployeeAsset, Long> {

    Optional<EmployeeAsset> findByCompanyAssetId(Long companyAssetId);

    void deleteByCompanyAssetId(Long companyAssetId);
}
