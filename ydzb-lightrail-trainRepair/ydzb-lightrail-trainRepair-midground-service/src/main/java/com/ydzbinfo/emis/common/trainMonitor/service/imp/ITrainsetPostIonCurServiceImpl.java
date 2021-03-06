package com.ydzbinfo.emis.common.trainMonitor.service.imp;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.common.general.service.ICommonService;
import com.ydzbinfo.emis.common.trainMonitor.dao.ITrainsetPostIonCurMapper;
import com.ydzbinfo.emis.common.trainMonitor.service.ITrainsetPostIonCurService;
import com.ydzbinfo.emis.common.trainMonitor.service.ITrainsetPostionHisService;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ShuntPlanModel;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetPostionCurWithNextTrack;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionHis;
import com.ydzbinfo.emis.trainRepair.workprocess.model.WorkTime;
import com.ydzbinfo.emis.utils.ContextUtils;
import com.ydzbinfo.emis.utils.DayPlanUtil;
import com.ydzbinfo.emis.utils.LockUtil;
import com.ydzbinfo.emis.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaohan
 * @description
 * @createDate 2021/2/26 12:06
 **/
@Transactional
@Service
public class ITrainsetPostIonCurServiceImpl extends ServiceImpl<ITrainsetPostIonCurMapper, TrainsetPostionCur> implements ITrainsetPostIonCurService {
    @Resource
    ITrainsetPostIonCurMapper ITrainsetPostIonCurMapper;

    @Resource
    ITrainsetPostionHisService trainsetpostionHisServiceI;

    @Resource
    IRemoteService remoteService;

    @Autowired
    ICommonService commonService;

    @Resource
    ITrainsetPostIonCurMapper trainsetPostIonCurMapper;


    @Override
    public List<TrainsetPostionCurWithNextTrack> getTrainsetPostion(Page page, List<String> trackCodes, List<String> trainsetNames, List<String> unitCodes, boolean showShuntPlan) throws ParseException {
        List<TrainsetPostionCur> trainsetPostionCurList = ITrainsetPostIonCurMapper.getTrainsetPostion(page, trackCodes, trainsetNames, unitCodes);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String unitCode = unitCodes.get(0);

        //???????????????
        String dayPlanId = DayPlanUtil.getDayPlanId(unitCode);
        //?????????????????????????????????
        WorkTime workTime = DayPlanUtil.getWorkTimeByDayPlanId(dayPlanId);
        String startTime = simpleDateFormat.format(workTime.getStartTime());
        String endTime = simpleDateFormat.format(workTime.getEndTime());
        List<ShuntPlanModel> shuntPlanModels = remoteService.getShuntingPlanByCondition(unitCode, "", startTime, endTime);

        List<TrainsetPostionCurWithNextTrack> trainsetPostionCurWithNextTracks = new ArrayList<>();
        for (TrainsetPostionCur trainsetPostionCur : trainsetPostionCurList) {
            TrainsetPostionCurWithNextTrack trainsetPostionCurWithNextTrack = new TrainsetPostionCurWithNextTrack();
            BeanUtils.copyProperties(trainsetPostionCur, trainsetPostionCurWithNextTrack);
            if (showShuntPlan) {
                List<ShuntPlanModel> shuntPlanModelsByTrainsetId = shuntPlanModels.stream().filter(v -> v.getEmuId().equals(trainsetPostionCur.getTrainsetId())).collect(Collectors.toList());
                shuntPlanModelsByTrainsetId.sort((a, b) -> {
                    if (a.getInTime().before(b.getInTime())) {
                        return -1;
                    } else if (a.getInTime().after(b.getInTime())) {
                        return 1;
                    } else {
                        return 0;
                    }
                });
                Date date = new Date();
                for (ShuntPlanModel shuntPlanModel : shuntPlanModelsByTrainsetId) {
                    if (shuntPlanModel.getInTime().after(date)) {
                        trainsetPostionCurWithNextTrack.setNextTrackName(shuntPlanModel.getTrackName());
                        trainsetPostionCurWithNextTrack.setNextTrackCode(shuntPlanModel.getTrackCode());
                        trainsetPostionCurWithNextTrack.setNextTrackPositionName(shuntPlanModel.getTrackPositionName());
                        trainsetPostionCurWithNextTrack.setNextTrackPositionCode(shuntPlanModel.getTrackPositionCode());
                        trainsetPostionCurWithNextTrack.setNextInTime(shuntPlanModel.getInTime());
                        break;
                    }
                }
            }
            trainsetPostionCurWithNextTracks.add(trainsetPostionCurWithNextTrack);
        }
        return trainsetPostionCurWithNextTracks;
    }

