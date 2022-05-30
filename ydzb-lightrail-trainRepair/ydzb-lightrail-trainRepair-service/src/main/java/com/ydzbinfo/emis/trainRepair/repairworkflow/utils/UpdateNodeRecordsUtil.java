package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.NodeRecordInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.QueryNodeRecord;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecord;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeRecordWithExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.INodeRecordService;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.Criteria;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.inParam;

/**
 * 用于集中更新节点记录的工具类
 *
 * @author 张天可
 * @since 2022/4/21
 */
public class UpdateNodeRecordsUtil {
    /**
     * 节点记录，初始化自数据库，不可变
     */
    private final List<NodeRecordInfo> initNodeRecords;
    /**
     * 实时流程运行的节点记录
     */
    private final List<NodeRecordInfo> currentNodeRecords;

    /**
     * 节点记录数据库操作服务
     */
    private final INodeRecordService nodeRecordService;

    private final List<NodeRecordInfo> inserts;
    private final List<NodeRecordInfo> deletes;
    /**
     * 曾经存在过的节点记录id
     */
    private final Set<String> existIds;
    /**
     * 是否已经持久化过
     */
    private boolean persistent = false;

    private void checkPersistent() {
        if (persistent) {
            throw new RuntimeException("已经持久化，无法进一步更新数据");
        }
    }

    private UpdateNodeRecordsUtil(INodeRecordService nodeRecordService, List<NodeRecordInfo> initNodeRecords) {
        this.nodeRecordService = nodeRecordService;
        this.currentNodeRecords = new LinkedList<>(initNodeRecords);
        this.initNodeRecords = Collections.unmodifiableList(initNodeRecords);
        this.existIds = initNodeRecords.stream().map(NodeRecord::getId).collect(Collectors.toSet());
        this.inserts = new LinkedList<>();
        this.deletes = new LinkedList<>();
    }

    public static UpdateNodeRecordsUtil newInstanceAndInitialData(INodeRecordService nodeRecordService, String flowRunId) {
        QueryNodeRecord queryNodeRecord = new QueryNodeRecord();
        queryNodeRecord.setFlowRunIds(new String[]{flowRunId});
        // 查询流程运行节点记录信息
        List<NodeRecordInfo> initNodeRecords = nodeRecordService.getNodeRecordInfoList(queryNodeRecord);
        return new UpdateNodeRecordsUtil(nodeRecordService, initNodeRecords);
    }

    public List<NodeRecordInfo> getCurrentNodeRecords() {
        return Collections.unmodifiableList(currentNodeRecords);
    }

    public List<NodeRecordInfo> getInitNodeRecords() {
        return initNodeRecords;
    }

    public void insert(NodeRecordInfo nodeRecordInfo) {
        checkPersistent();
        String id = nodeRecordInfo.getId();
        if (this.existIds.contains(id)) {
            throw new RuntimeException("禁止插入曾经存在过的数据");
        }
        this.inserts.add(nodeRecordInfo);
        this.currentNodeRecords.add(nodeRecordInfo);
        this.existIds.add(id);
    }

    private boolean nodeRecordEquals(NodeRecord a, NodeRecord b) {
        return Objects.equals(a.getId(), b.getId());
    }

    public void delete(Criteria<NodeRecord> param) {
        checkPersistent();
        Iterator<NodeRecordInfo> iterator = this.currentNodeRecords.iterator();
        while (iterator.hasNext()) {
            NodeRecordInfo next = iterator.next();
            if (param.test(next)) {
                iterator.remove();
                Predicate<NodeRecordInfo> eqCurrentPredicate = v -> nodeRecordEquals(v, next);
                this.inserts.removeIf(eqCurrentPredicate);
                if (CommonUtils.find(this.initNodeRecords, eqCurrentPredicate) != null) {
                    this.deletes.add(next);
                }
            }
        }
    }

    // public List<NodeRecordInfo> getInserts() {
    //     return inserts;
    // }

    // public List<NodeRecordInfo> getDeletes() {
    //     return deletes;
    // }

    public void setChangeToDatabase() {
        checkPersistent();
        if (inserts.size() > 0) {
            List<NodeRecordWithExtraInfo> nodeRecordWithExtraInfoList = inserts.stream().map(FlowUtil::convertTo).collect(Collectors.toList());
            nodeRecordService.batchInsertWithExtraInfo(nodeRecordWithExtraInfoList);
        }
        if (deletes.size() > 0) {
            MybatisPlusUtils.delete(
                nodeRecordService,
                inParam(NodeRecord::getId, deletes.stream().map(NodeRecord::getId).collect(Collectors.toList()))
            );
        }
        persistent = true;
    }
}
