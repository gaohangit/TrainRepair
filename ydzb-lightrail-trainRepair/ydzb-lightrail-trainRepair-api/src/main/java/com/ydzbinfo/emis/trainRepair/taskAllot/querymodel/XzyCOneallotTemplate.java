package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
public class XzyCOneallotTemplate extends Model<XzyCOneallotTemplate> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String sTemplateid;
    /**
     * 编组数  
     */
    private String sMarshalnum;
    /**
     * 辆序集合
     */
    private String sCarnolist;
    /**
     * 排序序号
     */
    private String iSort;
    /**
     * 唯一分组编码
     */
    private String sGroupid;
    /**
     * 分组内数据排序
     */
    private String sGroupsort;

    /**
     * 是否默认
     */
    private String sDefault;


    public String getsTemplateid() {
        return sTemplateid;
    }

    public void setsTemplateid(String sTemplateid) {
        this.sTemplateid = sTemplateid;
    }

    public String getsMarshalnum() {
        return sMarshalnum;
    }

    public void setsMarshalnum(String sMarshalnum) {
        this.sMarshalnum = sMarshalnum;
    }

    public String getsCarnolist() {
        return sCarnolist;
    }

    public void setsCarnolist(String sCarnolist) {
        this.sCarnolist = sCarnolist;
    }

    public String getiSort() {
        return iSort;
    }

    public void setiSort(String iSort) {
        this.iSort = iSort;
    }

    public String getsGroupid() {
        return sGroupid;
    }

    public void setsGroupid(String sGroupid) {
        this.sGroupid = sGroupid;
    }

    public String getsGroupsort() {
        return sGroupsort;
    }

    public void setsGroupsort(String sGroupsort) {
        this.sGroupsort = sGroupsort;
    }

    @Override
    protected Serializable pkVal() {
        return this.sTemplateid;
    }

    public String getsDefault() {
        return sDefault;
    }

    public void setsDefault(String sDefault) {
        this.sDefault = sDefault;
    }

    @Override
    public String toString() {
        return "XzyCOneallotTemplate{" +
                "sTemplateid='" + sTemplateid + '\'' +
                ", sMarshalnum='" + sMarshalnum + '\'' +
                ", sCarnolist='" + sCarnolist + '\'' +
                ", iSort='" + iSort + '\'' +
                ", sGroupid='" + sGroupid + '\'' +
                ", sGroupsort='" + sGroupsort + '\'' +
                ", sDefault='" + sDefault + '\'' +
                '}';
    }
}
