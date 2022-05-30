package com.ydzbinfo.emis.trainRepair.remotemodel.trainconfiguration;




/**
 * 批次级构型表实体
 */
public class BatchBomNodeEntity extends BatchBom {
    /**
     * 区域名称
     */
    private String SRegionName;

    /**
     * 配件基本信息实体
     */
    private PartsBaseInfoEntity partsBaseInfoEntity;
    /**
     * 配件类型实体
     */
    private PartsTypeEntity partsTypeEntity;

    public String getSRegionName() {
        return SRegionName;
    }

    public void setSRegionName(String SRegionName) {
        this.SRegionName = SRegionName;
    }



    public PartsBaseInfoEntity getPartsBaseInfoEntity() {
        return partsBaseInfoEntity;
    }

    public void setPartsBaseInfoEntity(PartsBaseInfoEntity partsBaseInfoEntity) {
        this.partsBaseInfoEntity = partsBaseInfoEntity;
    }

    public PartsTypeEntity getPartsTypeEntity() {
        return partsTypeEntity;
    }

    public void setPartsTypeEntity(PartsTypeEntity partsTypeEntity) {
        this.partsTypeEntity = partsTypeEntity;
    }
}
