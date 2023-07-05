package com.now.domain.permission;

/**
 * 해당 인터페이스는 특정 리소스에 대한 액세스 권한을 나타냄.
 * 구현 클래스는 특정 사용자가 리소스에 액세스할 수 있는지를 확인하는 방법을 제공해야함.
 */
public interface AccessPermission {

    /**
     * 주어진 문자열이 리소스에 액세스할 수 있다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param value 문자열
     * @return 리소스에 액세스할 수 있다면 true 반환, 그렇지 않다면 false 반환
     */
    boolean hasAccess(String value);
}
