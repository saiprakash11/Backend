package com.ems.hr.payroll;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payslips")
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", length = 50)
    private String employeeCode;

    @Column(name = "payroll_month", length = 10)
    private String payrollMonth;

    @Column(name = "basic_salary")
    private BigDecimal basicSalary;

    private BigDecimal hra;

    @Column(name = "allowances")
    private BigDecimal allowances;

    @Column(name = "pf_deduction")
    private BigDecimal pfDeduction;

    @Column(name = "tax_deduction")
    private BigDecimal taxDeduction;

    @Column(name = "other_deductions")
    private BigDecimal otherDeductions;

    @Column(name = "net_salary")
    private BigDecimal netSalary;

    @Column(name = "currency_code", length = 5)
    private String currencyCode;

    @Column(length = 30)
    private String status;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @JsonProperty("id")
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @JsonProperty("employee_code")
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    @JsonProperty("payroll_month")
    public String getPayrollMonth() { return payrollMonth; }
    public void setPayrollMonth(String payrollMonth) { this.payrollMonth = payrollMonth; }
    @JsonProperty("basic_salary")
    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }
    @JsonProperty("hra")
    public BigDecimal getHra() { return hra; }
    public void setHra(BigDecimal hra) { this.hra = hra; }
    @JsonProperty("allowances")
    public BigDecimal getAllowances() { return allowances; }
    public void setAllowances(BigDecimal allowances) { this.allowances = allowances; }
    @JsonProperty("pf_deduction")
    public BigDecimal getPfDeduction() { return pfDeduction; }
    public void setPfDeduction(BigDecimal pfDeduction) { this.pfDeduction = pfDeduction; }
    @JsonProperty("tax_deduction")
    public BigDecimal getTaxDeduction() { return taxDeduction; }
    public void setTaxDeduction(BigDecimal taxDeduction) { this.taxDeduction = taxDeduction; }
    @JsonProperty("other_deductions")
    public BigDecimal getOtherDeductions() { return otherDeductions; }
    public void setOtherDeductions(BigDecimal otherDeductions) { this.otherDeductions = otherDeductions; }
    @JsonProperty("net_salary")
    public BigDecimal getNetSalary() { return netSalary; }
    public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }
    @JsonProperty("currency_code")
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    @JsonProperty("status")
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    @JsonProperty("generated_at")
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
