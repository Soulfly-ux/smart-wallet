package app.web.dto.mapper;

import app.user.model.User;
import app.web.dto.EditProfileRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static EditProfileRequest mapUserToEditProfileRequest(User user) {
        // направи от  user -> editProfileRequest

        return EditProfileRequest.builder().
                firstName(user.getFirstName()).
                lastName(user.getLastName()).
                email(user.getEmail()).
                profilePictureUrl(user.getProfilePicture()).
                build();
    }
}
