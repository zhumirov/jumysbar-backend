package kz.btsd.edmarket.online.repository;

import kz.btsd.edmarket.online.model.Subsection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface SubsectionRepository extends CrudRepository<Subsection, Long> {

    List<Subsection> findBySectionIdOrderByPositionAsc(Long sectionId);

    boolean existsBySectionIdAndFree(Long sectionId, boolean free);

    @Query(value = "select count(t1.id)\n" +
            "from subsection t1\n" +
            "where t1.section_id = ?1\n" +
            "  and t1.id in (select replace(t2.id\\:\\:text, '\"', '')\\:\\:integer from (select jsonb_array_elements(e.subsections) id\n" +
            "                from event_progress e\n" +
            "                where e.user_id=?2 and e.event_id=?3) t2)", nativeQuery = true)
    int completedSubsectionsCount(Long sectionId, Long userId, Long eventId);

    int countAllBySectionId(Long sectionId);

    List<Subsection> findAll(Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Subsection c SET " +
            "c.likes = (select count(sl.id) from StepLike sl where sl.stepId=?1 and sl.value=kz.btsd.edmarket.comment.like.model.LikeValue.LIKE), " +
            "c.dislikes = (select count(sl.id) from StepLike sl where sl.stepId=?1 and sl.value=kz.btsd.edmarket.comment.like.model.LikeValue.DISLIKE) " +
            "WHERE c.id=?1")
    void updateSteptLikeAndDislike(Long stepId);
}
