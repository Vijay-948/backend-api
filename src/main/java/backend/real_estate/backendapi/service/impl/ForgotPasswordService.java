package backend.real_estate.backendapi.service.impl;

import backend.real_estate.backendapi.ExceptionHandling.ForgotPasswordExpection;
import backend.real_estate.backendapi.entity.OtpBO;
import backend.real_estate.backendapi.entity.UserBo;
import backend.real_estate.backendapi.repository.OtpRepository;
import backend.real_estate.backendapi.repository.UserRepository;
import backend.real_estate.backendapi.request.ResetPassword;
import backend.real_estate.backendapi.request.VerifyOtp;
import backend.real_estate.backendapi.service.EmailService;
import backend.real_estate.backendapi.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class ForgotPasswordService {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private EmailService emailService;
    private JwtService jwtService;

    @Autowired
    private OtpRepository otpRepository;


    public ForgotPasswordService(UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void sendOtpToEmail(String email) throws ForgotPasswordExpection, IOException {
        Optional<UserBo> userOptional = userRepository.findByEmail(email);

        if(userOptional.isPresent()){
            UserBo userBo = userOptional.get();
            String subject = "One Time Password for Reset Pin";
            String fileName = "otp_verification.html";

            String otp = CommonUtil.generateOTP(6);
            String content = StreamUtils.copyToString(new ClassPathResource(fileName).getInputStream(), Charset.defaultCharset());
            content = content.replace("[OTP]", otp);
            content = content.replace("[NAME]", userBo.getFirstName() + " " + userBo.getLastName());

            emailService.sendEmail(email, subject, content);

            OtpBO otpBO = new OtpBO();
            otpBO.setEmail(email);
            otpBO.setVerificationCode(otp);

            LocalDateTime presentTime = LocalDateTime.now().plusMinutes(10);
            Date expiryTime = Date.from(presentTime.atZone(ZoneId.systemDefault()).toInstant());
            otpBO.setOtpExpiryTime(expiryTime);
            otpRepository.save(otpBO);
        } else {
            throw  new ForgotPasswordExpection("Invalid email " + email);
        }
    }


    public void resetPassword(ResetPassword resetPassword) throws ForgotPasswordExpection {
        Optional<UserBo> user = userRepository.findByEmail(resetPassword.getEmail());

        String newPassword = resetPassword.getNewPassword();
        String confirmPassword = resetPassword.getConfirmPassword();

        UserBo userBo = user.get();

        if(!newPassword.equals(confirmPassword)){
            throw new ForgotPasswordExpection("newPassword & ConfirmPassword Must Match");
        }

        String encodedPassword = passwordEncoder.encode(confirmPassword);
        userBo.setPassword(encodedPassword);

        String newToken = jwtService.generateToken(userBo);

        userRepository.save(userBo);
    }

}
