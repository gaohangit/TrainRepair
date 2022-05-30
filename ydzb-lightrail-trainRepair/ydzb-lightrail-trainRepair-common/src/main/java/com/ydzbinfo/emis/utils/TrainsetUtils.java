package com.ydzbinfo.emis.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 车组逻辑工具箱
 */
public class TrainsetUtils {

    /**
     * 比较辆序先后顺序
     */
    public static int carNoComparator(String carNo1, String carNo2) {
        if (Objects.equals(carNo1, carNo2)) {
            return 0;
        } else if (Objects.equals(carNo1, "00")) {
            return 1;
        } else if (Objects.equals(carNo2, "00")) {
            return -1;
        } else {
            return Integer.parseInt(carNo1) - Integer.parseInt(carNo2);
        }
    }

    /**
     * 根据编组数量生成辆序列表
     */
    public static List<String> generateCarNoListFromMarshalCount(int marshalCount) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i < marshalCount; i++) {
            if (i >= 10) {
                list.add(i + "".trim());
            } else {
                list.add("0" + i);
            }
        }
        list.add("00");
        return list;
    }

}
