package com.ydzbinfo.emis.trainRepair.bill.model.attribute;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TemplateLinkAttrModel implements Serializable {
    /**
     * 主键
     */
    private String id;

    /**
     * 单据类型编码
     */
    private String templateTypeCode;

    /**
     * 单据类型编码QueryModel
     */
    private String attributeCode;

    /**
     * 属性对应的单元格是否允许修改（1-允许 0-不允许）
     */
    private String isChange;

    /**
     * 创建人编码
     */
    private String createUserCode;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除人编码
     */
    private String delUserCode;

    /**
     * 删除人名称
     */
    private String delUserName;

    /**
     * 删除时间
     */
    private Date delTime;

    /**
     * 是否可用（1-是 0-否）
     */
    private String flag;
}
