package backend.real_estate.backendapi.request;

import backend.real_estate.backendapi.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {


    private String firstName;
    private String lastName;
    private String email;
    private String password;

}
