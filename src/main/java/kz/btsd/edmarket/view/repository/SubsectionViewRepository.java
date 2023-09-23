package kz.btsd.edmarket.view.repository;

import kz.btsd.edmarket.view.model.SubsectionView;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface SubsectionViewRepository extends CrudRepository<SubsectionView, Long> {
    Optional<SubsectionView> findBySubsectionId(Long subsectionId);

    boolean existsBySubsectionId(Long subsectionId);

    @Transactional
    //@Modifying
    @Query(value = "UPDATE Subsection_View s SET " +
            "value = value+1" +
            "WHERE s.subsection_id=?1 " +
            "RETURNING s.value", nativeQuery = true)
    int incrementViewValue(Long subsectionId);
}
