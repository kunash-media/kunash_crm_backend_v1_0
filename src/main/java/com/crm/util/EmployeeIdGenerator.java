//package com.crm.util;
//
//import org.springframework.stereotype.Component;
//import java.time.Year;
//
//@Component
//public class EmployeeIdGenerator {
//
//    private static final String PREFIX = "EMP";
//    private static final int STARTING_NUMBER = 1;
//    private static final int DIGITS = 3; // 3 digits for incremental part (001, 002, etc.)
//
//    /**
//     * Gets the current year (e.g., 2026)
//     */
//    private int getCurrentYear() {
//        return Year.now().getValue();
//    }
//
//    /**
//     * Extracts the year from an employee ID
//     * @param employeeId Employee ID like "EMP2026001"
//     * @return year as integer, or -1 if extraction fails
//     */
//    private int extractYearFromId(String employeeId) {
//        try {
//            String withoutPrefix = employeeId.replace(PREFIX, "");
//            // Assuming format: YYYYNNN (4 digits year + 3 digits number)
//            String yearStr = withoutPrefix.substring(0, 4);
//            return Integer.parseInt(yearStr);
//        } catch (Exception e) {
//            return -1;
//        }
//    }
//
//    /**
//     * Extracts the sequential number from an employee ID
//     * @param employeeId Employee ID like "EMP2026001"
//     * @return sequential number as integer, or -1 if extraction fails
//     */
//    private int extractNumberFromId(String employeeId) {
//        try {
//            String withoutPrefix = employeeId.replace(PREFIX, "");
//            // Assuming format: YYYYNNN (4 digits year + 3 digits number)
//            String numberStr = withoutPrefix.substring(4);
//            return Integer.parseInt(numberStr);
//        } catch (Exception e) {
//            return -1;
//        }
//    }
//
//    /**
//     * Generates the next employee ID based on the latest employee ID
//     * Format: EMP + YYYY + XXX (e.g., EMP2026001, EMP2026002, etc.)
//     *
//     * @param latestEmployeeId The latest employee ID in the system (e.g., "EMP2026005")
//     * @return The next employee ID (e.g., "EMP2026006")
//     */
//    public String generateNextEmployeeId(String latestEmployeeId) {
//        int currentYear = getCurrentYear();
//
//        if (latestEmployeeId == null || latestEmployeeId.trim().isEmpty()) {
//            // If no employees exist yet, start with current year and number 001
//            return String.format("%s%04d%0" + DIGITS + "d", PREFIX, currentYear, STARTING_NUMBER);
//        }
//
//        try {
//            int latestYear = extractYearFromId(latestEmployeeId);
//            int latestNumber = extractNumberFromId(latestEmployeeId);
//
//            if (latestYear == currentYear) {
//                // Same year: increment the number
//                int nextNumber = latestNumber + 1;
//                return String.format("%s%04d%0" + DIGITS + "d", PREFIX, currentYear, nextNumber);
//            } else {
//                // New year: reset numbering to 001
//                return String.format("%s%04d%0" + DIGITS + "d", PREFIX, currentYear, STARTING_NUMBER);
//            }
//        } catch (Exception e) {
//            // If parsing fails, start fresh for current year
//            return String.format("%s%04d%0" + DIGITS + "d", PREFIX, currentYear, STARTING_NUMBER);
//        }
//    }
//
//    /**
//     * Generates the first employee ID for the current year
//     * @return e.g., "EMP2026001"
//     */
//    public String generateFirstEmployeeId() {
//        int currentYear = getCurrentYear();
//        return String.format("%s%04d%0" + DIGITS + "d", PREFIX, currentYear, STARTING_NUMBER);
//    }
//
//    /**
//     * Generates the first employee ID for a specific year
//     * @param year The year (e.g., 2026)
//     * @return e.g., "EMP2026001"
//     */
//    public String generateFirstEmployeeIdForYear(int year) {
//        return String.format("%s%04d%0" + DIGITS + "d", PREFIX, year, STARTING_NUMBER);
//    }
//
//    /**
//     * Validates if an employee ID matches the expected format
//     * @param employeeId Employee ID to validate
//     * @return true if format is valid (EMP + 4 digits year + 3 digits number)
//     */
//    public boolean isValidEmployeeId(String employeeId) {
//        if (employeeId == null || !employeeId.startsWith(PREFIX)) {
//            return false;
//        }
//        String withoutPrefix = employeeId.replace(PREFIX, "");
//        if (withoutPrefix.length() != 7) { // 4 digits year + 3 digits number
//            return false;
//        }
//        try {
//            Integer.parseInt(withoutPrefix); // Check if all digits
//            return true;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
//}