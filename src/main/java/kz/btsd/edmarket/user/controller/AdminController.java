package kz.btsd.edmarket.user.controller;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.user.model.AdminRoleRequest;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserRole;
import kz.btsd.edmarket.user.repository.UserRepository;
import kz.btsd.edmarket.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@CrossOrigin(origins = "*")
@RestController
public class AdminController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    private Set<String> SUPER_ADMIN_EMAILS = new HashSet<>(Arrays.asList("koblandy1301@gmail.com", "newbrisingrc@gmail.com", "jumysbar@gmail.com", "alexander.kim@bts-education.kz"));

    @PutMapping("/admins/org")
    public boolean org(Authentication authentication,
                       @RequestParam(required = false) Long id,
                       @RequestParam(required = false) String phone) {
        User user = null;
        if (id != null) {
            user = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(id));
        } else {
            user = userRepository.findByPhoneAndDeletedFalse(phone) //todo добавить платформу
                    .orElseThrow(() -> new EntityNotFoundException(id));
        }
        checkAdmin(authentication, user);
        user.setUserRole(UserRole.ORG);
        userRepository.save(user);
        return true;
    }

    @GetMapping("/admins/super/check")
    public AdminResponse checkSuperAdmin(Authentication authentication) {
        User admin = userService.findById(authentication.getName());
        return new AdminResponse(SUPER_ADMIN_EMAILS.contains(admin.getEmail()));
    }

    @PutMapping("/admins/{id}/role")
    public ResponseEntity<?> changeStatus(Authentication authentication, @RequestBody AdminRoleRequest request, @PathVariable Long id) {
        if (!(request.getUserRole().equals(UserRole.USER)
                || request.getUserRole().equals(UserRole.ORG))) {
            //todo права для суперадмина
            User admin = userService.findById(authentication.getName());
            if (!SUPER_ADMIN_EMAILS.contains(admin.getEmail())) {
                throw new AuthorizationServiceException("назначить можно только USER или ORG");
            }
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        checkAdmin(authentication, user);
        user.setUserRole(request.getUserRole());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    private void checkAdmin(Authentication authentication, User user) {
        User admin = userService.findById(authentication.getName());
        if (!admin.isAdmin()) {
            throw new AuthorizationServiceException("только ADMIN может изменить");
        }
        if (!user.getPlatform().equals(admin.getPlatform())) {
            throw new AuthorizationServiceException("ADMIN не принадлежит данной организации-platform");
        }
    }
}
