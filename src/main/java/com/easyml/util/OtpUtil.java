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

    public void sendOtpEmail(String email, Integer otp) throws EmailException {
        String subject = "EasyML - OTP Verification";
        String message = """
                <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Verify your login</title>
                    <style type="text/css">body, table, td, a { font-family: Arial, Helvetica, sans-serif !important; }</style>
                </head>
                                
                <body style="font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;">
                <table role="presentation"
                       style="width: 100%; border-collapse: collapse; border: 0px; border-spacing: 0px; font-family: Arial, Helvetica, sans-serif; background-color: rgb(239, 239, 239);">
                    <tbody>
                    <tr>
                        <td align="center" style="padding: 1rem 2rem; vertical-align: top; width: 100%;">
                            <table role="presentation" style="max-width: 600px; border-collapse: collapse; border: 0px; border-spacing: 0px; text-align: left;">
                                <tbody>
                                <tr>
                                    <td style="padding: 40px 0px 0px;">
                                        <div style="text-align: left;">
                                            <!---->
                                        </div>
                                        <div style="padding: 20px; background-color: rgb(255, 255, 255);">
                                            <div style="color: rgb(0, 0, 0); text-align: center;">
                                                <h1 style="margin: 1rem 0">Verification code</h1>
                                                <p style="padding-bottom: 16px">Please use the verification code below to sign in.</p>
                                
                                
                        <p style="padding-bottom: 16px"><strong style="font-size: 130%">
                """
                + otp +
                """ 
                                                        </strong></p>
                                        
                                                        <p style="padding-bottom: 16px">If you didn’t request this, you can ignore this email.</p>
                                                        <p style="padding-bottom: 16px">Thanks,<br>The EasyML team</p>
                                                    </div>
                                                </div>
                                                <div style="padding-top: 20px; color: rgb(153, 153, 153); text-align: center;">
                                                    <p style="padding-bottom: 16px">Made with ♥</p>
                                                </div>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                        </body>
                                        
                        </html>
                        """;
        emailUtil.sendEmail(email, subject, message, true);
    }

}
