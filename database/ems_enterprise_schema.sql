-- EMS Enterprise Schema – production-style MySQL
-- Import: mysql -u root -p < ems_enterprise_schema.sql

CREATE DATABASE IF NOT EXISTS emp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE emp;
SET NAMES utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `task_attachments`;
DROP TABLE IF EXISTS `task_comments`;
DROP TABLE IF EXISTS `project_tasks`;
DROP TABLE IF EXISTS `project_milestones`;
DROP TABLE IF EXISTS `project_team_members`;
DROP TABLE IF EXISTS `meeting_notes`;
DROP TABLE IF EXISTS `meeting_attendees`;
DROP TABLE IF EXISTS `workflow_history`;
DROP TABLE IF EXISTS `approval_comments`;
DROP TABLE IF EXISTS `leave_approvals`;
DROP TABLE IF EXISTS `payroll_transactions`;
DROP TABLE IF EXISTS `tax_deductions`;
DROP TABLE IF EXISTS `bonus_incentives`;
DROP TABLE IF EXISTS `reimbursements`;
DROP TABLE IF EXISTS `payslips`;
DROP TABLE IF EXISTS `employee_salary`;
DROP TABLE IF EXISTS `payroll`;
DROP TABLE IF EXISTS `employee_assets`;
DROP TABLE IF EXISTS `asset_assignments`;
DROP TABLE IF EXISTS `asset_maintenance`;
DROP TABLE IF EXISTS `asset_requests`;
DROP TABLE IF EXISTS `employee_ratings`;
DROP TABLE IF EXISTS `kpi_metrics`;
DROP TABLE IF EXISTS `interview_schedule`;
DROP TABLE IF EXISTS `job_applications`;
DROP TABLE IF EXISTS `offer_letters`;
DROP TABLE IF EXISTS `onboarding_tasks`;
DROP TABLE IF EXISTS `exit_interviews`;
DROP TABLE IF EXISTS `offboarding_tracking`;
DROP TABLE IF EXISTS `attendance_break_logs`;
DROP TABLE IF EXISTS `attendance_overtime`;
DROP TABLE IF EXISTS `attendance_regularization_requests`;
DROP TABLE IF EXISTS `attendance_records`;
DROP TABLE IF EXISTS `user_roles`;
DROP TABLE IF EXISTS `login_history`;
DROP TABLE IF EXISTS `password_reset_tokens`;
DROP TABLE IF EXISTS `sessions`;
DROP TABLE IF EXISTS `employee_documents`;
DROP TABLE IF EXISTS `employee_bank_details`;
DROP TABLE IF EXISTS `employee_emergency_contacts`;
DROP TABLE IF EXISTS `employee_addresses`;
DROP TABLE IF EXISTS `employee_education`;
DROP TABLE IF EXISTS `employee_experience`;
DROP TABLE IF EXISTS `employee_certifications`;
DROP TABLE IF EXISTS `notification_preferences`;
DROP TABLE IF EXISTS `in_app_notifications`;
DROP TABLE IF EXISTS `email_logs`;
DROP TABLE IF EXISTS `sms_logs`;
DROP TABLE IF EXISTS `manager_feedback`;
DROP TABLE IF EXISTS `performance_goals`;
DROP TABLE IF EXISTS `priority_tasks`;
DROP TABLE IF EXISTS `employee_statistics`;
DROP TABLE IF EXISTS `attendance_summary`;
DROP TABLE IF EXISTS `leave_summary`;
DROP TABLE IF EXISTS `payroll_summary`;
DROP TABLE IF EXISTS `dashboard_metrics`;
DROP TABLE IF EXISTS `activity_logs`;
DROP TABLE IF EXISTS `audit_logs`;
DROP TABLE IF EXISTS `expense_claims`;
DROP TABLE IF EXISTS `travel_requests`;
DROP TABLE IF EXISTS `leave_balances`;
DROP TABLE IF EXISTS `leave_requests`;
DROP TABLE IF EXISTS `onboarding_tracking`;
DROP TABLE IF EXISTS `recruitment`;
DROP TABLE IF EXISTS `approvals`;
DROP TABLE IF EXISTS `performance_reviews`;
DROP TABLE IF EXISTS `meetings`;
DROP TABLE IF EXISTS `projects`;
DROP TABLE IF EXISTS `holiday_calendar`;
DROP TABLE IF EXISTS `attendance_shifts`;
DROP TABLE IF EXISTS `attendance`;
DROP TABLE IF EXISTS `employee_profiles`;
DROP TABLE IF EXISTS `employees`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `roles`;
DROP TABLE IF EXISTS `permissions`;
DROP TABLE IF EXISTS `leave_types`;
DROP TABLE IF EXISTS `salary_structures`;
DROP TABLE IF EXISTS `salary_components`;
DROP TABLE IF EXISTS `job_postings`;
DROP TABLE IF EXISTS `candidates`;
DROP TABLE IF EXISTS `company_assets`;
DROP TABLE IF EXISTS `announcements`;
DROP TABLE IF EXISTS `company_events`;
DROP TABLE IF EXISTS `expense_categories`;
DROP TABLE IF EXISTS `department_master`;
DROP TABLE IF EXISTS `designation_master`;
DROP TABLE IF EXISTS `system_settings`;
DROP TABLE IF EXISTS `training_enrollments`;
DROP TABLE IF EXISTS `training_courses`;
DROP TABLE IF EXISTS `appraisals`;
DROP TABLE IF EXISTS `employee_kpis`;
DROP TABLE IF EXISTS `notification_messages`;
DROP TABLE IF EXISTS `onboarding_sessions`;
DROP TABLE IF EXISTS `file_upload_audit`;
DROP TABLE IF EXISTS `employee_profile_photos`;

SET FOREIGN_KEY_CHECKS = 1;


-- =========================================================
-- CORE AUTHENTICATION & USER MANAGEMENT
-- =========================================================

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(30) NOT NULL UNIQUE,
    role_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_code VARCHAR(80) NOT NULL UNIQUE,
    module_name VARCHAR(80) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    username VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN','HR','MANAGER','EMPLOYEE') NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_alive BOOLEAN NOT NULL DEFAULT TRUE,
    last_active DATETIME NULL,
    first_login DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(150) DEFAULT 'SYSTEM',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modified_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modified_by VARCHAR(150),
    profile_photo VARCHAR(500) NULL,
    profile_photo_name VARCHAR(255) NULL,
    profile_photo_content_type VARCHAR(100) NULL,
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

