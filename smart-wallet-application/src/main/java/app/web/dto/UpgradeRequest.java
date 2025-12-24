package app.web.dto;

import app.subscription.model.SubscriptionPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpgradeRequest {

    private SubscriptionPeriod subscriptionPeriod;

    private UUID walletId; // за да знаем кой wallet ще се вземат парите за този абонамент

}
