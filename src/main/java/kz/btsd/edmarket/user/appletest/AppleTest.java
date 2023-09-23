package kz.btsd.edmarket.user.appletest;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class AppleTest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "apple_test_seq")
    @SequenceGenerator(name = "apple_test_seq", sequenceName = "apple_test_seq",
            allocationSize = 1)
    private Long id;

    private String request;
}
