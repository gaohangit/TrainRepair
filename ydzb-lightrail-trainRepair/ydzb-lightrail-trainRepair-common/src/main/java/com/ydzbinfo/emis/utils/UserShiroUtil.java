package com.ydzbinfo.emis.utils;

import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.ydzbinfo.emis.trainRepair.common.model.UserShiroInfo;

/**
 * Title:
 * Description:
 * Author: zhoul
 * Create Date Time: 2020/9/29 11:39
 * Update Date Time: 2020/9/29 11:39
 *
 * @see
 */
public class UserShiroUtil {

    public static UserShiroInfo getUserInfo() {
        UserShiroInfo info = new UserShiroInfo();
        if (ShiroKit.getUser().getThirdOrgan() != null) {//所级
            info.setDeptCode(ShiroKit.getUser().getThirdOrgan().getOrganCode());
            info.setShortName(ShiroKit.getUser().getThirdOrgan().getShortName());
        } else {
            //获取默认组织
            info.setDeptCode(ShiroKit.getUser().getDepartMentOrgan().getOrganCode());
            info.setShortName(ShiroKit.getUser().getDepartMentOrgan().getShortName());
        }
        info.setUserName(ShiroKit.getUser().getName());//登陆人名称
        info.setAccount(ShiroKit.getUser().getAccount());//登陆人用户名

        return info;
    }
}
