(function () {
  var designer = GC.Spread.Sheets.Designer
  designer.loader.customReady(function () {
    // TODO 因组件变更，暂时禁用复制功能（需要重新整理相关代码）
    return;

    let copyBillConfigCallback = null
    let getInitBaseForm = () => {
      return {
        billType1: "", // 单据类型
        billType2: "", // 单据名称
        billType3: "", // 单据子名称
        trainsetType: "", // 车型
        itemCode: "", // 项目
        batch: "", // 批次
        trainsetId: "", // 车组Id
        unitCode: "", // 动车所
      }
    }
    let getInitConfigForm = () => {
      return {
        trainType: "",              // 车型
        trainSubType: "",           // 批次Id
        trainSetIdList: [],         // 车组list
        allocatedTempStatus: "",    // 回填状态
        itemName: "",               // 项目名称
        page: 1,
        limit: 10,
      }
    }
    let isInnerChange = false
    window.copyBillConfigDialog = new Vue({
      el: "#copyBillConfigDialog",
      data: {
        dialogVisible: false,
        billTypes1: [],
        billTypes2: [],
        billTypes3: [],
        addItemCondition: [],
        drpcartype: [],

        baseForm: getInitBaseForm(),
        unitList: [],
        dialogBatchsByTrainsetTypeList: [],

        optionDialogVisible: false,
        configForm: getInitConfigForm(),
        billProjectList: [],
        projectTotal: null,
        thirdBatchsByTrainsetTypeList: [],
        thirdTrainsetNamesByUnitCodeList: [],
        selectItemDetail: {},
        allocatedTempStatusList: [
          {
            value: "-1",
            label: "全部"
          },
          {
            value: "0",
            label: "编辑"
          },
          {
            value: "1",
            label: "已发布"
          }
        ]
      },
      computed: {
        templateBasicData: {
          get() {
            return app.core.templateBasicData
          }
        },
        allBillTypes() {
          return [
            ...this.billTypes1,
            ...this.billTypes2,
            ...this.billTypes3,
          ]
        },
        allBillTypesMap() {
          return this.allBillTypes.reduce((p, c) => {
            p[c.templateTypeCode] = c
            return p
          }, {})
        },
        billTypeList() {
          return [...this.billTypes1]
        },
        dialogBillNameList() {
          return this.billTypes2.filter(billType => {
            let billType1 = this.baseForm.billType1
            if (!billType1) {
              return true
            } else {
              return billType.fatherTypeCode == billType1
            }
          })
        },
        dialogBillChildrenTypeList() {
          return this.billTypes3.filter(billType => {
            let billType2 = this.baseForm.billType2
            if (!billType2) {
              return true
            } else {
              return billType.fatherTypeCode == billType2
            }
          })
        },
        billProjectMap() {
          let initObj = {}
          if (this.selectItemDetail && this.selectItemDetail.itemCode) {
            initObj[this.selectItemDetail.itemCode] = this.selectItemDetail
          }
          return this.billProjectList.reduce((p, c) => {
            p[c.itemCode] = c
            return p
          }, initObj)
        },
        billProjectNameMap() {
          return Object.keys(this.billProjectMap).reduce((p, c) => {
            p[c] = this.billProjectMap[c].itemName
            return p
          }, {})
        },
        trainTypeDisabled() {
          if ((this.selectItemDetail && this.selectItemDetail.trainType) || (!this.selectItemDetail && this.baseForm.itemCode)) {
            return true
          } else {
            return false
          }
        },
        batchIdDisabled() {
          if ((this.selectItemDetail && this.selectItemDetail.trainBatch) || (!this.selectItemDetail && this.baseForm.itemCode)) {
            return true
          } else {
            return false
          }
        },
        allocatedTempStatusMap() {
          return this.allocatedTempStatusList.reduce((p, c) => {
            p[c.value] = c.label
            return p
          }, {})
        },
        selectedBillTypeCode() {
          return this.baseForm.billType3 || this.baseForm.billType2 || this.baseForm.billType1
        },
        unitNameMap() {
          return this.unitList.reduce((p, c) => {
            p[c.unitCode] = c.unitName
            return p
          }, {})
        }
      },
      methods: {
        copyBillConfig(callback) {
          if (!callback) {
            throw "请设置成功回调函数"
          } else {
            isInnerChange = true
            this.initData().then(() => {
              copyBillConfigCallback = callback
              this.dialogVisible = true
            }).finally(() => {
              this.$nextTick(() => {
                isInnerChange = false
              })
            })
          }
        },
        initData() {
          return new Promise((resolve, reject) => {
            let loading = this.$loading()
            let a = this.queryBillTypes(1).then(v => this.billTypes1 = v)
            let b = this.queryBillTypes(2).then(v => this.billTypes2 = v)
            let c = this.queryBillTypes(3).then(v => this.billTypes3 = v)
            let tbc = this.templateBasicData
            let curBillTypeCode = tbc.templateTypeCode
            let d = this.getQueryCondition(curBillTypeCode).then(v => this.addItemCondition = v)
            let e = this.getTraintypeList()
            let f = this.getTrainRepairList()
            let g = this.getOneProject(tbc.itemCode, tbc.trainsetType, tbc.batch).then(v => this.selectItemDetail = v)
            Promise.all([a, b, c, d, e, f, g]).then(() => {
              this.setAllBillType(curBillTypeCode)
              this.setValues()
              resolve()
            }).catch(reject).finally(() => {
              loading.close()
            })
          })
        },
        setValues() {// 设置单据类型以外的变量
          // this._setValue("itemName", this.templateBasicData.itemName)
          this._setValue("itemCode", this.templateBasicData.itemCode)
          this._setValue("unitCode", this.templateBasicData.unitCode)
          // this._setValue("unitName", this.templateBasicData.unitName)
          this._setValue("trainsetType", this.templateBasicData.trainsetType)
          this._setValue("batch", this.templateBasicData.batch)
          this._setValue("trainsetId", this.templateBasicData.trainsetId)
        },
        _setValue(key, value) {
          this.$set(this.baseForm, key, value)
        },
        setAllBillType(billTypeCode) {// 必须等待billTypes列表获取完毕后调用
          let billType = this.allBillTypesMap[billTypeCode]
          if (billType) {
            this._setBillType(billType)
            if (billType.fatherTypeCode) {
              this.setAllBillType(billType.fatherTypeCode)
            }
          }
        },
        _setBillType(billTypeObj) {
          let value = billTypeObj.templateTypeCode
          switch (billTypeObj.type) {
            case "1":
              this._setValue("billType1", value)
              break
            case "2":
              this._setValue("billType2", value)
              break
            case "3":
              this._setValue("billType3", value)
              break
            default:
              break
          }
        },
        queryBillTypes(type, fatherTypeCode) {
          return new Promise((resolve, reject) => {
            axios.get("/apiTrainRepairMidGround/common/queryBillTypes", {
              params: {
                type,
                fatherTypeCode
              }
            }).then((res) => {
              resolve(res.data.data)
            }).catch(reject)
          })
        },
        // 获取需要编辑的属性
        getQueryCondition(billTypeCode) {
          return new Promise((resolve, reject) => {
            axios("/apiTrainRepairMidGround/common/getQueryCondition", {
              params: { billTypeCode }
            }).then((res) => {
              resolve(res.data.data)
            }).catch(reject)
          })
        },
        handleSelectItem() {
          this.billProjectList = []
          this.optionDialogVisible = true
          this.getProjectList()
        },
        // 查询项目list
        getProjectList() {
          return new Promise((resolve, reject) => {
            axios.post(
              "/apiTrainRepairMidGround/repairItem/selectRepairItemListByWorkParam",
              { ...this.configForm, itemCode: this.templateBasicData.itemCode }
            ).then((res) => {
              this.billProjectList = res.data.rows
              this.projectTotal = res.data.total
              resolve(res.data)
            }).catch(reject)
          })
        },
        // 根据itemCode获取项目详情
        getOneProject(itemCode, trainsetType, batch) {
          return new Promise((resolve, reject) => {
            axios.post(
              "/apiTrainRepairMidGround/repairItem/selectRepairItemListByWorkParam",
              {
                itemCode,
                trainType: trainsetType,
                trainSubType: batch,
                limit: 10,
                page: 1
              }
            ).then((res) => {
              if (res.data.rows.length == 1) {
                resolve(res.data.rows[0])
              } else {
                resolve(null)
              }
            }).catch(reject)
          })
        },
        // 车型下拉框数据
        getTraintypeList() {
          return new Promise((resolve, reject) => {
            axios.get("/apiTrainRecord/resume/getTraintypeList").then((res) => {
              this.drpcartype = res.data.data
              resolve(res.data.data)
            }).catch(reject)
          })
        },
        // 批次下拉框数据
        getPatchListByTraintype(traintype) {
          return new Promise((resolve, reject) => {
            axios.get("/apiTrainRecord/resume/getPatchListByTraintype", {
              params: { traintype }
            }).then((res) => {
              resolve(res.data.data)
            }).catch(reject)
          })
        },
        // 车组下拉框数据
        getTrainNameBypatchno(patchno) {
          return new Promise((resolve, reject) => {
            axios.get("/apiTrainRecord/resume/getTrainNameBypatchno", {
              params: { patchno }
            }).then(function (res) {
              resolve(res.data.data)
            }).catch(reject)
          })
        },
        // 修改三级dialog车型
        // changeThirdTraintype(val) {
        //   this.configForm.trainSubType = ""
        //   this.configForm.trainSetIdList = []
        //   if (val) {
        //     this.getPatchListByTraintype(val).then(v => this.thirdBatchsByTrainsetTypeList = v)
        //   } else {
        //     this.thirdBatchsByTrainsetTypeList = []
        //   }
        // },
        // 修改三级dialog的批次
        // changeThirdBatch(val) {
        //   this.configForm.trainSetIdList = []
        //   if (val) {
        //     this.getTrainNameBypatchno(val).then(v => this.thirdTrainsetNamesByUnitCodeList = v)
        //   } else {
        //     this.thirdTrainsetNamesByUnitCodeList = []
        //   }
        // },
        // 项目列表查询按钮
        searchProjectList() {
          this.configForm.page = 1
          this.getProjectList()
        },
        resetBaseForm() {
          this.baseForm = getInitBaseForm()
        },
        resetConfigForm() {
          this.configForm = getInitConfigForm()
        },
        // 表单重置
        // resetIndexForm(formName) {
        //   this.$refs[formName].resetFields()
        // },
        // 表单单选触发方法
        handleCurrentChange(val) {
          if (val) {
            val = { ...val }
            this.selectItemDetail = val
            this.baseForm.itemCode = val && val.itemCode
            this.baseForm.batch = val && val.trainBatch
            this.baseForm.trainsetType = val && val.trainType
            if (val.trainType) {
              this.getPatchListByTraintype(val.trainType).then(v => this.dialogBatchsByTrainsetTypeList = v)
            }
            val && this.closeProgremDialog()
          }
        },
        handleSecondSizeChange(val) {
          this.configForm.limit = val
          this.getProjectList()
        },
        handleSecondCurrentChange(val) {
          this.configForm.page = val
          this.getProjectList()
        },
        closeProgremDialog() {
          this.optionDialogVisible = false
          this.resetConfigForm()
          // this.resetIndexForm('configForm')
        },
        // 动车所下拉数据
        getTrainRepairList() {
          return new Promise((resolve, reject) => {
            const params = {
              depotCode: this.templateBasicData.depotCode
            }
            axios.get("/apiTrainRepairMidGround/common/getUnits", {
              params,
            }).then((res) => {
              this.unitList = [
                {
                  unitCode: "",
                  unitName: "全部",
                },
                ...res.data.data,
              ]
              resolve(res.data.data)
            }).catch(reject)
          })
        },
        // 修改dialog车型
        changeSecondTraintype(val) {
          this.baseForm.batch = ""
          this.baseForm.trainsetId = ""
          if (val) {
            this.getPatchListByTraintype(val).then(v => this.dialogBatchsByTrainsetTypeList = v)
          } else {
            this.dialogBatchsByTrainsetTypeList = []
          }
        },
        // 修改dialog的批次
        changDialogBatch(val) {
          this.baseForm.trainsetId = ""
          if (val) {
            this.getTrainNameBypatchno(val).then(v => this.dialogTrainsetNamesByUnitCodeList = v)
          } else {
            this.dialogTrainsetNamesByUnitCodeList = []
          }
        },
        // 关闭弹窗
        closeDialog() {
          this.dialogVisible = false
          this.onCloseDialog()
        },
        // 关闭弹窗时执行
        onCloseDialog() {
          copyBillConfigCallback = null
          this.addItemCondition = []
          this.resetBaseForm()
          // this.resetIndexForm("baseForm")
        },
        // 新增校验
        checkAddData() {
          return new Promise((resolve, reject) => {
            let loading = this.$loading()
            axios.post("/apiTrainRepairMidGround/billConfig/verifyBill", {
              billTypeCode: this.selectedBillTypeCode,
              unitCode: this.baseForm.unitCode,
              depotCode: this.templateBasicData.depotCode,
              trainsetType: this.baseForm.trainsetType,
              batchId: this.baseForm.batch,
              itemName: this.billProjectNameMap[this.baseForm.itemCode],
              itemCode: this.baseForm.itemCode,
            }).then(res => {
              if (res.data.state === 1) {
                resolve()
              } else {
                this.$message.error(res.data.msg)
                reject()
              }
            }).catch(reject).finally(() => {
              loading.close()
            })
          })
        },
        // 调用新增校验的接口
        handleSave() {
          this.checkAddData().then(() => {
            let baseForm = {
              ...this.baseForm
            }
            delete baseForm.billType1
            delete baseForm.billType2
            delete baseForm.billType3
            let batch = baseForm.batch
            delete baseForm.batch

            let selectedTemplateType = this.allBillTypesMap[this.selectedBillTypeCode]
            let actionInfo = {
              actionType: "add",
              data: {
                ...baseForm,
                batchId: batch,
                itemName: this.billProjectNameMap[baseForm.itemCode],
                unitName: this.unitNameMap[baseForm.unitCode],
                templateTypeCode: this.selectedBillTypeCode,
                moreCellFlag: selectedTemplateType.moreCellFlag,
                depotCode: this.templateBasicData.depotCode,
                depotName: this.templateBasicData.depotName,
                bureauCode: this.templateBasicData.bureauCode,
                bureauName: this.templateBasicData.bureauName,
                account: app.core.userName
              }
            }
            copyBillConfigCallback && copyBillConfigCallback(actionInfo, () => {
              this.closeDialog()
            })
          })

        }
      },
      watch: {
        selectedBillTypeCode(curBillTypeCode) {
          if (!isInnerChange) {
            this.baseForm = {
              ...this.baseForm,
              itemCode: '',
              trainsetType: '',
              batch: '',
              trainsetId: '',
            }
            this.selectItemDetail = {}
            if (curBillTypeCode) {
              this.getQueryCondition(curBillTypeCode).then(v => {
                this.addItemCondition = v
              })
            } else {
              this.addItemCondition = []
            }
          }
        },
        "configForm.trainType": function (value) {
          this.configForm.trainSubType = ""
          if (value) {
            this.getPatchListByTraintype(value).then(v => this.thirdBatchsByTrainsetTypeList = v)
          } else {
            this.thirdBatchsByTrainsetTypeList = []
          }
        },
        "configForm.trainSubType": function (value) {
          this.configForm.trainSetIdList = []
          if (value) {
            this.getTrainNameBypatchno(value).then(v => this.thirdTrainsetNamesByUnitCodeList = v)
          } else {
            this.thirdTrainsetNamesByUnitCodeList = []
          }
        }
      }
    })
  })

})()
