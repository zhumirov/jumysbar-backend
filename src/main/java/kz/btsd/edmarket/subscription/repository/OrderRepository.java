package kz.btsd.edmarket.subscription.repository;

import kz.btsd.edmarket.payment.model.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    //общая сумма продажи курса
    @Query("SELECT sum(o.price) FROM order_ed o join payment_result ps on o.id = ps.orderId where o.eventId = ?1 and ps.status=1")
    long sumByEventId(Long eventId);

    //общая сумма продажи для пользователя
    @Query("SELECT sum(o.price) FROM order_ed o join payment_result ps on o.id = ps.orderId join Event e on o.eventId = e.id where e.userId = ?1 and ps.status=1")
    long sumByUserId(Long userId);

    List<Order> findAll();

    long countByPlanId(Long planId);
    long countByPromocodeId(Long promocodeId);
}
