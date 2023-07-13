package com.now.core.authentication.constants;

/**
 * 사용자 권한을 나타내는 enm
 */
public enum Authority {

    /**
     * 회원 권한
     */
    MEMBER("MEMBER"),

    /**
     * 매니저 권한
     */
    MANAGER("MANAGER");

    private final String value;

    Authority(String value) {
        this.value = value;
    }

    /**
     * 권한을 나타내는 문자열 값을 반환
     *
     * @return 권한을 나타내는 문자열 값
     */
    public String getValue() {
        return value;
    }
}



