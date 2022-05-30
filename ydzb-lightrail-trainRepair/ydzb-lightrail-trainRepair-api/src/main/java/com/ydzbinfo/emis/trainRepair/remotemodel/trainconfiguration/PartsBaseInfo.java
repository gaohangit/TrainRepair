package com.ydzbinfo.emis.trainRepair.remotemodel.trainconfiguration;

import java.io.Serializable;
import java.util.Date;

/**
 * 配件基本信息
 */
public class PartsBaseInfo implements Serializable {
    /**
     * 配件ID
     */
    private String SPartsId;
    /**
     * 配件编码
     */
    private String SPartsCode;
    /**
     * 配件名称
     */
    private String SPartsName;
    /**
     * 型号
     */
    private String SModel;
    /**
     * 图号
     */
    private String SDrawingNo;
    /**
     * 配件类型ID
     */
    private String SPartsTypeId;

    /**
     * 供应商ID
     */
    private String SSupplierId;
    /**
     * 备注
     */
    private String SRemark;
    /**
     * 是否可用标识 0：可用，1:删除
     */
    private String  CAbleFlag;
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

    public String getSPartsId() {
        return SPartsId;
    }

    public void setSPartsId(String SPartsId) {
        this.SPartsId = SPartsId;
    }

    public String getSPartsCode() {
        return SPartsCode;
    }

    public void setSPartsCode(String SPartsCode) {
        this.SPartsCode = SPartsCode;
    }

    public String getSPartsName() {
        return SPartsName;
    }

    public void setSPartsName(String SPartsName) {
        this.SPartsName = SPartsName;
    }

    public String getSModel() {
        return SModel;
    }

    public void setSModel(String SModel) {
        this.SModel = SModel;
    }

    public String getSDrawingNo() {
        return SDrawingNo;
    }

    public void setSDrawingNo(String SDrawingNo) {
        this.SDrawingNo = SDrawingNo;
    }

    public String getSPartsTypeId() {
        return SPartsTypeId;
    }

    public void setSPartsTypeId(String SPartsTypeId) {
        this.SPartsTypeId = SPartsTypeId;
    }

    public String getSSupplierId() {
        return SSupplierId;
    }

    public void setSSupplierId(String SSupplierId) {
        this.SSupplierId = SSupplierId;
    }

    public String getSRemark() {
        return SRemark;
    }

    public void setSRemark(String SRemark) {
        this.SRemark = SRemark;
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
        return "PartsBaseInfo{" +
                "SPartsId='" + SPartsId + '\'' +
                ", SPartsCode='" + SPartsCode + '\'' +
                ", SPartsName='" + SPartsName + '\'' +
                ", SModel='" + SModel + '\'' +
                ", SDrawingNo='" + SDrawingNo + '\'' +
                ", SPartsTypeId='" + SPartsTypeId + '\'' +
                ", SSupplierId='" + SSupplierId + '\'' +
                ", SRemark='" + SRemark + '\'' +
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
