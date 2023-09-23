package kz.btsd.edmarket.subscription.model;

import lombok.Data;

@Data
public class EmailMessageResponse {
    private boolean accepted;
    private String text;

    public EmailMessageResponse(boolean accepted) {
        this.accepted = accepted;
    }

    public EmailMessageResponse(boolean accepted, String text) {
        this.accepted = accepted;
        this.text = text;
    }
}
