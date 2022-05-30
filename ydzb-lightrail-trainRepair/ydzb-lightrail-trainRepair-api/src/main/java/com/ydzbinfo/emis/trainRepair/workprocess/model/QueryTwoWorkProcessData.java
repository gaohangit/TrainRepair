package com.ydzbinfo.emis.trainRepair.workprocess.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.ydzbinfo.emis.trainRepair.workprocess.model.base.BaseProcessData;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPacket;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author: 冯帅
 * @Date: 2021/5/19 15:30
 * @Description: 二级修查询实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryTwoWorkProcessData extends BaseProcessData implements Serializable, Comparable<QueryTwoWorkProcessData> {

    /**
     * 作业标准主键
     */
    private String critertionId;

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
     * 记录时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date recordTime;

    /**
     * 图片集合
     */
    List<ProcessPic> processPicList;

    /**
     * 作业包集合
     */
    List<ProcessPacket> processPacketList;

    /**
     * 实际图片数量
     */
    private Integer actualPicCount;

    /**
     * 项目数量
     */
    Integer itemCount;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页显示条数
     */
    private Integer pageSize;

    /**
     * 手持机或PC，手持机1，PC2
     */
    private String dataSource;


    @Override
    public int compareTo(QueryTwoWorkProcessData o) {
        int result = 0;
        if(!ObjectUtils.isEmpty(this.recordTime)&&!ObjectUtils.isEmpty(o.getRecordTime())){
            result = -this.recordTime.compareTo(o.getRecordTime());
            if (0 == result) {
                if(!ObjectUtils.isEmpty(this.trainsetName)&&!ObjectUtils.isEmpty(o.getTrainsetName())){
                    result = this.trainsetName.compareTo(o.getTrainsetName());
                    if (0 == result) {
                        List<ProcessPacket> processPacketList = o.processPacketList;
                        ProcessPacket processPacket = Optional.ofNullable(processPacketList).orElseGet(ArrayList::new).stream().findFirst().orElse(null);
                        if(!ObjectUtils.isEmpty(processPacket)&&!ObjectUtils.isEmpty(processPacket.packetName)){
                            result = processPacket.packetName.compareTo(processPacket.packetName);
                            if (0 == result) {
                                if(!ObjectUtils.isEmpty(this.itemCount)&&!ObjectUtils.isEmpty(o.itemCount)){
                                    result = this.itemCount.compareTo(o.itemCount);
                                    if (0 == result) {
                                        if(!ObjectUtils.isEmpty(this.workerName)&&!ObjectUtils.isEmpty(o.getWorkerName())){
                                            result = this.workerName.compareTo(o.getWorkerName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}
