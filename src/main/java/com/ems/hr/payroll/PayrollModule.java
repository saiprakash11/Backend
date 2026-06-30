package com.ems.hr.payroll;

import com.ems.employee.repository.EmployeeProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

class PayslipDTO {

    private String employeeId;
    private String employeeName;
    private String department;
    private String designation;
    private String month;
    private double basicSalary;
    private double hra;
    private double allowances;
    private double deductions;
    private double netSalary;
    private String currencyCode;

    static PayslipDTO fromPayslip(Payslip p, String monthLabel,
                                   String empName, String dept, String desig) {
        PayslipDTO dto = new PayslipDTO();
        dto.employeeId = p.getEmployeeCode();
        dto.employeeName = empName != null ? empName : p.getEmployeeCode();
        dto.department = dept != null ? dept : "Unassigned";
        dto.designation = desig != null ? desig : "";
        dto.month = monthLabel;
        dto.basicSalary = p.getBasicSalary() != null ? p.getBasicSalary().doubleValue() : 0;
        dto.hra = p.getHra() != null ? p.getHra().doubleValue() : 0;
        dto.allowances = p.getAllowances() != null ? p.getAllowances().doubleValue() : 0;
        double pf = p.getPfDeduction() != null ? p.getPfDeduction().doubleValue() : 0;
        double tax = p.getTaxDeduction() != null ? p.getTaxDeduction().doubleValue() : 0;
        double other = p.getOtherDeductions() != null ? p.getOtherDeductions().doubleValue() : 0;
        dto.deductions = pf + tax + other;
        dto.netSalary = p.getNetSalary() != null ? p.getNetSalary().doubleValue() : 0;
        dto.currencyCode = p.getCurrencyCode() != null && !p.getCurrencyCode().isBlank() ? p.getCurrencyCode() : "INR";
        return dto;
    }

    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getDepartment() { return department; }
    public String getDesignation() { return designation; }
    public String getMonth() { return month; }
    public double getBasicSalary() { return basicSalary; }
    public double getHra() { return hra; }
    public double getAllowances() { return allowances; }
    public double getDeductions() { return deductions; }
    public double getNetSalary() { return netSalary; }
    public String getCurrencyCode() { return currencyCode; }
}

@Service
class PayrollService {

    private final PayslipRepository payslipRepository;
    private final EmployeeProfileRepository employeeProfileRepository;

    PayrollService(PayslipRepository payslipRepository,
                   EmployeeProfileRepository employeeProfileRepository) {
        this.payslipRepository = payslipRepository;
        this.employeeProfileRepository = employeeProfileRepository;
    }

    List<PayslipDTO> getAllPayslips(String month) {
        String payrollMonth = resolveMonth(month);
        return queryPayslips(payrollMonth, null);
    }

    List<PayslipDTO> getEmployeePayslips(String employeeId) {
        List<Payslip> payslips = payslipRepository.findByEmployeeCodeOrderByPayrollMonthDesc(employeeId);
        List<PayslipDTO> out = new ArrayList<>();
        for (Payslip p : payslips) {
            String label = formatMonthLabel(p.getPayrollMonth());
            String name = employeeProfileRepository.findFullNameByEmployeeCode(p.getEmployeeCode());
            out.add(PayslipDTO.fromPayslip(p, label, name, null, null));
        }
        return out;
    }

    PayslipDTO getPayslip(String employeeId, String month) {
        String payrollMonth = resolveMonth(month);
        List<Payslip> list = payslipRepository.findByPayrollMonthAndEmployeeCode(payrollMonth, employeeId);
        if (list.isEmpty()) return null;
        Payslip p = list.get(0);
        String label = formatMonthLabel(payrollMonth);
        String name = employeeProfileRepository.findFullNameByEmployeeCode(employeeId);
        return PayslipDTO.fromPayslip(p, label, name, null, null);
    }

    Map<String, Object> getSummary(String month) {
        String payrollMonth = resolveMonth(month);
        try {
            return payslipRepository.getSummaryByMonth(payrollMonth);
        } catch (Exception e) {
            return Map.of("total_gross", 0, "total_deductions", 0, "total_net", 0, "currency_code", "INR");
        }
    }

