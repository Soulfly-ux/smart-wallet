package app.transaction.model;

import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;


    @Column(nullable = false)
    private String sender;// от кой точно портфейл идват парите за дадена транзакция на owner

    @Column(nullable = false)
    private String receiver;

    @Column(nullable = false)
    private BigDecimal amount;


    @Column(nullable = false)
    private BigDecimal balanceLeft;

    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;// депозит или теглене

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Column(nullable = false)// причината за транзакцията, в практиката е добре да имаме причини за транзакцията
    private String description;


    @Column(name = "failure_reason")
    private String failureReason;


    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;// няма updatedOn защото няма да променяме транзакцията след като веднъж е създадена
}
