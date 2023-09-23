package kz.btsd.edmarket.payment.check;

import kz.btsd.edmarket.payment.check.model.KassaCheck;
import kz.btsd.edmarket.payment.check.model.KassaCheckRepository;
import kz.btsd.edmarket.payment.check.model.KassaInfo;
import kz.btsd.edmarket.payment.check.model.KassaInfoRepository;
import kz.btsd.edmarket.payment.check.model.auth.DataSaleResponse;
import kz.btsd.edmarket.payment.check.model.auth.KassaAuthRequest;
import kz.btsd.edmarket.payment.check.model.auth.KassaAuthResponse;
import kz.btsd.edmarket.payment.check.model.auth.KassaCloseRequest;
import kz.btsd.edmarket.payment.check.model.auth.KassaKkmInfoResponse;
import kz.btsd.edmarket.payment.check.model.auth.KassaOpenShiftResponse;
import kz.btsd.edmarket.payment.check.model.auth.KassaSaleRequest;
import kz.btsd.edmarket.payment.check.model.auth.KassaSaleResponse;
import kz.btsd.edmarket.payment.check.model.auth.KassaShiftRequest;
import kz.btsd.edmarket.payment.check.model.auth.KassaShiftResponse;
import kz.btsd.edmarket.payment.check.model.auth.KassaUidResponse;
import kz.btsd.edmarket.payment.check.model.auth.SalePosition;
import kz.btsd.edmarket.subscription.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class OnlineKassaService {
    private RestTemplate restTemplateAuth;
    @Value("${jumysbar.online-kassa.url}")
    private String url;
    @Value("${jumysbar.online-kassa.username}")
    private String username;
    @Value("${jumysbar.online-kassa.password}")
    private String password;
    @Value("${jumysbar.online-kassa.kkm}")
    private Long kkm;
    @Value("${jumysbar.online-kassa.enabled}")
    private boolean demoEnabled;
    @Autowired
    private KassaInfoRepository kassaInfoRepository;
    @Autowired
    private KassaCheckRepository kassaCheckRepository;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");


    @PostConstruct
    public void init() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        restTemplateAuth = builder
                //.defaultHeader("Content-Type", MediaType.APPLICATION_JSON)
                //   .basicAuthentication(username, password)
                .build();
    }

    private KassaInfo createNew(KassaInfo kassaInfo) {
        kassaInfo.clear();
        kassaInfo.setCreatedDate(new Date());
        kassaInfo.setToken(auth().getData().getToken());
        kassaInfo.setIdKkm(kkm);
        kassaInfo.setIdShift(shifts().getData().getShift().getId());
        kassaInfo.setUid(uid());
        return kassaInfoRepository.save(kassaInfo);
    }

    public KassaInfo getOrCreateActual() {
        LocalDateTime yesterdayLDT = LocalDateTime.now().minusDays(1);
        Date yesterdayDate = Date
                .from(yesterdayLDT.atZone(ZoneId.of("Asia/Almaty"))
                        .toInstant());

        KassaInfo kassaInfo;
        List<KassaInfo> list = kassaInfoRepository.findAll();
        if (list.size() == 1) {
            kassaInfo = list.get(0);
            if (kassaInfo.getCreatedDate().after(yesterdayDate)) {
                return kassaInfo;
            } else {
                shiftsZ(kassaInfo.getIdShift(), kassaInfo.getUid()); //закрыли смену
                return createNew(kassaInfo);
            }
        } else {
            //закрытие первый раз, когда пустая таблица
//            try {
//                shiftsZ(getKkmInfo().getData().getKkm().getIdShift(), uid()); //закрыли смену
//            } catch (Exception ex) {
//
//            }
            return createNew(new KassaInfo());
        }
    }



    //Получить токен
    public KassaAuthResponse auth() {
        KassaAuthRequest request = new KassaAuthRequest(username, password);
        KassaAuthResponse response = restTemplateAuth.postForObject(url + "/auth", request, KassaAuthResponse.class);
        return response;
    }

    /**
     * Получить список касс
     * Вернет список касс для всех компаний (юр. лиц), с которыми связан авторизованный пользователь.
     */
    public String kkms() {
        String token = auth().getData().getToken();
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
        String response = restTemplate.getForObject(url + "/kkms", String.class);
        return response;
    }

    /**
     * Получить данные о кассе
     */
    public KassaKkmInfoResponse getKkmInfo() {
        String token = auth().getData().getToken();
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
        KassaKkmInfoResponse response = restTemplate.getForObject(url + "/kkms/" + kkm, KassaKkmInfoResponse.class);
        return response;
    }

    /**
     * Получить Uid
     */
    public String uid() {
        String token = auth().getData().getToken();
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
        KassaUidResponse response = restTemplate.getForObject(url + "/uid/" + kkm, KassaUidResponse.class);
        return response.getData().getUid();
    }

    /**
     * Создать Z-отчет и закрыть смену
     */
    public String shiftsZ(Long idShift, String uid) {
        String token = auth().getData().getToken();
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Uid", uid)
                .build();
        KassaCloseRequest request = new KassaCloseRequest(kkm);

        String response = restTemplate.postForObject(url + "/shifts/"+idShift+"/z", request, String.class);
        return response;
    }

