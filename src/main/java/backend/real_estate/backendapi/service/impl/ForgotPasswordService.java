package backend.real_estate.backendapi.service.impl;

import backend.real_estate.backendapi.entity.UserBo;
import backend.real_estate.backendapi.repository.UserRepository;
import backend.real_estate.backendapi.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Optional;
import java.util.Random;

@Service
public class ForgotPasswordService {
    private UserRepository userRepository;
    private EmailService emailService;

    @Autowired
    public ForgotPasswordService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void sendOtpToEmail(String email){
        Optional<UserBo> user = userRepository.findByEmail(email);

        if(user == null){
            throw new NotFoundException("hfjadkfaksg");
        }

        String otp = generateOTP();
    }

    private String generateOTP() {
        Random random = new Random(100001);
        int otp = random.nextInt(999999);

        String otpString = String.valueOf(otp);

        System.out.println("Generated OTp " + otpString);

        return otpString;
    }

    private void sendOtpByEmail(String email, String otp){
        String subject = "Password Reset Otp";
        String body = "Your Otp" + otp;

        emailService.sendEmail(email, subject, body);
    }

}
