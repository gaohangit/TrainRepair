package com.ydzbinfo.emis.trainRepair.trainMonitor.utils;

/**
 * @author 高晗
 * @description
 * @createDate 2021/12/27 15:43
 **/
public class NumberToStringUtils {
    public static String numberToString(int section) {
        if (section >= 10 && section < 20)
            return "十" + numberToString(section % 10);
        String[] chnNumChar = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] chnUnitChar = {"", "十", "百", "千"};
        StringBuilder chnStr = new StringBuilder();
        StringBuilder strIns = new StringBuilder();
        int unitPos = 0;
        boolean zero = true;
        while (section > 0) {
            int v = section % 10;
            if (v == 0) {
                if (!zero) {
                    zero = true;
                    chnStr.append(chnNumChar[v]).append(chnStr);
                }
            } else {
                zero = false;
                strIns.delete(0, strIns.length());
                strIns.append(chnNumChar[v]);
                strIns.append(chnUnitChar[unitPos]);
                chnStr.insert(0, strIns);
            }
            unitPos++;
            section = (int) Math.floor(section / 10f);
        }
        return chnStr.toString();
    }

}
