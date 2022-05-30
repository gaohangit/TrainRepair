var checkResponse10Warppr = (defaultMessage) => {
  return (res) => {
    return new Promise((resolve, reject) => {
      if (res.data.code === 1) {
        resolve(res.data.data)
      } else {
        fault.$message({ type: 'error', message: res.data.msg || defaultMessage || '请求失败' })
        reject()
      }
    })
  }
}
window.fault = new Vue({
  el: '#one',
  data() {
    return {
      unitCode: '', //运用所

      // 搜索部分
      search: {
        starTime: '', //开始日期
        endTime: '', //结束日期
        trainsetType: '', //车型
        trainsetId: '', // 车组
        workTeam: '', // 作业班组
        workPackage: '', //作业包
        workPeople: '', //作业人员
      },
      trainTypeList: [], //车型列表
      trainsetList: [], //车组列表
      workTeamList: '', // 作业班组列表
      workPeopleList: '', //作业人员列表

      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 400,
      scrollHeight: 'calc(100% - 140px)',

      // 新增和修改部分
      twoWorkProcessDialog: {
        visible: false,
        addOrUpdate: '',
        // 作业班次
        time: '',
        shift: '',
        trainset: {}, // 车组
        workPackage: {}, // 作业包
        workProject: [], // 作业项目
        workTeam: '', // 作业班组
        workPeople: '', //作业人员
        starTime: '', //开始日期
        endTime: '', //结束日期
        imgLength: '0', //图片的数量

        workProjectUpdate: '',
        workPeopleUpdate: '',
      },

      twoWorkProcessDialogRules: {
        time: [{ required: true, message: '请选择作业班次', trigger: 'change' }],
        trainset: [{ required: true, message: '请选择车组号', trigger: 'change' }],
        workPackage: [{ required: true, message: '请选择作业包', trigger: 'change' }],
        workProject: [{ required: true, message: '请选择作业项目', trigger: 'change' }],
        workTeam: [{ required: true, message: '请选择作业班组', trigger: 'change' }],
        workPeople: [{ required: true, message: '请选择作业人员', trigger: 'change' }],
        starTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
      },

      shiftList: [
        {
          id: '00',
          name: '白班',
        },
        {
          id: '01',
          name: '夜班',
        },
      ],
      addTrainsetList: [], // 车组列表
      workPackageList: [], // 作业包列表
      workConentList: [], // 作业内容列表
      addWorkTeamList: [], // 作业班组列表
      addWorkPeopleList: [], //作业人员列表
      workProjectList: [], // 作业项目列表
      xiangmuList: {}, //点击项目数量的值

      //上传图片
      uploadPicture: {
        visible: false,
        workercarNo: '',
        // 添加图片作业项目
        uploadPictureproject: '',
        uploadImgList: {}, // 上传图片列表
        projectImgListOpen: [],
        carNoImgListOpen: [],
        ok: false,// 是否是点击确定按钮结束的
      },
      carNoList: [],

      dialogVisibleNum: false, //点击列表项目数量
      dialogVisiblePicCount: false, //点击列表的实际图片数量

      // 点击列表编辑
      zybcb: '', //作业班次
      alltimeb: '',
      zybcListb: [
        { id: '00', name: '白班' },
        { id: '01', name: '夜班' },
      ],

      rowsObj: {}, // 保存当前项全部信息

      fileList: {},
      editExistingPictures: [], // 保存编辑-数据项内所含的图片信息
      backupsImgList: {}, // 保存编辑-备份列表象展示图片
      backupseditExistingPictures: {}, // 保存编辑-备份数据项内所含的图片信息
      newFileList: {}, // 保存编辑-备份编辑中新增的图片信息
      ObjList: {},
      // 存放项目名称
      projectCodeMap: {},
    }
  },

  async created() {
    await this.getUnitCode() // 获取运用所编码
    //初始化
    await this.initData()
    await this.getTwoWorkProcessList() // 获取二级修作业过程列表
  },

  mounted() { },

  methods: {
    tableRowClassName({ row, rowIndex }) {
      if (rowIndex % 2 == 1) {
        return 'warning-row'
      } else {
        return 'success-row'
      }
    },

    // 获取当前登录人运用所编码
    async getUnitCode() {
      let { data } = await axios.get('/apiTrainRepair/common/getUnitCode')
      if (data.code == 1) {
        this.unitCode = data.data
      } else {
        this.$message.error('获取登录人信息失败！')
      }
    },

    //获取开始时间结束时间
    getFirstDate(date) {
      var date = new Date()
      var year = date.getFullYear().toString()
      var month = date.getMonth() + 1
      if (month < 10) {
        month = '0' + month
      }
      var da = date.getDate()
      if (da < 10) {
        da = '0' + da
      }
      var end = year + '-' + month + '-' + da

      this.search.starTime = end
      this.search.endTime = end
    },

    // 搜索车型
    getTraintypeList() {
      var that = this
      axios
        .get('/apiTrainResume/resume/getTraintypeList')
        .then(function (res) {
          that.search.trainsetType = ''
          that.trainTypeList = res.data.data
        })
        .catch(() => {
          this.$message({
            message: '获取车型失败',
            type: 'warning',
          })
        })
    },

    //车组号  根据车型
    getTrainsetNameList() {
      var that = this
      axios
        .post('/apiTrainResume/resume/getTrainsetNameList', { traintype: that.search.trainsetType })
        .then(function (res) {
          that.search.trainsetId = ''
          that.trainsetList = res.data.data
        })
        .catch(() => {
          this.$message({
            message: '获取车组失败',
            type: 'warning',
          })
        })
    },

    //作业班组 作业人员跟着改变
    getTaskDeptList() {
      var that = this
      if (that.twoWorkProcessDialog.visible) {
        axios
          .get('/apiTrainRepair/workProcess/getTaskDeptList', {
            params: {
              unitCode: '', // 运用所
              dayPlanId: that.dayPlanID, // 作业班次(日计划)
              repairType: '2', // 一级修传1 二级修传2 一体化传6
              trainsetId: that.twoWorkProcessDialog.trainset ? that.twoWorkProcessDialog.trainset.trainsetId : '', // 车组ID
              packetCode: that.twoWorkProcessDialog.workPackage ? that.twoWorkProcessDialog.workPackage.packetCode : '', // 一级修传空  二级修传用户选择的作业包编码
            },
          })
          .then(async (res) => {
            that.twoWorkProcessDialog.workTeam = res.data.data[0]
            that.addWorkTeamList = res.data.data
            await that.getPersonByDept()
          })
      } else {
        axios
          .get('/apiTrainRepair/common/getWorkTeamsByUnitCode', {
            params: {
              unitCode: that.unitCode,
            },
          })
          .then(function (res) {
            that.workTeamList = res.data.rows
          })
          .catch(() => {
            this.$message({
              message: '获取作业班组失败',
              type: 'warning',
            })
          })
      }
    },

    // 作业人员 选择作业班组后 作业人员跟随变化
    getPersonByDept() {
      var that = this
      that.twoWorkProcessDialog.workPeople = []
      var id = ''
      if (that.twoWorkProcessDialog.visible) {
        id = that.twoWorkProcessDialog.workTeam ? that.twoWorkProcessDialog.workTeam.deptCode : ''
      } else {
        id = that.search.workTeam
      }

      axios
        .get('/apiTrainRepair/workProcess/getPersonByDept', {
          params: {
            deptCode: id,
          },
        })
        .then(function (res) {
          if (that.twoWorkProcessDialog.visible) {
            that.twoWorkProcessDialog.workPeople = []
            that.addWorkPeopleList = res.data.data
          } else {
            that.search.workPeople = ''
            that.workPeopleList = res.data.data
          }
        })
        .catch(() => {
          this.$message({
            message: '获取作业人员失败',
            type: 'warning',
          })
        })
    },

    //列表接口
    getTwoWorkProcessList() {
      var that = this
      axios
        .post('/apiTrainRepair/workProcess/getTwoWorkProcessList', {
          startTime: that.search.starTime, // 开始时间
          endTime: that.search.endTime, // 结束时间
          workerId: that.search.workPeople, // 作业人员
          deptCode: that.search.workTeam, // 作业班组
          trainsetId: that.search.trainsetId, // 车组号
          packetName: that.search.workPackage, // 作业包
          pageNum: that.pageNum, // 当前页
          pageSize: that.pageSize, // 页大小
          trainsetType: that.search.trainsetType, // 车型
          unitCode: that.unitCode, // 所编码
        })
        .then(function (res) {
          if (res.data.code == '1') {
            that.tableData = res.data.data.twoWorkProcessList.records
            that.total = res.data.data.twoWorkProcessList.total
          } else {
            that.$message({
              message: '获取列表失败',
              type: 'warning',
            })
          }
        })
        .catch(() => { })
    },

    async initData() {
      //初始化
      await this.getFirstDate() // 初始化开始时间和结束时间
      await this.getTraintypeList() // 获取车型列表
      await this.getTrainsetNameList() // 获取车组列表
      await this.getTaskDeptList() // 获取班组列表
      await this.getPersonByDept() // 获取作业人员列表
    },

    //查询按钮
    clickList() {
      var that = this
      this.pageNum = 1
      if (that.search.endTime < that.search.starTime) {
        that.$message({
          type: 'warning',
          message: '结束日期不能早于开始日期',
        })
      } else {
        this.getTwoWorkProcessList()
      }
    },

    //重置按钮
    async repeatHandle() {
      var that = this
      that.search.workTeam = '' //作业班组
      that.search.workPeople = ''
      that.search.workPackage = ''
      await that.initData()
      await that.getTwoWorkProcessList()
      that.$message({
        type: 'success',
        message: '重置成功',
      })
    },

    // 新增获取当前所在班次
    async getDay() {
      let res = await axios.get('/apiTrainRepair/common/getDay')
      this.twoWorkProcessDialog.time =
        res.data.dayPlanId.split('-')[0] +
        '-' +
        res.data.dayPlanId.split('-')[1] +
        '-' +
        res.data.dayPlanId.split('-')[2]
      this.twoWorkProcessDialog.shift = res.data.dayPlanId.split('-')[3]

      await this.getTrainsetListByDayPlanId()
    },

    //添加按钮中   车组号接口
    getTrainsetListByDayPlanId: function (val) {
      var that = this
      axios
        .get('/apiTrainRepair/workProcess/getTrainsetListByDayPlanId', {
          params: {
            unitCode: '', // 运用所
            dayPlanId: that.dayPlanID, // 作业班次
            repairType: '2', // 当前页面一级修为'1', 二级修为'2'
          },
        })
        .then(async (res) => {
          that.twoWorkProcessDialog.trainset = res.data.data.length > 0 ? res.data.data[0] : {}
          that.addTrainsetList = res.data.data
          await that.getPacketTaskByDayplanIdAndTrainsetId()
        })
    },

    //获取作业辆序  根据车组号
    getCarNoByTrainsetID(val) {
      let that = this
      let id
      if (val) {
        id = val
      } else {
        id = that.twoWorkProcessDialog.trainset ? that.twoWorkProcessDialog.trainset.trainsetId : ''
      }

      axios
        .post('/apiTrainResume/resume/getCarno?trainsetid=' + id)
        .then((res) => {
          let list = []
          res.data.data.forEach((item) => {
            item = item.split('')
            list.push(item.slice(item.length - 2, item.length).join(''))
          })

          that.carNoList = list.length > 0 ? list : []
        })
        .catch(() => {
          this.$message({
            message: '获取作业辆序失败',
            type: 'warning',
          })
        })
    },

    //添加按钮中 获取作业包   根据车组号/作业班次/所编码
    async getPacketTaskByDayplanIdAndTrainsetId(dayPlanId, trainsetId) {
      var that = this

      await axios
        .get('/apiTrainUse/Task/getPacketTaskByDayplanIdAndTrainsetId', {
          params: {
            dayPlanId: dayPlanId ? dayPlanId : that.dayPlanID,
            trainsetId: trainsetId ? trainsetId : that.twoWorkProcessDialog.trainset.trainsetId,
            deptCode: this.unitCode,
          },
        })
        .then(async (res) => {
          if (res.status == '200') {
            that.twoWorkProcessDialog.workPackage = {}
            that.workPackageList = []
            that.twoWorkProcessDialog.workProject = []
            that.workProjectList = []
            that.twoWorkProcessDialog.workTeam = {}
            that.addWorkTeamList = []
            that.twoWorkProcessDialog.workPeople = []
            that.addWorkPeopleList = []
            let data = res.data.filter((item) => {
              return item.taskRepairCode == '2'
            })

            let projectObj = {}

            let projectList = data[0].lstTaskItemInfo.reduce((cur, next) => {
              projectObj[next.itemCode] ? '' : (projectObj[next.itemCode] = true && cur.push(next))
              return cur
            }, [])

            data.forEach((item) => {
              item.lstTaskItemInfo.forEach((items) => {
                if (!that.projectCodeMap[items.itemCode]) {
                  that.projectCodeMap[items.itemCode] = []
                }
                that.projectCodeMap[items.itemCode].push(items)
              })
            })

            that.twoWorkProcessDialog.workPackage = data[0]
            that.workPackageList = data
            that.twoWorkProcessDialog.workProject = []
            that.workProjectList.push(...projectList)
            that.twoWorkProcessDialog.workProject.push(...projectList)
            if (!trainsetId) {
              await that.getTaskDeptList()
            }
          } else {
            this.$message({
              message: '获取作业包失败',
              type: 'warning',
            })
          }
        })
        .catch(() => {
          // this.$message({
          //   message: '获取作业包失败',
          //   type: 'warning'
          // });
        })
    },

    // 切换作业项目切换
    projectChange() {
      this.uploadPicture.workercarNo = ''
      this.carNoList = []
      let carNoList = this.projectCodeMap[this.uploadPicture.uploadPictureproject].map((item) => {
        return item.carNo
      })
      carNoList = [...new Set(carNoList)]

      carNoList = carNoList.sort((a, b) => {
        return a - b
      })

      carNoList.forEach((item, index) => {
        if (item == '00') {
          carNoList.splice(index, 1)
          carNoList.push(item)
        }
      })
      this.carNoList = carNoList
    },

    changeWorkProject(val) {
      let obj = {}
      this.twoWorkProcessDialog.imgLength = 0
      Object.keys(this.uploadPicture.uploadImgList).forEach((workProjectCode) => {
        val.forEach((item) => {
          if (workProjectCode == item.itemCode) {
            Object.keys(this.uploadPicture.uploadImgList[workProjectCode]).forEach((carNo) => {
              let long = this.uploadPicture.uploadImgList[workProjectCode][carNo].length
              this.twoWorkProcessDialog.imgLength += long
              obj[workProjectCode] = this.uploadPicture.uploadImgList[workProjectCode]
            })
          }
        })
      })
      this.uploadPicture.uploadImgList = obj
    },

    // 根据班次获取工作时间
    async getWorkTimeByDayPlanId(dayPlanId) {
      let res = await axios.get('/apiTrainRepair/common/getWorkTimeByDayPlanId', {
        params: {
          dayPlanId,
        },
      })
      return res.data.data
    },

    //点击新增按钮
    async addTwoWorkProcess() {
      this.twoWorkProcessDialog.visible = true
      this.twoWorkProcessDialog.addOrUpdate = 'add'
      this.twoWorkProcessDialog.imgLength = 0
      await this.getDay()
      let res = await this.getWorkTimeByDayPlanId(this.dayPlanID)
      this.twoWorkProcessDialog.starTime = res.startTime ? res.startTime : ''
      this.twoWorkProcessDialog.endTime = res.startTime ? res.startTime : ''

      this.$nextTick().then(async () => {
        await this.$refs.twoWorkProcessDialog.clearValidate()
      })
    },

    // 切换班次
    async frequencyChange() {
      await this.getTrainsetListByDayPlanId()
      let res = await this.getWorkTimeByDayPlanId(this.dayPlanID)
      this.twoWorkProcessDialog.starTime = res.startTime ? res.startTime : ''
      this.twoWorkProcessDialog.endTime = res.startTime ? res.startTime : ''
    },

    //车组切换
    async getTrainset() {
      this.twoWorkProcessDialog.workPackage = ''
      await this.getPacketTaskByDayplanIdAndTrainsetId()
      let res = await this.getWorkTimeByDayPlanId(this.dayPlanID)
      this.twoWorkProcessDialog.starTime = res.startTime ? res.startTime : ''
      this.twoWorkProcessDialog.endTime = res.startTime ? res.startTime : ''
    },

    // 作业包切换
    workPackageChange(val) {
      this.twoWorkProcessDialog.workTeam = {}
      this.addWorkTeamList = []
      this.twoWorkProcessDialog.workPeople = []
      this.addWorkPeopleList = []
      this.workProjectList = []
      this.twoWorkProcessDialog.workProject = []
      this.getTaskDeptList()

      let projectObj = {},
        projectList = []
      projectList =
        val &&
        val.lstTaskItemInfo.reduce((cur, next) => {
          projectObj[next.itemCode] ? '' : (projectObj[next.itemCode] = true && cur.push(next))
          return cur
        }, [])

      this.workProjectList.push(...projectList)
      this.twoWorkProcessDialog.workProject.push(...projectList)
    },

    //点击新建按钮的dialog的保存按钮
    dialogServe(formName) {
      var that = this
      that.$refs[formName].validate(async (valid) => {
        if (valid) {
          let endtime = new Date(that.twoWorkProcessDialog.endTime).getTime()
          let startime = new Date(that.twoWorkProcessDialog.starTime).getTime()

          let previousShift
          let presentShift
          if (that.twoWorkProcessDialog.shift == '00') {
            presentShift = new Date(that.twoWorkProcessDialog.time).getTime()
            previousShift = new Date(presentShift - 3600 * 24 * 1000)

            previousShift =
              previousShift.getFullYear() +
              '-' +
              (previousShift.getMonth() + 1 > 9 ? previousShift.getMonth() + 1 : '0' + (previousShift.getMonth() + 1)) +
              '-' +
              (previousShift.getDate() > 9 ? previousShift.getDate() : '0' + previousShift.getDate()) +
              '-' +
              '01'
            presentShift = that.twoWorkProcessDialog.time + '-' + '00'

            previousShift = await that.getWorkTimeByDayPlanId(previousShift)
            presentShift = await that.getWorkTimeByDayPlanId(presentShift)
          } else if (that.twoWorkProcessDialog.shift == '01') {
            previousShift = that.twoWorkProcessDialog.time + '-' + '00'
            presentShift = that.twoWorkProcessDialog.time + '-' + '01'

            previousShift = await that.getWorkTimeByDayPlanId(previousShift)
            presentShift = await that.getWorkTimeByDayPlanId(presentShift)
          }

          if (
            new Date(previousShift.startTime).getTime() <= startime &&
            startime <= new Date(presentShift.endTime).getTime()
          ) {
            if (endtime <= startime) {
              that.$message({
                type: 'info',
                message: '结束时间不能小于或等于开始时间',
              })
            } else {
              let loading = this.$loading()
              try {
                let processPacketList = [],
                  processItemList = [],
                  processWorkerList = []
                let formData = new window.FormData()
                let url, data
                if (that.twoWorkProcessDialog.addOrUpdate == 'add') {
                  that.twoWorkProcessDialog.workProject.forEach((item) => {
                    let result = that.twoWorkProcessDialog.workPackage.lstTaskItemInfo.some((workItem) => {
                      if (workItem.itemCode == item.itemCode) {
                        return true
                      }
                    })

                    if (result) {
                      processItemList.push({
                        itemCode: item.itemCode,
                        itemName: item.itemName,
                      })
                    }
                  })

                  processPacketList.push({
                    packetCode: that.twoWorkProcessDialog.workPackage.packetCode,
                    packetName: that.twoWorkProcessDialog.workPackage.packetName,
                    packetType: that.twoWorkProcessDialog.workPackage.packetTypeCode,
                    processItemList,
                  })

                  that.twoWorkProcessDialog.workPeople.forEach((item) => {
                    processWorkerList.push({
                      workerId: item.workId,
                      workerName: item.workName,
                    })
                  })
                  url = '/apiTrainRepair/workProcess/addTwoWorkProcess'
                  data = {
                    dayPlanId: that.dayPlanID, // 作业班次
                    deptCode: that.twoWorkProcessDialog.workTeam.deptCode, // 班组编码
                    deptName: that.twoWorkProcessDialog.workTeam.deptName, // 班组名称
                    endTime: that.twoWorkProcessDialog.endTime, // 结束时间
                    startTime: that.twoWorkProcessDialog.starTime, // 开始时间
                    trainsetId: that.twoWorkProcessDialog.trainset.trainsetId, // 车组ID
                    trainsetName: that.twoWorkProcessDialog.trainset.trainsetName, // 车组名称
                    processWorkerList, // 作业人员集合
                    processPacketList, // 派工集合包
                    processItemList: that.twoWorkProcessDialog.workProject, // 作业项目集合
                  }
                } else {
                  processWorkerList.push({
                    workerId: that.rowsObj.workerId,
                    workerName: that.rowsObj.workerName,
                  })
                  url = '/apiTrainRepair/workProcess/updateTwoWorkProcess'
                  data = {
                    dayPlanId: that.rowsObj.dayPlanId, // 作业班次
                    deptCode: that.rowsObj.deptCode, // 班组编码
                    deptName: that.rowsObj.deptName, // 班组名称
                    endTime: that.twoWorkProcessDialog.endTime, // 结束时间
                    startTime: that.twoWorkProcessDialog.starTime, // 开始时间
                    trainsetId: that.rowsObj.trainsetId, // 车组ID
                    trainsetName: that.rowsObj.trainsetId, // 车组名称
                    processWorkerList, // 作业人员集合
                    processPacketList: that.rowsObj.processPacketList, // 作业包集合
                    processPicList: that.editExistingPictures, // 图片对象集合
                  }
                }
                formData.append('twoWorkData', JSON.stringify(data))
                Object.keys(that.fileList).forEach((item) => {
                  Object.keys(that.fileList[item]).forEach((val) => {
                    that.fileList[item][val].forEach((v) => {
                      formData.append(val + item, v)
                    })
                  })
                })

                let res = await axios({
                  method: 'post',
                  url,
                  data: formData,
                })

                if (res.data.code == 1) {
                  if (that.twoWorkProcessDialog.addOrUpdate == 'add') {
                    that
                      .$confirm('是否继续新增？', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                      })
                      .then(function () {
                        that.getTwoWorkProcessList()
                        that.addTwoWorkProcess()
                      })
                      .catch(function () {
                        that.twoWorkProcessDialog.visible = false
                        that.getTwoWorkProcessList()
                        that.getTaskDeptList()
                      })
                  } else {
                    that.twoWorkProcessDialog.visible = false
                    that.getTwoWorkProcessList()
                  }
                  that.$message.success(res.data.msg)
                } else {
                  that.$message.warning(res.data.msg)
                }
              } finally {
                loading.close()
              }
            }
          } else {
            that.$message({
              type: 'info',
              message: '作业班次和作业时间不匹配，请进行调整！',
            })
          }
        }
      })
    },

    onTwoWorkProcessDialogClosed() {
      this.twoWorkProcessDialog.addOrUpdate = ''
      this.uploadPicture.workercarNo = ''
      this.carNoList = []
      this.twoWorkProcessDialog.imgLength = 0
      this.uploadPicture.uploadImgList = {}
      this.fileList = {}
      this.$refs['twoWorkProcessDialog'].resetFields()
    },
    //点击dialog的重置按钮  新建按钮的
    cancelTwoWorkProcessDialog() {
      this.twoWorkProcessDialog.visible = false
    },

    //点击列表编辑
    async writeHandle(rows) {
      var that = this
      that.twoWorkProcessDialog.addOrUpdate = 'update'
      that.twoWorkProcessDialog.visible = true
      that.rowsObj = rows
      let ItemList = []
      rows.processPacketList[0].processItemList.forEach((item) => {
        ItemList.push(item)
      })
      that.uploadPicture.uploadPictureproject = ''
      that.uploadPicture.workercarNo = ''
      that.uploadPicture.uploadImgList = {}
      that.fileList = {}
      that.twoWorkProcessDialog.time =
        rows.dayPlanId.split('-')[0] + '-' + rows.dayPlanId.split('-')[1] + '-' + rows.dayPlanId.split('-')[2]
      that.twoWorkProcessDialog.shift = rows.dayPlanId.split('-')[3]

      that.twoWorkProcessDialog.trainset = rows.trainsetName //车组号
      await this.getPacketTaskByDayplanIdAndTrainsetId('', rows.trainsetId)
      that.twoWorkProcessDialog.workPackage = rows.processPacketList[0].packetName //作业包
      that.twoWorkProcessDialog.workProject = ItemList
      that.twoWorkProcessDialog.workProjectUpdate = ItemList.map((item) => {
        return item.itemName
      }).join(',') //作业项目
      that.twoWorkProcessDialog.starTime = rows.startTime
      that.twoWorkProcessDialog.endTime = rows.endTime
      that.twoWorkProcessDialog.workTeam = rows.deptName //作业班组
      that.twoWorkProcessDialog.workPeople = [
        {
          workId: rows.workerId,
          workName: rows.workerName,
        },
      ]
      that.twoWorkProcessDialog.workPeopleUpdate = rows.workerName //作业人员

      that.twoWorkProcessDialog.imgLength = rows.processPicList.length
      rows.processPicList.forEach((item) => {
        if (!that.uploadPicture.uploadImgList[item.itemCode]) {
          that.uploadPicture.uploadImgList[item.itemCode] = {}
        }

        if (!that.uploadPicture.uploadImgList[item.itemCode][item.carNo]) {
          that.uploadPicture.uploadImgList[item.itemCode][item.carNo] = []
        }

        that.uploadPicture.uploadImgList[item.itemCode][item.carNo].push(imgURL + item.picAddress)
      })

      that.backupsImgList = JSON.parse(JSON.stringify(that.uploadPicture.uploadImgList))
      that.backupseditExistingPictures = JSON.parse(JSON.stringify(rows.processPicList))
      that.editExistingPictures = JSON.parse(JSON.stringify(rows.processPicList))
      this.$nextTick().then(async () => {
        await this.$refs.twoWorkProcessDialog.clearValidate()
      })
    },

    //点击列表的实际图片数量
    async ClickPicCount(rows) {
      this.dialogVisiblePicCount = true
      await this.getPacketTaskByDayplanIdAndTrainsetId(rows.dayPlanId, rows.trainsetId)
      await this.getCarNoByTrainsetID(rows.trainsetId)

      this.uploadPicture.uploadImgList = {}
      rows.processPicList.forEach((item) => {
        if (!this.uploadPicture.uploadImgList[item.itemCode]) {
          this.uploadPicture.uploadImgList[item.itemCode] = {}
        }

        if (!this.uploadPicture.uploadImgList[item.itemCode][item.carNo]) {
          this.uploadPicture.uploadImgList[item.itemCode][item.carNo] = []
        }

        this.uploadPicture.uploadImgList[item.itemCode][item.carNo].push(imgURL + item.picAddress)
      })
    },

    onUploadPictureDialogClose() {
      this.uploadPicture.uploadPictureproject = ''
      this.uploadPicture.workercarNo = ''
      if (this.uploadPicture.ok) {
        this.twoWorkProcessDialog.imgLength = 0
        Object.keys(this.uploadPicture.uploadImgList).forEach((item) => {
          Object.keys(this.uploadPicture.uploadImgList[item]).forEach((val) => {
            let long = this.uploadPicture.uploadImgList[item][val].length
            this.twoWorkProcessDialog.imgLength += long
          })
        })
        this.backupsImgList = JSON.parse(JSON.stringify(this.uploadPicture.uploadImgList))
        this.newFileList = JSON.parse(JSON.stringify(this.fileList))
        this.backupseditExistingPictures = JSON.parse(JSON.stringify(this.editExistingPictures))
      } else {
        this.uploadPicture.uploadImgList = JSON.parse(JSON.stringify(this.backupsImgList))
        this.fileList = JSON.parse(JSON.stringify(this.newFileList))
        this.editExistingPictures = JSON.parse(JSON.stringify(this.backupseditExistingPictures))
      }
    },
    //dialog图片的确定按钮
    dialogVisiblePicOk() {
      this.uploadPicture.ok = true
      this.uploadPicture.visible = false
    },

    //dialog图片的取消按钮
    dialogVisiblePicNo() {
      this.uploadPicture.ok = false
      this.uploadPicture.visible = false
    },

    // 判断上传图片格式
    beforeAvatarUploadPdf(file) {
      var testmsg = file.name.substring(file.name.lastIndexOf('.') + 1)
      const extension = testmsg === 'jpg' || testmsg === 'JPG'
      const extension2 = testmsg === 'png' || testmsg === 'PNG'
      const extension3 = testmsg === 'jpeg' || testmsg === 'JPEG'
      const extension4 = testmsg === 'bmp' || testmsg === 'BMP'
      if (!extension && !extension2 && !extension3 && !extension4) {
        this.$message({
          message: '上传文件只能是 JPG、JPEG、BMP、PNG 格式!',
          type: 'warning',
        })
        return false
      }
      return extension || extension2 || extension3 || extension4
    },

    // 添加图片弹出层-选择图片
    successImg: function (param) {
      if (!this.uploadPicture.uploadImgList[this.uploadPicture.uploadPictureproject]) {
        this.uploadPicture.uploadImgList[this.uploadPicture.uploadPictureproject] = {}
      }
      if (!this.uploadPicture.uploadImgList[this.uploadPicture.uploadPictureproject][this.uploadPicture.workercarNo]) {
        this.uploadPicture.uploadImgList[this.uploadPicture.uploadPictureproject][this.uploadPicture.workercarNo] = []
      }
      if (!this.fileList[this.uploadPicture.uploadPictureproject]) {
        this.fileList[this.uploadPicture.uploadPictureproject] = {}
      }
      if (!this.fileList[this.uploadPicture.uploadPictureproject][this.uploadPicture.workercarNo]) {
        this.fileList[this.uploadPicture.uploadPictureproject][this.uploadPicture.workercarNo] = []
      }
      this.fileList[this.uploadPicture.uploadPictureproject][this.uploadPicture.workercarNo].push(param.file)

      var url = null
      if (window.createObjectURL != undefined) {
        url = window.createObjectURL(param.file)
      } else if (window.URL != undefined) {
        url = window.URL.createObjectURL(param.file)
      } else if (window.webkitURL != undefined) {
        url = window.webkitURL.createObjectURL(param.file)
      }

      this.uploadPicture.uploadImgList[this.uploadPicture.uploadPictureproject][this.uploadPicture.workercarNo].push(
        url
      )
      this.uploadPicture.projectImgListOpen.push(this.uploadPicture.uploadPictureproject)
      this.$forceUpdate()
    },

    // 选择图片 点击dialog的选择图片
    select_img() {
      if (this.uploadPicture.uploadPictureproject) {
        if (typeof this.uploadPicture.workercarNo === 'undefined' || this.uploadPicture.workercarNo.trim() === '') {
          this.$message({ type: 'warning', message: '请选择辆序' })
          event.stopPropagation()
          event.preventDefault()
        }
      } else {
        this.$message({ type: 'warning', message: '请选择作业项目' })
        event.stopPropagation()
        event.preventDefault()
      }
    },

    //删除图片
    deleteImgs(imgIndex, projectCode, key) {
      if (this.twoWorkProcessDialog.addOrUpdate == 'add') {
        this.fileList[projectCode][key].splice(imgIndex, 1)
      } else if (this.twoWorkProcessDialog.addOrUpdate == 'update') {
        let idx
        for (let i = 0; i < this.editExistingPictures.length; i++) {
          if (
            '/storageTrainRepair/' + this.editExistingPictures[i].picAddress ==
            this.uploadPicture.uploadImgList[projectCode][key][imgIndex]
          ) {
            idx = i
          }
        }

        if (idx > -1) {
          this.editExistingPictures.splice(idx, 1)
        } else if (this.fileList[projectCode][key]) {
          let URLlist = []
          this.fileList[projectCode][key].forEach((key) => {
            if (window.createObjectURL != undefined) {
              URLlist.push(window.createObjectURL(key))
            } else if (window.URL != undefined) {
              URLlist.push(window.URL.createObjectURL(key))
            } else if (window.webkitURL != undefined) {
              URLlist.push(window.webkitURL.createObjectURL(key))
            }
          })
          let indexes = URLlist.indexOf(this.uploadPicture.uploadImgList[projectCode][key][imgIndex])

          this.fileList[projectCode][key].splice(indexes, 1)
        }
      }

      this.uploadPicture.uploadImgList[projectCode][key].splice(imgIndex, 1)

      if (this.uploadPicture.uploadImgList[projectCode][key].length == 0) {
        delete this.uploadPicture.uploadImgList[projectCode][key]
      }

      if (JSON.stringify(this.uploadPicture.uploadImgList[projectCode]) == '{}') {
        delete this.uploadPicture.uploadImgList[projectCode]
      }
      this.$forceUpdate()
    },

    //点击dialog的上传图片按钮
    pictureHandle() {
      this.uploadPicture.visible = true
    },

    //点击列表项目数量
    numberHandle(rows) {
      this.dialogVisibleNum = true
      this.xiangmuList = rows.processPacketList[0]
      this.rowsObj = rows
    },

    //点击列表删除
    deleteHandle(rows) {
      var that = this
      that
        .$confirm('确认删除？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
        .then(() => {
          axios
            .post('/apiTrainRepair/workProcess/delTwoWorkProcess', {
              dayPlanId: rows.dayPlanId, // 作业班次
              trainsetId: rows.trainsetId, // 车组ID
              workerId: rows.workerId, // 作业人员ID
              packetCode: rows.processPacketList[0].packetCode
            })
            .then(function (res) {
              if(res.data.code == 1) {
                that.$message({
                  type: 'success',
                  message: res.data.msg,
                })
                that.getTwoWorkProcessList()
              } else {
                that.$message({
                  message: res.data.msg,
                  type: 'error',
                })
              }
            })
            .catch((err) => {
              that.$message({
                message: '删除失败',
                type: 'error',
              })
            })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '取消删除',
          })
        })
    },

    //改变页码大小
    handleSizeChange(val) {
      this.pageSize = val
      this.getTwoWorkProcessList()
    },
    //改变页码
    handleCurrentChange(val) {
      this.pageNum = val
      this.getTwoWorkProcessList()
    },

    // 双击图片
    // dbImage(img) {
    //   this.imgUrl = img
    //   this.imgZoom = true
    // },
  },
  components: {},
  filters: {
    //自定义过滤器
  },
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('二级修过程记录'),
    //计算属性
    dayPlanID() {
      return this.twoWorkProcessDialog.time + '-' + this.twoWorkProcessDialog.shift
    },

    getWorkProjectUpdate() {
      return this.twoWorkProcessDialog.workProjectUpdate.split(',').join(`</br>`)
    },

    uploadWorkProjectList() { },
  },
  watch: {
    //监听数据改变
  },
  filters: {
    operationShift(val) {
      let end = val.split('-').pop()
      let time = val.split('-').slice(0, 3).join('-')
      if (end == '00') {
        return time + '-' + '白班'
      } else {
        return time + '-' + '夜班'
      }
    },

    datesourseFilter(val) {
      return val == 1 ? '手持机录入' : '电脑录入'
    },

    carNosFilter(val) {
      if (Array.isArray(val)) {
        let list = []
        val.forEach((item) => {
          item = item.split('')
          list.push(item.slice(item.length - 2, item.length).join(''))
        })
        return list.join(',')
      } else {
        val = val.split('')
        return val.slice(val.length - 2, val.length).join('')
      }
    },
  },
  beforeDestroy() { },
})
