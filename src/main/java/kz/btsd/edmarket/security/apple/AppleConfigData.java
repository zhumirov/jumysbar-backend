package kz.btsd.edmarket.security.apple;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "apple")
public class AppleConfigData {
    private String url;
    private String publicKeyUrl;
}
