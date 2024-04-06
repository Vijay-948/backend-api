package backend.real_estate.backendapi.controller;


import backend.real_estate.backendapi.ExceptionHandling.ForgotPasswordExpection;
import backend.real_estate.backendapi.request.ForgotPasswordRequest;
import backend.real_estate.backendapi.request.ResetPassword;
import backend.real_estate.backendapi.request.VerifyOtp;
import backend.real_estate.backendapi.service.impl.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forgot")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;


    @Autowired
    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostMapping("/password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) throws ForgotPasswordExpection {
//        System.out.println(forgotPasswordService.sendOtpToEmail(forgotPasswordRequest.getEmail()));
        try {
            forgotPasswordService.sendOtpToEmail(forgotPasswordRequest.getEmail());
            return ResponseEntity.ok("Otp Sent Successfully");

        }catch (ForgotPasswordExpection e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtp verifyOtp){
        try{
            forgotPasswordService.verifyOtp(verifyOtp);
            return ResponseEntity.ok("OTP Verified Successfully");
        }catch (ForgotPasswordExpection e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPassword resetPassword){
        try {
            forgotPasswordService.resetPassword(resetPassword);
            return ResponseEntity.ok("Password Updated Successfully");
        }catch (ForgotPasswordExpection e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
