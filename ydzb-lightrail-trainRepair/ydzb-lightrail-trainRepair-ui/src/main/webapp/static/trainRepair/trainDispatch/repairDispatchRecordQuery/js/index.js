window.main = new Vue({
  el: '#app',
  data() {
    let timeGap = 30 * 24 * 60 * 60 * 1000;
    let getTimeNum = (time) => {
      return new Date(time).getTime();
    };
    let getNowTimeGap = (start, end) => {
      return getTimeNum(start) - getTimeNum(end);
    };
    let validateStartDate = (rule, value, callback) => {
      if (value && this.queryFormData.endDate) {
        let nowTimeGap = getNowTimeGap(value, this.queryFormData.endDate);
        if (nowTimeGap > 0) {
          callback(new Error('开始日期不能大于结束日期'));
        } else if (Math.abs(nowTimeGap) > timeGap) {
          callback(new Error('开始和结束日期不能间隔超一个月'));
        } else {
          callback();
        }
      } else {
        callback();
      }
    };
    let validateEndDate = (rule, value, callback) => {
      if (this.queryFormData.startDate && value) {
        let nowTimeGap = getNowTimeGap(this.queryFormData.startDate, value);
        if (nowTimeGap > 0) {
          callback(new Error('结束日期不能小于开始日期'));
        } else if (Math.abs(nowTimeGap) > timeGap) {
          callback(new Error('开始和结束日期不能间隔超一个月'));
        } else {
          callback();
        }
      } else {
        callback();
      }
    };
    return {
      isCenter: true,
      queryFormData: {
        // 运用所编码
        unitCode: '',
        // 开始时间
        startDate: '',
        // 结束时间
        endDate: '',
        // 车型集合
        trainType: '',
        // 车组ID集合
        trainsetIdList: [],
        // 作业类型：
        repairType: '',
        // 作业班组ID集合
        branchCode: '',
        // 作业任务：
        packetName: '',
        // 作业项目
        itemName: '',
        // 作业人员
        workerName:'',
        // itemCodeList: [],
        // workIdList: [],
      },
      queryFormRules: {
        unitCode: [],
        startDate: [
          {
            required: true,
            message: '开始日期不能为空',
            trigger: 'blur',
          },
          {
            validator: validateStartDate,
            trigger: 'blur',
          },
        ],
        endDate: [
          {
            required: true,
            message: '结束日期不能为空',
            trigger: 'blur',
          },
          {
            validator: validateEndDate,
            trigger: 'blur',
          },
        ],
        trainType: [],
        trainsetIdList: [],
        repairType: [],
        branchCode: [],
        packetName:[],
        itemName:[],
        workerName:[],
      },
      unitList: [],
      allTrainsetList: [],
      itemTypeList: [],
      workTeamList: [],
      workList: [],
      taskAllotData: [],
      tableHeight: 0,
      pageNum: 1,
      pageSize: 20,
      tablePageTotal: 0,
      branchNumMap: Object.freeze({
        '00': '白班',
        '01': '夜班',
      }),
    };
  },
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('检修派工查询'),
    trainsetIdTooltipShow({ queryFormData }) {
      return queryFormData.trainsetIdList.toString();
    },
    itemTypeMap({ itemTypeList }) {
      return itemTypeList.reduce((prev, item) => {
        prev[item.itemTypeCode] = item.itemTypeName;
        return prev;
      }, {});
    },
    trainTypeList({ allTrainsetList }) {
      // 去重+组合
      const map = allTrainsetList.reduce((prev, item) => {
        if (!prev.has(item.traintype)) {
          prev.set(item.traintype, {
            traintype: item.traintype,
          });
        }
        return prev;
      }, new Map());
      let arr = [...map.values()];

      return arr;
    },
    trainsetList({ allTrainsetList, queryFormData }) {
      let arr = [...allTrainsetList];
      // 过滤条件
      if (queryFormData.trainType) {
        arr = arr.filter((item) => item.traintype == queryFormData.trainType);
      }

      return arr;
    },
    // taskAllotDataAfterWorkerFilter({ taskAllotData, worker }) {
    //   return taskAllotData.filter(taskAllot => {
    //     return taskAllot.workerName.includes(worker)
    //   })
    // },
    // groupTaskAllotData({ taskAllotDataAfterWorkerFilter, tablePageSize }) {
    //   return taskAllotDataAfterWorkerFilter.reduce((prev, item, index) => {
    //     let pageNum = parseInt(index / tablePageSize) + 1;
    //     if (!prev[pageNum]) {
    //       prev[pageNum] = [];
    //     }
    //     prev[pageNum].push(item);
    //     return prev;
    //   }, {});
    // }
  },
  watch: {
    // taskAllotDataAfterWorkerFilter(val) {
    //   this.tablePageTotal = val.length
    //   this.tablePageNum = 1
    // },
  },

  filters: {
    carNosStr(val) {
      return val.join(',')
    }
  },

  created() {
    const nowDate = dayjs().format('YYYY-MM-DD');
    this.queryFormData.startDate = nowDate;
    this.queryFormData.endDate = nowDate;

    this.getItemType();
    this.getTrainsetList();
  },

  async mounted() {
    await this.getIsCenter();
    this.getTableHeight();
    window.addEventListener('resize', this.getTableHeight);
    this.$once('hook:beforeDestroy', () => {
      window.removeEventListener('resize', this.getTableHeight);
    });
    this.getQueryTaskAllotList()
  },
  methods: {
    getTableHeight() {
      this.$nextTick(() => {
        this.tableHeight = '100%';
      });
    },
    async getIsCenter() {
      // let res = await getIsCenter()
      // console.log(res)
      this.isCenter = await getIsCenter();
      await this.getUnitList();
    },
    async getUnitList() {
      this.unitList = await getUnitList();
      this.queryFormData.unitCode = this.unitList[0].unitCode;
    },
    unitCodeChange() {
      this.queryFormData.branchCode = ''
    },

    async getTrainsetList() {
        const data = await getTrainsetList();
        this.allTrainsetList = data;
    
    },
    trainsetTypeChange() {
      this.queryFormData.trainsetIdList = [];
    },
    async getItemType() {
      this.itemTypeList = await getItemType();
    },
    async getWorkTeamsByUnitCode(unitCode) {
      try {
        const {
          data: { rows },
        } = await getWorkTeamsByUnitCode(unitCode);
        this.workTeamList = rows;
      } catch (error) {
        this.$message.error('作业班组数据异常');
      }
    },
    branchCodeChange(val) {
      this.queryFormData.workerName = ''
      this.workList = []
      if (!val) return;
      this.getPersonByDept();
    },
    async getPersonByDept() {
      try {
        const {
          data: { data },
        } = await getPersonByDept(this.queryFormData.branchCode);
        this.workList = data;
      } catch (error) {
        this.$message.error('作业人员数据异常');
      }
    },
    querySearch(queryString, cb) {
      let res = queryString
        ? this.workList.filter((worker) => worker.workName.includes(queryString))
        : this.workList;
      cb(res);
    },
    async getQueryTaskAllotList() {
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      try {
        await this.$refs.queryFormRef.validate();
        const {records,total} = await getQueryTaskAllotList({
          ...this.queryFormData,
          trainsetTypeList: this.queryFormData.trainType ? [this.queryFormData.trainType] : [],
          branchCodeList: this.queryFormData.branchCode ? [this.queryFormData.branchCode] : [],
          pageNum:this.pageNum,
          pageSize:this.pageSize
        });
        loading.close();
        this.taskAllotData = records
        this.tablePageTotal = total
      } catch (error) {
        loading.close();
        console.log(error);
      }
    },
    resetQueryFormFields() {
      this.$refs.queryFormRef.resetFields();
      this.getQueryTaskAllotList()
      this.$message({
        type: 'success',
        message: '重置成功',
      });
    },
    packetTypeFormatter(row) {
      if(row.packetType=='1'){
        if(row.repairType=='1'){
          return this.itemTypeMap['0']
        }else{
          return this.itemTypeMap['1']
        }
      }else{
        return this.itemTypeMap[row.packetType]
      }

    },
    dayPlanFormatter(row) {
      const dayPlan = row.dayPlanId;
      const branchNum = this.branchNumMap[dayPlan.slice(-2)];
      return dayPlan.slice(0, -3) + ' ' + branchNum;
    },
    dayPlanSort(a, b) {
      const aNum = parseInt(a.dayPlanId.split('-').join(''));
      const bNum = parseInt(b.dayPlanId.split('-').join(''));
      return aNum - bNum;
    },
    pageNumChange(pageNum) {
      this.pageNum = pageNum
      this.getQueryTaskAllotList()
    },
    pageSizeChange(pageSize) {
      this.pageSize = pageSize
      this.getQueryTaskAllotList()
    },

  },
});
