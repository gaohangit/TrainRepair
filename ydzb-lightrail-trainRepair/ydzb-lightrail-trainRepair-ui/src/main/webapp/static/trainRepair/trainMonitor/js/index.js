let copyData = (data) => JSON.parse(JSON.stringify(data));

let promiseCache = {};
function getPromise(key, getPromise, getData) {
  let data = getData ? getData() : null;
  if (data == null && promiseCache[key] == null) {
    let pro = getPromise();
    promiseCache[key] = pro;
    pro.finally(() => {
      delete promiseCache[key];
    });
    return pro;
  } else if (promiseCache[key]) {
    return promiseCache[key];
  } else {
    return Promise.resolve(data);
  }
}

let dataCacheUtil = {
  dataCache: {},
  timeout: 1000, // 10秒
  timeoutCache: {},
  getCacheData(fnKey, dataKey) {
    if (this.dataCache[fnKey] && this.dataCache[fnKey][dataKey]) {
      return JSON.parse(this.dataCache[fnKey][dataKey]);
    }
  },
  setCacheData(fnKey, dataKey, data) {
    if (!this.dataCache[fnKey]) {
      this.dataCache[fnKey] = {};
    }
    this.dataCache[fnKey][dataKey] = JSON.stringify(data);
    // console.log(`set this.dataCache["${fnKey}"]["${dataKey}"]`, this.dataCache[fnKey][dataKey]);
    if (this.timeout > 0) {
      let timeoutCacheKey = fnKey + '_' + dataKey;
      if (!this.timeoutCache[timeoutCacheKey]) {
        let timeoutKey = setTimeout(() => {
          this.clearCacheData(fnKey, dataKey);
          delete this.timeoutCache[timeoutCacheKey];
        }, this.timeout);
        this.timeoutCache[timeoutCacheKey] = timeoutKey;
      }
    }
  },
  clearCacheData(fnKey, dataKey) {
    if (fnKey && dataKey) {
      if (fnKey in this.dataCache && dataKey in this.dataCache[fnKey]) {
        delete this.dataCache[fnKey][dataKey];
        // console.log(`delete this.dataCache["${fnKey}"]["${dataKey}"]`, this.dataCache[fnKey]);
      }
    } else if (fnKey && !dataKey) {
      delete this.dataCache[fnKey];
    } else if (!fnKey && !dataKey) {
      this.dataCache = {};
    } else {
      console.error('clearCacheData参数错误');
    }
  },
};
window.dataCacheUtil = dataCacheUtil;

