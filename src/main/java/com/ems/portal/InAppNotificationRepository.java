package com.ems.portal;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InAppNotificationRepository extends JpaRepository<InAppNotification, Long> {

    int countByEmployeeCodeAndReadFalse(String employeeCode);

    List<InAppNotification> findByEmployeeCodeAndReadFalseOrderByCreatedAtDesc(String employeeCode);

    List<InAppNotification> findByEmployeeCodeAndTimeCategoryOrderByCreatedAtDesc(String employeeCode, String timeCategory);

    List<InAppNotification> findByEmployeeCodeOrderByCreatedAtDesc(String employeeCode);

    java.util.Optional<InAppNotification> findByIdAndEmployeeCode(Long id, String employeeCode);
}
