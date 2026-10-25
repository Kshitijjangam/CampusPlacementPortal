package com.placement.server;

import com.placement.data.DataStore;
import com.placement.model.Application;
import com.placement.model.CompanyDrive;
import com.placement.model.Student;
import com.placement.service.AnalyticsService;
import com.placement.service.EligibilityService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Embedded HTTP Server for Campus Placement Portal.
 * Enables web browser access at http://localhost:8080 alongside the Swing Desktop GUI.
 */
public class WebServer {

    private static DataStore dataStore;
    private static final EligibilityService eligibilityService = new EligibilityService();
    private static final AnalyticsService analyticsService = new AnalyticsService();

    public static int start(int defaultPort, DataStore store) {
        dataStore = store;
        int port = defaultPort;
        HttpServer server = null;

        for (int p = defaultPort; p < defaultPort + 10; p++) {
            try {
                server = HttpServer.create(new InetSocketAddress(p), 0);
                port = p;
                break;
            } catch (Exception ignored) {}
        }

        if (server == null) {
            System.err.println("Could not bind HTTP server.");
            return -1;
        }

        server.createContext("/api", new ApiHandler());
        server.createContext("/", new StaticHandler());
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("==================================================================");
        System.out.println(" >> Campus Placement Portal Server is LIVE!");
        System.out.println(" >> Web Interface: http://localhost:" + port);
        System.out.println(" >> Desktop GUI: Active on screen");
        System.out.println("==================================================================");

        return port;
    }

    private static class StaticHandler implements HttpHandler {
        private static final String BASE = "src/main/resources/web";

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) path = "/index.html";
            path = path.replace("..", "").replace("//", "/");

            byte[] data = null;
            File f = new File(BASE + path);
            if (f.exists() && !f.isDirectory()) {
                data = Files.readAllBytes(f.toPath());
            } else {
                try (InputStream is = getClass().getResourceAsStream("/web" + path)) {
                    if (is != null) data = is.readAllBytes();
                }
            }

