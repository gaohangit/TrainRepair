package com.ydzbinfo.emis.trainRepair.common.model;

/**
 * Title:用户信息实体类
 * Description:用户信息实体类
 * Author: zhoul
 * Create Date Time: 2020/9/29 11:37
 * Update Date Time: 2020/9/29 11:37
 *
 * @see
 */
public class UserShiroInfo {
    String deptCode;//登陆人部门编码
    String userName;//登陆人姓名
    String account;//登陆人用户名
    String shortName;//当前所在单位简称

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
