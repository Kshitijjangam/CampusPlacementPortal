package com.placement.service;

import com.placement.model.Student;

import java.util.*;

/**
 * Computes placement statistics and KPIs for college administration reports.
 */
public class AnalyticsService {

    public static class PlacementStats {
        public int totalStudents;
        public int placedStudents;
        public double placementPercentage;
        public double highestPackageLpa;
        public double averagePackageLpa;
        public Map<String, Integer> branchTotal = new LinkedHashMap<>();
        public Map<String, Integer> branchPlaced = new LinkedHashMap<>();
    }

    public PlacementStats calculate(List<Student> students) {
        PlacementStats stats = new PlacementStats();
        stats.totalStudents = students.size();

        double sumPackage = 0;
        int placedCount = 0;
        double maxPackage = 0;

        for (Student s : students) {
            String b = s.getBranch().toUpperCase();
            stats.branchTotal.put(b, stats.branchTotal.getOrDefault(b, 0) + 1);

            if ("PLACED".equalsIgnoreCase(s.getPlacementStatus())) {
                placedCount++;
                stats.branchPlaced.put(b, stats.branchPlaced.getOrDefault(b, 0) + 1);
                double salary = s.getPlacedPackageLpa();
                sumPackage += salary;
                if (salary > maxPackage) {
                    maxPackage = salary;
                }
            }
        }

        stats.placedStudents = placedCount;
        stats.placementPercentage = stats.totalStudents > 0 ? ((double) placedCount / stats.totalStudents) * 100.0 : 0.0;
        stats.highestPackageLpa = maxPackage;
        stats.averagePackageLpa = placedCount > 0 ? (sumPackage / placedCount) : 0.0;

        return stats;
    }
}
