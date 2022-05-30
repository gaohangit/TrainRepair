package com.ydzbinfo.emis.common.taskAllot.service.impl.TaskPacketReview;

import com.ydzbinfo.emis.common.taskAllot.service.impl.*;
import com.ydzbinfo.emis.trainRepair.constant.ReviewSubTypeEnum;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.EntityLYOverRunRecord;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.EntitySJOverRunRecord;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>
 *  派工时向EMIS写入复核任务派工数据
 * </p>
 *
 * @author 史艳涛
 * @since 2022-01-18
 */
@Service
public class TaskPacketReviewItem extends TaskPacketItem {

    protected static final Logger logger = getLogger(TaskPacketReviewItem.class);
    @Autowired
    ZyCReviewtaskbillServiceImpl zyCReviewtaskbillService;
    @Autowired
    ZyMReviewtasksummaryServiceImpl zyMReviewtasksummaryService;
    @Autowired
    ZyMReviewtaskdetailServiceImpl zyMReviewtaskdetailService;
    @Autowired
    ZyMReviewtaskpersonServiceImpl zyMReviewtaskpersonService;
    @Autowired
    InternalSpurReview internalSpurReview;
    @Autowired
    CoaxialReview coaxialReview;

    /**
     * 获取复核处理实例
     * @param type 复核子类型
     */
    private BaseReview getReviewHandler(ReviewSubTypeEnum type){
        if(type.equals(ReviewSubTypeEnum.REVIEW_SUB_INTERNALSPUR.getValue()))
            return internalSpurReview;
        if(type.equals(ReviewSubTypeEnum.REVIEW_SUB_DETECTIONQR.getValue()))
            return internalSpurReview;
        if(type.equals(ReviewSubTypeEnum.REVIEW_SUB_COAXIAL.getValue()))
            return coaxialReview;
        if(type.equals(ReviewSubTypeEnum.REVIEW_SUB_LYOTHER.getValue()))
            return internalSpurReview;
        if(type.equals(ReviewSubTypeEnum.REVIEW_SUB_SKATEHIGHT.getValue()))
            return internalSpurReview;
        if(type.equals(ReviewSubTypeEnum.REVIEW_SUB_SKATEABRASION.getValue()))
            return internalSpurReview;
        if(type.equals(ReviewSubTypeEnum.REVIEW_SUB_KISSPRESSURE.getValue()))
            return internalSpurReview;
        if(type.equals(ReviewSubTypeEnum.REVIEW_SUB_SJOTHER.getValue()))
            return internalSpurReview;
        return null;
    }

    /**
     * 根据超限数据ID获取该超限数据的超限类型
     * @param analyseId 超限数据ID
     * @param trainsetName 车组名称
     * @return
     */
    private ZyCReviewtaskbill getSingleReviewBill(String analyseId, String trainsetName){
        ZyCReviewtaskbill bill = null;
//        try{
//            List<ZyCReviewtaskbill> lstBill = taskCommon.getReviewTaskBillDict();
//            List<EntityLYOverRunRecord> lyOverRunRecords = taskCommon.getLYOverRunRecord(trainsetName);
//            if(lyOverRunRecords != null && lyOverRunRecords.size() > 0){
//                for(EntityLYOverRunRecord r : lyOverRunRecords){
//                    if(r.getId().equals(analyseId)){
//                        List<ZyCReviewtaskbill> bs = lstBill.stream().filter(t->t.getCheckitemid().equals(r.getCheckItem())).collect(Collectors.toList());
//                        if(bs.size() > 0){
//                            bill = bs.get(0);
//                            break;
//                        }
//                    }
//                }
//            }
//            if(bill == null){
//                List<EntitySJOverRunRecord> sjOverRunRecords = taskCommon.getSJOverRunRecord(trainsetName);
//                if(sjOverRunRecords != null && sjOverRunRecords.size() > 0){
//                    for(EntitySJOverRunRecord r : sjOverRunRecords){
//                        if(r.getId().equals(analyseId)){
//                            List<ZyCReviewtaskbill> bs = lstBill.stream().filter(t->t.getCheckitemid().equals(r.getCheckItem())).collect(Collectors.toList());
//                            if(bs.size() > 0){
//                                bill = bs.get(0);
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//        }catch (Exception e){
//            bill = null;
//            logger.error("根据超限数据ID["+ analyseId +"]获取该超限数据的超限类型时引发异常：" + e.toString());
//        }
        return bill;
    }
}
