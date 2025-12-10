package app.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotNull
    private UUID fromWalletId; // от кой портфейл ще идват парите

    @NotNull
    private String toUsername; // кой ще получават парите

    @NotNull
    @Positive
    private BigDecimal amount; // колко пари ще идват





}
