package kz.btsd.edmarket.online.model;

public enum UnitType {
    TEXT, URL, VIDEO, IMAGE, FILE, TEST, HOMEWORK, WEBINAR;

    public static boolean isFile(UnitType type) {
        return type.equals(FILE) || type.equals(IMAGE);
    }
}
