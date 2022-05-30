package com.ydzbinfo.emis.utils;

import lombok.Getter;

@Getter
public enum CustomServiceNameEnum {
    MidGroundService("trainrepairmidgroundservice", "作业过程中台服务");

    private final String id;

    private final String name;

    CustomServiceNameEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
