package com.easyml.repository;

import com.easyml.model.Enquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnquiryRepository extends JpaRepository<Enquiry,Long> {

    Optional<Enquiry> findById(long l);
    Optional<Enquiry> findByName(String name);
    Optional<Enquiry> findByEmail(String email);
}
