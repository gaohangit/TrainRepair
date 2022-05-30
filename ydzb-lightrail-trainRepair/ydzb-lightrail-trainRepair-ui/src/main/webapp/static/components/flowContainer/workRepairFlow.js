(function () {
  let copyDataSimple = (data) => JSON.parse(JSON.stringify(data))
  let listToMap = (list, getKey) => {
    return list.reduce((pre, cur) => {
      pre[getKey(cur)] = cur
      return pre
    }, {})
  }

  let editableForEach = (array, callback) => {
    let index = 0
    let splice = (start, deleteCount, ...items) => {
      start = start > array.length - 1 ? array.length - 1 : start
      start = start < 0 ? 0 : start
      let deletes = array.splice(start, deleteCount, ...items)
      let actualDeleteCount = deletes.length

      let addItemCount = 0
      if (items && items.length > 0) {
        addItemCount = items.length
      }

      let cIndex = index

      if (actualDeleteCount > 0) {
        let end = start + actualDeleteCount - 1
        if (start <= index && index <= end) {
          cIndex = start - 1
        } else if (end < start) {
          cIndex -= actualDeleteCount
        }
      }
      if (addItemCount > 0) {
        if (start <= index) {
          cIndex += addItemCount
        }
      }
      index = cIndex
    }
    for (; index < array.length; index++) {
      const element = array[index]
      callback(element, index, splice)
    }
  }

  const virtualType = 'virtual'
  const workRepairFlowComponentConfig = {
    name: 'WorkRepairFlow',
    props: {
      data: {
        type: Object,
        default: () => ({}),
      },
      nowFlow: {
        type: Object,
        default: () => ({}),
      },
      moveNodeStatus: {
        type: Boolean,
        default: false,
      },
      rolemap: {
        type: Object,
        default: () => ({}),
      },
      handleStatus: {
        type: Boolean,
        default: false,
      },
      tooltipShow: {
        type: Boolean,
        default: true,
      },
    },
    data() {
      return {
        startNode: {
          id: 'start',
          name: '开始',
          couldDispose: true,
          finished: true,
        },
        nodesContext: {
          nodeShowConfig: {
            height: 80,
            width: 200,
            xGap: 140,
            yGap: 60,
          },
        },
        originalPoint: {
          // 影响绘制的起始位置、整体实际宽度和高度
          x: 0,
          y: 0,
        },
        flowHeight: 0,
        flowWidth: 0,
        highlightMap: {},
      }
    },
    watch: {},
    computed: {
      // 流程图因为成对节点连接显示而额外增加的的偏移
      flowWrapperMarginLeft() {
        let marginLeft = 0
        for (const key in this.pairNodeConnectOffsetXMap) {
          const offsetX = this.pairNodeConnectOffsetXMap[key]
          let cur = offsetX + 30
          if (cur > marginLeft) {
            marginLeft = cur
          }
        }
        return marginLeft
      },
      // 成对节点连接偏移map
      pairNodeConnectOffsetXMap() {
        let map = {}

        let getPairNodeStartIndex = (pairNodeItemId) => {
          let pairNodeItem = this.pairNodeItemMap[pairNodeItemId]
          return this.trunkFlowNodeIndexMap[pairNodeItem.nodeIds[0]]
        }

        let getPairNodeEndIndex = (pairNodeItemId) => {
          let pairNodeItem = this.pairNodeItemMap[pairNodeItemId]
          return this.trunkFlowNodeIndexMap[pairNodeItem.nodeIds[1]]
        }
        let getOffsetIndex = (pairNodeItemId) => {
          if (map[pairNodeItemId] != null) {
            return map[pairNodeItemId]
          }
          let startIndex = getPairNodeStartIndex(pairNodeItemId)
          let endIndex = getPairNodeEndIndex(pairNodeItemId)

          let innerNearPairNodeIndexArray = []

          for (let index = startIndex + 1; index < endIndex; index++) {
            const trunkFlowNodeId = this.trunkFlowNodeIdListSorted[index]
            const currentPairNodeItemId = this.pairNodeIdMap[trunkFlowNodeId]
            // 如果成对节点信息存在，将其offsetIndex获取出来，跳过这个成对节点
            if (currentPairNodeItemId) {
              innerNearPairNodeIndexArray.push(getOffsetIndex(currentPairNodeItemId))
              index = getPairNodeEndIndex(currentPairNodeItemId)
            }
          }

          // 如果内部存在其他成对节点，则为其中最大的加1，否则为0
          let offsetIndex = innerNearPairNodeIndexArray.length > 0 ? Math.max(...innerNearPairNodeIndexArray) + 1 : 0
          map[pairNodeItemId] = offsetIndex
          return offsetIndex
        }
        const baseOffset = 50
        const extraStepOffset = 30
        return this.pairNodeInfoSorted.reduce((pre, cur) => {
          pre[cur.id] = baseOffset + extraStepOffset * getOffsetIndex(cur.id)
          return pre
        }, {})
      },
      endNode({ originalEndNodes }) {
        if (originalEndNodes.every((node) => node.finished)) {
          return {
            id: 'end',
            name: '结束',
            couldDispose: true,
            finished: true,
          }
        } else {
          return {
            id: 'end',
            name: '结束',
            couldDispose: false,
            finished: false,
          }
        }
      },
      // [{id:1,nodeIds:[1,2]},{id:2,nodeIds:[3,4]}]\
      pairNodeInfoSorted({ data, trunkFlowNodeIdListSorted }) {
        if (!data.pairNodeInfo) {
          return []
        } else {
          let pairNodeInfo = copyDataSimple(data.pairNodeInfo)
          return pairNodeInfo.map((item) => {
            item.nodeIds = item.nodeIds.sort(
              (a, b) =>
                trunkFlowNodeIdListSorted.findIndex((nodeId) => nodeId == a) -
                trunkFlowNodeIdListSorted.findIndex((nodeId) => nodeId == b)
            )
            return item
          })
        }
      },
      pairNodeEndIds({ pairNodeInfoSorted }) {
        return pairNodeInfoSorted.map((item) => {
          return item.nodeIds[item.nodeIds.length - 1]
        })
      },
      // 节点id-》成对信息id
      pairNodeIdMap() {
        return this.pairNodeInfoSorted.reduce((prev, item) => {
          item.nodeIds.forEach((nodeId) => {
            prev[nodeId] = item.id
          })
          return prev
        }, {})
      },
      // 成对节点id -> 成对节点信息
      pairNodeItemMap() {
        return listToMap(this.pairNodeInfoSorted, v => v.id)
      },
      // 节点-》与他成对的节点id
      pairNodeIdRelationMap({ pairNodeInfoSorted }) {
        return pairNodeInfoSorted.reduce((prev, item) => {
          item.nodeIds.forEach((nodeId, index) => {
            prev[nodeId] = item.nodeIds[1 - index]
          })
          return prev
        }, {})
      },
      virtualNodes() {
        return [
          {
            ...this.startNode,
            type: virtualType,
          },
          {
            ...this.endNode,
            type: virtualType,
          },
        ]
      },
      originalFromNodeVectorsMap({ data }) {
        return data.nodeVectors.reduce((prev, item) => {
          if (!prev[item.fromNodeId]) {
            prev[item.fromNodeId] = []
          }
          prev[item.fromNodeId].push(item)
          return prev
        }, {})
      },
      originalToNodeVectorsMap({ data }) {
        return data.nodeVectors.reduce((prev, item) => {
          if (!prev[item.toNodeId]) {
            prev[item.toNodeId] = []
          }
          prev[item.toNodeId].push(item)
          return prev
        }, {})
      },
      allNodes() {
        return copyDataSimple(this.data.nodes.concat(this.virtualNodes))
      },
      allNodesMap() {
        return listToMap(this.allNodes, (v) => v.id)
      },
      parallelFlowConfigMap({ parallelFlowConfigSorted }) {
        return listToMap(parallelFlowConfigSorted, (v) => v.id)
      },
      // 节点id -> 并行区段id
      // 用来获取节点所在的并行区段
      nodeParallelIdMap({ data }) {
        let map = {}
        data.parallelFlowConfigs.forEach((parallelFlowConfig) => {
          parallelFlowConfig.nodeIds.forEach((nodeId) => {
            map[nodeId] = parallelFlowConfig.id
          })
        })
        return map
      },
      // 节点向量id -> 并行区段id
      // 用来获取节点向量所在的并行区段
      nodeVectorParallelIdMap() {
        let map = {}
        copyDataSimple(this.parallelFlowConfigSorted).forEach((parallelFlowConfig) => {
          let nodeIds = parallelFlowConfig.nodeIds
          //allFromNodeVectorsMap allToNodeVectorsMap
          nodeIds.forEach((nodeId) => {
            let nodeVector = this.allToNodeVectorsMap[nodeId][0]
            map[nodeVector.id] = parallelFlowConfig
          })
          let lastNodeId = nodeIds[nodeIds.length - 1]
          let lastNodeVector = this.allFromNodeVectorsMap[lastNodeId][0]
          map[lastNodeVector.id] = parallelFlowConfig
        })
        return map
      },
      // 实际开始节点
      originalStartNodes() {
        return copyDataSimple(this.data.nodes.filter((v) => !this.originalToNodeVectorsMap[v.id]))
      },
      // 实际结束节点
      originalEndNodes() {
        return copyDataSimple(this.data.nodes.filter((v) => !this.originalFromNodeVectorsMap[v.id]))
      },
      virtualNodeVectors() {
        if (this.originalStartNodes.length === 0) {
          return [
            {
              id: '00',
              fromNodeId: this.startNode.id,
              toNodeId: this.endNode.id,
            },
          ]
        } else {
          return [
            ...this.originalStartNodes.map((v) => ({
              id: '01_' + v.id,
              fromNodeId: this.startNode.id,
              toNodeId: v.id,
            })),
            ...this.originalEndNodes.map((v) => ({
              id: '02_' + v.id,
              fromNodeId: v.id,
              toNodeId: this.endNode.id,
            })),
          ]
        }
      },
      allNodeVectors() {
        return [...this.data.nodeVectors, ...this.virtualNodeVectors]
      },
      // 节点向量id -> 节点向量
      allNodeVectorsMap() {
        return listToMap(this.allNodeVectors, v => v.id)
      },
      parallelFlowNodeIds({ parallelFlowConfigSorted }) {
        // 并行区段的id数组
        return parallelFlowConfigSorted.map((parallel) => parallel.nodeIds).flat()
      },
      trunkFlowInfo({ allNodeVectors, allNodes }) {
        // 主干上的
        let nodeList = allNodes.filter((node) => this.isTrunkNode(node.id))
        let nodeVectors = allNodeVectors.filter(
          (vector) => this.isTrunkNode(vector.fromNodeId) && this.isTrunkNode(vector.toNodeId)
        )

        return {
          nodes: nodeList,
          nodeVectors,
        }
      },

      allFromNodeVectorsMap({ allNodeVectors }) {
        return allNodeVectors.reduce((prev, item) => {
          if (!prev[item.fromNodeId]) {
            prev[item.fromNodeId] = []
          }
          prev[item.fromNodeId].push(item)
          return prev
        }, {})
      },
      allToNodeVectorsMap({ allNodeVectors }) {
        return allNodeVectors.reduce((prev, item) => {
          if (!prev[item.toNodeId]) {
            prev[item.toNodeId] = []
          }
          prev[item.toNodeId].push(item)
          return prev
        }, {})
      },
      parallelFlowConfigSorted({ data }) {
        let parallelFlowConfigs = copyDataSimple(data.parallelFlowConfigs)
        parallelFlowConfigs.forEach((parallelFlow) => {
          let nodeIdListSorted = []
          let firstNodeId = parallelFlow.nodeIds.find(
            (nodeId) => this.isNodeParallelStart(nodeId)
          )
          nodeIdListSorted.push(firstNodeId)

          let nextNodeVectors = this.allFromNodeVectorsMap[firstNodeId]
          while (nextNodeVectors && nextNodeVectors.length > 0) {
            if (nextNodeVectors.length != 1) {
              throw "并行区段节点向量数据异常，异常节点id：" + nextNodeVectors[0].fromNodeId + "下游节点数量：" + nextNodeVectors.length
            }
            let nextNodeId = nextNodeVectors[0].toNodeId
            if (!this.isTrunkNode(nextNodeId)) {
              nodeIdListSorted.push(nextNodeId)
              nextNodeVectors = this.allFromNodeVectorsMap[nextNodeId]
            } else {
              break
            }
          }
          parallelFlow.nodeIds = nodeIdListSorted
        })
        return parallelFlowConfigs
      },
      trunkFlowNodeListSorted() {
        let nodeList = []
        let firstTrunkNode = this.allNodes.find((v) => this.isTrunkNode(v.id) && !this.allToNodeVectorsMap[v.id])
        nodeList.push(firstTrunkNode)

        let nextNodeVectors = this.allFromNodeVectorsMap[firstTrunkNode.id]
        while (nextNodeVectors && nextNodeVectors.length > 0) {
          let nextNodeTrunkVector = nextNodeVectors.find(v => this.isTrunkNode(v.toNodeId))
          let nextNode = this.allNodesMap[
            nextNodeTrunkVector.toNodeId
          ]
          nodeList.push(nextNode)
          nextNodeVectors = this.allFromNodeVectorsMap[nextNode.id]
        }
        return nodeList
      },
      trunkFlowNodeIdListSorted({ trunkFlowNodeListSorted }) {
        return trunkFlowNodeListSorted.map((item) => item.id)
      },
      trunkFlowNodeIndexMap() {
        return this.trunkFlowNodeIdListSorted.reduce((previous, current, index) => {
          previous[current] = index
          return previous
        }, {})
      },
      parallelFlowConfigSortedWithTrunk({
        parallelFlowConfigSorted,
        allFromNodeVectorsMap,
        allToNodeVectorsMap,
        trunkFlowNodeIdListSorted,
      }) {
        return parallelFlowConfigSorted.map((parallel) => {
          let trunkNodeIds = []
          const trunkStartId = this.getNextNodeId(allToNodeVectorsMap[parallel.nodeIds[0]][0].fromNodeId)
          const trunkStartIdIdx = trunkFlowNodeIdListSorted.findIndex((item) => item == trunkStartId)
          const trunkEndId = this.getPrevNodeId(
            allFromNodeVectorsMap[parallel.nodeIds[parallel.nodeIds.length - 1]][0].toNodeId
          )
          const trunkEndIdIdx = trunkFlowNodeIdListSorted.findIndex((item) => item == trunkEndId)
          let i = 0
          trunkNodeIds.push(trunkFlowNodeIdListSorted[trunkStartIdIdx + i])
          while (trunkStartIdIdx + i != trunkEndIdIdx) {
            i++
            trunkNodeIds.push(trunkFlowNodeIdListSorted[trunkStartIdIdx + i])
          }

          // let colNum = 0;
          // const curParallelFlowConfig = this.parallelFlowConfigMap[parallel.id];
          // const nodeIds = curParallelFlowConfig.nodeIds;
          // const startIdx = this.trunkFlowNodeIdListSorted.findIndex(
          //   (item) => item == this.allToNodeVectorsMap[nodeIds[0]][0].fromNodeId
          // );
          // const endIdx = this.trunkFlowNodeIdListSorted.findIndex(
          //   (item) => item == this.allFromNodeVectorsMap[nodeIds[nodeIds.length - 1]][0].toNodeId
          // );

          // this.parallelFlowConfigSorted.forEach((parallelFlow) => {
          //   if (parallelFlow.id != parallel.id) {
          //     const nowStartIdx = this.trunkFlowNodeIdListSorted.findIndex(
          //       (item) => item == this.allToNodeVectorsMap[parallelFlow.nodeIds[0]][0].fromNodeId
          //     );

          //     const nowEndIdx = this.trunkFlowNodeIdListSorted.findIndex(
          //       (item) =>
          //         item == this.allFromNodeVectorsMap[parallelFlow.nodeIds[parallelFlow.nodeIds.length - 1]][0].toNodeId
          //     );

          //     if (nowStartIdx == startIdx && nowEndIdx == endIdx) {
          //       if (parallelFlow.sort < curParallelFlowConfig.sort) {
          //         colNum += 1;
          //       }
          //     } else if (nowStartIdx >= startIdx && nowEndIdx <= endIdx) {
          //       colNum += 1;
          //     }
          //   }
          // });
          return {
            id: parallel.id,
            nodeIds: parallel.nodeIds,
            // sort: parallel.sort,
            // colNum: 2 + colNum,
            trunkNodeIds,
          }
        })
      },
      parallelRowInfoList({ parallelFlowConfigSortedWithTrunk }) {
        // [   [ [nodeId1,nodeId2],[nodeId3] ], [ [nodeId4,nodeId5],[nodeId6] ]   ]
        let arr = []
        parallelFlowConfigSortedWithTrunk.forEach((firstItem) => {
          arr.forEach((item) => {
            if (JSON.stringify(item).includes(JSON.stringify(firstItem.trunkNodeIds))) {
              item.push(firstItem.nodeIds)
            }
          })
          if (!arr.some((item) => JSON.stringify(item).includes(JSON.stringify(firstItem.trunkNodeIds)))) {
            arr.push([firstItem.trunkNodeIds, firstItem.nodeIds])
          }
        })
        return arr
      },
      cannotdeleteNodeIdList({ trunkFlowNodeIdListSorted, allFromNodeVectorsMap, allToNodeVectorsMap }) {
        let arr = []
        trunkFlowNodeIdListSorted.forEach((nodeId, nodeIdIdx) => {
          if (nodeIdIdx > 0 && nodeIdIdx < trunkFlowNodeIdListSorted.length - 1) {
            const prevIdx = nodeIdIdx - 1
            const nextIdx = nodeIdIdx + 1
            if (
              allFromNodeVectorsMap[trunkFlowNodeIdListSorted[prevIdx]].length > 1 &&
              allToNodeVectorsMap[trunkFlowNodeIdListSorted[nextIdx]].length > 1
            ) {
              arr.push(nodeId)
            }
          }
        })
        return arr
      },
      cannotdeleteSubFlowIdList() {
        return this.data.nodes
          .filter((item) => {
            return !item.childFlowId
          })
          .map((item) => item.id)
      },
      sameStartAndEndGroup() {
        let sameStartAndEndGroup = {}
        this.parallelFlowConfigSorted.forEach((parallelFlow) => {
          let key = this.getSameStartAndEndGroupKey(parallelFlow.id)
          if (!sameStartAndEndGroup[key]) {
            sameStartAndEndGroup[key] = []
          }
          sameStartAndEndGroup[key].push(parallelFlow.id)
        })
        return sameStartAndEndGroup
      },
    },

    created() {
      // console.log(this.acrossMap);
      // console.log(this.allNodes);
      // console.log('parallelFlowNodeIds', this.parallelFlowNodeIds);
      // console.log('trunkFlowNodeListSorted', this.trunkFlowNodeListSorted);
      // console.log('trunkFlowInfo',this.trunkFlowInfo);
      // console.log(this.nodeParallelIdMap);
      // console.log(this.allNodeVectors);
      // console.log(this.trunkFlowNodeIdListSorted);
      // console.log(this.parallelFlowConfigSorted);
      // console.log('allFromNodeVectorsMap',this.allFromNodeVectorsMap);
      // console.log('allToNodeVectorsMap',this.allToNodeVectorsMap);
      // console.log('cannotAddNextNodeList',this.cannotAddNextNodeList);
      // console.log('cannotAddPrevNodeList',this.cannotAddPrevNodeList);
      // console.log('parallelRowInfoList', this.parallelRowInfoList);
      // console.log('nodeIdColumnNumber', this.nodeIdColumnNumber);
      // console.log(this.getNodePostion('bfad0eeb-2c6b-46f5-bc90-68028b172259'));
      // console.log('cannotdeleteSubFlowIdList',this.cannotdeleteSubFlowIdList)
    },
    mounted() {
      console.log(this.tooltipShow)
    },
    methods: {
      isVirtualNode(nodeId) {
        return this.allNodesMap[nodeId].type == virtualType
      },
      isTrunkNode(nodeId) {
        if (!this.nodeParallelIdMap[nodeId]) {
          return true
        } else {
          return false
        }
      },
      getNodePostion(nodeId) {
        return {
          colNum: this.getNodeColumnNumber(nodeId),
          rowNum: this.getNodeRowNumber(nodeId),
        }
      },
      getTrunkStartIndex(parallelFlowId) {
        return this.trunkFlowNodeIdListSorted.findIndex(
          (item) => item == this.allToNodeVectorsMap[this.parallelFlowConfigMap[parallelFlowId].nodeIds[0]][0].fromNodeId
        )
      },
      getTrunkEndIndex(parallelFlowId) {
        return this.trunkFlowNodeIdListSorted.findIndex((item) => {
          let parallelFlow = this.parallelFlowConfigMap[parallelFlowId]
          return item == this.allFromNodeVectorsMap[parallelFlow.nodeIds[parallelFlow.nodeIds.length - 1]][0].toNodeId
        })
      },
      getSameStartAndEndGroupKey(parallelFlowId) {
        const nowStartIdx = this.getTrunkStartIndex(parallelFlowId)
        const nowEndIdx = this.getTrunkEndIndex(parallelFlowId)
        return nowStartIdx + '_' + nowEndIdx
      },
      getNodeColumnNumber(nodeId) {
        if (this.isTrunkNode(nodeId)) {
          return 1
        } else {
          const parallelFlowConfigId = this.nodeParallelIdMap[nodeId]
          const curParallelFlowConfig = this.parallelFlowConfigMap[parallelFlowConfigId]
          const nodeIds = curParallelFlowConfig.nodeIds
          const startIdx = this.trunkFlowNodeIdListSorted.findIndex(
            (item) => item == this.allToNodeVectorsMap[nodeIds[0]][0].fromNodeId
          )
          const endIdx = this.trunkFlowNodeIdListSorted.findIndex(
            (item) => item == this.allFromNodeVectorsMap[nodeIds[nodeIds.length - 1]][0].toNodeId
          )
          // console.log(startIdx,endIdx);
          let extraColumn = 0

          let sameStartAndEndGroup = this.sameStartAndEndGroup

          this.parallelFlowConfigSorted.forEach((parallelFlow) => {
            if (parallelFlow.id != parallelFlowConfigId) {
              const nowStartIdx = this.getTrunkStartIndex(parallelFlow.id)
              const nowEndIdx = this.getTrunkEndIndex(parallelFlow.id)
              if (nowStartIdx >= startIdx && nowEndIdx <= endIdx && nowEndIdx - nowStartIdx != endIdx - startIdx) {
                extraColumn += 1
              }
            }
          })

          let curSameStartAndEndGroup = sameStartAndEndGroup[this.getSameStartAndEndGroupKey(parallelFlowConfigId)]
          curSameStartAndEndGroup.sort((aId, bId) => {
            let a = this.parallelFlowConfigMap[aId].sort
            let b = this.parallelFlowConfigMap[bId].sort
            return a - b
          })
          return 2 + extraColumn + curSameStartAndEndGroup.findIndex((v) => v == parallelFlowConfigId)
        }
      },
      getNodeRowNumber(nodeId) {
        // 主干
        if (this.isTrunkNode(nodeId)) {
          if (!this.allToNodeVectorsMap[nodeId] || this.allToNodeVectorsMap[nodeId].length == 0) {
            return 1
          } else if (this.allToNodeVectorsMap[nodeId].length == 1) {
            // 如果上游节点只有一个，那么其一定为主干节点，获取其行号加一即可
            let nowIdx = this.trunkFlowNodeListSorted.findIndex(node => node.id == nodeId)
            return this.getNodeRowNumber(this.trunkFlowNodeListSorted[nowIdx - 1].id) + 1
          } else {
            // 获取所有的上游节点列表
            const fromNodeIdList = this.allToNodeVectorsMap[nodeId].map((item) => item.fromNodeId)
            // 获取这些节点的行号，取其中最大的并加一
            const rowList = fromNodeIdList.map((id) => this.getNodeRowNumber(id))
            return Math.max(...rowList) + 1
          }
        } else {
          // 并行
          if (this.isNodeParallelStart(nodeId)) {
            // 并行起点
            // 获取其上游节点（一定是主干节点）的id
            const fromNodeId = this.allToNodeVectorsMap[nodeId][0].fromNodeId
            // 找到上游节点的下游节点中为主干节点的rowNumber
            return this.getNodeRowNumber(
              this.allFromNodeVectorsMap[fromNodeId]
                .find(nodeVector => this.isTrunkNode(nodeVector.toNodeId))
                .toNodeId
            )
          } else {
            // 非起点，获取上游节点的行号加一
            const nodeIds = this.parallelFlowConfigMap[this.nodeParallelIdMap[nodeId]].nodeIds
            const currentIndex = nodeIds.findIndex((v) => v == nodeId)
            return this.getNodeRowNumber(nodeIds[currentIndex - 1]) + 1
          }
        }
      },
      isNodeParallelStart(nodeId) {
        if (!this.isTrunkNode(nodeId)) {
          let beforeVector = this.allToNodeVectorsMap[nodeId]
          // 如果并行区段节点的上游节点是主干节点，则当前并行区段节点为开始节点
          return this.isTrunkNode(beforeVector[0].fromNodeId)
        } else {
          return false
        }
      },
      // 根据id获取该节点的上一个节点
      getPrevNodeId(nodeId) {
        const nowIdx = this.trunkFlowNodeIdListSorted.findIndex((item) => item == nodeId)
        return this.trunkFlowNodeIdListSorted[nowIdx - 1]
      },
      // 根据获取该节点的下一个节点
      getNextNodeId(nodeId) {
        const nowIdx = this.trunkFlowNodeIdListSorted.findIndex((item) => item == nodeId)
        return this.trunkFlowNodeIdListSorted[nowIdx + 1]
      },
      contextmenu(e, nodeId) {
        this.$emit('context-menu', { e, nodeId })
        // e.preventDefault();
      },
      async getNewId() {
        const { data } = await getNewId({ number: 1 })
        return data[0]
      },
      async getNewIds(number) {
        const { data } = await getNewId({ number })
        return data
      },
      async getNewNodeVector(fromNodeId, toNodeId) {
        const id = await this.getNewId()
        return {
          id,
          fromNodeId,
          toNodeId,
        }
      },
      async getNewNodeVectors(nodeIdPairs) {
        const ids = await this.getNewIds(nodeIdPairs.length)
        return nodeIdPairs.map((pair, index) => ({
          id: ids[index],
          fromNodeId: pair[0],
          toNodeId: pair[1]
        }))
      },

      async getAddedNodeData(newNodeInfo, nodeVectorAsInsertPoint, extraDataSetFn) {
        let fromNodeId = nodeVectorAsInsertPoint.fromNodeId
        let toNodeId = nodeVectorAsInsertPoint.toNodeId
        let addNodes = [newNodeInfo]

        let data = copyDataSimple(this.data)
        data.nodes = [...data.nodes, ...addNodes]

        let addNodeVectors = []
        if (!this.isVirtualNode(fromNodeId)) {
          addNodeVectors.push(await this.getNewNodeVector(fromNodeId, newNodeInfo.id))
        }
        if (!this.isVirtualNode(toNodeId)) {
          addNodeVectors.push(await this.getNewNodeVector(newNodeInfo.id, toNodeId))
        }

        data.nodeVectors = [
          ...data.nodeVectors.filter((v) => !(fromNodeId == v.fromNodeId && toNodeId == v.toNodeId)),
          ...addNodeVectors,
        ]

        // 如果插入点（用节点向量表示）在并行区段上，变更并行区段信息，将插入的节点id置入其中
        if (this.nodeVectorParallelIdMap[nodeVectorAsInsertPoint.id]) {
          let parallelFlowConfig = copyDataSimple(this.nodeVectorParallelIdMap[nodeVectorAsInsertPoint.id])
          parallelFlowConfig.nodeIds = [...parallelFlowConfig.nodeIds, newNodeInfo.id]
          data.parallelFlowConfigs = [
            ...data.parallelFlowConfigs.filter((v) => v.id != parallelFlowConfig.id),
            parallelFlowConfig,
          ]
        }
        if (extraDataSetFn) {
          extraDataSetFn(data)
        }

        return data
      },
      async addNode(newNodeInfo, nodeVectorAsInsertPoint, extraDataSetFn) {
        this.$emit('change', await this.getAddedNodeData(newNodeInfo, nodeVectorAsInsertPoint, extraDataSetFn))
      },
      addPrevNode(newNodeInfo, targetNodeId, extraDataSetFn) {
        let isTrunk = this.isTrunkNode(targetNodeId)
        let toTargetNodeVectors = this.allToNodeVectorsMap[targetNodeId]
        let nodeVectorAsInsertPoint = isTrunk ? toTargetNodeVectors.find(
          (v) => this.isTrunkNode(v.fromNodeId)
        ) : toTargetNodeVectors[0]
        this.addNode(
          newNodeInfo,
          nodeVectorAsInsertPoint,
          extraDataSetFn
        )
      },
      addNextNode(newNodeInfo, targetNodeId, extraDataSetFn) {
        let isTrunk = this.isTrunkNode(targetNodeId)
        let fromTargetNodeVectors = this.allFromNodeVectorsMap[targetNodeId]
        let nodeVectorAsInsertPoint = isTrunk ? fromTargetNodeVectors.find(
          (v) => this.isTrunkNode(v.toNodeId)
        ) : fromTargetNodeVectors[0]

        this.addNode(
          newNodeInfo,
          nodeVectorAsInsertPoint,
          extraDataSetFn
        )
      },
      async addParall(newNodeInfo, targetNodeId, newParallelFlowConfigs) {
        const prevNodeId = this.getPrevNodeId(targetNodeId)
        const nextNodeId = this.getNextNodeId(targetNodeId)

        let addNodeVectors = []

        if (!this.isVirtualNode(prevNodeId)) {
          addNodeVectors.push(await this.getNewNodeVector(prevNodeId, newNodeInfo.id))
        }
        if (!this.isVirtualNode(nextNodeId)) {
          addNodeVectors.push(await this.getNewNodeVector(newNodeInfo.id, nextNodeId))
        }

        // 只能主干添加
        let addNodes = [newNodeInfo]
        let data = copyDataSimple(this.data)
        data.nodes = [...data.nodes, ...addNodes]
        data.nodeVectors = [...data.nodeVectors, ...addNodeVectors]

        let sameTrunkNum = 0
        this.parallelFlowConfigSorted.forEach((parallelFlow) => {
          const nowParallelFlowConfigsFromId = this.allToNodeVectorsMap[parallelFlow.nodeIds[0]][0].fromNodeId
          const nowParallelFlowConfigsToId = this.allFromNodeVectorsMap[
            parallelFlow.nodeIds[parallelFlow.nodeIds.length - 1]
          ][0].toNodeId
          if (prevNodeId == nowParallelFlowConfigsFromId && nextNodeId == nowParallelFlowConfigsToId) {
            sameTrunkNum += 1
          }
        })

        newParallelFlowConfigs.sort = sameTrunkNum

        data.parallelFlowConfigs = [...data.parallelFlowConfigs, newParallelFlowConfigs]
        this.$emit('change', data)
      },
      async getNodeDeletedData(targetNodeId) {
        const data = copyDataSimple(this.data)
        // 删除节点信息
        editableForEach(data.nodes, (node, index, splice) => {
          if (node.id == targetNodeId) {
            splice(index, 1)
          }
        })

        const newNodeVectors = data.nodeVectors

        const targetParallelId = this.nodeParallelIdMap[targetNodeId]
        if (targetParallelId) {
          // 如果当前节点属于并行区段
          // 删除当前节点的前后节点向量
          let preNodeVector = this.allToNodeVectorsMap[targetNodeId][0]
          let nextNodeVector = this.allFromNodeVectorsMap[targetNodeId][0]
          let needDeletedNodeVectorIds = [
            preNodeVector.id,
            nextNodeVector.id
          ]
          editableForEach(newNodeVectors, (nodeVector, index, splice) => {
            if (needDeletedNodeVectorIds.includes(nodeVector.id)) {
              splice(index, 1)
            }
          })
          // 处理并行区段信息和节点向量信息
          let currentParallelIndex = data.parallelFlowConfigs.findIndex(v => v.id == targetParallelId)
          let currentParallel = data.parallelFlowConfigs[currentParallelIndex]
          // 如果当前并行区段仅剩一个节点，即当前要删除的节点，那么直接移除当前并行区段，不考虑加新节点向量
          if (currentParallel.nodeIds.length === 1) {
            data.parallelFlowConfigs.splice(currentParallelIndex, 1)
          } else {// 否则删掉并行区段中对当前节点的引用
            currentParallel.nodeIds = currentParallel.nodeIds.filter((nodeId) => nodeId != targetNodeId)
            // 因为并行区段仍然存在，要考虑是否加入新的节点向量
            // 获取前后节点id
            let preNodeId = preNodeVector.fromNodeId
            let nextNodeId = nextNodeVector.toNodeId
            // 如果两者不存在虚拟节点，加入新的节点向量
            if (!this.isVirtualNode(preNodeId) && !this.isVirtualNode(nextNodeId)) {
              newNodeVectors.push(await this.getNewNodeVector(preNodeId, nextNodeId))
            }
          }
        } else {
          let preNodeVectors = this.allToNodeVectorsMap[targetNodeId]
          let nextNodeVectors = this.allFromNodeVectorsMap[targetNodeId]
          // 找到当前节点的前后主干节点（一定存在，因为虚拟节点禁止删除）
          let preTrunkNodeId = preNodeVectors.find(v => this.isTrunkNode(v.fromNodeId)).fromNodeId
          let nextTrunkNodeId = nextNodeVectors.find(v => this.isTrunkNode(v.toNodeId)).toNodeId

          // 删除全部当前节点相邻的节点向量
          let needDeletedNodeVectorIdMap = {}
          preNodeVectors.forEach(v => { needDeletedNodeVectorIdMap[v.id] = true })
          nextNodeVectors.forEach(v => { needDeletedNodeVectorIdMap[v.id] = true })
          editableForEach(newNodeVectors, (nodeVector, index, splice) => {
            if (needDeletedNodeVectorIdMap[nodeVector.id]) {
              splice(index, 1)
            }
          })

          // 整理需要添加的节点向量的节点id对
          let needAddNodeIdPairs = []
          // 前面的需要指向下一个主干节点（排除虚拟节点的节点向量）
          if (!this.isVirtualNode(nextTrunkNodeId)) {
            preNodeVectors.forEach(v => {
              if (!this.isVirtualNode(v.fromNodeId)) {
                needAddNodeIdPairs.push([v.fromNodeId, nextTrunkNodeId])
              }
            })
          }

          // 后面的需要指向前一个主干节点（排除虚拟节点的节点向量）
          if (!this.isVirtualNode(preTrunkNodeId)) {
            nextNodeVectors.forEach(v => {
              if (!this.isVirtualNode(v.toNodeId)) {
                needAddNodeIdPairs.push([preTrunkNodeId, v.toNodeId])
              }
            })
          }
          // 【节点id对】转换成节点向量对象并添加
          if (needAddNodeIdPairs.length > 0) {
            let needAddNodeVectors = await this.getNewNodeVectors(needAddNodeIdPairs)
            needAddNodeVectors.forEach(v => {
              newNodeVectors.push(v)
            })
          }

        }

        // 删除当前节点对应的成对节点信息
        if (data.pairNodeInfo) {
          editableForEach(data.pairNodeInfo, (pairNodeItem, index, splice) => {
            if (pairNodeItem.nodeIds.includes(targetNodeId)) {
              splice(index, 1)
            }
          })
        }

        return data
      },
      async deleteNode(targetNodeId) {
        this.$emit('change', await this.getNodeDeletedData(targetNodeId))
      },
      getNowParallConfig(targetNodeId) {
        const parallelFlowConfigId = this.nodeParallelIdMap[targetNodeId]
        const nodeIds = this.parallelFlowConfigMap[parallelFlowConfigId].nodeIds
        const startTrunkNodePrev = this.allToNodeVectorsMap[nodeIds[0]][0].fromNodeId
        const endTrunkNodeNext = this.allFromNodeVectorsMap[nodeIds[nodeIds.length - 1]][0].toNodeId
        return {
          trunkStartNodeId: this.getNextNodeId(startTrunkNodePrev),
          trunkEndNodeId: this.getPrevNodeId(endTrunkNodeNext),
        }
      },
      getTrunkList() {
        return this.trunkFlowNodeListSorted.map((item) => !this.isVirtualNode(item.id) && item).filter((item) => item)
      },
      /**
       *
       * @param {String} trunkStartNodeId 区段起始相邻节点 id
       * @param {String} trunkEndNodeId 区段结束相邻节点 id
       * @param {String} targetNodeId 右键目标节点 id
       */
      validateParallConfig(trunkStartNodeId, trunkEndNodeId, targetNodeId) {
        const trunkStartNodeIdIdx = this.trunkFlowNodeIdListSorted.findIndex((nodeId) => nodeId == trunkStartNodeId)
        const trunkEndNodeIdIdx = this.trunkFlowNodeIdListSorted.findIndex((nodeId) => nodeId == trunkEndNodeId)
        // 顺序
        if (trunkStartNodeIdIdx == trunkEndNodeIdIdx) {
          // 1个的
          return true
        } else if (trunkStartNodeIdIdx > trunkEndNodeIdIdx) {
          return false
        } else {
          // some&&（全包含或全属于）
          const newTrunkIds = this.trunkFlowNodeIdListSorted.slice(trunkStartNodeIdIdx, trunkEndNodeIdIdx + 1)
          const targetParallelFlowConfigSortedWithTrunk = this.parallelFlowConfigSortedWithTrunk.filter(
            (item) => item.id != this.nodeParallelIdMap[targetNodeId]
          )
          let valid = true
          newTrunkIds.forEach((newTrunkId) => {
            targetParallelFlowConfigSortedWithTrunk.forEach((targetParallel) => {
              const trunkNodeIds = targetParallel.trunkNodeIds
              if (trunkNodeIds.includes(newTrunkId)) {
                if (
                  !(
                    newTrunkIds.every((newTrunkId) => trunkNodeIds.includes(newTrunkId)) ||
                    trunkNodeIds.every((trunkId) => newTrunkIds.includes(trunkId))
                  )
                ) {
                  valid = false
                }
              }
            })
          })
          return valid
        }
      },
      /**
       *
       * @param {String} trunkStartNodeId 区段起始相邻节点 id
       * @param {String} trunkEndNodeId 区段结束相邻节点 id
       * @param {String} targetNodeId 右键目标节点 id
       */
      async parallConfig(trunkStartNodeId, trunkEndNodeId, targetNodeId) {
        const targetParallelFlowConfigId = this.nodeParallelIdMap[targetNodeId]
        const nodeIds = this.parallelFlowConfigMap[targetParallelFlowConfigId].nodeIds
        const targetParallelConfigStartId = nodeIds[0]
        const targetParallelConfigEndId = nodeIds[nodeIds.length - 1]
        let data = copyDataSimple(this.data)
        let addNodeVectors = []

        const fromNodeId = this.getPrevNodeId(trunkStartNodeId)
        const toNodeId = this.getNextNodeId(trunkEndNodeId)
        if (!this.isVirtualNode(fromNodeId)) {
          addNodeVectors.push(await this.getNewNodeVector(fromNodeId, targetParallelConfigStartId))
        }
        if (!this.isVirtualNode(toNodeId)) {
          addNodeVectors.push(await this.getNewNodeVector(targetParallelConfigEndId, toNodeId))
        }
        data.nodeVectors = [
          ...data.nodeVectors.filter(
            (v) => !(targetParallelConfigEndId == v.fromNodeId || targetParallelConfigStartId == v.toNodeId)
          ),
          ...addNodeVectors,
        ]
        let newInstance = new Vue(workRepairFlowComponentConfig)
        newInstance.data = copyDataSimple(data)
        let curNewSameStartAndEndGroup =
          newInstance.sameStartAndEndGroup[newInstance.getSameStartAndEndGroupKey(targetParallelFlowConfigId)]

        // 排除自身后的相同开始结束的并行区段
        const startAndEndGroupExcludeSelf = curNewSameStartAndEndGroup.filter((v) => v != targetParallelFlowConfigId)
        if (startAndEndGroupExcludeSelf.length == 0) {
          data.parallelFlowConfigs.find((v) => v.id == targetParallelFlowConfigId).sort = 0
        } else {
          data.parallelFlowConfigs.find((v) => v.id == targetParallelFlowConfigId).sort =
            Math.max(
              ...startAndEndGroupExcludeSelf.map((v) => {
                return newInstance.parallelFlowConfigMap[v].sort
              })
            ) + 1
        }

        this.$emit('change', data)
      },
      deleteChildFlow(targetNodeId) {
        let data = copyDataSimple(this.data)
        let nowNode = data.nodes.find((node) => node.id == targetNodeId)
        let childFlowId = nowNode.childFlowId
        nowNode.childFlowId = ''

        data.subflowInfoList = data.subflowInfoList.filter((item) => item.id != childFlowId)
        this.$emit('change', data)
      },
      // 插入流程模板
      async addFlowTemplate(targetNodeId, templateFlowData) {
        // console.log(targetNodeId,templateFlowData);
        const flowTemplateInstance = new Vue(WorkRepairFlowComponentConfig)
        flowTemplateInstance.data = templateFlowData
        // console.log(flowTemplateInstance);

        const targetNodeVector = this.allFromNodeVectorsMap[targetNodeId].find((v) => {
          return this.isTrunkNode(v.toNodeId)
        })

        const fromNodeId = targetNodeVector.fromNodeId
        const toNodeId = targetNodeVector.toNodeId
        let data = copyDataSimple(this.data)
        data.nodes = [...data.nodes, ...templateFlowData.nodes]

        // 模板的所有开始
        let allTemplateStartArr = []
        templateFlowData.nodes.forEach((node) => {
          if (!flowTemplateInstance.originalToNodeVectorsMap[node.id]) {
            allTemplateStartArr.push(node)
          }
        })

        // 模板的所有结束
        let allTemplateEndArr = []
        templateFlowData.nodes.forEach((node) => {
          if (!flowTemplateInstance.originalFromNodeVectorsMap[node.id]) {
            allTemplateEndArr.push(node)
          }
        })

        let addNodeVectors = []
        if (!this.isVirtualNode(fromNodeId)) {
          for (let i = 0; i < allTemplateStartArr.length; i++) {
            addNodeVectors.push(await this.getNewNodeVector(fromNodeId, allTemplateStartArr[i].id))
          }
        }
        if (!this.isVirtualNode(toNodeId)) {
          for (let i = 0; i < allTemplateEndArr.length; i++) {
            addNodeVectors.push(await this.getNewNodeVector(allTemplateEndArr[i].id, toNodeId))
          }
        }

        data.nodeVectors = [
          ...data.nodeVectors.filter((v) => !(fromNodeId == v.fromNodeId && toNodeId == v.toNodeId)),
          ...addNodeVectors,
          ...templateFlowData.nodeVectors,
        ]

        const parallelFlowConfigId = this.nodeParallelIdMap[targetNodeId]

        if (parallelFlowConfigId) {
          let parallelFlowConfig = {}
          parallelFlowConfig = copyDataSimple(data.parallelFlowConfigs.find((item) => item.id == parallelFlowConfigId))

          let nodeIdsArr = templateFlowData.nodes.map((node) => node.id)
          parallelFlowConfig.nodeIds = [...parallelFlowConfig.nodeIds, ...nodeIdsArr]

          data.parallelFlowConfigs = [
            ...data.parallelFlowConfigs.filter((v) => v.id != parallelFlowConfigId),
            parallelFlowConfig,
          ]
        } else {
          data.parallelFlowConfigs = [...data.parallelFlowConfigs, ...templateFlowData.parallelFlowConfigs]
        }

        if (data.subflowInfoList) {
          data.subflowInfoList = [...data.subflowInfoList, ...templateFlowData.subflowInfoList]
        }

        if (data.pairNodeInfo) {
          data.pairNodeInfo = [...data.pairNodeInfo, ...templateFlowData.pairNodeInfo]
        }

        this.$emit('change', data)
      },

      selectVector(e, nodeVector) {
        if (!this.moveNodeStatus) return
        this.$emit('has-select', nodeVector)
      },
      // 移动节点（先增加后删除原因：因为先删除可能会导致并行区段、成对节点、节点向量被删除，为后续添加造成很大麻烦）
      async moveNodeTo(targetNodeId, targetNodeVector) {
        // 先添加一个复制出来的节点，并添加
        let tempId = "__temp_node_id__"
        let copyNodeInfo = copyDataSimple(this.allNodesMap[targetNodeId])
        copyNodeInfo.id = tempId
        let copyNodeAddedFlowData = await this.getAddedNodeData(
          copyNodeInfo,
          targetNodeVector
        )
        // 移除原有节点信息
        let newInstance = new Vue(workRepairFlowComponentConfig)
        newInstance.data = copyNodeAddedFlowData
        let nodeMovedFlowData = await newInstance.getNodeDeletedData(targetNodeId)

        // 将临时id转换为原本id
        nodeMovedFlowData.nodes.find(v => v.id == tempId).id = targetNodeId
        nodeMovedFlowData.nodeVectors.forEach(v => {
          if (v.fromNodeId == tempId) {
            v.fromNodeId = targetNodeId
          }
          if (v.toNodeId == tempId) {
            v.toNodeId = targetNodeId
          }
        })
        nodeMovedFlowData.parallelFlowConfigs.forEach((parallelFlowConfig) => {
          parallelFlowConfig.nodeIds = parallelFlowConfig.nodeIds.map(
            nodeId => nodeId == tempId ? targetNodeId : nodeId
          )
        })
        nodeMovedFlowData.pairNodeInfo.forEach((pairNode) => {
          pairNode.nodeIds = pairNode.nodeIds.map(
            nodeId => nodeId == tempId ? targetNodeId : nodeId
          )
        })

        this.$emit('change', nodeMovedFlowData)
      },
      getCouldDispose(node) {
        let flag
        if('couldDispose' in node) {
          flag = node.couldDispose
        } else if ('couldDisposeRoleIds' in node) {
          flag = node.couldDisposeRoleIds.length > 0 ? true : false
        } else {
          flag = false
        }
        return flag
      }

    },
    render(createElement) {
      let React = { createElement }


      return (
        <div
          class="flow-graph-container"
        >
          {[<div
            class="flow-wrapper"
            {...{
              style: {
                height: this.flowHeight,
                width: this.flowWidth,
                marginLeft: this.flowWrapperMarginLeft + "px",
                marginRight: this.flowWrapperMarginLeft + "px",
              },
            }}
          >
            {[
              this.allNodes.map((node) => {
                let content = ''

                if (this.pairNodeEndIds.includes(node.id)) {
                  content = this.allNodesMap[this.pairNodeIdRelationMap[node.id]].roleConfigs
                    ? this.allNodesMap[this.pairNodeIdRelationMap[node.id]].roleConfigs
                      .map((roleConfig) => this.rolemap[roleConfig.roleId])
                      .toString()
                    : ''
                } else {
                  content = node.roleConfigs
                    ? node.roleConfigs.map((roleConfig) => this.rolemap[roleConfig.roleId]).toString()
                    : ''
                }

                let tooltipContent
                if (this.nowFlow.flowRunRecordInfo && this.nowFlow.flowRunRecordInfo.nodeId == node.id) {
                  tooltipContent = `驳回：${this.nowFlow.flowRunRecordInfo.workerName} 驳回时间：${this.nowFlow.flowRunRecordInfo.recordTime}`
                } else {
                  tooltipContent = node.nodeRecords
                    ? node.nodeRecords
                      .map((record) => {
                        return `打卡者：${record.workerName} 打卡角色：${record.roleName} 打卡时间：${record.recordTime}`
                      })
                      .toString()
                    : ''
                }

                return (
                  <work-repair-node
                    {...{
                      props: {
                        id: node.id,
                        name: node.name,
                        couldDispose: this.getCouldDispose(node),
                        finished: node.finished,
                        handleStatus: this.handleStatus,
                        content: content,
                        nodePosition: this.getNodePostion(node.id),
                        parallelStart: this.isNodeParallelStart(node.id), // 当前节点是否是并行区段的开始节点
                        nodeType: node.type,
                        context: this.nodesContext,
                        parallelRowInfoList: this.parallelRowInfoList,
                        originalPoint: this.originalPoint,
                        isVirtualNode: this.isVirtualNode(node.id),
                        tooltipContent,
                        flowRunRecordInfo: this.nowFlow.flowRunRecordInfo,
                        tooltipShow: this.tooltipShow
                      },
                      attrs: {
                        'data-id': node.id,
                      },
                      on: {
                        widthChange: (width) => {
                          this.flowWidth = width
                        },
                        heightChange: (height) => {
                          this.flowHeight = height
                        },
                      },
                      nativeOn: {
                        contextmenu: (e) => {
                          this.contextmenu(e, node.id)
                        },
                      },
                    }}
                  >
                    {/* {[<div>1</div>, <div>1</div>, <div>1</div>, <div>1</div>, <div>1</div>, <div>1</div>, <div>1</div>]} */}
                  </work-repair-node>
                )
              }),
              <svg
                {...{
                  style: {
                    overflow: 'visible',
                  },
                }}
              >
                {[
                  ...this.allNodeVectors.map((v) => (
                    <work-repair-node-vector
                      {...{
                        props: {
                          fromNodeId: v.fromNodeId,
                          toNodeId: v.toNodeId,
                          context: this.nodesContext,
                          isHighlight: this.highlightMap[v.id],
                        },
                        on: {
                          mouseenter: () => {
                            if (!this.moveNodeStatus) return
                            this.highlightMap = {
                              [v.id]: true,
                            }
                          },
                          mouseleave: () => {
                            // if (!this.moveNodeStatus) return;
                            this.highlightMap = {}
                          },
                        },
                        nativeOn: {
                          contextmenu: (e) => {
                            e.preventDefault()
                          },
                          click: (e) => {
                            this.selectVector(e, v)
                          },
                        },
                      }}
                    ></work-repair-node-vector>
                  )),
                  ...this.pairNodeInfoSorted.map((v) => (
                    <work-repair-pair-node-connect
                      {...{
                        props: {
                          fromNodeId: v.nodeIds[0],
                          toNodeId: v.nodeIds[1],
                          context: this.nodesContext,
                          offsetX: this.pairNodeConnectOffsetXMap[v.id],
                        },
                        nativeOn: {
                          contextmenu: (e) => {
                            e.preventDefault()
                          },
                        },
                      }}
                    ></work-repair-pair-node-connect>
                  )),
                ]}
              </svg>,
            ]}
          </div>]}
        </div>
      )
    },
  }
  Vue.component('work-repair-flow', workRepairFlowComponentConfig)
  window.WorkRepairFlowComponentConfig = workRepairFlowComponentConfig
})()
