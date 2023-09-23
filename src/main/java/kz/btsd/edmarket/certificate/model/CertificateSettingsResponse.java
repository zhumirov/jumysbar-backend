package kz.btsd.edmarket.certificate.model;

import lombok.Data;

@Data
public class CertificateSettingsResponse {
    private boolean exist;
    private CertificateSettings certificateSettings;

    public CertificateSettingsResponse(boolean exist) {
        this.exist = exist;
    }

    public CertificateSettingsResponse(boolean exist, CertificateSettings certificateSettings) {
        this.exist = exist;
        this.certificateSettings = certificateSettings;
    }
}
