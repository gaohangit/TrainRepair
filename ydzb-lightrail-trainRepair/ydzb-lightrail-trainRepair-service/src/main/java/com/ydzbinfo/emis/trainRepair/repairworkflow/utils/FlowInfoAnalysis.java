package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.*;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeVector;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseNode;
import com.ydzbinfo.emis.utils.CommonUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaohan
 * @description
 * @createDate 2021/6/9 10:31
 **/
public class FlowInfoAnalysis {

    private final FlowInfo flowInfo;

    private final Map<String, List<NodeVector>> groupedNodeVectorsByFromNodeId;
    private final Map<String, List<NodeVector>> groupedNodeVectorsByToNodeId;
    private final Map<String, NodeInfo> nodeMap;

    /**
     * 节点id -> 成对节点信息 映射
     */
    private final Map<String, PairNodeInfo> nodeIdPairNodeInfoMap;

    /**
     * 成对节点信息id -> 成对节点信息 映射
     */
    private final Map<String, PairNodeInfo> pairNodeInfoMap;

    private Map<String, Integer> pairNodeIdIndexMap;

    /**
     * 并行区段id -> 并行区段分析对象
     */
    private final Map<String, FlowSectionInfo> flowSectionInfoMap;

    /**
     * 节点id -> 并行区段对象
     */
    private final Map<String, FlowParallelSection> nodeParallelSectionMap;

    /**
     * 并行区段id -> 并行区段对象
     */
    private final Map<String, FlowParallelSection> flowParallelSectionMap;

    public FlowInfoAnalysis(FlowInfo flowInfo) {
        this.flowInfo = flowInfo;
        this.groupedNodeVectorsByFromNodeId = Collections.unmodifiableMap(flowInfo.getNodeVectors().stream().collect(Collectors.groupingBy(NodeVector::getFromNodeId)));
        this.groupedNodeVectorsByToNodeId = Collections.unmodifiableMap(flowInfo.getNodeVectors().stream().collect(Collectors.groupingBy(NodeVector::getToNodeId)));
        this.nodeMap = Collections.unmodifiableMap(CommonUtils.collectionToMap(flowInfo.getNodes(), BaseNode::getId));

        Map<String, FlowSectionInfo> flowSectionInfoMap = new HashMap<>();
        Map<String, FlowParallelSection> nodeParallelSectionMap = new HashMap<>();
        Map<String, FlowParallelSection> flowParallelSectionMap = new HashMap<>();
        if (flowInfo.getParallelSections() != null) {
            flowInfo.getParallelSections().forEach(parallelSection -> {
                flowSectionInfoMap.put(parallelSection.getId(), new FlowSectionInfo(parallelSection));
                for (String nodeId : parallelSection.getNodeIds()) {
                    nodeParallelSectionMap.put(nodeId, parallelSection);
                }
                flowParallelSectionMap.put(parallelSection.getId(), parallelSection);
            });
        }
        this.flowSectionInfoMap = Collections.unmodifiableMap(flowSectionInfoMap);
        this.nodeParallelSectionMap = Collections.unmodifiableMap(nodeParallelSectionMap);
        this.flowParallelSectionMap = Collections.unmodifiableMap(flowParallelSectionMap);

        Map<String, PairNodeInfo> nodeIdPairNodeInfoMap = new HashMap<>();
        Map<String, PairNodeInfo> pairNodeInfoMap = new HashMap<>();
        if (flowInfo.getPairNodeInfo() != null) {
            for (PairNodeInfo pairNodeInfo : flowInfo.getPairNodeInfo()) {
                pairNodeInfoMap.put(pairNodeInfo.getId(), pairNodeInfo);
                for (String nodeId : pairNodeInfo.getNodeIds()) {
                    nodeIdPairNodeInfoMap.put(nodeId, pairNodeInfo);
                }
            }
        }
        this.nodeIdPairNodeInfoMap = Collections.unmodifiableMap(nodeIdPairNodeInfoMap);
        this.pairNodeInfoMap = Collections.unmodifiableMap(pairNodeInfoMap);
    }

    public FlowInfo getFlowInfo() {
        return flowInfo;
    }

    public Map<String, NodeInfo> getNodeMap() {
        return nodeMap;
    }

