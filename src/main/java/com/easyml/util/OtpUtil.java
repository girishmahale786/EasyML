package com.easyml.util;

import com.easyml.exception.EmailException;
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

    public void sendOtpEmail(String name, String email, Integer otp) throws EmailException {
        String subject = "EasyML - OTP Verification";
        String message = "<div>Your OTP is: %d</div>".formatted(otp);
        emailUtil.sendEmail(email, subject, message, true);
    }

}
