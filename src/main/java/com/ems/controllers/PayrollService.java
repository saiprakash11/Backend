package com.ems.controllers;

import com.ems.employee.repository.EmployeeProfileRepository;
import com.ems.hr.payroll.Payslip;
import com.ems.hr.payroll.PayslipDTO;
import com.ems.hr.payroll.PayslipRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service("controllersPayrollService")
public class PayrollService {

    private final PayslipRepository payslipRepository;
    private final EmployeeProfileRepository employeeProfileRepository;

    public PayrollService(PayslipRepository payslipRepository,
                          EmployeeProfileRepository employeeProfileRepository) {
        this.payslipRepository = payslipRepository;
        this.employeeProfileRepository = employeeProfileRepository;
    }

    public List<PayslipDTO> getAllPayslips(String month) {
        return queryPayslips(resolveMonth(month), null);
    }

    public Map<String, Object> getSummary(String month) {
        String payrollMonth = resolveMonth(month);
        try {
            return payslipRepository.getSummaryByMonth(payrollMonth);
        } catch (Exception e) {
            return Map.of("total_gross", 0, "total_deductions", 0, "total_net", 0, "currency_code", "INR");
        }
    }

    public PayslipDTO getPayslip(String employeeId, String month) {
        String payrollMonth = resolveMonth(month);
        List<Payslip> list = payslipRepository.findByPayrollMonthAndEmployeeCode(payrollMonth, employeeId);
        if (list.isEmpty()) return null;
        Payslip p = list.get(0);
        String label = formatMonthLabel(payrollMonth);
        String name = employeeProfileRepository.findFullNameByEmployeeCode(employeeId);
        return PayslipDTO.fromPayslip(p, label, name, null, null);
    }

    public List<PayslipDTO> getEmployeePayslips(String employeeId) {
        List<Payslip> payslips = payslipRepository.findByEmployeeCodeOrderByPayrollMonthDesc(employeeId);
        List<PayslipDTO> out = new ArrayList<>();
        for (Payslip p : payslips) {
            String label = formatMonthLabel(p.getPayrollMonth());
            String name = employeeProfileRepository.findFullNameByEmployeeCode(p.getEmployeeCode());
            out.add(PayslipDTO.fromPayslip(p, label, name, null, null));
        }
        return out;
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
