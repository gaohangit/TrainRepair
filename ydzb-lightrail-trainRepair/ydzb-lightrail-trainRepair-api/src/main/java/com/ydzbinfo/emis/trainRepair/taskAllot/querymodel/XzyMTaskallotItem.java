package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.activerecord.Model;

import java.io.Serializable;
import java.util.List;

public class XzyMTaskallotItem extends Model<XzyMTaskallotItem> {

    /**
     *
     */
    private String id;

    /**
     * 派工项目Code
     */
    private String itemCode;

    /**
     * 派工项目名称
     */
    private String itemName;

    /**
     * 作业任务集合类型
     */
    private List<XzyMTaskcarpart> xzyMTaskcarpartList;

    /**
     * 派工人员集合
     */
    private List<XzyMTaskallotperson> workerList;
    
    
    /**
     * 显示派工项目名称
     */
    private String displayItemName;

    /**
     * 是否新项目 1—是，0—否
     */
    private String newItem = "0";

    /**
     * 排序标识
     */
    private int sort;

    private String fatherId;

    //记录单回填状态 1-工长签过字不可用更改派工  0-工长未签字可用更改派工
    private String fillType="0";

    public String getNewItem() {
        return newItem;
    }

    public void setNewItem(String newItem) {
        this.newItem = newItem;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public List<XzyMTaskcarpart> getXzyMTaskcarpartList() {
        return xzyMTaskcarpartList;
    }

    public void setXzyMTaskcarpartList(List<XzyMTaskcarpart> xzyMTaskcarpartList) {
        this.xzyMTaskcarpartList = xzyMTaskcarpartList;
    }

    public List<XzyMTaskallotperson> getWorkerList() {
        return workerList;
    }

    public void setWorkerList(List<XzyMTaskallotperson> workerList) {
        this.workerList = workerList;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "XzyMTaskallotItem{" +
                "itemName='" + itemName + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", xzyMTaskcarpartList=" + xzyMTaskcarpartList +
                ", workerList=" + workerList +
                '}';
    }

    @Override
    protected Serializable pkVal() {
        return this.itemCode;
    }

	public String getDisplayItemName() {
		return displayItemName;
	}

	public void setDisplayItemName(String displayItemName) {
		this.displayItemName = displayItemName;
	}


    public String getFillType() {
        return fillType;
    }

    public void setFillType(String fillType) {
        this.fillType = fillType;
    }
}
