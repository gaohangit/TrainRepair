let listToMap = (list, getKey) => {
  return list.reduce((pre, cur) => {
    pre[getKey(cur)] = cur;
    return pre;
  }, {});
};
let nodeSizeListener = elementResizeDetectorMaker();
Vue.component('work-repair-node', {
  name: 'WorkRepairNode',
  props: {
    id: {
      required: true,
    },
    name: {},
    couldDispose: {},
    finished: {},
    handleStatus: {},
    content: {},
    nodePosition: {
      // 里边包含了该节点的行、列信息
      type: Object,
      default: () => {},
    },
    parallelStart: {
      // 是不是并行区段的开始
      type: Boolean,
    },
    nodeType: {}, // 节点类型 virtual 虚拟节点，形状为圆
    context: {
      required: true,
    },
    parallelRowInfoList: {},
    originalPoint: {
      default() {
        return {
          x: 0,
          y: 0,
        };
      },
    },
    isVirtualNode: {},
    tooltipContent: {
      type: String,
    },
    flowRunRecordInfo: {},
  },
  data() {
    return {
      baseShowSizeObj: {
        height: null,
        width: null,
      },
      extendSlotSizeObj: {
        height: null,
        width: null,
      },
      showSlot: false,
    };
  },
  computed: {
    extraTopGap() {
      return 0;
    },
    extraBottomGap() {
      // console.log(this.contextMain);
      if (this.contextMain) {
        // console.log(this.contextMain);
        const rowHeightMap = this.contextMain.rowHeightMap;
        const nodeInstanceMap = this.contextMain.nodeInstanceMap;
        const nodeIdRowIdMap = this.contextMain.nodeIdRowIdMap;
        const nowRowId = nodeIdRowIdMap[this.id];
        // 如果当前节点不存在行信息，则返回0
        if (nowRowId == null) {
          return 0;
        } else {
          const nowRowInfo = this.contextMain.parallelRowInfoMap[nowRowId];
          const nowRowHeight = rowHeightMap[nowRowId];

          // 找到当前行的当前列
          let curColumn = nowRowInfo.columns.find((column) => column.includes(this.id));

          // 如果当前节点是当前列的最后一个
          if (curColumn[curColumn.length - 1] == this.id) {
            let getSimpleColumnHeight = (column) => {
              return (
                column
                  .map((nodeId) => (nodeInstanceMap[nodeId] ? nodeInstanceMap[nodeId].finalHeight : 0))
                  .reduce((a, b) => a + b, 0) +
                (column.length - 1) * this.context.nodeShowConfig.yGap
              );
            };
            // 如果当前列是重叠列(有子行)，则增加子行高度
            // console.log(this.id, nowRowInfo.coreColumnIndex, nowRowInfo.columns[nowRowInfo.coreColumnIndex]);
            if (
              nowRowInfo.coreColumnIndex != null &&
              nowRowInfo.columns[nowRowInfo.coreColumnIndex].includes(this.id)
            ) {
              let heightList = [
                getSimpleColumnHeight(curColumn.filter((nodeId) => nodeIdRowIdMap[nodeId] == nowRowId)),
                ...nowRowInfo.childIds.map((childRowId) => rowHeightMap[childRowId]),
              ];
              return (
                nowRowHeight -
                (heightList.reduce((a, b) => a + b, 0) + (heightList.length - 1) * this.context.nodeShowConfig.yGap)
              );
            } else {
              return nowRowHeight - getSimpleColumnHeight(curColumn);
            }
          } else {
            // 如果不是返回0
            return 0;
          }
        }
      } else {
        return 0;
      }
    },
    finalHeight() {
      let trueHeight = 0;
      let minHeight = this.context.nodeShowConfig.height;
      if (this.baseShowSizeObj.height) {
        trueHeight = trueHeight + this.baseShowSizeObj.height;
      }
      if (this.extendSlotSizeObj.height) {
        trueHeight = trueHeight + this.extendSlotSizeObj.height;
      }

      return trueHeight < minHeight ? minHeight : trueHeight;
    },
    finalWidth() {
      if (this.nodeType == 'virtual') {
        return this.finalHeight;
      } else {
        let trueWidth = 0;
        let minWidth = this.context.nodeShowConfig.width;
        if (this.baseShowSizeObj.width && this.baseShowSizeObj.width > trueWidth) {
          trueWidth = this.baseShowSizeObj.width;
        }
        if (this.extendSlotSizeObj.width && this.extendSlotSizeObj.width > trueWidth) {
          trueWidth = this.extendSlotSizeObj.width;
        }
        return trueWidth < minWidth ? minWidth : trueWidth;
      }
    },
    top() {
      // 禁止本类型组件及context里函数使用此值
      if (!this.contextMain) {
        return 0;
      }
      let top = this.originalPoint.y;
      if (this.nodePosition.colNum == 1) {
        let curColInstances = this.contextMain.colMap[this.nodePosition.colNum];
        for (let i = 0; i < curColInstances.length; i++) {
          let instance = curColInstances[i];
          if (instance == this) {
            break;
          }
          top =
            top +
            instance.extraTopGap +
            instance.finalHeight +
            this.context.nodeShowConfig.yGap +
            instance.extraBottomGap;
        }
      } else {
        let curColInstances = this.contextMain.colMap[this.nodePosition.colNum];

        let firstColInstances = this.contextMain.colMap[1];
        let curParallelStartNodeInstance = ((colInstances) => {
          let startInstance = null;
          for (let i = 0; i < colInstances.length; i++) {
            if (colInstances[i].parallelStart) {
              startInstance = colInstances[i];
            }
            if (colInstances[i] == this) {
              break;
            }
          }
          return startInstance;
        })(curColInstances);
        if (!curParallelStartNodeInstance) {
          return 0;
        }

        // 增加当前并行区段距主干开始节点的高度差
        let curParallelStartRowNum = curParallelStartNodeInstance.nodePosition.rowNum; //当前并行区段开始节点的行数
        for (let i = 0; i < curParallelStartRowNum - 1; i++) {
          let instance = firstColInstances[i];
          if (instance && instance.nodePosition.rowNum <= curParallelStartRowNum - 1) {
            top =
              top +
              instance.extraTopGap +
              instance.finalHeight +
              this.context.nodeShowConfig.yGap +
              instance.extraBottomGap;
          }

          // top = top + instance.finalHeight + this.context.nodeShowConfig.yGap;
        }
        // 现在的top是当前并行区段开始节点的top
        let started = false;
        for (let i = 0; i < curColInstances.length; i++) {
          let instance = curColInstances[i];

          if (instance == curParallelStartNodeInstance) {
            started = true;
          }
          if (instance == this) {
            break;
          }
          if (started) {
            top =
              top +
              instance.extraTopGap +
              instance.finalHeight +
              this.context.nodeShowConfig.yGap +
              instance.extraBottomGap;
          }
        }
      }
      return top + this.extraTopGap;
    },
    left() {
      // 禁止本类型组件及context里函数使用此值
      if (!this.contextMain) {
        return 0;
      }
      return this.contextMain.colCenterAxisPosition[this.nodePosition.colNum] - this.finalWidth / 2;
    },
    nodeStyle() {
      let baseStyle = {
        width: this.finalWidth + 'px',
        height: this.finalHeight + 'px',
      };
      if (!this.contextMain) {
        baseStyle = {
          ...baseStyle,
          left:
            (this.nodePosition.colNum - 1) * (this.context.nodeShowConfig.width + this.context.nodeShowConfig.xGap) +
            'px',
          top:
            (this.nodePosition.rowNum - 1) * (this.context.nodeShowConfig.height + this.context.nodeShowConfig.yGap) +
            'px',
          visibility: 'hidden',
        };
      } else {
        baseStyle = {
          ...baseStyle,
          left: this.left + 'px',
          top: this.top + 'px',
        };
      }

      if (this.nodeType == 'virtual') {
        baseStyle.borderRadius = '50%';
        baseStyle.justifyContent = 'center';
      }
      return baseStyle;
    },
    contextMain: {
      get() {
        return this.context.main;
      },
      set(main) {
        this.$set(this.context, 'main', main);
      },
    },
    defaultSlot() {
      return this.$slots.default;
    },
    state_1({ finished, couldDispose }) {
      return finished && !couldDispose;
    },
    state_2({ finished, couldDispose }) {
      return finished && couldDispose;
    },
    state_3({ finished, couldDispose }) {
      return !finished && couldDispose;
    },
    state_4({ finished, couldDispose }) {
      return !finished && !couldDispose;
    },
    state_5({ finished, flowRunRecordInfo, tooltipContent }) {
      return !finished && flowRunRecordInfo && !!tooltipContent;
    },
  },
  created() {
    this.initContext();
    this.registerSelf();
  },

  mounted() {
    nodeSizeListener.listenTo(this.$el.querySelector('.node-wrapper-children'), () => {
      this.reCalSize();
    });
    this.reCalSize();
  },
  destroyed() {
    this.unregisterSelf();
    nodeSizeListener.uninstall(this.$el.querySelector('.node-wrapper-children'));
  },
  methods: {
    // 重新计算宽高
    reCalSize() {
      let baseShowEl = this.$el.querySelector('.node-base-show');
      this.baseShowSizeObj = {
        height: baseShowEl.offsetHeight,
        width: baseShowEl.offsetWidth,
      };
      if (this.defaultSlot) {
        let extendSlotEl = this.$el.querySelector('.node-extend-slot');
        this.extendSlotSizeObj = {
          height: extendSlotEl.offsetHeight,
          width: extendSlotEl.offsetWidth,
        };
        this.showSlot = true;
      }
    },
    initContext() {
      if (!this.contextMain) {
        let nodeSelf = this;
        this.contextMain = new Vue({
          data: {
            nodeInstances: [],
          },
          computed: {
            parallelRowInfoList() {
              return nodeSelf.parallelRowInfoList || []; // [   [ [nodeId1,nodeId2],[nodeId3] ], [ [nodeId4,nodeId5],[nodeId6] ]   ]
            },
            parallelRowInfoWithIdList() {
              //[{id:1,columns:[[nodeId1,nodeId2],[nodeId3]]}]
              let temp = 1;
              let list = nodeSelf.parallelRowInfoList.map((row) => ({
                columns: row,
                id: temp++, // 行id
                parentId: null,
                childIds: [],
                coreColumnIndex: null,
              }));
              let rowMap = listToMap(list, (v) => v.id);
              let nodeRowMap = {};
              list.forEach((v) => {
                v.columns.forEach((column, colIndex) => {
                  column.forEach((nodeId) => {
                    if (!nodeRowMap[nodeId]) {
                      nodeRowMap[nodeId] = [];
                    }
                    nodeRowMap[nodeId].push({
                      rowId: v.id,
                      colIndex,
                    });
                  });
                });
              });
              for (const nodeId in nodeRowMap) {
                const rows = nodeRowMap[nodeId];
                if (rows.length > 1) {
                  let transRows = rows.map((row) => ({
                    rowId: row.rowId,
                    column: rowMap[row.rowId].columns[row.colIndex],
                    colIndex: row.colIndex,
                  }));
                  transRows.sort((a, b) => a.column.length - b.column.length);
                  for (let i = 0; i < transRows.length - 1; i++) {
                    const curTransRow = transRows[i];
                    const nextTransRow = transRows[i + 1];

                    // 校验数据
                    if (
                      curTransRow.column.some((nodeId) => !nextTransRow.column.find((v) => v == nodeId)) ||
                      curTransRow.column.length == nextTransRow.column.length
                    ) {
                      throw 'parallelRowInfoList参数错误';
                    }
                    rowMap[curTransRow.rowId].parentId = nextTransRow.rowId;
                    if (!rowMap[nextTransRow.rowId].childIds.includes(curTransRow.rowId)) {
                      rowMap[nextTransRow.rowId].childIds.push(curTransRow.rowId);
                    }

                    rowMap[curTransRow.rowId].coreColumnIndex = curTransRow.colIndex;
                    rowMap[nextTransRow.rowId].coreColumnIndex = nextTransRow.colIndex;
                  }
                }
              }
              return list;
            },
            parallelRowInfoMap() {
              return listToMap(this.parallelRowInfoWithIdList, (v) => v.id);
            },
            // 节点id -> 行id
            nodeIdRowIdMap() {
              let map = {};
              let isParentOf = (pRowId, rowId) => {
                let parentId = this.parallelRowInfoMap[rowId].parentId;
                if (parentId == null) {
                  return false;
                } else if (parentId == pRowId) {
                  return true;
                } else {
                  return isParentOf(pRowId, parentId);
                }
              };
              this.parallelRowInfoWithIdList.forEach((rowInfo) => {
                rowInfo.columns.forEach((column) => {
                  column.forEach((nodeId) => {
                    if (!map[nodeId] || isParentOf(map[nodeId], rowInfo.id)) {
                      map[nodeId] = rowInfo.id;
                    }
                  });
                });
              });
              return map;
            },

            // 行id -> 行高
            rowHeightMap() {
              let cache = {};
              let getRowHeight = (rowId) => {
                if (rowId in cache) {
                  return cache[rowId];
                }
                let rowInfo = this.parallelRowInfoMap[rowId];
                let getSimpleColumnHeight = (column) => {
                  return (
                    column
                      .map((nodeId) => (this.nodeInstanceMap[nodeId] ? this.nodeInstanceMap[nodeId].finalHeight : 0))
                      .reduce((a, b) => a + b, 0) +
                    (column.length - 1) * nodeSelf.context.nodeShowConfig.yGap
                  );
                };
                let rowHeight;
                if (rowInfo.childIds.length == 0) {
                  rowHeight = Math.max(...rowInfo.columns.map(getSimpleColumnHeight));
                } else {
                  let columns = rowInfo.columns;
                  let colIndex = rowInfo.coreColumnIndex;
                  let coreColumn = columns[colIndex];
                  rowHeight = Math.max(
                    ...rowInfo.columns.map((column, index) => {
                      if (index == colIndex) {
                        let heightList = [
                          getSimpleColumnHeight(coreColumn.filter((nodeId) => this.nodeIdRowIdMap[nodeId] == rowId)),
                          ...rowInfo.childIds.map((childRowId) => getRowHeight(childRowId)),
                        ];
                        return (
                          heightList.reduce((a, b) => a + b, 0) +
                          (heightList.length - 1) * nodeSelf.context.nodeShowConfig.yGap
                        );
                      } else {
                        return getSimpleColumnHeight(column);
                      }
                    })
                  );
                }
                cache[rowId] = rowHeight;
                return rowHeight;
              };

              // let rowHeightMap = () => {
              //   return this.parallelRowInfoWithIdList.reduce((prev, item) => {
              //     console.log(this.nodeIdRowIdMap);
              //     const columns = item.columns;
              //     let heightArr = [];
              //     columns.forEach((column) => {
              //       let height = 0;
              //       this.nodeIdRowIdMap[column[0]];
              //       column.forEach((nodeId) => {
              //         if (this.nodeIdRowIdMap[nodeId] != item.id) {
              //           height += 80;
              //           // height += rowHeightMap()[this.nodeIdRowIdMap[nodeId]];
              //         } else {
              //           height += this.nodeInstanceMap[nodeId] && this.nodeInstanceMap[nodeId].finalHeight;
              //         }
              //       });
              //       height += (column.length - 1) * nodeSelf.context.nodeShowConfig.yGap;
              //       heightArr.push(height);
              //     });
              //     prev[item.id] = Math.max(...heightArr);
              //     return prev;
              //   }, {});
              // };

              let rowHeightMap = {};
              this.parallelRowInfoWithIdList.forEach((rowInfo) => {
                rowHeightMap[rowInfo.id] = getRowHeight(rowInfo.id);
              });
              return rowHeightMap;
            },
            nodeInstanceMap() {
              // {
              //   节点id:'节点实例'
              // }
              return listToMap(this.nodeInstances, (v) => v.id);
            },
            colMap() {
              // 每列的节点list
              // {
              //   1:[node,node]
              // }
              var map = {};
              this.nodeInstances.forEach((nodeInstance) => {
                let colNum = nodeInstance.nodePosition.colNum;
                let rowNum = nodeInstance.nodePosition.rowNum;
                if (!map[colNum]) {
                  map[colNum] = {};
                }
                map[colNum][rowNum] = nodeInstance;
              });
              let transMap = {};
              for (const colNum in map) {
                const colMap = map[colNum];
                let colArr = [];
                Object.keys(colMap)
                  .map((v) => parseInt(v))
                  .sort((a, b) => a - b)
                  .forEach((rowNum) => {
                    colArr.push(colMap[rowNum]);
                  });
                transMap[colNum] = colArr;
              }
              return transMap;
            },
            colWidthMap() {
              // 每列的宽
              let map = {};
              for (const colNum in this.colMap) {
                let col = this.colMap[colNum];
                map[colNum] = Math.max(...col.map((nodeInstance) => nodeInstance.finalWidth));
              }
              return map;
            },
            colCenterAxisPosition() {
              // 每列的中轴
              let map = {};
              let tempList = [];
              for (const colNum in this.colWidthMap) {
                tempList[parseInt(colNum) - 1] = this.colWidthMap[colNum];
              }
              let startX = nodeSelf.originalPoint.x;
              tempList.forEach((colWidth, index) => {
                map[index + 1] = startX + colWidth / 2;
                startX = startX + colWidth + nodeSelf.context.nodeShowConfig.xGap;
              });
              return map;
            },
            width() {
              if (Object.keys(this.colMap).length == 0) {
                return '0px';
              } else {
                const maxColNum = Math.max(...Object.keys(this.colMap));
                const lastColInstances = this.colMap[maxColNum];
                const maxNodeWidth = lastColInstances.map((item) => {
                  return item.left + item.finalWidth;
                });
                return Math.max(...maxNodeWidth) + 'px';
              }
            },
            height() {
              if (this.colMap[1]) {
                const firstColInstances = this.colMap[1];
                let height = 0;
                firstColInstances.forEach((item) => {
                  height =
                    height +
                    item.extraTopGap +
                    item.finalHeight +
                    item.extraBottomGap +
                    nodeSelf.context.nodeShowConfig.yGap;
                });
                height = height - nodeSelf.context.nodeShowConfig.yGap;
                return height + 'px';
              } else {
                return '0px';
              }
            },
          },
          created() {},
          mounted() {
            console.log(this.parallelRowInfoWithIdList);
          },
          watch: {
            width(value) {
              nodeSelf.$emit('widthChange', value);
            },
            height(value) {
              nodeSelf.$emit('heightChange', value);
            },
          },
          methods: {
            // getPosId(colNum, rowNum) {
            //   return colNum + '_' + rowNum;
            // },
            registerNode(nodeInstance) {
              this.nodeInstances.push(nodeInstance);
            },
            unregisterNode(nodeInstance) {
              let index = this.nodeInstances.findIndex((v) => v == nodeInstance);
              if (index != -1) {
                this.nodeInstances.splice(index, 1);
              }
            },
          },
        });
      }
    },
    registerSelf() {
      this.contextMain.registerNode(this);
    },
    unregisterSelf() {
      this.contextMain.unregisterNode(this);
    },
  },
  render(createElement) {
    let React = { createElement };
    let children = [];
    if (this.content) {
      children = [
        <div class="node-base-show">
          {[
            <div>{this.name}</div>,
            <el-tooltip {...{
              props: {
                disabled: this.content.split('').length < 12,
              },
              attrs: {
                placement: 'bottom-start',
              },
            }}>
              {[
                <div slot="content">
                  {[<p>{this.name}</p>, <span>{this.content}</span>]}
                </div>,
                <span>{this.content}</span>,
              ]}
            </el-tooltip>,
          ]}
        </div>,
      ];
    } else {
      children = [<div class="node-base-show">{[<p>{this.name}</p>]}</div>];
    }

    if (this.defaultSlot) {
      children.push(
        <div class="node-extend-slot" style={{ visibility: this.showSlot ? 'visible' : 'hidden' }}>
          {[this.defaultSlot]}
        </div>
      );
    }
    return (
      <el-tooltip
        {...{
          props: {
            disabled: !this.handleStatus || this.state_3 || (this.state_4 && !this.state_5) || this.isVirtualNode,
            content: this.tooltipContent,
          },
          attrs: {
            placement: 'top-start',
          },
        }}
      >
        {[
          <div
            style={this.nodeStyle}
            {...{
              class: {
                'node-wrapper': true,
                state_1: this.state_1,
                state_2: this.state_2,
                state_3: this.state_3,
                state_4: this.state_4,
                state_5: this.state_5,
                handle_node: this.handleStatus,
              },
            }}
          >
            {[<div class="node-wrapper-children">{children}</div>]}
          </div>,
        ]}
      </el-tooltip>
    );
  },
});
