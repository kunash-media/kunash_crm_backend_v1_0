package com.crm.dto.response;

public class LeadSuggestionDto {
    private Long leadPrimeId;
    private String firstName;
    private String lastName;
    private String phone;
    private String company;
    private String status;

    public LeadSuggestionDto(Long leadPrimeId, String firstName, String lastName,
                             String phone, String company, String status) {
        this.leadPrimeId = leadPrimeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.company = company;
        this.status = status;
    }

    public Long getLeadPrimeId() { return leadPrimeId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getCompany() { return company; }
    public String getStatus() { return status; }
}