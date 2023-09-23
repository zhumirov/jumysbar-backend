package kz.btsd.edmarket.webkassa;

import kz.btsd.edmarket.webkassa.service.WebKassaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebKassaServiceTest {
    @Autowired
    WebKassaService webKassaService;

    @Test
    void authorize() {
        System.out.println(webKassaService.authorize());
    }

    @Test
    void check() {
        System.out.println(webKassaService.check());
    }

    @Test
    void checkString() {
        System.out.println(webKassaService.checkString());
    }

    @Test
    void checkWithRepeat() {
        System.out.println(webKassaService.checkWithRepeat().getData().getTicketUrl());
        System.out.println(webKassaService.checkWithRepeat().getData().getTicketUrl());
        System.out.println(webKassaService.checkWithRepeat().getData().getTicketUrl());
        System.out.println(webKassaService.checkWithRepeat().getData().getTicketUrl());
        System.out.println(webKassaService.checkWithRepeat().getData().getTicketUrl());
        System.out.println(webKassaService.checkWithRepeat().getData().getTicketUrl());
    }

    @Test
    void zReport() {
        System.out.println(webKassaService.zReport());
    }
}
