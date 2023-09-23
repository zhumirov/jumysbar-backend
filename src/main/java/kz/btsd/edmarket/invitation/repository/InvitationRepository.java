package kz.btsd.edmarket.invitation.repository;

import kz.btsd.edmarket.invitation.model.InvitationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface InvitationRepository extends CrudRepository<InvitationEntity, Long> {

    boolean existsByPhoneAndEventId(String phone, Long eventId);

    @Transactional
    @Modifying
    void deleteByPhoneAndEventId(String phone, Long eventId);

    boolean existsByEmailAndEventId(String email, Long eventId);

    @Transactional
    @Modifying
    void deleteByEmailAndEventId(String email, Long eventId);

    List<InvitationEntity> findByCreatedDateAfter(Date createdDate, Pageable pageable);

    Long countByCreatedDateAfter(Date createdDate);

    List<InvitationEntity> findAllByPhone(String phone);

    List<InvitationEntity> findAllByEventId(Long eventId);

    List<InvitationEntity> findAll();
}
