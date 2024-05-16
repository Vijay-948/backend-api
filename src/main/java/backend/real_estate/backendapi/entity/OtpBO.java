package backend.real_estate.backendapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "otp_auth")
public class OtpBO {

    @Id
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "one_time_password", nullable = false)
    private String verificationCode;

    @Column(name = "otp_expiry_time", nullable = false)
    private Date otpExpiryTime;
}
