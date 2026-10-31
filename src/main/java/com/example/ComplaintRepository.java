package com.example.demo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByReferenceNo(String referenceNo);
}