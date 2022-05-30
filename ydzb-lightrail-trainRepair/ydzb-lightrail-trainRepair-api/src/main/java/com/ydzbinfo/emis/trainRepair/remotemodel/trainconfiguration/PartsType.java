package com.ydzbinfo.emis.trainRepair.remotemodel.trainconfiguration;

import java.io.Serializable;
import java.util.Date;

/**
 * 配件类型表（统一清单）
 */
public class PartsType implements Serializable {
    /**
     * 配件类型ID
     */
    private String SPartsTypeId;
    /**
     * 配件类型编码
     */
    private String SPartsTypeCode;
    /**
     * 配件类型名称
     */
    private String SPartsTypeName;
    /**
     * 是否关键配件 0：否，1:是
     */
    private String CKeyFlag;
    /**
     * 是否磨耗件 0：否，1:是
     */
    private String CAbratiomFlag;

    /**
     * 是否委外修件 0：否，1:是
     */
    private String COutRepairFlag;

    /**
     * 是否可用标识 0：可用，1:删除
     */
    private String CAbleFlag;

    /**
     * 创建人ID
     */
    private String SCreatePersonId;
    /**
     * 创建人名称
     */
    private String SCreatePersonName;
    /**
     * 创建时间
     */
    private Date DCreateDate;
    /**
     * 最后更新人ID
     */
    private String SLastUpdatePersonId;
    /**
     * 最后更新人名称
     */
    private String SLastUpdatePersonName;
    /**
     * 最后更新时间
     */
    private Date DLastUpdateDate;
    /**
     * 单位编码
     */
    private String SUnitCode;
    /**
     * 单位名称
     */
    private String SUnitName;

    public String getSPartsTypeId() {
        return SPartsTypeId;
    }

    public void setSPartsTypeId(String SPartsTypeId) {
        this.SPartsTypeId = SPartsTypeId;
    }

    public String getSPartsTypeCode() {
        return SPartsTypeCode;
    }

    public void setSPartsTypeCode(String SPartsTypeCode) {
        this.SPartsTypeCode = SPartsTypeCode;
    }

    public String getSPartsTypeName() {
        return SPartsTypeName;
    }

    public void setSPartsTypeName(String SPartsTypeName) {
        this.SPartsTypeName = SPartsTypeName;
    }

    public String getCKeyFlag() {
        return CKeyFlag;
    }

    public void setCKeyFlag(String CKeyFlag) {
        this.CKeyFlag = CKeyFlag;
    }

    public String getCAbratiomFlag() {
        return CAbratiomFlag;
    }

    public void setCAbratiomFlag(String CAbratiomFlag) {
        this.CAbratiomFlag = CAbratiomFlag;
    }

    public String getCOutRepairFlag() {
        return COutRepairFlag;
    }

    public void setCOutRepairFlag(String COutRepairFlag) {
        this.COutRepairFlag = COutRepairFlag;
    }

    public String getCAbleFlag() {
        return CAbleFlag;
    }

    public void setCAbleFlag(String CAbleFlag) {
        this.CAbleFlag = CAbleFlag;
    }

    public String getSCreatePersonId() {
        return SCreatePersonId;
    }

    public void setSCreatePersonId(String SCreatePersonId) {
        this.SCreatePersonId = SCreatePersonId;
    }

    public String getSCreatePersonName() {
        return SCreatePersonName;
    }

    public void setSCreatePersonName(String SCreatePersonName) {
        this.SCreatePersonName = SCreatePersonName;
    }

    public Date getDCreateDate() {
        return DCreateDate;
    }

    public void setDCreateDate(Date DCreateDate) {
        this.DCreateDate = DCreateDate;
    }

    public String getSLastUpdatePersonId() {
        return SLastUpdatePersonId;
    }

    public void setSLastUpdatePersonId(String SLastUpdatePersonId) {
        this.SLastUpdatePersonId = SLastUpdatePersonId;
    }

    public String getSLastUpdatePersonName() {
        return SLastUpdatePersonName;
    }

    public void setSLastUpdatePersonName(String SLastUpdatePersonName) {
        this.SLastUpdatePersonName = SLastUpdatePersonName;
    }

    public Date getDLastUpdateDate() {
        return DLastUpdateDate;
    }

    public void setDLastUpdateDate(Date DLastUpdateDate) {
        this.DLastUpdateDate = DLastUpdateDate;
    }

    public String getSUnitCode() {
        return SUnitCode;
    }

    public void setSUnitCode(String SUnitCode) {
        this.SUnitCode = SUnitCode;
    }

    public String getSUnitName() {
        return SUnitName;
    }

    public void setSUnitName(String SUnitName) {
        this.SUnitName = SUnitName;
    }

    @Override
    public String toString() {
        return "PartsType{" +
                "SPartsTypeId='" + SPartsTypeId + '\'' +
                ", SPartsTypeCode='" + SPartsTypeCode + '\'' +
                ", SPartsTypeName='" + SPartsTypeName + '\'' +
                ", CKeyFlag='" + CKeyFlag + '\'' +
                ", CAbratiomFlag='" + CAbratiomFlag + '\'' +
                ", COutRepairFlag='" + COutRepairFlag + '\'' +
                ", CAbleFlag='" + CAbleFlag + '\'' +
                ", SCreatePersonId='" + SCreatePersonId + '\'' +
                ", SCreatePersonName='" + SCreatePersonName + '\'' +
                ", DCreateDate=" + DCreateDate +
                ", SLastUpdatePersonId='" + SLastUpdatePersonId + '\'' +
                ", SLastUpdatePersonName='" + SLastUpdatePersonName + '\'' +
                ", DLastUpdateDate=" + DLastUpdateDate +
                ", SUnitCode='" + SUnitCode + '\'' +
                ", SUnitName='" + SUnitName + '\'' +
                '}';
    }
}
