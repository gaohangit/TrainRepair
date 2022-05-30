var app = new Vue({
  el: '#app',
  filters: {},

  data() {
    return {
      // 运用所
      unitCode: '',
      // 用户信息
      user: undefined,
      // 搜索部分
      searchForm: {
        // 车组号
        trainNumber: '',
        // 交接股道
        handoverTrack: '',
        // 交接时间
        handoverTime: [],
        loading: undefined,
      },
      // 车组列表
      trainSetList: [],
      // 交接股道列表
      handoverTrackList: [],
      // 出所联检单列表数据
      tableData: [],
      // 选中的列表项
      multipleSelection: [],
      // 当前每页显示数据数量
      pageSize: 10,
      // 当前页
      currentPage: 1,
      // 数据列表条数
      total: 0,

      // 回填部分
      spreadDialog: {
        visible: false,
        btnShow: false,
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
        loading: undefined,
      },

      excelExport: {
        billSpreadModuleObj: undefined,

        completedNum: 0,
        status: null,
        statusMessage: null,
        progressShow: false,
      },
    };
  },

  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('出所联检单管理'),

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
    this.searchForm.handoverTime = [
      dayjs().subtract(1, 'day').format('YYYY-MM-DD') + ' 00:00:00',
      dayjs().format('YYYY-MM-DD') + ' 23:59:59',
    ];
    await this.getUnitCode();
    await this.getUser();
    await this.getTrainsetListReceived();
    await this.getAllTrackArea();
    await this.getLiveCheckSummaryList();
  },

  methods: {
    // 表格斑马纹颜色控制
    tableRowClassName({ row, rowIndex }) {
      if (rowIndex % 2) {
        return 'warning-row';
      } else {
        return 'success-row';
      }
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
      } else {
        this.$message.error('获取用户信息失败!');
      }
    },

    // 获取所有车组号
    async getTrainsetListReceived() {
      let { data } = await axios.get('/apiTrainRepair/common/getTrainsetListReceived');
      if (data.code == 1) {
        this.trainSetList = data.data;
      } else {
        this.$message.error('获取车组信息失败!');
      }
    },

    // 获取所有股道
    async getAllTrackArea() {
      let { data } = await axios.get('/apiTrainRepair/common/getTrackAreaByDept', {
        params: {
          unitCode: this.unitCode,
        },
      });
      if (data.code == 1) {
        let traskAllList = [];
        data.data.forEach((trackArea) => {
          let traskList = trackArea.lstTrackInfo.map((trask) => {
            return trask;
          });

          traskAllList.push(...traskList);
        });
        // 获取到的数据排序(要和后端排序一样)
        this.handoverTrackList = traskAllList.sort((a, b) => {
          let namea = a.trackName.toUpperCase()
          let nameb = b.trackName.toUpperCase()
          if(namea<nameb) {
            return -1
          }
          if(namea>nameb) {
            return 1
          }
          return 0
        });
      } else {
        this.$message.error('获取股道信息失败!');
      }
    },

    // 搜索栏——重置
    reset(formName) {
      this.$refs[formName].resetFields();
      this.searchForm.handoverTime = [
        dayjs().subtract(1, 'day').format('YYYY-MM-DD') + ' 00:00:00',
        dayjs().format('YYYY-MM-DD') + ' 23:59:59',
      ];
      this.submitForm()
    },

    // 搜索栏——搜索
    async submitForm() {
      this.currentPage = 1
      await this.getLiveCheckSummaryList();
    },

    // 获取出所联检单列表
    async getLiveCheckSummaryList() {
      this.searchForm.loading = this.$loading();
      try {
        let { data } = await axios.post('/apiTrainRepair/liveChecklist/getLiveCheckSummaryList', {
          // 车组号ID
          trainsetId: this.searchForm.trainNumber,
          // 交接股道
          track: this.searchForm.handoverTrack,
          // 下发开始时间
          createBeginTime: this.searchForm.handoverTime[0],
          // 下发结束时间
          createEndTime: this.searchForm.handoverTime[1],
          // 运用所编码
          unitCode: this.unitCode,
          // 数据条数
          pageSize: this.pageSize,
          // 页码
          pageNum: this.currentPage,
        });

        if (data.code == 1) {
          this.tableData = data.data.records;
          this.total = data.data.total;
          // this.$message.success('获取列表成功!');
        } else {
          this.$message.error('获取列表失败!');
        }
      } finally {
        this.searchForm.loading.close();
      }
    },

    handleSelectionChange(val) {
      this.multipleSelection = val;
    },

    async handleSizeChange(val) {
      this.pageSize = val;
      await this.getLiveCheckSummaryList();
    },
    async handleCurrentChange(val) {
      this.currentPage = val;
      await this.getLiveCheckSummaryList();
    },

    // 单据回填
    async billsBackfill(state, readOnly, row) {
      try {
        this.spreadDialog.btnShow = state == 'add' ? true : false;
        this.spreadDialog.billSpreadModuleConfig.readOnly = readOnly;
        this.spreadDialog.loading = true;
        this.spreadDialog.visible = true;
        this.columnData = row;

        let billDetail = await this.getNoPlanTaskSummaryInfo(row ? row.checklistSummaryId : '');
        this.spreadDialog.billDetail = billDetail;

        let ssjsonDataString = await this.getSSJsonFileContentByTempLateId(
          this.spreadDialog.billDetail.extraObject.templateId
        );
        let propertyTree = await this.getConfigAttr(this.spreadDialog.billDetail.extraObject.templateType);
        let propertyOptionList = await this.getTemplateValues(this.spreadDialog.billDetail.extraObject.templateType);

        if (!this.spreadDialog.billSpreadModuleObj) {
          this.spreadDialog.billSpreadModuleObj = this.$refs.billSpreadModule;
        }
        // 初始化所有数据
        this.spreadDialog.billSpreadModuleObj.initAllData({
          ssjsonDataString,
          billData: {
            cells: this.spreadDialog.billDetail.cells, // 单元格数据
            areas: this.spreadDialog.billDetail.areas, // 区域数据
            extraObject: this.spreadDialog.billDetail.extraObject, // 额外单据数据
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
        this.spreadDialog.loading = false;
      }
    },

    // 获取出所联检单据详情信息
    async getNoPlanTaskSummaryInfo(summaryId) {
      let { data } = await axios.get('/apiTrainRepair/noPlanTaskChecklist/getNoPlanTaskSummaryInfo', {
        params: {
          // 记录单ID
          summaryId,
          // 单据类型
          templateTypeCode: 'MANAGER_LIVECHECK_CELL',
        },
      });

      return data.data;
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
      this.spreadDialog.loading = true;
      try {
        let dataForSave = this.spreadDialog.billSpreadModuleObj.getTemplateData();
        let params = {
          triggerCell: '',
          extraObject: dataForSave.billData.extraObject,
          cells: dataForSave.billData.cells,
          areas: dataForSave.billData.areas,
        };
        this.SaveRepairCell(params);
      } finally {
        this.spreadDialog.loading = false;
      }
    },

    // 保存单据接口
    async SaveRepairCell(saveInfo) {
      let { data } = await axios.post('/apiTrainRepair/noPlanTaskChecklist/saveNoPlanTaskRepairCell', saveInfo);

      if (data.code == 1) {
        this.columnData = data.data.extraObject
        let changedCells = data.data && data.data.changedCells;
        this.spreadDialog.billSpreadModuleObj.updateBillData({
          changedCells,
        });
        this.$message.success(data.msg);
      } else {
        this.$message.error(data.msg);
      }
    },

    async dialogClose() {
      this.spreadDialog.visible = false;
      this.spreadDialog.billSpreadModuleObj.clearTemplateData()
      await this.getLiveCheckSummaryList();
    },

    fileName(columnData) {
      // 车组_单据类型_编组形式
      return columnData.trainsetName + '_' + columnData.track + '_' + moment(new Date(columnData.connectTime)).format('YYYY-MM-DD') + '.xlsx';
    },

    // 批量导出
    async batchExportExcel() {
      if (this.multipleSelection.length > 0) {
        this.excelExport.progressShow = true;
        let fileNum = 0;
        let formData = new FormData();
        for (let i = 0; i < this.multipleSelection.length; i++) {
          let billDetail = await this.getNoPlanTaskSummaryInfo(this.multipleSelection[i].checklistSummaryId);
  
          let ssjsonDataString = await this.getSSJsonFileContentByTempLateId(
            billDetail.extraObject.templateId
          );
          let propertyTree = await this.getConfigAttr(billDetail.extraObject.templateType);
          let propertyOptionList = await this.getTemplateValues(billDetail.extraObject.templateType);

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
          
          fileNum++
          this.excelExport.completedNum = fileNum;
        }

        let data = await this.buildZip(formData)

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
        headers: {'Content-Type': 'application/json; application/octet-stream'}
      })
      
      return data
    },

    // 导出
    exportExcel(columnData) {
      if(columnData) {
        this.loading = true;
        try {
          this.spreadDialog.billSpreadModuleObj.exportExcelBlob().then((blob) => {
            saveAs(blob, this.fileName(columnData));
          });
        } finally {
          this.loading = false;
        }
      } else {
        this.$message.warning('导出前请先保存单据!');
      }
    },
  },
});
