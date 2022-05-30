package com.ydzbinfo.emis.trainRepair.remotemodel.trainuse;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 列位实体
 */
@Data
public class ZtTaskPositionEntity {

    /**
     * 列位编码
     */
    private String trackPositionCode;

    /**
     * 股道编码
     */
    private String trackCode;

    /**
     * 列位名称
     */
    private String trackPostionName;

    /**
     * 列位简称
     */
    private String trackPostionAbbr;

    /**
     * 是否开口
     */
    private String isOpenFlag;

    /**
     * 方向编码
     */
    private String directionCode;

    /**
     * 排序
     */
    private String sort;

    /**
     * 配件ID
     */
    private String partId;

    /**
     * 生成时间
     */
    private Date createDate;

    /**
     * css显示名称
     */
    private Date cssDisplayName;

    /**
     * 默认调车时长
     */
    private String shuntingMin;


    /**
     * 备注
     */
    private String remark;

    /**
     * 运用所编码
     */
    private String deptCode;

    /**
     * 运用所名称
     */
    private String deptName;
}
