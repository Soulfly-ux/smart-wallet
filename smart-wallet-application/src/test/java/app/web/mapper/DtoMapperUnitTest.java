package app.web.mapper;

import app.user.model.User;
import app.web.dto.EditProfileRequest;
import app.web.dto.mapper.DtoMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


//@ExtendWith(MockitoExtension.class) - може и да не го слагам защото няма мокове
public class DtoMapperUnitTest {

    @Test
    void givenHappyPath_whenMappingUserToEditProfileRequest() {

        // Given
        User user = User.builder()
                .firstName("Stamat")
                .lastName("Ahmakov")
                .email("stamat@abv.bg")
                .profilePicture("image.jpg")
                .build();

        // When
        EditProfileRequest dto = DtoMapper.mapUserToEditProfileRequest(user);


        //Then
       assertEquals(user.getFirstName(), dto.getFirstName());
       assertEquals(user.getLastName(), dto.getLastName());
       assertEquals(user.getEmail(), dto.getEmail());
       assertEquals(user.getProfilePicture(), dto.getProfilePictureUrl());

    }

}
