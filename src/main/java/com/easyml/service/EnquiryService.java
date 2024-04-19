package com.easyml.service;

import com.easyml.model.Enquiry;
import com.easyml.repository.EnquiryRepository;

public class EnquiryService {

    private final EnquiryRepository enquiryRepository;

    public EnquiryService(EnquiryRepository enquiryRepository) {
        this.enquiryRepository = enquiryRepository;
    }

    public Enquiry saveEnquiry(String name, String email, String enquiryText) {
        if (name == null || name.isEmpty() || email == null || email.isEmpty() || enquiryText == null || enquiryText.isEmpty()) {
            return null;
        }else {
            Enquiry enquiry = new Enquiry();
            enquiry.setEmail(name);
            enquiry.setEmail(email);
            enquiry.setEnquiryText(enquiryText);
            return enquiryRepository.save(enquiry);
        }
    }
}
