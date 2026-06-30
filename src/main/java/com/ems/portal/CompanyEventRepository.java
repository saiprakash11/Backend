package com.ems.portal;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompanyEventRepository extends JpaRepository<CompanyEvent, Long> {

    List<CompanyEvent> findAllByOrderByEventDateDescCreatedAtDesc();
}
