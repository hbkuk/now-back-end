package com.now.common.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enum 값을 매핑하여 관리하는 유틸리티
 */
@Getter
@AllArgsConstructor
public class EnumMapperFactory {

    private Map<String, List<EnumMapperValue>> factory;

    /**
     * Enum 값을 매핑하여 추가
     *
     * @param key 매핑할 키
     * @param e   Enum 클래스
     */
    public void put(String key, Class<? extends EnumMapperType> e) {
        factory.put(key, toEnumValues(e));
    }

    /**
     * 주어진 Enum 클래스로부터 Enum 값을 매핑하여 EnumMapperValue 리스트로 반환
     *
     * @param e 매핑할 Enum 클래스
     * @return Enum 값을 매핑한 EnumMapperValue 리스트
     */
    private List<EnumMapperValue> toEnumValues (Class<? extends EnumMapperType> e) {
        return Arrays.stream(e.getEnumConstants())
                .map(EnumMapperValue::new)
                .collect(Collectors.toList());
    }

    /**
     * 주어진 키에 해당하는 Enum 값을 반환
     *
     * @param key 매핑된 키
     * @return Enum 값 리스트
     */
    public List<EnumMapperValue> get(String key) {
        return factory.get(key);
    }

    /**
     * 주어진 키들에 해당하는 Enum 값을 매핑하여 반환
     *
     * @param keys 매핑된 키들의 리스트
     * @return 매핑된 Enum 값들을 가진 Map
     */
    public Map<String, List<EnumMapperValue>> get(List<String> keys) {
        if(keys == null || keys.size() == 0) {
            return new LinkedHashMap<>();
        }
        return keys.stream()
                .collect(Collectors.toMap(Function.identity(), key -> factory.get(key)));
    }
}
