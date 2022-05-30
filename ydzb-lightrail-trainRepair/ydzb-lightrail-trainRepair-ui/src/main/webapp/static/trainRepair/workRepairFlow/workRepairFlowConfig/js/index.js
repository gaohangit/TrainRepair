let copyDataSimple = (data) => {
  if (data !== undefined) {
    return JSON.parse(JSON.stringify(data))
  } else {
    return undefined
  }
}
let checkNumber = (rule, value, callback) => {
  if (!value) {
    return callback()
  }
  setTimeout(() => {
    let reg = /\D/g
    if (reg.test(value)) {
      callback(new Error('请输入数字值'))
    } else {
      callback()
    }
  }, 1000)
}

let maxRecommendedPicNum = (rule, value, callback) => {
  if (!value) {
    return callback()
  }
  setTimeout(() => {
    let reg = /\D/g
    if (reg.test(value)) {
      callback(new Error('请输入数字值'))
    } else {
      if (value > 100) {
        callback(new Error('建议上传图片数量配置不能超过100'))
      } else {
        callback()
      }
    }
  }, 1000)
}

let maxIntervalNumber = (rule, value, callback) => {
  if (!value) {
    return callback()
  }
  setTimeout(() => {
    let reg = /\D/g
    if (reg.test(value)) {
      callback(new Error('请输入数字值'))
    } else {
      if (value > 1440) {
        callback(new Error('距上节点最小时间间隔不能超过1440'))
      } else {
        callback()
      }
    }
  }, 1000)
}

Vue.directive('init', {
  bind: function (el, binding) {
    if (binding.value) {
      let endFn = binding.value()
      if (typeof endFn == "function") {
        el.endFn_ = endFn
      }
    }
  },
  unbind: function (el, binding) {
    if (el.endFn_) {
      el.endFn_()
      delete el.endFn_
    }
  }
})
Vue.directive('background-black', function (el, binding) {
  let pEl = el.parentElement
  // console.log(el,pEl);
  if (!pEl) return
  if (binding.value === true) {
    let hasMask = pEl.querySelector('.mask')
    if (hasMask) {
      return
    }
    let mask = document.createElement('div')
    mask.className = 'mask'
    pEl.insertBefore(mask, el)
  } else if (binding.value === false) {
    let mask = pEl.querySelector('.mask')
    if (mask) {
      mask.remove()
    }
  }
})
var showConditionShowConfig = {
  REPAIR_TYPE: (flowTypeCode) => {
    return flowTypeCode == 'REPAIR_ONE'
  },
  KEY_WORD: (flowTypeCode) => {
    return flowTypeCode == 'TEMPORARY'
  },
}

let getNodeBaseFormData = () => {
  return {
    name: '',
    skip: false,
    roleConfigs: [],
    disposeAfterSkip: false,
    excludeRoles: [],
    pair: '',
    recommendedPicNum: '',
    minDisposeNum: '',
    overtimeWaring: '',
    disposeSubflow: false,
    minIntervalRestrict: '',
    remindRoles: [],
    functionType: '',
    remark: '',
  }
}

const tooltipContent = {
  roleConfigs:
    '满足选择的角色可进行节点处理，如果指定角色的输入数量为x，则表示当前节点至少要有x名满足指定角色的人进行处理才能完成。',
  excludeRoles: '满足当前角色的人禁止处理当前节点。',
  pair:
    '成对节点特性：1、处理了成对节点中靠前节点的人也必须处理另一个节点,2、多人可同时处理成对节点互不影响;成对节点限制：1、仅可在流程主干配置,2、以下属性强制成对节点之间保持一致：角色、排除角色、是否可跳过、跳过后是否可补卡、最小处理节点人数。',
  overtimeWaring: '据上节点时间达到此值时，提醒满足配置条件的人进行节点处理。',
  disposeSubflow:
    '实际处理带有子流程的节点时，当前节点与当前节点的子流程的节点不可同时进行处理。“是”代表处理子流程节点，“否”代表处理当前节点。',
  minIntervalRestrict: '处理节点时，如果据上节点处理时间不足此值，则经用户确认后进行节点处理。',
  functionType: '根据当前节点要接入的系统进行选择',
}

