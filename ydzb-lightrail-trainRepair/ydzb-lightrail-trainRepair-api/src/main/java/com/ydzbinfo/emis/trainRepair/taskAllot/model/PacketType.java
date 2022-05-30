package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import lombok.Data;

/**
 * Description: 作业包类型
 * Author: wuyuechang
 * Create Date Time: 2021/4/25 11:27
 * Update Date Time: 2021/4/25 11:27
 *
 * @see
 */
@Data
public class PacketType {

    /**
     * 作业包类型编码
     */
    private String PacketTypeCode;

    /**
     * 作业包类型名称
     */
    private String PacketTypeName;
}
