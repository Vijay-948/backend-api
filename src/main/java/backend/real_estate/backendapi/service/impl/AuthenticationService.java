package backend.real_estate.backendapi.service.impl;

import backend.real_estate.backendapi.ExceptionHandling.*;
import backend.real_estate.backendapi.ExceptionHandling.NoSuchFieldException;
import backend.real_estate.backendapi.dto.OtpDto;
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
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements OtpAuthService {

    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired private EmailService emailService;

    @Autowired OtpRepository otpRepository;

    public void register(RegisterRequest request) throws Exception, EmailAlreadyExistException, NoSuchFieldException, InvalidEmailException, InvalidPasswordException {

        if(StringUtils.isAnyBlank(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword())){
            throw new NoSuchFieldException("All fields are required");
        }
//
//        if(request.getFirstName().length() < 3){
//            throw new NoSuchFieldException("First name should be at least three characters");
//        }
//
//        if(request.getLastName().length() < 4){
//            throw new EmailAlreadyExistException("Last name should be at least four characters");
//        }
//
        if(!isValidEmail(request.getEmail())){
            throw new InvalidEmailException("Invalid email format");
        }

        if (!request.getEmail().toLowerCase().endsWith("@gmail.com")) {
            throw new IllegalArgumentException("Email must end with '@gmail.com'");
        }

//        if(!isValidPassword(request.getPassword())){
//            throw new InvalidPasswordException("Invalid Password format");
//        }

//        Optional<UserBo> existingEmail = userRepository.findByEmail(request.getEmail());
//
//        if(existingEmail.isPresent()){
//            throw new EmailAlreadyExistException("Email is Already exists");
//
//        }

        sendVerificationCode(request.getEmail());
    }

    public AuthenticationResponse login(AuthenticationRequest request) throws userNameNotFoundException {
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

            String jwtToken = jwtService.generateToken(user);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (AuthenticationException ex) {
            throw new userNameNotFoundException("Invalid username or password");
        }
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@gmail\\.com$";
        return email.matches(emailRegex);
    }

    @Override
    public void sendVerificationCode(String email) {
//        String email = otpRequest.get("email");

//        Optional<UserBo> userBo = userRepository.findByEmail(email);

        UserBo userBo = new UserBo();

        String subject = "One Time Password for Signup";
        String fileName = "otp_verification.html";

        String otp = generateOTP(6);

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
            throw new InvalidCredentialException("OTP Expiried Please go back click on send otp");
        }
//        RegisterRequest request = new RegisterRequest();
//        var userDetails = UserBo.builder()
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(Role.USER)
//                .build();
//        userRepository.save(userDetails);
//        var jwtToken = jwtService.generateToken(userDetails);
//        AuthenticationResponse.builder()
//                .token(jwtToken)
//                .build();
        RegisterRequest request = new RegisterRequest();
        UserBo userDetails = UserBo.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(otpDto.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(userDetails);

        String jwtToken = jwtService.generateToken(userDetails);

        // Clean up the OTP record
//        otpRepository.delete(userOtpBo);
//        otpRepository.delete(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();




    }

    private boolean isWithinTimeDiff(Date otpTimeStamp){
        if(otpTimeStamp == null) return false;

        LocalDateTime storedTime = otpTimeStamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime currentTime = LocalDateTime.now();

        return currentTime.isBefore(storedTime) || currentTime.equals(storedTime);
    }

    public String generateOTP(int length){
        String numbers = "0123456789";
        Random random = new Random();
        char[] otp = new char[length];

        for(int i = 0; i < otp.length; i++){
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }

        return new String(otp);
    }

//    public boolean isValidPassword(String password) {
//        String passwordPattern = "^(?!\\s)(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?!.*\\s).{8}$";
//        return password.matches(passwordPattern);
//    }
}