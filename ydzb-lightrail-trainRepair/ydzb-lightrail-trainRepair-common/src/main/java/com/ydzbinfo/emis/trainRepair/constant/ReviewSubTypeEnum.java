package com.ydzbinfo.emis.trainRepair.constant;

import lombok.Getter;

/**
 * @Description: 复核任务子类型
 * @Data: 2022/01/18
 * @Author: 史艳涛
 */
@Getter
public enum ReviewSubTypeEnum {

    REVIEW_SUB_DEFAULT("0", "默认类型"),

    REVIEW_SUB_INTERNALSPUR("1", "内距"),

    REVIEW_SUB_DETECTIONQR("2", "踏面磨耗、轮缘厚度、QR值、轮径、深度、长度、不圆度、探伤检测"),

    REVIEW_SUB_COAXIAL("3", "同轴、同转向架、同车厢轮径差"),

    REVIEW_SUB_SKATEHIGHT("4", "滑板高度差"),

    REVIEW_SUB_SKATEABRASION("5", "滑板磨耗到限"),

    REVIEW_SUB_KISSPRESSURE("6", "接触压力"),

    REVIEW_SUB_LYOTHER("7", "轮对其他类型"),

    REVIEW_SUB_SJOTHER("8", "受电弓其他类型");

    private final String value;
    private final String text;

    ReviewSubTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }
}
