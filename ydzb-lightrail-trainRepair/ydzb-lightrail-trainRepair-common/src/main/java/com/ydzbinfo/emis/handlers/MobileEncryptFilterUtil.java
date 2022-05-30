package com.ydzbinfo.emis.handlers;

import com.ydzbinfo.emis.configs.FilterNullValueFastJsonHttpMessageConverter;
import com.ydzbinfo.emis.utils.Constants;

/**
 * @author 张天可
 * @since 2022/4/19
 */
public class MobileEncryptFilterUtil {
    public static void disableMobileHttpJsonNullValueFilter() {
        FilterNullValueFastJsonHttpMessageConverter.addDisableFilterNullValueMatcher(
            (request) -> request.getRequestURI().contains("/" + Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/")
        );
    }

    public static String getUrlPattern() {
        return "/" + Constants.HTTP_MOBILE_ENCRYPT_PATTERN + "/*";
    }
}
