var checkResponse10 = (res) => {
  return new Promise((resolve, reject) => {
    if (res.data.code === 1) {
      resolve(res.data.data);
    } else {
      notFixed.showAlertMsg(res.data.msg, 'error');
      reject();
    }
  });
};
window.app = new Vue({
  el: '#notFixed',
  data: {
    unitCode: '',
    affirmMsg: '',
    isDisa: false,
    isDisabled: false,
    loading: false,
    dialogLoading: false,
    burecode: '',
    updatePersonId: '',
    search: {
      dateFrom: moment().format('YYYY-MM-DD'), // 开始时间
      dateTo: moment().format('YYYY-MM-DD'), // 结束时间
      trainType: '', // 车型
      trainTypeOptions: [], // 车型列表
      trainGroup: '', // 车组
      trainGroupOptions: [], // 车组列表
      jobGroup: '', // 作业班组
      jobGroupOptions: [], // 作业班组列表
    },
    dialog: {
      dayplayid: '',
      accountName: '',
      dialogCode: '',
      unitName_sel: '',
      jobProject_sel: '',
      confirmMan_sel: '',
      dialogTableVisible: false,
      dialogTitleName: '',
      burecodeAA: '',
      burecode: '',
      burecodeList: [],
      date: '',
      worktime: '',
      worktimeList: [
        { label: '白班', value: '00' },
        { label: '夜班', value: '01' },
      ],
      trainGroup: undefined,
      trainGroupOptions: [],
      CRHCarriage: [], // 车组辆序
      jobTypesOptions: [],
      jobProject: undefined,
      jobProjectOptions: [],
      jobGroup: undefined, // 作业班组
      confirmJobGroup: {}, // 确认班组
      jobGroupOptions: [], // 作业班组列表
      workers: [], // 作业人员
      workersOptions: [], // 作业人员列表
      confirmMan: [], // 确认人员
      confirmManOptions: [], // 确认人员列表
      workCarriage: '', // 作业辆序
      confirmWorkCarriage: '', // 确认辆序
      dateTo: '', // 作业结束时间
      confirmDateTo: '', // 确认结束时间
      currentData: {}, // 点击编辑时，保存当前数据详细信息
    },

    dialogRules: {
      date: [{ required: true, message: '请选择作业班次', trigger: 'blur' }],
      trainGroup: [{ required: true, message: '请选择车组号', trigger: 'blur' }],
      jobProject: [{ required: true, message: '请选择作业项目', trigger: 'blur' }],
      jobGroup: [{ required: true, message: '请选择作业班组', trigger: 'blur' }],
      workers: [{ required: true, message: '请选择作业人员', trigger: 'blur' }],
      workCarriage: [{ required: true, message: '请选择作业辆序', trigger: 'blur' }],
      dateTo: [{ required: true, message: '请选择作业完成时间', trigger: 'blur' }],
      // confirmJobGroup: [{ required: ConfirmEdit, message: '请选择确认班组', trigger: 'blur' }],
      // confirmMan: [{ required: ConfirmEdit, message: '请选择确认人', trigger: 'blur' }],
      // confirmWorkCarriage: [{ required: ConfirmEdit, message: '请选择确认辆序', trigger: 'blur' }],
      // confirmDateTo: [{ required: ConfirmEdit, message: '请选择确认完成时间', trigger: 'blur' }],
    },
    main: {
      scrollHeight: '100%',
      tableData: [],
      pageSize: 10,
      currentPage: 1,
    },
    restaurants: [],

    workerTypes: [],
    // 是否需要确认
    needConfirmSkippedNodes: true,

    userInfo: null,
  },
  async created() {
    //初始化
    await this.getUnitCode();
    await this.getConfig();
    await this.getTrainType(); //获取搜索栏车型
    await this.zyBzHandle(); // 获取搜索作业班组
  },
  mounted() {
    this.initTableList();
  },
  methods: {
    // 获取权限
    async getConfig() {
      let res = await axios.get('/apiTrainRepair/config/getConfig', {
        params: {
          paramName: 'ConfirmEdit',
        },
      });

      if (res.data.code == 1) {
        this.affirmMsg = res.data.data.paramValue;
      }
    },

    // 获取当前登录人运用所编码
    async getUnitCode() {
      let { data } = await axios.get('/apiTrainRepair/common/getUnitCode');
      if (data.code == 1) {
        this.unitCode = data.data;
      } else {
        this.$message.warning('获取登录人信息失败！');
      }
    },

    //搜索栏-获取车型
    getTrainType() {
      var that = this;
      var trainType_url_path = '/apiTrainResume/resume/getTraintypeList';
      axios
        .get(trainType_url_path)
        .then(function (res) {
          var data = res.data.data;
          that.search.trainTypeOptions = data;
          that.changeTrainType();
        })
        .catch(function (err) {
          //that.showAlertMsg('运用所接口调用失败!','warning')
        });
    },
    //搜索栏-获取车组
    changeTrainType() {
      //获取车组
      var that = this;
      this.search.trainGroup = '';
      let traintype;

      traintype = that.search.trainType;

      var trainGroup_url_path = '/apiTrainResume/resume/getTrainsetNameList';
      axios
        .post(trainGroup_url_path, {
          traintype,
        })
        .then(function (res) {
          var data = res.data.data;
          that.search.trainGroupOptions = data;
        })
        .catch(function (err) {
          that.showAlertMsg('获取车组失败!', 'warning');
        });
    },

    //搜索栏/弹出层-获取作业班组
    zyBzHandle(code) {
      var that = this;
      axios
        .get('/apiTrainRepair/common/getWorkTeamsByUnitCode', {
          params: {
            unitCode: that.unitCode,
          },
        })
        .then(function (res) {
          that.search.jobGroupOptions = res.data.rows;
          that.dialog.jobGroupOptions = res.data.rows;
        })
        .catch(function (err) {
          that.showAlertMsg('获取作业班组信息失败!', 'warning');
        });
    },

    //初始化表数据
    initTableList() {
      this.loading = true;
      var that = this;
      setTimeout(function () {
        var trainType_url_path = '/apiTrainRepair/workProcess/getIntegrationList';
        axios
          .post(trainType_url_path, {
            startTime: that.search.dateFrom, // 开始时间
            endTime: that.search.dateTo, // 结束时间
            deptCode: that.search.jobGroup, // 作业班组编码
            trainsetName: that.search.trainGroup, // 车组名称
            trainsetType: that.search.trainType, // 车型
            pageNum: that.main.currentPage, // 页码
            pageSize: that.main.pageSize, // 每页显示数据条数
            unitCode: that.unitCode, // 所编码
          })
          .then(function (res) {
            that.loading = false;
            if (res.data.code == 1) {
              var data = res.data.data.integrationList;
              that.main.tableData = data;
            } else {
              that.showAlertMsg('获取表数据失败!', 'warning');
            }
          })
          .catch(function (err) {
            that.loading = false;
            // that.showAlertMsg('获取表数据失败!', 'warning')
          });
      }, 500);
    },

    //点击搜索
    onSearch() {
      this.loading = true;
      this.main.currentPage = 1;
      var timeFrom = new Date(this.search.dateFrom).getTime();
      var timeTo = new Date(this.search.dateTo).getTime();
      if (timeTo != 0) {
        if (timeFrom > timeTo) {
          this.showAlertMsg('结束日期不能早于开始日期!');
          this.loading = false;
          $('.endDate .el-input__inner').focus();
        } else {
          //TODO刷新表
          this.initTableList();
        }
      }
    },

    //点击重置按钮
    reset() {
      if (!this.isDisabled) {
        this.search.burecode = '';
      }
      this.search.dateFrom = moment().format('YYYY-MM-DD');
      this.search.dateTo = moment().format('YYYY-MM-DD');
      this.search.trainType = '';
      this.search.trainGroup = '';
      this.search.jobGroup = '';
    },

    //点击新增按钮
    async newAdd() {
      var that = this;
      that.dialog.dialogTableVisible = true;
      that.dialogLoading = true;
      that.dialog.burecode = that.burecode;
      that.dialog.dialogTitleName = '一体化作业回填-新增';
      that.dialog.dialogCode = 'add';
      that.isDisa = false;
      await that.getDay();
      await that.zyBzHandle();
      let res = await that.getWorkTimeByDayPlayId(that.dayPlanID);
      that.dialog.dateTo = res.startTime;
      
      this.$nextTick().then(async () => {
        await this.$refs.dialog.clearValidate();
      });
      that.dialogLoading = false;
    },

    // 根据班次获取工作时间
    async getWorkTimeByDayPlayId(dayPlanId) {
      let res = await axios.get('/apiTrainRepair/common/getWorkTimeByDayPlanId', {
        params: {
          dayPlanId,
        },
      });
      return res.data.data;
    },

    //编辑
    async changeRow(index, rows) {
      var that = this;
      that.isDisa = true;
      that.dialog.dialogTitleName = '一体化作业回填-编辑';
      that.dialog.dialogCode = 'update';
      that.currentData = rows;
      that.dialog.dialogTableVisible = true;
      that.dialog.date =
        rows.dayPlanId.split('-')[0] + '-' + rows.dayPlanId.split('-')[1] + '-' + rows.dayPlanId.split('-')[2];
      that.dialog.worktime = rows.dayPlanId.split('-')[3];
      that.dialog.trainGroup = { trainsetName: rows.trainsetName, trainsetId: rows.trainsetId };
      that.dialog.trainGroupOptions = [that.dialog.trainGroup];
      that.dialog.jobProject = rows.itemName;
      that.dialog.CRHCarriage = rows.carNos;
      that.dialog.jobGroup = { id: rows.deptCode, name: rows.deptName };
      that.dialog.workers = [];
      rows.processWorkerList.forEach((item) => {
        that.dialog.workers.push({
          workId: item.workerId,
          workName: item.workerName,
        });
      });
      that.dialog.workCarriage = rows.workCarCount;
      that.dialog.dateTo = rows.workEndTime;
      that.dialog.confirmJobGroup = { name: rows.confirmDeptName, id: rows.confirmDeptCode };
      that.dialog.confirmMan = [];
      rows.processConfirmList.forEach((item) => {
        that.dialog.confirmMan.push({
          workId: item.workerId,
          workName: item.workerName,
        });
      });
      that.dialog.confirmWorkCarriage = rows.confirmCarCount;
      that.dialog.confirmDateTo = rows.confirmEndTime;
      await that.zyBzHandle();
      await that.getWorkers();
      await that.getConfirmPeople();

      this.$nextTick().then(async () => {
        await this.$refs.dialog.clearValidate();
      });
    },

    //删除
    deleteRow(index, rows) {
      var that = this;
      this.$confirm('确认删除此条数据?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(function () {
          var yys_url_path = '/apiTrainRepair/workProcess/delIntegration';
          axios
            .post(yys_url_path, {
              unitCode: that.unitCode, // 运用所编码
              dayPlanId: rows.dayPlanId, // 日计划ID
              itemCode: rows.itemCode, // 项目编码
              trainsetId: rows.trainsetId, // 车组ID
            })
            .then(function (res) {
              that.$message({
                type: 'success',
                message: '已移除!',
              });
              that.initTableList();
            })
            .catch(function (err) {
              //that.showAlertMsg('运用所接口调用失败!','warning')
            });
        })
        .catch(function () {
          that.$message({
            type: 'info',
            message: '已取消',
          });
        });
    },

    //弹出层-获取作业班次(日计划)
    async getDay() {
      var that = this;
      var url_path = '/apiTrainRepair/common/getDay';
      await axios
        .get(url_path)
        .then(async (res) => {
          var data = res.data;
          var oDate = data.dayPlanId.substring(0, 10);
          var dayNight = data.dayPlanId.substring(11);
          that.dialog.date = oDate;
          that.dialog.worktime = dayNight;
          that.dialog.dayplayid = data.dayPlanId;
          await that.getTrainsetListByDayPlanId();
        })
        .catch(function (err) {
          that.showAlertMsg('获取作业班次失败!', 'warning');
        });
    },

    // 作业班次切换
    changeGetDa() {
      this.getTrainsetListByDayPlanId();
    },

    //弹出层-获取车组号
    async getTrainsetListByDayPlanId() {
      var that = this;
      var dayPlan = '';
      if (this.dialog.date) {
        dayPlan = moment(this.dialog.date).format('YYYY-MM-DD') + '-' + that.dialog.worktime;
      }

      let Url;
      if (that.dialog.dialogCode == 'add') {
        Url = '/apiTrainRepair/workProcess/getIntegrationTrainsetList';
      } else {
        Url = '/apiTrainRepair/workProcess/getTrainsetListByDayPlanId';
      }

      await axios
        .get(Url, {
          params: {
            unitCode: '', // 运用所
            dayPlanId: dayPlan, // 作业班次
            repairType: '6', // 当前页面一级修为'1', 二级修为'2', 无修程为'6'
          },
        })
        .then(function (res) {
          if (res.data.code == '1') {
            that.dialog.trainGroup = res.data.data[0];
            that.dialog.trainGroupOptions = res.data.data;
            that.getPacketTaskByCondition();
            that.getCarno();
          } else {
            // that.showAlertMsg('获取车组号失败!', 'warning');
          }
        })
        .catch(function (err) {
          // that.showAlertMsg('获取车组号失败!', 'warning')
        });
    },

    // 车组切换
    changeMultipleUnit(val) {
      this.getPacketTaskByCondition();
      this.getCarno();
    },

    // 弹出层-获取作业项目
    getPacketTaskByCondition() {
      var that = this;
      var dayPlan = '';
      that.dialog.jobProjectOptions = [];
      if (this.dialog.date) {
        dayPlan = moment(this.dialog.date).format('YYYY-MM-DD') + '-' + that.dialog.worktime;
      }

      let Url;
      if (that.dialog.dialogCode == 'add') {
        Url = '/apiTrainRepair/workProcess/getIntegrationTaskList';
      } else {
        Url = '/apiTrainUse/Task/getPacketTaskByCondition';
      }

      if (!this.dialog.trainGroup) return;

      axios
        .get(Url, {
          params: {
            dayPlanId: dayPlan,
            trainsetId: that.dialog.trainGroup.trainsetId,
            repairDeptCode: '',
            deptCode: that.unitCode,
            lstPacketTypeCode: '6',
          },
        })
        .then(function (res) {
          if (that.dialog.dialogCode == 'add') {
            that.dialog.jobProjectOptions = res.data.data;
          } else {
            that.dialog.jobProjectOptions = res.data;
          }
        })
        .catch(() => {
          this.$message({
            message: '获取作业项目失败',
            type: 'warning',
          });
        });
    },

    // 弹出层-获取车组辆序  根据车组号
    getCarno() {
      let that = this;

      axios
        .post('/apiTrainResume/resume/getCarno?trainsetid=' + that.dialog.trainGroup.trainsetId)
        .then(function (res) {
          that.dialog.CRHCarriage = res.data.data;
          that.dialog.workCarriage = that.dialog.CRHCarriage.length;
        })
        .catch(() => {
          this.$message({
            message: '获取作业辆序失败',
            type: 'warning',
          });
        });
    },

    // 弹出框-验证 作业辆序/确认辆序 输入是否为正整数
    changeInpit(modelValue) {
      var pattern = /^[1-9][0-9]*$/; // 正整数正则表达式
      // 当不是正整数时
      if (!pattern.test(this.dialog[modelValue])) {
        this.dialog[modelValue] = '';
      } else if (this.dialog[modelValue] > this.dialog.CRHCarriage.length) {
        let msg;
        if (modelValue == 'workCarriage') {
          msg = '作业辆序不能大于车组辆序';
        } else if (modelValue == 'confirmWorkCarriage') {
          msg = '确认辆序不能大于车组辆序';
        }
        this.$message({
          message: msg,
          type: 'warning',
        });
        this.dialog[modelValue] = '';
      }
    },

    //弹出层-获取作业人员
    async getWorkers() {
      var that = this;
      if (!that.dialog.jobGroup.id) return;
      let { data } = await axios.get('/apiTrainRepair/workProcess/getPersonByDept', {
        params: {
          deptCode: that.dialog.jobGroup.id,
        },
      });
      if (data.code == 1) {
        that.dialog.workersOptions = data.data;
      } else {
        // this.$message({
        //   message: '获取作业人员失败',
        //   type: 'warning',
        // });
      }
    },

    async changeWorkTeam() {
      this.dialog.workers = [];
      this.dialog.workersOptions = [];
      await this.getWorkers();
    },

    //弹出层-获取确认人
    async getConfirmPeople() {
      var that = this;
      if (!that.dialog.confirmJobGroup.id) return;
      let { data } = await axios.get('/apiTrainRepair/workProcess/getPersonByDept', {
        params: {
          deptCode: that.dialog.confirmJobGroup.id,
        },
      });
      if (data.code) {
        that.dialog.confirmManOptions = data.data;
      }
    },

    async changeConfirmWorkTeam() {
      this.dialog.confirmMan = [];
      this.dialog.confirmManOptions = [];
      this.dialog.confirmWorkCarriage = '';
      this.dialog.confirmDateTo = '';
      await this.getConfirmPeople();
    },

    changeWorkerPeople() {
      if (this.dialog.confirmMan.length > 0) {
        this.dialog.confirmWorkCarriage = this.dialog.CRHCarriage.length;
        this.dialog.confirmDateTo = moment().format('YYYY-MM-DD HH:mm:ss');
      } else {
        this.dialog.confirmWorkCarriage = '';
        this.dialog.confirmDateTo = '';
      }
    },

    changeConfirmWorkCarriage() {
      if (this.dialog.confirmMan.length > 0) {
        this.changeInpit('confirmWorkCarriage');
      } else {
        this.dialog.confirmWorkCarriage = '';
        this.$message.warning('确认人员不可为空!');
      }
    },

    changeConfirmDateTo() {
      if (!this.dialog.confirmMan.length > 0) {
        this.dialog.confirmDateTo = '';
        this.$message.warning('确认人员不可为空!');
      }
    },

    // 根据班次获取工作时间
    async getWorkTimeByDayPlayId(dayPlanId) {
      let res = await axios.get('/apiTrainRepair/common/getWorkTimeByDayPlanId', {
        params: {
          dayPlanId,
        },
      });
      return res.data.data;
    },

    //弹出层-确定按钮点击事件
    onSave(formName) {
      let that = this;
      that.$refs[formName].validate(async (valid) => {
        if (valid) {
          let presentShift = await that.getWorkTimeByDayPlayId(that.dayPlanID);
          var isOk = true;

          if (!that.dialog.confirmJobGroup && (that.dialog.confirmMan.length > 0 || that.dialog.confirmWorkCarriage || that.dialog.confirmDateTo)) {
            that.$message({
              message: '请选择确认班组!',
              type: 'warning',
            });
          } else if (that.dialog.confirmMan.length == 0 && (that.dialog.confirmWorkCarriage || that.dialog.confirmDateTo)) {
            that.$message({
              message: '请选择确认人员!',
              type: 'warning',
            });
          } else if (
            (that.dialog.workCarriage && that.dialog.workCarriage > that.dialog.CRHCarriage.length) ||
            (that.dialog.confirmWorkCarriage && that.dialog.confirmWorkCarriage > that.dialog.CRHCarriage.length)
          ) {
            that.$message({
              message: '作业辆序数和确认辆序数不可大于车组辆序!',
              type: 'warning',
            });
            isOk = false;
          } else if (that.dialog.confirmDateTo && new Date(that.dialog.dateTo) > new Date(that.dialog.confirmDateTo)) {
            that.$message({
              message: '确认完成时间不可小于作业完成时间!',
              type: 'warning',
            });
            isOk = false;
          } else if (that.dialog.dateTo && new Date(presentShift.startTime).getTime() > new Date(that.dialog.dateTo).getTime()) {
            that.$message({
              type: 'warning',
              message: '作业结束时间不可小于当前班次开始时间',
            });
            isOk = false;
          } else {
            isOk = true;
          }

          if (isOk) {
            let processWorkerList = [];
            that.dialog.workers.forEach((item) => {
              processWorkerList.push({
                workerId: item.workId,
                workerName: item.workName,
              });
            });
         
            let processConfirmList = [];
            that.dialog.confirmMan.forEach((item) => {
              processConfirmList.push({
                workerId: item.workId,
                workerName: item.workName,
              });
            });

            let carNos = [],
              dayPlanId,
              itemCode,
              itemName,
              trainsetId,
              trainsetName;
            if (that.dialog.dialogCode == 'add') {
              that.dialog.CRHCarriage.forEach((item) => {
                item = item.split('');
                carNos.push(item.slice(item.length - 2, item.length).join(''));
              });
              dayPlanId = that.dialog.date + '-' + that.dialog.worktime;
              itemCode = that.dialog.jobProject.packetCode;
              itemName = that.dialog.jobProject.packetName;
              trainsetId = that.dialog.trainGroup.trainsetId;
              trainsetName = that.dialog.trainGroup.trainsetName;
            } else {
              carNos = that.currentData.carNos;
              dayPlanId = that.currentData.dayPlanId;
              itemCode = that.currentData.itemCode;
              itemName = that.currentData.itemName;
              trainsetId = that.currentData.trainsetId;
              trainsetName = that.currentData.trainsetName;
            }

            let postData = {
              carNos, // 辆序集合
              dayPlanId, // 日计划ID
              itemCode, // 项目编码
              itemName, // 项目名称
              trainsetId, // 车组ID
              trainsetName, // 车组名称

              workEndTime: that.dialog.dateTo, // 作业结束时间
              workCarCount: that.dialog.workCarriage, // 作业辆序数量
              confirmCarCount: that.dialog.confirmWorkCarriage, // 确认辆序数量
              confirmEndTime: that.dialog.confirmDateTo, // 确认结束时间
              deptCode: that.dialog.jobGroup.id, // 作业班组编码
              deptName: that.dialog.jobGroup.name, // 作业班组名称
              confirmDeptCode: that.dialog.confirmJobGroup.id, // 确认班组编码
              confirmDeptName: that.dialog.confirmJobGroup.name, // 确认班组名称
              processWorkerList, // 作业人员集合
              processConfirmList, // 确认人员集合
            };

            if (that.dialog.dialogCode == 'add') {
              var url_path = '/apiTrainRepair/workProcess/addIntegration';
              axios
                .post(url_path, postData)
                .then(checkResponse10)
                .then(function () {
                  that.showAlertMsg('添加成功!', 'success');
                  that.dialog.dialogTableVisible = false;
                  that.initTableList();
                })
                .catch((err) => {
                  that.$message({
                    message: '添加失败!',
                    type: 'warning',
                  });
                });
            } else if (that.dialog.dialogCode == 'update') {
              var url_path = '/apiTrainRepair/workProcess/updateIntegration';
              let res = await axios.post(url_path, postData);
              if (res.data.code == 1) {
                that.showAlertMsg('修改成功!', 'success');
                that.dialog.dialogTableVisible = false;
                that.initTableList();
              } else {
                that.$message({
                  message: '修改失败!',
                  type: 'warning',
                });
              }
            }
          }
        }
      });
    },
    //弹出层-取消
    onCancel(done) {
      this.dialog.dialogTableVisible = false;
      if (!this.isDisabled) {
        this.dialog.burecode = '';
      }

      // 作业班次
      this.dialog.date = '';
      this.dialog.worktime = '';
      // 车组号、车组列表
      this.dialog.trainGroup = {};
      this.dialog.trainGroupOptions = [];
      // 作业项目、项目列表
      this.dialog.jobProject = {};
      this.dialog.jobProjectOptions = [];
      // 车组辆序
      this.dialog.CRHCarriage = [];
      // 作业班组、班组列表
      this.dialog.jobGroup = {};
      this.dialog.jobGroupOptions = [];
      // 作业人员、人员列表
      this.dialog.workers = [];
      this.dialog.workersOptions = [];
      // 作业辆序
      this.dialog.workCarriage = '';
      // 作业结束时间
      this.dialog.dateTo = '';
      // 确认班组
      this.dialog.confirmJobGroup = {};
      // 确认人
      this.dialog.confirmMan = [];
      // 确认辆序
      this.dialog.confirmWorkCarriage = '';
      // 确认结束时间
      this.dialog.confirmDateTo = '';
    },
    handleSizeChange(val) {
      this.main.pageSize = val;
    },
    handleCurrentChange(val) {
      this.main.currentPage = val;
    },
    //补零
    toDou(n) {
      return n < 10 ? '0' + n : '' + n;
    },
    //提示文本样式统一
    showAlertMsg(msg, type, duration) {
      var options = {
        showClose: true,
        message: msg || '失败!',
        type: type || 'warning',
      };
      if (duration) {
        options.duration = duration;
      }
      this.$message(options);
    },
    //loading是否显示
    showLoading() {
      var loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)',
      });
    },
    hideLoading() {
      var loading = this.$loading();
      loading.close();
    },
  },
  components: {},
  filters: {
    //自定义过滤器
    dataTimeFilter(val) {
      if (val == '') return;
      var date = val.replace(/^(\d{4})(\d{2})(\d{2})$/, '$1-$2-$3');
      return date;
    },

    operationShift(val) {
      let end = val.split('-').pop();
      let time = val.split('-').slice(0, 3).join('-');
      if (end == '00') {
        return time + '-' + '白班';
      } else {
        return time + '-' + '夜班';
      }
    },

    // 列表作业人员
    peopleName(val) {
      let list = [];
      if (val.length != 0) {
        val.forEach((item) => {
          list.push(item.workerName);
        });
      }
      return list.join(',');
    },
  },
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('一体化作业管理'),
    //计算属性
    workerTypeMap() {
      var map = {};
      this.workerTypes &&
        this.workerTypes.forEach((workerType) => {
          map[workerType.value] = workerType.label;
        });
      return map;
    },

    // 作业班次拼接
    dayPlanID() {
      return this.dialog.date + '-' + this.dialog.worktime;
    },
  },
  watch: {
    // 监听数据改变
    'dialog.jobProjectOptions'() {
      this.dialog.jobProject = null;
    },
  },
  beforeDestroy() {},
});
