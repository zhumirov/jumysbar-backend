package kz.btsd.edmarket.common.exceptions;

public class SmsRateLimitException extends RuntimeException {
    public SmsRateLimitException() {
        super();
    }

    public SmsRateLimitException(String message) {
        super(message);
    }
}
