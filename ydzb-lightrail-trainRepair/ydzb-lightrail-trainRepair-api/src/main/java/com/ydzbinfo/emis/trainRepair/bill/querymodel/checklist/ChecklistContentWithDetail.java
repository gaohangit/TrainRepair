package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 张天可
 * @date 2021/6/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChecklistContentWithDetail extends ChecklistDetail {

    List<ChkDetailLinkContent> linkContents;

    List<ChecklistLinkControl> controls;

}
