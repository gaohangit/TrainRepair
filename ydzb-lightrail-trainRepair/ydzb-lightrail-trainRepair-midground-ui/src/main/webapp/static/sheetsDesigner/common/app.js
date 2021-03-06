var app = (function () {
  var _fileSelectorEventAdded = false
  var $emptyFileSelector
  var _saveAsDialog

  function processSelectedFile(file, action, callback, options) {
    if (file) {
      var operationResult = { status: 'success', file: file }
      switch (action) {
        case "insertPicture":
          readFile(operationResult, "dataurl", callback)
          break
        default:
          readFile(operationResult, "", callback, options)
          break
      }
    }
  }

  function getFileInfo(fullPath) {
    var result = { dir: '', fileName: '' }

    if (fullPath) {
      var found = false, pos;
      ['\\', '/'].forEach(function (c) {
        if (!found && (pos = fullPath.lastIndexOf(c)) !== -1) {
          found = true
        }
      })
      result.fileName = fullPath.substr(pos + 1)
      if (found) {
        result.dir = fullPath.substr(0, pos)
      }
    }

    return result
  }

  function getExtension(fileName, detail) {
    var pos = fileName.lastIndexOf('.')
    if (detail) {
      detail.pos = pos
      detail.name = fileName.substr(0, pos)
    }
    return pos === -1 ? "" : fileName.substring(pos + 1).toLowerCase()
  }

  function readFile(target, type, callback, options) {
    if (target && target.file) {
      var file = target.file
      if (!type) {
        type = getExtension(file.name)
      }

      var reader = new FileReader()
      reader.onload = function () {
        if (type === "xlsx") {
          importExcel(this.result, options, callback, file)
        } else {
          target.data = this.result
          callback(target)
        }
      }
      switch (type) {
        case "dataurl":
          reader.readAsDataURL(file)
          break
        case "xlsx":
          reader.readAsArrayBuffer(file)
          break
        default:
          reader.readAsText(file)
          break
      }
    }
  }

  function getFilters(nameFilters) {
    function convert(filterString) {
      var items = filterString.split('('),
        name = items[0].trim(),
        patterns = items[1].trim().replace(')', ''),
        extensions = patterns.split(' ').map(function (v) {
          return v.replace('*.', '.')
        })
      return {
        name: name,
        extensions: extensions
      }
    }

    return nameFilters && nameFilters.map(convert) || []
  }

  function importExcel(buffer, options, callback, file) {
    var excelIo = new GC.Spread.Excel.IO()
    excelIo.open(buffer, function (json) {
      callback && callback({
        status: 'success',
        data: json,
        file: file
      })
    }, function (err) {
      //process error
      callback && callback({
        status: 'failed',
        errorData: err,
        file: file
      })
    }, options)
  }

  function exportExcel(spreadJson, exportOptions, callback) {
    var options = { password: exportOptions.password }
    var excelIo = new GC.Spread.Excel.IO()
    excelIo.save(spreadJson, function (data) {
      callback && callback({
        status: 'success',
        data: data
      })
    }, function (err) {
      callback && callback({
        status: 'failed',
        errorData: err
      })
    }, options)
  }

  // ?????????
  axios.interceptors.response.use(res => {
    if (res.status == 200 && (res.data.code == 1 || (res.data.msg && res.data.msg.includes("??????")) || (Object.keys(res.data).length == 1 && res.data.data) || !res.headers["content-type"].includes("json"))) {
      return res
    } else {
      let msg = (res.data && res.data.msg) || "????????????"
      app.core.$message.error(msg)
      throw msg
    }
  }, error => {
    app.core.$message.error(error.message)
    throw error
  })

  let throttle = (() => {
    let map = new Map()
    return (fn, key, throttleTime) => {
      if (key == null || fn == null) {
        throw "throttle????????????"
      }
      if (throttleTime == null) {
        throttleTime = 200
      }
      let scope = null
      if (!map.has(key)) {
        scope = {}
        map.set(key, scope)
      } else {
        scope = map.get(key)
      }
      return function (...args) {
        if (scope.clearKey) {
          clearTimeout(scope.clearKey)
        }
        scope.clearKey = setTimeout(() => {
          try {
            return fn.apply(this, args)
          } catch (error) {
            throw error
          } finally {
            scope.clearKey = null
          }
        }, throttleTime)
      }
    }
  })()

  let copyDataSimple = (data) => JSON.parse(JSON.stringify(data))

  let wrapFn = (obj, name, fn) => {
    // ????????????????????????????????????
    let ori = obj[name]
    obj[name] = function (...args) {
      return fn.call(this, () => {
        return ori && ori.apply(this, args)
      }, args)
    }
    return ori
  }

  let debugPaint = () => {
    let fns = ["suspendPaint", "resumePaint"]
    fns.forEach(fnName => {
      wrapFn(GC.Spread.Sheets.Designer.wrapper.spread, fnName, (ori) => {
        if (window.debug === true) {
          console.log("enter:" + fnName)
          ori()
          console.log("out:" + fnName)
        } else {
          ori()
        }
      })
    })
  }

  let getPageByMarshallingType = (type) => {
    if (type == 1) {
      return 1
    } else if (type == 2) {
      return 2
    } else {
      return null
    }
  }

  // ????????????ssjson????????????
  let ssjsonCatch = new Map()

  // ??????????????????????????????????????????????????????????????????spread???????????????????????????
  let getMouseWheelingStateGetterFromSpreadSheet = sheet => {
    let isWheeling = false
    wrapFn(sheet, "RF", (ori, args) => {
      isWheeling = true
      try {
        return ori.apply(this, args)
      } catch (error) {
        throw error
      } finally {
        isWheeling = false
      }
    })
    return () => {
      return isWheeling
    }
  }

  let ActionTypeEnum = (() => {
    let enumObj = {
      ??????: "add",
      ??????: "edit",
      ??????: "view"
    }
    Object.freeze(enumObj)
    return enumObj
  })()

  let BusinessTypeEnum = (() => {
    let enumObj = {
      ??????????????????: "templateConfig",
      ????????????????????????: "baseTemplateConfig"
    }
    Object.freeze(enumObj)
    return enumObj
  })()

  let BusinessTypeConfigsMap = {
    [BusinessTypeEnum.??????????????????]: {
      canRelease: true,// ???????????????
      releaseTemplate: (templateId) => axios.get("/apiTrainRepairMidGround/billConfig/billConfigRelease", {
        params: {
          templateId
        }
      }),
      saveTemplate: (templates) => axios.post("/apiTrainRepairMidGround/billConfig/saveBillConfig", templates),
      queryTemplateDetail: (params) => axios.get("/apiTrainRepairMidGround/billConfig/getBillTemplateDetail", {
        params: {
          templateId: params.templateId,
          state: params.state//0????????????1????????????
        }
      }),
      getNewMainData: (billData) => {
        return {
          templateTypeCode: billData.templateTypeCode,
          trainsetType: billData.trainsetType,
          batch: billData.batchId,
          trainsetId: billData.trainsetId,
          itemCode: billData.itemCode,
          itemName: billData.itemName,
          marshalCount: billData.marshalCount,
          unitCode: billData.unitCode,
          unitName: billData.unitName,
          deptType: billData.unitCode ? "04" : "03",
          depotCode: billData.depotCode,
          depotName: billData.depotName,
          bureauCode: billData.bureauCode,
          bureauName: billData.bureauName
        }
      }
    },
    [BusinessTypeEnum.????????????????????????]: {
      canRelease: false,// ???????????????
      saveTemplate: (templates) => axios.post("/apiTrainRepairMidGround/billBaseTemplate/saveBaseTemplate", templates),
      queryTemplateDetail: (params) => axios.get("/apiTrainRepairMidGround/billBaseTemplate/getBaseTemplateDetail", {
        params: {
          templateId: params.templateId,
        }
      }),
      getNewMainData: (billData) => {
        return {
          templateTypeCode: billData.templateTypeCode,
          trainsetType: billData.trainsetType,
          batch: billData.batchId,
          trainsetId: billData.trainsetId,
          itemCode: billData.itemCode,
          itemName: billData.itemName,
          marshalCount: billData.marshalCount,
        }
      }
    }
  }

  return {
    showSaveDialog: function (options, callback) {
      if (!_saveAsDialog) {
        _saveAsDialog = new GC.Spread.Sheets.Designer.SaveAsDialog()
      }
      var fileExtension = options.fileExtension || getFilters(options.nameFilters)[0].extensions[0]
      _saveAsDialog.open({
        fileExtension: fileExtension,
        value: app.core.exportFileName,
        done: function (result) {
          var fileName = result && result.fileName, cancelled = result && result.cancelled
          if (cancelled || !fileName) {
            callback({ status: 'cancelled' })
          } else {
            callback({ status: 'success', fileName: fileName + fileExtension })
          }
        }
      })
    },
    showOpenDialog: function (options, callback, importOptions) {
      if (!$emptyFileSelector) {
        $emptyFileSelector = $("#fileSelector").clone()
      } else {
        // clear (with cloned object (.val("") not works for IE)) to make sure change event occurs even when same file selected again
        $("#fileSelector").replaceWith($emptyFileSelector.clone())
      }
      if (!_fileSelectorEventAdded) {
        $(document.body).on("change", "#fileSelector", function () {
          var files = this.files,
            file = files && files[0],
            action = $(this).data("action"),
            callback = $(this).data("callback"),
            options = $(this).data("importOptions")
          if (!file) {
            return false
          }
          processSelectedFile(file, action, callback, options)
        })
        _fileSelectorEventAdded = true
      }

      var $fileSelector = $("#fileSelector")
      $fileSelector.data("action", options.action)
      $fileSelector.data("callback", callback)
      $fileSelector.data("importOptions", importOptions)
      var filters = options.filters || getFilters(options.nameFilters)[0].extensions.join(" ")
      $fileSelector.attr("accept", filters || "")
      $fileSelector.click()
    },
    save: function (fileName, saveData, callback, options) {
      function getVariableName(fileName) {
        var detail = {}
        getExtension(fileName, detail)
        var name = detail.name

        if (/^[_a-zA-Z\$]{1}[_a-zA-Z0-9\$]*$/.test(name)) {
          return name
        } else {
          return '_' + name.replace(/[ \.]/g, '_')
        }
      }

      function saveFile(fileName, data, mimeType, callback, encoding) {
        try {
          var blobConfig = { type: mimeType }
          if (encoding) {
            blobConfig.encoding = encoding
          }
          saveAs(new Blob([data], blobConfig), fileName)
          callback({ status: 'success' })
        } catch (ex) {
          callback({ status: 'failed', message: ex })
        }
      }

      var data = saveData.spread || saveData

      var isJSFile = options && options.saveAsJS

      if (isJSFile) {
        data = 'var ' + getVariableName(fileName) + ' = ' + data + ';'
      }

      var ext = saveData.exportFileType,
        mimeType = "text/plain;charset=utf-8",
        encoding = null

      switch (ext) {
        case 'xlsx':
          mimeType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
          exportExcel(data, options, function (result) {
            if (result.status === 'success') {
              saveFile(fileName, result.data, mimeType, callback)
            } else {
              callback({
                status: 'failed',
                message: result.errorData
              })
            }
          })
          return
        case 'pdf':
          saveData.spread.savePDF(function (blob) {
            saveAs(blob, fileName)
            callback({ status: 'success' })
          }, function (ex) {
            callback({ status: 'failed', message: ex })
          }, options.setting, options.sheetIndex)
          return
        case 'csv':
          mimeType = 'text/csv;charset=utf-8'
          encoding = options && options.encoding
          break
      }

      saveFile(fileName, data, mimeType, callback, encoding)
    },
    notify: function () { return "" },
    openUrl: function () { return false },
    notifyReady: function () { },
    getFileInfo: getFileInfo,
    getExtension: getExtension,
    // ?????????
    isInSelectMode: false,
    ActionTypeEnum,
    BusinessTypeEnum,
    // ??????vue????????????????????????????????????????????????
    core: new Vue({
      data() {
        let parent = this
        let settingCore = new Vue({
          data: {
            billCount: 1,
            curBillNum: 1,
            isShowMode: false, // ?????????????????????????????????????????????????????????????????????????????????
            // saveBtnDisabled: false,
            // billCountRadioDisabled: true
          },
          computed: {
            billCountRadioDisabled() {
              return !parent.isMultiSheet || !parent.canEditConfig || parent.businessType === BusinessTypeEnum.????????????????????????
            },
            saveBtnDisabled() {
              return !parent.canEditConfig
            },
            showCloseBtn() {
              let closeFunctionName = parent.getUrlParam("closeFunctionName")
              if (closeFunctionName && typeof window.parent[closeFunctionName] == "function") {
                return true
              } else {
                return false
              }
            },
            showReleaseBtn() {
              return parent.businessTypeConfig.canRelease
            }
          },
          methods: {
            close() {
              let closeFunctionName = parent.getUrlParam("closeFunctionName")
              if (closeFunctionName && window.parent && typeof window.parent[closeFunctionName] == "function") {
                let doClose = () => {
                  window.parent[closeFunctionName]()
                }
                if (parent.needSave) {
                  let msg = (parent.actionType === ActionTypeEnum.?????? ? "????????????????????????" : "????????????????????????") + "???????????????????????????"
                  designer.MessageBox.show(msg, "????????????", 2 /* warning */, 2 /* okCancel */, function (event, result) {
                    if (result === 2 /* yes */) {
                      parent.saveBillConfig().then(doClose)
                    } else if (result === 3 /* no */) {
                      doClose()
                    }
                  })
                } else {
                  doClose()
                }
              }
            }
          }
        })
        return {
          ZSJCJYW_COMMAND_PREFIX: "zsjcjyw.command.",
          ZSJCJYW_MENUITEM_PREFIX: "zsjcjyw.menuItem.",

          attrDataOriginal: null,
          attrOptionsDataOriginal: null,
          billConfigSetting: settingCore,
          billJSONData: [],
          billExData: [],
          billBasicData: [],
          paramData: null,

          changed: false,// ??????????????????????????????????????????

          highlightInvalidData: false,// ????????????????????????????????????

          // userData: {},// ????????????

          added: false,
          templateTypeInfo: null,// ??????????????????

          ??????warnMessage: "??????????????????????????????????????????????????????????????????",
          show??????warnMessageByPage: [], // ????????????????????????????????????????????????????????????????????????
        }
      },
      computed: {
        businessTypeConfig() {
          return BusinessTypeConfigsMap[this.businessType]
        },
        needSave() {
          if (!this.isReadOnly && (this.changed || !this.templateId)) {
            return true
          } else {
            return false
          }
        },
        pageCode() {
          let pageCode = this.getUrlParam("pageCode")
          if (pageCode) {
            return pageCode
          } else {
            return "billConfig"
          }
        },
        show??????warnMessage() {// ????????????????????????????????????????????????????????????
          return this.show??????warnMessageByPage.some(v => v)
        },
        fixedErrorMessage() { // ???????????????????????????????????????????????????????????????????????????
          if (this.show??????warnMessage) {
            return this.??????warnMessage
          } else {
            return null
          }
        },
        isMultiSheet() {
          return this.templateTypeInfo && this.templateTypeInfo.moreCellFlag === "1"
        },
        allowBillCount() {
          let allowBillCount = this.isMultiSheet ? 2 : 1
          return allowBillCount
        },
        templateBasicData() {
          return this.getTemplateBasicData(this.billConfigSetting.curBillNum)
        },
        templateId() {
          return this.templateIds.join(",")
        },
        templateIds: {
          get() {
            let ids = []
            for (let i = 0; i < this.billConfigSetting.billCount; i++) {
              let templateBasicDataStr = this.billBasicData[i]
              if (templateBasicDataStr) {
                let templateBasicData = JSON.parse(templateBasicDataStr)
                if (templateBasicData && templateBasicData.templateId) {
                  ids.push(templateBasicData.templateId)
                }
              }
            }
            return ids
          },
          set(templateIds) {
            for (let i = 0; i < this.billConfigSetting.billCount; i++) {
              if (templateIds[i]) {
                let templateBasicDataStr = this.billBasicData[i]
                if (templateBasicDataStr) {
                  let templateBasicData = JSON.parse(templateBasicDataStr)
                  templateBasicData.templateId = templateIds[i]
                  this.$set(this.billBasicData, i, JSON.stringify(templateBasicData))
                }
              }
            }
          }
        },
        // userName() {
        //   return this.userData.name
        // },
        // userCode() {
        //   return this.userData.staffId
        // },
        businessType() {
          if (this.paramData && this.paramData.businessType) {
            let businessType = this.paramData.businessType
            if (Object.values(BusinessTypeEnum).includes(businessType)) {
              return businessType
            } else {
              let errorMsg = "businessType?????????????????????" + Object.values(BusinessTypeEnum).join(",")
              console.error(errorMsg)
              throw errorMsg
            }
          } else {
            return BusinessTypeEnum.??????????????????
          }
        },
        actionType() {
          if (this.paramData && this.paramData.actionType) {
            let actionType = this.paramData.actionType
            if (Object.values(ActionTypeEnum).includes(actionType)) {
              if (actionType === ActionTypeEnum.?????? && this.added) {
                return ActionTypeEnum.??????
              } else {
                return actionType
              }
            } else {
              let errorMsg = "actionType?????????????????????" + Object.values(ActionTypeEnum).join(",")
              console.error(errorMsg)
              throw errorMsg
            }
          } else {
            return null
          }
        },
        isReadOnly() {
          return [ActionTypeEnum.??????].some(v => this.actionType == v)
        },
        canEditConfig() {
          return !this.isReadOnly
        },
        propertyTree() {
          if (!this.attrDataOriginal) {
            return [
              {
                label: "??????????????????",
                value: "set1",
                children: [
                  {
                    label: "??????1",
                    value: "prop1"
                  },
                  {
                    label: "??????2",
                    value: "prop2",
                    children: [
                      {
                        label: "??????21",
                        value: "prop21"
                      },
                      {
                        label: "??????22asdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdas",
                        value: "prop22",
                        linkAttr: "1"
                      }
                    ]
                  },
                ]
              },
              {
                label: "??????**??????",
                value: "set2",
                children: [
                  {
                    label: "N/A",
                    value: "ATTR_NA"
                  },
                  {
                    label: "*??????2",
                    value: "_prop2",
                    children: [
                      {
                        label: "*??????21",
                        value: "_prop21"
                      },
                      {
                        label: "*??????22",
                        value: "_prop22"
                      }
                    ]
                  }
                ]
              }
            ]
          } else {
            return this.attrDataOriginal.map(v => {
              return {
                ...v,
                attributeCode: v.attributeTypeCode,
                attributeName: v.attributeTypeName
              }
            })
          }
        },
        propertyTreeProps() {
          if (!this.attrDataOriginal) {
            return {
              children: "children",
              label: "label",
              value: "value"
            }
          } else {
            return {
              children: "templateAttributes",
              label: "attributeName",
              value: "attributeCode"
            }
          }
        },
        propertyMap() {
          let map = {}
          let setOne = v => {
            let k = v[this.propertyTreeProps.value]
            map[k] = v
            let children = v[this.propertyTreeProps.children]
            if (children) {
              children.forEach(setOne)
            }
          }
          this.propertyTree.forEach(setOne)
          return map
        },
        setPropertyCommandName() {
          return this.ZSJCJYW_COMMAND_PREFIX + "setProperty"
        },
        deletePropertyCommandName() {
          return this.ZSJCJYW_COMMAND_PREFIX + "deleteProperty"
        },

        setAreaCommandName() {
          return this.ZSJCJYW_COMMAND_PREFIX + "setArea"
        },
        deleteAreaCommandName() {
          return this.ZSJCJYW_COMMAND_PREFIX + "deleteArea"
        },

        setCellPreposedCellControlsCommandName() {
          return this.ZSJCJYW_COMMAND_PREFIX + "setCellPreposedCellControls"
        },
        setCellPreposedAreaControlsCommandName() {
          return this.ZSJCJYW_COMMAND_PREFIX + "setCellPreposedAreaControls"
        },
        setCellEntirelyFillControlCommandName() {
          return this.ZSJCJYW_COMMAND_PREFIX + "setCellEntirelyFillControl"
        },
        deleteCellOrderControlsCommandName() {
          return this.ZSJCJYW_COMMAND_PREFIX + "deleteCellOrderControls"
        },

        setSettingDefaultValueCellControlsCommandName() {
          return this.ZSJCJYW_COMMAND_PREFIX + "setSettingDefaultValueCellControls"
        },
        setSettingDefaultValueAreaControlsCommandName() {
          return this.ZSJCJYW_COMMAND_PREFIX + "setSettingDefaultValueAreaControls"
        },
        deleteSettingDefaultValueControlsCommandName() {
          return this.ZSJCJYW_COMMAND_PREFIX + "deleteSettingDefaultValueControls"
        },

      },
      methods: {
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
        listenChanged(spread) {
          let commandManager = spread.commandManager()
          commandManager.addListener("__", (...args) => {
            this.changed = true
          })
        },
        getTemplateTypeInfo(templateTypeCode) {
          return new Promise((resolve, reject) => {
            axios.get("/apiTrainRepairMidGround/templateType/getTemplateTypeInfo", {
              params: {
                templateTypeCode
              }
            }).then(({ data: { data } }) => {
              resolve(data)
            }).catch(reject)
          })
        },
        getTemplateBasicData(pageNum) {
          let curSavedPageTemplateBasicData = this.getAllDataFromLocal(pageNum)[2]
          // ??????????????????????????????????????????????????????
          if (curSavedPageTemplateBasicData == null || JSON.stringify(curSavedPageTemplateBasicData) == "{}") {
            curSavedPageTemplateBasicData = this.getAllDataFromLocal(1)[2]
            if (curSavedPageTemplateBasicData) {
              curSavedPageTemplateBasicData.templateId = null
            } else {
              // ????????????????????????????????????????????????????????????????????????????????????
              curSavedPageTemplateBasicData = this.getTemplateBasicDataFromBillData(this.paramData.data)
            }
          }

          let billCount = this.billConfigSetting.billCount
          let curMarshallingType = billCount == 1 ? 3 : pageNum

          let templateBasicData = {
            marshallingType: curMarshallingType,
          }

          if (curSavedPageTemplateBasicData) {
            return { ...curSavedPageTemplateBasicData, ...templateBasicData }
          } else {
            return templateBasicData
          }
        },
        repairFlameWindowSize() {
          let clearKeyForNotZeroWidth = null
          let clearKey = () => {
            clearInterval(clearKeyForNotZeroWidth)
            clearKeyForNotZeroWidth = null
          }
          let getKey = () => {
            return clearKeyForNotZeroWidth
          }
          let setKey = (k) => {
            clearKeyForNotZeroWidth = k
          }
          let checkWindowWidth = () => {
            if (window.document.body.offsetWidth === 0 && !getKey()) {
              setKey(setInterval(() => {
                if (window.document.body.offsetWidth !== 0) {
                  clearKey()
                  GC.Spread.Sheets.Designer.wrapper.spread.refresh()
                }
              }, 40))
            }
          }
          wrapFn(GC.Spread.Sheets.Designer.wrapper.spread, "refresh", (ori, args) => {
            // isRefreshing = true
            try {
              checkWindowWidth()
              return ori()
            } catch (error) {
              throw error
            } finally {
              // isRefreshing = false
            }
          })
          wrapFn(GC.Spread.Sheets.Designer.wrapper.spread, "resumePaint", (ori, args) => {
            checkWindowWidth()
            return ori()
          })
        },
        getCellKey(...args) {
          return args.join(",")
        },
        getUser() {
          return new Promise((resolve, reject) => {
            axios.get("/apiTrainRepairMidGround/common/getUser").then(({ data: { data } }) => {
              resolve(data)
            }).catch(reject)
          })
        },
        getTemplateBasicDataFromBillData(billData) {
          return this.businessTypeConfig.getNewMainData(billData)
        },
        // ?????????????????????index.js????????????
        init() {
          if (app.isLocalTest) {
            this.paramData = {
              businessType: BusinessTypeEnum.????????????????????????,
              data: {
              }
            }
            this.templateTypeInfo = {
              moreCellFlag: "1",
              templateTypeName: "asd"
            }
            GC.Spread.Sheets.Designer.loader.customReady(() => {
              let spread = GC.Spread.Sheets.Designer.wrapper.spread
              this.repairFlameWindowSize()
              this.initBillModule(spread, {
                propertyTreeConfig: {
                  getCode(propertyObj) {
                    return propertyObj.value
                  },
                  getName(propertyObj) {
                    return propertyObj.label
                  },
                  getChildren(propertyObj) {
                    return propertyObj.children
                  }
                }
              })
              this.initCustomCommands(spread)

              let cellsDataStr = '[{"id":1009,"content":null,"attributeCode":"ATTR_NA","rowId":"5","colId":"8","controls":[],"linkContents":[]},{"id":1010,"content":null,"attributeCode":"ATTR_NA","rowId":"5","colId":"9","controls":[],"linkContents":[]},{"id":1011,"content":null,"attributeCode":"ATTR_NA","rowId":"5","colId":"10","controls":[],"linkContents":[]},{"id":1012,"content":null,"attributeCode":"ATTR_NA","rowId":"5","colId":"11","controls":[],"linkContents":[]},{"id":1209,"content":null,"attributeCode":"ATTR_NA","rowId":"6","colId":"8","controls":[],"linkContents":[]},{"id":1210,"content":null,"attributeCode":"ATTR_NA","rowId":"6","colId":"9","controls":[],"linkContents":[]},{"id":1211,"content":null,"attributeCode":"ATTR_NA","rowId":"6","colId":"10","controls":[],"linkContents":[]},{"id":1212,"content":null,"attributeCode":"ATTR_NA","rowId":"6","colId":"11","controls":[],"linkContents":[]},{"id":1409,"content":null,"attributeCode":"ATTR_NA","rowId":"7","colId":"8","controls":[],"linkContents":[]},{"id":1410,"content":null,"attributeCode":"ATTR_NA","rowId":"7","colId":"9","controls":[],"linkContents":[]},{"id":1411,"content":null,"attributeCode":"ATTR_NA","rowId":"7","colId":"10","controls":[],"linkContents":[]},{"id":1412,"content":null,"attributeCode":"ATTR_NA","rowId":"7","colId":"11","controls":[],"linkContents":[]},{"id":1609,"content":null,"attributeCode":"ATTR_NA","rowId":"8","colId":"8","controls":[],"linkContents":[]},{"id":1610,"content":null,"attributeCode":"ATTR_NA","rowId":"8","colId":"9","controls":[],"linkContents":[]},{"id":1611,"content":null,"attributeCode":"ATTR_NA","rowId":"8","colId":"10","controls":[],"linkContents":[]},{"id":1612,"content":null,"attributeCode":"ATTR_NA","rowId":"8","colId":"11","controls":[],"linkContents":[]},{"id":1809,"content":null,"attributeCode":"ATTR_NA","rowId":"9","colId":"8","controls":[],"linkContents":[]},{"id":1810,"content":null,"attributeCode":"ATTR_NA","rowId":"9","colId":"9","controls":[],"linkContents":[]},{"id":1811,"content":null,"attributeCode":"ATTR_NA","rowId":"9","colId":"10","controls":[],"linkContents":[]},{"id":1812,"content":null,"attributeCode":"ATTR_NA","rowId":"9","colId":"11","controls":[],"linkContents":[]},{"id":2009,"content":null,"attributeCode":"ATTR_NA","rowId":"10","colId":"8","controls":[],"linkContents":[]},{"id":2010,"content":null,"attributeCode":"ATTR_NA","rowId":"10","colId":"9","controls":[],"linkContents":[]},{"id":2011,"content":null,"attributeCode":"ATTR_NA","rowId":"10","colId":"10","controls":[],"linkContents":[]},{"id":2012,"content":null,"attributeCode":"ATTR_NA","rowId":"10","colId":"11","controls":[],"linkContents":[]},{"id":2209,"content":null,"attributeCode":"ATTR_NA","rowId":"11","colId":"8","controls":[],"linkContents":[]},{"id":2210,"content":null,"attributeCode":"ATTR_NA","rowId":"11","colId":"9","controls":[],"linkContents":[]},{"id":2211,"content":null,"attributeCode":"ATTR_NA","rowId":"11","colId":"10","controls":[],"linkContents":[]},{"id":2212,"content":null,"attributeCode":"ATTR_NA","rowId":"11","colId":"11","controls":[],"linkContents":[]},{"id":2409,"content":null,"attributeCode":"ATTR_NA","rowId":"12","colId":"8","controls":[],"linkContents":[]},{"id":2410,"content":null,"attributeCode":"ATTR_NA","rowId":"12","colId":"9","controls":[],"linkContents":[]},{"id":2411,"content":null,"attributeCode":"ATTR_NA","rowId":"12","colId":"10","controls":[],"linkContents":[]},{"id":2412,"content":null,"attributeCode":"ATTR_NA","rowId":"12","colId":"11","controls":[],"linkContents":[]},{"id":2609,"content":null,"attributeCode":"ATTR_NA","rowId":"13","colId":"8","controls":[],"linkContents":[]},{"id":2610,"content":null,"attributeCode":"ATTR_NA","rowId":"13","colId":"9","controls":[],"linkContents":[]},{"id":2611,"content":null,"attributeCode":"ATTR_NA","rowId":"13","colId":"10","controls":[],"linkContents":[]},{"id":2612,"content":null,"attributeCode":"ATTR_NA","rowId":"13","colId":"11","controls":[],"linkContents":[]},{"id":2809,"content":null,"attributeCode":"ATTR_NA","rowId":"14","colId":"8","controls":[],"linkContents":[]},{"id":2810,"content":null,"attributeCode":"ATTR_NA","rowId":"14","colId":"9","controls":[],"linkContents":[]},{"id":2811,"content":null,"attributeCode":"ATTR_NA","rowId":"14","colId":"10","controls":[],"linkContents":[]},{"id":2812,"content":null,"attributeCode":"ATTR_NA","rowId":"14","colId":"11","controls":[],"linkContents":[]},{"id":3009,"content":null,"attributeCode":"ATTR_NA","rowId":"15","colId":"8","controls":[],"linkContents":[]},{"id":3010,"content":null,"attributeCode":"ATTR_NA","rowId":"15","colId":"9","controls":[],"linkContents":[]},{"id":3011,"content":null,"attributeCode":"ATTR_NA","rowId":"15","colId":"10","controls":[],"linkContents":[]},{"id":3012,"content":null,"attributeCode":"ATTR_NA","rowId":"15","colId":"11","controls":[],"linkContents":[]},{"id":3209,"content":null,"attributeCode":"ATTR_NA","rowId":"16","colId":"8","controls":[],"linkContents":[]},{"id":3210,"content":null,"attributeCode":"ATTR_NA","rowId":"16","colId":"9","controls":[],"linkContents":[]},{"id":3211,"content":null,"attributeCode":"ATTR_NA","rowId":"16","colId":"10","controls":[],"linkContents":[]},{"id":3212,"content":null,"attributeCode":"ATTR_NA","rowId":"16","colId":"11","controls":[],"linkContents":[]}]'

              this.getBillModule().initAllData({
                propertyTree: this.propertyTree,
                propertyOptionList: [],
                ssjsonDataString: null,
                billData: {
                  cells: JSON.parse(cellsDataStr),
                  areas: [
                    {
                      "leftUp": "8,5",
                      "leftDown": "8,16",
                      "rightUp": "11,5",
                      "rightDown": "11,16",
                      "type": "3", "number": 1
                    },
                    {
                      "leftUp": "13,8",
                      "leftDown": "13,19",
                      "rightUp": "15,8",
                      "rightDown": "15,19",
                      "type": "4", "number": 2
                    }
                  ],
                }
              })
              this.billConfigSetting.billCount = 2
              this.billConfigSetting.isShowMode = true
              this.saveAllDataToLocal(1)
            })
            return Promise.resolve()
          } else {
            GC.Spread.Sheets.Designer.loader.customReady(() => {
              let spread = GC.Spread.Sheets.Designer.wrapper.spread
              this.repairFlameWindowSize()
              this.initBillModule(spread)
              this.initCustomCommands(spread)
              this.listenChanged(spread)
            })
            return new Promise((resolve, reject) => {
              let listener = (paramData) => {
                localStorageParamsBus.removeParamsListener(this.pageCode, "billEditParams", listener)
                this.paramData = paramData
                this.initData(paramData.data).then(() => {
                  app.initWindowSizeListenerForRefresh()
                  resolve()
                }).catch(reject)
              }
              localStorageParamsBus.addParamsListener(this.pageCode, "billEditParams", listener)
            })
          }
        },
        initBillModule(spread, exConfig) {
          let billModule = new BillSpreadModule()
          if (exConfig == null) {
            exConfig = {}
          }
          billModule.setConfig({
            configMode: true,
            ...exConfig
          })
          billModule.setSpread(spread)
          this.billModule = billModule
        },
        getBillModule() {
          return this.billModule
        },
        initCustomCommands(spread) {
          var Commands = GC.Spread.Sheets.Commands
          var wrapExecute = (context, options, isUndo, execute) => {
            if (isUndo) {
              try {
                Commands.undoTransaction(context, options)
              } catch (error) {
                console.error(error)
              }
            } else {
              Commands.startTransaction(context, options)
              context.suspendPaint()
              try {
                return execute()
              } catch (error) {
                console.error(error)
              } finally {
                context.resumePaint()
                Commands.endTransaction(context, options)
              }
            }
          }

          // ????????????????????????
          spread.commandManager().register(this.setPropertyCommandName, {
            canUndo: true,
            execute: (context, options, isUndo) => {
              return wrapExecute(context, options, isUndo, () => {
                var propertyKey = options.commandOptions.propertyKey
                var selections = options.selections
                for (var i = 0; i < selections.length; i++) {
                  var selection = selections[i]
                  this.getBillModule().setProperty({
                    rowIndex: selection.row,
                    columnIndex: selection.col,
                    rowCount: selection.rowCount,
                    columnCount: selection.colCount,
                    propertyKey,
                  })
                }
              })
            }
          })

          // ????????????????????????
          spread.commandManager().register(this.deletePropertyCommandName, {
            canUndo: true,
            execute: (context, options, isUndo) => {
              return wrapExecute(context, options, isUndo, () => {
                var selections = options.selections

                for (var i = 0; i < selections.length; i++) {
                  var selection = selections[i]
                  this.getBillModule().deleteProperty({ rowIndex: selection.row, columnIndex: selection.col, rowCount: selection.rowCount, columnCount: selection.colCount })
                }
              })
            }
          })

          // ????????????????????????
          spread.commandManager().register(this.setAreaCommandName, {
            canUndo: true,
            execute: (context, options, isUndo) => {
              return wrapExecute(context, options, isUndo, () => {
                var selections = options.selections
                var areaType = options.commandOptions.areaType
                for (var i = 0; i < selections.length; i++) {
                  var selection = selections[i]
                  this.getBillModule().setArea(selection.row, selection.col, selection.rowCount, selection.colCount, areaType)
                }
              })
            }
          })

          // ????????????????????????
          spread.commandManager().register(this.deleteAreaCommandName, {
            canUndo: true,
            execute: (context, options, isUndo) => {
              return wrapExecute(context, options, isUndo, () => {
                var selections = options.selections
                var areaType = options.commandOptions.areaType
                if (areaType === BillSpreadModule.AreaTypeEnum.DATA) {
                  // ????????????????????????????????????????????????????????????
                  let curAreaInfo = app.core.getBillModule().getArea(selections[0].row, selections[0].col, selections[0].rowCount, selections[0].colCount, areaType)
                  if (curAreaInfo) {
                    let ctrls = this.getBillModule().findControlsByTargetAreaId(curAreaInfo.id)
                    if (ctrls.length > 0) {
                      let areaTitle = this.getBillModule().getAreaTitle(curAreaInfo.id)
                      let getColName = (col) => {
                        return context.getActiveSheet().getText(0, col, GC.Spread.Sheets.SheetArea.colHeader)
                      }
                      app.showError(`???????????????????????????[${areaTitle}]?????????????????????????????????${ctrls.map(v => `${getColName(v.sourceCol)}${v.sourceRow + 1}`).join(",")}`)
                      return false
                    }
                  }
                }
                for (var i = 0; i < selections.length; i++) {
                  var selection = selections[i]
                  this.getBillModule().deleteArea(selection.row, selection.col, selection.rowCount, selection.colCount, areaType)
                }
              })
            }
          })

          let getSheet = (context, options) => {
            let sheetName = app.commonUtil.getSheetNameFromCommandOptions(options)
            return sheetName ? context.getSheetFromName(sheetName) : context.getActiveSheet()
          }
          let getCellsInSelectModeByCommand = (context, options, getInitCellPositions, endSelectMode) => {
            if (!app.isInSelectMode) {
              let core = this.getBillModule()
              let { row, col } = options.commandOptions
              let initCellPositions = getInitCellPositions(row, col)

              let backColorLayerManager = core.getBackColorLayerManager()
              let disableCellLayer = "disableCell"
              let disableCellColorObj = app.commonUtil.parseRgbString("rgba(255,16,0,0.5)")
              backColorLayerManager.pushRgbaBackColor(row, col, disableCellLayer, disableCellColorObj)

              let selectModeUtil = core.selectCellsInSelectMode(
                getSheet(context, options),
                initCellPositions
              )
              let svgMask = new SvgMask({
                elements: [
                  context.getHost(),
                  document.getElementById("styleShowModeCheckbox").parentElement
                ]
              })
              svgMask.appendToBody()
              let eventListener = () => {
                svgMask.refresh()
              }
              window.addEventListener("resize", eventListener)
              $(".ribbon-bar").bind("updateRibbonSize", eventListener)

              selectModeUtil.row = row
              selectModeUtil.col = col
              app.isInSelectMode = true

              wrapFn(selectModeUtil, "endSelectMode", (oriEndSelectMode, args) => {
                let cellPositions = selectModeUtil.getSelectedCellPositions()
                let end = () => {
                  backColorLayerManager.removeRgbaBackColor(row, col, disableCellLayer)
                  oriEndSelectMode()
                  svgMask.removeFromBody()
                  window.removeEventListener("resize", eventListener)
                  $(".ribbon-bar").unbind("updateRibbonSize", eventListener)
                  delete this.selectModeUtil
                  app.isInSelectMode = false
                }
                if (!cellPositions.every(([row, col], i) => {
                  return initCellPositions[i] && initCellPositions[i][0] == row && initCellPositions[i] && initCellPositions[i][1] == col
                })) {
                  setTimeout(() => {
                    endSelectMode(selectModeUtil.row, selectModeUtil.col, cellPositions)
                    end()
                  }, 0)
                  // app.showConfirm("???????????????????????????????????????").then(() => {
                  // }).finally(() => {
                  // })
                } else {
                  end()
                }
              })
              this.selectModeUtil = selectModeUtil
            } else {
              let { functionType } = options.commandOptions
              let cellFilter = (cellRow, cellCol) => {
                return !(cellRow == this.selectModeUtil.row && cellCol == this.selectModeUtil.col)
              }
              if (functionType == "select") {
                this.selectModeUtil.selectRanges(options.selections, cellFilter)
              } else if (functionType == "unselect") {
                this.selectModeUtil.unselectRanges(options.selections, cellFilter)
              } else if (functionType == "endSelect") {
                this.selectModeUtil.endSelectMode()
              }
            }
          }

          // ??????????????????????????????????????????
          spread.commandManager().register(this.setCellPreposedCellControlsCommandName + ".do", {
            canUndo: true,
            execute: (context, options, isUndo) => {
              return wrapExecute(context, options, isUndo, () => {
                this.getBillModule().setCellPreposedCellControls(...options.commandOptions)
              })
            }
          })
          // ??????????????????????????????????????????
          spread.commandManager().register(this.setCellPreposedCellControlsCommandName, {
            canUndo: false,
            execute: (context, options, isUndo) => {
              getCellsInSelectModeByCommand(
                context,
                options,
                (row, col) => {
                  let ctrls = this.getBillModule().getCellPreposedCellControls(row, col)
                  let initCellPositions = ctrls ? ctrls.map(v => [v.targetRow, v.targetCol]) : []
                  return initCellPositions
                },
                (row, col, cellPositions) => {
                  context.commandManager().execute({
                    cmd: this.setCellPreposedCellControlsCommandName + ".do",
                    sheetName: options.sheetName,
                    commandOptions: [row, col, cellPositions]
                  })
                }
              )
            }
          })
          // ????????????????????????
          spread.commandManager().register(this.setCellPreposedAreaControlsCommandName, {
            canUndo: true,
            execute: (context, options, isUndo) => {
              return wrapExecute(context, options, isUndo, () => {
                let { row, col, areaId } = options.commandOptions
                this.getBillModule().setCellPreposedAreaControls(row, col, [areaId])
              })
            }
          })
          // ???????????????????????????
          spread.commandManager().register(this.setCellEntirelyFillControlCommandName, {
            canUndo: true,
            execute: (context, options, isUndo) => {
              return wrapExecute(context, options, isUndo, () => {
                let { row, col } = options.commandOptions
                this.getBillModule().setCellEntirelyFillControl(row, col)
              })
            }
          })
          // ????????????????????????
          spread.commandManager().register(this.deleteCellOrderControlsCommandName, {
            canUndo: true,
            execute: (context, options, isUndo) => {
              return wrapExecute(context, options, isUndo, () => {
                let { row, col, functionName } = options.commandOptions
                this.getBillModule()[functionName](row, col)
              })
            }
          })


          // ???????????????????????????????????????????????????
          spread.commandManager().register(this.setSettingDefaultValueCellControlsCommandName + ".do", {
            canUndo: true,
            execute: (context, options, isUndo) => {
              return wrapExecute(context, options, isUndo, () => {
                this.getBillModule().setSettingDefaultValueCellControls(...options.commandOptions)
              })
            }
          })
          // ???????????????????????????????????????????????????
          spread.commandManager().register(this.setSettingDefaultValueCellControlsCommandName, {
            canUndo: false,
            execute: (context, options, isUndo) => {
              getCellsInSelectModeByCommand(
                context,
                options,
                (row, col) => {
                  let ctrls = this.getBillModule().getSettingDefaultValueCellControls(row, col)
                  let initCellPositions = ctrls ? ctrls.map(v => [v.targetRow, v.targetCol]) : []
                  return initCellPositions
                },
                (row, col, cellPositions) => {
                  context.commandManager().execute({
                    cmd: this.setSettingDefaultValueCellControlsCommandName + ".do",
                    sheetName: options.sheetName,
                    commandOptions: [row, col, cellPositions]
                  })
                }
              )
            }
          })
          // ?????????????????????????????????
          spread.commandManager().register(this.setSettingDefaultValueAreaControlsCommandName, {
            canUndo: true,
            execute: (context, options, isUndo) => {
              return wrapExecute(context, options, isUndo, () => {
                let { row, col, areaId } = options.commandOptions
                this.getBillModule().setSettingDefaultValueAreaControls(row, col, [areaId])
              })
            }
          })
          // ?????????????????????????????????
          spread.commandManager().register(this.deleteSettingDefaultValueControlsCommandName, {
            canUndo: true,
            execute: (context, options, isUndo) => {
              return wrapExecute(context, options, isUndo, () => {
                let { row, col, functionName } = options.commandOptions
                this.getBillModule()[functionName](row, col)
              })
            }
          })
        },
        initData(billDataAsParams) {
          return new Promise((resolve, reject) => {
            let containerEl = document.querySelector("body>.container")
            let classNamePro = containerEl.className
            containerEl.className = classNamePro + " hide-content"
            Promise.all([
              // ????????????????????????
              this.getAttrs(billDataAsParams.templateTypeCode),
              this.getAttrOptions(billDataAsParams.templateTypeCode),
              // ??????????????????
              this.getTemplateData((() => {
                let params = {
                  templateId: billDataAsParams.templateId
                }
                if (this.businessTypeConfig.canRelease) {
                  params.state = billDataAsParams.state
                }
                return params
              })()),
              // // ??????????????????
              // this.getUser(),
              // ????????????????????????
              this.getTemplateTypeInfo(billDataAsParams.templateTypeCode)
            ]).then(([attrs, attrOptions, templateData/* , userData */, templateTypeInfo]) => {
              // this.userData = userData
              this.templateTypeInfo = templateTypeInfo
              GC.Spread.Sheets.Designer.loader.customReady(() => {
                debugPaint()
                let loading = this.$loading()
                this.attrDataOriginal = attrs
                this.attrOptionsDataOriginal = attrOptions
                this.getBillModule().setConfig({
                  readOnly: this.isReadOnly
                })
                this.getBillModule().setPropertyData({
                  propertyTree: attrs,
                  propertyOptionList: attrOptions
                })
                // ??????????????????
                this.loadTemplatesFromServerData(templateData).then(() => {
                  if (!this.isMultiSheet && templateData.length > 1) {
                    window.alert("???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????")
                  }
                  setTimeout(() => {
                    containerEl.className = classNamePro
                    GC.Spread.Sheets.Designer.wrapper.spread.refresh()
                    this.changed = false
                  }, 0)
                }).finally(() => {
                  loading.close()
                })
              })
              resolve()
            }).catch(reject)
          })
        },
        getAttrOptions(billTypeCode) {
          return new Promise((resolve, reject) => {
            axios.get("/apiTrainRepairMidGround/common/getTemplateValues", {
              params: {
                billTypeCode: billTypeCode
              }
            }).then(({ data }) => {
              resolve(data.data)
            }).catch(reject)
          })
        },
        getAttrs(billTypeCode) {
          return new Promise((resolve, reject) => {
            axios.get("/apiTrainRepairMidGround/common/getConfigAttr", {
              params: {
                billTypeCode: billTypeCode
              }
            }).then(({ data }) => {
              if (data.data) {
                resolve(data.data)
              } else {
                resolve([])
              }
            }).catch(reject)
          })
        },
        getTemplateData(param) {
          return new Promise((resolve, reject) => {
            if (this.actionType === ActionTypeEnum.??????) {
              resolve([])
              // axios.get("/apiTrainRepairMidGround/billConfig/getBaseBillConfig", {
              //   params: {
              //     billTypeCode: param.templateTypeCode
              //   }
              // }).then(({ data }) => { resolve(data.rows) }).catch(reject)
            } else {
              this.businessTypeConfig.queryTemplateDetail(param).then(({ data }) => {
                if (data.data.length === 0) {
                  let msg = "??????????????????"
                  this.$message.error(msg)
                  reject(msg)
                } else {
                  resolve(data.data)
                }
              }).catch(reject)
            }
          })

        },
        getSsjsonFileByPath(templatePath) {
          if (ssjsonCatch.has(templatePath)) {
            return Promise.resolve(ssjsonCatch.get(templatePath))
          } else {
            return new Promise((resolve, reject) => {
              axios.post("/apiTrainRepairMidGround/billConfig/getSSJsonFileContent", app.toFromData({
                templatePath
              })).then(({ data }) => {
                ssjsonCatch.set(templatePath, data.data)
                resolve(data.data)
              }).catch(reject)
            })
          }
        },
        async loadTemplatesFromServerData(templates) {
          let spread = GC.Spread.Sheets.Designer.wrapper.spread
          spread.suspendPaint()
          for (let i = 0; i < templates.length; i++) {
            const templateInfo = templates[i]
            let page = getPageByMarshallingType(templateInfo.marshallingType)
            if (page == null) {
              page = i + 1
            }
            let templateJson = null
            try {
              templateJson = await this.getSsjsonFileByPath(templateInfo.templatePath)
            } catch (error) {
              console.error(error)
            }
            let cells = templateInfo.contents
            let areas = templateInfo.areas || []

            let templateJsonObj = templateJson ? JSON.parse(templateJson) : null
            this.setAllDataToLocal(page, [
              templateJsonObj,
              [cells, areas],
              this._getTemplateBasicData(templateInfo)
            ])
            if (!this.isReadOnly && templateJsonObj) {
              this.$set(this.show??????warnMessageByPage, page - 1, this.checkSsjsonDataHasValueStartsWith(templateJsonObj, "??????"))
            }
          }
          if (this.actionType === ActionTypeEnum.??????) {
            this.billConfigSetting.billCount = this.allowBillCount
          } else {
            this.billConfigSetting.billCount = templates.length > 0 ? templates.length : 1
          }
          // ?????????????????????????????????????????????
          this.readAllDataFromLocal(1, true)
          spread.resumePaint()
          this.$nextTick(() => {
            this.billConfigSetting.isShowMode = true
          })
        },
        getRowsAndColumnsFromSsjsonSheet(sheet) {
          let rowCount = sheet.rowCount == null ? 200 : sheet.rowCount
          let columnCount = sheet.columnCount == null ? 20 : sheet.columnCount
          return [rowCount, columnCount]
        },
        getNamedStylesMap(ssjsonObj) {
          let map = {}
          if (ssjsonObj && ssjsonObj.namedStyles) {
            ssjsonObj.namedStyles.forEach(style => {
              map[style.name] = style
            })
          }
          return map
        },
        setLockedToSsjsonTable(dataTable, row, col, locked, namedStylesMap) {
          if (locked === true) {
            let cellExist = dataTable[row] != null && dataTable[row][col] != null
            if (cellExist) {
              let cell = dataTable[row][col]
              if (cell.style) {
                if (typeof cell.style == "string") {
                  delete namedStylesMap[cell.style].locked
                } else {
                  delete cell.style.locked
                  let _objStr = JSON.stringify({})
                  if (JSON.stringify(cell.style) === _objStr) {
                    delete cell.style
                    if (JSON.stringify(cell) === _objStr) {
                      delete dataTable[row][col]
                      if (Object.keys(dataTable[row]).length === 0) {
                        delete dataTable[row]
                      }
                    }
                  }
                }
              }
            }
          } else if (locked === false) {
            if (!dataTable[row]) {
              dataTable[row] = {}
            }
            if (!dataTable[row][col]) {
              dataTable[row][col] = {}
            }
            let cell = dataTable[row][col]
            if (!cell.style) {
              cell.style = {}
            }
            if (typeof cell.style == "string") {
              namedStylesMap[cell.style].locked = false
            } else {
              cell.style.locked = false
            }
          }
        },
        getFirstSheetNameFromJsonObj(sheets) {
          return Object.keys(sheets).find(k => sheets[k].index == 0)
        },
        getCellPosFromCellData(cell) {
          let row = null
          let col = null
          row = parseInt(cell.rowId)
          col = parseInt(cell.colId)
          if (isNaN(row) || isNaN(col)) {
            throw {
              msg: "?????????????????????????????????",
              data: cell
            }
          }
          return [row, col]
        },
        _getTemplateBasicData(proTemplateData) {
          let templateBasicData = { ...proTemplateData }
          delete templateBasicData.contents
          delete templateBasicData.areas
          return templateBasicData
        },
        _getAreaData(proAreaData) {
          return proAreaData.map(({ leftUp, rightDown }) => {
            let [leftX, upY] = leftUp.split(",").map(v => parseInt(v))
            let [rightX, downY] = rightDown.split(",").map(v => parseInt(v))
            return {
              x: leftX,
              y: upY,
              width: rightX - leftX + 1,
              height: downY - upY + 1
            }
          })
        },
        saveBillConfig() {
          let needAlert??????WarnMessage = false
          let doCheck = () => {
            this.forEachAllCurrentData(({ ssjsonData, pageNum }) => {
              this.$set(this.show??????warnMessageByPage, pageNum - 1, this.checkSsjsonDataHasValueStartsWith(ssjsonData, "??????"))
            })
          }
          if (!this.show??????warnMessage) {
            doCheck()
            if (this.show??????warnMessage) {
              needAlert??????WarnMessage = true
            }
          } else {
            doCheck()
          }
          if (needAlert??????WarnMessage) {
            return app.showWarn(this.??????warnMessage).then(() => this.saveBillConfigInner())
          } else {
            return this.saveBillConfigInner()
          }
        },
        saveBillConfigInner() {
          return new Promise((resolve, reject) => {
            let spread = GC.Spread.Sheets.Designer.wrapper.spread
            // ????????????
            spread.suspendPaint()
            // ????????????
            this.saveAllDataToLocal()
            try {
              let templates = []
              this._getArray(Math.min(this.allowBillCount, this.billConfigSetting.billCount)).map((v, i) => i + 1).map(pageNum => {

                // ??????????????????billSpreadModule
                this.readAllDataFromLocal(pageNum)
                // ????????????
                let vi = this.getBillModule().validateTemplate()
                if (!vi.pass) {
                  this.$message({
                    type: "error",
                    message: vi.msg,
                    showClose: true
                  })
                  throw vi.msg
                }
                let templateBasicData = this.getTemplateBasicData(pageNum)

                // ???billSpreadModule???????????????????????????????????????????????????
                let { ssjsonDataString, billData: { cells, areas } } = this.getBillModule().getTemplateData()
                templateBasicData.ssjsonFile = ssjsonDataString
                // ????????????
                if (ssjsonCatch.has(templateBasicData.templatePath)) {
                  ssjsonCatch.set(templateBasicData.templatePath, templateBasicData.ssjsonFile)
                }
                // ?????????????????????
                templateBasicData.contents = cells
                templateBasicData.areas = areas
                templates.push(templateBasicData)
              })
              let loading = this.$loading()
              this.businessTypeConfig.saveTemplate(templates).then(({ data }) => {
                if (data.data) {
                  let newTemplateIds = data.data
                  if (this.actionType === ActionTypeEnum.??????) {
                    this.added = true
                  }
                  // id??????????????????????????????????????????????????????id??????
                  if (this.templateIds.length != newTemplateIds.length) {
                    // ??????????????????
                    return this.updateBasicData(newTemplateIds.join(","))
                  } else {
                    // ??????id
                    this.templateIds = newTemplateIds
                  }
                }
              }).then(() => {
                this.changed = false
                this.$message({
                  type: "success",
                  message: "????????????",
                  showClose: true
                })
                loading.close()
                localStorageParamsBus.sendParams(this.pageCode, "refreshList", true)
                resolve()
              }).catch((...args) => {
                loading.close()
                reject(...args)
              })
            } catch (error) {
              reject()
              console.error(error)
            } finally {
              // ??????????????????
              this.readAllDataFromLocal(this.billConfigSetting.curBillNum)
              // ????????????
              spread.resumePaint()
            }
          })
        },
        updateBasicData(templateId) {
          return new Promise((resolve, reject) => {
            this.getTemplateData({
              templateId: templateId,
              state: 0//0????????????1????????????
            }).then((data) => {
              let setPages = []
              data.forEach((templateInfo, i) => {
                let page = getPageByMarshallingType(templateInfo.marshallingType)
                if (page == null) {
                  page = i + 1
                }
                let [ssjson, [cells, areas]] = this.getAllDataFromLocal(page)
                let templateBasicData = this._getTemplateBasicData(templateInfo)
                this.setAllDataToLocal(page, [
                  ssjson,
                  [cells, areas],
                  templateBasicData
                ])
                setPages.push(page)
              })
              // ????????????????????????
              this._getArray(this.billConfigSetting.billCount).map((v, i) => i + 1).forEach((page) => {
                if (!setPages.includes(page)) {
                  this.setAllDataToLocal(page, [
                    null,
                    null,
                    null
                  ])
                }
              })
              resolve()
            }).catch(reject)
          })
        },

        _objMap(treeData, propsMap, extraSet) {
          return treeData.map(node => {
            let newObj = {}
            for (const key in node) {
              let value = node[key]
              if (Array.isArray(value)) {
                value = this._objMap(value, propsMap, v => extraSet(v, node))
              }
              if (propsMap[key]) {
                newObj[propsMap[key]] = value
              } else {
                newObj[key] = value
              }
            }
            extraSet(newObj)
            return newObj
          })
        },
        _getArray(length, fillObj) {
          let arr = new Array(length).fill(null)
          if (fillObj != null) {
            return arr.map(() => (copyDataSimple(fillObj)))
          } else {
            return arr
          }
        },

        getPropertyMenuItems() {// TODO ?????????????????????????????????????????????(???????????????)
          return this._objMap(this.propertyTree, {
            [this.propertyTreeProps.children]: "subMenu",
            [this.propertyTreeProps.label]: "text",
            [this.propertyTreeProps.value]: "name"
          }, (obj, pObj) => {
            if (!obj.subMenu) {
              obj.command = this.setPropertyCommandName
            }
            obj.options = { propertyKey: obj.name }
            if (pObj) {
              obj.name = this.ZSJCJYW_MENUITEM_PREFIX + pObj.name + "." + obj.name
            } else {
              obj.name = this.ZSJCJYW_MENUITEM_PREFIX + obj.name
            }
          })
        },
        saveAllDataToLocal(pageNum) {
          if (pageNum == null) {
            pageNum = this.billConfigSetting.curBillNum
          }
          let { ssjsonDataString, billData: { cells, areas } } = this.getBillModule().getTemplateData(true)
          this.setAllDataToLocal(pageNum, [ssjsonDataString == null ? null : JSON.parse(ssjsonDataString), [cells, areas]])
        },
        readAllDataFromLocal(pageNum, init) {
          let [ssjsonData, [cells, areas]] = this.getAllDataFromLocal(pageNum)
          // ????????????????????????????????????
          this.getBillModule().setTemplateData({
            ssjsonDataString: ssjsonData == null ? null : JSON.stringify(ssjsonData),
            billData: {
              cells,
              areas,
            }
          }, !init)
        },
        getAllDataFromLocal(pageNum) {
          let ssjsonData = this.billJSONData[pageNum - 1]
          let exData = this.billExData[pageNum - 1]
          let billBasicData = this.billBasicData[pageNum - 1]
          let get = (v, defaultData) => {
            if (v == null || v == "null") {
              if (defaultData != null) {
                return defaultData
              } else {
                return null
              }
            } else {
              return JSON.parse(v)
            }
          }
          return [get(ssjsonData), get(exData, [[], []]), get(billBasicData)]
        },
        setAllDataToLocal(pageNum, [ssjsonData, exData, billBasicData]) {
          let stringify = (data) => {
            return data == null ? null : JSON.stringify(data)
          }
          this.$set(this.billJSONData, pageNum - 1, stringify(ssjsonData))
          this.$set(this.billExData, pageNum - 1, stringify(exData))
          if (billBasicData) {
            this.$set(this.billBasicData, pageNum - 1, stringify(billBasicData))
          }
        },
        // ??????????????????????????????????????????
        afterSaveConfig(msg, callback) {
          if (this.needSave) {
            app.showConfirm(msg).then(() => {
              return this.saveBillConfig()
            }).then(() => {
              callback()
            })
          } else {
            callback()
          }
        },
        copyBillConfig() {
          this.afterSaveConfig("?????????????????????????????????????????????????????????", () => {
            this.copyBillConfigInner()
          })
        },
        elConfirm(msg) {
          return new Promise((resolve, reject) => {
            this.$confirm(msg, '??????', {
              confirmButtonText: '??????',
              cancelButtonText: '??????',
              closeOnClickModal: false,
              closeOnPressEscape: false,
              type: 'warning'
            }).then(function () {
              reject()
            }).catch(function () {
              resolve()
            })
          })
        },
        copyBillConfigInner() {
          window.copyBillConfigDialog.copyBillConfig((paramData, closeDialog) => {
            this.elConfirm("?????????????????????").then(v => {
              let proBillCount = this.billConfigSetting.billCount
              this.paramData = paramData
              let curBillCount = this.billConfigSetting.billCount = this.isMultiSheet ? 2 : 1
              this._getArray(curBillCount).map((v, i) => i + 1).forEach(page => {
                // ??????????????????
                if (page <= proBillCount) {
                  // ???????????????????????????????????????????????????
                  let [ssjson, [propertyData, areaData]] = this.getAllDataFromLocal(page)
                  this.setAllDataToLocal(page, [ssjson, [propertyData, areaData], null])
                } else {
                  // ????????????????????????????????????????????????????????????
                  this.setAllDataToLocal(page, [
                    null,
                    null,
                    null
                  ])
                }
              })
              this.changed = true
              this.readAllDataFromLocal(this.billConfigSetting.curBillNum)
              closeDialog()
            })
          })
        },
        checkSsjsonDataHasValueNotEmpty(ssjsonData) {
          return this.checkSsjsonDataAnyMatched(ssjsonData, (value) => {
            return value != null && value != ""
          })
        },
        checkSsjsonDataHasValueStartsWith(ssjsonData, startStr) {
          return this.checkSsjsonDataAnyMatched(ssjsonData, (value) => {
            return value != null && value.startsWith && value.startsWith(startStr)
          })
        },
        checkSsjsonDataAnyMatched(ssjsonData, matcher) {
          let tempSpread = new GC.Spread.Sheets.Workbook(document.createElement("div"))
          tempSpread.fromJSON(ssjsonData)
          let sheet = tempSpread.getActiveSheet()
          let matched = false
          app.commonUtil.executeFnUseForceEnd((forceEnd) => {
            app.commonUtil.everyCell(0, 0, sheet.getRowCount(), sheet.getColumnCount(), (row, col) => {
              let value = sheet.getValue(row, col)
              if (matcher(value)) {
                matched = true
                forceEnd()
              }
            })
          })
          tempSpread.destroy()
          return matched
        },
        forEachAllCurrentData(callback) {
          this._getArray(this.billConfigSetting.billCount).map((v, i) => i + 1).forEach(pageNum => {
            let ssjsonData = null
            let cells = null
            if (this.billConfigSetting.curBillNum == pageNum) {
              let { ssjsonDataString, billData: { cells: cells_, areas } } = this.getBillModule().getTemplateData(true)
              ssjsonData = JSON.parse(ssjsonDataString)
              cells = cells_
            } else {
              let [ssjsonData_, [cells_, areas]] = this.getAllDataFromLocal(pageNum)
              ssjsonData = ssjsonData_
              cells = cells_
            }
            callback({ ssjsonData, cells, pageNum })
          })
        },
        allCurrentDataAnyMatch(matcher) {
          let matched = false
          app.commonUtil.executeFnUseForceEnd((forceEnd) => {
            this.forEachAllCurrentData((currentData) => {
              if (matcher(currentData)) {
                matched = true
                forceEnd()
              }
            })
          })
          return matched
        },
        publishBillConfig() {
          if (this.allCurrentDataAnyMatch(({ ssjsonData, cells }) => {
            if ((!cells || cells.every(v => !v.content)) && (!ssjsonData || !this.checkSsjsonDataHasValueNotEmpty(ssjsonData))) {
              return true
            } else {
              return false
            }
          })) {
            app.showError("??????????????????????????????????????????")
            return
          }

          this.afterSaveConfig("????????????????????????????????????????????????????????????", () => {
            this.publishBillConfigInner()
          })
        },
        publishBillConfigInner() {
          let loading = this.$loading()
          let templateId = this.templateId
          this.businessTypeConfig.releaseTemplate(templateId).then((res) => {
            return this.updateBasicData(templateId)
          }).then(() => {
            localStorageParamsBus.sendParams(this.pageCode, "refreshList", true)
            this.$message.success("???????????????")
          }).finally(() => {
            loading.close()
          })
        },
        importExcel(ssjsonData) {
          this.getBillModule().setTemplateData({
            ssjsonDataString: JSON.stringify(ssjsonData),
            billData: {
              cells: [],
              areas: []
            }
          })
          let pageNum = this.billConfigSetting.curBillNum
          this.setAllDataToLocal(pageNum, [
            ssjsonData,
            null,
            this.getAllDataFromLocal(pageNum)[2]
          ])
          this.changed = true
          this.$set(
            this.show??????warnMessageByPage,
            pageNum - 1,
            this.checkSsjsonDataHasValueStartsWith(ssjsonData, "??????")
          )
        }
      },
      watch: {
        highlightInvalidData: function (v) {
          let spread = GC.Spread.Sheets.Designer.wrapper.spread
          spread.options.highlightInvalidData = v
        },
        "billConfigSetting.billCount": function (v) {
          if (v == 1) {
            this.billConfigSetting.curBillNum = 1
          }
          this.changed = true
        },
        // ????????????????????????????????????
        "billConfigSetting.curBillNum": function (toNum, fromNum) {
          let spread = GC.Spread.Sheets.Designer.wrapper.spread
          // ????????????
          spread.suspendPaint()

          // ???????????????
          this.saveAllDataToLocal(fromNum)
          this.readAllDataFromLocal(toNum)

          // ????????????
          spread.resumePaint()
        },
        "billConfigSetting.isShowMode": function (v) {
          this.getBillModule().setIsShowMode(v)
        }
      }
    }),
    toFromData(obj) {
      let formData = new FormData()
      app.objectDeepIterator(obj, (v, path) => {
        formData.append(path, v)
      })
      return formData
    },
    objectDeepIterator(obj, callback, path) {
      if (path == null) {
        path = ""
      }
      let getPrefix = p => {
        if (path == "") {
          return p
        } else if (Array.isArray(obj)) {
          return "[" + p + "]"
        } else {
          return "." + p
        }
      }
      for (const key in obj) {
        if (obj.hasOwnProperty(key)) {
          const value = obj[key]
          let curPath = path + getPrefix(key)
          if (value && (Array.isArray(value) || value.__proto__ === app.__proto__)) {
            app.objectDeepIterator(value, callback, curPath)
          } else {
            callback(value, curPath)
          }
        }
      }
    },
    showError(message) {
      let designer = GC.Spread.Sheets.Designer
      return new Promise((resolve, reject) => {
        designer.MessageBox.show(message, designer.res.title, 3 /* error */, 0 /* ok */, resolve)
      })
    },
    showWarn(message) {
      let designer = GC.Spread.Sheets.Designer
      return new Promise((resolve, reject) => {
        designer.MessageBox.show(message, designer.res.title, 2 /* warn */, 0 /* ok */, resolve)
      })
    },
    showConfirm(message) {
      let designer = GC.Spread.Sheets.Designer
      return new Promise((resolve, reject) => {
        designer.MessageBox.show(message, "????????????", 2 /* warning */, 1 /* okCancel */, function (event, result) {
          if (result === 1 /* ok */) {
            resolve()
          } else {
            reject()
          }
        })
      })
    },
    close() {
      if (top !== window) {
        let curIframe = Array.from(top.document.querySelectorAll("iframe")).find(v => {
          return v.contentWindow === window
        })
        if (curIframe && top.HussarTab && top.HussarTab.tabDelete) {
          top.HussarTab.tabDelete(curIframe.getAttribute("tab-id"))
          return
        }
      }
      window.close()
    },
    refreshFullWindow() {
      $(".ribbon-bar").gcuitabs("hideAllTabs")
      $(".ribbon-bar").gcuitabs("select", "homeTab")
      $(".ribbon-bar").gcuiribbon("updateRibbonSize")
      GC.Spread.Sheets.Designer.wrapper.spread.refresh()
    },
    initWindowSizeListenerForRefresh() {
      // let timeout = 1000 * 60
      // let startTime = Date.now()
      let clearKeyForZeroWidth = setInterval(() => {
        if (window.document.body.offsetWidth === 0) {
          clearInterval(clearKeyForZeroWidth)
          clearKeyForZeroWidth = null
          GC.Spread.Sheets.Designer.loader.customReady(() => {
            let clearKeyForNotZeroWidth = setInterval(() => {
              if (window.document.body.offsetWidth !== 0) {
                clearInterval(clearKeyForNotZeroWidth)
                this.refreshFullWindow()
              }
            }, 40)
          })
        }/*  else if (Date.now() > timeout + startTime) {
          clearInterval(clearKeyForZeroWidth)
        } */
      }, 50)
      GC.Spread.Sheets.Designer.loader.customReady(() => {
        setTimeout(() => {
          if (clearKeyForZeroWidth) {
            clearInterval(clearKeyForZeroWidth)
            clearKeyForZeroWidth = null
          }
        }, 1000)
      })
    },
    commonUtil: BillSpreadModule.commonUtil
  }
})()