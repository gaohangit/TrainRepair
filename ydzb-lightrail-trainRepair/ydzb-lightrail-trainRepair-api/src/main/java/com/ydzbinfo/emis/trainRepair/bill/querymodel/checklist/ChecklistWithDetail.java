package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
@EqualsAndHashCode(callSuper = false)
@Data
public class ChecklistWithDetail implements Serializable {
    private List<ChecklistContentWithDetail> contents;
    private List<ChecklistArea> areas;
}
