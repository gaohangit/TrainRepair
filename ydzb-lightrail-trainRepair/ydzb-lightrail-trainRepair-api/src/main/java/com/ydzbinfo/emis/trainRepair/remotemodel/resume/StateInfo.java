package com.ydzbinfo.emis.trainRepair.remotemodel.resume;

import lombok.Data;

/**
 * @author 史艳涛
 * @description 状态信息
 * @createDate 2021/10/22 15:54
 **/
@Data
public class StateInfo {

    /**
     *  确认值
     */
    private String configValue;
    /**
     * 确认值来源
     */
    private String valueSource;
}
