package com.ems.auth.repository;

import com.ems.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByEmployeeCode(String employeeCode);

    long countByIsAliveTrue();

    @Query("SELECT u.employeeCode FROM User u WHERE u.role IN :roles ORDER BY u.employeeCode")
    List<String> findEmployeeCodesByRoleIn(@Param("roles") List<String> roles);

    @Query("SELECT u.employeeCode FROM User u WHERE u.role = :role ORDER BY u.employeeCode")
    List<String> findEmployeeCodesByRole(@Param("role") String role);
}
