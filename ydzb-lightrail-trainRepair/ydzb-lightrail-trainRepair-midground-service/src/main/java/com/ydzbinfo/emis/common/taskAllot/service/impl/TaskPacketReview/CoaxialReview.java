package com.ydzbinfo.emis.common.taskAllot.service.impl.TaskPacketReview;

import com.ydzbinfo.emis.trainRepair.constant.ReviewSubTypeEnum;
import com.ydzbinfo.emis.trainRepair.constant.ReviewTypeEnum;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CoaxialReview extends BaseReview  {
    @Override
    protected ReviewTypeEnum getReviewType() {
        return ReviewTypeEnum.REVIEW_WHEEL;
    }

    @Override
    protected ReviewSubTypeEnum getReviewSubType() {
        return ReviewSubTypeEnum.REVIEW_SUB_COAXIAL;
    }

    @Override
    protected int getFillRowStartIndex() {
        return 0;
    }

    @Override
    protected int getFillColumnStartIndex() {
        return 0;
    }

    @Override
    protected void setReviewSummary(TaskAllotPacketEntity t, List<TaskAllotItemEntity> items, ZyMReviewtasksummary summary, List<ZyMReviewtaskdetail> lstDetail, List<ZyMReviewtaskperson> lstPerson) {

    }
}
