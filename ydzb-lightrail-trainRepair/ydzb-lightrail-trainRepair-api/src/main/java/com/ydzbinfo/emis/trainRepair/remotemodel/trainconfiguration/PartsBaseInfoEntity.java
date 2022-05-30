package com.ydzbinfo.emis.trainRepair.remotemodel.trainconfiguration;


import java.util.List;

/**
 * 配件基本信息实体
 */
public class PartsBaseInfoEntity extends PartsBaseInfo {

    /**
     * 配件类型名称
     */
    private String SPartsTypeName;

    /**
     * 供应商名称
     */
    private String SSupplierName;
    /**
     * 适用平台
     */
    private List<String> PlatformList;

    public String getSPartsTypeName() {
        return SPartsTypeName;
    }

    public void setSPartsTypeName(String SPartsTypeName) {
        this.SPartsTypeName = SPartsTypeName;
    }

    public String getSSupplierName() {
        return SSupplierName;
    }

    public void setSSupplierName(String SSupplierName) {
        this.SSupplierName = SSupplierName;
    }

    public List<String> getPlatformList() {
        return PlatformList;
    }

    public void setPlatformList(List<String> platformList) {
        PlatformList = platformList;
    }
}
