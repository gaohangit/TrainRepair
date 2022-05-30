package com.ydzbinfo.emis.trainRepair.bill.general.base;

/**
 * @author 张天可
 * @since 2021/6/25
 */
public interface IBillAreaGeneral {
    String getLeftUp();

    String getLeftDown();

    String getRightUp();

    String getRightDown();

    String getType();

    Integer getNumber();

    void setLeftUp(String leftUp);

    void setLeftDown(String leftDown);

    void setRightUp(String rightUp);

    void setRightDown(String rightDown);

    void setType(String type);

    void setNumber(Integer number);
}