    @Override
    public List<TrainsetPostionCur> getTrainsetPostionByTrackCode(String trackCode) {
        return ITrainsetPostIonCurMapper.getTrainsetPostionByTrackCode(trackCode);
    }

    @Override
    public int addTrainsetPostion(TrainsetPostionCur trainsetPositionEntity) {
        trainsetPositionEntity.setUnitName(ContextUtils.getUnitName());
        return ITrainsetPostIonCurMapper.addTrainsetPostion(trainsetPositionEntity);
    }

    @Override
    public int updateTrainsetPostion(TrainsetPostionCur trainsetPositionEntity) throws ParseException {
        String unitCode = ContextUtils.getUnitCode();
        ShuntPlanModel shuntPlanModel = getOutTimeInfoByTrainsetId(trainsetPositionEntity.getTrainsetId(), unitCode);
        trainsetPositionEntity.setOutTime(shuntPlanModel == null ? null : shuntPlanModel.getInTime());
        return ITrainsetPostIonCurMapper.updateTrainsetPostion(trainsetPositionEntity);
    }

    @Override
    public TrainsetPostionCur getTrainsetPositionById(String trainsetId) {
        return ITrainsetPostIonCurMapper.getTrainsetPositionById(trainsetId);
    }

    @Override
    public int deleteTrainsetPostion(String trainsetId) {
        return ITrainsetPostIonCurMapper.deleteTrainsetPostion(trainsetId);
    }

    private final LockUtil lockUtil = LockUtil.newInstance();

    @Override
    public String setTrainsetPosition(TrainsetPostionCur trainsetPositionEntity) throws ParseException {
        Set<Object> lockKeys = new HashSet<>();
        try {
            lockKeys.add(trainsetPositionEntity);
            lockUtil.getDoLock(trainsetPositionEntity).lock();
            // ????????????????????????
            if (trainsetPositionEntity.getOutTime() == null) {
                if (StringUtils.isBlank(trainsetPositionEntity.getTrackCode())) {
                    Boolean hasTrackByTrainsetId = hasTrackByTrainsetId(trainsetPositionEntity);
                    if (hasTrackByTrainsetId) {
                        return trainsetPositionEntity.getTrainsetName() + "??????????????????" + trainsetPositionEntity.getTrackName() + "??????,?????????!";
                    }
                    this.updateTrainsetPostion(trainsetPositionEntity);
                } else {
                    //??????????????????,????????????????????????
                    String hasTaskByTrainsetId = getTrainsetByTask(trainsetPositionEntity, true);
                    if (hasTaskByTrainsetId != null) {
                        return hasTaskByTrainsetId;
                    }
                    //??????????????????
                    String id = UUID.randomUUID().toString();
                    trainsetPositionEntity.setTrainsetPostionId(id);
                    this.addTrainsetPostion(trainsetPositionEntity);
                }
            } else {// ????????????
                // ?????????????????????????????????
                TrainsetPostionCur fullTrainsetPositionEntity = this.getTrainsetPositionById(trainsetPositionEntity.getTrainsetId());
                // ????????????????????????
                int delCount = this.deleteTrainsetPostion(trainsetPositionEntity.getTrainsetId());
                if (fullTrainsetPositionEntity == null || delCount == 0) {
                    // ????????????????????????????????????
                    for (Object lockKey : lockKeys) {
                        lockUtil.unlockAll(lockKey);
                    }
                    return trainsetPositionEntity.getTrainsetName() + "????????????????????????!";
                }
                // ??????????????????????????????????????????
                TrainsetPostionHis trainsetpostionHis = new TrainsetPostionHis();
                BeanUtils.copyProperties(fullTrainsetPositionEntity, trainsetpostionHis);
                trainsetpostionHis.setId(fullTrainsetPositionEntity.getTrainsetPostionId());
                trainsetpostionHis.setTrainsetId(fullTrainsetPositionEntity.getTrainsetId());
                trainsetpostionHisServiceI.addTrainsetPostionHis(trainsetpostionHis);
            }
            lockUtil.unlock(trainsetPositionEntity);
        } finally {
            // ????????????????????????????????????
            for (Object lockKey : lockKeys) {
                lockUtil.unlockAll(lockKey);
            }
        }
        return null;
    }

