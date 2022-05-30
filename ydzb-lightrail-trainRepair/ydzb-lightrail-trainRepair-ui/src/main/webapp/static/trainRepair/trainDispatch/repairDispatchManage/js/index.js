const copyData = (data) => JSON.parse(JSON.stringify(data));
// api：获取班次联动数据
const getBcData = function(config) {
  return {
    method: 'get',
    url: '/apiTrainRepair/xzyMTaskallotpacket/getQuery',
    ...config
  }
}
window.app = new Vue({
  el: '#app',
  data() {
    return {
      unitCode: '',
      unitName: '',
      tableDataFilter: [],
      saveFaultTaskData: [],
      saveStatus: '未派工',
      contentStyle: {
        height: '100%',
        'overflow-y': 'auto',
      },
      loading: false,
      dialogVisibleTask: false,
      dialogVisiblePeople: false,
      search: {
        bcDate: '', //班次日期
        bcDay: '', //班次的黑白班
        bcDayList: [
          { id: '00', name: '白班' },
          { id: '01', name: '夜班' },
        ],
        bzData: '', //班组
        bzDataList: [],
        trainsetTypeCode: '', //车型
        trainTypeList: [],
        trainsetId: '', //车组
        trainGroupList: [],
        workTypeCode: '', //作业类型
        worktypeList: [],
        workTaskName: '', //作业任务
        worktaskList: [],
        trainNo: '', //辆序
        trainNoList: [],
        allotStateCode: [], //派工状态
        workStatusList: [],
        showWay: '',
        showWayList: [
          {
            id: '1',
            value: '1',
            label: '作业包',
          },
          {
            id: '2',
            value: '2',
            label: '专业分工',
          },
        ],
      },

      trainsetAll: [],

      mainTableData: {
        data: [],
        scrollHeight: 'calc(100vh - 215px)',
        currentPage: 1,
        pageSize: 100,
        total: 1,
      },
      prevTableData: [],
      faultTask: {
        bureauName: '',
        workTimes: '',
        workTeam: '',
        tableData: {
          data: [],
          currentPage: 1,
          pageSize: 5,
        },
      },
      chooseWorkers: {
        bureauName: '',
        workTeam: '',
        quickQuery: '',
      },
      showAddGroup: false,
      tmDisplay: false,
      peopleDisplay: false,
      man2GroupDisplay: false,
      isPartTimeJob: false,
      choosePostionStatus: false, //选择岗位状态
      treeData: [],
      defaultProps: {
        children: 'children',
        label: 'label',
      },
      multipleSelection_main: [],
      multipleSelection_sub1: [],
      sub1Select: [],
      multipleSelection_sub2: [],
      user: {
        unitCode: '',
        unitName: '',
        deptCode: '',
        deptName: '',
      },
      postObj: {},
      postList: [], //岗位信息
      treeKey: 0,
      man2GroupList: [],
      changeGroupType: '',
      nowTreeRows: {},
      nowTreeNode: '',
      newTreeId: 1000,
      groupName: '',
      dialogVisibleGroup: false,
      vals: [],
      showMulti: false, //人员是否兼职
      notWorkNum: {
        //未派工
        trainset: 0, //未派工车组数
        task: 0, //未派工任务数
      },
      faultTaskSelect: [],
      checkPeople: [], //人员分配选中的人员
      workerList: [], //人员集合
      saveBcDate: '',
      saveBcDay: '',
      savebzData: '',
      pCheckNodes: [],
      faultNewArr: [],
      sortArr: [],
      c_num: 0,
      changeType: '',
      oldbcDate: '',
      oldbcDay: '',
      oldbzData: '',
      oldShowWay: '',
      // 复核任务
      reCheckedTaskdialogVisible: false,
      reCheckedTaskTableData: [],
      reCheckedTaskSelectArr: [], //已勾选的
      reCheckedTaskTablePageNum: 1,
      reCheckedTaskTablePageSize: 5,
      reCheckedTaskTableTotal: 0,
      wholeConfigObj: {},

      // 人员选择
      chooseWorkerVisible: false,
      quickQueryWorkersText: '',
      workerTeamDataPrev: [],
      workerUserDataPrev: [],
      workerTeamData: [],
      workerUserData: [],
      workersTreeKey: 1,
      workTeamMenuList: [
        {
          icon: 'el-icon-circle-plus-outline',
          label: '新建小组',
          value: 'addGroup',
        },
      ],
      teamMenuList: [
        {
          icon: 'el-icon-edit',
          label: '修改小组名称',
          value: 'editGroup',
        },
        {
          icon: 'el-icon-delete',
          label: '清空小组人员',
          value: 'clearGroup',
        },
        {
          icon: 'el-icon-remove-outline',
          label: '删除小组',
          value: 'deleteGroup',
        },
        {
          icon: 'el-icon-sort-up',
          label: '上移小组',
          value: 'upGroup',
        },
        {
          icon: 'el-icon-sort-down',
          label: '下移小组',
          value: 'downGroup',
        },
      ],
      axis: {},
      contextmenuWrapStatus: false,
      contextMenuList: [],
      contextMenuText: [],
      workersTreeCheckedNodes: [],
      groupDialogVisible: false,
      nowTreeRowId: 0,
      emptyArrr: [],
      baseImgPath: ctxPath + '/static/trainRepair/trainDispatch/repairDispatchManage/img/',
      originTrainsetList: [],
      selectPost: [],

      reCheck: [],

      // 故障车组过滤
      faultFilterTrainsetId: '',

      // 刷新故障和复核数据定时器Timer
      refreshTimer: null,

      recheckFilterTrainsetId: '',
      recheckFilterItem: '',
    };
  },
  async created() {
    await this.getUnitCode();
    await this.getUnitName();
    await this.getUser(); //获取登录人信息
    await this.getTaskAllotStyle(this.unitCode, this.search.bzData);
    await this.getConfigList();
    let { data } = await getTrainsetList();
    this.trainsetAll = data;
    this.getHeight();
    window.addEventListener('resize', this.getHeight);
    await this.getDay(); //获取班次
    await this.initTable();
  },
  mounted() {},
  methods: {
    async getFault() {
      let trainsetIds = [];
      this.search.trainGroupList.forEach((item, index) => {
        trainsetIds.push(item.trainsetId);
      });

      const findFaultTime = moment(this.oldbcDate).format('YYYY-MM-DD');

      const { code, data } = await getFaultData(trainsetIds, findFaultTime, moment(this.search.bcDate).format('YYYY-MM-DD') + '-' + this.search.bcDay);
      if (code == 1) {
        // trainSetId->trainsetId
        this.faultTask.tableData.data =
          data &&
          data.map((item) => {
            return {
              ...item,
              trainsetId: item.trainSetId,
              trainsetName: item.trainSetName,
            };
          });
      } else {
        this.faultTask.tableData.data = [];
      }
    },

    async getReCheck() {
      let trainsetNames = [];
      this.search.trainGroupList.forEach((item, index) => {
        trainsetNames.push(item.trainsetName);
      });
      if (trainsetNames.length === 0) return;
      const { code, data } = await getOverRunRecordList({
        TrainsetNameList: trainsetNames,
        dayPlanId: moment(this.search.bcDate).format('YYYY-MM-DD') + '-' + this.search.bcDay
      });
      if (code == 1) {
        this.reCheck = data;
      } else {
        this.reCheck = [];
      }
    },

    getButtonType(list) {
      if (list.length > 0) {
        return 'danger';
      } else {
        return 'primary';
      }
    },
    // 每隔一分钟刷新一下故障和复核数据
    refreshFaultAndRecheckData() {
      this.destoryRefreshTimer();
      this.refreshTimer = setInterval(() => {
        this.getFault();
        this.getReCheck();
      }, 60 * 1000);
    },
    destoryRefreshTimer() {
      clearInterval(this.refreshTimer);
      this.refreshTimer = null;
    },
    //动态加载页面高度
    getHeight() {
      if ($('.showButton').length == 0) {
        this.mainTableData.scrollHeight = window.innerHeight - 213 + 'px';
      } else {
        this.mainTableData.scrollHeight = window.innerHeight - 251 + 'px';
      }
    },
    //获取班次
    async getDay() {
      const urlPath = '/apiTrainRepair/common/getDay';
      await axios
        .get(urlPath, {
          params: {
            unitCode: this.unitCode,
          },
        })
        .then((res) => {
          if (res.data.code === 1) {
            const dayPlanId = res.data.dayPlanId;
            this.search.bcDate = dayPlanId.substring(0, 10);
            this.saveBcDate = dayPlanId.substring(0, 10);
            this.oldbcDate = this.search.bcDate;
            this.search.bcDay = dayPlanId.substring(11, 13);
            this.saveBcDay = dayPlanId.substring(11, 13);
            this.oldbcDay = this.search.bcDay;
            // this.$nextTick().then(() => {
            // this.initTable();
            // });
          } else {
            let msg = res.data.msg || '班次获取失败!';
            this.showAlertMsg(msg);
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    //获取班组
    // getGroups() {
    //   const urlPath = '/apiTrainRepair/common/getWorkTeamsByUnitCode?unitCode=' + unitCode;
    //   axios
    //     .get(urlPath)
    //     .then((res) => {
    //       if (res.data.code === 1) {
    //         this.search.bzDataList = res.data.rows;
    //       } else {
    //         let msg = res.data.msg || '班组获取失败!';
    //         this.showAlertMsg(msg);
    //       }
    //     })
    //     .catch((err) => {
    //       console.log(err);
    //     });
    // },
    async getUser() {
      const res = await getUser();
      let user = res.data.data;
      if (user && user.workTeam) {
        this.search.bzDataList = [
          {
            id: user.workTeam.teamCode,
            name: user.workTeam.teamName,
          },
        ];
        this.search.bzData = user.workTeam.teamCode;
        this.savebzData = user.workTeam.teamCode;
        this.oldbzData = this.search.bzData;
      }

      if (user.partTimeWorkTeams.length > 0) {
        user.partTimeWorkTeams.forEach((WorkTeam) => {
          this.search.bzDataList.push({
            id: WorkTeam.teamCode,
            name: WorkTeam.teamName,
          });
        });
      }
    },

    // 获取当前登录人运用所编码
    async getUnitCode() {
      let { data } = await axios.get('/apiTrainRepair/common/getUnitCode');
      if (data.code == 1) {
        this.unitCode = data.data;
      } else {
        this.$message.error('获取登录人信息失败！');
      }
    },

    // 获取当前登录人运用所名称
    async getUnitName() {
      let { data } = await axios.get('/apiTrainRepair/common/getUnitName');
      if (data.code == 1) {
        this.unitName = data.data;
      } else {
        this.$message.error('获取登录人信息失败！');
      }
    },

    //查询
    handleSearch() {
      // 当日计划班组未改变时，不查询
      // if (
      //   this.oldbcDate == this.search.bcDate &&
      //   this.oldbcDay == this.search.bcDay &&
      //   this.oldbzData == this.search.bzData &&
      //   this.oldShowWay == this.search.showWay
      // ) {
      //   this.tableDataFilter = this.tableFilter(this.mainTableData.data);
      //   return;
      // }
      let c_num = 0;
      this.mainTableData.data.forEach((item, index) => {
        if (item.allotStateCode == 3) {
          c_num++;
        }
      });
      if (c_num === 0) {
        this.multipleSelection_main = [];
        this.multipleSelection_sub1 = [];
        this.multipleSelection_sub2 = [];
        this.faultTaskSelect = [];
        this.oldbcDate = this.search.bcDate;
        this.oldbcDay = this.search.bcDay;
        this.oldbzData = this.search.bzData;
        this.oldShowWay = this.search.showWay;
        this.initTable();
        // this.search.trainsetTypeCode = '';
        // this.search.trainsetId = '';
        // this.search.workTypeCode = '';
        // this.search.workTaskName = '';
        // this.search.trainNo = '';
        // this.search.allotStateCode = [];
      } else {
        this.$confirm('您有未保存的派工任务，是否放弃保存？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            this.oldbcDate = this.search.bcDate;
            this.oldbcDay = this.search.bcDay;
            this.oldbzData = this.search.bzData;
            this.oldShowWay = this.search.showWay;

            this.initTable();
          })
          .catch(() => {
            this.showAlertMsg('已取消重新查询');
            this.search.bcDate = this.oldbcDate;
            this.search.bcDay = this.oldbcDay;
            this.search.bzData = this.oldbzData;
            this.search.showWay = this.oldShowWay;
          });
      }

      this.tableDataFilter = this.tableFilter(this.mainTableData.data);
    },
    //
    async handelChangeLimit(type) {
      this.changeType = type;
      if (type === 'date') {
        if (this.saveBcDate != moment(this.search.bcDate).format('YYYY-MM-DD')) {
          this.initSearch();
          this.handleSearch();
          this.saveBcDate = this.search.bcDate;
        }
      } else if (type === 'day') {
        if (this.saveBcDay != this.search.bcDay) {
          this.initSearch();
          this.handleSearch();
          this.saveBcDay = this.search.bcDay;
        }
      } else if (type === 'bzData') {
        if (this.savebzData != this.search.bzData) {
          await this.getTaskAllotStyle(this.unitCode, this.search.bzData);
          this.initSearch();
          this.handleSearch();
          this.savebzData = this.search.bzData;
        }
      }
    },
    //车型改变
    trainsetTypeCodeChange() {
      // 车型和车组关联
      this.search.trainsetId = '';
      if (this.search.trainsetTypeCode) {
        this.search.trainGroupList = this.originTrainsetList.filter(
          (trainset) => trainset.trainType == this.search.trainsetTypeCode
        );
      } else {
        this.search.trainGroupList = this.originTrainsetList;
      }
    },

    // 车组改变
    trainGroupChange() {
      if (this.search.trainsetId) {
        let trainGroup = this.search.trainGroupList.filter((trainset) => trainset.trainsetId == this.search.trainsetId);
        let trainType = this.search.trainTypeList.filter(
          (trainset) => trainset.trainsetTypeName == trainGroup[0].trainType
        );
        this.search.trainsetTypeCode = trainType[0].trainsetTypeCode;
      }
    },

    //重置
    handleReset() {
      let c_num = 0;
      this.mainTableData.data.forEach((item, index) => {
        if (item.allotStateCode == 3) {
          c_num++;
        }
      });
      this.countNum = c_num;
      if (c_num !== 0) {
        this.$confirm('重置后将清空未保存数据,确认重置?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            this.search.trainsetTypeCode = '';
            this.search.trainsetId = '';
            this.search.workTypeCode = '';
            this.search.workTaskName = '';
            this.search.trainNo = '';
            this.search.allotStateCode = [];
            this.multipleSelection_main = [];
            this.multipleSelection_sub1 = [];
            this.multipleSelection_sub2 = [];
            this.faultTaskSelect = [];
            this.search.bcDate = this.oldbcDate;
            this.search.bcDay = this.oldbcDay;
            this.search.bzData = this.oldbzData;
            this.search.showWay = '1';
            this.initTable();
            this.showAlertMsg('重置成功', 'success');
          })
          .catch(() => {
            this.showAlertMsg('已取消重置');
          });
      } else {
        this.search.trainsetTypeCode = '';
        this.search.trainsetId = '';
        this.search.workTypeCode = '';
        this.search.workTaskName = '';
        this.search.trainNo = '';
        this.search.allotStateCode = [];
        this.multipleSelection_main = [];
        this.multipleSelection_sub1 = [];
        this.multipleSelection_sub2 = [];
        this.faultTaskSelect = [];
        this.search.bcDate = this.oldbcDate;
        this.search.bcDay = this.oldbcDay;
        this.search.bzData = this.oldbzData;
        this.initTable();
        this.showAlertMsg('重置成功', 'success');
      }
    },
    //初始化筛选条件
    initSearch() {
      this.search.trainsetTypeCode = '';
      this.search.trainsetId = '';
      this.search.workTypeCode = '';
      this.search.workTaskName = '';
      this.search.trainNo = '';
      this.search.allotStateCode = [];
    },
    //派工
    onSave() {
      if (this.prevTableData.length == 0) {
        this.showAlertMsg('无检修项目可派工!');
        return;
      }

      if (JSON.stringify(this.prevTableData) == JSON.stringify(this.mainTableData.data)) {
        this.showAlertMsg('派工内容未变更', 'info');
        return;
      }

      const data = this.mainTableData.data.filter((item) => item.allotStateCode !== '0');

      data.forEach((item, index) => {
        item.packet.taskallotItemList.forEach((taskallotItem, taskallotItemIndex) => {
          let xzyMTaskcarpartList = data[index].packet.taskallotItemList[taskallotItemIndex].xzyMTaskcarpartList;
          xzyMTaskcarpartList = xzyMTaskcarpartList.filter((xzyMTaskcarpart) => {
            return xzyMTaskcarpart.workerList.length != 0;
          });
        });

        data[index].packet.taskallotItemList = data[index].packet.taskallotItemList.filter(taskallotItem => {
          return taskallotItem.workerList.length != 0 && taskallotItem.xzyMTaskcarpartList.length != 0
        })
      });

      // console.log(JSON.stringify(data,null,2));
      // return;
      this.loading = true;
      const urlPath = '/apiTrainRepair/xzyMTaskallotpacket/setTaskAllot';
      const jsonData = {
        data: JSON.stringify(data), //
        unitCode: this.unitCode,
        deptCode: this.search.bzData,
        mode: this.search.showWay,
      };

      axios.post(urlPath, jsonData).then(async (res) => {
        this.loading = false;
        if (res.data.code === 1) {
          this.initSearch();
          await this.initTable();
          this.showAlertMsg('派工成功!', 'success');
        } else {
          this.showAlertMsg('派工失败!');
        }
      });
    },
    //初始化表格
    async initTable() {
      // 记录当前查询专业分工状态
      // await setTaskAllotStyle(this.unitCode, this.search.bzData, this.search.showWay);
      this.loading = true;
      const urlPath = '/apiTrainRepair/xzyMTaskallotpacket/getRepairTask';
      let bcDate = '';
      if (this.search.bcDate && this.search.bcDate !== '') {
        bcDate = moment(this.search.bcDate).format('YYYY-MM-DD');
      }
      await axios
        .get(urlPath, {
          params: {
            unitCode: this.unitCode,
            dayplanId: bcDate + '-' + this.search.bcDay,
            deptCode: this.search.bzData,
            taskAllotType: '',
            mode: this.search.showWay,
          },
        })
        .then((res) => {
          console.log(res)
          this.loading = false;
          if (res.data.code === 1) {
            let rows = res.data;

            this.prevTableData = JSON.parse(JSON.stringify(rows.data));
            this.mainTableData.data = JSON.parse(JSON.stringify(rows.data));

            this.countNotWork(rows.data);
            let trainTypeList = [];
            let trainsetList = [];
            let taskList = [];

            rows.queryList.forEach((item, index) => {
              let json = {};
              json.trainsetTypeName = item.trainsetTypeName;
              json.trainsetTypeCode = item.trainsetTypeCode;
              trainTypeList.push(json);
              if (item.queryTrainsetList) {
                item.queryTrainsetList.forEach((el, i) => {
                  let trainsetJson = {};
                  trainsetJson.trainsetName = el.trainsetName;
                  trainsetJson.trainsetId = el.trainsetId;
                  trainsetJson.trainType = item.trainsetTypeCode;
                  trainsetList.push(trainsetJson);
                  if (el.queryWorkTaskList) {
                    el.queryWorkTaskList.forEach((n, v) => {
                      let taskJson = {};
                      taskJson.workTaskName = n.workTaskName;
                      taskJson.workTaskCode = n.workTaskCode;
                      taskList.push(taskJson);
                    });
                  }
                });
              }
            });
            //车型赋值
            this.search.trainTypeList = this.uniqueCommon(trainTypeList, 'trainsetTypeCode');
            //车组赋值

            let newtrainsetList = this.uniqueCommon(trainsetList, 'trainsetId').sort((a, b) => {
              if (a.trainsetId > b.trainsetId) {
                return 1;
              }
            });
            this.originTrainsetList = newtrainsetList;
            this.search.trainGroupList = newtrainsetList;
            //作业任务赋值
            this.search.worktaskList = this.uniqueCommon(taskList, 'workTaskName');

            this.search.worktypeList = rows.taskAllotTypeDict;
            this.search.workStatusList = rows.allotTypeDict;
            // this.search.workStatusList.push({ code: '3', name: '已分配' });

            this.getFault();
            this.getReCheck();
            this.refreshFaultAndRecheckData();
            this.multipleSelection_main = [];
            this.multipleSelection_sub1 = [];
            this.multipleSelection_sub2 = [];
          } else {
            let msg = res.data.msg || '班组获取失败!';
            this.showAlertMsg(msg);
          }
        })
        .catch((err) => {
          this.loading = false;
          console.log(err);
        });
    },
    //格式化人员
    formatWorker(row, index) {
      let person = [];
      if (row.workerList) {
        for (let i = 0; i < row.workerList.length; i++) {
          person.push(row.workerList[i].workerName);
        }
      }
      return person.join(',');
    },
    mainSelectChange(selection) {
      this.multipleSelection_main = selection.map((item) => item.id);
    },
    sub1SelectChange(selection, id) {
      this.sub1Select = this.sub1Select.filter((item) => item.parentId != id);

      const copySelection = JSON.parse(JSON.stringify(selection));
      copySelection.forEach((item) => {
        item.parentId = id;
      });
      this.sub1Select = [
        ...this.sub1Select,
        ...copySelection.map((item) => {
          return {
            id: item.id,
            parentId: item.parentId,
          };
        }),
      ];
      this.multipleSelection_sub1 = this.sub1Select.map((item) => item.id);
    },
    sub2SelectChange(selection, id) {
      this.multipleSelection_sub2 = this.multipleSelection_sub2.filter((item) => item.parentId != id);

      const copySelection = JSON.parse(JSON.stringify(selection));
      copySelection.forEach((item) => {
        item.parentId = id;
      });
      this.multipleSelection_sub2 = [
        ...this.multipleSelection_sub2,
        ...copySelection.map((item) => {
          return {
            itemCode: item.itemCode,
            carNo: item.carNo,
            partName: item.partName,
            processId: item.processId,
            parentId: item.parentId,
          };
        }),
      ];
    },
    // 表格复选框选中事件
    // handleCheck(checked, row, type, parentRow) {
    //   this.multipleSelection_main = [...new Set(this.multipleSelection_main)];
    //   this.multipleSelection_sub1 = [...new Set(this.multipleSelection_sub1)];
    //   this.multipleSelection_sub2 = [...new Set(this.multipleSelection_sub2)];
    //   this.faultTaskSelect = [...new Set(this.faultTaskSelect)];
    //   if (checked) {
    //     if (type === 'mainTable') {
    //       //主表的id(车组)
    //       this.multipleSelection_main.push(row.id);
    //     } else if (type === 'subTable1') {
    //       //副表的id(项目)
    //       this.multipleSelection_sub1.push(row.id);
    //     } else if (type === 'subTable2') {
    //       //副表的id(辆序)
    //       let json = {
    //         itemCode: row.itemCode,
    //         carNo: row.carNo,
    //         partName: row.partName,
    //         processId: row.processId,
    //         parentId: parentRow.id,
    //       };
    //       this.multipleSelection_sub2.push(json);
    //     } else if (type === 'faultTask') {
    //       this.faultTaskSelect.push(row);
    //     }
    //   } else {
    //     if (type === 'mainTable') {
    //       //主表的id(车组)
    //       // console.log(this.multipleSelection_main);
    //       this.multipleSelection_main = this.multipleSelection_main.filter((item) => item !== row.id);
    //       // this.multipleSelection_main.remove(row.id)
    //     } else if (type === 'subTable1') {
    //       //副表的id(项目)
    //       this.multipleSelection_sub1 = this.multipleSelection_sub1.filter((item) => item !== row.id);
    //       // this.multipleSelection_sub1.remove(row.id)
    //     } else if (type === 'subTable2') {
    //       //副表的id(辆序)
    //       this.multipleSelection_sub2.forEach((item, index) => {
    //         if (
    //           item.itemCode == row.itemCode &&
    //           item.carNo === row.carNo &&
    //           item.partName === row.partName &&
    //           item.parentId === parentRow.id
    //         ) {
    //           this.multipleSelection_sub2.splice(index, 1);
    //         }
    //       });
    //     } else if (type === 'faultTask') {
    //       this.faultTaskSelect.forEach((item, index) => {
    //         if (item.faultId == row.faultId) {
    //           this.faultTaskSelect.splice(index, 1);
    //         }
    //       });
    //     }
    //   }
    // },
    faultFilterTrainsetIdChange() {
      this.faultTask.tableData.currentPage = 1;
      this.$refs.faultTableList.clearSelection();
    },
    faultSelectChange(selection) {
      this.faultTaskSelect = selection;
    },
    //故障任务点击事件
    async getfaultTask() {
      this.destoryRefreshTimer();

      await this.getFault();

      this.faultTask.tableData.currentPage = 1;
      this.faultTask.workTimes = moment(this.oldbcDate).format('YYYY-MM-DD') + '-' + this.oldbcDay;
      let faultTaskWorkTeam = {};
      this.search.bzDataList.forEach((item, index) => {
        if (item.id === this.search.bzData) {
          faultTaskWorkTeam = item;
        }
      });
      this.faultTask.workTeam = faultTaskWorkTeam.name;
      let trainsetIds = [];
      this.search.trainGroupList.forEach((item, index) => {
        trainsetIds.push(item.trainsetId);
      });

      this.dialogVisibleTask = true;
    },
    //已有故障任务置灰
    // selectFaultable(row,index){
    //     return true;
    // },
    //保存故障任务
    saveFaultTask() {
      if (this.faultTaskSelect.length === 0) {
        this.showAlertMsg('请选择故障!');
      } else {
        const mainData = this.mainTableData.data;
        const taskData = this.faultTaskSelect;
        let newTrainsetIds = [];
        let faultIds = [];
        const urlPath = '/apiTrainRepair/common/getFaultPacket';
        // const postData = {
        //   params: {},
        //   instr: {},
        //   user: {},
        //   page: {},
        //   datas: [],
        // };

        let repairCode = '';
        let packetCode = '';
        let packetName = '';
        let packetType = '';
        let sTaskId = '';

        axios.get(urlPath).then((res) => {
          if (res.data.code === 1) {
            const data = res.data.data;
            if (data) {
              repairCode = data.repairCode;
              packetCode = data.packetCode;
              packetName = data.packetName;
              packetType = data.packetType;
              sTaskId = data.id;
              let workTeams = {};
              this.search.bzDataList.forEach((item, index) => {
                if (item.id == this.search.bzData) {
                  workTeams = item;
                }
              });
              // 得到所有workTypeCode === '5'的trainsetId
              const ignoreIds = mainData.reduce((prev, item) => {
                if (item.workTypeCode === '5') {
                  prev.push(item.trainsetId);
                }
                return prev;
              }, []);
              for (let i = 0; i < mainData.length; i++) {
                for (let j = 0; j < taskData.length; j++) {
                  if (mainData[i].trainsetId === taskData[j].trainsetId) {
                    if (mainData[i].workTypeCode === '5') {
                      this.saveStatus = mainData[i].allotStateName;
                      if (mainData[i].allotStateName != '未派工') {
                        mainData[i].allotStateName = '部分派工';
                      }
                      faultIds.push(mainData[i].trainsetId);
                      let subJson = {};
                      subJson.itemName = taskData[j].carNo
                        ? '[辆序：' + taskData[j].carNo + ']' + taskData[j].faultDescription
                        : taskData[j].faultDescription;
                      subJson.displayItemName = taskData[j].carNo
                        ? '[辆序：' + taskData[j].carNo + ']' + taskData[j].faultDescription
                        : taskData[j].faultDescription;
                      subJson.carNo = taskData[j].carNo;
                      subJson.trainsetId = '2222s';
                      subJson.newItem = '1';
                      subJson.trainsetName = taskData[j].trainsetName;
                      subJson.workerList = [];
                      subJson.fillType = 0;
                      mainData[i].showMode = 'NoCarPartMore';
                      subJson.itemCode = taskData[j].faultId;
                      subJson.carNo = taskData[j].carNo;
                      subJson.xzyMTaskcarpartList = [];
                      let bottomJson = {
                        newItem: '1',
                        itemCode: taskData[j].faultId,
                        itemName: taskData[j].faultDescription,
                        carNo: taskData[j].carNo,
                        arrageType: '0',
                        dayPlanID: moment(this.search.bcDate).format('YYYY-MM-DD') + '-' + this.search.bcDay,
                        mainCyc: '-1',
                        taskItemId: this.createId(),
                        trainsetId: taskData[j].trainsetId,
                        trainsetName: taskData[j].trainsetName,
                        unitCode: this.unitCode,
                        unitName: this.unitName,
                        workerList: [],
                        xzyMTaskallotdept: {
                          deptCode: this.search.bzData,
                          deptName: workTeams.name,
                        },
                      };
                      subJson.xzyMTaskcarpartList.push(bottomJson);

                      mainData[i].packet.taskallotItemList.push(subJson);
                      mainData[i].packet.taskallotItemList = this.unique(mainData[i].packet.taskallotItemList);
                    } else {
                      if (ignoreIds.includes(mainData[i].trainsetId)) continue;
                      //该车组不已存在故障
                      let arr = [];
                      arr.push(mainData[i]);
                      for (let x = 0; x < arr.length; x++) {
                        newTrainsetIds.push(arr[x].trainsetId);
                      }
                      newTrainsetIds = [...new Set(newTrainsetIds)];
                      for (let x = 0; x < newTrainsetIds.length; x++) {
                        for (let z = 0; z < faultIds.length; z++) {
                          if (newTrainsetIds[x] == faultIds[z]) {
                            newTrainsetIds.splice(x, 1);
                            x--;
                          }
                        }
                      }
                    }
                  }
                }
              }

              for (let i = 0; i < newTrainsetIds.length; i++) {
                let mainJson = {};
                mainJson.packet = {
                  mainCyc: repairCode,
                  packetCode: packetCode,
                  packetName: packetName,
                  packetType: packetType,
                  sTaskId: '',
                  taskallotItemList: [],
                };
                for (let j = 0; j < taskData.length; j++) {
                  if (newTrainsetIds[i] === taskData[j].trainsetId) {
                    mainJson.trainsetTypeName = this.traintypeTrainsetidMap[taskData[j].trainsetId];
                    mainJson.trainsetTypeCode = this.traintypeTrainsetidMap[taskData[j].trainsetId];
                    mainJson.taskRepairCode = repairCode;
                    mainJson.trainsetId = taskData[j].trainsetId;
                    mainJson.trainsetName = taskData[j].trainsetName;
                    mainJson.workTypeName = '故障项目';
                    mainJson.workTaskName = '故障处理任务';
                    mainJson.newItem = '1';
                    mainJson.workTypeCode = '5';
                    mainJson.packetType = '5';
                    mainJson.id = this.createId();
                    mainJson.workerList = [];
                    mainJson.allotStateName = '未派工';
                    mainJson.allotStateCode = 0;
                    mainJson.showMode = 'NoCarPartMore';
                    mainJson.fillType = 0;
                    let subJson = {
                      itemName: taskData[j].faultDescription,
                      displayItemName: taskData[j].carNo
                        ? '[辆序：' + taskData[j].carNo + ']' + taskData[j].faultDescription
                        : taskData[j].faultDescription,
                      workerList: [],
                      carNo: taskData[j].carNo,
                      itemCode: taskData[j].faultId,
                      newItem: '1',
                      id: this.createId(),
                      xzyMTaskcarpartList: [],
                    };
                    let bottomJson = {
                      newItem: '1',
                      repairType: packetType,
                      itemCode: taskData[j].faultId,
                      itemName: taskData[j].faultDescription,
                      carNo: taskData[j].carNo,
                      arrageType: '0',
                      dayPlanID: moment(this.search.bcDate).format('YYYY-MM-DD') + '-' + this.search.bcDay,
                      mainCyc: '-1',
                      taskItemId: this.createId(),
                      trainsetId: taskData[j].trainsetId,
                      trainsetName: taskData[j].trainsetName,
                      unitCode: this.unitCode,
                      unitName: this.unitName,
                      workerList: [],
                      xzyMTaskallotdept: {
                        deptCode: this.search.bzData,
                        deptName: workTeams.name,
                      },
                    };

                    subJson.xzyMTaskcarpartList.push(bottomJson);
                    subJson.xzyMTaskcarpartList = this.unique(subJson.xzyMTaskcarpartList);
                    mainJson.packet.taskallotItemList.push(subJson);
                    mainJson.packet.taskallotItemList = this.unique(mainJson.packet.taskallotItemList);
                  }
                }
                this.mainTableData.data.push(mainJson);
              }

              this.faultTaskSelect = [];
              this.faultFilterTrainsetId = '';
              this.dialogVisibleTask = false;
            } else {
              this.showAlertMsg('故障认领失败!数据类型为空!');
            }
          } else {
            this.showAlertMsg('认领故障失败!');
          }
        });
      }
    },
    compare(arr) {
      return function (a, b) {
        return a.trainsetId - b.trainsetId;
      };
    },
    //翻页序号问题修改
    indexNum(index) {
      index = index + 1 + this.faultTask.tableData.pageSize * (this.faultTask.tableData.currentPage - 1);
      return index;
    },
    //移除新增故障
    removeFault(index, rows, pIndex, pRows) {
      this.$confirm('确认删除此故障吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          // this.faultTask.tableData.data.forEach((item, index) => {
          //   rows.forEach((e, i) => {
          //     if (item.carNo == e.carNo && item.faultId == e.itemCode) {
          //       this.faultTask.tableData.data.splice(index, 1);
          //     }
          //   });
          // });
          let hasNewCount = 0;
          rows.splice(index, 1);
          this.$nextTick(() => {
            rows.forEach((item) => {
              if (item.newItem == '1') {
                hasNewCount++;
              } else {
                hasNewCount--;
              }
            });
            if (hasNewCount <= 0) {
              this.mainTableData.data[pIndex].allotStateName = this.saveStatus;
            }
          });
          if (rows.length === 0) {
            this.mainTableData.data.forEach((item, index) => {
              if (item.id == pRows.id) {
                this.mainTableData.data.splice(index, 1);
              }
            });
          }

          this.showAlertMsg('删除成功', 'success');
        })
        .catch(() => {
          this.showAlertMsg('已取消删除');
        });
    },
    //人员选择事件
    showgetWorker() {
      this.dialogVisiblePeople = true;
      this.chooseWorkers.quickQuery = '';
      this.$nextTick().then(() => {
        this.$refs.tree.setCheckedKeys([]);
      });
      this.checkPeople = [];
      this.getWorker();
      // if(this.multipleSelection_main.length === 0 && this.multipleSelection_sub1.length === 0 && this.multipleSelection_sub2.length === 0){
      //     this.showAlertMsg('请先选择任务','warning')
      // }else{
      //
      // }
    },

    //表格展开事件
    expandChange_main(row, expandRows) {
      if (expandRows.length != 0) {
        //展开
        this.$nextTick().then(() => {
          row.packet.taskallotItemList.forEach((item) => {
            if (this.multipleSelection_sub1.includes(item.id)) {
              this.$nextTick().then(() => {
                this.$refs[row.id.slice(0, 8)].toggleRowSelection(item);
              });
            }
          });
          // $('.sub1CheckBox').each((index, el) => {
          //   this.multipleSelection_sub1.forEach((item, index) => {
          //     if ($(el).attr('data-id') == item) {
          //       $(el).addClass('is-checked');
          //       $(el).find('.el-checkbox__input').addClass('is-checked');
          //     }
          //   });
          // });
        });
      }
    },
    //表格展开事件
    expandChange_sub(row, expandRows) {
      if (expandRows.length != 0) {
        //展开
        const sub2ProcessId = this.multipleSelection_sub2.map((sub2) => sub2.processId);
        this.$nextTick().then(() => {
          row.xzyMTaskcarpartList.forEach((item) => {
            if (sub2ProcessId.includes(item.processId)) {
              this.$nextTick().then(() => {
                this.$refs[row.id.slice(0, 8)].toggleRowSelection(item);
              });
            }
          });
          // $('.sub2CheckBox').each((index, el) => {
          //   this.multipleSelection_sub2.forEach((item, index) => {
          //     if ($(el).attr('data-id') == item.processId) {
          //       $(el).addClass('is-checked');
          //       $(el).find('.el-checkbox__input').addClass('is-checked');
          //     }
          //   });
          // });
        });
      }
    },
    // 组内变动
    async setBranch() {
      const urlPath = '/apiTrainRepair/xzyCAllotbranchConfig/setBranch';
      let data = JSON.parse(JSON.stringify(this.workerTeamData));
      data.forEach((team, teamIdx) => {
        team.sort = teamIdx;
        team.deptName = this.workTeamInfoMap.name;
        if (Array.isArray(team.perSonNelModels)) {
          team.perSonNelModels.forEach((worker, workerIdx) => {
            worker.sort = workerIdx;
            worker.deptName = this.workTeamInfoMap.name;
          });
        } else {
          team.perSonNelModels = [];
        }
      });
      await axios.post(`${urlPath}?deptCode=${this.search.bzData}&unitCode=${this.unitCode}`, data);
    },
    // 组外变动
    async setPostBySon() {
      // let userList = [];
      // this.workerUserData.forEach((user) => {
      //   if (Array.isArray(user.postModelList)) {
      //     user.postModelList.forEach((post) => {
      //       userList.push({
      //         staffId: user.staffId,
      //         staffName: user.name,
      //         postId: post.postId,
      //         deptCode: this.search.bzData,
      //         unitCode,
      //       });
      //     });
      //   }
      // });
      await setPostBySon(this.workerUserData);
    },
    //编辑班组
    async setGroup() {
      await this.setBranch();
      await this.setPostBySon();
    },

    //添加人员
    addWorkers() {
      let sub1 = [];
      let sub2 = [];
      const mainTableData = this.mainTableData.data;
      const multipleSelection_main = this.multipleSelection_main;
      const multipleSelection_sub1 = this.multipleSelection_sub1;
      const multipleSelection_sub2 = this.multipleSelection_sub2;

      if (
        multipleSelection_main.length === 0 &&
        multipleSelection_sub1.length === 0 &&
        multipleSelection_sub2.length === 0
      ) {
        if (
          JSON.stringify(this.workerTeamDataPrev) == JSON.stringify(this.workerTeamData) &&
          JSON.stringify(this.workerUserDataPrev) == JSON.stringify(this.workerUserData)
        ) {
          this.showAlertMsg('您未选择任务或未进行任何人员调整', 'info');
        } else {
          this.showAlertMsg('人员调整成功!', 'success');
          this.setGroup();
        }
      } else {
        if (this.checkPeople.length === 0) {
          this.showAlertMsg('请选择人员!', 'warning');
          return;
        }
        let resetAllotState = (mainTableRow) => {
          if (mainTableRow.workerList && mainTableRow.workerList.length > 0) {
            mainTableRow.allotStateCode = '3';
            mainTableRow.allotStateName = '已分配';
          } else {
            mainTableRow.allotStateCode = '0';
            mainTableRow.allotStateName = '未派工';
          }
        };

        for (let i = 0, len = mainTableData.length; i < len; i++) {
          //第一层表添加人员
          for (let j = 0, len1 = multipleSelection_main.length; j < len1; j++) {
            if (mainTableData[i].id === multipleSelection_main[j]) {
              mainTableData[i].workerList = this.checkPeople;

              mainTableData[i].packet.taskallotItemList.forEach((item, index) => {
                item.workerList = this.checkPeople;
                if (item.xzyMTaskcarpartList) {
                  item.xzyMTaskcarpartList.forEach((el, i) => {
                    el.workerList = this.checkPeople;
                  });
                }
              });
              resetAllotState(mainTableData[i]);
            }
          }
          //第二层表添加人员
          const taskallotItemList = mainTableData[i].packet.taskallotItemList;
          for (let j = 0, len1 = multipleSelection_sub1.length; j < len1; j++) {
            let nowMainData = {};
            for (let x = 0, len2 = taskallotItemList.length; x < len2; x++) {
              if (taskallotItemList[x].id === multipleSelection_sub1[j]) {
                nowMainData = mainTableData[i];
                taskallotItemList[x].workerList = this.checkPeople;
                //下一层级全部覆盖
                if (taskallotItemList[x].xzyMTaskcarpartList) {
                  taskallotItemList[x].xzyMTaskcarpartList.forEach((item, index) => {
                    item.workerList = this.checkPeople;
                  });
                }

                //查询同层级的人员(目的是向上一层添加本层所有人员)
                mainTableData[i].packet.taskallotItemList.forEach((item, index) => {
                  if (item.workerList.length != 0) {
                    item.workerList.forEach((z, x) => {
                      sub1.push(z);
                    });
                  }
                });
              }
            }
            nowMainData.workerList = this.uniqueCommon(sub1, 'workerID');
            resetAllotState(nowMainData);
          }
          //第三层表添加人员
          for (let j = 0, len1 = multipleSelection_sub2.length; j < len1; j++) {
            let nowMainData = {};
            for (let x = 0, len2 = taskallotItemList.length; x < len2; x++) {
              let nowSub1Data = taskallotItemList[x];
              let sub3 = [];
              const xzyMTaskcarpartList = nowSub1Data.xzyMTaskcarpartList;
              for (let z = 0, len3 = xzyMTaskcarpartList.length; z < len3; z++) {
                // console.log(xzyMTaskcarpartList[z].partName, multipleSelection_sub2[j])
                if (
                  nowSub1Data.id === multipleSelection_sub2[j].parentId &&
                  xzyMTaskcarpartList[z].itemCode === multipleSelection_sub2[j].itemCode &&
                  xzyMTaskcarpartList[z].carNo === multipleSelection_sub2[j].carNo &&
                  xzyMTaskcarpartList[z].partName === multipleSelection_sub2[j].partName
                ) {
                  xzyMTaskcarpartList[z].workerList = this.checkPeople;

                  nowMainData = mainTableData[i];

                  //查询同层级的人员(目的是向上一层添加本层所有人员)
                  nowSub1Data.xzyMTaskcarpartList.forEach((item, index) => {
                    if (item.workerList.length != 0) {
                      item.workerList.forEach((z, x) => {
                        sub3.push(z);
                      });
                    }
                  });
                  //查询第二层层级的人员(目的是向第一层添加本层所有人员)
                  this.$nextTick().then(() => {
                    nowMainData.packet.taskallotItemList.forEach((item, index) => {
                      if (item.workerList.length != 0) {
                        item.workerList.forEach((n, m) => {
                          sub2.push(n);
                          if (nowMainData.workerList) {
                            nowMainData.workerList = this.uniqueCommon(sub2, 'workerID');
                          }
                          resetAllotState(nowMainData);
                        });
                      }
                    });
                  });
                  nowSub1Data.workerList = this.uniqueCommon(sub3, 'workerID');
                }
              }
            }
          }
        }

        // let nowTreeData = mainTableData;

        // this.mainTableData.data = Object.assign(this.mainTableData.data, nowTreeData);

        this.emptyArrr.push({});

        // this.multipleSelection_main = [];
        // this.multipleSelection_sub1 = [];
        // this.multipleSelection_sub2 = [];
        this.setGroup();
        this.showAlertMsg('人员分配成功!', 'success');
        // this.$nextTick().then(() => {
        //   $('.mainCheckBox').each((index, el) => {
        //     $(el).removeClass('is-checked');
        //     $(el).find('.el-checkbox__input').removeClass('is-checked');
        //   });
        //   $('.sub1CheckBox').each((index, el) => {
        //     $(el).removeClass('is-checked');
        //     $(el).find('.el-checkbox__input').removeClass('is-checked');
        //   });
        //   $('.sub2CheckBox').each((index, el) => {
        //     $(el).removeClass('is-checked');
        //     $(el).find('.el-checkbox__input').removeClass('is-checked');
        //   });
        // });
      }

      this.chooseWorkerVisible = false;

      this.$nextTick().then(() => {
        this.countNotWork(this.mainTableData.data);
      });
    },
    // 清空人员
    clearWorker() {
      if (
        this.multipleSelection_main.length == 0 &&
        this.multipleSelection_sub1.length == 0 &&
        this.multipleSelection_sub2.length == 0
      ) {
        this.showAlertMsg('请选择检修任务!');
      } else {
        this.$confirm('此操作将人员清空, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            const mainTableData = this.mainTableData.data;
            const multipleSelection_main = this.multipleSelection_main;
            const multipleSelection_sub1 = this.multipleSelection_sub1;
            const multipleSelection_sub2 = this.multipleSelection_sub2;

            // 第一层
            mainTableData.forEach((tableItem) => {
              multipleSelection_main.forEach((item) => {
                if (tableItem.id == item) {
                  tableItem.allotStateCode = '0';
                  tableItem.allotStateName = '未派工';
                  tableItem.workerList = [];
                  tableItem.packet.taskallotItemList.forEach((taskallotItem) => {
                    taskallotItem.workerList = [];
                    taskallotItem.xzyMTaskcarpartList.forEach((xzyMTaskcarpartItem) => {
                      xzyMTaskcarpartItem.workerList = [];
                    });
                  });
                }
              });
            });

            // 第二层
            mainTableData.forEach((tableItem) => {
              const taskallotItemList = tableItem.packet.taskallotItemList;
              taskallotItemList.forEach((taskallotItem) => {
                multipleSelection_sub1.forEach((item) => {
                  if (taskallotItem.id == item) {
                    taskallotItem.workerList = [];
                    taskallotItem.xzyMTaskcarpartList.forEach((xzyMTaskcarpartItem) => {
                      xzyMTaskcarpartItem.workerList = [];
                    });
                  }
                });
              });

              // 刷新第一层的人员
              tableItem.workerList = this.uniqueCommon(
                taskallotItemList.map((taskallotItem) => taskallotItem.workerList).flat(),
                'workerID'
              );
            });

            if (this.search.showWay == '1') {
              // 第三层
              mainTableData.forEach((tableItem) => {
                const taskallotItemList = tableItem.packet.taskallotItemList;
                taskallotItemList.forEach((taskallotItem) => {
                  const xzyMTaskcarpartList = taskallotItem.xzyMTaskcarpartList;
                  xzyMTaskcarpartList.forEach((xzyMTaskcarpartItem) => {
                    multipleSelection_sub2.forEach((item) => {
                      if (xzyMTaskcarpartItem.itemCode == item.itemCode && xzyMTaskcarpartItem.carNo == item.carNo) {
                        xzyMTaskcarpartItem.workerList = [];
                      }
                    });
                  });
                  // 刷新第二层的人员
                  taskallotItem.workerList = this.uniqueCommon(
                    xzyMTaskcarpartList.map((xzyMTaskcarpartItem) => xzyMTaskcarpartItem.workerList).flat(),
                    'workerID'
                  );
                });
                // 刷新第一层的人员
                tableItem.workerList = this.uniqueCommon(
                  taskallotItemList.map((taskallotItem) => taskallotItem.workerList).flat(),
                  'workerID'
                );
              });
            }
            this.countNotWork(mainTableData);
            this.$message({
              type: 'success',
              message: '人员清空成功!',
            });
          })
          .catch(() => {
            this.$message({
              type: 'info',
              message: '已取消人员清空',
            });
          });
      }
    },

    countNotWork(data) {
      let num = 0;
      let trainsetNum = 0;
      let trainsetType = [];

      const tableData = data;
      for (let i = 0; i < tableData.length; i++) {
        if (tableData[i].allotStateCode === '0') {
          num++;
          trainsetType.push(tableData[i].trainsetId);
        }
      }

      let arr = [...new Set(trainsetType)];
      trainsetNum = arr.length;
      this.notWorkNum.task = num;
      this.notWorkNum.trainset = trainsetNum;
    },

    handleSizeChange(val) {
      this.mainTableData.pageSize = val;
      // this.initCheck();
    },
    handleCurrentChange(val) {
      this.mainTableData.currentPage = val;
      // this.initCheck();
    },
    //翻页保持复选框选中
    // initCheck() {
    //   const data = [...new Set(this.multipleSelection_main)];
    //   const sub1Data = [...new Set(this.multipleSelection_sub1)];
    //   const sub2Data = [...new Set(this.multipleSelection_sub2)];
    //   this.$nextTick().then(() => {
    //     $('.mainCheckBox').each((index, el) => {
    //       $(el).removeClass('is-checked');
    //       $(el).find('.el-checkbox__input').removeClass('is-checked');
    //       for (let i = 0; i < data.length; i++) {
    //         if (data[i] == $(el).attr('data-id')) {
    //           $(el).addClass('is-checked');
    //           $(el).find('.el-checkbox__input').addClass('is-checked');
    //         }
    //       }
    //     });
    //     $('.sub1CheckBox').each((index, el) => {
    //       $(el).removeClass('is-checked');
    //       $(el).find('.el-checkbox__input').removeClass('is-checked');
    //       for (let i = 0; i < sub1Data.length; i++) {
    //         if (sub1Data[i] == $(el).attr('data-id')) {
    //           $(el).addClass('is-checked');
    //           $(el).find('.el-checkbox__input').addClass('is-checked');
    //         }
    //       }
    //     });
    //     $('.sub2CheckBox').each((index, el) => {
    //       $(el).removeClass('is-checked');
    //       $(el).find('.el-checkbox__input').removeClass('is-checked');
    //       for (let i = 0; i < sub2Data.length; i++) {
    //         if (sub2Data[i] == $(el).attr('data-id')) {
    //           $(el).addClass('is-checked');
    //           $(el).find('.el-checkbox__input').addClass('is-checked');
    //         }
    //       }
    //     });
    //   });
    // },
    // initFaultCheck() {
    //   let arr = [];
    //   this.faultTaskSelect.forEach((item, index) => {
    //     this.faultTask.tableData.data.forEach((e, i) => {
    //       if (item.carNo == e.carNo && item.faultId == e.faultId) {
    //         arr.push(e);
    //       }
    //     });
    //   });
    //   this.$nextTick().then(() => {
    //     $('.faultTaskCheckBox').each((index, item) => {
    //       $(item).removeClass('is-checked');
    //       $(item).find('.el-checkbox__input').removeClass('is-checked');
    //       for (let i = 0; i < arr.length; i++) {
    //         if ($(item).attr('data-id') == arr[i].faultId) {
    //           $(item).addClass('is-checked');
    //           $(item).find('.el-checkbox__input').addClass('is-checked');
    //         }
    //       }
    //     });
    //   });
    // },
    closeFault() {
      this.$refs.faultTableList.clearSelection();
      this.refreshFaultAndRecheckData();
      this.recheckFilterTrainsetId = '';
      this.recheckFilterItem = '';
    },
    recheckFilterChange() {
      this.reCheckedTaskTablePageNum = 1;
      this.$refs.reCheckTable.clearSelection();
    },
    handleSizeChange_task(val) {
      this.faultTask.tableData.pageSize = val;
      // this.initFaultCheck();
    },
    handleCurrentChange_task(val) {
      this.faultTask.tableData.currentPage = val;
      // this.initFaultCheck();
    },
    //提示文本样式统一
    showAlertMsg: function (msg, type, duration) {
      this.$message({
        showClose: true,
        message: msg || '失败!',
        duration: duration || 2000,
        type: type || 'warning',
      });
    },
    //去除首位空格
    trim: function (val) {
      if (val == null) {
        return '';
      }
      return val.toString().replace(/(^\s*)|(\s*$)|\r|\n/g, '');
    },
    //去除数组中相同的json
    uniqueCommon(list, code) {
      let arr = [];
      if (list) {
        for (let i = 0; i < list.length; i++) {
          if (i === 0) arr.push(list[i]);
          let b = false;
          if (arr.length > 0 && i > 0) {
            for (let j = 0; j < arr.length; j++) {
              if (code == 'trainsetTypeCode') {
                if (arr[j].trainsetTypeCode == list[i].trainsetTypeCode) {
                  b = true;
                }
              } else if (code == 'trainsetId') {
                if (arr[j].trainsetId == list[i].trainsetId) {
                  b = true;
                }
              } else if (code == 'workerID') {
                if (arr[j].workerID == list[i].workerID) {
                  b = true;
                }
              } else if (code == 'workTaskName') {
                if (arr[j].workTaskName == list[i].workTaskName) {
                  b = true;
                }
              } else if (code == 'faultId') {
                if (arr[j].faultId == list[i].faultId) {
                  b = true;
                }
              }
            }
            if (!b) {
              arr.push(list[i]);
            }
          }
        }
      }

      return arr;
    },
    unique(list) {
      let arr = [];
      if (list) {
        for (let i = 0; i < list.length; i++) {
          if (i === 0) arr.push(list[i]);
          let b = false;
          if (arr.length > 0 && i > 0) {
            for (let j = 0; j < arr.length; j++) {
              if (arr[j].itemCode == list[i].itemCode) {
                b = true;
              }
            }
            if (!b) {
              arr.push(list[i]);
            }
          }
        }
      }
      return arr;
    },
    createId() {
      var d = new Date().getTime();
      var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c == 'x' ? r : (r & 0x3) | 0x8).toString(16);
      });
      return uuid;
    },
    openReCheckedTaskDialog() {
      this.reCheckedTaskdialogVisible = true;
    },
    reCheckedTaskdialogClose() {
      this.$refs.reCheckTable.clearSelection();
    },
    reCheckedTaskSelectionChange(selection) {
      this.reCheckedTaskSelectArr = selection;
    },
    // reCheckedTaskSelect(selection) {
    //   this.reCheckedTaskSelectArr = selection
    //   const isSelect = selection.find((item) => item.s_OVERTYPENAME === row.s_OVERTYPENAME);
    //   // 勾选
    //   if (isSelect) {
    //     this.reCheckedTaskSelectArr.push(row);
    //   } else {
    //     // 取消勾选
    //     this.reCheckedTaskSelectArr = this.reCheckedTaskSelectArr.filter(
    //       (item) => item.s_OVERTYPENAME !== row.s_OVERTYPENAME
    //     );
    //   }
    // },
    reCheckedTaskTablePageSizeChange(pageSize) {
      this.reCheckedTaskTablePageSize = pageSize;
    },
    reCheckedTaskTablePageNumChange(pageNum) {
      this.reCheckedTaskTablePageNum = pageNum;
    },
    confirmReCheckedTask() {
      if (this.reCheckedTaskSelectArr.length === 0) {
        this.showAlertMsg('请选择复核任务!');
      } else {
        let url = '/apiTrainRepair/common/getRecheckPacket';
        axios.get(url).then((res) => {
          const { repairCode, packetCode, packetName, packetType } = res.data.data;

          let workTeams = {};
          this.search.bzDataList.forEach((item, index) => {
            if (item.id == this.search.bzData) {
              workTeams = item;
            }
          });
          this.reCheckedTaskSelectArr.forEach((selectItem) => {
            const mainTableHas = this.mainTableData.data.find(
              (tableItem) => tableItem.trainsetId === selectItem.trainsetId && tableItem.workTypeCode == '11'
            );
            let mainJson = {};
            Object.keys(selectItem).forEach((item) => {
              mainJson[item] = selectItem[item] ? selectItem[item] : '无数据';
            });

            let displayItemName;
            if (selectItem.recheckType == '受电弓') {
              displayItemName =
                '复核类型：' +
                mainJson.recheckType +
                '，复核项目：' +
                mainJson.checkItemName +
                '，受电弓编号：' +
                mainJson.pantoCode +
                '，滑板编号：' +
                mainJson.skaCode +
                '，检验值：' +
                mainJson.checkValue;
            } else if (selectItem.recheckType == '轮对') {
              displayItemName =
                '复核类型：' +
                mainJson.recheckType +
                '，复核项目：' +
                mainJson.checkItemName +
                '，辆序：' +
                mainJson.carNo +
                '，轮对位置：' +
                mainJson.pantoCode +
                '，轮饼位置：' +
                mainJson.pantoCode +
                '，检验值：' +
                mainJson.checkValue;
            }

            if (mainTableHas) {
              // 有对应trainsetid且workTypeCode=='11'
              this.mainTableData.data.forEach((tableItem) => {
                if (tableItem.trainsetId === selectItem.trainsetId && tableItem.workTypeCode == '11') {
                  tableItem.packet.taskallotItemList.push({
                    itemName: selectItem.checkItemName,
                    // 复核类型：复核类型内容，复核项目：复核项目内容，受电弓编号：受电弓编号内容，滑板编号：滑板编号内容，检测值：检测值内容
                    // 复核类型：复核类型内容，复核项目：复核项目内容，辆序：辆序内容，轮对位置：轮对位置内容，轮饼位置：轮饼位置内容，检测值：检测值内容
                    // displayItemName: selectItem.carNo ? '[辆序：' + selectItem.carNo + ']' + selectItem.checkItemName : selectItem.checkItemName,
                    displayItemName,
                    workerList: [],
                    carNo: selectItem.carNo,
                    itemCode: selectItem.id,
                    fillType: 0,
                    newItem: '1',
                    id: this.createId(),
                    xzyMTaskcarpartList: [
                      {
                        newItem: '1',
                        repairType: packetType,
                        itemCode: selectItem.id,
                        itemName: selectItem.checkItemName,
                        carNo: selectItem.carNo,
                        arrageType: '0',
                        dayPlanID: moment(this.search.bcDate).format('YYYY-MM-DD') + '-' + this.search.bcDay,
                        mainCyc: '-1',
                        taskItemId: this.createId(),
                        trainsetId: selectItem.trainsetId,
                        trainsetName: selectItem.trainsetName,
                        unitCode: this.unitCode,
                        unitName: this.unitName,
                        workerList: [],
                        xzyMTaskallotdept: {
                          deptCode: this.search.bzData,
                          deptName: workTeams.name,
                        },
                      },
                    ],
                  });
                }
              });
            } else {
              // 没有
              mainJson.trainsetTypeName = this.traintypeTrainsetidMap[selectItem.trainsetId];
              mainJson.trainsetTypeCode = this.traintypeTrainsetidMap[selectItem.trainsetId];
              mainJson.trainsetId = selectItem.trainsetId;
              mainJson.trainsetName = selectItem.trainsetName;
              mainJson.taskRepairCode = repairCode;
              mainJson.workTypeName = '复核项目';
              mainJson.workTaskName = '复核处理任务';
              mainJson.newItem = '1';
              mainJson.workTypeCode = '11';
              mainJson.id = this.createId();
              mainJson.workerList = [];
              mainJson.allotStateName = '未派工';
              mainJson.allotStateCode = 0;
              mainJson.showMode = 'ShowCarPart';
              mainJson.packet = {
                mainCyc: repairCode,
                packetCode: packetCode,
                packetName: packetName,
                packetType: packetType,
                sTaskId: '',
                taskallotItemList: [
                  {
                    itemName: selectItem.checkItemName,
                    // displayItemName: selectItem.carNo ? '[辆序：' + selectItem.carNo + ']' + selectItem.checkItemName : selectItem.checkItemName,
                    displayItemName,
                    workerList: [],
                    carNo: selectItem.carNo,
                    itemCode: selectItem.id,
                    newItem: '1',
                    id: this.createId(),
                    fillType: 0,
                    xzyMTaskcarpartList: [
                      {
                        newItem: '1',
                        repairType: packetType,
                        itemCode: selectItem.id,
                        itemName: selectItem.checkItemName,
                        carNo: selectItem.carNo,
                        displayCarPartName: selectItem.carNo,
                        arrageType: '0',
                        dayPlanID: moment(this.search.bcDate).format('YYYY-MM-DD') + '-' + this.search.bcDay,
                        mainCyc: '-1',
                        taskItemId: this.createId(),
                        trainsetId: selectItem.trainsetId,
                        trainsetName: selectItem.trainsetName,
                        unitCode: this.unitCode,
                        unitName: this.unitName,
                        workerList: [],
                        xzyMTaskallotdept: {
                          deptCode: this.search.bzData,
                          deptName: workTeams.name,
                        },
                      },
                    ],
                  },
                ],
              };
              this.mainTableData.data.push(mainJson);
            }
          });
          this.reCheckedTaskdialogVisible = false;
        });
      }
    },
    removeReChecked(index, rows, pIndex, pRows) {
      // 删除复核任务
      this.$confirm('确认删除此复核任务吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          let hasNewCount = 0;
          rows.splice(index, 1);
          this.$nextTick(() => {
            rows.forEach((item) => {
              if (item.newItem == '1') {
                hasNewCount++;
              } else {
                hasNewCount--;
              }
            });
            if (hasNewCount <= 0) {
              this.mainTableData.data[pIndex].allotStateName = this.saveStatus;
            }
            if (rows.length === 0) {
              this.mainTableData.data.forEach((item, index) => {
                if (item.id == pRows.id) {
                  this.mainTableData.data.splice(index, 1);
                }
              });
            }
          });
          this.showAlertMsg('删除成功', 'success');
        })
        .catch(() => {
          this.showAlertMsg('已取消删除');
        });
    },
    // 获取复核任务和故障的按钮显示权限配置
    async getConfigList() {
      const { data } = await getConfigList();
      this.wholeConfigObj = data.reduce((prev, item, index) => {
        prev[item.paramName] = item.paramValue;
        return prev;
      }, {});
    },
    async getTaskAllotStyle(unitCode, deptCode) {
      const { data } = await getTaskAllotStyle(unitCode, deptCode);
      if (Array.isArray(data) && data.length > 0) {
        this.search.showWay = data[0].mode;
      } else {
        this.search.showWay = '1';
      }
      this.oldShowWay = this.search.showWay;
    },

    //   显示方式Change
    async showWayChange(val) {
      await setTaskAllotStyle(this.unitCode, this.search.bzData, val);
      this.$nextTick(() => {
        // this.$refs.showWayRef.blur();
        // this.changeType = 'showWay';
        this.handleSearch();
      });
    },

    // 人员选择
    // 获取岗位信息
    async getPostList() {
      const {
        data: { postList },
      } = await getPostList();
      this.postList = postList;
      this.postObj = this.postList.reduce((prev, item) => {
        prev[item.postId] = item;
        return prev;
      }, {});
    },

    // 获取人员
    async getGroup() {
      const res = await axios.post('/apiTrainRepair/xzyCAllotbranchConfig/getGroup', { deptCode: this.search.bzData });
      if (res.data.code != 1) {
        this.$message.error('获取人员失败');
      }
      const {
        data: { data, deptUserList },
      } = res;
      this.workerTeamDataPrev = data;
      this.workerTeamDataPrev.forEach((team) => {
        if (!team.perSonNelModels) {
          team.perSonNelModels = [];
        }
      });
      this.workerUserDataPrev = deptUserList;
      this.workerTeamData = JSON.parse(JSON.stringify(this.workerTeamDataPrev));
      this.workerUserData = JSON.parse(JSON.stringify(this.workerUserDataPrev));
    },
    // 打开人员分配对话框
    async openChooseWorkerDialog() {
      await this.getGroup();
      await this.getPostList();
      let workerList = [];
      let keys = [];
      if (this.multipleSelection_sub1.length == 1 && this.multipleSelection_main.length == 0) {
        this.mainTableData.data.forEach((main) => {
          if (main.packet.taskallotItemList.find((sub1) => sub1.id == this.multipleSelection_sub1[0])) {
            workerList = main.packet.taskallotItemList.find((sub1) => sub1.id == this.multipleSelection_sub1[0])
              .workerList;
          }
        });
      }

      if (
        this.multipleSelection_sub2.length != 0 &&
        this.multipleSelection_main.length == 0 &&
        this.multipleSelection_sub1.length == 0
      ) {
        this.mainTableData.data.forEach((main) => {
          main.packet.taskallotItemList.forEach((item) => {
            item.xzyMTaskcarpartList.forEach((car) => {
              if (this.multipleSelection_sub2.find((sub2) => sub2.processId == car.processId)) {
                workerList = [...workerList, ...car.workerList];
              }
            });
          });
        });
      }

      let teamKeys = [];
      this.workerTeamDataPrev.forEach((team) => {
        team.perSonNelModels.forEach((person) => {
          if (workerList.find((worker) => worker.workerID == person.workCode)) {
            teamKeys.push(person.rePairPeRSonAllotId);
          }
        });
      });
      let userKeys = [];
      this.workerUserDataPrev.forEach((user) => {
        if (workerList.find((worker) => worker.workerID == user.staffId)) {
          userKeys.push(user.staffId);
        }
      });

      keys = [...teamKeys, ...userKeys];
      this.chooseWorkerVisible = true;

      this.$nextTick().then(() => {
        this.$refs.workersTree.setCheckedKeys(keys);
      });
    },
    // 关闭人员分配对话框
    closeChooseWorkerDialog() {
      this.quickQueryWorkersText = '';
      this.contextmenuWrapStatus = false;
      // this.workersTreeCheckedNodes = [];
      this.$refs.workersTree.setCheckedKeys([]);
    },
    // 快速搜索
    quickQueryWorkers(val, data) {
      if (!val) return true;
      if (data.workerFlag == 1) {
        return data.label.indexOf(val) !== -1;
      }
    },
    //  右键树形
    workersTreeRightClick(e, data, node) {
      this.axis = {
        x: e.x,
        y: e.y,
      };
      this.contextmenuWrapStatus = false;
      // 判断
      // 1、如果什么都没选，只能小组右键，右键小组位置，出现小组菜单，小组只能一个一个处理，不支持批量
      if (this.workersTreeCheckedNodes.length === 0) {
        if (data.workerFlag == 2) {
          // 右键的班组
          this.contextMenuList = this.workTeamMenuList;
        } else if (data.workerFlag == 0) {
          // 右键的小组
          this.nowTreeRowId = data.id;
          this.contextMenuList = this.teamMenuList;
        } else {
          return;
        }
      } else {
        // 只选了小组
        let chooseTeamOnly = this.workersTreeCheckedNodes.every((item) => item.workerFlag == 0);
        if (chooseTeamOnly) {
          // 右键的小组
          this.nowTreeRowId = this.workersTreeCheckedNodes[0].id;
          this.contextMenuList = this.teamMenuList;
        } else {
          // 选了人
          //   选择的人员列表（去除小组）
          this.workersTreeCheckedNodes = this.workersTreeCheckedNodes.filter((item) => item.workerFlag == 1);

          if (
            this.workersTreeCheckedNodes.some((item) => {
              return item.partTime == 1;
            })
          ) {
            this.contextMenuList = this.workerMenuList;
          } else {
            this.contextMenuList = this.workerMenuList.filter((menu) => {
              return menu.label != '删除人员兼职';
            });
          }

          if (
            this.workersTreeCheckedNodes.some((item) => {
              return item.postIds && item.postIds.length > 0;
            })
          ) {
            this.contextMenuList = this.contextMenuList;
          } else {
            this.contextMenuList = this.contextMenuList.filter((menu) => {
              return menu.label != '删除岗位';
            });
          }
          if (this.workersTreeCheckedNodes.length == 1) {
            if (this.workersTreeCheckedNodes[0].postIds && this.workersTreeCheckedNodes[0].postIds.length > 0) {
              if (this.workersTreeCheckedNodes[0].postIds) {
                this.selectPost = this.workersTreeCheckedNodes[0].postIds.map((item) => {
                  return {
                    value: item,
                  };
                });
              }
            }
          }
        }
      }
      this.contextMenuText = [];
      this.contextmenuWrapStatus = true;
    },
    // 左键树形
    workersTreeNodeClick() {
      this.contextmenuWrapStatus = false;
    },
    // 选择的树形
    workersTreeCheckTree(data, isChecked, childChecked) {
      this.contextmenuWrapStatus = false;
      this.workersTreeCheckedNodes = this.$refs.workersTree.getCheckedNodes();
      this.checkPeople = [];
      this.workersTreeCheckedNodes.forEach((check) => {
        if (check.workerFlag == 1) {
          // 先判断这个是组内还是组外的人
          let isUser = this.workerUserData.find((item) => item.staffId == check.id);
          if (isUser) {
            this.checkPeople.push({
              workerID: check.workCode,
              workerName: check.label,
              taskAllotPersonPostEntityList: JSON.parse(JSON.stringify(isUser.postModelList)),
            });
          } else {
            for (let i = 0; i < this.workerTeamData.length; i++) {
              const team = this.workerTeamData[i];
              isUser = team.perSonNelModels.find((person) => {
                return person.rePairPeRSonAllotId === check.id;
              });
              if (isUser) break;
            }

            this.checkPeople.push({
              workerID: check.workCode,
              workerName: check.label,
              taskAllotPersonPostEntityList: JSON.parse(JSON.stringify(isUser.postModelList)),
            });
          }
        }
      });
      // 去重人员并合并去重岗位
      let personArr = [];
      this.checkPeople.forEach((people) => {
        const person = personArr.find((person) => person.workerID == people.workerID);
        if (person) {
          person.taskAllotPersonPostEntityList = [
            ...person.taskAllotPersonPostEntityList,
            ...people.taskAllotPersonPostEntityList,
          ];
          const map = person.taskAllotPersonPostEntityList.reduce((prev, item) => {
            if (!prev.has(item.postId)) {
              prev.set(item.postId, {
                postId: item.postId,
                postName: item.postName,
              });
            }
            return prev;
          }, new Map());
          person.taskAllotPersonPostEntityList = [...map.values()];
        } else {
          personArr.push(people);
        }
      });
      this.checkPeople = personArr;
    },
    // 增加小组
    addGroup() {
      this.groupName = '';
      this.groupDialogVisible = true;
      this.groupStatus = 'add';
    },
    // 编辑小组
    editGroup() {
      this.workerTeamData.forEach((item) => {
        if (item.branchCode == this.nowTreeRowId) {
          this.groupName = item.branchName;
        }
      });
      this.groupDialogVisible = true;
      this.groupStatus = 'edit';
    },
    // 确定小组名称
    confirmGroupName() {
      let groupName = this.groupName.trim();
      if (!groupName) {
        this.$message.warning('小组名称不能为空!');
        return;
      } else {
        let only = this.workerTeamData.some((item) => {
          return item.branchName == groupName;
        });

        if (only) {
          this.$message.warning('该小组名称已存在!');
        } else {
          console.log(groupName);
          if (this.groupStatus == 'add') {
            this.workerTeamData.push({
              branchCode: Math.random().toString(36).substr(2),
              branchName: groupName,
              perSonNelModels: [],
            });
            this.$message.success('添加小组成功');
          } else if (this.groupStatus == 'edit') {
            this.workerTeamData.forEach((item) => {
              if (item.branchCode == this.nowTreeRowId) {
                item.branchName = groupName;
              }
            });
          }

          this.groupDialogVisible = false;
        }
      }
    },
    // 清空小组
    async clearGroup() {
      const confirmRes = await this.$confirm('确认清空此小组?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      });
      if (confirmRes == 'confirm') {
        this.workerTeamData.forEach((item) => {
          if (item.branchCode == this.nowTreeRowId) {
            item.perSonNelModels.forEach((person) => {
              // 先判断组外有没有这个人
              let hasUser = this.workerUserData.find((item) => item.staffId == person.workCode);
              if (!hasUser) {
                this.workerUserData.push({
                  staffId: person.workCode,
                  name: person.workName,
                  postModelList: '',
                });
              }
            });
          }
        });

        this.workerTeamData.forEach((item) => {
          if (item.branchCode == this.nowTreeRowId) {
            item.perSonNelModels = [];
          }
        });
        this.$message.success('清空小组成功');
      } else {
        this.$message.info('已取消清空');
      }
    },
    // 删除小组
    async deleteGroup() {
      const confirmRes = await this.$confirm('确认删除此小组?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      });
      if (confirmRes == 'confirm') {
        this.workerTeamData.forEach((item) => {
          if (item.branchCode == this.nowTreeRowId) {
            item.perSonNelModels.forEach((person) => {
              // 先判断组外有没有这个人
              let hasUser = this.workerUserData.find((item) => item.staffId == person.workCode);
              if (!hasUser) {
                this.workerUserData.push({
                  staffId: person.workCode,
                  name: person.workName,
                  postModelList: '',
                });
              }
            });
          }
        });
        this.workerTeamData = this.workerTeamData.filter((item) => item.branchCode !== this.nowTreeRowId);
        this.$message.success('删除小组成功');
      } else {
        this.$message.info('已取消删除');
      }
    },
    // 上移小组
    upGroup() {
      // 先删再插
      let idx = 0;
      let workerTeamItem = {};
      this.workerTeamData.forEach((item, index) => {
        if (item.branchCode == this.nowTreeRowId) {
          idx = index;
          workerTeamItem = item;
        }
      });
      if (idx == 0) return;
      this.workerTeamData.splice(idx, 1);
      this.workerTeamData.splice(idx - 1, 0, workerTeamItem);
    },
    // 下移小组
    downGroup() {
      // 先删再插
      let idx = 0;
      let workerTeamItem = {};
      this.workerTeamData.forEach((item, index) => {
        if (item.branchCode == this.nowTreeRowId) {
          idx = index;
          workerTeamItem = item;
        }
      });
      if (idx == this.workerTeamData.length - 1) return;
      this.workerTeamData.splice(idx, 1);
      this.workerTeamData.splice(idx + 1, 0, workerTeamItem);
    },

    // 对人员操作
    // 移动
    moveWorker(data) {
      if (data.children) return;
      let chooseIdList = this.workersTreeCheckedNodes.map((item) => item.id);
      // 小组的id
      let targetTeamId = data.value;
      chooseIdList.forEach((chooseItem) => {
        // 先判断这个是组内还是组外的人
        let isUser = this.workerUserData.find((item) => item.staffId == chooseItem);

        if (isUser) {
          // 组外
          this.workerUserData = this.workerUserData.filter((item) => item.staffId != chooseItem);
          this.workerTeamData.forEach((teamItem, teamIndex) => {
            if (teamItem.branchCode == targetTeamId) {
              // 判断这个人是否已经兼职在此
              let alReadyPart = teamItem.perSonNelModels.find((item) => item.workCode == chooseItem);
              if (!alReadyPart) {
                teamItem.perSonNelModels.push({
                  rePairPeRSonAllotId: Math.random().toString(36).substr(2),
                  workName: isUser.name,
                  postModelList: isUser.postModelList ? isUser.postModelList : [],
                  partTime: isUser.partTime ? isUser.partTime : '0',
                  workCode: isUser.staffId,
                });
              } else {
                alReadyPart.partTime = '0';
              }
            }
          });
        } else {
          // 组内
          // 先拿到这个人的信息
          let nowWorker;
          this.workerTeamData.forEach((teamItem, teamIndex) => {
            nowWorker = teamItem.perSonNelModels.find((item) => item.rePairPeRSonAllotId == chooseItem) || nowWorker;
          });

          this.workerTeamData.forEach((teamItem, teamIndex) => {
            teamItem.perSonNelModels = teamItem.perSonNelModels.filter(
              (workerItem) => workerItem.rePairPeRSonAllotId != chooseItem
            );
          });
          this.workerTeamData.forEach((teamItem, teamIndex) => {
            if (teamItem.branchCode == targetTeamId) {
              // 判断这个人是否已经在此或兼职在此
              let alReadyPart = teamItem.perSonNelModels.find((item) => item.workCode == nowWorker.workCode);
              if (!alReadyPart) {
                teamItem.perSonNelModels.push({
                  rePairPeRSonAllotId: Math.random().toString(36).substr(2),
                  workName: nowWorker.workName,
                  postModelList: nowWorker.postModelList ? nowWorker.postModelList : [],
                  partTime: nowWorker.partTime,
                  workCode: nowWorker.workCode,
                });
              } else {
                // 如果已在兼职
                // 且移动的是本人
                if (nowWorker.partTime == '0') {
                  alReadyPart.partTime = '0';
                }
              }
            }
          });
        }
      });
    },
    // 移出小组
    outGroup() {
      let chooseIdList = this.workersTreeCheckedNodes.map((item) => item.id);
      chooseIdList.forEach((chooseItem) => {
        // 先判断这个是组内还是组外的人
        let isUser = this.workerUserData.find((item) => item.staffId == chooseItem);
        if (!isUser) {
          // 组内
          // 先拿到这个人
          let nowWorker;
          this.workerTeamData.forEach((teamItem, teamIndex) => {
            nowWorker = teamItem.perSonNelModels.find((item) => item.rePairPeRSonAllotId == chooseItem) || nowWorker;
          });
          // 先删
          this.workerTeamData.forEach((teamItem, teamIndex) => {
            teamItem.perSonNelModels = teamItem.perSonNelModels.filter(
              (workerItem, workIndex) => workerItem.rePairPeRSonAllotId != chooseItem
            );
          });
          // 后插
          this.workerUserData.push({
            staffId: nowWorker.workCode,
            name: nowWorker.workName,
            postModelList: '',
          });
        }
      });
    },
    // 人员上移
    upWorker() {
      let chooseIdList = this.workersTreeCheckedNodes.map((item) => item.id);
      chooseIdList.forEach((chooseItem) => {
        // 先判断这个是组内还是组外的人
        let isUser = this.workerUserData.find((item) => item.staffId == chooseItem);
        let teamIdx = 0;
        let idx = 0;
        let moveItem = {};
        if (isUser) {
          // 组外
          // 先删再插
          this.workerUserData.forEach((item, index) => {
            if (item.staffId == chooseItem) {
              idx = index;
              moveItem = item;
            }
          });
          if (idx == 0) return;

          this.workerUserData.splice(idx, 1);
          this.workerUserData.splice(idx - 1, 0, moveItem);
        } else {
          this.workerTeamData.forEach((teamItem, teamIndex) => {
            teamItem.perSonNelModels.forEach((workerItem, workIndex) => {
              if (workerItem.rePairPeRSonAllotId == chooseItem) {
                teamIdx = teamIndex;
                idx = workIndex;
                moveItem = workerItem;
              }
            });
          });
          if (idx == 0) return;
          this.workerTeamData[teamIdx].perSonNelModels.splice(idx, 1);
          this.workerTeamData[teamIdx].perSonNelModels.splice(idx - 1, 0, moveItem);
        }
      });
    },
    // 人员下移
    downWorker() {
      let chooseIdList = this.workersTreeCheckedNodes.map((item) => item.id).reverse();
      chooseIdList.forEach((chooseItem, chooseIndex) => {
        // 先判断这个是组内还是组外的人
        let isUser = this.workerUserData.find((item) => item.staffId == chooseItem);
        let teamIdx = 0;
        let idx = 0;
        let moveItem = {};
        if (isUser) {
          // 组外
          // 先删再插
          this.workerUserData.forEach((item, index) => {
            if (item.staffId == chooseItem) {
              idx = index;
              moveItem = item;
            }
          });
          if (idx == this.workerUserData.length - 1) return;
          this.workerUserData.splice(idx, 1);
          this.workerUserData.splice(idx + 1, 0, moveItem);
        } else {
          this.workerTeamData.forEach((teamItem, teamIndex) => {
            teamItem.perSonNelModels.forEach((workerItem, workIndex) => {
              if (workerItem.rePairPeRSonAllotId == chooseItem) {
                teamIdx = teamIndex;
                idx = workIndex;
                moveItem = workerItem;
              }
            });
          });
          if (idx == this.workerTeamData[teamIdx].perSonNelModels.length - 1) return;
          this.workerTeamData[teamIdx].perSonNelModels.splice(idx, 1);
          this.workerTeamData[teamIdx].perSonNelModels.splice(idx + 1, 0, moveItem);
        }
      });
    },
    // 人员兼职
    partWorker(data) {
      let chooseIdList = this.workersTreeCheckedNodes.map((item) => item.id);
      // 小组的id
      let targetTeamId = data.value;
      chooseIdList.forEach((chooseItem) => {
        // 先判断这个是组内还是组外的人
        let isUser = this.workerUserData.find((item) => item.staffId == chooseItem);
        if (isUser) {
          // 组外
          this.workerTeamData.forEach((teamItem, teamIndex) => {
            if (teamItem.branchCode == targetTeamId) {
              // 判断这个人是否已经兼职在此
              let alReadyPart = teamItem.perSonNelModels.find((item) => item.workCode == chooseItem);
              if (!alReadyPart) {
                teamItem.perSonNelModels.push({
                  rePairPeRSonAllotId: Math.random().toString(36).substr(2),
                  workName: isUser.name,
                  postModelList: isUser.postModelList ? JSON.parse(JSON.stringify(isUser.postModelList)) : [],
                  partTime: '1',
                  workCode: isUser.staffId,
                });
              }
            }
          });
        } else {
          // 组内
          // 先拿到这个人的信息
          let nowWorker;
          this.workerTeamData.forEach((teamItem, teamIndex) => {
            nowWorker = teamItem.perSonNelModels.find((item) => item.rePairPeRSonAllotId == chooseItem) || nowWorker;
          });
          this.workerTeamData.forEach((teamItem, teamIndex) => {
            if (teamItem.branchCode == targetTeamId) {
              // 判断这个人是否已经在此或兼职在此
              let alReadyPart = teamItem.perSonNelModels.find((item) => item.workCode == nowWorker.workCode);
              if (!alReadyPart) {
                teamItem.perSonNelModels.push({
                  rePairPeRSonAllotId: Math.random().toString(36).substr(2),
                  workName: nowWorker.workName,
                  postModelList: nowWorker.postModelList ? JSON.parse(JSON.stringify(nowWorker.postModelList)) : [],
                  partTime: '1',
                  workCode: nowWorker.workCode,
                });
              }
            }
          });
        }
      });
    },
    // 删除人员兼职
    delPartWorker() {
      let chooseIdList = this.workersTreeCheckedNodes
        .map((item) => {
          return item.partTime == '1' && item.id;
        })
        .filter((item) => item);
      chooseIdList.forEach((chooseItem) => {
        // 肯定是是组内人
        let teamIdx = 0;
        let idx = 0;
        this.workerTeamData.forEach((teamItem, teamIndex) => {
          teamItem.perSonNelModels.forEach((workerItem, workIndex) => {
            if (workerItem.rePairPeRSonAllotId == chooseItem) {
              teamIdx = teamIndex;
              idx = workIndex;
            }
          });
        });
        this.workerTeamData[teamIdx].perSonNelModels.splice(idx, 1);
      });
    },
    // 选择岗位
    choosePostion(list) {
      // let notHasThisId = false;

      if (this.postList.length === 0) return;
      let chooseIdList = this.workersTreeCheckedNodes.map((item) => item.id);

      // let targetPostId = data.value;

      chooseIdList.forEach((chooseItem) => {
        // 先判断这个是组内还是组外的人
        let isUser = this.workerUserData.find((item) => item.staffId == chooseItem);

        if (isUser) {
          // 组外

          this.workerUserData.forEach((item, index) => {
            if (item.staffId == chooseItem) {
              item.postModelList = list.map((itm) => {
                return this.postObj[itm.value];
              });
              // if (!item.postModelList) {
              //   // notHasThisId = true;
              //   item.postModelList = [this.postObj[targetPostId]];
              // } else {
              //   if (
              //     !item.postModelList.find(
              //       (postItem) => postItem.postId == targetPostId
              //     )
              //   ) {
              //     // notHasThisId = true;
              //     item.postModelList.push(this.postObj[targetPostId]);
              //   }
              // }
            }
          });
        } else {
          this.workerTeamData.forEach((teamItem, teamIndex) => {
            teamItem.perSonNelModels.forEach((workerItem, workIndex) => {
              if (workerItem.rePairPeRSonAllotId == chooseItem) {
                workerItem.postModelList = list.map((itm) => {
                  return this.postObj[itm.value];
                });
                // if (!workerItem.postModelList) {
                //   // notHasThisId = true;
                //   workerItem.postModelList = [this.postObj[targetPostId]];
                // } else {
                //   if (
                //     !workerItem.postModelList.find(
                //       (postItem) => postItem.postId == targetPostId
                //     )
                //   ) {
                //     // notHasThisId = true;

                //     workerItem.postModelList.push(this.postObj[targetPostId]);
                //   }
                // }
              }
            });
          });
        }
      });
      // if (notHasThisId) {
      // this.$message.success('岗位选择成功');
      // }
    },
    // 删除岗位
    delPositon() {
      let chooseIdList = this.workersTreeCheckedNodes.map((item) => item.id);
      chooseIdList.forEach((chooseItem) => {
        // 先判断这个是组内还是组外的人
        let isUser = this.workerUserData.find((item) => item.staffId == chooseItem);
        if (isUser) {
          this.workerUserData.forEach((item, index) => {
            if (item.staffId == chooseItem) {
              item.postModelList = '';
            }
          });
        } else {
          // 组内
          this.workerTeamData.forEach((teamItem, teamIndex) => {
            teamItem.perSonNelModels.forEach((workerItem, workIndex) => {
              if (workerItem.rePairPeRSonAllotId == chooseItem) {
                workerItem.postModelList = [];
              }
            });
          });
        }
      });
    },
    select(item) {
      if (item.action == 'choosePostion') {
        return;
      }
      this.contextmenuWrapStatus = false;
      this.contextmenuWrapStatus = false;
      if (item.action) {
        this[item.action](item);
      } else {
        this[item.value](item);
      }
      this.workersTreeCheckedNodes = [];
      this.$nextTick(() => {
        this.$refs.workersTree.setCheckedKeys([]);
      });
    },
    postChange(val) {
      this.selectPost = val;
    },
    tableFilter(data) {
      let tableData = data;
      const { trainsetTypeCode, trainsetId, workTypeCode, workTaskName, allotStateCode } = this.search;
      tableData = tableData.filter((item) => {
        if (!trainsetTypeCode) {
          return true;
        } else {
          return item.trainsetTypeCode == trainsetTypeCode;
        }
      });

      tableData = tableData.filter((item) => {
        if (!trainsetId) {
          return true;
        } else {
          return item.trainsetId == trainsetId;
        }
      });
      // 作业类型 有特殊处理不再此处过滤
      // tableData = tableData.filter((item) => {
      //   if (!workTypeCode) {
      //     return true;
      //   } else {
      //     return item.workTypeCode == workTypeCode;
      //   }
      // });

      tableData = tableData.filter((item) => {
        if (!workTaskName) {
          return true;
        } else {
          return item.workTaskName == workTaskName;
        }
      });

      tableData = tableData.filter((item) => {
        if (allotStateCode.length === 0) {
          return true;
        } else {
          return allotStateCode.includes(item.allotStateCode);
        }
      });
      this.$nextTick().then(() => {
        this.$refs.maintableRef.sort('trainsetName', 'ascending');
      });
      return tableData;
    },

    checkSelectable1(row) {
      let list = [];
      row.packet.taskallotItemList.forEach((item) => {
        if (item.fillType == 1) {
          list.push(item);
        }
      });

      if (list.length == row.packet.taskallotItemList.length) {
        return false;
      } else {
        return true;
      }
    },

    checkSelectable2(row) {
      if (row.fillType == 1) {
        return false;
      } else {
        return true;
      }
    },

    checkSelectable3(row) {
      if (row.fillType == 1) {
        return false;
      } else {
        return true;
      }
    },
    // 切换班次联动数据
    searchBcDate() {
      // 清空车型、车组和作业任务
      this._getBcData(
        this.unitCode,
        moment(this.search.bcDate).format('YYYY-MM-DD') + '-' + this.search.bcDay,
        this.search.bzData,
        '',
        this.search.showWay
        )
    },
    // 切换班次白班或夜班
    searchBcDayDate() {
      this._getBcData(
        this.unitCode,
        moment(this.search.bcDate).format('YYYY-MM-DD') + '-' + this.search.bcDay,
        this.search.bzData,
        '',
        this.search.showWay)
    },
    // 切换班次请求联动是数据
    _getBcData(unitCode, dayplanId, deptCode, taskAllotType, mode) {
      this.loading = true
      axios(getBcData({
          params: {
            unitCode,
            dayplanId,
            deptCode,
            taskAllotType,
            mode,
          },
        }))
          .then((res) => {
            this.loading = false
            if (res.data.code === 1) {
              // 清除相应的条件数据
              this.search.allotStateCode = []
              this.search.trainsetTypeCode = ''
              this.search.workTypeCode = ''
              this.search.workTaskName = ''
              this.search.trainsetId = ''
              // 给相应的数组赋值
              let rows = res.data.data;
              let trainTypeList = [];
              let trainsetList = [];
              let taskList = [];

            rows.queryList.forEach((item, index) => {
              let json = {};
              json.trainsetTypeName = item.trainsetTypeName;
              json.trainsetTypeCode = item.trainsetTypeCode;
              trainTypeList.push(json);
              if (item.queryTrainsetList) {
                item.queryTrainsetList.forEach((el, i) => {
                  let trainsetJson = {};
                  trainsetJson.trainsetName = el.trainsetName;
                  trainsetJson.trainsetId = el.trainsetId;
                  trainsetJson.trainType = item.trainsetTypeCode;
                  trainsetList.push(trainsetJson);
                  if (el.queryWorkTaskList) {
                    el.queryWorkTaskList.forEach((n, v) => {
                      let taskJson = {};
                      taskJson.workTaskName = n.workTaskName;
                      taskJson.workTaskCode = n.workTaskCode;
                      taskList.push(taskJson);
                    });
                  }
                });
              }
            });
            //车型赋值
            this.search.trainTypeList = this.uniqueCommon(trainTypeList, 'trainsetTypeCode');
            //车组赋值
            let newtrainsetList = this.uniqueCommon(trainsetList, 'trainsetId').sort((a, b) => {
              if (a.trainsetId > b.trainsetId) {
                return 1;
              }
            });
            this.originTrainsetList = newtrainsetList;
            this.search.trainGroupList = newtrainsetList;
            //作业任务赋值
            this.search.worktaskList = this.uniqueCommon(taskList, 'workTaskName');
            this.search.worktypeList = rows.taskAllotTypeDict;
            this.search.workStatusList = rows.allotTypeDict;
            } else {
              let msg = res.data.msg || '班次获取失败!';
              this.showAlertMsg(msg);
            }
          })
          .catch(({ response }) => {
            this.loading = false;
          })
    },
    getWorkTypeCode(workTypeCode) {
      if(!workTypeCode) return
      let workTypeName = null
      this.search.worktypeList.forEach((worktype) => {
        if(workTypeCode == worktype.sTaskallottypecode) {
          workTypeName = worktype.sTaskallottypename
        }
      })
      return workTypeName
    }
  },
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('检修派工'),
    contextmenuWrapStyle() {
      let x = this.axis.x;
      let y = this.axis.y;
      // 判断menu距离浏览器可视窗口底部距离，一面距离底部太近的时候，会导致menu被遮挡
      let menuHeight = this.contextMenuList.length * 32; //不能用scrollHeight，获取到的menu是上一次的menu宽高
      let menuWidth = 150; //不能用scrollWidth,同理
      return {
        left: (document.body.clientWidth < x + menuWidth ? x - menuWidth : x) + 'px',
        top: (document.body.clientHeight < y + menuHeight ? y - menuHeight : y) + 'px',
      };
    },

    // 班组信息名称
    workTeamInfoMap({ search }) {
      let workTeamInfoMap = {};
      search.bzDataList.forEach((item, index) => {
        if (item.id == this.search.bzData) {
          workTeamInfoMap = item;
        }
      });
      return workTeamInfoMap;
    },

    workersTreeData({ workTeamInfoMap, workerTeamData, workerUserData }) {
      let workerTeamTreeData = workerTeamData.map((teamItem) => {
        let children = teamItem.perSonNelModels
          ? teamItem.perSonNelModels.map((workerItem) => {
            return {
              id: workerItem.rePairPeRSonAllotId,
              label: workerItem.workName,
              workerFlag: 1,
              postIds:
                workerItem.postModelList && workerItem.postModelList.length > 0
                  ? workerItem.postModelList.map((postItem) => postItem.postId)
                  : '',
              post:
                workerItem.postModelList && workerItem.postModelList.length > 0
                  ? `(${workerItem.postModelList.map((postItem) => postItem.postName).toString()})`
                  : '',
              partTime: workerItem.partTime ? workerItem.partTime : '0',
              workCode: workerItem.workCode,
            };
          })
          : [];
        return {
          id: teamItem.branchCode,
          label: teamItem.branchName,
          children,
          workerFlag: 0,
        };
      });

      let workerUserTreeData = workerUserData.map((workerItem) => {
        return {
          id: workerItem.staffId,
          label: workerItem.name,
          workerFlag: 1,
          postIds:
            workerItem.postModelList && workerItem.postModelList.length > 0
              ? workerItem.postModelList.map((postItem) => postItem.postId)
              : '',
          post:
            workerItem.postModelList && workerItem.postModelList.length > 0
              ? `(${workerItem.postModelList.map((postItem) => postItem.postName).toString()})`
              : '',
          partTime: workerItem.partTime ? workerItem.partTime : '0',
          workCode: workerItem.staffId,
        };
      });

      return [
        {
          id: workTeamInfoMap.id,
          label: workTeamInfoMap.name,
          children: [...workerTeamTreeData, ...workerUserTreeData],
          workerFlag: 2,
        },
      ];
    },

    workerMenuList({ workerTeamData }) {
      return [
        {
          icon: 'el-icon-tickets',
          label: '人员移动',
          value: 'moveWorker',
          children: workerTeamData.map((item) => {
            return {
              label: item.branchName,
              value: item.branchCode,
              action: 'moveWorker',
            };
          }),
        },
        {
          icon: 'el-icon-error',
          label: '移出小组',
          value: 'outGroup',
        },
        {
          icon: 'el-icon-sort-up',
          label: '人员上移',
          value: 'upWorker',
        },
        {
          icon: 'el-icon-sort-down',
          label: '人员下移',
          value: 'downWorker',
        },
        {
          icon: 'el-icon-view',
          label: '人员兼职',
          value: 'partWorker',
          children: workerTeamData.map((item) => {
            return {
              label: item.branchName,
              value: item.branchCode,
              action: 'partWorker',
            };
          }),
        },
        {
          icon: 'el-icon-remove-outline',
          label: '删除人员兼职',
          value: 'delPartWorker',
        },
        {
          icon: 'el-icon-postcard',
          label: '选择岗位',
          value: 'choosePostion',
          children: this.postList.map((item) => {
            return {
              label: item.postName,
              value: item.postId,
              action: 'choosePostion',
              multiple: true,
            };
          }),
        },
        {
          icon: 'el-icon-close',
          label: '删除岗位',
          value: 'delPositon',
        },
      ];
    },

    // 分页
    nowPageTableList({ tableDataFilter, mainTableData, search }) {
      // 筛选出分页数据
      let tableData = tableDataFilter.slice(
        (mainTableData.currentPage - 1) * mainTableData.pageSize,
        mainTableData.currentPage * mainTableData.pageSize
      )
      // 筛选出符合条件的数据
      return tableData.filter((data) => {
        if (
          (search.trainsetTypeCode ? data.trainsetTypeCode == search.trainsetTypeCode : true) &&
          (search.trainsetId ? data.trainsetId == search.trainsetId : true) &&
          (search.workTypeCode ? this.getWorkTypeCode(search.workTypeCode) == data.workTypeName : true) &&
          (search.workTaskName ? data.workTaskName == search.workTaskName : true) &&
          (search.allotStateCode && search.allotStateCode.length > 0 ? (search.allotStateCode.indexOf(data.allotStateCode) == -1 ? false: true)  : true)
          ) return true
      });
    },
    // 过滤已经认领的故障
    faultFilterDone({ faultTask, mainTableData }) {
      // 先排除已经认领的
      let faultTaskData = copyData(faultTask.tableData.data);
      let s_proArr = [];
      let mainData = mainTableData.data;
      for (let i = 0; i < mainData.length; i++) {
        if (mainData[i].workTypeCode == 5) {
          if (mainData[i].packet.taskallotItemList) {
            let s_pro = mainData[i].packet.taskallotItemList;
            for (let j = 0; j < s_pro.length; j++) {
              s_proArr.push(s_pro[j]);
            }
          }
        }
      }

      s_proArr = this.uniqueCommon(s_proArr, 'id');
      for (let x = 0; x < s_proArr.length; x++) {
        for (let j = 0; j < faultTaskData.length; j++) {
          if (s_proArr[x].itemCode == faultTaskData[j].faultId && s_proArr[x].carNo == faultTaskData[j].carNo) {
            faultTaskData.splice(j, 1);
          }
        }
      }

      faultTaskData && faultTaskData.sort((a, b) => a.trainSetName.localeCompare(b.trainSetName));
      return faultTaskData;
    },

    faultFilterTrainsetList({ faultFilterDone }) {
      let list = [];
      faultFilterDone &&
        faultFilterDone.forEach((item) => {
          if (list.findIndex((itm) => itm.id === item.trainSetId) === -1) {
            list.push({
              id: item.trainsetId,
              value: item.trainsetId,
              label: item.trainsetName,
            });
          }
        });

      list.sort((a, b) => a.label.localeCompare(b.label));
      return list;
    },
    // 根据车组号过滤
    faultDataFilter({ faultFilterDone, faultFilterTrainsetId }) {
      if (!faultFilterTrainsetId) return faultFilterDone;
      return faultFilterDone.filter((item) => item.trainsetId === faultFilterTrainsetId);
    },
    // 前台分页后
    faultDataShow({ faultTask, faultDataFilter }) {
      return faultDataFilter.slice(
        (faultTask.tableData.currentPage - 1) * faultTask.tableData.pageSize,
        faultTask.tableData.currentPage * faultTask.tableData.pageSize
      );
    },

    // 过滤已经认领的复核
    reCheckFilterDone({ reCheck, mainTableData }) {
      // 先排除已经认领的
      let data = copyData(reCheck);
      let s_proArr = [];
      let mainData = mainTableData.data;
      for (let i = 0; i < mainData.length; i++) {
        if (mainData[i].workTypeCode == 11) {
          if (mainData[i].packet.taskallotItemList) {
            let s_pro = mainData[i].packet.taskallotItemList;
            for (let j = 0; j < s_pro.length; j++) {
              s_proArr.push(s_pro[j]);
            }
          }
        }
      }

      // s_proArr = this.uniqueCommon(s_proArr, 'id');
      for (let x = 0; x < s_proArr.length; x++) {
        for (let j = 0; j < data.length; j++) {
          if (s_proArr[x].itemCode == data[j].id && s_proArr[x].carNo == data[j].carNo) {
            data.splice(j, 1);
          }
        }
      }

      data.sort((a, b) => a.trainsetName.localeCompare(b.trainsetName));
      return data;
    },

    reCheckFilterTrainsetList({ reCheckFilterDone }) {
      let list = [];
      reCheckFilterDone.forEach((item) => {
        if (list.findIndex((itm) => itm.id === item.trainsetId) === -1) {
          list.push({
            id: item.trainsetId,
            value: item.trainsetId,
            label: item.trainsetName,
          });
        }
      });

      list.sort((a, b) => a.label.localeCompare(b.label));
      return list;
    },
    // 根据条件过滤
    recheckDataFilter({ reCheckFilterDone, recheckFilterTrainsetId, recheckFilterItem }) {
      let data = copyData(reCheckFilterDone);
      if (recheckFilterTrainsetId) {
        data = reCheckFilterDone.filter((item) => item.trainsetId === recheckFilterTrainsetId);
      }
      if (recheckFilterItem) {
        data = reCheckFilterDone.filter((item) => item.checkItemName.includes(recheckFilterItem));
      }
      return data;
    },
    // 前台分页后
    recheckDataShow({ reCheckedTaskTablePageNum, reCheckedTaskTablePageSize, recheckDataFilter }) {
      return recheckDataFilter.slice(
        (reCheckedTaskTablePageNum - 1) * reCheckedTaskTablePageSize,
        reCheckedTaskTablePageNum * reCheckedTaskTablePageSize
      );
    },

    traintypeTrainsetidMap({ trainsetAll }) {
      return trainsetAll.reduce((prev, item) => {
        prev[item.trainsetid] = item.traintype;
        return prev;
      }, {});
    },
  },
  watch: {
    'chooseWorkers.quickQuery': function (val) {
      if (val == '') {
        this.$refs.tree.filter(val);
        this.$refs.tree.setCheckedKeys([]);
        this.$refs.tree.setCheckedNodes(this.pCheckNodes);
      } else {
        this.$refs.tree.filter(val);
      }
    },
    quickQueryWorkersText(val) {
      this.$refs.workersTree.filter(val);
    },
    contextmenuWrapStatus(val) {
      if (!val) {
        if (this.selectPost.length > 0) {
          this.choosePostion(this.selectPost);
        }

        this.selectPost = [];
      }
    },
    'mainTableData.data': {
      handler(val) {
        this.tableDataFilter = this.tableFilter(val);
      },
      deep: true,
    },
  },
  components: {},
  filters: {
    //转时间格式为: YYYY-MM-DD
    dataTimeFilter: function (val) {
      if (val == '') return;
      var date = val.replace(/^(\d{4})(\d{2})(\d{2})$/, '$1-$2-$3');
      return date;
    },
    //实现千分位
    str2thousand: function (val) {
      if (val) {
        val = val.toString();
        return val == '' ? val : val.replace(/\d{1,3}(?=(\d{3})+$)/g, '$&,');
      } else {
        return '';
      }
    },

    // 班次
    workTimesFilter(val) {
      let list = val.split('-');
      let shift = list.splice(list.length - 1, list.length);
      list.push(shift[0] == '00' ? '白班' : '黑班');
      return list.join('-');
    },
  },
  directives: {
    focus: {
      inserted: function (el) {
        $(el).find('.el-input__inner').focus();
      },
      updata(el) {
        $(el).find('.el-input__inner').focus();
      },
    },
  },
});
