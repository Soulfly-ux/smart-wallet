package app.web.dto;

import app.user.model.Country;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
@Builder
@Data  // Lombok - generates getters and setters + toString, equals, hashCode, constructor
public class RegisterRequest {

    @Size(min = 6, message = "Username must be at least 6 characters long")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotNull
    private Country country;
}
