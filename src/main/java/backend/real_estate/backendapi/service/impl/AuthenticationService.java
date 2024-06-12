package backend.real_estate.backendapi.service.impl;

import backend.real_estate.backendapi.ExceptionHandling.*;
import backend.real_estate.backendapi.ExceptionHandling.NoSuchFieldException;
import backend.real_estate.backendapi.dto.OtpDto;
import backend.real_estate.backendapi.dto.UserDetailsDto;
import backend.real_estate.backendapi.entity.OtpBO;
import backend.real_estate.backendapi.entity.Role;
import backend.real_estate.backendapi.entity.UserBo;
import backend.real_estate.backendapi.repository.OtpRepository;
import backend.real_estate.backendapi.repository.UserRepository;
import backend.real_estate.backendapi.request.AuthenticationRequest;
import backend.real_estate.backendapi.request.AuthenticationResponse;
import backend.real_estate.backendapi.request.RegisterRequest;
import backend.real_estate.backendapi.service.EmailService;
import backend.real_estate.backendapi.service.OtpAuthService;
import backend.real_estate.backendapi.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements OtpAuthService {

    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired private final EmailService emailService;

    @Autowired OtpRepository otpRepository;

    public void register(RegisterRequest request) throws Exception, EmailAlreadyExistException, NoSuchFieldException, InvalidEmailException, InvalidPasswordException {


        Optional<UserBo> existingEmail = userRepository.findByEmail(request.getEmail());

        if(!existingEmail.isEmpty()){
            UserBo email = existingEmail.get();
            if(!email.getActive()){
                sendVerificationCode(email.getEmail());

            }else{
                throw new EmailAlreadyExistException("Email is Already exists. Please go to Login Page");
            }


        }else {
            var user = UserBo.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .active(false)
                    .createdOn(new Date())
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(user);

            sendVerificationCode(request.getEmail());
        }



    }

    public AuthenticationResponse  login(AuthenticationRequest request) throws userNameNotFoundException {
        try {
            System.out.println("Received AuthenticationRequest: " + request.getEmail());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );


            UserBo user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new userNameNotFoundException("Invalid username or password"));

            if(!user.getActive()) {
                log.info("user status", user.getActive());
                throw new InvalidCredentialException("Invalid username or password");
            }

            log.info("user status", user.getActive());

            String jwtToken = jwtService.generateToken(user);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (AuthenticationException ex) {
            throw new userNameNotFoundException("Invalid username or password");
        }
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    @Override
    public void  sendVerificationCode(String email) {
//        String email = otpRequest.get("email");

        Optional<UserBo> optionalUserBo = userRepository.findByEmail(email);

        UserBo userBo = optionalUserBo.get();

        String subject = "One Time Password for Signup";
        String fileName = "otp_verification.html";

        String otp = CommonUtil.generateOTP(6);

        try{
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


        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthenticationResponse verifyOtp(OtpDto otpDto) {

        Optional<OtpBO> optionalOtpBO = otpRepository.findByEmail(otpDto.getEmail());

        if(optionalOtpBO.isEmpty()) throw new InvalidCredentialException("Invalid OTP");

        OtpBO user = optionalOtpBO.get();

        String otp = user.getVerificationCode();
        String userOtp = otpDto.getVerificationCode();

        if(!otp.equals(userOtp)){
            throw new InvalidCredentialException("Invalid OTP");
        }

        Date otpTimeStamp = user.getOtpExpiryTime();
        boolean isValid = isWithinTimeDiff(otpTimeStamp);

        if(!isValid){
            throw new InvalidCredentialException("OTP Expired Please go back click on send otp");
        }

        UserBo user_data = userRepository.findByEmail(otpDto.getEmail()).orElseThrow(() -> new InvalidCredentialException("Email not Found"));

        user_data.setActive(true);
        userRepository.save(user_data);

        otpRepository.delete(user);
//        RegisterRequest request = new RegisterRequest();

//        var userBo = UserBo.builder().email(otpDto.getEmail());

        var jwtToken = jwtService.generateToken(user_data);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public UserDetailsDto getFirstNameAndLastName(String token) throws userNameNotFoundException {
        String email  = jwtService.extractUserName(token);
        UserBo userBo = userRepository.findByEmail(email).orElseThrow(()-> new userNameNotFoundException("user Not Found"));

        return new UserDetailsDto(userBo.getFirstName(), userBo.getLastName(), userBo.getEmail());
    }

    private boolean isWithinTimeDiff(Date otpTimeStamp){
        if(otpTimeStamp == null) return false;

        LocalDateTime storedTime = otpTimeStamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime currentTime = LocalDateTime.now();

        return currentTime.isBefore(storedTime) || currentTime.equals(storedTime);
    }





//    public boolean isValidPassword(String password) {
//        String passwordPattern = "^(?!\\s)(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?!.*\\s).{8}$";
//        return password.matches(passwordPattern);
//    }

    // Scheduled method to delete unverified users
//    @Scheduled(cron = "0 0 0 * * ?") // This cron expression means the task will run every day at midnight
    @Scheduled(cron = "0 0 0 ? * SUN") //This cron expression means the task will run every Sunday at midnight
    @Transactional
    public void deleteUnverifiedUsers() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusMinutes(1);
        List<UserBo> unverifiedUsers = userRepository.findAllByActiveFalseAndCreatedOnBefore(Date.from(oneWeekAgo.atZone(ZoneId.systemDefault()).toInstant()));

        for (UserBo user : unverifiedUsers) {
            userRepository.delete(user);
        }

        System.out.println("Deleted unverified users: " + unverifiedUsers.size());
    }




}