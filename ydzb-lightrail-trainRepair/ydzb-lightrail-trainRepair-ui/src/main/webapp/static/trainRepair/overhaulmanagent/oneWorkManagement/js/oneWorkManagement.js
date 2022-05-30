var checkResponse10Warppr = (defaultMessage) => {
  return (res) => {
    return new Promise((resolve, reject) => {
      if (res.data.code === 1) {
        resolve(res.data.data);
      } else {
        fault.$message({
          type: 'error',
          message: res.data.msg || defaultMessage || '请求失败',
        });
        reject();
      }
    });
  };
};

window.fault = new Vue({
  el: '#one',
  data: {
    unitCode: '', //运用所
    // 搜索部分
    search: {
      starTime: '', //开始日期
      endTime: '', //结束日期
      trainsetType: '', //车型
      trainsetName: '', // 车组
      workTeam: '', // 作业班组
      workPeople: '', //作业人员
      workStatus: '', //作业状态
    },
    trainTypeList: [], //车型列表
    trainsetList: [], //车组列表
    workTeamList: '', // 作业班组列表
    workPeopleList: '', //作业人员列表

    tableData: [],
    pageNum: 1,
    pageSize: 10,
    total: 400,
    scrollHeight: 'calc(100% - 100px)',

    // 新增和修改部分
    oneWorkProcessDialog: {
      visible: false,
      addOrUpdate: '',
      // 作业班次
      time: '',
      shift: '',
      trainset: {}, // 车组
      workConent: {}, // 作业内容
      Carno: [], // 辆序
      workTeam: '', // 作业班组
      workPeople: '', //作业人员
      starTime: '', //开始日期
      endTime: '', //结束日期
    },

    oneWorkProcessDialogRules: {
      time: [{ required: true, message: '请选择作业班次', trigger: 'change' }],
      trainset: [{ required: true, message: '请选择车组号', trigger: 'change' }],
      workConent: [{ required: true, message: '请选择作业内容', trigger: 'change' }],
      Carno: [{ required: true, message: '请选择作业辆序', trigger: 'change' }],
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
    workConentList: [], // 作业内容列表
    Carnos: [], // 辆序列表
    addWorkTeamList: [], // 作业班组列表
    addWorkPeopleList: [], //作业人员列表

    // 上传图片
    addPicDialog: {
      visible: false,
      uploadCarno: '', //上传图片的作业辆序
      uploadCarnos: [],
    },

    workTimeDialog: {
      visible: false,
      workLong: '', //点击作业时长
      workDeteil: [], //作业详情
    }, //点击作业时长弹框

    picCountDialog: {
      visible: false,
    }, //点击列表的实际图片数量

    imgLength: '0', //图片的数量
    imgList: {}, // 要展示的图片路径
    fileList: {}, // 上传图片file对象储存
    editExistingPictures: [], // 保存编辑-数据项内所含的图片信息
    backupsImgList: {}, // 保存编辑-备份列表象展示图片
    backupseditExistingPictures: {}, // 保存编辑-备份数据项内所含的图片信息
    newFileList: {}, // 保存编辑-备份编辑中新增的图片信息
  },
  async created() {
    await this.getUnitCode(); // 获取运用所编码
    await this.initData();
    await this.getOneWorkProcessList(); // 获取一级修作业过程列表
  },
  mounted() {},

  methods: {
    tableRowClassName({ row, rowIndex }) {
      if (rowIndex % 2 == 1) {
        return 'warning-row';
      } else {
        return 'success-row';
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

    //获取开始时间结束时间
    getFirstDate() {
      var date = new Date();
      var year = date.getFullYear().toString();
      var month = date.getMonth() + 1;
      if (month < 10) {
        month = '0' + month;
      }
      var da = date.getDate();
      if (da < 10) {
        da = '0' + da;
      }
      var end = year + '-' + month + '-' + da;

      this.search.starTime = end;
      this.search.endTime = end;
    },

    //车型
    getTraintypeList() {
      var that = this;
      axios
        .get('/apiTrainResume/resume/getTraintypeList')
        .then(function (res) {
          that.search.trainsetType = '';
          that.trainTypeList = res.data.data;
        })
        .catch(() => {
          this.$message({
            message: '获取车型失败',
            type: 'warning',
          });
        });
    },

    //车组号  根据车型改变车组号
    getTrainsetNameList() {
      var that = this;
      axios
        .post('/apiTrainResume/resume/getTrainsetNameList', { traintype: that.search.trainsetType })
        .then(function (res) {
          that.search.trainsetName = '';
          that.trainsetList = res.data.data;
        })
        .catch(() => {
          this.$message({
            message: '获取车组号失败',
            type: 'warning',
          });
        });
    },

    //作业班组 作业人员跟着改变
    getTaskDeptList() {
      var that = this;
      if (that.oneWorkProcessDialog.addOrUpdate) {
        axios
          .get('/apiTrainRepair/workProcess/getTaskDeptList', {
            params: {
              unitCode: '', // 运用所
              dayPlanId: that.dayPlanId, // 作业班次(日计划)
              repairType: '1', // 一级修传1 二级修传2 一体化传6
              trainsetId: that.oneWorkProcessDialog.trainset.trainsetId
                ? that.oneWorkProcessDialog.trainset.trainsetId
                : '', // 车组ID
              packetCode: '', // 一级修传空  二级修传用户选择的作业包编码
            },
          })
          .then(async (res) => {
            that.addWorkTeamList = res.data.data;
            that.oneWorkProcessDialog.workTeam = res.data.data ? res.data.data[0] : {};
            await this.getPersonByDept();
          });
      } else {
        axios
          .get('/apiTrainRepair/common/getWorkTeamsByUnitCode', {
            params: {
              unitCode: that.unitCode,
            },
          })
          .then(function (res) {
            that.workTeamList = res.data.rows;
          })
          .catch(() => {
            this.$message({
              message: '获取作业班组失败',
              type: 'warning',
            });
          });
      }
    },

    //作业人员 选择作业班组后 作业人员跟随变化
    getPersonByDept() {
      var that = this;
      var id = '';
      if (that.oneWorkProcessDialog.addOrUpdate) {
        id = that.oneWorkProcessDialog.workTeam.deptCode || '';
      } else {
        id = that.search.workTeam;
      }
      axios
        .get('/apiTrainRepair/workProcess/getPersonByDept', {
          params: {
            deptCode: id,
          },
        })
        .then((res) => {
          if (that.oneWorkProcessDialog.addOrUpdate) {
            if (that.oneWorkProcessDialog.addOrUpdate == 'add') {
              that.oneWorkProcessDialog.workPeople = [];
            }
            that.addWorkPeopleList = res.data.data;
          } else {
            that.search.workPeople = '';
            that.workPeopleList = res.data.data;
          }
        })
        .catch(() => {
          this.$message({
            message: '获取作业人员失败',
            type: 'warning',
          });
        });
    },

    //列表接口
    getOneWorkProcessList() {
      var that = this;
      let data = {
        trainsetName: that.search.trainsetName, //车组号
        trainsetType: that.search.trainsetType, //车型
        trainsetSubType: '', //批次
        deptCode: that.search.workTeam, //作业班组
        workerId: that.search.workPeople, //作业人员
        startTime: that.search.starTime, // 开始时间
        endTime: that.search.endTime, // 结束时间
        timeStatus: that.search.workStatus, // 作业状态
        pageSize: that.pageSize, //页大小
        pageNum: that.pageNum, //当前页
        unitCode: that.unitCode, // 所编码
      };
      axios
        .post('/apiTrainRepair/workProcess/getOneWorkProcessList', data)
        .then(function (res) {
          if (res.data.code == '1') {
            let list = [];
            list = res.data.data.queryOneWorkProcessDataList;
            list.forEach((item) => {
              item.carNos.sort((a, b) => {
                return a - b;
              });
              if (item.carNos[0] && item.carNos[0] == '00') {
                item.carNos.push(item.carNos.shift());
              }
            });

            that.tableData = list;
            that.total = res.data.data.count;
            return list;
          } else {
            that.$message({
              message: '获取列表失败',
              type: 'warning',
            });
          }
        })
        .catch(() => {});
    },

    async initData() {
      //初始化
      await this.getFirstDate(); // 初始化开始时间和结束时间
      await this.getTraintypeList(); // 获取车型列表
      await this.getTrainsetNameList(); // 获取车组列表
      await this.getTaskDeptList(); // 获取班组列表
      await this.getPersonByDept(); // 获取作业人员列表
    },

    //查询按钮
    clickList() {
      var that = this;
      this.pageNum = 1;
      if (that.search.endTime < that.search.starTime) {
        that.$message({
          type: 'warning',
          message: '结束日期不能早于开始日期',
        });
      } else {
        this.getOneWorkProcessList();
      }
    },

    //搜索重置按钮
    async repeatHandle() {
      var that = this;
      await that.initData();
      that.search.workPeople = '';
      that.search.workTeam = ''; //作业班组
      that.search.workStatus = ''; // 作业状态
      await this.getOneWorkProcessList();
      that.$message({
        type: 'success',
        message: '重置成功',
      });
    },

    // 新增获取当前所在班次
    async getDay() {
      let res = await axios.get('/apiTrainRepair/common/getDay');
      if (res.data.code == 1) {
        this.oneWorkProcessDialog.time =
          res.data &&
          res.data.dayPlanId.split('-')[0] +
            '-' +
            res.data.dayPlanId.split('-')[1] +
            '-' +
            res.data.dayPlanId.split('-')[2];
        this.oneWorkProcessDialog.shift = res.data.dayPlanId.split('-')[3];
        await this.getTrainsetListByDayPlanId();
      }
    },

    // 根据班次获取工作时间
    async getWorkTimeByDayPlanId(dayPlanId) {
      let res = await axios.get('/apiTrainRepair/common/getWorkTimeByDayPlanId', {
        params: {
          dayPlanId,
        },
      });
      return res.data.data;
    },

    //添加更新的  车组号接口
    async getTrainsetListByDayPlanId(val) {
      var that = this;
      await axios
        .get('/apiTrainRepair/workProcess/getTrainsetListByDayPlanId', {
          params: {
            unitCode: '', // 运用所
            dayPlanId: that.dayPlanId, // 作业班次
            repairType: '1', // 当前页面一级修为'1', 二级修为'2'
          },
        })
        .then(async (res) => {
          let data = res.data.data;
          if (that.oneWorkProcessDialog.addOrUpdate == 'add') {
            that.oneWorkProcessDialog.trainset = data ? data[0] : {};
          }
          that.addTrainsetList = res.data.data;
          if(that.oneWorkProcessDialog.addOrUpdate == 'add') {
            await that.getWorkcritertionLByTrainsetIdOne();
          } else {
            await that.getWorkcritertionLByTrainsetId();
          }
          await that.getCarNoByTrainsetID();
          await that.getTaskDeptList();
        });
    },

    //添加按钮中 获取作业内容
    getWorkcritertionLByTrainsetId() {
      var that = this;
      axios
        .get('/apiTrainRepair/xzyCWorkcritertion/getWorkcritertionLByTrainsetId', {
          params: {
            trainsetId: that.oneWorkProcessDialog.trainset.trainsetId
              ? that.oneWorkProcessDialog.trainset.trainsetId
              : '',
          },
        })
        .then(function (res) {
          that.workConentList = [];
          if (res.data.data.length > 0) {
            if (that.oneWorkProcessDialog.addOrUpdate == 'add') {
              that.oneWorkProcessDialog.workConent = res.data.data[0];
            }
            that.workConentList = res.data.data;
          } else {
            this.$message({
              message: '当前车组无作业内容',
              type: 'warning',
            });
          }
        })
        .catch(() => {
          // this.$message({
          //   message: '获取作业内容失败',
          //   type: 'warning'
          // });
        });
    },

    //点击新增按钮获取作业内容
    getWorkcritertionLByTrainsetIdOne() {
      var that = this;
      axios
        .get('/apiTrainRepair/xzyCWorkcritertion/getWorkcritertionLByTrainsetIdOne', {
          params: {
            trainsetId: that.oneWorkProcessDialog.trainset.trainsetId
              ? that.oneWorkProcessDialog.trainset.trainsetId
              : '',
          },
        })
        .then(function (res) {
          that.workConentList = [];
          if (res.data.data.length > 0) {
            if (that.oneWorkProcessDialog.addOrUpdate == 'add') {
              that.oneWorkProcessDialog.workConent = res.data.data[0];
            }
            that.workConentList = res.data.data;
          } else {
            this.$message({
              message: '当前车组无作业内容',
              type: 'warning',
            });
          }
        })
        .catch(() => {
          // this.$message({
          //   message: '获取作业内容失败',
          //   type: 'warning'
          // });
        });
    },
    //获取作业辆序  根据车组号
    getCarNoByTrainsetID(val) {
      let that = this;
      let id;
      if (that.oneWorkProcessDialog.addOrUpdate) {
        id = that.oneWorkProcessDialog.trainset.trainsetId ? that.oneWorkProcessDialog.trainset.trainsetId : '';
      } else if (that.picCountDialog.visible) {
        id = val;
      }

      axios
        .post('/apiTrainResume/resume/getCarno?trainsetid=' + id)
        .then(function (res) {
          let list = [];
          res.data.data.forEach((item) => {
            item = item.split('');
            list.push(item.slice(item.length - 2, item.length).join(''));
          });

          if (that.oneWorkProcessDialog.addOrUpdate == 'add') {
            that.imgList = {};
            that.imgLength = 0;
            that.oneWorkProcessDialog.Carno = list.length > 0 ? list : [];
            that.addPicDialog.uploadCarnos = list.length > 0 ? list : [];
          }
          that.Carnos = list.length > 0 ? list : [];
        })
        .catch(() => {
          this.$message({
            message: '获取作业辆序失败',
            type: 'warning',
          });
        });
    },

    //点击新增按钮
    async addOneWorkProcess() {
      this.oneWorkProcessDialog.visible = true;
      this.oneWorkProcessDialog.addOrUpdate = 'add';
      this.imgList = {};
      this.imgLength = 0;
      await this.getDay();
      let res = await this.getWorkTimeByDayPlanId(this.dayPlanId);
      this.oneWorkProcessDialog.starTime = res.startTime ? res.startTime : '';
      this.oneWorkProcessDialog.endTime = res.startTime ? res.startTime : '';

      this.$nextTick().then(async () => {
        await this.$refs.oneWorkProcessDialog.clearValidate();
      });
    },

    // 新增切换班次
    async frequencyChange() {
      await this.getTrainsetListByDayPlanId();
      let res = await this.getWorkTimeByDayPlanId(this.dayPlanId);
      this.oneWorkProcessDialog.starTime = res.startTime ? res.startTime : '';
      this.oneWorkProcessDialog.endTime = res.startTime ? res.startTime : '';
    },

    //车组切换
    async getTrainset() {
      this.oneWorkProcessDialog.workConent = '';
      await this.getWorkcritertionLByTrainsetId();
      await this.getCarNoByTrainsetID();
      await this.getTaskDeptList();
      let res = await this.getWorkTimeByDayPlanId(this.dayPlanId);
      this.oneWorkProcessDialog.starTime = res.startTime ? res.startTime : '';
      this.oneWorkProcessDialog.endTime = res.startTime ? res.startTime : '';
    },

    carNoChange(val) {
      val = val.sort((a, b) => {
        return a - b;
      });
      val.forEach((item, index) => {
        if (item == '00') {
          val.splice(index, 1);
          val.push(item);
        }
      });
      this.addPicDialog.uploadCarnos = val;

      let obj = {};
      this.imgLength = 0;
      Object.keys(this.imgList).forEach((carNo) => {
        val.forEach((sumCarNo) => {
          if (carNo == sumCarNo) {
            let long = this.imgList[carNo].length;
            this.imgLength += long;

            obj[carNo] = this.imgList[carNo];
          }
        });
      });
      this.imgList = obj;
    },

    //点击dialog的上传图片按钮
    pictureHandle() {
      this.addPicDialog.visible = true;
    },

    //点击新建按钮的dialog的保存按钮
    dialogServe(formName) {
      var that = this;
      that.$refs[formName].validate(async (valid) => {
        if (valid) {
          let etime = new Date(that.oneWorkProcessDialog.endTime).getTime();
          let stime = new Date(that.oneWorkProcessDialog.starTime).getTime();
          let previousShift;
          let presentShift;
          if (that.oneWorkProcessDialog.shift == '00') {
            presentShift = new Date(that.oneWorkProcessDialog.time).getTime();
            previousShift = new Date(presentShift - 3600 * 24 * 1000);

            previousShift =
              previousShift.getFullYear() +
              '-' +
              (previousShift.getMonth() + 1 > 9 ? previousShift.getMonth() + 1 : '0' + (previousShift.getMonth() + 1)) +
              '-' +
              (previousShift.getDate() > 9 ? previousShift.getDate() : '0' + previousShift.getDate()) +
              '-' +
              '01';
            presentShift = that.oneWorkProcessDialog.time + '-' + '00';

            previousShift = await that.getWorkTimeByDayPlanId(previousShift);
            presentShift = await that.getWorkTimeByDayPlanId(presentShift);
          } else if (that.oneWorkProcessDialog.shift == '01') {
            previousShift = that.oneWorkProcessDialog.time + '-' + '00';
            presentShift = that.oneWorkProcessDialog.time + '-' + '01';

            previousShift = await that.getWorkTimeByDayPlanId(previousShift);
            presentShift = await that.getWorkTimeByDayPlanId(presentShift);
          }

          if (
            new Date(previousShift.startTime).getTime() <= stime &&
            stime <= new Date(presentShift.endTime).getTime()
          ) {
            if (etime <= stime) {
              that.$message({
                type: 'warning',
                message: '结束时间不能小于或等于开始时间',
              });
            } else {
              if(that.oneWorkProcessDialog.workConent.iPiccount && that.imgLength < that.oneWorkProcessDialog.workConent.iPiccount) {
                let res = await that.$confirm('上传图片数量小于标准图片数量', '提示', {
                  confirmButtonText: '确定',
                  cancelButtonText: '取消',
                  type: 'warning',
                }).catch(() => {
                  this.$message.info('已取消设置')
                })
                if (res !== 'confirm') {
                  return
                }
              }
              let loading = this.$loading();
              try {
                let processWorkerList = [];
                let formData = new window.FormData();

                that.oneWorkProcessDialog.workPeople.forEach((item) => {
                  processWorkerList.push({
                    workerId: item.workId,
                    workerName: item.workName,
                  });
                });

                let data = {
                  carNos: that.oneWorkProcessDialog.Carno, // 作业辆序
                  critertionId: that.oneWorkProcessDialog.workConent.sCritertionid, // 作业内容ID
                  dayPlanId: that.dayPlanId, // 作业班次
                  deptCode: that.oneWorkProcessDialog.workTeam.deptCode, // 作业班组id
                  deptName: that.oneWorkProcessDialog.workTeam.deptName, // 作业班组名称
                  startTime: that.oneWorkProcessDialog.starTime, // 开始时间
                  endTime: that.oneWorkProcessDialog.endTime, // 结束时间
                  itemCode: that.oneWorkProcessDialog.workConent.sItemcode, // 作业内容id
                  itemName: that.oneWorkProcessDialog.workConent.sItemname, // 作业内容名称
                  trainsetId: that.oneWorkProcessDialog.trainset.trainsetId, // 车组ID
                  trainsetName: that.oneWorkProcessDialog.trainset.trainsetName, // 车组名称
                  processWorkerList, // 作业人员集合
                };

                let Url;
                if (that.oneWorkProcessDialog.addOrUpdate === 'update') {
                  Url = '/apiTrainRepair/workProcess/updateOneWorkProcess';
                  data.processPicList = that.editExistingPictures; // 图片对象集合
                } else {
                  Url = '/apiTrainRepair/workProcess/addOneWorkProcess';
                }

                formData.append('oneWorkData', JSON.stringify(data));

                Object.keys(that.fileList).forEach((item) => {
                  that.fileList[item].forEach((v) => {
                    formData.append(item, v);
                  });
                });

                let res = await axios({
                  method: 'post',
                  url: Url,
                  data: formData,
                });

                if (res.data.code == 1) {
                  if (that.oneWorkProcessDialog.addOrUpdate === 'update') {
                    that.oneWorkProcessDialog.visible = false;
                    that.getOneWorkProcessList();
                  } else {
                    that
                      .$confirm('是否继续新增？', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                      })
                      .then(() => {
                        that.getOneWorkProcessList();
                        that.addOneWorkProcess();
                      })
                      .catch(() => {
                        that.oneWorkProcessDialog.visible = false;
                        that.getOneWorkProcessList();
                      });
                  }
                  that.$message.success(res.data.msg);
                } else {
                  that.$message.warning(res.data.msg);
                }
              } finally {
                loading.close();
              }
            }
          } else {
            that.$message({
              type: 'warning',
              message: '作业班次和作业时间不匹配，请进行调整！',
            });
          }
        }
      });
    },
    onOneWorkProcessDialogClosed(){
      this.oneWorkProcessDialog.addOrUpdate = '';
      this.addPicDialog.uploadCarno = '';
      this.addPicDialog.uploadCarnos = [];
      this.imgLength = 0;
      this.imgList = {};
      this.fileList = {};
      this.$refs['oneWorkProcessDialog'].resetFields();
    },

    //点击dialog的取消按钮  新建按钮的
    dialogRepeat() {
      this.oneWorkProcessDialog.visible = false;
    },

    //点击列表编辑
    async updateOneWorkProcess(rows) {
      this.oneWorkProcessDialog.visible = true;
      this.oneWorkProcessDialog.addOrUpdate = 'update';
      this.oneWorkProcessDialog.time =
        rows.dayPlanId.split('-')[0] + '-' + rows.dayPlanId.split('-')[1] + '-' + rows.dayPlanId.split('-')[2];
      this.oneWorkProcessDialog.shift = rows.dayPlanId.split('-')[3];
      this.oneWorkProcessDialog.trainset = {
        trainsetId: rows.trainsetId,
        trainsetName: rows.trainsetName,
      }; //车组号
      await this.getTrainsetListByDayPlanId();
      this.oneWorkProcessDialog.workTeam = {
        deptName: rows.deptName,
        deptCode: rows.deptCode,
      }; //作业班组
      this.oneWorkProcessDialog.workPeople = [
        {
          workName: rows.workerName,
          workId: rows.workerId,
        },
      ]; //作业人员
      this.oneWorkProcessDialog.workConent = {
        sItemname: rows.itemName,
        sItemcode: rows.itemCode,
        iPiccount: rows.standardPicCount
      }; //作业内容
      this.oneWorkProcessDialog.Carno = rows.carNos; //作业辆序
      this.oneWorkProcessDialog.starTime = rows.startTime;
      this.oneWorkProcessDialog.endTime = rows.endTime;

      this.imgLength = rows.processPicList.length;
      this.imgList = {};
      rows.processPicList.forEach((item) => {
        if (!this.imgList[item.carNo]) {
          this.imgList[item.carNo] = [];
        }
        this.imgList[item.carNo].push(imgURL + item.picAddress);
      });

      let carNos = rows.carNos.sort((a, b) => {
        return a - b;
      });
      carNos.forEach((item, index) => {
        if (item == '00') {
          carNos.splice(index, 1);
          carNos.push(item);
        }
      });
      this.addPicDialog.uploadCarnos = carNos;

      // let obj = {};
      // Object.keys(this.imgList)
      //   .sort()
      //   .map((item) => {
      //     obj[item] = this.imgList[item];
      //   });
      // this.imgList = obj;

      this.backupsImgList = JSON.parse(JSON.stringify(this.imgList));
      this.backupseditExistingPictures = JSON.parse(JSON.stringify(rows.processPicList));
      this.editExistingPictures = JSON.parse(JSON.stringify(rows.processPicList));
      
      this.$nextTick().then(async () => {
        await this.$refs.oneWorkProcessDialog.clearValidate();
      });
    },

    //点击列表的实际图片数量
    ClickPicCount(rows) {
      this.addPicDialog.uploadCarnos = [];
      this.imgList = {};
      rows.processPicList.forEach((item) => {
        if (!this.imgList[item.carNo]) {
          this.imgList[item.carNo] = [];
        }
        this.imgList[item.carNo].push(imgURL + item.picAddress);
        if (
          !this.addPicDialog.uploadCarnos.some((carNo) => {
            return carNo == item.carNo;
          })
        ) {
          this.addPicDialog.uploadCarnos.push(item.carNo);
        }
        this.addPicDialog.uploadCarnos.sort((a, b) => {
          return a - b;
        });
        if (this.addPicDialog.uploadCarnos[0] && this.addPicDialog.uploadCarnos[0] == '00') {
          this.addPicDialog.uploadCarnos.push(this.addPicDialog.uploadCarnos.shift());
        }
      });
      this.picCountDialog.visible = true;
    },

    //dialog图片的确定按钮
    dialogVisiblePicOk() {
      this.addPicDialog.visible = false;
      this.imgLength = 0;
      this.addPicDialog.uploadCarno = '';
      Object.keys(this.imgList).forEach((item) => {
        let long = this.imgList[item].length;
        this.imgLength += long;
      });

      this.backupsImgList = JSON.parse(JSON.stringify(this.imgList));
      this.newFileList = JSON.parse(JSON.stringify(this.fileList));
      this.backupseditExistingPictures = JSON.parse(JSON.stringify(this.editExistingPictures));
    },

    //dialog图片的取消按钮
    dialogVisiblePicNo() {
      this.addPicDialog.visible = false;
      this.addPicDialog.uploadCarno = '';
      this.imgList = JSON.parse(JSON.stringify(this.backupsImgList));
      this.fileList = JSON.parse(JSON.stringify(this.newFileList));

      this.editExistingPictures = JSON.parse(JSON.stringify(this.backupseditExistingPictures));
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

    // 添加图片弹出层-选择图片
    successImg(param) {
      if (!this.imgList[this.addPicDialog.uploadCarno]) {
        this.imgList[this.addPicDialog.uploadCarno] = [];
      }
      if (!this.fileList[this.addPicDialog.uploadCarno]) {
        this.fileList[this.addPicDialog.uploadCarno] = [];
      }

      this.fileList[this.addPicDialog.uploadCarno].push(param.file);

      var url = null;
      if (window.createObjectURL != undefined) {
        url = window.createObjectURL(param.file);
      } else if (window.URL != undefined) {
        url = window.URL.createObjectURL(param.file);
      } else if (window.webkitURL != undefined) {
        url = window.webkitURL.createObjectURL(param.file);
      }

      this.imgList[this.addPicDialog.uploadCarno].push(url);

      let obj = {};
      Object.keys(this.imgList)
        .sort()
        .map((item) => {
          obj[item] = this.imgList[item];
        });

      this.imgList = obj;
      this.$forceUpdate();
    },
    // 选择图片 点击dialog的选择图片
    select_img() {
      if (typeof this.addPicDialog.uploadCarno === 'undefined' || this.addPicDialog.uploadCarno.trim() === '') {
        this.$message({
          type: 'warning',
          message: '请选择辆序',
        });
        event.stopPropagation();
        event.preventDefault();
      }
    },
    //删除图片
    deleteImgs(imgIndex, item) {
      if (this.oneWorkProcessDialog.addOrUpdate == 'add') {
        this.fileList[item].splice(imgIndex, 1);
      } else if (this.oneWorkProcessDialog.addOrUpdate == 'update') {
        let idx;
        for (let i = 0; i < this.editExistingPictures.length; i++) {
          if ('/storageTrainRepair/' + this.editExistingPictures[i].picAddress == this.imgList[item][imgIndex]) {
            idx = i;
          }
        }

        if (idx > -1) {
          this.editExistingPictures.splice(idx, 1);
        } else if (this.fileList[item]) {
          let URLlist = [];

          this.fileList[item].forEach((item) => {
            if (window.createObjectURL != undefined) {
              URLlist.push(window.createObjectURL(item));
            } else if (window.URL != undefined) {
              URLlist.push(window.URL.createObjectURL(item));
            } else if (window.webkitURL != undefined) {
              URLlist.push(window.webkitURL.createObjectURL(item));
            }
          });
          let indexes = URLlist.indexOf(this.imgList[item][imgIndex]);

          this.fileList[item].splice(indexes, 1);
        }
      }

      this.imgList[item].splice(imgIndex, 1);
      Object.keys(this.imgList).forEach((v) => {
        if (this.imgList[v].length == 0) {
          delete this.imgList[v];
        }
      });
      this.$forceUpdate();
    },

    //点击列表的作业时长
    WorkTimeHandle(rows) {
      let that = this;
      that.workTimeDialog.workLong = rows;
      that.workTimeDialog.visible = true;
      // 获取作业详情
      axios
        .get('/apiTrainRepair/workProcess/getRfidCardSummaryList', {
          params: {
            trainsetId: rows.trainsetId, //车组ID
            dayPlanId: rows.dayPlanId, //作业班次(日计划)
            workerId: rows.workerId, // 作业人员
            itemCode: rows.itemCode //项目code
          },
        })
        .then(function (res) {
          that.workTimeDialog.workDeteil = res.data.data.queryOneWorkProcessDataList;
        });
    },

    //点击列表删除
    deleteHandle: function (rows) {
      var that = this;
      that
        .$confirm('确认删除？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
        .then(() => {
          axios
            .post('/apiTrainRepair/workProcess/delOneWorkProcess', {
              dayPlanId: rows.dayPlanId,
              itemCode: rows.itemCode,
              trainsetId: rows.trainsetId,
              workerId: rows.workerId,
            })
            .then(() => {
              that.$message({
                type: 'success',
                message: '删除成功',
              });
              that.getOneWorkProcessList();
            })
            .catch(() => {
              this.$message({
                message: '删除失败',
                type: 'warning',
              });
            });
        })
        .catch(() => {});
    },

    handleSizeChange(val) {
      this.pageSize = val;
      this.getOneWorkProcessList();
    },

    handleCurrentChange(val) {
      this.pageNum = val;
      this.getOneWorkProcessList();
    },
  },
  components: {},
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('一级修作业过程记录'),
    //计算属性
    dayPlanId() {
      if (this.oneWorkProcessDialog.time && this.oneWorkProcessDialog.shift) {
        return this.oneWorkProcessDialog.time + '-' + this.oneWorkProcessDialog.shift;
      } else {
        return '';
      }
    },

    addDialogTitle() {
      return this.oneWorkProcessDialog.addOrUpdate == 'add' ? '一级修作业记录 -- 新增' : '一级修作业记录 -- 编辑';
    },
  },
  watch: {
    //监听数据改变
  },
  filters: {
    operationShift(val) {
      if (val) {
        let end = val.split('-').pop();
        let time = val.split('-').slice(0, 3).join('-');
        if (end == '00') {
          return time + '-' + '白班';
        } else {
          return time + '-' + '夜班';
        }
      }
    },

    datesourseFilter(val) {
      return val == 1 ? '手持机录入' : '电脑录入'
    },

    carNosFilter(val) {
      if (Array.isArray(val)) {
        let list = [];
        val.forEach((item) => {
          item = item.split('');
          list.push(item.slice(item.length - 2, item.length).join(''));
        });
        return list.join(',');
      } else {
        val = val.split('');
        return val.slice(val.length - 2, val.length).join('');
      }
    },

    timeTagFilter(val) {
      if (val == 1) {
        return '开始';
      } else if (val == 2) {
        return '暂停';
      } else if (val == 3) {
        return '继续';
      } else if (val == 4) {
        return '结束';
      }
    },
  },
  beforeDestroy() {},
});
