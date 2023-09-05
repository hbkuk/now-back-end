package com.now.core.authentication.application.util;

import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.ResponseCookie;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CookieUtil {

    public static final String REQUEST_COOKIE_NAME_IN_HEADER = "Cookie";
    public static final String RESPONSE_COOKIE_NAME_IN_HEADERS = "Set-Cookie";

    /**
     * HttpOnly 설정을 포함한 ResponseCookie 생성
     *
     * @param key             쿠키의 이름 (key)
     * @param value           쿠키의 값 (value)
     * @param httpOnlySetting HttpOnly 설정 여부. true인 경우 HttpOnly로 설정
     * @return 생성된 ResponseCookie 쿠키 객체
     */
    public static ResponseCookie createResponseCookieWithHttpOnly(String key, String value, boolean httpOnlySetting) {
        String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
        return ResponseCookie.from(key, encodedValue)
                .httpOnly(httpOnlySetting)
                .secure(true)
                .sameSite(SameSite.NONE.attributeValue()).build();
    }

    /**
     * HttpOnly 설정과 경로 설정을 포함한 쿠키 생성
     *
     * @param key             쿠키의 이름 (key)
     * @param value           쿠키의 값 (value)
     * @param path            쿠키의 경로 (path)
     * @param httpOnlySetting HttpOnly 설정 여부. true인 경우 HttpOnly로 설정
     * @return 생성된 ResponseCookie 쿠키 객체
     */
    public static ResponseCookie createResponseCookieWithPathAndHttpOnly(String key, String value, String path, boolean httpOnlySetting) {
        String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
        return ResponseCookie.from(key, encodedValue)
                .httpOnly(httpOnlySetting)
                .path(path)
                .secure(true)
                .sameSite(SameSite.NONE.attributeValue()).build();
    }

    /**
     * 쿠키 삭제
     *
     * @param key 쿠키의 이름 (key)
     * @return 삭제되도록 만료 시간이 설정된 쿠키 객체
     */
    public static ResponseCookie deleteResponseCookie(String key) {
        return ResponseCookie.from(key, "")
                .maxAge(0)
                .secure(true)
                .sameSite(SameSite.NONE.attributeValue())
                .build();
    }

    /**
     * HttpOnly 설정을 포함한 쿠키 생성
     *
     * @param key             쿠키의 이름 (key)
     * @param value           쿠키의 값 (value)
     * @param httpOnlySetting HttpOnly 설정 여부. true인 경우 HttpOnly로 설정
     * @return 생성된 HttpOnly 쿠키 객체
     */
    public static Cookie createCookieWithHttpOnly(String key, String value, boolean httpOnlySetting) {
        Cookie cookie = new Cookie(key, URLEncoder.encode(value, StandardCharsets.UTF_8));

        cookie.setHttpOnly(httpOnlySetting);
        return cookie;
    }

    /**
     * HttpOnly 설정과 경로 설정을 포함한 쿠키 생성
     *
     * @param key             쿠키의 이름 (key)
     * @param value           쿠키의 값 (value)
     * @param path            쿠키의 경로 (path)
     * @param httpOnlySetting HttpOnly 설정 여부. true인 경우 HttpOnly로 설정
     * @return 생성된 쿠키 객체
     */
    public static Cookie createCookieWithPathAndHttpOnly(String key, String value, String path, boolean httpOnlySetting) {
        Cookie cookie = new Cookie(key, URLEncoder.encode(value, StandardCharsets.UTF_8));

        cookie.setPath(path);
        cookie.setHttpOnly(httpOnlySetting);
        return cookie;
    }

    /**
     * 쿠키 삭제
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
