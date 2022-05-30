package com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: 冯帅
 * @Date: 2021/4/23 09:56
 * @Description: 作业标准配置—岗位
 */
@TableName(value = "XZY_C_WORKCRITERTION_POST")
@Data
public class XzyCWorkcritertionPost extends Model<XzyCWorkcritertionPost> {

    private static final long serialVersionUID = 1L;

    @Override
    protected Serializable pkVal() {
        return null;
    }

    /**
     * 主键
     */
    @TableId("S_ID")
    private String Id;

    /**
     * XZY_C_WORKCRITERTION表主键
     */
    @TableField("S_CRITERTIONID")
    private String sCritertionid;

    /**
     * 岗位编码
     */
    @TableField("S_POSTCODE")
    private String postCode;

    /**
     * 岗位名称
     */
    @TableField("S_POSTNAME")
    private String postName;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 是否可用  1-可用  0-不可用
     */
    @TableField("C_FLAG")
    private String flag;

    /**
     * 类型  1-岗位  2-角色
     */
    @TableField("S_TYPE")
    private String type;
}
