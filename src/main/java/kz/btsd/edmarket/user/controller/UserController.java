package kz.btsd.edmarket.user.controller;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.security.config.JwtTokenProvider;
import kz.btsd.edmarket.user.model.LoginEmailRequest;
import kz.btsd.edmarket.user.model.LoginPhoneRequest;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserAddOrgInfoDto;
import kz.btsd.edmarket.user.model.UserChangePasswordRequest;
import kz.btsd.edmarket.user.model.UserChangePersonalRequest;
import kz.btsd.edmarket.user.model.UserConverter;
import kz.btsd.edmarket.user.model.UserDto;
import kz.btsd.edmarket.user.model.UserPasswordResetRequest;
import kz.btsd.edmarket.user.model.UserPasswordStatusDto;
import kz.btsd.edmarket.user.model.UserRole;
import kz.btsd.edmarket.user.model.UserTokenDto;
import kz.btsd.edmarket.user.model.dto.PhoneEmailRequest;
import kz.btsd.edmarket.user.model.erg.LoginEmployeeRequest;
import kz.btsd.edmarket.user.repository.UserRepository;
import kz.btsd.edmarket.user.service.AuthService;
import kz.btsd.edmarket.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Enumeration;

@CrossOrigin(origins = "*")
@RestController
public class UserController {
    @Autowired
    private UserRepository repository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    private UserTokenDto getToken(User user, String password, String param) {
        if (password.length() < 6) {
            throw new BadCredentialsException("Неверный номер телефона или пароль"); //todo переделать валидацию , обработатываем случай пустого пароля, поменять с 6
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Неверный номер телефона или пароль");
        }
        String token = jwtTokenProvider.createToken(user.getId());

        return new UserTokenDto(token, userConverter.convertToDto(user), param);
    }

    @PostMapping(value = "/users/token/phone")
    public UserTokenDto getToken(HttpServletRequest httpServletRequest, @RequestBody LoginPhoneRequest request) {
        request.setPlatform(userService.getPlatform(httpServletRequest));//todo добавить на фронте, тут удалить
        User user = repository.findByPhoneAndPlatformAndDeletedFalse(request.getPhone(), request.getPlatform())
                .orElseThrow(() -> new BadCredentialsException("Неправильный номер телефона"));
        return getToken(user, request.getPassword(), null);
    }

    public String param(HttpServletRequest httpServletRequest) {
        Enumeration<String> getHeaderNames = httpServletRequest.getHeaderNames();
        String result = getHeaderNames.nextElement();

        while (getHeaderNames.hasMoreElements()) {
            String element = getHeaderNames.nextElement();
            result += element + ":" + httpServletRequest.getHeader(element) + ";\n ";
        }
        result += "1remoteHost: " + httpServletRequest.getRemoteHost();
        result += "1requestUrl: " + httpServletRequest.getRequestURL();
        return result;
    }

    @PostMapping(value = "/users/token/email")
    public UserTokenDto getTokenEmail(HttpServletRequest httpServletRequest, @RequestBody LoginEmailRequest request) {
        request.setPlatform(userService.getPlatform(httpServletRequest));//todo добавить на фронте, тут удалить
        User user = repository.findByEmailAndPlatformAndDeletedFalse(request.getEmail(), request.getPlatform())
                .orElseThrow(() -> new BadCredentialsException("Неправильная почта"));

        //todo for ecommerce переделать для всех
        userService.checkHost(user, httpServletRequest);
//        if (!user.isEmailConfirmed()) {
//            throw new BadCredentialsException("Нельзя войти, подтвердите почту");
//        }
        return getToken(user, request.getPassword(), param(httpServletRequest));
    }

    @PostMapping(value = "/users/token/employee")
    public UserTokenDto getTokenEmployee(@RequestBody LoginEmployeeRequest request) {
        User user = repository.findByEmployeeIdAndDeletedFalse(request.getEmployeeId())
                .orElseThrow(() -> new BadCredentialsException("Неправильный табельный номер"));
        return getToken(user, request.getPassword(), null);
    }

