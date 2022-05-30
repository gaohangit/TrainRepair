package com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("XZY_B_PRWER_DICT")
public class XzyBPowerDict {

    /**
     * ID 1--供电   2 -- 断电   3--无要求
     */
    @TableId("S_ID")
    private String id;

    /**
     * 字典名称
     */
    @TableField("S_NAME")
    private String name;

    /**
     * 是否可用 0 -- 不可用  1--可用
     */
    @TableField("C_FLAG")
    private String flag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
