package com.ydzbinfo.emis.trainRepair.bill.general.constant;

import com.ydzbinfo.emis.utils.entity.IJsonEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

/**
 * 记录单回填实体(单元格、区域)变更类型
 *
 * @author 张天可
 * @since 2021/8/19
 */
@Getter
@AllArgsConstructor
public enum BillEntityChangeTypeEnum implements IJsonEnum<Integer> {
    INSERT(1, "增加单元格或区域"),
    UPDATE(2, "更新单元格或区域"),
    DELETE(3, "删除单元格或区域"),
    NO_CHANGE(0, "未变更"),
    ;

    private final Integer value;
    private final String description;

    @Configuration
    public static class Config {
        static {
            IJsonEnum.register(BillEntityChangeTypeEnum.class);
        }
    }

}
