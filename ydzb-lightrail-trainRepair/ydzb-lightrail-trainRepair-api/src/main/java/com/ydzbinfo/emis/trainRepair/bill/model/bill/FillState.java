package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FillState {
    //状态CODE
    private  String stateCode;
    //状态名称
    private  String stateName;
}
