package kz.btsd.edmarket.user.repository;

import kz.btsd.edmarket.user.model.link.InviteLink;
import kz.btsd.edmarket.user.model.link.InviteStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InviteLinkRepository extends CrudRepository<InviteLink, Long> {
    Optional<InviteLink> findByUuid(String uuid);

    List<InviteLink> findByEventIdAndStatus(Long eventId, InviteStatus status, Pageable pageable);
}
