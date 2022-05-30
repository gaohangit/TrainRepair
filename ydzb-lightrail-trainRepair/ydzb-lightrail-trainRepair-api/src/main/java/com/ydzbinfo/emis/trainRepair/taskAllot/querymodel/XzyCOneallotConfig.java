package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
@TableName("XZY_C_ONEALLOT_CONFIG")
public class XzyCOneallotConfig extends Model<XzyCOneallotConfig> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableField("S_CONFIGID")
    private String sConfigid;

    /**
     * 编组数  
     */
    @TableField("S_MARSHALNUM")
    private String sMarshalnum;

    /**
     * 运用所CODE
     */
    @TableField("S_UNITCODE")
    private String sUnitcode;

    /**
     * 部门CODE
     */
    @TableField("S_DEPTCODE")
    private String sDeptcode;

    /**
     * 部门名称
     */
    private String sDeptName;

    /**
     * 唯一分组编码
     */
    @TableField("S_GROUPID")
    private String sGroupid;

    /**
     * 是否启用   1--启用；0--不启用
     */
    @TableField("C_FLAG")
    private String cFlag;

    /**
     * 是否自定义   1--是     0--否
     */
    @TableField("C_CUSTOMFLAGA")
    private String cCustomflaga;

    /**
     * 排序标识
     */
    private Integer sort;

    private List<XzyCOneallotTemplate> oneallotTemplateList;


    public String getsConfigid() {
        return sConfigid;
    }

    public void setsConfigid(String sConfigid) {
        this.sConfigid = sConfigid;
    }

    public String getsMarshalnum() {
        return sMarshalnum;
    }

    public void setsMarshalnum(String sMarshalnum) {
        this.sMarshalnum = sMarshalnum;
    }

    public String getsUnitcode() {
        return sUnitcode;
    }

    public void setsUnitcode(String sUnitcode) {
        this.sUnitcode = sUnitcode;
    }

    public String getsDeptcode() {
        return sDeptcode;
    }

    public void setsDeptcode(String sDeptcode) {
        this.sDeptcode = sDeptcode;
    }

    public String getsGroupid() {
        return sGroupid;
    }

    public void setsGroupid(String sGroupid) {
        this.sGroupid = sGroupid;
    }

    public String getcFlag() {
        return cFlag;
    }

    public void setcFlag(String cFlag) {
        this.cFlag = cFlag;
    }

    public String getcCustomflaga() {
        return cCustomflaga;
    }

    public void setcCustomflaga(String cCustomflaga) {
        this.cCustomflaga = cCustomflaga;
    }

    public List<XzyCOneallotTemplate> getOneallotTemplateList() {
        return oneallotTemplateList;
    }

    public void setOneallotTemplateList(List<XzyCOneallotTemplate> oneallotTemplateList) {
        this.oneallotTemplateList = oneallotTemplateList;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getsDeptName() {
        return sDeptName;
    }

    public void setsDeptName(String sDeptName) {
        this.sDeptName = sDeptName;
    }

    @Override
    protected Serializable pkVal() {
        return this.sConfigid;
    }

    @Override
    public String toString() {
        return "XzyCOneallotConfig{" +
        "sConfigid=" + sConfigid +
        ", sMarshalnum=" + sMarshalnum +
        ", sUnitcode=" + sUnitcode +
        ", sDeptcode=" + sDeptcode +
        ", sGroupid=" + sGroupid +
        ", cFlag=" + cFlag +
        ", cCustomflaga=" + cCustomflaga +
        ", oneallotTemplateList=" + oneallotTemplateList +
        "}";
    }
}
