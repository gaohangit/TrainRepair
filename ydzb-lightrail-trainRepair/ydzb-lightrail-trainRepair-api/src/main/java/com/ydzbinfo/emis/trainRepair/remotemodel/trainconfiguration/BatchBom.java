package com.ydzbinfo.emis.trainRepair.remotemodel.trainconfiguration;

/**
 * 批次级构型
 */
public class BatchBom {
    /**
     * 节点ID
     */
    private String SNodeId;
    /**
     * 节点编码
     */
    private String SNodeCode;
    /**
     * 节点名称
     */
    private String SNodeName;
    /**
     * 相对父件位置
     */
    private String SPartsPosition;
    /**
     * 相对车厢位置
     */
    private String SCarPosition;
    /**
     * 父节点ID
     */
    private String SFatherNodeId;
    /**
     * 父节点编码
     */
    private String SFatherNodeCode;
    /**
     * 层级
     */
    private Long ILevel;
    /**
     * 区域编码
     */
    private String SRegionCode;

    /**
     * 相对位置路径
     */
    private String SPositionPath;
    /**
     * 平台节点ID
     */
    private String SPlatformNodeId;
    /**
     * 同类标记
     */
    private String SKindredFlag;
    /**
     * 技术平台编号
     */
    private String STechPlatformNo;
    /**
     * 技术平台标注
     */
    private String STechPlatformCallOut;
    /**
     * 功能分类编码
     */
    private String SFunctionClassCode;
    /**
     * 功能分类名称
     */
    private String SFunctionClassName;
    /**
     * 功能分类编码（辅助）
     */
    private String SFunctionClassCode2;
    /**
     * 功能分类名称（辅助）
     */
    private String SFunctionClassName2;
    /**
     * 数量
     */
    private Long INumber;

    /**
     * 备注
     */
    private String SRemark;
    /**
     * 是否可用标识 0：可用，1:删除
     */
    private String  CAbleFlag;

    /**
     * 配件ID
     * @return
     */
    private String SPartsId;

    /**
     * 配件类型ID
     * @return
     */
    private String SPartsTypeId;

    /**
     * 批次构型ID
     * @return
     */
    private String SBatchBomId;

    /**
     * 虚拟标识 0：否，1：是
     * @return
     */
    private String CVirtualFlag;

    /**
     * 是否启用序列号0：否，1：是
     * @return
     */
    private String CSeriesFlag;
    /**
     * 失效标识（0：有效，1：失效）
     * @return
     */
    private String CValidFlag;

    /**
     * 排序
     */
    private  Integer ISort;

    public Integer getISort() {
        return ISort;
    }

    public void setISort(Integer ISort) {
        this.ISort = ISort;
    }

    public String getSNodeId() {
        return SNodeId;
    }

    public void setSNodeId(String SNodeId) {
        this.SNodeId = SNodeId;
    }

    public String getSNodeCode() {
        return SNodeCode;
    }

    public void setSNodeCode(String SNodeCode) {
        this.SNodeCode = SNodeCode;
    }

    public String getSNodeName() {
        return SNodeName;
    }

    public void setSNodeName(String SNodeName) {
        this.SNodeName = SNodeName;
    }

    public String getSPartsPosition() {
        return SPartsPosition;
    }

    public void setSPartsPosition(String SPartsPosition) {
        this.SPartsPosition = SPartsPosition;
    }

    public String getSCarPosition() {
        return SCarPosition;
    }

    public void setSCarPosition(String SCarPosition) {
        this.SCarPosition = SCarPosition;
    }

    public String getSFatherNodeId() {
        return SFatherNodeId;
    }

    public void setSFatherNodeId(String SFatherNodeId) {
        this.SFatherNodeId = SFatherNodeId;
    }

    public String getSFatherNodeCode() {
        return SFatherNodeCode;
    }

    public void setSFatherNodeCode(String SFatherNodeCode) {
        this.SFatherNodeCode = SFatherNodeCode;
    }

    public Long getILevel() {
        return ILevel;
    }

    public void setILevel(Long ILevel) {
        this.ILevel = ILevel;
    }

    public String getSRegionCode() {
        return SRegionCode;
    }

    public void setSRegionCode(String SRegionCode) {
        this.SRegionCode = SRegionCode;
    }

    public String getSPositionPath() {
        return SPositionPath;
    }

    public void setSPositionPath(String SPositionPath) {
        this.SPositionPath = SPositionPath;
    }

    public String getSPlatformNodeId() {
        return SPlatformNodeId;
    }

    public void setSPlatformNodeId(String SPlatformNodeId) {
        this.SPlatformNodeId = SPlatformNodeId;
    }

    public String getSKindredFlag() {
        return SKindredFlag;
    }

    public void setSKindredFlag(String SKindredFlag) {
        this.SKindredFlag = SKindredFlag;
    }

    public String getSTechPlatformNo() {
        return STechPlatformNo;
    }

    public void setSTechPlatformNo(String STechPlatformNo) {
        this.STechPlatformNo = STechPlatformNo;
    }

    public String getSTechPlatformCallOut() {
        return STechPlatformCallOut;
    }

    public void setSTechPlatformCallOut(String STechPlatformCallOut) {
        this.STechPlatformCallOut = STechPlatformCallOut;
    }

    public String getSFunctionClassCode() {
        return SFunctionClassCode;
    }

    public void setSFunctionClassCode(String SFunctionClassCode) {
        this.SFunctionClassCode = SFunctionClassCode;
    }

    public String getSFunctionClassName() {
        return SFunctionClassName;
    }

    public void setSFunctionClassName(String SFunctionClassName) {
        this.SFunctionClassName = SFunctionClassName;
    }

    public String getSFunctionClassCode2() {
        return SFunctionClassCode2;
    }

    public void setSFunctionClassCode2(String SFunctionClassCode2) {
        this.SFunctionClassCode2 = SFunctionClassCode2;
    }

    public String getSFunctionClassName2() {
        return SFunctionClassName2;
    }

    public void setSFunctionClassName2(String SFunctionClassName2) {
        this.SFunctionClassName2 = SFunctionClassName2;
    }

    public Long getINumber() {
        return INumber;
    }

    public void setINumber(Long INumber) {
        this.INumber = INumber;
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

    public String getSPartsId() {
        return SPartsId;
    }

    public void setSPartsId(String SPartsId) {
        this.SPartsId = SPartsId;
    }

    public String getSPartsTypeId() {
        return SPartsTypeId;
    }

    public void setSPartsTypeId(String SPartsTypeId) {
        this.SPartsTypeId = SPartsTypeId;
    }

    public String getSBatchBomId() {
        return SBatchBomId;
    }

    public void setSBatchBomId(String SBatchBomId) {
        this.SBatchBomId = SBatchBomId;
    }

    public String getCVirtualFlag() {
        return CVirtualFlag;
    }

    public void setCVirtualFlag(String CVirtualFlag) {
        this.CVirtualFlag = CVirtualFlag;
    }

    public String getCSeriesFlag() {
        return CSeriesFlag;
    }

    public void setCSeriesFlag(String CSeriesFlag) {
        this.CSeriesFlag = CSeriesFlag;
    }

    public String getCValidFlag() {
        return CValidFlag;
    }

    public void setCValidFlag(String CValidFlag) {
        this.CValidFlag = CValidFlag;
    }

}
