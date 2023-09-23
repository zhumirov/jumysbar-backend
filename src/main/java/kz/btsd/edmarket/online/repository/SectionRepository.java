package kz.btsd.edmarket.online.repository;

import kz.btsd.edmarket.online.model.Section;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface SectionRepository extends CrudRepository<Section, Long> {

    @Query("select s.id from Section s where s.eventId=?1")
    List<Long> findIdsByEventId(Long eventId);

    @Query("select s from Section s join Subsection sub on sub.sectionId=s.id where sub.id=?1")
    Section findBySubsectionid(Long subsectionId);

    @Query("select s.id from Section s where s.moduleId=?1 order by s.position")
    List<Long> findIdsByModuleIdOrderByPosition(Long moduleId);

    @Transactional
    @Modifying
    @Query("UPDATE Section c SET " +
            "c.interest = (select avg(sl.interest) from LessonRating sl where sl.lessonId=?1 and sl.interest is not null), " +
            "c.useful = (select avg(sl.useful) from LessonRating sl where sl.lessonId=?1 and sl.useful is not null)" +
            "WHERE c.id=?1")
    void updateLessonRating(Long lessonId);
}
