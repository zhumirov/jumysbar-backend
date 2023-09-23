package kz.btsd.edmarket.online.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class ListConverterTest {

    @Test
    void listToString() {
        List<String> list = Arrays.asList("option1", "option2", "option3", "option4");
        String result = ListConverter.listToString(list);
        String expected="option1&#&option2&#&option3&#&option4";
        Assert.assertEquals(expected, result);
    }

    @Test
    void stringToList() {
        List<String> expected = Arrays.asList("option1", "option2", "option3", "option4");
        String string="option1&#&option2&#&option3&#&option4";
        List<String> result = ListConverter.stringToList(string);
        Assert.assertArrayEquals(expected.toArray(), result.toArray());
        String empty="";
        Assert.assertArrayEquals(new String[0], ListConverter.stringToList(empty).toArray());
        String option1="option1";
        List<String> expected1 = Arrays.asList("option1");
        Assert.assertArrayEquals(expected1.toArray(), ListConverter.stringToList(option1).toArray());

    }
}
