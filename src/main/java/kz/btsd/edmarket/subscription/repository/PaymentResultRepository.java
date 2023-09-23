package kz.btsd.edmarket.subscription.repository;

import kz.btsd.edmarket.payment.model.PaymentResult;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentResultRepository extends CrudRepository<PaymentResult, String> {
    List<PaymentResult> findAll();
}
