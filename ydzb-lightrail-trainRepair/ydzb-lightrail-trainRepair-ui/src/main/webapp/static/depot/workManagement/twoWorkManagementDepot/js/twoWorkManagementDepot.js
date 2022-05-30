var checkResponse10Warppr = (defaultMessage) => {
  return (res) => {
    return new Promise((resolve, reject) => {
      if (res.data.code === 1) {
        resolve(res.data.data)
      } else {
        fault.$message({ type: "error", message: res.data.msg || defaultMessage || "请求失败" })
        reject()
      }
    })
  }
}
var fault = new Vue({
  el: '#one',
  data: {
    unitCode: '',
    tableData: [],
    operationDepot: "",//运用所
    operationDepotList: [],
    trainType: "",//车型
    trainTypeList: [],
    chezuH: "",//车组号
    chezuHList: [],
    zyBz: "",//作业班组
    zyBzList: [],
    zyState: "",//作业状态
    zyStateList: [],
    packetCode: '', // 作业包
    zyPeople: "",//作业人员
    zyPeopleList: [],
    starTime: "",//开始日期
    endTime: "",//结束日期
    pageNum: 1,
    pageSize: 10,
    total: 400,
    scrollHeight: "calc(100% - 42px)",
    sortTxet: "",//排序的字段
    sortTy: "",//排序的方法

    dialogVisibleList: false,//列表的编辑
    dialogVisiblePic: false,//点击dialog的上传图片
    dialogVisibleNum: false,//点击列表项目数量
    dialogVisiblePicCount: false,//点击列表的实际图片数量

    xiangmuList: [],//点击项目数量的值

    // 点击新增按钮
    yys: "",//运用所
    yysList: [],
    zybc: "", //作业班次
    alltimep: "",
    zybcList: [{ id: "00", name: "白班" }, { id: "01", name: "夜班" }],
    czh: "", //车组号
    czhList: [],
    zybz: "", //作业班组
    zybzList: [],
    zyry: "", //作业人员
    zyryList: [],
    stime: "", //开始时间
    etime: "", //结束时间
    zyb: "", //作业包
    zybList: [],
    zyxm: [], //作业项目
    zyxmList: [], // 作业项目列表

    // 点击列表编辑
    zybcb: "", //作业班次
    alltimeb: "",
    zybcListb: [{ id: "00", name: "白班" }, { id: "01", name: "夜班" }],
    czhb: "", //车组号
    czhListb: [],
    zybzb: "", //作业班组
    zybzListb: [],
    zyryb: "", //作业人员
    zyryListb: [],
    stimeb: "", //开始时间  时间选择
    etimeb: "", //结束时间
    zybb: "", //作业包
    zybbListb: [],
    zyxmb: "", //作业项目
    zyxmbListb: [],
    rowsObj: {}, // 保存当前项全部信息


    ZYLX: "",//上传图片的作业辆序
    ZYLXList: [],
    imgLength: "0",//图片的数量
    imgList: {},
    fileList: {},
    editExistingPictures: [], // 保存编辑-数据项内所含的图片信息
    backupsImgList: {}, // 保存编辑-备份列表象展示图片
    backupseditExistingPictures: {}, // 保存编辑-备份数据项内所含的图片信息
    newFileList: {}, // 保存编辑-备份编辑中新增的图片信息
    ObjList: {},
    imgKey: 1
  },
  created() {
    //初始化
    this.getUnitCode()
    this.getFirstDate()
  },
  mounted() {
    //dom操作
    this.getUnitList()
    this.listHandle()
    this.trainTypeHandle()
    this.zyBzHandle()
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

      this.imgList = {}
      rows.processPicList.forEach(item => {
        if (!this.imgList[item.carNo]) {
          this.imgList[item.carNo] = []
        }
        this.imgList[item.carNo].push(imgURL + item.picAddress)
      })
    },

    //查询按钮
    clickList: function () {
      var that = this
      if (that.endTime < that.starTime) {
        that.$message({
          type: 'info',
          message: '结束日期不能早于开始日期'
        })
      } else {
        this.listHandle()
      }
    },
    //列表接口
    listHandle: function () {
      var that = this
      axios.post("/apiTrainRepair/workProcess/getTwoWorkProcessList", {
        startTime: that.starTime + ' ' + '00:00:00',// 开始时间
        endTime: that.endTime + ' ' + '24:00:00',// 结束时间
        workerId: that.zyPeople, // 作业人员
        deptCode: that.zyBz,// 作业班组
        trainsetId: that.chezuH,// 车组号
        packetCode: that.packetCode, // 作业包
        pageNum: that.pageNum, // 当前页
        pageSize: that.pageSize,// 页大小
        trainsetType: that.trainType,// 车型
        unitCode: that.operationDepot, // 所编码
      })
        .then(function (res) {
          that.tableData = res.data.data.twoWorkProcessList;
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
      var that = this
      that.starTime = new Date() // 开始时间
      that.endTime = new Date() // 结束时间
      that.trainType = "";//车型
      that.chezuH = "";//车组号
      that.zyBz = "";//作业班组
      that.packetCode = "";//作业包
      that.zyPeople = "";//作业人员
      that.$message({
        type: 'success',
        message: '重置成功'
      });
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

          that.ZYLXList = list
        }).catch(() => {
          this.$message({
            message: '获取作业辆序失败',
            type: 'warning'
          });
        })
    },

    //点击列表项目数量
    numberHandle: function (rows) {
      this.dialogVisibleNum = true
      this.xiangmuList = rows.processPacketList[0]
      this.rowsObj = rows
    },

    //车型
    trainTypeHandle: function () {
      var that = this
      axios.get("/apiTrainResume/resume/getTraintypeList")
        .then(function (res) {
          that.trainTypeList = res.data.data
          that.chezuHHandle()
        }).catch(() => {
          that.$message({
            message: '获取车型失败',
            type: 'warning'
          });
        })
    },
    //车组号  根据车型
    chezuHHandle: function () {
      var that = this
      axios.post("/apiTrainResume/resume/getTrainsetNameList", { traintype: that.trainType })
        .then(function (res) {
          that.chezuHList = res.data.data
        }).catch(() => {
          that.$message({
            message: '获取车组失败',
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


    // 作业人员 选择作业班组后 作业人员跟随变化
    zyPeopleHandle: function () {
      var that = this
      that.zyry = []
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
    //改变页码大小
    handleSizeChange: function (val) {
      this.pageSize = val
      this.listHandle()
    },
    //改变页码
    handleCurrentChange: function (val) {
      this.pageNum = val
      this.listHandle()
    },
    // 改变排序
    changeSortHandle: function (sort) {
      this.sortTxet = sort.prop
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
    //dialog的关闭
    handleClose(done) {
      var that = this
      this.$confirm('确认关闭？', {
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
    }
  },
  components: {

  },
  filters: {
    //自定义过滤器

  },
  computed: {

  },
  watch: {
    //监听数据改变

  },
  filters: {
    operationShift(val) {
      let end = val.split('-').pop()
      if (end == '00') {
        return '白班'
      } else {
        return '夜班'
      }
    },

    carNosFilter(val) {
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

    projectName(val) {
      let str = []
      val.forEach(item => {
        str.push(item.itemName)
      })

      return str.join(';')
    }
  },
  beforeDestroy() {

  }
});
