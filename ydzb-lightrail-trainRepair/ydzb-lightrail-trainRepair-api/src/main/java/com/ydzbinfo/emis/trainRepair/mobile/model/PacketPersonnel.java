package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

import java.util.List;

/**
 * Description:  作业包包含人员集合
 * Author: wuyuechang
 * Create Date Time: 2021/4/26 14:41
 * Update Date Time: 2021/4/26 14:41
 *
 * @see
 */
@Data
public class PacketPersonnel {

    /**
     * 派工人员名称集合
     */
    private List<String> workerList;

    /**
     * 是否包含本人 1是 2否
     */
    private String isAuthor = "2";

    /**
     * 包名称
     */
    private String packetName;
}
