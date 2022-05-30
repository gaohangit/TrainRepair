let copyDataSimple = (data) => JSON.parse(JSON.stringify(data));
window.main = new Vue({
  el: '#workRepairFlowRecord',
  data() {
    return {
      unitCode: '',
      sheetLabels: [
        '日期',
        '车组',
        '车组位置',
        '流程名称',
        '作业节点名称',
        '作业人员',
        '记录时间',
        '作业身份',
        '作业班组',
      ],
      sheetProps: [
        'startTime',
        'trainsetName',
        'trainsetPosition',
        'flowName',
        'nodeName',
        'workerName',
        'recordTime',
        'roleName',
        'teamName',
      ],
      stateList: Object.freeze([
        { id: 1, label: '正在进行', value: 0 },
        { id: 2, label: '已结束', value: 1 },
        { id: 3, label: '已驳回', value: 2 },
        { id: 4, label: '已撤销', value: 3 }
      ]),
      pickerOptions: {
        disabledDate(time) {
          let timeOptionRange = main.timeOptionRange;
          let secondNum = 60 * 60 * 24 * 30 * 1000;
          if (timeOptionRange) {
            return (
              time.getTime() > timeOptionRange.getTime() + secondNum ||
              time.getTime() < timeOptionRange.getTime() - secondNum
            );
          }
        },
        onPick({ minDate, maxDate }) {
          if (minDate && !maxDate) {
            main.timeOptionRange = minDate;
          }
          if (maxDate) {
            main.timeOptionRange = '';
          }
        },
      },
      timeOptionRange: '',
      trainsetAllList: [],
      trainsetList: [],
      flowTypeList: [],
      flowList: [],
      workTeamList: [],
      workList: [],
      queryModel: {
        time: [],
        trainsetId: '',
        flowTypeCode: '',
        flowId: '',
        teamCode: '',
        workId: '',
        flowRunState: '',
        queryPastFlow: true,
        dayPlanCode: '',
        nodeAllRecord: false
      },
      queryRules: {
        time: [
          {
            required: true,
            message: '请选择时间范围',
            trigger: 'change',
          },
        ],
        trainsetId: [],
        flowTypeCode: [],
        flowId: [],
        teamCode: [],
        workId: [],
        flowRunState: [],
        queryPastFlow: '',
        dayPlanCode: '',
        nodeAllRecord: ''
      },
      dayPlanList: [
        {
          name: '白班',
          code: '00',
        },
        {
          name: '夜班',
          code: '01',
        },
      ],
      recordList: [],
      nodeDealDetailDialog: {
        visible: false,
        detailInfo: {
          title: '',
          pics: [],
          prePics: [],
        },
      },
      imgUrl: '',
      tableData: [],
      sortSign: [],
      SecRowsSign: [],
      toolltip: '',
      loading: false
    };
  },
  computed: {
    trainsetIdNameMap({ trainsetAllList }) {
      return trainsetAllList.reduce((prev, item) => {
        prev[item.trainsetid] = item.trainsetname;
        return prev;
      }, {});
    },
    // fstRowSpan({ flowInfoFstRows}) {
    //   let newArr = [];
    //   flowInfoFstRows &&
    //     flowInfoFstRows.forEach((item, index) => {
    //       newArr.push(item);
    //       for (let i = 1; i < item; i++) {
    //         newArr.push(0);
    //       }
    //     });
    //   return newArr;
    // },
    fstRowSpan() {
      let newArr = [];
      this.sortSign.forEach((item, index) => {
        newArr.push(item);
        for (let i = 1; i < item; i++) {
          newArr.push(0);
        }
      });
      return newArr;
    },
    // flowInfoSecRows({ recordList }) {
    //   let arr = [];
    //   recordList.forEach((flow) => {
    //     flow.nodeList.forEach((node) => {
    //       arr.push(node.nodeRecords.length || 1);
    //     });
    //   });
    //   return arr;
    // },
    secRowSpan() {
      let newArr = [];
      this.SecRowsSign.forEach((item, index) => {
        newArr.push(item);
        for (let i = 1; i < item; i++) {
          newArr.push(0);
        }
      });
      return newArr;
    },
    pageTitle: (() => {
      let defaultPageTitleMap = {
        ONE: '一级修流程记录',
        TWO: '二级修流程记录',
        TEMPORARY: '临修作业流程记录',
        HOSTLING: '整备作业流程记录',
        ALL: '流程记录统计分析',
      };
      let defaultPageTitle = defaultPageTitleMap[flowPageCode] || '作业流程记录';
      return GeneralUtil.getObservablePageTitleGetter(defaultPageTitle);
    })(),
  },
  async created() {
    this.loading = true
    await this.getUnitCode();
    this.queryModel.time = [dayjs().subtract(30, 'day').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')];
    await this.getFlowTypeListByFlowPageCode();
    await this.getTrainsetList();
    await this.getTrainsetByDateAndFlowTypeCode();
    await this.getPersonByDept();
    await this.getFlowList();
    await this.getFlowRunRecordList();
    this.loading = false
  },
  mounted() {},
  methods: {
    // 获取当前登录人运用所编码
    async getUnitCode() {
      let data = await getUnitCode();
      this.unitCode = data;
    },

    trainPosition(row) {
      if (row.headTrackPositionCode == row.tailTrackPositionCode) {
        return row.trackName + (row.headTrackPositionName ? '-' : '') + row.headTrackPositionName;
      } else {
        return row.trackName;
      }
    },

    async getTrainsetList() {
      const data = await getTrainsetList();
      this.trainsetAllList = data;
    },

    async getTrainsetByDateAndFlowTypeCode() {
      this.queryModel.trainsetId = '';
      const startDate = this.queryModel.time[0];
      const endDate = dayjs(this.queryModel.time[1]).add(1, 'day').format('YYYY-MM-DD');
      const data = await getTrainsetByDateAndFlowTypeCode({
        unitCode: this.unitCode,
        flowTypeCode: this.queryModel.flowTypeCode,
        startDate,
        endDate,
        flowPageCode: flowPageCode == 'ALL' ? '' : flowPageCode,
      });
      this.trainsetList = data;
    },

    async getFlowTypeListByFlowPageCode() {
      this.flowTypeList = await getFlowTypeListByFlowPageCode({
        unitCode: this.unitCode,
        flowPageCode: flowPageCode == 'ALL' ? '' : flowPageCode,
      });
      this.queryModel.flowTypeCode = this.flowTypeList[0].code;
    },

    flowTypeCodeChange() {
      this.queryModel.flowId = '';
      this.getTrainsetByDateAndFlowTypeCode();
      this.getFlowList();
    },

    async getFlowList() {
      this.flowList = await getFlowTypes({
        unitCode: this.unitCode,
        queryPastFlow: this.queryModel.queryPastFlow,
        flowTypeCode: this.queryModel.flowTypeCode,
        flowPageCode: flowPageCode == 'ALL' ? '' : flowPageCode,
      });
      this.queryModel.flowId = '';
    },
    async getWorkTeamsByUnitCode() {
      try {
        const {
          data: { data },
        } = await getWorkTeamsByUnitCode(this.unitCode);
        this.workTeamList = data;
      } catch (error) {
        this.$message.error('作业班组数据异常');
      }
    },
    branchCodeChange() {
      this.queryModel.workId = '';
    },
    async getPersonByDept() {
      this.workList = [];
      try {
        const {
          data: { data },
        } = await getPersonByDept(this.queryModel.teamCode);
        this.workList = data;
      } catch (error) {
        this.$message.error('作业人员数据异常');
      }
    },
    async getFlowRunRecordList() {
      try {
        await this.$refs.queryForm.validate();
        const startDate = this.queryModel.time[0];
        const endDate = dayjs(this.queryModel.time[1]).add(1, 'day').format('YYYY-MM-DD');
        const {
          trainsetId,
          flowTypeCode,
          flowId,
          teamCode,
          workId,
          flowRunState,
          queryPastFlow,
          dayPlanCode,
          nodeAllRecord
        } = this.queryModel;
        let { flowRunInfoWithKeyWorkBases, statisticsResult, standardGroupResult } = await getFlowRunRecordList({
          queryPastFlow,
          trainsetId,
          flowTypeCode,
          flowPageCode: flowPageCode == 'ALL' ? '' : flowPageCode,
          flowName: flowId ? this.getFlowName(flowId) : '',
          teamCode,
          workId,
          flowRunState,
          unitCode: this.unitCode,
          startDate,
          endDate,
          dayPlanCode,
          nodeAllRecord

        });

        // 防止空记录错位
        flowRunInfoWithKeyWorkBases.forEach((item) => {
          if (item.nodeList.length == 0) {
            item.nodeList.push({ nodeRecords: [], name: '' });
          }
        });

        this.recordList = flowRunInfoWithKeyWorkBases;
        this.toolltip = flowPageCode == 'ALL' ? standardGroupResult : statisticsResult;
        this.flowData();
        this.flowInfoFstRows();
        this.flowInfoSecRows();
      } catch (error) {
        console.log(error);
      }
    },
    resetQueryModel() {
      this.$refs.queryForm.resetFields();
      this.queryModel.time = [dayjs().subtract(30, 'day').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')];
      this.queryModel.flowTypeCode = this.flowTypeList[0].code;
      this.getFlowRunRecordList();
    },
    spanMethod({ row, column, rowIndex, columnIndex }) {
      let mergeColumnn = this.queryModel.flowTypeCode == 'PLANLESS_KEY' ? 5 : 4;
      if (columnIndex < mergeColumnn) {
        const rowspan = this.fstRowSpan[rowIndex];
        const colspan = rowspan > 0 ? 1 : 0;
        return {
          rowspan,
          colspan,
        };
      } else if (columnIndex === mergeColumnn) {
        const rowspan = this.secRowSpan[rowIndex];
        const colspan = rowspan > 0 ? 1 : 0;
        return {
          rowspan,
          colspan,
        };
      }
    },
    exportExcel() {
      let mergeColumnn = this.queryModel.flowTypeCode == 'PLANLESS_KEY' ? 5 : 4;
      let spread = new GC.Spread.Sheets.Workbook(document.createElement('div'));
      let sheet = spread.getActiveSheet();

      let style = new GC.Spread.Sheets.Style();
      style.hAlign = GC.Spread.Sheets.HorizontalAlign.center;
      style.vAlign = GC.Spread.Sheets.VerticalAlign.center;

      let fontSize = new GC.Spread.Sheets.Style();
      fontSize.font = '18pt';

      if (this.queryModel.flowTypeCode == 'PLANLESS_KEY') {
        this.sheetProps[0] = 'startTime';
        let carNoList = this.sheetLabels.some((item) => {
          return item == '辆序';
        });
        if (!carNoList) {
          this.sheetLabels.splice(4, 0, '辆序');
          this.sheetProps.splice(4, 0, 'carNoList');
        }
        let contain = this.sheetLabels.some((item) => {
          return item == '作业内容';
        });

        if (!contain) {
          this.sheetLabels.splice(5, 0, '作业内容');
          this.sheetProps.splice(5, 0, 'content');
        }

      } else {
        this.sheetProps[0] = 'dayPlanId';
      }
      // 设置sheet的条数
      let rowCount = this.tableData.length + 1
      sheet.setRowCount(rowCount)

      this.tableData.forEach((flow, index) => {
        for (let i = 0; i < this.sheetLabels.length; i++) {
          sheet.setValue(index + 1, i, flow[this.sheetProps[i]]);
          if (i < mergeColumnn && this.fstRowSpan[index] > 0) {
            sheet.addSpan(index + 1, i, this.fstRowSpan[index], 1);
          }
          if (i === mergeColumnn && this.secRowSpan[index] > 0) {
            sheet.addSpan(index + 1, i, this.secRowSpan[index], 1);
          }
        }
        sheet.autoFitRow(index);
      });

      this.sheetLabels.forEach((label, index) => {
        sheet.setValue(0, index, label);
        sheet.setStyle(0, index, fontSize);
        sheet.setStyle(-1, index, style, GC.Spread.Sheets.SheetArea.viewport);
        sheet.setColumnWidth(index, 150);
      });

      let ex = new GC.Spread.Excel.IO();
      GC.Spread.Sheets.LicenseKey = '123';
      let json = spread.toJSON();
      ex.save(
        json,
        function (blob) {
          saveAs(blob, '作业流程处理记录.xlsx');
        },
        function (e) {
          console.log(e);
        }
      );
    },
    lookDetail(data) {
      this.nodeDealDetailDialog.visible = true;
      this.nodeDealDetailDialog.detailInfo.title = `${data.workerName} 上传图片：`;
      this.nodeDealDetailDialog.detailInfo.prePics = data.pictureUrls.map((item) => {
        return `/storageTrainRepair/${item.relativeUrl}`;
      });
      this.nodeDealDetailDialog.detailInfo.pics = data.pictureUrls.map((item) => {
        let url = item.relativeUrl.split('/');
        return {
          name: url[url.length - 1],
          url: `/storageTrainRepair/${item.relativeUrl}`,
        };
      });
    },
    previewPicture(file) {
      this.imgUrl = file.url;
      this.$refs.myImg.showViewer = true;
    },

    flowData() {
      this.tableData = this.recordList
        .map((flow) => {
          return flow.nodeList.map((node) => {
            let trainsetPosition;
            if (flow.headTrackPositionCode == flow.tailTrackPositionCode) {
              trainsetPosition = flow.trackName + '-' + flow.headTrackPositionName;
            } else {
              trainsetPosition = flow.trackName;
            }
            let dayPlanId = flow.dayPlanId.split('-');
            let shift = dayPlanId[dayPlanId.length - 1];
            dayPlanId.splice(dayPlanId.length - 1, dayPlanId.length, shift == '00' ? '白班' : '夜班');
            dayPlanId = dayPlanId.join('-');
            if (node.nodeRecords.length == 0) {
              return {
                dayPlanId,
                startTime: flow.startTime,
                trainsetId: flow.trainsetId,
                trainsetName: this.trainsetIdNameMap[flow.trainsetId],
                trackName: flow.trackName,
                headTrackPositionCode: flow.headTrackPositionCode,
                headTrackPositionName: flow.headTrackPositionName,
                tailTrackPositionCode: flow.tailTrackPositionCode,
                tailTrackPositionName: flow.tailTrackPositionName,
                flowName: flow.flowConfig.name,
                nodeName: node.name,
                workerName: '',
                recordTime: '',
                roleName: '',
                teamName: '',
                pictureUrls: [],
                content: flow.content,
                trainsetPosition,
                carNoList: flow.carNoList.toString() ? flow.carNoList.toString() : "全列"
              };
            } else {
              return node.nodeRecords.map((record) => {
                return {
                  dayPlanId,
                  startTime: flow.startTime,
                  trainsetId: flow.trainsetId,
                  trainsetName: this.trainsetIdNameMap[flow.trainsetId],
                  trackName: flow.trackName,
                  headTrackPositionCode: flow.headTrackPositionCode,
                  headTrackPositionName: flow.headTrackPositionName,
                  tailTrackPositionCode: flow.tailTrackPositionCode,
                  tailTrackPositionName: flow.tailTrackPositionName,
                  flowName: flow.flowConfig.name,
                  nodeName: node.name,
                  workerName: record.workerName,
                  recordTime: record.recordTime,
                  roleName: record.roleName,
                  teamName: record.teamName,
                  pictureUrls: record.pictureUrls,
                  content: flow.content,
                  trainsetPosition,
                  carNoList: flow.carNoList.toString() ? flow.carNoList.toString() : "全列"
                };
              });
            }
          });
        })
        .flat(2);
    },

    flowInfoFstRows() {
      this.sortSign = this.recordList.map((flow) => {
        let recordNum = 0;
        if (flow.nodeList.length === 0) {
          recordNum = 1;
        } else {
          flow.nodeList.forEach((node) => {
            if (node.nodeRecords.length === 0) {
              recordNum += 1;
            } else {
              recordNum += node.nodeRecords.length;
            }
          });
        }
        return recordNum;
      });
    },

    flowInfoSecRows() {
      this.SecRowsSign = [];
      this.recordList.forEach((flow) => {
        flow.nodeList.forEach((node) => {
          this.SecRowsSign.push(node.nodeRecords.length || 1);
        });
      });
    },

    changeTableSort({ column, prop, order }) {
      let len = this.recordList.length;
      let str;
      if (prop === 'startTime' || prop === 'dayPlanId') {
        str = 'startTime';
      } else if (prop === 'trainsetName') {
        console.log(1);
        str = 'trainsetId';
      }
      if (order === 'ascending') {
        // 上升
        for (let i = 0; i < len; i++) {
          for (let j = 0; j < len - 1 - i; j++) {
            if (this.recordList[j][str] > this.recordList[j + 1][str]) {
              let temp = this.recordList[j + 1];
              this.recordList[j + 1] = this.recordList[j];
              this.recordList[j] = temp;
            }
          }
        }
      } else if (order === 'descending') {
        // 下降
        for (let i = 0; i < len; i++) {
          for (let j = 0; j < len - 1 - i; j++) {
            if (this.recordList[j][str] < this.recordList[j + 1][str]) {
              let temp = this.recordList[j];
              this.recordList[j] = this.recordList[j + 1];
              this.recordList[j + 1] = temp;
            }
          }
        }
      }

      this.flowData();
      this.flowInfoFstRows();
      this.flowInfoSecRows();
    },
    getFlowName(flowId) {
      let flowName = ''
      if(flowId) {
        this.flowList.forEach(flow => {
          if(flowId == flow.id) {
            flowName = flow.name
          }
        });
      }
      return flowName
    },
    // 关键作业记录查询时流程运行状态显示已驳回和已撤销
    flowRunStateShow(value) {
      if(this.queryModel.flowTypeCode == 'PLANLESS_KEY') return true
      if(value < 2) return true
    }
  },

  watch: {
    'queryModel.queryPastFlow': {
      async handler() {
        await this.getFlowList();
      },
    },
  },

  filters: {},
});