    private List<PayslipDTO> queryPayslips(String payrollMonth, String employeeCode) {
        List<Payslip> payslips;
        if (employeeCode != null) {
            payslips = payslipRepository.findByPayrollMonthAndEmployeeCode(payrollMonth, employeeCode);
        } else {
            payslips = payslipRepository.findByPayrollMonthOrderByEmployeeCode(payrollMonth);
        }
        String label = formatMonthLabel(payrollMonth);
        List<PayslipDTO> out = new ArrayList<>();
        for (Payslip p : payslips) {
            String name = employeeProfileRepository.findFullNameByEmployeeCode(p.getEmployeeCode());
            out.add(PayslipDTO.fromPayslip(p, label, name, null, null));
        }
        return out;
    }

    private String resolveMonth(String month) {
        if (month != null && month.matches("\\d{4}-\\d{2}")) return month;
        LocalDate now = LocalDate.now();
        return String.format("%04d-%02d", now.getYear(), now.getMonthValue());
    }

    private String formatMonthLabel(String payrollMonth) {
        try {
            String[] parts = payrollMonth.split("-");
            int y = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            return LocalDate.of(y, m, 1)
                    .getMonth()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + y;
        } catch (Exception e) {
            return payrollMonth;
        }
    }
}

@RestController
@RequestMapping("/api/payroll")
class PayrollController {

    private final PayrollService service;

    PayrollController(PayrollService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PayslipDTO>> getAll(@RequestParam(required = false) String month) {
        return ResponseEntity.ok(service.getAllPayslips(month));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> summary(@RequestParam(required = false) String month) {
        return ResponseEntity.ok(service.getSummary(month));
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<?> getOne(
            @PathVariable String employeeId,
            @RequestParam(required = false) String month) {
        PayslipDTO payslip = service.getPayslip(employeeId, month);
        if (payslip == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(payslip);
    }

    @GetMapping("/{employeeId}/pdf")
    public void getPdf(
            @PathVariable String employeeId,
            @RequestParam(required = false) String month,
            jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        PayslipDTO p = service.getPayslip(employeeId, month);
        if (p == null) {
            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Payslip not found");
            return;
        }
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"payslip_" + employeeId + "_" + p.getMonth().replace(" ", "_") + ".pdf\"");
        
        List<String> headers = java.util.Arrays.asList("Payroll Component", "Amount / Detail");
        List<List<String>> rows = new java.util.ArrayList<>();
        rows.add(java.util.Arrays.asList("Company", "Enterprise Management Systems Pvt. Ltd."));
        rows.add(java.util.Arrays.asList("Employee Name", p.getEmployeeName()));
        rows.add(java.util.Arrays.asList("Employee Code", p.getEmployeeId()));
        rows.add(java.util.Arrays.asList("Department", p.getDepartment()));
        rows.add(java.util.Arrays.asList("Designation", p.getDesignation()));
        rows.add(java.util.Arrays.asList("Month", p.getMonth()));
        rows.add(java.util.Arrays.asList("Basic Salary", String.format("%.2f %s", p.getBasicSalary(), p.getCurrencyCode())));
        rows.add(java.util.Arrays.asList("HRA", String.format("%.2f %s", p.getHra(), p.getCurrencyCode())));
        rows.add(java.util.Arrays.asList("Allowances", String.format("%.2f %s", p.getAllowances(), p.getCurrencyCode())));
        rows.add(java.util.Arrays.asList("Deductions", String.format("%.2f %s", p.getDeductions(), p.getCurrencyCode())));
        rows.add(java.util.Arrays.asList("Net Salary", String.format("%.2f %s", p.getNetSalary(), p.getCurrencyCode())));
        
        byte[] pdfBytes = com.ems.hr.reports.SimplePdfGenerator.generatePdf("Enterprise Management Systems — Payslip", headers, rows);
        response.getOutputStream().write(pdfBytes);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PayslipDTO>> getEmployeePayslips(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(service.getEmployeePayslips(employeeId));
    }
}
