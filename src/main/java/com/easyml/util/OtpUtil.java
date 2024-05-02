package com.easyml.util;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtpUtil {

    private final EmailUtil emailUtil;

    public OtpUtil(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }

    public Integer generateOtp() {
        Random random = new Random();
        return random.nextInt(100000, 999999);
    }

    public void sendOtpEmail(String email, Integer otp) throws MessagingException {
        String subject = "EasyML - OTP Verification";
        String message = "<div>Your OTP is: %d</div>".formatted(otp);
        emailUtil.sendEmail(email, subject, message, true);
    }

}