window.main = new Vue({
  el: '#workRepairFlowConfig',
  data() {
    let booleanToStrMap = {
      true: "1",
      false: "0",
    }
    let strToBooleanMap = {
      1: true,
      0: false,
    }
    let nodeConfigCore = new Vue({
      data: {
        formData: getNodeBaseFormData(),
        yesNoOptions: [
          {
            value: "1",
            label: "是"
          },
          {
            value: "0",
            label: "否"
          }
        ]
      },
      computed: {
        strSkip: {
          get() {
            return booleanToStrMap[this.formData.skip]
          },
          set(v) {
            this.$set(this.formData, "skip", strToBooleanMap[v])
          }
        },
        strDisposeAfterSkip: {
          get() {
            return booleanToStrMap[this.formData.disposeAfterSkip]
          },
          set(v) {
            this.$set(this.formData, "disposeAfterSkip", strToBooleanMap[v])
          }
        }
      }
    })
    // 最小处理节点人数检查 （最小处理节点人数大于等于角色已配置的明确的总人数并且大于等于1）
    let maxDisposeNumber = (rule, value, callback) => {
      if (!value) {
        return callback()
      }
      setTimeout(() => {
        let reg = /\D/g
        if (reg.test(value)) {
          callback(new Error('请输入数字值'))
        } else {
          if (value < 1 || value < this.nodeConfigFormData.roleConfigs.length) {
            callback(new Error('最小处理节点人数大于等于角色已配置的明确的总人数并且大于等于1'))
          } else {
            callback()
          }
        }
      }, 1000)
    }
    return {
      unitCode: '',
      currentSection: '',
      loading: false,
      that: this,
      flowData: {
        nodes: [],
        nodeVectors: [],
        parallelFlowConfigs: [],
        subflowInfoList: [],
        pairNodeInfo: [],
      },

      subFlowData: {
        nodes: [],
        nodeVectors: [],
        parallelFlowConfigs: [],
        pairNodeInfo: [],
      },
      prevChildFlowData: {},
      flowTypeList: [],
      allTrainsetList: [],
      trainsetListOrigin: [],
      headerQueryFormData: {
        flowName: '',
        flowTypeCode: '',
        trainType: '',
        trainTemplate: '',
        trainsetId: '',
        unitCode: '',
        usable: '',
      },
      headerQueryFormRules: {
        flowName: [],
        flowTypeCode: [],
        trainType: [],
        trainTemplate: [],
        trainsetId: [],
        unitCode: [],
        usable: [],
      },
      mainTableHeight: 200,
      mainFlowTableData: [],
      mainFlowTablePageNum: 1,
      mainFlowTablePageSize: 10,
      mainFlowTablePageTotal: 10,
      typeDialogStatus: '',
      typeDialogVisible: false,
      // loadFlow: false,
      unitList: [],
      repairTypeList: [],
      trainTypesLabel: '车型：',
      trainTemplatesLabel: '批次：',
      trainsetIdsLabel: '车组：',
      flowId: '',
      conditionId: '',
      defaultType: '',
      typeDialogFormData: {
        flowTypeCode: '',
        name: '',
        unitCode: '',
        trainTypeExclude: false,
        trainTemplateExclude: false,
        trainsetIdExclude: false,
        trainTypes: [],
        trainTemplates: [],
        trainsetIds: [],
        keyWords: [],
        repairType: '',
      },
      originalFlowConfigUsable: false,
      typeDialogFormRules: {
        flowTypeCode: [
          {
            required: true,
            message: '请选择流程类型',
            trigger: 'change',
          },
        ],
        name: [
          {
            required: true,
            message: '请输入流程名称',
            trigger: 'change',
          },
        ],
        unitCode: [
          {
            required: true,
            message: '请选择运用所',
            trigger: 'change',
          },
        ],
        trainTypeExclude: [],
        trainTemplateExclude: [],
        trainsetIdExclude: [],
        trainTypes: [],
        trainTemplates: [],
        trainsetIds: [],
        keyWords: [],
        repairType: [],
      },
      keywordConfigDialogVisible: false,
      keywordConfigFormData: {
        mark: '',
      },
      keywordConfigFormRules: {
        mark: [
          {
            required: true,
            message: '请输入关键词',
            trigger: 'blur',
          },
        ],
      },
      keywordConfigTableData: [],
      transferDialogVisible: false,
      transferType: '',
      transferValue: [],
      transferList: [],
      transferConfig: { key: 'traintype', label: 'traintype' },

      // 右键菜单
      axis: {
        x: 0,
        y: 0,
      },
      contextmenuWrapStatus: false, //显示
      nowContentMenuStatus: '', //点击的操作
      contextMenuListOrigin: [
        {
          icon: 'el-icon-circle-plus-outline',
          label: '插入上一个节点',
          value: 'addPrevNode',
        },
        {
          icon: 'el-icon-circle-plus-outline',
          label: '插入下一个节点',
          value: 'addNextNode',
        },
        {
          icon: 'el-icon-circle-plus-outline',
          label: '删除节点',
          value: 'deleteNode',
        },
        {
          icon: 'el-icon-circle-plus-outline',
          label: '移动节点到...',
          value: 'moveNodeTo',
        },
        {
          icon: 'el-icon-circle-plus-outline',
          label: '节点配置',
          value: 'nodeConfig',
        },
        // {
        //   icon: "el-icon-circle-plus-outline",
        //   label: "子流程配置",
        //   value: "configChildFlow",
        // },
        // {
        //   icon: "el-icon-circle-plus-outline",
        //   label: "删除子流程",
        //   value: "deleteChildFlow",
        // },
        {
          icon: 'el-icon-circle-plus-outline',
          label: '添加并行区段',
          value: 'addParall',
        },
        {
          icon: 'el-icon-circle-plus-outline',
          label: '并行区段配置',
          value: 'parallConfig',
        },

        {
          icon: 'el-icon-circle-plus-outline',
          label: '插入流程模板',
          value: 'addFlowTemplate',
        },
      ],
      contextMenuStatus: '', //是对父流程还是子流程的右键

      selfNodeConfig: false,
      nodeConfigDialogVisible: false,
      nodeTaskFlowConfigList: [],
      nodeConfigCore,
      nodeConfigPairNodeInfo: null,
      nodeConfigTempWorkPepairFlowInstance: null,
      nodeConfigFormRules: {
        name: [
          {
            required: true,
            message: '请输入节点名称',
            trigger: 'blur',
          },
        ],
        skip: [],
        roleConfigs: [
          {
            required: true,
            message: '请选择角色',
            trigger: 'change',
          },
        ],
        disposeAfterSkip: [],
        excludeRoles: [],
        pair: [],
        recommendedPicNum: [
          {
            validator: maxRecommendedPicNum,
            trigger: ['blur', 'change'],
          },
        ],
        minDisposeNum: [
          {
            validator: maxDisposeNumber,
            trigger: ['blur', 'change'],
          },
        ],
        overtimeWaring: [
          {
            validator: checkNumber,
            trigger: ['blur', 'change'],
          },
        ],
        disposeSubflow: [],
        minIntervalRestrict: [
          {
            validator: maxIntervalNumber,
            trigger: ['blur', 'change'],
          },
        ],
        remindRoles: [],
        functionType: [],
        remark: [],
      },

      pFlowId: '',
      childFlowId: '',
      newId: '',
      nowNodeId: '',
      newNode: {},
      parallelConfigDialog: {},
      trunkNodeList: [],
      parallelConfigDialogVisible: false,
      parallelConfigFormData: {
        trunkStartNodeId: '',
        trunkEndNodeId: '',
      },
      parallelConfigFormRules: {
        trunkStartNodeId: [
          {
            required: true,
            message: '请选择区段起始相邻节点',
            trigger: 'change',
          },
        ],
        trunkEndNodeId: [
          {
            required: true,
            message: '请选择区段结束相邻节点',
            trigger: 'change',
          },
        ],
      },
      roleList: [],
      roleConfigsNum: {},
      flowTempalteDialog: {
        flowTempalteDialogVisible: false,
        flowTempalteFormData: {
          flowName: '',
        },
        flowTempalteRules: {
          flowName: [],
        },
        flowTempalteTableData: [],
        flowTempaltePageNumber: 1,
        flowTempaltePageSize: 20,
        flowTempalteTotal: 20,
      },
      childFlowDialog: {
        childFlowDialogVisible: false,
      },
      subFlowConfigurating: false,

      workRepairFlowComponentRefreshFlag: 'A',

      // 移动节点
      selectVectorStatus: {
        child: false,
        parent: false,
      },
      selectVectorObj: {},

      // 标记当前节点是否是成对后一个节点
      selectPairEndNode: false,
    }
  },
  watch: {
    'typeDialogFormData.trainTypeExclude'(val) {
      this.typeDialogFormData.trainTemplates = []
      this.typeDialogFormData.trainsetIds = []
      if (!val) {
        this.trainTypesLabel = '车型：'
        this.typeDialogFormRules.trainTypes = [{ required: false }]
        this.$refs.typeDialogFormRef.validateField('trainTypes')
      } else {
        this.trainTypesLabel = '排除车型：'
        this.typeDialogFormRules.trainTypes = [{ required: true, message: '请选择车型', trigger: 'change' }]
      }
    },
    'typeDialogFormData.trainTemplateExclude'(val) {
      this.typeDialogFormData.trainsetIds = []
      if (!val) {
        this.trainTemplatesLabel = '批次：'
        this.typeDialogFormRules.trainTemplates = [{ required: false }]
        this.$refs.typeDialogFormRef.validateField('trainTemplates')
      } else {
        this.trainTemplatesLabel = '排除批次：'
        this.typeDialogFormRules.trainTemplates = [{ required: true, message: '请选择批次', trigger: 'change' }]
      }
    },
    'typeDialogFormData.trainsetIdExclude'(val) {
      if (!val) {
        this.trainsetIdsLabel = '车组：'
        this.typeDialogFormRules.trainsetIds = [{ required: false }]
        this.$refs.typeDialogFormRef.validateField('trainsetIds')
      } else {
        this.trainsetIdsLabel = '排除车组：'
        this.typeDialogFormRules.trainsetIds = [{ required: true, message: '请选择车组', trigger: 'change' }]
      }
    },
    flowData(val) {
      // console.log(JSON.stringify(val, null, 2));
      // console.log(val);
    },
  },
  computed: {
    nodeConfigFormData: {
      get() {
        return this.nodeConfigCore.formData
      },
      set(v) {
        this.nodeConfigCore.formData = v
      }
    },
    pageTitle: GeneralUtil.getObservablePageTitleGetter('作业流程配置'),
    postRoleList({ roleList, nodeConfigFormData }) {
      let list = copyDataSimple(roleList)
      if (this.typeDialogFormData.flowTypeCode === 'PLANLESS_KEY') {
        list = list.filter((item) => item.type !== '1')
      }
      return list
        .map((role) => ({
          id: role.code,
          value: role.code,
          label: role.name,
        }))
        .filter((role) => !nodeConfigFormData.excludeRoles.includes(role.id))
        .filter((role) => !nodeConfigFormData.remindRoles.includes(role.id))
    },
    roleCodeTypeMap({ roleList }) {
      return roleList.reduce((prev, item) => {
        prev[item.code] = item.type
        return prev
      }, {})
    },
    roleCodeNameMap({ roleList }) {
      return roleList.reduce((prev, item) => {
        prev[item.code] = item.name
        return prev
      }, {})
    },
    excludeRoleList({ roleList, nodeConfigFormData }) {
      let list = copyDataSimple(roleList)
      if (this.typeDialogFormData.flowTypeCode === 'PLANLESS_KEY') {
        list = list.filter((item) => item.type !== '1')
      }
      return list
        .map((role) => ({
          id: role.code,
          value: role.code,
          label: role.name,
        }))
        .filter((role) => !nodeConfigFormData.roleConfigs.includes(role.id))
        .filter((role) => !nodeConfigFormData.remindRoles.includes(role.id))
    },
    remindRoleList({ roleList, nodeConfigFormData }) {
      let list = copyDataSimple(roleList)
      if (this.typeDialogFormData.flowTypeCode === 'PLANLESS_KEY') {
        list = list.filter((item) => item.type !== '1')
      }
      return list
        .map((role) => ({
          id: role.code,
          value: role.code,
          label: role.name,
        }))
        .filter((role) => !nodeConfigFormData.excludeRoles.includes(role.id))
        .filter((role) => !nodeConfigFormData.roleConfigs.includes(role.id))
    },

    flowTypeMap({ flowTypeList }) {
      return flowTypeList.reduce((prev, item) => {
        prev[item.code] = item.name
        return prev
      }, {})
    },
    trainTypeList({ allTrainsetList }) {
      // 去重+组合
      const map = allTrainsetList.reduce((prev, item) => {
        if (!prev.has(item.traintype)) {
          prev.set(item.traintype, {
            traintype: item.traintype,
          })
        }
        return prev
      }, new Map())
      let arr = [...map.values()]

      return arr
    },
    trainTempList({ allTrainsetList, headerQueryFormData }) {
      // 去重+组合
      const map = allTrainsetList.reduce((prev, item) => {
        if (item.traintempid && !prev.has(item.traintempid)) {
          prev.set(item.traintempid, {
            traintempid: item.traintempid,
            traintype: item.traintype,
          })
        }
        return prev
      }, new Map())
      let arr = [...map.values()]
      // 过滤条件
      if (headerQueryFormData.trainType) {
        arr = arr.filter((item) => item.traintype == headerQueryFormData.trainType)
      }

      return arr
    },
    trainsetList({ allTrainsetList, headerQueryFormData }) {
      let arr = [...allTrainsetList]
      // 过滤条件
      if (headerQueryFormData.trainType) {
        arr = arr.filter((item) => item.traintype == headerQueryFormData.trainType)
      }
      if (headerQueryFormData.trainTemplate) {
        arr = arr.filter((item) => item.traintempid == headerQueryFormData.trainTemplate)
      }
      return arr
    },
    typeDialogTitle({ typeDialogStatus }) {
      if (typeDialogStatus == 'add') {
        return '作业流程配置-新增'
      } else if (typeDialogStatus == 'edit') {
        return '作业流程配置-编辑'
      } else if (typeDialogStatus == 'look') {
        return '作业流程配置-查看'
      }
    },
    trainTypesName({ typeDialogFormData }) {
      return typeDialogFormData.trainTypes.toString()
    },
    trainTemplatesName({ typeDialogFormData }) {
      return typeDialogFormData.trainTemplates.toString()
    },
    trainsetIdNameMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        prev[item.trainsetid] = item.trainsetname
        return prev
      }, {})
    },
    trainsetIdsName({ typeDialogFormData, trainsetIdNameMap }) {
      return typeDialogFormData.trainsetIds
        .reduce((prev, item) => {
          prev += trainsetIdNameMap[item] + ','
          return prev
        }, '')
        .slice(0, -1)
    },
    keyWordsName({ typeDialogFormData }) {
      return typeDialogFormData.keyWords.toString()
    },
    transferTitles({ transferType }) {
      if (transferType == 'trainTypes') {
        return ['待选择车型', '已选择车型']
      } else if (transferType == 'trainTemplates') {
        return ['待选择批次', '已选择批次']
      } else if (transferType == 'trainsetIds') {
        return ['待选择车组', '已选择车组']
      } else {
        return ['待选择关键字', '已选择关键字']
      }
    },
    transferTrainTypeList({ allTrainsetList }) {
      // 去重+组合
      const map = allTrainsetList.reduce((prev, item) => {
        if (!prev.has(item.traintype)) {
          prev.set(item.traintype, {
            traintype: item.traintype,
          })
        }
        return prev
      }, new Map())
      let arr = [...map.values()]

      return arr
    },
    transferTrainTempList({ allTrainsetList, typeDialogFormData }) {
      // 去重+组合
      const map = allTrainsetList.reduce((prev, item) => {
        if (!prev.has(item.traintempid)) {
          prev.set(item.traintempid, {
            traintempid: item.traintempid,
            traintype: item.traintype,
          })
        }
        return prev
      }, new Map())
      let arr = [...map.values()].filter((item) => {
        if (typeDialogFormData.trainTypes.length > 0) {
          let include = typeDialogFormData.trainTypes.includes(item.traintype)
          // 包含
          if (!typeDialogFormData.trainTypeExclude) {
            return include
          } else {
            // 排除
            return !include
          }
        } else {
          return item
        }
      })

      return arr
    },
    transferTrainsetList({ allTrainsetList, typeDialogFormData }) {
      let arr = [...allTrainsetList]
      // 过滤条件
      if (!typeDialogFormData.trainTypeExclude) {
        // 包含
        if (typeDialogFormData.trainTypes.length > 0) {
          arr = arr.filter((item) => typeDialogFormData.trainTypes.includes(item.traintype))
        }
      } else {
        // 排除
        if (typeDialogFormData.trainTypes.length > 0) {
          arr = arr.filter((item) => !typeDialogFormData.trainTypes.includes(item.traintype))
        }
      }
      if (!typeDialogFormData.trainTemplateExclude) {
        // 包含
        if (typeDialogFormData.trainTemplates.length > 0) {
          arr = arr.filter((item) => typeDialogFormData.trainTemplates.includes(item.traintempid))
        }
      } else {
        // 排除
        if (typeDialogFormData.trainTemplates.length > 0) {
          arr = arr.filter((item) => !typeDialogFormData.trainTemplates.includes(item.traintempid))
        }
      }
      return arr
    },
    contextmenuWrapStyle() {
      let x = this.axis.x
      let y = this.axis.y
      // 判断menu距离浏览器可视窗口底部距离，一面距离底部太近的时候，会导致menu被遮挡
      let menuHeight = this.contextMenuList.length * 32 //不能用scrollHeight，获取到的menu是上一次的menu宽高
      let menuWidth = 150 //不能用scrollWidth,同理
      return {
        left: (document.body.clientWidth < x + menuWidth ? x - menuWidth : x) + 'px',
        top: (document.body.clientHeight < y + menuHeight ? y - menuHeight : y) + 'px',
      }
    },
    contextMenuList() {
      let menuList = copyDataSimple(this.contextMenuListOrigin)
      if (this.nowNodeId == 'start') {
        menuList.forEach((menu) => {
          if (menu.value == 'addNextNode') {
            menu.disabled = false
          } else {
            menu.disabled = true
          }
        })
      } else if (this.nowNodeId == 'end') {
        menuList.forEach((menu) => {
          if (menu.value == 'addPrevNode') {
            menu.disabled = false
          } else {
            menu.disabled = true
          }
        })
      }
      if (this.curWorkRepairFlowRef && this.curWorkRepairFlowRef.cannotdeleteNodeIdList.includes(this.nowNodeId)) {
        menuList.forEach((menu) => {
          if (menu.value == 'deleteNode') {
            menu.disabled = true
          }
          if (menu.value == 'moveNodeTo') {
            menu.disabled = true
          }
        })
      }
      if (this.curWorkRepairFlowRef && this.curWorkRepairFlowRef.parallelFlowNodeIds.includes(this.nowNodeId)) {
        menuList.forEach((menu) => {
          if (menu.value == 'addParall') {
            menu.disabled = true
          }
        })
      }
      if (this.curWorkRepairFlowRef && !this.curWorkRepairFlowRef.parallelFlowNodeIds.includes(this.nowNodeId)) {
        menuList.forEach((menu) => {
          if (menu.value == 'parallConfig') {
            menu.disabled = true
          }
        })
      }
      if (this.curWorkRepairFlowRef && this.curWorkRepairFlowRef.cannotdeleteSubFlowIdList.includes(this.nowNodeId)) {
        menuList.forEach((menu) => {
          if (menu.value == 'deleteChildFlow') {
            menu.disabled = true
          }
        })
      }
      if (this.typeDialogFormData.flowTypeCode == 'PLANLESS_KEY') {
        menuList.forEach((menu) => {
          if (menu.value == 'configChildFlow') {
            menu.disabled = true
          }
        })
      }

      if (this.contextMenuStatus == 'childContextMenu') {
        menuList.forEach((menu) => {
          if (menu.value == 'addParall') {
            menu.disabled = true
          }
          if (menu.value == 'parallConfig') {
            menu.disabled = true
          }
          if (menu.value == 'configChildFlow') {
            menu.disabled = true
          }
          if (menu.value == 'deleteChildFlow') {
            menu.disabled = true
          }
        })
      }
      return menuList
    },

    subFlowInfoMap({ flowData }) {
      return flowData.subflowInfoList.reduce((prev, item) => {
        (item.parallelFlowConfigs = []), (item.pairNodeInfo = [])
        prev[item.id] = item
        return prev
      }, {})
    },
    curWorkRepairFlowRef() {
      // 根据refreshWorkRepairFlowComponentFlag变化 重新计算返回值
      if (this.workRepairFlowComponentRefreshFlag && !this.subFlowConfigurating) {
        // console.log(1, this.$refs.workRepairFlow);
        return this.$refs.workRepairFlow
      } else {
        // console.log(2, this.$refs.workRepairSubFlow);//2,undefined
        return this.$refs.workRepairSubFlow
      }
    },
    pairNodeList() {
      // !this.nodeConfigTempWorkPepairFlowInstance.pairNodeIdMap[node.id]&&
      if (this.nodeConfigTempWorkPepairFlowInstance) {
        return this.nodeConfigTempWorkPepairFlowInstance.trunkFlowNodeListSorted.filter((node) => {
          if (this.selfNodeConfig) {
            return (
              this.nodeConfigTempWorkPepairFlowInstance.isTrunkNode(node.id) &&
              !this.nodeConfigTempWorkPepairFlowInstance.isVirtualNode(node.id) &&
              node.id != this.nowNodeId
            )
          } else {
            return (
              this.nodeConfigTempWorkPepairFlowInstance.isTrunkNode(node.id) &&
              !this.nodeConfigTempWorkPepairFlowInstance.isVirtualNode(node.id)
            )
          }
        })
      } else {
        return []
      }
    },
    transferTitle({ transferType }) {
      if (transferType == 'trainTypes') {
        return '车型配置'
      } else if (transferType == 'trainTemplates') {
        return '批次配置'
      } else if (transferType == 'trainsetIds') {
        return '车组配置'
      } else {
        return '关键字配置'
      }
    },
  },
  async created() {
    await this.getUnitCode()
    await this.isCenter()
    await this.getFlowTypeList()
    await this.getTrainsetList()
    await this.getTaskFlowConfigList()
    await this.getUnitList()
    await this.getPostRoleList()
  },
  mounted() {
    this.mainTableHeightChange()
    window.addEventListener('resize', this.mainTableHeightChange)
    this.$once('hook:beforeDestroy', () => {
      window.removeEventListener('resize', this.mainTableHeightChange)
    })
  },

  methods: {
    // 获取当前登录人运用所编码
    async getUnitCode() {
      let data = await getUnitCode()
      if (data.code == 1) {
        this.unitCode = data.data
      } else {
        this.$message.error('获取登录人信息失败！')
      }
    },

    // 获取当前登录是否为所级
    async isCenter() {
      let res = await isCenter()
      this.currentSection = res.data
    },

    // 去除过期岗位信息
    postFilter(data) {
      let nodeList = data.nodeList || data.nodes || []
      nodeList.forEach((node) => {
        node.roleConfigs = node.roleConfigs.filter((role) => this.roleCodeNameMap[role.roleId])
      })
    },
    async verifyFlowType() {
      let loading = this.$loading()
      try {
        const res = await verifyFlowType({ unitCode: this.unitCode })

        if (res.code == 1) {
          this.$message.success(res.msg)
        } else {
          this.$message.warning(res.msg)
        }
      } finally {
        loading.close()
      }

    },
    mainTableHeightChange() {
      this.$nextTick(() => {
        this.mainTableHeight = 'calc(100vh - 220px)'
      })
    },
    async getUnitList() {
      const { data } = await getUnitList()
      this.unitList = data

      if (!this.currentSection) {
        this.headerQueryFormData.unitCode = this.unitList[0].unitCode
      }
    },
    async getPostRoleList() {
      const { code, data } = await getPostRoleList()
      if (code == 1) {
        if (data) {
          this.roleList = data
        } else {
          this.roleList = []
        }
      } else {
        this.$message.error('获取角色列表失败')
        this.roleList = []
      }
    },
    roleConfigsChange({ key, num }) {
      this.$set(this.roleConfigsNum, key, num)
    },
    getFormItemPairShow() {
      if (this.curWorkRepairFlowRef && this.nowContentMenuStatus == 'nodeConfig') {
        return !this.curWorkRepairFlowRef.pairNodeIdMap[this.nowNodeId]
      } else {
        return true
      }
    },
    getFormItemChildFlowShow() {
      if (this.curWorkRepairFlowRef && this.curWorkRepairFlowRef.data.nodes.find((node) => node.id == this.nowNodeId)) {
        return this.curWorkRepairFlowRef.data.nodes.find((node) => node.id == this.nowNodeId).childFlowId
      } else {
        return false
      }
    },
    getFormItemPairNodeShow() {
      if (this.contextMenuStatus == 'childContextMenu') {
        return false
      } else if (this.nowContentMenuStatus == 'addParall') {
        return false
      } else if (this.curWorkRepairFlowRef && this.curWorkRepairFlowRef.parallelFlowNodeIds.includes(this.nowNodeId)) {
        return false
      } else {
        return true
      }
    },
    refreshWorkRepairFlowComponentFlag() {
      this.workRepairFlowComponentRefreshFlag = this.workRepairFlowComponentRefreshFlag == 'A' ? 'B' : 'A'
    },
    conditionShow(key) {
      return showConditionShowConfig[key](this.typeDialogFormData.flowTypeCode)
    },
    getTooltipContent(key) {
      return tooltipContent[key]
    },
    async getFlowTypeList() {
      const { code, data } = await getFlowTypeList({
        unitCode: this.unitCode,
        config: true,
      })
      if (code == 1) {
        if (data) {
          this.flowTypeList = data
        } else {
          this.flowTypeList = []
        }
      } else {
        this.$message.error('获取流程类型失败')
        this.flowTypeList = []
      }
    },
    async getTrainsetList() {
      const { code, data } = await getTrainsetList()
      if (code == 1) {
        this.allTrainsetList = data
      }
    },
    // async getTrainsetListReceived() {
    //   const { code, data } = await getTrainsetListReceived({ unitCode: this.typeDialogFormData.unitCode });
    //   if (code == 1) {
    //     if (data) {
    //       this.trainsetListOrigin = data;
    //     } else {
    //       this.trainsetListOrigin = [];
    //     }
    //   } else {
    //     this.$message.error('获取车型、批次、车组失败');
    //     this.trainsetList = [];
    //   }
    // },
    headerQueryFormTrainTypeChange() {
      this.headerQueryFormData.trainTemplate = ''
      this.headerQueryFormData.trainsetId = ''
    },
    headerQueryFormTrainTemplateChange(val) {
      if (val) {
        this.headerQueryFormData.trainType = this.trainTempList.find((item) => item.traintempid == val).traintype
      }
      this.headerQueryFormData.trainsetId = ''
    },
    headerQueryFormTrainsetIdChange(val) {
      if (val) {
        this.headerQueryFormData.trainType = this.trainsetList.find((item) => item.trainsetid == val).traintype
        this.headerQueryFormData.trainTemplate = this.trainsetList.find((item) => item.trainsetid == val).traintempid
      }
    },
    async getTaskFlowConfigList(formData = {}) {
      let params = Object.assign(formData, {
        pageSize: this.mainFlowTablePageSize,
        pageNum: this.mainFlowTablePageNum,
      })
      const {
        code,
        data: { total, records },
      } = await getTaskFlowConfigList(params)
      if (code == 1) {
        this.mainFlowTablePageTotal = total
        if (records) {
          let middleData = records.filter((item) => item)
          this.mainFlowTableData = middleData
        } else {
          this.mainFlowTableData = []
        }
      } else {
        this.$message.error('查询作业流程配置失败')
        this.mainFlowTableData = []
      }
    },
    getTaskFlowConfigListBtn() {
      this.getTaskFlowConfigList(this.headerQueryFormData)
    },
    resetHeaderQueryFormDataBtn() {
      this.$refs.headerQueryFormRef.resetFields()
      this.getTaskFlowConfigList()
    },
    mainFlowTablePageSizeChange(pageSize) {
      this.mainFlowTablePageSize = pageSize
      this.getTaskFlowConfigList(this.headerQueryFormData)
    },
    mainFlowTablePageNumChange(pageNum) {
      this.mainFlowTablePageNum = pageNum
      this.getTaskFlowConfigList(this.headerQueryFormData)
    },
    async delTaskFlowConfig(row) {
      const { code, data } = await delTaskFlowConfig(row)
      if (code == 1) {
        if (data) {
          this.$message.success(data)
        } else {
          this.$message.success('删除成功')
        }
      } else {
        this.$message.error('删除失败')
      }
    },
    async deleteTaskFlowConfig(row) {
      // 删除时从后端获取提示
      const { data, code, msg } = await delTaskFlowPrompt({flowId: row.id})
      if(code == 1) {
        let res = await this.$confirm(data, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).catch(() => {
        // this.$message.info('已取消删除');
      })
      if (res == 'confirm') {
        await this.delTaskFlowConfig(row)
        this.getTaskFlowConfigList(this.headerQueryFormData)
      }
      } else {
        this.$message.error(msg);
      }
    },

    async setFlowDefault(id) {
      const { code } = await setFlowDefault({ flowId: id })
      if (code == 1) {
        this.$message.success('默认流程设置成功')
      } else {
        this.$message.error('默认流程设置失败')
      }
    },
    async setFlowDefaultBtn(flowId) {
      let res = await this.$confirm('此操作将该流程设置为默认流程，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).catch(() => {
        // this.$message.info('已取消设置');
      })
      if (res == 'confirm') {
        const {
          code,
          data: { resultInfo },
        } = await setDefaultFlowConfig({ flowId })
        if (code == 1 && resultInfo) {
          this.$message.success(resultInfo)
        }
        this.getTaskFlowConfigList(this.headerQueryFormData)
      }
    },
    async getFlowMarkConfigList() {
      const { code, data } = await getFlowMarkConfigList({ unitCode: this.unitCode })
      if (code == 1) {
        if (data) {
          this.keywordConfigTableData = data
        } else {
          this.keywordConfigTableData = []
        }
      } else {
        this.$message.error('获取关键字失败')
        this.keywordConfigTableData = []
      }
    },
    flowTypeCodeChange(flowTypeCode) {
      this.typeDialogFormData.repairType = ''
      this.typeDialogFormData.keyWords = []
      if (this.typeDialogFormData.flowTypeCode === 'REPAIR_ONE') {
        this.typeDialogFormData.repairType = '1'
      }
      if (flowTypeCode == 'TEMPORARY') {
        this.getFlowMarkConfigList()
      }
    },
    async delFlowMarkConfigByIdBtn(id) {
      let res = await this.$confirm('此操作将永久删除该关键字，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).catch(() => {
        this.$message.info('已取消删除')
      })
      if (res == 'confirm') {
        await this.delFlowMarkConfigById(id)
        this.getFlowMarkConfigList()
      }
    },
    async delFlowMarkConfigById(id) {
      const { code } = await delFlowMarkConfigById({ id })
      if (code == 1) {
        this.$message.success('删除成功')
      } else {
        this.$message.error('删除失败')
      }
    },
    addFlowMarkConfig() {
      this.$refs.keywordConfigForm.validate(async (valid) => {
        if (!valid) return
        const { code, data } = await addFlowMarkConfig({
          ...this.keywordConfigFormData,
          unitCode: this.unitCode,
        })
        if (code == 1) {
          if (data) {
            this.$message.warning(data)
          } else {
            this.$message.success('新增成功')
          }
          this.keywordConfigFormData.mark = ''
          this.getFlowMarkConfigList()
        } else {
          this.$message.error('新增失败')
        }
      })
    },
    async setFlowUsableBtn(flowId) {
      let res = await this.$confirm('此操作将该流程状态设置为发布状态，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).catch(() => {
        this.$message.info('已取消设置')
      })
      if (res == 'confirm') {
        const { code, data, msg } = await setFlowUsable({ flowId })
        if (code == 1) {
          this.$message.success("发布成功")
        } else {
          this.$message.error(msg)
        }
        this.getTaskFlowConfigList(this.headerQueryFormData)
      }
    },
    async getRepairTypes() {
      const { code, data } = await getRepairTypes()
      if (code == 1) {
        if (data) {
          this.repairTypeList = data
        } else {
          this.repairTypeList = []
        }
      } else {
        this.$message.error('获取检修类型失败')
        this.repairTypeList = []
      }
    },

    async initTypeConfigDialog(flowId, state = null) {
      const { data } = await getFlowInfoById({ flowId })
      // 如果是复制到新增就取消发布状态
      if(state && state == "copyToAdd") {
        // 取消state
        this.originalFlowConfigUsable = false
      } else {
        this.originalFlowConfigUsable = data.usable
      }
      
      this.postFilter(data)
      // 初始化表单数据
      for (const key in this.typeDialogFormData) {
        this.typeDialogFormData[key] = data[key]
      }
      if (this.typeDialogFormData.flowTypeCode == 'TEMPORARY') {
        this.getFlowMarkConfigList()
      }
      let flowData = {
        nodes: data.nodes,
        nodeVectors: data.nodeVectors,
        parallelFlowConfigs: data.parallelSections,
        subflowInfoList: data.subflowInfoList,
        pairNodeInfo: data.pairNodeInfo,
      }

      this.flowData = await this.getUpdateIdData(flowData)

      if (this.typeDialogStatus == 'edit') {
        this.flowId = data.id
        this.conditionId = data.conditionId
        this.defaultType = data.defaultType
      } else {
        this.flowId = await this.getNewId()
      }
    },
    async openTypeConfigDialog(status, flowId) {
      this.typeDialogStatus = status
      this.getRepairTypes()
      if (flowId) {
        await this.initTypeConfigDialog(flowId, status)
      } else {
        if (status == "add") {
          this.originalFlowConfigUsable = false
        }
        this.flowId = await this.getNewId()
        if (this.currentSection) {
          this.typeDialogFormData.unitCode = ''
        } else {
          if (this.unitList.length) {
            this.typeDialogFormData.unitCode = this.unitList[0].unitCode
          }
        }
      }
      // await this.getTrainsetListReceived();
      this.typeDialogVisible = true
    },
    async setTaskFlowConfig(dataJson) {
      this.loading = true
      try {
        const { code, data, msg } = await setTaskFlowConfig(dataJson)
        this.loading = false
        if (code == 1) {
          if (data.needCancelUsable) {
            let res = await this.$confirm(data.needCancelUsableMessage, '提示', {
              confirmButtonText: '是',
              cancelButtonText: '否',
              type: 'warning',
            }).catch(() => {
              this.$message.info('已取消')
            })
            if (res == 'confirm') {
              let data = JSON.parse(JSON.stringify(dataJson))
              data["confirmCancelUsable"] = true
              this.setTaskFlowConfig(data)
            }
          } else {
            const { id, resultInfo, warningInfo } = data
            let finalResultInfo = null
            if (resultInfo) {
              finalResultInfo = resultInfo
            } else {
              finalResultInfo = '操作成功'
            }
            if (warningInfo) {
              this.$message.warning(warningInfo)
              setTimeout(() => {
                this.$message.success(finalResultInfo)
              }, 0)
            } else {
              this.$message.success(finalResultInfo)
            }

            this.typeDialogStatus = 'edit'
            this.originalFlowConfigUsable = dataJson.usable
            // 发布no
            // 新增保存
            // 刷新数据
            await this.getTaskFlowConfigList(this.headerQueryFormData)
            this.initTypeConfigDialog(id)
          }
        } else if (code == 0) {
          this.$message.warning(
            {
              dangerouslyUseHTMLString: true,
              message: `<div style="max-height: 100px;overflow-y: auto">${msg}</div>`
            }
          )
        } else {
          this.$message.error(
            {
              dangerouslyUseHTMLString: true,
              message: `<div style="max-height: 100px;overflow-y: auto">${msg}</div>`
            }
          )
        }
      } catch (error) {
        console.error(error);
      } finally {
        this.loading = false
      }
    },

    typeDialogClose() {
      this.contextmenuWrapStatus = false
      this.contextMenuStatus = ''

      this.typeDialogStatus = ''
      this.typeDialogFormData = {
        flowTypeCode: '',
        name: '',
        unitCode: '',
        trainTypeExclude: false,
        trainTemplateExclude: false,
        trainsetIdExclude: false,
        trainTypes: [],
        trainTemplates: [],
        trainsetIds: [],
        keyWords: [],
        repairType: '',
      }
      this.flowId = ''
      this.conditionId = ''
      this.defaultType = ''
      this.flowData = {
        nodes: [],
        nodeVectors: [],
        parallelFlowConfigs: [],
        subflowInfoList: [],
        pairNodeInfo: [],
      }
      this.$refs.typeDialogFormRef.resetFields()
    },

    confirmTypeConfigBtn(usable) {
      if (usable == null) {
        usable = this.originalFlowConfigUsable
      }
      this.$refs.typeDialogFormRef.validate((valid) => {
        if (!valid) return

        if (this.typeDialogFormData.flowTypeCode === 'PLANLESS_KEY' && this.flowData.subflowInfoList.length > 0) {
          return this.$message.warning('关键作业禁止配置子流程')
        }
        // 除了成对节点后一个是否都有角色信息
        let workPepairFlowInstance = new Vue(window.WorkRepairFlowComponentConfig)
        workPepairFlowInstance.data = copyDataSimple(this.flowData)

        let warnNode = workPepairFlowInstance.data.nodes.find((node) => {
          return node.roleConfigs.length === 0 && !workPepairFlowInstance.pairNodeEndIds.includes(node.id)
        })

        if (warnNode) {
          return this.$message.warning(`请给节点 ${warnNode.name} 配置角色`)
        }

        if (this.typeDialogFormData.flowTypeCode === 'REPAIR_ONE_AND_TWO') {
          this.typeDialogFormData.repairType = '1'
        }
        let data = {
          id: this.flowId,
          conditionId: this.conditionId,
          defaultType: this.defaultType,
          usable,
          ...this.typeDialogFormData,
          ...{
            nodes: this.flowData.nodes,
            nodeVectors: this.flowData.nodeVectors,
            parallelSections: this.flowData.parallelFlowConfigs,
            subflowInfoList: this.flowData.subflowInfoList,
            pairNodeInfo: this.flowData.pairNodeInfo,
          },
        }

        this.setTaskFlowConfig(data)

        // this.typeDialogVisible = false;
      })
    },

    openkeywordConfigDialog() {
      this.keywordConfigDialogVisible = true
    },

    keywordConfigDialogClose() { },
    
    openTransferDialog(type) {
      this.transferType = type
      if (type == 'trainTypes') {
        this.transferValue = this.typeDialogFormData.trainTypes
        this.transferList = this.transferTrainTypeList
        this.transferConfig = { key: 'traintype', label: 'traintype' }
      } else if (type == 'trainTemplates') {
        this.transferValue = this.typeDialogFormData.trainTemplates
        this.transferList = this.transferTrainTempList
        this.transferConfig = { key: 'traintempid', label: 'traintempid' }
      } else if (type == 'trainsetIds') {
        this.transferValue = this.typeDialogFormData.trainsetIds
        this.transferList = this.transferTrainsetList
        this.transferConfig = { key: 'trainsetid', label: 'trainsetname' }
      } else {
        this.transferValue = this.typeDialogFormData.keyWords
        this.transferList = this.keywordConfigTableData
        this.transferConfig = { key: 'mark', label: 'mark' }
      }
      this.transferDialogVisible = true
    },
    confirmTransferBtn() {
      if (this.transferType == 'trainTypes') {
        this.typeDialogFormData.trainTypes = this.transferValue

        this.typeDialogFormData.trainTemplates = []
        this.typeDialogFormData.trainsetIds = []

        this.$refs.typeDialogFormRef.validateField('trainTypes')
      } else if (this.transferType == 'trainTemplates') {
        this.typeDialogFormData.trainTemplates = this.transferValue

        if (!this.typeDialogFormData.trainTemplateExclude) {
          // 包含批次
          if (this.typeDialogFormData.trainTemplates.length > 0 && this.typeDialogFormData.trainTypes.length == 0) {
            this.typeDialogFormData.trainTypeExclude = false
            this.typeDialogFormData.trainTypes = [
              ...new Set(
                this.transferTrainTempList
                  .map((item) => {
                    if (this.typeDialogFormData.trainTemplates.includes(item.traintempid)) {
                      return item.traintype
                    }
                  })
                  .filter((item) => item)
              ),
            ]
            // if (!this.typeDialogFormData.trainTypeExclude) {
            //   // 包含车型
            //   this.typeDialogFormData.trainTypes = [
            //     ...new Set(
            //       this.transferTrainTempList
            //         .map((item) => {
            //           if (this.typeDialogFormData.trainTemplates.includes(item.traintempid)) {
            //             return item.traintype;
            //           }
            //         })
            //         .filter((item) => item)
            //     ),
            //   ];
            // } else {
            //   // 排除车型
            //   this.typeDialogFormData.trainTypes = [
            //     ...new Set(
            //       this.transferTrainTempList
            //         .map((item) => {
            //           if (!this.typeDialogFormData.trainTemplates.includes(item.traintempid)) {
            //             return item.traintype;
            //           }
            //         })
            //         .filter((item) => item)
            //     ),
            //   ];
            // }
          }
        } else {
          // 排除批次
          if (this.typeDialogFormData.trainTemplates.length > 0 && this.typeDialogFormData.trainTypes.length == 0) {
            this.typeDialogFormData.trainTypeExclude = false

            // if (!this.typeDialogFormData.trainTypeExclude) {
            //   // 包含车型
            //   this.typeDialogFormData.trainTypes = [
            //     ...new Set(
            //       this.transferTrainTempList
            //         .map((item) => {
            //           if (!this.typeDialogFormData.trainTemplates.includes(item.traintempid)) {
            //             return item.traintype;
            //           }
            //         })
            //         .filter((item) => item)
            //     ),
            //   ];
            // } else {
            //   // 排除车型
            //   this.typeDialogFormData.trainTypes = [
            //     ...new Set(
            //       this.transferTrainTempList
            //         .map((item) => {
            //           if (this.typeDialogFormData.trainTemplates.includes(item.traintempid)) {
            //             return item.traintype;
            //           }
            //         })
            //         .filter((item) => item)
            //     ),
            //   ];
            // }
          }
        }

        this.typeDialogFormData.trainsetIds = []

        this.$refs.typeDialogFormRef.validateField('trainTemplates')
      } else if (this.transferType == 'trainsetIds') {
        this.typeDialogFormData.trainsetIds = this.transferValue
        if (!this.typeDialogFormData.trainsetIdExclude) {
          // 包含

          if (this.typeDialogFormData.trainsetIds.length > 0 && this.typeDialogFormData.trainTypes.length == 0) {
            this.typeDialogFormData.trainTypeExclude = false
            this.typeDialogFormData.trainTypes = [
              ...new Set(
                this.transferTrainsetList
                  .map((item) => {
                    if (this.typeDialogFormData.trainsetIds.includes(item.trainsetid)) {
                      return item.traintype
                    }
                  })
                  .filter((item) => item)
              ),
            ]
          }
          if (this.typeDialogFormData.trainsetIds.length > 0 && this.typeDialogFormData.trainTemplates.length == 0) {
            this.typeDialogFormData.trainTemplateExclude = false
            this.typeDialogFormData.trainTemplates = [
              ...new Set(
                this.transferTrainsetList
                  .map((item) => {
                    if (this.typeDialogFormData.trainsetIds.includes(item.trainsetid)) {
                      return item.traintempid
                    }
                  })
                  .filter((item) => item)
              ),
            ]
          }
        } else {
          // 排除
          if (this.typeDialogFormData.trainsetIds.length > 0 && this.typeDialogFormData.trainTypes.length == 0) {
            this.typeDialogFormData.trainTypeExclude = false
          }

          if (this.typeDialogFormData.trainsetIds.length > 0 && this.typeDialogFormData.trainTemplates.length == 0) {
            this.typeDialogFormData.trainTemplateExclude = false
          }
        }

        this.$refs.typeDialogFormRef.validateField('trainTypes')
      } else {
        this.typeDialogFormData.keyWords = this.transferValue
      }
      this.transferDialogVisible = false
    },
    transferDialogClose() {
      this.$refs.transfer.clearQuery('left')
      this.$refs.transfer.clearQuery('right')
    },
    parentFlowContainerContextMenu() {
      this.contextMenuStatus = 'parentContextMenu'
    },
    childFlowContainerContextMenu() {
      this.contextMenuStatus = 'childContextMenu'
    },
    // 右键菜单
    select(item) {
      this.contextmenuWrapStatus = false

      this.nowContentMenuStatus = item.value
      if (item.action) {
        this[item.action](item)
      } else {
        this[item.value](item)
      }
      // this.workersTreeCheckedNodes = [];
    },
    contextMenu({ e, nodeId }) {
      if (this.typeDialogStatus == 'look') return
      this.nowNodeId = nodeId
      this.axis = {
        x: e.x,
        y: e.y,
      }
      this.nowContentMenuStatus = ''
      this.contextmenuWrapStatus = true
    },
    flowContainerClick() {
      this.contextmenuWrapStatus = false
      this.contextMenuStatus = ''
    },
    // 配置节点信息和成对节点信息
    configNode(nodeInfo, flowData) {
      return new Promise((resolve, reject) => {
        this.$nextTick(() => {
          this.roleConfigsNum = {}
          nodeInfo.roleConfigs.forEach((roleConfig) => {
            this.$set(this.roleConfigsNum, roleConfig.roleId, roleConfig.minNum)
          })

          if (!nodeInfo.remindRoles) {
            nodeInfo.remindRoles = []
          }

          this.nodeConfigFormData = copyDataSimple(nodeInfo)

          // this.nodeConfigFormData.roleConfigs = Object.keys(this.roleConfigsNum);
          this.nodeConfigFormData.excludeRoles = this.nodeConfigFormData.excludeRoles.map(
            (excludeRole) => excludeRole.roleId
          )
          this.nodeConfigFormData.remindRoles = this.nodeConfigFormData.remindRoles.map(
            (remindRole) => remindRole.roleId
          )
          this.$set(this.nodeConfigFormData, 'roleConfigs', Object.keys(this.roleConfigsNum))
        })

        this.nodeConfigPairNodeInfo = copyDataSimple(flowData.pairNodeInfo)
        let WorkRepairFlowConstructor = Vue.extend(WorkRepairFlowComponentConfig)
        let workPepairFlowInstance = new WorkRepairFlowConstructor()
        workPepairFlowInstance.data = copyDataSimple(flowData)

        this.nodeConfigTempWorkPepairFlowInstance = workPepairFlowInstance

        this.selectPairEndNode = workPepairFlowInstance.pairNodeEndIds.includes(nodeInfo.id)

        this.nodeConfigDialogVisible = true

        this.getNodeTaskFlowConfigList()

        this.$nextTick(() => {
          let okFlag = false

          let nodeConfigDialogConfirmBtnRef = this.$refs.nodeConfigDialogConfirmBtn
          let onConfirm = () => {
            this.nodeConfigFormData.remark = this.$refs.wangEditor.editor.txt.html();
            this.$refs.nodeConfigFormRef.validate(async (valid) => {
              if (valid) {
                okFlag = true
                this.nodeConfigDialogVisible = false
              }
            })
          }
          nodeConfigDialogConfirmBtnRef.$on("click", onConfirm)

          let unwatch = this.$watch("nodeConfigDialogVisible", (value) => {
            if(!value){
              nodeConfigDialogConfirmBtnRef.$off("click", onConfirm)
              unwatch()
            }
          })

          let nodeConfigDialogRef = this.$refs.nodeConfigDialog
          nodeConfigDialogRef.$once("close", () => {
            if (okFlag) {

              let resultNodeInfo = copyDataSimple(this.nodeConfigFormData)
              this.dealNodeInfo(resultNodeInfo)

              let newPairNodeInfo = copyDataSimple(this.nodeConfigPairNodeInfo)
              resolve({
                nodeInfo: resultNodeInfo,
                extraFlowDataSetFn: (flowData) => {
                  if (JSON.stringify(flowData.pairNodeInfo) != JSON.stringify(newPairNodeInfo)) {
                    this.$set(flowData, "pairNodeInfo", newPairNodeInfo)

                    let tempWorkPepairFlowInstance = new WorkRepairFlowConstructor()
                    tempWorkPepairFlowInstance.data = copyDataSimple(flowData)
                    let currentNodeId = resultNodeInfo.id
                    // 如果当前节点是成对节点
                    let pairNodeItemId = tempWorkPepairFlowInstance.pairNodeIdMap[currentNodeId]
                    if (pairNodeItemId) {
                      let pairNodeItem = tempWorkPepairFlowInstance.pairNodeInfoSorted.find(v => v.id == pairNodeItemId)
                      let copyPairNodePropertiesTo = (fromNode, toNode) => {
                        let properties = ["skip", "roleConfigs", "disposeAfterSkip", "excludeRoles", "minDisposeNum"]
                        properties.forEach(property => {
                          this.$set(toNode, property, copyDataSimple(fromNode[property]))
                        })
                      }

                      // 如果当前节点是前一个节点，设置后一个节点的属性
                      if (pairNodeItem.nodeIds[0] == currentNodeId) {
                        let afterNodeId = tempWorkPepairFlowInstance.pairNodeIdRelationMap[currentNodeId]
                        copyPairNodePropertiesTo(
                          tempWorkPepairFlowInstance.allNodesMap[currentNodeId],
                          flowData.nodes.find(v => v.id == afterNodeId)
                        )
                      } else {
                        // 如果当前节点是后一个节点，设置当前节点的属性
                        let preNodeId = tempWorkPepairFlowInstance.pairNodeIdRelationMap[currentNodeId]
                        copyPairNodePropertiesTo(
                          tempWorkPepairFlowInstance.allNodesMap[preNodeId],
                          flowData.nodes.find(v => v.id == currentNodeId)
                        )
                      }

                    }
                  }

                },
              })
            } else {
              reject()
            }
            this.nodeConfigTempWorkPepairFlowInstance = null
            this.nodeConfigPairNodeInfo = null
            // this.$refs.nodeConfigFormRef.resetFields();
          })

        })
      })
    },
    async getNodeTaskFlowConfigList() {
      const { code, data } = await getNodeTaskFlowConfigList()
      if (code == 1) {
        if (data) {
          this.nodeTaskFlowConfigList = data
        } else {
          this.nodeTaskFlowConfigList = []
        }
      } else {
        this.$message.error('查询节点业务类型失败')
        this.nodeTaskFlowConfigList = []
      }
    },
    // 成对节点change
    async pairChange(targetNodeId) {
      let editingNodeId
      if (this.nowContentMenuStatus === 'nodeConfig') {
        editingNodeId = this.nowNodeId
      } else {
        editingNodeId = this.newId
      }

      const pairNodeIdMap = this.nodeConfigTempWorkPepairFlowInstance.pairNodeIdMap

      let oriPairNodeInfo = this.nodeConfigTempWorkPepairFlowInstance.data.pairNodeInfo
      let newPairNodeInfo = copyDataSimple(oriPairNodeInfo) || []

      // 判断是否目标节点已经是成对节点
      if (pairNodeIdMap[targetNodeId]) {
        // 先拆在添
        newPairNodeInfo = newPairNodeInfo.filter((item) => item.id != pairNodeIdMap[targetNodeId])
      }
      // 判断他自身是否是成对节点
      if (pairNodeIdMap[editingNodeId]) {
        // 是，先拆再添
        newPairNodeInfo = newPairNodeInfo.filter((item) => item.id != pairNodeIdMap[editingNodeId])
      }

      const trunkFlowNodeIdListSorted = this.nodeConfigTempWorkPepairFlowInstance.trunkFlowNodeIdListSorted
      // 判断当前节点是否是成对后一个节点
      if (!targetNodeId) {
        this.selectPairEndNode = false
      } else {
        // 如果当前配置的节点是后一个成对节点，如果是，则隐藏部分信息
        // 复制主干节点id列表
        let newTrunkNodeIds = copyDataSimple(trunkFlowNodeIdListSorted)
        // 如果是新增节点，则将新节点id临时插入主干节点id列表
        if (this.nowContentMenuStatus === 'addPrevNode') {
          let nowNo = newTrunkNodeIds.findIndex((nodeId) => nodeId == this.nowNodeId)
          newTrunkNodeIds.splice(nowNo, 0, this.newId)
        } else if (this.nowContentMenuStatus === 'addNextNode') {
          let nowNo = newTrunkNodeIds.findIndex((nodeId) => nodeId == this.nowNodeId)
          newTrunkNodeIds.splice(nowNo + 1, 0, this.newId)
        }
        let currentPairNodeIds = [editingNodeId, targetNodeId]
        // console.log(nodeIds);
        // 验证会不会形成交叉成对节点
        const newPairNodeIndexA = newTrunkNodeIds.findIndex((nodeId) => nodeId == currentPairNodeIds[0])
        const newPairNodeIndexB = newTrunkNodeIds.findIndex((nodeId) => nodeId == currentPairNodeIds[1])
        let isOpenBetween = (value, a, b) => {
          if (a > b) {
            [a, b] = [b, a]
          }
          return value > a && value < b
        }
        // 如果存在交叉成对节点现象
        if (
          newPairNodeInfo.some((piarNode) => {
            const itemPairNodeIndexA = newTrunkNodeIds.findIndex((nodeId) => nodeId == piarNode.nodeIds[0])
            const itemPairNodeIndexB = newTrunkNodeIds.findIndex((nodeId) => nodeId == piarNode.nodeIds[1])
            let curPairNodeIndexList = [itemPairNodeIndexA, itemPairNodeIndexB]
            // 如果一个节点在当前成对节点范围(开集)内，一个在成对节点范围(开集)外，则此成对节点和当前成对节点存在交叉现象
            return (
              curPairNodeIndexList.some((v) => isOpenBetween(v, newPairNodeIndexA, newPairNodeIndexB)) &&
              curPairNodeIndexList.some((v) => !isOpenBetween(v, newPairNodeIndexA, newPairNodeIndexB))
            )
          })
        ) {
          this.$message.warning('产生了交叉成对节点')
          return
        }

        // 判断是否目标节点已经是成对节点
        if (pairNodeIdMap[targetNodeId]) {
          try {
            await this.$confirm('目标节点已经是成对节点，是否断开连接？', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            })
          } catch (error) {
            // this.$message.info('已取消')
            return
          }
        }

        // 直接添加
        let id = await this.getNewId()
        newPairNodeInfo.push({
          id,
          nodeIds: currentPairNodeIds,
        })
        // 找到当前节点与成对节点的位置，判断其相对位置，从而获知当前节点是否是成对后一个节点
        let editNo = newTrunkNodeIds.findIndex((nodeId) => nodeId == editingNodeId)
        let targetNo = newTrunkNodeIds.findIndex((nodeId) => nodeId == targetNodeId)
        if (editNo > targetNo) {
          this.selectPairEndNode = true
        } else {
          this.selectPairEndNode = false
        }
      }

      // 设置数据
      this.$set(this.nodeConfigFormData, 'pair', targetNodeId)
      this.nodeConfigPairNodeInfo = newPairNodeInfo
    },
    // 获取右键节点的上一个节点
    getPrevNode(nodeId) {
      const { colNum, rowNum } = this.curWorkRepairFlowRef.getNodePostion(nodeId)
      return this.curWorkRepairFlowRef.nodesContext.main.nodeInstanceMap[
        this.curWorkRepairFlowRef.nodesContext.main.getPosId(colNum, rowNum - 1)
      ]
    },
    // 获取右键节点的下一个节点
    getNextNode(nodeId) {
      const { colNum, rowNum } = this.curWorkRepairFlowRef.getNodePostion(nodeId)
      return this.curWorkRepairFlowRef.nodesContext.main.nodeInstanceMap[
        this.curWorkRepairFlowRef.nodesContext.main.getPosId(colNum, rowNum + 1)
      ]
    },
    // 判断是不是虚拟节点
    isVirtualNode(nodeId) {
      return this.curWorkRepairFlowRef.virtualNodes.some((item) => item.id == nodeId)
    },
    async getNewId(number = 1) {
      const { data } = await getNewId({ number })
      return data[0]
    },
    async getNewIds(number = 1) {
      const { data } = await getNewId({ number })
      return data
    },

    async getNewParallelFlowConfigs(nowNodeId, newNodeId) {
      const id = await this.getNewId()
      return {
        id,
        nodeIds: [newNodeId],
        sort: 0,
      }
    },
    dealNodeInfo(nodeInfo) {
      nodeInfo.roleConfigs = nodeInfo.roleConfigs.map((roleConfig) => {
        return {
          minNum: this.roleConfigsNum[roleConfig] || 0,
          roleId: roleConfig,
          type: this.roleCodeTypeMap[roleConfig],
        }
      })
      nodeInfo.excludeRoles = nodeInfo.excludeRoles.map((excludeRole) => {
        return {
          minNum: this.roleConfigsNum[excludeRole] || 0,
          roleId: excludeRole,
          type: this.roleCodeTypeMap[excludeRole],
        }
      })
      nodeInfo.remindRoles = nodeInfo.remindRoles.map((remindRole) => {
        return {
          minNum: this.roleConfigsNum[remindRole] || 0,
          roleId: remindRole,
          type: this.roleCodeTypeMap[remindRole],
        }
      })
      return nodeInfo
    },
    async addPrevNode() {
      this.selfNodeConfig = false
      // const id = await this.getNewId();
      this.newId = await this.getNewId()
      this.nodeConfigFormData = getNodeBaseFormData()
      this.configNode({ id: this.newId, ...this.nodeConfigFormData }, this.curWorkRepairFlowRef.data).then(
        ({ nodeInfo, extraFlowDataSetFn }) => {
          this.curWorkRepairFlowRef.addPrevNode(nodeInfo, this.nowNodeId, extraFlowDataSetFn)
        }
      )
    },
    async addNextNode() {
      this.selfNodeConfig = false
      // const id = await this.getNewId();
      this.newId = await this.getNewId()
      this.nodeConfigFormData = getNodeBaseFormData()
      this.configNode({ id: this.newId, ...this.nodeConfigFormData }, this.curWorkRepairFlowRef.data).then(
        ({ nodeInfo, extraFlowDataSetFn }) => {
          this.curWorkRepairFlowRef.addNextNode(nodeInfo, this.nowNodeId, extraFlowDataSetFn)
        }
      )
    },
    async deleteNode() {
      let res = await this.$confirm('此操作将永久删除该节点，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).catch(() => {
        this.$message.info('已取消删除')
      })
      if (res == 'confirm') {
        this.curWorkRepairFlowRef.deleteNode(this.nowNodeId)
      }
    },
    selectVectorForMoveNode() {
      return new Promise((resolve, reject) => {
        let pressESC = (e) => {
          if (e.keyCode == 27) {
            this.selectVectorObj.rejectSelectVector()
          }
        }

        document.body.addEventListener('keydown', pressESC)
        let end = () => {
          this.selectVectorStatus = {
            child: false,
            parent: false,
          }
          document.body.removeEventListener('keydown', pressESC)
        }
        this.selectVectorObj.resolveSelectVector = (nodeVector) => {
          end()
          resolve(nodeVector)
        }
        this.selectVectorObj.rejectSelectVector = () => {
          end()
          reject()
        }
      })
    },
    moveNodeTo() {
      if (this.contextMenuStatus == 'childContextMenu') {
        this.selectVectorStatus.child = true
      } else {
        this.selectVectorStatus.parent = true
      }
      this.selectVectorForMoveNode().then((targetNodeVector) => {
        this.curWorkRepairFlowRef.moveNodeTo(this.nowNodeId, targetNodeVector)
      })
    },
    hasSelect(targetNodeVector) {
      // 移动成对节点时的约束：
      // 1、成对节点不能跨并行区段/流程主干
      if (
        this.curWorkRepairFlowRef.pairNodeIdMap[this.nowNodeId] &&
        (!this.curWorkRepairFlowRef.isTrunkNode(targetNodeVector.fromNodeId) ||
          !this.curWorkRepairFlowRef.isTrunkNode(targetNodeVector.toNodeId))
      ) {
        this.$message.warning('成对节点不能跨并行区段/流程主干')
        return
      }
      // 2、成对节点的先后关系不能改变
      if (
        this.curWorkRepairFlowRef.isTrunkNode(this.nowNodeId) &&
        this.curWorkRepairFlowRef.isTrunkNode(targetNodeVector.fromNodeId) &&
        this.curWorkRepairFlowRef.isTrunkNode(targetNodeVector.toNodeId)
      ) {
        if (this.curWorkRepairFlowRef.pairNodeIdRelationMap[this.nowNodeId]) {
          let startIndex = this.curWorkRepairFlowRef.trunkFlowNodeIdListSorted.findIndex(
            (nodeId) => nodeId == this.nowNodeId
          )
          let endIndex =
            this.curWorkRepairFlowRef.trunkFlowNodeIdListSorted.findIndex(
              (nodeId) => nodeId == targetNodeVector.fromNodeId
            ) + 1
          let targetIndex = this.curWorkRepairFlowRef.trunkFlowNodeIdListSorted.findIndex(
            (nodeId) => nodeId == this.curWorkRepairFlowRef.pairNodeIdRelationMap[this.nowNodeId]
          )
          if (startIndex < targetIndex && endIndex > targetIndex) {
            this.$message.warning('成对节点的先后关系不能改变')
            return
          } else if (startIndex > targetIndex && endIndex <= targetIndex) {
            this.$message.warning('成对节点的先后关系不能改变')
            return
          }
        }
      }
      this.selectVectorObj.resolveSelectVector(targetNodeVector)
    },
    nodeConfig() {
      this.selfNodeConfig = true

      let nowNodeInfo = this.curWorkRepairFlowRef.data.nodes.find((node) => node.id == this.nowNodeId)
      nowNodeInfo.pair = this.curWorkRepairFlowRef.pairNodeIdRelationMap[this.nowNodeId] || ''
      return this.configNode(nowNodeInfo, this.curWorkRepairFlowRef.data).then(({ nodeInfo, extraFlowDataSetFn }) => {
        let index = this.curWorkRepairFlowRef.data.nodes.findIndex((v) => v.id == nodeInfo.id)
        if (index != -1) {
          this.curWorkRepairFlowRef.data.nodes.splice(index, 1, nodeInfo)
        } else {
          throw '程序异常，请检查'
        }
        extraFlowDataSetFn(this.curWorkRepairFlowRef.data)
      })
    },
    configChildFlow() {
      this.pFlowId = this.nowNodeId
      this.childFlowId = this.flowData.nodes.find((node) => node.id == this.nowNodeId).childFlowId
      if (this.subFlowInfoMap[this.childFlowId]) {
        this.subFlowData = this.subFlowInfoMap[this.childFlowId]
        this.prevChildFlowData = copyDataSimple(this.subFlowData)
      } else {
        this.subFlowData = {
          nodes: [],
          nodeVectors: [],
          parallelFlowConfigs: [],
          pairNodeInfo: [],
        }
      }

      this.subFlowConfigurating = true
      this.childFlowDialog.childFlowDialogVisible = true
    },
    async deleteChildFlow() {
      let res = await this.$confirm('此操作将永久删除该子流程，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).catch(() => {
        this.$message.info('已取消删除')
      })
      if (res == 'confirm') {
        this.pFlowId = this.nowNodeId
        this.curWorkRepairFlowRef.deleteChildFlow(this.pFlowId)
      }
    },
    async addParall() {
      const id = await this.getNewId()
      this.nodeConfigFormData = getNodeBaseFormData()
      this.configNode({ id, ...this.nodeConfigFormData }, this.curWorkRepairFlowRef.data).then(
        async ({ nodeInfo, extraFlowDataSetFn }) => {
          const newParallelFlowConfigs = await this.getNewParallelFlowConfigs(this.nowNodeId, nodeInfo.id)
          this.curWorkRepairFlowRef.addParall(nodeInfo, this.nowNodeId, newParallelFlowConfigs)
        }
      )
    },
    getNowParallConfig() {
      return this.curWorkRepairFlowRef.getNowParallConfig(this.nowNodeId)
    },
    getTrunkList() {
      this.trunkNodeList = this.curWorkRepairFlowRef.getTrunkList()
    },
    configParall() {
      return new Promise((resolve, reject) => {
        this.getTrunkList()
        this.openParallelConfigDialog(this.getNowParallConfig())
        this.parallelConfigDialog.resolveParallelConfigDialog = () => {
          resolve(copyDataSimple(this.parallelConfigFormData))
        }
        this.parallelConfigDialog.rejectParallelConfigDialog = reject
      })
    },
    parallConfig() {
      this.selfNodeConfig = false
      this.configParall().then((copyData) => {
        this.curWorkRepairFlowRef.parallConfig(copyData.trunkStartNodeId, copyData.trunkEndNodeId, this.nowNodeId)
      })
    },
    async getFlowTemplateList() {
      const {
        code,
        data: { total, records },
      } = await getTaskFlowConfigList({
        flowTypeCode: 'TEMPLATE',
        ...this.flowTempalteDialog.flowTempalteFormData,
        pageNum: this.flowTempalteDialog.flowTempaltePageNumber,
        pageSize: this.flowTempalteDialog.flowTempaltePageSize,
      })
      if (code == 1) {
        this.flowTempalteDialog.flowTempalteTotal = total
        if (records) {
          this.flowTempalteDialog.flowTempalteTableData = records
        } else {
          this.flowTempalteDialog.flowTempalteTableData = []
        }
      } else {
        this.$message.error('获取流程模板失败')
        this.flowTempalteDialog.flowTempalteTableData = []
      }
    },
    addFlowTemplate() {
      this.getFlowTemplateList()
      this.flowTempalteDialog.flowTempalteDialogVisible = true
    },

    async getIdMap(list) {
      const idNum = list.length
      if (idNum == 0) return {}
      const idArr = await this.getNewIds(idNum)
      let idMap = {}
      list.forEach((item, index) => {
        idMap[item.id] = idArr[index]
      })
      return idMap
    },

    async getUpdateIdData(flowData) {
      const nodeIdMap = await this.getIdMap(flowData.nodes)
      const subFlowIdMap = await this.getIdMap(flowData.subflowInfoList)
      const nodeVectorIdMap = await this.getIdMap(flowData.nodeVectors)
      const parallelFlowConfigsIdMap = await this.getIdMap(flowData.parallelFlowConfigs)
      const pairNodeInfoIdMap = await this.getIdMap(flowData.pairNodeInfo)

      let updateNodes = copyDataSimple(flowData.nodes).map((item) => {
        item.flowId = ''
        item.id = nodeIdMap[item.id]
        item.childFlowId = subFlowIdMap[item.childFlowId] || ''

        return item
      })
      let updateSubFlow = await Promise.all(
        copyDataSimple(flowData.subflowInfoList).map(async (item) => {
          item.id = subFlowIdMap[item.id]
          const itemNodeIdMap = await this.getIdMap(item.nodes)
          const itemNodeVectorIdMap = await this.getIdMap(item.nodeVectors)
          item.nodes = copyDataSimple(item.nodes).map((item) => {
            item.flowId = ''
            item.id = itemNodeIdMap[item.id]
            return item
          })
          item.nodeVectors = copyDataSimple(item.nodeVectors).map((item) => {
            item.flowId = ''
            item.id = itemNodeVectorIdMap[item.id]
            item.fromNodeId = itemNodeIdMap[item.fromNodeId]
            item.toNodeId = itemNodeIdMap[item.toNodeId]
            return item
          })
          return item
        })
      )

      let updateNodeVectors = copyDataSimple(flowData.nodeVectors).map((item) => {
        item.flowId = ''
        item.id = nodeVectorIdMap[item.id]
        item.fromNodeId = nodeIdMap[item.fromNodeId]
        item.toNodeId = nodeIdMap[item.toNodeId]
        return item
      })
      let updateParallelFlowConfigs = copyDataSimple(flowData.parallelFlowConfigs).map((item) => {
        item.id = parallelFlowConfigsIdMap[item.id]
        item.nodeIds = item.nodeIds.map((nodeId) => {
          return nodeIdMap[nodeId]
        })
        return item
      })
      let updatePairNodeInfo = copyDataSimple(flowData.pairNodeInfo).map((item) => {
        item.id = pairNodeInfoIdMap[item.id]
        item.nodeIds = item.nodeIds.map((nodeId) => {
          return nodeIdMap[nodeId]
        })
        return item
      })
      return {
        nodes: updateNodes,
        subflowInfoList: updateSubFlow,
        nodeVectors: updateNodeVectors,
        parallelFlowConfigs: updateParallelFlowConfigs,
        pairNodeInfo: updatePairNodeInfo,
      }
    },
    async insertFlowTemplate(row) {
      const data = row
      const insertFlowData = {
        nodes: data.nodes,
        nodeVectors: data.nodeVectors,
        parallelFlowConfigs: data.parallelSections,
        subflowInfoList: data.subflowInfoList,
        pairNodeInfo: data.pairNodeInfo,
      }
      // 1、要改变模板的所有id，并要保持数据之间的关系

      const updateIdData = await this.getUpdateIdData(insertFlowData)

      // 2、在nowNode后插入该模板（并行区段插入时：1、不能插入带有成对节点的主干。2、含有并行区段的流程）

      if (this.contextMenuStatus == 'childContextMenu') {
        if (updateIdData.pairNodeInfo.length > 0) {
          this.$message.warning('子流程不能插入带有成对节点的流程模板')
          return
        }
        if (updateIdData.parallelFlowConfigs.length > 0) {
          this.$message.warning('子流程不能插入含有并行区段的流程模板')
          return
        }
      } else {
        if (!this.curWorkRepairFlowRef.isTrunkNode(this.nowNodeId)) {
          if (updateIdData.pairNodeInfo.length > 0) {
            this.$message.warning('并行区段上的节点不能插入带有成对节点的流程模板')
            return
          }
          if (updateIdData.parallelFlowConfigs.length > 0) {
            this.$message.warning('并行区段上的节点不能插入含有并行区段的流程模板')
            return
          }
        }
      }
      this.curWorkRepairFlowRef.addFlowTemplate(this.nowNodeId, updateIdData)

      this.flowTempalteDialog.flowTempalteDialogVisible = false
    },
    openParallelConfigDialog(parallelConfig) {
      this.parallelConfigDialogVisible = true
      this.$nextTick(() => {
        this.parallelConfigFormData = parallelConfig
      })
    },
    confirmParallelConfigBtn() {
      this.$refs.parallelConfigFormRef.validate((valid) => {
        if (!valid) return
        if (
          this.curWorkRepairFlowRef.validateParallConfig(
            copyDataSimple(this.parallelConfigFormData).trunkStartNodeId,
            copyDataSimple(this.parallelConfigFormData).trunkEndNodeId,
            this.nowNodeId
          )
        ) {
          this.parallelConfigDialog.ok = true
          this.parallelConfigDialogVisible = false
        } else {
          this.$message.warning('并行区段不允许互相交叉！')
        }
      })
    },
    parallelConfigDialogClose() {
      if (this.parallelConfigDialog.ok) {
        this.parallelConfigDialog.resolveParallelConfigDialog()
      } else {
        this.parallelConfigDialog.rejectParallelConfigDialog()
      }
      this.$refs.parallelConfigFormRef.resetFields()
      this.parallelConfigDialog.ok = null
    },
    // 流程模板
    getFlowTemplateListBtn() {
      this.getFlowTemplateList()
    },

    flowTempalteDialogClose() {
      this.$refs.flowTempalteFormRef.resetFields()
    },
    flowTempaltePageSizeChange(pageSize) {
      this.flowTempalteDialog.flowTempaltePageSize = pageSize
      this.getFlowTemplateList()
    },
    flowTempaltePageNumberChange(pageNum) {
      this.flowTempalteDialog.flowTempaltePageNumber = pageNum
      this.getFlowTemplateList()
    },

    // 配置子流程
    childFlowDialogClose() {
      this.contextmenuWrapStatus = false
      this.contextMenuStatus = ''
      this.subFlowConfigurating = false
      this.subFlowData = {
        nodes: [],
        nodeVectors: [],
        parallelFlowConfigs: [],
        pairNodeInfo: [],
      }
      this.prevChildFlowData = {}
    },
    confirmConfigSubFlowBtn() {
      let nowNode = this.flowData.nodes.find((node) => node.id == this.pFlowId)
      let nowNodeIndex = this.flowData.nodes.findIndex((node) => node.id == this.pFlowId)
      nowNode.childFlowId = this.childFlowId
      this.$set(this.flowData.nodes, nowNodeIndex, nowNode)
      this.childFlowDialog.childFlowDialogVisible = false
    },
    beforeConfigSubFlowDialogClose(done) {
      this.cancelConfigSubFlowBtn()
      done()
    },
    cancelConfigSubFlowBtn() {
      if (this.prevChildFlowData.id) {
        this.prevChildFlowData.id = this.childFlowId
        this.flowData.subflowInfoList = this.flowData.subflowInfoList.filter(
          (subFlow) => subFlow.id != this.childFlowId
        )
        this.flowData.subflowInfoList.push(copyDataSimple(this.prevChildFlowData))
      } else {
        if (this.subFlowData.id) {
          this.flowData.subflowInfoList = this.flowData.subflowInfoList.filter(
            (subFlow) => subFlow.id != this.subFlowData.id
          )
        }
      }
      this.childFlowDialog.childFlowDialogVisible = false
    },
    async subFlowDataChange(val) {
      let copyData = copyDataSimple(val)
      copyData.flowTypeCode = 'SUBFLOW'
      if (this.childFlowId) {
        this.subFlowData = val
        // 存在流程，替换
        this.flowData.subflowInfoList = this.flowData.subflowInfoList.filter(
          (subFlow) => subFlow.id != this.childFlowId
        )
        this.flowData.subflowInfoList.push(copyData)
      } else {
        this.subFlowData = val
        const id = await this.getNewId()
        // 不存在添加
        this.childFlowId = id
        this.subFlowData.id = this.childFlowId
        copyData.id = this.childFlowId

        this.flowData.subflowInfoList.push(copyData)
      }
    },

    flowDataChange(data) {
      this.flowData = data
    },
  },
  filters: {
    booToTextFilter(val) {
      if (val) {
        return '是'
      } else if (val === null) {
        return ''
      } else {
        return '否'
      }
    },
    repairTypeFilter(val, that) {
      return that.flowTypeMap[val] || ''
    },
  },
})
