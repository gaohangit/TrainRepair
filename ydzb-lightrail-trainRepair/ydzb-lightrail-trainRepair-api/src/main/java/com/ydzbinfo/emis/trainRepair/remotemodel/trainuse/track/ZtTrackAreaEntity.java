package com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 线区
 * </p>
 *
 * @author Jeff
 * @since 2020-05-29
 */
public class ZtTrackAreaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 线区编码
     */
    private int trackAreaCode;

    /**
     * 线区名称
     */
    private String trackAreaName;

    /**
     * 排序
     */
    private int sort;

    /**
     * 所属单位编码
     */
    private String deptCode;

    /**
     * 所属单位名称
     */
    private String deptName;

    /**
     * 股道
     */
    private List<ZtTrackEntity> lstTrackInfo;

    public int getTrackAreaCode() {
        return trackAreaCode;
    }

    public void setTrackAreaCode(int trackAreaCode) {
        this.trackAreaCode = trackAreaCode;
    }

    public String getTrackAreaName() {
        return trackAreaName;
    }

    public void setTrackAreaName(String trackAreaName) {
        this.trackAreaName = trackAreaName;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
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

    public List<ZtTrackEntity> getLstTrackInfo() {
        return lstTrackInfo;
    }

    public void setLstTrackInfo(List<ZtTrackEntity> lstTrackInfo) {
        this.lstTrackInfo = lstTrackInfo;
    }
}
