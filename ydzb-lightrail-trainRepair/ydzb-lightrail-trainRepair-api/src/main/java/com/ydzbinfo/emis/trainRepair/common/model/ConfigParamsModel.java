package com.ydzbinfo.emis.trainRepair.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigParamsModel {
    String type;
    String name;
    String value;
    /**
     * 此属性暂时禁用
     */
    @Deprecated
    String unitCode;
}

