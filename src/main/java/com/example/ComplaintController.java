package com.example.demo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintRepository repo;
    private final String adminUsername;
    private final String adminPassword;

    private static final Set<String> STATUSES =
            Set.of("SUBMITTED", "IN_PROGRESS", "RESOLVED", "REJECTED");

    public ComplaintController(
            ComplaintRepository repo,
            @Value("${app.admin.username:}") String adminUsername,
            @Value("${app.admin.password:}") String adminPassword) {
        this.repo = repo;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    // Citizen: submit a complaint
    @PostMapping
    public Complaint submit(@RequestBody Complaint c) {
        if (c.getName() == null || c.getName().isBlank()
                || c.getDescription() == null || c.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and description are required");
        }
        if (c.getPhone() != null && !c.getPhone().isBlank() && !c.getPhone().matches("\\d{10}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone must be 10 digits");
        }
        c.setStatus("SUBMITTED");
        Complaint saved = repo.save(c);
        saved.setReferenceNo(String.format("CC-%05d", saved.getId()));
        return repo.save(saved);
    }

    // Citizen: track by reference number
    @GetMapping("/track/{ref}")
    public Complaint track(@PathVariable String ref) {
        return repo.findByReferenceNo(ref)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));
    }

    // Admin: login
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> b) {
        if (!adminUsername.isBlank() && !adminPassword.isBlank()
                && adminUsername.equals(b.get("username"))
                && adminPassword.equals(b.get("password"))) {
            return "ok";
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong login");
    }

    // Admin: list all complaints
    @GetMapping
    public List<Complaint> all() {
        return repo.findAll();
    }

    // Admin: update status and remarks
    @PutMapping("/{id}/status")
    public Complaint update(@PathVariable Long id, @RequestBody Map<String, String> b) {
        Complaint c = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));
        String s = b.get("status");
        if (s == null || !STATUSES.contains(s)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }
        if (c.getStatus().equals("RESOLVED") || c.getStatus().equals("REJECTED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Closed complaints cannot be changed");
        }
        c.setStatus(s);
        c.setRemarks(b.get("remarks"));
        return repo.save(c);
    }
}