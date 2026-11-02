package com.placement.model;

/**
 * Entity Model: CompanyDrive
 * Represents a visiting company's campus recruitment drive specifications.
 * 
 * @author Kshitij Jangam (PRN: 1032230618)
 * Module: Student & Company Profile Registry
 */
public class CompanyDrive {
    private String driveId; // e.g. DRV-01
    private String companyName;
    private String jobRole;
    private double ctcLpa;
    private double minCgpa;
    private int maxBacklogs;
    private String eligibleBranches; // e.g. "CSE;ISE;ECE"
    private String driveDate; // YYYY-MM-DD
    private String status; // OPEN, CLOSED, COMPLETED

    public CompanyDrive() {}

    public CompanyDrive(String driveId, String companyName, String jobRole, double ctcLpa,
                        double minCgpa, int maxBacklogs, String eligibleBranches,
                        String driveDate, String status) {
        this.driveId = driveId;
        this.companyName = companyName;
        this.jobRole = jobRole;
        this.ctcLpa = ctcLpa;
        this.minCgpa = minCgpa;
        this.maxBacklogs = maxBacklogs;
        this.eligibleBranches = eligibleBranches;
        this.driveDate = driveDate;
        this.status = status;
    }

    public String getDriveId() { return driveId; }
    public void setDriveId(String driveId) { this.driveId = driveId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }

    public double getCtcLpa() { return ctcLpa; }
    public void setCtcLpa(double ctcLpa) { this.ctcLpa = ctcLpa; }

    public double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(double minCgpa) { this.minCgpa = minCgpa; }

    public int getMaxBacklogs() { return maxBacklogs; }
    public void setMaxBacklogs(int maxBacklogs) { this.maxBacklogs = maxBacklogs; }

    public String getEligibleBranches() { return eligibleBranches; }
    public void setEligibleBranches(String eligibleBranches) { this.eligibleBranches = eligibleBranches; }

    public String getDriveDate() { return driveDate; }
    public void setDriveDate(String driveDate) { this.driveDate = driveDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String toCsvRow() {
        return String.join(",",
                escapeCsv(driveId),
                escapeCsv(companyName),
                escapeCsv(jobRole),
                String.valueOf(ctcLpa),
                String.valueOf(minCgpa),
                String.valueOf(maxBacklogs),
                escapeCsv(eligibleBranches),
                escapeCsv(driveDate),
                escapeCsv(status)
        );
    }

    public static CompanyDrive fromCsvRow(String line) {
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (parts.length < 9) return null;
        try {
            String id = unescapeCsv(parts[0]);
            String name = unescapeCsv(parts[1]);
            String role = unescapeCsv(parts[2]);
            double ctc = Double.parseDouble(parts[3].trim());
            double cgpa = Double.parseDouble(parts[4].trim());
            int backlogs = Integer.parseInt(parts[5].trim());
            String branches = unescapeCsv(parts[6]);
            String date = unescapeCsv(parts[7]);
            String status = unescapeCsv(parts[8]);
            return new CompanyDrive(id, name, role, ctc, cgpa, backlogs, branches, date, status);
        } catch (Exception e) {
            return null;
        }
    }

    private static String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static String unescapeCsv(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1).replace("\"\"", "\"");
        }
        return s;
    }
}
