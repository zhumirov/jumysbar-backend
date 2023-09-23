package kz.btsd.edmarket.subscription.repository;

import kz.btsd.edmarket.subscription.model.EmailMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmailMessageRepository extends CrudRepository<EmailMessage, Long> {
    Optional<EmailMessage> findFirstByEventIdOrderByCreatedDateDesc(Long eventId);
}
