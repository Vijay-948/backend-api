package backend.real_estate.backendapi.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class ForgotPasswordController {

    @PostMapping("/forgot")
    public String forgotPassword(){
        return "";
    }





    @PostMapping("/generateOtp")
    public String generateOTP(@RequestParam("email") String email){
        System.out.println("email " + email);

        Random random = new Random(100001);
        int otp = random.nextInt(999999);

        System.out.println("OTP" + otp);

        return "verify_OTP";

    }

}
