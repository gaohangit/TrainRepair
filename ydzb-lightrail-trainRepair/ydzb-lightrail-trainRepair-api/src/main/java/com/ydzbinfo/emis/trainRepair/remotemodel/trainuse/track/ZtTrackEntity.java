package com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 股道
 * </p>
 *
 * @author Jeff
 * @since 2020-05-29
 */
public class ZtTrackEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 股道编码
     */
    private int trackCode;

    /**
     * 线区编码
     */
    private int trackAreaCode;

    /**
     * 股道名称
     */
    private String trackName;

    /**
     * 股道简称
     */
    private String trackAbbr;

    /**
     * 排序
     */
    private int sort;

    /**
     * 打印标记
     */
    private String isPrintFlag;

    /**
     * 所属单位编码
     */
    private String deptCode;

    /**
     * 所属单位名称
     */
    private String deptName;

    /**
     * 列位
     */
    private List<ZtTrackPositionEntity> lstTrackPositionInfo;

    public int getTrackCode() {
        return trackCode;
    }

    public void setTrackCode(int trackCode) {
        this.trackCode = trackCode;
    }

    public int getTrackAreaCode() {
        return trackAreaCode;
    }

    public void setTrackAreaCode(int trackAreaCode) {
        this.trackAreaCode = trackAreaCode;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackAbbr() {
        return trackAbbr;
    }

    public void setTrackAbbr(String trackAbbr) {
        this.trackAbbr = trackAbbr;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getIsPrintFlag() {
        return isPrintFlag;
    }

    public void setIsPrintFlag(String isPrintFlag) {
        this.isPrintFlag = isPrintFlag;
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

    public List<ZtTrackPositionEntity> getLstTrackPositionInfo() {
        return lstTrackPositionInfo;
    }

    public void setLstTrackPositionInfo(List<ZtTrackPositionEntity> lstTrackPositionInfo) {
        this.lstTrackPositionInfo = lstTrackPositionInfo;
    }
}
