package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 额外流程类型作业包配置表
 * </p>
 *
 * @author zhangtk
 * @since 2021-05-08
 */
@TableName("XZY_C_EXTRAFLOWTYPEPACKET")
public class ExtraFlowTypePacket implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 额外流程类型标识
     */
    @TableField("S_EXTRAFLOWTYPECODE")
    private String extraFlowTypeCode;

    /**
     * 作业包编码
     */
    @TableField("S_PACKETCODE")
    private String packetCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExtraFlowTypeCode() {
        return extraFlowTypeCode;
    }

    public void setExtraFlowTypeCode(String extraFlowTypeCode) {
        this.extraFlowTypeCode = extraFlowTypeCode;
    }

    public String getPacketCode() {
        return packetCode;
    }

    public void setPacketCode(String packetCode) {
        this.packetCode = packetCode;
    }


    @Override
    public String toString() {
        return "ExtraFlowTypePacket{" +
            "id=" + id +
            ", extraFlowTypeCode=" + extraFlowTypeCode +
            ", packetCode=" + packetCode +
        "}";
    }
}
