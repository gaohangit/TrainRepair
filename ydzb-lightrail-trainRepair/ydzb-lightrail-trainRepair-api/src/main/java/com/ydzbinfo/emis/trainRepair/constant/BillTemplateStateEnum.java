package com.ydzbinfo.emis.trainRepair.constant;

import com.ydzbinfo.emis.utils.entity.IJsonEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 单据发布状态
 *
 * @author 张天可
 **/
@Getter
@AllArgsConstructor
public enum BillTemplateStateEnum implements IJsonEnum<String> {
    UNRELEASED("0", "未发布"),
    RELEASED("1", "已发布"),
    ;

    String value;
    String label;

    public static String getTip() {
        return Arrays.stream(BillTemplateStateEnum.values()).map(v -> v.getValue() + ":" + v.getLabel()).collect(Collectors.joining(" "));
    }

    @Configuration
    public static class Config {
        static {
            IJsonEnum.register(BillTemplateStateEnum.class);
        }
    }
}
