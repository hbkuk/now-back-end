package com.now.common.exception;

import lombok.Getter;

/**
 * 클라이언트가 주어진 시간 동안 너무 많은 요청을 보냈을 때 던져지는 예외
 */
@Getter
public class TooManyRequestsException extends RuntimeException {
    private final long retryAfterMillis;

    /**
     * 새로운 예외 객체를생성
     *
     * @param nanosToWaitForRefill 대기해야 할 나노초 단위 시간
     */
    public TooManyRequestsException(long nanosToWaitForRefill) {
        super("Rate Limit exceeded. Retry after " + toMilliseconds(nanosToWaitForRefill) + " milliseconds.");
        this.retryAfterMillis = toMilliseconds(nanosToWaitForRefill);
    }

    /**
     * 밀리초 단위로 대기해야 할 시간 반환
     *
     * @return 밀리초 단위 대기 시간
     */
    private static long toMilliseconds(long nanosToWaitForRefill) {
        return nanosToWaitForRefill / 1_000_000;
    }
}

