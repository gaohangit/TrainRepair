package com.ydzbinfo.emis.trainRepair.bill.general.constant;

import com.ydzbinfo.emis.utils.entity.IJsonEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

/**
 * 记录单回填单元格变更类型
 *
 * @author 张天可
 * @since 2021/8/19
 */
@Getter
@AllArgsConstructor
public enum BillCellTriggerTimingEnum implements IJsonEnum<Integer> {
    BEFORE_CHANGE(1, "设置界面数据前触发"),
    AFTER_CHANGE(2, "设置界面数据后触发"),
    ;
    private final Integer value;
    private final String description;

    @Configuration
    public static class Config {
        static {
            IJsonEnum.register(BillCellTriggerTimingEnum.class);
        }
    }

}
