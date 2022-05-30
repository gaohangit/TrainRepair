package com.ydzbinfo.emis.trainRepair.bill.general.constant;

import com.ydzbinfo.emis.utils.entity.IJsonEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

/**
 * 记录单回填单元格触发url类型
 *
 * @author 张天可
 * @since 2021/8/19
 */
@Getter
@AllArgsConstructor
public enum BillCellTriggerUrlTypeEnum implements IJsonEnum<Integer> {
    FUNCTION(1, "接口方法"),
    PAGE(2, "页面"),
    ;
    private final Integer value;
    private final String description;

    @Configuration
    public static class Config {
        static {
            IJsonEnum.register(BillCellTriggerUrlTypeEnum.class);
        }
    }

}
