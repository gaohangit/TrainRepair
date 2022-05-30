package com.ydzbinfo.emis.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张天可
 * @since 2021/12/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValueWrapper<T> {
    T value;
}
