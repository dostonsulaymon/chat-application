package dasturlash.uz.repository;

import dasturlash.uz.entity.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageStatusRepository extends JpaRepository<MessageStatus, UUID> {
}
