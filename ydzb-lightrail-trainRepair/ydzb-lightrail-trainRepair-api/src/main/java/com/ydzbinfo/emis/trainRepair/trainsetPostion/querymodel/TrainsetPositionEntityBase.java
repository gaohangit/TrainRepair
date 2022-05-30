package com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ydzbinfo.emis.utils.entity.Constants;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 车组位置实体(中台实体类)
 *
 * @author 张天可
 **/
@Data
public class TrainsetPositionEntityBase {

    private String id;

    /**
     * 编组状态
     */
    private String isLong;

    /**
     * 车头方向
     */
    private String headDirection;

    /**
     * 车头所在列位
     */
    private String headDirectionPla;

    /**
     * 车头所在列位CODE
     */
    private String headDirectionPlaCode;

    /**
     * 车尾方向
     */
    private String tailDirection;

    /**
     * 车尾所在列位
     */
    private String tailDirectionPla;

    /**
     * 车尾所在列位CODE
     */
    private String tailDirectionPlaCode;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 车组ID
     */
    private String trainsetId;

    /**
     * 股道编号
     */
    private String trackCode;

    /**
     * 股道名称
     */
    private String trackName;

    /**
     * 进入时间
     */
    @DateTimeFormat(pattern = Constants.DEFAULT_DATE_TIME_FORMAT)
    @JsonFormat(pattern = Constants.DEFAULT_DATE_TIME_FORMAT, timezone = "GMT+8")
    @JSONField(format = Constants.DEFAULT_DATE_TIME_FORMAT)
    private Date inTime;

    /**
     * 离开时间
     */
    @DateTimeFormat(pattern = Constants.DEFAULT_DATE_TIME_FORMAT)
    @JsonFormat(pattern = Constants.DEFAULT_DATE_TIME_FORMAT, timezone = "GMT+8")
    @JSONField(format = Constants.DEFAULT_DATE_TIME_FORMAT)
    private Date outTime;

    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 运用所编号
     */
    private String unitCode;


    /**
     * 运用所名称
     */
    private String unitName;

    /**
     * 重联状态
     */
    private String isConnect;

}
