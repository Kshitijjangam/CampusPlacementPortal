package com.placement.model;

/**
 * Entity Model: Application
 * Represents a student's application to a company drive and tracks
 * the candidate through the 7-stage recruitment interview pipeline.
 * 
 * @author Aayush Sharma (PRN: 1032220990)
 * Module: Recruitment Pipeline & Interview Tracker
 */
public class Application {
    private String applicationId; // e.g. APP-101
    private String studentUsn; // e.g. 1032230618
    private String studentName;
    private String driveId; // e.g. DRV-01
    private String companyName;
    private String jobRole;
    private String appliedDate; // YYYY-MM-DD
    private String stage; // APPLIED, SHORTLISTED, ONLINE_TEST, TECHNICAL_INTERVIEW, HR_INTERVIEW, OFFERED, REJECTED
    private String interviewDate; // YYYY-MM-DD or empty
    private String remarks;

    public Application() {}

    public Application(String applicationId, String studentUsn, String studentName,
                       String driveId, String companyName, String jobRole,
                       String appliedDate, String stage, String interviewDate, String remarks) {
        this.applicationId = applicationId;
        this.studentUsn = studentUsn;
        this.studentName = studentName;
        this.driveId = driveId;
        this.companyName = companyName;
        this.jobRole = jobRole;
        this.appliedDate = appliedDate;
        this.stage = stage;
        this.interviewDate = (interviewDate != null) ? interviewDate : "";
        this.remarks = (remarks != null) ? remarks : "";
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

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getNotes() { return remarks; }
    public void setNotes(String notes) { this.remarks = notes; }

    public String toCsvRow() {
        return String.join(",",
                escapeCsv(applicationId),
                escapeCsv(studentUsn),
                escapeCsv(studentName),
                escapeCsv(driveId),
                escapeCsv(companyName),
                escapeCsv(jobRole),
                escapeCsv(appliedDate),
                escapeCsv(stage),
                escapeCsv(interviewDate),
                escapeCsv(remarks)
        );
    }

    public static Application fromCsvRow(String line) {
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (parts.length < 8) return null;
        try {
            String id = unescapeCsv(parts[0]);
            String usn = unescapeCsv(parts[1]);
            String sname = unescapeCsv(parts[2]);
            String did = unescapeCsv(parts[3]);
            String cname = unescapeCsv(parts[4]);
            String role = unescapeCsv(parts[5]);
            String date = unescapeCsv(parts[6]);
            String stage = unescapeCsv(parts[7]);
            String idate = (parts.length > 8) ? unescapeCsv(parts[8]) : "";
            String rem = (parts.length > 9) ? unescapeCsv(parts[9]) : "";
            return new Application(id, usn, sname, did, cname, role, date, stage, idate, rem);
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
