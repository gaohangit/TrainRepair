package com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track;

import java.io.Serializable;

/**
 * <p>
 * 列位
 * </p>
 *
 * @author Jeff
 * @since 2020-05-29
 */
public class ZtTrackPositionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 列位编码
     */
    private int trackPositionCode;

    /**
     * 股道编码
     */
    private int trackCode;

    /**
     * 列位名称
     */
    private String trackPostionName;

    /**
     * 列位简称
     */
    private String trackPostionAbbr;

    /**
     * 列位是否开口
     */
    private int isOpenFlag;

    /**
     * 方向编码
     */
    private String directionCode;

    /**
     * 排序
     */
    private int sort;

    /**
     * CCS显示名称
     */
    private String ccsDisplayName;

    /**
     * 默认调车时长
     */
    private String shuntingMin;

    /**
     * 注释
     */
    private String remark;

    /**
     * 所属单位编码
     */
    private String deptCode;

    /**
     * 所属单位名称
     */
    private String deptName;

    public int getTrackPositionCode() {
        return trackPositionCode;
    }

    public void setTrackPositionCode(int trackPositionCode) {
        this.trackPositionCode = trackPositionCode;
    }

    public int getTrackCode() {
        return trackCode;
    }

    public void setTrackCode(int trackCode) {
        this.trackCode = trackCode;
    }

    public String getTrackPostionName() {
        return trackPostionName;
    }

    public void setTrackPostionName(String trackPostionName) {
        this.trackPostionName = trackPostionName;
    }

    public String getTrackPostionAbbr() {
        return trackPostionAbbr;
    }

    public void setTrackPostionAbbr(String trackPostionAbbr) {
        this.trackPostionAbbr = trackPostionAbbr;
    }

    public int getIsOpenFlag() {
        return isOpenFlag;
    }

    public void setIsOpenFlag(int isOpenFlag) {
        this.isOpenFlag = isOpenFlag;
    }

    public String getDirectionCode() {
        return directionCode;
    }

    public void setDirectionCode(String directionCode) {
        this.directionCode = directionCode;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getCcsDisplayName() {
        return ccsDisplayName;
    }

    public void setCcsDisplayName(String ccsDisplayName) {
        this.ccsDisplayName = ccsDisplayName;
    }

    public String getShuntingMin() {
        return shuntingMin;
    }

    public void setShuntingMin(String shuntingMin) {
        this.shuntingMin = shuntingMin;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
