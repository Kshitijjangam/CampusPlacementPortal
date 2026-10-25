package com.placement.model;

/**
 * Represents a company placement drive with eligibility criteria.
 */
public class CompanyDrive {
    private String driveId; // e.g. DRV-01
    private String companyName;
    private String jobRole;
    private double ctcLpa;
    private double minCgpa;
    private int maxBacklogs;
    private String eligibleBranches; // e.g. CSE;IT;ECE
    private String driveDate;
    private String status; // OPEN, CLOSED

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
        return String.format(java.util.Locale.US, "%s,%s,%s,%.2f,%.2f,%d,%s,%s,%s",
                driveId, companyName, jobRole, ctcLpa, minCgpa, maxBacklogs,
                eligibleBranches.replace(",", ";"), driveDate, status);
    }

    public static CompanyDrive fromCsvRow(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 9) return null;
        try {
            String id = parts[0].trim();
            String comp = parts[1].trim();
            String role = parts[2].trim();
            double ctc = Double.parseDouble(parts[3].trim());
            double cgpa = Double.parseDouble(parts[4].trim());
            int backlogs = Integer.parseInt(parts[5].trim());
            String branches = parts[6].replace(";", ",").trim();
            String date = parts[7].trim();
            String status = parts[8].trim();
            return new CompanyDrive(id, comp, role, ctc, cgpa, backlogs, branches, date, status);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return companyName + " - " + jobRole + " (" + ctcLpa + " LPA)";
    }
}
