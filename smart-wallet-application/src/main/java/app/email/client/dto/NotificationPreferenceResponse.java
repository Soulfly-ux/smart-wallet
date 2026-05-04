package app.email.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
public class NotificationPreferenceResponse {

//    това дто е за GET заявката от NotificationClient

//    private UUID id; Въпреки, че това дто в notification- svc има id , тук не ни трябва, защото в HTML - а нямаме ид, за да го показваме

    private UUID userId;

    private String type;

    private boolean enabled;

    private String contactInfo;
}
