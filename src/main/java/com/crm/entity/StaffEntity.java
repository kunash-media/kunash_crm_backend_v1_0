package com.crm.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Table(
        name = "staff",
        uniqueConstraints = {
                @jakarta.persistence.UniqueConstraint(columnNames = "staffEmail"),
                @jakarta.persistence.UniqueConstraint(columnNames = "staffMobile")
        }
)
@Entity
public class StaffEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffPrimeId;

    @Column(name = "staff_str_id", unique = true, nullable = false)
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
    private String staffPassword;
    private Boolean staffActive = true;

    public StaffEntity() {
    }

    public StaffEntity(String staffStrId, String staffFirstName, String staffMiddleName,
                       String staffLastName, String staffMobile, String staffEmail,
                       String staffWorkingEmail, String staffAddress, BigDecimal staffSalary,
                       LocalDate joiningDate, String staffRole, String staffDepartment) {
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

    public String getStaffPassword() {
        return staffPassword;
    }

    public void setStaffPassword(String staffPassword) {
        this.staffPassword = staffPassword;
    }
    public Boolean getStaffActive() {
        return staffActive;
    }

    public void setStaffActive(Boolean staffActive) {
        this.staffActive = staffActive;
    }
}