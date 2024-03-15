package backend.real_estate.backendapi.controller;


import backend.real_estate.backendapi.ExceptionHandling.*;
import backend.real_estate.backendapi.ExceptionHandling.NoSuchFieldException;
import backend.real_estate.backendapi.entity.UserBo;
import backend.real_estate.backendapi.repository.UserRepository;
import backend.real_estate.backendapi.request.AuthenticationRequest;
import backend.real_estate.backendapi.request.AuthenticationResponse;
import backend.real_estate.backendapi.request.RegisterRequest;
import backend.real_estate.backendapi.service.impl.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserRepository userRepository;

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) throws Exception{
        try{
            AuthenticationResponse response = service.register(request);
            return ResponseEntity.ok(response);
        }catch (EmailAlreadyExistException | NoSuchFieldException | InvalidEmailException | InvalidPasswordException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }


    @PostMapping("/authenticate")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) throws userNameNotFoundException {
        try{
            AuthenticationResponse response = service.login(request);
            return ResponseEntity.ok(response);

        }catch (userNameNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }


}
