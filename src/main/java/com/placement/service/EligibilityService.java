package com.placement.service;

import com.placement.model.CompanyDrive;
import com.placement.model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to evaluate eligibility criteria for placement drives.
 */
public class EligibilityService {

    public static class EligibilityResult {
        private final boolean eligible;
        private final List<String> details;

        public EligibilityResult(boolean eligible, List<String> details) {
            this.eligible = eligible;
            this.details = details;
        }

        public boolean isEligible() { return eligible; }
        public List<String> getDetails() { return details; }
    }

    /**
     * Checks if a student is eligible for a specific company placement drive.
     */
    public EligibilityResult evaluate(Student student, CompanyDrive drive) {
        List<String> details = new ArrayList<>();
        boolean isEligible = true;

        if (student == null || drive == null) {
            details.add("Invalid student or drive selection.");
            return new EligibilityResult(false, details);
        }

        // 1. CGPA Criterion
        if (student.getCgpa() >= drive.getMinCgpa()) {
            details.add(String.format("PASS: Student CGPA (%.2f) meets minimum requirement (%.2f)",
                    student.getCgpa(), drive.getMinCgpa()));
        } else {
            isEligible = false;
            details.add(String.format("FAIL: Student CGPA (%.2f) is below cutoff (%.2f)",
                    student.getCgpa(), drive.getMinCgpa()));
        }

        // 2. Active Backlogs Criterion
        if (student.getActiveBacklogs() <= drive.getMaxBacklogs()) {
            details.add(String.format("PASS: Active backlogs (%d) within allowed limit (%d)",
                    student.getActiveBacklogs(), drive.getMaxBacklogs()));
        } else {
            isEligible = false;
            details.add(String.format("FAIL: Active backlogs (%d) exceed allowed limit (%d)",
                    student.getActiveBacklogs(), drive.getMaxBacklogs()));
        }

        // 3. Branch / Department Criterion
        String branches = drive.getEligibleBranches().toUpperCase();
        if (branches.contains(student.getBranch().toUpperCase()) || branches.contains("ALL")) {
            details.add("PASS: Branch (" + student.getBranch() + ") is eligible");
        } else {
            isEligible = false;
            details.add("FAIL: Branch (" + student.getBranch() + ") not eligible. Allowed: " + drive.getEligibleBranches());
        }

        // 4. College Dream Company Policy
        // If student is already placed, new drive must offer >= 1.5x current package
        if ("PLACED".equalsIgnoreCase(student.getPlacementStatus()) && student.getPlacedPackageLpa() > 0) {
            double threshold = student.getPlacedPackageLpa() * 1.5;
            if (drive.getCtcLpa() >= threshold) {
                details.add(String.format("PASS: Dream Company Policy: Drive CTC (%.1f LPA) >= 1.5x current package (%.1f LPA)",
                        drive.getCtcLpa(), student.getPlacedPackageLpa()));
            } else {
                isEligible = false;
                details.add(String.format("FAIL: Placed Policy: Current package is %.1f LPA. Must be at least %.1f LPA (1.5x) to apply.",
                        student.getPlacedPackageLpa(), threshold));
            }
        }

        return new EligibilityResult(isEligible, details);
    }
}
