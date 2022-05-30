var faultConfigQuery = new Vue({
  el: '#faultConfigQuery',
  data: {
    loading: false,
    isDisabled: false,
    search: {
      keyword: '', // 关键字
      featureClassify: '', // 功能分类
      faultPattern: '', // 故障模式
      faultLevel: '', // 故障等级
      // 故障等级列表
      faultLevelList: [
        { faultGrade: 'A', faultCode: 'A' },
        { faultGrade: 'B', faultCode: 'B' },
        { faultGrade: 'C', faultCode: 'C' },
        { faultGrade: 'D', faultCode: 'D' },
      ],
    },
    main: {
      scrollHeight: 'calc(100% - 42px)',
      tableData: [],
      pageSize: 10,
      currentPage: 1,
      total: '', // 总条数
    },
    dialog: {
      faultInputId: '', // 主键
      bureaOption: [],
      changeId: '',
      keyword: '',
      dialogTableVisible: false,
      dialogCode: '',
      funClassification: '',
      funClassificationList: [],
      optionsProps: {
        value: 'code',
        label: 'name',
        children: 'children',
        emitPath: false,
      },
      gnflList: [],
      jgflList: [],
      selectedfunClassification: '',
      selectedstrucClassification: '',
      strucClassification: '',
      strucClassificationList: [],
      faultPattern: '',
      faultPatternList: [],
      faultLevel: '',
      faultLevelList: [
        { faultGrade: 'A', faultCode: 'A' },
        { faultGrade: 'B', faultCode: 'B' },
        { faultGrade: 'C', faultCode: 'C' },
        { faultGrade: 'D', faultCode: 'D' },
      ],
    },
    restaurants: [],
  },
  created: function () {
    //初始化
  },
  mounted: function () {
    this.initTableList();
  },
  methods: {
    //搜索栏-获取运用所
    initUnitInfo(code) {
      var that = this;
      var yys_url_path = ctxPath + '/MidCommon/getUnitInfo?depotCode=' + code;
      axios
        .get(yys_url_path)
        .then(function (res) {
          if (res.data.code == 1) {
            var data = res.data.rows;
            that.search.bureaOption = data;
            that.dialog.bureaOption = data;
          } else {
            that.showAlertMsg('获取运用所信息失败!', 'warning');
          }
        })
        .catch(function (err) {
          //that.showAlertMsg('运用所接口调用失败!','warning')
        });
    },
    //获取表数据
    initTableList() {
      this.loading = true;
      var that = this;
      setTimeout(function () {
        var trainType_url_path = '/apiTrainRepair/faultconfig/getFaultInputDictList';
        axios
          .post(trainType_url_path, {
            key: that.search.keyword, // 关键字
            functionTypeCode: that.search.featureClassify, // 功能分类
            faultModeName: that.search.faultPattern, // 故障模式
            faultLevelCdoe: that.search.faultLevel, // 故障等级
            pageNum: that.main.currentPage, // 页码
            pageSize: that.main.pageSize, // 每页显示数据条数
          })
          .then(function (res) {
            that.loading = false;
            if (res.data.code == '1') {
              var data = res.data.data;
              that.main.tableData = data.records;
              that.main.total = data.total;
            } else {
              that.showAlertMsg('获取表数据失败!', 'warning');
            }
          })
          .catch(function (err) {
            that.loading = false;
            //that.showAlertMsg('运用所接口调用失败!','warning')
          });
      }, 500);
    },

    //点击搜索
    onSearch() {
      this.initTableList();
    },

    //重置
    onEmpty() {
      this.search.keyword = '';
      this.search.featureClassify = '';
      this.search.faultPattern = '';
      this.search.faultLevel = '';
    },

    //获取功能分类
    getFaultFunctionDict() {
      let that = this;
      var url_path = '/apiTrainRepair/common/getFunctionClass';
      axios
        .get(url_path)
        .then(function (res) {
          if (res.data.code == 1) {
            let data = res.data.data;
            that.dialog.gnflList = data;
            that.dialog.funClassificationList = data;
          } else {
            that.showAlertMsg('获取功能分类信息失败!', 'warning');
          }
        })
        .catch(function (err) {
          //that.showAlertMsg('运用所接口调用失败!','warning')
        });
      $('.el-popper').hide();
    },

    //获取故障模式
    getFaultDict(value) {
      this.dialog.faultPatternList = [];
      this.dialog.faultPattern = '';
      this.dialog.faultLevel = '';
      let subFunctionClassCode
      if (value) {
        subFunctionClassCode = value
      } else {
        subFunctionClassCode = that.dialog.selectedfunClassification
      }
      var that = this;
      var url_path = '/apiTrainFault/faultPart/getFaultDictStandardByFunctionClassCode';
      axios
        .get(url_path, {
          params: {
            subFunctionClassCode
          },
        })
        .then(function (res) {
          if (res.data.code == 0) {
            var data = res.data.data;
            that.dialog.faultPatternList = data;
          } else {
            that.showAlertMsg('获取故障信息失败!', 'warning');
          }
        })
        .catch(function (err) {
          // that.showAlertMsg('运用所接口调用失败!','warning')
        });

      $('.el-cascader__dropdown').hide();
    },

    //获取故障等级
    getFaultLevel(val) {
      let that = this;
      that.dialog.faultLevel = '';
      that.dialog.faultLevelList = []
      if (val) {
        let value = that.dialog.faultPatternList.filter((item) => {
          return item.faultAppCode == val.faultAppCode;
        });
        console.log(value)
        that.dialog.faultLevelList.push({
          faultGrade: value[0]&&value[0].faultGrade,
          faultCode: value[0]&&value[0].faultGrade,
        });
        that.dialog.faultLevel = that.dialog.faultLevelList[0];
      } else {
        that.dialog.faultLevelList = [
          { faultGrade: 'A', faultCode: 'A' },
          { faultGrade: 'B', faultCode: 'B' },
          { faultGrade: 'C', faultCode: 'C' },
          { faultGrade: 'D', faultCode: 'D' },
        ];
        that.dialog.faultLevel = that.dialog.faultLevelList[0];
      }
    },

    //新增
    onAdd() {
      this.dialog.dialogTableVisible = true;
      this.dialog.dialogTitleName = '故障快速录入配置-新增';
      this.dialog.dialogCode = 'add';

      this.getFaultFunctionDict();
    },

    //编辑
    async changeRow(index, row) {
      let that = this;
      that.loading = true;
      that.dialog.dialogTitleName = '故障快速录入配置-编辑';
      that.dialog.dialogCode = 'update';
      var nowIndex = index;
      var data = row[nowIndex];
      that.dialog.faultInputId = data.key.faultInputId;
      that.dialog.keyword = data.key;
      await that.getFaultFunctionDict();
      that.dialog.selectedfunClassification = data.functionTypeCode;
      that.getFaultDict(data.functionTypeCode);
      
      setTimeout(function () {
        that.dialog.faultPattern = {
          faultAppCode: data.faultModeCode,
          faultAppName: data.faultModeName
        };
        that.getFaultLevel(that.dialog.faultPattern);
        that.dialog.faultLevel = {
          faultCode: data.faultLevelCdoe,
          faultGrade: data.faultLevelName
        };
        that.loading = false;
        that.dialog.dialogTableVisible = true;
      }, 500);
    },
    //删除
    deleteRow(index, rows) {
      var nowIndex = index;
      var that = this;
      this.$confirm('确认删除此条数据?', '提示', {
        confirmButtonText: '取消',
        cancelButtonText: '确定',
        type: 'warning',
      })
        .then(function () {
          that.$message({
            type: 'info',
            message: '已取消',
          });
        })
        .catch(function () {
          var url_path = '/apiTrainRepair/faultconfig/delFaultInputDict';
          axios
            .get(url_path, {
              params: {
                faultInputId: rows[nowIndex].faultInputId,
              },
            })
            .then(function (res) {
              that.$message({
                type: 'success',
                message: '已删除!',
              });
              that.initTableList();
            })
            .catch(function (err) {
              //that.showAlertMsg('运用所接口调用失败!','warning')
            });
        });
    },

    //弹出层-确定
    async onSave() {
      var isOk = true;
      if (this.dialog.keyword == '' || this.dialog.keyword == null) {
        this.showAlertMsg('请输入关键字!', 'warning');
        isOk = false;
      } else if (this.dialog.selectedfunClassification == '' || this.dialog.selectedfunClassification == null) {
        this.showAlertMsg('请选择功能分类!', 'warning');
        isOk = false;
      } else if (this.dialog.faultPattern == '' || this.dialog.faultPattern == null) {
        this.showAlertMsg('请选择故障模式!', 'warning');
        isOk = false;
      } else if (this.dialog.faultLevel == '' || this.dialog.faultLevel == null) {
        this.showAlertMsg('请选择故障等级!', 'warning');
        isOk = false;
      } else {
        isOk = true;
      }
      if (isOk) {
        let data = this.$refs.Cascader.getCheckedNodes()[0].data;
        console.log(data);
        var that = this;
        if (this.dialog.dialogCode == 'add') {
          url_path = '/apiTrainRepair/faultconfig/addFaultInputDict';
          postData = {
            key: this.dialog.keyword, // 关键字
            functionTypeCode: data.code, // 功能分类编码
            functionTypeName: data.name, // 功能分类名称
            partCode: '', // 故障部件编码
            partName: '', // 故障部件名称
            partPostion: '', // 故障部件位置ID
            partType: '', // 故障部件类型
            faultLevelCdoe: this.dialog.faultLevel.faultGrade, // 故障等级编码
            faultLevelName: this.dialog.faultLevel.faultCode, // 故障等级名称
            faultModeCode: this.dialog.faultPattern.faultAppCode, // 故障模式编码
            faultModeName: this.dialog.faultPattern.faultAppName, // 故障模式名称
          };
          alertMsg = '添加成功!';
        } else if (this.dialog.dialogCode == 'update') {
          url_path = '/apiTrainRepair/faultconfig/updateFaultInputDict';
          postData = {
            faultInputId: this.dialog.faultInputId, // 主键
            key: this.dialog.keyword, // 关键字
            functionTypeCode: data.code, // 功能分类编码
            functionTypeName: data.name, // 功能分类名称
            partCode: '', // 故障部件编码
            partName: '', // 故障部件名称
            partPostion: '', // 故障部件位置ID
            partType: '', // 故障部件类型
            faultLevelCdoe: this.dialog.faultLevel.faultGrade, // 故障等级编码
            faultLevelName: this.dialog.faultLevel.faultCode, // 故障等级名称
            faultModeCode: this.dialog.faultPattern.faultAppCode, // 故障模式编码
            faultModeName: this.dialog.faultPattern.faultAppName, // 故障模式名称
          };
          alertMsg = '修改成功!';
        }

        let res = await axios.post(url_path, postData);
        if (res.data.code == 1) {
          that.showAlertMsg(alertMsg, 'success');
          that.dialog.dialogTableVisible = false;
          that.initTableList();
        } else {
          that.showAlertMsg(res.data.msg, 'warning');
        }
      }
    },
    //弹出层-取消
    onCancel() {
      this.dialog.dialogTableVisible = false;
      this.dialog.keyword = '';
      this.dialog.selectedfunClassification = '';
      this.dialog.selectedstrucClassification = '';
      this.dialog.faultPattern = '';
      this.dialog.faultLevel = ''; 
      this.dialog.faultLevelList = [
        { faultGrade: 'A', faultCode: 'A' },
        { faultGrade: 'B', faultCode: 'B' },
        { faultGrade: 'C', faultCode: 'C' },
        { faultGrade: 'D', faultCode: 'D' },
      ];
    },

    handleSizeChange(val) {
      this.main.pageSize = val;
      this.initTableList();
    },
    handleCurrentChange(val) {
      this.main.currentPage = val;
      this.initTableList();
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
        duration: duration || 1000,
        type: type || 'warning',
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
  },
  computed: {
    //计算属性
  },
  watch: {
    //监听数据改变
  },
  beforeDestroy() {},
});
