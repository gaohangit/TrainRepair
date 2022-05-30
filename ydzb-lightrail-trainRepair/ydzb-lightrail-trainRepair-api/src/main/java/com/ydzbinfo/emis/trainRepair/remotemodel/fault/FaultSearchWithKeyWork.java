package com.ydzbinfo.emis.trainRepair.remotemodel.fault;

import com.ydzbinfo.emis.trainRepair.mobile.model.FaultSearch;
import lombok.Data;

/**
 * @author 高晗
 * @description
 * @createDate 2022/4/27 15:07
 **/
@Data
public class FaultSearchWithKeyWork extends FaultSearch {
    private Boolean convertKeyWork;
}
