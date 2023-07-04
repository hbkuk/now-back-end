package com.now.domain.permission;

/**
 * 해당 인터페이스는 특정 리소스에 대한 액세스 권한을 나타냄.
 * 구현 클래스는 특정 사용자가 리소스에 액세스할 수 있는지를 확인하는 방법을 제공해야함.
 */
public interface AccessPermission {

    /**
     * 주어진 사용자 ID가 리소스에 액세스할 수 있는지 여부를 확인합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자가 액세스할 수 있는 경우 true, 그렇지 않은 경우 false
     */
    boolean hasAccess(String userId);
}
