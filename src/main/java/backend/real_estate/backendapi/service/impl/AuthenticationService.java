package backend.real_estate.backendapi.service.impl;

import backend.real_estate.backendapi.ExceptionHandling.EmailAlreadyExistException;
import backend.real_estate.backendapi.ExceptionHandling.InvalidEmailException;
import backend.real_estate.backendapi.ExceptionHandling.NoSuchFieldException;
import backend.real_estate.backendapi.ExceptionHandling.userNameNotFoundException;
import backend.real_estate.backendapi.entity.Role;
import backend.real_estate.backendapi.entity.UserBo;
import backend.real_estate.backendapi.repository.UserRepository;
import backend.real_estate.backendapi.request.AuthenticationRequest;
import backend.real_estate.backendapi.request.AuthenticationResponse;
import backend.real_estate.backendapi.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    public AuthenticationResponse register(RegisterRequest request) throws Exception, EmailAlreadyExistException, NoSuchFieldException, InvalidEmailException {

        if(StringUtils.isAnyBlank(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword())){
            throw new NoSuchFieldException("All fields are required");
        }

        if(request.getFirstName().length() < 3){
            throw new NoSuchFieldException("First name should be at least three characters");
        }

        if(request.getLastName().length() < 4){
            throw new EmailAlreadyExistException("Last name should be at least four characters");
        }

        if(!isValidEmail(request.getEmail())){
            throw new InvalidEmailException("Invalid email format");
        }

        if (!request.getEmail().toLowerCase().endsWith("@gmail.com")) {
            throw new IllegalArgumentException("Email must end with '@gmail.com'");
        }

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
        Optional<UserBo> emailOptional = userRepository.findByEmail(request.getEmail());

        if(emailOptional.isEmpty()){
            throw new userNameNotFoundException("Invalid username or password");
        }

        UserBo user = emailOptional.get();
        BCryptPasswordEncoder passwordEncoder1 = new BCryptPasswordEncoder();
        if (emailOptional.isPresent() && !passwordEncoder1.matches(request.getPassword(), user.getPassword())) {
            throw new userNameNotFoundException("Invalid password");
        }


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var jwtToken = jwtService.generateToken(user);
        Optional<UserBo> userBo = userRepository.findByEmail(request.getEmail());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@gmail\\.com$";
        return email.matches(emailRegex);
    }
}
