const effectStateList = [
  {
    id: 1,
    label: '生效',
    value: '1',
  },
  {
    id: 0,
    label: '失效',
    value: '0',
  },
];

window.main = new Vue({
  el: '#repairWarningManage',
  data: {
    traintypeList: [],
    trainsetList: [],
    effectStateList: Object.freeze(effectStateList),
    headerFormData: {
      startTime: '',
      endTime: '',
      trainsetType: '',
      trainsetName: '',
      effectState: '',
    },
    headerFormRules: {
      startTime: [],
      endTime: [],
      trainsetType: [],
      trainsetName: [],
      effectState: [],
    },
    mainTableData: [],
    mainTableHeight: 0,
    mainTablePageNum: 1,
    mainTablePageSize: 20,
    mainTablePageTotal: 20,
    confirmWarningDialog: {
      visible: false,
      warningInfo: {},
    },
  },
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('作业预警管理'),
    trainsetTrainTypeMap({ trainsetList }) {
      return trainsetList.reduce((prev, item) => {
        prev[item.trainsetid] = item.traintype;
        return prev;
      }, {});
    },
  },
  created() {
    // HH:mm:ss
    this.headerFormData.startTime = dayjs().subtract(10, 'day').format('YYYY-MM-DD');
    this.headerFormData.endTime = dayjs().format('YYYY-MM-DD');

    this.getTraintypeList();
    this.getTrainsetList();
    this.getWorkWorningList();
  },
  mounted() {
    this.mainTableHeightChange();
    window.addEventListener('resize', this.mainTableHeightChange);
    this.$once('hook:beforeDestroy', () => {
      window.removeEventListener('resize', this.mainTableHeightChange);
    });
  },
  methods: {
    mainTableHeightChange() {
      this.$nextTick(() => {
        this.mainTableHeight = 'calc(100vh - 164px)';
      });
    },
    async getTraintypeList() {
      const {
        data: { data },
      } = await axios({
        url: '/apiTrainResume/resume/getTraintypeList',
      });
      this.traintypeList = data;
    },
    async getTrainsetList() {
      const {
        data: { data },
      } = await axios({
        url: '/apiTrainResume/resume/getTrainsetList',
        method: 'post',
        data: {
          traintype: this.headerFormData.trainsetType,
        },
      });
      this.trainsetList = data;
    },
    async getWorkWorningList() {
      if (new Date(this.headerFormData.startTime) > new Date(this.headerFormData.endTime)) {
        this.$message({
          type: 'warning',
          message: '结束时间不能早于开始时间',
        });
      } else {
        let endTime = new Date(this.headerFormData.endTime);
        endTime = endTime.getTime() + 24 * 60 * 60 * 1000;
        endTime = dayjs(endTime).format('YYYY-MM-DD');
        let { startTime, trainsetType, trainsetName, effectState } = this.headerFormData;
        const { total, records } = await getWorkWorningList({
          startTime,
          endTime,
          trainsetType,
          trainsetName,
          effectState,
          pageNum: this.mainTablePageNum,
          pageSize: this.mainTablePageSize,
        });
        this.mainTableData = records;
        this.mainTablePageTotal = total;
        // this.$message.success('获取预警数据列表成功');
      }
    },
    trainTypeChange() {
      this.headerFormData.trainsetName = '';
    },
    trainsetChange(val) {
      this.headerFormData.trainsetType = this.headerFormData.trainsetType
        ? this.headerFormData.trainsetType
        : this.trainsetTrainTypeMap[val];
    },
    resetHeaderForm() {
      this.$refs.headerFormRef.resetFields();
    },
    mainTablePageSizeChange(pageSize) {
      this.mainTablePageSize = pageSize;
      this.getWorkWorningList();
    },
    mainTablePageNumChange(pageNum) {
      this.mainTablePageNum = pageNum;
      this.getWorkWorningList();
    },
    openWarningDialog(row) {
      this.confirmWarningDialog.warningInfo = row;
      this.confirmWarningDialog.visible = true;
    },
    confirmWarningDialogClose() {
      this.confirmWarningDialog.warningInfo = {};
    },
    async effectWorkWorning() {
      await effectWorkWorning(this.confirmWarningDialog.warningInfo);
      this.$message.success('确认成功');
      this.getWorkWorningList();
      this.confirmWarningDialog.visible = false;
    },
    cancelConfirmWarning() {
      this.confirmWarningDialog.visible = false;
    },
    effectStateFormatter(row) {
      if (row.effectState == 1) {
        return '生效';
      } else if (row.effectState == 0) {
        return '失效';
      } else {
        return '';
      }
    },
    createTimeFormatter(row) {
      return dayjs(row.createTime).format('YYYY-MM-DD');
    },
    effectTimeFormatter(row) {
      return dayjs(row.effectTime).format('YYYY-MM-DD');
    },
  },
  filters: {
    timeFormat(val) {
      return dayjs(val).format('YYYY-MM-DD');
    },
    effectStateFormat(val) {
      if (val == 1) {
        return '生效';
      } else if (val == 0) {
        return '失效';
      } else {
        return '';
      }
    },
  },
});
