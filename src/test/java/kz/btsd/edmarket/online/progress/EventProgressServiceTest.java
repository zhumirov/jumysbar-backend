package kz.btsd.edmarket.online.progress;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class EventProgressServiceTest {

    @Test
    void fillEventProgresses() throws ParseException {
        String endDateString = "2021-10-11T23:55:00.000+06:00";
        String createDateString = "2021-10-10T21:14:59.590+00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date endDate = sdf.parse(endDateString);
        Date createDate = sdf.parse(createDateString);
        if (createDate.before(endDate)) {
            System.out.println(true);
        }
    }
}

