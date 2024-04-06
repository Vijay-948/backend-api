package backend.real_estate.backendapi.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPassword {
    private String email;
    private String newPassword;
    private String confirmPassword;

    public String getEmail(){
        return this.email;
    }
//
//    public String setPassword(){
//        return this.setPassword();
//    }
}
