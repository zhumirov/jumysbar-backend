package kz.btsd.edmarket.user.listener;

import kz.btsd.edmarket.user.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserCreatedEvent extends ApplicationEvent {
    private User user;
    private String password;
    private String bitrixPage;

    public UserCreatedEvent(Object source, User user, String bitrixPage) {
        super(source);
        this.user = user;
        this.bitrixPage = bitrixPage;
    }

    public UserCreatedEvent(Object source, User user, String bitrixPage, String password) {
        super(source);
        this.user = user;
        this.password = password;
        this.bitrixPage = bitrixPage;
    }
}
