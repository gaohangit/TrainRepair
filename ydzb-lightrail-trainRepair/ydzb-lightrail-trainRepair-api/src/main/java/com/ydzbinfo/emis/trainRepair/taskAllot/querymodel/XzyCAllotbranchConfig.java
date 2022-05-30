package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.activerecord.Model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
public class XzyCAllotbranchConfig extends Model<XzyCAllotbranchConfig> {

    private static final long serialVersionUID = 1L;

    /**
     * 班组CODE
     */
    private String id;
    /**
     * 班组名称
     */
    private String label;
    /**
     * 部门CODE
     */
    private String sDeptcode;
    /**
     * 部门名称
     */
    private String sDeptname;
    /**
     * 运用所编码
     */
    private String sUnitcode;
    /**
     * 排序ID
     */
    private String sSort;
    /**
     * 是否可用  1--可用 0--不可用
     */
    private String cFlag;
    /**
     * 创建时间
     */
    private Date dCreatetime;
    /**
     * 创建人
     */
    private String sCreateusercode;
    /**
     * 创建人名称
     */
    private String sCreateusername;
    /**
     * 删除时间
     */
    private Date dDeltime;
    /**
     * 删除人
     */
    private String sDelusercode;
    /**
     * 删除人名称
     */
    private String sDelusername;

    private boolean isGroup;

    /**
     * 人员集合
     */
    private List<XzyCAllotpersonConfig> children;

    public void setChildren(List<XzyCAllotpersonConfig> children) {
        this.children = children;
    }

    public boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean group) {
        isGroup = group;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public List<XzyCAllotpersonConfig> getChildren() {
        return children;
    }

    public String getsDeptcode() {
        return sDeptcode;
    }

    public void setsDeptcode(String sDeptcode) {
        this.sDeptcode = sDeptcode;
    }

    public String getsDeptname() {
        return sDeptname;
    }

    public void setsDeptname(String sDeptname) {
        this.sDeptname = sDeptname;
    }

    public String getsUnitcode() {
        return sUnitcode;
    }

    public void setsUnitcode(String sUnitcode) {
        this.sUnitcode = sUnitcode;
    }

    public String getsSort() {
        return sSort;
    }

    public void setsSort(String sSort) {
        this.sSort = sSort;
    }

    public String getcFlag() {
        return cFlag;
    }

    public void setcFlag(String cFlag) {
        this.cFlag = cFlag;
    }

    public Date getdCreatetime() {
        return dCreatetime;
    }

    public void setdCreatetime(Date dCreatetime) {
        this.dCreatetime = dCreatetime;
    }

    public String getsCreateusercode() {
        return sCreateusercode;
    }

    public void setsCreateusercode(String sCreateusercode) {
        this.sCreateusercode = sCreateusercode;
    }

    public String getsCreateusername() {
        return sCreateusername;
    }

    public void setsCreateusername(String sCreateusername) {
        this.sCreateusername = sCreateusername;
    }

    public Date getdDeltime() {
        return dDeltime;
    }

    public void setdDeltime(Date dDeltime) {
        this.dDeltime = dDeltime;
    }

    public String getsDelusercode() {
        return sDelusercode;
    }

    public void setsDelusercode(String sDelusercode) {
        this.sDelusercode = sDelusercode;
    }

    public String getsDelusername() {
        return sDelusername;
    }

    public void setsDelusername(String sDelusername) {
        this.sDelusername = sDelusername;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "XzyCAllotbranchConfig{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", sDeptcode='" + sDeptcode + '\'' +
                ", sDeptname='" + sDeptname + '\'' +
                ", sUnitcode='" + sUnitcode + '\'' +
                ", sSort='" + sSort + '\'' +
                ", cFlag='" + cFlag + '\'' +
                ", dCreatetime=" + dCreatetime +
                ", sCreateusercode='" + sCreateusercode + '\'' +
                ", sCreateusername='" + sCreateusername + '\'' +
                ", dDeltime=" + dDeltime +
                ", sDelusercode='" + sDelusercode + '\'' +
                ", sDelusername='" + sDelusername + '\'' +
                ", isGroup=" + isGroup +
                ", children=" + children +
                '}';
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