    @GetMapping("/users/{id}")
    public UserDto findById(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        User user = userService.findById(jwt.getSubject());
        if (!user.getId().equals(id)) {
            throw new AccessDeniedException("Access denied");
        }
        return userConverter.convertToDto(user);
    }

    @GetMapping("/users/{id}/password/status")
    public UserPasswordStatusDto checkNullPassword(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        User user = userService.findById(jwt.getSubject());
        if (!user.getId().equals(id)) {
            throw new AccessDeniedException("Access denied");
        }
        return new UserPasswordStatusDto(userService.existsPassword(id));
    }


    // смена пароля
    @PutMapping("/users/password/reset")
    public ResponseEntity<?> resetPassword(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UserPasswordResetRequest request) {
        User admin = userService.findById(jwt.getSubject());
        User user = repository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(request.getUserId()));
        if (!admin.getUserRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("только админ может изменить");
        }
        if (!admin.getPlatform().equals(user.getPlatform())) {
            throw new AccessDeniedException("пользователь из другой системы");
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        repository.save(user);
        return ResponseEntity.ok().build();
    }

    // смена пароля
    @PutMapping("/users/{id}/password")
    public ResponseEntity<?> changePassword(Authentication authentication, @Valid @RequestBody UserChangePasswordRequest changePasswordRequest, @PathVariable Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        authService.checkOwner(authentication.getName(), user.getId());
        if (userService.existsPassword(id)) {
            if (passwordEncoder.matches(changePasswordRequest.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
                user.setSystemPassword(false);
                repository.save(user);
            } else {
                throw new BadCredentialsException("Неверный пароль");
            }
        } else {
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            user.setSystemPassword(false);
            repository.save(user);
        }
        return ResponseEntity.ok().build();
    }

    // смена личных данных
    @PutMapping("/users/{id}/personal")
    public UserDto changePersonalData(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UserChangePersonalRequest request,
            @PathVariable Long id) {
        User admin = userService.findById(jwt.getSubject());
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        if (!admin.getUserRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("только админ может изменить");
        }
        if (!jwt.getSubject().equals(user.getId().toString())) {
            throw new AccessDeniedException("Access denied");
        }
        return userService.update(id, request);
    }


    @PutMapping("/users/{id}/change-deleted-status")
    public void deactivateUser(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam Boolean deletedStatus,
            @PathVariable Long id) {
        User admin = userService.findById(jwt.getSubject());
        User user = repository.getUserBy(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        if (!admin.getUserRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("только админ может изменить");
        }
        userService.changeDeletedStatus(user, deletedStatus);
    }

    @GetMapping("/users/validate-data")
    public void checkUniqueness(
            @RequestBody PhoneEmailRequest request) {
        userService.checkUniqueness(request);
    }

    @PutMapping("/users/personal")
    public ResponseEntity<?> changePersonalData(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UserChangePersonalRequest request) {
        userService.update(userService.findById(jwt.getSubject()).getId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/personal")
    public UserDto getPersonalData(@AuthenticationPrincipal Jwt jwt) {
        return userService.findByIdtoDto(jwt.getSubject());
    }

    @PutMapping("/users/avatar")
    public ResponseEntity<?> changeAvatar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("file") MultipartFile multipartFile) throws IOException {
        // todo добавить поддержку application/octet-stream
//        if (!isSupportedContentType(multipartFile.getContentType())) {
//            throw new UnexpectedTypeException("не поддерживыемый тип файла:" + multipartFile.getContentType());
//        }
        userService.updateAvatar(userService.findById(jwt.getSubject()).getId(), multipartFile);
        return ResponseEntity.ok().build();
    }

    /**
     * первый вход после регистрации организатора.
     *
     * @param userAddOrgInfoDto
     * @return
     */
    @PutMapping("/users/reg/org/{id}")
    public UserDto addOrgInfo(Authentication authentication, @RequestBody UserAddOrgInfoDto userAddOrgInfoDto, @PathVariable Long id) {
        User user = userConverter.convertToSavedEntity(userAddOrgInfoDto);
        authService.checkOwner(authentication.getName(), user.getId());
        return userConverter.convertToDto(repository.save(user));
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }
}
