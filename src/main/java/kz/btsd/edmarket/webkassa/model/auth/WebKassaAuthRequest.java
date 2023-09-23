package kz.btsd.edmarket.webkassa.model.auth;

import lombok.Data;

@Data
public class WebKassaAuthRequest {
    private String login;
    private String password;

    public WebKassaAuthRequest() {
    }

    public WebKassaAuthRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
