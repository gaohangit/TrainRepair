package com.ydzbinfo.emis.common.taskAllot.service.impl.TaskPacketReview;

import com.ydzbinfo.emis.trainRepair.constant.ReviewSubTypeEnum;
import com.ydzbinfo.emis.trainRepair.constant.ReviewTypeEnum;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 内距
 */
@Component
public class InternalSpurReview extends BaseReview {
    @Override
    protected ReviewTypeEnum getReviewType() {
        return ReviewTypeEnum.REVIEW_WHEEL;
    }

    @Override
    protected ReviewSubTypeEnum getReviewSubType() {
        return ReviewSubTypeEnum.REVIEW_SUB_INTERNALSPUR;
    }

    @Override
    protected int getFillRowStartIndex() {
        return 8;
    }

    @Override
    protected int getFillColumnStartIndex() {
        return 6;
    }

    @Override
    protected void setReviewSummary(TaskAllotPacketEntity t, List<TaskAllotItemEntity> items, ZyMReviewtasksummary summary, List<ZyMReviewtaskdetail> lstDetail, List<ZyMReviewtaskperson> lstPerson) {
        try{
            int rowIndex = getFillRowStartIndex();
            for(TaskAllotItemEntity item : items){
                ZyMReviewtaskdetail detail = createReviewDetail(summary, item);
                List<ZyMReviewtaskdetail> ds = lstDetail.stream().filter(f->f.getCarname().equals(detail.getCarname())).filter(f->f.getPosition().equals(detail.getPosition())).filter(f->f.getItemname().equals(detail.getItemname())).filter(f->f.getWheelposition().equals(detail.getWheelposition())).collect(Collectors.toList());
                if(ds.size() == 0){
                    lstDetail.add(detail);
                    for(TaskAllotPersonEntity p : item.getTaskAllotPersonEntityList()){
                        ZyMReviewtaskperson person = createReviewPerson(detail, p);
                        lstPerson.add(person);
                    }
                    rowIndex++;
                }
            }
        }catch (Exception e){

        }
    }
}
