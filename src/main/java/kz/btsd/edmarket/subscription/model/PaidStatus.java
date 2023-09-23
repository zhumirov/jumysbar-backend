package kz.btsd.edmarket.subscription.model;

/**
 * https://wiki.kassa24.kz/index.php?title=%D0%A2%D0%B5%D1%85%D0%BD%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B0%D1%8F_%D0%B4%D0%BE%D0%BA%D1%83%D0%BC%D0%B5%D0%BD%D1%82%D0%B0%D1%86%D0%B8%D1%8F_%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D1%8B_Ecommerce24
 * 0	Неуспешная транзакция
 * 1	Успешная транзакция
 * 2	Сумма успешно заблокирована (для двухэтапных транзакций)
 * 3	Транзакция отменена или был совершен возврат
 */
public enum PaidStatus {
    FAILURE(0), SUCCESS(1), BLOCKED(2), CANCELED(3);

    private int numVal;

    PaidStatus(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
