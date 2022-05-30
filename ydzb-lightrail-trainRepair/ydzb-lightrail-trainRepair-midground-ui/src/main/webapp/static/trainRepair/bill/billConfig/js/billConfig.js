var ctxPath = "/trainRepair"
{
  /* <script src="${ctxPath}/static/js/localStorageParamsBus.js"></script> */
}
const ManageTypeEnum = {
  SYSTEM: "1",
  CUSTOM: "0",
  properties: {
    "1": {
      description: "系统"
    },
    "0": {
      description: "自定义"
    },
  }
}
const BillStateEnum = {
  ALL: "-1",
  EDITING: "0",
  PUBLISHED: "1",
  properties: {
    "-1": {
      description: "全部"
    },
    "0": {
      description: "编辑"
    },
    "1": {
      description: "已发布"
    },
  }
}
const QueryConditionEnum = {
  ITEM: "Item",
  UNIT: "Unit",
  TRAINSET_TYPE: "TrainsetType",
  BATCH: "Batch",
  TRAINSET: "Trainset",
  MARSHAL_COUNT: "MarshalCount",
  properties: {
    "Item": {
      description: "项目",
      sort: 0,
      property: "itemCode",
      nameProperty: "itemName",
      required: true
    },
    "Unit": {
      description: "运用所",
      sort: 1,
      property: "unitCode",
      nameProperty: "unitName",
      required: false
    },
    "TrainsetType": {
      description: "车型",
      sort: 2,
      property: "trainsetType",
      required: true
    },
    "Batch": {
      description: "批次",
      sort: 3,
      property: "batch",
      required: false
    },
    "Trainset": {
      description: "车组",
      sort: 4,
      property: "trainsetId",
      required: false
    },
    "MarshalCount": {
      description: "编组数量",
      sort: 5,
      property: "marshalCount",
      required: true
    },
  }
}
const isBlank = GeneralUtil.isBlank
const enumToList = function (arr, enumObj) {
  return arr.map(v => ({ value: v, text: enumObj.properties[v].description }))
}
const getCachedPromise = (() => {
  var cache = {}
  return (key, promiseGetter) => {
    if (cache[key]) {
      return cache[key]
    }
    let promise = promiseGetter()
    cache[key] = promise
    promise.then(() => {
      delete cache[key]
    })
    return promise
  }
})()

const uniqueMessageFunctionGetter = () => {
  let map = {}
  return (key, doAlertMessage) => {
    if (map[key]) {
      map[key]()
    }
    map[key] = doAlertMessage(() => {
      map[key] = null
    })
  }
}

const getUniqueMessageGetterByType = (type) => {
  let uniqueAlertMessage = uniqueMessageFunctionGetter()
  return (message) => {
    return uniqueAlertMessage(message, (onClose) => {
      let messageManager = Vue.prototype.$message[type]({
        message,
        onClose
      })
      return () => {
        messageManager.close()
      }
    })
  }
}
const uniqueWarningMessage = getUniqueMessageGetterByType("warning")
const uniqueErrorMessage = getUniqueMessageGetterByType("error")

const getCachedDictionaryPromise = (() => {
  var dataCache = {}
  return (key, promiseGetter) => {
    if (dataCache[key]) {
      return Promise.resolve(dataCache[key])
    }
    let promise = getCachedPromise(key, promiseGetter)
    promise.then((v) => {
      dataCache[key] = v
    })
    return promise
  }
})()

const distinctBy = (array, getKey) => {
  let newArray = []
  let map = {}
  array.forEach(v => {
    let key = getKey(v)
    if (!map[key]) {
      newArray.push(v)
      map[key] = v
    }
  })
  return newArray
}

const getFamilyPropertyDefinitions = GeneralUtil.getFamilyPropertyDefinitions

const getParentValueGetter = (valueProperty, parentProperty) => {
  return (options, value) => {
    let valueOption = options.find(v => v[valueProperty] == value)
    return valueOption && valueOption[parentProperty]
  }
}

const billTypeParentValueGetter = getParentValueGetter("templateTypeCode", "fatherTypeCode")
const batchParentValueGetter = getParentValueGetter("traintempid", "traintype")
const trainsetIdParentValueGetter = getParentValueGetter("trainsetid", "traintempid")

const axiosResultChecker = (res) => {
  if (res.data.code == 1) {
    return Promise.resolve(res.data.data)
  } else if (res.data.code == 0) {
    uniqueWarningMessage(res.data.msg)
    return Promise.reject()
  } else {
    uniqueErrorMessage(res.data.msg)
    return Promise.reject()
  }
}

