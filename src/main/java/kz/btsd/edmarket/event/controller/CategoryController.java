package kz.btsd.edmarket.event.controller;

import kz.btsd.edmarket.event.model.Category;
import kz.btsd.edmarket.event.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class CategoryController {
    @Autowired
    private CategoryRepository repository;

    @GetMapping("/categories/all")
    public List<Category> findAll() {
        return repository.findAll();
    }
}
