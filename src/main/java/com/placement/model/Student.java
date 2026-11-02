package com.placement.model;

/**
 * Entity Model: Student
 * Represents a registered candidate in the campus recruitment system.
 * 
 * @author Kshitij Jangam (PRN: 1032230618)
 * Module: Student & Company Profile Registry
 */
public class Student {
    private String usn; // University Registration Number / PRN (e.g. 1032230618)
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

    public boolean isPlaced() {
        return "PLACED".equalsIgnoreCase(this.placementStatus);
    }

    /**
     * Serializes student object into CSV row format.
     */
    public String toCsvRow() {
        return String.join(",",
                escapeCsv(usn),
                escapeCsv(name),
                escapeCsv(email),
                escapeCsv(branch),
                String.valueOf(cgpa),
                String.valueOf(activeBacklogs),
                escapeCsv(skills),
                escapeCsv(placementStatus),
                escapeCsv(placedCompany),
                String.valueOf(placedPackageLpa)
        );
    }

    /**
     * Parses a CSV row into a Student entity instance.
     */
    public static Student fromCsvRow(String line) {
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (parts.length < 8) return null;
        try {
            String usn = unescapeCsv(parts[0]);
            String name = unescapeCsv(parts[1]);
            String email = unescapeCsv(parts[2]);
            String branch = unescapeCsv(parts[3]);
            double cgpa = Double.parseDouble(parts[4].trim());
            int backlogs = Integer.parseInt(parts[5].trim());
            String skills = unescapeCsv(parts[6]);
            String status = unescapeCsv(parts[7]);
            String company = (parts.length > 8) ? unescapeCsv(parts[8]) : "";
            double pkg = (parts.length > 9 && !parts[9].trim().isEmpty()) ? Double.parseDouble(parts[9].trim()) : 0.0;
            return new Student(usn, name, email, branch, cgpa, backlogs, skills, status, company, pkg);
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
