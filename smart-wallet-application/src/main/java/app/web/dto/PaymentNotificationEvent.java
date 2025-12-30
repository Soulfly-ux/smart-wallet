package app.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class PaymentNotificationEvent {


    // поставяме информация каквато искаме да пренесем
    // за този потребител, с този email и този email е извършено плащане на тази дата

    private UUID userId;

    private String email;

    private BigDecimal amount;

    private LocalDate paymentTime;
}
