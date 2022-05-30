window.app = new Vue({
  el: '#app',
  data() {
    // let timeGap = 30 * 24 * 60 * 60 * 1000;
    let getTimeNum = (time) => {
      return new Date(time).getTime();
    };
    let getNowTimeGap = (start, end) => {
      return getTimeNum(start) - getTimeNum(end);
    };
    let validateStartDate = (rule, value, callback) => {
      if (value && this.queryData.endTime) {
        let nowTimeGap = getNowTimeGap(value, this.queryData.endTime);
        if (nowTimeGap > 0) {
          callback(new Error('开始日期不能大于结束日期'));
        } else {
          callback();
        }
        // else if (Math.abs(nowTimeGap) > timeGap) {
        //   callback(new Error('开始和结束日期不能间隔超一个月'));
        // }
      } else {
        callback();
      }
    };
    let validateEndDate = (rule, value, callback) => {
      if (this.queryData.startTime && value) {
        let nowTimeGap = getNowTimeGap(this.queryData.startTime, value);
        if (nowTimeGap > 0) {
          callback(new Error('结束日期不能小于开始日期'));
        } else {
          callback();
        }
        // else if (Math.abs(nowTimeGap) > timeGap) {
        //   callback(new Error('开始和结束日期不能间隔超一个月'));
        // }
      } else {
        callback();
      }
    };
    return {
      isCenter: true,
      unitList: [],
      allTrainsetList: [],
      workTeamList: [],
      queryData: {
        unitCode: '',
        startTime: '',
        endTime: '',
        trainsetType: '',
        trainsetNames: [],
        deptNames: [],
        itemName: '',
      },
      queryRules: {
        unitCode: [],
        startTime: [
          {
            required: true,
            message: '开始日期不能为空',
            trigger: 'blur',
          },
          {
            validator: validateStartDate,
            trigger: 'blur',
          },
        ],
        endTime: [
          {
            required: true,
            message: '结束日期不能为空',
            trigger: 'blur',
          },
          {
            validator: validateEndDate,
            trigger: 'blur',
          },
        ],
        trainsetType: [],
        trainsetNames: [],
        deptNames: [],
        itemName: [],
      },
      activeTab: 'time',
      workTimeTable: {
        height: 0,
        pageNum: 1,
        pageSize: 20,
        total: 20,
      },
      branchNumMap: Object.freeze({
        '00': '白班',
        '01': '夜班',
      }),
      powerStateMap: Object.freeze({
        1: '有电作业',
        2: '无电作业',
      }),
      workShowInfo: {
        startTime: '',
        endTime: '',
        trainsetCount: 0,
        column: 0,
        yesDuration: 0,
        averageYesDuration: 0,
        noDuration: 0,
        averageNoDuration: 0,
        itemDurations: [],
        noTrainsetDuration: {},
        yesTrainsetDuration: {},
        dayPlanDurationsChartInfo: {},
        durations: [],
      },
      rowData: {
        dayPlanId: '',
        trainsetName: '',
        powerState: '',
      },
      averageEchart: null,
      dayEchart: null,
      detailDialog: {
        visible: false,
        detailTable: {
          pageNum: 1,
          pageSize: 20,
          total: 20,
        },
        detailList: [],
      },
      teamEchartInfo: { xAxisData: [], valueData: [] },
      teamEchart: null,
      personEchartInfo: {},
      personEchart: null,
      warningTable: {
        height: 0,
        pageNum: 1,
        pageSize: 20,
        total: 20,
      },
      warningList: [],
    };
  },
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('派工配置'),
    groupWorkTimeMap({ workShowInfo, workTimeTable }) {
      let obj = {};
      let pageNum = 1;
      for (let i = 0; i < workShowInfo.durations.length; i += workTimeTable.pageSize) {
        obj[pageNum] = workShowInfo.durations.slice(i, i + workTimeTable.pageSize);
        pageNum++;
      }
      return obj;
    },
    trainTypeList({ allTrainsetList }) {
      // 去重+组合
      const map = allTrainsetList.reduce((prev, item) => {
        if (!prev.has(item.traintype)) {
          prev.set(item.traintype, {
            traintype: item.traintype,
          });
        }
        return prev;
      }, new Map());
      let arr = [...map.values()];

      return arr;
    },
    trainsetList({ allTrainsetList, queryData }) {
      let arr = [...allTrainsetList];
      // 过滤条件
      if (queryData.trainsetType) {
        arr = arr.filter((item) => item.traintype == queryData.trainsetType);
      }

      return arr;
    },
  },
  watch: {},
  created() {
    this.queryData.startTime = dayjs(dayjs().format('YYYY-MM-01')).valueOf();
    this.queryData.endTime = dayjs(dayjs().format('YYYY-MM-DD')).valueOf();
    this.getIsCenter();
    this.getTrainsetList();
  },
  async mounted() {
    await this.getDuration();
    this.initAverageDurationChart();
    this.initDayDurationChart();
    echarts.connect([this.averageEchart, this.dayEchart]);
    this.initTeamChart();
    this.initPersonChart();
    this.resize();
    window.addEventListener('resize', this.resize);
    this.$once('hook:beforeDestroy', () => {
      window.removeEventListener('resize', this.resize);
    });
  },
  methods: {
    async getIsCenter() {
      this.isCenter = await getIsCenter();
      await this.getUnitList();
    },
    async getUnitList() {
      this.unitList = await getUnitList();
      this.queryData.unitCode = this.unitList[0].unitCode;
    },
    unitCodeChange() {
      this.queryData.deptNames = [];
    },
    timeChange() {
      this.queryData.itemName = '';
    },
    async getTrainsetList() {
      const data = await getTrainsetList();
      this.allTrainsetList = data;
    },
    trainsetTypeChange() {
      this.queryData.trainsetNames = [];
      this.queryData.itemName = '';
    },
    async getWorkTeamsByUnitCode(unitCode) {
      try {
        const {
          data: { rows },
        } = await getWorkTeamsByUnitCode(unitCode);
        this.workTeamList = rows;
      } catch (error) {
        this.$message.error('作业班组数据异常');
      }
    },
    async getDuration() {
      try {
        await this.$refs.queryForm.validate();
        const {
          data: { code, data },
        } = await getDuration({
          ...this.queryData,
          trainsetNames: this.queryData.trainsetNames.toString(),
          deptNames: this.queryData.deptNames.toString(),
        });
        if (code == 1) {
          const xAxisData = data.dayPlanDurations.map((item) => {
            const branchNum = this.branchNumMap[item.dayPlanId.slice(-2)].slice(0, 1);
            return item.dayPlanId.slice(5, -3) + '(' + branchNum + ')';
          });
          const hasData = data.dayPlanDurations.map((item) => {
            return item.yesDuration;
          });
          const noData = data.dayPlanDurations.map((item) => {
            return item.noDuration;
          });
          this.workShowInfo = {
            startTime: dayjs(this.queryData.startTime).format('YYYY-MM-DD'),
            endTime: dayjs(this.queryData.endTime).format('YYYY-MM-DD'),
            trainsetCount: data.trainsetCount,
            column: data.column,
            yesDuration: data.yesDuration,
            averageYesDuration: data.averageYesDuration,
            noDuration: data.noDuration,
            averageNoDuration: data.averageNoDuration,
            itemDurations: data.itemDurations || [],
            noTrainsetDuration: data.noTrainsetDuration || {},
            yesTrainsetDuration: data.yesTrainsetDuration || {},
            dayPlanDurationsChartInfo: {
              xAxisData,
              hasData,
              noData,
            },
            durations: data.durations,
            averageTotalDuration: data.averageTotalDuration,
          };
          this.workTimeTable.total = data.durations.length;

          // if(this.workShowInfo.averageYesDuration===0&&this.workShowInfo.averageNoDuration===0){

          // }else{

          // }

          this.initAverageDurationChart();

          this.initDayDurationChart();
        }
      } catch (error) {}
    },
    async queryBtnClick() {
      if (this.activeTab == 'time') {
        await this.getDuration();
        this.initAverageDurationChart();
        this.initDayDurationChart();
      } else {
        await this.getWarning();
        this.initTeamChart();
        this.initPersonChart();
      }
      this.resize();
    },
    resetQueryForm() {
      this.$refs.queryForm.resetFields();
      this.queryBtnClick();
    },
    async tabClick(tab, event) {
      if (tab.name == 'time') {
        await this.getDuration();
        this.initAverageDurationChart();
        this.initDayDurationChart();
      } else {
        await this.getWarning();
        this.initTeamChart();
        this.initPersonChart();
      }
      this.resize();
    },
    resize() {
      this.workTimeTable.height = 0;
      this.warningTable.height = 0;
      this.$nextTick(() => {
        this.averageEchart.resize();
        this.dayEchart.resize();
        this.workTimeTable.height = this.$refs.mainTable.clientHeight + 'px';

        this.teamEchart.resize();
        this.personEchart.resize();
        this.warningTable.height = this.$refs.warningTable.clientHeight + 'px';
      });
    },
    initAverageDurationChart() {
      let option = {
        title: {
          text: '平均时长',
          left: 'left',
          top: '0',
        },
        tooltip: {
          trigger: 'item',
        },
        legend: {
          orient: 'vertical',
          top: '5%',
          left: 'right',
          align: 'left',
          data: ['有电作业', '无电作业'],
        },
        color: ['#f3765a', '#9ddc69'],
        series: [
          {
            name: '平均时长',
            type: 'pie',
            radius: ['55%', '70%'],
            label: {
              show: true,
              formatter: '{d}%',
            },
            labelLine: {
              show: false,
            },

            data: [
              {
                value: this.workShowInfo.averageYesDuration,
                name: '有电作业',
                label: {
                  color: '#f3765a',
                },
              },
              {
                value: this.workShowInfo.averageNoDuration,
                name: '无电作业',
                label: {
                  color: '#9ddc69',
                },
              },
            ],
          },
          {
            name: '平均时长',
            type: 'pie',
            radius: ['55%', '70%'],
            label: {
              show: true,
              position: 'center',
              fontSize: '12',
              formatter: () => {
                return `{a|平均作业时长}\n{b|${this.workShowInfo.averageTotalDuration}}分钟`;
              },
              rich: {
                a: {
                  lineHeight: 20,
                },
                b: {
                  fontSize: '24',
                  fontWeight: 'bold',
                },
              },
            },
            labelLine: {
              show: false,
            },
            data: [
              {
                value: this.workShowInfo.averageYesDuration,
                name: '有电作业',
              },
              {
                value: this.workShowInfo.averageNoDuration,
                name: '无电作业',
              },
            ],
          },
        ],
      };
      this.averageEchart = echarts.init(this.$refs['average-duration-charts']);
      this.averageEchart.setOption(option);
    },
    initDayDurationChart() {
      let option = {
        title: {
          text: '日时长',
          left: 'left',
          subtext: '单位：分钟',
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
          },
        },
        legend: { show: false, data: ['有电作业', '无电作业'] },
        color: ['#f3765a', '#9ddc69'],
        grid: {
          left: '3%',
          right: '4%',
          bottom: '5%',
          containLabel: true,
        },
        xAxis: {
          type: 'category',
          axisLabel: {
            interval: 0,
            rotate: '-40',
            align: 'center',
            margin: '30',
          },
          axisTick: {
            show: false,
          }, // 刻度线
          data: this.workShowInfo.dayPlanDurationsChartInfo.xAxisData,
        },
        yAxis: {
          type: 'value',
          axisLine: {
            show: true,
          }, //y轴
          splitLine: {
            show: false,
          }, // 网格线
        },
        dataZoom: [
          {
            show: true,
            realtime: true,
            start: 0,
            end: 50,
          },
          {
            type: 'inside',
            realtime: true,
            start: 0,
            end: 50,
          },
        ],
        series: [
          {
            name: '有电作业',
            type: 'bar',
            stack: 'total',
            itemStyle: {
              borderRadius: [0, 0, 10, 10],
            },
            barWidth: 5,
            data: this.workShowInfo.dayPlanDurationsChartInfo.hasData,
          },
          {
            name: '无电作业',
            type: 'bar',
            stack: 'total',
            itemStyle: {
              borderRadius: [10, 10, 0, 0],
            },
            barWidth: 5,
            data: this.workShowInfo.dayPlanDurationsChartInfo.noData,
          },
        ],
      };
      this.dayEchart = echarts.init(this.$refs['day-duration-charts']);
      this.dayEchart.setOption(option);
    },
    dayPlanFormatter(row, column, cellValue, index) {
      if (!cellValue) return '';
      const branchNum = this.branchNumMap[cellValue.slice(-2)];
      return cellValue.slice(0, -3) + ' ' + branchNum;
    },
    powerStateFormatter(row, column, cellValue, index) {
      if (!cellValue) return '';
      return this.powerStateMap[cellValue];
    },
    timeFormatter(row, column, cellValue, index) {
      if (!cellValue) return '';
      return dayjs(cellValue).format('YYYY-MM-DD HH:mm:ss');
    },
    workTimeTablePageSizeChange(pageSize) {
      this.workTimeTable.pageSize = pageSize;
    },
    workTimeTablePageNumChange(pageNum) {
      this.workTimeTable.pageNum = pageNum;
    },
    async getDurationDetail(row) {
      const {
        data: {
          code,
          data: { records, total },
        },
      } = await getDurationDetail({ ...row, ...this.detailDialog.detailTable });

      if (code == 0) {
        this.detailDialog.detailList = records;
        this.detailDialog.detailTable.total = total;
      } else {
        this.$message.error('数据异常');
        throw new Error('数据异常');
      }
    },
    async openDetailDialog(row) {
      try {
        this.rowData = row;
        await this.getDurationDetail(this.rowData);
        this.detailDialog.visible = true;
      } catch (error) {
        console.log(error);
      }
    },
    detailTablePageSizeChange(pageSize) {
      this.detailDialog.detailTable.pageSize = pageSize;
      this.getDurationDetail(this.rowData);
    },
    detailTablePageNumChange(pageNum) {
      this.detailDialog.detailTable.pageNum = pageNum;
      this.getDurationDetail(this.rowData);
    },
    async getWarning() {
      try {
        await this.$refs.queryForm.validate();
        const {
          data: {
            code,
            data: { total, workDept, workPerson, workWornings },
          },
        } = await getWarning({
          ...this.queryData,
          trainsetNames: this.queryData.trainsetNames.toString(),
          deptNames: this.queryData.deptNames.toString(),
          limit: this.warningTable.pageSize,
          page: this.warningTable.pageNum,
        });

        if (code == 1) {
          this.warningTable.total = total || 20;
          this.warningList = workWornings;
          const teamList = workDept.slice(0, 10);
          const personList = workPerson.slice(0, 10);
          this.teamEchartInfo = {
            xAxisData: teamList.map((item) => item.deptName),
            valueData: teamList.map((item) => item.count),
          };
          this.personEchartInfo = {
            xAxisData: personList.map((item) => `${item.workName}\n${item.deptName}`),
            valueData: personList.map((item, index) => {
              if (!personList[index - 1] || item.deptName !== personList[index - 1].deptName) {
                return {
                  value: item.count,
                  itemStyle: {
                    color: '#f2ae63',
                  },
                };
              } else {
                return item.count;
              }
            }),
          };

          this.initTeamChart();
          this.initPersonChart();
        }
      } catch (error) {
        console.log(error);
      }
    },
    initTeamChart() {
      let option = {
        title: {
          text: '班组预警排名',
          left: '10px',
          subtext: '单位：分钟',
          top: '5%',
        },
        tooltip: {
          trigger: 'item',
        },
        color: ['#4f85ff'],
        grid: {
          left: '3%',
          right: '4%',
          bottom: '11%',
          containLabel: true,
          top: 80,
        },
        xAxis: {
          type: 'category',
          axisTick: {
            show: false,
          }, // 刻度线
          data: this.teamEchartInfo.xAxisData,
        },
        yAxis: {
          type: 'value',
          axisLine: {
            show: true,
          }, //y轴
          splitLine: {
            show: false,
          }, // 网格线
        },
        dataZoom: [
          {
            show: true,
            realtime: true,
            start: 0,
            end: 50,
          },
          {
            type: 'inside',
            realtime: true,
            start: 0,
            end: 50,
          },
        ],
        series: [
          {
            name: '班组预警排名',
            type: 'bar',
            itemStyle: {
              borderRadius: [10, 10, 0, 0],
            },
            barWidth: 5,
            data: this.teamEchartInfo.valueData,
          },
        ],
      };

      this.teamEchart = echarts.init(this.$refs['team-charts']);

      this.teamEchart.setOption(option);
    },
    initPersonChart() {
      let option = {
        title: {
          text: '个人预警排名',
          left: '10px',
          subtext: '单位：分钟',
          top: '5%',
        },
        tooltip: {
          trigger: 'item',
        },
        color: ['#4f85ff'],

        grid: {
          left: '3%',
          right: '4%',
          bottom: '11%',
          containLabel: true,
          top: 80,
        },
        xAxis: {
          type: 'category',
          axisLabel: {
            interval: 0,
            rotate: '-40',
            align: 'center',
            margin: '40',
          },
          axisTick: {
            show: false,
          }, // 刻度线
          data: this.personEchartInfo.xAxisData,
        },
        yAxis: {
          type: 'value',
          axisLine: {
            show: true,
          }, //y轴
          splitLine: {
            show: false,
          }, // 网格线
        },
        dataZoom: [
          {
            show: true,
            realtime: true,
            start: 0,
            end: 50,
          },
          {
            type: 'inside',
            realtime: true,
            start: 0,
            end: 50,
          },
        ],
        series: [
          {
            name: '个人预警排名',
            type: 'bar',
            itemStyle: {
              borderRadius: [10, 10, 0, 0],
            },
            barWidth: 5,
            data: this.personEchartInfo.valueData,
          },
        ],
      };

      this.personEchart = echarts.init(this.$refs['person-charts']);

      this.personEchart.setOption(option);
    },
    warningTablePageSizeChange(pageSize) {
      this.warningTable.pageSize = pageSize;
      this.getWarning();
    },
    warningTablePageNumChange(pageNum) {
      this.warningTable.pageNum = pageNum;
      this.getWarning();
    },
  },
});