            if (data != null) {
                String mime = "application/octet-stream";
                String l = path.toLowerCase();
                if (l.endsWith(".html")) mime = "text/html; charset=UTF-8";
                else if (l.endsWith(".css")) mime = "text/css; charset=UTF-8";
                else if (l.endsWith(".js")) mime = "application/javascript; charset=UTF-8";
                else if (l.endsWith(".json")) mime = "application/json; charset=UTF-8";

                exchange.getResponseHeaders().set("Content-Type", mime);
                exchange.sendResponseHeaders(200, data.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(data); }
            } else {
                byte[] notFound = "<h1>404 Resource Not Found</h1>".getBytes();
                exchange.sendResponseHeaders(404, notFound.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(notFound); }
            }
        }
    }

    private static class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod().toUpperCase();

            try {
                if (path.equals("/api/stats") && method.equals("GET")) {
                    handleStats(exchange);
                } else if (path.equals("/api/students") && method.equals("GET")) {
                    handleGetStudents(exchange);
                } else if (path.equals("/api/students") && method.equals("POST")) {
                    handleAddStudent(exchange);
                } else if (path.startsWith("/api/students/") && method.equals("DELETE")) {
                    dataStore.deleteStudent(path.substring("/api/students/".length()));
                    sendJson(exchange, 200, "{\"message\":\"Deleted\"}");
                } else if (path.equals("/api/drives") && method.equals("GET")) {
                    handleGetDrives(exchange);
                } else if (path.equals("/api/drives") && method.equals("POST")) {
                    handleAddDrive(exchange);
                } else if (path.startsWith("/api/drives/") && method.equals("DELETE")) {
                    dataStore.deleteDrive(path.substring("/api/drives/".length()));
                    sendJson(exchange, 200, "{\"message\":\"Deleted\"}");
                } else if (path.equals("/api/eligibility/check") && method.equals("POST")) {
                    handleCheckEligibility(exchange);
                } else if (path.equals("/api/applications/apply") && method.equals("POST")) {
                    handleApply(exchange);
                } else if (path.equals("/api/applications") && method.equals("GET")) {
                    handleGetApplications(exchange);
                } else if (path.equals("/api/applications/update") && method.equals("POST")) {
                    handleUpdateApplication(exchange);
                } else if (path.equals("/api/reset") && method.equals("POST")) {
                    dataStore.seedSampleData();
                    sendJson(exchange, 200, "{\"message\":\"Reset successful\"}");
                } else {
                    sendJson(exchange, 404, "{\"error\":\"Not found\"}");
                }
            } catch (Exception e) {
                sendJson(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }

        private void handleStats(HttpExchange exchange) throws Exception {
            AnalyticsService.PlacementStats stats = analyticsService.calculate(dataStore.getStudents());
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"totalStudents\":").append(stats.totalStudents).append(",");
            sb.append("\"placedStudents\":").append(stats.placedStudents).append(",");
            sb.append(String.format(java.util.Locale.US, "\"placementPercentage\":%.1f,", stats.placementPercentage));
            sb.append(String.format(java.util.Locale.US, "\"highestPackageLpa\":%.2f,", stats.highestPackageLpa));
            sb.append(String.format(java.util.Locale.US, "\"averagePackageLpa\":%.2f,", stats.averagePackageLpa));
            sb.append("\"branchStats\":[");
            int i = 0;
            for (String b : stats.branchTotal.keySet()) {
                int tot = stats.branchTotal.get(b);
                int plc = stats.branchPlaced.getOrDefault(b, 0);
                double pct = tot > 0 ? ((double) plc / tot) * 100.0 : 0.0;
                sb.append(String.format(java.util.Locale.US, "{\"branch\":\"%s\",\"total\":%d,\"placed\":%d,\"percent\":%.1f}", b, tot, plc, pct));
                if (i < stats.branchTotal.size() - 1) sb.append(",");
                i++;
            }
            sb.append("]}");
            sendJson(exchange, 200, sb.toString());
        }

        private void handleGetStudents(HttpExchange exchange) throws Exception {
            List<Student> list = dataStore.getStudents();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Student s = list.get(i);
                sb.append(String.format(java.util.Locale.US,
                        "{\"usn\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"branch\":\"%s\",\"cgpa\":%.2f,\"activeBacklogs\":%d,\"skills\":\"%s\",\"placementStatus\":\"%s\",\"placedCompany\":\"%s\",\"placedPackageLpa\":%.2f}",
                        s.getUsn(), s.getName(), s.getEmail(), s.getBranch(), s.getCgpa(), s.getActiveBacklogs(), s.getSkills(), s.getPlacementStatus(), s.getPlacedCompany(), s.getPlacedPackageLpa()));
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            sendJson(exchange, 200, sb.toString());
        }

        private void handleAddStudent(HttpExchange exchange) throws Exception {
            Map<String, String> body = parseJson(exchange);
            Student s = new Student(
                    body.getOrDefault("usn", "1RV22CS099"),
                    body.getOrDefault("name", "Student Name"),
                    body.getOrDefault("email", "student@college.edu"),
                    body.getOrDefault("branch", "CSE"),
                    Double.parseDouble(body.getOrDefault("cgpa", "8.0")),
                    Integer.parseInt(body.getOrDefault("activeBacklogs", "0")),
                    body.getOrDefault("skills", "Java"),
                    "NOT PLACED", "", 0.0
            );
            dataStore.addStudent(s);
            sendJson(exchange, 200, "{\"message\":\"Created\"}");
        }

        private void handleGetDrives(HttpExchange exchange) throws Exception {
            List<CompanyDrive> list = dataStore.getDrives();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                CompanyDrive d = list.get(i);
                sb.append(String.format(java.util.Locale.US,
                        "{\"driveId\":\"%s\",\"companyName\":\"%s\",\"jobRole\":\"%s\",\"ctcLpa\":%.2f,\"minCgpa\":%.2f,\"maxBacklogs\":%d,\"eligibleBranches\":\"%s\",\"driveDate\":\"%s\",\"status\":\"%s\"}",
                        d.getDriveId(), d.getCompanyName(), d.getJobRole(), d.getCtcLpa(), d.getMinCgpa(), d.getMaxBacklogs(), d.getEligibleBranches(), d.getDriveDate(), d.getStatus()));
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            sendJson(exchange, 200, sb.toString());
        }

        private void handleAddDrive(HttpExchange exchange) throws Exception {
            Map<String, String> body = parseJson(exchange);
            String id = "DRV-0" + (dataStore.getDrives().size() + 1);
            CompanyDrive d = new CompanyDrive(
                    id,
                    body.getOrDefault("companyName", "Tech Corp"),
                    body.getOrDefault("jobRole", "Software Engineer"),
                    Double.parseDouble(body.getOrDefault("ctcLpa", "10.0")),
                    Double.parseDouble(body.getOrDefault("minCgpa", "7.5")),
                    Integer.parseInt(body.getOrDefault("maxBacklogs", "0")),
                    body.getOrDefault("eligibleBranches", "CSE,ISE,ECE"),
                    body.getOrDefault("driveDate", "2026-11-20"),
                    "OPEN"
            );
            dataStore.addDrive(d);
            sendJson(exchange, 200, "{\"message\":\"Drive added\"}");
        }

        private void handleCheckEligibility(HttpExchange exchange) throws Exception {
            Map<String, String> body = parseJson(exchange);
            String usn = body.get("usn");
            String driveId = body.get("driveId");

            Student s = dataStore.findStudentByUsn(usn);
            CompanyDrive d = dataStore.findDriveById(driveId);

            EligibilityService.EligibilityResult res = eligibilityService.evaluate(s, d);
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"isEligible\":").append(res.isEligible()).append(",");
            sb.append("\"checks\":[");
            for (int i = 0; i < res.getDetails().size(); i++) {
                sb.append("\"").append(res.getDetails().get(i).replace("\"", "\\\"")).append("\"");
                if (i < res.getDetails().size() - 1) sb.append(",");
            }
            sb.append("]}");
            sendJson(exchange, 200, sb.toString());
        }

        private void handleApply(HttpExchange exchange) throws Exception {
            Map<String, String> body = parseJson(exchange);
            String usn = body.get("usn");
            String driveId = body.get("driveId");

            Student s = dataStore.findStudentByUsn(usn);
            CompanyDrive d = dataStore.findDriveById(driveId);

            if (s == null || d == null) {
                sendJson(exchange, 400, "{\"error\":\"Student or Drive not found\"}");
                return;
            }

            for (Application a : dataStore.getApplications()) {
                if (a.getStudentUsn().equalsIgnoreCase(usn) && a.getDriveId().equalsIgnoreCase(driveId)) {
                    sendJson(exchange, 400, "{\"error\":\"Already applied for this drive!\"}");
                    return;
                }
            }

            String id = "APP-" + (dataStore.getApplications().size() + 101);
            Application app = new Application(id, s.getUsn(), s.getName(), d.getDriveId(),
                    d.getCompanyName(), d.getJobRole(), LocalDate.now().toString(), "APPLIED", "-", "Application submitted");
            dataStore.addApplication(app);
            sendJson(exchange, 200, "{\"message\":\"Applied successfully\"}");
        }

        private void handleGetApplications(HttpExchange exchange) throws Exception {
            List<Application> list = dataStore.getApplications();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                Application a = list.get(i);
                sb.append(String.format("{\"applicationId\":\"%s\",\"studentUsn\":\"%s\",\"studentName\":\"%s\",\"driveId\":\"%s\",\"companyName\":\"%s\",\"jobRole\":\"%s\",\"appliedDate\":\"%s\",\"stage\":\"%s\",\"interviewDate\":\"%s\",\"notes\":\"%s\"}",
                        a.getApplicationId(), a.getStudentUsn(), a.getStudentName(), a.getDriveId(), a.getCompanyName(), a.getJobRole(), a.getAppliedDate(), a.getStage(), a.getInterviewDate(), a.getNotes()));
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            sendJson(exchange, 200, sb.toString());
        }

        private void handleUpdateApplication(HttpExchange exchange) throws Exception {
            Map<String, String> body = parseJson(exchange);
            String appId = body.get("applicationId");
            String stage = body.get("stage");
            String date = body.getOrDefault("interviewDate", "");

            for (Application a : dataStore.getApplications()) {
                if (a.getApplicationId().equalsIgnoreCase(appId)) {
                    a.setStage(stage);
                    if (!date.isEmpty()) a.setInterviewDate(date);
                    dataStore.updateApplication(a);

                    if ("OFFERED".equalsIgnoreCase(stage)) {
                        Student s = dataStore.findStudentByUsn(a.getStudentUsn());
                        CompanyDrive d = dataStore.findDriveById(a.getDriveId());
                        if (s != null && d != null) {
                            s.setPlacementStatus("PLACED");
                            s.setPlacedCompany(d.getCompanyName());
                            s.setPlacedPackageLpa(d.getCtcLpa());
                            dataStore.updateStudent(s);
                        }
                    }
                    sendJson(exchange, 200, "{\"message\":\"Stage updated\"}");
                    return;
                }
            }
            sendJson(exchange, 404, "{\"error\":\"Application not found\"}");
        }

        private Map<String, String> parseJson(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).trim();
            Map<String, String> map = new HashMap<>();
            if (body.startsWith("{") && body.endsWith("}")) {
                String trimmed = body.substring(1, body.length() - 1);
                String[] tokens = trimmed.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for (String t : tokens) {
                    int col = t.indexOf(":");
                    if (col != -1) {
                        String k = t.substring(0, col).trim().replace("\"", "");
                        String v = t.substring(col + 1).trim();
                        if (v.startsWith("\"") && v.endsWith("\"")) v = v.substring(1, v.length() - 1);
                        map.put(k, v);
                    }
                }
            }
            return map;
        }

        private void sendJson(HttpExchange exchange, int code, String json) throws IOException {
            byte[] b = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(code, b.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(b); }
        }
    }
}
