package kz.btsd.edmarket.event.moderation;

import kz.btsd.edmarket.user.model.Platform;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventModerationRepository extends CrudRepository<EventModeration, Long> {
    Optional<EventModeration> findByEventId(Long eventId);

    @Query("select m from EventModeration m join Event e on m.eventId=e.id where e.platform=:platform and m.status=:status")
    List<EventModeration> findByStatus(@Param("status") ModerationStatus moderationStatus, @Param("platform") Platform platform, Sort sort);

    @Query("select count(m) from EventModeration m join Event e on m.eventId=e.id where e.platform=:platform and m.status=:status")
    Long countByStatus(@Param("status") ModerationStatus moderationStatus, @Param("platform") Platform platform);

}
