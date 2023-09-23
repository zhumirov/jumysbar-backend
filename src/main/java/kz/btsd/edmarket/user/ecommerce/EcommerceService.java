package kz.btsd.edmarket.user.ecommerce;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Документация https://documenter.getpostman.com/view/11511138/TzzALbPE
 * Пример запроса: https://kompra.kz/api/v2/basic?identifier=980840005389&api-token=test
 */
@Service
public class EcommerceService {
    private RestTemplate restTemplate;
    @Value("${jumysbar.kompra.url}")
    private String kompraUrl;
    @Value("${jumysbar.kompra.token}")
    private String token;

    @PostConstruct
    public void init() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        restTemplate = builder
                .build();
    }

    public UserEcommerceInfo getResult(String identifier) {
        Map<String, String> uriVariables = new HashMap<String, String>();
        uriVariables.put("api-token", token);
        uriVariables.put("identifier", identifier);

        UserEcommerceInfo response = restTemplate.getForObject(kompraUrl+"?api-token="+token+"&identifier="+identifier, UserEcommerceInfo.class);
        return response;
    }
}
