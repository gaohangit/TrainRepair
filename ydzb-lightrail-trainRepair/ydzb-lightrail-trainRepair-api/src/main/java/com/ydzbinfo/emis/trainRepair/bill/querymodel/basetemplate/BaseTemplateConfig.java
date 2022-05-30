package com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 基础单据模板配置表
 * </p>
 *
 * @author 张天可
 * @since 2021-12-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_C_BASETEMPLATECONFIG")
public class BaseTemplateConfig implements Serializable {

    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 需要配置基础模板的模板类型(名称)
     */
    @TableField("S_TEMPLATETYPECODE")
    private String templateTypeCode;

    /**
     * 此基础模板的查询条件
     */
    @TableField("S_QUERYCONDITION")
    private String queryCondition;

}
