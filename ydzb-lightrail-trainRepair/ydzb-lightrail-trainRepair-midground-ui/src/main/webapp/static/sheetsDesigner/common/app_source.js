var app = (function() {
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
      ['\\', '/'].forEach(function(c) {
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
      reader.onload = function() {
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
        extensions = patterns.split(' ').map(function(v) {
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
    excelIo.open(buffer, function(json) {
      callback && callback({
        status: 'success',
        data: json,
        file: file
      })
    }, function(err) {
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
    excelIo.save(spreadJson, function(data) {
      callback && callback({
        status: 'success',
        data: data
      })
    }, function(err) {
      callback && callback({
        status: 'failed',
        errorData: err
      })
    }, options)
  }

  // 张天可
  axios.interceptors.response.use(res => {
    if (res.status == 200 && (res.data.code == 1 || (res.data.msg && res.data.msg.includes("成功")) || (Object.keys(res.data).length == 1 && res.data.data) || !res.headers["content-type"].includes("json"))) {
      return res
    } else {
      let msg = (res.data && res.data.msg) || "请求失败"
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
        throw "throttle参数错误"
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
      return function(...args) {
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
    // 以包装的方式扩展对象方法
    let ori = obj[name]
    obj[name] = function(...args) {
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

  // 定义校验结果对象
  function ValidateResult(isVd, msg) {
    this.isVd = isVd
    this.msg = msg
  }
  ValidateResult.prototype.isValidated = function() {
    return !!this.isVd
  }
  ValidateResult.prototype.getMessage = function() {
    return this.msg
  }

  let marshallingTypeNameMap = {
    "1": "前",
    "2": "后",
    "3": "全部"
  }

  let getPageByMarshallingType = (type) => {
    if (type == "1") {
      return 1
    } else if (type == "2") {
      return 2
    } else {
      return null
    }
  }

  // 用于缓存ssjson文件内容
  let ssjsonCatch = new Map()

  var PageMode = {
    模版配置: 1,
    单据回填: 2,
    单据导出: 3
  }

  // 返回获取表格内滚轮是否滚动的状态，能够优先于spread对于鼠标滚轮的监听
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

  return {
    showSaveDialog: function(options, callback) {
      if (!_saveAsDialog) {
        _saveAsDialog = new GC.Spread.Sheets.Designer.SaveAsDialog()
      }
      var fileExtension = options.fileExtension || getFilters(options.nameFilters)[0].extensions[0]
      _saveAsDialog.open({
        fileExtension: fileExtension,
        value: app.core.exportFileName,
        done: function(result) {
          var fileName = result && result.fileName, cancelled = result && result.cancelled
          if (cancelled || !fileName) {
            callback({ status: 'cancelled' })
          } else {
            callback({ status: 'success', fileName: fileName + fileExtension })
          }
        }
      })
    },
    showOpenDialog: function(options, callback, importOptions) {
      if (!$emptyFileSelector) {
        $emptyFileSelector = $("#fileSelector").clone()
      } else {
        // clear (with cloned object (.val("") not works for IE)) to make sure change event occurs even when same file selected again
        $("#fileSelector").replaceWith($emptyFileSelector.clone())
      }
      if (!_fileSelectorEventAdded) {
        $(document.body).on("change", "#fileSelector", function() {
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
    save: function(fileName, saveData, callback, options) {
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
          exportExcel(data, options, function(result) {
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
          saveData.spread.savePDF(function(blob) {
            saveAs(blob, fileName)
            callback({ status: 'success' })
          }, function(ex) {
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
    notify: function() { return "" },
    openUrl: function() { return false },
    notifyReady: function() { },
    getFileInfo: getFileInfo,
    getExtension: getExtension,
    // 张天可
    isInSelectMode: false,
    PageMode,
    // 使用vue作为数据中心、事件中心和操作中心
    core: new Vue({
      data: {
        ZSJCJYW_COMMAND_PREFIX: "zsjcjyw.command.",
        ZSJCJYW_MENUITEM_PREFIX: "zsjcjyw.menuItem.",

        cellPropertyData: [],// 单元格属性数据，二维数组

        attrDataOriginal: null,
        attrOptionsDataOriginal: null,
        billConfig: {
          billCount: 1,
          curBillNum: 1,
          isShowMode: false, // 是否为查看模式（将数据区域和单元格属性显示成独特样式）
          saveBtnDisabled: false,
          billCountRadioDisabled: true
        },
        billJSONData: [],
        billExData: [],
        billBasicData: [],
        pageCode: "billConfig",
        paramData: null,// actionType: "edit","view","add","backfill","export"

        userData: null,

        changed: false,// 用于标记当前单据是否发生改变

        highlightInvalidData: false,// 用于标记是否显示校验错误

        getIsWheelingOnActiveSheet: null,// 判断当前表格是否有滚轮在滚动
      },
      computed: {
        templateBasicData() {
          return this.getTemplateBasicData(this.billConfig.curBillNum)
        },
        exportFileName() {
          let fileName = ""
          if (this.mode === PageMode.单据回填 || this.mode === PageMode.单据导出) {
            let billData = this.templateBasicData
            fileName += billData.dayPlanId
            fileName += "_" + billData.trainSetId
            fileName += "_" + billData.itemName
            let typeName = marshallingTypeNameMap[billData.marshallingType]
            if (typeName != "全部") {
              fileName += "_" + typeName
            }
          }
          return fileName
        },
        templateId() {
          let ids = []
          for (let i = 0; i < this.billConfig.billCount; i++) {
            let templateBasicDataStr = this.billBasicData[i]
            if (templateBasicDataStr) {
              let templateBasicData = JSON.parse(templateBasicDataStr)
              if (templateBasicData && templateBasicData.templateId) {
                ids.push(templateBasicData.templateId)
              }
            }
          }
          return ids.join(",")
        },
        // userNameMap() {
        //   let map = {}
        //   if (this.mode === PageMode.单据回填 || this.mode === PageMode.单据导出) {
        //     this.billExData.forEach(v => {
        //       let [details] = JSON.parse(v)
        //       details.forEach(detail => {
        //         if (this.isSignAttribute(detail.attributeCode) && detail.code && detail.value) {
        //           let userCodes = detail.code.split(",")
        //           let userNames = detail.value.split(",")
        //           for (let i = 0; i < userCodes.length; i++) {
        //             if (!map[userCodes[i]]) {
        //               map[userCodes[i]] = userNames[i]
        //             }
        //           }
        //         }
        //       })
        //     })
        //   }
        //   if (!map[this.userCode]) {
        //     map[this.userCode] = this.userName
        //   }
        //   return map
        // },
        tabStripRatio() {
          // return this.mode != PageMode.单据回填 || this.isReadOnly ? 0 : 0.2
          return 0
        },
        userName() {
          return this.userData.name
        },
        userCode() {
          return this.userData.staffId
        },
        attrOptionsMapData() {
          let map = {}
          let repMap = {}
          let ori = this.attrOptionsDataOriginal
          if (ori) {
            ori.forEach(v => {
              if (!map[v.attributeCode]) {
                map[v.attributeCode] = []
                repMap[v.attributeCode] = {}
              }
              if (!repMap[v.attributeCode][v.attributeValue]) {
                repMap[v.attributeCode][v.attributeValue] = true
                map[v.attributeCode].push(v.attributeValue)
              } else {
                console.error("值重复：" + v.attributeValue)
              }
            })
          }
          return map
        },
        actionType() {
          return this.paramData && this.paramData.actionType
        },
        // mode: 1, // 1 模版配置 2 单据回填 3 单据导出
        mode() {
          if (app.isLocalTest) {
            return PageMode.模版配置
          }
          if (!this.paramData) {
            return 0
          }
          if (["edit", "add", "view"].some(v => this.actionType == v)) {
            return PageMode.模版配置
          } else if (["backView", "backfill"].some(v => this.actionType == v)) {
            return PageMode.单据回填
          } else if (this.actionType == "export") {
            return PageMode.单据导出
          } else {
            return 0
          }
        },
        isReadOnly() {
          return ["view", "backView", "export"].some(v => this.actionType == v)
        },
        canEditConfig() {
          return this.mode === PageMode.模版配置 && !this.isReadOnly
        },
        propertyTree() {
          if (!this.attrDataOriginal) {
            return [
              {
                label: "设置业务属性",
                value: "set1",
                children: [
                  {
                    label: "属性1",
                    value: "prop1"
                  },
                  {
                    label: "属性2",
                    value: "prop2",
                    children: [
                      {
                        label: "属性21",
                        value: "prop21"
                      },
                      {
                        label: "属性22",
                        value: "prop22",
                        linkAttr: "1"
                      }
                    ]
                  },
                ]
              },
              {
                label: "设置**属性",
                value: "set2",
                children: [
                  {
                    label: "N/A",
                    value: "ATTR_NA"
                  },
                  {
                    label: "*属性2",
                    value: "_prop2",
                    children: [
                      {
                        label: "*属性21",
                        value: "_prop21"
                      },
                      {
                        label: "*属性22",
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

        defaultSsjson() {
          return `{"version":"13.2.0","tabStripRatio":${this.tabStripRatio},"customList":[],"sheets":{"Sheet1":{"name":"Sheet1","columnCount":200,"isSelected":true,"activeRow":0,"activeCol":0,"theme":"Office","data":{"defaultDataNode":{"style":{"themeFont":"Body"}},"dataTable":{}},"rowHeaderData":{"defaultDataNode":{"style":{"themeFont":"Body"}}},"colHeaderData":{"defaultDataNode":{"style":{"themeFont":"Body"}}},"leftCellIndex":0,"topCellIndex":0,"selections":{"0":{"row":0,"rowCount":1,"col":0,"colCount":1},"length":1},"cellStates":{},"outlineColumnOptions":{},"autoMergeRangeInfos":[],"printInfo":{"paperSize":{"width":850,"height":1100,"kind":1}},"index":0}}}`
        },
        isMultiSheet() {
          let isMultiSheet = false
          if (this.actionType == "add") {
            isMultiSheet = this.paramData.data.moreCellFlag == "1" ? true : false
          } else if (this.mode == PageMode.模版配置) {
            isMultiSheet = this.paramData.data.billOnes == "1" ? true : false
          }
          return isMultiSheet
        },
        roleMap() {
          let map = {}
          if (this.userData && this.userData.roles) {
            this.userData.roles.forEach(v => {
              map[v] = true
            })
          }
          return map
        },

      },
      methods: {
        getTemplateBasicData(pageNum) {
          let data = this.getAllDataFromLocal(this.billConfig.curBillNum)

          let billCount = this.billConfig.billCount
          let curMarshallingType = billCount == 2 ? `${pageNum}` : "3"

          let templateBasicData = {
            marshallingType: curMarshallingType,
            marshAllingTypeName: marshallingTypeNameMap[curMarshallingType]
          }

          if (data[1] && data[1][2]) {
            return { ...data[1][2], ...templateBasicData }
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
        async startExport() {
          let exportVue = new Vue({
            el: ".export-container",
            data: {
              completedNum: 0,
              status: null,
              statusMessage: null
            },
            computed: {
              total() {
                if (app.core.paramData && Array.isArray(app.core.paramData.data)) {
                  return app.core.paramData.data.length
                } else {
                  return 1
                }
              },
              message() {
                if (!this.status) {
                  return `正在导出${this.completedNum}/${this.total}`
                } else {
                  return this.statusMessage
                }
              },
              progressPropData() {
                return {
                  type: "circle",
                  percentage: Math.max(0, Math.min(100, Math.round(this.completedNum / this.total * 100))),
                  status: this.status
                }
              }
            }
          })
          let files = []
          let exportExcelWarp = () => {
            let spreadJson = GC.Spread.Sheets.Designer.wrapper.spread.toJSON()
            let mimeType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            return new Promise((resolve, reject) => {
              exportExcel(spreadJson, {}, (result) => {
                if (result.status === 'success') {
                  resolve(new Blob([result.data], { type: mimeType }))
                } else {
                  reject(result)
                }
              })
            })
          }
          try {
            if (this.paramData && Array.isArray(this.paramData.data) && this.paramData.data.length > 0) {
              let fileExtension = ".xlsx"
              for (let i = 0; i < this.paramData.data.length; i++) {
                let billParamData = this.paramData.data[i]
                let [data, attrs, attrOptions] = await Promise.all([
                  this.getTemplateData(billParamData),
                  this.getAttrs(billParamData.templateTypeCode),
                  this.getAttrOptions(billParamData.templateTypeCode),
                ])
                this.getBillModule().setPropertyData({
                  propertyTree: attrs,
                  propertyOptionList: attrOptions
                })
                // 加载数据
                await this.loadTemplatesFromServerData(data)
                let blob = await exportExcelWarp()
                files.push({
                  fileName: this.exportFileName + fileExtension,
                  blob
                })
                exportVue.completedNum = files.length
              }
              if (files.length === 1) {
                saveAs(files[0].blob, files[0].fileName)
              } else if (files.length > 1) {
                let fileNameMap = {}
                let zip = new JSZip()
                for (let i = 0; i < files.length; i++) {
                  let { fileName, blob } = files[i]
                  if (!fileNameMap[fileName]) {
                    fileNameMap[fileName] = 0
                  }
                  let fileNameLast
                  if (fileNameMap[fileName] > 0) {
                    fileNameLast = fileName.replace(fileExtension, "") + "_(" + fileNameMap[fileName] + ")" + fileExtension
                  } else {
                    fileNameLast = fileName
                  }
                  zip.file(fileNameLast, blob)
                  fileNameMap[fileName]++
                }
                let zipBlob = await zip.generateAsync({ type: "blob" })
                saveAs(zipBlob, "单据文件包.zip")
              }
              exportVue.status = "success"
              let seconds = 3
              let getMsg = () => `导出成功，${seconds}秒后关闭标签页`
              exportVue.statusMessage = getMsg()
              let key = setInterval(() => {
                seconds--
                exportVue.statusMessage = getMsg()
                if (seconds === 0) {
                  clearInterval(key)
                  app.close()
                }
              }, 1000)
            }
          } catch (error) {
            console.error(error)
            exportVue.status = "exception"
            exportVue.statusMessage = "导出失败"
          }
        },
        exportInitData() {
          let containerEl = document.querySelector("body>.container")
          containerEl.className = containerEl.className + " hide-content"
          return new Promise((resolve, reject) => {
            Promise.all([
              // 获取用户信息
              this.getUser()
            ]).then((userData) => {
              this.userData = userData
              resolve()
              GC.Spread.Sheets.Designer.loader.customReady(() => {
                this.startExport()
              })
            }).catch(reject)
          })
        },

        getUser() {
          return new Promise((resolve, reject) => {
            axios.get("/apiTrainRepairMidGround/common/getUser").then(({ data: { data } }) => {
              resolve(data)
            }).catch(reject)
          })
        },
        isRole(role) {// 判断当前用户是否为该角色
          return !!this.roleMap[role]
        },
        getDefaultTemplateBasicData(pageNum) {
          if (this.paramData) {
            let data = this.paramData.data
            let marshallingType = null
            if (this.isMultiSheet) {
              marshallingType = `${pageNum}`
            } else {
              marshallingType = "3"
            }
            if (this.actionType == "add") {
              return {
                trainSetType: data.trainSetType,
                batch: data.batchId,
                trainSetId: data.trainSetId,
                templateTypeCode: data.templateTypeCode,
                templateTypeName: data.templateTypeName,
                createUser: this.userName,
                // createtime: ,
                unitCode: data.unitCode,
                unitName: data.unitName,
                deptType: data.unitCode == "" ? "03" : "04",
                itemCode: data.itemCode,
                itemName: data.itemName,
                billOnes: data.moreCellFlag,// 0:不能选张数，1:可以选择张数
                depotCode: data.depotCode,
                depotName: data.depotName ? data.depotName : this.userData.depot.name,
                bureauCode: data.bureaCode,
                bureauName: data.bureaName ? data.bureaName : this.userData.bureau.name,
                marshallingType,
                MarshAllingTypeName: marshallingTypeNameMap[marshallingType],
                remark: ""
              }
            }
          }
          return {}
        },
        init() {
          if (app.isLocalTest) {
            if (this.mode == PageMode.单据导出) {
              this.startExport()
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

              this.getBillModule().initAllData({
                propertyTree: this.propertyTree,
                propertyOptionList: [],
                ssjsonDataString: null,
                cellDataList: [],
                areaList: [],
              })
              this.billConfig.billCountRadioDisabled = false
              this.billConfig.billCount = 2
              this.billConfig.isShowMode = true
              this.saveAllDataToLocal(1)
              this.saveAllDataToLocal(2)

            })
            return Promise.resolve()
          }
          GC.Spread.Sheets.Designer.loader.customReady(() => {
            let spread = GC.Spread.Sheets.Designer.wrapper.spread
            this.repairFlameWindowSize()
            this.initBillModule(spread)
            this.initCustomCommands(spread)
          })
          return new Promise((resolve, reject) => {
            let listener = (paramData) => {
              localStorageParamsBus.removeParamsListener(this.pageCode, "billEditParams", listener)
              this.paramData = paramData
              let promise = null
              if (this.mode === PageMode.单据导出) {
                promise = this.exportInitData()
              } else {
                promise = this.initData()
              }
              promise.then(() => {
                app.initWindowSizeListenerForRefresh()
                resolve()
              }).catch(reject)
            }
            localStorageParamsBus.addParamsListener(this.pageCode, "billEditParams", listener)
          })
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
          if (this.mode === PageMode.模版配置) {
            var Commands = GC.Spread.Sheets.Commands
            var wrapExecute = (context, options, isUndo, execute) => {
              if (isUndo) {
                Commands.undoTransaction(context, options)
              } else {
                Commands.startTransaction(context, options)
                context.suspendPaint()
                execute()
                context.resumePaint()
                Commands.endTransaction(context, options)
              }
            }

            // 模板编辑模式
            // 更改为显示模式
            // app.core.billConfig.isShowMode = true
            // 注册设置属性命令
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

            // 注册删除属性命令
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
            // 注册设置区域命令
            spread.commandManager().register(this.setAreaCommandName, {
              canUndo: true,
              execute: (context, options, isUndo) => {
                return wrapExecute(context, options, isUndo, () => {
                  var selections = options.selections
                  for (var i = 0; i < selections.length; i++) {
                    var selection = selections[i]
                    this.getBillModule().setArea(selection.row, selection.col, selection.rowCount, selection.colCount)
                  }
                })
              }
            })
            // 注册删除区域命令
            spread.commandManager().register(this.deleteAreaCommandName, {
              canUndo: true,
              execute: (context, options, isUndo) => {
                return wrapExecute(context, options, isUndo, () => {
                  var selections = options.selections

                  for (var i = 0; i < selections.length; i++) {
                    var selection = selections[i]
                    this.getBillModule().deleteArea(selection.row, selection.col, selection.rowCount, selection.colCount)
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
                    // app.showConfirm("是否保存前置单元格的变更？").then(() => {
                    // }).finally(() => {
                    // })
                  } else {
                    end()
                  }
                })
                this.selectModeUtil = selectModeUtil
              } else {
                let { functionType } = options.commandOptions
                if (functionType == "select") {
                  this.selectModeUtil.selectRanges(options.selections)
                } else if (functionType == "unselect") {
                  this.selectModeUtil.unselectRanges(options.selections)
                } else if (functionType == "endSelect") {
                  this.selectModeUtil.endSelectMode()
                }
              }
            }

            // 设置前置回填单元格的设置操作
            spread.commandManager().register(this.setCellPreposedCellControlsCommandName + ".do", {
              canUndo: true,
              execute: (context, options, isUndo) => {
                return wrapExecute(context, options, isUndo, () => {
                  this.getBillModule().setCellPreposedCellControls(...options.commandOptions)
                })
              }
            })
            // 设置前置回填单元格的模式操作
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
            // 设置前置回填区域
            spread.commandManager().register(this.setCellPreposedAreaControlsCommandName, {
              canUndo: true,
              execute: (context, options, isUndo) => {
                return wrapExecute(context, options, isUndo, () => {
                  let { row, col, areaId } = options.commandOptions
                  this.getBillModule().setCellPreposedAreaControls(row, col, [areaId])
                })
              }
            })
            // 设置完全回填后执行
            spread.commandManager().register(this.setCellEntirelyFillControlCommandName, {
              canUndo: true,
              execute: (context, options, isUndo) => {
                return wrapExecute(context, options, isUndo, () => {
                  let { row, col } = options.commandOptions
                  this.getBillModule().setCellEntirelyFillControl(row, col)
                })
              }
            })
            // 删除回填顺序设置
            spread.commandManager().register(this.deleteCellOrderControlsCommandName, {
              canUndo: true,
              execute: (context, options, isUndo) => {
                return wrapExecute(context, options, isUndo, () => {
                  let { row, col, functionName } = options.commandOptions
                  this.getBillModule()[functionName](row, col)
                })
              }
            })


            // 设置关联回填默认值单元格的设置操作
            spread.commandManager().register(this.setSettingDefaultValueCellControlsCommandName + ".do", {
              canUndo: true,
              execute: (context, options, isUndo) => {
                return wrapExecute(context, options, isUndo, () => {
                  this.getBillModule().setSettingDefaultValueCellControls(...options.commandOptions)
                })
              }
            })
            // 设置关联回填默认值单元格的模式操作
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
            // 设置关联回填默认值区域
            spread.commandManager().register(this.setSettingDefaultValueAreaControlsCommandName, {
              canUndo: true,
              execute: (context, options, isUndo) => {
                return wrapExecute(context, options, isUndo, () => {
                  let { row, col, areaId } = options.commandOptions
                  this.getBillModule().setSettingDefaultValueAreaControls(row, col, [areaId])
                })
              }
            })
            // 删除关联回填默认值设置
            spread.commandManager().register(this.deleteSettingDefaultValueControlsCommandName, {
              canUndo: true,
              execute: (context, options, isUndo) => {
                return wrapExecute(context, options, isUndo, () => {
                  let { row, col, functionName } = options.commandOptions
                  this.getBillModule()[functionName](row, col)
                })
              }
            })
          }
        },
        initData() {
          return new Promise((resolve, reject) => {
            let containerEl = document.querySelector("body>.container")
            let classNamePro = containerEl.className
            containerEl.className = classNamePro + " hide-content"
            Promise.all([
              // 获取属性基础信息
              this.getAttrs(this.paramData.data.templateTypeCode),
              this.getAttrOptions(this.paramData.data.templateTypeCode),
              // 获取模板数据
              this.getTemplateData(this.paramData.data),
              // 获取用户信息
              this.getUser()
            ]).then(([attrs, attrOptions, templateData, userData]) => {
              this.userData = userData
              GC.Spread.Sheets.Designer.loader.customReady(() => {
                debugPaint()
                let loading = this.$loading()
                setTimeout(() => {
                  let className = classNamePro
                  if (this.mode === PageMode.单据回填) {
                    className += " billBackContainer"
                  }
                  containerEl.className = className
                  GC.Spread.Sheets.Designer.wrapper.spread.refresh()
                }, 200)
                this.attrDataOriginal = attrs
                this.attrOptionsDataOriginal = attrOptions
                this.getBillModule().setPropertyData({
                  propertyTree: attrs,
                  propertyOptionList: attrOptions
                })
                // 加载模板数据
                this.loadTemplatesFromServerData(templateData).finally(() => {
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
              resolve(data.rows)
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
              if (data.rows) {
                resolve(data.rows)
              } else {
                resolve([])
              }
            }).catch(reject)
          })
        },
        getTemplateData(param) {
          return new Promise((resolve, reject) => {
            if (this.mode == PageMode.模版配置) {
              if (this.actionType == "add") {
                axios.get("/apiTrainRepairMidGround/billConfig/getBaseBillConfig", {
                  params: {
                    billTypeCode: param.templateTypeCode
                  }
                }).then(({ data }) => { resolve(data.rows) }).catch(reject)
              } else {
                axios.get("/apiTrainRepairMidGround/billConfig/getBillTemplateDetail", {
                  params: {
                    templateId: param.templateId,
                    state: param.state//0—编辑，1—已发布
                  }
                }).then(({ data }) => { resolve(data.data) }).catch(reject)
              }
            } else if (this.mode == PageMode.单据回填 || this.mode == PageMode.单据导出) {
              axios.post("/apiTrainRepairMidGround/billBack/getBillData", {
                ...param
              }).then(({ data }) => { resolve([data.rows]) }).catch(reject)
            } else {
              reject("程序错误，意外的mode值")
            }
          })

        },
        getSsjsonFileByPath(templatePath) {
          if (ssjsonCatch.has(templatePath)) {
            return Promise.resolve(ssjsonCatch.get(templatePath))
          } else {
            return new Promise((resolve, reject) => {
              $.ajax({
                url: getSsjsonBaseUrl + "/apiTrainRepairMidGround/common/getCenterFilesForJsonP?" + $.param({ templatePath }),
                type: "GET",
                dataType: "jsonp",
                success: function(data) {
                  if (data && data.content) {
                    ssjsonCatch.set(templatePath, data.content)
                    resolve(data.content)
                  } else {
                    reject(data)
                  }
                },
                error: function(error) {
                  reject(error)
                }
              })
              // axios.get("/apiTrainRepairMidGround/billConfig/getSSJsonFileContent", {
              //   params: {
              //     templatePath
              //   }
              // }).then(({ data/* , request */ }) => {
              //   // 后端json转换有bug，会出现大量无意义的\u0000，导致此后转换对象报错
              //   // (这个bug已经修复，并且replaceAll方法对浏览器版本要求过于苛刻)
              //   // let str = request.response.replaceAll("\\u0000", "")
              //   // let data = JSON.parse(str)
              //   if (data && data.content) {
              //     resolve(data.content)
              //   } else {
              //     reject(data)
              //   }
              // }).catch(reject)
            })
          }
        },
        async loadTemplatesFromServerData(data) {
          let spread = GC.Spread.Sheets.Designer.wrapper.spread
          spread.suspendPaint()
          let templates = data
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
            let cellDataList = (this.mode == PageMode.模版配置 ? templateInfo.contents : templateInfo.details) || []
            let areaList = (this.mode == PageMode.模版配置 ? templateInfo.areas : null) || []

            let templateJsonObj = templateJson ? JSON.parse(templateJson) : null
            this.setAllDataToLocal(page, [
              templateJsonObj,
              [cellDataList, areaList],
              this._getTemplateBasicData(templateInfo)
            ])
          }
          this.billConfig.billCount = this.isMultiSheet ? 2 : 1
          this.billConfig.billCountRadioDisabled = !this.isMultiSheet || !this.canEditConfig
          this.billConfig.saveBtnDisabled = !this.canEditConfig
          // 补充默认内容
          this._getArray(this.billConfig.billCount).forEach((v, i) => {
            if (!templates[i]) {
              if (this.actionType != "add") {
                throw {
                  msg: "添加以外的情况未获取到模板",
                  data: data
                }
              }
              let page = i + 1

              this.setAllDataToLocal(page, [
                null,
                [[], []],
                this.getDefaultTemplateBasicData(page)
              ])
            }
          })
          this.readAllDataFromLocal(1)
          spread.resumePaint()
          if (this.mode == PageMode.模版配置) {
            this.$nextTick(() => {
              this.billConfig.isShowMode = true
            })
          }
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
              msg: "单元格坐标数据格式错误",
              data: cell
            }
          }
          return [row, col]
        },
        _getTemplateBasicData(proTemplateData) {
          let templateBasicData = { ...proTemplateData }
          if (this.mode == PageMode.模版配置) {
            delete templateBasicData.contents
            delete templateBasicData.areas
            return templateBasicData
          } else {
            delete templateBasicData.details
            return templateBasicData
          }
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
          return new Promise((resolve, reject) => {
            let spread = GC.Spread.Sheets.Designer.wrapper.spread
            // 暂停渲染
            spread.suspendPaint()
            // 保存数据
            this.saveAllDataToLocal()
            try {
              let templates = []
              this._getArray(this.billConfig.billCount).map((v, i) => i + 1).map(pageNum => {

                // 将数据设置进billSpreadModule
                this.readAllDataFromLocal(pageNum)
                // 校验数据
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

                // 从billSpreadModule读取携带关联单元格信息的单元格信息
                let { ssjsonDataString, cellDataList, areaList } = this.getBillModule().getTemplateData()
                templateBasicData.ssjsonFile = ssjsonDataString
                // 更新缓存
                if (ssjsonCatch.has(templateBasicData.templatePath)) {
                  ssjsonCatch.set(templateBasicData.templatePath, templateBasicData.ssjsonFile)
                }
                // 设置单元格信息
                templateBasicData.contents = cellDataList
                templateBasicData.areas = areaList
                templates.push(templateBasicData)
              })
              let loading = this.$loading()
              axios.post("/apiTrainRepairMidGround/billConfig/saveBillConfig", templates).then(({ data }) => {
                if (this.actionType == "add" && !this.templateBasicData.templateId && data.rows) {
                  // 更新基础数据
                  return this.updateBasicData(data.rows.join(","), 0)
                }
              }).then(() => {
                this.changed = false
                this.$message({
                  type: "success",
                  message: "保存成功",
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
              // 重新读取数据
              this.readAllDataFromLocal(this.billConfig.curBillNum)
              // 恢复渲染
              spread.resumePaint()
            }
          })
        },
        updateBasicData(templateId, state) {
          return new Promise((resolve, reject) => {
            getTemplateData({
              templateId: templateId,
              state: state//0—编辑，1—已发布
            }).then((data) => {
              data.forEach((templateInfo, i) => {
                let page = getPageByMarshallingType(templateInfo.marshallingType)

                if (page == null) {
                  page = i + 1
                }
                let [ssjson, [cellDataList, areaList]] = this.getAllDataFromLocal(page)
                let templateBasicData = this._getTemplateBasicData(templateInfo)
                this.setAllDataToLocal(page, [ssjson, [cellDataList, areaList], templateBasicData])
              })
              resolve()
            }).catch(reject)
          })
        },
        saveBillBack(disableSuccessMsg) {
          if (disableSuccessMsg == null) {
            disableSuccessMsg = false
          }
          return new Promise((resolve, reject) => {
            let spread = GC.Spread.Sheets.Designer.wrapper.spread
            // 暂停渲染
            spread.suspendPaint()
            try {
              let vi = this.validateBillBackfill()
              if (!vi.pass) {
                this.$message({
                  type: "error",
                  message: vi.msg,
                  showClose: true
                })
                throw vi.msg
              }
              let data = []
              this.saveAllDataToLocal()
              this._getArray(this.billConfig.billCount).map((v, i) => i + 1).map(i => {
                let backfillBasicData = { ...this.templateBasicData }

                this.readAllDataFromLocal(i)

                // 设置单元格信息等
                let { ssjsonDataString, cellDataList, areaList } = this.getBillModule().getTemplateData()
                backfillBasicData.details = cellDataList
                backfillBasicData.areas = areaList

                backfillBasicData.lastFillWorkCode = this.userCode
                backfillBasicData.lastFillWorkName = this.userName

                backfillBasicData.backFillType = this.getBillModule().checkBillBackfillIsOver() ? "1" : "0"
                data.push(backfillBasicData)
              })
              let loading = this.$loading()
              axios.post("/apiTrainRepairMidGround/billBack/billSave", data).then(() => {
                return this.updateBillData()
              }).then(() => {
                this.changed = false
                if (!disableSuccessMsg) {
                  this.$message({
                    type: "success",
                    message: "保存成功",
                    showClose: true
                  })
                }
                loading.close()
                localStorageParamsBus.sendParams(this.pageCode, "refreshBackList", true)
                resolve()
              }).catch((...args) => {
                loading.close()
                reject(...args)
              })
            } catch (error) {
              reject()
              console.error(error)
            } finally {
              // 重新读取数据
              // 为了和模板行为一致
              this.readAllDataFromLocal(1)

              // 恢复渲染
              spread.resumePaint()
            }
          })
        },
        updateBillData() {
          if (this.mode != PageMode.单据回填) {
            throw "此方法只能在回填模式调用！"
          }
          return new Promise((resolve, reject) => {
            this.getTemplateData(this.paramData.data).then((data) => {
              return this.loadTemplatesFromServerData(data)
            }).then(resolve).catch(reject)
          })
        },


        validateBillBackfill() {// TODO 校验回填内容(不用弄)
          let pass = true
          let msg = null
          // try {
          // } catch (error) {
          // }
          return {
            pass,
            msg
          }
        },
        _objMap(treeData, propsMap, extraSet) {
          return treeData.map(node => {
            let newObj = {}
            for (const key in node) {
              let value = node[key]
              if (Array.isArray(value)) {
                value = this._objMap(value, propsMap, extraSet)
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

        getPropertyMenuItems(selections, spread) {// TODO 是否需要根据属性改变属性菜单？(暂时不需要)
          return this._objMap(this.propertyTree, {
            [this.propertyTreeProps.children]: "subMenu",
            [this.propertyTreeProps.label]: "text",
            [this.propertyTreeProps.value]: "name"
          }, (obj) => {
            if (!obj.subMenu) {
              obj.command = this.setPropertyCommandName
            }
            obj.options = { propertyKey: obj.name }
            obj.name = this.ZSJCJYW_MENUITEM_PREFIX + obj.name
          })
        },
        saveAllDataToLocal(pageNum) {
          if (pageNum == null) {
            pageNum = this.billConfig.curBillNum
          }
          let { ssjsonDataString, cellDataList, areaList } = this.getBillModule().getTemplateData(true)
          this.setAllDataToLocal(pageNum, [ssjsonDataString == null ? null : JSON.parse(ssjsonDataString), [cellDataList, areaList]])
        },
        readAllDataFromLocal(pageNum) {
          let [ssjsonData, [cellDataList, areaList]] = this.getAllDataFromLocal(pageNum)
          this.getBillModule().setTemplateData({
            ssjsonDataString: ssjsonData == null ? null : JSON.stringify(ssjsonData),
            cellDataList,
            areaList,
          })
          let spread = GC.Spread.Sheets.Designer.wrapper.spread
          this.getIsWheelingOnActiveSheet = getMouseWheelingStateGetterFromSpreadSheet(spread.getActiveSheet())
        },
        getAllDataFromLocal(pageNum) {
          let ssjsonData = this.billJSONData[pageNum - 1]
          let exData = this.billExData[pageNum - 1]
          let billBasicData = this.billBasicData[pageNum - 1]
          let get = (v) => {
            if (v == null) {
              return null
            } else {
              return JSON.parse(v)
            }
          }
          return [get(ssjsonData), get(exData), get(billBasicData)]
        },
        setAllDataToLocal(pageNum, [ssjsonData, exData, billBasicData]) {
          this.$set(this.billJSONData, pageNum - 1, JSON.stringify(ssjsonData))
          this.$set(this.billExData, pageNum - 1, JSON.stringify(exData))
          if (billBasicData) {
            this.$set(this.billBasicData, pageNum - 1, JSON.stringify(billBasicData))
          }
        },
        // 保存模板单据之后执行回调方法
        afterSaveConfig(msg, callback) {
          if (this.changed || !this.templateId) {
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
          this.afterSaveConfig("请先保存单据再进行复制，是否现在保存？", () => {
            this.copyBillConfigInner()
          })
        },
        elConfirm(msg) {
          return new Promise((resolve, reject) => {
            this.$confirm(msg, '提示', {
              confirmButtonText: '取消',
              cancelButtonText: '确定',
              closeOnClickModal: false,
              closeOnPressEscape: false,
              type: 'warning'
            }).then(function() {
              reject()
            }).catch(function() {
              resolve()
            })
          })
        },
        copyBillConfigInner() {
          window.copyBillConfigDialog.copyBillConfig((paramData, closeDialog) => {
            this.elConfirm("是否确定复制？").then(v => {
              let proBillCount = this.billConfig.billCount
              this.paramData = paramData
              let curBillCount = this.billConfig.billCount = this.isMultiSheet ? 2 : 1
              this._getArray(curBillCount).map((v, i) => i + 1).forEach(page => {
                // 重置基础数据
                if (page <= proBillCount) {
                  // 小于原先页面的页面更新基础数据即可
                  let [ssjson, [propertyData, areaData]] = this.getAllDataFromLocal(page)
                  let templateBasicData = this.getDefaultTemplateBasicData(page)
                  this.setAllDataToLocal(page, [ssjson, [propertyData, areaData], templateBasicData])
                } else {
                  // 大于原先总页码的页面需要重新设置所有数据
                  this.setAllDataToLocal(page, [
                    null,
                    [[], []],
                    this.getDefaultTemplateBasicData(page)
                  ])
                }
              })
              this.changed = true
              this.billConfig.billCountRadioDisabled = !this.isMultiSheet || !this.canEditConfig
              this.billConfig.saveBtnDisabled = !this.canEditConfig
              this.readAllDataFromLocal(this.billConfig.curBillNum)
              closeDialog()
            })
          })
        },
        publishBillConfig() {
          this.afterSaveConfig("请先保存单据再进行发布，是否保存并发布？", () => {
            this.publishBillConfigInner()
          })
        },
        publishBillConfigInner() {
          let loading = this.$loading()
          let templateId = this.templateId
          axios.get("/apiTrainRepairMidGround/billConfig/billConfigRelease", {
            params: {
              templateId
            }
          }).then((res) => {
            return this.updateBasicData(templateId, 0)
          }).then(() => {
            localStorageParamsBus.sendParams(this.pageCode, "refreshList", true)
            this.$message.success("发布成功！")
          }).finally(() => {
            loading.close()
          })
        },
        // 保存回填单据之后执行回调方法
        afterSaveBillBack(msg, callback) {
          if (this.changed || !this.templateBasicData.checkListSummaryId) {
            if (msg != null) {
              app.showConfirm(msg).then(() => {
                return this.saveBillBack()
              }).then(() => {
                callback()
              })
            } else {
              setTimeout(() => {
                this.saveBillBack(true).then(callback)
              }, 0)
            }
          } else {
            callback()
          }
        },
        importExcel(ssjsonData) {
          this.getBillModule().setTemplateData({
            ssjsonDataString: JSON.stringify(ssjsonData),
            cellDataList: [],
            areaList: []
          })
          let pageNum = this.billConfig.curBillNum
          this.setAllDataToLocal(pageNum, [
            ssjsonData,
            [[], []],
            this.getDefaultTemplateBasicData(pageNum)
          ])
        }
      },
      watch: {
        highlightInvalidData: function(v) {
          let spread = GC.Spread.Sheets.Designer.wrapper.spread
          spread.options.highlightInvalidData = v
        },
        "billConfig.billCount": function(v) {
          if (v == 1) {
            this.billConfig.curBillNum = 1
          }
        },
        // 当页码改变时切换页面内容
        "billConfig.curBillNum": function(toNum) {
          let spread = GC.Spread.Sheets.Designer.wrapper.spread
          // 暂停渲染
          spread.suspendPaint()

          // 切换数据源
          let fromNum = toNum == 1 ? 2 : 1
          this.saveAllDataToLocal(fromNum)
          this.readAllDataFromLocal(toNum)

          // 恢复渲染
          spread.resumePaint()
        },
        "billConfig.isShowMode": function(v) {
          this.getBillModule().setIsShowMode(v)
        }
      }
    }),
    toFromData(obj) {
      let formData = new FormData()
      app.objectDeepIterater(obj, (v, path) => {
        formData.append(path, v)
      })
      return formData
    },
    objectDeepIterater(obj, callback, path) {
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
            app.objectDeepIterater(value, callback, curPath)
          } else {
            callback(value, curPath)
          }
        }
      }
    },
    showError(message) {
      let designer = GC.Spread.Sheets.Designer
      designer.MessageBox.show(message, designer.res.title, 3 /* error */)
    },
    showWarn(message) {
      let designer = GC.Spread.Sheets.Designer
      designer.MessageBox.show(message, designer.res.title, 2 /* warn */)
    },
    showConfirm(message) {
      let designer = GC.Spread.Sheets.Designer
      return new Promise((resolve, reject) => {
        designer.MessageBox.show(message, "操作确认", 2 /* warning */, 1 /* okCancel */, function(event, result) {
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