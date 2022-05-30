package com.ydzbinfo.emis.trainRepair.bill.querymodel.base;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 张天可
 * @since 2021-06-17
 */
@Data
public class TemplateContentBase implements ITemplateContentBase {
/**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 模板ID
     */
    @TableField("S_TEMPLATEID")
    private String templateId;

    /**
     * 横坐标
     */
    @TableField("S_ROWID")
    private String rowId;

    /**
     * 纵坐标
     */
    @TableField("S_COLID")
    private String colId;

    /**
     * 默认内容
     */
    @TableField("S_CONTENT")
    private String content;

    /**
     * 属性编码
     */
    @TableField("S_ATTRIBUTECODE")
    private String attributeCode;

}
