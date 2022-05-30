package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhangtk
 * @since 2021-03-17
 */
@TableName("XZY_C_AGGITEMCONFIG_ITEM")
public class AggItemConfigItem implements Serializable {


    /**
     * 主键
     */
    @TableId("S_PITEMID")
    private String pitemId;

    /**
     * 分包分组表ID
     */
    @TableField("S_PACKETID")
    private String packetId;

    /**
     * 项目CODE
     */
    @TableField("S_ITEMCODE")
    private String itemCode;

    /**
     * 项目NAME
     */
    @TableField("S_ITEMNAME")
    private String itemName;

    /**
     * 是否删除(1:是,0:否)
     */
    @TableField("C_FLAG")
    private String flag;

    public String getPitemId() {
        return pitemId;
    }

    public void setPitemId(String pitemId) {
        this.pitemId = pitemId;
    }
    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }
    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "AggItemConfigItem{" +
            "pitemId=" + pitemId +
            ", packetId=" + packetId +
            ", itemCode=" + itemCode +
            ", itemName=" + itemName +
            ", flag=" + flag +
        "}";
    }
}
