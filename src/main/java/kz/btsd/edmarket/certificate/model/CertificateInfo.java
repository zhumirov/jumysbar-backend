package kz.btsd.edmarket.certificate.model;

import lombok.Data;

import java.util.List;

@Data
public class CertificateInfo {
    private Certificate certificate;
    private CertificateSettings certificateSettings;

    public CertificateInfo(Certificate certificate, CertificateSettings certificateSettings) {
        this.certificate = certificate;
        this.certificateSettings = certificateSettings;
    }
}
