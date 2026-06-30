package com.ems.employee.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @Column(name = "employee_code", length = 50)
    private String employeeCode;

    @Column(name = "notify_leave_status")
    private Boolean notifyLeaveStatus = true;

    @Column(name = "notify_payslip")
    private Boolean notifyPayslip = true;

    @Column(name = "notify_performance_reminders")
    private Boolean notifyPerformanceReminders = true;

    @Column(name = "notify_announcements")
    private Boolean notifyAnnouncements = true;

    @Column(name = "notify_attendance_reminders")
    private Boolean notifyAttendanceReminders = true;

    @Column(name = "digest_frequency")
    private String digestFrequency = "realtime";

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public Boolean getNotifyLeaveStatus() { return notifyLeaveStatus; }
    public void setNotifyLeaveStatus(Boolean notifyLeaveStatus) { this.notifyLeaveStatus = notifyLeaveStatus; }
    public Boolean getNotifyPayslip() { return notifyPayslip; }
    public void setNotifyPayslip(Boolean notifyPayslip) { this.notifyPayslip = notifyPayslip; }
    public Boolean getNotifyPerformanceReminders() { return notifyPerformanceReminders; }
    public void setNotifyPerformanceReminders(Boolean notifyPerformanceReminders) { this.notifyPerformanceReminders = notifyPerformanceReminders; }
    public Boolean getNotifyAnnouncements() { return notifyAnnouncements; }
    public void setNotifyAnnouncements(Boolean notifyAnnouncements) { this.notifyAnnouncements = notifyAnnouncements; }
    public Boolean getNotifyAttendanceReminders() { return notifyAttendanceReminders; }
    public void setNotifyAttendanceReminders(Boolean notifyAttendanceReminders) { this.notifyAttendanceReminders = notifyAttendanceReminders; }
    public String getDigestFrequency() { return digestFrequency; }
    public void setDigestFrequency(String digestFrequency) { this.digestFrequency = digestFrequency; }
}
