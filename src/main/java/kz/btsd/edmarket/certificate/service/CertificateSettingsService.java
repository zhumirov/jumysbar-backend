package kz.btsd.edmarket.certificate.service;

import kz.btsd.edmarket.certificate.model.CertificateSettings;
import kz.btsd.edmarket.certificate.repository.CertificateSettingsRepository;
import kz.btsd.edmarket.event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CertificateSettingsService {
    @Autowired
    private EventService eventService;
    @Autowired
    private CertificateSettingsRepository certificateSettingsRepository;

    public Optional<CertificateSettings> findByEvenId(Long eventId) {
        eventId = eventService.getParentEventId(eventId);
        return certificateSettingsRepository.findByEventId(eventId);
    }
}
