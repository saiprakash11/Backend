package com.ems.hr.settings;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SettingsService {

    private final SystemSettingRepository repository;

    public SettingsService(SystemSettingRepository repository) {
        this.repository = repository;
    }

    public SettingsPayload load() {
        SettingsPayload payload = new SettingsPayload();
        payload.setCompanyName(value("company_name"));
        payload.setCompanyLogoUrl(value("company_logo_url"));
        payload.setCompanyEmail(value("company_email"));
        payload.setCompanyPhone(value("company_phone"));
        payload.setCompanyAddress(value("company_address"));
        payload.setTimeZone(value("time_zone"));
        payload.setCurrency(value("currency"));
        payload.setWorkingHours(value("working_hours"));
        payload.setShiftStartTime(value("shift_start_time"));
        payload.setShiftEndTime(value("shift_end_time"));
        payload.setLateMarkThreshold(value("late_mark_threshold"));
        payload.setOvertimeRules(value("overtime_rules"));
        payload.setAnnualLeave(value("annual_leave"));
        payload.setSickLeave(value("sick_leave"));
        payload.setCasualLeave(value("casual_leave"));
        payload.setApprovalWorkflow(value("leave_approval_workflow"));
        payload.setPasswordPolicy(value("password_policy"));
        payload.setSessionTimeout(value("session_timeout"));
        payload.setLoginRestrictions(value("login_restrictions"));
        payload.setEmailNotifications(value("email_notifications"));
        payload.setInAppNotifications(value("in_app_notifications"));
        payload.setLeaveNotifications(value("leave_notifications"));
        payload.setPayrollNotifications(value("payroll_notifications"));
        payload.setThemeSettings(value("theme_settings"));
        payload.setAttendancePreferences(value("attendance_preferences"));
        return payload;
    }

    public Map<String, Object> save(SettingsPayload payload) {
        upsert("company_name", payload.getCompanyName());
        upsert("company_logo_url", payload.getCompanyLogoUrl());
        upsert("company_email", payload.getCompanyEmail());
        upsert("company_phone", payload.getCompanyPhone());
        upsert("company_address", payload.getCompanyAddress());
        upsert("time_zone", payload.getTimeZone());
        upsert("currency", payload.getCurrency());
        upsert("working_hours", payload.getWorkingHours());
        upsert("shift_start_time", payload.getShiftStartTime());
        upsert("shift_end_time", payload.getShiftEndTime());
        upsert("late_mark_threshold", payload.getLateMarkThreshold());
        upsert("overtime_rules", payload.getOvertimeRules());
        upsert("annual_leave", payload.getAnnualLeave());
        upsert("sick_leave", payload.getSickLeave());
        upsert("casual_leave", payload.getCasualLeave());
        upsert("leave_approval_workflow", payload.getApprovalWorkflow());
        upsert("password_policy", payload.getPasswordPolicy());
        upsert("session_timeout", payload.getSessionTimeout());
        upsert("login_restrictions", payload.getLoginRestrictions());
        upsert("email_notifications", payload.getEmailNotifications());
        upsert("in_app_notifications", payload.getInAppNotifications());
        upsert("leave_notifications", payload.getLeaveNotifications());
        upsert("payroll_notifications", payload.getPayrollNotifications());
        upsert("theme_settings", payload.getThemeSettings());
        upsert("attendance_preferences", payload.getAttendancePreferences());
        return grouped();
    }

    public Map<String, Object> grouped() {
        SettingsPayload payload = load();
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("company", Map.of(
                "companyName", payload.getCompanyName(),
                "companyLogoUrl", payload.getCompanyLogoUrl(),
                "companyEmail", payload.getCompanyEmail(),
                "companyPhone", payload.getCompanyPhone(),
                "companyAddress", payload.getCompanyAddress(),
                "timeZone", payload.getTimeZone(),
                "currency", payload.getCurrency()
        ));
        out.put("attendance", Map.of(
                "workingHours", payload.getWorkingHours(),
                "shiftStartTime", payload.getShiftStartTime(),
                "shiftEndTime", payload.getShiftEndTime(),
                "lateMarkThreshold", payload.getLateMarkThreshold(),
                "overtimeRules", payload.getOvertimeRules(),
                "attendancePreferences", payload.getAttendancePreferences()
        ));
        out.put("leave", Map.of(
                "annualLeave", payload.getAnnualLeave(),
                "sickLeave", payload.getSickLeave(),
                "casualLeave", payload.getCasualLeave(),
                "approvalWorkflow", payload.getApprovalWorkflow()
        ));
        out.put("security", Map.of(
                "passwordPolicy", payload.getPasswordPolicy(),
                "sessionTimeout", payload.getSessionTimeout(),
                "loginRestrictions", payload.getLoginRestrictions()
        ));
        out.put("notifications", Map.of(
                "emailNotifications", payload.getEmailNotifications(),
                "inAppNotifications", payload.getInAppNotifications(),
                "leaveNotifications", payload.getLeaveNotifications(),
                "payrollNotifications", payload.getPayrollNotifications(),
                "themeSettings", payload.getThemeSettings()
        ));
        return out;
    }

    public String updateLogo(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return value("company_logo_url");
        }
        String original = file.getOriginalFilename() == null ? "logo" : file.getOriginalFilename();
        String ext = original.contains(".") ? original.substring(original.lastIndexOf(".")).toLowerCase() : ".png";
        Path dir = Paths.get("uploads", "settings");
        Files.createDirectories(dir);
        String name = "company-logo-" + System.currentTimeMillis() + ext;
        Path target = dir.resolve(name);
        Files.copy(file.getInputStream(), target);
        String url = "/uploads/settings/" + name;
        upsert("company_logo_url", url);
        return url;
    }

    private String value(String key) {
        return repository.findBySettingKey(key).map(SystemSetting::getSettingValue).orElse("");
    }

    private void upsert(String key, String value) {
        if (value == null) return;
        SystemSetting setting = repository.findBySettingKey(key).orElseGet(SystemSetting::new);
        setting.setSettingKey(key);
        setting.setSettingValue(value);
        setting.setDescription(key.replace('_', ' '));
        repository.save(setting);
    }
}
