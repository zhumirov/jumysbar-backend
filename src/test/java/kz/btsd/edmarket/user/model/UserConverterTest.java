package kz.btsd.edmarket.user.model;

import kz.btsd.edmarket.user.model.erg.SignupEmployeeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
class UserConverterTest {
    @Autowired
    private UserConverter userConverter;

    @Test
    void convertToEntity() {
        SignupEmployeeDto signupEmployeeDto = new SignupEmployeeDto();
        signupEmployeeDto.setName("test");
        User user = userConverter.convertToEntity(signupEmployeeDto, "123");
        System.out.println(user);
    }
}
