package com.now.core.authentication.constants;

/**
 * 사용자 권한을 나타내는 enm
 */
public enum Authority {

    MEMBER("MEMBER"), // 회원 권한

    MANAGER("MANAGER"); // 매니저 권한

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

    /**
     * 주어진 권한(Authority)이 매니저라면 true 반환, 그렇지 않다면 false 반환
     *
     * @param authority 확인할 권한(Authority)
     * @return 주어진 권한(Authority)이 매니저라면 true 반환, 그렇지 않다면 false 반환
     */
    public static boolean isManager(Authority authority) {
        if (authority == MANAGER) {
            return true;
        }
        return false;
    }
}



