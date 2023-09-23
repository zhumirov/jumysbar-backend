package kz.btsd.edmarket.user.service;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.user.model.*;
import kz.btsd.edmarket.security.config.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private VerificationService verificationService;

    public AuthTokenDto loginByPassword(String phone, String password) {
        UserDto userDto = userService.findByPhoneToDto(phone);
        if (!userService.checkPassword(phone, password)) {
            throw new BadCredentialsException("Неверный номер телефона или пароль");
        }
        String accessToken = jwtTokenProvider.createToken(userDto.getId());
        return AuthTokenDto.builder()
                .accessToken(accessToken)
                .user(userDto)
                .build();
    }

    public AuthTokenDto loginBySmsCode(LoginBySmsCodeRequest request) {
        VerificationType verificationType = userService.existsByPhone(request.getPhone())
                ? VerificationType.AUTH : VerificationType.REGISTRATION;

        if (!verificationService.verify(request.getVerificationId(),
                verificationType, request.getCode())) {
            throw new BadCredentialsException("Неверный номер телефона или смс код");
        }

        UserDto userDto = userService.existsByPhone(request.getPhone())
                ? userService.findByPhoneToDto(request.getPhone())
                : userService.create(request.getPhone());

        String accessToken = jwtTokenProvider.createToken(userDto.getId());
        return AuthTokenDto.builder()
                .accessToken(accessToken)
                .user(userDto)
                .build();
    }

    public void checkOwner(String authUserId, Long userId) {
        if (!authUserId.equals(userId.toString())) {
            throw new AccessDeniedException("Нет доступа: ");
        }
    }

    public void checkAdmin(String userId) {
        User owner = userService.findById(userId);
        if (!owner.getUserRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("Только ADMIN может использовать");
        }
    }
}