    public Map<String, FlowParallelSection> getFlowParallelSectionMap() {
        return flowParallelSectionMap;
    }

    public boolean isPairNode(String nodeId) {
        return nodeIdPairNodeInfoMap.containsKey(nodeId);
    }

    /**
     * 获取成对节点的排序值
     *
     * @param nodeId 成对节点id
     * @return index值（0、1、null）
     */
    public Integer getPairNodeIndexForSort(String nodeId) {
        return getPairNodeIdIndexMap().get(nodeId);
    }

    public FlowSectionInfo getAnalyzedFlowSectionInfo(String parallelSectionId) {
        return flowSectionInfoMap.get(parallelSectionId);
    }

    /**
     * @return 成对节点id -> 指定成对节点 在 两个成对节点按实际流程排序后的列表 中的index值(目前仅可能为0或1)
     */
    public Map<String, Integer> getPairNodeIdIndexMap() {
        if (pairNodeIdIndexMap == null) {
            Map<String, Integer> pairNodeIdIndexMap = new HashMap<>();
            for (PairNodeInfo pairNodeInfo : getFlowInfo().getPairNodeInfo()) {
                FlowSectionInfo flowSectionInfo;
                FlowParallelSection flowParallelSection = nodeParallelSectionMap.get(pairNodeInfo.getNodeIds()[0]);
                if (flowParallelSection != null) {
                    flowSectionInfo = flowSectionInfoMap.get(flowParallelSection.getId());
                } else {
                    flowSectionInfo = getTrunkSectionInfo();
                }

                int oneNodeIdIndex = flowSectionInfo.getSortedNodeIds().indexOf(pairNodeInfo.getNodeIds()[0]);
                int anotherNodeIdIndex = flowSectionInfo.getSortedNodeIds().indexOf(pairNodeInfo.getNodeIds()[1]);
                if (oneNodeIdIndex < anotherNodeIdIndex) {
                    oneNodeIdIndex = 0;
                    anotherNodeIdIndex = 1;
                } else {
                    oneNodeIdIndex = 1;
                    anotherNodeIdIndex = 0;
                }
                pairNodeIdIndexMap.put(pairNodeInfo.getNodeIds()[0], oneNodeIdIndex);
                pairNodeIdIndexMap.put(pairNodeInfo.getNodeIds()[1], anotherNodeIdIndex);
            }
            this.pairNodeIdIndexMap = Collections.unmodifiableMap(pairNodeIdIndexMap);
        }
        return pairNodeIdIndexMap;
    }

    public PairNodeInfo getPairNodeInfoByNodeId(String nodeId) {
        return nodeIdPairNodeInfoMap.get(nodeId);
    }

    public PairNodeInfo getPairNodeInfoByInfoId(String pairNodeInfoId) {
        return pairNodeInfoMap.get(pairNodeInfoId);
    }

    public boolean hasNextNode(String nodeId) {
        return groupedNodeVectorsByFromNodeId.containsKey(nodeId);
    }

    public List<NodeInfo> getNextNodes(String nodeId) {
        List<NodeVector> nodeVectorList = groupedNodeVectorsByFromNodeId.get(nodeId);
        if (nodeVectorList == null) {
            return new ArrayList<>();
        } else {
            return nodeVectorList.stream().map(v -> this.nodeMap.get(v.getToNodeId())).collect(Collectors.toList());
        }
    }

    public boolean hasPreviousNode(String nodeId) {
        return groupedNodeVectorsByToNodeId.containsKey(nodeId);
    }

    public List<NodeInfo> getPreviousNodes(String nodeId) {
        List<NodeVector> nodeVectorList = groupedNodeVectorsByToNodeId.get(nodeId);
        if (nodeVectorList == null) {
            return new ArrayList<>();
        } else {
            return nodeVectorList.stream().map(v -> this.nodeMap.get(v.getFromNodeId())).collect(Collectors.toList());
        }
    }


    public boolean isInSomeParallelSection(String nodeId) {
        return nodeParallelSectionMap.containsKey(nodeId);
    }

    public FlowParallelSection getFlowParallelSectionByNodeId(String nodeId) {
        return nodeParallelSectionMap.get(nodeId);
    }

