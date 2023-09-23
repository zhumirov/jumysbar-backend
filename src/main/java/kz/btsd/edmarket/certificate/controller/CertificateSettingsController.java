package kz.btsd.edmarket.certificate.controller;

import kz.btsd.edmarket.certificate.model.CertificateSettings;
import kz.btsd.edmarket.certificate.model.CertificateSettingsResponse;
import kz.btsd.edmarket.certificate.repository.CertificateSettingsRepository;
import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.elastic.EventElasticService;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
public class CertificateSettingsController {
    @Autowired
    private CertificateSettingsRepository certificateSettingsRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private EventElasticService eventElasticService;
    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/certificate/settings/{id}")
    public CertificateSettings findById(@PathVariable Long id) {
        CertificateSettings certificateSettings = certificateSettingsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        return certificateSettings;
    }

    @GetMapping("/certificate/settings")
    public CertificateSettingsResponse findByEvenId(@RequestParam Long eventId) {
        eventId = eventService.getParentEventId(eventId);
        Optional<CertificateSettings> optional = certificateSettingsRepository.findByEventId(eventId);
        if (optional.isPresent()) {
            return new CertificateSettingsResponse(true, optional.get());
        } else {
            return new CertificateSettingsResponse(false);
        }
    }

    public void updateCertInfo(Long eventId) {
        Event event = eventRepository.findById(eventId).get();
        if (event.getStatus().equals(EventStatus.APPROVED)) {
            eventElasticService.publish(event);
        }
    }


    @PostMapping("/certificate/settings")
    public CertificateSettings save(@RequestBody CertificateSettings certificateSettings) {
        certificateSettings.setEventId(eventService.getParentEventId(certificateSettings.getEventId()));
        certificateSettings = certificateSettingsRepository.save(certificateSettings);
        updateCertInfo(certificateSettings.getEventId());
        return certificateSettings;
    }

    @PutMapping("/certificate/settings/{id}")
    public CertificateSettings change(@RequestBody CertificateSettings certificateSettings, @PathVariable Long id) {
        certificateSettings.setEventId(eventService.getParentEventId(certificateSettings.getEventId()));
        certificateSettings = certificateSettingsRepository.save(certificateSettings);
        updateCertInfo(certificateSettings.getEventId());
        return certificateSettings;
    }

    //удалить  сертификаты по id
    @Transactional
    @DeleteMapping("/certificate/settings/{id}")
    public void delete(@PathVariable Long id) {
        CertificateSettings certificateSettings = certificateSettingsRepository.findById(id).get();
        Long eventId = eventService.getParentEventId(certificateSettings.getEventId());
        certificateSettingsRepository.deleteById(id);
        updateCertInfo(eventId);
    }
}
