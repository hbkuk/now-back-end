package com.now.config.utilities;

import java.time.LocalDateTime;
import java.util.List;

public class SortUtils {

    public static boolean isChronologicalOrder(List<LocalDateTime> localDateTimes) {
        for (int i = 0; i < localDateTimes.size() - 1; i++) {
            if (localDateTimes.get(i).isAfter(localDateTimes.get(i + 1))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFirstElementMaxAndDescending(List<Integer> numbers) {
        if (numbers.isEmpty()) {
            return true;  // 비어있는 리스트도 정렬된 것으로 간주
        }

        int maxNumber = numbers.get(0);

        for (int i = 1; i < numbers.size(); i++) {
            if (numbers.get(i) > maxNumber) {
                return false;
            }
            maxNumber = numbers.get(i);
        }

        return true;
    }

}
