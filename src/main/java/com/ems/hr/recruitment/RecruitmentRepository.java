package com.ems.hr.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentRepository extends JpaRepository<RecruitmentCandidate, String> {
}
