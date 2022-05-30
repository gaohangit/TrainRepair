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
var notFixed = new Vue({
  el: '#notFixed',
  data: {
    unitCode: '',
    isDisa: false,
    isDisabled: false,
    loading: false,
    burecode: '',
    updatePersonId: '',
    search: {
      operationDepot: '', // 运用所
      operationDepotList: [],
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
      isDisjobGroup: false,
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
      trainGroup: {},
      trainGroupOptions: [],
      CRHCarriage: '', // 车组辆序
      jobTypesOptions: [],
      jobProject: {},
      jobProjectOptions: [],
      jobGroup: {}, // 作业班组
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
    main: {
      scrollHeight: 'calc(100% - 42px)',
      tableData: [],
      pageSize: 100,
      currentPage: 1,
    },
    restaurants: [],

    workerTypes: [],
    // 是否需要确认
    needConfirmSkippedNodes: true,

    userInfo: null,
  },
  created: function () {
    //初始化
    this.getUnitCode()
    this.getUnitList();
    this.getTrainType(); //获取搜索栏车型
    this.zyBzHandle(); // 获取搜索作业班组
  },
  mounted: function () {
    this.initTableList();
  },
  methods: {
    // 获取当前登录人运用所编码
    async getUnitCode() {
      let { data } = await axios.get('/apiTrainRepair/common/getUnitCode');
      if (data.code == 1) {
        this.unitCode = data.data;
      } else {
        this.$message.error('获取登录人信息失败！');
      }
    },

    // 获取运用所下拉列表
    async getUnitList() {
      let res = await axios.get('/apiTrainRepair/common/getUnitList');
      if (res.data.code == 1) {
        this.search.operationDepotList = res.data.data;
      } else {
        this.$message({
          message: '获取运用所信息失败',
          type: 'warning',
        });
      }
    },

    //搜索栏-获取车型
    getTrainType: function (code) {
      var that = this;
      var trainType_url_path = '/apiTrainResume/resume/getTraintypeList';
      axios
        .get(trainType_url_path)
        .then(function (res) {
          var data = res.data.data;
          that.search.trainTypeOptions = data;
          console.log(1);
          that.changeTrainType();
        })
        .catch(function (err) {
          //that.showAlertMsg('运用所接口调用失败!','warning')
        });
    },
    //搜索栏-获取车组
    changeTrainType: function () {
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
    zyBzHandle: function (code) {
      var that = this;
      axios
        .get('/apiTrainRepair/common/getWorkTeamsByUnitCode', {
          params: {
            unitCode: that.search.operationDepot ? that.search.operationDepot : that.unitCode,
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
    initTableList: function () {
      this.loading = true;
      var that = this;
      setTimeout(function () {
        var trainType_url_path = '/apiTrainRepair/workProcess/getIntegrationList';
        axios
          .post(trainType_url_path, {
            startTime: that.search.dateFrom, // 开始时间
            endTime: that.search.dateTo, // 结束时间
            deptCode: that.search.jobGroup, // 作业班组编码
            trainsetId: that.search.trainGroup, // 车组ID
            trainsetType: that.search.trainType, // 车型
            pageNum: that.main.currentPage, // 页码
            pageSize: that.main.pageSize, // 每页显示数据条数
            unitCode: that.search.operationDepot, // 所编码
          })
          .then(function (res) {
            console.log(res.data.data.integrationList);
            that.loading = false;
            var data = res.data.data.integrationList;
            that.main.tableData = data;
          })
          .catch(function (err) {
            that.loading = false;
            that.showAlertMsg('获取表数据失败!', 'warning');
          });
      }, 500);
    },

    //点击搜索
    onSearch: function () {
      this.loading = true;
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
    reset: function () {
      if (!this.isDisabled) {
        this.search.burecode = '';
      }
      this.search.dateFrom = moment().format('YYYY-MM-DD');
      this.search.dateTo = moment().format('YYYY-MM-DD');
      this.search.trainType = '';
      this.search.trainGroup = '';
      this.search.jobGroup = '';
    },

    handleSizeChange: function (val) {
      this.main.pageSize = val;
    },
    handleCurrentChange: function (val) {
      this.main.currentPage = val;
    },
    //补零
    toDou: function (n) {
      return n < 10 ? '0' + n : '' + n;
    },
    //提示文本样式统一
    showAlertMsg: function (msg, type, duration) {
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
    showLoading: function () {
      var loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)',
      });
    },
    hideLoading: function () {
      var loading = this.$loading();
      loading.close();
    },
  },
  components: {},
  filters: {
    //自定义过滤器
    dataTimeFilter: function (val) {
      if (val == '') return;
      var date = val.replace(/^(\d{4})(\d{2})(\d{2})$/, '$1-$2-$3');
      return date;
    },

    // 列表作业人员
    peopleName(val) {
      let list = [];
      console.log(val);
      if (val.length != 0) {
        val.forEach((item) => {
          list.push(item.workerName);
        });
      }
      return list.join(',');
    },
  },
  computed: {
    //计算属性
    workerTypeMap() {
      var map = {};
      this.workerTypes &&
        this.workerTypes.forEach((workerType) => {
          map[workerType.value] = workerType.label;
        });
      return map;
    },

    operationShift(val) {
      let end = val.split('-').pop();
      if (end == '00') {
        return '白班';
      } else {
        return '夜班';
      }
    },

    // 作业班次拼接
    dayPlanID() {
      return this.dialog.date + '-' + this.dialog.worktime;
    },
  },
  watch: {
    // 监听数据改变
    'dialog.jobProjectOptions': function () {
      this.dialog.jobProject = null;
    },
  },
  beforeDestroy: function () {},
});
