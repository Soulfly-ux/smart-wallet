package app.user.model;

import app.subscription.model.Subscription;
import app.wallet.model.Wallet;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)//зашото ни трябва при регистрация и логин на нов потребител
    private String password;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    @Enumerated(EnumType.STRING)
    private Country country;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on", nullable = false)
    private LocalDateTime updatedOn;


    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)//  FetchType.EAGER -> за да върне всички абонаменти за конкретния потребител
                                                           // mappedBy = "owner" -> това е полето в Subscription класа, където има поле "owner", за да знае Hibernate на кой потребител са тези абонаменти
    @OrderBy("createdOn DESC")// правим това, че на нулев индекс да е абонамент, който е най-нов
    private List<Subscription> subscriptions = new ArrayList<>();// ако по някаква причина потребителя няма абонамент, това поле да не връша null, а да връща празен списък


    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    @OrderBy("createdOn ASC")// дава на нулев индекса абонамент, който е най-стар- първият портфейл
    private List<Wallet> wallets = new ArrayList<>();




}
