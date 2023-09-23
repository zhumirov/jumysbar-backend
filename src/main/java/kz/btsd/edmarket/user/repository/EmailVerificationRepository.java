package kz.btsd.edmarket.user.repository;

import kz.btsd.edmarket.user.model.EmailVerification;
import kz.btsd.edmarket.user.model.SmsCodeVerification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface EmailVerificationRepository extends CrudRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByUserIdAndUuid(Long userid, String uuid);

    @Query("select count(ev) from email_verification ev where ev.userId=:userId and ev.createdDate >= :lastDay")
    Integer countForOneDay(@Param("userId") Long userId, @Param("lastDay") Date lastDay);
}
