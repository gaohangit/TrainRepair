var repairDispatchQuery = new Vue({
  el: '#app',
  data() {
    return {
      unitCode: '',
      unitName: '',
      saveFaultTaskData: [],
      saveStatus: '未派工',
      contentStyle: {
        height: '',
        'overflow-y': 'auto',
      },
      loading: false,
      table: {
        data: [
          { aaaa: 111, bbbb: 222 },
          { aaaa: 111, bbbb: 222 },
          { aaaa: 111, bbbb: 222 },
        ],
        scrollHeight: '480px',
        currentPage: 1,
        pageSize: 5,
        total: 1,
      },
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
        trainGroupList: [
          {
            queryTrainsetName: 'CRH2A-2010',
            queryTrainsetId: 'CRH2A-2010',
          },
          {
            queryTrainsetName: 'CRH2A-2012',
            queryTrainsetId: 'CRH2A-2012',
          },
        ],
        workTypeCode: '', //作业类型
        worktypeList: [],
        workTaskCode: '', //作业任务
        worktaskList: [
          {
            queryWorkTaskName: '一级修(01-08)',
            workTaskCode: 1,
          },
          {
            queryWorkTaskName: '一级修(09-00)',
            workTaskCode: 2,
          },
        ],
        trainNo: '', //辆序
        trainNoList: [],
        allotStateCode: '', //派工状态
        workStatusList: [],
      },
      mainTableData: {
        data: [],
        scrollHeight: '480px',
        currentPage: 1,
        pageSize: 100,
        total: 1,
      },
      copymainTableData: [], //复制mainTableData中的data
      initmainTableData: [],
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
      treeData: [],
      defaultProps: {
        children: 'children',
        label: 'label',
      },
      multipleSelection_main: [],
      multipleSelection_sub1: [],
      multipleSelection_sub2: [],
      user: {
        unitCode: '',
        unitName: '',
        deptCode: '',
        deptName: '',
      },
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
      initMainData: [],
      c_num: 0,
      changeType: '',
      oldbcDate: '',
      oldbcDay: '',
      oldbzData: '',
    };
  },
  created() {
    this.getHeight();
    window.addEventListener('resize', this.getHeight);
    this.getUnitCode()
    this.getUnitName()
    //this.getUser(); //获取登录人信息
    this.getDay(); //获取班次
    this.getGroups(); //获取班组
    this.search.bzData = workTeam;
    this.savebzData = workTeam;
  },
  mounted() {},
  methods: {
    //动态加载页面高度
    getHeight() {
      this.contentStyle.height = window.innerHeight + 'px';
      if ($('.showButton').length == 0) {
        this.mainTableData.scrollHeight = window.innerHeight - 135 + 'px';
      } else {
        this.mainTableData.scrollHeight = window.innerHeight - 173 + 'px';
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

    // 获取当前登录人运用所编码
    async getUnitName() {
      let { data } = await axios.get('/apiTrainRepair/common/getUnitName');
      if (data.code == 1) {
        this.unitName = data.data;
      } else {
        this.$message.error('获取登录人信息失败！');
      }
    },

    //获取班次
    getDay() {
      const urlPath = '/apiTrainRepair/common/getDay';
      axios
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
            this.search.bcDay = dayPlanId.substring(11, 13);
            this.saveBcDay = dayPlanId.substring(11, 13);
            this.$nextTick().then(() => {
              this.initTable();
            });
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
    getGroups() {
      const urlPath = '/apiTrainRepair/common/getWorkTeamsByUnitCode?unitCode=' + this.unitCode;
      axios
        .get(urlPath)
        .then((res) => {
          if (res.data.code === 1) {
            this.search.bzDataList = res.data.rows;
          } else {
            let msg = res.data.msg || '班组获取失败!';
            this.showAlertMsg(msg);
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    //查询
    handleSearch() {
      let c_num = 0;
      this.mainTableData.data.forEach((item, index) => {
        if (item.allotStateCode == 1) {
          c_num++;
        }
      });
      if (c_num === 0) {
        this.multipleSelection_main = [];
        this.multipleSelection_sub1 = [];
        this.multipleSelection_sub2 = [];
        this.faultTaskSelect = [];
        this.initTable();
        this.search.trainsetTypeCode = '';
        this.search.trainsetId = '';
        this.search.workTypeCode = '';
        this.search.workTaskCode = '';
        this.search.trainNo = '';
        this.search.allotStateCode = '';
      } else {
        this.$confirm('查询后将清空未保存数据,确认重新查询?', '提示', {
          confirmButtonText: '取消',
          cancelButtonText: '确定',
          closeOnClickModal: false,
          closeOnPressEscape: false,
          type: 'warning',
        })
          .then(() => {
            this.showAlertMsg('已取消重新查询');
            if (this.changeType != '') {
              if (this.changeType === 'date') {
                this.search.bcDate = this.oldbcDate;
                this.saveBcDate = this.oldbcDate;
              } else if (this.changeType === 'day') {
                this.search.bcDay = this.oldbcDay;
                this.saveBcDay = this.oldbcDay;
              } else if (this.changeType === 'bzData') {
                this.search.bzData = this.oldbzData;
                this.savebzData = this.oldbzData;
              }
            }
          })
          .catch(() => {
            this.initTable();
          });
      }
    },
    //
    handelChangeLimit(type) {
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
          this.initSearch();
          this.handleSearch();
          this.savebzData = this.search.bzData;
        }
      }
    },
    //关联数据
    initLimit() {
      this.mainTableData.data = this.copymainTableData;
      //过滤
      let filterJson = {
        trainsetTypeCode: this.search.trainsetTypeCode,
        trainsetId: this.search.trainsetId,
        workTypeCode: this.search.workTypeCode,
        workTaskCode: this.search.workTaskCode,
        allotStateCode: this.search.allotStateCode,
      };
      let filterArr = [];
      for (let name in filterJson) {
        if (filterJson[name] !== '') {
          filterArr.push(name);
        }
      }
      let that = this;
      let arr = [];

      if (filterArr.length !== 0) {
        this.mainTableData.data.forEach((item, index) => {
          for (let i = 0; i < filterArr.length; i++) {
            if (filterArr.length === 1) {
              if (this.search[filterArr[i]] == item[filterArr[i]]) {
                arr.push(item);
              }
            } else if (filterArr.length === 2) {
              if (
                this.search[filterArr[i]] === item[filterArr[0]] &&
                this.search[filterArr[i + 1]] === item[filterArr[1]]
              ) {
                arr.push(item);
                return;
              }
            } else if (filterArr.length === 3) {
              if (
                this.search[filterArr[i]] === item[filterArr[0]] &&
                this.search[filterArr[i + 1]] === item[filterArr[1]] &&
                this.search[filterArr[i + 2]] === item[filterArr[2]]
              ) {
                arr.push(item);
                return;
              }
            } else if (filterArr.length === 4) {
              if (
                this.search[filterArr[i]] === item[filterArr[0]] &&
                this.search[filterArr[i + 1]] === item[filterArr[1]] &&
                this.search[filterArr[i + 2]] === item[filterArr[2]] &&
                this.search[filterArr[i + 3]] === item[filterArr[3]]
              ) {
                arr.push(item);
                return;
              }
            } else if (filterArr.length === 5) {
              if (
                this.search[filterArr[i]] === item[filterArr[0]] &&
                this.search[filterArr[i + 1]] === item[filterArr[1]] &&
                this.search[filterArr[i + 2]] === item[filterArr[2]] &&
                this.search[filterArr[i + 3]] === item[filterArr[3]] &&
                this.search[filterArr[i + 4]] === item[filterArr[4]]
              ) {
                arr.push(item);
                return;
              }
            }
          }
        });
        this.mainTableData.data = arr;
      } else {
        this.mainTableData.data = this.copymainTableData;
      }
    },
    //重置
    handleReset() {
      let c_num = 0;
      this.mainTableData.data.forEach((item, index) => {
        if (item.allotStateCode == 1) {
          c_num++;
        }
      });
      this.countNum = c_num;
      if (c_num !== 0) {
        this.$confirm('清空后将清空未保存数据,确认清空?', '提示', {
          confirmButtonText: '取消',
          cancelButtonText: '确定',
          closeOnClickModal: false,
          closeOnPressEscape: false,
          type: 'warning',
        })
          .then(() => {
            this.showAlertMsg('已取消清空');
          })
          .catch(() => {
            this.search.trainsetTypeCode = '';
            this.search.trainsetId = '';
            this.search.workTypeCode = '';
            this.search.workTaskCode = '';
            this.search.trainNo = '';
            this.search.allotStateCode = '';
            this.multipleSelection_main = [];
            this.multipleSelection_sub1 = [];
            this.multipleSelection_sub2 = [];
            this.faultTaskSelect = [];
            this.initTable();
            this.showAlertMsg('清空成功');
          });
      } else {
        this.search.trainsetTypeCode = '';
        this.search.trainsetId = '';
        this.search.workTypeCode = '';
        this.search.workTaskCode = '';
        this.search.trainNo = '';
        this.search.allotStateCode = '';
        this.multipleSelection_main = [];
        this.multipleSelection_sub1 = [];
        this.multipleSelection_sub2 = [];
        this.faultTaskSelect = [];
        this.mainTableData.data = this.copymainTableData;
        this.showAlertMsg('清空成功');
      }
    },
    //初始化筛选条件
    initSearch() {
      this.search.trainsetTypeCode = '';
      this.search.trainsetId = '';
      this.search.workTypeCode = '';
      this.search.workTaskCode = '';
      this.search.trainNo = '';
      this.search.allotStateCode = '';
    },
    //派工
    onSave() {
      if (this.initMainData.length == 0) {
        this.showAlertMsg('无检修项目可派工!');
        return;
      }
      this.loading = true;
      const urlPath = '/apiTrainRepair/xzyMTaskallotpacket/setTaskAllot';
      const jsonData = {
        data: JSON.stringify(this.mainTableData.data), //
        unitCode: this.unitCode,
        deptCode: this.search.bzData,
      };

      axios.post(urlPath, jsonData).then((res) => {
        this.loading = false;
        if (res.data.code === 1) {
          this.initTable();
          this.initSearch();
          this.showAlertMsg('派工成功!', 'success');
        } else {
          this.showAlertMsg('派工失败!');
        }
      });
    },
    //初始化表格
    initTable() {
      this.loading = true;
      const urlPath = '/apiTrainRepair/xzyMTaskallotpacket/getRepairTask';
      let bcDate = '';
      if (this.search.bcDate && this.search.bcDate !== '') {
        bcDate = moment(this.search.bcDate).format('YYYY-MM-DD');
      }
      axios
        .get(urlPath, {
          params: {
            unitCode: this.unitCode,
            dayplanId: bcDate + '-' + this.search.bcDay,
            deptCode: this.search.bzData,
            taskAllotType: '',
            mode: 1,
          },
        })
        .then((res) => {
          this.loading = false;
          if (res.data.code === 1) {
            let rows = res.data;
            this.mainTableData.data = rows.data;
            this.initMainData = rows.data;
            this.copymainTableData = rows.data;
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

            this.search.trainGroupList = newtrainsetList;
            //作业任务赋值
            this.search.worktaskList = this.uniqueCommon(taskList, 'workTaskCode');

            this.search.worktypeList = rows.taskAllotTypeDict;
            this.search.workStatusList = rows.allotTypeDict;
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
    //表格复选框选中事件
    handleCheck(checked, row, type, parentRow) {
      this.multipleSelection_main = [...new Set(this.multipleSelection_main)];
      this.multipleSelection_sub1 = [...new Set(this.multipleSelection_sub1)];
      this.multipleSelection_sub2 = [...new Set(this.multipleSelection_sub2)];
      this.faultTaskSelect = [...new Set(this.faultTaskSelect)];
      if (checked) {
        if (type === 'mainTable') {
          //主表的id(车组)
          this.multipleSelection_main.push(row.id);
        } else if (type === 'subTable1') {
          //副表的id(项目)
          this.multipleSelection_sub1.push(row.id);
        } else if (type === 'subTable2') {
          //副表的id(辆序)
          let json = {
            itemCode: row.itemCode,
            carNo: row.carNo,
            partName: row.partName,
            processId: row.processId,
            parentId: parentRow.id,
          };
          this.multipleSelection_sub2.push(json);
        } else if (type === 'faultTask') {
          this.faultTaskSelect.push(row);
        }
      } else {
        if (type === 'mainTable') {
          //主表的id(车组)
          this.multipleSelection_main.remove(row.id);
        } else if (type === 'subTable1') {
          //副表的id(项目)
          this.multipleSelection_sub1.remove(row.id);
        } else if (type === 'subTable2') {
          //副表的id(辆序)
          this.multipleSelection_sub2.forEach((item, index) => {
            if (
              item.itemCode == row.itemCode &&
              item.carNo === row.carNo &&
              item.partName === row.partName &&
              item.parentId === parentRow.id
            ) {
              this.multipleSelection_sub2.splice(index, 1);
            }
          });
        } else if (type === 'faultTask') {
          this.faultTaskSelect.forEach((item, index) => {
            if (item.faultId == row.faultId) {
              this.faultTaskSelect.splice(index, 1);
            }
          });
        }
      }
    },
    //故障任务点击事件
    getfaultTask() {
      this.dialogVisibleTask = true;
      this.faultTask.tableData.currentPage = 1;
      this.faultTask.workTimes = moment(this.search.bcDate).format('YYYY-MM-DD') + '-' + this.search.bcDay;
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
      const urlPath = '/apiTrainRepair/xzyMTaskallotpacket/getFaultData';
      const postData = {
        trainsetIdList: trainsetIds,
        findFaultTime: moment(this.search.bcDate).format('YYYY-MM-DD'),
      };
      axios
        .post(urlPath, postData)
        .then((res) => {
          if (res.data.code === 1) {
            this.faultTask.tableData.data = res.data.data;
            let faultTaskData = res.data.data;
            let arr = [];
            let s_proArr = [];
            let mainData = this.initMainData;
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
            this.$nextTick().then(() => {
              this.faultTask.tableData.data = faultTaskData;
            });
          }
        })
        .catch((err) => {});
      this.$nextTick().then(() => {
        $('.faultTaskCheckBox').each((index, item) => {
          $(item).removeClass('is-checked');
          $(item).find('.el-checkbox__input').removeClass('is-checked');
        });
      });
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
        let newMainData = this.initMainData;
        const mainData = this.initMainData;
        const taskData = this.faultTaskSelect;
        let num = null;
        let newTrainsetIds = [];
        let faultIds = [];
        let hasfaultId = '';
        let hasFault = false;
        const urlPath = '/apiTrainRepair/xzyMTaskallotpacket/getFaultPacket';
        const postData = {
          params: {},
          instr: {},
          user: {},
          page: {},
          datas: [],
        };

        let packetCode = '';
        let packetName = '';
        let packetType = '';
        let sTaskId = '';

        axios.post(urlPath, postData).then((res) => {
          if (res.data.code === 200) {
            const data = res.data.result;
            if (data.length != 0) {
              packetCode = data[0].S_PACKET_CODE;
              packetName = data[0].S_PACKET_NAME;
              packetType = data[0].S_PACKET_TYPE;
              sTaskId = data[0].ID;
              let workTeams = {};
              this.search.bzDataList.forEach((item, index) => {
                if (item.id == this.search.bzData) {
                  workTeams = item;
                }
              });
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
                      subJson.itemName = '[辆序：' + taskData[j].carNo + ']' + taskData[j].faultDescription;
                      subJson.displayItemName = '[辆序：' + taskData[j].carNo + ']' + taskData[j].faultDescription;
                      subJson.carNo = taskData[j].carNo;
                      subJson.trainsetId = '2222s';
                      subJson.isNew = true;
                      subJson.trainsetName = taskData[j].trainsetName;
                      subJson.workerList = [];
                      mainData[i].showMode = 'NoCarPartMore';
                      subJson.itemCode = taskData[j].faultId;
                      subJson.carNo = taskData[j].carNo;
                      subJson.xzyMTaskcarpartList = [];
                      let bottomJson = {
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
                      num = null;
                    } else {
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
                  packetCode: packetCode,
                  packetName: packetName,
                  packetType: packetType,
                  sTaskId: '',
                  taskallotItemList: [],
                };
                for (let j = 0; j < taskData.length; j++) {
                  if (newTrainsetIds[i] === taskData[j].trainsetId) {
                    mainJson.trainsetId = taskData[j].trainsetId;
                    mainJson.trainsetName = taskData[j].trainsetName;
                    mainJson.workTypeName = '故障项目';
                    mainJson.workTaskName = '故障处理任务';
                    mainJson.isNew = true;
                    mainJson.workTypeCode = '5';
                    mainJson.packetType = '5';
                    mainJson.id = this.createId();
                    mainJson.workerList = [];
                    mainJson.allotStateName = '未派工';
                    mainJson.allotStateCode = 0;
                    mainJson.showMode = 'NoCarPartMore';
                    let subJson = {
                      itemName: taskData[j].faultDescription,
                      displayItemName: '[辆序：' + taskData[j].carNo + ']' + taskData[j].faultDescription,
                      workerList: [],
                      carNo: taskData[j].carNo,
                      itemCode: taskData[j].faultId,
                      isNew: true,
                      id: this.createId(),
                      xzyMTaskcarpartList: [],
                    };
                    let bottomJson = {
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

              this.$nextTick().then(() => {
                this.$refs.maintableRef.sort('trainsetName', 'ascending');
              });

              this.faultTaskSelect = [];
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
        confirmButtonText: '取消',
        cancelButtonText: '确定',
        closeOnClickModal: false,
        closeOnPressEscape: false,
        type: 'warning',
      })
        .then(() => {
          this.showAlertMsg('已取消删除');
        })
        .catch(() => {
          this.faultTask.tableData.data.forEach((item, index) => {
            rows.forEach((e, i) => {
              if (item.carNo == e.carNo && item.faultId == e.itemCode) {
                this.faultTask.tableData.data.splice(index, 1);
              }
            });
          });
          let hasNewCount = 0;
          rows.splice(index, 1);
          this.$nextTick(() => {
            rows.forEach((item) => {
              if (item.isNew) {
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
    //获取人员列表
    getWorker() {
      const urlPath = '/apiTrainRepair/xzyCAllotbranchConfig/getGroup';
      const postData = {
        deptCode: this.search.bzData,
      };
      axios
        .post(urlPath, postData)
        .then((res) => {
          if (res.data.code === 1) {
            this.chooseWorkers.bureauName = this.unitName;
            //TODO 绑定班组名字(现在是部门名称)
            let workTeams = {};
            this.search.bzDataList.forEach((item, index) => {
              if (item.id == this.search.bzData) {
                workTeams = item;
              }
            });
            this.chooseWorkers.workTeam = workTeams.name;
            const data = res.data;
            this.workerList = data;
            let wokers = [];
            if (data.data.length === 0) {
              //所有人员全部在班下面,没有小组
              data.deptUserList.forEach((item, index) => {
                let json_worker = {};
                json_worker.id = item.id;
                json_worker.label = item.name;
                json_worker.cParttime = 0;
                wokers.push(json_worker);
              });
            } else {
              data.data.forEach((item, index) => {
                let json_group = {};
                json_group.id = item.id;
                json_group.label = item.label;
                json_group.isGroup = item.isGroup;
                json_group.children = item.children;
                wokers.push(json_group);
              });
              data.deptUserList.forEach((item, index) => {
                let json_worker = {};
                json_worker.id = item.id;
                json_worker.label = item.name;
                json_worker.cParttime = 0;
                wokers.push(json_worker);
              });
            }

            let obj = {};
            this.search.bzDataList.forEach((item, index) => {
              if (item.id === this.search.bzData) {
                obj = item;
              }
            });
            let json_tree = {
              id: obj.id,
              label: obj.name,
              children: wokers,
            };
            let treeData = [];
            treeData.push(json_tree);
            this.treeData = treeData;
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    //树形选择过滤人员
    handleCheckTree(data, isChecked, childChecked) {
      if (isChecked) {
        if (this.chooseWorkers.quickQuery == '') {
          if (data.cParttime == 0 || data.cParttime == 1) {
            let json = {};
            json.workerID = data.id;
            json.workerName = data.label;
            this.checkPeople.push(json);
            this.checkPeople = this.uniqueCommon(this.checkPeople, 'workerID');
          }
        } else {
          let ckeckArr = this.$refs.tree.getCheckedKeys();
          let checkNodes = [];
          ckeckArr.forEach((item, index) => {
            let nodes = this.$refs.tree.getNode(item);
            if (nodes.visible === true) {
              this.limitWorker(nodes);
              if (nodes.data.cParttime == 0 || nodes.data.cParttime == 1) {
                checkNodes.push(nodes.data);
                checkNodes = this.uniqueCommon(checkNodes, 'id');
              }
            }
          });
          this.pCheckNodes = checkNodes;
        }
      } else {
        this.checkPeople.forEach((item, index) => {
          if (data.id === item.workerID) {
            this.checkPeople.remove(item);
          }
        });
      }
    },
    //表格展开事件
    expandChange_main(row, expandRows) {
      if (expandRows.length != 0) {
        //展开
        this.$nextTick().then(() => {
          $('.sub1CheckBox').each((index, el) => {
            this.multipleSelection_sub1.forEach((item, index) => {
              if ($(el).attr('data-id') == item) {
                $(el).addClass('is-checked');
                $(el).find('.el-checkbox__input').addClass('is-checked');
              }
            });
          });
        });
      }
    },
    //表格展开事件
    expandChange_sub(row, expandRows) {
      if (expandRows.length != 0) {
        //展开
        this.$nextTick().then(() => {
          $('.sub2CheckBox').each((index, el) => {
            this.multipleSelection_sub2.forEach((item, index) => {
              if ($(el).attr('data-id') == item.processId) {
                $(el).addClass('is-checked');
                $(el).find('.el-checkbox__input').addClass('is-checked');
              }
            });
          });
        });
      }
    },
    limitWorker(nodes) {
      if (nodes.data.cParttime == 0 || nodes.data.cParttime == 1) {
        let json = {};
        json.workerID = nodes.data.id;
        json.workerName = nodes.data.label;
        this.checkPeople.push(json);
        this.checkPeople = this.uniqueCommon(this.checkPeople, 'workerID');
      }
    },
    //编辑班组
    setGroup() {
      const urlPath = '/apiTrainRepair/xzyCAllotbranchConfig/setBranch';
      let classGroup = this.treeData[0].children;
      let data = [];
      let hasGroup = false;
      if (classGroup.length !== 0) {
        classGroup.forEach((item, index) => {
          if (item.isGroup) {
            hasGroup = true;
            let groupJson = {};
            groupJson.label = item.label;
            let arr = [];
            if (item.children) {
              item.children.forEach((el, i) => {
                let jsonC = {};
                jsonC.label = el.label;
                jsonC.id = el.id;
                jsonC.cParttime = el.cParttime;
                arr.push(jsonC);
              });
            }
            groupJson.children = arr;
            data.push(groupJson);
          }
        });
      }
      if (hasGroup) {
        this.loading = true;
        let postData = {
          data: JSON.stringify(data),
          deptCode: this.search.bzData,
          unitCode: this.unitCode,
        };
        axios.post(urlPath, postData).then((res) => {
          this.loading = false;
          this.countNotWork(this.mainTableData.data);
        });
      }
    },
    //添加人员
    addWorkers() {
      let sub1 = [];
      let sub2 = [];
      const mainTableData = this.initMainData;
      const multipleSelection_main = this.multipleSelection_main;
      const multipleSelection_sub1 = this.multipleSelection_sub1;
      const multipleSelection_sub2 = this.multipleSelection_sub2;

      if (
        multipleSelection_main.length === 0 &&
        multipleSelection_sub1.length === 0 &&
        multipleSelection_sub2.length === 0
      ) {
        this.showAlertMsg('人员调整成功!', 'success');
        this.setGroup();
      } else {
        if (this.checkPeople.length === 0) {
          this.showAlertMsg('请选择人员!', 'warning');
          return;
        }
        let resetAllotState = (mainTableRow) => {
          if (mainTableRow.workerList && mainTableRow.workerList.length > 0) {
            mainTableRow.allotStateCode = '1';
            mainTableRow.allotStateName = '已分配人员';
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
                }
              }
              nowSub1Data.workerList = this.uniqueCommon(sub3, 'workerID');
            }
          }
        }

        let nowTreeData = this.mainTableData.data;

        this.$nextTick().then(() => {
          this.mainTableData.data = Object.assign(this.mainTableData.data, nowTreeData);
        });

        this.multipleSelection_main = [];
        this.multipleSelection_sub1 = [];
        this.multipleSelection_sub2 = [];
        this.setGroup();
        this.showAlertMsg('人员分配成功!', 'success');
        this.$nextTick().then(() => {
          $('.mainCheckBox').each((index, el) => {
            $(el).removeClass('is-checked');
            $(el).find('.el-checkbox__input').removeClass('is-checked');
          });
          $('.sub1CheckBox').each((index, el) => {
            $(el).removeClass('is-checked');
            $(el).find('.el-checkbox__input').removeClass('is-checked');
          });
          $('.sub2CheckBox').each((index, el) => {
            $(el).removeClass('is-checked');
            $(el).find('.el-checkbox__input').removeClass('is-checked');
          });
        });
      }

      this.dialogVisiblePeople = false;
    },
    closePeople() {
      this.dialogVisiblePeople = false;
      this.pCheckNodes = [];
      this.checkPeople = [];
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
    //人员/小组的操作
    //新建小组
    addGroup() {
      this.vals = [];
      let labels = this.callBackData(this.treeData[0].children);
      if (labels.length !== 0) {
        for (let i = 0; i < labels.length; i++) {
          if (this.trim(labels[i].label) === this.trim(this.groupName)) {
            this.showAlertMsg('此小组名已存在,请重新输入!');
            return;
          }
        }
      }
      const newChild = { id: this.newTreeId++, label: this.trim(this.groupName), children: [], isGroup: true };
      if (!this.nowTreeRows.children) {
        this.$set(this.nowTreeRows, 'children', []);
      }
      //const parent = this.nowTreeNode.parent;
      this.treeData[0].children.push(newChild);
      this.dialogVisibleGroup = false;
      this.showAddGroup = false;
      this.tmDisplay = false;
    },
    //新建/修改小组名称
    changeGroupName(type) {
      this.dialogVisibleGroup = true;
      this.changeGroupType = type;
      if (this.changeGroupType === 'edit') {
        this.groupName = this.nowTreeRows.label;
      } else if (this.changeGroupType === 'new') {
        this.groupName = '';
      }
    },
    //小组名字确定事件
    editName() {
      if (this.trim(this.groupName) == '') {
        this.showAlertMsg('请输入小组名!');
        return;
      }
      if (this.changeGroupType == 'new') {
        this.addGroup();
      } else if (this.changeGroupType == 'edit') {
        if (!this.treeData[0].children) return;
        this.vals = [];
        let labels = this.callBackData(this.treeData[0].children);
        if (labels.length !== 0) {
          for (let i = 0; i < labels.length; i++) {
            if (this.trim(labels[i].label) === this.trim(this.groupName)) {
              this.showAlertMsg('此小组名已存在,请重新输入!');
              return;
            } else {
              this.$set(this.nowTreeRows, 'label', this.groupName);
              this.dialogVisibleGroup = false;
            }
          }
        }
      }
    },
    //递归查找小组名字(用于重复名字不能添加)
    callBackData(arr) {
      arr.forEach((v, i) => {
        let json = {};
        json.label = v.label;
        json.id = v.id;
        this.vals.push(json);
        if (v.children) {
          this.callBackData(v.children);
        }
      });
      return this.vals;
    },
    //清空小组人员
    clearGroup() {
      this.$confirm('确认清空此小组?', '提示', {
        confirmButtonText: '取消',
        cancelButtonText: '确定',
        closeOnClickModal: false,
        closeOnPressEscape: false,
        type: 'warning',
      })
        .then(() => {
          this.showAlertMsg('已取消', 'info');
        })
        .catch(() => {
          const nowPeople = this.nowTreeRows.children;
          this.nowTreeRows.children = [];
          const parent = this.nowTreeNode.parent;

          for (let i = 0; i < nowPeople.length; i++) {
            parent.data.children.push(nowPeople[i]);
          }
        });
    },
    //删除小组
    deleteGroup() {
      this.$confirm('确认删除此小组?', '提示', {
        confirmButtonText: '取消',
        cancelButtonText: '确定',
        closeOnClickModal: false,
        closeOnPressEscape: false,
        type: 'warning',
      })
        .then(() => {
          this.showAlertMsg('已取消', 'info');
        })
        .catch(() => {
          const parent = this.nowTreeNode.parent;
          const children = parent.data.children || parent.data;
          const index = children.findIndex((item) => item.id === this.nowTreeRows.id);
          children.splice(index, 1);
          const nowPeople = this.nowTreeRows.children;
          for (let i = 0; i < nowPeople.length; i++) {
            parent.data.children.push(nowPeople[i]);
          }
        });
    },
    //上移小组
    upGroup() {
      this.moveUp();
    },
    //下移小组
    downGroup() {
      this.moveDown();
    },

    //人员移动右键菜单
    movePerson() {
      this.tmDisplay = false;
      this.peopleDisplay = false;
      this.man2GroupDisplay = true;
      this.isPartTimeJob = false;
      const parent = this.nowTreeNode.parent;
      let arr = [];
      if (!parent.data.isGroup) {
        parent.data.children.forEach((item, index) => {
          if (item.isGroup) {
            arr.push(item);
          }
        });
      } else {
        if (parent.data.isGroup) {
          let oParent = parent.parent;
          oParent.data.children.forEach((item, index) => {
            if (item.isGroup) {
              arr.push(item);
            }
          });
        }
      }
      this.man2GroupList = arr;
    },
    //人员移动到xx组
    moveTo(id) {
      const parent1 = this.nowTreeNode.parent;
      const children1 = parent1.data.children || parent1.data;
      const index = children1.findIndex((item) => item.id === this.nowTreeRows.id);
      let nowPeople = this.nowTreeRows;
      let newPeople = {};
      if (this.isPartTimeJob) {
        newPeople.cParttime = 1;
        newPeople.id = nowPeople.id;
        newPeople.label = nowPeople.label;
        if (nowPeople.children) {
          newPeople.children = nowPeople.children;
        }
      } else {
        newPeople = nowPeople;
        children1.splice(index, 1);
      }

      const parent = this.treeData[0];
      const children = parent.children;

      let groups = [];
      children.forEach((item, index) => {
        if (item.isGroup) {
          groups.push(item);
        }
      });

      for (let i = 0; i < groups.length; i++) {
        if (groups[i].id === id) {
          let arr = [];
          if (groups[i].children.length !== 0) {
            for (let j = 0; j < groups[i].children.length; j++) {
              if (groups[i].children[j].id === newPeople.id) {
                this.showAlertMsg('该组已存在此人员!');
                return;
              } else {
                arr.push(newPeople);
              }
            }
          } else {
            arr.push(newPeople);
          }
          groups[i].children.push(...new Set(arr));
        }
      }
      this.man2GroupDisplay = false;
    },
    //人员上移
    upPerson() {
      this.moveUp();
    },
    //人员下移
    downPerson() {
      this.moveDown();
    },
    //人员兼职
    multiPerson() {
      this.tmDisplay = false;
      this.peopleDisplay = false;
      this.man2GroupDisplay = true;
      this.isPartTimeJob = true;
      const parent = this.nowTreeNode.parent;
      let aLabels = [];
      if (this.treeData[0].children) {
        this.treeData[0].children.forEach((item, index) => {
          if (item.isGroup) {
            aLabels.push(item);
          }
        });
      }
      let arr = [];
      for (let i = 0; i < aLabels.length; i++) {
        if (aLabels[i].id !== parent.data.id) {
          arr.push(aLabels[i]);
        }
      }

      this.man2GroupList = [...new Set(arr)];
    },
    //删除人员兼职
    multiPersonDel() {
      this.$confirm('确认删除此兼职?', '提示', {
        confirmButtonText: '取消',
        cancelButtonText: '确定',
        closeOnClickModal: false,
        closeOnPressEscape: false,
        type: 'warning',
      })
        .then(() => {
          this.showAlertMsg('已取消', 'info');
        })
        .catch(() => {
          const parent = this.nowTreeNode.parent;
          const children = parent.data.children || parent.data;
          const index = children.findIndex((item) => item.id === this.nowTreeRows.id);
          children.splice(index, 1);
          if (this.nowTreeRows.children) {
            const nowPeople = this.nowTreeRows.children;
            for (let i = 0; i < nowPeople.length; i++) {
              parent.data.children.push(nowPeople[i]);
            }
          }
        });
    },
    moveUp() {
      let parent = this.nowTreeNode.parent;
      const children = parent.data.children || parent.data;
      const index = children.findIndex((item) => item.id === this.nowTreeRows.id);
      if (index === 0) {
        return;
      }
      let nowData = children[index];
      let preData = children[index - 1];
      nowData.sort--;
      preData.sort++;

      const nowTreeData = this.treeData;
      children.splice(index, 1);
      children.splice(index - 1, 0, nowData);
      this.treeData = [];
      this.treeData = Object.assign(this.treeData, nowTreeData);
    },
    moveDown() {
      const parent = this.nowTreeNode.parent;
      const children = parent.data.children || parent.data;
      const index = children.findIndex((item) => item.id === this.nowTreeRows.id);

      if (index === this.treeData[0].children.length - 1) {
        return;
      }
      const nowData = children[index];
      const nextData = children[index + 1];
      nextData.sort--;
      nowData.sort++;
      const nowTreeData = this.treeData;
      children.splice(index, 1);
      children.splice(index + 1, 0, nowData);
      this.treeData = [];
      this.treeData = Object.assign(this.treeData, nowTreeData);
    },

    //快速查询
    quickQuery(value, data) {
      if (!value) return true;
      if (data.cParttime == 0 || data.cParttime == 1) {
        return data.label.indexOf(value) !== -1;
      }
    },
    //属性节点点击事件
    handleNodeClick() {
      this.tmDisplay = false;
      this.peopleDisplay = false;
      this.man2GroupDisplay = false;
    },
    //属性节点右键事件
    rightClick(e, data, node, comp) {
      this.tmDisplay = false;
      this.peopleDisplay = false;
      this.man2GroupDisplay = false;
      this.nowTreeRows = data;
      this.nowTreeNode = node;

      if (this.nowTreeRows.cParttime === 1) {
        this.showMulti = true;
      } else if (this.nowTreeRows.cParttime === 0) {
        this.showMulti = false;
      }
      if (!data.isGroup && !data.cParttime) {
        this.showAddGroup = true;
        this.tmDisplay = true;
      }
      if (data.isGroup) {
        this.tmDisplay = true;
        this.peopleDisplay = false;
        this.man2GroupDisplay = false;
        this.showAddGroup = false;
        const that = this;
        document.onclick = function (ev) {
          if (ev.target !== document.getElementById('perTreeMenu')) {
            that.tmDisplay = false;
          }
        };
      }
      if (data.cParttime == 0 || data.cParttime == 1) {
        this.tmDisplay = false;
        this.man2GroupDisplay = false;
        this.peopleDisplay = true;
        this.showAddGroup = false;
        const that = this;
        document.onclick = function (ev) {
          if (ev.target !== document.getElementById('perTreeMenu')) {
            that.peopleDisplay = false;
          }
        };
      }
      this.rightMenu = { top: e.pageY + 'px', left: e.pageX + 'px' };
    },
    handleSizeChange(val) {
      this.mainTableData.pageSize = val;
      this.initCheck();
    },
    handleCurrentChange(val) {
      this.mainTableData.currentPage = val;
      this.initCheck();
    },
    //翻页保持复选框选中
    initCheck() {
      const data = [...new Set(this.multipleSelection_main)];
      const sub1Data = [...new Set(this.multipleSelection_sub1)];
      const sub2Data = [...new Set(this.multipleSelection_sub2)];
      this.$nextTick().then(() => {
        $('.mainCheckBox').each((index, el) => {
          $(el).removeClass('is-checked');
          $(el).find('.el-checkbox__input').removeClass('is-checked');
          for (let i = 0; i < data.length; i++) {
            if (data[i] == $(el).attr('data-id')) {
              $(el).addClass('is-checked');
              $(el).find('.el-checkbox__input').addClass('is-checked');
            }
          }
        });
        $('.sub1CheckBox').each((index, el) => {
          $(el).removeClass('is-checked');
          $(el).find('.el-checkbox__input').removeClass('is-checked');
          for (let i = 0; i < sub1Data.length; i++) {
            if (sub1Data[i] == $(el).attr('data-id')) {
              $(el).addClass('is-checked');
              $(el).find('.el-checkbox__input').addClass('is-checked');
            }
          }
        });
        $('.sub2CheckBox').each((index, el) => {
          $(el).removeClass('is-checked');
          $(el).find('.el-checkbox__input').removeClass('is-checked');
          for (let i = 0; i < sub2Data.length; i++) {
            if (sub2Data[i] == $(el).attr('data-id')) {
              $(el).addClass('is-checked');
              $(el).find('.el-checkbox__input').addClass('is-checked');
            }
          }
        });
      });
    },
    initFaultCheck() {
      let arr = [];
      this.faultTaskSelect.forEach((item, index) => {
        this.faultTask.tableData.data.forEach((e, i) => {
          if (item.carNo == e.carNo && item.faultId == e.faultId) {
            arr.push(e);
          }
        });
      });
      this.$nextTick().then(() => {
        $('.faultTaskCheckBox').each((index, item) => {
          $(item).removeClass('is-checked');
          $(item).find('.el-checkbox__input').removeClass('is-checked');
          for (let i = 0; i < arr.length; i++) {
            if ($(item).attr('data-id') == arr[i].faultId) {
              $(item).addClass('is-checked');
              $(item).find('.el-checkbox__input').addClass('is-checked');
            }
          }
        });
      });
    },
    closeFault() {
      this.faultTaskSelect = [];
      this.faultTask.tableData.data = [];
      this.dialogVisibleTask = false;
    },
    handleSizeChange_task(val) {
      this.faultTask.tableData.pageSize = val;
      this.initFaultCheck();
    },
    handleCurrentChange_task(val) {
      this.faultTask.tableData.currentPage = val;
      this.initFaultCheck();
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
              } else if (code == 'workTaskCode') {
                if (arr[j].workTaskCode == list[i].workTaskCode) {
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
  },
  computed: {},
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
    'search.bcDate': function (val, oldVal) {
      if (oldVal != '') {
        this.oldbcDate = moment(oldVal).format('YYYY-MM-DD');
      }
    },
    'search.bcDay': function (val, oldVal) {
      if (oldVal !== '') {
        this.oldbcDay = oldVal;
      }
    },
    'search.bzData': function (val, oldVal) {
      if (oldVal !== '') {
        this.oldbzData = oldVal;
      }
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
