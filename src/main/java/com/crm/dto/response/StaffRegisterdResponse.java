package com.crm.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StaffRegisterdResponse {

    private Long staffPrimeId;
    private String staffStrId;
    private String staffFirstName;
    private String staffMiddleName;
    private String staffLastName;
    private String staffMobile;
    private String staffEmail;
    private String staffWorkingEmail;
    private String staffAddress;
    private BigDecimal staffSalary;
    private LocalDate joiningDate;
    private String staffRole;
    private String staffDepartment;

    public StaffRegisterdResponse() {
    }

    public StaffRegisterdResponse(Long staffPrimeId, String staffStrId, String staffFirstName,
                                  String staffMiddleName, String staffLastName, String staffMobile,
                                  String staffEmail, String staffWorkingEmail, String staffAddress,
                                  BigDecimal staffSalary, LocalDate joiningDate, String staffRole,
                                  String staffDepartment) {
        this.staffPrimeId = staffPrimeId;
        this.staffStrId = staffStrId;
        this.staffFirstName = staffFirstName;
        this.staffMiddleName = staffMiddleName;
        this.staffLastName = staffLastName;
        this.staffMobile = staffMobile;
        this.staffEmail = staffEmail;
        this.staffWorkingEmail = staffWorkingEmail;
        this.staffAddress = staffAddress;
        this.staffSalary = staffSalary;
        this.joiningDate = joiningDate;
        this.staffRole = staffRole;
        this.staffDepartment = staffDepartment;
    }

    public Long getStaffPrimeId() {
        return staffPrimeId;
    }

    public void setStaffPrimeId(Long staffPrimeId) {
        this.staffPrimeId = staffPrimeId;
    }

    public String getStaffStrId() {
        return staffStrId;
    }

    public void setStaffStrId(String staffStrId) {
        this.staffStrId = staffStrId;
    }

    public String getStaffFirstName() {
        return staffFirstName;
    }

    public void setStaffFirstName(String staffFirstName) {
        this.staffFirstName = staffFirstName;
    }

    public String getStaffMiddleName() {
        return staffMiddleName;
    }

    public void setStaffMiddleName(String staffMiddleName) {
        this.staffMiddleName = staffMiddleName;
    }

    public String getStaffLastName() {
        return staffLastName;
    }

    public void setStaffLastName(String staffLastName) {
        this.staffLastName = staffLastName;
    }

    public String getStaffMobile() {
        return staffMobile;
    }

    public void setStaffMobile(String staffMobile) {
        this.staffMobile = staffMobile;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
    }

    public String getStaffWorkingEmail() {
        return staffWorkingEmail;
    }

    public void setStaffWorkingEmail(String staffWorkingEmail) {
        this.staffWorkingEmail = staffWorkingEmail;
    }

    public String getStaffAddress() {
        return staffAddress;
    }

    public void setStaffAddress(String staffAddress) {
        this.staffAddress = staffAddress;
    }

    public BigDecimal getStaffSalary() {
        return staffSalary;
    }

    public void setStaffSalary(BigDecimal staffSalary) {
        this.staffSalary = staffSalary;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getStaffRole() {
        return staffRole;
    }

    public void setStaffRole(String staffRole) {
        this.staffRole = staffRole;
    }

    public String getStaffDepartment() {
        return staffDepartment;
    }

    public void setStaffDepartment(String staffDepartment) {
        this.staffDepartment = staffDepartment;
    }
}