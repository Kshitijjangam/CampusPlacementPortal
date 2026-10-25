package com.placement.data;

import com.placement.model.Application;
import com.placement.model.CompanyDrive;
import com.placement.model.Student;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Handles CSV file storage and retrieval for students, drives, and applications.
 * Enables offline, zero-database persistence.
 */
public class DataStore {

    private static final String DATA_DIR = "data";
    private static final String STUDENTS_FILE = DATA_DIR + "/students.csv";
    private static final String DRIVES_FILE = DATA_DIR + "/drives.csv";
    private static final String APPS_FILE = DATA_DIR + "/applications.csv";

    private final List<Student> students = new ArrayList<>();
    private final List<CompanyDrive> drives = new ArrayList<>();
    private final List<Application> applications = new ArrayList<>();

    public DataStore() {
        ensureDataDir();
        loadAllData();
        if (students.isEmpty() || drives.isEmpty()) {
            seedSampleData();
        }
    }

    private void ensureDataDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ================= STUDENTS =================
    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }

    public Student findStudentByUsn(String usn) {
        for (Student s : students) {
            if (s.getUsn().equalsIgnoreCase(usn)) return s;
        }
        return null;
    }

    public void addStudent(Student s) {
        students.add(s);
        saveStudents();
    }

    public void updateStudent(Student s) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getUsn().equalsIgnoreCase(s.getUsn())) {
                students.set(i, s);
                saveStudents();
                return;
            }
        }
    }

    public void deleteStudent(String usn) {
        students.removeIf(s -> s.getUsn().equalsIgnoreCase(usn));
        saveStudents();
    }

    // ================= DRIVES =================
    public List<CompanyDrive> getDrives() {
        return new ArrayList<>(drives);
    }

    public CompanyDrive findDriveById(String id) {
        for (CompanyDrive d : drives) {
            if (d.getDriveId().equalsIgnoreCase(id)) return d;
        }
        return null;
    }

    public void addDrive(CompanyDrive d) {
        drives.add(d);
        saveDrives();
    }

    public void deleteDrive(String id) {
        drives.removeIf(d -> d.getDriveId().equalsIgnoreCase(id));
        saveDrives();
    }

    // ================= APPLICATIONS =================
    public List<Application> getApplications() {
        return new ArrayList<>(applications);
    }

    public void addApplication(Application a) {
        applications.add(a);
        saveApplications();
    }

    public void updateApplication(Application a) {
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).getApplicationId().equalsIgnoreCase(a.getApplicationId())) {
                applications.set(i, a);
                saveApplications();
                return;
            }
        }
    }

    // ================= FILE LOAD & SAVE =================
    private void loadAllData() {
        loadStudents();
        loadDrives();
        loadApplications();
    }

    private void loadStudents() {
        students.clear();
        File f = new File(STUDENTS_FILE);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                Student s = Student.fromCsvRow(line);
                if (s != null) students.add(s);
            }
        } catch (Exception e) {
            System.err.println("Error reading students: " + e.getMessage());
        }
    }

    private void saveStudents() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENTS_FILE, StandardCharsets.UTF_8))) {
            for (Student s : students) {
                writer.write(s.toCsvRow());
                writer.newLine();
            }
        } catch (Exception e) {
            System.err.println("Error saving students: " + e.getMessage());
        }
    }

    private void loadDrives() {
        drives.clear();
        File f = new File(DRIVES_FILE);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                CompanyDrive d = CompanyDrive.fromCsvRow(line);
                if (d != null) drives.add(d);
            }
        } catch (Exception e) {
            System.err.println("Error reading drives: " + e.getMessage());
        }
    }

    private void saveDrives() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DRIVES_FILE, StandardCharsets.UTF_8))) {
            for (CompanyDrive d : drives) {
                writer.write(d.toCsvRow());
                writer.newLine();
            }
        } catch (Exception e) {
            System.err.println("Error saving drives: " + e.getMessage());
        }
    }

    private void loadApplications() {
        applications.clear();
        File f = new File(APPS_FILE);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                Application a = Application.fromCsvRow(line);
                if (a != null) applications.add(a);
            }
        } catch (Exception e) {
            System.err.println("Error reading applications: " + e.getMessage());
        }
    }

    private void saveApplications() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPS_FILE, StandardCharsets.UTF_8))) {
            for (Application a : applications) {
                writer.write(a.toCsvRow());
                writer.newLine();
            }
        } catch (Exception e) {
            System.err.println("Error saving applications: " + e.getMessage());
        }
    }

    // ================= SAMPLE DATA GENERATION =================
    public void seedSampleData() {
        students.clear();
        drives.clear();
        applications.clear();

        // Realistic B.Tech CSE / IT / ECE Student Profiles
        students.add(new Student("1RV22CS001", "Aarav Sharma", "aarav.cs@college.edu", "CSE", 8.85, 0, "Java, Spring, MySQL, DSA", "PLACED", "Google", 28.5));
        students.add(new Student("1RV22CS014", "Priya Patel", "priya.cs@college.edu", "CSE", 8.40, 0, "Python, Django, AWS, SQL", "NOT PLACED", "", 0.0));
        students.add(new Student("1RV22IS022", "Rohan Verma", "rohan.is@college.edu", "ISE", 7.60, 0, "C++, Java, Linux, OOPs", "PLACED", "Infosys", 7.2));
        students.add(new Student("1RV22EC008", "Ananya Iyer", "ananya.ec@college.edu", "ECE", 9.15, 0, "Java, Embedded C, Microcontrollers", "PLACED", "Microsoft", 34.0));
        students.add(new Student("1RV22CS031", "Karan Malhotra", "karan.cs@college.edu", "CSE", 6.80, 1, "HTML, CSS, JavaScript, PHP", "NOT PLACED", "", 0.0));
        students.add(new Student("1RV22IS045", "Sneha Reddy", "sneha.is@college.edu", "ISE", 7.90, 0, "Java, Android, Firebase", "PLACED", "TCS Digital", 9.0));
        students.add(new Student("1RV22ME019", "Vikas Nair", "vikas.me@college.edu", "MECH", 7.20, 0, "AutoCAD, Python, Analytics", "NOT PLACED", "", 0.0));
        students.add(new Student("1RV22CS052", "Neha Gupta", "neha.cs@college.edu", "CSE", 8.90, 0, "Machine Learning, Java, React", "NOT PLACED", "", 0.0));

        // Company Drives
        drives.add(new CompanyDrive("DRV-01", "Google", "Software Engineer - Campus", 28.5, 8.5, 0, "CSE,ISE", "2026-11-10", "OPEN"));
        drives.add(new CompanyDrive("DRV-02", "Microsoft", "Software Development Engineer (SDE-1)", 34.0, 8.0, 0, "CSE,ISE,ECE", "2026-11-18", "OPEN"));
        drives.add(new CompanyDrive("DRV-03", "Amazon", "Graduate Software Engineer", 26.0, 7.5, 0, "CSE,ISE,ECE", "2026-11-25", "OPEN"));
        drives.add(new CompanyDrive("DRV-04", "Infosys", "Specialist Programmer (Power Programmer)", 9.5, 6.5, 1, "CSE,ISE,ECE,MECH", "2026-12-02", "OPEN"));
        drives.add(new CompanyDrive("DRV-05", "TCS", "TCS Digital Associate", 7.5, 7.0, 0, "CSE,ISE,ECE", "2026-12-10", "OPEN"));

        // Applications in recruitment pipeline
        applications.add(new Application("APP-101", "1RV22CS001", "Aarav Sharma", "DRV-01", "Google", "Software Engineer - Campus", "2026-10-20", "OFFERED", "2026-10-24", "Cleared all technical rounds"));
        applications.add(new Application("APP-102", "1RV22CS014", "Priya Patel", "DRV-01", "Google", "Software Engineer - Campus", "2026-10-20", "TECHNICAL_INTERVIEW", "2026-10-28", "Cleared online coding assessment"));
        applications.add(new Application("APP-103", "1RV22EC008", "Ananya Iyer", "DRV-02", "Microsoft", "Software Development Engineer (SDE-1)", "2026-10-21", "OFFERED", "2026-10-25", "Selected for Azure team"));
        applications.add(new Application("APP-104", "1RV22IS045", "Sneha Reddy", "DRV-03", "Amazon", "Graduate Software Engineer", "2026-10-22", "ONLINE_TEST", "2026-10-30", "Shortlisted for coding round"));
        applications.add(new Application("APP-105", "1RV22CS052", "Neha Gupta", "DRV-02", "Microsoft", "Software Development Engineer (SDE-1)", "2026-10-23", "HR_INTERVIEW", "2026-10-29", "Final HR discussion"));

        saveStudents();
        saveDrives();
        saveApplications();
    }
}
