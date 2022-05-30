package com.ydzbinfo.emis.trainRepair.trainMonitor.model;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

import java.util.List;

/**
 * @author: gaoHan
 * @date: 2021/8/7 15:11
 * @description:
 */
@Data
public class MonitorWithPackets {
    /**
     * 作业包名称
     */
    private String packetName;

    /**
     * 作业包CODE
     */

    private String packetCode;

}