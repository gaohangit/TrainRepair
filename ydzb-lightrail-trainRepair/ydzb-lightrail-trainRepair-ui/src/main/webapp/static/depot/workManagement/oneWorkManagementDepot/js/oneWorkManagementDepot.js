var checkResponse10Warppr = (defaultMessage) => {
  return (res) => {
    return new Promise((resolve, reject) => {
      if (res.data.code === 1) {
        resolve(res.data.data)
      } else {
        fault.$message({
          type: "error",
          message: res.data.msg || defaultMessage || "请求失败"
        })
        reject()
      }
    })
  }
}

window.fault = new Vue({
  el: '#one',
  data: {
    unitCode: '',
    tableData: [],
    operationDepot: "", //运用所
    operationDepotList: [],
    trainsetType: "", //车型
    trainTypeList: [],
    chezuH: "", //车组号
    chezuHList: [],
    zyBz: "", //作业班组
    zyBzList: [],
    zyPeople: "", //作业人员
    timeStatus: '', //作业状态
    zyPeopleList: [],
    starTime: "", //开始日期
    endTime: "", //结束日期
    pageNum: 1,
    pageSize: 10,
    total: 400,
    scrollHeight: "calc(100% - 42px)",
    dialogVisibleWorkTime: false, //点击作业时长弹框
    dialogVisiblePicCount: false, //点击列表的实际图片数量

    sortTxet: "", //排序的字段
    sortTy: "", //排序的方法

    czh: "", //车组号
    czhList: [],
    zyry: [], //作业人员
    zyryList: [],
    stime: "", //开始时间
    etime: "", //结束时间
    zynr: "", //作业内容
    zynrList: [],
    zylx: "", //作业辆序
    zylxList: [],
    // 点击列表编辑
    yysb: "", //运用所
    yysListb: [],
    zybcb: "", //作业班次
    alltimeb: "",
    zybcListb: [{
      id: "00",
      name: "白班"
    }, {
      id: "01",
      name: "夜班"
    }],
    itemList: {}, // 用于保存点击编辑按钮的当条数据
    czhb: "", //车组号
    czhListb: [],
    zybzb: "", //作业班组
    zybzListb: [],
    zyryb: "", //作业人员
    zyryListb: [],
    stimeb: "", //开始时间  时间选择
    etimeb: "", //结束时间
    zynrb: "", //作业内容
    zynrListb: [],
    zylxb: "", //作业辆序
    zylxListb: [],
    ZYLX: "", //上传图片的作业辆序
    ZYLXList: [],
    imgLength: "0", //图片的数量
    imgList: {}, // 要展示的图片路径
    fileList: {}, // 上传图片file对象储存
    workLong: "", //点击作业时长
    workDeteil: [], //作业详情
    editExistingPictures: [], // 保存编辑-数据项内所含的图片信息
    backupsImgList: {}, // 保存编辑-备份列表象展示图片
    backupseditExistingPictures: {}, // 保存编辑-备份数据项内所含的图片信息
    newFileList: {}, // 保存编辑-备份编辑中新增的图片信息
    imgKey: 1,
  },
  created() {
    //初始化
    // this.scrollHeight = ($(window).height() - 250) + 'px';
    // this.getUser()
    this.getFirstDate()
    this.getUnitCode()

  },
  mounted() {
    //dom操作
    this.getUnitList()
    // 获取班组
    this.zyBzHandle()
    this.trainTypeHandle()
    // 获取列表
    this.listHandle()
  },
  methods: {
    tableRowClassName({ row, rowIndex }) {
      if (rowIndex % 2 == 1) {
        return 'warning-row';
      } else {
        return 'success-row';
      }
    },

    // 获取当前登录人运用所编码
    async getUnitCode () {
      let {data} = await axios.get("/apiTrainRepair/common/getUnitCode")
      if (data.code == 1) {
        this.unitCode = data.data
      } else {
        this.$message.error("获取登录人信息失败！");
      }
    },

    // 获取运用所下拉列表
    async getUnitList() {
      let res = await axios.get('/apiTrainRepair/common/getUnitList');
      if (res.data.code == 1) {
        this.operationDepotList = res.data.data;
      } else {
        this.$message({
          message: '获取运用所信息失败',
          type: 'warning',
        });
      }
    },

    //点击列表的实际图片数量
    ClickPicCount: function (rows) {
      this.dialogVisiblePicCount = true;
      this.getCarNoByTrainsetID(rows.trainsetId)
      this.itemList = rows
      this.ZYLXList = rows.carNos
      this.imgList = {}
      rows.processPicList.forEach(item => {
        if (!this.imgList[item.carNo]) {
          this.imgList[item.carNo] = []
        }
        this.imgList[item.carNo].push(imgURL + item.picAddress)
      })
    },

    //获取作业辆序  根据车组号
    getCarNoByTrainsetID(val) {
      let that = this
      let id = val

      axios.post("/apiTrainResume/resume/getCarno?trainsetid=" + id)
        .then(function (res) {
          let list = []
          res.data.data.forEach(item => {
            item = item.split('')
            list.push(item.slice(item.length - 2, item.length).join(''))
          })

          that.zylx = list
          that.ZYLXList = list
          that.zylxList = list
        }).catch(() => {
          this.$message({
            message: '获取作业辆序失败',
            type: 'warning'
          });
        })
    },

    zylxChange(val) {
      this.ZYLXList = val
    },

    //点击列表的作业时长
    WorkTimeHandle: function (rows) {
      let that = this
      that.workLong = rows;
      that.dialogVisibleWorkTime = true
      // 获取作业详情
      axios.get("/apiTrainRepair/workProcess/getRfidCardSummaryList", {
        params: {
          trainsetId: rows.trainsetId, //车组ID
          dayPlanId: rows.dayPlanId, //作业班次(日计划)
          workerId: rows.workerId // 作业人员
        }
      }).then(function (res) {
        that.workDeteil = res.data.data.queryOneWorkProcessDataList
      })
    },
    changeSortHandle: function (sort) {
      this.sortTxet = sort.prop;
      if (sort.order == null) {
        this.sortTy = ""
      } else {
        if (sort.order == "ascending") {
          this.sortTy = sort.order.slice(0, 3)
        } else {
          this.sortTy = sort.order.slice(0, 4)
        }
      }
      this.listHandle()
    },

    //查询按钮
    clickList: function () {
      var that = this;
      if (that.endTime < that.starTime) {
        that.$message({
          type: 'info',
          message: '结束日期不能早于开始日期'
        });
        $('.endtime').focus();
      } else {
        this.listHandle()
      }
    },
    //列表接口
    listHandle: function () {
      var that = this;
      let data = {
        trainsetId: that.chezuH, //车组号
        trainsetType: that.trainsetType, //车型
        trainsetSubType: '', //批次
        deptCode: that.zyBz, //作业班组
        workerId: that.zyPeople, //作业人员
        startTime: that.starTime + ' ' + '00:00:00', // 开始时间
        endTime: that.endTime + ' ' + '24:00:00', // 结束时间
        timeStatus: that.timeStatus, // 作业状态
        pageSize: that.pageSize, //页大小
        pageNum: that.pageNum, //当前页
        unitCode: that.operationDepot, // 所编码
      }
      axios.post("/apiTrainRepair/workProcess/getOneWorkProcessList", data)
        .then(function (res) {
          that.tableData = res.data.data.queryOneWorkProcessDataList;
          that.total = res.data.data.count
        }).catch(() => {
          this.$message({
            message: '获取列表失败',
            type: 'warning'
          });
        })
    },
    //重置按钮
    repeatHandle: function () {
      var that = this;
      that.starTime = new Date() // 开始时间
      that.endTime = new Date() // 结束时间
      that.trainsetType = ""; //车型
      that.chezuH = ""; //车组号
      that.zyBz = ""; //作业班组
      that.zyPeople = ""; //作业人员
      that.timeStatus = ''; // 作业状态
      that.$message({
        type: 'success',
        message: '重置成功'
      });
    },

    //车型
    trainTypeHandle: function () {
      var that = this
      axios.get("/apiTrainResume/resume/getTraintypeList")
        .then(function (res) {
          that.trainTypeList = res.data.data
          that.chezuHHandle()
        }).catch(() => {
          this.$message({
            message: '获取车型失败',
            type: 'warning'
          });
        })
    },

    //车组号  根据车型改变车组号
    chezuHHandle: function () {
      var that = this
      axios.post("/apiTrainResume/resume/getTrainsetNameList", { traintype: that.trainsetType })
        .then(function (res) {
          that.chezuHList = res.data.data
        }).catch(() => {
          this.$message({
            message: '获取车组号失败',
            type: 'warning'
          });
        })
    },

    //作业班组 作业人员跟着改变
    zyBzHandle: function () {
      var that = this
      axios.get('/apiTrainRepair/common/getWorkTeamsByUnitCode', {
        params: {
          unitCode: that.operationDepot ? that.operationDepot : this.unitCode
        }
      })
        .then(function (res) {
          that.zyBzList = res.data.rows
        }).catch(() => {
          this.$message({
            message: '获取作业班组失败',
            type: 'warning'
          });
        })
    },

    //作业人员 选择作业班组后 作业人员跟随变化
    zyPeopleHandle: function () {
      var that = this
      var id = that.zyBz
      axios.get("/apiTrainRepair/workProcess/getPersonByDept", {
        params: {
          deptCode: id,
        }
      })
        .then(function (res) {
          that.zyPeople = ''
          that.zyPeopleList = res.data.data
        }).catch(() => {
          this.$message({
            message: '获取作业人员失败',
            type: 'warning'
          });
        })
    },
    //获取开始时间结束时间
    getFirstDate: function (date) {
      var date = new Date()
      var year = date.getFullYear().toString()
      var month = date.getMonth() + 1
      if (month < 10) {
        month = "0" + month
      }
      var da = date.getDate()
      if (da < 10) {
        da = "0" + da
      }
      var end = year + '-' + month + "-" + da
      var beg = year + "-" + month + "-" + "1"
      this.value9 = [beg, end]
      this.starTime = end
      this.endTime = end
    },
    handleSizeChange: function (val) {
      this.pageSize = val
      this.listHandle()
    },
    handleCurrentChange: function (val) {
      this.pageNum = val
      this.listHandle()
    },
    handleClose(done) {
      var that = this
      that.$confirm('确认关闭？', {
        confirmButtonText: '取消',
        cancelButtonText: '确定',
      })
        .then(function () {
          that.$message({
            type: 'info',
            message: '取消关闭'
          });
        })
        .catch(function () {
          done();
          that.zyBzHandle()
        })
    },
  },
  components: {

  },
  computed: {

    dayPlanIDb() {
      if (this.alltimeb && this.zybcb) {
        return this.alltimeb + "-" + this.zybcb
      } else {
        return ""
      }
    }
  },
  watch: {
    //监听数据改变

  },
  filters: {
    operationShift(val) {
      if (val) {
        let end = val.split('-').pop()
        if (end == '00') {
          return '白班'
        } else {
          return '夜班'
        }
      }
    },

    carNosFilter (val) {
      if (Array.isArray(val)) {
        let list = []
        val.forEach(item => {
          item = item.split('')
          list.push(item.slice(item.length - 2, item.length).join(''))
        })
        return list.join(',')
      } else {
        val = val.split('')
        return val.slice(val.length - 2, val.length).join('')
      }
    },

    timeTagFilter (val) {
      if (val == 0) {
        return '开始'
      } else if ( val == 1) {
        return '暂停'
      } else if ( val == 2) {
        return '继续'
      } else if ( val == 3) {
        return '结束'
      }
    }
  },
  beforeDestroy() {

  }
});