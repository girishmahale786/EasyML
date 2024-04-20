package com.easyml.service;

import com.easyml.model.Enquiry;
import com.easyml.repository.EnquiryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EnquiryService {

    private final EnquiryRepository enquiryRepository;
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;

    public EnquiryService(EnquiryRepository enquiryRepository, JavaMailSender javaMailSender) {

        this.enquiryRepository = enquiryRepository;
        this.javaMailSender = javaMailSender;
    }

    public Enquiry saveEnquiry(String name, String email, String enquiryText) {
        if (name == null || name.isEmpty() || email == null || email.isEmpty() || enquiryText == null || enquiryText.isEmpty()) {
            System.out.println(name + " " + email + " " + enquiryText);
            return null;
        } else {
            System.out.println(name + " " + email + " " + enquiryText);
            Enquiry enquiry = new Enquiry();
            enquiry.setName(name);
            enquiry.setEmail(email);
            enquiry.setEnquiryText(enquiryText);
            enquiry = enquiryRepository.save(enquiry);
            String status = sendSimpleMail(enquiry);
            System.out.println(status);
            return enquiry;

        }

    }

    public String sendSimpleMail(Enquiry enquiry) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(sender);
            message.setSubject("Enquiry from " + enquiry.getName());
            message.setText(enquiry.getEnquiryText() + "\n \nName: " + enquiry.getName() + " \nEmail: " + enquiry.getEmail());
            javaMailSender.send(message);
            return "Mail Sent Successfully";
        } catch (Exception e) {
            return "Error while Sending Mail";
        }

    }
}
