window.app = new Vue({
  el: '#app',
  data() {
    return {
      unitName: '',
      unitCode: '',
      contentStyle: {
        height: '',
        'overflow-y': 'auto',
      },
      loading: false,
      table: {
        data: [],
        scrollHeight: '480px',
        currentPage: 1,
        pageSize: 10,
        total: 1,
      },
      bzData: '',
      bzDataList: [],
      bureauName: '北京北所',
      configItem: '1',
      dialogVisible: false,
      group: 4,
      groupListItem: '',
      groupList: [],
      editNow: '',
      ///////////////
      configObj: {},
      nowPageStatus: '', //页面状态
      trainTypeList: [],
      batchList: [],
      secLevelFormData: {
        trainType: '',
        stageId: '',
        aggPacketName: '',
        itemName: '',
      },
      secLevelFormRules: {
        trainType: [],
        stageId: [],
        aggPacketName: [],
        itemName: [],
      },
      secLevelTableHeight: 0,
      secLevelTableData: [],
      secLevelTablePageNum: 1,
      secLevelTablePageSize: 20,
      secLevelTablePageTotal: 20,
      secPageStatus: '',
      aggPacketNameDialogVisible: false,

      secDialogFormData: {
        trainType: '',
        stageId: '',
        aggPacketName: '',
        aggPacketCar: [],
      },
      secDialogFormPrevData: '',
      secDialogBatchList: [],
      marshalCountList: [],
      secDialogFormRules: {
        trainType: [
          {
            required: true,
            message: '请选择车型',
            trigger: 'change',
          },
        ],
        stageId: [
          {
            required: true,
            message: '请选择批次',
            trigger: 'change',
          },
        ],
        aggPacketName: [
          {
            required: true,
            message: '请输入专业分工名称',
            trigger: 'blur',
          },
        ],
        aggPacketCar: [
          {
            required: true,
            message: '请选择辆序',
            trigger: 'change',
          },
        ],
      },
      editId: '',
      notAddFormData: {
        notAddCondition: '',
      },
      notAddFormRules: {
        notAddCondition: [],
      },
      notAddTableDataPrev: [],
      selectionNotArr: [],

      hasAddFormData: {
        hasAddCondition: '',
      },
      hasAddFormRules: {
        hasAddCondition: [],
      },
      hasAddTableDataPrev: [],
      selectionHasArr: [],
    };
  },
  async created() {
    await this.getUnitCode();
    await this.getUnitName();
    await this.getUser();
    await this.getHeight();
    window.addEventListener('resize', this.getHeight);
    // await this.getGroups();
    //   获取配置控制一级修二级修的权限
    await this.getConfigList();
    if (this.configObj.OneRepairConfig == 1) {
      this.nowPageStatus = '一级修';
    } else if (this.configObj.TwoRepairConfig == 1) {
      this.nowPageStatus = '二级修';
    }

    // 获取车型
    await this.getTraintypeListLocal();
    await this.getAggItemConfigListBtn();
    this.initTable();
  },
  mounted() {
    window.addEventListener('resize', this.mainTableHeightChange);
    this.$once('hook:beforeDestroy', () => {
      window.removeEventListener('resize', this.mainTableHeightChange);
    });
  },
  methods: {
    getHeight() {
      this.contentStyle.height = window.innerHeight + 'px';
      this.table.scrollHeight = window.innerHeight - 87 + 'px';
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

    // 获取当前登录人运用所名称
    async getUnitName() {
      let { data } = await axios.get('/apiTrainRepair/common/getUnitName');
      if (data.code == 1) {
        this.unitName = data.data;
      } else {
        this.$message.error('获取登录人信息失败！');
      }
    },

    //获取用户信息
    async getUser() {
      var url_path = '/apiTrainRepairMidGround/common/getUser';
      let res = await axios.get(url_path);
      if (res.data.code == 1) {
        let data = res.data.data;
        this.bzDataList = [data.workTeam, ...data.partTimeWorkTeams];
        this.bzData = this.bzDataList[0].teamCode;
      } else {
        this.$message.error('获取用户信息失败!');
      }
    },

    mainTableHeightChange() {
      this.secLevelTableHeight = 0;
      this.$nextTick(() => {
        this.secLevelTableHeight = 'calc(100vh - 215px)';
      });
    },
    nowPageStatusChange() {
      this.mainTableHeightChange();
    },
    indexNum(index) {
      index = index + 1 + this.table.pageSize * (this.table.currentPage - 1);
      return index;
    },
    edit(row) {
      this.groupListItem = row.sGroupid;
      this.dialogVisible = true;
      this.group = row.sMarshalnum;
      this.editNow = row;
      const urlPath = '/apiTrainRepair/xzyCOneallotTemplate/getAllTemplateByMarshalNum?marshalNum=' + row.sMarshalnum;
      axios
        .get(urlPath)
        .then((res) => {
          this.groupList = res.data.data;
        })
        .catch((err) => {
          console.log(err);
        });
    },
    changeBz() {
      this.initTable();
    },
    //获取班组
    getGroups() {
      const urlPath = '/apiTrainRepair/common/getWorkTeamsByUnitCode?unitCode=' + this.unitCode;
      axios
        .get(urlPath)
        .then((res) => {
          if (res.data.code === 1) {
            this.bzDataList = res.data.rows;
          } else {
            let msg = res.data.msg || '班组获取失败!';
            this.showAlertMsg(msg);
          }
        })
        .catch((err) => {
          console.log(err);
        });
    },
    handleSave() {
      const urlPath = '/apiTrainRepair/xzyCOneallotConfig/updateOneAllotConfig';
      let json = this.editNow;
      json.sGroupid = this.groupListItem;
      json.sUnitcode = this.unitCode;
      json.sDeptcode = this.bzData;
      axios
        .post(urlPath, json)
        .then((res) => {
          this.dialogVisible = false;
          if (res.data.code === 1) {
            this.showAlertMsg('修改成功!', 'success');
            this.initTable();
          }
        })
        .catch((err) => {});
    },
    //表数据
    initTable: function () {
      this.loading = true;
      const urlPath = '/apiTrainRepair/xzyCOneallotConfig/getOneAllotConfig';
      axios
        .get(urlPath, {
          params: {
            unitCode: this.unitCode,
            deptCode: this.bzData,
            groupId: '',
            marshalNum: '',
          },
        })
        .then((res) => {
          this.loading = false;
          if (res.data.code === 1) {
            this.table.data = res.data.data;
          } else {
            this.showAlertMsg('获取表数据失败!');
          }
        })
        .catch((err) => {
          this.loading = false;
        });
    },
    fomatFlag(row) {
      if (row.cFlag === '1') {
        return '启用';
      } else if (row.cFlag === '0') {
        return '不启用';
      }
    },
    fomatGroup(val) {
      let str = '';
      if (val.oneallotTemplateList) {
        val.oneallotTemplateList.forEach((item, index) => {
          str += item.sCarnolist;
          if (val.oneallotTemplateList.length - 1 != index) {
            str += ',';
          }
        });
      } else {
        str = '01-00';
      }
      return str;
    },
    handleSizeChange(val) {
      this.table.pageSize = val;
      //this.table.currentPage = 1;
      //this.initTableList()
    },
    handleCurrentChange(val) {
      this.table.currentPage = val;
      //this.initTableList()
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

    //   二级修
    async getConfigList() {
      const { data } = await getConfigList();
      data.forEach((item) => {
        this.$set(this.configObj, item.paramName, item.paramValue);
      });
    },
    async getTraintypeListLocal() {
      const { data } = await getTraintypeListLocal();
      this.trainTypeList = data;
    },
    async getPatchListByTraintype(params) {
      const { data } = await getPatchListByTraintype(params);

      if (this.secPageStatus) {
        this.secDialogBatchList = data;
      } else {
        this.batchList = data;
      }
    },
    secLevelFormDataTrainsetChange(val) {
      if (!val) {
        this.secLevelFormData.stageId = '';
        this.batchList = [];
        return;
      }
      this.getPatchListByTraintype({ trainType: val });
    },
    async getAggItemConfigList(params) {
      const {
        data: { aggItemConfigs, count },
      } = await getAggItemConfigList(params);
      this.secLevelTableData = aggItemConfigs;
      this.secLevelTablePageTotal = count;
    },
    getAggItemConfigListBtn() {
      this.getAggItemConfigList(
        Object.assign(this.secLevelFormData, {
          pageSize: this.secLevelTablePageSize,
          pageNum: this.secLevelTablePageNum,
        })
      );
    },
    resetSecLevelFormDataBtn() {
      this.batchList = [];
      this.$refs.secLevelFormRef.resetFields();
    },
    secLevelTablePageSizeChange(pageSize) {
      this.secLevelTablePageSize = pageSize;
      this.getAggItemConfigListBtn();
    },
    secLevelTablePageNumChange(pageNum) {
      this.secLevelTablePageNum = pageNum;
      this.getAggItemConfigListBtn();
    },

    async selectRepairItemListByCarNoParam() {
      const { code, data } = await selectRepairItemListByCarNoParam(
        '',
        this.secDialogFormData.trainType,
        this.secDialogFormData.stageId,
        this.secDialogFormData.aggPacketCar
      );
      if (code == 1) {
        this.notAddTableDataPrev = data;
      }
    },

    async initDialogData(row) {
      this.loading = true
      if (this.secPageStatus !== 'add') {
        this.secDialogFormData.trainType = row.trainType;
        await this.trainTypeChange(this.secDialogFormData.trainType);
        this.secDialogFormData.stageId = row.stageId;
        this.secDialogFormData.aggPacketName = row.aggPacketName;
        this.secDialogFormData.aggPacketCar = row.aggPacketCar.split(',');
        this.hasAddTableDataPrev = row.aggItemConfigItems;
        this.hasAddTableDataPrev.forEach((item) => {
          item.trainType = this.secDialogFormData.trainType;
          item.stageId = this.secDialogFormData.stageId;
        });
        this.editId = row.packetId;
        await this.selectRepairItemListByCarNoParam();
      }
      this.loading = false
      this.aggPacketNameDialogVisible = true;
    },
    addSpecializationBtn() {
      this.secPageStatus = 'add';
      this.initDialogData();
    },
    editSpecializa(row) {
      this.secPageStatus = 'edit';
      this.initDialogData(row);
    },
    lookSpecializa(row) {
      this.secPageStatus = 'look';
      this.initDialogData(row);
    },
    async delAggItemConfig(data) {
      await delAggItemConfig(data);
    },
    async deleteSpecializa(id) {
      const confirmResult = await this.$confirm('此操作将永久删除该配置，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })

      if (confirmResult == 'confirm') {
        await this.delAggItemConfig({ packetId: id });
        this.$message.success('删除成功');
        this.getAggItemConfigListBtn();
      }
    },
    async changeWarning(type, val) {
      let changeFlag = true;
      if (this.hasAddTableDataPrev.length > 0) {
        const confirmResult = await this.$confirm(
          '由于车型、批次、专业分工辆序变更，已添加列表中的项目将被清空，是否继续变更？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        ).catch(() => {});
        if (confirmResult !== 'confirm') {
          changeFlag = false;
          return changeFlag;
        }
        // 确定
        this.secDialogFormData[type] = val;
        this.hasAddTableDataPrev = [];
      } else {
        this.secDialogFormData[type] = val;
      }
      return changeFlag;
    },
    async getCarNoListByTrainType(params) {
      const { data } = await getCarNoListByTrainType(params);

      this.marshalCountList = data.map((item) => {
        return {
          id: item,
          value: item,
          label: item,
        };
      });
    },
    async trainTypeChange(val) {
      if (!(await this.changeWarning('trainType', val))) return;
      this.secDialogBatchList = [];
      this.marshalCountList = [];
      this.secDialogFormData.stageId = '';
      this.secDialogFormData.aggPacketCar = [];
      this.getPatchListByTraintype({ trainType: val });
      if (val) {
        this.getCarNoListByTrainType({ trainType: val });
      }
      this.notAddTableDataPrev = [];
    },
    async batchChange(val) {
      if (!(await this.changeWarning('stageId', val))) return;
      this.notAddTableDataPrev = [];
      if (val) {
        this.selectRepairItemListByCarNoParam();
      }
    },
    async aggPacketCarChange(val) {
      if (!(await this.changeWarning('aggPacketCar', val))) return;
      this.notAddTableDataPrev = [];
      if (val.length > 0) {
        this.selectRepairItemListByCarNoParam();
      }
    },

    selectNotAddTableDataChange(selection) {
      this.selectionNotArr = selection;
    },
    addProjectBtn() {
      if (this.selectionNotArr.length == 0) {
        this.$message.warning('请选择检修项目');
      } else {
        this.hasAddTableDataPrev = [...this.hasAddTableDataPrev, ...this.selectionNotArr];
      }
    },
    selectHasAddTableDataChange(selection) {
      this.selectionHasArr = selection;
    },
    deleteProjectBtn() {
      if (this.selectionHasArr.length == 0) {
        this.$message.warning('请选择检修项目');
      } else {
        const hasSelectIdArr = this.selectionHasArr.map((item) => item.itemCode);
        this.hasAddTableDataPrev = this.hasAddTableDataPrev.filter((item) => !hasSelectIdArr.includes(item.itemCode));
      }
    },
    aggPacketNameDialogClose() {
      this.secDialogFormData = {
        trainType: '',
        stageId: '',
        aggPacketName: '',
        aggPacketCar: [],
      };
      this.$refs.secDialogFormRef.resetFields();
      this.$refs.notAddFormRef.resetFields();
      this.$refs.hasAddFormRef.resetFields();
      this.notAddTableDataPrev = [];
      this.hasAddTableDataPrev = [];
      this.secPageStatus = '';
      this.secDialogFormPrevData = '';
      this.secDialogBatchList = [];
      this.marshalCountList = [];
      this.editId = '';
      this.$nextTick(() => {
        this.$refs.secDialogFormRef.clearValidate();
      });
    },
    async updAggItemConfig(data) {
      await updAggItemConfig(data);
    },
    async addAggItemConfig(data) {
      return await addAggItemConfig(data);
    },
    async confirmProjectBtn() {
      if (this.secPageStatus === 'add') {
        this.$refs.secDialogFormRef.validate(async (valid) => {
          if (!valid) return;
          if (this.hasAddTableData.length === 0) {
            return this.$message.error('至少选择一条检修项目');
          }
          let postObj = JSON.parse(JSON.stringify(this.secDialogFormData));
          postObj.aggItemConfigItems = this.hasAddTableData;
          postObj.aggPacketCar = postObj.aggPacketCar.toString();
          // // 运用所编码
          postObj.unitCode = this.unitCode;
          // // 运用所名称
          postObj.unitName = this.unitName;
          // // 班组编码
          postObj.deptCode = departMentCode;
          // // 班组名称
          postObj.deptName = departMentName;
          this.loading = true
          let data = await this.addAggItemConfig(postObj);
          this.loading = false
          if (data.code == 1) {
            this.aggPacketNameDialogVisible = false;
            this.$message.success('保存成功');
          } else {
            this.$message({
              message: data.msg,
              type: 'warning',
            });
          }
          this.getAggItemConfigListBtn();
        });
      } else if (this.secPageStatus === 'edit') {
        if (this.hasAddTableData.length === 0) {
          return this.$message.error('至少选择一条检修项目');
        }
        this.loading = true
        await this.updAggItemConfig({ packetId: this.editId, aggItemConfigItems: this.hasAddTableData });
        this.loading = false
        this.aggPacketNameDialogVisible = false;
        this.$message.success('保存成功');
        this.getAggItemConfigListBtn();
      } else if (this.secPageStatus === 'look') {
        this.aggPacketNameDialogVisible = false;
      }
    },
  },
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('派工配置'),
    aggPacketNameDialogTitle({ secPageStatus }) {
      if (secPageStatus === 'add') {
        return '新增专业分工';
      } else if (secPageStatus === 'edit') {
        return '编辑专业分工';
      } else if (secPageStatus === 'look') {
        return '查看专业分工';
      }
    },
    notAddTableData({ secDialogFormData, notAddTableDataPrev, hasAddTableDataPrev }) {
      // 添加基础表单的车型批次
      let notAddTableData = notAddTableDataPrev.map((item) => {
        let newObj = JSON.parse(JSON.stringify(item));
        newObj.trainType = secDialogFormData.trainType;
        newObj.stageId = secDialogFormData.stageId;
        return newObj;
      });

      // 通过筛选条件
      notAddTableData = notAddTableData.filter((item) => item.itemName.includes(this.notAddFormData.notAddCondition));
      // 通过右侧表格内容
      let hasIdArr = hasAddTableDataPrev.map((item) => {
        return item.itemCode;
      });
      let arr = notAddTableData.filter((notItem) => {
        return !hasIdArr.includes(notItem.itemCode);
      });
      return arr;
    },
    // 真实的已添加列表
    hasAddTableData({ hasAddTableDataPrev }) {
      // 通过筛选条件
      return hasAddTableDataPrev;
    },
    // 通过筛选条件查看的已添加列表
    showHasAddTableData({ hasAddTableDataPrev }) {
      // 通过筛选条件
      return hasAddTableDataPrev.filter((item) => item.itemName.includes(this.hasAddFormData.hasAddCondition));
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

  watch: {},

  //指令
  directives: {
    focus: {
      inserted: function (el) {
        el.focus();
      },
    },
  },
});
