package kz.btsd.edmarket.user.service;

import java.util.Random;

public class SmsCodeGenerator {
    public static long generate() {
        int min = 1000;
        int max = 9999;
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static int mockGenerate() {
        return 1111;
    }
}