//    /**
//     * Получить рабочую смену
//     */
//    public KassaShiftResponse getActualShift() {
//        KassaShiftResponse lastShift = getShift();
//        if (lastShift.getData().getIdStatusShift().equals(1l)) {
//            return lastShift;
//        } else {
//
//        }
//    }

    /**
     * Получить информацию о смене
     */
    public KassaShiftResponse getShift() {
        String token = auth().getData().getToken();
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
        Long idShift = getKkmInfo().getData().getKkm().getIdShift();
        KassaShiftResponse response = restTemplate.getForObject(url + "/shifts/" + idShift, KassaShiftResponse.class);
        return response;
    }

    /**
     * Открытие смены на кассе
     */
    public KassaOpenShiftResponse shifts() {
        String token = auth().getData().getToken();
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
        KassaShiftRequest request = new KassaShiftRequest(kkm);
        KassaOpenShiftResponse response = null;
        try {
            response = restTemplate.postForObject(url + "/shifts", request, KassaOpenShiftResponse.class);
        } catch (HttpClientErrorException ex) {
            //	Если пользователь пытается открыть смену, когда за ним уже числится открытая смена
            if (ex.getStatusCode().equals(HttpStatus.FAILED_DEPENDENCY)) {

            }
            //   System.out.println(ex.getStatusCode());
        }
        return response;
    }


    private KassaSaleRequest getMockKassaSaleRequest() {
        KassaSaleRequest request = new KassaSaleRequest();
        request.setIdDomain(1);
        request.setNonCash(200);
        request.setPositions(Arrays.asList(new SalePosition("TEST Product Management", 1, 200, 1d, 1)));
        request.setTotal(200d);
        request.setReceiptDate(sdf.format(new Date()));
        return request;
    }

    private KassaSaleRequest getKassaSaleRequest(String eventTitle, Long price, Date creationDate) {
        KassaSaleRequest request = new KassaSaleRequest();
        request.setIdDomain(1);
        request.setNonCash(price.intValue());
        request.setPositions(Arrays.asList(new SalePosition(eventTitle, 1, price.intValue(), 1d, 1)));
        request.setTotal(price);
        request.setReceiptDate(sdf.format(creationDate));
        return request;
    }

    private KassaCheck createCheck(Subscription subscription, DataSaleResponse response) {
        KassaCheck kassaCheck = new KassaCheck();
        kassaCheck.setUserId(subscription.getUserId());
        kassaCheck.setEventId(subscription.getEventId());
        kassaCheck.setUrl(response.getLocation());
        kassaCheck.setIdDocument(response.getIdDocument());
        kassaCheck.setReceipt(response.getReceipt());
        return kassaCheck;
    }

    /**
     * Операция продажи
     */
    public KassaCheck kkmsSales(Subscription subscription) {
        KassaInfo kassaInfo = getOrCreateActual();
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder
                .defaultHeader("Authorization", "Bearer " + kassaInfo.getToken())
                .defaultHeader("Uid", kassaInfo.getUid())
                .build();
        HttpEntity<KassaSaleRequest> request = new HttpEntity<>(getKassaSaleRequest(subscription.getTitle(), subscription.getPrice(), subscription.getCreatedDate()));
        ResponseEntity<KassaSaleResponse> response = restTemplate.exchange(url + "/kkms/" + kassaInfo.getIdKkm() + "/sales", HttpMethod.POST, request, KassaSaleResponse.class);
        kassaInfoRepository.updateUid(response.getHeaders().get("Uid").get(0), kkm);
        KassaCheck kassaCheck = kassaCheckRepository.save(createCheck(subscription, response.getBody().getData()));
        return kassaCheck;
    }
}
