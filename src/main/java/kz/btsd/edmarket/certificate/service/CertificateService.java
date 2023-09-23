package kz.btsd.edmarket.certificate.service;

import kz.btsd.edmarket.certificate.model.Certificate;
import kz.btsd.edmarket.certificate.model.CertificateConverter;
import kz.btsd.edmarket.certificate.model.CertificateRequest;
import kz.btsd.edmarket.certificate.model.CertificateSettings;
import kz.btsd.edmarket.certificate.repository.CertificateRepository;
import kz.btsd.edmarket.certificate.repository.CertificateSettingsRepository;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.online.progress.EventProgressService;
import kz.btsd.edmarket.online.progress.testhomework.EventProgressUserRow;
import kz.btsd.edmarket.online.progress.testhomework.EventProgressUsersTableDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class CertificateService {

    @Autowired
    private CertificateConverter certificateConverter;
    @Autowired
    private EventProgressService eventProgressService;
    @Autowired
    private CertificateRepository certificateRepository;

    public List<Certificate> saveCertificate(CertificateRequest request) {
        List<Certificate> certificates = new LinkedList<>();
        EventProgressUsersTableDto eventProgressUsersTableDto = eventProgressService.fillForCertificate(request.getEventId(), request.getUsers());
        for (EventProgressUserRow row : eventProgressUsersTableDto.getEventProgresses()) {
            Optional<Certificate> optional = certificateRepository.findByUserIdAndEventId(row.getUser().getId(), row.getEventId());
            if (!optional.isPresent()) {
                Certificate certificate = certificateConverter.convertToCertificate(row);
                certificate.setPercentageTestSize(certificate.getTestSize() != 0 ? certificate.getResolvedTestSize() / certificate.getTestSize() : 0);
                certificate = certificateRepository.save(certificate);
                certificates.add(certificate);
            } else {
                certificates.add(optional.get());
            }
        }
        return certificates;
    }
}
