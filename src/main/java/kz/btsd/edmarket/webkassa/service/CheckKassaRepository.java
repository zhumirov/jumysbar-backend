package kz.btsd.edmarket.webkassa.service;

import kz.btsd.edmarket.webkassa.model.CheckKassa;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CheckKassaRepository extends CrudRepository<CheckKassa, Long> {
    Optional<CheckKassa> findByCheckNumber(String checkNumber);
}
