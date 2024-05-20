package backend.real_estate.backendapi.service.impl;

import backend.real_estate.backendapi.ExceptionHandling.ForgotPasswordExpection;
import backend.real_estate.backendapi.entity.OtpBO;
import backend.real_estate.backendapi.entity.UserBo;
import backend.real_estate.backendapi.repository.UserRepository;
import backend.real_estate.backendapi.request.ResetPassword;
import backend.real_estate.backendapi.request.VerifyOtp;
import backend.real_estate.backendapi.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Optional;
import java.util.Random;

@Service
public class ForgotPasswordService {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private EmailService emailService;

    private JwtService jwtService;


    public ForgotPasswordService(UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void sendOtpToEmail(String email) throws ForgotPasswordExpection {
        Optional<UserBo> userOptional = userRepository.findByEmail(email);

//        if(userOptional.isPresent()){
//            OtpBO user = userOptional.get();
//            String otp = generateOTP();
//            user.setOtp(otp);
//            userRepository.save(user);
//            sendOtpByEmail(email, otp);
//        } else {
//            throw  new ForgotPasswordExpection("Invalid email " + email);
//        }
    }

    private String generateOTP() {
        Random random = new Random();
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

    public void verifyOtp(VerifyOtp verifyOtp) throws ForgotPasswordExpection{
        String userEmail = verifyOtp.getEmail();
        String userEnteredOtp = verifyOtp.getOtp();
        Optional<UserBo> user = userRepository.findByEmail(userEmail);

//        if(user.isPresent()){
//            UserBo userBo = user.get();
//            String storedOtp = userBo.getOtp();
//
//            if(!userEnteredOtp.equals(storedOtp)){
//                throw new ForgotPasswordExpection("Invalid Otp");
//            }
//
//            userBo.setOtp(null);
//            userRepository.save(userBo);
//        }

        // this method is only for verifying only otp based not an email;
//        String userEnteredOtp = verifyOtp.getOtp();
//
//        Optional<UserBo> userOptional = userRepository.findByOtp(userEnteredOtp);
//
//        if (userOptional.isPresent()) {
//            UserBo user = userOptional.get();
//            String storedOtp = user.getOtp(); // Assuming there's a method to get the OTP from the UserBo class
//
//            if (!userEnteredOtp.equals(storedOtp)) {
//                throw new ForgotPasswordException("Invalid OTP");
//            }
//
//            // Clear the OTP after successful verification
//            user.setOtp(null);
//            userRepository.save(user);
//        } else {
//            throw new ForgotPasswordException("Invalid OTP");
//        }

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
