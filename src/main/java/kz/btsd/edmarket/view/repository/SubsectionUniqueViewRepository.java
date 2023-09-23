package kz.btsd.edmarket.view.repository;

import kz.btsd.edmarket.view.model.SubsectionUniqueView;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SubsectionUniqueViewRepository extends CrudRepository<SubsectionUniqueView, Long> {
    long countBySubsectionId(Long subsectionId);

    boolean existsBySubsectionIdAndUserId(Long subsectionId, Long userId);
    Optional<SubsectionUniqueView> findBySubsectionIdAndUserId(Long subsectionId, Long userId);
}
