package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

public interface ISingleCondition<T> {
    /**
     * 是否相等 相等返回true
     * @param value
     * @param otherValue
     * @return
     */
    boolean equals(T value, T otherValue);

    /**
     * 是否存在交集，交集意味着仅依靠当前条件进行匹配时，匹配到的结果存在重合
     * @param value
     * @param otherValue
     * @return
     */
    boolean hasIntersection(T value, T otherValue);

    /**
     * 是否因为描述的清晰度级别不同而存在交集，为否时返回null，为真时返回哪侧为模糊的一侧
     * 比如一边是空白，一边不是，那么空白的一边因为模糊了描述而包含了另一边
     * @param value
     * @param otherValue
     * @return
     */
    DescriptionLevelDifferentDirectionEnum getDescriptionLevelDifferentDirection(T value, T otherValue);

}
