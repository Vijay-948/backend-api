package backend.real_estate.backendapi.dto;

import lombok.Data;

@Data
public class UserDetailsDto {

    private String firstName;
    private String lastName;
    private String email;

    public UserDetailsDto(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName=lastName;
        this.email = email;
    }
}
