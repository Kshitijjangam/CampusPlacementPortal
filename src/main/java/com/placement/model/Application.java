package com.placement.model;

/**
 * Represents a student's job application and interview progress.
 */
public class Application {
    private String applicationId; // e.g. APP-101
    private String studentUsn;
    private String studentName;
    private String driveId;
    private String companyName;
    private String jobRole;
    private String appliedDate;
    private String stage; // APPLIED, SHORTLISTED, ONLINE_TEST, TECHNICAL_INTERVIEW, HR_INTERVIEW, OFFERED, REJECTED
    private String interviewDate;
    private String notes;

    public Application() {}

    public Application(String applicationId, String studentUsn, String studentName, String driveId,
                       String companyName, String jobRole, String appliedDate,
                       String stage, String interviewDate, String notes) {
        this.applicationId = applicationId;
        this.studentUsn = studentUsn;
        this.studentName = studentName;
        this.driveId = driveId;
        this.companyName = companyName;
        this.jobRole = jobRole;
        this.appliedDate = appliedDate;
        this.stage = stage;
        this.interviewDate = (interviewDate != null && !interviewDate.isEmpty()) ? interviewDate : "-";
        this.notes = (notes != null) ? notes : "";
    }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getStudentUsn() { return studentUsn; }
    public void setStudentUsn(String studentUsn) { this.studentUsn = studentUsn; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getDriveId() { return driveId; }
    public void setDriveId(String driveId) { this.driveId = driveId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }

    public String getAppliedDate() { return appliedDate; }
    public void setAppliedDate(String appliedDate) { this.appliedDate = appliedDate; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getInterviewDate() { return interviewDate; }
    public void setInterviewDate(String interviewDate) { this.interviewDate = interviewDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String toCsvRow() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                applicationId, studentUsn, studentName, driveId, companyName, jobRole,
                appliedDate, stage, interviewDate, notes.replace(",", ";"));
    }

    public static Application fromCsvRow(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 10) return null;
        try {
            return new Application(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                    parts[3].trim(), parts[4].trim(), parts[5].trim(), parts[6].trim(),
                    parts[7].trim(), parts[8].trim(), parts[9].replace(";", ",").trim());
        } catch (Exception e) {
            return null;
        }
    }
}
