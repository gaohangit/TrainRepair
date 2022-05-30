package com.ydzbinfo.emis.common.taskAllot.service.impl.TaskPacketReview;

import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.ydzbinfo.emis.trainRepair.constant.ReviewSubTypeEnum;
import com.ydzbinfo.emis.trainRepair.constant.ReviewTypeEnum;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.EntityLYOverRunRecord;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.EntitySJOverRunRecord;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>
 *  生成复核记录单基类
 * </p>
 *
 * @author 史艳涛
 * @since 2022-01-20
 */
@Component
public abstract class BaseReview {

    protected static final Logger logger = getLogger(BaseReview.class);
    /**
     *  获取复核主类型
     * @return 复核主类型
     */
    protected abstract ReviewTypeEnum getReviewType();

    /**
     * 获取复核子类型
     * @return 复核子类型
     */
    protected abstract ReviewSubTypeEnum getReviewSubType();

    /**
     * 获取回填数据起始行下标
     * @return 回填数据起始行下标
     */
    protected abstract int getFillRowStartIndex();

    /**
     * 获取回填数据起始列下标
     * @return 回填数据起始列下标
     */
    protected abstract int getFillColumnStartIndex();

    /**
     * 设置复核记录单信息
     * @param t 作业包信息
     * @param items 作业项目信息
     * @param summary 复核记录单主表
     * @param lstDetail 复核记录单子表
     * @param lstPerson 复核记录单人员信息
     */
    protected abstract void setReviewSummary(TaskAllotPacketEntity t, List<TaskAllotItemEntity> items, ZyMReviewtasksummary summary, List<ZyMReviewtaskdetail> lstDetail, List<ZyMReviewtaskperson> lstPerson);

    /**
     * 创建复核记录单主表
     * @param t 作业包信息
     * @param ms 作业项目信息
     * @return 复核记录单主表
     */
    public ZyMReviewtasksummary createReviewSummary(TaskAllotPacketEntity t, List<TaskAllotItemEntity> ms){
        try{
            ZyMReviewtasksummary summary = new ZyMReviewtasksummary();
            summary.setReviewtasksummaryid(UUID.randomUUID().toString());
            summary.setDayplanid(t.getDayPlanId());
            summary.setTrainsetname(t.getTrainsetName());
            List<String> ns = new ArrayList<String>();
            ms.stream().forEach(f-> ns.add(f.getItemName()));
            summary.setReviewtaskname(String.join("、", ns.stream().distinct().collect(Collectors.toList())));
            summary.setType(getReviewType().getValue());
            summary.setSubtype(getReviewSubType().getValue());
            summary.setDeptcode(t.getDeptCode());
            summary.setDeptname(t.getDeptName());
            summary.setChecklistno(getCheckListNo(summary.getDayplanid()));
            summary.setBackfillstate("0");
            summary.setCreatestaffcode(ShiroKit.getUser().getStaffId());
            summary.setCreatestaffname(ShiroKit.getUser().getName());
            summary.setForemansgincode("");
            summary.setForemansginname("");
            summary.setQualityinspectorscode("");
            summary.setQualityinspectorsname("");
            summary.setDispatchercode("");
            summary.setDispatchername("");
            summary.setPublishcode(UUID.randomUUID().toString());
            summary.setCreatetime(new Date());
            summary.setRemarks("");
            return summary;
        }catch (Exception e){
            logger.error("创建复核记录单主表时引发异常：" + e.toString());
        }
        return null;
    }