    /**
     * 主干区段信息对象
     */
    private FlowSectionInfo trunkSectionInfo;

    public FlowSectionInfo getTrunkSectionInfo() {
        if (trunkSectionInfo == null) {
            IFlowSection trunkParallelSection = new IFlowSection() {
                private final String id = getFlowInfo().getId() + "_trunk";
                private final String[] nodeIds = getFlowInfo().getNodes().stream()
                    .filter(v -> !isInSomeParallelSection(v.getId()))
                    .map(BaseNode::getId).toArray(String[]::new);

                @Override
                public String getId() {
                    return id;
                }

                @Override
                public String[] getNodeIds() {
                    return nodeIds;
                }
            };
            trunkSectionInfo = new FlowSectionInfo(trunkParallelSection);
        }
        return trunkSectionInfo;
    }

    /**
     * 起始节点id -> 并行区段对象
     */
    private Map<String, List<FlowSectionInfo>> startNodeFlowSectionInfoListMap;


    private Map<String, List<FlowSectionInfo>> getStartNodeFlowSectionInfoListMap() {
        if (startNodeFlowSectionInfoListMap == null) {
            Map<String, List<FlowSectionInfo>> startNodeFlowSectionInfoListMap = new HashMap<>();
            for (FlowSectionInfo flowSectionInfo : flowSectionInfoMap.values()) {
                NodeInfo node = flowSectionInfo.getFromStartNode();
                String startNodeId = node == null ? null : node.getId();
                if (!startNodeFlowSectionInfoListMap.containsKey(startNodeId)) {
                    startNodeFlowSectionInfoListMap.put(startNodeId, new ArrayList<>());
                }
                startNodeFlowSectionInfoListMap.get(startNodeId).add(flowSectionInfo);
            }
            this.startNodeFlowSectionInfoListMap = Collections.unmodifiableMap(startNodeFlowSectionInfoListMap);
        }
        return startNodeFlowSectionInfoListMap;
    }

    /**
     * 根据前起始节点获取并行区段
     */
    public List<FlowSectionInfo> getSectionInfoListByStartNodeId(String startNodeId) {
        List<FlowSectionInfo> flowSectionInfoList = getStartNodeFlowSectionInfoListMap().get(startNodeId);
        return flowSectionInfoList != null ? new ArrayList<>(flowSectionInfoList) : new ArrayList<>();
    }


    /**
     * 起始节点id -> 并行区段对象
     */
    private Map<String, List<FlowSectionInfo>> endNodeFlowSectionInfoListMap;


    private Map<String, List<FlowSectionInfo>> getEndNodeFlowSectionInfoListMap() {
        if (endNodeFlowSectionInfoListMap == null) {
            Map<String, List<FlowSectionInfo>> endNodeFlowSectionInfoListMap = new HashMap<>();
            for (FlowSectionInfo flowSectionInfo : flowSectionInfoMap.values()) {
                NodeInfo node = flowSectionInfo.getToEndNode();
                String endNodeId = node == null ? null : node.getId();
                if (!endNodeFlowSectionInfoListMap.containsKey(endNodeId)) {
                    endNodeFlowSectionInfoListMap.put(endNodeId, new ArrayList<>());
                }
                endNodeFlowSectionInfoListMap.get(endNodeId).add(flowSectionInfo);
            }
            this.endNodeFlowSectionInfoListMap = Collections.unmodifiableMap(endNodeFlowSectionInfoListMap);
        }
        return endNodeFlowSectionInfoListMap;
    }

    /**
     * 根据后结束节点获取并行区段
     */
    public List<FlowSectionInfo> getSectionInfoListByEndNodeId(String endNodeId) {
        List<FlowSectionInfo> flowSectionInfoList = getEndNodeFlowSectionInfoListMap().get(endNodeId);
        return flowSectionInfoList != null ? new ArrayList<>(flowSectionInfoList) : new ArrayList<>();
    }

    /**
     * 分析了的区段信息封装类
     */
    public class FlowSectionInfo {
        private final IFlowSection flowSection;
        private Map<String, NodeInfo> sectionNodeMap;

        private List<String> sortedNodeIds;
        private Map<String, Integer> nodeIndexMap;

