package app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileRequest {
    @Size(max = 20, message = "First name must be less than 20 characters")
    private String firstName;
    @Size(max = 20, message = "Last name must be less than 20 characters")
    private String lastName;
    @Email(message = "Requires correct email format")
    private String email;
    @URL(message = "Profile picture url must be a valid URL")
    private String profilePictureUrl;
}
