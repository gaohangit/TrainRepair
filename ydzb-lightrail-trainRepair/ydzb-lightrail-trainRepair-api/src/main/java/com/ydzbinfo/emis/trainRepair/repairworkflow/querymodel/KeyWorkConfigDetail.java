package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 关键作业配置详情表
 * </p>
 *
 * @author 高晗
 * @since 2021-06-19
 */
@TableName("XZY_C_KEYWORKCONFIGDETAIL")
public class KeyWorkConfigDetail implements Serializable {


    /**
     * 关键作业配置主键
     */
    @TableField("S_KEYWORKCONFIGID")
    private String keyWorkConfigId;

    /**
     * 配置值类型
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 配置值
     */
    @TableField("S_VALUE")
    private String value;

    public String getKeyWorkConfigId() {
        return keyWorkConfigId;
    }

    public void setKeyWorkConfigId(String keyWorkConfigId) {
        this.keyWorkConfigId = keyWorkConfigId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "KeyWorkConfigDetail{" +
            "keyWorkConfigId=" + keyWorkConfigId +
            ", type=" + type +
            ", value=" + value +
        "}";
    }
}
