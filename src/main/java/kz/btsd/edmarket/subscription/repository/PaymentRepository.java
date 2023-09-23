package kz.btsd.edmarket.subscription.repository;

import kz.btsd.edmarket.payment.model.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, String> {
}
