package kz.btsd.edmarket.certificate.repository;

import kz.btsd.edmarket.certificate.model.Certificate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends CrudRepository<Certificate, Long> {
    List<Certificate> findByUserId(Long userId);

    Optional<Certificate> findByUserIdAndEventId(Long userId, Long eventId);

    @Transactional
    @Modifying
    void deleteByEventId(Long eventId);
}
