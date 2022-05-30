package com.ydzbinfo.emis.trainRepair.faultconfig.model;

import com.ydzbinfo.emis.trainRepair.faultconfig.querymodel.FaultInputDict;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 张天可
 * @since 2021/12/8
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FaultInputDictQueryParam extends FaultInputDict {

    private Integer pageNum;

    private Integer pageSize;
}
