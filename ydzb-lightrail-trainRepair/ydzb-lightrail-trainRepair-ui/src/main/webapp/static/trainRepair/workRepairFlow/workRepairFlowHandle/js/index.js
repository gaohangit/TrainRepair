let copyDataSimple = (data) => JSON.parse(JSON.stringify(data));
window.main = new Vue({
  el: '#workRepairFlowHandle',
  data() {
    var valiWorkTeam = (rule, value, callback) => {
      if (Object.keys(value).length == 0) {
        return callback(new Error('请选择打卡班组'));
      } else {
        callback();
      }
    };

    return {
      unitCode: '',
      unitName: '',
      uploadLimit: '',
      dayPlanId: '',
      userInfo: {},
      roleList: [],
      flowState: {
        '-1': '【未开始】',
        0: '【正在进行】',
        1: '【已完成】',
      },
      dayPlanIdList: [],
      headerFormData: {
        trainsetId: '',
        dayPlanId: '',
      },
      headerFormRules: {
        trainsetId: [],
        dayPlanId: [],
      },
      emptyTrackShow: false,
      queryTrackCode: '',
      trainGroupNameList: [],
      trackPageNum: 1,
      trackPageSize: 4,
      allTrackAreaData: [],
      allTrainsetList: [],
      trackPowerList: [],

      nodeDealDialog: {
        trainsetInfo: {},
        flowRunInfoForSimpleShows: [],
        nodeDealDialogVisible: false,
        activeTabName: '',
        fileList: [],
        uploadedFileInfos: [],
        currentUploaded: [],
        nodeDealFormData: {
          nodeId: '',
          oriNodeId: '',
          remark: '',
        },
        nodeRecords: [],
      },

      // 选择作业班组部分
      workTeamDialog: {
        visible: false,
        selectWorkTeamlist: [],
        workTeamFormData: {
          selectWorkTeam: {},
        },
        workTeamFormRules: {
          selectWorkTeam: [
            {
              validator: valiWorkTeam,
              trigger: 'select',
            },
          ],
        },
      },

      // 选择作业人员部分
      roleDialog: {
        visible: false,
        selectRolelist: [],
        roleFormData: {
          roleId: '',
          oriRoleId: '',
        },
        roleFormRules: {
          roleId: [
            {
              required: true,
              message: '请选择打卡角色',
              trigger: 'select',
            },
          ],
        },
      },

      lookupNodeDealInfoDialog: {
        visible: false,
      },
      lookupFlowDataDialog: {
        visible: false,
        flowData: {
          nodes: [
            {
              childFlowId: '',
              disposeAfterSkip: false,
              disposeSubflow: '',
              excludeRoles: [],
              flowId: '7f1b5c786d7d4ef5b670b7af8b665b2b',
              functionType: '',
              id: '13b55e3e-d879-4067-8703-ee7765f81cbe',
              minDisposeNum: 0,
              minIntervalRestrict: 0,
              name: '1',
              onlyDispatching: false,
              overtimeWaring: 0,
              recommendedPicNum: 0,
              remark: '',
              roleConfigs: [],
              skip: false,
            },
            {
              childFlowId: '',
              disposeAfterSkip: false,
              disposeSubflow: '',
              excludeRoles: [],
              flowId: '7f1b5c786d7d4ef5b670b7af8b665b2b',
              functionType: '',
              id: '99fc521a-af32-4ef1-8eb7-7720c3606aa0',
              minDisposeNum: 0,
              minIntervalRestrict: 0,
              name: '3',
              onlyDispatching: false,
              overtimeWaring: 0,
              recommendedPicNum: 0,
              remark: '',
              roleConfigs: [],
              skip: false,
            },
            {
              childFlowId: '',
              disposeAfterSkip: false,
              disposeSubflow: '',
              excludeRoles: [],
              flowId: '7f1b5c786d7d4ef5b670b7af8b665b2b',
              functionType: '',
              id: '5daff92c-3d51-4193-8d58-f1ee2032308a',
              minDisposeNum: 0,
              minIntervalRestrict: 0,
              name: '2',
              onlyDispatching: false,
              overtimeWaring: 0,
              recommendedPicNum: 0,
              remark: '',
              roleConfigs: [],
              skip: false,
            },
            {
              childFlowId: '',
              disposeAfterSkip: false,
              disposeSubflow: '',
              excludeRoles: [],
              flowId: '7f1b5c786d7d4ef5b670b7af8b665b2b',
              functionType: '',
              id: 'cc8d1962-9f04-4054-8115-c69f8e5f8548',
              minDisposeNum: 0,
              minIntervalRestrict: 0,
              name: '4',
              onlyDispatching: false,
              overtimeWaring: 0,
              recommendedPicNum: 0,
              remark: '',
              roleConfigs: [],
              skip: false,
            },
          ],
          nodeVectors: [
            {
              flowId: '7f1b5c786d7d4ef5b670b7af8b665b2b',
              fromNodeId: '13b55e3e-d879-4067-8703-ee7765f81cbe',
              id: 'b8a9931d-e3e1-49fd-ac50-57d3aaf31438',
              nodeVectorExtraInfoList: [
                {
                  nodeVectorId: '',
                  synDate: '',
                  synFlag: '0',
                  type: '',
                  value: '',
                },
              ],
              synDate: '',
              synFlag: '0',
              toNodeId: '5daff92c-3d51-4193-8d58-f1ee2032308a',
            },
            {
              flowId: '7f1b5c786d7d4ef5b670b7af8b665b2b',
              fromNodeId: '5daff92c-3d51-4193-8d58-f1ee2032308a',
              id: '65187910-0282-413b-9a18-391033b9596a',
              nodeVectorExtraInfoList: [
                {
                  nodeVectorId: '',
                  synDate: '',
                  synFlag: '0',
                  type: '',
                  value: '',
                },
              ],
              synDate: '',
              synFlag: '0',
              toNodeId: '99fc521a-af32-4ef1-8eb7-7720c3606aa0',
            },
            {
              flowId: '7f1b5c786d7d4ef5b670b7af8b665b2b',
              fromNodeId: '99fc521a-af32-4ef1-8eb7-7720c3606aa0',
              id: 'abf0fedf-9ef8-471c-b4e2-58e49527a7fb',
              nodeVectorExtraInfoList: [
                {
                  nodeVectorId: '',
                  synDate: '',
                  synFlag: '0',
                  type: '',
                  value: '',
                },
              ],
              synDate: '',
              synFlag: '0',
              toNodeId: 'cc8d1962-9f04-4054-8115-c69f8e5f8548',
            },
          ],
          parallelFlowConfigs: [],
          subflowInfoList: [],
          pairNodeInfo: [],
        },
      },
      flowChangeDialog: {
        visible: false,
        type: '',
        name: '',
        state: false,
        tableData: [],
        flowTypeList: [],
      },
      forceEndFlowDialog: {
        visible: false,
        formData: {
          remark: '',
        },
        formRules: {
          remark: [],
        },
      },
      nodeDealDetailDialog: {
        visible: false,
      },

      viewPictureDialog: {
        visible: false,
        detailInfo: {
          title: '',
          pics: [],
          prePics: [],
        },
      },

      workerPicMap: {},
      workerDetail: {},
      imgUrl: '',
      prePics: [],
      flowList: [],
      oriFileUrl: [],
    };
  },
  computed: {
    allTrackAreaSortList({ allTrackAreaData }) {
      let data = copyData(allTrackAreaData);

      data.forEach((trackArea) => {
        // trackArea.lstTrackInfo.lstTrackPositionInfo
        trackArea.lstTrackInfo.sort((a, b) => a.sort - b.sort);
      });
      data.sort((a, b) => a.sort - b.sort);

      return data;
    },
    // 扁平所有的股道信息
    trackList({ allTrackAreaSortList }) {
      return allTrackAreaSortList.map((item) => item.lstTrackInfo).flat();
    },
    allTrackStr({ trackList }) {
      return trackList.map((track) => track.trackCode).toString();
    },
    // 显示的扁平所有的股道信息
    allTrackList({ allTrackAreaSortList, headerFormData, trainsetIdTrackCodeMap, emptyTrackShow, trainsetInTrackMap }) {
      const queryTrackCode = trainsetIdTrackCodeMap[headerFormData.trainsetId] || '';
      let list = [];
      if (queryTrackCode == '' && headerFormData.trainsetId) {
        return [];
      } else if (queryTrackCode == '' && headerFormData.trainsetId == '') {
        list = allTrackAreaSortList.map((item) => item.lstTrackInfo).flat();
      } else {
        list = allTrackAreaSortList
          .map((item) => item.lstTrackInfo)
          .flat()
          .filter((track) => track.trackCode == queryTrackCode);
      }
      if (emptyTrackShow) {
        return list;
      } else {
        return list.filter((item) => trainsetInTrackMap[item.trackCode]);
      }
    },
    // 根据展示条数分组 pageNum=>[]
    groupAllTrackMap({ trackPageSize, allTrackList }) {
      return allTrackList.reduce((prev, item, index) => {
        let pageNum = parseInt(index / trackPageSize) + 1;
        if (!prev[pageNum]) {
          prev[pageNum] = [];
        }
        prev[pageNum].push(item);
        return prev;
      }, {});
    },
    // 总页数
    trackPageCount({ groupAllTrackMap }) {
      return Math.max(...Object.keys(groupAllTrackMap));
    },
    trackPageTotal({ allTrackList }) {
      return allTrackList.length;
    },
    // 车组id=》股道code
    trainsetIdTrackCodeMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        prev[item.trainsetId] = item.trackCode;
        return prev;
      }, {});
    },
    // 含有重联车的股道
    connectTrackCodes({ allTrainsetList, trainsetInTrackMap }) {
      return allTrainsetList.reduce((prev, item) => {
        if (item.isConnect == '1' && trainsetInTrackMap[item.trackCode].length > 1) {
          prev[item.trackCode] = true;
        }
        return prev;
      }, {});
    },
    // {trackCode:[trainsetId]}
    trainsetInTrackMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        if (!prev[item.trackCode]) {
          prev[item.trackCode] = [];
        }
        prev[item.trackCode].push(item.trainsetId);
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
    // 长编车车尾所在列位在视图上不显示
    notShowPositionCodesByLongTail({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        if (item.isLong == '1') {
          prev.push(Number(item.tailDirectionPlaCode));
        }
        return prev;
      }, []);
    },
    // 列位code对trainsetInfo
    positionCodeTrainsetInfoMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        prev[item.headDirectionPlaCode] = item;
        prev[item.tailDirectionPlaCode] = item;
        return prev;
      }, {});
    },
    trainsetIdPacketMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        if (!prev[item.trainsetId]) {
          prev[item.trainsetId] = {};
        }
        item.ztTaskPacketEntities.forEach((packet) => {
          if (!prev[item.trainsetId][packet.repairDeptName]) {
            prev[item.trainsetId][packet.repairDeptName] = [];
          }
          prev[item.trainsetId][packet.repairDeptName].push(packet.packetName);
        });
        return prev;
      }, {});
    },
    roleCodeNameMap({ roleList }) {
      return roleList.reduce((prev, item) => {
        prev[item.code] = item.name;
        return prev;
      }, {});
    },
    roleCodeInfoMap({ roleList }) {
      return roleList.reduce((prev, item) => {
        prev[item.code] = {
          roleId: item.code,
          roleName: item.name,
          type: item.type,
        };
        return prev;
      }, {});
    },
    flowRunInfoMap({ nodeDealDialog }) {
      return nodeDealDialog.flowRunInfoForSimpleShows.reduce((prev, item) => {
        if (item.id) {
          prev[item.id] = item;
        } else {
          prev[item.flowId] = item;
        }
        return prev;
      }, {});
    },
    flowRun({ flowRunInfoMap, nodeDealDialog }) {
      if (nodeDealDialog.activeTabName) {
        return flowRunInfoMap[nodeDealDialog.activeTabName] || {};
      } else {
        return {};
      }
    },
    lasetedNode({ flowRun }) {
      if (!flowRun.nodeList) return [];
      let copyNodeList = copyData(flowRun.nodeList);
      copyNodeList.reverse();
      return copyNodeList.find((item) => !(!item.finished && item.couldDisposeRoleIds.length == 0)) || {};
    },
    dealNode({ flowRun, nodeDealDialog }) {
      if (nodeDealDialog.nodeDealFormData.nodeId) {
        let nodeListFind = flowRun.nodeList.find((node) => node.id === nodeDealDialog.nodeDealFormData.nodeId);
        nodeListFind = JSON.parse(JSON.stringify(nodeListFind));
        return nodeListFind;
      } else {
        return {};
      }
    },
    nodeColumns({ flowRun, roleCodeNameMap }) {
      if (!flowRun.nodeList) return [];
      let roleIds = [
        ...new Set(
          flowRun.nodeList
            .map((node) => {
              return node.roleConfigs.map((role) => {
                return role.roleId;
              });
            })
            .flat()
        ),
      ];
      roleIds = roleIds.filter((item) => roleCodeNameMap[item]);
      return roleIds.map((id) => {
        return {
          id: id,
          label: roleCodeNameMap[id],
          prop: id,
        };
      });
    },
    nodeDealData({ flowRun }) {
      if (!flowRun.nodeList) return [];
      return flowRun.nodeList.map((node) => {
        const record = node.nodeRecords.reduce((prev, item) => {
          if (prev[item.roleId]) {
            prev[item.roleId] = prev[item.roleId] + ',' + item.workerName;
          } else {
            prev[item.roleId] = item.workerName;
          }

          return prev;
        }, {});
        const roleWorkerPicUrls = node.nodeRecords.reduce((prev, item) => {
          if (!prev[item.roleId]) {
            prev[item.roleId] = {};
          }
          prev[item.roleId] = node.nodeRecords.reduce((prev1, item1) => {
            if (!prev1[item1.workerId]) {
              prev1[item1.workerId] = [];
            }
            if(item.roleId == item1.roleId) {
              prev1[item1.workerId] = [
                ...prev1[item1.workerId],
                ...item1.pictureUrls.map((item1) => {
                  let url = item1.relativeUrl.split('/');
                  return {
                    name: url[url.length - 1],
                    url: `/storageTrainRepair/${item1.relativeUrl}`,
                  };
                }),
              ];
            }
            return prev1;
          }, {});
          // let obj2 = {};
          // for (const key in obj1) {
          //   const element = obj1[key];
          //   if (element.length > 0) {
          //     obj2[key] = element;
          //   }
          // }
          // prev[item.roleId] = obj2;
          return prev;
        }, {});

        const roleWorkerDetails = node.nodeRecords.reduce((prev, item) => {
          if (!prev[item.roleId]) {
            prev[item.roleId] = {};
          }

          if (!prev[item.roleId][item.workerId]) {
            prev[item.roleId][item.workerId] = [];
          }

          prev[item.roleId][item.workerId] = `${item.workerName}在 ${item.recordTime} 打卡`;

          return prev;
        }, {});

        return {
          id: node.id,
          node: node.name,
          ...record,
          roleWorkerPicUrls,
          roleWorkerDetails,
        };
      });
    },
    nodeConfigRoleList({ dealNode }) {
      return dealNode.roleConfigs || [];
    },
    flowChangeDialogTitle({ flowChangeDialog }) {
      if (flowChangeDialog.type == 'spare') {
        return '备用流程切换';
      } else {
        return '强制流程切换';
      }
    },
    workerIdNameMap({ flowRun }) {
      return flowRun.nodeList.reduce((prev, item) => {
        item.nodeRecords.forEach((record) => {
          prev[record.workerId] = record.workerName;
        });
        return prev;
      }, {});
    },
    workerIdDetail({ flowRun }) {
      if (!flowRun.nodeList) return [];
      return flowRun.nodeList.map((node) => {
        const record = node.nodeRecords.reduce((prev, item) => {
          prev[item.roleId] = `${item.workerName}在 ${item.recordTime} 打卡`;
          return prev;
        }, {});
        return {
          id: node.id,
          node: node.name,
          ...record,
          roleWorkerPicUrls,
          roleWorkerDetails,
        };
      });
    },
    pageTitle: (() => {
      let defaultPageTitleMap = {
        ONE: '一级修流程确认',
        TWO: '二级修流程确认',
        TEMPORARY: '临修作业流程确认',
        HOSTLING: '整备作业流程确认',
      };
      let defaultPageTitle = defaultPageTitleMap[flowPageCode] || '作业流程确认';
      return GeneralUtil.getObservablePageTitleGetter(defaultPageTitle);
    })(),

    flowTypeFilter() {
      return function (val) {
        let flowType = this.flowChangeDialog.flowTypeList.filter((item) => {
          if (item.code == val) {
            return item;
          }
        });
        return flowType[0] && flowType[0].name;
      };
    },
  },
  async created() {
    await this.getUnitCode();
    await this.getUnitName();
    await this.getDay();
    await this.getUser();
    this.getPostRoleList();

    this.getTrainsetListReceived();
    this.getAllTrackArea();
    // this.getPictureUploadMax();
    await this.getFlowRunInfos({
      flowPageCode,
      unitCode: this.unitCode,
      dayPlanId: this.headerFormData.dayPlanId,
      trainsetId: '',
    });
    await this.getFlowTypeList();
  },

  methods: {
    couldNodeDispose(nodeInfo) {
      return nodeInfo.couldDisposeRoleIds && nodeInfo.couldDisposeRoleIds.length > 0;
    },
    // 获取当前登录人运用所编码
    async getUnitCode() {
      let data = await getUnitCode();
      this.unitCode = data;
    },

    // 获取当前登录人运用所名称
    async getUnitName() {
      let data = await getUnitName();
      this.unitName = data;
    },

    // 查询流程类型
    async getFlowTypeList() {
      let res = await getFlowTypeList({
        unitCode: this.unitCode,
      });
      this.flowChangeDialog.flowTypeList = res;
    },

    cellStyle({ row, column, rowIndex, columnIndex }) {
      if (columnIndex == 0) return '';
      this.nodeColumns[columnIndex - 1].prop;
      if (row[this.nodeColumns[columnIndex - 1].prop]) {
        return 'background:#82c343 !important;';
      } else {
        return '';
      }
    },
    getDetail(info) {
      if (typeof info == 'object') {
        return Object.values(info).toString();
      } else {
        return '';
      }
    },
    trackShowChange() {
      this.trackPageNum = 1;
    },
    async getTaskPostList(trainsetId) {
      const userRoleList = await getTaskPostList(
        this.unitCode,
        this.headerFormData.dayPlanId,
        this.userInfo.workerId,
        trainsetId
      );
      this.userInfo.roles = userRoleList.map((item) => item.roleId);
    },
    async getUser() {
      const {
        data: {
          data: { staffId, name, workTeam },
        },
      } = await getUser();
      this.userInfo = {
        workerId: staffId,
        workerName: name,
        teamCode: workTeam && workTeam.teamCode ? workTeam.teamCode : '',
        teamName: workTeam && workTeam.teamName ? workTeam.teamName : '',
      };
    },
    // 获取日计划编号
    async getDay() {
      const res = await axios({
        url: '/apiTrainRepair/common/getDay',
        params: {
          unitCode: this.unitCode,
        },
      });
      this.dayPlanId = res.data.dayPlanId;
      this.headerFormData.dayPlanId = res.data.dayPlanId;
      let dayPlanArr = this.headerFormData.dayPlanId.split('-');
      const datType = dayPlanArr.pop();
      const time = dayPlanArr.join('-');
      if (datType == '00') {
        this.dayPlanIdList = [
          {
            id: 1,
            dayPlanId: `${dayjs(time).subtract(1, 'day').format('YYYY-MM-DD')}-00`,
            dayPlanName: `${dayjs(time).subtract(1, 'day').format('YYYY-MM-DD')} 白班`,
          },
          {
            id: 2,
            dayPlanId: `${dayjs(time).subtract(1, 'day').format('YYYY-MM-DD')}-01`,
            dayPlanName: `${dayjs(time).subtract(1, 'day').format('YYYY-MM-DD')} 夜班`,
          },
          {
            id: 3,
            dayPlanId: res.data.dayPlanId,
            dayPlanName: `${time} 白班`,
          },
        ];
      } else {
        this.dayPlanIdList = [
          {
            id: 1,
            dayPlanId: `${dayjs(time).subtract(1, 'day').format('YYYY-MM-DD')}-01`,
            dayPlanName: `${dayjs(time).subtract(1, 'day').format('YYYY-MM-DD')} 夜班`,
          },
          {
            id: 2,
            dayPlanId: `${dayjs(time).format('YYYY-MM-DD')}-00`,
            dayPlanName: `${dayjs(time).format('YYYY-MM-DD')} 白班`,
          },
          {
            id: 3,
            dayPlanId: res.data.dayPlanId,
            dayPlanName: `${time} 夜班`,
          },
        ];
      }
    },
    async dayPlanIdChange() {
      await this.getTrainsetAndTrackPowerInfo(this.allTrackStr);
      await this.getFlowRunInfos({
        flowPageCode,
        unitCode: this.unitCode,
        dayPlanId: this.headerFormData.dayPlanId,
        trainsetId: '',
      });
    },
    // 出入所车组
    async getTrainsetListReceived() {
      const res = await getTrainsetListReceived({
        unitCode: this.unitCode,
      });

      this.trainGroupNameList = res;
    },

    // 股道
    async getAllTrackArea() {
      const { trackAreas } = await getAllTrackArea();

      this.allTrackAreaData = trackAreas;
      this.getTrainsetAndTrackPowerInfo(this.allTrackStr);
    },
    // 车组、供断电
    async getTrainsetAndTrackPowerInfo(trackCodesJsonStr) {
      const { trackPowerState, trainsetPostIon } = await getTrainsetAndTrackPowerInfo({
        unitCode: this.unitCode,
        trackCodesJsonStr,
        trainsetNameStr: '',
        flowPageCode,
        dayPlanId: this.headerFormData.dayPlanId,
      });
      this.allTrainsetList = trainsetPostIon;
      this.trackPowerList = trackPowerState;
    },
    mainTablePageNumChange(pageNum) {
      this.trackPageNum = pageNum;
    },
    async getFlowRunInfos(params, trainsetId) {
      const { flowRunInfoGroups, errorFlowFunInfos } = hasPermission
        ? await getFlowRunInfosNotCheck(params)
        : await getFlowRunInfos({ ...params, personal: true });
      if (params.trainsetId) {
        if (errorFlowFunInfos) {
          this.$message({
            message: errorFlowFunInfos,
            type: 'info',
            customClass: 'mzindex',
          });
        }
      }
      if (params.trainsetId) {
        if (
          flowRunInfoGroups[0] &&
          flowRunInfoGroups[0].flowRunInfoForSimpleShows &&
          flowRunInfoGroups[0].flowRunInfoForSimpleShows[0]
        ) {
          this.nodeDealDialog.flowRunInfoForSimpleShows = flowRunInfoGroups[0].flowRunInfoForSimpleShows;
          this.nodeDealDialog.activeTabName = this.nodeDealDialog.flowRunInfoForSimpleShows[0].id
            ? this.nodeDealDialog.flowRunInfoForSimpleShows[0].id
            : this.nodeDealDialog.flowRunInfoForSimpleShows[0].flowId;
        } else {
          return Promise.reject(new Error('当前车组没有触发流程'));
        }
      } else {
        this.flowList = flowRunInfoGroups.reduce((prev, item) => {
          if (!prev[item.trainsetId]) {
            prev[item.trainsetId] = [];
          }
          prev[item.trainsetId] = item.flowRunInfoForSimpleShows;
          return prev;
        }, {});
      }
    },
    scrollLastedNode() {
      this.$nextTick().then(() => {
        if (!this.lasetedNode.id) {
          this.nodeDealDialog.nodeDealFormData.nodeId = '';
          this.nodeDealDialog.nodeDealFormData.oriNodeId = '';
          this.nodeDealDialog.nodeDealFormData.remark = '';
          return;
        }
        this.nodeChange(this.lasetedNode.id);

        const targetId = document.querySelector(`#node-${this.lasetedNode.id}`);
        targetId.scrollIntoView({
          behavior: 'smooth',
          block: 'center',
          inline: 'start',
        });
      });
    },
    async trainClick({ trainsetInfo }) {
      try {
        this.nodeDealDialog.trainsetInfo = trainsetInfo;
        await this.getTaskPostList(trainsetInfo.trainsetId);
        await this.getFlowRunInfos({
          flowPageCode,
          unitCode: this.unitCode,
          dayPlanId: this.headerFormData.dayPlanId,
          trainsetId: trainsetInfo.trainsetId,
        });
        // // if (this.trainsetIdflowMap[trainsetInfo.trainsetId]) {
        // //   this.nodeDealDialog.flowRunInfoForSimpleShows = this.trainsetIdflowMap[trainsetInfo.trainsetId]
        // //   this.nodeDealDialog.activeTabName = this.nodeDealDialog.flowRunInfoForSimpleShows && this.nodeDealDialog.flowRunInfoForSimpleShows[0].flowId;
        // // } else {
        // //   return Promise.reject(new Error('当前车组没有触发流程'));
        // // }
        if (!this.nodeDealDialog.nodeDealDialogVisible) {
          this.nodeDealDialog.nodeDealDialogVisible = true;
        }
        this.scrollLastedNode();
      } catch (err) {
        this.$message.info(err.message);
      }
    },
    // 节点处理弹出框
    // 关闭事件
    async nodeDealDialogClose() {
      await this.getFlowRunInfos({
        flowPageCode,
        unitCode: this.unitCode,
        dayPlanId: this.headerFormData.dayPlanId,
        trainsetId: '',
      });
      this.nodeDealDialog.activeTabName = '';
      this.nodeDealDialog.nodeDealFormData.nodeId = '';
      this.nodeDealDialog.nodeDealFormData.oriNodeId = '';
      this.nodeDealDialog.nodeDealFormData.remark = '';
      this.nodeDealDialog.fileList = [];

      this.nodeDealDialog.uploadedFileInfos = [];
      this.nodeDealDialog.nodeDealDialogVisible = false;
    },
    nodeChange(id) {
      if (this.nodeDealDialog.nodeDealFormData.nodeId == id) return;

      this.nodeDealDialog.nodeDealFormData.nodeId = id;
      this.nodeDealDialog.nodeDealFormData.remark = this.dealNode.remark || '无';

      this.nodeDealDialog.uploadedFileInfos = [];

      let list = this.nodeConfigRoleList.filter((role) => {
        return this.dealNode.couldDisposeRoleIds.some((RoleId) => {
          return RoleId == role.roleId;
        });
      });

      this.roleDialog.selectRolelist = [];
      list.forEach((role) => {
        if (this.roleCodeInfoMap[role.roleId]) {
          this.roleDialog.selectRolelist.push(this.roleCodeInfoMap[role.roleId]);
        }
      });

      this.nodeDealDialog.fileList = [];
      this.nodeDealDialog.nodeRecords = [];
      this.roleDialog.roleFormData.roleId = '';
      if (this.roleDialog.selectRolelist.length > 1) {
        this.roleDialog.visible = true
        this.$nextTick(() => {
          let isOk = false
          this.$refs.roleDialogRef.$once("close", () => {
            if (!isOk) {
              this.$refs.roleFormRef.resetFields();
              if (!this.nodeDealDialog.nodeDealFormData.oriNodeId) {
                this.nodeDealDialog.nodeDealFormData.nodeId = '';
              } else {
                this.filterNodeRecords();
              }
            }
          })
          let onConfirm = async () => {
            try {
              await this.$refs.roleFormRef.validate();
              this.roleDialog.roleFormData.oriRoleId = this.roleDialog.roleFormData.roleId;
              this.filterNodeRecords();
              isOk = true;
              this.roleDialog.visible = false;
            } catch(e){}
          }
          this.$refs.roleDialogConfirmBtnRef.$on("click", onConfirm)
          let unwatch = this.$watch("roleDialog.visible", (value) => {
            if(!value){
              this.$refs.roleDialogConfirmBtnRef.$off("click", onConfirm)
              unwatch()
            }
          })
        })
      } else {
        this.filterNodeRecords();
      }
    },

    filterNodeRecords() {
      if (this.roleDialog.selectRolelist.length > 1 && !this.roleDialog.roleFormData.roleId) {
        this.roleDialog.roleFormData.roleId = this.roleDialog.roleFormData.oriRoleId;
        this.nodeDealDialog.nodeDealFormData.nodeId = this.nodeDealDialog.nodeDealFormData.oriNodeId;
        this.nodeDealDialog.nodeDealFormData.remark = this.dealNode.remark || '无';
      } else {
        this.nodeDealDialog.nodeDealFormData.oriNodeId = this.nodeDealDialog.nodeDealFormData.nodeId;
      }

      let roleId;
      if (this.roleDialog.selectRolelist.length === 1) {
        roleId = this.roleDialog.selectRolelist[0].roleId;
      } else {
        roleId = this.roleDialog.roleFormData.roleId;
      }

      let selfNodeRecords = this.dealNode.nodeRecords.filter((node) => {
        return node.workerId == this.userInfo.workerId && roleId == node.roleId;
      });

      this.nodeDealDialog.fileList = selfNodeRecords
        .map((record) => {
          return record.pictureUrls.map((item) => {
            let url = item.relativeUrl.split('/');
            return {
              indexOfList: item.indexOfList,
              isOfList: item.isOfList,
              oldName: item.oldName,
              paramName: item.paramName,
              payload: item.payload,
              name: url[url.length - 1],
              relativePath: item.relativePath,
              url: `/storageTrainRepair/${item.relativeUrl}`,
            };
          });
        })
        .flat();

      this.oriFileUrl = copyDataSimple(this.nodeDealDialog.fileList);

      this.nodeDealDialog.nodeRecords = this.dealNode.nodeRecords.filter((node) => {
        return node.workerId != this.userInfo.workerId || roleId != node.roleId;
      });
    },

    seeNodeRecordPhoto(row) {
      this.viewPictureDialog.visible = true;
      this.viewPictureDialog.detailInfo.title = `${row.workerName} 上传图片：`;
      this.viewPictureDialog.detailInfo.prePics = row.pictureUrls.map((item) => {
        return `/storageTrainRepair/${item.relativeUrl}`;
      });
      this.viewPictureDialog.detailInfo.pics = row.pictureUrls.map((item) => {
        let url = item.relativeUrl.split('/');
        return {
          name: url[url.length - 1],
          url: `/storageTrainRepair/${item.relativeUrl}`,
        };
      });
    },

    // tab点击
    handleTabClick() {
      this.nodeDealDialog.nodeDealFormData.nodeId = '';
      this.nodeDealDialog.nodeDealFormData.oriNodeId = '';
      this.nodeDealDialog.nodeDealFormData.remark = '';
      this.nodeDealDialog.fileList = [];
      this.nodeDealDialog.uploadedFileInfos = [];

      this.scrollLastedNode();
    },
    async getPictureUploadMax() {
      const { paramValue } = await getPictureUploadMax();
      this.uploadLimit = Number(paramValue);
    },
    onExceed() {
      this.$message.error(`最大上传图片数量不能超过${this.uploadLimit}张`);
    },
    previewPicture(file, fileList) {
      this.imgUrl = file.url;
      this.prePics = fileList.map((item) => item.url);
      this.$refs.myImg.showViewer = true;
    },

    fileListChange(file, fileList) {
      this.nodeDealDialog.fileList = fileList.map((item) => item);
    },

    fileSuccess(response, file, fileList) {
      this.nodeDealDialog.fileList = fileList.map((item) => item);
    },

    removePicture(file) {
      this.nodeDealDialog.fileList = this.nodeDealDialog.fileList.filter((item) => item.uid != file.uid);
      this.nodeDealDialog.uploadedFileInfos = this.nodeDealDialog.uploadedFileInfos.filter(
        (item) => item.uid != file.uid
      );
    },
    beforeUpload(file) {
      const types = ['image/jpeg', 'iamge/gif', 'image/bmp', 'iamge/png'];
      if (!types.includes(file.type)) {
        this.$message.error('上传图片只能是 JPG、GIF、BMP、PNG 格式！');
        return false;
      }

      const maxSize = 5 * 1024 * 1024;
      if (maxSize < file.size) {
        this.$message.error('图片大小最大不能超过5M');
        return false;
      }

      return true;
    },

    // 判断上传图片格式
    beforeAvatarUploadPdf(file) {
      var testmsg = file.name.substring(file.name.lastIndexOf('.') + 1);
      const extension = testmsg === 'jpg' || testmsg === 'JPG';
      const extension2 = testmsg === 'png' || testmsg === 'PNG';
      const extension3 = testmsg === 'jpeg' || testmsg === 'JPEG';
      const extension4 = testmsg === 'bmp' || testmsg === 'BMP';
      if (!extension && !extension2 && !extension3 && !extension4) {
        this.$message({
          message: '上传文件只能是 JPG、JPEG、BMP、PNG 格式!',
          type: 'warning',
        });
        return false;
      }
      return extension || extension2 || extension3 || extension4;
    },

    async uploadFiles({ file }) {
      this.nodeDealDialog.uploadedFileInfos = [...this.nodeDealDialog.uploadedFileInfos, file];
    },

    workTeamDialogClose() {
      this.workTeamDialog.visible = false;
    },

    async dealNodeBtn(couldDisposeRoleIds) {
      if (!this.dealNode.id) {
        return this.$message.info('请先要选择处理的节点');
      }

      if (couldDisposeRoleIds.length == 0) {
        await this.confirmDealNode();
        return;
      }

      if (this.nodeDealDialog.fileList.length < Number(this.dealNode.recommendedPicNum)) {
        const res = await this.$confirm('上传图像实际数量小于节点信息的建议上传图片数量', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          closeOnClickModal: false,
          closeOnPressEscape: false,
          type: 'warning',
        }).catch(() => {
          this.$message.info('已取消处理');
        });
        if (res != 'confirm') return;
      }

      let res = await getActuallyWorkTeams({
        unitCode: this.unitCode,
        dayPlanId: this.headerFormData.dayPlanId,
        trainsetIds: this.nodeDealDialog.flowRunInfoForSimpleShows[0].trainsetId,
        staffId: this.userInfo.workerId,
      });
      this.workTeamDialog.selectWorkTeamlist = res;

      if (this.workTeamDialog.selectWorkTeamlist.length !== 1) {
        this.workTeamDialog.workTeamFormData.selectWorkTeam = {};
        return (this.workTeamDialog.visible = true);
      } else {
        this.workTeamDialog.workTeamFormData.selectWorkTeam =
          this.workTeamDialog && this.workTeamDialog.selectWorkTeamlist[0];
        this.confirmDealNode();
      }
    },

    confirmWorkTeam() {
      this.$refs.workTeamFormRef.validate((valid) => {
        if (valid) {
          this.workTeamDialog.visible = false;
          this.confirmDealNode();
        }
      });
    },

    async confirmDealNode(skipFlag = false, confirmIgnoreMinIntervalRestrict = false, confirmCoverNodeRecord = false) {
      let roleInfo = {};

      if (this.roleDialog.selectRolelist.length == 1) {
        roleInfo = this.roleDialog && this.roleDialog.selectRolelist[0];
      } else {
        roleInfo = this.roleCodeInfoMap[this.roleDialog.roleFormData.roleId];
      }

      for (let i = 0; i < this.nodeDealDialog.uploadedFileInfos.length; i++) {
        let fileFormData = new FormData();
        let file = this.nodeDealDialog.uploadedFileInfos[i];
        fileFormData.append(file.uid, file);
        let res = await uploadedFileInfo(fileFormData);
        this.nodeDealDialog.currentUploaded.push(...res);
        if (!this.nodeDealDialog.uploadedFileInfos[i + 1]) {
          this.nodeDealDialog.uploadedFileInfos = [];
        }
      }

      let oldFileUrl = this.oriFileUrl
        .filter((file) => {
          return this.nodeDealDialog.fileList.some((item) => {
            return item.url == file.url;
          });
        })
        .map((v) => {
          return {
            indexOfList: v.indexOfList,
            isOfList: v.isOfList,
            oldName: v.oldName,
            paramName: v.paramName,
            payload: v.payload,
            relativePath: v.relativePath,
            relativeUrl: v.url.split('/').slice(2, v.url.split('/').length).join('/'),
          };
        });

      let nodeDisposeJson = {
        workerId: this.userInfo.workerId,
        workerName: this.userInfo.workerName,
        teamCode: this.workTeamDialog.workTeamFormData.selectWorkTeam.teamCode,
        teamName: this.workTeamDialog.workTeamFormData.selectWorkTeam.teamName,
        flowId: this.flowRun.flowId,
        roleInfo,
        nodeId: this.nodeDealDialog.nodeDealFormData.nodeId,
        skipFlag,
        confirmIgnoreMinIntervalRestrict,
        confirmCoverNodeRecord,
        uploadedFileInfos: [...oldFileUrl, ...this.nodeDealDialog.currentUploaded],
      };
      if (this.flowRun.id) {
        nodeDisposeJson = {
          ...nodeDisposeJson,
          flowRunId: this.flowRun.id,
        };
      } else {
        nodeDisposeJson = {
          ...nodeDisposeJson,
          flowRun: this.flowRun,
        };
      }

      const nodeDisposeJsonStr = JSON.stringify(nodeDisposeJson);

      let formData = new FormData();
      formData.append('nodeDisposeJsonStr', nodeDisposeJsonStr);

      let loading = this.$loading();
      let res;
      try {
        if (hasPermission) {
          res = await setNodeDisposeNotCheck(formData);
        } else {
          res = await setNodeDispose(formData);
        }
      } finally {
        loading.close();
      }

      if (res.data.code == 1) {
        if (
          res.data.data.needConfirmSkippedNodes ||
          res.data.data.needConfirmIgnoreMinIntervalRestrict ||
          res.data.data.needConfirmCoverNodeRecord
        ) {
          let confirm = (message) => {
            return this.$confirm(message, '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              closeOnClickModal: false,
              closeOnPressEscape: false,
              type: 'warning',
            });
          };
          let confirmAllInfo = async (res) => {
            let confirms = [];
            let confirmResult = {
              confirmedSkippedNodes: false,
              confirmedIgnoreMinIntervalRestrict: false,
              confirmedCoverNodeRecord: false,
            };

            if (res.needConfirmSkippedNodes) {
              let nodeStr = res.needSkippedNodes.map((node) => node.name).toString();
              confirms.push(() =>
                confirm(`当前流程将跳过 ${nodeStr} 节点，是否继续进行刷卡？`).then((v) => {
                  return (confirmResult.confirmedSkippedNodes = v === 'confirm');
                })
              );
            }

            if (res.needConfirmIgnoreMinIntervalRestrict) {
              confirms.push(() =>
                confirm(res.confirmIgnoreMinIntervalRestrictMessage).then((v) => {
                  return (confirmResult.confirmedIgnoreMinIntervalRestrict = v === 'confirm');
                })
              );
            }

            if (res.needConfirmCoverNodeRecord) {
              confirms.push(() =>
                confirm('是否覆盖上次打卡记录!').then((v) => {
                  return (confirmResult.confirmedCoverNodeRecord = v === 'confirm');
                })
              );
            }

            for (let i = 0; i < confirms.length; i++) {
              let rs = await confirms[i]();
              if (!rs) {
                throw '取消刷卡';
              }
            }
            return confirmResult;
          };

          await confirmAllInfo(res.data.data)
            .then((confirmResult) => {
              return this.confirmDealNode(
                confirmResult.confirmedSkippedNodes,
                confirmResult.confirmedIgnoreMinIntervalRestrict,
                confirmResult.confirmedCoverNodeRecord
              );
            })
            .catch(() => {
              this.$message.info('已取消处理');
            });
        } else {
          if (res.data.data.disposeAfterSkip) {
            this.$forceUpdate();
            this.$message.success('补卡成功');
          } else {
            this.$forceUpdate();
            this.$message.success('打卡成功');
          }
          this.nodeDealDialog.currentUploaded = [];
          await this.getFlowRunInfos({
            flowPageCode,
            unitCode: this.unitCode,
            dayPlanId: this.headerFormData.dayPlanId,
            trainsetId: this.nodeDealDialog.flowRunInfoForSimpleShows[0].trainsetId,
          });
          this.nodeDealDialog.fileList = [];
          this.nodeDealDialog.uploadedFileInfos = [];
          this.nodeDealDialog.nodeDealFormData.nodeId = '';
          this.nodeDealDialog.nodeDealFormData.oriNodeId = '';
          this.nodeDealDialog.nodeDealFormData.remark = '';
        }
      } else {
        // let messageList = res.data.msg.split('!');
        // if (messageList.length > 1) {
        //   messageList.splice(messageList.length - 1, messageList.length);
        // }
        // let list = [];
        // messageList.forEach((item) => {      
        //   const strList = item.split(']')
        //   let reg = new RegExp('^[0-9]*$');
        //   let arrItem = strList[1].split('');
        //   for (let i = 0; i < arrItem.length; i++) {
        //     if (reg.test(arrItem[i])) {
        //       arrItem[i] = `<span style="font-size: 16px;"> ${arrItem[i]} </span>`;
        //     }
        //   }
        //   let str = arrItem.join('');
        //   list.push(`<div>${strList[0]}${str}!</div>`);
          
        // });
        // let message = list.join('');
        let newStr = res.data.msg.replace(/(__[0-9]__)/g, "<span style='font-size: 16px';>$1</span>").replace((/_/g), '')
        this.$message({
          dangerouslyUseHTMLString: true,
          message: newStr,
          type: 'warning',
          duration: '3000',
        });
      }
    },

    lookupNodeDealInfoBtn() {
      this.lookupNodeDealInfoDialog.visible = true;
    },
    cellClick(row, property) {
      if (!row.roleWorkerPicUrls[property]) return;
      this.workerPicMap = row.roleWorkerPicUrls[property];
      this.workerDetail = row.roleWorkerDetails[property];
      this.nodeDealDetailDialog.visible = true;
    },
    // 去除过期岗位信息
    postFilter(data) {
      let nodeList = data || [];
      nodeList.forEach((node) => {
        node.roleConfigs = node.roleConfigs.filter((role) => this.roleCodeNameMap[role.roleId]);
      });
    },
    async lookupFlowDataBtn() {
      if (!this.flowRun.id) {
        const { nodes, nodeVectors, pairNodeInfo, parallelSections, subflowInfoList } = await getFlowInfoById({
          flowId: this.flowRun.flowId,
        });
        this.postFilter(nodes);
        this.lookupFlowDataDialog.flowData = {
          nodes,
          nodeVectors,
          pairNodeInfo,
          parallelFlowConfigs: parallelSections,
          subflowInfoList,
        };
        this.lookupFlowDataDialog.visible = true;
      } else {
        const {
          flowInfo: { nodes, nodeVectors, pairNodeInfo, parallelSections, subflowInfoList },
        } = await getFlowDisposeGraph(this.flowRun.id);
        this.postFilter(nodes);
        this.lookupFlowDataDialog.flowData = {
          nodes,
          nodeVectors,
          pairNodeInfo,
          parallelFlowConfigs: parallelSections,
          subflowInfoList,
        };
        this.lookupFlowDataDialog.visible = true;
      }
    },
    async getPostRoleList() {
      this.roleList = await getPostRoleList();
    },
    async flowChangeBtn(type) {
      // return;
      this.flowChangeDialog.type = type;
      this.flowChangeDialog.name = this.flowRun.flowConfig.name;
      this.flowChangeDialog.state = this.flowRun.flowConfig.deleteWorkerId;
      this.flowChangeDialog.visible = true;
      if (type == 'spare') {
        await this.getSwitchoverFlow();
      } else {
        await this.getForceSwitchoverFlow();
      }
    },

    // 获取可切换流程
    async getSwitchoverFlow() {
      let { data } = await getSwitchoverFlow(this.flowRun.id, this.unitCode, this.flowRun.flowId);
      if (data.code == 1) {
        this.flowChangeDialog.tableData = data.data ? data.data : [];
      } else {
        this.flowChangeDialog.tableData = [];
        this.$message.warning(data.msg);
      }
    },

    // 获取可切换流程(强制)
    async getForceSwitchoverFlow() {
      let { data } = await getForceSwitchoverFlow(this.flowRun.id, this.unitCode, this.flowRun.flowId);
      if (data.code == 1) {
        this.flowChangeDialog.tableData = data.data ? data.data : [];
      } else {
        this.flowChangeDialog.tableData = [];
        this.$message.warning(data.msg);
      }
    },

    async switchToNowFlowBtn(targetFlowId) {
      const res = await this.$confirm(
        '在节点处理信息迁移至新流程的过程中，任何节点名称的不一致都会导致剩余节点处理信息的迁移被中断，确认切换流程吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          closeOnClickModal: false,
          closeOnPressEscape: false,
          type: 'warning',
        }
      ).catch(() => {
        this.$message.info('已取消切换');
      });
      if (res == 'confirm') {
        if (this.flowChangeDialog.type == 'spare') {
          await setSwitchoverFlow(this.flowRun, targetFlowId);
        } else {
          await setForceSwitchoverFlow(this.flowRun, targetFlowId);
        }
        await this.trainClick({ trainsetInfo: this.nodeDealDialog.trainsetInfo });
        this.flowChangeDialog.visible = false;
      }
    },

    forceEndFlowBtn() {
      if (
        this.nodeDealDialog.flowRunInfoForSimpleShows[0].id &&
        this.nodeDealDialog.flowRunInfoForSimpleShows[0].state &&
        this.nodeDealDialog.flowRunInfoForSimpleShows[0].state != -1
      ) {
        if (this.nodeDealDialog.flowRunInfoForSimpleShows[0].state != 1) {
          if (this.nodeDealDialog.flowRunInfoForSimpleShows[0].state == 0) {
            this.forceEndFlowDialog.visible = true;
          }
        } else {
          this.$message.warning('流程已结束不可以强制结束');
        }
      } else {
        this.$message.warning('流程未启动不可以强制结束');
      }
    },

    // 强制关闭按钮
    async closeforceEndFlowDialog() {
      if (this.forceEndFlowDialog.formData.remark) {
        await this.forceEndFlowRun({
          flowRunId: this.nodeDealDialog.flowRunInfoForSimpleShows[0].id,
          workerId: this.userInfo.workerId,
          workerName: this.userInfo.workerName,
          remark: this.forceEndFlowDialog.formData.remark,
        }).then(() => {
          this.$message.success('流程已强制结束！');
          this.forceEndFlowDialog.visible = false;
        });
      } else {
        this.$message.warning('备注信息不能为空！');
      }
    },

    // 强制关闭请求
    async forceEndFlowRun(data) {
      return await forceEndFlowRun(data);
    },

    forceEndFlowDialogClose() {
      this.$refs.forceEndFlowForm.resetFields();
    },
  },
  filters: {
    whetherFilter(val) {
      if (val) {
        return '是';
      } else {
        return '否';
      }
    },
    packetFilter(packet) {
      return Object.keys(packet)
        .map((item) => {
          return `${item}:${packet[item]}`;
        })
        .join(';');
    },
  },
});
