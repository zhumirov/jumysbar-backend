package kz.btsd.edmarket.mentor.repository;

import kz.btsd.edmarket.mentor.model.Mentor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface MentorRepository extends CrudRepository<Mentor, Long> {
    boolean existsByPhoneAndEventId(String phone, Long eventId);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    Optional<Mentor> findByPhoneAndEventId(String phone, Long eventId);

    List<Mentor> findByEventId(Long eventId);

    @Transactional
    @Modifying
    @Query("UPDATE Mentor m SET m.userId = :userId WHERE m.phone=:phone")
    void updateMentorUserId(@Param("userId") Long userId, @Param("phone") String phone);
}
