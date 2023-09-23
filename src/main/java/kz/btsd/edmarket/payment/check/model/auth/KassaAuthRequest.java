package kz.btsd.edmarket.payment.check.model.auth;

import lombok.Data;

@Data
public class KassaAuthRequest {
    private String login;
    private String password;

    public KassaAuthRequest() {
    }

    public KassaAuthRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
