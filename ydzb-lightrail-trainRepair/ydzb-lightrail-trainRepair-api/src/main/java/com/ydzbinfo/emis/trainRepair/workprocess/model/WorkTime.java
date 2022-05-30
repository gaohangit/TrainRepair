package com.ydzbinfo.emis.trainRepair.workprocess.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.ydzbinfo.emis.utils.entity.Constants;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: 冯帅
 * @Date: 2021/6/28 15:55
 * @Description: 白班/夜班的工作时间
 */
@Data
public class WorkTime implements Serializable {

    /**
     * 开始时间
     */
    @JSONField( format = Constants.DEFAULT_DATE_TIME_FORMAT)
    private Date startTime;

    /**
     * 结束时间
     */
    @JSONField( format = Constants.DEFAULT_DATE_TIME_FORMAT)
    private Date endTime;
}
