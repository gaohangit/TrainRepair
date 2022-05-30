package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Data: 2021/8/5
 * @Author: 韩旭
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MainCycWithTypes extends MainCyc {
    //单据名称集合
    private List<TemplateType> templateTypeList;
}
