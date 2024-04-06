package backend.real_estate.backendapi.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtp {
    private String email;
    private String otp;

    public String getEmail() {
        return this.email;
    }
}
