package kz.btsd.edmarket.online.controller;

import kz.btsd.edmarket.online.model.SectionFullDto;
import kz.btsd.edmarket.online.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
public class SectionController {
    @Autowired
    private SectionService sectionService;

    @GetMapping("/sections/{id}")
    public SectionFullDto findById(@PathVariable Long id) {
        return sectionService.findById(id);
    }

    @PostMapping("/sections")
    public SectionFullDto save(Authentication authentication, @Valid @RequestBody SectionFullDto sectionFullDto) {
        sectionService.checkSectionOwner(authentication.getName(), sectionFullDto.getId());
        return sectionService.save(sectionFullDto);
    }

    @PutMapping("/sections/{id}")
    public SectionFullDto changeEvent(Authentication authentication, @RequestBody SectionFullDto sectionFullDto, @PathVariable Long id) {
        sectionService.checkSectionOwner(authentication.getName(), sectionFullDto.getId());
        return sectionService.save(sectionFullDto);
    }

    //todo продумать по каскаду
    @DeleteMapping("/sections/{id}")
    void delete(Authentication authentication, @PathVariable Long id) {
        sectionService.checkSectionOwner(authentication.getName(), id);
        sectionService.delete(id);
    }
}
