package kz.btsd.edmarket.event.controller;

import kz.btsd.edmarket.event.model.Level;
import kz.btsd.edmarket.event.repository.LevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class LevelController {
    @Autowired
    private LevelRepository repository;

    @GetMapping("/levels/all")
    public List<Level> findAll() {
        return repository.findAll();
    }
}
