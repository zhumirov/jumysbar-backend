package kz.btsd.edmarket.user.model;

public enum Platform {
    JUMYSBAR, BTSD, ERG, DEMO, ECOMMERCE, CMTIS;

    public static boolean isCorp(Platform platform) {
        return !platform.equals(JUMYSBAR);
    }
}
