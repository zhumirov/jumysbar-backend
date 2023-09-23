package kz.btsd.edmarket.event.repository;

import kz.btsd.edmarket.event.model.Level;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LevelRepository extends CrudRepository<Level, Long> {
    List<Level> findAll();
}
