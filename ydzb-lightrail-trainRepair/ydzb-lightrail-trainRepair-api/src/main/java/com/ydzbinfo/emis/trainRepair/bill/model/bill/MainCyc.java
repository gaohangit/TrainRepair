package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Data: 2021/8/5
 * @Author: 韩旭
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MainCyc {
    //修程编码
    private String taskRepairCode;
    //修程名称
    private String taskRepairName;
}
