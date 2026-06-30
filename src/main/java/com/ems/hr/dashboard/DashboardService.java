package com.ems.hr.dashboard;

import com.ems.auth.entity.User;
import com.ems.auth.repository.UserRepository;
import com.ems.employee.repository.EmployeeProfileRepository;
import com.ems.hr.attendance.AttendanceRecordRepository;
import com.ems.hr.leave.EmployeeLeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeLeaveRequestRepository leaveRequestRepository;
    private final EmployeeProfileRepository employeeProfileRepository;

    public DashboardService(UserRepository userRepository,
                            AttendanceRecordRepository attendanceRecordRepository,
                            EmployeeLeaveRequestRepository leaveRequestRepository,
                            EmployeeProfileRepository employeeProfileRepository) {
        this.userRepository = userRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeProfileRepository = employeeProfileRepository;
    }

    public DashboardSummary getSummary() {
        int totalEmployees = (int) userRepository.countByIsAliveTrue();
        LocalDate today = LocalDate.now();
        long presentToday = attendanceRecordRepository.countPresentOnDate(today);
        long onLeave = leaveRequestRepository.countApprovedOnDate(today);
        LocalDate thirtyDaysAgo = today.minusDays(30);
        int newJoiners = employeeProfileRepository.countByDateOfJoiningAfter(thirtyDaysAgo);
        return new DashboardSummary((int) totalEmployees, (int) presentToday, (int) onLeave, newJoiners);
    }
}