        public FlowSectionInfo(IFlowSection flowSection) {
            this.flowSection = flowSection;
        }

        /**
         * 获取原始区段信息
         */
        public IFlowSection getFlowSection() {
            return flowSection;
        }

        private Map<String, NodeInfo> getSectionNodeMap() {
            if (sectionNodeMap == null) {
                sectionNodeMap = new HashMap<>();
                if (flowSection != null) {
                    for (String nodeId : flowSection.getNodeIds()) {
                        sectionNodeMap.put(nodeId, nodeMap.get(nodeId));
                    }
                }
            }
            return sectionNodeMap;
        }

        /**
         * 判断节点是否在并行区段上
         */
        public boolean sectionHasNode(String nodeId) {
            return getSectionNodeMap().containsKey(nodeId);
        }

        private void initNodeIdListInfo() {
            sortedNodeIds = new ArrayList<>();
            nodeIndexMap = new HashMap<>();
            if (flowSection != null) {
                String firstNodeId = CommonUtils.find(flowSection.getNodeIds(), v -> {
                    List<NodeInfo> nodes = getPreviousNodes(v);
                    return nodes.size() == 0 || !sectionHasNode(nodes.get(0).getId());
                });
                int index = 0;
                sortedNodeIds.add(firstNodeId);
                nodeIndexMap.put(firstNodeId, index++);
                String nextNodeId = getNextNodeId(firstNodeId);
                while (nextNodeId != null) {
                    sortedNodeIds.add(nextNodeId);
                    nodeIndexMap.put(nextNodeId, index++);
                    nextNodeId = getNextNodeId(nextNodeId);
                }
            }
        }

        /**
         * 获取当前节点在并行区段的位置
         */
        public int getNodeIndex(String nodeId) {
            if (nodeIndexMap == null) {
                initNodeIdListInfo();
            }
            return Objects.requireNonNull(nodeIndexMap.get(nodeId), "节点不在当前并行区段上");
        }

        /**
         * 获取当前并行区段按照节点向量排序了的节点列表
         */
        public List<String> getSortedNodeIds() {
            if (sortedNodeIds == null) {
                initNodeIdListInfo();
            }
            return sortedNodeIds;
        }

        /**
         * 获取节点在当前并行区段上的前一个节点
         */
        public String getNextNodeId(String nodeId) {
            List<NodeInfo> nodeInfoList = getNextNodes(nodeId);
            if (nodeInfoList.size() > 0) {
                NodeInfo nodeInfo = CommonUtils.find(nodeInfoList, v -> sectionHasNode(v.getId()));
                return nodeInfo == null ? null : nodeInfo.getId();
            } else {
                return null;
            }
        }

        /**
         * 获取节点在当前并行区段上的前一个节点
         */
        public String getPreviousNodeId(String nodeId) {
            List<NodeInfo> nodeInfoList = getPreviousNodes(nodeId);
            if (nodeInfoList.size() > 0) {
                NodeInfo nodeInfo = CommonUtils.find(nodeInfoList, v -> sectionHasNode(v.getId()));
                return nodeInfo == null ? null : nodeInfo.getId();
            } else {
                return null;
            }
        }

        /**
         * 获取并行区段前一个在主干上的节点，为null意味着当前节点为头部节点
         */
        public NodeInfo getFromStartNode() {
            List<NodeInfo> nodeInfoList = getPreviousNodes(this.getSortedNodeIds().get(0));
            if (nodeInfoList != null && nodeInfoList.size() > 0) {
                return nodeInfoList.get(0);
            } else {
                return null;
            }
        }

        /**
         * 获取并行区段后一个在主干上的节点，为null意味着当前节点为尾部节点
         */
        public NodeInfo getToEndNode() {
            List<NodeInfo> nodeInfoList = getNextNodes(this.getSortedNodeIds().get(this.getSortedNodeIds().size() - 1));
            if (nodeInfoList != null && nodeInfoList.size() > 0) {
                return nodeInfoList.get(0);
            } else {
                return null;
            }
        }

        int getSize() {
            return flowSection != null ? flowSection.getNodeIds().length : this.getSortedNodeIds().size();
        }

    }
}
