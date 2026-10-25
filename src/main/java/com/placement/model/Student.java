package com.placement.model;

/**
 * Represents a student candidate in the campus placement system.
 */
public class Student {
    private String usn; // University Seat Number / Roll No, e.g. 1RV22CS001
    private String name;
    private String email;
    private String branch; // CSE, ISE, ECE, MECH
    private double cgpa;
    private int activeBacklogs;
    private String skills;
    private String placementStatus; // NOT PLACED, PLACED
    private String placedCompany;
    private double placedPackageLpa;

    public Student() {}

    public Student(String usn, String name, String email, String branch, double cgpa,
                   int activeBacklogs, String skills, String placementStatus,
                   String placedCompany, double placedPackageLpa) {
        this.usn = usn;
        this.name = name;
        this.email = email;
        this.branch = branch;
        this.cgpa = cgpa;
        this.activeBacklogs = activeBacklogs;
        this.skills = skills;
        this.placementStatus = placementStatus;
        this.placedCompany = (placedCompany != null) ? placedCompany : "";
        this.placedPackageLpa = placedPackageLpa;
    }

    public String getUsn() { return usn; }
    public void setUsn(String usn) { this.usn = usn; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public int getActiveBacklogs() { return activeBacklogs; }
    public void setActiveBacklogs(int activeBacklogs) { this.activeBacklogs = activeBacklogs; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getPlacementStatus() { return placementStatus; }
    public void setPlacementStatus(String placementStatus) { this.placementStatus = placementStatus; }

    public String getPlacedCompany() { return placedCompany; }
    public void setPlacedCompany(String placedCompany) { this.placedCompany = placedCompany; }

    public double getPlacedPackageLpa() { return placedPackageLpa; }
    public void setPlacedPackageLpa(double placedPackageLpa) { this.placedPackageLpa = placedPackageLpa; }

    // Convert student object to CSV format
    public String toCsvRow() {
        return String.format(java.util.Locale.US, "%s,%s,%s,%s,%.2f,%d,%s,%s,%s,%.2f",
                usn, name, email, branch, cgpa, activeBacklogs,
                skills.replace(",", ";"), placementStatus, placedCompany, placedPackageLpa);
    }

    // Parse CSV line into student object
    public static Student fromCsvRow(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 10) return null;
        try {
            String usn = parts[0].trim();
            String name = parts[1].trim();
            String email = parts[2].trim();
            String branch = parts[3].trim();
            double cgpa = Double.parseDouble(parts[4].trim());
            int backlogs = Integer.parseInt(parts[5].trim());
            String skills = parts[6].replace(";", ",").trim();
            String status = parts[7].trim();
            String comp = parts[8].trim();
            double salary = Double.parseDouble(parts[9].trim());
            return new Student(usn, name, email, branch, cgpa, backlogs, skills, status, comp, salary);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return usn + " - " + name + " (" + branch + ", CGPA: " + cgpa + ")";
    }
}
