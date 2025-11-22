package app.wallet.model;


import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column
    @Enumerated(EnumType.STRING)
    private WalletStatus status;

    @Column
    private BigDecimal balance;




    @Column(name = "created_on")
    private LocalDateTime createdOn;


    @Column(name = "updated_on")
    private LocalDateTime updatedOn;
}
