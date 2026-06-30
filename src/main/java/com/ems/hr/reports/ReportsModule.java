package com.ems.hr.reports;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReportsModule {

    private final EntityManager em;

    public ReportsModule(EntityManager em) {
        this.em = em;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getReportsOverview() {
        List<Map<String, Object>> general = new ArrayList<>();
        List<Map<String, Object>> department = new ArrayList<>();
        List<Map<String, Object>> analytics = new ArrayList<>();
        List<Map<String, Object>> employeeData = getEmployeeReport();
        List<Map<String, Object>> deptData = getDepartmentReport();
        general.add(Map.of("id", 1, "title", "Employee Directory", "date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()), "status", "Completed"));
        general.add(Map.of("id", 2, "title", "Payroll Summary", "date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()), "status", "Completed"));
        general.add(Map.of("id", 3, "title", "Attendance Overview", "date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()), "status", "Pending"));
        for (Map<String, Object> d : deptData) {
            department.add(Map.of("id", d.get("dept_code"), "department", d.get("dept_name"), "employees", d.get("employee_count"), "avgRating", "-"));
        }
        analytics.add(Map.of("metric", "Total Employees", "value", employeeData.size(), "change", "+" + Math.round(employeeData.size() * 0.05)));
        analytics.add(Map.of("metric", "Active Departments", "value", deptData.size(), "change", "0"));
        return ResponseEntity.ok(Map.of("general", general, "department", department, "analytics", analytics));
    }

    @GetMapping("/employee")
    public List<Map<String, Object>> getEmployeeReport() {
        return queryForList(
                "SELECT employee_code, full_name, email, phone_number, department, designation, date_of_joining, salary, status, gender FROM employee_profiles ORDER BY employee_code ASC",
                "employee_code", "full_name", "email", "phone_number", "department", "designation", "date_of_joining", "salary", "status", "gender"
        );
    }

    @GetMapping("/department")
    public List<Map<String, Object>> getDepartmentReport() {
        return queryForList(
                "SELECT d.dept_code, d.dept_name, d.head_employee_code, (SELECT COUNT(*) FROM employee_profiles ep WHERE ep.department = d.dept_name) as employee_count FROM department_master d ORDER BY d.dept_code ASC",
                "dept_code", "dept_name", "head_employee_code", "employee_count"
        );
    }

    @GetMapping("/recruitment")
    public List<Map<String, Object>> getRecruitmentReport() {
        return queryForList(
                "SELECT id, full_name, email, phone, source, created_at FROM candidates ORDER BY id DESC",
                "id", "full_name", "email", "phone", "source", "created_at"
        );
    }

    @GetMapping("/payroll")
    public List<Map<String, Object>> getPayrollReport() {
        return queryForList(
                "SELECT employee_code, payroll_month, basic_salary, hra, allowances, pf_deduction, tax_deduction, other_deductions, net_salary, currency_code, status FROM payslips ORDER BY payroll_month DESC, employee_code ASC",
                "employee_code", "payroll_month", "basic_salary", "hra", "allowances", "pf_deduction", "tax_deduction", "other_deductions", "net_salary", "currency_code", "status"
        );
    }

    @GetMapping("/export/{reportType}/{format}")
    public void exportReport(
            @PathVariable String reportType,
            @PathVariable String format,
            HttpServletResponse response) throws IOException {

        System.out.println("Exporting " + reportType + " report as " + format);

        List<String> headers = new ArrayList<>();
        List<List<String>> data = new ArrayList<>();
        String title = "";

        if ("employee".equalsIgnoreCase(reportType)) {
            title = "Employee Directory Report";
            headers = Arrays.asList("Code", "Name", "Email", "Phone", "Department", "Designation", "Joining Date", "Salary", "Status", "Gender");
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT employee_code, full_name, email, phone_number, department, designation, date_of_joining, salary, status, gender FROM employee_profiles"
            ).getResultList();
            for (Object[] r : rows) {
                data.add(Arrays.asList(str(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4]), str(r[5]), str(r[6]), str(r[7]), str(r[8]), str(r[9])));
            }
        } else if ("department".equalsIgnoreCase(reportType)) {
            title = "Department Master Report";
            headers = Arrays.asList("Dept Code", "Dept Name", "Head Code", "Employees Count");
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT d.dept_code, d.dept_name, d.head_employee_code, (SELECT COUNT(*) FROM employee_profiles ep WHERE ep.department = d.dept_name) as emp_count FROM department_master d"
            ).getResultList();
            for (Object[] r : rows) {
                data.add(Arrays.asList(str(r[0]), str(r[1]), str(r[2]), str(r[3])));
            }
        } else if ("recruitment".equalsIgnoreCase(reportType)) {
            title = "Recruitment Funnel Report";
            headers = Arrays.asList("ID", "Candidate Name", "Email", "Phone", "Source");
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT id, full_name, email, phone, source FROM candidates"
            ).getResultList();
            for (Object[] r : rows) {
                data.add(Arrays.asList(str(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4])));
            }
        } else if ("payroll".equalsIgnoreCase(reportType)) {
            title = "Payroll Run Report";
            headers = Arrays.asList("Emp Code", "Month", "Basic", "HRA", "Allowances", "PF Ded.", "Tax Ded.", "Net Salary", "Status");
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT employee_code, payroll_month, basic_salary, hra, allowances, pf_deduction, tax_deduction, net_salary, status FROM payslips"
            ).getResultList();
            for (Object[] r : rows) {
                data.add(Arrays.asList(str(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4]), str(r[5]), str(r[6]), str(r[7]), str(r[8])));
            }
        } else if ("attendance".equalsIgnoreCase(reportType)) {
            title = "Attendance Summary Report";
            headers = Arrays.asList("Emp Code", "Work Date", "Check In", "Check Out", "Status", "Hours Worked");
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT employee_code, work_date, check_in, check_out, status, hours_worked FROM attendance_records"
            ).getResultList();
            for (Object[] r : rows) {
                data.add(Arrays.asList(str(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4]), str(r[5])));
            }
        } else if ("leave".equalsIgnoreCase(reportType)) {
            title = "Leave Request Report";
            headers = Arrays.asList("ID", "Emp Code", "Emp Name", "Leave Type", "Start Date", "End Date", "Days", "Status");
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT id, employee_code, employee_name, leave_type, start_date, end_date, number_of_days, status FROM leave_requests"
            ).getResultList();
            for (Object[] r : rows) {
                data.add(Arrays.asList(str(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4]), str(r[5]), str(r[6]), str(r[7])));
            }
        } else {
            title = "System Report";
            headers = Arrays.asList("Message");
            data.add(Collections.singletonList("Report type " + reportType + " not recognized."));
        }

        if ("csv".equalsIgnoreCase(format)) {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + reportType + "_report.csv\"");
            writeCsv(headers, data, response.getOutputStream());
        } else if ("excel".equalsIgnoreCase(format)) {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + reportType + "_report.xls\"");
            writeTsv(headers, data, response.getOutputStream());
        } else if ("pdf".equalsIgnoreCase(format)) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + reportType + "_report.pdf\"");
            byte[] pdfBytes = SimplePdfGenerator.generatePdf(title, headers, data);
            response.getOutputStream().write(pdfBytes);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Unsupported format: " + format);
        }
    }

    private List<Map<String, Object>> queryForList(String sql, String... columns) {
        List<Object[]> rows = em.createNativeQuery(sql).getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 0; i < columns.length && i < row.length; i++) {
                map.put(columns[i], row[i]);
            }
            result.add(map);
        }
        return result;
    }

    private String str(Object val) {
        if (val == null) return "";
        return val.toString();
    }

    private void writeCsv(List<String> headers, List<List<String>> rows, OutputStream os) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            sb.append(escapeCsv(headers.get(i)));
            if (i < headers.size() - 1) sb.append(",");
        }
        sb.append("\n");
        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                sb.append(escapeCsv(row.get(i)));
                if (i < row.size() - 1) sb.append(",");
            }
            sb.append("\n");
        }
        os.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        os.flush();
    }

    private String escapeCsv(String val) {
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }

    private void writeTsv(List<String> headers, List<List<String>> rows, OutputStream os) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            sb.append(headers.get(i));
            if (i < headers.size() - 1) sb.append("\t");
        }
        sb.append("\n");
        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                sb.append(row.get(i));
                if (i < row.size() - 1) sb.append("\t");
            }
            sb.append("\n");
        }
        os.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        os.flush();
    }
}