const faultLevelList = [
  {
    id: 1,
    label: 'A',
    value: 'A',
  },
  {
    id: 2,
    label: 'B',
    value: 'B',
  },
  {
    id: 3,
    label: 'C',
    value: 'C',
  },
  {
    id: 4,
    label: 'D',
    value: 'D',
  },
]
const dealStateList = [
  {
    id: 0,
    label: '待处理',
    value: 0,
  },
  // {
  //   id: 1,
  //   label: '已处理',
  //   value: 1,
  // },

  {
    id: 2,
    label: '未处理',
    value: 2,
  },
]

const convertKeyWorkList = [
  {
    id: 0,
    label: '是',
    value: true,
  },
  {
    id: 1,
    label: '否',
    value: false,
  },
]
window.main = new Vue({
  el: '#faultToKeyWork',
  data() {
    return {
      unitCode: '',
      dayPlanId: '',
      userInfo: {},
      trainsetList: [],
      timeOptionRange: '',
      // pickerOptions: {
      //   disabledDate(time) {
      //     let timeOptionRange = main.timeOptionRange;
      //     let secondNum = 60 * 60 * 24 * 7 * 1000;
      //     if (timeOptionRange) {
      //       return (
      //         time.getTime() > timeOptionRange.getTime() + secondNum ||
      //         time.getTime() < timeOptionRange.getTime() - secondNum
      //       );
      //     }
      //   },
      //   onPick({ minDate, maxDate }) {
      //     if (minDate && !maxDate) {
      //       main.timeOptionRange = minDate;
      //     }
      //     if (maxDate) {
      //       main.timeOptionRange = '';
      //     }
      //   },
      // },
      classificationTreeData: [],
      funcNodeCodeProps: {
        value: 'id',
        label: 'name',
        emitPath: false,
        checkStrictly: true,
        lazy: true,
      },
      faultLevelList: Object.freeze(faultLevelList),
      dealStateList: Object.freeze(dealStateList),
      convertKeyWorkList: Object.freeze(convertKeyWorkList),
      headerFormData: {
        trainsetId: '',
        findTime: [],
        subFunctionClassId: [],
        faultGrade: '',
        dealWithDescCode: '',
        convertKeyWork: ''
      },
      headerFormRules: {
        trainsetId: [],
        findTime: [
          {
            required: true,
            message: '请选择发现时间',
            trigger: 'change',
          },
        ],
        subFunctionClassId: [],
        faultGrade: [],
        dealWithDescCode: [],
        convertKeyWork: []
      },
      mainTable: {
        data: [],
        height: 0,
        pageNum: 1,
        pageSize: 20,
        total: 20,
      },
      selectFaultList: [],
      workTypeList: [],
      workEnvList: [],
      workTeamList: [],
      toKeyWorkDialog: {
        visible: false,
        faultToKeyWorkList: [],
        faultFormData: {},
        faultFormRules: {
          content: [{ required: true, message: '必填', trigger: 'blur' }],
        },
        toKeyWorkFormData: {
          keyWorkType: '',
          position: '',
          workEnv: '',
          remark: '',
          workTeam: {}
        },
        toKeyWorkFormRules: {
          keyWorkType: [],
          position: [],
          workEnv: [],
          remark: [],
          workTeam: [{ required: true, message: '请选择作业班组', trigger: 'blur' }]
        },
      },
    }
  },
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('检修派工')
  },
  created() {
    this.getUnitCode()
    this.getDay()
    this.getUser()
    this.initFindTime()
    this.getTrainsetListReceived()
    this.getFunctionClass()
  },
  mounted() {
    this.getCenterFaultInfo()
    this.initMainTableHeight()
    window.addEventListener('resize', this.initMainTableHeight())
    this.$once('hook:beforeDestroy', () => {
      window.removeEventListener('resize', this.initMainTableHeight())
    })
  },
  methods: {
    // 获取当前登录人运用所编码
    async getUnitCode() {
      let data = await getUnitCode()
      this.unitCode = data
    },
    // 获取日计划
    async getDay() {
      const { data: { dayPlanId } } = await getDay()
      this.dayPlanId = dayPlanId
    },

    async getUser() {
      const {
        data: {
          data: { staffId, name, workTeam },
        },
      } = await getUser()
      this.userInfo = {
        workerId: staffId,
        workerName: name,
        teamCode: workTeam && workTeam.teamCode ? workTeam.teamCode : '',
        teamName: workTeam && workTeam.teamName ? workTeam.teamName : '',
      }
    },

    initFindTime() {
      this.headerFormData.findTime = [dayjs().format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')]
    },
    async getTrainsetListReceived() {
      this.trainsetList = await getTrainsetListReceived()
    },
    async getFunctionClass() {
      this.classificationTreeData = await getFunctionClass()
      this.addListLeaf(this.classificationTreeData)
    },
    addListLeaf(list) {
      list.forEach((item) => {
        if (item.children && item.children.length > 0) {
          item.leaf = false
          this.addListLeaf(item.children)
        } else {
          item.leaf = true
        }
      })
    },
    initMainTableHeight() {
      this.$nextTick(() => {
        this.mainTable.height = this.$refs.mainTable.clientHeight + 'px'
      })
    },
    async getCenterFaultInfo() {
      try {
        await this.$refs.headerForm.validate()
        const { trainsetId, subFunctionClassId, faultGrade, dealWithDescCode, convertKeyWork } = this.headerFormData
        const { pageSize, pageNum } = this.mainTable

        const { records, total } = await getCenterFaultInfo({
          trainsetId,
          subFunctionClassId,
          faultGrade,
          dealWithDescCode,
          convertKeyWork,
          startTime: Array.isArray(this.headerFormData.findTime) ? this.headerFormData.findTime[0] || '' : '',
          endTime: Array.isArray(this.headerFormData.findTime)
            ? dayjs(this.headerFormData.findTime[1]).add(1, 'day').format('YYYY-MM-DD') || ''
            : '',
          pageNum,
          pageSize,
        })
        this.mainTable.data = records
        this.mainTable.total = Number(total)
      } catch (error) {
        console.log(error)
      }
    },
    resetHeaderForm() {
      this.$refs.headerForm.resetFields()
      this.getCenterFaultInfo()
    },
    timeFormat(row, column, cellValue, index) {
      return dayjs(cellValue).format('YYYY-MM-DD')
    },
    faultTablePageSizeChange(pageSize) {
      this.mainTable.pageSize = pageSize
      this.getCenterFaultInfo()
    },
    faultTablePageNumChange(pageNum) {
      this.mainTable.pageNum = pageNum
      this.getCenterFaultInfo()
    },
    selectFault(selection) {
      this.selectFaultList = selection
    },
    async getWorkTypeList() {
      this.workTypeList = await getWorkTypeList()
    },
    async getWorkEnvList() {
      this.workEnvList = await getWorkEnvList()
    },

    // 获取当前登录人所在班组
    async getActuallyWorkTeams(trainsetIds) {
      return await getActuallyWorkTeams({
        unitCode: this.unitCode,
        dayPlanId: this.dayPlanId,
        trainsetIds,
        staffId: this.userInfo.workerId,
      })
    },

    async openToKeyWorkDialog() {
      if (this.selectFaultList.length > 0) {
        this.toKeyWorkDialog.faultToKeyWorkList = await getKeyWorkFlowRunByFault(this.selectFaultList)
        let trainsetIdMap = this.toKeyWorkDialog.faultToKeyWorkList.map((v) => {
          return v.trainsetId
        })
        this.workTeamList = await this.getActuallyWorkTeams(trainsetIdMap.join(','))
        if (this.workTeamList.length == 1) {
          console.log(this.workTeamList[0])
          this.toKeyWorkDialog.toKeyWorkFormData.workTeam = this.workTeamList[0]
        }
        this.toKeyWorkDialog.faultToKeyWorkList.forEach((fault) => {
          this.$set(this.toKeyWorkDialog.faultFormData, fault.faultId, fault.content)
          this.$set(this.toKeyWorkDialog.faultFormRules, fault.faultId, [
            { required: true, message: '请输入作业内容', trigger: 'blur' },
          ])
        })
        this.getWorkTypeList()
        this.getWorkEnvList()
        this.toKeyWorkDialog.visible = true
      } else {
        this.$message.warning('请先选择故障');
      }
    },
    toKeyWorkDialogClose() {
      this.$refs.faultForm.resetFields()
      this.$refs.toKeyWorkDialogFormRef.resetFields()
    },
    async setKeyWorkFlowRunByFault() {
      try {
        await this.$refs.faultForm.validate()
        const afterTranList = this.toKeyWorkDialog.faultToKeyWorkList.map(fault => {
          return { ...fault, content: this.toKeyWorkDialog.faultFormData[fault.faultId], ...this.toKeyWorkDialog.toKeyWorkFormData, ...this.toKeyWorkDialog.toKeyWorkFormData.workTeam }
        })
        await setKeyWorkFlowRunByFault(afterTranList)
        this.$message.success('转换成功')
        this.getCenterFaultInfo()
        this.toKeyWorkDialog.visible = false
      } catch (error) {
        console.log(error)
      }
    },
  },
})
