package com.ydzbinfo.emis.utils.user;

import com.ydzbinfo.emis.trainRepair.common.model.*;
import com.ydzbinfo.hussar.system.bsp.organ.SysOrgan;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户信息获取
 *
 * @author 张天可
 **/
public class UserUtilGeneral {

    /**
     * 段级单位类型
     */
    private static final String[] depotTypeCodes = new String[]{
        "04",// 车辆段
        "05",// 动车段
        "06",// 机务段
        "07",// 客运段
        "14",// 客车段
        "15",// 货车段
        "16"// 机车检修段
    };

    /**
     * 所类型
     */
    private static final String[] unitTypeCodes = new String[]{
        "08", // 动车运用所
        "10"// 客车整备所
    };

    private static SysOrgan getOrganFromMap(Map<String, SysOrgan> mapTree, String[] targetTypeCodes) {
        for (String depotTypeCode : targetTypeCodes) {
            if (depotTypeCode.startsWith("0")) {
                depotTypeCode = depotTypeCode.substring(1);
            }
            if (mapTree.get(depotTypeCode) != null) {
                return mapTree.get(depotTypeCode);
            }
        }
        return null;
    }

    private static SysOrgan getDepotFromMap(Map<String, SysOrgan> mapTree) {
        return getOrganFromMap(mapTree, depotTypeCodes);
    }

    private static SysOrgan getUnitFromMap(Map<String, SysOrgan> mapTree) {
        return getOrganFromMap(mapTree, unitTypeCodes);
    }

    public static User transformToUser(IShiroUser shiroUser, String workTeamTypeCode) {
        String accountName = shiroUser.getAccount();
        String userDataID = shiroUser.getId();
        String userName = shiroUser.getName();
        String ipAddress = shiroUser.getRemoteAddress();
        String staffId = shiroUser.getStaffId();
        // 获取用户下的组织结构
        Map<String, SysOrgan> mapTree = shiroUser.getOrganMap();
        SysOrgan sysBureau = null;
        SysOrgan sysDepot = null;
        SysOrgan sysUnit = null;
        SysOrgan sysTeam = null;

        if (mapTree != null) {
            sysBureau = mapTree.get("3");// 用户局
            sysDepot = getDepotFromMap(mapTree);// 用户段
            sysUnit = getUnitFromMap(mapTree);// 用户运用所
            sysTeam = mapTree.get(workTeamTypeCode);// 用户班组
        }
        User user = new User();

        // 获取兼职部门
        List<SysOrgan> partTimeOrganList = shiroUser.getLstPartTimeOrgan();
        if (partTimeOrganList != null) {
            user.setPartTimeWorkTeams(partTimeOrganList.stream().filter(v -> workTeamTypeCode.equals(v.getDeptType())).map(v -> {
                WorkTeam workTeam = new WorkTeam();
                workTeam.setTeamCode(v.getOrganCode());
                workTeam.setTeamName(v.getOrganName());
                workTeam.setTeamShortName(v.getShortName());
                workTeam.setParentDeptCode(v.getParentTypeCode());
                return workTeam;
            }).collect(Collectors.toList()));
        } else {
            user.setPartTimeWorkTeams(new ArrayList<>());
        }


        // 用户所属局
        Bureau bureau = new Bureau();
        user.setBureau(bureau);
        if (sysBureau != null) {
            user.setUnitType("3");// 人员类型是局的
            bureau.setCode(sysBureau.getOrganCode());
            bureau.setName(sysBureau.getOrganName());
        }
        Depot depot = new Depot();
        user.setDepot(depot);
        if (sysDepot != null) {
            user.setUnitType("5");// 人员类型是段的
            // 用户段
            depot.setCode(sysDepot.getOrganCode());
            depot.setName(sysDepot.getOrganName());
            depot.setId(sysDepot.getOrganId());
        }
        Unit unit = new Unit();
        user.setUnit(unit);
        if (sysUnit != null) {
            user.setUnitType("8");// 人员类型是所的
            // 运用所s
            unit.setUnitCode(sysUnit.getOrganCode());
            unit.setUnitName(sysUnit.getOrganName());
        }
        WorkTeam workTeam = new WorkTeam();
        user.setWorkTeam(workTeam);
        if (sysTeam != null) {
            // 用户所属班组
            workTeam.setTeamCode(sysTeam.getOrganCode());
            workTeam.setTeamName(sysTeam.getOrganName());
            workTeam.setTeamShortName(sysTeam.getShortName());
            workTeam.setParentDeptCode(sysTeam.getParentTypeCode());
        }
        user.setAccountName(accountName);
        user.setId(userDataID);
        user.setStaffId(staffId);
        user.setName(userName);
        user.setIpAddress(ipAddress);
        user.setRoles(shiroUser.getRolesList());
        user.setRoleInfoList(shiroUser.getSysRoleList().stream().map(v -> {
            Role role = new Role();
            role.setCode(v.getRoleId());
            role.setName(v.getRoleName());
            return role;
        }).collect(Collectors.toList()));
        user.setDepartmentOrgan(shiroUser.getDepartmentOrgan());
        return user;

    }
}
