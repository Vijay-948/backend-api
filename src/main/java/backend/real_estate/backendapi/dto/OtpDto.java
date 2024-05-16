package backend.real_estate.backendapi.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OtpDto {
    private String email;
    private String verificationCode;
    private Date otpExpiryTime;
}
