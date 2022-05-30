package com.ydzbinfo.emis.trainRepair.bill.querymodel.base;


import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key.ITemplateUniqueKey;

import java.util.Date;

/**
 * @author 张天可
 * @date 2021/6/18
 */
public interface IConfigTemplateBase extends ITemplateUniqueKey, ITemplateBase {

    String getCreateUser();

    void setCreateUser(String createUser);

    Date getCreateTime();

    void setCreateTime(Date createTime);

    String getBureauCode();

    void setBureauCode(String bureauCode);

    String getUnitName();

    void setUnitName(String unitName);

    String getDepotName();

    void setDepotName(String depotName);

    String getBureauName();

    void setBureauName(String bureauName);

    String getDeptType();

    void setDeptType(String deptType);
}
