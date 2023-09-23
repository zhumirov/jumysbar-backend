package kz.btsd.edmarket.event.promocode;

import org.springframework.stereotype.Service;

@Service
public class PromocodeService {

    public Long price(Long price, Promocode promocode) {
        Long finalPrice;
        if (promocode.getDiscountType().equals(DiscountType.PERCENT)) {
            finalPrice = price * (100 - promocode.getDiscountAmount()) / 100;
        } else {
            finalPrice = price - promocode.getDiscountAmount();
        }
        return finalPrice;
    }
}
