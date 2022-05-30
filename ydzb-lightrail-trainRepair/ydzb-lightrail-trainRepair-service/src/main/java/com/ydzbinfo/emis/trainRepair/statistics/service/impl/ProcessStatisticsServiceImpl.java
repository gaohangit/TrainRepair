package com.ydzbinfo.emis.trainRepair.statistics.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetInfo;
import com.ydzbinfo.emis.trainRepair.statistics.model.*;
import com.ydzbinfo.emis.trainRepair.statistics.querymodel.DurationDetail;
import com.ydzbinfo.emis.trainRepair.statistics.querymodel.DurationInfo;
import com.ydzbinfo.emis.trainRepair.statistics.service.ProcessStatisticsService;
import com.ydzbinfo.emis.trainRepair.warnmanagent.querymodel.WorkWorning;
import com.ydzbinfo.emis.trainRepair.warnmanagent.service.IWorkWorningService;
import com.ydzbinfo.emis.trainRepair.workProcess.service.IProcessCarPartService;
import com.ydzbinfo.emis.trainRepair.workProcess.service.IRfidCardSummaryService;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.RfidCardSummary;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.DateTimeUtil;
import com.ydzbinfo.emis.utils.DateUtils;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.*;

/**
 * Description:
 * Author: 吴跃常
 * Create Date Time: 2021/6/22 11:02
 * Update Date Time: 2021/6/22 11:02
 *
 * @see
 */
@Service
public class ProcessStatisticsServiceImpl implements ProcessStatisticsService {

    @Autowired
    private IProcessCarPartService processCarPartService;

    @Autowired
    private IRfidCardSummaryService rfidCardSummaryService;

    @Autowired
    private IRemoteService remoteService;

    @Autowired
    private IWorkWorningService workWorningService;

