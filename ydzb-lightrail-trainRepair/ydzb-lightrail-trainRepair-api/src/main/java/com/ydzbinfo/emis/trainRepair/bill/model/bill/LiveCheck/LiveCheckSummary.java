package com.ydzbinfo.emis.trainRepair.bill.model.bill.LiveCheck;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class LiveCheckSummary {

    //主表id
    private String checklistSummaryId;

    //车组ID
    private String trainsetId;

    //车组名称
    private String trainsetName;

    //交接股道
    private String track;

    //交接时间
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date connectTime;

    //创建人名称
    private String createstuffname;

    //创建时间
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date createTime;

}
