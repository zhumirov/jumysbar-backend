package kz.btsd.edmarket.common;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ExceptionLogRepository extends CrudRepository<ExceptionLog, Long> {
    List<ExceptionLog> findByCreatedDateAfter(Date createdDate, Pageable pageable);

    long countByCreatedDateAfter(Date createdDate);
}
