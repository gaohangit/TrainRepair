package com.ydzbinfo.emis.trainRepair.bill.querymodel.base;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @date: 2021/10/27
 * @author: 冯帅
 */
@Data
public class TemplateTypeBase implements ITemplateTypeBase {

    /**
     * 类型编码
     */
    @TableId("S_TEMPLATETYPECODE")
    private String templateTypeCode;

    /**
     * 类型名称
     */
    @TableField("S_TEMPLATETYPENAME")
    private String templateTypeName;
}