const iframeBaseUrlMap = {
  one: '/webTrainRepair/projection/workRepairmonitor',
  two: '/webTrainRepair/projection/workRepairmonitor',
  temporary: '/webTrainRepair/workRepairFlow/temporaryRepairOperationFlowMonitor',
  planLessKey: '/webTrainRepair/workRepairFlow/keyWorkFlowMonitor',
  self: '/webTrainRepair/trainMonitor?trainsetId=11',
};
let listToMap = (list, getKey, getData) => {
  return list.reduce((pre, cur, i) => {
    pre[getKey(cur)] = getData ? getData(cur, i) : cur;
    return pre;
  }, {});
};
const updateData = (oriDataList, newDataList, setFinalNewDataList, valuesForUpdate, getValueForUpdate) => {
  if (oriDataList && newDataList && setFinalNewDataList && valuesForUpdate && getValueForUpdate) {
    let valuesForUpdateMap = listToMap(
      valuesForUpdate,
      (v) => v,
      (v) => true
    );
    let finalNewDataList = oriDataList.filter((v) => !valuesForUpdateMap[getValueForUpdate(v)]);
    newDataList.forEach((v) => {
      finalNewDataList.push(v);
    });
    setFinalNewDataList(finalNewDataList);
  } else {
    console.error('参数错误');
  }
};
window.main = new Vue({
  el: '#app',
  components: {
    vueSeamlessScroll,
  },
  data: {
    transferTrainJurisdiction: transferTrainJurisdiction, // 调车权限
    unitCode: '',
    unitName: '',
    limitMoveNum: 5,
    scrollKey: 1,
    monitorFlag: false,
    trackContainerHeight: '100%',
    params: '',
    iframeSrc: '',
    iframeVisible: false,
    refreshTimer: null,
    dayPlanId: '',
    localtrackShowConfig: {},
    warningConfig: {},
    globalConfigObj: {}, // 全局配置
    baseConfigDialog: {
      visible: false,
      configList: [],
      trackShowConfig: {},
      formData: {
        TrackRefreshTime: '',
        ShowLastRepairTask: false,
        ShowShuntPlan: false,
        ShowRunningPlan: false,
        ShowRepair: false,
        AutoMoveTrain: false,
        IsMoveTrainPlanWarning: false,
        MoveTrainPlanWarningTime: '',
        ShowMoveTrainPlan: false,
        ShowTrainsetTrackcode: false,
        HeadDirection: false,
        ShowNoTrainsetTrack: false,
        MovePlan: true,
      },
      // 股道位置切换
      TrackPositionChange: true,
      // 股道位置切换重置
      oldTrackPositionChange: true,
      timer: null,
    }, // 基础配置
    noteNameShow: '', //调车记事input标题
    baseImgPath: ctxPath + '/static/trainRepair/trainMonitor/img',
    // 时间
    time: '',
    timer: null,
    configButtonsVisible: false,
    isFullScreen: false,
    // 出入所车组
    inOutTrainGroup: {
      trainGroupName: '',
      trainGroupNameList: [],
      inOutActive: {},
    },
    // 其它车组
    otherTrainGroup: {
      otherHeight: '20%',
      otherTrainGroupName: '',
      otherTrainGroupNameList: [],
    },
    // 调车记事
    noteConfigDialog: {
      visible: false,
      dblState: false,
      noteList: [],
      noteState: '',
      editNoteDialogVisible: false,
      noteDialogContent: '',
      noteId: '',
    },
    // 车
    allTrackAreaList: [],
    allTrainsetList: [],
    trackPageSize: 4,
    trackPageNum: 1,
    trainsetList: [],
    trackPowerList: [],
    trainsetIdTaskList: [],
    LYList: [],
    SJList: [],
    focusId: '',
    trainWrapperActive: {},

    // 右键菜单
    axis: {
      x: 0,
      y: 0,
    },
    rightMenuShow: false, //显示
    rightMenuType: '', //菜单类型
    contextMenuMap: {},
    rightTargetInfo: {},
    dragTrain: false,

    action: [],
    active: {},
    childVisible: {
      0: false,
      1: false,
      2: false,
    },

    // 开行信息
    runInfoDialog: {
      visible: false,
      tableData: [],
      tableHeight: '250px',
      tableCurrentPage: 1,
      tablePageSize: 20,
      tableTotal: 0,
    },
    runInfoList: [],
    shuntInfoList: [],
    shuntTimer: null,
    // 调车信息
    shuntInfoDialog: {
      visible: false,
      tableData: [],
      tableHeight: '250px',
      tableCurrentPage: 1,
      tablePageSize: 20,
      tableTotal: 0,
    },
    // 重联配置
    reconnectConfigDialogVisible: false, //重联弹出框的显示与隐藏
    reconnectConfigTableHeight: '200px',
    reconnectConfigTableData: [],
    reConnectTableTotal: 300,
    reConnectTableCurrentPage: 1,
    reConnectTablePageSize: 20,

    chooseTrainTypeDialogVisible: false, // 车型选择弹出框的显示与隐藏
    connectTrainTypeId: 0,
    connectTrainTypeTrainType: '',
    prevConnectTrainType: [],
    chooseTrainTypeDialogBodyHeight: '',
    trainTypeList: [],
    connectTrainType: [],

    // 检修配置
    repairConfigDialogVisible: false, //检修配置弹出框的显示与隐藏
    repairConfigTableData: [],
    repairConfigTableHeight: '200px',
    repairTableCurrentPage: 1,
    repairTablePageSize: 20,
    repairTableTotal: 300,

    chooseJobPackageDialogVisible: false, //作业包弹出框的显示与隐藏
    jobPackageList: [],
    chooseJobPackageList: [],
    chooseAllJobPackage:[],
    isIndeterminateChooseJobPackage: false,
    postPacketJson: {},
    chooseJobPackageDialogBodyHeight: '',

    // 检修信息
    // 检修显示内容配置
    yyRepairConfig: true,
    gjRepairConfig: true,
    repairInfoDialogVisible: false,
    yyRepairInfoTableData: [],
    repairInfoTableCurrentPage: 1,
    repairInfoTablePageSize: 20,
    repairInfoTableTotal: 0,

    gjRepairInfoTableData: [],

    pgMList: [],

    // 故障列表
    faultTrainsetId: '',
    faultListDialogVisible: false,
    faultListTableData: [],
    faultListTableHeight: '250px',
    faultListTableCurrentPage: 1,
    faultListTablePageSize: 20,
    faultListTableTotal: 0,

    // 预警列表
    warningType: '',
    warningDialogVisible: false,
    warningTableData: [],
    warningTableHeight: '250px',
    warningTableCurrentPage: 1,
    warningTablePageSize: 20,
    warningTableTotal: 0,

    hostLingList: [],
    flowList: [],
    lookupFlowList: [],
    nodeList: [],
    roleList: [],
    lookupNodeDealInfoDialog: {
      visible: false,
    },
    nodeDealDetailDialog: {
      visible: false,
    },
    workerPicMap: {},
    workerDetail: {},
    imgUrl: '',
    prePics: [],
    oriShowLastRepairTask: false,
    trainsetAllList: [],
    tableRefresh: null
  },
  computed: {
    getRightMenuShow() {
      if (this.rightMenuShow) {
        if (this.contextMenuMap.menu && this.contextMenuMap.menu.length > 0) {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    },

    monitorTitle() {
      return this.unitName || 'xxx';
    },

    paramsMap({ params, trainsetIdInfoMap }) {
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
        if (obj.trainsetId && trainsetIdInfoMap[obj.trainsetId]) {
          obj.trackCode = trainsetIdInfoMap[obj.trainsetId].trackCode;
        }
        return obj;
      }
    },
    // 调车信息
    shuntInTimeMap({ shuntInfoList, trainsetIdConnectMap }) {
      return shuntInfoList.reduce((prev, item) => {
        if (!prev[item.inTime / 1000]) {
          prev[item.inTime / 1000] = [];
        }
        if (!prev[item.inTime / 1000].find((train) => train.emuId == trainsetIdConnectMap[item.emuId])) {
          prev[item.inTime / 1000].push(item);
        }

        return prev;
      }, {});
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

    // 根据任务得到id=>[itemIds]
    trainsetIdItemIdMap({ trainsetIdTaskList }) {
      return trainsetIdTaskList.reduce((prev, item) => {
        prev[item.trainsetId] = item.itemId;
        return prev;
      }, {});
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

    // 基础配置
    configIdObj({ baseConfigDialog }) {
      return baseConfigDialog.configList.reduce((prev, item) => {
        prev[item.paramName] = item.id;
        return prev;
      }, {});
    },
    baseFormConfig({ baseConfigDialog }) {
      return baseConfigDialog.configList.reduce((prev, item) => {
        if (!Number(item.paramName)) {
          if (
            item.paramName == 'TrackRefreshTime' ||
            item.paramName == 'MoveTrainPlanWarningTime' ||
            item.paramName == 'NoteName' ||
            item.paramName == 'DataRefreshTime'
          ) {
            prev[item.paramName] = item.paramValue;
          } else {
            prev[item.paramName] = item.paramValue == '1' ? false : true;
          }
        }
        return prev;
      }, {});
    },
    // 出入所显示车组
    trainGroupNameShowList({ inOutTrainGroup }) {
      return inOutTrainGroup.trainGroupNameList.filter((item) => {
        return item.trainsetname.includes(inOutTrainGroup.trainGroupName);
      });
    },
    trainsetIdTypeMap({ inOutTrainGroup }) {
      return inOutTrainGroup.trainGroupNameList.reduce((prev, item) => {
        prev[item.trainsetid] = item.traintype;
        return prev;
      }, {});
    },
    inOutTrainsetIdNameMap({ trainsetAllList }) {
      return trainsetAllList.reduce((prev, item) => {
        prev[item.trainsetid] = item.trainsetname;
        return prev;
      }, {});
    },

    // 其他车组
    otherTrainGroupNameShowList({ otherTrainGroup }) {
      return otherTrainGroup.otherTrainGroupNameList.filter((item) => {
        return item.trainsetName.includes(otherTrainGroup.otherTrainGroupName);
      });
    },
    // 调车记事
    classOption({ limitMoveNum }) {
      return {
        step: 0.2,
        limitMoveNum,
      };
    },
    // 车
    // 线区codeName关系
    trackAreaCodeNameMap({ allTrackAreaSortList }) {
      return allTrackAreaSortList.reduce((prev, item) => {
        prev[item.trackAreaCode] = item.trackAreaName;
        return prev;
      }, {});
    },
    // 线区:[股道code]
    trackAreaCodeTrackCodeMap({ allTrackAreaSortList }) {
      return allTrackAreaSortList.reduce((prev, item) => {
        prev[item.trackAreaCode] = item.lstTrackInfo.map((track) => track.trackCode);
        return prev;
      }, {});
    },
    // 线区:[股道name]
    trackAreaCodeTrackNameMap({ allTrackAreaSortList }) {
      return allTrackAreaSortList.reduce((prev, item) => {
        prev[item.trackAreaCode] = item.lstTrackInfo.map((track) => track.trackName);
        return prev;
      }, {});
    },
    // 扁平所有的股道信息
    trackList({ allTrackAreaSortList }) {
      let trackList = allTrackAreaSortList.map((item) => item.lstTrackInfo).flat();
      return trackList;
    },
    // 扁平所有的股道信息
    allTrackList({ allTrackAreaSortList, localtrackShowConfig, baseFormConfig, trackHasTrainList, paramsMap }) {
      let getAllTrackAreaSortList = copyData(allTrackAreaSortList);
      // 响应基本配置
      let trackList = getAllTrackAreaSortList.map((item) => item.lstTrackInfo).flat();

      // 勾选的股道
      trackList = trackList.filter((item) => localtrackShowConfig[item.trackCode]);

      // 是否显示无车组的股道
      if (baseFormConfig.ShowNoTrainsetTrack) {
        trackList = trackList.filter((item) => trackHasTrainList.includes(String(item.trackCode)));
      }

      // url有参数时，只显示目标车和对应轨道
      if (paramsMap.trackCode) {
        trackList = trackList.filter((item) => item.trackCode == paramsMap.trackCode);
      }

      if (!this.baseConfigDialog.oldTrackPositionChange) {
        trackList.forEach((item) => {
          item.lstTrackPositionInfo.reverse();
        });
      }

      return trackList;
    },

    // 所有股道code
    allTrackStr({ allTrackList }) {
      return allTrackList.map((item) => item.trackCode).toString();
    },

    // 股道CodeName关系
    trackCodeNameMap({ trackList }) {
      return trackList.reduce((prev, item) => {
        prev[item.trackCode] = item.trackName;
        return prev;
      }, {});
    },

    // 扁平所有的列位信息
    allPositionList({ trackList }) {
      return trackList.map((item) => item.lstTrackPositionInfo).flat();
    },

    // trackCode:[positionCode]
    trackCodepositionCodesSortMap({ allPositionList }) {
      return allPositionList.reduce((prev, item) => {
        if (!prev[item.trackCode]) {
          prev[item.trackCode] = [];
        }
        prev[item.trackCode].push(item.trackPositionCode);
        return prev;
      }, {});
    },
    // 列位CodeName关系
    positionCodeNameMap({ allPositionList }) {
      return allPositionList.reduce((prev, item) => {
        prev[item.trackPositionCode] = item.trackPostionName;
        return prev;
      }, {});
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

    // 当前页所有的股道
    nowPageTracks({ groupAllTrackMap, trackPageNum }) {
      if (groupAllTrackMap[trackPageNum]) {
        return groupAllTrackMap[trackPageNum].map((item) => item.trackCode);
      } else {
        return [];
      }
    },

    // 当前页的车组
    nowPageTrainIds({ nowPageTracks, allTrainsetList }) {
      return allTrainsetList
        .filter((train) => nowPageTracks.includes(Number(train.trackCode)))
        .map((train) => train.trainsetId);
    },
    // 总页数
    trackPageCount({ groupAllTrackMap }) {
      return Math.max(...Object.keys(groupAllTrackMap));
    },

    // {trackCode:[{trainsetInfo},{}]}
    // trackCodeTrainInfoMap({ allTrainsetList }) {
    //   return allTrainsetList.reduce((prev, item) => {
    //     if(!prev[item.trackCode]){
    //       prev[item.trackCode] = []
    //     }
    //     prev[item.trackCode].push(item)
    //     return prev;
    //   }, {});
    // },
    // 所有在股道上的车
    trainsetIdNameMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        prev[item.trainsetId] = item.trainsetName;
        return prev;
      }, {});
    },
    // 所有车组的name
    trainsetNames({ allTrainsetList }) {
      return allTrainsetList.map((train) => train.trainsetName);
      // 所有有车的股道code
    },
    trackHasTrainList({ allTrainsetList }) {
      return [...new Set(allTrainsetList.map((item) => item.trackCode))];
    },
    // id->trackCode
    trainsetIdTrackCodeMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        prev[item.trainsetId] = item.trackCode;
        return prev;
      }, {});
    },
    // id->positionCodes
    trainsetIdPositionCodeMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        prev[item.trainsetId] = [];
        prev[item.trainsetId].push(item.headDirectionPlaCode);
        if (prev[item.trainsetId].indexOf(item.tailDirectionPlaCode) == -1) {
          prev[item.trainsetId].push(item.tailDirectionPlaCode);
        }
        return prev;
      }, {});
    },
    // 列位code对trainsetInfo
    positionCodeTrainsetInfoMap({ allTrainsetList }) {
      let getallTrainsetList = copyData(allTrainsetList);
      return getallTrainsetList.reduce((prev, item) => {
        if (!this.baseConfigDialog.oldTrackPositionChange) {
          let carNo = item.headDirection;
          item.headDirection = item.tailDirection;
          item.tailDirection = carNo;
        }

        prev[item.headDirectionPlaCode] = item;
        prev[item.tailDirectionPlaCode] = item;
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
    connectTrackCodes({ allTrainsetList, trainsetInTrackMap }) {
      return allTrainsetList.reduce((prev, item) => {
        if (item.isConnect == '1' && trainsetInTrackMap[item.trackCode].length > 1) {
          prev[item.trackCode] = true;
        }
        return prev;
      }, {});
    },

    // 列位code对trainsetName
    positionCodeTrainsetNameMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        prev[item.headDirectionPlaCode] = item.trainsetName;
        prev[item.tailDirectionPlaCode] = item.trainsetName;
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

    trainsetIdInfoMap({ allTrainsetList }) {
      // 所有trainsetId =》trainsetInfo
      return allTrainsetList.reduce((prev, item) => {
        prev[item.trainsetId] = item;

        return prev;
      }, {});
    },
    // 作为该页线区第一个，则显示线区名字
    showTrackAreaCodeMap({ groupAllTrackMap, trackPageNum }) {
      return groupAllTrackMap[trackPageNum].reduce((prev, item) => {
        if (Object.keys(prev).length == 0) {
          prev[item.trackCode] = item.trackAreaCode;
        } else if (!Object.values(prev).includes(item.trackAreaCode)) {
          prev[item.trackCode] = item.trackAreaCode;
        }
        return prev;
      }, {});
    },
    // id:task
    trainsetIdTaskMap({ trainsetIdTaskList }) {
      return trainsetIdTaskList.reduce((prev, item) => {
        prev[item.trainsetId] = item;
        return prev;
      }, {});
    },
    trackCodePowerMap({ trackPowerList }) {
      return trackPowerList.reduce((prev, item) => {
        if (!prev[item.trackCode]) {
          prev[item.trackCode] = {};
        }
        prev[item.trackCode][item.trackPlaCode] = item;
        return prev;
      }, {});
    },
    // {trackCode:[trainsetId]}重联车
    connectTrainInTrackMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        if (!prev[item.trackCode]) {
          prev[item.trackCode] = [];
        }
        if (item.isConnect == '1') {
          prev[item.trackCode].push(item.trainsetId);
        }
        return prev;
      }, {});
    },
    trainsetIdConnectMap({ connectTrainInTrackMap }) {
      let obj = {};
      for (const key in connectTrainInTrackMap) {
        const element = connectTrainInTrackMap[key];
        if (element.length > 0) {
          obj[element[0]] = element[1];
          obj[element[1]] = element[0];
        }
      }
      return obj;
    },
    jobPackageObj({ jobPackageList }) {
      return jobPackageList.reduce((prev, item) => {
        prev[item.packetCode] = item.packetName;
        return prev;
      }, {});
    },

    // 所有车的预警信息
    trainsetIdLYMap({ LYList }) {
      return LYList.reduce((prev, item) => {
        if (!prev[item.trainsetId]) {
          prev[item.trainsetId] = [];
        }
        prev[item.trainsetId].push(item);
        return prev;
      }, {});
    },

    trainsetIdSJMap({ SJList }) {
      return SJList.reduce((prev, item) => {
        if (!prev[item.trainsetId]) {
          prev[item.trainsetId] = [];
        }
        prev[item.trainsetId].push(item);
        return prev;
      }, {});
    },

    // 车组id对应节点id
    trainsetIdNodeIdMap({ flowList }) {
      return flowList.reduce((prev, item) => {
        prev[item.trainsetId] = item.id;
        return prev;
      }, {});
    },

    // 车组id对应流程Id
    trainsetIdNodeListIdMap({ flowList }) {
      return flowList.reduce((prev, item) => {
        prev[item.trainsetId] = item.id;
        return prev;
      }, {});
    },
    // 车组id对应节点信息
    trainsetIdNodeListMap({ lookupFlowList }) {
      return lookupFlowList.reduce((prev, item, index, data) => {
        if(prev[item.trainsetId]) {
          prev[item.trainsetId] = prev[item.trainsetId].concat(item.nodeList);
        } else {
          prev[item.trainsetId] = item.nodeList;
        }
        return prev;
      }, {});
    },
    // 车组id对应节点信息
    trainsetIdNodeListMapFooter({ flowList }) {
      return flowList.reduce((prev, item) => {
        prev[item.trainsetId] = item.nodeList;
        return prev;
      }, {});
    },

    // 车组id对应整备流程Id
    trainsetIdHostLingNodeListIdMap({ hostLingList }) {
      return hostLingList.reduce((prev, item) => {
        prev[item.trainsetId] = item.id;
        return prev;
      }, {});
    },

    // 车组id对应整备节点信息
    trainsetIdHostLingNodeListMap({ hostLingList }) {
      return hostLingList.reduce((prev, item) => {
        prev[item.trainsetId] = item.nodeList;
        return prev;
      }, {});
    },

    trainsetIdHasRun({ runInfoList }) {
      return runInfoList.reduce((prev, item) => {
        prev[item.trainsetId] = true;
        return prev;
      }, {});
    },
    trainsetIdHasShunt({ shuntInfoList }) {
      return shuntInfoList.reduce((prev, item) => {
        prev[item.emuId] = true;
        return prev;
      }, {});
    },
    roleCodeNameMap({ roleList }) {
      return roleList.reduce((prev, item) => {
        prev[item.code] = item.name;
        return prev;
      }, {});
    },
    nodeColumns({ nodeList, roleCodeNameMap }) {
      if (nodeList) {
        let roleIds = [
          ...new Set(
            nodeList
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
      }
    },
    nodeDealData({ nodeList }) {
      if (nodeList) {
        return nodeList.map((node) => {
          const record = node.nodeRecords.reduce((prev, item) => {
            if (prev[item.roleId]) {
              prev[item.roleId] = prev[item.roleId] + ',' + item.workerName;
            } else {
              prev[item.roleId] = item.workerName;
            }

            // prev[item.roleId] = `${item.workerName}在 ${item.recordTime} 打了卡`;
            return prev;
          }, {});
          const roleWorkerPicUrls = node.nodeRecords.reduce((prev, item) => {
            if (!prev[item.roleId]) {
              prev[item.roleId] = {};
            }
            prev[item.roleId] = node.nodeRecords.reduce((prev, item) => {
              if (!prev[item.workerId]) {
                prev[item.workerId] = [];
              }
              prev[item.workerId] = [
                ...prev[item.workerId],
                ...item.pictureUrls.map((item) => {
                  let url = item.relativeUrl.split('/');
                  return {
                    name: url[url.length - 1],
                    url: `/storageTrainRepair/${item.relativeUrl}`,
                  };
                }),
              ];

              return prev;
            }, {});
            return prev;
          }, {});

          const roleWorkerDetails = node.nodeRecords.reduce((prev, item) => {
            if (!prev[item.roleId]) {
              prev[item.roleId] = {};
            }
            prev[item.roleId] = node.nodeRecords.reduce((prev, item) => {
              if (!prev[item.workerId]) {
                prev[item.workerId] = [];
              }
              prev[item.workerId] = `${item.workerName}在 ${item.recordTime} 打卡`;

              return prev;
            }, {});
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
      }
    },

    pgMMap({ pgMList }) {
      return pgMList.reduce((prev, item) => {
        // if (!prev[item.trainsetId]) {
        prev[item.trainsetId] = item;
        // }
        // prev[item.trainsetId].push(item);
        return prev;
      }, {});
    },

    // 故障列表
    getFaultListTableData() {
      let faultListTableData = copyData(this.faultListTableData);
      // let pageCount = Math.ceil(faultListTableData / this.faultListTablePageSize)
      return faultListTableData.slice(
        (this.faultListTableCurrentPage - 1) * this.faultListTablePageSize,
        this.faultListTableCurrentPage * this.faultListTablePageSize
      );
    },
  },
  watch: {
    'baseFormConfig.TrackRefreshTime'(val) {
      // 页面切换定时器
      this.timerStart(val);
    },
    iframeVisible(val) {
      if (val) {
        this.timerEnd();
      } else {
        this.timerStart(this.baseFormConfig.TrackRefreshTime);
      }
    },
    rightMenuShow(val) {
      if (val) {
        this.timerEnd();
      } else {
        this.timerStart(this.baseFormConfig.TrackRefreshTime);
      }
    },
    'baseFormConfig.MovePlan'(val) {
      if (val) {
        this.otherTrainGroup.otherHeight = '40%';
      } else {
        this.otherTrainGroup.otherHeight = '25%';
      }
    },
    // 出入所颜色初始值
    trainGroupNameShowList: {
      handler(val) {
        val.forEach((item) => {
          this.$set(this.inOutTrainGroup.inOutActive, item.trainsetid, false);
        });
        this.focusId = '';
      },
      immediate: true,
    },

    nowPageTrainIds: {
      handler(ids, oldIds) {
        let deletedTrainsetIds = oldIds.filter((v) => !ids.includes(v));
        let addedTrainsetIds = ids.filter((v) => !oldIds.includes(v));
        // console.log('nowPageTrainIds_changed', ids, oldIds);
        // console.log('deletedTrainsetIds', deletedTrainsetIds);
        // console.log('addedTrainsetIds', addedTrainsetIds);

        updateData(
          this.flowList,
          [],
          (v) => (this.flowList = v),
          deletedTrainsetIds,
          (v) => v.trainsetId
        );
        updateData(
          this.lookupFlowList,
          [],
          (v) => (this.lookupFlowList = v),
          deletedTrainsetIds,
          (v) => v.trainsetId
        );
        // 获取本页一二流程信息
        addedTrainsetIds.forEach(async (id) => {
          const { code, data } = await getPromise('getFlowRunInfos_' + id, () =>
            getFlowRunInfos(this.unitCode, this.dayPlanId, id, this.baseConfigDialog.formData.ShowLastRepairTask)
          );
          if (code == 1) {
            if (
              data.flowRunInfoGroups[0] &&
              data.flowRunInfoGroups[0].flowRunInfoForSimpleShows &&
              data.flowRunInfoGroups[0].flowRunInfoForSimpleShows[0]
            ) {
              this.flowList = [...this.flowList, data.flowRunInfoGroups[0].flowRunInfoForSimpleShows[0]];
              data.flowRunInfoGroups[0].flowRunInfoForSimpleShows.forEach((flow, index) => {
                this.lookupFlowList = [...this.lookupFlowList, data.flowRunInfoGroups[0].flowRunInfoForSimpleShows[index]];
              });
            }
          }
        });

        updateData(
          this.hostLingList,
          [],
          (v) => (this.hostLingList = v),
          deletedTrainsetIds,
          (v) => v.trainsetId
        );
        // 获取本页整备流程信息
        // addedTrainsetIds.forEach(async (id) => {
        //   const { code, data } = await getPromise('getHostLingFlowRunInfos_' + id, () =>
        //     getHostLingFlowRunInfos(
        //       this.unitCode,
        //       this.dayPlanId,
        //       id,
        //       this.baseConfigDialog.formData.ShowLastRepairTask
        //     )
        //   );
        //   if (code == 1) {
        //     let flowRunInfoGroups = data.flowRunInfoGroups;
        //     if (
        //       flowRunInfoGroups[0] &&
        //       flowRunInfoGroups[0].flowRunInfoForSimpleShows &&
        //       flowRunInfoGroups[0].flowRunInfoForSimpleShows[0]
        //     ) {
        //       this.hostLingList = [...this.hostLingList, flowRunInfoGroups[0].flowRunInfoForSimpleShows[0]];
        //     }
        //   }
        // });

        updateData(
          this.pgMList,
          [],
          (v) => (this.pgMList = v),
          deletedTrainsetIds.map((v) => this.trainsetIdNameMap[v]),
          (v) => v.S_TRAINSETNAME
        );
        // 获取本页检修信息
        addedTrainsetIds.forEach(async (id) => {
          const { code, data } = await getPromise('getPgMPacketrecordList_' + id, () =>
            getPgMPacketrecordList({
              trainsetId: id,
              trainsetName: this.trainsetIdNameMap[id],
              unitCode: this.unitCode,
            })
          );

          if (code == 1) {
            this.pgMList = [...this.pgMList, data];
          }
        });
      },
    },

    'baseConfigDialog.formData.ShowLastRepairTask': {
      handler(val, oldVal) {},
    },

    limitMoveNum() {
      this.scrollKey += 1;
    },
    chooseJobPackageList(val) {
      let checkedCount = val.length;
      this.chooseAllJobPackage = checkedCount === this.jobPackageList.length;
      this.isIndeterminateChooseJobPackage = checkedCount > 0 && checkedCount < this.jobPackageList.length;
    },
    'lookupNodeDealInfoDialog.visible'(val) {
      // 打开关闭dialog刷新表格
      this.tableRefresh = new Date().getTime()
    }
  },
  async created() {
    this.params = location.search;
    await this.getTrainsetLocationConfigs();
    await this.getUnitCode();
    await this.getUnitName();
    await this.getConfigList();

    // 请求
    this.createRequests();

    this.timer = setInterval(() => {
      this.time = dayjs().format('YYYY-MM-DD HH:mm:ss');
    }, 1000);

    // 调车计划自动调车
    this.shuntTimer = setInterval(() => {
      if (!this.baseFormConfig.AutoMoveTrain) {
        const nowTimeStamp = Number(String(new Date().getTime()).slice(0, 10));
        const outTrainList = this.shuntInTimeMap[nowTimeStamp];

        if (outTrainList) {
          outTrainList.forEach(async (train) => {
            const trainInfo = this.trainsetIdInfoMap[train.emuId];
            if (trainInfo) {
              if (trainInfo.isConnect == '1' && train.relatedEmuId == '--') {
                const trackTrainIds = this.trainsetInTrackMap[trainInfo.trackCode];
                const trainsetPostIonIds = trackTrainIds.map((trainsetId) => {
                  return this.trainsetIdInfoMap[trainsetId].trainsetPostionId;
                });
                await setTrainsetState({
                  status: '0',
                  trainsetPostIonIds,
                });
                await this.getAllTrackArea(null, trackTrainIds);
              }

              // 车在股道上，转线
              await this.updTrackCode(trainInfo, {
                trackPositionCode: trainInfo.nextTrackPositionCode,
                trackCode: trainInfo.nextTrackCode,
              });
            } else {
              // 车不在股道上，新增
              await this.insertTrainsetPostIon(
                { trainsetId: train.emuId },
                {
                  trackPositionCode: train.trackPositionCode,
                  trackCode: train.trackCode,
                }
              );
            }
          });
        }
      }
    }, 1000);

    // 数据刷新
    this.refreshTimer = setInterval(async () => {
      this.requests();
    }, (this.baseFormConfig.DataRefreshTime || 60) * 1000);
    this.$once('hook:beforeDestroy', () => {
      this.timer = null;
      this.shuntTimer = null;
      this.refreshTimer = null;
    });
  },
  mounted() {
    this.resize();
    // 监听全屏
    window.addEventListener('resize', this.resize);

    // 监听点击
    window.addEventListener('click', this.bodyClick);
    this.$once('hook:beforeDestroy', () => {
      window.removeEventListener('resize', this.resize);
      window.removeEventListener('click', this.bodyClick);
    });
  },
  updated() {
    let scrollUl = this.$refs.scrollUl && this.$refs.scrollUl.clientHeight;
    let scroll = this.$refs.scroll && this.$refs.scroll.$el.clientHeight;
    if (scrollUl > scroll) {
      this.limitMoveNum = this.noteConfigDialog.noteList.length;
    } else {
      this.limitMoveNum = 10;
    }
  },
  methods: {
    // 获取运用所编码
    async getUnitCode() {
      let data = await getUnitCode();
      if (data.code == 1) {
        this.unitCode = data.data;
      } else {
        this.$message.error('获取登录人信息失败！');
      }
    },

    // 获取运用所名称
    async getUnitName() {
      let data = await getUnitName();
      if (data.code == 1) {
        this.unitName = data.data;
      } else {
        this.$message.error('获取登录人信息失败！');
      }
    },
    // 页面初始化请求
    async createRequests() {
      this.getDay();
      // 所有股道
      await this.getAllTrackArea();
      this.getConfigToLocal();
      this.getPostRoleList();
      // 所有车组
      this.getTrainsetList();
      // 出入所车组
      this.getTrainsetListReceived();
      // 其他车组
      this.getTrainsetHotSpareInfo();
      // 调车记事
      this.getMonitornotecontents();
      // 重联配置
      this.getConnectTrainTypes();
      // 作业包
      this.getMonitorPackets();
      // 车

      this.getShuntingPlanByCondition();
      this.getRunRoutingDataByDate();
      this.getWarningConfig();

      this.getTrackPowerInfo();
    },
    // 定时刷新请求
    async requests() {
      this.getDay();
      // 所有股道
      await this.getAllTrackArea();
      this.getConfigToLocal();
      this.getPostRoleList();
      // 所有车组
      this.getTrainsetList();
      // 出入所车组
      this.getTrainsetListReceived();
      // 其他车组
      this.getTrainsetHotSpareInfo();
      // 调车记事
      this.getMonitornotecontents();
      // 重联配置
      this.getConnectTrainTypes();
      // 作业包
      this.getMonitorPackets();
      // 车

      this.getShuntingPlanByCondition();
      this.getRunRoutingDataByDate();
      this.getWarningConfig();

      this.getTrackPowerInfo();
      // 获取本页一二流程信息
      this.getFlowRun();
    },
    // 获取全局配置
    async getConfigList() {
      const { data } = await getConfigList();
      this.globalConfigObj = data.reduce((prev, item) => {
        prev[item.paramName] = item.paramValue;
        return prev;
      }, {});
    },
    // 获取基础配置
    async getTrainsetLocationConfigs() {
      const { code, data } = await getTrainsetLocationConfigs();
      if (code == 1) {
        this.baseConfigDialog.configList = data;
        this.baseConfigDialog.formData = copyData(this.baseFormConfig);
        this.oriShowLastRepairTask = this.baseConfigDialog.formData.ShowLastRepairTask;
        if (this.baseConfigDialog.configList.length === 0) {
          let configs = [
            {
              id: '14',
              paramName: 'MovePlan',
              paramValue: '1',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '88',
              paramName: 'ShowTrackMenu',
              paramValue: '1',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '11',
              paramName: 'TrackCode',
              paramValue: '1',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '12',
              paramName: 'ShowNoTrainsetTrack',
              paramValue: '1',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '13',
              paramName: 'TrackPowerManualInput',
              paramValue: '1',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '1',
              paramName: 'TrackRefreshTime',
              paramValue: '60',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '3',
              paramName: 'ShowRepair',
              paramValue: '1',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '4',
              paramName: 'AutoMoveTrain',
              paramValue: '1',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '5',
              paramName: 'IsMoveTrainPlanWarning',
              paramValue: '1',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '6',
              paramName: 'MoveTrainPlanWarningTime',
              paramValue: '3',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '7',
              paramName: 'ShowMoveTrainPlan',
              paramValue: '1',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '8',
              paramName: 'ShowTrainsetTrackcode',
              paramValue: '1',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '9',
              paramName: 'HeadDirection',
              paramValue: '1',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '10',
              paramName: 'NoteName',
              paramValue: '调车记事',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '15',
              paramName: 'DataRefreshTime',
              paramValue: '60',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '99',
              paramName: 'ShowRunningPlan',
              paramValue: '1',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '889',
              paramName: 'ShowLastRepairTask',
              paramValue: '1',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
            {
              id: '778',
              paramName: 'ShowShuntPlan',
              paramValue: '1',
              unitCode: this.unitCode,
              unitName: this.unitName,
            },
          ];
          const ids = await this.getNewIds(configs.length);
          configs.forEach((item, index) => {
            item.id = ids[index];
          });
          await addTrainsetLocationConfigs(configs);
          this.getTrainsetLocationConfigs();
        }
      }
    },
    async getNewIds(number = 1) {
      const { data } = await getNewId({ number });
      return data;
    },
    setTrackShowConfig({ trackCode, value }) {
      this.$set(this.baseConfigDialog.trackShowConfig, trackCode, value);
    },
    openBaseConfigDialog() {
      this.configButtonsVisible = false;
      this.baseConfigDialog.visible = true;
      this.baseConfigDialog.trackShowConfig = copyData(this.localtrackShowConfig);
      this.baseConfigDialog.formData = copyData(this.baseFormConfig);
    },
    updBaseConfigBtn() {
      if(!this.confirmCheck()) return
      let postArr = [];
      for (const key in this.baseConfigDialog.formData) {
        const element = this.baseConfigDialog.formData[key];
        if (
          key == 'TrackRefreshTime' ||
          key == 'MoveTrainPlanWarningTime' ||
          key == 'NoteName' ||
          key == 'DataRefreshTime'
        ) {
          postArr.push({
            id: this.configIdObj[key],
            paramValue: element,
          });
        } else {
          postArr.push({
            id: this.configIdObj[key],
            paramValue: element ? '0' : '1',
          });
        }
      }

      this.baseConfigDialog.oldTrackPositionChange = this.baseConfigDialog.TrackPositionChange;

      this.updTrainsetLocationConfig(postArr);
      this.setConfigToLocal();
      this.getConfigToLocal();
      if (this.baseConfigDialog.formData.ShowLastRepairTask != this.oriShowLastRepairTask) {
        this.getFlowRun();
      }
    },
    // 股道切换时间和提前预警时长检查
    confirmCheck() {
      if(!this.baseConfigDialog.formData.TrackRefreshTime && !this.baseConfigDialog.formData.ShowTrackRefresh) {
        this.$message.error('股道切换时间不能为空！')
        return false
      }
      if(this.baseConfigDialog.formData.TrackRefreshTime < 30 && !this.baseConfigDialog.formData.ShowTrackRefresh) {
        this.$message.error('股道切换时间最小为30秒！')
        return false
      }
      if(!this.baseConfigDialog.formData.MoveTrainPlanWarningTime) {
        this.$message.error('提前预警时长不能为空！')
        return false
      }
      return true
    },
    cancel() {
      this.baseConfigDialog.TrackPositionChange = this.baseConfigDialog.oldTrackPositionChange;
      this.baseConfigDialog.visible = false;
    },

    getFlowRun() {
      // 获取任务
      this.getRepairTask(this.nowPageTrainIds);

      // 获取本页一二流程信息
      this.flowList = [];
      this.lookupFlowList = [];
      this.nowPageTrainIds.forEach(async (id) => {
        const { code, data } = await getPromise('getFlowRunInfos_' + id, () =>
          getFlowRunInfos(this.unitCode, this.dayPlanId, id, this.baseConfigDialog.formData.ShowLastRepairTask)
        );
        if (code == 1) {
          if (
            data.flowRunInfoGroups[0] &&
            data.flowRunInfoGroups[0].flowRunInfoForSimpleShows &&
            data.flowRunInfoGroups[0].flowRunInfoForSimpleShows[0]
          ) {
            this.flowList = [...this.flowList, data.flowRunInfoGroups[0].flowRunInfoForSimpleShows[0]];
            data.flowRunInfoGroups[0].flowRunInfoForSimpleShows.forEach((flow, index) => {
              this.lookupFlowList = [...this.lookupFlowList, data.flowRunInfoGroups[0].flowRunInfoForSimpleShows[index]];
            });
          }
        }
      });

      // 获取本页整备流程信息
      // this.hostLingList = [];
      // this.nowPageTrainIds.forEach(async (id) => {
      //   const { code, data } = await getPromise('getHostLingFlowRunInfos_' + id, () =>
      //     getHostLingFlowRunInfos(this.unitCode, this.dayPlanId, id, this.baseConfigDialog.formData.ShowLastRepairTask)
      //   );
      //   if (code == 1) {
      //     let flowRunInfoGroups = data.flowRunInfoGroups;
      //     if (
      //       flowRunInfoGroups[0] &&
      //       flowRunInfoGroups[0].flowRunInfoForSimpleShows &&
      //       flowRunInfoGroups[0].flowRunInfoForSimpleShows[0]
      //     ) {
      //       this.hostLingList = [...this.hostLingList, flowRunInfoGroups[0].flowRunInfoForSimpleShows[0]];
      //     }
      //   }
      // });
    },

    async updTrainsetLocationConfig(data) {
      const { code } = await updTrainsetLocationConfig(data);
      if (code == 1) {
        this.$message.success('基础配置已变更');
        this.getTrainsetLocationConfigs();
        this.baseConfigDialog.visible = false;
      }
    },

    getConfigToLocal() {
      const trainMonitorStr = localStorage.getItem('trainMonitor');
      if (trainMonitorStr) {
        this.localtrackShowConfig = JSON.parse(trainMonitorStr);
      } else {
        this.trackList.forEach((track) => {
          this.$set(this.localtrackShowConfig, track.trackCode, true);
        });
      }
    },

    setConfigToLocal() {
      const trainMonitorStr = JSON.stringify(this.baseConfigDialog.trackShowConfig);
      localStorage.setItem('trainMonitor', trainMonitorStr);
    },

    // 关闭车组切换的定时器
    timerEnd() {
      if (this.baseConfigDialog.timer) {
        clearInterval(this.baseConfigDialog.timer);
      }
    },

    // 开启车组切换的定时器
    timerStart(time) {
      this.timerEnd();
      if (time != 0 && !this.baseConfigDialog.formData.ShowTrackRefresh) {
        this.baseConfigDialog.timer = setInterval(() => {
          let pageNum = this.trackPageNum + 1;
          if (pageNum === this.trackPageCount + 1) {
            this.trackPageNum = 1;
          } else {
            this.trackPageNum = pageNum;
          }
        }, time * 1000);
      }
    },
    wrapperMouseOver() {
      this.timerEnd();
    },
    wrapperMouseLeave() {
      if (this.iframeVisible) {
        return;
      }
      this.timerStart(this.baseFormConfig.TrackRefreshTime);
    },
    // 开行信息

    async getRunRoutingDataByDate() {
      const { code, data } = await getRunRoutingDataByDate({
        date: dayjs().format('YYYYMMDD'),
        planFlag: '0',
        trainsetId: '',
      });
      if (code == 1) {
        data.forEach((infoItem) => {
          infoItem.crews.forEach((manItem) => {
            if (manItem.trainsetId === infoItem.trainsetId) {
              if (!infoItem.man) {
                infoItem.man = [];
              }

              infoItem.man.push(manItem.stuffName + ',' + manItem.stuffMobile.replace('-', '/'));
            } else if (manItem.trainsetId === infoItem.joinTrainsetId) {
              if (!infoItem.joinMan) {
                infoItem.joinMan = [];
              }
              infoItem.joinMan.push(manItem.stuffName + ',' + manItem.stuffMobile);
            }
          });
        });
        data.forEach((item) => {
          if (item.man) {
            item.man = item.man.toString();
          }
          if (item.joinMan) {
            item.joinMan = item.joinMan.toString();
          }
        });
        this.runInfoList = data;
      }
    },

    openRunInfoDialog(trainsetId) {
      this.runInfoDialog.tableCurrentPage = 1;
      this.runInfoDialog.tablePageSize = 20;
      this.runInfoDialog.tableData = this.runInfoList.filter((item) => !trainsetId || item.trainsetId === trainsetId);
      this.runInfoDialog.tableTotal = this.runInfoDialog.tableData.length;
      this.runInfoDialog.visible = true;
      this.$nextTick(() => {
        this.initRunInfoTableHeight();
      });
    },
    initRunInfoTableHeight() {
      this.runInfoDialog.tableHeight = window.innerHeight - 200 + 'px';
    },
    handleRunInfoTableSizeChange(pageSize) {
      this.runInfoDialog.tablePageSize = pageSize;
    },
    handleRunInfoTableCurrentChange(pageNum) {
      this.runInfoDialog.tableCurrentPage = pageNum;
    },
    timeFormat(row, column, cellValue, index) {
      return dayjs(cellValue).format('YYYY-MM-DD HH:mm');
    },
    // 调车信息
    async getShuntingPlanByCondition() {
      const {
        data: { startTime, endTime },
      } = await getWorkTimeByDayPlanId(this.dayPlanId);
      const { code, data } = await getShuntingPlanByCondition(startTime, endTime, this.unitCode, '');
      if (code == 1) {
        this.shuntInfoList = data;
      }
    },
    async openShuntInfoDialog(trainsetId) {
      this.shuntInfoDialog.tablePageSize = 20;
      this.shuntInfoDialog.tableCurrentPage = 1;
      this.shuntInfoDialog.tableData = this.shuntInfoList.filter((item) => !trainsetId || item.emuId === trainsetId);
      this.shuntInfoDialog.tableTotal = this.shuntInfoDialog.tableData.length;
      this.shuntInfoDialog.visible = true;
      this.$nextTick(() => {
        this.initShuntInfoTableHeight();
      });
    },
    initShuntInfoTableHeight() {
      this.shuntInfoDialog.tableHeight = window.innerHeight - 200 + 'px';
    },
    handleShuntInfoTableSizeChange(pageSize) {
      this.shuntInfoDialog.tablePageSize = pageSize;
    },
    handleShuntInfoTableCurrentChange(pageNum) {
      this.shuntInfoDialog.tableCurrentPage = pageNum;
    },
    // 显示设置按钮
    showConfigButtons() {
      this.configButtonsVisible = !this.configButtonsVisible;
    },
    bodyClick(e) {
      let target = e.target;
      // 调车记事标题
      if (this.$refs.dblInputRef) {
        if (!this.$refs.dblInputRef.$el.contains(target) && target !== 'I') {
          this.dbltitleEnter();
        }
      }
      // 右键菜单
      if (this.$refs.rightMenu && this.$refs.rightMenu.$el.contains(target)) {
        return;
      }

      this.focusId = '';
      // 重置
      Object.keys(this.inOutTrainGroup.inOutActive).forEach((item) => {
        this.$set(this.inOutTrainGroup.inOutActive, item, false);
      });
      this.rightMenuShow = false;
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
        this.trackPageSize = 6;
      } else {
        this.isFullScreen = false;
        this.trackPageSize = 4;
      }

      this.trackContainerHeight = window.innerHeight - this.$refs.header.$el.offsetHeight - 100 + 'px';
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

    // 获取所有重联车型配置
    async getConnectTrainTypes() {
      const {
        code,
        data: { count, monitorNoteContents },
      } = await getConnectTrainTypes({
        pageNum: this.reConnectTableCurrentPage,
        pageSize: this.reConnectTablePageSize,
      });
      if (code == 1) {
        this.reconnectConfigTableData = monitorNoteContents;
        this.reConnectTableTotal = count;
      }
    },
    // 重联车型选择列表
    async getTrainTypeList() {
      const {
        data: { data },
      } = await getTrainTypeList();
      this.trainTypeList = data;
    },
    // 打开重联配置弹框
    openReconnectConfigDialog() {
      this.configButtonsVisible = false;
      this.getTrainTypeList();
      this.reconnectConfigDialogVisible = true;
      this.$nextTick(() => {
        this.initReconnectConfigTableHeight();
      });
    },
    // 初始化表格高度
    initReconnectConfigTableHeight() {
      this.reconnectConfigTableHeight = window.innerHeight - 200 + 'px';
    },
    // 打开重联车型选择框
    editReconnectConfig(data) {
      this.connectTrainTypeId = data.id;
      this.connectTrainTypeTrainType = data.trainType;
      if (data.connectTrainType === '') {
        this.connectTrainType = [];
      } else {
        this.connectTrainType = data.connectTrainType.split(',');
      }
      // 原数据选择的重联车型
      this.prevConnectTrainType = JSON.parse(JSON.stringify(data.connectTrainType.split(',')));
      this.chooseTrainTypeDialogVisible = true;
      this.$nextTick(() => {
        this.initChooseTrainTypeDialogBodyHeight();
      });
    },
    // 重置该车的重连配置
    resetReconnectConfig(data) {
      this.connectTrainTypeId = data.id;
      this.connectTrainTypeTrainType = data.trainType;
      this.connectTrainType = [];
      // 原数据选择的重联车型
      this.prevConnectTrainType = JSON.parse(JSON.stringify(data.connectTrainType.split(',')));
      this.chooseTrainTypeConfirmBtn();
    },
    // 初始化body高度
    initChooseTrainTypeDialogBodyHeight() {
      this.chooseTrainTypeDialogBodyHeight = window.innerHeight - 400 + 'px';
    },
    async chooseTrainTypeConfirmBtn() {
      // 该项选择完成后
      this.reconnectConfigTableData.forEach((configItem) => {
        if (configItem.trainType === this.connectTrainTypeTrainType) {
          configItem.connectTrainType = this.connectTrainType.toString();
        }
      });

      // 对应车型增加
      this.connectTrainType.forEach((connectItem) => {
        this.reconnectConfigTableData.forEach((configItem) => {
          if (connectItem === configItem.trainType) {
            if (!configItem.connectTrainType) {
              configItem.connectTrainType = this.connectTrainTypeTrainType;
            } else {
              let arr = configItem.connectTrainType.split(',');
              if (!arr.includes(this.connectTrainTypeTrainType)) {
                arr.push(this.connectTrainTypeTrainType);
              }
              configItem.connectTrainType = arr.toString();
            }
          }
        });
      });

      // 对应车型删除

      let decreaseArr = [];

      this.prevConnectTrainType.forEach((nowItem) => {
        if (!this.connectTrainType.includes(nowItem)) {
          decreaseArr.push(nowItem);
        }
      });

      decreaseArr.forEach((disConnectItem) => {
        this.reconnectConfigTableData.forEach((configItem) => {
          if (disConnectItem === configItem.trainType) {
            let arr = configItem.connectTrainType.split(',');
            configItem.connectTrainType = arr.filter((item) => item !== this.connectTrainTypeTrainType).toString();
          }
        });
      });

      // const postJson = {
      //   connectTrainType: this.connectTrainType.toString(),
      //   id: this.connectTrainTypeId,
      //   trainType: this.connectTrainTypeTrainType,
      // };
      await updConnectTrainType(this.reconnectConfigTableData);
      // return
      this.getConnectTrainTypes();
      this.chooseTrainTypeDialogVisible = false;
    },
    handleReConnectTableSizeChange(pageSize) {
      this.reConnectTablePageSize = pageSize;
      this.getConnectTrainTypes();
    },
    handleReConnectTableCurrentChange(pageNum) {
      this.reConnectTableCurrentPage = pageNum;
      this.getConnectTrainTypes();
    },
    // 检修作业包配置
    // 获取所有作业包
    async getMonitorPackets() {
      const { code, data } = await getMonitorPackets({
        departmentCode: this.unitCode,
        pageNum: this.repairTableCurrentPage,
        pageSize: this.repairTablePageSize,
      });
      if (code == 1) {
        const { records, total } = data;

        this.repairTableTotal = total;
        this.repairConfigTableData = records;
      }
    },
    // 获取选择框作业包集合
    async getPacketList(params) {
      const { code, data } = await getPacketList(params);
      if (code == 1) {
        this.jobPackageList = data;
      }
    },
    // 打开检修作业包配置
    openRepairConfigDialog() {
      this.configButtonsVisible = false;
      this.repairConfigDialogVisible = true;
      this.$nextTick(() => {
        this.initRepairConfigTableHeight();
      });
    },
    // 初始化高度
    initRepairConfigTableHeight() {
      this.repairConfigTableHeight = window.innerHeight - 200 + 'px';
    },
    // 打开作业包选择框
    async editJobPackageConfig(data) {
      await this.getPacketList({
        trainType: data.trainType,
        batchCode: data.batchCode,
      });
      this.chooseJobPackageDialogVisible = true;

      this.chooseJobPackageList = data.monitorWithPacketsList.map((item) => item.packetCode);
      this.postPacketJson.id = data.id;
      this.postPacketJson.batchCode = data.batchCode;
      this.postPacketJson.trainType = data.trainType;
      this.$nextTick(() => {
        this.initChooseJobPackageDialogBodyHeight();
      });
    },
    // 全选作业包
    chooseJobPackageAllChange(val) {
      this.chooseJobPackageList = val ? this.jobPackageList.map((item) => {return item.packetCode}) : [];
      this.isIndeterminateChooseJobPackage = false;
    },
    // 选择作业包
    // chooseJobPackageChange(value) {
    //   let checkedCount = this.chooseJobPackageList.length;
    //   this.chooseAllJobPackage = checkedCount === this.jobPackageList.length;
    //   this.isIndeterminateChooseJobPackage = checkedCount > 0 && checkedCount < this.jobPackageList.length;
    // },
    // 初始化body高度
    initChooseJobPackageDialogBodyHeight() {
      this.chooseJobPackageDialogBodyHeight = window.innerHeight - 400 + 'px';
    },

    async resetJobPackage(data) {
      this.postPacketJson = {
        trainType: data.trainType,
        batchCode: data.batchCode,
        id: data.id,
        monitorWithPacketsList: [],
      };
      await updMonitorPacket([this.postPacketJson]);
      await this.getMonitorPackets();
    },

    // 确定修改作业包
    async updMonitorPacketConfirmBtn() {
      let jobPackageMap = this.jobPackageList.reduce((prev, item) => {
        prev[item.packetCode] = item;
        return prev;
      }, {});
      this.postPacketJson.monitorWithPacketsList = this.chooseJobPackageList.map((item) => {
        return {
          packetCode: jobPackageMap[item].packetCode,
          packetName: jobPackageMap[item].packetName,
        };
      });
      await updMonitorPacket([this.postPacketJson]);
      await this.getMonitorPackets();
      this.chooseJobPackageDialogVisible = false;
    },
    handleRepairTableSizeChange(pageSize) {
      this.repairTablePageSize = pageSize;
      this.getMonitorPackets();
    },
    handleRepairTableCurrentChange(pageNum) {
      this.repairTableCurrentPage = pageNum;
      this.getMonitorPackets();
    },

    // 检修信息
    async getPgMPacketrecordList(params) {
      const { code, data } = await getPgMPacketrecordList(params);
      if (code == 1) {
        this.yyRepairInfoTableData = data;
      }
    },
    openRepairInfoDialog(trainsetId) {
      if (this.pgMMap[trainsetId]) {
        this.yyRepairInfoTableData = this.pgMMap[trainsetId].unitLevelRepairInfos;
        this.gjRepairInfoTableData = this.pgMMap[trainsetId].highLevelRepairInfos;
      } else {
        this.yyRepairInfoTableData = [];
        this.gjRepairInfoTableData = [];
      }

      this.repairInfoDialogVisible = true;
      this.$nextTick(() => {
        this.initRepairInfoTableHeight();
      });
    },
    initRepairInfoTableHeight() {
      this.repairInfoTableHeight = window.innerHeight - 400 + 'px';
    },
    // 故障列表

    async getFaultDataByIdList(params) {
      const {
        code,
        data: { records, total },
      } = await getFaultDataByIdList(params);
      if (code == 1) {
        this.faultListTableData = records;
        this.faultListTableTotal = total;
      }
    },
    openFaultListDialog(trainsetId) {
      this.faultTrainsetId = trainsetId;
      // const itemId = this.trainsetIdItemIdMap[trainsetId]&&this.trainsetIdItemIdMap[trainsetId].toString();
      // this.getFaultDataByIdList({
      //   itemCode: itemId,
      //   pageNum: this.faultListTableCurrentPage,
      //   pageSize: this.faultListTablePageSize,
      // });
      this.faultListTableData = this.trainsetIdTaskMap[trainsetId] && this.trainsetIdTaskMap[trainsetId].faultItemList;
      this.faultListTableTotal =
        this.trainsetIdTaskMap[trainsetId] && this.trainsetIdTaskMap[trainsetId].faultItemList.length;
      this.faultListDialogVisible = true;
      this.$nextTick(() => {
        this.initFaultListTableHeight();
      });
    },
    initFaultListTableHeight() {
      this.faultListTableHeight = window.innerHeight - 200 + 'px';
    },
    handleFaultListTableSizeChange(pageSize) {
      this.faultListTablePageSize = pageSize;
      // const itemId = this.trainsetIdItemIdMap[this.faultTrainsetId].toString();

      // this.getFaultDataByIdList({
      //   itemCode: itemId,
      //   pageNum: this.faultListTableCurrentPage,
      //   pageSize: this.faultListTablePageSize,
      // });
    },
    handleFaultListTableCurrentChange(pageNum) {
      this.faultListTableCurrentPage = pageNum;
      // const itemId = this.trainsetIdItemIdMap[this.faultTrainsetId].toString();

      // this.getFaultDataByIdList({
      //   itemCode: itemId,
      //   pageNum: this.faultListTableCurrentPage,
      //   pageSize: this.faultListTablePageSize,
      // });
    },

    // 预警信息

    openWarningDialog(warningType, trainsetId) {
      this.warningType = warningType;

      if (this.warningType === 'SJ') {
        // SJ
        this.warningTableData = this.trainsetIdSJMap[trainsetId];
      } else {
        // LY
        this.warningTableData = this.trainsetIdLYMap[trainsetId];
      }

      this.warningTableTotal = this.warningTableData.length;
      this.warningDialogVisible = true;
      this.$nextTick(() => {
        this.initWarningTableHeight();
      });
    },
    initWarningTableHeight() {
      this.warningTableHeight = window.innerHeight - 200 + 'px';
    },
    handleWarningTableSizeChange(pageSize) {
      this.warningTablePageSize = pageSize;
    },
    handleWarningTableCurrentChange(pageNum) {
      this.warningTableCurrentPage = pageNum;
    },

    // 出入所车组
    async getTrainsetListReceived() {
      const { code, data } = await getTrainsetListReceived({
        unitCode: this.unitCode,
      });
      if (code == 1) {
        this.inOutTrainGroup.trainGroupNameList = data;
      }
    },

    // 获取所有车组
    async getTrainsetList() {
      const { code, data } = await getTrainsetList();
      if (code == 1) {
        this.trainsetAllList = data;
      }
    },

    getTrainTrackPostionInfo(trainsetId) {
      if (
        this.trainsetIdTrackCodeMap[trainsetId] &&
        this.trackCodeNameMap[this.trainsetIdTrackCodeMap[trainsetId]] &&
        this.trainsetIdPositionCodeMap[trainsetId]
      ) {
        const trackName = this.trackCodeNameMap[this.trainsetIdTrackCodeMap[trainsetId]];
        return this.trainsetIdPositionCodeMap[trainsetId]
          .map((positionCode) => `${trackName}-${this.positionCodeNameMap[positionCode]}`)
          .toString();
      } else {
        return '';
      }
    },
    // 其他车组
    async getTrainsetHotSpareInfo() {
      const { code, data } = await getTrainsetHotSpareInfo();
      if (code == 1) {
        this.otherTrainGroup.otherTrainGroupNameList = data || [];
      }
    },
    // 调车记事
    // 双击调车记事
    shuntingNotesDblclick() {
      this.noteNameShow = this.baseFormConfig['NoteName'];
      this.noteConfigDialog.dblState = true;
      this.$nextTick(() => {
        this.$refs.dblInputRef.focus();
      });
    },
    dbltitleEnter() {
      if (this.noteNameShow.length != 0) {
        this.updTrainsetLocationConfig([
          {
            id: this.configIdObj['NoteName'],
            paramValue: this.noteNameShow,
          },
        ]);
      }
      this.noteConfigDialog.dblState = false;
    },
    // 右键
    noteRgihtClick() {
      this.noteConfigDialog.visible = true;
    },
    // 查
    async getMonitornotecontents() {
      const { data } = await getMonitornotecontents();
      this.noteConfigDialog.noteList = data;
    },
    addNote() {
      this.noteConfigDialog.noteDialogContent = '';
      this.noteConfigDialog.noteState = 'add';
      this.noteConfigDialog.editNoteDialogVisible = true;
    },

    editNoteContent(data) {
      this.noteConfigDialog.noteState = 'edit';
      this.noteConfigDialog.editNoteDialogVisible = true;
      this.noteConfigDialog.noteDialogContent = data.content;
      this.noteConfigDialog.noteId = data.id;
    },
    // 改
    async noteDialogContentEditBtn() {
      await updMonitornotecontent({
        content: this.noteConfigDialog.noteDialogContent,
        id: this.noteConfigDialog.noteId,
      });
      await this.getMonitornotecontents();
      this.noteDialogContentCancel();
    },
    // 增
    async noteDialogContentAddBtn() {
      await addMonitornotecontent({
        content: this.noteConfigDialog.noteDialogContent,
      });
      await this.getMonitornotecontents();
      this.noteDialogContentCancel();
    },
    // 删
    async deleteNote(id) {
      await delMonitornotecontent({
        id,
      });
      await this.getMonitornotecontents();
    },
    noteDialogContentCancel() {
      this.noteConfigDialog.editNoteDialogVisible = false;
      this.noteConfigDialog.noteDialogContent = '';
      this.noteConfigDialog.noteState = '';
    },
    async getDay() {
      const { dayPlanId } = await getDay();
      this.dayPlanId = dayPlanId;
    },
    // main车组

    // 获取显示股道
    async getAllTrackArea(trackCodes, trainsetIds) {
      const {
        code,
        data: { trackAreas },
      } = await getAllTrackArea();

      if (code == 1) {
        this.allTrackAreaList = trackAreas;
        await this.getAllTrainsetPostIon(trackCodes, trainsetIds);
      }
    },
    // 获取根据运用所和股道集合获取所有车组位置信息
    async getAllTrainsetPostIon(trackCodes, trainsetIds) {
      if (!trackCodes) {
        trackCodes = [];
      }
      if (!trainsetIds) {
        trainsetIds = [];
      }
      let trackCodesJsonStr = trackCodes.join(',');

      let trainsetNameStr = trainsetIds.map((v) => this.trainsetIdNameMap[v]).join(',');

      const { code, data } = await getPromise(
        'getAllTrainSetPostIon_' + trackCodesJsonStr + '_' + trainsetNameStr,
        () =>
          getTrainsetPostIon({
            unitCode: this.unitCode,
            trackCodesJsonStr: trackCodesJsonStr,
            trainsetNameStr: trainsetNameStr,
          })
      );
      if (code == 1) {
        if (trainsetIds.length == 0) {
          this.allTrainsetList = data;
        } else {
          updateData(
            this.allTrainsetList,
            data,
            (v) => (this.allTrainsetList = v),
            trainsetIds,
            (v) => v.trainsetId
          );
        }
        let queryTrainsetIds =
          trainsetIds.length == 0 ? this.allTrainsetList.map((item) => item.trainsetId) : trainsetIds;
        this.getRepairTask(queryTrainsetIds);
        this.getAllOverRunRecordList(queryTrainsetIds);
      }
    },
    getWarningConfig() {
      const warningConfig = localStorage.getItem('warningConfig');
      if (warningConfig) {
        this.warningConfig = JSON.parse(warningConfig);
      } else {
        this.allTrainsetList.forEach((train) => {
          this.$set(this.warningConfig, train.trainsetId, true);
        });
      }
    },
    setWarningConfig() {
      const warningConfig = JSON.stringify(this.warningConfig);
      localStorage.setItem('warningConfig', warningConfig);
    },
    getAllOverRunRecordList(trainsetIds) {
      let trainsetNameList = trainsetIds.map((v) => this.trainsetIdNameMap[v]);
      axios.all([getLyOverRunRecordList(trainsetNameList), getSjOverRunRecordList(trainsetNameList)]).then(
        axios.spread((res1, res2) => {
          if (res1.code == 1) {
            if (trainsetIds.length == 0) {
              this.LYList = res1.data;
            } else {
              updateData(
                this.LYList,
                res1.data,
                (v) => (this.LYList = v),
                trainsetIds,
                (v) => v.trainsetId
              );
            }
          }
          if (res2.code == 1) {
            if (trainsetIds.length == 0) {
              this.SJList = res2.data;
            } else {
              updateData(
                this.SJList,
                res2.data,
                (v) => (this.SJList = v),
                trainsetIds,
                (v) => v.trainsetId
              );
            }
          }
        })
      );
    },
    // 获取任务
    async getRepairTask(trainsetIds) {
      let trainsetIdStr = trainsetIds.join(',');
      let fnKey = 'getRepairTask';
      let cacheData = dataCacheUtil.getCacheData(fnKey, trainsetIdStr);
      let showForceEndFlowRun = null
      if (cacheData) {
        return;
      }
      // 如果本地有配置就用本地的配置，如果没有就去查
      const keyWorkConfigStr = localStorage.getItem('keyWorkConfig');
      if (keyWorkConfigStr) {
        let localConfig = JSON.parse(keyWorkConfigStr);
        showForceEndFlowRun = localConfig.queryPastFlow
      } else {
        let params = {
          paramName: 'FlowRunForceEnd',
        };
        let res = await getConfig(params);
  
        if (res.paramValue == 1) {
          showForceEndFlowRun = false;
        } else {
          showForceEndFlowRun = true;
        }
      }
      const { data } = await getPromise(fnKey + '_' + trainsetIdStr, () =>
        getRepairTask({
          dayPlanId: this.dayPlanId,
          trainsetIdStr,
          unitCode: this.unitCode,
          showDayRepairTask: this.baseConfigDialog.formData.ShowLastRepairTask,
          showForceEndFlowRun
        })
      );
      dataCacheUtil.setCacheData(fnKey, trainsetIdStr, data);
      if (trainsetIds.length == 0) {
        this.trainsetIdTaskList = data;
      } else {
        updateData(
          this.trainsetIdTaskList,
          data,
          (v) => (this.trainsetIdTaskList = v),
          trainsetIds,
          (v) => v.trainsetId
        );
      }
    },

    // 获取股道供断电信息
    async getTrackPowerInfo() {
      let fnKey = 'getTrackPowerInfo';
      let cacheData = dataCacheUtil.getCacheData(fnKey);
      if (cacheData) {
        return;
      }
      const { data } = await getPromise(fnKey + '_trackCode', () =>
        getTrackPowerInfo({
          unitCodeList: this.unitCode,
          trackCodeList: '',
        })
      );

      dataCacheUtil.setCacheData(fnKey, 'trackCode', data);

      this.trackPowerList = data;
    },
    // 改变股道供断电信息
    async trackPowerChange(trackPowerInfo) {
      if (!this.transferTrainJurisdiction) return;
      if (this.globalConfigObj.TrackPowerManualInput != '1') {
        return this.$message.error('暂时无法人工设置');
      }
      await setTrackPowerInfo(trackPowerInfo);
      this.getTrackPowerInfo();
    },
    rightMenuClick(type, rightTargetInfo, e) {
      // 重置
      Object.keys(this.inOutTrainGroup.inOutActive).forEach((item) => {
        this.$set(this.inOutTrainGroup.inOutActive, item, false);
      });
      this.focusId = '';
      if (this.globalConfigObj.ManualInput != '1') {
        return this.$message.error('暂时无法人工设置');
      }

      this.rightMenuType = type;
      this.rightTargetInfo = rightTargetInfo;
      // 一级菜单
      this.getMenuList();

      this.axis = {
        x: e.x,
        y: e.y,
      };
      this.rightMenuShow = true;
    },
    emptyTrackClick({ targetTrackPostionInfo, e }) {
      this.rightMenuClick('emptyTrack', targetTrackPostionInfo, e);
    },
    // 右键选中
    rightMenuSelect(level) {
      let action = this.action[level];
      if (this.rightMenuType == 'inOut') {
        let targetTrackPostionInfo = this.getTargetTrackPostionInfo(action);
        this.inOutTrainMoveTo(this.rightTargetInfo, targetTrackPostionInfo);
      } else if (this.rightMenuType == 'trainBody') {
        if (level == 0) {
          switch (action) {
            case 'connect':
              this.setTrainsetState('1');
              break;
            case 'disconnect':
              this.setTrainsetState('0');
              break;
            case 'delete':
              this.deleteTrainsetPostIon(this.rightTargetInfo);
              break;
            case 'close':
              this.closeTrainsetWarning(this.rightTargetInfo);
              break;
          }
        } else if (level == 2) {
          let targetTrackPostionInfo = this.getTargetTrackPostionInfo(action);
          this.updTrackCode(this.rightTargetInfo, targetTrackPostionInfo);
        }
      } else {
        this.insertTrainsetPostIon({ trainsetId: action }, this.rightTargetInfo);
      }
      this.rightMenuShow = false;
    },
    // 获取目标股道信息
    getTargetTrackPostionInfo(trackPosition) {
      return {
        trackCode: Number(trackPosition.split('-')[0]),
        trackPositionCode: Number(trackPosition.split('-')[1]),
      };
    },
    // 获取转线线区menuList
    getTrackAreaMenu() {
      const menu = this.allTrackAreaSortList.map((trackArea) => {
        // if (
        //   this.trackAreaCodeTrackCodeMap[trackArea.trackAreaCode].some(
        //     (track) => this.baseConfigDialog.trackShowConfig[track] !== false
        //   )
        // ) {
        return {
          label: trackArea.trackAreaName,
          value: trackArea.trackAreaCode,
          lastLevel: false,
        };
        // }
      });
      return menu.filter((item) => item);
    },
    // 获取转线股道列位menuList
    getTrackPositionMenu(trackArea) {
      const trackList = this.trackList.filter((item) => item.trackAreaCode == trackArea);
      const positionList = trackList.map((item) => item.lstTrackPositionInfo).flat();

      const menu = positionList.map((position) => {
        if (this.positionCodeTrainsetNameMap[position.trackPositionCode]) {
          return {
            label: this.trackCodeNameMap[position.trackCode] + '-' + position.trackPostionName,
            value: position.trackCode + '-' + position.trackPositionCode,
            lastLevel: true,
            disabled: true,
            tooltipContent: this.positionCodeTrainsetNameMap[position.trackPositionCode],
          };
        } else {
          return {
            label: this.trackCodeNameMap[position.trackCode] + '-' + position.trackPostionName,
            value: position.trackCode + '-' + position.trackPositionCode,
            lastLevel: true,
            disabled: false,
            tooltipContent: '',
          };
        }
      });

      return menu;
    },
    // 获取调入车组列表menuList
    getTrainMenu() {
      const menu = this.inOutTrainGroup.trainGroupNameList.map((train) => {
        if (this.trainsetIdPositionCodeMap[train.trainsetid]) {
          const trackName = this.trackCodeNameMap[this.trainsetIdTrackCodeMap[train.trainsetid]];

          return {
            label: train.trainsetname,
            value: train.trainsetid,
            lastLevel: true,
            disabled: true,
            tooltipContent: this.trainsetIdPositionCodeMap[train.trainsetid]
              .map((positionCode) => `${trackName}-${this.positionCodeNameMap[positionCode]}`)
              .toString(),
          };
        } else {
          return {
            label: train.trainsetname,
            value: train.trainsetid,
            lastLevel: true,
            disabled: false,
            tooltipContent: '',
          };
        }
      });
      return menu;
    },
    // 获取一级菜单
    getMenuList() {
      if (this.rightMenuType == 'inOut') {
        let menu = [];
        if (this.transferTrainJurisdiction) {
          menu = this.getTrackAreaMenu();
        }
        this.contextMenuMap = {
          title: '转线线区',
          level: 0,
          menu,
          children: {},
          filterable: true,
        };
      } else if (this.rightMenuType == 'trainBody') {
        let menu_0;
        if (this.rightTargetInfo.isConnect == '0') {
          menu_0 = [
            {
              label: '转线',
              value: 'updateTrain',
              lastLevel: false,
            },
            {
              label: '重联',
              value: 'connect',
              lastLevel: true,
            },
            {
              label: '删除',
              value: 'delete',
              lastLevel: true,
            },
            {
              label: '关闭调车预警',
              value: 'close',
              lastLevel: true,
            },
          ];

          if (!this.transferTrainJurisdiction) {
            menu_0 = menu_0.filter((item) => {
              return item.value != 'updateTrain' && item.value != 'connect';
            });
          }
        } else {
          menu_0 = [
            {
              label: '转线',
              value: 'updateTrain',
              lastLevel: false,
            },
            {
              label: '解编',
              value: 'disconnect',
              lastLevel: true,
            },
            {
              label: '删除',
              value: 'delete',
              lastLevel: true,
            },
            {
              label: '关闭调车预警',
              value: 'close',
              lastLevel: true,
            },
          ];

          if (!this.transferTrainJurisdiction) {
            menu_0 = menu_0.filter((item) => {
              return item.value != 'updateTrain' && item.value != 'disconnect';
            });
          }
        }
        // 关闭调车预警(只有在调车预警情况下菜单项才显示)
        if (this.baseFormConfig.IsMoveTrainPlanWarning) {
          menu_0.pop();
        }

        this.contextMenuMap = {
          title: '',
          level: 0,
          menu: menu_0,
          children: {},
        };
      } else {
        // 空股道
        let menu_0 = [];
        if (this.transferTrainJurisdiction) {
          menu_0 = [
            {
              label: '调入车组',
              value: 'updateTrack',
              lastLevel: false,
            },
            // {
            //   label: '关闭预警',
            //   value: 'close',
            //   lastLevel: true,
            // },
          ];
        }
        this.contextMenuMap = {
          title: '',
          level: 0,
          menu: menu_0,
          children: {},
        };
      }
    },
    // 获取子菜单
    childrenChange({ action, level }) {
      // 获得下级菜单
      if (this.rightMenuType == 'inOut') {
        this.contextMenuMap.children = {
          title: '转线股道列位',
          level: 1,
          menu: this.getTrackPositionMenu(action[level]),
        };
      } else if (this.rightMenuType == 'trainBody') {
        if (level == 0) {
          this.contextMenuMap.children = {
            title: '转线线区',
            level: 1,
            menu: this.getTrackAreaMenu(),
            children: {},
            filterable: true,
          };
        } else if (level == 1) {
          this.contextMenuMap.children.children = {
            title: '转线股道列位',
            level: 2,
            menu: this.getTrackPositionMenu(action[level]),
          };
        }
      } else {
        // 空股道
        this.contextMenuMap.children = {
          title: '调入车组列表',
          level: 1,
          filterable: true,
          menu: this.getTrainMenu(),
        };
      }
    },
    // 出入所车组右键选中或拖拽调用
    inOutTrainMoveTo(rightTargetInfo, targetTrackPostionInfo) {
      // 判断是否有调车权限
      if (!this.transferTrainJurisdiction) return;
      // 判断该车是否已在股道
      if (this.trainsetIdNameMap[rightTargetInfo.trainsetid]) {
        return this.$message.error(`该车组${this.trainsetIdNameMap[rightTargetInfo.trainsetid]}已在股道上`);
      }

      if (this.dragTrain) {
        this.updTrackCode(rightTargetInfo, targetTrackPostionInfo);
        this.dragTrain = false;
      } else {
        this.insertTrainsetPostIon(rightTargetInfo, targetTrackPostionInfo);
      }
    },
    async getTrainBaseInfo(rightTargetInfo, targetTrackPostionInfo) {
      const trainsetId = rightTargetInfo.trainsetid || rightTargetInfo.trainsetId;
      const trainsetName = this.inOutTrainsetIdNameMap[trainsetId];
      const isLong = await this.getTrainsetDetialInfo(trainsetId);
      const headDirection = rightTargetInfo.headDirection || this.baseFormConfig.HeadDirection == '0' ? '01' : '00'; //根据基础配置
      const tailDirection = headDirection == '00' ? '01' : '00';
      const headDirectionPlaCode = targetTrackPostionInfo.trackPositionCode;
      const headDirectionPla = this.positionCodeNameMap[targetTrackPostionInfo.trackPositionCode];
      const trackCode = targetTrackPostionInfo.trackCode;
      const trackName = this.trackCodeNameMap[targetTrackPostionInfo.trackCode];
      return {
        trainsetId,
        trainsetName,
        isLong,
        headDirection,
        tailDirection,
        headDirectionPlaCode,
        headDirectionPla,
        trackCode,
        trackName,
      };
    },
    // 对短编车的验证
    validateshortTrain(trackName, headDirectionPlaCode, headDirectionPla) {
      let tailDirectionPlaCode = '';
      let tailDirectionPla = '';
      // 判断目标车头列位是否有位置即可
      if (this.positionCodeTrainsetNameMap[headDirectionPlaCode]) {
        // 有车提示
        this.$message.error(
          `股道列位 ${trackName}-${headDirectionPla} 已经存在车组${this.positionCodeTrainsetNameMap[headDirectionPlaCode]}`
        );
        return false;
      } else {
        tailDirectionPlaCode = headDirectionPlaCode;
        tailDirectionPla = headDirectionPla;
      }
      return {
        tailDirectionPlaCode,
        tailDirectionPla,
      };
    },
    // 对长编车的验证
    validateLongTrain(trackCode, headDirectionPlaCode) {
      if (this.trainsetInTrackMap[trackCode] || this.trackCodepositionCodesSortMap[trackCode].length < 2) {
        this.$message.error('该股道已有车组，无法调入当前车组。');
        return false;
      }
      const tailDirectionPlaCode = this.trackCodepositionCodesSortMap[trackCode].find(
        (item) => item != headDirectionPlaCode
      );
      const tailDirectionPla = this.positionCodeNameMap[tailDirectionPlaCode];
      return {
        tailDirectionPlaCode,
        tailDirectionPla,
      };
    },

    // 增加车组
    async insertTrainsetPostIon(rightTargetInfo, targetTrackPostionInfo) {
      const {
        trainsetId,
        trainsetName,
        isLong,
        headDirection,
        tailDirection,
        headDirectionPlaCode,
        headDirectionPla,
        trackCode,
        trackName,
      } = await this.getTrainBaseInfo(rightTargetInfo, targetTrackPostionInfo);

      // 验证
      let res = false;
      if (isLong == '0') {
        // 短车
        res = this.validateshortTrain(trackName, headDirectionPlaCode, headDirectionPla);
      } else {
        // 长车
        res = this.validateLongTrain(trackCode, headDirectionPlaCode);
      }
      if (!res) {
        return;
      }
      {
        const { tailDirectionPlaCode, tailDirectionPla } = res;
        const insertTrainInfo = {
          headDirection,
          headDirectionPla,
          headDirectionPlaCode,
          inTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
          isConnect: '0',
          isLong,
          tailDirection,
          tailDirectionPla,
          tailDirectionPlaCode,
          trackCode,
          trackName,
          trainsetId,
          trainsetName,
          unitCode: this.unitCode,
        };

        const { code, data } = await setTrainsetPostIon(insertTrainInfo);
        if (code == 1) {
          if (data) {
            this.$message.error(data);
          } else {
            this.$message.success(`车组${trainsetName}已进入股道${trackName}`);
          }
        }
        // 刷新
        this.getAllTrackArea(null, [trainsetId]);
      }
    },
    // 转线方法
    async updTrackCode(rightTargetInfo, targetTrackPostionInfo) {
      try {
        const {
          trainsetId,
          trainsetName,
          isLong,
          headDirection,
          tailDirection,
          headDirectionPlaCode,
          headDirectionPla,
          trackCode,
          trackName,
        } = await this.getTrainBaseInfo(rightTargetInfo, targetTrackPostionInfo);

        // 验证
        let insertTrainInfoList = [];
        if (rightTargetInfo.isConnect == '1') {
          // 只考虑短车
          const trackTrainIds = this.trainsetInTrackMap[rightTargetInfo.trackCode];

          // 任务校验
          let taskIdArr = [];
          trackTrainIds.forEach((trainsetId) => {
            if (
              this.trainsetIdTaskMap[trainsetId] &&
              (this.trainsetIdTaskMap[trainsetId].repairOne || this.trainsetIdTaskMap[trainsetId].repairTwo)
            ) {
              taskIdArr.push(trainsetId);
            }
          });
          if (taskIdArr.length > 0) {
            await this.$confirm(`当前车组 ${taskIdArr.toString()} 有未完成的作业任务，是否进行转线操作？`, '提示', {
              customClass: 'train-confirm-box',
            });
          }

          // 判断目标股道有车吗
          if (this.trainsetInTrackMap[trackCode]) {
            return this.$message.error('该股道已有车组，无法调入当前车组。');
          }

          const targetTrackPositionCodes = this.trackCodepositionCodesSortMap[trackCode];

          for (let i = 0; i < trackTrainIds.length; i++) {
            if (trackTrainIds[i] == trainsetId) {
              // 当前右键车
              insertTrainInfoList.push({
                trainsetId: trainsetId,
                trainsetPostionId: this.trainsetIdInfoMap[trainsetId].trainsetPostionId,
                headDirectionPla: headDirectionPla,
                headDirectionPlaCode: headDirectionPlaCode,
                tailDirectionPla: headDirectionPla,
                tailDirectionPlaCode: headDirectionPlaCode,
                isLong: this.trainsetIdInfoMap[trainsetId].isLong,
                isConnect: this.trainsetIdInfoMap[trainsetId].isConnect,
                trackCode,
                trackName,
              });
            } else {
              // 重练车
              const positionCode = targetTrackPositionCodes.find(
                (positionCode) => positionCode != headDirectionPlaCode
              );
              const pisitionPla = this.positionCodeNameMap[positionCode];

              insertTrainInfoList.push({
                trainsetId: trackTrainIds[i],
                trainsetPostionId: this.trainsetIdInfoMap[trackTrainIds[i]].trainsetPostionId,
                headDirectionPla: pisitionPla,
                headDirectionPlaCode: positionCode,
                tailDirectionPla: pisitionPla,
                tailDirectionPlaCode: positionCode,
                isLong: this.trainsetIdInfoMap[trainsetId].isLong,
                isConnect: this.trainsetIdInfoMap[trainsetId].isConnect,
                trackCode,
                trackName,
              });
            }
          }
        } else {
          // 任务校验

          if (
            this.trainsetIdTaskMap[trainsetId] &&
            (this.trainsetIdTaskMap[trainsetId].repairOne || this.trainsetIdTaskMap[trainsetId].repairTwo)
          ) {
            await this.$confirm(`当前车组 ${trainsetName} 有未完成的作业任务，是否进行转线操作？`, '提示', {
              customClass: 'train-confirm-box',
            });
          }
          let res = false;
          if (isLong == '0') {
            // 短车
            res = this.validateshortTrain(trackName, headDirectionPlaCode, headDirectionPla);
          } else {
            // 长车
            res = this.validateLongTrain(trackCode, headDirectionPlaCode);
          }
          if (!res) {
            return;
          }
          {
            const { tailDirectionPlaCode, tailDirectionPla } = res;
            insertTrainInfoList.push({
              trainsetId: trainsetId,
              trainsetPostionId: rightTargetInfo.trainsetPostionId,
              headDirectionPla,
              headDirectionPlaCode,
              tailDirectionPla,
              tailDirectionPlaCode,
              isLong: this.trainsetIdInfoMap[trainsetId].isLong,
              isConnect: this.trainsetIdInfoMap[trainsetId].isConnect,
              trackCode,
              trackName,
            });
          }
        }

        const { code, data } = await updTrackCode(insertTrainInfoList);
        if (code == 1) {
          if (data) {
            return this.$message.error(data);
          }
          let trainsetNameStr = insertTrainInfoList.map((item) => this.trainsetIdNameMap[item.trainsetId]).toString();
          this.$message.success(`车组${trainsetNameStr}已进入股道${trackName}`);
        }
        // 刷新
        this.getAllTrackArea(null, this.trainsetInTrackMap[rightTargetInfo.trackCode]);
      } catch (error) {
        console.log(error);
      }
    },
    // 右键删除车
    async deleteTrainsetPostIon(rightTargetInfo) {
      if (rightTargetInfo.isConnect == '1') {
        this.$message.error(`请先解编车组${rightTargetInfo.trainsetName}`);
        return;
      }
      let loading = this.$loading({
        text: '处理中…',
        background: 'rgba(20, 70, 121, 0.33)'
      })
      let copyTrainInfo = copyData(rightTargetInfo);
      if (!copyTrainInfo.outTime) {
        copyTrainInfo.outTime = dayjs().format('YYYY-MM-DD HH:mm:ss');
      }
      const { code, data } = await setTrainsetPostIon(copyTrainInfo);
      if (code == 1) {
        if (data) {
          this.$message.error(data);
        } else {
          this.$message.success(`车组${rightTargetInfo.trainsetName}已离开股道${rightTargetInfo.trackName}`);
        }
      }
      // 刷新
      await this.getAllTrackArea(null, [rightTargetInfo.trainsetId]);
      loading.close()
    },
    // 重联解编
    async setTrainsetState(status) {
      const trackTrainIds = this.trainsetInTrackMap[this.rightTargetInfo.trackCode];

      // 验证
      let canReconnect = true;
      if (status == '1') {
        // 重联
        if (trackTrainIds.length === 1) {
          canReconnect = false;
          return this.$message.error('一辆车无法进行重联操作');
        } else {
          const nowTrainsetId = this.rightTargetInfo.trainsetId;
          const nowTrainsetType = this.trainsetIdTypeMap[nowTrainsetId];
          const nowTrainsetName = this.rightTargetInfo.trainsetName;
          const targetTrainIds = trackTrainIds.filter((id) => id != nowTrainsetId);
          const targetTrainTypes = targetTrainIds.map((id) => this.trainsetIdTypeMap[id]);
          const targetTrainName = targetTrainIds.map((id) => this.trainsetIdNameMap[id]).toString();
          this.reconnectConfigTableData.forEach((item) => {
            if (item.trainType === nowTrainsetType) {
              if (item.connectTrainType === '') {
                canReconnect = false;
                return this.$message.error(
                  `当前车组${nowTrainsetName}不能和车组${targetTrainName}进行重联，车型不匹配，请检查相关配置！`
                );
              } else {
                const connectTrainTypes = item.connectTrainType.split(',');
                for (let i = 0; i < targetTrainTypes; i++) {
                  const type = targetTrainTypes[i];
                  if (!connectTrainTypes.includes(type)) {
                    canReconnect = false;
                    return this.$message.error(
                      `当前车组${nowReconnectTrainsetName}不能和车组${trainsetNameArr[0]}进行重联，车型不匹配，请检查相关配置！`
                    );
                  }
                }
              }
            }
          });
        }
      }
      if (!canReconnect) return;
      const trainsetPostIonIds = trackTrainIds.map((trainsetId) => {
        return this.trainsetIdInfoMap[trainsetId].trainsetPostionId;
      });
      const { code } = await setTrainsetState({
        status,
        trainsetPostIonIds,
      });
      if (code == 1) {
        if (status == '1') {
          this.$message.success('重联成功');
        } else {
          this.$message.success('解编成功');
        }
      }
      // 刷新
      this.getAllTrackArea(null, trackTrainIds);
    },
    // 车头方向改变
    async trainHeadChange(trainsetInfo) {
      if (!this.transferTrainJurisdiction) return;
      let trainInfo = copyData(trainsetInfo);
      trainInfo.headDirection = trainInfo.headDirection == '00' ? '01' : '00';
      trainInfo.tailDirection = trainInfo.tailDirection == '00' ? '01' : '00';
      delete trainInfo.outTime;
      delete trainInfo.trackCode;
      const { code, data } = await setTrainsetPostIon(trainInfo);
      if (code == 1) {
        if (data) {
          this.$message.error(data);
        }
      }
      // 刷新
      this.getAllTrackArea(null, [trainsetInfo.trainsetId]);
    },
    // 交换车位
    async trainPostionExchange(trackCode) {
      if (!this.transferTrainJurisdiction) return;
      const trainsetIds = this.connectTrainInTrackMap[trackCode];
      const trainsetInfo = this.trainsetIdInfoMap[trainsetIds[0]];
      const nextTrainsetInfo = this.trainsetIdInfoMap[trainsetIds[1]];
      let postArr = [
        {
          trackCode,
          trainsetId: trainsetInfo.trainsetId,
          trainsetName: trainsetInfo.trainsetName,
          isConnect: trainsetInfo.isConnect,
          isLong: trainsetInfo.isLong,
          trainsetPostionId: trainsetInfo.trainsetPostionId,
          headDirectionPla: nextTrainsetInfo.headDirectionPla,
          headDirectionPlaCode: nextTrainsetInfo.headDirectionPlaCode,
          tailDirectionPla: nextTrainsetInfo.tailDirectionPla,
          tailDirectionPlaCode: nextTrainsetInfo.tailDirectionPlaCode,
        },
        {
          trackCode, 
          trainsetId: nextTrainsetInfo.trainsetId,
          trainsetName: nextTrainsetInfo.trainsetName,
          isConnect: nextTrainsetInfo.isConnect,
          isLong: nextTrainsetInfo.isLong,
          trainsetPostionId: nextTrainsetInfo.trainsetPostionId,
          headDirectionPla: trainsetInfo.headDirectionPla,
          headDirectionPlaCode: trainsetInfo.headDirectionPlaCode,
          tailDirectionPla: trainsetInfo.tailDirectionPla,
          tailDirectionPlaCode: trainsetInfo.tailDirectionPlaCode,
        },
      ];
      const { code, data, msg } = await updTrackCode(postArr);
      if (code == 1) {
        if (data) {
          this.$message.success(data);
        } else {
          this.$message.success('车组列位已交换');
        }
      } else {
        this.$message.error(msg);
      }
      // 刷新
      this.getAllTrackArea(null, trainsetIds);
    },
    // 请求长编还是短编
    async getTrainsetDetialInfo(trainsetId) {
      const { data } = await getTrainsetDetialInfo({ trainsetId });
      return data;
    },
    // 关闭调车预警
    closeTrainsetWarning(trainsetInfo) {
      const { trainsetId } = trainsetInfo;
      this.$set(this.warningConfig, trainsetId, false);
      this.setWarningConfig();
    },
    // 拖拽
    dragstart(rightTargetInfo, dragTrain) {
      this.rightTargetInfo = rightTargetInfo;
      if (dragTrain) {
        this.dragTrain = dragTrain;
      }
    },
    drop({ trackCode, trackPositionCode }) {
      let targetTrackPostionInfo = { trackCode, trackPositionCode };
      if (this.globalConfigObj.ManualInput != '1') {
        return this.$message.error('暂时无法人工设置');
      }

      if(!this.rightTargetInfo.trainsetId && !this.rightTargetInfo.trainsetid) return
      this.inOutTrainMoveTo(this.rightTargetInfo, targetTrackPostionInfo);
    },

    // 双击出入所
    dblclickLi(rightTargetInfo) {
      if (this.trainsetIdTrackCodeMap[rightTargetInfo.trainsetid]) {
        // 重置
        Object.keys(this.inOutTrainGroup.inOutActive).forEach((item) => {
          this.$set(this.inOutTrainGroup.inOutActive, item, false);
        });
        // 选中
        this.$set(this.inOutTrainGroup.inOutActive, rightTargetInfo.trainsetid, true);
        // 定位
        const trackCode = this.trainsetIdTrackCodeMap[rightTargetInfo.trainsetid];
        let pageNum = 1;
        while (!this.groupAllTrackMap[pageNum].find((item) => item.trackCode == trackCode)) {
          pageNum++;
        }
        this.trackPageNum = pageNum;

        this.focusId = rightTargetInfo.trainsetid;
      }
    },
    setTrainWrapperActive({ key, active }) {
      this.$set(this.trainWrapperActive, key, active);
    },
    resetTrainWrapperActive() {
      this.trainWrapperActive = {};
    },
    trainContextmenu({ trainsetInfo, e }) {
      this.rightMenuClick('trainBody', trainsetInfo, e);
    },
    rootLeftChange(x) {
      this.$set(this.axis, 'x', x);
    },
    rootTopChange(y) {
      this.$set(this.axis, 'y', this.axis.y - y);
    },
    setActive({ key, value }) {
      this.$set(this.active, key, value);
    },
    resetActive() {
      this.active = {};
    },
    setChildVisible({ key, value }) {
      this.$set(this.childVisible, key, value);
    },
    resetChildVisible(level) {
      if (level == 0) {
        this.childVisible = { 0: false, 1: false, 2: false };
      } else {
        this.childVisible = { 0: true, 1: false, 2: false };
      }
    },
    setAction({ level, value }) {
      this.action.splice(level, this.action.length - level, value);
    },

    showIframe(type, trainsetId) {
      if (type == 'one') {
        this.monitorFlag = false;
        this.iframeSrc = `${iframeBaseUrlMap[type]}?dayPlanId=${this.dayPlanId}&unitCode=${this.unitCode}&trainsetName=${this.trainsetIdNameMap[trainsetId]}&status=1&processShow=false`;
      } else if (type == 'two') {
        this.monitorFlag = false;

        this.iframeSrc = `${iframeBaseUrlMap[type]}?dayPlanId=${this.dayPlanId}&unitCode=${this.unitCode}&trainsetName=${this.trainsetIdNameMap[trainsetId]}&status=2&processShow=false`;
      } else {
        this.monitorFlag = true;
        this.iframeSrc = `${iframeBaseUrlMap[type]}?trainsetId=${trainsetId}`;
      }
      this.iframeVisible = true;
    },
    async getPostRoleList() {
      const { code, data } = await getPostRoleList();
      if (code == 1) {
        this.roleList = data;
      }
    },
    cellStyle({ row, column, rowIndex, columnIndex }) {
      if (columnIndex == 0) return '';
      this.nodeColumns[columnIndex - 1].prop;
      if (row[this.nodeColumns[columnIndex - 1].prop]) {
        return 'background:#82c343;';
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
    cellClick(row, property) {
      if (!row.roleWorkerPicUrls[property]) return;
      this.workerPicMap = row.roleWorkerPicUrls[property];
      this.workerDetail = row.roleWorkerDetails[property];
      this.nodeDealDetailDialog.visible = true;
    },
    lookupNodeDealInfoBtn(nodeList, flow) {

        // 用flowRunId和flowId
        // let flowId = flow.flowId
        // let flowRunId = flow.flowRunId
        // if(!flowId && !flowRunId) {
        //   this.nodeList = []
        // }
        // if(flowId && !flowRunId) {
        //   this.nodeList = nodeList.filter((node) => {
        //     if(flowId == node.flowId) {
        //       return node
        //     }
        //   })
        // }
        // if(flowId && flowRunId) {
        //   this.nodeList = nodeList.filter((node) => {
        //     if(flowId == node.flowId && flowRunId == node.id) {
        //       return node
        //     }
        //   })
        // }
      if(nodeList && flow) {
        // 用flowId
        let flowId = flow.flowId
        if(!flowId) {
          this.nodeList = []
        }
        if(flowId) {
          this.nodeList = nodeList.filter((node) => {
            if(flowId == node.flowId) {
              return node
            }
          })
        }
      } else {
        this.nodeList = nodeList ? nodeList : [];
      }
      this.lookupNodeDealInfoDialog.visible = true;
    },
    previewPicture(file, fileList) {
      this.imgUrl = file.url;

      this.prePics = fileList.map((item) => item.url);

      this.$refs.myImg.showViewer = true;
    },
  },
});