CREATE TABLE user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_role (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE login_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    login_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    status ENUM('SUCCESS','FAILED') NOT NULL DEFAULT 'SUCCESS',
    INDEX idx_login_user (user_id),
    CONSTRAINT fk_login_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at DATETIME NOT NULL,
    used_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_token VARCHAR(512) NOT NULL UNIQUE,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sessions_user (user_id),
    CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- ADMIN MASTER DATA
-- =========================================================

CREATE TABLE department_master (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dept_code VARCHAR(20) NOT NULL UNIQUE,
    dept_name VARCHAR(100) NOT NULL,
    head_employee_code VARCHAR(20),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE designation_master (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    designation_code VARCHAR(20) NOT NULL UNIQUE,
    designation_name VARCHAR(100) NOT NULL,
    level_rank INT DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE system_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT NOT NULL,
    description VARCHAR(255),
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================================================
-- EMPLOYEE MANAGEMENT (+ HR legacy employees)
-- =========================================================

CREATE TABLE employees (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(20),
    department VARCHAR(100),
    designation VARCHAR(100),
    join_date VARCHAR(20),
    status VARCHAR(50) DEFAULT 'Active',
    salary DOUBLE DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE employee_profiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    phone_number VARCHAR(20),
    date_of_birth DATE,
    residential_address TEXT,
    emergency_contact_name VARCHAR(150),
    emergency_contact_phone VARCHAR(20),
    department VARCHAR(100) NOT NULL,
    designation VARCHAR(100) NOT NULL,
    date_of_joining DATE NOT NULL,
    reporting_manager_code VARCHAR(20),
    work_location VARCHAR(100),
    salary DECIMAL(12,2),
    status ENUM('Active','Inactive','On Leave','Terminated') DEFAULT 'Active',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ep_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ep_mgr FOREIGN KEY (reporting_manager_code) REFERENCES users(employee_code) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE employee_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    document_name VARCHAR(255) NOT NULL,
    document_type VARCHAR(100),
    document_data LONGBLOB,
    content_type VARCHAR(100),
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_edoc_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE employee_bank_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    bank_name VARCHAR(150) NOT NULL,
    account_number VARCHAR(30) NOT NULL,
    ifsc_code VARCHAR(20) NOT NULL,
    account_holder_name VARCHAR(150),
    is_primary BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ebank_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE employee_emergency_contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    contact_name VARCHAR(150) NOT NULL,
    relationship VARCHAR(50),
    phone_number VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_eec_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE employee_addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    address_type ENUM('CURRENT','PERMANENT','OFFICE') NOT NULL,
    line1 VARCHAR(255) NOT NULL,
    line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    country VARCHAR(80) DEFAULT 'India',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_eaddr_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE employee_education (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    degree VARCHAR(150) NOT NULL,
    institution VARCHAR(200) NOT NULL,
    year_completed YEAR,
    grade VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_eedu_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE employee_experience (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    company_name VARCHAR(200) NOT NULL,
    job_title VARCHAR(150) NOT NULL,
    start_date DATE,
    end_date DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_eexp_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE employee_certifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    certification_name VARCHAR(200) NOT NULL,
    issuing_body VARCHAR(200),
    issue_date DATE,
    expiry_date DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ecert_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- ATTENDANCE MANAGEMENT
-- =========================================================

CREATE TABLE attendance_shifts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shift_code VARCHAR(20) NOT NULL UNIQUE,
    shift_name VARCHAR(100) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    grace_minutes INT DEFAULT 15,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE attendance (
    id VARCHAR(20) PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL,
    employee_name VARCHAR(150),
    date VARCHAR(20) NOT NULL,
    status VARCHAR(50) NOT NULL,
    INDEX idx_att_hr_emp (employee_id)
) ENGINE=InnoDB;

CREATE TABLE attendance_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    work_date DATE NOT NULL,
    check_in TIME,
    check_out TIME,
    status ENUM('Present','Absent','Late','Half Day','On Leave') DEFAULT 'Present',
    total_hours DECIMAL(5,2),
    hours_worked DECIMAL(5,2),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_att_rec (employee_code, work_date),
    CONSTRAINT fk_att_rec_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE attendance_break_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    work_date DATE NOT NULL,
    break_start TIME NOT NULL,
    break_end TIME,
    break_minutes INT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_att_br_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE attendance_overtime (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    work_date DATE NOT NULL,
    overtime_hours DECIMAL(5,2) NOT NULL,
    reason TEXT,
    status ENUM('Pending','Approved','Rejected') DEFAULT 'Pending',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_att_ot_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE attendance_regularization_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    work_date DATE NOT NULL,
    requested_check_in TIME,
    requested_check_out TIME,
    reason TEXT,
    status ENUM('Pending','Approved','Rejected') DEFAULT 'Pending',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_att_reg_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- LEAVE MANAGEMENT
-- =========================================================

CREATE TABLE leave_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_code VARCHAR(20) NOT NULL UNIQUE,
    type_name VARCHAR(80) NOT NULL,
    annual_quota INT DEFAULT 0,
    is_paid BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE holiday_calendar (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    holiday_date DATE NOT NULL UNIQUE,
    holiday_name VARCHAR(150) NOT NULL,
    region VARCHAR(80) DEFAULT 'India',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE leave_requests (
    id VARCHAR(36) PRIMARY KEY,
    employee_code VARCHAR(20),
    employee_id VARCHAR(20),
    employee_name VARCHAR(150),
    leave_type VARCHAR(50) NOT NULL,
    start_date DATE,
    end_date DATE,
    from_date VARCHAR(20),
    to_date VARCHAR(20),
    number_of_days INT DEFAULT 1,
    reason TEXT,
    status ENUM('Pending','Approved','Rejected','Cancelled') DEFAULT 'Pending',
    applied_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(150),
    CONSTRAINT fk_lr_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE leave_balances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    year INT NOT NULL DEFAULT 2026,
    annual_leave INT DEFAULT 18,
    sick_leave INT DEFAULT 10,
    casual_leave INT DEFAULT 6,
    privilege_leave INT DEFAULT 15,
    loss_of_pay INT DEFAULT 5,
    used_annual INT DEFAULT 0,
    used_sick INT DEFAULT 0,
    used_casual INT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_lb (employee_code, year),
    CONSTRAINT fk_lb_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE leave_approvals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    leave_request_id VARCHAR(36) NOT NULL,
    approver_code VARCHAR(20) NOT NULL,
    decision ENUM('Approved','Rejected') NOT NULL,
    comments TEXT,
    decided_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_la_leave FOREIGN KEY (leave_request_id) REFERENCES leave_requests(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_la_approver FOREIGN KEY (approver_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- PAYROLL MANAGEMENT (INR)
-- =========================================================

CREATE TABLE salary_structures (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    structure_code VARCHAR(30) NOT NULL UNIQUE,
    structure_name VARCHAR(150) NOT NULL,
    currency_code CHAR(3) NOT NULL DEFAULT 'INR',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE salary_components (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    component_code VARCHAR(30) NOT NULL UNIQUE,
    component_name VARCHAR(100) NOT NULL,
    component_type ENUM('EARNING','DEDUCTION') NOT NULL,
    is_taxable BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE employee_salary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    structure_id BIGINT NOT NULL,
    basic_salary DECIMAL(12,2) NOT NULL,
    hra DECIMAL(12,2) DEFAULT 0,
    allowances DECIMAL(12,2) DEFAULT 0,
    effective_from DATE NOT NULL,
    currency_code CHAR(3) NOT NULL DEFAULT 'INR',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_esal (employee_code, effective_from),
    CONSTRAINT fk_esal_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_esal_struct FOREIGN KEY (structure_id) REFERENCES salary_structures(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE payroll (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payroll_month CHAR(7) NOT NULL,
    processed_by VARCHAR(20),
    total_gross DECIMAL(14,2) NOT NULL DEFAULT 0,
    total_deductions DECIMAL(14,2) NOT NULL DEFAULT 0,
    total_net DECIMAL(14,2) NOT NULL DEFAULT 0,
    currency_code CHAR(3) NOT NULL DEFAULT 'INR',
    status ENUM('Draft','Processing','Completed','Failed') DEFAULT 'Draft',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_payroll_month (payroll_month)
) ENGINE=InnoDB;

CREATE TABLE payroll_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payroll_id BIGINT NOT NULL,
    employee_code VARCHAR(20) NOT NULL,
    transaction_type ENUM('CREDIT','DEBIT') NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pt_payroll FOREIGN KEY (payroll_id) REFERENCES payroll(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_pt_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE payslips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    payroll_month CHAR(7) NOT NULL,
    basic_salary DECIMAL(12,2) NOT NULL,
    hra DECIMAL(12,2) DEFAULT 0,
    allowances DECIMAL(12,2) DEFAULT 0,
    pf_deduction DECIMAL(12,2) DEFAULT 0,
    tax_deduction DECIMAL(12,2) DEFAULT 0,
    other_deductions DECIMAL(12,2) DEFAULT 0,
    net_salary DECIMAL(12,2) NOT NULL,
    currency_code CHAR(3) NOT NULL DEFAULT 'INR',
    status ENUM('Generated','Paid','On Hold') DEFAULT 'Generated',
    generated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_payslip (employee_code, payroll_month),
    CONSTRAINT fk_ps_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE tax_deductions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    financial_year CHAR(9) NOT NULL,
    tds_amount DECIMAL(12,2) NOT NULL,
    regime ENUM('OLD','NEW') DEFAULT 'NEW',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tax_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE bonus_incentives (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    bonus_type VARCHAR(80) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    payout_month CHAR(7),
    status ENUM('Pending','Approved','Paid') DEFAULT 'Pending',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bonus_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE reimbursements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    status ENUM('Pending','Approved','Rejected','Paid') DEFAULT 'Pending',
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reimb_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- PROJECT MANAGEMENT
-- =========================================================

CREATE TABLE projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    project_name VARCHAR(255),
    description TEXT,
    project_manager VARCHAR(150) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME,
    target_completion_date DATETIME NOT NULL,
    priority ENUM('Low','Medium','High','Critical') DEFAULT 'Medium',
    status ENUM('Planning','In Progress','On Hold','Completed','Cancelled') DEFAULT 'Planning',
    progress INT DEFAULT 0,
    budget DECIMAL(14,2),
    currency_code CHAR(3) DEFAULT 'INR',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE project_team_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    team_member VARCHAR(150),
    employee_code VARCHAR(20),
    role_in_project VARCHAR(80),
    CONSTRAINT fk_ptm_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE project_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    assigned_to VARCHAR(20),
    due_date DATE,
    status ENUM('Open','In Progress','Done','Blocked') DEFAULT 'Open',
    progress_percent INT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ptask_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE task_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    author_code VARCHAR(20) NOT NULL,
    comment_text TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tc_task FOREIGN KEY (task_id) REFERENCES project_tasks(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE task_attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ta_task FOREIGN KEY (task_id) REFERENCES project_tasks(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE project_milestones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    milestone_name VARCHAR(200) NOT NULL,
    target_date DATE,
    status ENUM('Pending','Achieved','Missed') DEFAULT 'Pending',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pm_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE priority_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE,
    priority ENUM('Low','Medium','High') DEFAULT 'Medium',
    status ENUM('Pending','In Progress','Done') DEFAULT 'Pending',
    progress_percent INT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pri_task_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- MEETINGS & COLLABORATION
-- =========================================================

CREATE TABLE meetings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    organizer VARCHAR(150) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    location VARCHAR(255),
    meeting_link VARCHAR(500),
    status ENUM('Scheduled','Completed','Cancelled') DEFAULT 'Scheduled',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE meeting_attendees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    meeting_id BIGINT NOT NULL,
    attendee VARCHAR(150),
    employee_code VARCHAR(20),
    rsvp_status ENUM('Pending','Accepted','Declined') DEFAULT 'Pending',
    CONSTRAINT fk_ma_meeting FOREIGN KEY (meeting_id) REFERENCES meetings(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE meeting_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    meeting_id BIGINT NOT NULL,
    author_code VARCHAR(20) NOT NULL,
    note_text TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mn_meeting FOREIGN KEY (meeting_id) REFERENCES meetings(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE announcements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    published_by VARCHAR(20),
    published_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    audience ENUM('ALL','HR','MANAGEMENT','EMPLOYEE') DEFAULT 'ALL'
) ENGINE=InnoDB;

CREATE TABLE company_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_name VARCHAR(200) NOT NULL,
    event_date DATE NOT NULL,
    venue VARCHAR(255),
    description TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================================================
-- PERFORMANCE MANAGEMENT
-- =========================================================

CREATE TABLE performance_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20),
    employee_name VARCHAR(150),
    reviewer_id VARCHAR(20),
    reviewer_name VARCHAR(150),
    employee_code VARCHAR(20),
    reviewer_code VARCHAR(20),
    performance_rating INT NOT NULL,
    strengths TEXT,
    areas_for_improvement TEXT,
    overall_comments TEXT,
    review_period VARCHAR(50),
    review_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    next_review_date DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE manager_feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    manager_code VARCHAR(20) NOT NULL,
    review_date DATE,
    rating_score DECIMAL(3,1),
    feedback_text TEXT,
    quarter VARCHAR(10),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mf_emp FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_mf_mgr FOREIGN KEY (manager_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE performance_goals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    goal_title VARCHAR(255),
    description TEXT,
    target_date DATE,
    quarter VARCHAR(10),
    progress_pct INT DEFAULT 0,
    status ENUM('In Progress','Achieved','Missed') DEFAULT 'In Progress',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pg_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE employee_kpis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    kpi_name VARCHAR(150) NOT NULL,
    target_value DECIMAL(12,2),
    actual_value DECIMAL(12,2),
    unit VARCHAR(30),
    period VARCHAR(20),
    status ENUM('On Track','At Risk','Off Track') DEFAULT 'On Track',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ekpi_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE appraisals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    appraisal_period VARCHAR(20) NOT NULL,
    appraisal_rating DECIMAL(4,2) NOT NULL,
    recommendation VARCHAR(255),
    promotion_recommendation BOOLEAN DEFAULT FALSE,
    comments TEXT,
    appraisal_date DATE NOT NULL,
    next_appraisal_date DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_app_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE kpi_metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    metric_code VARCHAR(30) NOT NULL UNIQUE,
    metric_name VARCHAR(150) NOT NULL,
    target_value DECIMAL(12,2),
    unit VARCHAR(30),
    department VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE employee_ratings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    kpi_id BIGINT NOT NULL,
    rating_value DECIMAL(5,2) NOT NULL,
    review_period VARCHAR(20),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_er_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_er_kpi FOREIGN KEY (kpi_id) REFERENCES kpi_metrics(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- RECRUITMENT & HIRING
-- =========================================================

CREATE TABLE recruitment (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(120),
    phone VARCHAR(20),
    position VARCHAR(100),
    department VARCHAR(100),
    applied_date VARCHAR(20),
    status VARCHAR(50)
) ENGINE=InnoDB;

CREATE TABLE job_postings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    posting_code VARCHAR(20) NOT NULL UNIQUE,
    job_title VARCHAR(150) NOT NULL,
    department VARCHAR(100),
    openings INT DEFAULT 1,
    status ENUM('Open','Closed','On Hold') DEFAULT 'Open',
    posted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE candidates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(20),
    source VARCHAR(80),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE job_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    posting_id BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    status ENUM('Applied','Screening','Interview','Offer','Rejected','Hired') DEFAULT 'Applied',
    applied_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ja_post FOREIGN KEY (posting_id) REFERENCES job_postings(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ja_cand FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE interview_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    interviewer_code VARCHAR(20),
    scheduled_at DATETIME NOT NULL,
    mode ENUM('In Person','Video','Phone') DEFAULT 'Video',
    status ENUM('Scheduled','Completed','Cancelled') DEFAULT 'Scheduled',
    CONSTRAINT fk_is_app FOREIGN KEY (application_id) REFERENCES job_applications(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE offer_letters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    offered_ctc DECIMAL(12,2) NOT NULL,
    currency_code CHAR(3) DEFAULT 'INR',
    joining_date DATE,
    status ENUM('Draft','Sent','Accepted','Declined') DEFAULT 'Draft',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ol_app FOREIGN KEY (application_id) REFERENCES job_applications(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- ONBOARDING & OFFBOARDING
-- =========================================================

CREATE TABLE onboarding_tracking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    employee_name VARCHAR(150) NOT NULL,
    department VARCHAR(100) NOT NULL,
    joining_date DATE NOT NULL,
    current_step VARCHAR(100) DEFAULT 'Personal Details',
    current_step_num INT DEFAULT 1,
    total_steps INT DEFAULT 5,
    progress_percent INT DEFAULT 0,
    assigned_hr VARCHAR(150),
    status ENUM('Pending','In Progress','Completed') DEFAULT 'Pending',
    step1_done BOOLEAN DEFAULT FALSE,
    step2_done BOOLEAN DEFAULT FALSE,
    step3_done BOOLEAN DEFAULT FALSE,
    step4_done BOOLEAN DEFAULT FALSE,
    step5_done BOOLEAN DEFAULT FALSE,
    notes TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_onb_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE onboarding_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL UNIQUE,
    employee_code VARCHAR(20) NULL,
    current_step INT NOT NULL DEFAULT 1,
    session_data_json LONGTEXT NOT NULL,
    status ENUM('ACTIVE','COMPLETED','ABANDONED') NOT NULL DEFAULT 'ACTIVE',
    last_step_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_onboarding_sessions_employee_code (employee_code),
    INDEX idx_onboarding_sessions_status (status),
    CONSTRAINT fk_onboarding_sessions_employee FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE onboarding_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    onboarding_id BIGINT NOT NULL,
    task_name VARCHAR(200) NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    due_date DATE,
    CONSTRAINT fk_ot_onb FOREIGN KEY (onboarding_id) REFERENCES onboarding_tracking(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE asset_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    asset_tag VARCHAR(50) NOT NULL,
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    returned_at DATETIME NULL,
    CONSTRAINT fk_aa_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE offboarding_tracking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    last_working_day DATE NOT NULL,
    reason VARCHAR(255),
    status ENUM('Initiated','In Progress','Completed') DEFAULT 'Initiated',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_off_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE exit_interviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    conducted_by VARCHAR(20),
    feedback TEXT,
    conducted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ei_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- NOTIFICATIONS & COMMUNICATION
-- =========================================================

CREATE TABLE in_app_notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    title VARCHAR(255),
    message TEXT,
    time_category ENUM('TODAY','THIS WEEK','OLDER') DEFAULT 'TODAY',
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_in_app_notifications_employee_code (employee_code),
    INDEX idx_in_app_notifications_is_read (is_read),
    CONSTRAINT fk_notif_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE notification_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_code VARCHAR(20) NOT NULL,
    sender_name VARCHAR(150),
    recipient_code VARCHAR(20) NOT NULL,
    recipient_role VARCHAR(30) NOT NULL,
    recipient_type ENUM('INDIVIDUAL','DEPARTMENT','TEAM','ALL') NOT NULL DEFAULT 'ALL',
    title VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    priority ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL DEFAULT 'MEDIUM',
    category VARCHAR(100),
    send_date DATE,
    expiry_date DATE,
    status ENUM('UNREAD','READ','ARCHIVED') NOT NULL DEFAULT 'UNREAD',
    channel ENUM('IN_APP','EMAIL','SMS') NOT NULL DEFAULT 'IN_APP',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    archived_at DATETIME NULL,
    deleted_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_notification_messages_sender (sender_code),
    INDEX idx_notification_messages_recipient (recipient_code),
    INDEX idx_notification_messages_is_read (is_read),
    INDEX idx_notification_messages_recipient_read (recipient_code, is_read),
    CONSTRAINT fk_notification_messages_sender FOREIGN KEY (sender_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_notification_messages_recipient FOREIGN KEY (recipient_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE notification_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    notify_leave_status BOOLEAN DEFAULT TRUE,
    notify_payslip BOOLEAN DEFAULT TRUE,
    notify_performance_reminders BOOLEAN DEFAULT TRUE,
    notify_announcements BOOLEAN DEFAULT TRUE,
    notify_attendance_reminders BOOLEAN DEFAULT TRUE,
    email_alerts BOOLEAN DEFAULT TRUE,
    push_alerts BOOLEAN DEFAULT TRUE,
    payroll_alerts BOOLEAN DEFAULT TRUE,
    digest_frequency ENUM('realtime','daily','weekly') DEFAULT 'realtime',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_np_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE email_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_email VARCHAR(150) NOT NULL,
    subject VARCHAR(255),
    status ENUM('Sent','Failed','Queued') DEFAULT 'Queued',
    sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE sms_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL,
    message TEXT,
    status ENUM('Sent','Failed','Queued') DEFAULT 'Queued',
    sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================================================
-- APPROVALS & WORKFLOW
-- =========================================================

CREATE TABLE approvals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(50) UNIQUE,
    request_type VARCHAR(50) NOT NULL,
    requested_by VARCHAR(150) NOT NULL,
    approver VARCHAR(150) NOT NULL,
    description TEXT,
    amount DECIMAL(12,2),
    currency_code CHAR(3) DEFAULT 'INR',
    status ENUM('Pending','Approved','Rejected') NOT NULL DEFAULT 'Pending',
    comments TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at DATETIME,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE workflow_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    approval_id BIGINT NOT NULL,
    step_name VARCHAR(100) NOT NULL,
    actor_code VARCHAR(20),
    action_taken VARCHAR(80),
    acted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wh_approval FOREIGN KEY (approval_id) REFERENCES approvals(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE approval_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    approval_id BIGINT NOT NULL,
    author_code VARCHAR(20) NOT NULL,
    comment_text TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ac_approval FOREIGN KEY (approval_id) REFERENCES approvals(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- ASSETS & IT MANAGEMENT
-- =========================================================

CREATE TABLE company_assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asset_tag VARCHAR(50) NOT NULL UNIQUE,
    asset_name VARCHAR(200) NOT NULL,
    category VARCHAR(80),
    purchase_date DATE,
    cost DECIMAL(12,2),
    currency_code CHAR(3) DEFAULT 'INR',
    status ENUM('Available','Assigned','Maintenance','Retired') DEFAULT 'Available',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE employee_assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    company_asset_id BIGINT NOT NULL,
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_eas_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_eas_asset FOREIGN KEY (company_asset_id) REFERENCES company_assets(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE asset_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    asset_category VARCHAR(80) NOT NULL,
    justification TEXT,
    status ENUM('Pending','Approved','Rejected','Fulfilled') DEFAULT 'Pending',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ar_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE asset_maintenance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_asset_id BIGINT NOT NULL,
    maintenance_date DATE NOT NULL,
    vendor VARCHAR(150),
    cost DECIMAL(12,2),
    notes TEXT,
    CONSTRAINT fk_am_asset FOREIGN KEY (company_asset_id) REFERENCES company_assets(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- FINANCE & EXPENSE MANAGEMENT
-- =========================================================

CREATE TABLE expense_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_code VARCHAR(20) NOT NULL UNIQUE,
    category_name VARCHAR(100) NOT NULL,
    max_limit DECIMAL(12,2),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE expense_claims (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    category_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    currency_code CHAR(3) DEFAULT 'INR',
    status ENUM('Pending','Approved','Rejected','Paid') DEFAULT 'Pending',
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ec_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ec_cat FOREIGN KEY (category_id) REFERENCES expense_categories(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE travel_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    destination VARCHAR(150) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    estimated_cost DECIMAL(12,2),
    currency_code CHAR(3) DEFAULT 'INR',
    status ENUM('Pending','Approved','Rejected','Completed') DEFAULT 'Pending',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tr_user FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- ADMIN & AUDIT
-- =========================================================

CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_code VARCHAR(20),
    action_type VARCHAR(80) NOT NULL,
    entity_name VARCHAR(80),
    entity_id VARCHAR(50),
    details TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20),
    activity_type VARCHAR(80) NOT NULL,
    description TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================================================
-- EMPLOYEE PROFILE PHOTOS
-- =========================================================

CREATE TABLE employee_profile_photos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL,
    photo_name VARCHAR(255),
    content_type VARCHAR(100),
    photo_data LONGBLOB NOT NULL,
    is_active BOOLEAN DEFAULT FALSE,
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_epp_employee (employee_code),
    CONSTRAINT fk_profile_photo FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- DASHBOARD & ANALYTICS
-- =========================================================

CREATE TABLE dashboard_metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    metric_key VARCHAR(80) NOT NULL UNIQUE,
    metric_value DECIMAL(14,2) NOT NULL,
    metric_label VARCHAR(150),
    recorded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE employee_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date DATE NOT NULL,
    total_employees INT NOT NULL,
    active_employees INT NOT NULL,
    new_hires INT DEFAULT 0,
    attrition INT DEFAULT 0,
    UNIQUE KEY uq_emp_stat (stat_date)
) ENGINE=InnoDB;

CREATE TABLE attendance_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    summary_date DATE NOT NULL UNIQUE,
    present_count INT DEFAULT 0,
    absent_count INT DEFAULT 0,
    late_count INT DEFAULT 0,
    half_day_count INT DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE leave_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    summary_month CHAR(7) NOT NULL UNIQUE,
    pending_requests INT DEFAULT 0,
    approved_requests INT DEFAULT 0,
    rejected_requests INT DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE payroll_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    summary_month CHAR(7) NOT NULL UNIQUE,
    total_gross DECIMAL(14,2) NOT NULL,
    total_deductions DECIMAL(14,2) NOT NULL,
    total_net DECIMAL(14,2) NOT NULL,
    currency_code CHAR(3) DEFAULT 'INR'
) ENGINE=InnoDB;

CREATE TABLE training_courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    duration_hours INT,
    instructor VARCHAR(150),
    status VARCHAR(20) DEFAULT 'Active',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE training_enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    employee_code VARCHAR(20) NOT NULL,
    status VARCHAR(30) DEFAULT 'Enrolled',
    progress INT DEFAULT 0,
    enrolled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME NULL,
    CONSTRAINT fk_te_course FOREIGN KEY (course_id) REFERENCES training_courses(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_te_employee FOREIGN KEY (employee_code) REFERENCES users(employee_code) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- SAMPLE DATA (4 rows per table)
-- =========================================================

INSERT INTO roles (role_code, role_name, description) VALUES
('ADMIN','Administrator','Full system access'),
('HR','Human Resources','HR operations'),
('MANAGER','Manager','Team management'),
('EMPLOYEE','Employee','Self-service portal');

INSERT INTO permissions (permission_code, module_name, description) VALUES
('USER_READ','Auth','View users'),
('USER_WRITE','Auth','Manage users'),
('PAYROLL_RUN','Payroll','Process payroll'),
('LEAVE_APPROVE','Leave','Approve leave requests');

INSERT INTO users (employee_code, username, password_hash, role, is_active, is_alive, created_by) VALUES
('EMP-ADMIN-001','admin@company.com','$2a$10$TBSysrJbsFUasNDEVpmFve1LAoPY/K7Lgifu7G6jmtRxhgwS7Rdce','ADMIN',1,1,'SYSTEM'),
('EMP-HR-001','hr@company.com','$2a$10$TBSysrJbsFUasNDEVpmFve1LAoPY/K7Lgifu7G6jmtRxhgwS7Rdce','HR',1,1,'SYSTEM'),
('EMP-MGR-001','manager@company.com','$2a$10$TBSysrJbsFUasNDEVpmFve1LAoPY/K7Lgifu7G6jmtRxhgwS7Rdce','MANAGER',1,1,'SYSTEM'),
('EMP-001','arun@company.com','$2a$10$TBSysrJbsFUasNDEVpmFve1LAoPY/K7Lgifu7G6jmtRxhgwS7Rdce','EMPLOYEE',1,1,'SYSTEM');

INSERT INTO user_roles (user_id, role_id) VALUES (1,1),(2,2),(3,3),(4,4);
INSERT INTO login_history (user_id, ip_address, status) VALUES
(1,'192.168.1.10','SUCCESS'),(2,'192.168.1.11','SUCCESS'),(3,'192.168.1.12','SUCCESS'),(4,'192.168.1.13','FAILED');
INSERT INTO password_reset_tokens (user_id, token_hash, expires_at) VALUES
(1,SHA2('tok-admin',256),'2026-12-31 23:59:59'),(2,SHA2('tok-hr',256),'2026-12-31 23:59:59'),
(3,SHA2('tok-mgr',256),'2026-12-31 23:59:59'),(4,SHA2('tok-emp',256),'2026-12-31 23:59:59');
INSERT INTO sessions (user_id, session_token, expires_at) VALUES
(1,'sess-admin-001','2026-06-30 23:59:59'),(2,'sess-hr-001','2026-06-30 23:59:59'),
(3,'sess-mgr-001','2026-06-30 23:59:59'),(4,'sess-emp-001','2026-06-30 23:59:59');

INSERT INTO training_courses (title, description, category, duration_hours, instructor, status) VALUES
('Corporate Compliance 101','Overview of company policies and regulatory compliance requirements','Compliance',4,'Sarah Jenkins','Active'),
('Advanced Java Development','Spring Boot, microservices architecture and cloud deployment patterns','Technical',16,'Michael Chen','Active'),
('Effective Communication','Business communication, presentation skills and stakeholder management','Soft Skills',8,'David Ross','Active'),
('Data Security Fundamentals','GDPR, data protection and cybersecurity best practices','Compliance',6,'Priya Nair','Active');
INSERT INTO training_enrollments (course_id, employee_code, status, progress) VALUES
(1,'EMP-001','Completed',100),(2,'EMP-001','In Progress',45),
(3,'EMP-HR-001','Enrolled',0),(4,'EMP-MGR-001','Completed',100);

INSERT INTO department_master (dept_code, dept_name, head_employee_code) VALUES
('ADM','Administration','EMP-ADMIN-001'),('HR','Human Resources','EMP-HR-001'),
('ENG','Engineering','EMP-MGR-001'),('FIN','Finance','EMP-ADMIN-001');
INSERT INTO designation_master (designation_code, designation_name, level_rank) VALUES
('SYSADM','System Admin',10),('HRMGR','HR Manager',8),('ENGMGR','Engineering Manager',7),('SWE','Software Engineer',4);
INSERT INTO system_settings (setting_key, setting_value, description) VALUES
('currency','INR','Default currency'),
('company_name','TechNova India Pvt Ltd','Legal name'),
('timezone','Asia/Kolkata','Office timezone'),
('fiscal_year_start','04-01','India FY start');

INSERT INTO employees (id, name, email, phone, department, designation, join_date, status, salary) VALUES
('EMP-ADMIN-001','Karthik Raman','admin@company.com','9876543210','Administration','System Admin','2024-01-01','Active',100000),
('EMP-HR-001','Priya Nair','hr@company.com','9876543211','HR','HR Manager','2024-02-01','Active',75000),
('EMP-MGR-001','Rahul Sharma','manager@company.com','9876543212','Engineering','Engineering Manager','2024-03-01','Active',125000),
('EMP-001','Arun Kumar','arun@company.com','9876543213','Engineering','Software Engineer','2024-04-01','Active',54167);

INSERT INTO employee_profiles (employee_code, full_name, email, phone_number, department, designation, date_of_joining, reporting_manager_code, work_location, salary, status) VALUES
('EMP-ADMIN-001','Karthik Raman','admin@company.com','9000000000','Administration','System Admin','2024-01-01',NULL,'Chennai',100000,'Active'),
('EMP-HR-001','Priya Nair','hr@company.com','9000000001','HR','HR Manager','2024-02-01','EMP-ADMIN-001','Chennai',75000,'Active'),
('EMP-MGR-001','Rahul Sharma','manager@company.com','9000000002','Engineering','Engineering Manager','2024-03-01','EMP-ADMIN-001','Bengaluru',125000,'Active'),
('EMP-001','Arun Kumar','arun@company.com','9876543210','Engineering','Software Engineer','2025-01-10','EMP-MGR-001','Bengaluru',54167,'Active');

INSERT INTO employee_documents (employee_code, document_name, document_type) VALUES
('EMP-ADMIN-001','Aadhaar Card','ID'),
('EMP-HR-001','Aadhaar Card','ID'),
('EMP-MGR-001','Aadhaar Card','ID'),
('EMP-001','Aadhaar Card','ID');

INSERT INTO employee_bank_details (employee_code, bank_name, account_number, ifsc_code, account_holder_name, is_primary) VALUES
('EMP-ADMIN-001','HDFC Bank','5010001234567','HDFC0001234','Karthik Raman',1),
('EMP-HR-001','HDFC Bank','5010011234567','HDFC0001234','Priya Nair',1),
('EMP-MGR-001','HDFC Bank','5010021234567','HDFC0001234','Rahul Sharma',1),
('EMP-001','HDFC Bank','5010031234567','HDFC0001234','Arun Kumar',1);

INSERT INTO employee_emergency_contacts (employee_code, contact_name, relationship, phone_number) VALUES
('EMP-ADMIN-001','Emergency Contact 1','Family','9800000000'),
('EMP-HR-001','Emergency Contact 2','Family','9810000001'),
('EMP-MGR-001','Emergency Contact 3','Family','9820000002'),
('EMP-001','Emergency Contact 4','Family','9830000003');

INSERT INTO employee_addresses (employee_code, address_type, line1, city, state, postal_code, country) VALUES
('EMP-ADMIN-001','CURRENT','1 MG Road','Chennai','Tamil Nadu','600000','India'),
('EMP-HR-001','CURRENT','2 MG Road','Chennai','Tamil Nadu','600001','India'),
('EMP-MGR-001','CURRENT','3 MG Road','Chennai','Tamil Nadu','600002','India'),
('EMP-001','CURRENT','4 MG Road','Chennai','Tamil Nadu','600003','India');

INSERT INTO employee_education (employee_code, degree, institution, year_completed, grade) VALUES
('EMP-ADMIN-001','B.Tech','Anna University',2010,'First Class'),
('EMP-HR-001','B.Tech','Anna University',2011,'First Class'),
('EMP-MGR-001','B.Tech','Anna University',2012,'First Class'),
('EMP-001','B.Tech','Anna University',2013,'First Class');

INSERT INTO employee_experience (employee_code, company_name, job_title, start_date, end_date) VALUES
('EMP-ADMIN-001','Previous Corp 1','Analyst','2018-01-01','2023-12-31'),
('EMP-HR-001','Previous Corp 2','Analyst','2018-01-01','2023-12-31'),
('EMP-MGR-001','Previous Corp 3','Analyst','2018-01-01','2023-12-31'),
('EMP-001','Previous Corp 4','Analyst','2018-01-01','2023-12-31');

INSERT INTO employee_certifications (employee_code, certification_name, issuing_body, issue_date, expiry_date) VALUES
('EMP-ADMIN-001','AWS Solutions Architect','Amazon','2024-06-01','2027-06-01'),
('EMP-HR-001','AWS Solutions Architect','Amazon','2024-06-01','2027-06-01'),
('EMP-MGR-001','AWS Solutions Architect','Amazon','2024-06-01','2027-06-01'),
('EMP-001','AWS Solutions Architect','Amazon','2024-06-01','2027-06-01');

INSERT INTO attendance_shifts (shift_code, shift_name, start_time, end_time, grace_minutes) VALUES
('GEN','General Shift','09:00:00','18:00:00',15),('MOR','Morning Shift','06:00:00','14:00:00',10),
('EVE','Evening Shift','14:00:00','22:00:00',10),('NIGHT','Night Shift','22:00:00','06:00:00',5);
INSERT INTO attendance (id, employee_id, employee_name, date, status) VALUES
('ATT001','EMP-ADMIN-001','Karthik Raman','2026-05-01','Present'),('ATT002','EMP-HR-001','Priya Nair','2026-05-01','Late'),
('ATT003','EMP-MGR-001','Rahul Sharma','2026-05-01','Present'),('ATT004','EMP-001','Arun Kumar','2026-05-01','Late');
INSERT INTO attendance_records (employee_code, work_date, check_in, check_out, status, total_hours, hours_worked) VALUES
('EMP-ADMIN-001','2026-05-01','09:00','18:00','Present',9.00,9.00),('EMP-HR-001','2026-05-01','09:20','18:00','Late',8.67,8.67),
('EMP-MGR-001','2026-05-01','09:00','17:30','Present',8.50,8.50),('EMP-001','2026-05-01','09:35','18:00','Late',8.42,8.42);
INSERT INTO attendance_break_logs (employee_code, work_date, break_start, break_end, break_minutes) VALUES
('EMP-ADMIN-001','2026-05-01','13:00','13:30',30),('EMP-HR-001','2026-05-01','13:00','13:20',20),
('EMP-MGR-001','2026-05-01','13:10','13:40',30),('EMP-001','2026-05-01','13:00','13:25',25);
INSERT INTO attendance_overtime (employee_code, work_date, overtime_hours, reason, status) VALUES
('EMP-ADMIN-001','2026-05-02',2.0,'Month-end close','Approved'),('EMP-HR-001','2026-05-02',1.5,'Payroll','Approved'),
('EMP-MGR-001','2026-05-02',3.0,'Release','Pending'),('EMP-001','2026-05-02',2.5,'Bug fix','Approved');
INSERT INTO attendance_regularization_requests (employee_code, work_date, requested_check_in, requested_check_out, reason, status) VALUES
('EMP-ADMIN-001','2026-04-28','09:05','18:00','Biometric miss','Approved'),('EMP-HR-001','2026-04-28','09:10','18:00','Late entry','Pending'),
('EMP-MGR-001','2026-04-28','09:00','17:45','Early leave','Approved'),('EMP-001','2026-04-28','09:40','18:00','Traffic','Pending');

INSERT INTO leave_types (type_code, type_name, annual_quota, is_paid) VALUES
('ANN','Annual Leave',18,1),('SICK','Sick Leave',10,1),('CAS','Casual Leave',6,1),('UNPAID','Unpaid Leave',0,0);
INSERT INTO holiday_calendar (holiday_date, holiday_name, region) VALUES
('2026-01-26','Republic Day','India'),('2026-08-15','Independence Day','India'),
('2026-10-02','Gandhi Jayanti','India'),('2026-11-14','Diwali','India');
INSERT INTO leave_requests (id, employee_code, employee_id, employee_name, leave_type, start_date, end_date, from_date, to_date, number_of_days, reason, status) VALUES
('L001','EMP-001','EMP-001','Arun Kumar','Annual','2026-06-01','2026-06-03','2026-06-01','2026-06-03',3,'Family trip','Approved'),
('L002','EMP-HR-001','EMP-HR-001','Priya Nair','Sick','2026-05-10','2026-05-11','2026-05-10','2026-05-11',2,'Fever','Approved'),
('L003','EMP-MGR-001','EMP-MGR-001','Rahul Sharma','Casual','2026-07-15','2026-07-15','2026-07-15','2026-07-15',1,'Personal','Pending'),
('L004','EMP-ADMIN-001','EMP-ADMIN-001','Karthik Raman','Unpaid','2026-08-01','2026-08-02','2026-08-01','2026-08-02',2,'Travel','Rejected');
INSERT INTO leave_balances (employee_code, year, annual_leave, sick_leave, casual_leave, used_annual, used_sick, used_casual) VALUES
('EMP-ADMIN-001',2026,18,10,6,2,0,1),('EMP-HR-001',2026,18,10,6,1,2,0),
('EMP-MGR-001',2026,18,10,6,3,0,1),('EMP-001',2026,18,10,6,4,1,0);
INSERT INTO leave_approvals (leave_request_id, approver_code, decision, comments) VALUES
('L001','EMP-MGR-001','Approved','Enjoy'),('L002','EMP-ADMIN-001','Approved','Get well'),
('L003','EMP-ADMIN-001','Approved',NULL),('L004','EMP-HR-001','Rejected','Peak season');

INSERT INTO salary_structures (structure_code, structure_name, currency_code) VALUES
('STD-IN','Standard India INR','INR'),('EXEC-IN','Executive INR','INR'),
('CONT-IN','Contract INR','INR'),('INT-IN','Intern INR','INR');
INSERT INTO salary_components (component_code, component_name, component_type, is_taxable) VALUES
('BASIC','Basic Salary','EARNING',1),('HRA','House Rent Allowance','EARNING',1),
('PF','Provident Fund','DEDUCTION',0),('TDS','Tax Deducted at Source','DEDUCTION',0);
INSERT INTO employee_salary (employee_code, structure_id, basic_salary, hra, allowances, effective_from, currency_code) VALUES
('EMP-ADMIN-001',1,100000,40000,10000,'2024-01-01','INR'),('EMP-HR-001',1,75000,30000,7500,'2024-02-01','INR'),
('EMP-MGR-001',2,125000,50000,12500,'2024-03-01','INR'),('EMP-001',1,54167,21667,5417,'2025-01-10','INR');
INSERT INTO payroll (payroll_month, processed_by, total_gross, total_deductions, total_net, currency_code, status) VALUES
('2026-01','EMP-HR-001',354167,53000,301167,'INR','Completed'),('2026-02','EMP-HR-001',354167,53000,301167,'INR','Completed'),
('2026-03','EMP-HR-001',354167,53000,301167,'INR','Completed'),('2026-04','EMP-HR-001',354167,53000,301167,'INR','Processing');
INSERT INTO payslips (employee_code, payroll_month, basic_salary, hra, allowances, pf_deduction, tax_deduction, other_deductions, net_salary, currency_code, status) VALUES
('EMP-ADMIN-001','2026-04',100000,40000,10000,12000,8000,500,130500,'INR','Paid'),
('EMP-HR-001','2026-04',75000,30000,7500,9000,6000,400,97000,'INR','Paid'),
('EMP-MGR-001','2026-04',125000,50000,12500,15000,12000,800,160700,'INR','Paid'),
('EMP-001','2026-04',54167,21667,5417,6500,4200,300,71251,'INR','Paid');
INSERT INTO payroll_transactions (payroll_id, employee_code, transaction_type, amount, description) VALUES
(4,'EMP-ADMIN-001','CREDIT',150000,'Net pay'),(4,'EMP-HR-001','CREDIT',112500,'Net pay'),
(4,'EMP-MGR-001','CREDIT',187500,'Net pay'),(4,'EMP-001','CREDIT',81250,'Net pay');
INSERT INTO tax_deductions (employee_code, financial_year, tds_amount, regime) VALUES
('EMP-ADMIN-001','2025-2026',96000,'NEW'),('EMP-HR-001','2025-2026',72000,'NEW'),
('EMP-MGR-001','2025-2026',144000,'NEW'),('EMP-001','2025-2026',50400,'NEW');
INSERT INTO bonus_incentives (employee_code, bonus_type, amount, payout_month, status) VALUES
('EMP-ADMIN-001','Annual Bonus',50000,'2026-03','Paid'),('EMP-HR-001','Performance',25000,'2026-03','Paid'),
('EMP-MGR-001','Project Bonus',75000,'2026-03','Approved'),('EMP-001','Spot Award',10000,'2026-04','Paid');
INSERT INTO reimbursements (employee_code, title, amount, status) VALUES
('EMP-001','Client travel',8500,'Approved'),('EMP-HR-001','Training',12000,'Paid'),
('EMP-MGR-001','Conference',22000,'Pending'),('EMP-ADMIN-001','Hardware',45000,'Approved');

INSERT INTO projects (name, project_name, description, project_manager, start_date, target_completion_date, priority, status, progress, budget, currency_code) VALUES
('EMS Portal','EMS Portal','HRMS platform','Rahul Sharma','2026-01-01 09:00:00','2026-12-31 18:00:00','High','In Progress',70,5000000,'INR'),
('Payroll Automation','Payroll Automation','Payroll module','Priya Nair','2026-02-01 09:00:00','2026-10-31 18:00:00','Medium','In Progress',50,2000000,'INR'),
('Recruitment Hub','Recruitment Hub','Hiring portal','Priya Nair','2026-03-01 09:00:00','2026-09-30 18:00:00','High','Planning',20,1500000,'INR'),
('Analytics Dashboard','Analytics Dashboard','BI dashboard','Rahul Sharma','2026-04-01 09:00:00','2026-11-30 18:00:00','Low','On Hold',10,1000000,'INR');
INSERT INTO project_team_members (project_id, team_member, employee_code, role_in_project) VALUES
(1,'Rahul Sharma','EMP-MGR-001','Manager'),(1,'Arun Kumar','EMP-001','Developer'),
(2,'Priya Nair','EMP-HR-001','Owner'),(3,'Karthik Raman','EMP-ADMIN-001','Sponsor');
INSERT INTO project_tasks (project_id, title, assigned_to, due_date, status, progress_percent) VALUES
(1,'API Module','EMP-001','2026-06-10','In Progress',70),(1,'UI Polish','EMP-MGR-001','2026-06-15','Open',30),
(2,'PF Calculation','EMP-HR-001','2026-06-20','In Progress',50),(3,'Job Board','EMP-HR-001','2026-07-01','Open',10);
INSERT INTO task_comments (task_id, author_code, comment_text) VALUES
(1,'EMP-MGR-001','Good progress on APIs'),(2,'EMP-ADMIN-001','Align with design system'),
(3,'EMP-HR-001','Verify statutory rates'),(4,'EMP-MGR-001','Add screening workflow');
INSERT INTO task_attachments (task_id, file_name, file_path) VALUES
(1,'api-spec.pdf','/files/api-spec.pdf'),(2,'wireframes.zip','/files/wireframes.zip'),
(3,'pf-rules.xlsx','/files/pf-rules.xlsx'),(4,'jd-template.docx','/files/jd-template.docx');
INSERT INTO project_milestones (project_id, milestone_name, target_date, status) VALUES
(1,'MVP Release','2026-06-30','Pending'),(2,'Go Live','2026-10-01','Pending'),
(3,'Pilot Hire','2026-08-15','Pending'),(4,'Beta Dashboard','2026-09-01','Pending');
INSERT INTO priority_tasks (employee_code, title, due_date, priority, status, progress_percent) VALUES
('EMP-001','API Module','2026-06-10','High','In Progress',70),('EMP-HR-001','Policy Update','2026-06-12','Medium','Pending',0),
('EMP-MGR-001','Architecture Review','2026-06-15','High','Done',100),('EMP-ADMIN-001','Server Audit','2026-06-20','Low','Pending',0);

INSERT INTO meetings (title, organizer, start_time, end_time, location, status) VALUES
('Sprint Planning','Rahul Sharma','2026-06-01 10:00:00','2026-06-01 11:00:00','Conf A','Scheduled'),
('HR Review','Priya Nair','2026-06-02 14:00:00','2026-06-02 15:00:00','Conf B','Completed'),
('Budget Meeting','Karthik Raman','2026-06-03 11:00:00','2026-06-03 12:00:00','Board Room','Scheduled'),
('Town Hall','Rahul Sharma','2026-06-04 16:00:00','2026-06-04 17:00:00','Auditorium','Cancelled');
INSERT INTO meeting_attendees (meeting_id, attendee, employee_code, rsvp_status) VALUES
(1,'Arun Kumar','EMP-001','Accepted'),(2,'Priya Nair','EMP-HR-001','Accepted'),
(3,'Karthik Raman','EMP-ADMIN-001','Accepted'),(4,'Rahul Sharma','EMP-MGR-001','Declined');
INSERT INTO meeting_notes (meeting_id, author_code, note_text) VALUES
(1,'EMP-MGR-001','Sprint goals finalized'),(2,'EMP-HR-001','Hiring plan approved'),
(3,'EMP-ADMIN-001','Q2 budget signed off'),(4,'EMP-MGR-001','Rescheduled to next week');
INSERT INTO announcements (title, body, published_by, audience) VALUES
('Holiday Calendar','Updated 2026 holidays published','EMP-HR-001','ALL'),
('Security Policy','MFA rollout next month','EMP-ADMIN-001','ALL'),
('Town Hall','Join us Friday 4 PM','EMP-MGR-001','ALL'),
('Payroll Date','April payroll on 30th','EMP-HR-001','EMPLOYEE');
INSERT INTO company_events (event_name, event_date, venue, description) VALUES
('Annual Day','2026-12-15','Chennai Convention Center','Company celebration'),
('Hackathon','2026-07-20','Bengaluru Office','48-hour internal hackathon'),
('Wellness Week','2026-09-01','All offices','Health and wellness activities'),
('Leadership Offsite','2026-11-05','Ooty','Management strategy meet');

INSERT INTO performance_reviews (employee_id, employee_name, reviewer_id, reviewer_name, employee_code, reviewer_code, performance_rating, strengths, overall_comments, review_period) VALUES
('EMP-001','Arun Kumar','EMP-MGR-001','Rahul Sharma','EMP-001','EMP-MGR-001',5,'Delivery','Excellent','H1-2026'),
('EMP-HR-001','Priya Nair','EMP-ADMIN-001','Karthik Raman','EMP-HR-001','EMP-ADMIN-001',4,'HR ops','Strong','H1-2026'),
('EMP-MGR-001','Rahul Sharma','EMP-ADMIN-001','Karthik Raman','EMP-MGR-001','EMP-ADMIN-001',5,'Leadership','Great','H1-2026'),
('EMP-ADMIN-001','Karthik Raman','EMP-HR-001','Priya Nair','EMP-ADMIN-001','EMP-HR-001',4,'Governance','Good','H1-2026');
INSERT INTO manager_feedback (employee_code, manager_code, review_date, rating_score, feedback_text, quarter) VALUES
('EMP-001','EMP-MGR-001','2026-06-01',4.8,'Excellent delivery','Q2'),
('EMP-HR-001','EMP-ADMIN-001','2026-06-01',4.5,'Strong HR leadership','Q2'),
('EMP-MGR-001','EMP-ADMIN-001','2026-06-01',4.9,'Team leadership','Q2'),
('EMP-ADMIN-001','EMP-HR-001','2026-06-01',4.2,'IT governance','Q2');
INSERT INTO performance_goals (employee_code, goal_title, target_date, quarter, progress_pct, status) VALUES
('EMP-001','Complete EMS APIs','2026-09-30','Q3',70,'In Progress'),
('EMP-HR-001','Reduce hiring TAT','2026-09-30','Q3',100,'Achieved'),
('EMP-MGR-001','Improve velocity','2026-09-30','Q3',80,'In Progress'),
('EMP-ADMIN-001','Cloud migration','2026-09-30','Q3',20,'Missed');
INSERT INTO kpi_metrics (metric_code, metric_name, target_value, unit, department) VALUES
('ATT-95','Attendance Rate',95,'percent','All'),
('HIRE-30','Hiring TAT',30,'days','HR'),
('DEL-90','Sprint Delivery',90,'percent','Engineering'),
('NPS-50','Employee NPS',50,'score','HR');
INSERT INTO employee_ratings (employee_code, kpi_id, rating_value, review_period) VALUES
('EMP-001',3,88,'Q2-2026'),('EMP-HR-001',2,28,'Q2-2026'),
('EMP-MGR-001',3,92,'Q2-2026'),('EMP-ADMIN-001',1,96,'Q2-2026');

INSERT INTO recruitment (id, name, email, phone, position, department, applied_date, status) VALUES
('R001','Vijay','vijay@mail.com','9000000001','Developer','Engineering','2026-05-01','Shortlisted'),
('R002','Sneha','sneha@mail.com','9000000002','QA Engineer','Engineering','2026-05-02','Interview'),
('R003','Rakesh','rakesh@mail.com','9000000003','HR Executive','HR','2026-05-03','Selected'),
('R004','Anita','anita@mail.com','9000000004','Analyst','Finance','2026-05-04','Applied');
INSERT INTO job_postings (posting_code, job_title, department, openings, status) VALUES
('JP-001','Senior Developer','Engineering',2,'Open'),('JP-002','HR Executive','HR',1,'Open'),
('JP-003','QA Lead','Engineering',1,'Open'),('JP-004','Finance Analyst','Finance',1,'Closed');
INSERT INTO candidates (full_name, email, phone, source) VALUES
('Vijay Kumar','vijay@mail.com','9000000001','LinkedIn'),('Sneha Rao','sneha@mail.com','9000000002','Referral'),
('Rakesh Menon','rakesh@mail.com','9000000003','Portal'),('Anita Desai','anita@mail.com','9000000004','Campus');
INSERT INTO job_applications (posting_id, candidate_id, status) VALUES
(1,1,'Interview'),(2,3,'Offer'),(3,2,'Screening'),(4,4,'Applied');
INSERT INTO interview_schedule (application_id, interviewer_code, scheduled_at, mode, status) VALUES
(1,'EMP-MGR-001','2026-06-10 10:00:00','Video','Scheduled'),(2,'EMP-HR-001','2026-06-11 14:00:00','In Person','Completed'),
(3,'EMP-MGR-001','2026-06-12 11:00:00','Video','Scheduled'),(4,'EMP-HR-001','2026-06-13 15:00:00','Phone','Cancelled');
INSERT INTO offer_letters (application_id, offered_ctc, currency_code, joining_date, status) VALUES
(2,900000,'INR','2026-07-01','Sent'),(1,1200000,'INR','2026-07-15','Draft'),
(3,700000,'INR','2026-08-01','Draft'),(4,650000,'INR','2026-08-15','Declined');

INSERT INTO onboarding_tracking (employee_code, employee_name, department, joining_date, current_step, current_step_num, total_steps, progress_percent, assigned_hr, status) VALUES
('EMP-001','Arun Kumar','Engineering','2025-01-10','Documents Upload',4,5,60,'Priya Nair','In Progress'),
('EMP-HR-001','Priya Nair','HR','2024-02-01','Completed',5,5,100,'Priya Nair','Completed'),
('EMP-MGR-001','Rahul Sharma','Engineering','2024-03-01','Payroll & Benefits',3,5,40,'Priya Nair','In Progress'),
('EMP-ADMIN-001','Karthik Raman','Administration','2024-01-01','Personal Details',1,5,0,'Priya Nair','Pending');
INSERT INTO onboarding_tasks (onboarding_id, task_name, is_completed, due_date) VALUES
(1,'Submit PAN',1,'2025-01-15'),(2,'Complete induction',1,'2024-02-10'),
(3,'Bank details',0,'2024-03-20'),(4,'ID verification',0,'2024-01-20');
INSERT INTO asset_assignments (employee_code, asset_tag) VALUES
('EMP-001','LT-1001'),('EMP-HR-001','LT-1002'),('EMP-MGR-001','LT-1003'),('EMP-ADMIN-001','LT-1004');
INSERT INTO offboarding_tracking (employee_code, last_working_day, reason, status) VALUES
('EMP-001','2027-12-31','Not applicable','Initiated'),
('EMP-HR-001','2027-12-31','Not applicable','Initiated'),
('EMP-MGR-001','2027-12-31','Not applicable','Initiated'),
('EMP-ADMIN-001','2027-12-31','Not applicable','Initiated');
INSERT INTO exit_interviews (employee_code, conducted_by, feedback) VALUES
('EMP-001','EMP-HR-001','N/A - active'),('EMP-HR-001','EMP-ADMIN-001','N/A'),
('EMP-MGR-001','EMP-HR-001','N/A'),('EMP-ADMIN-001','EMP-HR-001','N/A');

INSERT INTO in_app_notifications (employee_code, title, message, time_category, is_read) VALUES
('EMP-001','Task Assigned','New API task','TODAY',0),('EMP-HR-001','Leave Request','Pending approval','TODAY',1),
('EMP-MGR-001','Project Update','Sprint review','THIS WEEK',0),('EMP-ADMIN-001','Security','MFA reminder','THIS WEEK',1);
INSERT INTO notification_messages (sender_code, sender_name, recipient_code, recipient_role, recipient_type, title, subject, message, priority, category, send_date, expiry_date, status, channel, is_read) VALUES
('EMP-ADMIN-001','Karthik Raman','EMP-001','EMPLOYEE','INDIVIDUAL','Welcome aboard','Onboarding completed','Your onboarding session has been saved and can be resumed anytime.','HIGH','Onboarding','2026-06-03',NULL,'UNREAD','IN_APP',0),
('EMP-HR-001','Priya Nair','EMP-MGR-001','MANAGER','INDIVIDUAL','Policy reminder','HR Policy Update','Please review the updated leave and attendance policy before Friday.','MEDIUM','Policy','2026-06-03',NULL,'UNREAD','IN_APP',0),
('EMP-MGR-001','Rahul Sharma','EMP-ADMIN-001','ADMIN','INDIVIDUAL','Project review','Sprint review','The next project review has been scheduled for tomorrow morning.','LOW','Projects','2026-06-03',NULL,'READ','IN_APP',1);
INSERT INTO notification_preferences (employee_code, email_alerts, push_alerts, payroll_alerts, digest_frequency) VALUES
('EMP-001',1,1,1,'realtime'),('EMP-HR-001',1,1,1,'daily'),
('EMP-MGR-001',1,0,1,'daily'),('EMP-ADMIN-001',1,1,0,'weekly');
INSERT INTO email_logs (recipient_email, subject, status) VALUES
('arun@company.com','Payslip Ready','Sent'),('hr@company.com','Leave Approved','Sent'),
('manager@company.com','Meeting Invite','Sent'),('admin@company.com','Password Reset','Failed');
INSERT INTO sms_logs (phone_number, message, status) VALUES
('9876543210','OTP for login','Sent'),('9000000001','Leave approved','Sent'),
('9000000002','Meeting reminder','Queued'),('9000000000','Payroll processed','Sent');

INSERT INTO approvals (request_id, request_type, requested_by, approver, description, amount, currency_code, status) VALUES
('REQ-001','Expense','EMP-001','EMP-MGR-001','Travel reimbursement',8500,'INR','Approved'),
('REQ-002','Purchase','EMP-HR-001','EMP-ADMIN-001','Laptop upgrade',65000,'INR','Pending'),
('REQ-003','Budget','EMP-MGR-001','EMP-ADMIN-001','Project budget',1000000,'INR','Approved'),
('REQ-004','Leave','EMP-ADMIN-001','EMP-HR-001','Leave approval',0,'INR','Rejected');
INSERT INTO workflow_history (approval_id, step_name, actor_code, action_taken) VALUES
(1,'Manager Review','EMP-MGR-001','Approved'),(2,'Finance Check','EMP-ADMIN-001','Pending'),
(3,'CFO Sign-off','EMP-ADMIN-001','Approved'),(4,'HR Review','EMP-HR-001','Rejected');
INSERT INTO approval_comments (approval_id, author_code, comment_text) VALUES
(1,'EMP-MGR-001','Approved with receipts'),(2,'EMP-ADMIN-001','Awaiting quote'),
(3,'EMP-ADMIN-001','Within annual cap'),(4,'EMP-HR-001','Blackout period');

INSERT INTO company_assets (asset_tag, asset_name, category, purchase_date, cost, currency_code, status) VALUES
('LT-1001','Dell Latitude 5540','Laptop','2024-01-15',85000,'INR','Assigned'),
('LT-1002','MacBook Pro 14','Laptop','2024-02-01',145000,'INR','Assigned'),
('LT-1003','ThinkPad X1','Laptop','2024-03-01',120000,'INR','Assigned'),
('LT-1004','Dell OptiPlex','Desktop','2024-01-01',65000,'INR','Assigned');
INSERT INTO employee_assets (employee_code, company_asset_id) VALUES
('EMP-001',1),('EMP-HR-001',2),('EMP-MGR-001',3),('EMP-ADMIN-001',4);
INSERT INTO asset_requests (employee_code, asset_category, justification, status) VALUES
('EMP-001','Monitor','Dual monitor for dev','Approved'),('EMP-HR-001','Headset','Recruitment calls','Pending'),
('EMP-MGR-001','Tablet','Field demos','Rejected'),('EMP-ADMIN-001','Server','Lab environment','Approved');
INSERT INTO asset_maintenance (company_asset_id, maintenance_date, vendor, cost, notes) VALUES
(1,'2025-06-01','Dell Care',2500,'Annual service'),(2,'2025-06-01','Apple Care',4500,'Battery check'),
(3,'2025-07-01','Lenovo',3000,'Keyboard replace'),(4,'2025-08-01','Dell',2000,'RAM upgrade');

INSERT INTO expense_categories (category_code, category_name, max_limit) VALUES
('TRAVEL','Travel',50000),('MEAL','Meals',5000),('OFFICE','Office Supplies',10000),('TRAIN','Training',25000);
INSERT INTO expense_claims (employee_code, category_id, title, amount, currency_code, status) VALUES
('EMP-001',1,'Client visit Mumbai',8500,'INR','Approved'),('EMP-HR-001',4,'SHRM course',12000,'INR','Paid'),
('EMP-MGR-001',1,'Bangalore trip',22000,'INR','Pending'),('EMP-ADMIN-001',3,'Server parts',45000,'INR','Approved');
INSERT INTO travel_requests (employee_code, destination, start_date, end_date, estimated_cost, currency_code, status) VALUES
('EMP-001','Mumbai','2026-06-10','2026-06-12',15000,'INR','Approved'),
('EMP-HR-001','Delhi','2026-07-01','2026-07-03',18000,'INR','Pending'),
('EMP-MGR-001','Hyderabad','2026-06-20','2026-06-22',12000,'INR','Approved'),
('EMP-ADMIN-001','Pune','2026-08-05','2026-08-07',10000,'INR','Pending');

INSERT INTO audit_logs (actor_code, action_type, entity_name, entity_id, details) VALUES
('EMP-ADMIN-001','UPDATE','users','1','Role verified'),('EMP-HR-001','CREATE','leave_requests','L002','Created'),
('EMP-MGR-001','APPROVE','approvals','REQ-001','Approved expense'),('EMP-001','LOGIN','sessions','4','Success');
INSERT INTO activity_logs (employee_code, activity_type, description) VALUES
('EMP-001','CHECK_IN','Checked in 09:35'),('EMP-HR-001','LEAVE_APPROVE','Approved L002'),
('EMP-MGR-001','PROJECT_UPDATE','Updated sprint'),('EMP-ADMIN-001','SETTINGS','Updated currency to INR');

INSERT INTO dashboard_metrics (metric_key, metric_value, metric_label) VALUES
('total_employees',4,'Total Employees'),('attendance_rate',75,'Attendance %'),
('pending_leaves',1,'Pending Leaves'),('payroll_ready',1,'Payroll Ready');
INSERT INTO employee_statistics (stat_date, total_employees, active_employees, new_hires, attrition) VALUES
('2026-05-01',4,4,1,0),('2026-04-01',4,4,0,0),('2026-03-01',3,3,1,0),('2026-02-01',3,3,0,0);
INSERT INTO attendance_summary (summary_date, present_count, absent_count, late_count, half_day_count) VALUES
('2026-05-01',2,0,2,0),('2026-04-30',3,0,1,0),('2026-04-29',4,0,0,0),('2026-04-28',3,1,0,0);
INSERT INTO leave_summary (summary_month, pending_requests, approved_requests, rejected_requests) VALUES
('2026-05',1,2,1),('2026-04',0,3,0),('2026-03',2,1,0),('2026-02',1,2,1);
INSERT INTO payroll_summary (summary_month, total_gross, total_deductions, total_net, currency_code) VALUES
('2026-04',354167,53000,301167,'INR'),('2026-03',354167,53000,301167,'INR'),
('2026-02',354167,53000,301167,'INR'),('2026-01',354167,53000,301167,'INR');

-- Default password for all demo users: Admin@123 (BCrypt)
