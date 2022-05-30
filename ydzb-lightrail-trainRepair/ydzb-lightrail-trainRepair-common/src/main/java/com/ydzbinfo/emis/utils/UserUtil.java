package com.ydzbinfo.emis.utils;

import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.configs.TrainRepairProperties;
import com.ydzbinfo.emis.trainRepair.common.model.User;
import com.ydzbinfo.emis.utils.user.IShiroUser;
import com.ydzbinfo.emis.utils.user.UserUtilGeneral;
import com.ydzbinfo.hussar.system.bsp.organ.SysOrgan;
import com.ydzbinfo.hussar.system.permit.SysRoles;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author gaohan
 * @description
 * @createDate 2021/4/20 8:56
 * @modified 张天可
 **/
@Component("appUserUtil")
public class UserUtil {
    private static TrainRepairProperties trainRepairProperties;

    public UserUtil(TrainRepairProperties trainRepairProperties) {
        UserUtil.trainRepairProperties = trainRepairProperties;
    }

    public static User getUserInfo() {
        return CacheUtil.getDataUseThreadCache("UserUtil.getUserInfo_" + ShiroKit.getUser().getStaffId(), () -> {
            ShiroUser shiroUser = ShiroKit.getUser();
            User user = UserUtilGeneral.transformToUser(new IShiroUser() {
                @Override
                public String getId() {
                    return shiroUser.getId();
                }

                @Override
                public String getStaffId() {
                    return shiroUser.getStaffId();
                }

                @Override
                public String getName() {
                    return shiroUser.getName();
                }

                @Override
                public String getAccount() {
                    return shiroUser.getAccount();
                }

                @Override
                public String getRemoteAddress() {
                    return shiroUser.getRemoteAddress();
                }

                @Override
                public Map<String, SysOrgan> getOrganMap() {
                    return shiroUser.getOrganMap();
                }

                @Override
                public List<String> getRolesList() {
                    return shiroUser.getRolesList();
                }

                @Override
                public List<SysRoles> getSysRoleList() {
                    return shiroUser.getSysRoleList();
                }

                @Override
                public List<SysOrgan> getLstPartTimeOrgan() {
                    return shiroUser.getLstPartTimeOrgan();
                }

                @Override
                public SysOrgan getDepartmentOrgan() {
                    return shiroUser.getDepartMentOrgan();
                }
            }, trainRepairProperties.getWorkTeamTypeCode());
            return user;
        });

    }
}
