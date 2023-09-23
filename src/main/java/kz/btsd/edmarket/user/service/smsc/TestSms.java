package kz.btsd.edmarket.user.service.smsc;

public class TestSms {

    public static void main(String[] args) {
        String SMSC_LOGIN = "Edmarket_kz";     // логин клиента
        String SMSC_PASSWORD = "edmarket2019";  // пароль
        Smsc smsc = new Smsc(SMSC_LOGIN, SMSC_PASSWORD);
        String[] ret = smsc.send_sms("+77761023741", "edmarket test 3456", 1, "", "", 0, "", "");
        for (String str : ret) {
            System.out.println(str);
        }
        System.out.println(ret);
    }
}