    @Override
    public DurationStatistics getStatisticsDuration(DurationEntity durationEntity) {
        long startTime = durationEntity.getStartTime();
        long endTime = durationEntity.getEndTime();
        String startDate = null;
        String endDate = null;
        //设置开始日计划id
        if (startTime != 0) {
            Date date = new Date(startTime);
            startDate = DateUtils.dateToFormatStr(date, "yyyy-MM-dd");
            durationEntity.setStartDate(startDate + "-00");
        }
        //设置结束日计划id
        if (endTime != 0) {
            Date date = new Date(endTime);
            endDate = DateUtils.dateToFormatStr(date, "yyyy-MM-dd");
            durationEntity.setEndDate(endDate + "-01");
        }
        List<DurationInfo> durationInfos = processCarPartService.selectStatisticsDuration(durationEntity);
        //如果作业过程没有数据则返回空对象
        if (CollectionUtils.isEmpty(durationInfos)) {
            return new DurationStatistics();
        }
        //过滤出无电的项目
        List<DurationInfo> noDurationInfoList = durationInfos.stream().filter(t -> t.getPowerState().equals("2")).collect(Collectors.toList());
        //过滤出有电的项目
        List<DurationInfo> durationInfoList = durationInfos.stream().filter(t -> t.getPowerState().equals("1")).collect(Collectors.toList());
        List<String> itemCodes = durationInfos.stream().map(DurationInfo::getItemCode).collect(Collectors.toList());
        List<String> trainsetIds = durationInfos.stream().map(DurationInfo::getTrainsetId).distinct().collect(Collectors.toList());

        List<RfidCardSummary> rfidCardSummaries = MybatisPlusUtils.selectList(
            rfidCardSummaryService,
            inParam(RfidCardSummary::getItemCode, itemCodes),
            eqParam(RfidCardSummary::getRepairType, "0")
        );
        //如果作业时间没有数据则返回空对象
        if (CollectionUtils.isEmpty(rfidCardSummaries)) {
            return new DurationStatistics();
        }
        //列数
        int column = 0;
        //车组数
        int trainsetCount = 0;
        for (String trainsetId : trainsetIds) {
            TrainsetInfo trainsetDetialInfo = remoteService.getTrainsetDetialInfo(trainsetId);
            if ("8".compareTo(trainsetDetialInfo.getIMarshalcount()) <= 0) {
                column++;
            } else {
                column = column + 2;
            }
            trainsetCount++;
        }

        //有任务的班次数量
        long dayPlanIdCount = durationInfos.stream().map(DurationInfo::getDayPlanId).distinct().count();

        //获取总时长
        int totalDuration = getTotalDuration(durationInfos, rfidCardSummaries);
        //平均总时长
        long averageTotalDuration = totalDuration / dayPlanIdCount;
        //有电总时长
        int yesDuration = getTotalDuration(durationInfoList, rfidCardSummaries);
        //平均有电时长
        long averageYesDuration = yesDuration / dayPlanIdCount;
        //无电总时长
        int noDuration = getTotalDuration(noDurationInfoList, rfidCardSummaries);
        //平均无电时长
        long averageNoDuration = noDuration / dayPlanIdCount;

        List<String> timeList = DateUtils.getBetweenDate(startDate, endDate);
        List<DayPlanDuration> dayPlanDurations = new ArrayList<>();
        for (String time : timeList) {
            String whiteDayPlanId = time + "-00";
            String nightDayPlanId = time + "-01";
            //当前白班班次有电总时长
            int yesWhiteDuration = getDayPlanDuration(rfidCardSummaries, durationInfoList, whiteDayPlanId);
            //当前白班班次无电总时长
            int noWhiteDuration = getDayPlanDuration(rfidCardSummaries, noDurationInfoList, whiteDayPlanId);
            //当前夜班班次有电总时长
            int yesNightDuration = getDayPlanDuration(rfidCardSummaries, durationInfoList, nightDayPlanId);
            //当前夜班班次无电总时长
            int noNightDuration = getDayPlanDuration(rfidCardSummaries, noDurationInfoList, nightDayPlanId);
            //白班日计划时间信息
            DayPlanDuration dayPlanWhiteDuration = getDayPlanDuration(whiteDayPlanId, yesWhiteDuration, noWhiteDuration);
            //夜班日计划时间信息
            DayPlanDuration dayPlanNightDuration = getDayPlanDuration(nightDayPlanId, yesNightDuration, noNightDuration);
            dayPlanDurations.add(dayPlanWhiteDuration);
            dayPlanDurations.add(dayPlanNightDuration);
        }

        //获取项目信息
        List<ItemDuration> itemDurations = new ArrayList<>();
        for (DurationInfo durationInfo : durationInfos) {
            //当前项目总时长
            int itemTotalDuration = 0;
            //根据项目code获取时间
            List<RfidCardSummary> rfidCardSummaryList = rfidCardSummaries.stream().filter(rfidCardSummary -> rfidCardSummary.getItemCode().equals(durationInfo.getItemCode())
                && rfidCardSummary.getTrainsetId().equals(durationInfo.getTrainsetId())
                && rfidCardSummary.getDayPlanId().equals(durationInfo.getDayPlanId())).collect(Collectors.toList());
            //当前项目总时长
            itemTotalDuration = getDuration(rfidCardSummaryList, itemTotalDuration);
            //当前项目工作的人员
            Set<String> collect = rfidCardSummaryList.stream().map(RfidCardSummary::getStuffId).collect(Collectors.toSet());
            ItemDuration itemDuration = new ItemDuration();
            itemDuration.setItemName(durationInfo.getItemName());
            itemDuration.setItemCode(durationInfo.getItemCode());
            itemDuration.setTrainsetSubtype(durationInfo.getTrainsetSubtype());
            itemDuration.setTrainsetType(durationInfo.getTrainsetType());
            itemDuration.setItemTotalDuration(itemTotalDuration);
            itemDuration.setItemWorkCount(collect.size());
            itemDurations.add(itemDuration);
        }

        //设置有电车组最小最大时长
        TrainsetDuration yesTrainsetDuration = getTrainsetTreeMap(rfidCardSummaries, durationInfoList);
        //设置无电车组最小最大时长
        TrainsetDuration noTrainsetDuration = getTrainsetTreeMap(rfidCardSummaries, noDurationInfoList);
        //班次时长集合信息
        List<DurationInfo> durations = durationInfos.stream().distinct().collect(Collectors.toList());

        DurationStatistics durationStatistics = new DurationStatistics();
        durationStatistics.setColumn(column);
        durationStatistics.setTotalDuration(totalDuration);
        durationStatistics.setTrainsetCount(trainsetCount);
        durationStatistics.setAverageTotalDuration(averageTotalDuration);
        durationStatistics.setYesDuration(yesDuration);
        durationStatistics.setAverageYesDuration(averageYesDuration);
        durationStatistics.setNoDuration(noDuration);
        durationStatistics.setAverageNoDuration(averageNoDuration);
        durationStatistics.setYesTrainsetDuration(yesTrainsetDuration);
        durationStatistics.setNoTrainsetDuration(noTrainsetDuration);
        durationStatistics.setDayPlanDurations(dayPlanDurations);
        List<ItemDuration> itemDurationArrayList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(itemDurations)) {
            Map<String, List<ItemDuration>> collect = itemDurations.stream().collect(Collectors.groupingBy(ItemDuration::getItemCode));
            for (String itemCode : collect.keySet()) {
                ItemDuration itemDuration = new ItemDuration();
                itemDuration.setItemCode(itemCode);
                itemDuration.setItemName(collect.get(itemCode).get(0).getItemName());
                int itemTotalDuration = collect.get(itemCode).stream().mapToInt(ItemDuration::getItemTotalDuration).sum();
                itemDuration.setItemTotalDuration(itemTotalDuration);
                int workCount = collect.get(itemCode).stream().mapToInt(ItemDuration::getItemWorkCount).sum();
                //项目平均时长
                int itemAverageDuration = itemTotalDuration / workCount;
                itemDuration.setItemAverageDuration(itemAverageDuration);
                itemDuration.setTrainsetSubtype(collect.get(itemCode).get(0).getTrainsetSubtype());
                itemDuration.setTrainsetType(collect.get(itemCode).get(0).getTrainsetType());
                itemDurationArrayList.add(itemDuration);
            }

        }
        durationStatistics.setItemDurations(itemDurationArrayList);
        durationStatistics.setDurations(durations);
        return durationStatistics;
    }

    @Override
    public Page<DurationDetail> getDurationDetail(String trainsetType, String trainsetId, String dayPlanId, Integer pageSize, Integer pageNum) {
        Page<DurationDetail> durationDetailPage = rfidCardSummaryService.selectWorkerDetail(trainsetType, trainsetId, dayPlanId, pageNum, pageSize);
        List<DurationDetail> records = durationDetailPage.getRecords();
        Consumer<DurationDetail> durationDetailConsumer = durationDetail -> {
            Duration duration = Duration.between(DateTimeUtil.asLocalDateTime(durationDetail.getStartTime()), DateTimeUtil.asLocalDateTime(durationDetail.getEndTime()));
            long seconds = duration.getSeconds();
            long minutes = seconds / 60;
            long m = seconds % 60;
            if (m > 0) {
                minutes++;
            }
            durationDetail.setDuration(minutes);
        };
        records.forEach(durationDetailConsumer);
        return durationDetailPage;
    }

    @Override
    public WarningStatistics getWorkWarning(DurationEntity durationEntity) {
        List<ColumnParam<WorkWorning>> columnParamList = new ArrayList<>();

        long startDate = durationEntity.getStartTime();
        long endDate = durationEntity.getEndTime();
        columnParamList.add(betweenParam(WorkWorning::getCreateTime, new Date(startDate), new Date(endDate)));
        if (StringUtils.isNotBlank(durationEntity.getTrainsetType())) {
            columnParamList.add(eqParam(WorkWorning::getTrainsetType, durationEntity.getTrainsetType()));
        }
        if (StringUtils.isNotBlank(durationEntity.getUnitCode())) {
            columnParamList.add(eqParam(WorkWorning::getUnitCode, durationEntity.getUnitCode()));
        }
        if (!CollectionUtils.isEmpty(durationEntity.getTrainsetNames())) {
            columnParamList.add(inParam(WorkWorning::getTrainsetName, durationEntity.getTrainsetNames()));
        }
        if (StringUtils.isNotBlank(durationEntity.getItemName())) {
            columnParamList.add(likeIgnoreCaseParam(WorkWorning::getItemName, durationEntity.getItemName()));
        }
        if (!CollectionUtils.isEmpty(durationEntity.getDeptNames())) {
            columnParamList.add(inParam(WorkWorning::getDeptName, durationEntity.getDeptNames()));
        }
        List<WorkWorning> workWornings = MybatisPlusUtils.selectList(
            workWorningService,
            columnParamList
        );
        //如果作业预警没有数据则返回空对象
        if (CollectionUtils.isEmpty(workWornings)) {
            return new WarningStatistics();
        }
        //统计个人
        Map<String, List<WorkWorning>> workNameMap = workWornings.stream().collect(Collectors.groupingBy(WorkWorning::getWorkerName));
        List<WorkDeptWarning> work = new ArrayList<>();
        for (Map.Entry<String, List<WorkWorning>> stringListEntry : workNameMap.entrySet()) {
            WorkDeptWarning workDeptWarning = new WorkDeptWarning();
            workDeptWarning.setCount(stringListEntry.getValue().size());
            workDeptWarning.setWorkName(stringListEntry.getKey());
            workDeptWarning.setDeptName(stringListEntry.getValue().get(0).getDeptName());
            work.add(workDeptWarning);
        }
        //统计班组
        Map<String, List<WorkWorning>> deptNameMap = workWornings.stream().collect(Collectors.groupingBy(WorkWorning::getDeptName));
        List<WorkDeptWarning> dept = new ArrayList<>();
        for (Map.Entry<String, List<WorkWorning>> stringListEntry : deptNameMap.entrySet()) {
            WorkDeptWarning workDeptWarning = new WorkDeptWarning();
            workDeptWarning.setCount(stringListEntry.getValue().size());
            workDeptWarning.setDeptName(stringListEntry.getKey());
            dept.add(workDeptWarning);
        }
        Page<WorkWorning> page = CommonUtils.getPage(workWornings, durationEntity.getPage(), durationEntity.getLimit());
        List<WorkWorning> records = page.getRecords();
        int total = page.getTotal();
        WarningStatistics warningStatistics = new WarningStatistics();
        warningStatistics.setTotal(total);
        warningStatistics.setWorkDept(dept);
        warningStatistics.setWorkPerson(work);
        warningStatistics.setWorkWornings(records);
        return warningStatistics;
    }

    /**
     * 根据车组取最大时长和最小时长
     *
     * @param rfidCardSummaries 作业时长
     * @param durationInfoList  车组项目
     * @return 最大时长和最小时长
     */
    private TrainsetDuration getTrainsetTreeMap(List<RfidCardSummary> rfidCardSummaries, List<DurationInfo> durationInfoList) {
        Map<String, List<DurationInfo>> durationInfoMap = durationInfoList.stream().collect(Collectors.groupingBy(d -> d.getTrainsetName() + "," + d.getDayPlanId()));
        TreeMap<Integer, String> treeMap = new TreeMap<>();
        for (String trainsetNameAndDayPlanId : durationInfoMap.keySet()) {
            int trainsetDuration = getTotalDuration(durationInfoMap.get(trainsetNameAndDayPlanId), rfidCardSummaries);
            treeMap.put(trainsetDuration, trainsetNameAndDayPlanId);
        }
        TrainsetDuration trainsetDuration = new TrainsetDuration();
        if (!CollectionUtils.isEmpty(treeMap)) {
            Map.Entry<Integer, String> noMinTrainset = treeMap.firstEntry();
            Integer minTrainsetDuration = noMinTrainset.getKey();
            String minTrainsetName = noMinTrainset.getValue();
            String[] minTrainset = StringUtils.commaDelimitedListToStringArray(minTrainsetName);
            Map.Entry<Integer, String> noMaxTrainset = treeMap.lastEntry();
            Integer maxTrainsetDuration = noMaxTrainset.getKey();
            String maxTrainsetName = noMaxTrainset.getValue();
            String[] maxTrainset = StringUtils.commaDelimitedListToStringArray(maxTrainsetName);
            trainsetDuration.setMinTrainsetDuration(minTrainsetDuration);
            trainsetDuration.setMinTrainsetName(minTrainset[0]);
            trainsetDuration.setMinDayPlanId(minTrainset[1]);
            trainsetDuration.setMaxTrainsetDuration(maxTrainsetDuration);
            trainsetDuration.setMaxTrainsetName(maxTrainset[0]);
            trainsetDuration.setMaxDayPlanId(maxTrainset[1]);
        }

        return trainsetDuration;

    }

    /**
     * 封装日计划信息
     *
     * @param dayPlanId   日计划id
     * @param yesDuration 有电时长
     * @param noDuration  无电时长
     * @return 日计划信息
     */
    private DayPlanDuration getDayPlanDuration(String dayPlanId, int yesDuration, int noDuration) {
        DayPlanDuration dayPlanDuration = new DayPlanDuration();
        dayPlanDuration.setDayPlanId(dayPlanId);
        dayPlanDuration.setYesDuration(yesDuration);
        dayPlanDuration.setNoDuration(noDuration);
        return dayPlanDuration;
    }

    /**
     * 根据日计划获取有电无电时长
     *
     * @param rfidCardSummaries 作业时长
     * @param durationInfoList  有电/无电项目
     * @param dayPlanId         日计划id
     * @return 有电无电时长
     */
    private int getDayPlanDuration(List<RfidCardSummary> rfidCardSummaries, List<DurationInfo> durationInfoList, String dayPlanId) {
        //获取当前班次有电/无电所有项目信息
        List<DurationInfo> dayPlanDurationInfos = durationInfoList.stream().filter(durationInfo -> durationInfo.getDayPlanId().equals(dayPlanId)).collect(Collectors.toList());
        //如果当前班次没有项目则返回0
        if (CollectionUtils.isEmpty(dayPlanDurationInfos)) {
            return 0;
        }
        //计算当前班次有电/无电总时长
        return getTotalDuration(dayPlanDurationInfos, rfidCardSummaries);
    }

    /**
     * 计算作业时长
     *
     * @param durationInfos     作业过程项目信息
     * @param rfidCardSummaries 时间信息
     * @return 作业时长
     */
    private int getTotalDuration(List<DurationInfo> durationInfos, List<RfidCardSummary> rfidCardSummaries) {
        int duration = 0;
        for (DurationInfo durationInfo : durationInfos) {
            //根据项目code获取时间
            List<RfidCardSummary> rfidCardSummaryList = rfidCardSummaries.stream().filter(rfidCardSummary -> rfidCardSummary.getItemCode().equals(durationInfo.getItemCode())
                && rfidCardSummary.getTrainsetId().equals(durationInfo.getTrainsetId())
                && rfidCardSummary.getDayPlanId().equals(durationInfo.getDayPlanId())
            ).collect(Collectors.toList());
            duration = getDuration(rfidCardSummaryList, duration);
        }
        return duration;
    }

    /**
     * 获取当前项目的总时长
     *
     * @param rfidCardSummaryList 作业时长
     * @param duration            时长总数
     * @return 项目总时长
     */
    private int getDuration(List<RfidCardSummary> rfidCardSummaryList, int duration) {

        //根据人对时间分组
        Map<String, List<RfidCardSummary>> rfidCardSummaryMap = rfidCardSummaryList.stream().collect(Collectors.groupingBy(RfidCardSummary::getStuffId));
        //循环的处理每个人的时长
        for (String key : rfidCardSummaryMap.keySet()) {
            List<RfidCardSummary> cardSummaryList = rfidCardSummaryMap.get(key);
            //获取开始时间和结束时间
            Date beginDate = new Date();
            Date endDate = new Date();
            for (RfidCardSummary rfidCardSummary : cardSummaryList) {
                if (rfidCardSummary.getTimeTag().equals("1")) {
                    beginDate = rfidCardSummary.getRepairTime();
                } else if (rfidCardSummary.getTimeTag().equals("4")) {
                    endDate = rfidCardSummary.getRepairTime();
                }
            }
            //获取分钟数
            Duration between = Duration.between(DateTimeUtil.asLocalDateTime(beginDate), DateTimeUtil.asLocalDateTime(endDate));
            long seconds = between.getSeconds();
            long minutes = seconds / 60;
            long m = seconds % 60;
            if (m > 0) {
                minutes++;
            }
            duration += minutes;
        }
        return duration;
    }
}
