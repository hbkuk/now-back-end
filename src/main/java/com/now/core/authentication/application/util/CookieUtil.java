package com.now.core.authentication.application.util;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

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

    /**
     * 쿠키를 삭제
     *
     * @param key 쿠키의 이름 (key)
     * @return 삭제되도록 만료 시간이 설정된 쿠키 객체
     */
    public static Cookie deleteCookie(String key) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0); // 쿠키의 만료 시간을 0으로 설정
        return cookie;
    }

    /**
     * 쿠키 배열에서 주어진 키(key)에 해당하는 쿠키의 값을 반환, 없으면 null 반환
     *
     * @param cookies 쿠키 배열
     * @param key     찾고자 하는 쿠키의 키(key)
     * @return 주어진 키(key)에 해당하는 쿠키의 값, 해당하는 쿠키가 없는 경우에는 null 반환
     */
    public static String getValue(Cookie[] cookies, String key) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(key))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
