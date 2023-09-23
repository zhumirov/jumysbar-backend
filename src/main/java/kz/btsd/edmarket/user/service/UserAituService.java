package kz.btsd.edmarket.user.service;

import kz.btsd.edmarket.security.config.JwtTokenProvider;
import kz.btsd.edmarket.user.listener.UserCreatedEvent;
import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserAituInfo;
import kz.btsd.edmarket.user.model.UserConverter;
import kz.btsd.edmarket.user.model.UserTokenDto;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class UserAituService {
    @Autowired
    private ApplicationEventPublisher publisher;
    @Value("${jumysbar.aitu-passport.url}")
    private String url;
    @Value("${jumysbar.aitu-passport.logout-url}")
    private String logoutUrl;
    @Value("${jumysbar.aitu-passport.state}")
    private String state;
    @Value("${jumysbar.aitu-passport.authorization}")
    private String authorization;
    @Value("${jumysbar.aitu-passport.redirect_uri}")
    private String redirectUri;
    @Value("${jumysbar.aitu-passport.redirect_uri_ergdu}")
    private String redirectUriErg;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private UserRepository userRepository;

    public UserTokenDto loginAituByCode(String code, Platform platform) {
        String aituRedirect=redirectUri;
        if (platform.equals(Platform.ERG)) {
            aituRedirect = redirectUriErg;;
        }
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", aituRedirect);
        map.add("code", code);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<UserAituInfo> response =
                restTemplate.exchange(url,
                        HttpMethod.POST,
                        entity,
                        UserAituInfo.class);
        JwtDecoder jwtDecoder1 = NimbusJwtDecoder.withJwkSetUri("https://passport.aitu.io/.well-known/jwks.json").jwsAlgorithm(SignatureAlgorithm.RS256).build();

        Jwt jwt = jwtDecoder1.decode(response.getBody().getId_token());
        String phone = jwt.getClaimAsString("phone");
        Optional<User> optionalUser = userRepository.findByPhoneAndPlatformAndDeletedFalse(phone, platform);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            String name = jwt.getClaimAsString("last_name") + " " + jwt.getClaimAsString("first_name");
            user = createByAitu(phone, name, platform);
        }
        String aituUserId = jwt.getClaimAsString("sub");
        user.setAituUserId(aituUserId);
        user.setAituTokenId(response.getBody().getId_token());
        user = userRepository.save(user);

        String token = jwtTokenProvider.createToken(user.getId());
        return new UserTokenDto(token, userConverter.convertToDto(user), null);

    }

    private User createByAitu(String phone, String name, Platform platform) {
        User user = new User();
        user.setPhone(phone);
        user.setName(name);
        user.setPlatform(platform);
        user = userRepository.save(user);
        publisher.publishEvent(new UserCreatedEvent(this, user, "createByAitu"));
        return user;
    }
}
