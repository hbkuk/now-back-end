package com.now.core.authentication.application.util;

import com.now.common.exception.ErrorType;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CookieUtil {

    /**
     * HttpOnly 설정을 포함한 HttpOnly 쿠키를 생성
     *
     * @param key            쿠키의 이름 (key)
     * @param value          쿠키의 값 (value)
     * @param httpOnlySetting HttpOnly 설정 여부. true인 경우 HttpOnly로 설정됩니다.
     * @return 생성된 HttpOnly 쿠키 객체
     */
    public static Cookie generateHttpOnlyCookie(String key, String value, boolean httpOnlySetting) {

        Cookie cookie = null;
        try {
            cookie = new Cookie(key, URLEncoder.encode( value, "UTF-8" ));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        cookie.setHttpOnly(httpOnlySetting);
        return cookie;
    }
}
