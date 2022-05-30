package com.ydzbinfo.emis.trainRepair.bill.model;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ConfigTemplateBase;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ITemplateForShow;
import com.ydzbinfo.emis.trainRepair.constant.BillTemplateStateEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询模板配置列表使用，部分属性会进行混合
 *
 * @author 张天可
 * @since 2022/2/16
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateGroupForShow extends ConfigTemplateBase implements ITemplateForShow {

    /**
     * 模板类型名称
     */
    private String templateTypeName;

    /**
     * 编组形式名称 1--前 2--后 3--全部
     */
    private String marshallingTypeName;

    /**
     * 状态 0—编辑，1—已发布
     */
    private BillTemplateStateEnum state;

    /**
     * 是否可以修改 1—可以，0—不可以，已发布状态的不可以，编辑状态的可以
     */
    private String isUpdate;

    /**
     * 版本
     */
    private String version;
}
