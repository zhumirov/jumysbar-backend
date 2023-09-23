package kz.btsd.edmarket.event.repository;

import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.event.model.EventTitleDto;
import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.UserShortDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends CrudRepository<Event, Long> {
    Optional<Event> findByParentId(Long parentId);

    List<Event> findAllByPlatform(Platform platform, Sort sort);

    List<Event> findAllByStatus(EventStatus status, Platform platform, Sort sort);

    List<Event> findAllByStatus(EventStatus status, Pageable pageable);

    List<Event> findAll(Pageable pageable);

    List<Event> findAllByUserIdAndPlatformAndStatusIn(Long userId, Platform platform, List<EventStatus> statuses, Sort sort);

    List<Event> findAllByUserIdNotAndPlatformAndStatusIn(Long userId, Platform platform, List<EventStatus> statuses, Sort sort);

    List<Event> findByCreatedDateAfter(Date createdDate, Pageable pageable);

    long countByCreatedDateAfter(Date createdDate);

    Long countByStatus(EventStatus status);

    @Query("select e from Event e where e.id=(select m.eventId from Module m where m.id=(select s.moduleId from Section s where s.id=:lessonId))")
    Event findByLessonId(@Param("lessonId") Long lessonId);

    @Query("select e from Event e where e.id=(select m.eventId from Module m where m.id=(select s.moduleId from Section s where s.id=(select sb.sectionId from Subsection sb where sb.id=:stepId)))")
    Event findByStepId(@Param("stepId") Long stepId);

    @Query("select e from Event e join EventModeration m on e.id=m.eventId where e.platform=:platform and m.status=kz.btsd.edmarket.event.moderation.ModerationStatus.NEW")
    List<Event> findModerationEvent(@Param("platform") Platform platform, Sort sort);

    @Transactional
    @Modifying
    @Query("UPDATE Event e SET e.subsectionsSize = (select sum(s.subsectionsSize) from Section s join Module m on m.id=s.moduleId where m.eventId=?1 and s.type = kz.btsd.edmarket.online.model.SectionType.LESSON) WHERE e.id=?1")
    void updateSubsectionsSize(Long eventId);

    @Query("select e from Event e join Mentor m on e.id=m.eventId where m.userId=:userId and e.parentId is not null")
    List<Event> findMentorEventsByUserId(@Param("userId") Long userId);

    @Query("select new kz.btsd.edmarket.event.model.EventTitleDto(e.id, e.title) from Event e join Subscription s on e.id = s.eventId  where s.userId = ?1")
    List<EventTitleDto> findAllSubscribedEventdByUserId(Long userId);

    @Query("SELECT s.eventId FROM Subscription s join Event e on s.eventId=e.id where e.platform=:platform GROUP BY s.eventId ORDER BY COUNT(s.eventId) DESC")
    List<Long> countMostSubscribedByPlatform(@Param("platform") Platform platform, Pageable pageable);

    @Query("select new kz.btsd.edmarket.event.model.EventTitleDto(e.id, e.title) from Event e where e.id = :eventId")
    Optional<EventTitleDto> findByIdEventTitleDto(@Param("eventId") Long eventId);
}
