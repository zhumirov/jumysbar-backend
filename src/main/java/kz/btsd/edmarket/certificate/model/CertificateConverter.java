package kz.btsd.edmarket.certificate.model;

import kz.btsd.edmarket.online.progress.testhomework.EventProgressUserRow;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateConverter {
    @Autowired
    private ModelMapper modelMapper;

    public Certificate convertToCertificate(EventProgressUserRow row) {
        Certificate certificate = modelMapper.map(row, Certificate.class);
        certificate.setUserId(row.getUser().getId());
        return certificate;
    }

}