Vue.directive('scroll-into-view', (() => {
  return {
    bind: function (el, binding) {
      if (binding.value) {
        let { getTarget, shouldScroll, onScrolled, scrollIntoViewOptions } = binding.value
        if (shouldScroll()) {
          setTimeout(() => {
            let target = getTarget(el)
            if (target) {
              target.scrollIntoView(scrollIntoViewOptions || { block: "center" })
              if (onScrolled) {
                onScrolled()
              }
            }
          }, 0)
        }
      }
    }
  }
})())

var app = new Vue({
  el: "#billConfig",
  // name: "dataList",
  data() {
    let parent = this
    let filterGetterMap = {
      trainsetType: (trainsetType) => {
        return (trainsetInfo) => {
          return !trainsetType || trainsetInfo.traintype == trainsetType
        }
      },
      batch: (batch) => {
        return (trainsetInfo) => {
          return !batch || trainsetInfo.traintempid == batch
        }
      }
    }
    return {
      timeout: null,
      cardLoading: false,
      departments: [], // 运用所下拉数据
      oriBillConfigConditions: [],
      QueryConditionEnum,
      multipleSelection: [], // 表格选中的数据
      indexTable: [
        // {
        //     itemCode:'1',
        //     itemName:'1',
        //     mainTainCardNo:'1',
        //     strPartsTypeName:'1',
        //     strRepairMethod:'1',
        //     strItemCycle:'1',
        //     strItemTrainsetType:'1',
        //     maintainNatureName:'1',
        //     manualVersion:'1'
        // }
      ],
      query: (() => {
        let indexFormData = new Vue({
          data: {
            indexForm: {
              unitCode: "", // 运用所code
              depotCode: "", // 段编码
              trainsetType: "", // 车型
              batch: "", // 批次ID
              marshalCount: null,
              trainsetId: "", // 车组号ID
              billType: "", // 单据类型
              billSubType: "", // 单据子类型
              billName: "", // 单据名称
              itemName: "", // 项目名称
              state: "-1", // 状态
              page: 1, // 当前页码
              limit: 10, // 每页页大小
              orderInfo: "", // 排序方式
              orderAscOrDesc: "", // 排序方式
              manageType: "0",// 单据管理模式 1 系统 0 自定义
            },
          }
        })
        return new Vue({
          data() {
            return {
              ...getFamilyPropertyDefinitions([
                {
                  name: "billType",
                  getOptions: (parentValue) => {
                    return parent.queryBillType("1", parentValue)
                  },
                  getValue: () => {
                    return indexFormData.indexForm.billType
                  },
                  setValue: (v) => {
                    indexFormData.indexForm.billType = v
                  }
                },
                {
                  name: "billSubType",
                  getOptions: (parentValue) => {
                    return parent.queryBillType("2", parentValue)
                  },
                  getParentValue: billTypeParentValueGetter,
                  getValue: () => {
                    return indexFormData.indexForm.billSubType
                  },
                  setValue: (v) => {
                    indexFormData.indexForm.billSubType = v
                  }
                },
                {
                  name: "billName",
                  getOptions: (parentValue) => {
                    return parent.queryBillType("3", parentValue)
                  },
                  getParentValue: billTypeParentValueGetter,
                  getValue: () => {
                    return indexFormData.indexForm.billName
                  },
                  setValue: (v) => {
                    indexFormData.indexForm.billName = v
                  }
                }
              ]),
              ...getFamilyPropertyDefinitions([
                {
                  name: "trainsetType",
                  getOptions: () => {
                    return parent.getTrainsetTypeList()
                  },
                  getValue: () => {
                    return indexFormData.indexForm.trainsetType
                  },
                  setValue: (v) => {
                    indexFormData.indexForm.trainsetType = v
                  }
                },
                {
                  name: "batch",
                  getOptions: (parentValue, parentName) => {
                    return parent.getTrainsetList().then(v => {
                      return distinctBy(v.filter(filterGetterMap[parentName](parentValue)), v => v.traintempid)
                    })
                  },
                  getParentValue: batchParentValueGetter,
                  getValue: () => {
                    return indexFormData.indexForm.batch
                  },
                  setValue: (v) => {
                    indexFormData.indexForm.batch = v
                  }
                }
              ]),
            }
          },
          computed: {
            indexForm: {
              get() {
                return indexFormData.indexForm
              },
              set(v) {
                indexFormData.indexForm = v
              }
            }
          }
        })
      })(),
      manageTypes: enumToList([ManageTypeEnum.CUSTOM, ManageTypeEnum.SYSTEM], ManageTypeEnum),
      billStates: enumToList([BillStateEnum.ALL, BillStateEnum.EDITING, BillStateEnum.PUBLISHED], BillStateEnum),
      indexTableTotal: 0,
      marshalCountOptions: [4, 6, 8, 16, 17],
      billBasicInfoDialog: (() => {
        let defineReadonlyProperty = (obj, key, getter) => {
          Object.defineProperty(obj, key, {
            configurable: false,
            enumerable: true,
            get() {
              return getter.call(this)
            },
            set(){}
          })
        }
        let getInitBaseFormData = (initData_) => {
          let getDefaultValue = (key, defaultValue) => {
            let v = initData_ && initData_[key]
            return v || defaultValue
          }
          let initData = {
            billType: "", // 单据类型
            billSubType: "", // 单据子名称
            billName: "", // 单据名称
            trainsetType: "", // 车型
            marshalCount: null, // 编组数量
            batch: "", // 批次
            trainsetId: "", // 车组ID
            unitCode: "", // 运用所
          }
          for (const key in initData) {
            if (key === "marshalCount") {
              initData[key] = getDefaultValue(key, null)
            } else {
              initData[key] = getDefaultValue(key, "")
            }
          }
          defineReadonlyProperty(initData, "itemCode", () => {
            let itemDetail = baseFormData.selectItemDetail
            let itemCode = itemDetail && itemDetail.itemCode
            return itemCode || ""
          })
          defineReadonlyProperty(initData, "itemName", () => {
            let itemDetail = baseFormData.selectItemDetail
            let itemName = itemDetail && itemDetail.itemName
            return itemName || ""
          })
          defineReadonlyProperty(initData, "unitName", () => {
            let unit = parent.departments.find((item) => {
              return item.unitCode === baseFormData.baseForm.unitCode
            })
            let unitName = unit && unit.unitName
            return unitName ? unitName : "全部"
          })
          return initData
        }
        let baseFormData = new Vue({
          data: {
            baseForm: getInitBaseFormData(),
            selectItemDetail: {}
          }
        })
        return new Vue({
          data() {
            return {
              visible: false,
              ...getFamilyPropertyDefinitions([
                {
                  name: "billType",
                  getOptions: (parentValue) => {
                    return parent.queryBillType("1", parentValue, parent.isSystem, !parent.isSystem)
                  },
                  getValue: () => {
                    return baseFormData.baseForm.billType
                  },
                  setValue: (v) => {
                    baseFormData.baseForm.billType = v
                  }
                },
                {
                  name: "billSubType",
                  getOptions: (parentValue) => {
                    return parent.queryBillType("2", parentValue, parent.isSystem, !parent.isSystem)
                  },
                  getParentValue: billTypeParentValueGetter,
                  getValue: () => {
                    return baseFormData.baseForm.billSubType
                  },
                  setValue: (v) => {
                    baseFormData.baseForm.billSubType = v
                  }
                },
                {
                  name: "billName",
                  getOptions: (parentValue) => {
                    return parent.queryBillType("3", parentValue, parent.isSystem, !parent.isSystem)
                  },
                  getParentValue: billTypeParentValueGetter,
                  getValue: () => {
                    return baseFormData.baseForm.billName
                  },
                  setValue: (v) => {
                    baseFormData.baseForm.billName = v
                  }
                }
              ]),
              ...getFamilyPropertyDefinitions([
                {
                  name: "trainsetType",
                  getOptions: () => {
                    return parent.getTrainsetTypeList()
                  },
                  getValue: () => {
                    return baseFormData.baseForm.trainsetType
                  },
                  setValue: (v) => {
                    baseFormData.baseForm.trainsetType = v
                  }
                },
                {
                  name: "batch",
                  getOptions: (parentValue, parentName) => {
                    return parent.getTrainsetList().then(v => {
                      return distinctBy(v.filter(filterGetterMap[parentName](parentValue)), v => v.traintempid)
                    })
                  },
                  getParentValue: batchParentValueGetter,
                  getValue: () => {
                    return baseFormData.baseForm.batch
                  },
                  setValue: (v) => {
                    baseFormData.baseForm.batch = v
                  }
                },
                {
                  name: "trainsetId",
                  getOptions: (parentValue, parentName) => {
                    return parent.getTrainsetList().then(v => {
                      return v.filter(filterGetterMap[parentName](parentValue))
                    })
                  },
                  getParentValue: trainsetIdParentValueGetter,
                  getValue: () => {
                    return baseFormData.baseForm.trainsetId
                  },
                  setValue: (v) => {
                    baseFormData.baseForm.trainsetId = v
                  }
                }
              ]),
            }
          },
          computed: {
            selectItemDetail: {
              get() {
                return baseFormData.selectItemDetail
              },
              set(v) {
                baseFormData.selectItemDetail = v
              }
            },
            baseForm: {
              get() {
                return baseFormData.baseForm
              },
              set(v) {
                throw "baseForm禁止直接覆盖，请使用resetBaseForm方法"
              }
            },
            // 项目是否选择
            itemSelected() {
              if (JSON.stringify(this.selectItemDetail) == "{}") {
                return false
              } else {
                return true
              }
            },
            trainsetTypeDisabled() { // 新增弹窗车型下拉是否禁用
              return this.itemSelected
            },
            batchDisabled() {// 新增弹窗批次下拉是否禁用
              return this.itemSelected
            },
            marshalCountDisabled() {// 新增弹窗编组下拉是否禁用
              return this.itemSelected
            },
            lastBillType() {
              if (this.baseForm.billName) {
                return this.billName.options.find(v => v.templateTypeCode === this.baseForm.billName)
              } else if (this.baseForm.billSubType) {
                return this.billSubType.options.find(v => v.templateTypeCode === this.baseForm.billSubType)
              } else if (this.baseForm.billType) {
                return this.billType.options.find(v => v.templateTypeCode === this.baseForm.billType)
              }
            },
            templateTypeName() {
              return this.lastBillType && this.lastBillType.templateTypeName
            },
            templateTypeCode() {
              return this.lastBillType && this.lastBillType.templateTypeCode
            },
          },
          methods: {
            resetBaseForm(initData) {
              baseFormData.baseForm = getInitBaseFormData(initData)
            },
          }
        })
      })(),
      itemSelectDialog: (() => {
        let configFormData = new Vue({
          data: {
            configForm: {
              trainType: "", // 车型
              trainSubType: "", // 批次ID
              trainSetIdList: [], // 车组list
              allocatedTempStatus: "", // 回填状态
              itemName: "", // 项目名称
              page: 1,
              limit: 10,
            },
          }
        })
        return new Vue({
          data() {
            return {
              visible: false,// 选择项目弹窗显隐
              ...getFamilyPropertyDefinitions([
                {
                  name: "trainsetType",
                  getOptions: () => {
                    return parent.getTrainsetTypeList()
                  },
                  getValue: () => {
                    return configFormData.configForm.trainType
                  },
                  setValue: (v) => {
                    configFormData.configForm.trainType = v
                  }
                },
                {
                  name: "batch",
                  getOptions: (parentValue, parentName) => {
                    return parent.getTrainsetList().then(v => {
                      return distinctBy(v.filter(filterGetterMap[parentName](parentValue)), v => v.traintempid)
                    })
                  },
                  getParentValue: batchParentValueGetter,
                  getValue: () => {
                    return configFormData.configForm.trainSubType
                  },
                  setValue: (v) => {
                    configFormData.configForm.trainSubType = v
                  }
                }/* ,
                {
                  name: "trainsetId",
                  getOptions: (parentValue, parentName) => {
                    return parent.getTrainsetList().then(v => {
                      return v.filter(filterGetterMap[parentName](parentValue))
                    })
                  },
                  getParentValue: trainsetIdParentValueGetter,
                  getValue: () => {
                    return configFormData.configForm.trainSetIdList.join(",")
                  },
                  setValue: (v) => {
                    if (!v) {
                      configFormData.configForm.trainSetIdList = []
                    } else {
                      configFormData.configForm.trainSetIdList = v.split(",")
                    }
                  }
                } */
              ]),

              billProjectList: [],
              projectTotal: 0,
            }
          },
          computed: {
            configForm: {
              get() {
                return configFormData.configForm
              },
              set(v) {
                configFormData.configForm = v
              }
            },
          }
        })
      })(),

      isSystem: false,
      useNewItem: false, // 是否启用新项目
      // loginFouction: '', // 保存登录人权限信息(功能)
      // loginCode: '' // 保存登录人权限信息(编码)
      billEditorDialog: new Vue({
        data: {
          visible: false,
          closeFunctionName: "closeBillEditor"
        },
        computed: {
          url() {
            return billConfigEditorUrl + "?pageCode=" + parent.pageCode + "&closeFunctionName=" + this.closeFunctionName
          }
        }
      }),

      isScrollingSelectedRow: false, // 标记是否正在定位选择行

    }
  },
  computed: {
    billConfigConditions() {
      let conditions = this.oriBillConfigConditions
        .filter(v => QueryConditionEnum.properties[v.queryCode])
        .map(v => ({
          ...v,
          enumProperties: QueryConditionEnum.properties[v.queryCode]
        }));
      conditions.sort((a, b) => {
        return a.enumProperties.sort - b.enumProperties.sort
      })
      return conditions
    },
    pageCode() {
      return "billConfig_" + this.getUrlParam("configType")
    },
    showSystemTemplate() {
      return this.isSystem ? true : this.query.indexForm.manageType === ManageTypeEnum.SYSTEM
    },
    showCustomTemplate() {
      return !this.showSystemTemplate
    },
    disableStateSelect() {
      return !this.isSystem && this.showSystemTemplate
    },
    finalState: {
      get() {
        if (!this.isSystem && this.showSystemTemplate) {
          return BillStateEnum.PUBLISHED
        } else {
          return this.query.indexForm.state
        }
      },
      set(state) {
        this.$set(this.query.indexForm, "state", state)
      },
    },
    pageTitle() {
      return GeneralUtil.getObservablePageTitleGetter('单据配置' + (this.isSystem ? "（系统）" : "（自定义）"))()
    }
  },
  created() {
    this.isSystem = this.getUrlParam("configType") === "SYSTEM"
    // this.loginFouction = this.getUrlParam('DeptType')
    // this.loginCode = this.getUrlParam('UnitCode')
    // if (this.loginCode) {
    //   this.query.indexForm.unitCode = this.loginCode
    // }
  },
  mounted() {
    this.init()
    // //表格数据
    this.getTable()
    // //配属所下拉框数据
    this.getTrainRepairList()
    // 初始化车组查询条件
    this.resetTrainsetOptions(this.query)
    // 初始化类型查询条件
    this.resetAllBillTypeOptions(this.query)
  },
  watch: {
    "billBasicInfoDialog.templateTypeCode": function () {
      this.getQueryCondition()
    },
    showSystemTemplate() {
      this.query.indexForm = {
        ...this.query.indexForm,
        billType: "",
        billSubType: "",
        billName: "",
      }
      this.resetAllBillTypeOptions(this.query)
    }
  },
  methods: {
    resetTrainsetOptions(scope) {
      scope.trainsetType.resetOptions()
      scope.batch.resetOptions()
      scope.trainsetId && scope.trainsetId.resetOptions()
    },
    resetAllBillTypeOptions(scope) {
      scope.billType.resetOptions()
      scope.billSubType.resetOptions()
      scope.billName.resetOptions()
    },
    // 监听本地数据
    init() {
      localStorageParamsBus.clearData(this.pageCode, 'refreshList')
      localStorageParamsBus.addParamsListener(this.pageCode, 'refreshList', (val) => {
        if (val) {
          this.getTable()
        }
      })
    },
    // 获取当前登录人的信息
    async getUser() {
      return await axios({
        url: "/apiTrainRepairMidGround/common/getUser",
        method: "get",
      }).then(axiosResultChecker)
    },

    //运用所下拉数据
    async getTrainRepairList() {
      const params = {
        depotCode,
      }
      await axios({
        url: "/apiTrainRepairMidGround/common/getUnits",
        method: "get",
        params,
      }).then(axiosResultChecker).then((data) => {
        this.departments = [
          {
            unitCode: "",
            unitName: "全部",
          },
          ...data,
        ]
      }).catch((error) => {
        console.log(error)
      })
    },

    //车型下拉框数据
    getTrainsetTypeList() {
      return getCachedDictionaryPromise("trainsetTypeList", () => {
        return axios({
          url: "/apiTrainResume/resume/getTraintypeList",
          method: "get",
        }).then((res) => {
          return res.data.data
        })
      })
    },

    // 查询全部车组
    getTrainsetList() {
      return getCachedDictionaryPromise("trainsetList", () => {
        return axios
          .post("/apiTrainResume/resume/getTrainsetList", {})
          .then((res) => {
            // console.log("车型下所有车组", res)
            return res.data.data
          })
      })
    },

    //单据类型/单据名称/单据子名称下拉框数据
    queryBillType(type, fatherTypeCode, showSystemTemplate, showCustomTemplate) {
      const params = {
        type: type,
        fatherTypeCode: fatherTypeCode,
        showSystemTemplate: showSystemTemplate == null ? this.showSystemTemplate : showSystemTemplate,
        showCustomTemplate: showCustomTemplate == null ? this.showCustomTemplate : showCustomTemplate,
      }
      return getCachedPromise("queryBillType_" + JSON.stringify(params), () => {
        return axios({
          url: "/apiTrainRepairMidGround/common/queryBillTypes",
          method: "get",
          params,
        }).then(axiosResultChecker)
      })
    },

    //获取记录单配置表格数据
    getTable() {
      this.$refs["indexFormRef"].validate((valid) => {
        if (valid) {
          let loading = this.$loading()
          var billTypeCode = this.query.indexForm.billName || this.query.indexForm.billSubType || this.query.indexForm.billType
          var params = {
            ...this.query.indexForm,
            templateTypeCode: billTypeCode,
            batch: this.query.indexForm.batch,
            depotCode: depotCode,
            showSystemTemplate: this.showSystemTemplate,
            showCustomTemplate: this.showCustomTemplate,
            state: this.finalState
          }
          // console.log(billTypeCode, params, "查询列表的参数");
          let url = "/apiTrainRepairMidGround/billConfig/queryBillConfigs"
          //传入参数，查询列表
          axios
            .get(url, { params })
            .then(axiosResultChecker)
            .then((pageInfo) => {
              this.indexTable = pageInfo.records
              // console.log(this.indexTable)
              this.indexTableTotal = pageInfo.total
            })
            .catch((error) => {
              console.log(error)
            }).finally(() => {
              loading.close()
            })
        } else {
          return false
        }
      })
    },

    // 改变dialog记录单类型
    changeDialogBillType(val) {
      this.billBasicInfoDialog.resetBaseForm({
        ...this.billBasicInfoDialog.baseForm,
        unitCode: "",
        trainsetType: "",
        batch: "",
        marshalCount: null,
        trainsetId: "",
      })
      this.billBasicInfoDialog.selectItemDetail = {}
    },

    // 改变dialog记录子类型
    changeDialogBillSubType(val) {
      this.billBasicInfoDialog.resetBaseForm({
        ...this.billBasicInfoDialog.baseForm,
        unitCode: "",
        trainsetType: "",
        batch: "",
        marshalCount: null,
        trainsetId: "",
      })
      this.billBasicInfoDialog.selectItemDetail = {}
    },

    // 改变dialog记录单名称
    changeDialogBillName(val) {
      this.billBasicInfoDialog.resetBaseForm({
        ...this.billBasicInfoDialog.baseForm,
        unitCode: "",
        trainsetType: "",
        batch: "",
        marshalCount: null,
        trainsetId: "",
      })
      this.billBasicInfoDialog.selectItemDetail = {}
    },

    // 根据单据类型最低层级获取新增查巡条件的显隐
    async getQueryCondition() {
      const params = {
        billTypeCode: this.billBasicInfoDialog.templateTypeCode,
      }
      await axios({
        url: "/apiTrainRepairMidGround/common/getQueryCondition",
        method: "get",
        params,
      }).then(axiosResultChecker).then((data) => {
        this.oriBillConfigConditions = data
      }).catch((error) => {
        console.log(error)
      })
    },
    // 搜索
    search() {
      this.query.indexForm.page = 1
      this.getTable()
    },

    // 重置
    resetFormFields(formName) {
      this.$refs[formName].resetFields()
    },

    // 新增
    addItem() {
      this.billBasicInfoDialog.visible = true
      // 重置记录单类型
      this.resetAllBillTypeOptions(this.billBasicInfoDialog)
      this.resetTrainsetOptions(this.billBasicInfoDialog)
    },

    // 发布
    async handlePublish() {
      if (!this.multipleSelection.length) {
        uniqueWarningMessage("请先选择未发布的数据，再进行操作!")
        return
      }
      const params = {
        templateId: this.multipleSelection.join(","),
      }
      this.cardLoading = true
      await axios({
        url: "/apiTrainRepairMidGround/billConfig/billConfigRelease",
        method: "get",
        params,
      }).then(axiosResultChecker).then(() => {
        this.cardLoading = false
        this.$message.success("发布成功！")
        this.getTable()
      }).catch((error) => {
        this.cardLoading = false
        console.log(error)
      })
    },

    // 表格中选择方法
    handleSelectionChange(val) {
      var ary = []
      val.forEach((item) => {
        if (item.state === "0") {
          ary.push(item.templateId)
        }
      })
      this.multipleSelection = ary
      console.log(this.multipleSelection, "this.multipleSelection")
    },

    // 查看
    checkitem(row) {
      var actionInfo = {
        actionType: "view",
        data: row,
      }
      localStorageParamsBus.sendParams(
        this.pageCode,
        "billEditParams",
        actionInfo
      )
      this.show()
    },

    // 编辑
    handleEdit(row) {
      var actionInfo = {
        actionType: "edit",
        data: row,
      }
      localStorageParamsBus.sendParams(
        this.pageCode,
        "billEditParams",
        actionInfo
      )
      this.show()
    },

    // 分页跳转
    handleSizeChange(val) {
      this.query.indexForm.limit = val
      this.getTable()
    },
    handlePageChange(val) {
      this.query.indexForm.page = val
      this.getTable()
    },
    startScrollSelectedRow() {
      this.isScrollingSelectedRow = true
    },

    // 选择项目
    goSelectItem() {
      this.itemSelectDialog.visible = true
      this.itemSelectDialog.billProjectList = []
      this.itemSelectDialog.projectTotal = 0
      this.getUseNewItem().then((v) => {
        this.useNewItem = v
      })
      let selectedItem = this.billBasicInfoDialog.selectItemDetail
      if (selectedItem && selectedItem.itemCode) {
        this.itemSelectDialog.configForm.trainType = selectedItem.trainType
        this.itemSelectDialog.configForm.page = 1
        this.getProjectList({
          positionItemCode: selectedItem.itemCode,
          positionItemBatch: selectedItem.trainBatch
        })
      }
      this.resetTrainsetOptions(this.itemSelectDialog)
    },

    //  关闭新增/查看弹窗
    closeDialog() {
      this.billBasicInfoDialog.visible = false
    },
    onDialogClose() {
      this.billConfigConditions = []
      this.resetFormFields("baseFormRef")
    },

    // 调用新增校验的接口
    async handleSave() {
      let formData = this.billBasicInfoDialog.baseForm
      if (!formData.billType) {
        uniqueWarningMessage("请先选择单据类型!")
        return
      }
      if (!formData.billSubType) {
        uniqueWarningMessage("请先选择单据子类型!")
        return
      }
      if (!formData.billName) {
        uniqueWarningMessage("请选择单据名称!")
        return
      }
      for (let index = 0; index < this.billConfigConditions.length; index++) {
        const condition = this.billConfigConditions[index]
        let enumInfo = QueryConditionEnum.properties[condition.queryCode]
        if (enumInfo && enumInfo.required && isBlank(formData[enumInfo.property])) {
          let disabled = this.billBasicInfoDialog[enumInfo.property + "Disabled"]
          // 属性未被禁用才能提示
          if (disabled !== true) {
            uniqueWarningMessage(`请选择${condition.queryName}!`)
            return
          }
        }
      }
      const params = {
        templateTypeCode: formData.billName,
        unitCode: formData.unitCode,
        depotCode: depotCode,
        trainsetType: formData.trainsetType,
        batch: formData.batch,
        marshalCount: formData.marshalCount,
        itemCode: formData.itemCode,
      }
      var url = "/apiTrainRepairMidGround/billConfig/verifyBill"
      await axios.post(url, params).then(axiosResultChecker).then(() => {
        var actionInfo = {
          actionType: "add",
          data: {
            ...formData,
            billType: null,
            billName: null,
            billSubType: null,
            templateTypeCode: this.billBasicInfoDialog.templateTypeCode,
            templateTypeName: this.billBasicInfoDialog.templateTypeName,
            depotCode: depotCode,
            depotName: depotName,
            bureauCode: bureauCode,
            bureauName: bureauName,
          },
        }
        localStorageParamsBus.sendParams(
          this.pageCode,
          "billEditParams",
          actionInfo
        )
        this.show()
        this.closeDialog()
      }).catch((error) => {
        console.log(error)
      })
    },

    // // 关闭tab标签页
    // close() {
    //   if (top !== window) {
    //     let curIframe = Array.from(top.document.querySelectorAll("iframe")).find(v => {
    //       return v.getAttribute("src") === billConfigEditorUrl
    //     })
    //     if (curIframe && top.HussarTab && top.HussarTab.tabDelete) {
    //       top.HussarTab.tabDelete(curIframe.getAttribute("tab-id"))
    //       return
    //     }
    //   }
    // },
    // 打开模板编辑器
    show() {
      var closeFunctionName = this.billEditorDialog.closeFunctionName
      window[closeFunctionName] = () => {
        window[closeFunctionName] = null
        this.billEditorDialog.visible = false
      }
      this.billEditorDialog.visible = true
      // 打开新的tab标签页面
      // if (top !== null || top !== undefined) {
      //   var r = top.HussarTab
      //   r.tabAdd("单据内容配置", billConfigEditorUrl)
      // }
    },
    // 项目列表查询按钮
    searchProjectList() {
      this.itemSelectDialog.configForm.page = 1
      this.getProjectList()
    },

    // 防抖函数
    debounceChange() {
      // console.log('1111')
      var _this = this,
        arg = arguments
      clearTimeout(this.timeout)
      this.timeout = setTimeout(() => {
        console.log('3333')
        _this.getProjectList.apply(this, arg)
      }, 1000)
    },

    async getUseNewItem() {
      return await axios.get("/apiTrainRepairMidGround/repairItem/useNewItem").then(axiosResultChecker)
    },
    // 查询项目list
    async getProjectList(extraParams) {
      let positionItemCode = extraParams && extraParams.positionItemCode
      let positionItemBatch = extraParams && extraParams.positionItemBatch
      let validate = await this.$refs.configForm.validate()
      if (!validate) {
        return
      }
      const params = {
        ...this.itemSelectDialog.configForm,
        currentTemplateTypeCode: this.billBasicInfoDialog.baseForm.billName,
        positionItemCode,
        positionItemBatch
      }
      // console.log(params, "查询项目list")
      let loading = this.$loading()
      const url = "/apiTrainRepairMidGround/repairItem/selectRepairItemListByWorkParam"
      await axios.post(url, params).then(axiosResultChecker).then((pageObj) => {
        this.itemSelectDialog.billProjectList = pageObj.records
        this.itemSelectDialog.projectTotal = pageObj.total
        this.itemSelectDialog.configForm.page = pageObj.current
        if (positionItemCode) {
          this.startScrollSelectedRow()
        }
      }).catch((error) => {
        console.log(error)
      }).finally(() => {
        loading.close()
      })
    },

    // 表单单选触发方法
    selectRow(val) {
      let formData = this.billBasicInfoDialog.baseForm
      this.billBasicInfoDialog.selectItemDetail = val
      if (this.billConfigConditions.some(v => v.queryCode == QueryConditionEnum.TRAINSET_TYPE)) {
        formData.trainsetType = val.trainType
      }

      if (this.billConfigConditions.some(v => v.queryCode == QueryConditionEnum.BATCH)) {
        formData.batch = val.trainBatch
      }

      if (this.billConfigConditions.some(v => v.queryCode == QueryConditionEnum.MARSHAL_COUNT)) {
        axios.get("/apiTrainRepairMidGround/common/getMarshalCountByTrainType", {
          params: {
            trainType: val.trainType
          }
        }).then(axiosResultChecker).then((marshalCount) => {
          formData.marshalCount = marshalCount
        })
      }

      this.itemSelectDialog.visible = false
      this.itemSelectDialog.billProjectList = []
    },

    handleSecondSizeChange(val) {
      this.itemSelectDialog.configForm.limit = val
      this.getProjectList()
    },

    handleSecondCurrentChange(val) {
      this.itemSelectDialog.configForm.page = val
      this.getProjectList()
    },

    closeProgremDialog() {
      this.resetFormFields("configForm")
    },

    getUrlParam(name) {
      if (location.search.startsWith("?")) {
        let paramPairs = location.search.replace("?", "").split("&").map(v => v.split("=").map(v2 => decodeURIComponent(v2)))
        let targetParamPair = paramPairs.find(v => v[0] === name)
        if (targetParamPair) {
          return targetParamPair[1]
        }
      }
      return ""
    },
    getItemKey(item) {
      return JSON.stringify({
        itemCode: item.itemCode,
        trainType: item.trainType,
        trainBatch: item.trainBatch,
      })
    },
    tableRowClassName({ row, rowIndex }) {
      let selectedItem = this.billBasicInfoDialog.selectItemDetail

      if (selectedItem && this.getItemKey(selectedItem) === this.getItemKey(row)) {
        return 'selectable-table-row selected-row'
      } else {
        return 'selectable-table-row'
      }
    },
    getRowScrollIntoViewConfig(row) {
      return {
        getTarget: (el) => {
          return el.parentElement.parentElement.parentElement
        },
        shouldScroll: () => {
          if (this.isScrollingSelectedRow) {
            let selectedItem = this.billBasicInfoDialog.selectItemDetail
            return selectedItem && this.getItemKey(selectedItem) === this.getItemKey(row)
          } else {
            return false
          }
        },
        onScrolled: () => {
          this.isScrollingSelectedRow = false
        }
      }
    }
  },

})