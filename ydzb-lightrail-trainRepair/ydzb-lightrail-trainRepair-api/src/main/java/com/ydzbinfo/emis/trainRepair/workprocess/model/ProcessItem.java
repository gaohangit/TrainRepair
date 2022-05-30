package com.ydzbinfo.emis.trainRepair.workprocess.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: 冯帅
 * @Date: 2021/5/12 17:05
 * @Description: 作业项目
 */
@Data
public class ProcessItem implements Serializable {

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;
}
