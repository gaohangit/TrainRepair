package com.ydzbinfo.emis.utils.user;

import com.ydzbinfo.hussar.system.bsp.organ.SysOrgan;
import com.ydzbinfo.hussar.system.permit.SysRoles;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author 张天可
 * @since 2021/7/2
 */
public interface IShiroUser {
    String getId();

    String getStaffId();

    String getName();

    String getAccount();

    String getRemoteAddress();

    Map<String, SysOrgan> getOrganMap();

    List<String> getRolesList();

    List<SysRoles> getSysRoleList();

    List<SysOrgan> getLstPartTimeOrgan();

    SysOrgan getDepartmentOrgan();
}
