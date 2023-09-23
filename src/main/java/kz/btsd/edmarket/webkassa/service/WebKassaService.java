package kz.btsd.edmarket.webkassa.service;

import kz.btsd.edmarket.webkassa.model.CheckInfo;
import kz.btsd.edmarket.webkassa.model.auth.WebKassaAuthRequest;
import kz.btsd.edmarket.webkassa.model.auth.WebKassaAuthResponse;
import kz.btsd.edmarket.webkassa.model.check.WebKassaCheckPayment;
import kz.btsd.edmarket.webkassa.model.check.WebKassaCheckPosition;
import kz.btsd.edmarket.webkassa.model.check.WebKassaCheckRequest;
import kz.btsd.edmarket.webkassa.model.check.WebKassaCheckResponse;
import kz.btsd.edmarket.webkassa.model.zreport.WebKassaZReportRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class WebKassaService {
    private RestTemplate restTemplateAuth;
    @Value("${jumysbar.webkassa.url}")
    private String url;
    @Value("${jumysbar.webkassa.login}")
    private String login;
    @Value("${jumysbar.webkassa.password}")
    private String password;
    @Value("${jumysbar.webkassa.x-api-key}")
    private String xApiKey;
    @Value("${jumysbar.webkassa.cashbox}")
    private String cashboxUniqueNumber;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private static final DecimalFormat df = new DecimalFormat("#.##");
    private static String token;


    @PostConstruct
    public void init() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        restTemplateAuth = builder
                .defaultHeader("x-api-key", xApiKey)
                //   .basicAuthentication(username, password)
                .build();
    }

    //Получить токен
    public WebKassaAuthResponse authorize() {
        WebKassaAuthRequest request = new WebKassaAuthRequest(login, password);
        WebKassaAuthResponse response = restTemplateAuth.postForObject(url + "/authorize", request, WebKassaAuthResponse.class);
        return response;
    }

    private WebKassaCheckPosition createPositon(CheckInfo checkInfo) {
        double price = Double.parseDouble(checkInfo.getPrice());
        int taxPercent = 12;
        double tax = price / (100 + taxPercent) * taxPercent;
        String taxStr = df.format(tax);
        WebKassaCheckPosition position = new WebKassaCheckPosition();
        position.setCount(1);
        position.setPrice(price);
        position.setTaxPercent(taxPercent);
        try {
            position.setTax(df.parse(taxStr).doubleValue());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        position.setPositionName("JumysBar");
        position.setPositionCode("1");
        return position;
    }

    private String getToken() {
        if (token == null) {
            token = authorize().getData().getToken();
        }
        return token;
    }

    private WebKassaCheckPayment createPayment(CheckInfo checkInfo) {
        double sum = Double.parseDouble(checkInfo.getPrice());
        WebKassaCheckPayment payment = new WebKassaCheckPayment();
        payment.setSum(sum);
        return payment;
    }

    private WebKassaCheckRequest createCheckRequest(CheckInfo checkInfo) {
        WebKassaCheckRequest request = new WebKassaCheckRequest();

        request.setExternalCheckNumber(checkInfo.getExternalCheckNumber());
        request.setOperationType(2);
        request.setCashboxUniqueNumber(cashboxUniqueNumber);
        request.setToken(getToken());
        List<WebKassaCheckPosition> positionList = new ArrayList<>();
        positionList.add(createPositon(checkInfo));
        request.setPositions(positionList);
        List<WebKassaCheckPayment> payments = new ArrayList<>();
        payments.add(createPayment(checkInfo));
        request.setPayments(payments);
        return request;
    }

    @Async
    public WebKassaCheckResponse checkWithRepeat(CheckInfo checkInfo) {
        WebKassaCheckResponse response = check(checkInfo);
        if (response.getErrors() != null) {
            //if (response.getErrors().get(0).getCode() == 11) { //todo сейчас приходит code=0 уточнить почему
                zReport();
                return check(checkInfo);
           // }
        }
        return response;
    }

    public WebKassaCheckResponse check(CheckInfo checkInfo) {
        WebKassaCheckRequest request = createCheckRequest(checkInfo);
        WebKassaCheckResponse response = restTemplateAuth.postForObject(url + "/check", request, WebKassaCheckResponse.class);
        if (response.getErrors() != null) {
            System.out.println(response.getErrors());
        }
        return response;
    }

    public String checkString(CheckInfo checkInfo) {
        WebKassaCheckRequest request = createCheckRequest(checkInfo);
        String response = restTemplateAuth.postForObject(url + "/check", request, String.class);
        return response;
    }

    private WebKassaZReportRequest createZReport() {
        return new WebKassaZReportRequest(getToken(), cashboxUniqueNumber);
    }

    public String zReport() {
        WebKassaZReportRequest request = createZReport();
        String response = restTemplateAuth.postForObject(url + "/zreport", request, String.class);
        return response;
    }
}
