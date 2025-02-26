package dasturlash.uz.repository;

import dasturlash.uz.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndIsDeletedFalse(String username);

    Optional<User> findByPhoneNumberAndIsDeletedFalse(String username);
}
