package com.ems.hr.leave;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_balances")
public class LeaveBalance {

    @Id
    @Column(name = "employee_code", length = 50)
    private String employeeCode;

    @Column(name = "annual_leave")
    private Integer annualLeave;

    @Column(name = "sick_leave")
    private Integer sickLeave;

    @Column(name = "casual_leave")
    private Integer casualLeave;

    @Column(name = "privilege_leave")
    private Integer privilegeLeave;

    @Column(name = "loss_of_pay")
    private Integer lossOfPay;

    @Column(name = "used_annual")
    private Integer usedAnnual;

    @Column(name = "used_sick")
    private Integer usedSick;

    @Column(name = "used_casual")
    private Integer usedCasual;

    @Column
    private Integer year;

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public Integer getAnnualLeave() { return annualLeave; }
    public void setAnnualLeave(Integer annualLeave) { this.annualLeave = annualLeave; }
    public Integer getSickLeave() { return sickLeave; }
    public void setSickLeave(Integer sickLeave) { this.sickLeave = sickLeave; }
    public Integer getCasualLeave() { return casualLeave; }
    public void setCasualLeave(Integer casualLeave) { this.casualLeave = casualLeave; }
    public Integer getPrivilegeLeave() { return privilegeLeave; }
    public void setPrivilegeLeave(Integer privilegeLeave) { this.privilegeLeave = privilegeLeave; }
    public Integer getLossOfPay() { return lossOfPay; }
    public void setLossOfPay(Integer lossOfPay) { this.lossOfPay = lossOfPay; }
    public Integer getUsedAnnual() { return usedAnnual; }
    public void setUsedAnnual(Integer usedAnnual) { this.usedAnnual = usedAnnual; }
    public Integer getUsedSick() { return usedSick; }
    public void setUsedSick(Integer usedSick) { this.usedSick = usedSick; }
    public Integer getUsedCasual() { return usedCasual; }
    public void setUsedCasual(Integer usedCasual) { this.usedCasual = usedCasual; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}
