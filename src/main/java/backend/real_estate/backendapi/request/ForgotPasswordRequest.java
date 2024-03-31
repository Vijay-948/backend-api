package backend.real_estate.backendapi.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {
    private String email;
}
