package com.ydzbinfo.emis.trainRepair.bill.querymodel.base;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key.TemplateUniqueKey;
import com.ydzbinfo.emis.utils.entity.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 单据模板配置表共同属性对象
 *
 * @author 张天可
 * @date 2021/6/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ConfigTemplateBase extends TemplateUniqueKey implements IConfigTemplateBase {

    /**
     * 模板ID
     */
    @TableId("S_TEMPLATEID")
    private String templateId;

    /**
     * 模板编号
     */
    @TableField("S_TEMPLATENO")
    private String templateNo;

    /**
     * 模板名称
     */
    @TableField("S_TEMPLATENAME")
    private String templateName;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 项目名称
     */
    @TableField("S_ITEMNAME")
    private String itemName;

    /**
     * 模板路径
     */
    @TableField("S_TEMPLATEPATH")
    private String templatePath;

    /**
     * 编组形式  1--前   2--后   3--全部
     */
    @TableField("I_MARSHALLINGTYPE")
    private Integer marshallingType;

    /**
     * 创建人
     */
    @TableField("S_CREATEUSER")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    @JSONField(format = Constants.DEFAULT_DATE_TIME_FORMAT)
    private Date createTime;

    /**
     * 模板使用路局
     */
    @TableField("S_BUREAUCODE")
    private String bureauCode;

    /**
     * 模板使用所名称
     */
    @TableField("S_UNITNAME")
    private String unitName;

    /**
     * 模板使用段名称
     */
    @TableField("S_DEPOTNAME")
    private String depotName;

    /**
     * 模板使用路局名称
     */
    @TableField("S_BUREAUNAME")
    private String bureauName;

    /**
     * 模板使用单位类型  04-运用所，03-动车段
     */
    @TableField("S_DEPTTYPE")
    private String deptType;

}
