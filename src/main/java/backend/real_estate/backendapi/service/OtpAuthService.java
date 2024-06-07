package backend.real_estate.backendapi.service;

import backend.real_estate.backendapi.ExceptionHandling.userNameNotFoundException;
import backend.real_estate.backendapi.dto.OtpDto;
import backend.real_estate.backendapi.dto.UserDetailsDto;
import backend.real_estate.backendapi.request.AuthenticationResponse;

public interface OtpAuthService {

    void sendVerificationCode(String email);

    AuthenticationResponse verifyOtp(OtpDto otpDto);

     UserDetailsDto getFirstNameAndLastName(String token) throws userNameNotFoundException;
}
