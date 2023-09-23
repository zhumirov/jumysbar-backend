package kz.btsd.edmarket.certificate.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CertificateRequest {
    private List<Long> users;
    private Long eventId;
}
