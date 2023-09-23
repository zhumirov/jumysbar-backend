package kz.btsd.edmarket.security.apple;

import lombok.Data;

@Data
public class AppleTokenHeader {
    private String kid;
    private String alg;
}
