package com.ems.hr.payroll;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {

    List<Payslip> findByEmployeeCodeOrderByPayrollMonthDesc(String employeeCode);

    List<Payslip> findByPayrollMonthOrderByEmployeeCode(String payrollMonth);

    List<Payslip> findByPayrollMonthAndEmployeeCode(String payrollMonth, String employeeCode);

    @Query(value = "SELECT " +
           "COALESCE(SUM(p.basic_salary + p.hra + p.allowances), 0) AS total_gross, " +
           "COALESCE(SUM(p.pf_deduction + p.tax_deduction + p.other_deductions), 0) AS total_deductions, " +
           "COALESCE(SUM(p.net_salary), 0) AS total_net, " +
           "COALESCE(MAX(p.currency_code), 'INR') AS currency_code " +
           "FROM payslips p WHERE p.payroll_month = :month", nativeQuery = true)
    java.util.Map<String, Object> getSummaryByMonth(@Param("month") String payrollMonth);
}
