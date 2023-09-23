package kz.btsd.edmarket.user.repository;

import kz.btsd.edmarket.user.model.SmsCodeVerification;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SmsCodeVerificationRepository extends CrudRepository<SmsCodeVerification, Long> {
    Optional<SmsCodeVerification> findFirstByPhoneOrderByCreatedDateDesc(String phone);
}
