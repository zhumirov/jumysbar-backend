package kz.btsd.edmarket.user.controller;

import kz.btsd.edmarket.user.listener.UserEmailListener;
import kz.btsd.edmarket.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;

@SpringBootTest
class UserEmailListenerTest {
    @Autowired
    private UserEmailListener userEmailListener;

    @Test
    public void processUserCreatedEvent() throws MessagingException {
        User user = new User();
        user.setEmail("andrei.tkachev@bts-education.kz");
        userEmailListener.sendEmail(user);
    }
}
