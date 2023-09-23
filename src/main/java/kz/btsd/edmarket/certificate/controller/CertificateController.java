package kz.btsd.edmarket.certificate.controller;

import kz.btsd.edmarket.certificate.model.Certificate;
import kz.btsd.edmarket.certificate.model.CertificateConverter;
import kz.btsd.edmarket.certificate.model.CertificateInfo;
import kz.btsd.edmarket.certificate.model.CertificateRequest;
import kz.btsd.edmarket.certificate.model.CertificateSettings;
import kz.btsd.edmarket.certificate.repository.CertificateRepository;
import kz.btsd.edmarket.certificate.repository.CertificateSettingsRepository;
import kz.btsd.edmarket.certificate.service.CertificateService;
import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.online.progress.EventProgressService;
import kz.btsd.edmarket.online.progress.testhomework.EventProgressUserRow;
import kz.btsd.edmarket.online.progress.testhomework.EventProgressUsersTableDto;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
public class CertificateController {
    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private CertificateSettingsRepository certificateSettingsRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CertificateService certificateService;

    //выдать сертификаты
    @PostMapping("/certificate")
    public List<Certificate> save(@RequestBody CertificateRequest request) {
        return certificateService.saveCertificate(request);
    }

    //вернуть сертификат по id
    @GetMapping("/certificate/{id}")
    public CertificateInfo findById(@PathVariable Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        certificate.setUser(userRepository.findByIdShortDto(certificate.getUserId()).get());
        Long eventId = eventService.getParentEventId(certificate.getEventId());
        CertificateSettings certificateSettings = certificateSettingsRepository.findByEventId(eventId)
                .orElseThrow(() -> new EntityNotFoundException(id));
        return new CertificateInfo(certificate, certificateSettings);
    }

    //вернуть все сертификаты пользователя
    @GetMapping("/certificate")
    public List<CertificateInfo> allByUserId(@RequestParam Long userId) {
        List<CertificateInfo> infos = new LinkedList<>();
        List<Certificate> certificates = certificateRepository.findByUserId(userId);
        for (Certificate certificate :
                certificates) {
            certificate.setUser(userRepository.findByIdShortDto(certificate.getUserId()).get());
            Long eventId = eventService.getParentEventId(certificate.getEventId());
            CertificateSettings certificateSettings = certificateSettingsRepository.findByEventId(eventId)
                    .orElseThrow(() -> new EntityNotFoundException(eventId));
            infos.add(new CertificateInfo(certificate, certificateSettings));
        }
        return infos;
    }

    //удалить все сертификаты по eventId
    @DeleteMapping("/certificate")
    public void delete(@RequestBody CertificateRequest request) {
        Long eventId = eventService.getChildEventId(request.getEventId());
        certificateRepository.deleteByEventId(eventId);
    }
}
