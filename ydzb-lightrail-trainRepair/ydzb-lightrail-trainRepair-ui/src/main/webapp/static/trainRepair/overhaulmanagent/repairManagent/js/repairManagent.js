var app = new Vue({
  el: '#MaintenanceOS',
  data() {
    var inputLimit = (rule, value, callback) => {
      let reg = /^[0-9]+.?[0-9]*/;
      if (value && !reg.test(value)) {
        callback(new Error('请输入数字值'));
      } else {
        if (value && value > 1000) {
          callback(new Error('最大数不能超过1000'));
        } else {
          callback();
        }
      }
    };

    var imgNumLimit = (rule, value, callback) => {
      if (value) {
        let reg = /^[0-9]+.?[0-9]*/;
        if (value && !reg.test(value)) {
          callback(new Error('请输入数字值'));
        } else {
          if (value && value > 1000) {
            callback(new Error('最大数不能超过1000'));
          } else {
            callback();
          }
        }
      } else {
        callback(new Error('请输入图片数量'));
      }
    };
    return {
      loading: false,
      show01: true,
      show02: false,
      isDisDcd: false,
      maxMinDis: false,
      warningDis: false,
      isAddorUpdate: '',
      editId: '',
      search: {
        trainType: '',
        trainTypeOptions: [],
        batch: '', // 选中批次
        batchOptions: [], // 批次列表
        pc: '',
        pcOptions: [],
        xc: '1',
        xcOptions: [
          {
            name: '一级修',
            id: '1',
          },
          {
            name: '二级修',
            id: '2',
          },
        ],
        zyxm: '',
        zyxmOptions: [],
        MyType: '', // 类型
        lx: '',
        lxczOptions: [],
        dialogTableVisible: false,
      },
      main: {
        scrollHeight: 'calc(100% - 42px)',
        tableData: [],
        pageSize: 10,
        currentPage: 1,
        total: null,
      },
      dialog: {
        isDisabled: false,
        dialogTitle: '',
        trainType: '', // 车型
        batch: '', // 批次
        batchOptions: [], // 批次列表
        maxTime: '',
        minTime: '',
        warningTime: '',
        taskPost: [], // 作业岗位
        taskPostOption: [], // 作业岗位列表/优先作业角色列表
        ele: '1',
        imgNum: 0,
        elOption: [], // 供断电状态列表
        priorityTask: [], // 优先作业角色
        xc: '1',
        pc: '',
        pcOptions: [],
        content: '', // 作业内容
        contentAbbr: '', // 作业内容简称
        workerNumber: '', // 作业人数
        czOptions: [],
        man: [],
        manOptions: [],
        isCopy: false, // 控制复制弹框的弹出

        current: {}, // 二级修--修改保存当前列表项数据
      },
      dialogRules: {
        trainType: [{ required: true, message: '请选择车型', trigger: 'blur' }],
        batch: [{ required: true, message: '请选择批次', trigger: 'blur' }],
        taskPost: [{ required: true, message: '请选择作业岗位', trigger: 'blur' }],
        ele: [{ required: true, message: '请选择供断电状态', trigger: 'blur' }],
        content: [{ required: true, message: '请输入作业内容', trigger: 'blur' }],
        contentAbbr: [{ required: true, message: '请输入作业内容简称', trigger: 'blur' }],
        minTime: [{ validator: inputLimit, trigger: 'blur' }],
        maxTime: [{ validator: inputLimit, trigger: 'blur' }],
        warningTime: [{ validator: inputLimit, trigger: 'blur' }],
        imgNum: [{ validator: inputLimit, trigger: 'blur' }],
        workerNumber: [{ validator: inputLimit, trigger: 'blur' }],
      },
      iscopy: {
        trainType: '', // 车型
        batch: '', // 批次
        batchOptions: [], // 批次列表
        content: '', // 作业内容
        contentList: '', // 作业内容列表
      },

      iscopyRules: {
        trainType: [{ required: true, message: '请选择车型', trigger: 'blur' }],
        content: [{ required: true, message: '请选择作业内容', trigger: 'blur' }],
      },

      twoWorkEdit: {
        visible: false, // 二级修编辑弹框控制
        imgNum: 0,
      },

      twoWorkEditRules: {
        imgNum: [{ required: true, validator: imgNumLimit, trigger: 'blur' }],
      },
      restaurants: [],
      selectTableData: [],
    };
  },
  created() {
    //初始化
    this.getTrainType();
    this.getPower();
  },
  mounted() {
    //dom操作
    var that = this;
    this.loading = true;
    // window.onresize = function (ev) {
    //     that.main.scrollHeight = $(window).height() - 260;
    // }
    // this.main.scrollHeight = $(window).height() - 260;
    setTimeout(function () {
      that.initTableList();
    }, 500);
  },
  methods: {
    //点击搜索
    onSearch() {
      this.loading = true;
      this.main.currentPage = 1;
      var that = this;
      setTimeout(function () {
        that.initTableList();
      }, 200);
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
        })
        .catch(function (error) {
          that.showAlertMsg('获取车型失败!', 'warning');
        });
    },

    // 切换-模糊查询-车型
    changTrainType(val) {
      var that = this;
      if (!that.search.dialogTableVisible) {
        that.search.batch = '';
        that.getPatchListByTraintype(val, '1');
      } else if (that.dialog.isCopy) {
        that.iscopy.batch = '';
        this.iscopy.content = '';
        that.getPatchListByTraintype(val, '3');
        that.getWorkcritertionList();
      } else {
        that.dialog.batch = '';
        that.getPatchListByTraintype(val, '2');
      }
    },

    //批次下拉框数据
    getPatchListByTraintype(val, flag) {
      var that = this;
      const params = {
        traintype: val,
      };
      axios({
        url: '/apiTrainResume/resume/getPatchListByTraintype',
        method: 'get',
        params,
      })
        .then(function (res) {
          switch (flag) {
            case '1':
              that.search.batchOptions = res.data.data;
              break;
            case '2':
              that.dialog.batchOptions = res.data.data;
              break;
            case '3':
              // that.iscopy.batch = res.data.data[0]
              that.iscopy.batchOptions = res.data.data;
              break;
          }
        })
        .catch(function (error) {
          that.showAlertMsg('获取批次失败!', 'warning');
        });
    },
    //初始化表数据
    initTableList() {
      var that = this;
      this.main.tableData = [];
      if (that.search.xc == 1) {
        var trainType_url_path = '/apiTrainRepair/xzyCWorkcritertion/getWorkcritertionList';
        let data = {
          trainsetType: that.search.trainType, // 车型 ,
          trainsetSubType: that.search.batch, //批次
          cyc: that.search.xc, //修程 ,
          itemName: that.search.zyxm, // 作业内容
          pageNum: that.main.currentPage, // 页码
          pageSize: that.main.pageSize, // 每页显示数据条数
        };
        axios
          .post(trainType_url_path, data)
          .then(function (res) {
            that.loading = false;
            if (res.data.code == 1) {
              var data = res.data.data;
              that.main.tableData = data.xzyCWorkcritertionList;
              that.main.total = data.count;
            } else {
              that.showAlertMsg('获取列表失败!', 'warning');
            }
          })
          .catch(function (err) {
            that.loading = false;
            //that.showAlertMsg('运用所接口调用失败!','warning')
          });
      } else {
        var trainType_url_path = '/apiTrainRepair/xzyCWorkcritertion/getSecondWorkcritertionList';
        let data = {
          unitCode: '', // 所编码
          trainsetType: that.search.trainType, // 车型 ,
          trainsetSubType: that.search.batch, //批次
          itemName: that.search.zyxm, // 作业内容
          pageNum: that.main.currentPage, // 页码
          pageSize: that.main.pageSize, // 每页显示数据条数
        };
        axios
          .post(trainType_url_path, data)
          .then(function (res) {
            that.loading = false;
            if (res.data.code == 1) {
              var data = res.data.data;
              that.main.tableData = data.xzySecondCWorkcritertionList;
              that.main.total = data.count;
            }
          })
          .catch(function (err) {
            that.loading = false;
            //that.showAlertMsg('运用所接口调用失败!','warning')
          });
      }
    },
    eleFormatter(row, column) {
      return this.powerStateMap[row.sPowerstate];
    },
    //重置
    reset() {
      this.search.trainType = '';
      this.search.batch = '';
      this.search.zyxm = '';
      this.search.MyType = '';
    },

    //新增
    onAdd() {
      this.initDialog();
      this.search.dialogTableVisible = true;
      this.dialog.isDisabled = false;
      this.dialog.dialogTitle = '新增配置';
      this.isAddorUpdate = 'add';
      this.editId = '';
      this.getTrainType('2');
      this.getWarningPeople();
      this.getPostRoleList();

      this.$nextTick().then(async () => {
        await this.$refs.dialog.clearValidate();
      });
    },

    //获取预警角色
    getWarningPeople() {
      var that = this;
      var url_path = '/apiTrainRepair/xzyCWorkcritertion/getCritertionDict';
      axios
        .get(url_path)
        .then(function (res) {
          that.dialog.manOptions = res.data.data;
        })
        .catch(function (err) {
          //that.showAlertMsg('运用所接口调用失败!','warning')
        });
    },

    // 作业岗位
    async getPostRoleList() {
      let url = '/apiTrainRepair/post/getPostList';
      let {data} = await axios.get(url, {
        params: {
          pageNum: 1,
          pageSize: -1,
        },
      });
      let list = []
      data.data.postList.forEach(item => {
        list.push({
          code: item.postId,
          name: item.postName,
        })
      })
      this.dialog.taskPostOption = list;
    },

    //获取供断电状态
    getPower() {
      let that = this;
      var url_path = '/apiTrainRepair/xzyCWorkcritertion/getPowerStateDict';
      axios
        .get(url_path)
        .then(function (res) {
          that.dialog.ele = res.data.data[0].id;
          that.dialog.elOption = res.data.data;
        })
        .catch(function (err) {
          that.showAlertMsg('获取供断电状态失败!', 'warning');
        });
    },

    //初始化弹出层
    initDialog() {
      this.dialog.trainType = '';
      this.dialog.batch = '';
      this.dialog.pc = '';
      this.dialog.maxTime = '';
      this.dialog.warningTime = '';
      this.dialog.minTime = '';
      this.dialog.imgNum = 0;
      this.dialog.man = '';
      this.dialog.ele = '1';
      this.dialog.content = '';
      this.dialog.taskPost = [];
      this.dialog.priorityTask = [];
      this.dialog.contentAbbr = [];
      this.dialog.batchOptions = [];
    },

    reduction(List, code) {
      return List.map((item) => {
        return item[code];
      });
    },

    //修改
    changeRow(index, rows) {
      var data = rows[index];
      this.maxMinDis = false;
      this.warningDis = false;
      this.dialog.isDisabled = true;
      if (this.search.xc == 1) {
        this.search.dialogTableVisible = true;
        this.dialog.dialogTitle = '修改配置';
        this.isAddorUpdate = 'update';
        this.getTrainType('2');
        this.getWarningPeople();
        this.getPostRoleList();

        this.getPatchListByTraintype(data.sTrainsettype, '2');

        this.editId = data.sCritertionid;
        this.dialog.trainType = data.sTrainsettype;
        this.dialog.batch = data.sTrainsetsubtype;
        this.dialog.minTime = data.iMinworktime;
        this.dialog.maxTime = data.iMaxworktime;
        this.dialog.warningTime = data.sWarningtime;
        this.dialog.man = this.reduction(data.xzyCWorkcritertionRoleList, 'sRolecode');
        this.dialog.taskPost = this.reduction(data.xzyCWorkcritertionPostList, 'postCode');
        this.dialog.ele = data.sPowerstate;
        this.dialog.priorityTask = this.reduction(data.xzyCWorkritertionPriRoleList, 'priRoleCode');
        this.dialog.sItemcode = data.sItemcode;
        this.dialog.content = data.sItemname;
        this.dialog.contentAbbr = data.sItemnameAbbr;
        this.dialog.workerNumber = data.personCount;
        this.dialog.imgNum = data.iPiccount;

        this.$nextTick().then(async () => {
          await this.$refs.dialog.clearValidate();
        });
      } else {
        this.twoWorkEdit.visible = true;
        this.dialog.current = data;
        this.twoWorkEdit.imgNum = data.iPiccount;

        this.$nextTick().then(async () => {
          await this.$refs.twoWorkEdit.clearValidate();
        });
      }
    },

    // 二级修修改-确认
    modify(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          this.twoWorkEdit.visible = false;
          this.dialog.current.iPiccount = this.twoWorkEdit.imgNum;
          await this.updateSecondWorkcriterion(this.dialog.current);
          this.twoWorkEdit.imgNum = '';
          await this.initTableList();
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },

    // 二级修修改-取消
    cancelCopyEdit() {
      this.twoWorkEdit.imgNum = '';
    },

    // 二级修修改请求
    async updateSecondWorkcriterion(data) {
      var url_path = '/apiTrainRepair/xzyCWorkcritertion/updateSecondWorkcriterion';
      let res = await axios.post(url_path, data);
      if (res.data.code == 1) {
        this.$message({
          type: 'success',
          message: '修改成功',
        });
      } else {
        this.$message({
          type: 'error',
          message: err,
        });
      }
    },

    // 复制
    onCopy() {
      this.dialog.isCopy = true;
      this.iscopy.trainType = this.search.trainTypeOptions[0];
      this.getPatchListByTraintype(this.search.trainTypeOptions[0], '3');
      this.getWorkcritertionList();

      this.$nextTick().then(async () => {
        await this.$refs.iscopy.clearValidate();
      });
    },

    // 复制-获取作业内容下拉
    async getWorkcritertionList() {
      var that = this;
      that.iscopy.contentList = [];
      var trainType_url_path = '/apiTrainRepair/xzyCWorkcritertion/getWorkcritertionList';
      let Data = {
        trainsetType: that.iscopy.trainType, // 车型 ,
        trainsetSubType: that.iscopy.batch, //批次
        cyc: '', //修程 ,
        itemName: that.iscopy.content, // 作业内容
        pageNum: 1, // 页码
        pageSize: -1, // 每页显示数据条数
      };
      await axios
        .post(trainType_url_path, Data)
        .then(function (res) {
          that.loading = false;
          if (res.data.code == 1) {
            var data = res.data.data.xzyCWorkcritertionList;
            if (Data.itemName) {
              that.editId = data[0].sCritertionid;
              that.dialog.trainType = data[0].sTrainsettype;
              that.dialog.batch = data[0].sTrainsetsubtype;
              that.dialog.minTime = data[0].iMinworktime;
              that.dialog.maxTime = data[0].iMaxworktime;
              that.dialog.warningTime = data[0].sWarningtime;
              that.dialog.man = that.reduction(data[0].xzyCWorkcritertionRoleList, 'sRolecode');
              that.dialog.imgNum = data[0].iPiccount;
              that.dialog.taskPost = that.reduction(data[0].xzyCWorkcritertionPostList, 'postCode');
              that.dialog.ele = data[0].sPowerstate;
              that.dialog.priorityTask = that.reduction(data[0].xzyCWorkritertionPriRoleList, 'priRoleCode');
              that.dialog.sItemcode = data[0].sItemcode;
              that.dialog.content = data[0].sItemname;
              that.dialog.contentAbbr = data[0].sItemnameAbbr;
            } else {
              let List = [];
              data.forEach((item) => {
                List.push(item.sItemname);
              });
              that.iscopy.contentList = List;
            }
          }
        })
        .catch(function (err) {
          that.loading = false;
          //that.showAlertMsg('运用所接口调用失败!','warning')
        });
    },

    // 复制-切换批次
    changeBatch() {
      this.iscopy.content = '';
      this.getWorkcritertionList();
    },

    // 确认复制
    confirmCopy(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          this.getWorkcritertionList();
          this.dialog.batchOptions = this.iscopy.batchOptions;
          this.dialog.isCopy = false;
        }
      });
    },

    // 取消复制
    cancelCopy() {
      this.iscopy.batch = '';
      this.iscopy.content = '';
      this.dialog.isCopy = false;
    },

    //删除
    deleteRow(index, rows) {
      // var nowIndex = index;
      // if (this.main.currentPage != 1) {
      //     nowIndex = (this.main.currentPage - 1) * this.main.pageSize + index
      // }
      let tips;
      if (this.search.xc == 1) {
        tips = '确认删除此条数据?';
      } else {
        tips = '确认清空此条数据?';
      }
      var that = this;
      this.$confirm(tips, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(function () {
          that.deleteWorkcritertion(index, rows);
        })
        .catch(function () {
          // that.$message({
          //   type: 'info',
          //   message: '已取消',
          // });
        });
    },

    // 删除列表项
    async deleteWorkcritertion(index, rows) {
      var that = this;
      if (that.search.xc == 1) {
        var yys_url_path = '/apiTrainRepair/xzyCWorkcritertion/deleteWorkcritertion';
        let res = await axios.post(yys_url_path, {
          xzyCWorkcritertionIds: [rows[index].sCritertionid],
        });

        if (res.data.code == 1) {
          that.$message({
            type: 'success',
            message: '删除成功',
          });
          that.initTableList();
        } else {
          that.$message({
            type: 'error',
            message: '删除失败',
          });
        }
      } else {
        var yys_url_path = '/apiTrainRepair/xzyCWorkcritertion/delSecondWorkcriterion';
        axios
          .get(yys_url_path, {
            params: {
              sCritertionid: rows[index].sCritertionid,
            },
          })
          .then(function (res) {
            that.$message({
              type: 'success',
              message: res.data.msg,
            });
            that.initTableList();
          })
          .catch(function (err) {
            that.$message({
              type: 'error',
              message: err,
            });
          });
      }
    },

    changeXc_dialog() {
      if (this.dialog.xc == '1') {
        this.dialog.isDisabled = false;
      } else if (this.dialog.xc == '2') {
        this.dialog.isDisabled = true;
      }
    },
    //限制中文长度
    // limitLentth_CN(str,len){
    //     var temp = 0;
    //     for(var i=0;i<str.value.length;i++){
    //         if(/[\u4e00-\u9fa5]/.test(str.value[i])){
    //             temp += 2
    //         }else{
    //             temp++
    //         }
    //         if(temp>len){
    //             str.value = str.value.substr(0,i)
    //         }
    //     }
    // },
    changeXc() {
      this.main.currentPage = 1;
      this.reset();
      this.initTableList();
    },
    handleSizeChange(val) {
      this.main.pageSize = val;
      this.initTableList();
    },
    handleCurrentChange(val) {
      this.main.currentPage = val;
      this.initTableList();
    },
    // onCopy () {
    //     var that = this;
    //     if (parseInt(this.dialog.maxTime) < parseInt(this.dialog.minTime)) {
    //         that.showAlertMsg('最小检修时长不能大于最大检修时长!', 'success', 3000);
    //     } else {
    //         if (this.isAddorUpdate == 'add') {
    //             if (this.isOk()) {
    //                 this.addLimt = true;
    //                 var url_path = "/apiTrainRepair/xzyCWorkcritertion/add";
    //                 axios.post(url_path, that.getPostData()).then(function (res) {
    //                     if (res.data.code == 0) {
    //                         that.showAlertMsg('添加成功!', 'success');
    //                         that.copy.dialogTableVisible = true;
    //                         that.itemCode = res.data.itemCode;
    //                         that.copy.content = that.dialog.content;
    //                         setTimeout(function () {
    //                             that.addLimt = false;
    //                         }, 500)
    //                     } else {
    //                         that.showAlertMsg(res.data.msg, 'error')
    //                         setTimeout(function () {
    //                             that.addLimt = false;
    //                         }, 500)
    //                     }
    //                 }).catch(function (err) {
    //                     //that.showAlertMsg('运用所接口调用失败!','warning')
    //                 });
    //             } else {
    //                 that.showAlertMsg('请填写必填项!', 'warning')
    //             }
    //         } else {
    //             that.copy.dialogTableVisible = true;
    //             that.copy.content = that.dialog.content;
    //         }
    //     }
    // },

    // 新增参数数组对象结构
    myPost(val, List, code, name, dataCode, dataName, dataType) {
      var values = val;
      let s_workers = [];

      for (var i = 0; i < List.length; i++) {
        for (var j = 0; j < values.length; j++) {
          if (List[i][code] == values[j]) {
            var workersJson = {};
            workersJson[dataName] = List[i][name];
            workersJson[dataCode] = List[i][code];
            if (List[i].type) {
              workersJson[dataType] = List[i].type;
            }
            s_workers.push(workersJson);
          }
        }
      }
      return s_workers;
    },

    // 新增接口请求参数
    getPostData() {
      var that = this;
      let sItemnameabbr;
      if (that.dialog.contentAbbr == '' || that.dialog.contentAbbr == null) {
        sItemnameabbr = that.dialog.content;
      } else {
        sItemnameabbr = that.dialog.contentAbbr;
      }

      var postData = {
        sTrainsettype: that.dialog.trainType, // 车型
        sTrainsetsubtype: that.dialog.batch, // 批次
        sCyc: that.dialog.xc, // 修程
        iMaxworktime: that.dialog.maxTime, // 最大作业时长
        iMinworktime: that.dialog.minTime, // 最小作业时长
        sWarningtime: that.dialog.warningTime, // 预警时间长
        iPiccount: that.dialog.imgNum, // 照片数量
        sPowerstate: that.dialog.ele, // 供断电状态
        sItemcode: that.dialog.sItemcode,
        sItemname: that.dialog.content, // 作业内容
        sItemnameabbr: sItemnameabbr, // 作业内容简称
        xzyCWorkcritertionPostList: this.myPost(
          this.dialog.taskPost,
          this.dialog.taskPostOption,
          'code',
          'name',
          'postCode',
          'postName',
          'type'
        ), // 岗位集合
        xzyCWorkcritertionRoleList: this.myPost(
          this.dialog.man,
          this.dialog.manOptions,
          'roleCode',
          'roleName',
          'sRolecode',
          'sRolename'
        ), // 预警角色集合
        xzyCWorkritertionPriRoleList: this.myPost(
          this.dialog.priorityTask,
          this.dialog.taskPostOption,
          'code',
          'name',
          'priRoleCode',
          'priRoleName',
          'type'
        ), // 优先角色集合
        personCount: that.dialog.workerNumber,
      };
      return postData;
    },

    isOk() {
      let that = this;
      var dataObj = that.dialog;
      if (!dataObj.trainType || dataObj.trainType == null) {
        return false;
      }
      if (!dataObj.batch || dataObj.batch == null) {
        return false;
      }
      if (dataObj.taskPost.length == 0) {
        return false;
      }
      if (!dataObj.content || dataObj.content == null) {
        return false;
      }
      if (!dataObj.ele || dataObj.ele == null) {
        return false;
      }
      if (!dataObj.contentAbbr || dataObj.contentAbbr == null) {
        return false;
      }
      return true;
    },

    // 新增、修改确认
    onSave(formName) {
      var that = this;
      that.$refs[formName].validate(async (valid) => {
        if (valid) {
          if (that.dialog.warningTime) {
            if (that.dialog.man == '' || that.dialog.man == null) {
              that.showAlertMsg('提前预警时长填写后,预警角色不能为空!', 'warning');
              return;
            }
          }
          if (
            that.dialog.minTime <= 1000 &&
            that.dialog.maxTime <= 1000 &&
            that.dialog.warningTime <= 1000 &&
            that.dialog.imgNum <= 1000 &&
            that.dialog.workerNumber <= 1000
          ) {
            if (parseInt(that.dialog.maxTime) < parseInt(that.dialog.minTime)) {
              that.showAlertMsg('最小检修时长不能大于最大检修时长!', 'warning', 3000);
            } else {
              if (that.isAddorUpdate == 'add') {
                var url_path = '/apiTrainRepair/xzyCWorkcritertion/addWorkcritertion';
                var loading = that.$loading();
                await axios
                  .post(url_path, that.getPostData())
                  .then(function (res) {
                    if (res.data.code == 1) {
                      that.showAlertMsg('添加成功!', 'success');
                      that.initTableList();
                      that.search.dialogTableVisible = false;
                    } else {
                      that.showAlertMsg(res.data.msg, 'error');
                    }
                  })
                  .finally(function () {
                    loading.close();
                  });
              } else if (that.isAddorUpdate == 'update') {
                var postData = that.getPostData();
                postData.sCritertionid = that.editId;
                var url_path = '/apiTrainRepair/xzyCWorkcritertion/updateWorkcritertion';
                var loading = that.$loading();
                await axios
                  .post(url_path, postData)
                  .then(function (res) {
                    if (res.data.code == 1) {
                      that.showAlertMsg('修改成功!', 'success');
                      that.search.dialogTableVisible = false;
                      that.initTableList();
                    } else {
                      that.showAlertMsg(res.data.msg, 'warning');
                    }
                  })
                  .finally(function () {
                    loading.close();
                  });
              }
            }
          } else {
            that.showAlertMsg('最小检修时长、最大检修时长、提前预警时长，作业人数，图片数量要求最大为1000', 'warning');
          }
        }
      });
      // if (that.isOk()) {
      // } else {
      //   that.showAlertMsg('请填写必填项!', 'warning');
      // }
    },
    onCancel() {
      this.initDialog();
      this.search.dialogTableVisible = false;
      this.dialog.trainType = '';
      this.dialog.maxTime = '';
      this.dialog.warningTime = '';
      this.dialog.minTime = '';
      this.dialog.imgNum = '';
      this.dialog.man = [];
      this.dialog.ele = '';
      this.dialog.content = '';
      this.dialog.workerNumber = '';
    },
    //补零
    toDou(n) {
      return n < 10 ? '0' + n : '' + n;
    },
    //提示文本样式统一
    showAlertMsg(msg, type, duration) {
      this.$message({
        showClose: true,
        message: msg || '失败!',
        duration: duration || 3000,
        type: type || 'warning',
      });
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
    querySearchAsync(queryString, cb) {
      var restaurants = this.restaurants;
      var results = queryString ? restaurants.filter(this.createStateFilter(queryString)) : restaurants;

      clearTimeout(this.timeout);
      this.timeout = setTimeout(function () {
        cb(results);
      }, 1000);
    },
    createStateFilter(queryString) {
      return function (state) {
        return state.trainNo.toLowerCase().indexOf(queryString.toLowerCase()) === 0;
      };
    },

    // 列表切换
    onSelectionChange(val) {
      this.selectTableData = val;
    },

    // 点击批量删除
    ondelect() {
      var that = this;
      if(that.selectTableData.length == 0) {
        that.$message({
          type: 'warning',
          message: "请选择要删除的作业配置！",
        });
      } else {
        that
        .$confirm('确认删除数据?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
        .then(function () {
          var yys_url_path = '/apiTrainRepair/xzyCWorkcritertion/deleteWorkcritertion';
          axios
            .post(yys_url_path, {
              xzyCWorkcritertionIds: that.selectTableData.map((item) => {
                return item.sCritertionid;
              }),
            })
            .then(function (res) {
              that.$message({
                type: 'success',
                message: res.data.msg,
              });
              that.initTableList();
            })
            .catch(function (err) {
              that.$message({
                type: 'error',
                message: err,
              });
            });
        })
        .catch(function () {
          // that.$message({
          //   type: 'info',
          //   message: '已取消',
          // });
        });
      }
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

    // 预警角色过滤
    sRolenameFilter(val) {
      let list = [];
      val.forEach((item) => {
        list.push(item.sRolename);
      });
      return list.join(',');
    },

    // 岗位过滤
    postNameFilter(val) {
      let list = [];
      // val &&
      //   val.forEach((item) => {
      //     list.push(item.postName);
      //   });

      return (
        val &&
        val
          .map((item) => {
            return item.postName;
          })
          .toString()
      );
    },
  },
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('派工配置'),
    //计算属性
    powerStateMap() {
      var map = {};
      if (this.dialog.elOption) {
        this.dialog.elOption.forEach((powerStateOption) => {
          map[powerStateOption.id] = powerStateOption.name;
        });
      }
      return map;
    },
    isCopy() {
      return this.isAddorUpdate === 'copy';
    },
  },
  watch: {
    //监听数据改变
  },
  beforeDestroy() {},
});

function fomatterColumn(row) {
  var showName = [];
  for (var i = 0; i < row.length; i++) {
    showName.push(row[i].sRolename);
  }
  return showName.join(',');
}
