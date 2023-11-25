package com.yww.api.modules.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author yww
 * @since 2023/11/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MethodVo {

    private String name;

    private String service;

    private String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MethodVo methodVo = (MethodVo) o;
        return Objects.equals(value, methodVo.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
