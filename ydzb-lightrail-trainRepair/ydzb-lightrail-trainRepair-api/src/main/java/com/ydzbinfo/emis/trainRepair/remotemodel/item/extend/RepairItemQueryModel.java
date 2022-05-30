package com.ydzbinfo.emis.trainRepair.remotemodel.item.extend;

import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemWorkQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 查询项目接口参数实体，封装额外参数
 *
 * @author 张天可
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RepairItemQueryModel extends RepairItemWorkQuery {


    /**
     * 配置状态【全部、未配置、已配置】
     */
    String allocatedTempStatus;

    /**
     * 当前单据类型编码
     */
    String currentTemplateTypeCode;

    /**
     * 需要定位的项目编码
     */
    String positionItemCode;

    /**
     * 需要定位的项目批次
     */
    String positionItemBatch;
}
