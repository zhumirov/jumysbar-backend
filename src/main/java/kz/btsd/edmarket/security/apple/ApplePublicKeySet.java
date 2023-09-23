package kz.btsd.edmarket.security.apple;

import lombok.Data;

import java.util.List;

@Data
public class ApplePublicKeySet {
    private List<ApplePublicKey> keys;
}
