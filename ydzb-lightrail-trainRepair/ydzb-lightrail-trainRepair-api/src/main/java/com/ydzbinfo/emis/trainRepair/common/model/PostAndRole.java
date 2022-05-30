package com.ydzbinfo.emis.trainRepair.common.model;

import com.ydzbinfo.emis.trainRepair.constant.RoleTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: 冯帅
 * @Date: 2021/4/28 09:45
 * @Description: 岗位+角色
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostAndRole extends Role {

    /**
     * 类型  1-岗位  2-角色
     */
    private RoleTypeEnum type;
}
