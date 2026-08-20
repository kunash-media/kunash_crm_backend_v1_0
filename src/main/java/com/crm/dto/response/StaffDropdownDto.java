package com.crm.dto.response;

public class StaffDropdownDto {

    private Long staffPrimeId;
    private String staffStrId;
    private String staffFirstName;
    private String staffLastName;
    private String staffRole;
    private String staffEmail;

    public StaffDropdownDto() {
    }

    public StaffDropdownDto(Long staffPrimeId, String staffStrId, String staffFirstName, String staffLastName, String staffRole, String staffEmail) {
        this.staffPrimeId = staffPrimeId;
        this.staffStrId = staffStrId;
        this.staffFirstName = staffFirstName;
        this.staffLastName = staffLastName;
        this.staffRole = staffRole;
        this.staffEmail = staffEmail;
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

    public String getStaffLastName() {
        return staffLastName;
    }

    public void setStaffLastName(String staffLastName) {
        this.staffLastName = staffLastName;
    }

    public String getStaffRole() {
        return staffRole;
    }

    public void setStaffRole(String staffRole) {
        this.staffRole = staffRole;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
    }
}