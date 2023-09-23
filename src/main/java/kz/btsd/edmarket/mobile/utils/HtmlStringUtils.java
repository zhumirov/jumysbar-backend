package kz.btsd.edmarket.mobile.utils;

public class HtmlStringUtils {

    public static String removeHtmlTags(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("\\<.*?\\>", "");
    }
}
