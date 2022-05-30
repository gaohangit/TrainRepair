package com.ydzbinfo.emis.trainRepair.remotemodel.device;

import lombok.Data;

/**
 * @author 史艳涛
 * @description 镟轮设备上传的镟轮数据实体
 * @createDate 2021/10/25 9:04
 */
@Data
public class EntityUlatheDevice {
    /**
     * 构造函数
     */
    public EntityUlatheDevice(){}
    /**
     * 主键ID
     */
    private   String	S_ID;
    /**
     * 设备信息
     */
    private  String	S_DEVICE_INFO;
    /**
     * 加工日期
     */
    private  String	D_PRODUCT_TIME;
    /**
     * 加工结束时间
     */
    private  String	S_END_PRODUCT_TIME;
    /**
     * 加工时长
     */
    private  String	S_MACHINING_LENGTH;
    /**
     * 操作员id
     */
    private  String	S_OPERATOR_ID;
    /**
     * 加工原因
     */
    private  String	S_PRODUCT_CAUSE;
    /**
     * 加工类型
     */
    private  String	S_PRODUCT_TYPE;
    /**
     * 轮廓型
     */
    private  String	S_OUTLINE_TYPE;
    /**
     * 轮对ID
     */
    private  String	S_WHEELSET_ID;
    /**
     * 轮对位置
     */
    private  String	S_WHEELSET_SITUATION;
    /**
     * 轮对方向
     */
    private  String	S_WHEELSET_DIRECTION;
    /**
     * 转向架id
     */
    private  String	S_BOGIE_ID;
    /**
     * 车辆ID
     */
    private  String	S_CAR_ID;
    /**
     * 车辆方向 0=A-B, 1=B-A
     */
    private  String	S_CAR_DIRECTION;
    /**
     * 车辆类型
     */
    private  String	S_CAR_TYPE;
    /**
     * 轮对内侧距（预测量）
     */
    private  String	I_PRE_WHEELSET_ID;
    /**
     * 轮对外侧距（预测量）
     */
    private  String	I_PRE_WHEELSET_OD;
    /**
     * 轮径差（预测量）
     */
    private  String	I_PRE_WHEEL_DD;
    /**
     * 轮径左（预测量）
     */
    private  String	I_PRE_WHEEL_DL;
    /**
     * 轮径右（预测量）
     */
    private  String	I_PRE_WHEEL_DR;
    /**
     * 轴向窜动左（预测量）
     */
    private  String	I_PRE_ASL;
    /**
     * 轴向窜动右（预测量）
     */
    private  String	I_PRE_ASR;
    /**
     * 径向跳动左（预测量）
     */
    private  String	I_PRE_RSL;
    /**
     * 径向跳动右（预测量）
     */
    private  String	I_PRE_RSR;
    /**
     * 轮缘高度左（预测量）
     */
    private  String	I_PRE_SHL;
    /**
     * 轮缘高度右（预测量）
     */
    private  String	I_PRE_SHR;
    /**
     * 轮缘厚度左（预测量）
     */
    private  String	I_PRE_SDL;
    /**
     *  轮缘厚度右（预测量）
     */
    private  String	I_PRE_SDR;
    /**
     * QR尺寸左（预测量）
     */
    private  String	I_PRE_QRL;
    /**
     * QR尺寸右（预测量）
     */
    private  String	I_PRE_QRR;
    /**
     * 轮对多边形低阶最大值左（预测量,粗糙度等级）
     */
    private  String	I_PRE_WHEEL_PLBL;
    /**
     * 轮对多边形低阶最大值右（预测量,粗糙度等级）
     */
    private  String	I_PRE_WHEEL_PLBR;
    /**
     * 轮对多边形低阶最大值阶次左（预测量,粗糙度等级）
     */
    private  String	S_PRE_WHEEL_PLBCL;
    /**
     * 轮对多边形低阶最大值阶次右（预测量,粗糙度等级）
     */
    private  String	S_PRE_WHEEL_PLBCR;
    /**
     * 轮对多边形高阶最大值左（预测量,粗糙度等级）
     */
    private  String	I_PRE_WHEELSET_PLBL;
    /**
     * 轮对多边形高阶最大值右（预测量,粗糙度等级）
     */
    private  String	I_PRE_WHEELSET_PLBR;
    /**
     * 轮对多边形高阶最大值阶次左（预测量,粗糙度等级）
     */
    private  String	S_PRE_WHEELSET_PLBCL;
    /**
     * 轮对多边形高阶最大值阶次右（预测量,粗糙度等级）
     */
    private  String	S_PRE_WHEELSET_PLBCR;
    /**
     * 等效锥度左（UIC519方法）（预测量）
     */
    private  String	S_PRE_ETL_UIC;
    /**
     * 等效锥度右（UIC519方法）（预测量）
     */
    private  String	S_PRE_ETR_UIC;
    /**
     * 等效锥度左（简易方法）（预测量）
     */
    private  String	S_PRE_ETL_TEM;
    /**
     * 等效锥度右（简易方法）（预测量）
     */
    private  String	S_PRE_ETR_TEM;
    /**
     * 轮对多边形左（1-40阶）（预测量）
     */
    private  String	S_PRE_WHEELSET_PL;
    /**
     * 轮对多边形右（1-40阶）（预测量）
     */
    private  String	S_PRE_WHEELSET_PR;
    /**
     * 径跳函数（预测量，左右）
     */
    private  String	S_PRE_RJF;
    /**
     * 踏面轮廓左（预测量）
     */
    private  String	S_PRE_TOLL;
    /**
     * 踏面轮廓右（预测量）
     */
    private  String	S_PRE_TOLR;
    /**
     * 改型（目标值）
     */
    private  String	S_TARGET_CHANGETYPE;
    /**
     * 轮径（目标值）
     */
    private  String	I_TARGET_WR;
    /**
     * 轮缘厚度左（目标值）
     */
    private  String	I_TARGET_SDL;
    /**
     * 轮缘厚度右（目标值）
     */
    private  String	I_TARGET_SDR;
    /**
     * 轮对内侧距（加工后测量值）
     */
    private  String	I_LT_WHEELSET_ID;
    /**
     * 轮对外侧距（加工后测量值）
     */
    private  String	I_LT_WHEELSET_OD;
    /**
     * 轮径差（加工后测量值）
     */
    private  String	I_LT_WHEEL_DD;
    /**
     * 轮径左（加工后测量值）
     */
    private  String	I_LT_WHEEL_DL;
    /**
     * 轮径右（加工后测量值）
     */
    private  String	I_LT_WHEEL_DR;
    /**
     * 轴向窜动左（加工后测量值）
     */
    private  String	I_LT_ASL;
    /**
     * 轴向窜动右（加工后测量值）
     */
    private  String	I_LT_ASR;
    /**
     * 径向跳动左（加工后测量值）
     */
    private  String	I_LT_RSL;
    /**
     * 径向跳动右（加工后测量值）
     */
    private  String	I_LT_RSR;
    /**
     * 轮缘高度左（加工后测量值）
     */
    private  String	I_LT_SHL;
    /**
     * 轮缘高度右（加工后测量值）
     */
    private  String	I_LT_SHR;
    /**
     * 轮缘厚度左（加工后测量值）
     */
    private  String	I_LT_SDL;
    /**
     * 轮缘厚度右（加工后测量值）
     */
    private  String	I_LT_SDR;
    /**
     * QR尺寸左（加工后测量值）
     */
    private  String	I_LT_QRL;
    /**
     * QR尺寸右（加工后测量值)
     */
    private  String	I_LT_QRR;
    /**
     * 轮对多边形低阶最大值左（加工后测量值,粗糙度等级）
     */
    private  String	I_LT_WHEEL_PLBL;
    /**
     * 轮对多边形低阶最大值右（加工后测量值,粗糙度等级）
     */
    private  String	I_LT_WHEEL_PLBR;
    /**
     * 轮对多边形高阶最大值阶次左（加工后测量值,粗糙度等级）
     */
    private  String	S_LT_WHEEL_PLBCL;
    /**
     * 轮对多边形高阶最大值阶次右（加工后测量值,粗糙度等级）
     */
    private  String	S_LT_WHEEL_PLBCR;
    /**
     * 轮对多边形高阶最大值左（加工后测量值,粗糙度等级）
     */
    private  String	I_LT_WHEELSET_PLBL;
    /**
     * 轮对多边形高阶最大值右（加工后测量值,粗糙度等级）
     */
    private  String	I_LT_WHEELSET_PLBR;
    /**
     * 轮对多边形高阶最大值阶次左（加工后测量值,粗糙度等级）
     */
    private  String	S_LT_WHEELSET_PLBCL;
    /**
     * 轮对多边形高阶最大值阶次右（加工后测量值,粗糙度等级）
     */
    private  String	S_LT_WHEELSET_PLBCR;
    /**
     * 等效锥度左（UIC519方法）（加工后测量值）
     */
    private  String	S_LT_ETL_UIC;
    /**
     * 等效锥度右（UIC519方法）（加工后测量值）
     */
    private  String	S_LT_ETR_UIC;
    /**
     * 等效锥度左（简易方法）（加工后测量值）
     */
    private  String	S_LT_ETL_TEM;
    /**
     * 等效锥度右（简易方法）（加工后测量值）
     */
    private  String	S_LT_ETR_TEM;
    /**
     * 轮对多边形左（1-40阶）（加工后测量值）
     */
    private  String	S_LT_WHEELSET_PL;
    /**
     * 轮对多边形右（1-40阶）（加工后测量值）
     */
    private  String	S_LT_WHEELSET_PR;
    /**
     * 径跳函数（加工后测量值，左右）
     */
    private  String	S_LT_RJF;
    /**
     * 踏面轮廓左（加工后测量值）
     */
    private  String	S_LT_TOLL;
    /**
     * 踏面轮廓右（加工后测量值）
     */
    private  String	S_LT_TOLR;
    /**
     * 上传时间
     */
    private  String	D_UPLOADTIME;
    /**
     * 里程数
     */
    private  int	I_MILEAGE;
    /**
     * 转向架方向(新加2017-08-07)
     */
    private  String	S_BOGIE_DIRECTION;

}
