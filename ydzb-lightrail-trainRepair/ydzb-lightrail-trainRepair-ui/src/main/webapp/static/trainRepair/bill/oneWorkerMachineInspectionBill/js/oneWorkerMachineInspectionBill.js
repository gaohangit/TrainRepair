var app = new Vue({
  el: '#app',
  filters: {},

  data() {
    return {
      // URL携带参数
      webPageTitle: '',
      // 是否可编辑
      edit: edit,
      technique: technique,
      dispatch: dispatch,
      // 当前登录人所编码
      unitCode: '',
      // 当前登录人信息
      user: undefined,
      // 搜索部分
      searchForm: {
        loading: undefined,
        name: '',
        // 日期
        Date: '',
        oriDate: '',
        // 班次
        trainShift: '',
        oriTrainShift: '',
        // 作业班组
        workerTeam: '',
        oriWorkerTeam: '',
        // 派工人员
        dispatchPerson: '',
        oriDispatchPerson: '',
        // 动车组
        trainGroup: '',
      },

      // 班次列表
      trainShiftList: [
        {
          name: '白班',
          id: '00',
        },
        {
          name: '夜班',
          id: '01',
        },
      ],

      // 作业班组列表
      workerTeamList: [],
      // 派工人员列表
      dispatchPersonList: [],
      // 动车组列表
      trainGroupList: [],

      // 数据列表
      tableData: [],
      oriTableData: [],
      // 选中的列表项
      multipleSelection: [],
      // 当前页
      pageNum: 1,
      // 每页展示条数
      pageSize: 10,
      // 总条数
      total: 0,

      // 回填部分
      spreadDialog: {
        visible: false,
        // 回填数据
        billDetail: {},
        // Excel表格组件配置
        billSpreadModuleConfig: {
          readOnly: false, // 是否只读，默认值为false
          allowExtendOperation: false, // 20211104新增 是否允许扩展操作：撤销、单元格粘贴、dragFill(单元格右下角加号拖拽)，默认值为false
          maxTextLength: 200, // 20211108新增 用户编辑最大字数限制，默认200
        },
        // Excel表格组件
        billSpreadModuleObj: undefined,
      },

      excelExport: {
        billSpreadModuleObj: undefined,

        completedNum: 0,
        status: null,
        statusMessage: null,
        progressShow: false,
      },

      loading: undefined,
      columnData: undefined,
    };
  },

  computed: {
    pageTitle() {
      return GeneralUtil.getObservablePageTitleGetter(this.webPageTitle)();
    },

    // 日计划
    dayPlayId() {
      return this.searchForm.Date + '-' + this.searchForm.trainShift;
    },

    // 展示列表
    getTableData() {
      let tableData = JSON.parse(JSON.stringify(this.oriTableData));
      return tableData.slice((this.pageNum - 1) * this.pageSize, this.pageNum * this.pageSize);
    },

    excelTotal() {
      return this.multipleSelection.length;
    },

    message() {
      if (!this.excelExport.status) {
        return `正在导出${this.excelExport.completedNum}/${this.excelTotal}`;
      } else {
        return this.excelExport.statusMessage;
      }
    },

    progressPropData() {
      return {
        type: 'circle',
        percentage: Math.max(0, Math.min(100, Math.round((this.excelExport.completedNum / this.excelTotal) * 100))),
        status: this.excelExport.status,
      };
    },
  },

  async created() {
    this.searchForm.loading = this.$loading();
    this.webPageTitle = '一级修机检记录单回填';
    await this.getUnitCode();
    await this.getUser();
    await this.getDay();
    await this.getChecklistQueryCondition();
    await this.getPersonByDept();

    await this.submitForm();

    let result = this.dispatchPersonList.filter((item) => {
      return item.workId == this.user.staffId;
    });

    if (this.searchForm.workerTeam && !this.technique && !this.dispatch && result.length > 0) {
      this.searchForm.dispatchPerson = this.user.staffId;
    }
  },

  mounted() {},

  methods: {
    // 表格斑马纹颜色控制
    tableRowClassName({ row, rowIndex }) {
      if (rowIndex % 2) {
        return 'warning-row';
      } else {
        return 'success-row';
      }
    },

    // 获取url信息
    getUrlKey(name) {
      var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
      var r = window.location.search.substr(1).match(reg);
      if (r != null) {
        return r[2];
      }
      return null;
    },

    /**
     * 获取运用所编码
     */
    async getUnitCode() {
      let { data } = await axios.get('/apiTrainRepair/common/getUnitCode');
      if (data.code == 1) {
        this.unitCode = data.data;
      } else {
        this.$message.error('获取用户信息失败!');
      }
    },

    //获取用户信息
    async getUser() {
      var url_path = '/apiTrainRepairMidGround/common/getUser';
      let res = await axios.get(url_path);
      if (res.data.code == 1) {
        this.user = res.data.data;

        await this.getWorkTeamsByUnitCode();

        if (this.technique || this.dispatch) {
          this.searchForm.workerTeam = '';
        } else {
          this.searchForm.workerTeam = this.user.departmentOrgan.organCode;
        }
      } else {
        this.$message.error('获取用户信息失败!');
      }
    },

    // 新增获取当前所在班次
    async getDay() {
      let res = await axios.get('/apiTrainRepair/common/getDay');
      this.searchForm.Date =
        res.data.dayPlanId.split('-')[0] +
        '-' +
        res.data.dayPlanId.split('-')[1] +
        '-' +
        res.data.dayPlanId.split('-')[2];
      this.searchForm.oriDate = this.searchForm.Date;
      this.searchForm.trainShift = res.data.dayPlanId.split('-')[3];
      this.searchForm.oriTrainShift = this.searchForm.trainShift;
    },

    // 搜索栏切换日计划
    async dayPlayIdChange() {
      await this.getChecklistQueryCondition();
    },

    // 搜索栏切换日计划
    async repairChange() {
      await this.getChecklistQueryCondition();
    },

    // 获取一级修机检记录单查询条件
    async getChecklistQueryCondition() {
      if (!this.unitCode) return;
      let res = await axios.post('/apiTrainRepair/checklist/getChecklistEquipmentQueryCondition', {
        // 日计划
        dayPlanId: this.dayPlayId,
        // 部门code
        deptCode: this.searchForm.workerTeam,
        // 运用所编码
        unitCode: this.unitCode,
      });

      this.searchForm.trainGroup = '';
      this.trainGroupList = res.data.data.trainsetList;
    },

    // 获取班组
    async getWorkTeamsByUnitCode() {
      if (this.technique || this.dispatch) {
        let res = await axios.get('/apiTrainRepair/common/getWorkTeamsByUnitCode', {
          params: {
            unitCode: this.unitCode,
          },
        });

        if (res.data.code == 1) {
          this.workerTeamList = res.data.rows;
        } else {
          this.$message({
            message: '获取作业班组失败',
            type: 'error',
          });
        }
      } else {
        this.workerTeamList = [];
        this.workerTeamList.push({
          name: this.user.departmentOrgan.organName,
          id: this.user.departmentOrgan.organCode,
        });
        this.user.partTimeWorkTeams.forEach((item) => {
          this.workerTeamList.push({ name: item.teamName, id: item.teamCode });
        });
      }
    },

    async changeWorkerTeam() {
      await this.getChecklistQueryCondition();
      await this.getPersonByDept();
    },

    // 获取作业人员
    async getPersonByDept() {
      this.searchForm.dispatchPerson = '';
      let res = await axios.get('/apiTrainRepair/workProcess/getPersonByDept', {
        params: {
          deptCode: this.searchForm.workerTeam,
        },
      });

      if (res.data.code == 1) {
        let list = res.data.data;
        for (let i = 0; i < list.length; i++) {
          for (let j = i + 1; j < list.length; j++) {
            if (!list[j].workId || list[i].workId == list[j].workId) {
              list.splice(j, 1);
              j--;
            }
          }
        }

        this.dispatchPersonList = list;
      }
    },

    // 搜索栏——重置
    async reset(formName) {
      this.$refs[formName].resetFields();
      await this.getDay();
      if (this.technique || this.dispatch) {
        this.searchForm.workerTeam = '';
      } else {
        this.searchForm.workerTeam = this.user.departmentOrgan.organCode;
      }
      await this.getChecklistQueryCondition();
      await this.getPersonByDept();

      let result = this.dispatchPersonList.filter((item) => {
        return item.workId == this.user.staffId;
      });

      if (this.searchForm.workerTeam && !this.technique && !this.dispatch && result.length > 0) {
        this.searchForm.dispatchPerson = this.user.staffId;
      }
      await this.submitForm();
    },

    // 搜索栏——搜索
    async submitForm() {
      // if (
      //   this.searchForm.oriDate != this.searchForm.Date ||
      //   this.searchForm.oriTrainShift != this.searchForm.trainShift ||
      //   this.searchForm.oriWorkerTeam != this.searchForm.workerTeam ||
      //   this.searchForm.oriDispatchPerson != this.searchForm.dispatchPerson
      // ) {
      //   await this.getChecklistSummaryList();
      // }

      await this.getChecklistSummaryList();
      this.pageNum = 1;
    },

    // 查询一二级修记录单回填列表
    async getChecklistSummaryList() {
      this.searchForm.loading = this.$loading();
      try {
        let paramData = {
          // 日计划
          dayPlanId: this.dayPlayId,
          // 部门CODE
          deptCode: this.searchForm.workerTeam,
          // 派工作业人员ID
          allotWorkCode: this.searchForm.dispatchPerson,
          // 运用所编码
          unitCode: this.unitCode,
          // 类型
          type: 'Equipment',
        };

        let res = await axios.post('/apiTrainRepair/checklist/getChecklistSummaryList', paramData);

        if (res.data.code == 1) {
          this.tableData = res.data.data.getChecklistSummaryList;
          this.oriTableData = JSON.parse(JSON.stringify(this.tableData));

          this.searchForm.oriDate = this.searchForm.Date;
          this.searchForm.oriTrainShift = this.searchForm.trainShift;
          this.searchForm.oriWorkerTeam = this.searchForm.workerTeam;
          this.searchForm.oriDispatchPerson = this.searchForm.dispatchPerson;

          this.oriTableData = JSON.parse(JSON.stringify(this.tableData));
          this.oriTableData = this.oriTableData.filter((item) => {
            if (item.trainsetId.indexOf(this.searchForm.trainGroup) >= 0) {
              return item;
            }
          });

          this.total = this.oriTableData.length;
        } else {
          this.$message.error('获取回填列表失败!');
        }
      } finally {
        this.searchForm.loading.close();
      }
    },

    handleSelectionChange(val) {
      this.multipleSelection = val;
    },

    handleSizeChange(val) {
      this.pageSize = val;
      this.pageNum = 1;
    },
    handleCurrentChange(val) {
      this.pageNum = val;
    },

    async getTemplateValues(billTypeCode) {
      let { data } = await axios.get('/apiTrainRepairMidGround/common/getTemplateValues', {
        params: {
          billTypeCode,
        },
      });
      if (data.code == 1) {
        return data.data;
      } else {
        this.$message.error('获取属性选项数据信息失败!');
      }
    },

    async getConfigAttr(billTypeCode) {
      let { data } = await axios.get('/apiTrainRepairMidGround/common/getConfigAttr', {
        params: {
          billTypeCode,
        },
      });
      if (data.code == 1) {
        return data.data;
      } else {
        this.$message.error('获取属性数据信息失败!');
      }
    },

    async getBillDetails(row) {
      let [billDetail, propertyTree, propertyOptionList, ssjsonDataString] = await Promise.all([
        this.getCheckDetail(row),
        this.getConfigAttr(row.templateType),
        this.getTemplateValues(row.templateType),
        this.getSSJsonFileContentByTempLateId(row.templateId),
      ]);

      this.spreadDialog.billDetail = billDetail;

      return [billDetail, propertyTree, propertyOptionList, ssjsonDataString];
    },

    // 单据回填
    async billsBackfill(row, readOnly) {
      try {
        this.loading = true;
        this.spreadDialog.visible = true;
        this.spreadDialog.billSpreadModuleConfig.readOnly = readOnly;
        this.columnData = row;

        let [billDetail, propertyTree, propertyOptionList, ssjsonDataString] = await this.getBillDetails(row);

        if (!this.spreadDialog.billSpreadModuleObj) {
          this.spreadDialog.billSpreadModuleObj = this.$refs.billSpreadModule;
        }

        // 初始化所有数据
        this.spreadDialog.billSpreadModuleObj.initAllData({
          ssjsonDataString,
          billData: {
            cells: billDetail.cells, // 单元格数据
            areas: billDetail.areas, // 区域数据
            extraObject: billDetail.extraObject, // 额外单据数据
          },
          propertyTree, // 属性数据
          propertyOptionList, // 属性选项数据
          userData: {
            // 用户数据
            id: this.user.staffId,
            name: this.user.name,
            roles: this.user.roleInfoList,
          },
        });
      } finally {
        this.loading = false;
      }
    },

    // 获取一二级修单据详情信息
    async getCheckDetail(params) {
      let { data } = await axios.post('/apiTrainRepair/checkInfo/getCheckDetail', params);

      if (data.code == 1) {
        return data.data;
      } else {
        this.$message.error('获取单据详情信息失败!');
      }
    },

    // 获取Json字符串
    async getSSJsonFileContentByTempLateId(templateId) {
      let { data } = await axios.get('/apiTrainRepairMidGround/common/getSSJsonFileContentByTemplateId', {
        params: {
          templateId,
        },
      });
      if (data.code == 1) {
        return data.data;
      } else {
        this.$message.error('模板样式获取失败!');
      }
    },

    // 保存单据
    async dataSave() {
      this.loading = true;
      try {
        let dataForSave = this.spreadDialog.billSpreadModuleObj.getTemplateData();
        let params = {
          triggerCell: '',
          extraObject: dataForSave.billData.extraObject,
          cells: dataForSave.billData.cells,
          areas: dataForSave.billData.areas,
        };
        this.SaveRepairCell(params);
        await this.getChecklistSummaryList();
      } finally {
        this.loading = false;
      }
    },

    // 保存单据接口
    async SaveRepairCell(saveInfo) {
      let { data } = await axios.post('/apiTrainRepair/checkInfo/SaveRepairCell', saveInfo);

      if (data.code == 1) {
        let changedCells = data.data && data.data.changedCells;
        this.spreadDialog.billSpreadModuleObj.updateBillData({
          changedCells,
        });
        this.$message.success(data.msg);
      } else {
        this.$message.error(data.msg);
      }
    },

    // 导出
    exportExcel(columnData) {
      this.loading = true;
      try {
        this.spreadDialog.billSpreadModuleObj.exportExcelBlob().then((blob) => {
          saveAs(blob, this.fileName(columnData));
        });
      } finally {
        this.loading = false;
      }
    },

    fileName(columnData) {
      let dayPlanId = columnData.dayPlanId.split('-');
      dayPlanId.splice(3, 4, dayPlanId[dayPlanId.length - 1] == '00' ? '白班' : '黑班');

      // 日计划_车组_项目名称_单据类型_编组形式
      return (
        dayPlanId.join('-') +
        '_' +
        columnData.trainsetName +
        '_' +
        columnData.itemName +
        '_' +
        columnData.templateTypeName +
        '_' +
        columnData.marshallingType +
        '.xlsx'
      );
    },

    // 批量导出
    async batchExportExcel() {
      let List = this.multipleSelection.filter((item) => {
        return item.templateExisit != '未配置';
      });
      if (List.length > 0) {
        this.excelExport.progressShow = true;
        let fileNum = 0;
        let formData = new FormData();
        for (let i = 0; i < List.length; i++) {
          let [billDetail, propertyTree, propertyOptionList, ssjsonDataString] = await this.getBillDetails(List[i]);

          this.excelExport.billSpreadModuleObj && this.excelExport.billSpreadModuleObj.destroy();
          this.excelExport.billSpreadModuleObj = new BillSpreadModule();

          // 挂载div元素
          this.excelExport.billSpreadModuleObj.mountSpread(document.createElement('div'));

          // 初始化所有数据
          this.excelExport.billSpreadModuleObj.initAllData({
            ssjsonDataString,
            billData: {
              cells: billDetail.cells, // 单元格数据
              areas: billDetail.areas, // 区域数据
              extraObject: billDetail.extraObject, // 额外单据数据
            },
            propertyTree, // 属性数据
            propertyOptionList, // 属性选项数据
            userData: {
              // 用户数据
              id: this.user.staffId,
              name: this.user.name,
              roles: this.user.roleInfoList,
            },
          });

          let blob = await this.excelExport.billSpreadModuleObj.exportExcelBlob();

          formData.append(this.fileName(this.multipleSelection[i]), blob);

          fileNum++;
          this.excelExport.completedNum = fileNum;
        }

        let data = await this.buildZip(formData);

        saveAs(data, '单据文件包.zip');
        this.excelExport.status = 'success';
        let seconds = 3;
        let getMsg = () => `导出成功，${seconds}秒后关闭标签页`;
        this.excelExport.statusMessage = getMsg();
        this.$refs.multipleTable.clearSelection();
        let key = setInterval(() => {
          seconds--;
          this.excelExport.statusMessage = getMsg();
          if (seconds === 0) {
            clearInterval(key);
            this.excelExport.progressShow = false;
            this.excelExport.completedNum = 0;
            this.excelExport.status = null;
          }
        }, 1000);
      } else {
        this.$message.warning('请选择需要导出的单据!');
      }
    },

    // 批量导出接口
    async buildZip(formData) {
      let { data } = await axios({
        method: 'post',
        url: '/apiTrainRepairMidGround/common/buildZip',
        data: formData,
        responseType: 'blob',
        headers: { 'Content-Type': 'application/json; application/octet-stream' },
      });

      return data;
    },

    async importExcel() {
      await this.ImportData();
    },

    // 导入单据接口
    async ImportData() {
      let { data } = await axios.post('/apiTrainRepair/checkInfo/ImportData', this.spreadDialog.billDetail.extraObject);
      if (data.code == 1) {
        let changedCells = data.data && data.data.changedCells;
        this.spreadDialog.billSpreadModuleObj.updateBillData({
          changedCells,
        });
        await this.getChecklistSummaryList();
        this.$message.success('导入成功!');
      } else {
        this.$message.error('导入失败!');
      }
    },

    async dialogClose() {
      await this.getChecklistSummaryList();
      if ((this.pageNum - 1) * this.pageSize >= this.total) {
        this.pageNum = 1;
      }
      this.spreadDialog.billSpreadModuleObj.clearTemplateData();
      this.spreadDialog.visible = false;
    },
  },
});
