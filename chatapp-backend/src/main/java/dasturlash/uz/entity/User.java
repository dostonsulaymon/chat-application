package dasturlash.uz.entity;

import dasturlash.uz.enums.GeneralStatus;
import dasturlash.uz.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "users",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"oauth_provider", "oauth_id"})}
)
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(unique = true, length = 50)
    private String username;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(unique = true, length = 100)
    private String email;  // Nullable now

    @Column(unique = true, length = 20)
    private String phoneNumber;  // Added phone number

    @Column(name = "password_hash")
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_picture_id")
    private Attach profilePicture;

    @Column(name = "connection_status", nullable = false, length = 20)
    private String connectionStatus = "OFFLINE";

    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private GeneralStatus accountStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "account_locked")
    private Boolean accountLocked = false;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "oauth_provider", length = 20)
    private String oauthProvider;

    @Column(name = "oauth_id", length = 100)
    private String oauthId;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    // Enforcing email or phone number must be provided
    @PrePersist
    @PreUpdate
    private void validateEmailOrPhone() {
        if (email == null && phoneNumber == null) {
            throw new IllegalStateException("Either email or phone number must be provided.");
        }
    }
}
