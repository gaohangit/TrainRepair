package com.ydzbinfo.emis.trainRepair.workprocess.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPacket;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author:
 * @Date:
 * @Description: 二级修作业查询实体
 */
@Data
public class TwoWorkProcess  implements Serializable {

    /**
     * 开始时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 车组ID
     */
    private String trainsetId;

    /**
     * 作业班组
     */
    private String deptCode;

    /**
     * 作业包
     */
    private String packetName;

    /**
     * 作业人员
     */
    private String workerId;


}
