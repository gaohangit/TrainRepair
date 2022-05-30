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
public class XzyCOneallotCarConfig extends Model<XzyCOneallotCarConfig> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String sId;
    /**
     * XZY_C_ONEALLOT_TEMPLATE主键
     */
    private String sTemplateid;
    /**
     * 辆序
     */
    private String sCarno;
    /**
     * 排序ID
     */
    private String sSortid;

    /**
     * 是否默认 1-是 0-否
     */
    private String sDefault;

    /**
     * 编组数量
     */
    private String marshalNum;

    /**
     * 用户自定义配置id（可为空）
     */
    private String configId;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getsTemplateid() {
        return sTemplateid;
    }

    public void setsTemplateid(String sTemplateid) {
        this.sTemplateid = sTemplateid;
    }

    public String getsCarno() {
        return sCarno;
    }

    public void setsCarno(String sCarno) {
        this.sCarno = sCarno;
    }

    public String getsSortid() {
        return sSortid;
    }

    public void setsSortid(String sSortid) {
        this.sSortid = sSortid;
    }

    public String getsDefault() {
        return sDefault;
    }

    public void setsDefault(String sDefault) {
        this.sDefault = sDefault;
    }

    public String getMarshalNum() {
        return marshalNum;
    }

    public void setMarshalNum(String marshalNum) {
        this.marshalNum = marshalNum;
    }

    @Override
    protected Serializable pkVal() {
        return this.sId;
    }

    @Override
    public String toString() {
        return "XzyCOneallotCarConfig{" +
        "sId=" + sId +
        ", sTemplateid=" + sTemplateid +
        ", sCarno=" + sCarno +
        ", sSortid=" + sSortid +
        ", sDefault=" + sDefault +
        ", marshalNum=" + marshalNum +
        "}";
    }
}
