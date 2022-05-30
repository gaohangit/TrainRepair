window.app = new Vue({
  el: '#keyWorkFlowMonitor',
  data() {
    return {
      unitCode: '',
      configShow: false,
      keyWorkMonitorConfigs: [],
      params: '',
      isFullScreen: false,
      flowState: Object.freeze({
        '-1': '未开始',
        0: '正在进行',
        1: '已完成',
        2: '已驳回',
      }),
      dayPlanId: '',
      dayPlanIds: [],
      baseImgPath: ctxPath + '/static/trainRepair/workRepairFlow/keyWorkFlowMonitor/img',
      timer: null,
      time1: '',
      time2: '',
      planlessKeyList: [],
      localConfig: {
        trackRefreshTime: '60',
        fixedNum: '',
        flowConfig: {},
        trackShowConfig: {},
      },
      trainGroupNameList: [],
      allTrackAreaList: [],
      allTrainsetList: [],
      trackPowerList: [],

      monitorDialog: {
        visible: false,
        trackShowConfig: {},
        trackRefreshTime: '60',
        flowId: '',
        fixedNum: '',
        flowConfig: {},
        queryPastFlow: true, // 是否显示失效流程
        // 股道位置切换
        TrackPositionChange: true
      },
      mainTableHeight: 0,
      mainTableData: [],
      specialColumnKeys: Object.freeze(['FUNCTION_CLASS', 'BATCH_BOM_NODE_CODE']),
      staticColumns: [
        {
          key: 'TRAINSETID',
          label: '车组',
          property: 'trainsetId',
          fixed: false,
          type: 'static',
          width: '150',
        },
        {
          key: 'CONTENT',
          label: '作业内容',
          property: 'content',
          fixed: false,
          type: 'static',
          width: '150',
        },
        {
          key: 'STARTWORKERNAME',
          label: '录入人',
          property: 'startWorkerName',
          fixed: false,
          type: 'static',
          width: '120',
        },
        {
          key: 'STARTTIME',
          label: '录入时间',
          property: 'startTime',
          fixed: false,
          type: 'static',
          width: '160',
        },
        {
          key: 'STATE',
          label: '流程状态',
          property: 'state',
          fixed: false,
          type: 'static',
          width: '120',
        },
      ],
      columns: [],
      columnListMap: {},

      showColumnList: [],
      pageNum: 1,
      pageChangetimer: null,
      refreshTimer: null,
      dataRefreshTime: 60,

      commonDialog: {
        visible: false,
        trackShowConfig: {},
      },
      tableReresh: null,
      // 监测鼠标是否在table上，在table上时停止滚动
      move: true,
      scrollTimer: null,
    };
  },
  computed: {
    keyWorkMonitorConfigsMap({ keyWorkMonitorConfigs }) {
      return keyWorkMonitorConfigs.reduce((prev, item) => {
        prev[item.paramName] = Boolean(Number(item.paramValue));
        return prev;
      }, {});
    },
    keyWorkMonitorConfigsIdMap({ keyWorkMonitorConfigs }) {
      return keyWorkMonitorConfigs.reduce((prev, item) => {
        prev[item.paramName] = item.id;
        return prev;
      }, {});
    },
    paramsMap({ params }) {
      if (params === '') {
        return {};
      } else {
        let obj = params
          .slice(1)
          .split('&')
          .reduce((prev, param) => {
            prev[param.split('=')[0]] = param.split('=')[1];
            return prev;
          }, {});
        return obj;
      }
    },
    lineTableData() {
      return this.allTrackAreaSortList.map((item) => {
        let obj = {};
        obj.trackAreaName = item.trackAreaName;
        obj.trackAreaCode = item.trackAreaCode;
        obj.trackNameList = item.lstTrackInfo.map((itm) => {
          return itm.trackName;
        });
        obj.trackCodeList = item.lstTrackInfo.map((itm) => {
          return itm.trackCode;
        });
        return obj;
      });
    },
    allTrackAreaSortList({ allTrackAreaList }) {
      let data = copyData(allTrackAreaList);

      data.forEach((trackArea) => {
        // trackArea.lstTrackInfo.lstTrackPositionInfo
        trackArea.lstTrackInfo.sort((a, b) => a.sort - b.sort);
      });
      data.sort((a, b) => a.sort - b.sort);
      return data;
    },
    trackList({ allTrackAreaSortList }) {
      // 响应基本配置
      let trackList = allTrackAreaSortList.map((item) => item.lstTrackInfo).flat();
      return trackList;
    },
    // 所有股道code
    allTrackStr({ trackList }) {
      return trackList.map((item) => item.trackCode).toString();
    },
    trainsetIdFlowIdDataMap({ allTrainsetList, planlessKeyIdNodesMap }) {
      return allTrainsetList.reduce((prev, item) => {
        prev[item.trainsetId] = item.keyWorkFlowRunInfos.reduce((pre, itm) => {
          if (planlessKeyIdNodesMap[itm.flowId]) {
            if (!pre[itm.flowId]) {
              pre[itm.flowId] = [];
            }
            pre[itm.flowId].push(itm);
          }

          return pre;
        }, {});

        return prev;
      }, {});
    },

    trainsetList({ allTrainsetList, localConfig, paramsMap }) {
      let getAllTrackArea = JSON.parse(JSON.stringify(allTrainsetList));
      let list;
      if (paramsMap.trainsetId) {
        list = getAllTrackArea.filter((item) => item.trainsetId === paramsMap.trainsetId);
      } else {
        list = getAllTrackArea.filter((item) => localConfig.trackShowConfig[item.trackCode]);
      }

      if (!this.localConfig.TrackPositionChange) {
        list.forEach((train) => {
          let name = train.headDirectionPla;
          train.headDirectionPla = train.tailDirectionPla;
          train.tailDirectionPla = name;

          let carNo = train.headDirection;
          train.headDirection = train.tailDirection;
          train.tailDirection = carNo;
        });
      }

      return list;
    },
    train({ trainsetList, pageNum }) {
      return trainsetList.slice(pageNum - 1, pageNum)[0] || {};
    },
    pageTotal({ trainsetList }) {
      return trainsetList.length;
    },

    trainsetIdTrainTypeMap({ trainGroupNameList }) {
      return trainGroupNameList.reduce((prev, item) => {
        prev[item.trainsetid] = item.traintype;
        return prev;
      }, {});
    },
    // 股道通电信息
    trackCodePowerMap({ trackPowerList }) {
      return trackPowerList.reduce((prev, item) => {
        if (!prev[item.trackCode]) {
          prev[item.trackCode] = {};
        }
        prev[item.trackCode][item.trackPlaCode] = item;
        return prev;
      }, {});
    },

    trainsetIdNameMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        prev[item.trainsetId] = item.trainsetName;
        return prev;
      }, {});
    },
    trainsetIdInfoMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        prev[item] = item;
        return prev;
      }, {});
    },
    planlessKeyIdNameMap({ planlessKeyList }) {
      return planlessKeyList.reduce((prev, item) => {
        prev[item.id] = item.name;
        return prev;
      }, {});
    },
    planlessKeyIdNodesMap({ planlessKeyList, allTrainsetList }) {
      // 现有的
      let planlessKey = planlessKeyList.reduce((prev, item) => {
        prev[item.id] = item.nodes;
        return prev;
      }, {});

      // 整合全部的
      allTrainsetList.forEach((trainset) => {
        trainset.keyWorkFlowRunInfos.forEach((keyWorkFlowRun) => {
          if (!planlessKey[keyWorkFlowRun.flowId]) {
            planlessKey[keyWorkFlowRun.flowId] = keyWorkFlowRun.nodeWithRecords;
          }
        });
      });
      console.log(planlessKey);
      return planlessKey;
    },
    // 所有节点的nodeRecord
    flowRunNodeIdRecordsMap({ allTrainsetList }) {
      let obj = {};
      allTrainsetList.forEach((train) => {
        train.keyWorkFlowRunInfos.forEach((flowRun) => {
          const nodeRecords = flowRun.nodeWithRecords.reduce((prev, node) => {
            let record;
            record = node.nodeRecords
              .map((item) => {
                return `${item.workerName}<br>${item.recordTime}<br>`;
              })
              .join('');

            if (flowRun.flowRunRecordInfo && flowRun.flowRunRecordInfo.nodeId === node.id) {
              record = `<span style="color: red;">驳回: ${flowRun.flowRunRecordInfo.workerName}<br>${dayjs(
                flowRun.flowRunRecordInfo.recordTime
              ).format('YYYY-MM-DD HH:mm:ss')}</span>`;
            }

            prev[node.id] = record;
            return prev;
          }, {});

          obj[flowRun.id] = nodeRecords;
        });
      });
      return obj;
    },
  },
  watch: {
    train: {
      async handler(train) {
        const trainsetId = train.trainsetId;
        const trainType = this.trainsetIdTrainTypeMap[trainsetId];
        if (!trainType) return;
        const carNoList = await getKeyWorkExtraColumnValueList({
          columnKey: 'CAR',
          trainModel: trainType,
        });
        const carNoListStr = carNoList.map((item) => item.value).toString();
        if (!this.columnListMap[`${trainsetId}BATCH_BOM_NODE_CODEFlatList`]) {
          let list = await getBatchBomNodeCode(trainsetId, carNoListStr);
          if (list === null) {
            return;
          }
          this.$set(this.columnListMap, `${trainsetId}BATCH_BOM_NODE_CODEFlatList`, this.tranListToFlatData(list));
        }
      },
    },
    planlessKeyList(list) {
      list.forEach((item) => {
        this.$set(this.localConfig.flowConfig, item.id, {});
      });
    },
  },
  async created() {
    this.params = location.search;
    this.getConfigFromLocal();
    await this.getUnitCode();
    await this.getTrainsetListReceived();
    await this.getDay();
    await this.getAllTrackArea();
    await this.getKeyWorkMonitorConfigs();

    await this.getTrainsetLocationConfigs();
    await this.getTaskFlowConfigList();

    await this.getKeyWorkExtraColumnList();
    this.getConfigFromLocal();
    this.timer = setInterval(() => {
      this.time1 = dayjs().format('YYYY-MM-DD');
      this.time2 = dayjs().format('HH:mm:ss');
    }, 1000);

    this.getTrackPowerInfo(this.allTrackStr);
    this.getKeyWorkFlowRunInfoWithTrainsetList();

    this.startTrackChange();

    this.refreshTimer = setInterval(() => {
      this.getTrackPowerInfo(this.allTrackStr);
      this.getKeyWorkFlowRunInfoWithTrainsetList();
    }, this.dataRefreshTime * 1000);

    this.$once('hook:beforeDestroy', () => {
      this.timer = null;
      this.pageChangetimer = null;
      this.refreshTimer = null;
    });
  },
  mounted() {
    // table滚动条自动滚动
    this.$nextTick(() => {
      this.scrollTimer = setInterval(() => {
        if(this.move && this.pageTotal > 0) {
         for(flowId in this.trainsetIdFlowIdDataMap[this.train.trainsetId]) {
            this.$refs[`keyWorkTableRef${flowId}`][0].bodyWrapper.scrollTop = this.$refs[`keyWorkTableRef${flowId}`][0].bodyWrapper.scrollTop + 1
            let flag = this.$refs[`keyWorkTableRef${flowId}`][0].bodyWrapper.scrollHeight - this.$refs[`keyWorkTableRef${flowId}`][0].bodyWrapper.scrollTop === this.$refs[`keyWorkTableRef${flowId}`][0].bodyWrapper.clientHeight
            if(flag) {
              setTimeout(() => {
                this.$refs[`keyWorkTableRef${flowId}`][0].bodyWrapper.scrollTop = 0
              }, 1000);
            }
            document.getElementById(`keyWorkTableId${flowId}`).addEventListener("mouseover", () => {
              this.move = false
            })
            document.getElementById(`keyWorkTableId${flowId}`).addEventListener("mouseleave", () => {
              this.move = true
            })
         }
        }
      }, 100);
    })
    // 监听全屏
    window.addEventListener('resize', this.resize);
    this.$once('hook:beforeDestroy', () => {
      this.scrollTimer = null
      window.removeEventListener('resize', this.resize);
    });
  },
  methods: {
    // 获取当前登录人运用所编码
    async getUnitCode() {
      let data = await getUnitCode();
      this.unitCode = data;
    },

    async getKeyWorkMonitorConfigs() {
      try {
        this.keyWorkMonitorConfigs = await getKeyWorkMonitorConfigs();
      } catch (error) {
        console.log(error);
      } finally {
        if (this.keyWorkMonitorConfigs.length === 0) {
          // 无配置初始化
          this.keyWorkMonitorConfigs = this.trackList.map((item) => {
            return {
              paramName: item.trackCode,
              paramValue: '1',
            };
          });
        }
      }
    },
    getTableHeight(flowInfo) {
      const flowList = Object.keys(flowInfo);
      const num = flowList.length;
      return 100 / num + '%';
    },
    // 出入所车组
    async getTrainsetListReceived() {
      this.trainGroupNameList = await getTrainsetListReceived({
        unitCode: this.unitCode,
      });
    },
    // 获取数据刷新时间
    async getTrainsetLocationConfigs() {
      const res = await getTrainsetLocationConfigs();
      const dataRefresh = res.find((item) => item.paramName === 'DataRefreshTime');
      this.dataRefreshTime = Number(dataRefresh.paramValue) || 60;
    },
    openCommonDialog() {
      this.commonDialog.trackShowConfig = copyData(this.keyWorkMonitorConfigsMap);
      this.commonDialog.visible = true;
    },
    setTrackShowCommonConfig({ trackCode, value }) {
      this.$set(this.commonDialog.trackShowConfig, trackCode, value);
    },
    async updCommonConfigBtn() {
      const configList = this.trackList.map((item, index) => {
        return {
          id: this.keyWorkMonitorConfigsIdMap[item.trackCode],
          paramName: item.trackCode,
          paramValue: this.commonDialog.trackShowConfig[item.trackCode] === false ? '0' : '1',
          sortId: index,
        };
      });

      await setKeyWorkMonitorConfigs(configList);
      this.getKeyWorkMonitorConfigs();
      this.commonDialog.visible = false;
    },
    // 页面窗口变化
    resize() {
      if (
        document.fullscreenElement ||
        document.webkitFullscreenElement ||
        document.mozFullscreenElement ||
        document.msFullscreenElement
      ) {
        this.isFullScreen = true;
      } else {
        this.isFullScreen = false;
      }
    },
    // 进入全屏

    requestFullscreen() {
      let element = document.documentElement;
      if (element.requestFullscreen) {
        element.requestFullscreen();
      } else if (element.webkitRequestFullScreen) {
        element.webkitRequestFullScreen();
      } else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
      } else if (element.msRequestFullscreen) {
        // IE11
        element.msRequestFullscreen();
      }
    },

    // 退出全屏

    exitFullscreen() {
      if (document.exitFullscreen) {
        document.exitFullscreen();
      } else if (document.webkitCancelFullScreen) {
        document.webkitCancelFullScreen();
      } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
      } else if (document.msExitFullscreen) {
        document.msExitFullscreen();
      }
    },
    // 右上角的全屏点击
    fullscreenClick() {
      if (!this.isFullScreen) {
        this.requestFullscreen();
      } else {
        this.exitFullscreen();
      }
    },
    async getTaskFlowConfigList() {
      this.planlessKeyList = await getTaskFlowConfigList();
    },
    startTrackChange() {
      clearInterval(this.pageChangetimer);
      if (this.localConfig.trackRefreshTime != 0) {
        this.pageChangetimer = setInterval(() => {
          this.pageNum++;
          if (this.pageNum > this.pageTotal) {
            this.pageNum = 1;
          }
        }, this.localConfig.trackRefreshTime * 1000);
      }
    },
    tableRowClassName({ row, rowIndex }) {
      if (rowIndex % 2 === 1) {
        return 'odd';
      } else {
        return 'even';
      }
    },

    // 获取日计划
    async getDay() {
      const {
        data: { dayPlanId },
      } = await getDay();
      this.dayPlanId = dayPlanId;
      let dayPlanArr = dayPlanId.split('-');
      const datType = dayPlanArr.pop();
      const time = dayPlanArr.join('-');
      if (datType == '00') {
        this.dayPlanIds = [`${dayjs(time).subtract(1, 'day').format('YYYY-MM-DD')}-01`, dayPlanId];
      } else {
        this.dayPlanIds = [`${dayjs(time).format('YYYY-MM-DD')}-00`, dayPlanId];
      }
    },
    // 获取显示股道
    async getAllTrackArea() {
      const { trackAreas } = await getAllTrackArea();

      this.allTrackAreaList = trackAreas;
    },

    // 获取股道供断电信息
    async getTrackPowerInfo(trackCodeList) {
      const res = await getTrackPowerInfo({
        unitCodeList: this.unitCode,
        trackCodeList,
      });
      this.trackPowerList = res;
    },
    // 获取车组信息(包含表格数据)
    async getKeyWorkFlowRunInfoWithTrainsetList() {
      this.allTrainsetList = await getKeyWorkFlowRunInfoWithTrainsetList(this.dayPlanIds.toString(), this.unitCode, !this.localConfig.queryPastFlow);
      this.getConfigFromLocal();
    },

    getColumnLabel(row, { key, property }) {
      if (key === 'FUNCTION_CLASS') {
        return (
          this.columnListMap[`${key}FlatList`] &&
          this.columnListMap[`${key}FlatList`].find((item) => item.code == row[property]) &&
          this.columnListMap[`${key}FlatList`].find((item) => item.code == row[property]).name
        );
      } else if (key === 'BATCH_BOM_NODE_CODE') {
        return (
          this.columnListMap[`${row.trainsetId}${key}FlatList`] &&
          this.columnListMap[`${row.trainsetId}${key}FlatList`].find((item) => item.code == row[property]) &&
          this.columnListMap[`${row.trainsetId}${key}FlatList`].find((item) => item.code == row[property]).name
        );
      } else {
        return (
          this.columnListMap[`${key}List`] &&
          this.columnListMap[`${key}List`].find((item) => item.value == row[property]) &&
          this.columnListMap[`${key}List`].find((item) => item.value == row[property]).label
        );
      }
    },
    async getKeyWorkExtraColumnList() {
      this.columns = await getKeyWorkExtraColumnList();
      this.columns.forEach(async (column) => {
        column.fixed = false;
        if (['options', 'specialOptions'].includes(column.type)) {
          // 配置框表单
          let list = [];
          if (this.specialColumnKeys.includes(column.key)) {
            if (column.key === 'FUNCTION_CLASS') {
              list = await getFunctionClass();
              this.$set(this.columnListMap, `${column.key}FlatList`, this.tranListToFlatData(list));
              this.addListLeaf(list);
            }
          } else {
            if (column.key !== 'CAR') {
              list = await getKeyWorkExtraColumnValueList({
                columnKey: column.key,
              });
            }
          }

          this.$set(this.columnListMap, `${column.key}List`, list);
        }
      });
    },
    addListLeaf(list) {
      list.forEach((item) => {
        if (item.children && item.children.length > 0) {
          item.leaf = false;
          this.addListLeaf(item.children);
        } else {
          item.leaf = true;
        }
      });
    },
    tranListToFlatData(list) {
      let arr = [];
      list.forEach((item) => {
        arr.push({
          id: item.id,
          code: item.code,
          disabled: item.disabled,
          name: item.name,
        });
        if (item.children && item.children.length > 0) {
          arr = [...arr, ...this.tranListToFlatData(item.children)];
        }
      });
      return arr;
    },

    openMonitorDialog() {
      // 第三方调用页面时不润许使用“系统配置”
      // if (this.paramsMap.trainsetId) return;
      this.monitorDialog.trackShowConfig = copyData(this.localConfig.trackShowConfig);
      this.monitorDialog.trackRefreshTime = this.localConfig.trackRefreshTime;
      this.monitorDialog.flowId = this.planlessKeyList[0] && this.planlessKeyList[0].id;
      this.monitorDialog.fixedNum = this.localConfig.fixedNum;
      this.monitorDialog.flowConfig = copyData(this.localConfig.flowConfig);
      this.monitorDialog.TrackPositionChange = this.localConfig.TrackPositionChange;
      this.monitorDialog.queryPastFlow = this.localConfig.queryPastFlow;

      this.monitorDialog.visible = true;
    },
    setTrackShowConfig({ trackCode, value }) {
      this.$set(this.monitorDialog.trackShowConfig, trackCode, value);
    },
    async updMonitorConfigBtn() {
      // 保存前校验
      let numList = []
      for (const flowId in this.monitorDialog.flowConfig) {
        numList.push(this.monitorDialog.flowConfig[flowId].rightList.length)
      }
      if (this.monitorDialog.fixedNum > Math.min.apply(null, numList)) {
        return this.$message.error('固定数量不能大于显示列数!');
      }

      this.getKeyWorkMonitorConfigs();

      this.setConfigToLocal();

      this.getConfigFromLocal();

      this.getKeyWorkFlowRunInfoWithTrainsetList();

      this.startTrackChange();
      // 刷新table
      this.tableReresh = new Date().getTime()
      this.monitorDialog.visible = false;
    },

    updMonitorCancel() {
      this.monitorDialog.visible = false;
    },

    columnChange({ left, right }) {
      this.$set(this.monitorDialog.flowConfig[this.monitorDialog.flowId], 'leftList', left);
      this.$set(this.monitorDialog.flowConfig[this.monitorDialog.flowId], 'rightList', right);
    },

    getConfigFromLocal() {
      const keyWorkConfigStr = localStorage.getItem('keyWorkConfig');
      // 第三方调用页面时不润许使用“系统配置”
      if (keyWorkConfigStr) {
        this.localConfig = JSON.parse(keyWorkConfigStr);
      } else {
        if (this.paramsMap.trainsetId) {
          this.trackList.forEach((track) => {
            this.$set(this.localConfig.trackShowConfig, track.trackCode, true);
          });
        } else {
          this.localConfig.trackShowConfig = copyData(this.keyWorkMonitorConfigsMap);
        }
        this.localConfig.fixedNum = 0;
        this.localConfig.trackRefreshTime = 60;
        this.localConfig.TrackPositionChange = true;
        this.localConfig.queryPastFlow = true;
        for (const key in this.localConfig.flowConfig) {
          this.$set(this.localConfig.flowConfig[key], 'leftList', []);
          this.$set(this.localConfig.flowConfig[key], 'rightList', [
            ...this.staticColumns,
            ...this.columns,
            ...this.planlessKeyIdNodesMap[key].map((node) => {
              return {
                key: node.id,
                label: node.name,
                property: node.id,
                type: 'node',
              };
            }),
          ]);
        }
        
      }
      let flowIds = (() => {
        // 包含当前生效的关键作业流程配置的id和正在运行的关键作业的对应的流程配置的id
        let runtimeFlowIds = Object.keys(this.planlessKeyIdNodesMap)
        // 已经配置过了的流程配置的id
        let configedFlowIds = Object.keys(this.localConfig.flowConfig)
        return Array.from(new Set([...runtimeFlowIds,...configedFlowIds]))
      })()
      // 更新修复flowConfig
      let flowConfig = {};
      flowIds.forEach(flowId => {
        if (this.localConfig.flowConfig[flowId]) {
          flowConfig[flowId] = this.localConfig.flowConfig[flowId];
        } else {
          flowConfig[flowId] = {};
          flowConfig[flowId].leftList = [];
          flowConfig[flowId].rightList = [
            ...this.staticColumns,
            ...this.columns,
            ...this.planlessKeyIdNodesMap[flowId].map((node) => {
              return {
                key: node.id,
                label: node.name,
                property: node.id,
                type: 'node',
              };
            }),
          ];
        }
      })
      this.$set(this.localConfig, 'flowConfig', flowConfig);
    },
    setConfigToLocal() {
      for (const flowId in this.monitorDialog.flowConfig) {
        const ele = this.monitorDialog.flowConfig[flowId];
        for (let i = 0; i < ele.rightList.length; i++) {
          this.$set(ele.rightList[i], 'fixed', false);
        }
        for (let i = 0; i < this.monitorDialog.fixedNum - 1; i++) {
          this.$set(ele.rightList[i], 'fixed', true);
        }
      }

      const keyWorkConfigStr = JSON.stringify({
        trackShowConfig: this.monitorDialog.trackShowConfig,
        trackRefreshTime: this.monitorDialog.trackRefreshTime,
        fixedNum: this.monitorDialog.fixedNum,
        flowConfig: this.monitorDialog.flowConfig,
        TrackPositionChange: this.monitorDialog.TrackPositionChange,
        queryPastFlow: this.monitorDialog.queryPastFlow
      });
      localStorage.setItem('keyWorkConfig', keyWorkConfigStr);
    },
  },
});
