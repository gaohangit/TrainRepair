package com.ydzbinfo.emis.trainRepair.warnmanagent.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.ydzbinfo.emis.utils.entity.Constants;
import lombok.Data;

import java.util.Date;

/**
 * @author 张天可
 * @since 2021/12/8
 */
@Data
public class WorkWarningQueryParam {

    @JSONField(format = Constants.DEFAULT_DATE_FORMAT)
    private Date startTime;

    @JSONField(format = Constants.DEFAULT_DATE_FORMAT)
    private Date endTime;

    private Integer pageNum;

    private Integer pageSize;

    private String trainsetType;

    private String trainsetName;

    private String effectState;
}
