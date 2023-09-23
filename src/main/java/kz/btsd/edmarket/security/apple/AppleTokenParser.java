package kz.btsd.edmarket.security.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Component
public class AppleTokenParser {

    private final AppleConfigData appleConfigData;
    private final RestTemplate restTemplate;


    public ApplePublicKey getApplePublicKey(String kid) {

        if (StringUtils.isEmpty(kid)) {
            throw new RuntimeException("Apple auth. Kid can't be empty.");
        }

        ResponseEntity<ApplePublicKeySet> response = restTemplate.getForEntity(appleConfigData.getPublicKeyUrl(),
                ApplePublicKeySet.class);
        if (Objects.isNull(response.getBody())) {
            throw new RuntimeException("Apple auth. Can't get public keys.");
        }
        Optional<ApplePublicKey> applePublicKey = response.getBody()
                .getKeys()
                .stream()
                .filter(key -> StringUtils.equals(kid, key.getKid()))
                .findFirst();

        return applePublicKey.orElseThrow(() -> new RuntimeException("Apple auth. Can't find public key by kid."));
    }

    public PublicKey getPublicKey(ApplePublicKey applePublicKey) {
        if (!"RSA".equals(applePublicKey.getKty())) {
            throw new RuntimeException("Apple auth. Unexpected key kty.");
        }
        byte[] publicKeyString = Base64.decodeBase64(applePublicKey.getN());
        byte[] publicKeyExponent = Base64.decodeBase64(applePublicKey.getE());

        BigInteger n = new BigInteger(1, publicKeyString);
        BigInteger e = new BigInteger(1, publicKeyExponent);

        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(applePublicKey.getKty());
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException("Apple auth. " + exception.getMessage());
        }

        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);

        try {
            return keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (InvalidKeySpecException exception) {
            throw new RuntimeException("Apple auth. " + exception.getMessage());
        }
    }

    public AppleTokenHeader getHeader(String token) {
        String header = token.split("\\.")[0];
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(
                    new String(Base64.decodeBase64(header), StandardCharsets.UTF_8),
                    AppleTokenHeader.class
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Apple auth. " + e.getMessage());
        }
    }

    public AppleTokenPayload getPayload(String token) {
        String header = token.split("\\.")[1];
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(
                    new String(Base64.decodeBase64(header), StandardCharsets.UTF_8),
                    AppleTokenPayload.class
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Apple auth. " + e.getMessage());
        }
    }

    public void validateToken(PublicKey publicKey, String jwtToken) {
        Jwts.parser().setSigningKey(publicKey).parseClaimsJws(jwtToken).getBody();
    }
}
