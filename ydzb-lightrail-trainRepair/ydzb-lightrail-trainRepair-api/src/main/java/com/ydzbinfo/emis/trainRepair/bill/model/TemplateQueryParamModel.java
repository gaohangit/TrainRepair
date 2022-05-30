package com.ydzbinfo.emis.trainRepair.bill.model;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key.ITemplateUniqueKey;
import lombok.Data;

/**
 * 记录单查询model
 *
 * @author 张天可
 * @date 2021/6/18
 */
@Data
public class TemplateQueryParamModel implements ITemplateUniqueKey {

    /**
     * 模板类型CODE
     */
    private String templateTypeCode;

    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 批次
     */
    private String batch;

    /**
     * 编组数量
     */
    private Integer marshalCount;

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 模板使用所
     */
    private String unitCode;

    /**
     * 模板使用段
     */
    private String depotCode;
}