    /**
     * 创建复核记录单子表
     * @param summary 复核记录单主表
     * @param item 项目信息
     * @return 复核记录单子表
     */
    public ZyMReviewtaskdetail createReviewDetail(ZyMReviewtasksummary summary, TaskAllotItemEntity item){
        try{
            ZyMReviewtaskdetail detail = new ZyMReviewtaskdetail();
            detail.setReviewtaskdetailid(UUID.randomUUID().toString());
            detail.setReviewtasksummaryid(summary.getReviewtasksummaryid());
            detail.setReviewtaskitemid(item.getId());   //在创建前需要对 TaskAllotItemEntity 赋值才能使用
            detail.setAnalyseid(item.getItemCode());
            detail.setDayplanid(summary.getDayplanid());
            detail.setTrainsetname(summary.getTrainsetname());
            detail.setType(summary.getType());
            detail.setSubtype(summary.getSubtype());
            detail.setCarname(item.getCarNo());
            detail.setItemname(item.getItemName());
            detail.setColindex(getFillColumnStartIndex());
            if(getReviewType().getValue().equals(ReviewTypeEnum.REVIEW_WHEEL.getValue())){
                EntityLYOverRunRecord lyOverRunRecord = getLyOverRunRecord(summary.getTrainsetname(), item);
                if(lyOverRunRecord!= null){
                    detail.setPosition(lyOverRunRecord.getWheelSetPosition());
                    detail.setWheelposition(lyOverRunRecord.getWheelPosition());
                    detail.setCheckvalue(lyOverRunRecord.getCheckValue());
                }
            }else if(getReviewType().getValue().equals(ReviewTypeEnum.REVIEW_PANTOGRAPH.getValue())){
                EntitySJOverRunRecord sjOverRunRecord = getSJOverRunRecord(summary.getTrainsetname(), item);
                if(sjOverRunRecord!= null){
                    detail.setPosition(sjOverRunRecord.getPantoCode());
                    detail.setWheelposition(sjOverRunRecord.getSkaCode());
                    detail.setCheckvalue(sjOverRunRecord.getCheckValue());
                }
            }
            detail.setValue("");
            detail.setCreatetime(new Date());
            detail.setPublishcode(summary.getPublishcode());
            return detail;
        }catch (Exception e){
            logger.error("创建复核记录单子表时引发异常：" + e.toString());
        }
        return null;
    }

    /**
     * 创建复核记录单人员分配信息
     * @param detail 复核记录单子表
     * @param personEntity 项目的人员分配信息
     * @return 复核记录单人员分配信息
     */
    public ZyMReviewtaskperson createReviewPerson(ZyMReviewtaskdetail detail, TaskAllotPersonEntity personEntity){
        try{
            ZyMReviewtaskperson person = new ZyMReviewtaskperson();
            person.setReviewtaskpersonid(UUID.randomUUID().toString());
            person.setReviewtasksummaryid(detail.getReviewtasksummaryid());
            person.setReviewtaskitemid(detail.getReviewtaskitemid());
            person.setDayplanid(detail.getDayplanid());
            person.setWorkerstaffcode(ShiroKit.getUser().getStaffId());
            person.setWorkerstaffname(ShiroKit.getUser().getName());
            person.setPersoncode(personEntity.getWorkerId());
            person.setPersonname(personEntity.getWorkerName());
            person.setPublishcode(detail.getPublishcode());
            return person;
        }catch (Exception e){
            logger.error("创建复核记录单人员分配信息时引发异常：" + e.toString());
        }
        return null;
    }

    /**
     * 获取轮对超限数据
     * @param trainsetname 车组名称
     * @param item 项目信息
     * @return 轮对超限数据
     */
    public EntityLYOverRunRecord getLyOverRunRecord(String trainsetname, TaskAllotItemEntity item){
//        List<EntityLYOverRunRecord> records = taskCommon.getLYOverRunRecord(trainsetname);
//        List<EntityLYOverRunRecord> rs = records.stream().filter(r->r.getId().equals(item.getItemCode())).collect(Collectors.toList());
//        if(rs.size() > 0)
//            return rs.get(0);
        return null;
    }

    /**
     * 获取受电弓超限数据
     * @param trainsetname 车组名称
     * @param item 项目信息
     * @return 受电弓超限数据
     */
    public EntitySJOverRunRecord getSJOverRunRecord(String trainsetname, TaskAllotItemEntity item){
//        List<EntitySJOverRunRecord> records = taskCommon.getSJOverRunRecord(trainsetname);
//        List<EntitySJOverRunRecord> rs = records.stream().filter(r->r.getId().equals(item.getItemCode())).collect(Collectors.toList());
//        if(rs.size() > 0)
//            return rs.get(0);
        return null;
    }

    /**
     * 获取记录单编号
     * @param dayplanid 日计划ID
     * @return 记录单编号
     */
    private String getCheckListNo(String dayplanid){
        int ran = (int)(Math.random() * 1000);
        String checklistno = dayplanid;
        checklistno += "-" + getReviewType().getValue();
        checklistno += "-" + getReviewSubType().getValue();
        checklistno += "-" + ran;
        return checklistno;
    }
}
