let copyDataSimple = (data) => JSON.parse(JSON.stringify(data));

window.main = new Vue({
  el: '#flowTypeConfig',
  data() {
    return {
      unitCode: '',
      centerFlag: false,
      home: {
        allPackets: [],
        mainTableHeight: 0,
        typeConfigMainTableData: [],
        typeConfigMainTablePageNum: 1,
        typeConfigMainTablePageSize: 20,
        typeConfigMainTablePageTotal: 20,
      },
      workPackageDialog: {
        workPackageDialogVisible: false,
        workPackageDialogFormData: {
          name: '',
          trainType: '',
          batch: '',
        },
        workPackageDialogFormRules: {
          name: [],
          trainType: [],
          batch: [],
        },
        toSelectWorkPackageTableData: [],
        toSelectWorkPackageSelection: [],
        selectedWorkPackageTableData: [],
        selectedWorkPackageSelection: [],
        trainsetList: [],
        configPackageInfo: {},
      },
    };
  },
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('流程类型配置'),
    packetCodeNameMap({ home }) {
      return home.allPackets.reduce((prev, item) => {
        prev[item.packetCode] = item.packetName;
        return prev;
      }, {});
    },
    toSelectWorkPackageTableShowData({ workPackageDialog }) {
      return workPackageDialog.toSelectWorkPackageTableData.filter(
        (toSelectedItem) =>
          toSelectedItem.packetName
            .toLowerCase()
            .includes(workPackageDialog.workPackageDialogFormData.name.toLowerCase()) &&
          (toSelectedItem.suitModel == 'ALL' ||
            !workPackageDialog.workPackageDialogFormData.trainType ||
            (workPackageDialog.workPackageDialogFormData.trainType &&
              toSelectedItem.suitModel === workPackageDialog.workPackageDialogFormData.trainType)) &&
          !this.workPackageDialog.selectedWorkPackageTableData.some(
            (selectedItem) => selectedItem.packetCode == toSelectedItem.packetCode
          )
        // (toSelectedItem.suitBatch == 'ALL' ||
        // toSelectedItem.suitBatch.includes(workPackageDialog.workPackageDialogFormData.batch)) &&
      );
    },
    trainTypeList({ workPackageDialog }) {
      // 去重+组合
      const map = workPackageDialog.trainsetList.reduce((prev, item) => {
        if (!prev.has(item.traintype)) {
          prev.set(item.traintype, {
            id: item.traintype,
            value: item.traintype,
            label: item.traintype,
          });
        }
        return prev;
      }, new Map());
      let arr = [...map.values()];
      return arr;
    },
    batchList({ workPackageDialog }) {
      // 去重+组合
      const map = workPackageDialog.trainsetList.reduce((prev, item) => {
        if (!prev.has(item.traintempid)) {
          prev.set(item.traintempid, {
            id: item.traintempid,
            value: item.traintempid,
            label: item.traintempid,
            trainType: item.traintype,
          });
        }
        return prev;
      }, new Map());
      let arr = [...map.values()];

      if (workPackageDialog.workPackageDialogFormData.trainType) {
        arr = arr.filter((item) => {
          return item.trainType == workPackageDialog.workPackageDialogFormData.trainType;
        });
      }

      return arr;
    },
  },
  async created() {
    await this.getUnitCode();
    await this.isCenter();
    await this.getAllPackets();
    this.getFlowTypeAndPacket();

    this.getTrainsetListReceived();
  },
  mounted() {
    this.mainTableHeightChange();
    window.addEventListener('resize', this.mainTableHeightChange);
    this.$once('hook:beforeDestroy', () => {
      window.removeEventListener('resize', this.mainTableHeightChange);
    });
  },
  methods: {
    // 获取运用所编码
    async getUnitCode() {
      let data = await getUnitCode();
      if (data.code == 1) {
        this.unitCode = data.data;
      } else {
        this.$message.error('获取登录人信息失败！');
      }
    },

    async isCenter() {
      const { data } = await isCenter();
      this.centerFlag = data;
    },
    async getAllPackets() {
      const { code, data } = await getPackets({ flag: true });
      if (code == 1) {
        this.home.allPackets = data;
      }
    },
    // home
    // main
    mainTableHeightChange() {
      this.$nextTick(() => {
        this.home.mainTableHeight = 'calc(100vh - 105px)';
      });
    },
    async getFlowTypeAndPacket() {
      const {
        code,
        data: { records, total },
      } = await getFlowTypeAndPacket({
        pageNum: this.home.typeConfigMainTablePageNum,
        pageSize: this.home.typeConfigMainTablePageSize,
        unitCode: this.centerFlag ? '' : this.unitCode,
        config: true,
      });
      if (code == 1) {
        this.home.typeConfigMainTableData = records;
        this.home.typeConfigMainTablePageTotal = total;
      }
    },
    async getPackets(flowTypeCode, parentFlowTypeCode) {
      const { code, data } = await getPackets({ flowTypeCode, parentFlowTypeCode, unitCode: this.unitCode });
      if (code == 1) {
        this.workPackageDialog.toSelectWorkPackageTableData = data;
      }
    },

    async configPackageBtn(row) {
      await this.getPackets(row.code, row.parentFlowTypeCode);

      this.workPackageDialog.configPackageInfo = copyDataSimple(row);
      this.workPackageDialog.workPackageDialogVisible = true;

      // 回显
      this.workPackageDialog.selectedWorkPackageTableData = this.workPackageDialog.toSelectWorkPackageTableData
        .map((item) => {
          if (this.workPackageDialog.configPackageInfo.packetCodes.includes(item.packetCode)) {
            return item;
          }
        })
        .filter((item) => item);
    },
    // footer
    typeConfigMainTablePageSizeChange(pageSize) {
      this.home.typeConfigMainTablePageSize = pageSize;
      this.getFlowTypeAndPacket();
    },
    typeConfigMainTablePageNumChange(pageNum) {
      this.home.typeConfigMainTablePageNum = pageNum;
      this.getFlowTypeAndPacket();
    },
    // workPackageDialog
    async getTrainsetListReceived() {
      const { code, data } = await getTrainsetListReceived();
      if (code == 1) {
        this.workPackageDialog.trainsetList = data;
      }
    },
    trainTypeChange() {
      this.workPackageDialog.workPackageDialogFormData.batch = '';
    },
    batchChange(batch) {
      if (!batch) return;
      this.workPackageDialog.workPackageDialogFormData.trainType = this.batchList.find(
        (item) => item.value == batch
      ).trainType;
    },
    workPackageDialogClose() {
      this.$refs.workPackageDialogFormRef.resetFields();
      this.workPackageDialog.selectedWorkPackageTableData = [];
      this.workPackageDialog.configPackageInfo = {};
    },
    // 取消
    cancelConfigPackageBtn() {
      this.workPackageDialog.workPackageDialogVisible = false;
    },
    // 选择要增加的
    toSelectWorkPackage(selection) {
      this.workPackageDialog.toSelectWorkPackageSelection = selection;
    },
    // 增加
    addSelectWorkPackageToTable() {
      if (this.workPackageDialog.toSelectWorkPackageSelection.length == 0) {
        return this.$message.warning('请选择要添加的作业包');
      }
      this.workPackageDialog.selectedWorkPackageTableData = [
        ...this.workPackageDialog.selectedWorkPackageTableData,
        ...this.workPackageDialog.toSelectWorkPackageSelection,
      ];
    },
    // 选择要移除的
    selectedWorkPackage(selection) {
      this.workPackageDialog.selectedWorkPackageSelection = selection;
    },
    // 移除
    removeSelectedWorkPackage() {
      if (this.workPackageDialog.selectedWorkPackageSelection.length == 0) {
        return this.$message.warning('请选择要移除的作业包');
      }
      this.workPackageDialog.selectedWorkPackageTableData = this.workPackageDialog.selectedWorkPackageTableData.filter(
        (selectedItem) =>
          !this.workPackageDialog.selectedWorkPackageSelection.some(
            (removeItem) => removeItem.packetCode == selectedItem.packetCode
          )
      );
    },
    // 确定
    async setExtraFlowTypePacket(data) {
      try {
        const { code } = await setExtraFlowTypePacket(data);
        if (code == 1) {
          this.workPackageDialog.workPackageDialogVisible = false;
          this.$message.success('配置成功');
          this.getFlowTypeAndPacket();
        }
      } catch (error) {
        console.error(error);
        this.$message.success('配置失败');
      }
    },
    setExtraFlowTypePacketBtn() {
      this.workPackageDialog.configPackageInfo.packetCodes = this.workPackageDialog.selectedWorkPackageTableData.map(
        (item) => item.packetCode
      );

      this.setExtraFlowTypePacket(this.workPackageDialog.configPackageInfo);
    },
  },
});
