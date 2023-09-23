package kz.btsd.edmarket.event.repository;

import kz.btsd.edmarket.event.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findAll();
}
