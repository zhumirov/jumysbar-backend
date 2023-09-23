package kz.btsd.edmarket.certificate.repository;

import kz.btsd.edmarket.certificate.model.CertificateSettings;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CertificateSettingsRepository extends CrudRepository<CertificateSettings, Long> {
    Optional<CertificateSettings> findByEventId(Long eventId);
}
