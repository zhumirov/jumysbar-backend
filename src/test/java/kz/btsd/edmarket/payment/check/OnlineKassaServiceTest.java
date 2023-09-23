package kz.btsd.edmarket.payment.check;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OnlineKassaServiceTest {
    @Autowired
    OnlineKassaService onlineKassaService;

    @org.junit.jupiter.api.Test
    void auth() {
        System.out.println(onlineKassaService.auth());
    }

    @Test
    void kkms() {
        System.out.println(onlineKassaService.kkms());
    }

    @Test
    void shifts() {
        System.out.println(onlineKassaService.shifts());
    }

    @Test
    void uid() {
        System.out.println(onlineKassaService.uid());
    }

    @Test
    void getKkmInfo() {
        System.out.println(onlineKassaService.getKkmInfo());
    }

    @Test
    void shiftsZ() {
        System.out.println(onlineKassaService.shiftsZ(4009136l, "27654dd5-2af7-4555-b75a-5b3e41ba588e"));
    }

    @Test
    void getShift() {
        System.out.println(onlineKassaService.getShift());
    }

    @Test
    void kkmsSales() {
       // System.out.println(onlineKassaService.kkmsSales());
    }
}
