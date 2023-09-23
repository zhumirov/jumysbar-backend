package kz.btsd.edmarket.mobile.utils;

import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

public class AitubeUtils {

    public static String getAitubeId(String uri) {
        if (StringUtils.isEmpty(uri)) {
            return null;
        }
        MultiValueMap<String, String> parameters =
                UriComponentsBuilder.fromUriString(uri).build().getQueryParams();
        List<String> ids = parameters.get("id");
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        return ids.get(0);
    }
}
