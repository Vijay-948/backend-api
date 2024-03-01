package backend.real_estate.backendapi.service.impl;

import backend.real_estate.backendapi.ExceptionHandling.EmailAlreadyExistException;
import backend.real_estate.backendapi.ExceptionHandling.userNameNotFoundException;
import backend.real_estate.backendapi.entity.Role;
import backend.real_estate.backendapi.entity.UserBo;
import backend.real_estate.backendapi.repository.UserRepository;
import backend.real_estate.backendapi.request.AuthenticationRequest;
import backend.real_estate.backendapi.request.AuthenticationResponse;
import backend.real_estate.backendapi.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final AuthenticationManager authenticationManager;

//    public AuthenticationService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService){
//        this.authenticationManager = authenticationManager;
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.jwtService = jwtService;
//    }

    public AuthenticationResponse register(RegisterRequest request) throws Exception, EmailAlreadyExistException {
        Optional<UserBo> existingEmail = userRepository.findByEmail(request.getEmail());

        if(existingEmail.isPresent()){
            throw new EmailAlreadyExistException("Email is Already exists");

        }
        var user = UserBo.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse login(AuthenticationRequest request) throws userNameNotFoundException {
        Optional<UserBo> email = userRepository.findByEmail(request.getEmail());

        if(email.isEmpty()){
            throw new userNameNotFoundException("Invalid username");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new userNameNotFoundException("Invalid User"));

            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }catch(AuthenticationException e){
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
