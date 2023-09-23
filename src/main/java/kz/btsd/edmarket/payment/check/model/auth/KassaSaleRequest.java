package kz.btsd.edmarket.payment.check.model.auth;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class KassaSaleRequest {
    //Идентификатор вида деятельности
    private Integer idDomain;
    //Сумма оплаты наличными
    private int cash;
    //Сумма оплаты безналом
    private int nonCash;
    //Список позиций товаров или услуг
    private List<SalePosition> Positions = new ArrayList<>();
    //Итоговая сумма
    private double total;
    //Генерация чека
    private boolean generateCheck = true;
    //Идентификатор наличия/отсутствия автономного режима работы
    //private Integer AFP = 1;

    //Время пробития чека при автономном режиме работы
    private String receiptDate;
}