    @Override
    public void setTrainsetState(TrainsetIsConnect trainsetIsConnect) {
        ITrainsetPostIonCurMapper.setTrainsetState(trainsetIsConnect);
    }

    public ShuntPlanModel getOutTimeInfoByTrainsetId(String trainsetId, String unitCode) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //???????????????
        String dayPlanId = DayPlanUtil.getDayPlanId(unitCode);
        //?????????????????????????????????
        WorkTime workTime = DayPlanUtil.getWorkTimeByDayPlanId(dayPlanId);
        String startTime = simpleDateFormat.format(workTime.getStartTime());
        String endTime = simpleDateFormat.format(workTime.getEndTime());
        List<ShuntPlanModel> shuntPlanModels = remoteService.getShuntingPlanByCondition(unitCode, trainsetId, startTime, endTime);
        shuntPlanModels.sort((a, b) -> {
            if (a.getInTime().before(b.getInTime())) {
                return -1;
            } else if (a.getInTime().after(b.getInTime())) {
                return 1;
            } else {
                return 0;
            }
        });
        Date date = new Date();
        for (ShuntPlanModel shuntPlanModel : shuntPlanModels) {
            if (shuntPlanModel.getInTime().after(date)) {
                return shuntPlanModel;
            }
        }
        return null;
    }

    @Override
    public String updTrackCode(List<TrainsetPostionCur> trainsetPostionCurs) throws ParseException {
        StringBuffer errorUpdTracks = new StringBuffer();
        //??????????????????
        Map<String, List<TrainsetPostionCur>> groupedFlowRunListByTrainsetId = trainsetPostionCurs.stream().collect(Collectors.groupingBy(TrainsetPostionCur::getTrackCode));
        for (List<TrainsetPostionCur> trainsetPostionCurList : groupedFlowRunListByTrainsetId.values()) {
            Set<Object> lockKeys = new HashSet<>();
            try {
                lockKeys.add(trainsetPostionCurList);
                lockUtil.getDoLock(trainsetPostionCurList).lock();
                //?????????????????????????????????????????????????????????
                StringBuffer errorUpdTrack = new StringBuffer();

                for (TrainsetPostionCur trainsetPostionCur : trainsetPostionCurs) {
                    //??????????????????????????????????????????
                    String hasTaskByTrainsetId = getTrainsetByTask(trainsetPostionCur, false);
                    if (hasTaskByTrainsetId != null) {
                        errorUpdTracks.append(hasTaskByTrainsetId);
                        errorUpdTrack.append(hasTaskByTrainsetId);
                    }
                }

                if (StringUtils.isBlank(errorUpdTrack)) {
                    for (TrainsetPostionCur trainsetPostionCur : trainsetPostionCurList) {
                        this.updateTrainsetPostion(trainsetPostionCur);
                    }
                }
                lockUtil.unlock(trainsetPostionCurList);
            } finally {
                // ????????????????????????????????????
                for (Object lockKey : lockKeys) {
                    lockUtil.unlockAll(lockKey);
                }
            }
        }
        return errorUpdTracks.toString();
    }

    //???????????????????????????????????????
    public String getTrainsetByTask(TrainsetPostionCur trainsetPostionCur, boolean isAddTrainset) {
        //????????????????????????
        Boolean hasTrackByTrainsetId = hasTrackByTrainsetId(trainsetPostionCur);
        if (!isAddTrainset && hasTrackByTrainsetId) {
            return  trainsetPostionCur.getTrainsetName()+ "????????????,?????????!";
        }

        String unitCode = ContextUtils.getUnitCode();
        TrainsetPostionCur trainsetPostIonByHead = new TrainsetPostionCur();
        trainsetPostIonByHead.setUnitCode(trainsetPostionCur.getUnitCode());
        trainsetPostIonByHead.setTrackCode(trainsetPostionCur.getTrackCode());
        trainsetPostIonByHead.setHeadDirectionPlaCode(trainsetPostionCur.getHeadDirectionPlaCode());
        trainsetPostIonByHead.setTailDirectionPlaCode(trainsetPostionCur.getTailDirectionPlaCode());
        trainsetPostIonByHead.setUnitCode(unitCode);

        TrainsetPostionCur trainsetPostIonByTail = new TrainsetPostionCur();
        trainsetPostIonByTail.setUnitCode(trainsetPostionCur.getUnitCode());
        trainsetPostIonByTail.setTrackCode(trainsetPostionCur.getTrackCode());
        trainsetPostIonByTail.setHeadDirectionPlaCode(trainsetPostionCur.getTailDirectionPlaCode());
        trainsetPostIonByTail.setTailDirectionPlaCode(trainsetPostionCur.getHeadDirectionPlaCode());
        trainsetPostIonByTail.setUnitCode(unitCode);

        TrainsetPostionCur trainsetPostIonByHeadTrack = trainsetPostIonCurMapper.selectOne(trainsetPostIonByHead);
        TrainsetPostionCur trainsetPostIonByTailTrack = trainsetPostIonCurMapper.selectOne(trainsetPostIonByTail);



        //??????????????????????????????,???????????????????????????????????????
        if (trainsetPostionCur.getIsConnect().equals("1") || trainsetPostionCur.getIsLong().equals("1")) {
            if ((trainsetPostIonByHeadTrack != null || trainsetPostIonByTailTrack != null) && isAddTrainset) {
                String trainsetName = trainsetPostIonByHeadTrack == null ? trainsetPostIonByTailTrack.getTrainsetName() : trainsetPostIonByHeadTrack.getTrainsetName();
                String trackName = trainsetPostIonByHeadTrack == null ? trainsetPostIonByTailTrack.getTrackName() : trainsetPostIonByHeadTrack.getTrackName();
                return trackName + "???????????????:" + trainsetName + "??????,?????????!";
            }
        } else {
            if (trainsetPostIonByHeadTrack != null) {
                String trainsetName = trainsetPostIonByHeadTrack.getTrainsetName();
                String trackName = trainsetPostIonByHeadTrack.getTrackName();
                return trackName + "???????????????:" + trainsetName + "??????,?????????!";
            }
        }
        return null;
    }

    //????????????????????????
    public Boolean hasTrackByTrainsetId(TrainsetPostionCur trainsetPostionCur) {
        String unitCode = ContextUtils.getUnitCode();
        //??????????????????????????????
        TrainsetPostionCur hasTrackByTrainsetIdEntity = new TrainsetPostionCur();
        hasTrackByTrainsetIdEntity.setTrainsetId(trainsetPostionCur.getTrainsetId());
        hasTrackByTrainsetIdEntity.setUnitCode(unitCode);
        TrainsetPostionCur hasTrackByTrainsetId = trainsetPostIonCurMapper.selectOne(hasTrackByTrainsetIdEntity);
        return hasTrackByTrainsetId == null;
    }
}
