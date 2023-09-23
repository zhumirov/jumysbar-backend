package kz.btsd.edmarket.event.promocode;

import kz.btsd.edmarket.event.model.EntityStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PromoCodeRepository extends CrudRepository<Promocode, Long> {
    Optional<Promocode> findByEventIdAndTitleAndStatus(Long eventId, String title, EntityStatus status);

    Optional<Promocode> findByParentId(Long parentId);
}
