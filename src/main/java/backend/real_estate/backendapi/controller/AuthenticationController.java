package backend.real_estate.backendapi.controller;


import backend.real_estate.backendapi.ExceptionHandling.*;
import backend.real_estate.backendapi.ExceptionHandling.NoSuchFieldException;
import backend.real_estate.backendapi.dto.OtpDto;
import backend.real_estate.backendapi.repository.UserRepository;
import backend.real_estate.backendapi.request.AuthenticationRequest;
import backend.real_estate.backendapi.request.AuthenticationResponse;
import backend.real_estate.backendapi.request.RegisterRequest;
import backend.real_estate.backendapi.service.impl.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserRepository userRepository;

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) throws Exception{
        try{
            service.register(request);
            return ResponseEntity.ok("OTP Sent Successfully ");
        }catch (EmailAlreadyExistException | NoSuchFieldException | InvalidEmailException | InvalidPasswordException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PostMapping("/sent")
    public void sentOTP(@RequestBody OtpDto otpDto){
        service.sendVerificationCode(otpDto.getEmail());
    }

    @PostMapping("/verify")
    public AuthenticationResponse verifyOTP(@RequestBody OtpDto otpDto){

         return service.verifyOtp(otpDto);
//        try{
//            return
////           return service.verifyOtp(otpDto);
////            return ResponseEntity.ok("OTP verified Successfully");
//        }catch (InvalidCredentialException ex){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
//        }
    }




    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) throws userNameNotFoundException  {
        try{
            AuthenticationResponse response = service.login(request);
            return ResponseEntity.ok(response);

        }catch (userNameNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }


}
