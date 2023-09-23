package kz.btsd.edmarket.payment.check.model.auth;

import lombok.Data;

@Data
public class SalePosition {
    //Наименование позиции
    private String name;
    //Идентификатор отдела (секции)
    private Integer idSection;
    //Цена позиции
    private Integer price;
    //Количество товара
    private Double qty;
    //Единица измерения товара
    private Integer idUnit;
    //позиция была сторнирована (отменена)
    private boolean storno=false;

    public SalePosition(String name, Integer idSection, Integer price, Double qty, Integer idUnit) {
        this.name = name;
        this.idSection = idSection;
        this.price = price;
        this.qty = qty;
        this.idUnit = idUnit;
    }
}
