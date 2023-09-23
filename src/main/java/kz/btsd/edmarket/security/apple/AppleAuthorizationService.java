package kz.btsd.edmarket.security.apple;

import kz.btsd.edmarket.security.config.JwtTokenProvider;
import kz.btsd.edmarket.user.model.AppleAuthRequest;
import kz.btsd.edmarket.user.model.AuthTokenDto;
import kz.btsd.edmarket.user.model.UserDto;
import kz.btsd.edmarket.user.model.UserTokenDto;
import kz.btsd.edmarket.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.security.PublicKey;

@AllArgsConstructor
@Service
public class AppleAuthorizationService {

    private final AppleTokenParser tokenParser;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthTokenDto getToken(AppleAuthRequest request) {
        AppleTokenHeader tokenHeader = tokenParser.getHeader(request.getIdentityToken());

        ApplePublicKey applePublicKey = tokenParser.getApplePublicKey(tokenHeader.getKid());
        PublicKey publicKey = tokenParser.getPublicKey(applePublicKey);

        tokenParser.validateToken(publicKey, request.getIdentityToken());

        String email = tokenParser.getPayload(request.getIdentityToken()).getEmail();
        request.setEmail(email);
        UserDto userDto;
        if (userService.existsByEmail(email)) {
            userDto = userService.findByEmail(email);
        } else {
            userDto = userService.create(request);
        }

        return AuthTokenDto.builder()
                .accessToken(jwtTokenProvider.createToken(userDto.getId()))
                .user(userDto)
                .build();
    }
}
