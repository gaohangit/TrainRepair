package com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author: gaoHan
 * @date: 2021/8/3 9:32
 * @description:
 */
public class SjReCheckResult extends LyReCheckResult{
    /**
     * 复核值
     */
    private String wuCha;
    /**
     * 处理意见
     */
    private String dealAdvice;

    public String getWuCha() {
        return wuCha;
    }

    @JSONField(name = "S_WUCHA")
    public void setWuCha(String wuCha) {
        this.wuCha = wuCha;
    }

    public String getDealAdvice() {
        return dealAdvice;
    }

    @JSONField(name = "S_DEALADVICE")
    public void setDealAdvice(String dealAdvice) {
        this.dealAdvice = dealAdvice;
    }
}