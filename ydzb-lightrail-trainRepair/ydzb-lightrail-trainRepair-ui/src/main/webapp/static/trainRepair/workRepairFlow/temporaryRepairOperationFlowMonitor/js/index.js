window.app = new Vue({
  el: '#temporaryRepairOperationFlowMonitor',
  data() {
    return {
      unitCode: '',
      params: '',
      isFullScreen: false,
      dayPlanIds: [],
      baseImgPath: ctxPath + '/static/trainRepair/workRepairFlow/keyWorkFlowMonitor/img',
      timer: null,
      time1: '',
      time2: '',
      localConfig: {
        trackShowConfig: {},
        trackRefreshTime: '60',
      },
      allTrackAreaList: [],
      allTrainsetList: [],
      trackPowerList: [],
      monitorDialog: {
        visible: false,
        trackShowConfig: {},
        trackRefreshTime: '',
        // 股道位置切换
        TrackPositionChange: true,
        // 股道位置切换重置
        oldTrackPositionChange: true,
      },
      pageNum: 1,
      trackChangetimer: null,
      scrollHeight: '0px',
      foucsNodeId: '',
      nodeRecord: '无',
      refreshTimer: null,
      dataRefreshTime: 60,
      msg: ''
    };
  },
  computed: {
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
    allTrackAreaSortList({ allTrackAreaList }) {
      let data = copyData(allTrackAreaList);

      data.forEach((trackArea) => {
        // trackArea.lstTrackInfo.lstTrackPositionInfo
        trackArea.lstTrackInfo.sort((a, b) => a.sort - b.sort);
      });
      data.sort((a, b) => a.sort - b.sort);
      return data;
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

    trackList({ allTrackAreaSortList }) {
      let trackList = allTrackAreaSortList.map((item) => item.lstTrackInfo).flat();
      return trackList;
    },

    // 所有股道code
    allTrackStr({ trackList }) {
      return trackList.map((item) => item.trackCode).toString();
    },

    trainsetIdInfoMap({ allTrainsetList }) {
      // 所有trainsetId =》trainsetInfo
      return allTrainsetList.reduce((prev, item) => {
        prev[item.trainsetId] = item;

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
    trainsetList({ allTrainsetList, localConfig, paramsMap }) {
      let getAllTrackArea = JSON.parse(JSON.stringify(allTrainsetList));
      let list;
      if (paramsMap.trainsetId) {
        list = getAllTrackArea.filter((item) => item.trainsetId === paramsMap.trainsetId);
      } else {
        list = getAllTrackArea.filter((item) => localConfig.trackShowConfig[item.trackCode]);
      }
      if (!this.monitorDialog.oldTrackPositionChange) {
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
    pageTotal({ trainsetList }) {
      return trainsetList.length;
    },
    train({ trainsetList, pageNum }) {
      return trainsetList[pageNum - 1] || {};
    },
    trainsetIdTemporaryMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        prev[item.trainsetId] = item.nodeInfoForSimpleShows;
        return prev;
      }, {});
    },
    flowList({ trainsetIdTemporaryMap, train }) {
      return trainsetIdTemporaryMap[train.trainsetId] || [];
    },
    lasetedNode({ flowList }) {
      let copyNodeList = copyData(flowList);
      copyNodeList.reverse();
      // console.log(copyNodeList)
      // let arr = copyNodeList.filter(item => {
      //   return [item].some(node => !(!node.finished && node.couldDisposeRoleIds.length == 0))
      // })
      // console.log(arr)
      // for (let i = 0; i < arr.length; i++) {
      //   for (let j = 0; j < copyNodeList.length; j++) {
      //     if (JSON.stringify(arr[i]) === JSON.stringify(copyNodeList[j])) {
      //       let nodePrev = !(copyNodeList[j - 1].finished && copyNodeList[j - 1].couldDisposeRoleIds.length !== 0)
      //       let nodeNext = !(copyNodeList[j + 1].finished && copyNodeList[j + 1].couldDisposeRoleIds.length !== 0)
      //       console.log(nodePrev)
      //       console.log(nodePrev)
      //       if (nodePrev && nodeNext) {
      //         arr.splice(i, 1, false)
      //       }
      //     }
      //   }
      // }
      // console.log(arr)
      return copyNodeList.find((item) => !(!item.finished && item.couldDisposeRoleIds.length == 0)) || {};
    },
    flowMap({ flowList }) {
      return flowList.reduce((prev, item) => {
        prev[item.id] = item.remark;
        return prev;
      }, {});
    },
    nodeRecordList({ flowList }) {
      return flowList
        .map((node) => {
          return node.nodeRecords;
        })
        .flat()
        .sort((a, b) => {
          return new Date(b.recordTime) - new Date(a.recordTime);
        });
    },
    trainsetIdTaskPacketMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        prev[item.trainsetId] = item.taskPacketEntities;
        return prev;
      }, {});
    },
    packetList({ trainsetIdTaskPacketMap, nowTrain }) {
      return trainsetIdTaskPacketMap[nowTrain.trainsetId];
    },

    // 所有节点id：name
    nodeIdNameMap({ flowList }) {
      return flowList.reduce((prev, item) => {
        prev[item.id] = item.name;
        item.children.forEach((subNode) => {
          prev[subNode.id] = subNode.name;
        });
        return prev;
      }, {});
    },
    // 父子节点关系
    subNodeWithParentMap({ flowList }) {
      return flowList.reduce((prev, item) => {
        item.children.forEach((subNode) => {
          prev[subNode.id] = item.id;
        });
        return prev;
      }, {});
    },
  },
  watch: {
    allTrackStr(val) {
      if (val) {
        this.getTrackPowerInfo();
        this.getTemporaryFlowRunInfoWithTrainsetList();
      }
    },
    train(train) {
      this.scrollLastedNode();
    },
  },
  async created() {
    this.params = location.search;
    await this.getUnitCode();
    await this.getTrainsetLocationConfigs();
    this.timer = setInterval(() => {
      this.time1 = dayjs().format('YYYY-MM-DD');
      this.time2 = dayjs().format('HH:mm:ss');
    }, 1000);
    await this.getDay();
    await this.getAllTrackArea();
    this.getConfigToLocal();
    this.startTrackChange();
    this.refreshTimer = setInterval(() => {
      this.getTrackPowerInfo();
      this.getTemporaryFlowRunInfoWithTrainsetList();
    }, this.dataRefreshTime * 1000);
    this.$once('hook:beforeDestroy', () => {
      this.timer = null;
      this.trackChangetimer = null;
      this.refreshTimer = null;
    });
  },
  mounted() {
    // 监听全屏
    window.addEventListener('resize', this.resize);
    this.$once('hook:beforeDestroy', () => {
      window.removeEventListener('resize', this.resize);
    });
  },
  methods: {
    // 获取当前登录人运用所编码
    async getUnitCode() {
      let data = await getUnitCode();
      this.unitCode = data;
    },

    nodeNetalis(node) {
      return node.nodeRecords
        ? node.nodeRecords
            .map((record) => {
              return `打卡者：${record.workerName} 打卡角色：${record.roleName} 打卡时间：${record.recordTime}`;
            })
            .toString()
        : '';
    },

    // 获取数据刷新时间
    async getTrainsetLocationConfigs() {
      const res = await getTrainsetLocationConfigs();
      const dataRefresh = res.find((item) => item.paramName === 'DataRefreshTime');
      this.dataRefreshTime = Number(dataRefresh.paramValue) || 60;
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

    scrollTo(type, id) {
      const targetId = document.querySelector(`#${type}-${id}`);
      targetId.scrollIntoView({
        behavior: 'smooth',
        block: 'center',
        inline: 'start',
      });
    },

    scrollLeft(type, id) {
      const targetclass = document.getElementsByClassName(`flow-wrapper`);
      const targetId = document.querySelector(`#${type}-${id}`);

      targetclass[0].scrollLeft = ((targetId.offsetLeft / targetclass[0].scrollWidth) * targetclass[0].offsetWidth) / 2;
    },

    scrollLastedNode() {
      this.$nextTick().then(() => {
        if (!this.lasetedNode.id) return;
        this.nodeChange(this.lasetedNode.id);

        this.scrollLeft('flow', this.lasetedNode.id);
        this.scrollTo('node', this.lasetedNode.id);
      });
    },

    startTrackChange() {
      clearInterval(this.trackChangetimer);
      this.trackChangetimer = setInterval(() => {
        if (this.pageNum + 1 > this.pageTotal) {
          this.pageNum = 1;
        } else {
          this.pageNum++;
        }

        this.getTrackPowerInfo();
        this.getTemporaryFlowRunInfoWithTrainsetList();

        this.foucsNodeId = '';
        this.nodeRecord = '';
      }, this.localConfig.trackRefreshTime * 1000);
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
    async getTrackPowerInfo() {
      const res = await getTrackPowerInfo({
        unitCodeList: this.unitCode,
        trackCodeList: '',
      });
      this.trackPowerList = res;
    },
    async getTemporaryFlowRunInfoWithTrainsetList() {
      try {
        this.allTrainsetList = await getTemporaryFlowRunInfoWithTrainsetList(
          this.dayPlanIds.toString(),
          this.unitCode,
          this.allTrackStr
        );
        if(this.allTrainsetList.length == 0) {
          this.msg = '没有符合条件的流程信息'
        } else {
          this.msg = ''
        }
      } catch (error) {
        this.msg = '没有符合条件的流程信息'
      }
    },
    openMonitorDialog() {
      this.monitorDialog.trackShowConfig = copyData(this.localConfig.trackShowConfig);
      this.monitorDialog.trackRefreshTime = this.localConfig.trackRefreshTime;
      this.monitorDialog.visible = true;
    },
    setTrackShowConfig({ trackCode, value }) {
      this.$set(this.monitorDialog.trackShowConfig, trackCode, value);
    },
    updMonitorConfigBtn() {
      this.monitorDialog.visible = false;
      this.monitorDialog.oldTrackPositionChange = this.monitorDialog.TrackPositionChange;

      this.setConfigToLocal();
      this.getConfigToLocal();
    },

    updMonitorCancel() {
      this.monitorDialog.visible = false;
      this.monitorDialog.TrackPositionChange = this.monitorDialog.oldTrackPositionChange;
    },

    getConfigToLocal() {
      const temporaryRepairConfigStr = localStorage.getItem('temporaryRepairConfig');
      if (temporaryRepairConfigStr && !this.paramsMap.trainsetId) {
        this.localConfig = JSON.parse(temporaryRepairConfigStr);
      } else {
        this.trackList.forEach((track) => {
          this.$set(this.localConfig.trackShowConfig, track.trackCode, true);
        });
      }
    },

    setConfigToLocal() {
      const temporaryRepairConfigStr = JSON.stringify({
        trackShowConfig: this.monitorDialog.trackShowConfig,
        trackRefreshTime: this.monitorDialog.trackRefreshTime,
      });
      localStorage.setItem('temporaryRepairConfig', temporaryRepairConfigStr);
    },

    nodeChange(id) {
      this.foucsNodeId = id;
      this.nodeRecord = this.flowMap[id] || '无';
    },
  },
  filters: {
    packetFilter(packet) {
      return packet
        .map((item) => {
          return `作业任务名称：${item.packetName} 作业班组：${item.repairDeptName} 作业人员：${item.workers}`;
        })
        .join(';');
    },
  },
});
