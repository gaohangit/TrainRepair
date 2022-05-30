/*!
 *
 * BillSpreadModule 1.0
 *
 * by 张天可
 *
 */
(function (context) {
  // 版本升级需要重新变更的标记: VERSION_CHECK
  // 【ISSUES 1】 下一个执行帧才移除监听的原因，spreadjs内部存在bug，这种只监听一次的方法超过一个就会导致内部报错（原因：循环触发方法时，把原始事件数量缓存了，在触发的过程中移除会循环到不存在的下标的事件）

  const VERSION_CHECK_ERROR = "spread依赖版本(打包批次)变更错误"
  var spreadVersion = "13.2.0" // 请勿修改此版本号来避免报错，如果想使用新版本spread请联系组件开发者
  validateSpreadDependencies(spreadVersion, context)
  validateOtherDependencies(context)

  let isObject = (value) => {
    return Object.prototype.toString.call(value) === "[object Object]"
  }

  let isNull = (value) => {
    return Object.prototype.toString.call(value) === "[object Null]"
  }

  let isUndefined = (value) => {
    return Object.prototype.toString.call(value) === "[object Undefined]"
  }

  let isBlank = (value) => {
    if (value == null) {
      return true
    } else {
      if (typeof value === "string") {
        return value.trim() === ""
      } else {
        return false
      }
    }
  }

  let isValueEqual = (oldValue, newValue) => {
    if (oldValue === "" || isUndefined(oldValue)) {
      oldValue = null
    }
    if (newValue === "" || isUndefined(newValue)) {
      newValue = null
    }
    if (oldValue === newValue) {
      return true
    } else {
      if (typeof oldValue !== typeof newValue) {
        return false
      } else {
        return JSON.stringify(oldValue) === JSON.stringify(newValue)
      }
    }
  }

  let copyDataSimple = (data) => JSON.parse(JSON.stringify(data))

  let wrapFn = (obj, name, fn) => {
    // 以包装的方式扩展对象方法
    let ori = obj[name]
    obj[name] = function (...args) {
      return fn.call(this, (...customArgs) => {
        return ori && ori.apply(this, customArgs.length === 0 ? args : customArgs)
      }, args)
    }
    return () => {
      delete obj[name]
      if (obj[name] !== ori) {
        obj[name] = ori
      }
    }
  }

  let reduceBind = (obj, bindFnName, unbindFnName) => {
    let unbinding = null
    wrapFn(obj, bindFnName, (doOri, args) => {
      unbinding = doOri()
    })
    wrapFn(obj, unbindFnName, (doOri, args) => {
      if (unbinding) {
        unbinding()
        unbinding = null
      }
      doOri()
    })
  }

  let getLogErrorFn = (fn) => {
    return (...args) => {
      try {
        fn(...args)
      } catch (error) {
        console.error(error)
        throw error
      }
    }
  }

  // 修复spread的bug
  (() => {
    // 修复Workbook示例unbind事件时，就算传递方法也会把对应事件的全部都解除绑定的bug，导致其在之后新增的Worksheet对象中不生效
    wrapFn(context.GC.Spread.Sheets.Workbook.prototype, "unbind", function (doOri, args) {
      let [event, fn] = args
      // VERSION_CHECK
      let boundEvents = this.rv

      let revertSpliceWrap = null
      try {
        if (typeof fn == "function" && boundEvents) {
          // 重写splice方法，如果被删除的事件不符合参数方法则不进行删除
          revertSpliceWrap = wrapFn(boundEvents, "splice", function (doSplice, spliceArgs) {
            let [start, deleteCount, ...items] = spliceArgs
            if (deleteCount === 1 && items.length === 0) {
              if (this[start].data === fn) {
                doSplice()
              }
            } else {
              doSplice()
            }
          })
        }
        doOri()
      } finally {
        if (revertSpliceWrap) {
          revertSpliceWrap()
        }
      }
    })
  })()

  // let getReadOnlyObj = (obj) => {
  //   let newObj = {}
  //   for (const key in obj) {
  //     if (Object.hasOwnProperty.call(obj, key)) {
  //       Object.defineProperty(newObj, key, {
  //         writable: false,
  //         value: obj[key]
  //       })
  //     }
  //   }
  // }

  context.BillSpreadModule = (function () {
    const Sheets = GC.Spread.Sheets
    let getDefaultConfig = () => {
      return {
        allowExtendOperation: false,// 是否允许扩展操作：粘贴、填充、撤销
        readOnly: false, // 是否只读
        configMode: false, // 是否为模板编辑模式
        expandMode: false, // 是否为扩展模式，仅在模板编辑模式下生效
        maxTextLength: 200,
        headerVisible: false,
        propertyTreeConfig: {
          getCode(propertyObj) {
            if ("attributeCode" in propertyObj) {
              return propertyObj.attributeCode
            } else if ("attributeTypeCode" in propertyObj) {
              return propertyObj.attributeTypeCode
            } else {
              return propertyObj.code
            }
          },
          getName(propertyObj) {
            if ("attributeName" in propertyObj) {
              return propertyObj.attributeName
            } else if ("attributeTypeName" in propertyObj) {
              return propertyObj.attributeTypeName
            } else {
              return propertyObj.name
            }
          },
          getChildren(propertyObj) {
            if ("templateAttributes" in propertyObj) {
              return propertyObj.templateAttributes
            } else {
              return propertyObj.children
            }
          },
          getModeCode(propertyObj) {
            return propertyObj.attributeModeCode
          },
        },
        propertyOptionListConfig: {
          getOptionValue(templateValueObj) {
            return templateValueObj.attributeRangeValue
          },
          getAttributeCode(templateValueObj) {
            return templateValueObj.attributeCode
          },
          getIsDefault(templateValueObj) {
            if (templateValueObj.isDefault == "1") {
              return true
            } else if (templateValueObj.isDefault == "0") {
              return false
            } else {
              return null
            }
          }
        },
        cellDataConfig: {
          getCellValue(cell, config) {
            if (config.configMode) {
              return cell.content
            } else {
              return cell.value
            }
          },
          setCellValue(cell, value, config) {
            if (config.configMode) {
              cell.content = value
            } else {
              cell.value = value
            }
          },
          getCellAttributeCode(cell, config) {
            return cell.attributeCode
          },
          setCellAttributeCode(cell, attributeCode, config) {
            cell.attributeCode = attributeCode
          },
          getCellRow(cell, config) {
            return parseInt(cell.rowId)
          },
          setCellRow(cell, row, config) {
            cell.rowId = row + ""
          },
          getCellCol(cell, config) {
            return parseInt(cell.colId)
          },
          setCellCol(cell, col, config) {
            cell.colId = col + ""
          },

          getCellId(cell, config) {
            return cell.id
          },
          setCellId(cell, id, config) {
            cell.id = id
          },
          getCellCode(cell, config) {
            return cell.code
          },
          setCellCode(cell, code, config) {
            cell.code = code
          },
          getControls(cell, config) {
            return cell.controls
          },
          setControls(cell, controls, config) {
            cell.controls = controls
          },
          // 单元格变更类型只写不读
          setCellChangeType(cell, changeType, config) {
            cell.changeType = changeType
          },
          // 关联单元格(只写不读)
          setLinkCells(cell, linkCells, config) {
            if (config.configMode) {
              cell.linkContents = linkCells
            } else {
              cell.linkCells = linkCells
            }
          },
          // 单元格是否保存(只读不写)
          getCellSaved(cell, config) {
            if (!config.configMode) {
              return cell.saved
            }
          },
          // 单元格是否初始化完毕(只读不写)
          getCellInitialized(cell, config) {
            if (!config.configMode) {
              return cell.initialized
            }
          },
          // 单元格是否需要使用属性默认值进行初始化
          getCellNeedInitializeByAttributeDefaultValue(cell, config) {
            if (!config.configMode) {
              return cell.needInitializeByAttributeDefaultValue
            }
          },

          getLinkCellId(cell, config) {
            if (config.configMode) {
              return cell.linkContentId
            } else {
              return cell.linkCellId
            }
          },
          setLinkCellId(cell, linkCellId, config) {
            if (config.configMode) {
              cell.linkContentId = linkCellId
            } else {
              cell.linkCellId = linkCellId
            }
          },
        },
        confirmMessage(message, title) {
          if (Vue.prototype.$confirm) {
            return Vue.prototype.$confirm(message, title)
          } else {
            throw "未发现【消息确认】的默认实现，请实现confirmMessage方法(返回Promise)，或者引入实现了Vue.prototype.$confirm的框架，如ElementUI"
          }
        },
        alertMessage(message, type, onClose) {
          if (Vue.prototype.$message) {
            return Vue.prototype.$message({
              message,
              type,
              onClose
            })
          } else {
            throw "未发现【消息提示】的默认实现，请实现配置中的alertMessage方法(返回对象要求存在结束信息展示的close方法: { close: Function })，或者引入实现了Vue.prototype.$message的框架，如ElementUI"
          }
        },
        windowLoading() {
          if (Vue.prototype.$loading) {
            return Vue.prototype.$loading()
          } else {
            throw "未发现【全屏加载中】的默认实现，请实现配置中的windowLoading方法(返回对象要求存在结束加载的close方法: { close: Function })，或者引入实现了Vue.prototype.$loading的框架，如ElementUI"
          }
        },
        buttonComponentGetter() {
          if (window.ELEMENT) {
            return {
              template: `<el-button type="primary" size="mini" style="margin: 7px;">
                <slot></slot>
              </el-button>`
            }
          } else {
            return {
              template: `<button style="margin: 5px;">
                <slot></slot>
              </button>`
            }
          }
        }
      }
    }
    let mergeConfigs = (...configs) => {
      let config = configs[0]
      for (let i = 1; i < configs.length; i++) {
        const curConfig = configs[i]
        for (const key in curConfig) {
          const value = curConfig[key]
          if (isObject(value)) {
            let arr = [{}]
            if (config[key]) {
              arr.push(config[key])
            }
            arr.push(value)
            config[key] = mergeConfigs(...arr)
          } else {
            if (value != null) {
              if (typeof config[key] === typeof value || isUndefined(config[key]) || isNull(config[key])) {
                config[key] = value
              } else {
                console.error(`配置参数类型错误，参数名称：${key}，原始类型：${typeof config[key]}，输入类型：${typeof value}，输入值：${value}`)
              }
            }
          }
        }
      }
      return config
    }
    let getTransCellDataConfig = (config) => {
      let cellDataConfig = mergeConfigs({}, config.cellDataConfig)
      for (const fnName in config.cellDataConfig) {
        if (fnName.startsWith("set")) {
          const fn = config.cellDataConfig[fnName]
          cellDataConfig[fnName] = (cellData, value) => {
            return fn(cellData, value, config)
          }
        } else if (fnName.startsWith("get")) {
          const fn = config.cellDataConfig[fnName]
          cellDataConfig[fnName] = (cellData) => {
            return fn(cellData, config)
          }
        }
      }
      return cellDataConfig
    }

    let getCellPosFromCellData = (cell, config) => {
      let row = null
      let col = null
      let {
        getCellRow,
        getCellCol
      } = getTransCellDataConfig(config)
      row = getCellRow(cell)
      col = getCellCol(cell)
      if (isNaN(row) || isNaN(col)) {
        throw {
          msg: "获取单元格坐标数据失败",
          data: cell
        }
      }
      return [row, col]
    }

    let everySheetName = (context, sheetName, fn) => {
      if (typeof sheetName == "string") {
        fn(sheetName)
      } else if (Array.isArray(sheetName)) {
        sheetName.forEach((v) => {
          fn(v)
        })
      } else {
        fn(context.getActiveSheet().name())
      }
    }

    let initTabStripStyle = (() => {
      let n = 0
      let getNewId = () => {
        return "_spread_host_" + n++ + "_"
      }

      return (hostEl) => {

        if (!hostEl.id) {
          hostEl.id = getNewId()
        }
        let hostElId = hostEl.id
        let tabStripStyleId = hostElId + "_bill_tab_strip_"
        if (!document.getElementById(tabStripStyleId)) {
          let targetEl = hostEl.querySelector("table>tr:last-child table td:nth-child(2)>div")
          let backgroundColor = targetEl.style.backgroundColor
          let height = targetEl.clientHeight
          let borderTop = targetEl.style.borderTop
          let tabStripStyleEl = document.createElement("style")
          tabStripStyleEl.innerHTML = `
div[gcUIElement="gcSpread"]#${hostElId}>table>tr:last-child table td:first-child>div>canvas {
  display: none;
}

div[gcUIElement="gcSpread"]#${hostElId}>table>tr:last-child table td:first-child>div {
  border-top: ${borderTop};
  background-color: ${backgroundColor};
  height: ${height}px !important;
}`
          tabStripStyleEl.id = tabStripStyleId
          document.head.append(tabStripStyleEl)
        }
        return () => {
          let styleEl = document.getElementById(tabStripStyleId)
          if (styleEl) {
            styleEl.remove()
          }
        }
      }
    })()


    let listenMouseEnterCell = (spread, listener) => {
      let hostEl = spread.getHost()
      let lastCellPosition = [-1, -1]
      let posEq = (posA, posB) => {
        return posA[0] === posB[0] && posA[1] === posB[1]
      }
      let mousemoveListener = (e) => {
        if (e.target.getAttribute("gcuielement") === "gcWorksheetCanvas") {
          var x = e.offsetX
          var y = e.offsetY
          var rs = spread.hitTest(x, y)
          let curPos = [-1, -1]
          if (rs && rs.worksheetHitInfo) {
            let { rowViewportIndex, colViewportIndex } = rs.worksheetHitInfo
            if (rowViewportIndex === 1 && colViewportIndex === 1) {
              let { row, col } = rs.worksheetHitInfo
              curPos = [row, col]
            }
          }
          if (!posEq(curPos, lastCellPosition) && rs && rs.worksheetHitInfo) {
            listener(rs.worksheetHitInfo)
          }
          lastCellPosition = curPos
        }
      }
      hostEl.addEventListener("mousemove", mousemoveListener)
      return () => {
        hostEl.removeEventListener("mousemove", mousemoveListener)
      }
    }

    // 设置鼠标悬停在单元格上时的显示内容
    let setSpreadMouseHoverTitle = (spread, vueCore) => {
      let hostEl = spread.getHost()

      // 对hover单元格的key值缓存，防止两个单元格内容一致时不会更新title让其位置变化
      let hoverCellKey = ""
      let setTitle = (title, curHoverCellKey) => {
        if (hostEl.title == title && curHoverCellKey != hoverCellKey) {
          hostEl.title = ""
          setTimeout(function () {
            hostEl.title = title
          }, 0)
        } else {
          hostEl.title = title
        }
        hoverCellKey = curHoverCellKey
      }

      return listenMouseEnterCell(spread, ({ row, col }) => {
        if (row == -1 || col == -1) {
          hostEl.title = ""
        } else {
          let {
            configMode,
            propertyTreeConfig: {
              getName
            }
          } = vueCore.getConfig()
          let cellKey = row + "," + col
          if (configMode) {
            let property = vueCore.getProperty(row, col)
            setTitle(property ? getName(property) : "", cellKey)
          } else {
            let oriCellInfo = vueCore.getOriCellInfo(row, col)
            let text = spread.getActiveSheet().getText(row, col)
            let tip = oriCellInfo && oriCellInfo.tip
            let value
            if (!tip) {
              value = text
            } else {
              if (text) {
                value = "内容：" + text + "\n提示：" + tip
              } else {
                value = "提示：" + tip
              }
            }
            setTitle(value ? value.toString() : "", cellKey)
          }
        }
      })
    }

    let {
      setCellTagAttr,
      deleteCellTagAttr,
      getCellTagAttr
    } = (() => {
      let getCellTagObj = (cell) => {
        let tag = cell.tag()
        let tagObj = null
        if (!tag) {
          tagObj = {}
        } else {
          try {
            tagObj = JSON.parse(tag)
          } catch (error) {
            console.error(error)
            tagObj = {}
          }
        }
        return tagObj
      }

      let setCellTagObj = (cell, tagObj) => {
        cell.tag(JSON.stringify(tagObj))
      }

      return {
        setCellTagAttr: (cell, key, value) => {
          let tagObj = getCellTagObj(cell)
          tagObj[key] = value
          setCellTagObj(cell, tagObj)
        },
        deleteCellTagAttr: (cell, key) => {
          let tagObj = getCellTagObj(cell)
          if (key in tagObj) {
            delete tagObj[key]
            setCellTagObj(cell, tagObj)
          }
        },
        getCellTagAttr: (cell, key) => {
          return getCellTagObj(cell)[key]
        }
      }
    })()

    const AreaTypeEnum = (() => {
      let enumObj = {
        DATA: "1",
        SEGREGATE: "2",
        ALLOW_EXPAND: "3",
        ALLOW_ATTRIBUTE_EDIT: "4",
        properties: {
          "1": {
            description: "数据区域",
            allowOverlap: true,// 仅数据区域允许重叠
            titleTextColor: "rgba(255,0,0,0.7)",
            titlePrefix: "D",
            borderColor: "rgba(255,0,0,0.3)",
          },
          "2": {
            description: "隔离区域",
            allowOverlap: false,
            titleTextColor: "rgba(255,192,0,0.7)",
            titlePrefix: "S",
            borderColor: "rgba(255,192,0,0.3)",
          },
          "3": {
            description: "可扩展区域（限制模式下生效）",// 限制模式下生效
            allowOverlap: false,
            titleTextColor: "rgba(0,96,192,0.7)",
            titlePrefix: "E",
            borderColor: "rgba(0,96,192,0.5)",
          },
          "4": {
            description: "属性可编辑区域（限制模式下生效）",// 限制模式下生效
            allowOverlap: false,
            titleTextColor: "rgba(0,192,0,0.7)",
            titlePrefix: "A",
            borderColor: "rgba(0,192,0,0.5)",
          },
        }
      }
      Object.freeze(enumObj)
      return enumObj
    })()

    const ControlTypeEnum = (() => {
      let enumObj = {
        ENTIRELY_FILL_CTRL: "1",
        PREMISS_CTRL: "2",
        DEFAULT_SETTING_CTRL: "3",
        properties: {
          "1": {
            description: "完全回填"
          },
          "2": {
            description: "回填条件(单元格或数据区域)"
          },
          "3": {
            description: "设置默认回填值(单元格或数据区域)"
          },
        }
      }
      Object.freeze(enumObj)
      return enumObj
    })()

    const BillEntityChangeTypeEnum = (() => {
      let enumObj = {
        INSERT: 1,
        UPDATE: 2,
        DELETE: 3,
        NO_CHANGE: 0,
        properties: {
          1: {
            description: "增加单元格"
          },
          2: {
            description: "更新单元格"
          },
          3: {
            description: "删除单元格"
          },
          0: {
            description: "未变更"
          },
        }
      }
      Object.freeze(enumObj)
      return enumObj
    })()

    const AttributeModeEnum = (() => {
      let enumObj = {
        TEXT: "TextControl",
        NUMBER: "NumberControl",
        SELECT: "DropDownControl",
        EDITABLE_SELECT: "DropDownUpdateControl",
        SIGN: "SignControl",
        OTHER: "OtherControl",
        // BUTTON: "ButtonControl",
        RADIO: "SingleChoiceControl",
        DATE_TIME: "DateTimeControl",
        DATE: "DateControl",
        TIME: "HourMinute",
        TIME_WITH_SECOND: "HourMinuteSecond",
        properties: {
          "TextControl": {
            description: "文本",
            hasDefaultValue: true,
            onlyDefault: true
          },
          "NumberControl": {
            description: "数字",
            hasDefaultValue: false,
          },
          "DropDownControl": {
            description: "下拉选项",
            hasDefaultValue: true,
            onlyDefault: false
          },
          "DropDownUpdateControl": {
            description: "下拉选项（可编辑）",
            hasDefaultValue: true,
            onlyDefault: false
          },
          "SignControl": {
            description: "签字",
            hasDefaultValue: false,
          },
          "OtherControl": {
            description: "其他",
            hasDefaultValue: false,
          },
          // "ButtonControl": {
          //   description: "按钮",
          //   hasDefaultValue: false,
          // },
          "SingleChoiceControl": {
            description: "单选",
            hasDefaultValue: true,
          },
          "DateTimeControl": {
            description: "时间控件",
            hasDefaultValue: false,
          },
          "DateControl": {
            description: "日期控件",
            hasDefaultValue: false,
          },
          "HourMinute": {
            description: "时间控件(小时/分钟)",
            hasDefaultValue: false,
          },
          "HourMinuteSecond": {
            description: "时间控件(小时/分钟/秒)",
            hasDefaultValue: false,
          }
        }
      }
      Object.freeze(enumObj)
      return enumObj
    })()

    const BillCellTriggerTimingEnum = (() => {
      let enumObj = {
        BEFORE_CHANGE: 1,
        AFTER_CHANGE: 2,
        properties: {
          1: {
            description: "设置界面数据前触发"
          },
          2: {
            description: "设置界面数据后触发"
          },
        }
      }
      Object.freeze(enumObj)
      return enumObj
    })()

    const BillCellTriggerUrlTypeEnum = (() => {
      let enumObj = {
        FUNCTION: 1,
        PAGE: 2,
        properties: {
          1: {
            description: "接口方法"
          },
          2: {
            description: "页面"
          },
        }
      }
      Object.freeze(enumObj)
      return enumObj
    })()

    // 对象数据管理工具，防止外界对内部数据进行直接更改
    let dataMapUtil = (function () {
      let dataMap = new Map()
      return {
        hasData(object) {
          return dataMap.has(object)
        },
        getData(object) {
          return dataMap.has(object) ? dataMap.get(object) : null
        },
        setData(object, data) {
          dataMap.set(object, data)
        },
        deleteData(object) {
          dataMap.delete(object)
        }
      }
    })()
    // 获取对象数据，没有数据则先初始化
    let getData = (obj) => {
      if (dataMapUtil.hasData(obj)) {
        return dataMapUtil.getData(obj)
      } else {
        let data = {}
        dataMapUtil.setData(obj, data)
        return data
      }
    }

    let commonUtil = (() => {
      let mergeColorChannel = (channel1, alpha1, channel2, alpha2, mergedAlpha) => {
        return Math.round((channel1 * alpha1 * (1 - alpha2) + channel2 * alpha2) / mergedAlpha)
      }
      let fixNumber = v => parseFloat(v.toFixed(4))
      let mergeAlpha = (alpha1, alpha2) => {
        return fixNumber(alpha1 + alpha2 - alpha1 * alpha2)
      }
      let mergeTwoColors = ({ r: r1, g: g1, b: b1, a: a1 }, { r: r2, g: g2, b: b2, a: a2 }) => {
        let mergedAlpha = mergeAlpha(a1, a2)
        return {
          r: mergeColorChannel(r1, a1, r2, a2, mergedAlpha),
          g: mergeColorChannel(g1, a1, g2, a2, mergedAlpha),
          b: mergeColorChannel(b1, a1, b2, a2, mergedAlpha),
          a: mergedAlpha
        }
      }

      let toRgbaString = (colorObj) => {
        return `rgba(${colorObj.r},${colorObj.g},${colorObj.b},${colorObj.a})`
      }
      let toRgbString = (colorObj) => {
        return `rgb(${colorObj.r},${colorObj.g},${colorObj.b})`
      }

      return {
        everyCell: (rowIndex, columnIndex, rowCount, columnCount, callback) => {
          if (rowIndex == -1) {
            rowIndex = 0
          }
          if (columnIndex == -1) {
            columnIndex = 0
          }
          for (let row = rowIndex; row < rowIndex + rowCount; row++) {
            for (let col = columnIndex; col < columnIndex + columnCount; col++) {
              callback && callback(row, col)
            }
          }
        },
        // [{r,g,b,a},{r,g,b,a}...]
        mergeColors: (colors) => {
          let finalColor = colors[0]
          for (let index = 1; index < colors.length; index++) {
            const color = colors[index]
            finalColor = mergeTwoColors(finalColor, color)
          }
          return finalColor
        },
        parseRgbString: (rgb) => {
          if (typeof rgb == "string") {
            var result = /^rgb[a]?\([ \n]*([\d]+)[ \n]*,[ \n]*([\d]+)[ \n]*,[ \n]*([\d]+)[ \n]*,?[ \n]*([.\d]+)?[ \n]*\)$/i.exec(rgb)
            var rgbObj = {}
            rgbObj.r = parseInt(result[1])
            rgbObj.g = parseInt(result[2])
            rgbObj.b = parseInt(result[3])
            if (result.length > 4 && result[4] != null) {
              rgbObj.a = parseFloat(result[4])
            } else {
              rgbObj.a = 1
            }
            rgbObj.originColor = rgb
            return rgbObj
          } else {
            return null
          }
        },
        toRgbaString,
        toRgbString,
        colorObjToString: (colorObj) => {
          if (!isBlank(colorObj.a)) {
            return toRgbaString(colorObj)
          } else {
            return toRgbString(colorObj)
          }
        },
        listToMap: (list, getKey) => {
          var map = {}
          list.forEach(v => {
            var key = getKey(v)
            if (!isBlank(key)) {
              map[key] = v
            }
          })
          return map
        },
        integersToRange: (integers) => {
          if (!integers || integers.length == 0) {
            return []
          }
          let sortedIntegers = [...integers].sort((a, b) => a - b)
          let ranges = []
          let curRange = [sortedIntegers[0], sortedIntegers[0]]
          for (let i = 1; i < sortedIntegers.length; i++) {
            const integer = sortedIntegers[i]
            if (integer === curRange[1] + 1) {
              curRange[1] = integer
            } else {
              ranges.push(curRange)
              curRange = [integer, integer]
            }
          }
          ranges.push(curRange)
          return ranges
        },
        isBlank: (str) => {
          return isBlank(str)
        },
        forEach: (array, callback) => {
          let index = 0
          let splice = (start, deleteCount, ...items) => {
            start = start > array.length - 1 ? array.length - 1 : start
            start = start < 0 ? 0 : start
            let deletes = array.splice(start, deleteCount, ...items)
            deleteCount = deletes.length

            let addItemCount = 0
            if (items && items.length > 0) {
              addItemCount = items.length
            }

            let cIndex = index

            if (deleteCount > 0) {
              let end = start + deleteCount - 1
              if (start <= index && index <= end) {
                cIndex = start - 1
              } else if (end < start) {
                cIndex -= deleteCount
              }
            }
            if (addItemCount > 0) {
              if (start <= index) {
                cIndex += addItemCount
              }
            }
            index = cIndex
          }
          for (; index < array.length; index++) {
            const element = array[index]
            callback(element, index, splice)
          }
        },
        getSheetNameFromCommandOptions: (options) => {
          if (!options.sheetName) {
            return null
          } else {
            let sheetName = options.sheetName
            if (typeof sheetName == "string") {
              return sheetName
            } else if (Array.isArray(sheetName)) {
              return sheetName[0]
            } else {
              return null
            }
          }
        },
        getUniqueCallFunction: () => {
          let tempMap = {}
          return (call, key) => {
            if (!tempMap[key]) {
              call()
              tempMap[key] = true
            }
          }
        },
        getArrayFillBy: (length, fillObj) => {
          let arr = new Array(length).fill(null)
          if (fillObj != null) {
            return arr.map(() => (copyDataSimple(fillObj)))
          } else {
            return arr
          }
        },
        isCellInArea: (row, col, { x, y, width, height }) => {
          return row >= y && row < y + height && col >= x && col < x + width
        },
        checkSingleCellSelection: (selections, sheet) => {
          if (selections.length == 1) {
            if (selections[0].colCount == 1 && selections[0].rowCount == 1) {
              return true
            }
            let { row, col, colCount, rowCount } = selections[0]
            let span = sheet.getSpan(row, col)
            if (span && span.row == row && span.col == col && span.colCount == colCount && span.rowCount == rowCount) {
              return true
            }
          }
          return false
        },
        isHideBySpan: (row, col, sheet) => {
          let spanInfo = sheet.getSpan(row, col)
          if (spanInfo && !(spanInfo.row == row && spanInfo.col == col)) {
            return true
          } else {
            return false
          }
        },
        getCellName: (sheet, row, col) => {
          let colName = sheet.getValue(0, col, Sheets.SheetArea.colHeader)
          let rowName = sheet.getValue(row, 0, Sheets.SheetArea.rowHeader)
          return colName + rowName
        },
        getSelectedRangeString(range, getRowCount, getColumnCount) {
          //得到选择区域的行列数描述字符串
          var selectionInfo = ""
          var rowCount = range.rowCount
          var columnCount = range.colCount
          if (rowCount < 0 && columnCount > 0) {
            selectionInfo = columnCount + "C"
          } else if (columnCount < 0 && rowCount > 0) {
            selectionInfo = rowCount + "R"
          } else if (rowCount < 0 && columnCount < 0) {
            selectionInfo = getRowCount() + "R x " + getColumnCount() + "C"
          } else {
            selectionInfo = rowCount + "R x " + columnCount + "C"
          }
          return selectionInfo
        },
        extends: (target, source) => {
          for (var key in source) {
            if (source.hasOwnProperty(key)) {
              target[key] = source[key]
            }
          }
          function __() {
            this.constructor = target
          }
          __.prototype = source.prototype
          target.prototype = new __()
        },
        trimArray: (array) => {
          if (array.length > 0) {
            let i = array.length - 1
            while (array[i] == null && i > -1) {
              i--
            }
            array.splice(i + 1, (array.length - 1) - i)
          }
        },
        executeFnUseForceEnd: (() => {
          function InnerError() {
          }
          return (fn) => {
            let forceEnd = () => {
              throw new InnerError()
            }
            try {
              fn(forceEnd)
            } catch (error) {
              if (!(error instanceof InnerError)) {
                throw error
              }
            }
          }
        })(),
        getLinkedMap: () => {
          return (() => {
            let valuesObj = {}
            let startKey = null
            let endKey = null
            let keyNextKeyMap = {}
            let keyPreKeyMap = {}
            let hasKey = (key) => {
              return key in valuesObj
            }
            let get = (key) => {
              return valuesObj[key]
            }
            let remove = (key) => {
              let preKey = keyPreKeyMap[key]
              let nextKey = keyNextKeyMap[key]
              delete keyPreKeyMap[key]
              delete keyNextKeyMap[key]
              delete valuesObj[key]
              if (preKey != null) {
                keyNextKeyMap[preKey] = nextKey
              } else {
                startKey = nextKey
              }
              if (nextKey != null) {
                keyPreKeyMap[nextKey] = preKey
              } else {
                endKey = preKey
              }
            }
            let set = (key, value) => {
              if (hasKey(key)) {
                remove(key)
              }
              if (startKey == null) {
                valuesObj[key] = value
                startKey = key
                endKey = key
                keyNextKeyMap[key] = null
                keyPreKeyMap[key] = null
              } else {
                valuesObj[key] = value
                keyNextKeyMap[endKey] = key
                keyNextKeyMap[key] = null
                keyPreKeyMap[key] = endKey
                endKey = key
              }
            }
            let forEach = (callback) => {
              let i = 0
              let key = startKey
              while (hasKey(key)) {
                callback(key, get(key), i)
                key = keyNextKeyMap[key]
              }
            }
            let values = () => {
              let arr = []
              forEach((_, v) => arr.push(v))
              return arr
            }
            let keys = () => {
              let arr = []
              forEach(v => arr.push(v))
              return arr
            }
            return {
              set,
              hasKey,
              remove,
              get,
              forEach,
              values,
              keys
            }
          })()
        },
        uniqueAlertMessage: (() => {
          let map = {}
          return (key, doAlertMessage) => {
            if (map[key]) {
              map[key]()
            }
            map[key] = doAlertMessage(() => {
              map[key] = null
            })
          }
        })(),
        getEnumConstants: (enumData) => {
          return Object.keys(enumData.properties)
        },
        getCachedFn: (fn) => {
          let cacheObj = {}
          return (key) => {
            if (!(key in cacheObj)) {
              cacheObj[key] = fn(key)
            }
            return cacheObj[key]
          }
        },
        isAreaAContainAreaB(areaA, areaB) {
          let { y: yA, x: xA, height: hA, width: wA } = areaA
          let { y: yB, x: xB, height: hB, width: wB } = areaB
          return xB >= xA && xB + wB <= xA + wA && yB >= yA && yB + hB <= yA + hA
        },
        hasAreaIntersection(areaA, areaB) {
          let { y: yA, x: xA, height: hA, width: wA } = areaA
          let { y: yB, x: xB, height: hB, width: wB } = areaB
          return !(xB + wB - 1 < xA) && !(xB > xA + wA - 1) && !(yB + hB - 1 < yA) && !(yB > yA + hA - 1)
        },
        isAreaRangeEquals(areaA, areaB) {
          return areaA.x == areaB.x && areaA.y == areaB.y && areaA.width == areaB.width && areaA.height == areaB.height
        },
        mergeConfigs,
      }
    })()

    var DateTimeCellType = (function () {
      let TYPE_NAME = "c1"
      Sheets.CellTypes._o[TYPE_NAME] = DateTimeCellType
      let initDateTimeCellTypeCss = () => {
        let styleId = "__date-time-cell-type-style__"
        if (!document.getElementById(styleId)) {
          let styleEl = document.createElement("style")
          styleEl.innerHTML = `
.spread-datetime-picker.el-date-editor {
  width: 100%;
  height: 100%;
  display: block;
}

.spread-datetime-picker.el-date-editor .el-input__inner {
  padding: 0px;
  text-align: center;
  width: 100%;
  height: 100%;
  border: none;
  border-radius: 0px;
  background-color: transparent;
}

.spread-datetime-picker.el-date-editor span.el-input__prefix {
  display: none;
}

.spread-datetime-picker.el-date-editor .el-picker-panel {
  height: auto;
}`
          styleEl.id = styleId
          document.head.append(styleEl)
        }
      }
      let getConfig = () => {
        return new Vue({
          data: {
            formatString_: null,
            datetimeDefaultFormatString: 'yyyy-MM-dd HH:mm:ss',
            dateDefaultFormatString: 'yyyy-MM-dd',
            timeDefaultFormatString: 'HH:mm:ss',
            type: DateTimeCellType.type.datetime
          },
          computed: {
            formatString: {
              get() {
                if (this.formatString_) {
                  return this.formatString_
                } else {
                  if (this.type == DateTimeCellType.type.datetime) {
                    return this.datetimeDefaultFormatString
                  } else if (this.type == DateTimeCellType.type.date) {
                    return this.dateDefaultFormatString
                  } else if (this.type == DateTimeCellType.type.time) {
                    return this.timeDefaultFormatString
                  }
                }
              },
              set(formatString) {
                this.formatString_ = formatString
              }
            }
          }
        })
      }
      let _super = Sheets.CellTypes.Base
      commonUtil.extends(DateTimeCellType, _super)

      function DateTimeCellType() {
        _super.call(this)
        this.config = getConfig()
        this.typeName = TYPE_NAME
      }

      DateTimeCellType.prototype.toJSON = function () {
        let json = {}
        if (this.config.formatString_) {
          json.format = this.config.formatString_
        }
        json.type = this.config.type
        json.typeName = this.typeName
        return json
      }

      DateTimeCellType.prototype.fromJSON = function (json) {
        if (json.format) {
          this.setFormat(json.format)
        }
        if (json.type) {
          this.setType(json.type)
        }
      }

      DateTimeCellType.type = {
        datetime: "datetime",
        date: "date",
        time: "time"
      }

      DateTimeCellType.prototype.setFormat = function (formatString) {
        if (formatString) {
          this.config.formatString = formatString
        }
      }

      DateTimeCellType.prototype.setType = function (type) {
        if (type) {
          this.config.type = type
        }
      }

      DateTimeCellType.prototype.getFormat = function () {
        return this.config.formatString
      }

      DateTimeCellType.prototype.getType = function () {
        return this.config.type
      }

      DateTimeCellType.prototype.createEditorElement = function (context, parentNode) {
        if (!window.ELEMENT) {
          alert("时间控件依赖elementUI，如果您是用户请联系管理员")
          return null
        }
        initDateTimeCellTypeCss()
        parentNode.style.overflow = "visible"
        parentNode.children[0].style.width = "100%"
        parentNode.children[0].style.height = "100%"
        let vueObj = new Vue({
          template: `
            <div style="width: 100%;height: 100%;display: block;">
              <component
                :is="component"
                ref="picker"
                v-model="dynamicValue"
                :clearable="false"
                :format="format"
                placeholder=""
                :append-to-body="false"
                class="spread-datetime-picker"
                :type="type"
              >
              </component>
            </div>
          `,
          data() {
            return {
              innerValue: null,
              dynamicValue: null,
            }
          },
          computed: {
            value: {
              set(v) {
                this.innerValue = v
                this.dynamicValue = v
              },
              get() {
                return this.innerValue
              }
            },
            format: () => {
              return this.config.formatString
            },
            type: () => {
              return this.config.type
            },
            component() {
              if (this.type === DateTimeCellType.type.time) {
                return "el-time-picker"
              } else {
                return "el-date-picker"
              }
            }
          }
        })
        vueObj.$mount()
        return vueObj.$el
      }

      DateTimeCellType.prototype.getEditorValue = function (editorContext) {
        if (editorContext && editorContext.__vue__) {
          let rootVueObj = editorContext.__vue__.$root
          if (window.event && window.event.type === "keydown" && window.event.code === "Enter") {
            this.setEditorValue(editorContext, rootVueObj.dynamicValue)
          }
          return rootVueObj.value
        }
      }

      DateTimeCellType.prototype.setEditorValue = function (editorContext, value) {
        if (editorContext && editorContext.__vue__) {
          editorContext.__vue__.$root.value = typeof value == "string" ? null : value
        }
      }

      wrapFn(DateTimeCellType.prototype, "activateEditor", function (doOri, [editorContext, cellStyle, cellRect, context]) {
        if (editorContext && editorContext.__vue__) {
          let root = editorContext.__vue__.$root
          let pickerVueObj = root.$refs.picker
          pickerVueObj.showPicker()
          let pickListener = (dateValue, visible) => {
            // 隐藏时触发退出编辑
            if (!visible) {
              pickerVueObj.$nextTick(() => {
                this.setEditorValue(editorContext, dateValue)
                context.sheet.endEdit()
              })
            }
          }
          this.endListen = () => {
            pickerVueObj.picker.$off("pick", pickListener)
            this.endListen = null
          }
          pickerVueObj.picker.$on("pick", pickListener)
        }
        doOri()
      })

      wrapFn(DateTimeCellType.prototype, "deactivateEditor", function (doOri, args) {
        if (this.endListen) {
          this.endListen()
        }
        doOri()
      })
      return DateTimeCellType
    })()

    // 进入编辑状态立刻弹出下拉选项，关闭下拉选项后自动退出编辑
    var CustomComboBox = (function () {
      let TYPE_NAME = "c2"
      Sheets.CellTypes._o[TYPE_NAME] = CustomComboBox

      let _super = Sheets.CellTypes.ComboBox
      commonUtil.extends(CustomComboBox, _super)

      function CustomComboBox() {
        _super.call(this)
        this.typeName = TYPE_NAME
        this.isDeactivatingEditor = false
      }

      wrapFn(CustomComboBox.prototype, "toJSON", function (doOri, args) {
        let json = doOri()
        json.typeName = this.typeName
        return json
      })

      wrapFn(CustomComboBox.prototype, "activateEditor", function (doOri, args) {
        doOri()
        setTimeout(() => {
          let editorContext = args[0]
          let comboBox = editorContext && editorContext.parentNode.parentNode.comboBox
          if (comboBox) {
            comboBox.showDropDownList()
            wrapFn(comboBox, "closeDropDownList", (doOri) => {
              doOri()
              let { sheet } = args[3]
              if (!this.isDeactivatingEditor) {
                sheet.endEdit()
              }
            })
          }
        }, 0)
      })

      wrapFn(CustomComboBox.prototype, "deactivateEditor", function (doOri, args) {
        this.isDeactivatingEditor = true
        doOri()
        setTimeout(() => {
          this.isDeactivatingEditor = false
          let { sheet } = args[1]
          // VERSION_CHECK 修复选择下拉选项后可能会忽略下次选择单元格事件的bug
          let ignoreNextSelectionChangedEventProperty = "Uja"
          if (sheet[ignoreNextSelectionChangedEventProperty]) {
            delete sheet[ignoreNextSelectionChangedEventProperty]
          }
        }, 0)
      })

      return CustomComboBox
    })()

    let ssjsonUtil = {
      getFirstSheetFromJsonObj({
        sheets
      }) {
        return sheets[Object.keys(sheets).find(k => sheets[k].index == 0)]
      },
      everySheet({
        sheets
      }, callback) {
        for (const key in sheets) {
          callback(sheets[key])
        }
      },
      clearCellLock(ssjsonObj) {
        let namedStylesMap = this.getNamedStylesMap(ssjsonObj)
        this.everySheet(ssjsonObj, (sheet) => {
          let sheetTable = sheet.data.dataTable
          if (Object.hasOwnProperty.call(sheet, "isProtected")) {
            delete sheet.isProtected
          }
          if (Object.hasOwnProperty.call(sheet, "protectionOptions")) {
            delete sheet.protectionOptions
          }
          let [rowCount, columnCount] = this.getRowsAndColumnsFromSsjsonSheet(sheet)
          commonUtil.everyCell(0, 0, rowCount, columnCount, (row, col) => {
            this.setLockedToSsjsonTable(sheetTable, row, col, true, namedStylesMap)
          })
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
    }

    // 定义校验结果对象
    var ValidateResult = (function () {
      function ValidateResult(isVd, msg) {
        this.isVd = isVd
        this.msg = msg
      }
      ValidateResult.prototype.isValidated = function () {
        return !!this.isVd
      }
      ValidateResult.prototype.getMessage = function () {
        return this.msg
      }
      return ValidateResult
    })()

    // 定义区域选择状态展示类
    var RangeSelectorItem = (function () {
      var StatusItem = Sheets.StatusBar.StatusItem
      commonUtil.extends(RangeSelectorItem, StatusItem)
      function RangeSelectorItem(name, options) {
        StatusItem.call(this, name, options)
      }
      RangeSelectorItem.prototype.onCreateItemView = function (container) {
        this._container = container
        var valueControl = this._valueControl
        if (!valueControl) {
          valueControl = this._valueControl = document.createElement("span")
        }
        container.appendChild(valueControl)
      }
      RangeSelectorItem.prototype.onBind = function (context) {
        this._context = context
        this.selectionChanging = false
        this.onUpdate()
        let onSpreadSelectionChanging = getLogErrorFn(() => {
          this.selectionChanging = true
          this.onUpdate()
        })
        let onSpreadSelectionChanged = getLogErrorFn(() => {
          this.selectionChanging = false
          this.onUpdate()
        })
        let Events = Sheets.Events
        context.bind(Events.SelectionChanging, onSpreadSelectionChanging)
        context.bind(Events.SelectionChanged, onSpreadSelectionChanged)
        return () => {
          this._context = null
          context.unbind(Events.SelectionChanging, onSpreadSelectionChanging)
          context.unbind(Events.SelectionChanged, onSpreadSelectionChanged)
        }
      }
      reduceBind(RangeSelectorItem.prototype, "onBind", "onUnbind")
      RangeSelectorItem.prototype.onUpdate = function () {
        StatusItem.prototype.onUpdate.call(this)
        if (this._context && this._context.getActiveSheet && this._context.getActiveSheet()) {
          updateValue(this._context, this.selectionChanging, (value) => {
            if (this._valueControl) {
              this._valueControl.innerText = value
            }
          })
        }
      }
      function updateValue(spread, isSelecting, setValue) {
        var activeSheet = spread.getActiveSheet()
        var selection = activeSheet.getSelections()[activeSheet.getSelections().length - 1]
        if (selection !== null && selection !== undefined) {
          var selectionInfo
          if (commonUtil.checkSingleCellSelection([selection], activeSheet) || !isSelecting) {
            selectionInfo = commonUtil.getCellName(activeSheet, activeSheet.getActiveRowIndex(), activeSheet.getActiveColumnIndex())
          } else {
            selectionInfo = commonUtil.getSelectedRangeString(selection, () => activeSheet.getRowCount(), () => activeSheet.getColumnCount())
          }
          setValue(selectionInfo)
        }
      }
      return RangeSelectorItem
    })()

    // 将属性信息标记在单元格的tag数据里，防止设置属性时因没有任何表格数据的改变而导致无法undo
    let basicAttrTagWrapper = (attr, vueCore, getCellPosition) => {
      wrapFn(attr, "initCellAttribute", function (doOri, args) {
        let removeCellAttribute = doOri()
        let [innerCellId, sheet] = args
        let [row, col] = getCellPosition(innerCellId)
        setCellTagAttr(sheet.getCell(row, col), "attr", this.code)
        return () => {
          let [row, col] = getCellPosition(innerCellId)
          if (removeCellAttribute) {
            removeCellAttribute()
          }
          deleteCellTagAttr(sheet.getCell(row, col), "attr")
        }
      })
    }

    let validateCellWrapper = (attr, vueCore, getCellPosition) => {
      let isAttributeValueRequiredActually = (attrObj) => {
        return vueCore.isAttributeValueRequiredActually(attrObj.code)
      }
      let isCellDisabledInner = (innerCellId) => {
        let [row, col] = getCellPosition(innerCellId)
        return vueCore.isCellDisabled(row, col)
      }
      let validateCellValueRequired = (attrObj, innerCellId, source) => {
        if (isAttributeValueRequiredActually(attrObj)) {
          let [row, col] = getCellPosition(innerCellId)
          let val = source.getValue(row, col)
          if (!isBlank(val)) {
            return new ValidateResult(true)
          } else {
            return new ValidateResult(false, "此内容为必填项")
          }
        } else {
          return new ValidateResult(true)
        }
      }

      wrapFn(attr, "validateCell", function (doOri, args) {
        let [innerCellId, source] = args
        if (isCellDisabledInner(innerCellId)) { // 跳过被禁用的单元格
          return new ValidateResult(true)
        } else {
          let rs = doOri()
          if (rs && !rs.isValidated()) {
            return rs
          } else {
            return validateCellValueRequired(this, innerCellId, source)
          }
        }
      })
    }

    let disableSetWrapper = (attr, vueCore, getCellPosition) => {
      // 判断单元格是否需要隐藏额外影响
      let isCellHideEffect = (innerCellId) => {
        let [row, col] = getCellPosition(innerCellId)
        return vueCore.isCellDisabled(row, col) && !attr.showEffectOnCellDisabled
      }

      wrapFn(attr, "initCellAttribute", function (doOri, args) {
        let unbinds = []
        let removeCellAttribute = null

        let [innerCellId, sheet] = args
        // 如果单元格没有被禁用，初始化单元格属性影响
        if (!isCellHideEffect(innerCellId)) {
          removeCellAttribute = doOri()
        }
        unbinds.unshift(() => {
          if (removeCellAttribute) {
            removeCellAttribute()
          }
        })

        // 通过vue监听，根据单元格的禁用情况动态初始化、结束单元格属性影响
        let endWatchCellDisabled = vueCore.$watch(() => {
          return isCellHideEffect(innerCellId)
        }, (cellDisabled) => {
          if (cellDisabled) {
            if (removeCellAttribute) {
              removeCellAttribute()
              removeCellAttribute = null
            }
          } else {
            removeCellAttribute = doOri()
          }
        })
        unbinds.unshift(endWatchCellDisabled)

        return () => {
          unbinds.forEach(v => v())
        }
      })
    }

    // 获取数据之后包装属性配置
    let regAttrWrapperAfterDataInited = (attr, vueCore, getCellPosition) => {
      basicAttrTagWrapper(attr, vueCore, getCellPosition)
      validateCellWrapper(attr, vueCore, getCellPosition)
      disableSetWrapper(attr, vueCore, getCellPosition)
    }

    function getPropClipboard(setProperty, deleteProperty, getAllCellPropertyData, getPropertyKeyFromCellData, propClipboardInfo) {
      let _getPropData = (row, col, propData) => {
        if (!propData) {
          propData = getAllCellPropertyData()
        }
        return propData[row] && propData[row][col]
      }
      return {
        copyData: propClipboardInfo ? propClipboardInfo.copyData : null,
        selections: propClipboardInfo ? propClipboardInfo.selections : null,
        isCut: propClipboardInfo ? propClipboardInfo.isCut : false,
        copyProp(selections) {
          this.selections = copyDataSimple(selections)
          this.copyData = selections.map(({
            row,
            col,
            rowCount,
            colCount
          }) => {
            if (row == -1) {
              row = 0
            }
            if (col == -1) {
              col = 0
            }
            let copyData = []
            commonUtil.everyCell(row, col, rowCount, colCount, (row, col) => {
              let cellPropData = _getPropData(row, col)
              if (cellPropData) {
                if (!copyData[row]) {
                  copyData[row] = []
                }
                copyData[row][col] = copyDataSimple(cellPropData)
              }
            })
            return {
              data: copyData,
              area: {
                row,
                col,
                rowCount,
                colCount
              }
            }
          })
          this.isCut = false
        },
        cutProp(selections) {
          this.copyProp(selections)
          this.isCut = true
        },
        pasteProp(selections, sheet) {
          if (!this.copyData) {
            return
          }
          if (this.isCut) {
            this.isCut = false
            this.copyData.forEach(({
              area: {
                row,
                col,
                rowCount,
                colCount
              }
            }) => {
              commonUtil.everyCell(row, col, rowCount, colCount, (row, col) => {
                deleteProperty({ rowIndex: row, columnIndex: col })
              })
            })
          }

          let copyCellPropTo = (fromRow, fromCol, toRow, toCol, fromCopyData) => {
            let cellPropData = _getPropData(fromRow, fromCol, fromCopyData)
            if (cellPropData) {
              let propKey = getPropertyKeyFromCellData(cellPropData)
              setProperty({ rowIndex: toRow, columnIndex: toCol, propertyKey: propKey, sheet })
            } else {
              deleteProperty({ rowIndex: toRow, columnIndex: toCol })
            }
          }

          // 如果复制的数据只有一个单元格，则使用fill的逻辑进行复制
          if (this.copyData.length == 1 && this.copyData[0].area.rowCount == 1 && this.copyData[0].area.colCount == 1) {
            let {
              data: fromCopyData,
              area: {
                row: fromCellRow,
                col: fromCellCol,
              }
            } = this.copyData[0]
            selections.forEach(({
              row: startRow,
              col: startCol,
              rowCount: startRowCount,
              colCount: startColCount
            }) => {
              commonUtil.everyCell(startRow, startCol, startRowCount, startColCount, (toCellRow, toCellCol) => {
                if (!commonUtil.isHideBySpan(toCellRow, toCellCol, sheet)) {
                  copyCellPropTo(fromCellRow, fromCellCol, toCellRow, toCellCol, fromCopyData)
                }
              })
            })
          } else {
            selections.forEach(({
              row: startRow,
              col: startCol
            }) => {
              this.copyData.forEach(({
                data: fromCopyData,
                area: {
                  row: copyStartRow,
                  col: copyStartCol,
                  rowCount: copyRowCount,
                  colCount: copyColCount
                }
              }) => {
                commonUtil.everyCell(copyStartRow, copyStartCol, copyRowCount, copyColCount, (fromRow, fromCol) => {
                  if (!commonUtil.isHideBySpan(fromRow, fromCol, sheet)) {
                    let toRow = fromRow + (startRow - copyStartRow)
                    let toCol = fromCol + (startCol - copyStartCol)
                    copyCellPropTo(fromRow, fromCol, toRow, toCol, fromCopyData)
                  }
                })
              })
            })
          }
        },
        clear() {
          this.copyData = null
          this.selections = null
          this.isCut = false
          this.pageNum = null
        },
        getPropClipboardInfo() {
          return {
            copyData: copyDataSimple(this.copyData),
            selections: copyDataSimple(this.selections),
            isCut: this.isCut
          }
        }
      }
    }

    // 组内可以统一进行 行、列的删除、增加
    let getCellDataManagerGroup = () => {

      let addCopyRows = (manager, rowIndex, rowCount, rowData) => {
        let allCellData = manager.getAllCellData()
        let rowDataList = commonUtil.getArrayFillBy(rowCount, rowData)
        if (allCellData.length <= rowIndex) {
          manager.$set(allCellData, rowIndex, null)
        }
        allCellData.splice(rowIndex, 0, ...rowDataList)
      }

      let addCopyColumns = (manager, columnIndex, columnCount, getColumnData) => {
        manager.getAllCellData().forEach((rowData, i) => {
          if (rowData) {
            let columnProperty = getColumnData(rowData, i)
            let columns = commonUtil.getArrayFillBy(columnCount, columnProperty)
            if (rowData.length <= columnIndex) {
              manager.$set(rowData, columnIndex, null)
            }
            rowData.splice(columnIndex, 0, ...columns)
          }
        })
      }

      let modesForAddCells = (() => {
        let obj = {
          COPY: 1,
          properties: {
            1: {
              description: "添加新行或新列时，复制指定行或指定列",
              extraArgs: [
                {
                  name: "sourceRowIndex",
                  description: "新行的复制源的行坐标",
                  scopeFunction: "addRows"
                },
                {
                  name: "sourceColumnIndex",
                  description: "新列的复制源的列坐标",
                  scopeFunction: "addColumns"
                },
              ],
              setAddRowsData(manager, rowIndex, rowCount, extraArgs) {
                if (!extraArgs || extraArgs.sourceRowIndex == null) {
                  throw "getAddRowsData参数错误"
                }
                let allCellData = manager.getAllCellData()
                let sourceRowIndex = extraArgs.sourceRowIndex
                let rowData = allCellData[sourceRowIndex] || null
                addCopyRows(manager, rowIndex, rowCount, rowData)
              },
              setAddColumnsData(manager, columnIndex, columnCount, extraArgs) {
                if (!extraArgs || extraArgs.sourceColumnIndex == null) {
                  throw "setAddColumnsData参数错误"
                }
                let sourceColumnIndex = extraArgs.sourceColumnIndex
                addCopyColumns(manager, columnIndex, columnCount, (rowData) => {
                  return rowData[sourceColumnIndex] || null
                })
              }
            }
          }
        }
        Object.freeze(obj)
        return obj
      })()

      let managers = []

      function UnobservableMockVue({ data, methods }) {
        return {
          ...data,
          ...methods,
          $set(obj, key, value) {
            obj[key] = value
          }
        }
      }

      let managerGroup = {
        modesForAddCells,
        getCellDataManager: (config) => {
          let { modeForAddCell, onDeleteCell, observable } = config ? config : {}
          if (observable == null) {
            observable = true
          }
          let vueConfig = {
            data: {
              allCellData: [],
            },
            methods: {
              setCellData(row, col, data) {
                let allCellData = this.allCellData
                if (!allCellData[row]) {
                  this.$set(allCellData, row, [])
                }
                this.$set(allCellData[row], col, data)
              },
              getCellData(row, col) {
                let allCellData = this.allCellData
                if (allCellData[row] && allCellData[row][col]) {
                  return allCellData[row][col]
                } else {
                  return null
                }
              },
              deleteCellData(row, col) {
                if (this.getCellData(row, col)) {
                  let allCellData = this.allCellData
                  this.$set(allCellData[row], col, null)
                  commonUtil.trimArray(allCellData[row])
                  if (allCellData[row].length === 0) {
                    this.$set(allCellData, row, null)
                  }
                }
              },
              clearCellData() {
                this.allCellData = []
              },
              getAllCellData() {
                return this.allCellData
              },
              setAllCellData(allCellData) {
                this.allCellData = allCellData
              },
              addRows(rowIndex, rowCount, extraArgs) {
                let modeObj = modesForAddCells.properties[modeForAddCell]
                if (modeObj && modeObj.setAddRowsData) {
                  modeObj.setAddRowsData(this, rowIndex, rowCount, extraArgs)
                } else {
                  addCopyRows(this, rowIndex, rowCount, null)
                }
              },
              addColumns(columnIndex, columnCount, extraArgs) {
                let modeObj = modesForAddCells.properties[modeForAddCell]
                if (modeObj && modeObj.setAddColumnsData) {
                  modeObj.setAddColumnsData(this, columnIndex, columnCount, extraArgs)
                } else {
                  addCopyColumns(this, columnIndex, columnCount, () => null)
                }
              },
              deleteRows(rowIndex, rowCount) {
                let tempRowCount = rowCount
                for (let row = rowIndex; row < rowIndex + tempRowCount; row++) {
                  if (!this.allCellData[row]) {
                    this.$set(this.allCellData, row, null)
                  }
                  let [rowData] = this.allCellData.splice(row, 1)
                  if (rowData && onDeleteCell) {
                    rowData.forEach((cellData) => {
                      onDeleteCell(cellData)
                    })
                  }
                  row--
                  tempRowCount--
                }
              },
              deleteColumns(columnIndex, columnCount) {
                this.allCellData.forEach((rowData, row) => {
                  if (rowData) {
                    let tempColumnCount = columnCount
                    for (let col = columnIndex; col < columnIndex + tempColumnCount; col++) {
                      if (!rowData[col]) {
                        this.$set(rowData, col, null)
                      }
                      let [cellData] = rowData.splice(col, 1)
                      if (cellData && onDeleteCell) {
                        onDeleteCell(cellData)
                      }
                      col--
                      tempColumnCount--
                    }
                  }
                })
              },
              forEachCellData(callback) {
                let allCellData = this.allCellData
                callback && allCellData.forEach((rowData, row) => {
                  if (!rowData) {
                    return
                  }
                  rowData.forEach((cellData, col) => {
                    if (cellData != null) {
                      callback(row, col, cellData)
                    }
                  })
                })
              },
              findCellData(callback) {
                let allCellData = this.allCellData
                let foundCellData = null
                callback && allCellData.find((rowData, row) => {
                  if (!rowData) {
                    return false
                  }
                  foundCellData = rowData.find((cellData, col) => {
                    if (cellData) {
                      return callback(row, col, cellData)
                    } else {
                      return false
                    }
                  })
                  if (foundCellData) {
                    return true
                  } else {
                    return false
                  }
                })
                return foundCellData
              }
            }
          }
          let manager = observable ? new Vue(vueConfig) : UnobservableMockVue(vueConfig)
          managers.push(manager)
          return manager
        }
      }

      let fns = ["addRows", "addColumns", "deleteRows", "deleteColumns"]
      fns.forEach(fn => {
        managerGroup[fn] = (...args) => {
          managers.forEach(manager => manager[fn](...args))
        }
      })

      return managerGroup
    }

    let getGetterForAttributeOriOperationalByMode = ({
      getCellPosition,// 根据innerCellId获取单元格位置
      getInnerCellId,// 根据单元格位置获取innerCellId
      getCellId,// 根据单元格位置获取cellId
      startTriggerUrl,// 执行检查单元格url操作的单元格变更
      getNewCellData,// 根据cellId获取单元格新数据
      getCellChangeType,// 根据cellId获取单元格当前变更状态
      getCheckCellPreposedControlUtil,// 前置区域检查工具初始化方法
    }) => {
      let getCachedData = (vueCore) => {
        return new Vue({
          computed: {
            isDisableOptions() {// 是否禁用下拉列表，仅在回填只读模式下生效
              let config = vueCore.getConfig()
              return config.readOnly && !config.configMode
            },
            extraOptionValueListMap() {
              let cellsString = vueCore.originalData.cellsString
              if (cellsString) {
                let map = {}
                let config = vueCore.getConfig()
                let {
                  getCellRow,
                  getCellCol
                } = getTransCellDataConfig(config)
                let cells = JSON.parse(cellsString)
                cells.forEach(cellData => {
                  let cellId = getInnerCellId(
                    getCellRow(cellData),
                    getCellCol(cellData)
                  )
                  if (!map[cellId]) {
                    let extraOptionValues = cellData.extraOptions
                    if (extraOptionValues && extraOptionValues.length > 0) {
                      map[cellId] = extraOptionValues
                    }
                    if (getNewCellData(cellId)) {
                      let newCellExtraOptionValues = getNewCellData(cellId).extraOptions
                      if (newCellExtraOptionValues && newCellExtraOptionValues.length > 0) {
                        map[cellId] = newCellExtraOptionValues
                      }
                    }
                  } else {
                    console.error("单元格数据重复", cellData)
                  }
                })
                return map
              } else {
                return {}
              }
            },
          }
        })
      }
      let getOptionsAttribute = (editable, vueCore) => {
        let cachedData = getCachedData(vueCore)
        let obj = {
          initCellAttribute(innerCellId, sheet) {
            let endFns = []
            let removeCellAttribute = () => {
              let [row, col] = getCellPosition(innerCellId)
              if (!cachedData.isDisableOptions) {
                sheet.setCellType(row, col, null)
              }
              let fn = null
              while (fn = endFns.shift()) {
                fn()
              }
            }
            let initCellAttribute = () => {
              let [row, col] = getCellPosition(innerCellId)
              if (!cachedData.isDisableOptions) {
                let propertyOptionValues = vueCore.propertyOptionValueListMap[this.code] || []
                let extraOptionValues = cachedData.extraOptionValueListMap[innerCellId] || []
                let optionValues = [].concat(propertyOptionValues, extraOptionValues)

                let comboBoxCellType = new CustomComboBox()
                comboBoxCellType.editable(editable)
                comboBoxCellType.items(optionValues)
                sheet.setCellType(row, col, comboBoxCellType)

                let endWatchCellExtraOptions = cachedData.$watch(() => {
                  return JSON.stringify(cachedData.extraOptionValueListMap[innerCellId] || [])
                }, () => {
                  removeCellAttribute()
                  initCellAttribute()
                })
                endFns.unshift(() => endWatchCellExtraOptions())
              }
              let endWatchIsDisableOptions = cachedData.$watch("isDisableOptions", () => {
                removeCellAttribute()
                initCellAttribute()
              })
              endFns.unshift(() => endWatchIsDisableOptions())
            }
            initCellAttribute()
            return removeCellAttribute
          },
          validateCell(innerCellId, source) {
            let [row, col] = getCellPosition(innerCellId)
            // 可修改则不进行校验
            if (editable) {
              return new ValidateResult(true)
            } else {
              let value = source.getValue(row, col)
              let optionValueMap = vueCore.propertyOptionMap
              if (optionValueMap[this.code] && !optionValueMap[this.code][value]) {
                return new ValidateResult(false, `请选择列表之内的数据：${commonUtil.getCellName(source, row, col)}`)
              } else {
                return new ValidateResult(true)
              }
            }
          },
          clear(innerCellId, sheet) {
            let [row, col] = getCellPosition(innerCellId)
            sheet.setCellType(row, col, null)
          }
        }
        return obj
      }
      let checkValue = (value) => {
        return !isBlank(value)
      }

      function RepairRadioButtonList() {
        this._type_name = "RepairRadioButtonList"
      }
      RepairRadioButtonList.prototype = new Sheets.CellTypes.RadioButtonList()
      // 修复radio选项不能为数字、时间等可格式化的值的bug
      wrapFn(RepairRadioButtonList.prototype, "processMouseUp", (doOri, args) => {
        let revertWrap = wrapFn(args[0].sheet.parent.commandManager().editCell, "execute", (doOriExecute, executeArgs) => {
          executeArgs[1].autoFormat = false
          return doOriExecute()
        })
        try {
          return doOri()
        } finally {
          revertWrap()
        }
      })

      let getDateTimeAttribute = (type, formatString) => {
        return {
          showEffectOnCellDisabled: true,
          initCellAttribute(innerCellId, sheet) {
            let [row, col] = getCellPosition(innerCellId)
            let cell = sheet.getCell(row, col)
            let datetimeCellType = new DateTimeCellType()
            datetimeCellType.setFormat(formatString)
            datetimeCellType.setType(type)
            cell.cellType(datetimeCellType)
            cell.formatter(formatString)
            return () => {
              let [row, col] = getCellPosition(innerCellId)
              let cell = sheet.getCell(row, col)
              cell.cellType(null)
              cell.formatter(null)
            }
          },
          setValue(col, row, value, sheet) {
            sheet.setText(col, row, value == null ? "" : value.toString())
          },
          getValue(col, row, sheet) {
            return sheet.getText(col, row)
          },
          setExportingStyle(innerCellId, sheet) {
            let [row, col] = getCellPosition(innerCellId)
            let cell = sheet.getCell(row, col)
            cell.formatter(formatString)
            return () => {
              let [row, col] = getCellPosition(innerCellId)
              let cell = sheet.getCell(row, col)
              cell.formatter(null)
            }
          }
        }
      }

      // 将监听方法转换为仅监听当前属性的方法
      let getCurrentPropertyCellEventBinder = (propertyBinder, vueCore, oriFn) => {
        return (...args) => {
          let argsObj = args[1]
          let propertyKey = vueCore.getPropertyKey(argsObj.row, argsObj.col)
          if (propertyKey === propertyBinder.code) {
            oriFn(...args)
          }
        }
      }
      let map = {
        [AttributeModeEnum.TEXT]: (vueCore) => {
          let setCellWordWrapAndFormatter = (innerCellId, sheet) => {
            let [row, col] = getCellPosition(innerCellId)
            let cell = sheet.getCell(row, col)
            let unbinds = []
            let oriWordWrap = cell.wordWrap()
            if (oriWordWrap !== true) {
              cell.wordWrap(true)
              unbinds.unshift(() => {
                let [row, col] = getCellPosition(innerCellId)
                let cell = sheet.getCell(row, col)
                cell.wordWrap(oriWordWrap)
              })
            }
            let oriFormatter = cell.formatter()
            if (oriFormatter !== "@") {
              cell.formatter("@")
              unbinds.unshift(() => {
                let [row, col] = getCellPosition(innerCellId)
                let cell = sheet.getCell(row, col)
                cell.formatter(oriFormatter)
              })
            }
            return () => {
              unbinds.forEach(v => v())
            }
          }
          return {
            initCellAttribute(innerCellId, sheet) {
              return setCellWordWrapAndFormatter(innerCellId, sheet)
            },
            setExportingStyle(innerCellId, sheet) {
              return setCellWordWrapAndFormatter(innerCellId, sheet)
            },
            setValue(col, row, value, sheet) {
              sheet.setText(col, row, value == null ? "" : value.toString())
            },
            getValue(col, row, sheet) {
              return sheet.getText(col, row)
            },
          }
        },
        [AttributeModeEnum.NUMBER]: (vueCore) => {
          let getFixDigit = (propertyKey) => {
            let propertyOptionValues = vueCore.propertyOptionValueListMap[propertyKey] || ["0"]
            return parseInt(propertyOptionValues[0])
          }
          return {
            formatter(value, innerCellId, sheet) {
              let fixNumber = v => {
                return parseFloat(v.toFixed(getFixDigit(this.code)))
              }
              // 合理化数值，最大值1亿
              let reasonable = (fixedNumber) => {
                if (fixedNumber > 100000000) {
                  return sheet.getValue(...getCellPosition(innerCellId))
                } else {
                  return fixedNumber
                }
              }
              if (typeof value == "number") {
                return reasonable(fixNumber(value))
              }
              if (typeof value == "string") {
                try {
                  let v = parseFloat(value)
                  if (isNaN(v)) {
                    return null
                  } else {
                    return reasonable(fixNumber(v))
                  }
                } catch (error) { }
              }
              return null
            },
            // clear(row, col, sheet) {
            //   let cell = sheet.getCell(row, col)
            //   cell.formatter(null)
            // }
          }
        },
        [AttributeModeEnum.SELECT]: (vueCore) => {
          return getOptionsAttribute(false, vueCore)
        },
        [AttributeModeEnum.EDITABLE_SELECT]: (vueCore) => {
          return getOptionsAttribute(true, vueCore)
        },
        [AttributeModeEnum.SIGN]: (vueCore) => {
          let cachedData = getCachedData(vueCore)
          let getAllowRoles = (row, col) => {
            let propertyKey = vueCore.getPropertyKey(row, col)
            let innerCellId = getInnerCellId(row, col)
            let propertyOptionValues = vueCore.propertyOptionValueListMap[propertyKey] || []
            let extraOptionValues = cachedData.extraOptionValueListMap[innerCellId] || []
            return [].concat(propertyOptionValues, extraOptionValues)
          }
          let checkSigned = (row, col, sheet) => {
            let userId = vueCore.getUserId()
            let cellCode = vueCore.getExtraData(row, col)
            let signedUserIds = cellCode ? cellCode.split(",") : []
            let cellValue = sheet.getValue(row, col)
            let signedUserNames = cellValue ? cellValue.split(",") : []
            let curUserIndex = signedUserIds.findIndex(v => v == userId)
            let setSign = (userIds, userNames) => {
              sheet.setValue(row, col, userNames.join(","))
              vueCore.setExtraData(row, col, userIds.join(","))
            }
            let getSignInfo = (userIds, userNames) => {
              return {
                value: userNames.join(","),
                code: userIds.join(","),
              }
            }
            let getCurSignInfo = () => {
              return {
                value: cellValue,
                code: cellCode,
              }
            }
            if (curUserIndex == -1) {
              let getDoSignValue = () => {
                let copySignedUserIds = [...signedUserIds]
                let copySignedUserNames = [...signedUserNames]
                copySignedUserIds.push(userId)
                copySignedUserNames.push(vueCore.getUserName())
                return [copySignedUserIds, copySignedUserNames]
              }

              let doSign = () => {
                setSign(...getDoSignValue())
              }
              let getDoSignInfo = () => {
                return getSignInfo(...getDoSignValue())
              }
              return {
                signed: false,
                doSign,
                getDoSignInfo,
                getCurSignInfo
              }
            } else {
              let getCancelSignValue = () => {
                let copySignedUserIds = [...signedUserIds]
                let copySignedUserNames = [...signedUserNames]
                copySignedUserIds.splice(curUserIndex, 1)
                copySignedUserNames.splice(curUserIndex, 1)
                return [copySignedUserIds, copySignedUserNames]
              }
              let cancelSign = () => {
                setSign(...getCancelSignValue())
              }
              let getCancelSignInfo = () => {
                return getSignInfo(...getCancelSignValue())
              }

              return {
                signed: true,
                cancelSign,
                getCancelSignInfo,
                getCurSignInfo
              }
            }
          }

          return {
            readOnlyOnEdit: true,
            initCellAttribute(innerCellId, sheet) {
              let [row, col] = getCellPosition(innerCellId)
              let config = vueCore.getConfig()
              if (config.configMode) {
                // 配置模式静默清理即可
                if (checkValue(sheet.getValue(row, col))) {
                  sheet.setValue(row, col, null)
                }
              }
              return () => { }
            },
            afterValueInit(row, col, sheet) {
              let config = vueCore.getConfig()
              if (!config.configMode && !config.readOnly && !vueCore.isCellDisabled(row, col)) {
                let hasExtraData = checkValue(vueCore.getExtraData(row, col))
                let hasCellValue = checkValue(sheet.getValue(row, col))
                let cleared = false
                if (hasExtraData != hasCellValue) {
                  if (hasCellValue) {
                    sheet.setValue(row, col, null)
                  }
                  if (hasExtraData) {
                    vueCore.setExtraData(row, col, null)
                  }
                  cleared = true
                } else if (hasExtraData && hasCellValue) {
                  let extraData = vueCore.getExtraData(row, col)
                  let cellValue = sheet.getValue(row, col)
                  let roleIds = extraData.split(",")
                  let roleNames = cellValue.split(",")
                  if (roleIds.length != roleNames.length || roleIds.find(v => !checkValue(v)) || roleNames.find(v => !checkValue(v))) {
                    sheet.setValue(row, col, null)
                    vueCore.setExtraData(row, col, null)
                    cleared = true
                  }
                }
                if (cleared) {
                  config.alertMessage(`已清理异常签字信息：${commonUtil.getCellName(sheet, row, col)}`, "error")
                }
              }
            },
            install(spread) {
              let {
                checkCellPreposedControl,
                hideInfo
              } = getCheckCellPreposedControlUtil(() => spread.getActiveSheet(), vueCore)

              // let showMessage = getShowMessageFn(() => spread.getActiveSheet())
              // let messageShowObj = null
              // let closeMessage = () => {
              //   if (messageShowObj) {
              //     messageShowObj.close()
              //     messageShowObj = null
              //   }
              //   hideInfo()
              // }
              let setMessage = (/* innerCellId,  */message) => {
                commonUtil.uniqueAlertMessage(message, (onClose) => {
                  let ctrlObj = vueCore.getConfig().alertMessage(message, "warning", onClose)
                  return () => ctrlObj.close()
                })
              }

              let checkingAllowSign = false

              let cellClickListener = getCurrentPropertyCellEventBinder(this, vueCore, (e, args) => {
                if (checkingAllowSign) {
                  return
                }
                let { row, col, sheet } = args
                // 如果单元格可编辑，点击视图区域，则可进行签字操作
                if (args.sheetArea === Sheets.SheetArea.viewport && vueCore.couldCellEdited(getInnerCellId(row, col))) {
                  let allowRoles = getAllowRoles(row, col)
                  // 如果没有权限则返回
                  if (!allowRoles.some(role => vueCore.hasRole(role))) {
                    setMessage(/* getInnerCellId(row, col),  */"您没有当前单元格的签字权限")
                    hideInfo()
                    return
                  }
                  // 如果回填顺序没有检查通过则返回
                  if (!checkCellPreposedControl(row, col)) {
                    return
                  }
                  // 检查是否签过字
                  let checkResult = checkSigned(row, col, sheet)
                  let beforeChangeType = getCellChangeType(getCellId(row, col))
                  let afterChangeType = beforeChangeType === BillEntityChangeTypeEnum.NO_CHANGE ? BillEntityChangeTypeEnum.UPDATE : beforeChangeType
                  let changingCells = null
                  if (checkResult.signed) {
                    let curSignInfo = checkResult.getCurSignInfo()
                    let cancelSignInfo = checkResult.getCancelSignInfo()
                    changingCells = [{
                      row,
                      col,
                      beforeValue: curSignInfo.value,
                      beforeCode: curSignInfo.code,
                      beforeChangeType,
                      afterValue: cancelSignInfo.value,
                      afterCode: cancelSignInfo.code,
                      afterChangeType,
                      doChange: (allowChange) => {
                        if (allowChange) {
                          checkResult.cancelSign()
                          vueCore.getConfig().alertMessage("撤销签字成功", "success")
                        }
                      },
                      defaultConfirmMessageBeforeChange: "是否撤销签字？",
                      defaultNotAllowChangeMessage: "根据业务规则，您被禁止撤销签字",
                      defaultCheckErrorMessage: "撤销签字校验请求调用失败"
                    }]
                  } else {
                    let curSignInfo = checkResult.getCurSignInfo()
                    let doSignInfo = checkResult.getDoSignInfo()
                    changingCells = [{
                      row,
                      col,
                      beforeValue: curSignInfo.value,
                      beforeCode: curSignInfo.code,
                      beforeChangeType,
                      afterValue: doSignInfo.value,
                      afterCode: doSignInfo.code,
                      afterChangeType,
                      doChange: (allowChange) => {
                        if (allowChange) {
                          checkResult.doSign()
                          vueCore.getConfig().alertMessage("签字成功", "success")
                        }
                      },
                      // defaultConfirmMessageBeforeChange,
                      defaultNotAllowChangeMessage: "根据业务规则，您被禁止签字",
                      defaultCheckErrorMessage: "签字校验请求调用失败"
                    }]
                  }
                  checkingAllowSign = true
                  startTriggerUrl(changingCells).finally(() => {
                    checkingAllowSign = false
                  })
                }
                // closeMessage()
              })
              spread.bind(Sheets.Events.CellClick, cellClickListener)
              return () => {
                spread.unbind(Sheets.Events.CellClick, cellClickListener)
              }
            },
            getDefaultValueAndCode(row, col, sheet) {
              let allowRoles = getAllowRoles(row, col)
              // 如果没有权限则不进行默认签字
              if (!allowRoles.some(role => vueCore.hasRole(role))) {
                return null
              }
              let checkSignedResult = checkSigned(row, col, sheet)
              // 如果没有签字则返回签字后的值
              if (!checkSignedResult.signed) {
                return checkSignedResult.getDoSignInfo()
              } else {
                return null
              }
            }
          }
        },
        [AttributeModeEnum.RADIO]: (vueCore) => {
          let cachedData = getCachedData(vueCore)
          let getOptionValues = (innerCellId, attributeCode) => {
            let propertyOptionValues = vueCore.propertyOptionValueListMap[attributeCode] || []
            let extraOptionValues = cachedData.extraOptionValueListMap[innerCellId] || []
            let optionValues = [].concat(propertyOptionValues, extraOptionValues)
            return optionValues
          }
          return {
            initCellAttribute(innerCellId, sheet) {
              let endFns = []
              let removeCellAttribute = () => {
                let [row, col] = getCellPosition(innerCellId)
                sheet.setCellType(row, col, null)
                let fn = null
                while (fn = endFns.shift()) {
                  fn()
                }
              }
              let initCellAttribute = () => {
                let [row, col] = getCellPosition(innerCellId)
                let optionValues = getOptionValues(innerCellId, this.code)

                let radioButtonListCellType = new RepairRadioButtonList()
                radioButtonListCellType.items(optionValues)
                sheet.setCellType(row, col, radioButtonListCellType)
                let endWatchCellExtraOptions = cachedData.$watch(() => {
                  return JSON.stringify(cachedData.extraOptionValueListMap[innerCellId] || [])
                }, () => {
                  removeCellAttribute()
                  initCellAttribute()
                })
                endFns.unshift(() => endWatchCellExtraOptions())
              }
              initCellAttribute()
              return removeCellAttribute
            },
            formatter(value, innerCellId, sheet) {
              // let [row, col] = getCellPosition(innerCellId)
              let propertyOptionValues = vueCore.propertyOptionValueListMap[this.code] || []
              let extraOptionValues = cachedData.extraOptionValueListMap[innerCellId] || []
              let optionValues = [].concat(propertyOptionValues, extraOptionValues)
              if (checkValue(value) && optionValues.some(v => v == value)) {
                return value
              } else {
                return null
              }
            },
            install(spread) {// 特殊处理radio的url接口调用
              let { checkInnerSetting, doInner } = (() => {
                let innerSetting = false
                return {
                  checkInnerSetting: () => !innerSetting,
                  doInner: (fn) => {
                    try {
                      innerSetting = true
                      fn()
                    } finally {
                      innerSetting = false
                    }
                  }
                }
              })()
              let checkingAllowChange = false
              let cellChangedListenerForRadioButton = getCurrentPropertyCellEventBinder(this, vueCore, (e, args) => {
                if (
                  args.sheetArea === Sheets.SheetArea.viewport &&
                  args.propertyName == "value" &&
                  args.sheet.getCellType(args.row, args.col) instanceof RepairRadioButtonList &&
                  checkInnerSetting() &&
                  !vueCore.isSettingDefaultValue()// 排除设置默认值时的情况
                ) {
                  // 如果正在请求中，直接撤销变更
                  if (checkingAllowChange) {
                    setTimeout(() => {
                      // 撤销变更
                      doInner(() => {
                        spread.undoManager().undo()
                      })
                    }, 0)
                  } else {
                    setTimeout(() => {
                      let { newValue, oldValue } = args
                      if (!isValueEqual(newValue, oldValue)) {
                        // 暂停渲染（防止后续反复变换被展示出来）
                        spread.suspendPaint()
                        let { row, col } = args
                        let cellId = getCellId(row, col)
                        let afterChangeType = getCellChangeType(cellId)
                        // 撤销变更
                        doInner(() => {
                          spread.undoManager().undo()
                        })
                        // 获取变更前的changeType
                        let beforeChangeType = getCellChangeType(cellId)
                        let changingCells = [{
                          row,
                          col,
                          beforeValue: oldValue,
                          beforeChangeType,
                          afterValue: newValue,
                          afterChangeType,
                          doChange: (allowChange) => {
                            // 允许变更则执行重做操作
                            if (allowChange) {
                              doInner(() => {
                                spread.undoManager().redo()
                              })
                            }
                          },
                          // defaultConfirmMessageBeforeChange,
                          defaultNotAllowChangeMessage: "根据业务规则，当前单元格编辑被撤销",
                          defaultCheckErrorMessage: "变更检测校验请求调用失败"
                        }]
                        checkingAllowChange = true
                        setTimeout(() => {
                          startTriggerUrl(changingCells).finally(() => {
                            checkingAllowChange = false
                            // 恢复渲染
                            spread.resumePaint()
                          })
                        }, 0)
                      }
                    }, 0)
                  }
                }
              })
              spread.bind(Sheets.Events.CellChanged, cellChangedListenerForRadioButton)
              return () => {
                spread.unbind(Sheets.Events.CellChanged, cellChangedListenerForRadioButton)
              }
            },
            clear(innerCellId, sheet) {
              let [row, col] = getCellPosition(innerCellId)
              sheet.setCellType(row, col, null)
            },
            showEffectOnCellDisabled: true,// 只读时仍然显示额外影响
            setExportingStyle(innerCellId, sheet) {// 导出时，用●○符号拼接出选项的效果
              let [row, col] = getCellPosition(innerCellId)
              let cell = sheet.getCell(row, col)
              let oriValue = cell.value()
              let optionValues = getOptionValues(innerCellId, this.code)
              let newValue = optionValues.map(v => {
                if (v == oriValue) {
                  return "●" + v
                } else {
                  return "○" + v
                }
              }).join(" ")
              cell.value(newValue)
              cell.wordWrap(true)
              return () => {
                let [row, col] = getCellPosition(innerCellId)
                let cell = sheet.getCell(row, col)
                cell.value(oriValue)
                cell.wordWrap(undefined)
              }
            }
          }
        },
        [AttributeModeEnum.DATE]: (vueCore) => {
          return getDateTimeAttribute(DateTimeCellType.type.date, "yyyy-MM-dd")
        },
        [AttributeModeEnum.DATE_TIME]: (vueCore) => {
          return getDateTimeAttribute(DateTimeCellType.type.datetime, "yyyy-MM-dd HH:mm")
        },
        [AttributeModeEnum.TIME]: (vueCore) => {
          return getDateTimeAttribute(DateTimeCellType.type.time, "HH:mm")
        },
        [AttributeModeEnum.TIME_WITH_SECOND]: (vueCore) => {
          return getDateTimeAttribute(DateTimeCellType.type.time, "HH:mm:ss")
        }
      }

      return (vueCore, modeCode) => {
        if (map[modeCode]) {
          return map[modeCode](vueCore)
        } else {
          return null
        }
      }
    }

    let getVueCoreObj = ((getCellDataManagerGroup_, getPropClipboard_) => {
      return ({
        getSpread,
        getUndoManager,
        setUndoManager
      }) => {
        let currentCellDataManagerGroup = getCellDataManagerGroup_()

        let innerCellIdManager = ((currentCellDataManagerGroup_) => {
          let dataManager = currentCellDataManagerGroup_.getCellDataManager({ observable: false })
          let getCellPositionMap = () => {
            let map = {}
            dataManager.forEachCellData((row, col, id) => {
              map[id] = [row, col]
            })
            return map
          }
          let cellPositionMap = getCellPositionMap()

          let n = 1
          return {
            getInnerCellId(row, col) {
              let innerCellId = dataManager.getCellData(row, col)
              if (innerCellId != null) {
                return innerCellId
              } else {
                innerCellId = n++
                dataManager.setCellData(row, col, innerCellId)
                cellPositionMap[innerCellId] = [row, col]
                return innerCellId
              }
            },
            getCellPosition(innerCellId) {
              return cellPositionMap[innerCellId] || [-1, -1]
            },
            getAllData() {
              return dataManager.getAllCellData()
            },
            setAllData(allIdData) {
              dataManager.setAllCellData(allIdData)
              cellPositionMap = getCellPositionMap()
            },
            clearAllData() {
              dataManager.clearCellData()
              cellPositionMap = getCellPositionMap()
            }
          }
        })(currentCellDataManagerGroup)

        // 将cellKey设置为内置单元格id，防止增、删单元格导致key值变化
        let getCellKey = (row, col) => {
          return innerCellIdManager.getInnerCellId(row, col) + ""
        }

        let backColorLayerManager = (() => {
          let backColorLayerData = []

          let initCell = (row, col) => {
            if (!backColorLayerData[row]) {
              backColorLayerData[row] = []
            }
            if (!backColorLayerData[row][col]) {
              backColorLayerData[row][col] = []
            }
          }

          let oriBackColorData = []

          let changedCells = {}
          let setChangeCell = (row, col) => {
            let cellKey = getCellKey(row, col)
            if (!changedCells[cellKey]) {
              changedCells[cellKey] = [row, col]
            }
          }

          let _isInTempToOriBackColor = false
          return {
            pushRgbaBackColor(row, col, layerName, rgbaColor) {
              initCell(row, col)
              backColorLayerData[row][col].push({
                colorObj: typeof rgbaColor == "string" ? commonUtil.parseRgbString(rgbaColor) : rgbaColor,
                layer: layerName
              })
              setChangeCell(row, col)
            },
            unshiftRgbaBackColor(row, col, layerName, rgbaColor) {
              initCell(row, col)

              backColorLayerData[row][col].unshift({
                colorObj: typeof rgbaColor == "string" ? commonUtil.parseRgbString(rgbaColor) : rgbaColor,
                layer: layerName
              })
              setChangeCell(row, col)
            },
            removeAllRgbaBackColor(layerName) {
              backColorLayerData.forEach((rowData, row) => {
                rowData.forEach((cellColors, col) => {
                  this.removeRgbaBackColor(row, col, layerName)
                })
              })
            },
            removeRgbaBackColor(row, col, layerName) {
              if (backColorLayerData[row] && backColorLayerData[row][col]) {
                let cellColors = backColorLayerData[row][col]
                commonUtil.forEach(cellColors, (colorObj, i, splice) => {
                  if (colorObj.layer == layerName) {
                    splice(i, 1)
                    setChangeCell(row, col)
                  }
                })
                if (cellColors.length == 0) {
                  delete backColorLayerData[row][col]
                  if (backColorLayerData[row].length == 0) {
                    delete backColorLayerData[row]
                  }
                }
              }
            },
            isInTempToOriBackColor() {
              return _isInTempToOriBackColor
            },
            setBackColorToSheet(sheet) {
              if (_isInTempToOriBackColor) {
                throw "临时恢复原始背景时禁止更新颜色"
              }
              sheet.suspendPaint()
              for (const key in changedCells) {
                const [row, col] = changedCells[key]
                let cell = sheet.getCell(row, col)
                if (!oriBackColorData[row]) {
                  oriBackColorData[row] = []
                }
                if (backColorLayerData[row] && backColorLayerData[row][col]) {
                  if (!(col in oriBackColorData[row])) {
                    oriBackColorData[row][col] = cell.backColor()
                  }
                  let mergedColor = commonUtil.mergeColors(backColorLayerData[row][col].map(v => v.colorObj))
                  cell.backColor(commonUtil.toRgbaString(mergedColor))
                } else {
                  if (col in oriBackColorData[row]) {
                    cell.backColor(oriBackColorData[row][col])
                    delete oriBackColorData[row][col]
                  } else {
                    cell.backColor(null)
                  }
                }
              }
              sheet.resumePaint()
              changedCells = {}
            },
            tempToOriBackColor(sheet) {
              if (_isInTempToOriBackColor) {
                throw "请先结束上次调用"
              }
              sheet.suspendPaint()
              oriBackColorData.forEach((rowData, row) => {
                rowData.forEach((color, col) => {
                  sheet.getCell(row, col).backColor(rowData[col])
                })
              })
              _isInTempToOriBackColor = true
              sheet.resumePaint()
              return () => {
                sheet.suspendPaint()
                backColorLayerData.forEach((rowData, row) => {
                  if (!oriBackColorData[row]) {
                    oriBackColorData[row] = []
                  }
                  rowData.forEach((cellColors, col) => {
                    let cell = sheet.getCell(row, col)
                    if (!(col in oriBackColorData[row])) {
                      oriBackColorData[row][col] = cell.backColor()
                    }
                    let mergedColor = commonUtil.mergeColors(cellColors.map(v => v.colorObj))
                    cell.backColor(commonUtil.toRgbaString(mergedColor))
                  })
                })
                changedCells = {}
                _isInTempToOriBackColor = false
                sheet.resumePaint()
              }
            },
            updateOriBackColors(sheet) {
              oriBackColorData.forEach((rowData, row) => {
                rowData.forEach((color, col) => {
                  rowData[col] = sheet.getCell(row, col)
                })
              })
            },
            clear() {
              backColorLayerData = []
              oriBackColorData = []
              changedCells = {}
            },
            // getOriBackColor(row, col) {
            //   if (oriBackColorData[row] && col in oriBackColorData[row]) {
            //     return oriBackColorData[row][col]
            //   }
            // },
            // getActualBackColor(row, col) {
            //   if (backColorLayerData[row] && col in backColorLayerData[row]) {
            //     if (backColorLayerData[row][col]) {
            //       let mergedColor = commonUtil.mergeColors(backColorLayerData[row][col].map(v => v.colorObj))
            //       return commonUtil.toRgbaString(mergedColor)
            //     } else {
            //       return null
            //     }
            //   }
            // }

          }
        })()

        let { getNewAreaId, setExistAreaId } = (() => {
          let n = 1
          return {
            getNewAreaId: () => {
              return n++
            },
            setExistAreaId: (id) => {
              let iId = parseInt(id)
              if (iId + 1 > n) {
                n = iId + 1
              }
            }
          }
        })()

        let revertAttributeEffectManager = (() => {
          let fnData = {}

          let revertAttributeEffect = (innerCellId, resetAttributeEffectFunction) => {
            let fn = fnData[innerCellId]
            if (fn) {
              fn()
              delete fnData[innerCellId]
              if (fnsForUndo) {
                if (!resetAttributeEffectFunction) {
                  console.error("此时正处于事务中，调用revertCell需要传入resetAttributeEffectFunction方法，请检查程序")
                }
                fnsForUndo.unshift(() => {
                  fnData[innerCellId] = resetAttributeEffectFunction()
                })
              }
            }
          }

          let setAttributeEffect = (innerCellId, setAttributeEffectFunction) => {
            fnData[innerCellId] = setAttributeEffectFunction()
            if (fnsForUndo) {
              fnsForUndo.unshift(() => {
                fnData[innerCellId]()
                delete fnData[innerCellId]
              })
            }
          }

          let fnsForUndo = null
          let optionName = "_revert_attribute_effect_"

          let startTransaction = (context, options) => {
            fnsForUndo = []
          }
          let endTransaction = (context, options) => {
            options[optionName] = fnsForUndo
            fnsForUndo = null
          }
          let undoTransaction = (context, options) => {
            let transaction = options[optionName]
            if (transaction) {
              let fn = null
              while (fn = transaction.shift()) {
                fn()
              }
            }
          }

          return {
            revertAttributeEffect,
            setAttributeEffect,
            startTransaction,
            endTransaction,
            undoTransaction,
            revertAllCell() {
              for (const innerCellId in fnData) {
                revertAttributeEffect(innerCellId)
              }
            },

          }
        })()

        let cellPropertyDataManager = (() => {
          let manager = currentCellDataManagerGroup.getCellDataManager({
            modeForAddCell: currentCellDataManagerGroup.modesForAddCells.COPY,
          })
          manager.getPropertyKeyFromCellData = (cellData) => {
            return cellData.propertyKey
          }
          manager.setPropertyKeyToCellData = (cellData, propertyKey) => {
            return cellData.propertyKey = propertyKey
          }
          manager.getExtraDataFromCellData = (cellData) => {
            return cellData.extraData
          }
          manager.setExtraDataToCellData = (cellData, extraData) => {
            return cellData.extraData = extraData
          }
          manager.getPropertyKey = (row, col) => {
            let cellData = manager.getCellData(row, col)
            return cellData && manager.getPropertyKeyFromCellData(cellData)
          }
          manager.getExtraData = (row, col) => {
            let cellData = manager.getCellData(row, col)
            return cellData && manager.getExtraDataFromCellData(cellData)
          }
          manager.setPropertyKey = (row, col, propertyKey) => {
            let cellData = copyDataSimple(manager.getCellData(row, col) || {})
            manager.setPropertyKeyToCellData(cellData, propertyKey)
            manager.setExtraDataToCellData(cellData, null)
            manager.setCellData(row, col, cellData)
          }
          manager.setExtraData = (row, col, extraData) => {
            let cellData = manager.getCellData(row, col)
            if (cellData) {
              cellData = copyDataSimple(cellData)
              manager.setExtraDataToCellData(cellData, extraData)
              manager.setCellData(row, col, cellData)
            } else {
              console.error("程序运行错误，必须先设置属性再设置额外信息")
            }
          }

          let propertySetListeners = []
          wrapFn(manager, "setCellData", (doOri, args) => {
            doOri()
            let propertyKey = manager.getPropertyKeyFromCellData(args[2])
            propertySetListeners.forEach(v => v(args[0], args[1], propertyKey))
          })
          manager.addPropertySetListener = (listener) => {
            propertySetListeners.push(listener)
          }
          manager.removePropertySetListener = (listener) => {
            let index = propertySetListeners.findIndex(v => v == listener)
            if (index >= 0) {
              propertySetListeners.splice(index, 1)
            }
          }
          return manager
        })()

        let getNewPropChangeManager = () => {
          let getId = (row, col) => {
            return innerCellIdManager.getInnerCellId(row, col)
          }
          // let getKey = (row, col) => {
          //   return [row, col].join(",")
          // }
          // let rangesToCellMap = (ranges) => {
          //   let map = {}
          //   ranges.forEach(({
          //     col,
          //     colCount,
          //     row,
          //     rowCount
          //   }) => {
          //     if (row == -1) {
          //       row = 0
          //     }
          //     if (col == -1) {
          //       col = 0
          //     }
          //     commonUtil.everyCell(row, col, rowCount, colCount, (row, col) => {
          //       map[getKey(row, col)] = true
          //     })
          //   })
          //   return map
          // }

          let arrayCellToMap = (cellPropertyData) => {
            let map = {}
            cellPropertyData && cellPropertyData.forEach((rowData, row) => {
              rowData && rowData.forEach((cellData, col) => {
                if (cellData) {
                  map[getId(row, col)] = cellPropertyDataManager.getPropertyKeyFromCellData(cellData)
                }
              })
            })
            return map
          }

          return {
            tempList: [],
            isCommandExecuting: false,
            setOldData(data) {
              if (this.isCommandExecuting) {
                throw "命令未结束"
              }
              this.tempList[0] = arrayCellToMap(data)
              this.isCommandExecuting = true
            },
            setNewData(data) {
              if (!this.isCommandExecuting) {
                throw "命令未开始"
              }
              this.tempList[1] = arrayCellToMap(data)
            },
            getChange(id) {
              let oldPropMapData = this.tempList[0]
              let newPropMapData = this.tempList[1]
              if (oldPropMapData && newPropMapData) {
                let oldProperty = oldPropMapData[id]
                let newProperty = newPropMapData[id]
                if (oldProperty != newProperty) {
                  return {
                    oldProperty,
                    newProperty,
                  }
                }
              }
            },
            everyChange(callback) {
              let tempMap = {}
              let oldPropMapData = this.tempList[0]
              let newPropMapData = this.tempList[1]
              for (const id in oldPropMapData) {
                let change = this.getChange(id)
                if (change) {
                  callback(id, change)
                }
                tempMap[id] = true
              }
              for (const id in newPropMapData) {
                if (!tempMap[id]) {
                  let change = this.getChange(id)
                  if (change) {
                    callback(id, change)
                  }
                }
              }
            },
            clear() {
              this.tempList = []
              this.isCommandExecuting = false
            }
          }
        }

        let getPropChangeManager = (() => {
          let map = new Map()
          return (sheet) => {
            if (!map.has(sheet)) {
              map.set(sheet, getNewPropChangeManager())
            }
            return map.get(sheet)
          }
        })()

        let customGetPropClipboard = (propClipboardInfo) => {
          return getPropClipboard_(
            (...args) => {
              vueCore.setProperty(...args)
            },
            (...args) => {
              vueCore.deleteProperty(...args)
            },
            () => {
              return cellPropertyDataManager.getAllCellData()
            },
            (...args) => {
              return cellPropertyDataManager.getPropertyKeyFromCellData(...args)
            },
            propClipboardInfo
          )
        }

        let propClipboard = customGetPropClipboard()

        let cellIdManager = ((currentCellDataManagerGroup_) => {
          let cellIdDataManager = currentCellDataManagerGroup_.getCellDataManager()
          let computedDataManager = new Vue({
            computed: {
              cellIdPositionMap() {
                let map = {}
                let arr = cellIdDataManager.getAllCellData()
                if (arr) {
                  arr.forEach((rowData, row) => {
                    if (rowData) {
                      rowData.forEach((cellData, col) => {
                        if (cellData) {
                          map[cellData] = [row, col]
                        }
                      })
                    }
                  })
                }
                return map
              }
            }
          })
          let getCellPositionById = (cellId) => {
            if (computedDataManager.cellIdPositionMap[cellId]) {
              return computedDataManager.cellIdPositionMap[cellId]
            } else {
              return [-1, -1]
            }
          }
          return {
            setCellId(row, col, cellId) {
              cellIdDataManager.setCellData(row, col, cellId)
            },
            getCellId(row, col) {
              return cellIdDataManager.getCellData(row, col)
            },
            getCellPositionById,
            getAllData() {
              return cellIdDataManager.getAllCellData()
            },
            setAllData(cellIdData) {
              cellIdDataManager.setAllCellData(cellIdData)
            },
            clearAllData() {
              cellIdDataManager.clearCellData()
            }
          }
        })(currentCellDataManagerGroup)

        // 初始化单元格id，获取单元格是否变化，设置单元格已保存(清空变化历史)
        let cellChangedManager = ((getSpread_, currentCellDataManagerGroup_) => {
          let deleteCellIdSet = new Set()
          let cellChangedDataManager = currentCellDataManagerGroup_.getCellDataManager({
            onDeleteCell: (cellData) => {
              if (cellData != null && cellData.id != null) {
                deleteCellIdSet.add(cellData.id)
              }
            }
          })
          let getCellPositionById = (cellId) => {
            return cellIdManager.getCellPositionById(cellId)
          }
          let push = (row, col, value) => {
            let cellData = cellChangedDataManager.getCellData(row, col)
            if (cellData != null) {
              if (!cellData.history) {
                cellData.history = []
              }
              cellData.history.push(value)
            }
          }
          let pop = (row, col, value) => {
            let cellData = cellChangedDataManager.getCellData(row, col)
            if (cellData != null) {
              if (!cellData.history || cellData.history.length === 0) {
                let msg = "运行异常，请检查程序"
                console.error(msg)
                throw msg
              }
              let lastValue = cellData.history[cellData.history.length - 1]
              if (lastValue != value) {
                let msg = "运行异常，请检查程序"
                console.error(msg)
                throw msg
              }
              cellData.history.pop()
            }
          }
          const SheetArea = Sheets.SheetArea
          return {
            initSheetListener(sheet) {
              let cellChangedListener = (e, info) => {
                if (info.sheetArea === SheetArea.viewport && info.propertyName === "value") {
                  let { isUndo, row, col, newValue, oldValue } = info
                  if (!isUndo) {
                    push(row, col, newValue)
                  } else {
                    pop(row, col, oldValue)
                  }
                }
              }
              // let columnChangedListener = (e, info) => { }
              // let rowChangedListener = (e, info) => { }
              sheet.bind(Sheets.Events.CellChanged, cellChangedListener)
              // sheet.bind(Sheets.Events.ColumnChanged, columnChangedListener)
              // sheet.bind(Sheets.Events.RowChanged, rowChangedListener)
              return () => {
                sheet.unbind(Sheets.Events.CellChanged, cellChangedListener)
                // sheet.unbind(Sheets.Events.ColumnChanged, columnChangedListener)
                // sheet.unbind(Sheets.Events.RowChanged, rowChangedListener)
              }
            },
            setCellInitialized(row, col) {
              let cellData = cellChangedDataManager.getCellData(row, col)
              if (!cellData) {
                let innerCellId = innerCellIdManager.getInnerCellId(row, col)
                cellChangedDataManager.setCellData(row, col, { id: innerCellId })
              }
            },
            getCellChanged(cellId) {
              // 问题：回填条件无法增量更新，可能出现互相覆盖的情况。
              // 方案（前提：回填单元格不增减）：回填条件不用传值，在回填时不用理会即可；
              // 另，关联单元格也不用传值，但需要传递关联单元格id，这样后台即可通过单元格id找到原本单元格信息(值、属性等)
              let [row, col] = getCellPositionById(cellId)
              let cellData = cellChangedDataManager.getCellData(row, col)
              if (cellData) {
                if (cellData.history && cellData.history.length > 0) {
                  return true
                } else {
                  return false
                }
              } else {
                return null
              }
            },
            getCellDeleted(cellId) {
              return deleteCellIdSet.has(cellId)
            },
            getCellChangeType(cellId) {
              let cellChanged = this.getCellChanged(cellId)
              let cellDeleted = this.getCellDeleted(cellId)
              if (cellChanged != null) {
                return cellChanged ? BillEntityChangeTypeEnum.UPDATE : BillEntityChangeTypeEnum.NO_CHANGE
              } else {
                return cellDeleted ? BillEntityChangeTypeEnum.DELETE : BillEntityChangeTypeEnum.INSERT
              }
            },
            setCellsSaved(cellIds) {
              if (cellIds) {
                let someCellReset = false
                cellIds.forEach((cellId) => {
                  let [row, col] = getCellPositionById(cellId)
                  let cellData = cellChangedDataManager.getCellData(row, col)
                  if (cellData && cellData.history && cellData.history.length > 0) {
                    cellData.history = []
                    if (!someCellReset) {
                      someCellReset = true
                    }
                  }
                })
                if (someCellReset) {
                  // 如果清空了历史，则需要重置undoManager，禁止用户撤销
                  getSpread_().undoManager().clear()
                }
              }
            },
            // 设置单元格具有原初的外部变动，即一上来就具有需要保存的特性
            setCellHasOuterChanged(cellId, value) {
              let [row, col] = getCellPositionById(cellId)
              let cellData = cellChangedDataManager.getCellData(row, col)
              if (cellData && cellData.history && cellData.history.length == 0) {
                cellData.history.push(value)
              } else {
                let msg = "此方法仅在初始化时调用赋予初始变动"
                console.error(msg)
                throw msg
              }
            },
            getAllData() {// 数据包含两方面，1是单元格变更历史记录信息。2是单元格删除信息(暂不使用)
              return [cellChangedDataManager.getAllCellData(), Array.from(deleteCellIdSet)]
            },
            setAllData([cellChangedData, deleteCellIdArray]) {
              cellChangedDataManager.setAllCellData(cellChangedData)
              deleteCellIdSet = new Set(deleteCellIdArray)
            },
            clearAllData() {
              cellChangedDataManager.clearCellData()
              deleteCellIdSet = new Set()
            }
          }
        })(getSpread, currentCellDataManagerGroup)

        let getShowMessageFn = (getSheet) => {
          return (innerCellId, message, title) => {
            var dv = new Sheets.DataValidation.DefaultDataValidator()
            dv.inputMessage(message)
            dv.inputTitle(title)
            let [row, col] = innerCellIdManager.getCellPosition(innerCellId)
            getSheet().setDataValidator(row, col, 1, 1, dv)
            return {
              close: () => {
                let [row, col] = innerCellIdManager.getCellPosition(innerCellId)
                getSheet().setDataValidator(row, col, 1, 1, null)
              },
              innerCellId,
              message,
              title
            }
          }
        }
        // 前置区域检查工具，依赖vueCore
        let getCheckCellPreposedControlUtil = (() => {
          let messageShowObj = null

          let closeMessage = () => {
            if (messageShowObj) {
              messageShowObj.close()
              messageShowObj = null
            }
          }

          return (getSheet, vueObj) => {
            let showMessage = getShowMessageFn(getSheet)
            let setMessage = (innerCellId, message, title) => {
              closeMessage()
              if (!title) {
                title = "提示"
              }
              messageShowObj = showMessage(innerCellId, message, title)
            }

            let checkValue = (row, col) => {
              let value = getSheet().getValue(row, col)
              return !isBlank(value)
            }
            let getHighlightCellCheckUtil = () => {
              let map = {}
              let arr = []
              let checkedAll = true
              let addHighlightCell = (row, col, reverse) => {
                let checked
                // if (!vueObj.couldCellEdited(innerCellIdManager.getInnerCellId(row, col))) {
                //   checked = true
                // } else {
                checked = checkValue(row, col)
                if (reverse) {
                  checked = !checked
                }
                // }

                if (!checked && checkedAll) {
                  checkedAll = false
                }
                let cellKey = row + "," + col
                if (!map[cellKey]) {
                  map[cellKey] = {
                    checked,
                    row,
                    col,
                  }
                  arr.push(cellKey)
                } else if (map[cellKey].checked && !checked) {
                  map[cellKey].checked = false
                  arr.splice(arr.findIndex(v => v == cellKey), 1)
                  arr.push(cellKey)
                }
              }
              let getHighlightCellInfoList = () => {
                // 如果每个单元格都检查通过，则不高亮单元格
                if (arr.every(cellKey => map[cellKey].checked)) {
                  return []
                } else {
                  let checkedColor = "rgba(0,128,0,0.4)"
                  let uncheckedColor = "rgba(255,128,0,0.6)"
                  return arr.map(cellKey => {
                    let { row, col, checked } = map[cellKey]
                    return {
                      id: innerCellIdManager.getInnerCellId(row, col),
                      color: checked ? checkedColor : uncheckedColor
                    }
                  })
                }
              }
              return {
                addHighlightCell,
                getHighlightCellInfoList,
                checkedAll() {
                  return checkedAll
                }
              }
            }

            // 1、要把当前单元格的前置单元格遍、前置区域历一遍，判断当前单元格是否可编辑
            // 2、如果可编辑，则需要把当前单元格从readOnlyCellIdMapByPreposedControl中去除，清空高亮信息
            // 3、如果不可编辑，则需要把前置区域、前置单元格中未通过校验的单元格进行高亮

            let checkCellPreposedControl = (row, col, silent) => {
              let { addHighlightCell, getHighlightCellInfoList, checkedAll } = getHighlightCellCheckUtil()

              let cellPreposedCellControls = vueObj.getCellPreposedCellControls(row, col)
              cellPreposedCellControls && cellPreposedCellControls.forEach(({ targetRow, targetCol }) => {
                addHighlightCell(targetRow, targetCol)
              })

              let cellPreposedAreaControls = vueObj.getCellPreposedAreaControls(row, col)
              cellPreposedAreaControls && cellPreposedAreaControls.forEach(({ targetAreaId }) => {
                let { x, y, width, height } = vueObj.areaMap[targetAreaId]
                commonUtil.everyCell(y, x, height, width, (areaCellRow, areaCellCol) => {
                  if (!commonUtil.isHideBySpan(areaCellRow, areaCellCol, getSheet())) {
                    let propertyKey = vueObj.getPropertyKey(areaCellRow, areaCellCol)
                    if (propertyKey && vueObj.isAttributeRegistered(propertyKey) && vueObj.isValueRequiredAttribute(propertyKey)) {
                      addHighlightCell(areaCellRow, areaCellCol)
                    }
                  }
                })
              })

              let cellEntirelyFillControl = vueObj.getCellEntirelyFillControl(row, col)
              if (cellEntirelyFillControl) {
                let sheetRowCount = getSheet().getRowCount()
                let sheetColumnCount = getSheet().getColumnCount()
                commonUtil.everyCell(0, 0, sheetRowCount, sheetColumnCount, (sheetCellRow, sheetCellCol) => {
                  if (!commonUtil.isHideBySpan(sheetCellRow, sheetCellCol, getSheet())) {
                    let propertyKey = vueObj.getPropertyKey(sheetCellRow, sheetCellCol)
                    if (propertyKey && vueObj.isAttributeRegistered(propertyKey) && vueObj.isValueRequiredAttribute(propertyKey)) {
                      addHighlightCell(sheetCellRow, sheetCellCol)
                    }
                  }
                })
              }

              let innerCellId = innerCellIdManager.getInnerCellId(row, col)
              if (!checkedAll()) {
                if (!silent) {
                  vueObj.highlightCellsInBackFillMode = getHighlightCellInfoList()
                  setMessage(innerCellId, "存在尚未填写的前置单元格，请检查前置单元格(高亮显示)的内容")
                }
                if (!vueObj.readOnlyCellIdMapByPreposedControl[innerCellId]) {
                  vueObj.$set(vueObj.readOnlyCellIdMapByPreposedControl, innerCellId, true)
                }
                return false
              }
              // 如果当前单元格不为空，且没有被禁用，则反向校验后置单元格的填写情况，如果后置单元格填写了则也要禁用此单元格
              if (checkValue(row, col) && !vueObj.isCellDisabled(row, col)) {
                let {
                  addHighlightCell: addHighlightCellR,
                  getHighlightCellInfoList: getHighlightCellInfoListR,
                  checkedAll: checkedAllR,
                } = getHighlightCellCheckUtil()

                let afterCellControls = vueObj.cellPreposedCellControlsReverseMap[innerCellId]
                afterCellControls && afterCellControls.forEach(({ sourceRow, sourceCol }) => {
                  addHighlightCellR(sourceRow, sourceCol, true)
                })
                let propertyKey = vueObj.getPropertyKey(row, col)
                // 如果当前单元格存在属性且不是只读属性且为必填属性，则根据区域控制信息、完全回填控制信息进一步校验
                if (propertyKey && vueObj.isAttributeRegistered(propertyKey) && !vueObj.isReadOnlyAttribute(propertyKey) && vueObj.isValueRequiredAttribute(propertyKey)) {
                  let areaIds = vueObj.areaData.filter(v => commonUtil.isCellInArea(row, col, v)).map(v => v.id)
                  areaIds.forEach(areaId => {
                    let ctrls = vueObj.cellPreposedAreaControlsReverseMap[areaId]
                    if (ctrls) {
                      ctrls.forEach(({ sourceRow, sourceCol }) => {
                        addHighlightCellR(sourceRow, sourceCol, true)
                      })
                    }
                  })
                  for (const key in vueObj.cellEntirelyFillControlMap) {
                    const { sourceRow, sourceCol } = vueObj.cellEntirelyFillControlMap[key]
                    addHighlightCellR(sourceRow, sourceCol, true)
                  }
                }
                if (!checkedAllR()) {
                  if (!silent) {
                    vueObj.highlightCellsInBackFillMode = getHighlightCellInfoListR()
                    setMessage(innerCellId, "根据回填顺序配置以及实际回填情况，此单元格已被禁止修改，如果想修改，请根据高亮单元格依次清空回填")
                  }
                  if (!vueObj.readOnlyCellIdMapByPreposedControl[innerCellId]) {
                    vueObj.$set(vueObj.readOnlyCellIdMapByPreposedControl, innerCellId, true)
                  }
                  return false
                }
              }

              if (!silent) {
                vueObj.highlightCellsInBackFillMode = getHighlightCellInfoList()
              }
              if (!silent) {
                closeMessage()
              }
              if (vueObj.readOnlyCellIdMapByPreposedControl[innerCellId]) {
                vueObj.$delete(vueObj.readOnlyCellIdMapByPreposedControl, innerCellId)
              }
              return true
            }
            let hideInfo = () => {
              if (vueObj.highlightCellsInBackFillMode.length > 0) {
                vueObj.highlightCellsInBackFillMode = []
              }
              closeMessage()
            }
            return {
              checkCellPreposedControl,
              hideInfo,
              closeMessage,
              setMessage
            }
          }
        })()

        let getAxiosInstance = ((alertMessage) => {
          let axiosInstance = null
          return () => {
            if (!axios) {
              console.error("本组件依赖axios，请引入")
              return
            }
            if (!axiosInstance) {
              axiosInstance = axios.create({})
              axiosInstance.interceptors.response.use(
                (res) => {
                  const { code, data, msg } = res.data
                  if (code == 1) {
                    return data
                  } else {
                    let typeMap = {
                      "0": "warning",
                      "-1": "error"
                    }
                    alertMessage(msg, typeMap[code] || "error")
                    let error = new Error(msg)
                    if (code == 0) {
                      error.normal = true
                    }
                    return Promise.reject(error)
                  }
                },
                (err) => {
                  return Promise.reject(err)
                }
              )

            }
            return axiosInstance
          }
        })((message, type) => {
          commonUtil.uniqueAlertMessage(message, (onClose) => {
            let ctrlObj = vueCore.getConfig().alertMessage(message, type, onClose)
            return () => ctrlObj.close()
          })
        })

        let cellUrlTriggerUtil = (({
          getOriCellData,
          getBillData,
          getConfig,
          updateExtraObject,
          setValue
        }) => {
          let newCellDataVueCore = new Vue({
            data: {
              map: {}
            }
          })
          let setNewCellData = (cellId, cellData) => {
            Vue.set(newCellDataVueCore.map, cellId, cellData)
          }
          let getNewCellData = (cellId) => {
            return newCellDataVueCore.map[cellId]
          }

          let getAllNewCellData = () => {
            return newCellDataVueCore.map
          }

          let updateCellsValue = (cells) => {
            let {
              getCellId,
              getCellValue,
              getCellCode,
            } = getTransCellDataConfig(getConfig())
            let sheet = getSpread().getActiveSheet()
            sheet.suspendPaint()
            cells.forEach(cell => {
              let cellId = getCellId(cell)
              let [row, col] = cellIdManager.getCellPositionById(cellId)
              if (row == -1 || col == -1) {
                [row, col] = getCellPosFromCellData(cell, this.getConfig())
              }
              let cellCode = getCellCode(cell)
              cellPropertyDataManager.setExtraData(row, col, cellCode)
              let cellValue = getCellValue(cell)
              setValue(row, col, cellValue, sheet)
            })
            sheet.resumePaint()
          }

          let mergeCellDataProperty = (newData, dataA, dataB, getProperty, setProperty) => {
            if (getProperty(dataB) != null) {
              setProperty(newData, getProperty(dataB))
            } else if (getProperty(dataA) != null) {
              setProperty(newData, getProperty(dataA))
            }
          }
          let getSimpleGetterSetter = (property) => {
            return [
              o => o[property],
              (o, v) => o[property] = v
            ]
          }
          let con = {
            readOnly: "readOnly",
            extraOptions: "extraOptions",
            triggerTiming: "triggerTiming",
            triggerConditionOption: "triggerConditionOption",
            triggerUrlInfo: "triggerUrlInfo",
            triggerUrlConfirmMessage: "triggerUrlConfirmMessage",
            triggerGroupKey: "triggerGroupKey",
            tip: "tip",
          }
          let setCellId = (cellData, id) => {
            let config = getConfig()
            config.cellDataConfig.setCellId(cellData, id, config)
          }
          let getCellId = (cellData) => {
            let config = getConfig()
            return config.cellDataConfig.getCellId(cellData, config)
          }
          let getCellRow = (cellData) => {
            let config = getConfig()
            return config.cellDataConfig.getCellRow(cellData, config)
          }
          let getCellCol = (cellData) => {
            let config = getConfig()
            return config.cellDataConfig.getCellCol(cellData, config)
          }

          let confirmMessage = (msg) => {
            return getConfig().confirmMessage(msg, "操作确认")
          }

          let alertMessage = (message, type) => {
            commonUtil.uniqueAlertMessage(message, (onClose) => {
              let ctrlObj = getConfig().alertMessage(message, type || "info", onClose)
              return () => ctrlObj.close()
            })
          }

          // let conMap = commonUtil.listToMap(Object.keys(con).map(v => con[v]), v => v)
          let mergeCellData = (cellDataA, cellDataB) => {
            let newCellData = {
              ...cellDataA,
              ...cellDataB
            }
            let cellIdA = getCellId(cellDataA)
            let cellIdB = getCellId(cellDataB)
            if (cellIdA != cellIdB && cellIdA != null && cellIdB != null) {
              console.error([cellDataA, cellDataB], "程序错误，合并了不同id的单元格信息，默认使用了左边的id")
            }
            setCellId(newCellData, cellIdA || cellIdB)
            mergeCellDataProperty(newCellData, cellDataA, cellDataB, ...getSimpleGetterSetter(con.readOnly))
            mergeCellDataProperty(newCellData, cellDataA, cellDataB, ...getSimpleGetterSetter(con.extraOptions))
            mergeCellDataProperty(newCellData, cellDataA, cellDataB, ...getSimpleGetterSetter(con.tip))

            if (cellDataB.triggerUrlInfo) {
              mergeCellDataProperty(newCellData, {}, cellDataB, ...getSimpleGetterSetter(con.triggerTiming))
              mergeCellDataProperty(newCellData, {}, cellDataB, ...getSimpleGetterSetter(con.triggerConditionOption))
              mergeCellDataProperty(newCellData, {}, cellDataB, ...getSimpleGetterSetter(con.triggerUrlInfo))
              mergeCellDataProperty(newCellData, {}, cellDataB, ...getSimpleGetterSetter(con.triggerUrlConfirmMessage))
              mergeCellDataProperty(newCellData, {}, cellDataB, ...getSimpleGetterSetter(con.triggerGroupKey))
            } else if (cellDataA.triggerUrlInfo) {
              mergeCellDataProperty(newCellData, cellDataA, {}, ...getSimpleGetterSetter(con.triggerTiming))
              mergeCellDataProperty(newCellData, cellDataA, {}, ...getSimpleGetterSetter(con.triggerConditionOption))
              mergeCellDataProperty(newCellData, cellDataA, {}, ...getSimpleGetterSetter(con.triggerUrlInfo))
              mergeCellDataProperty(newCellData, cellDataA, {}, ...getSimpleGetterSetter(con.triggerUrlConfirmMessage))
              mergeCellDataProperty(newCellData, cellDataA, {}, ...getSimpleGetterSetter(con.triggerGroupKey))
            }
            return newCellData
          }
          let _setNewCellData = (cellData) => {
            let cellId = getCellId(cellData)
            if (cellId) {
              if (!getOriCellData(cellId)) {
                let row = getCellRow(cellData)
                let col = getCellCol(cellData)
                if (!cellIdManager.getCellId(row, col)) {
                  cellIdManager.setCellId(row, col, cellId)
                } else {
                  let msg = `${row + 1}行${col + 1}列已经存在的单元格id与当前需要更新的单元格数据的id存在冲突，请检查程序`
                  window.alert(msg)
                  console.error(msg, cellData)
                }
              }
              setNewCellData(cellId, copyDataSimple(cellData))
            } else {
              let msg = "单元格数据缺少id，更新数据失败，请检查程序"
              window.alert(msg)
              console.error(msg, cellData)
            }
          }
          let getMergedCellData = (cellId) => {
            let oriCellData = getOriCellData(cellId)
            let newCellData = getNewCellData(cellId)
            if (!oriCellData && !newCellData) {
              return null
            }
            return mergeCellData(oriCellData || {}, newCellData || {})
          }

          let getValueIfHasCellId = (row, col, getValue) => {
            let cellId = cellIdManager.getCellId(row, col)
            if (cellId) {
              return getValue(cellId)
            }
            return null
          }

          let getCellTriggerTiming = (row, col) => {
            return getValueIfHasCellId(row, col, cellId => {
              return getMergedCellData(cellId)[con.triggerTiming]
            })
          }
          let getCellTriggerUrlInfo = (row, col) => {
            return getValueIfHasCellId(row, col, cellId => {
              return getMergedCellData(cellId)[con.triggerUrlInfo]
            })
          }

          let getCellTriggerUrlConfirmMessage = (row, col) => {
            return getValueIfHasCellId(row, col, cellId => {
              return getMergedCellData(cellId)[con.triggerUrlConfirmMessage]
            })
          }
          let getCellTriggerConditionOption = (row, col) => {
            return getValueIfHasCellId(row, col, cellId => {
              return getMergedCellData(cellId)[con.triggerConditionOption]
            })
          }
          let getCellTriggerGroupKey = (row, col) => {
            return getValueIfHasCellId(row, col, cellId => {
              return getMergedCellData(cellId)[con.triggerGroupKey]
            })
          }

          let checkCellTriggerConditionOption = (row, col, value) => {
            let triggerConditionOption = getCellTriggerConditionOption(row, col)
            if (isBlank(triggerConditionOption)) {
              return true
            } else {
              return triggerConditionOption === value
            }
          }


          let getNotBlankValueUseDefault = (value, defaultValue) => {
            return isBlank(value) ? defaultValue : value
          }

          let getCellUrlGroups = (cells) => {
            let groups = commonUtil.getLinkedMap()
            let getNoneUrlGroupKey = (cell) => {
              let noneUrlGroupKey = "_noneUrlGroupKey_"
              let defaultConfirmMessageBeforeChange = getNotBlankValueUseDefault(cell.defaultConfirmMessageBeforeChange, "")
              let defaultNotAllowChangeMessage = getNotBlankValueUseDefault(cell.defaultNotAllowChangeMessage, "")
              let defaultCheckErrorMessage = getNotBlankValueUseDefault(cell.defaultCheckErrorMessage, "")
              if (defaultConfirmMessageBeforeChange || defaultNotAllowChangeMessage || defaultCheckErrorMessage) {
                return noneUrlGroupKey + [defaultConfirmMessageBeforeChange, defaultNotAllowChangeMessage, defaultCheckErrorMessage].join("_") + "_"
              } else {
                let cellId = cellIdManager.getCellId(cell.row, cell.col)
                return noneUrlGroupKey + cellId + "_"
              }
            }
            cells.forEach((cell) => {
              let { row, col, afterValue } = cell
              let triggerUrlInfo = getCellTriggerUrlInfo(row, col)
              if (triggerUrlInfo && checkCellTriggerConditionOption(row, col, afterValue)) {
                let key = triggerUrlInfo.url
                let triggerGroupKey = getCellTriggerGroupKey(row, col)
                if (!isBlank(triggerGroupKey)) {
                  key = triggerGroupKey
                }
                if (!groups.hasKey(key)) {
                  groups.set(key, {
                    groupKey: key,
                    urlInfo: triggerUrlInfo,
                    timing: getCellTriggerTiming(row, col),
                    triggerUrlConfirmMessage: getCellTriggerUrlConfirmMessage(row, col),
                    cells: [],
                    defaultConfirmMessageBeforeChange: cell.defaultConfirmMessageBeforeChange,
                    defaultNotAllowChangeMessage: cell.defaultNotAllowChangeMessage,
                    defaultCheckErrorMessage: cell.defaultCheckErrorMessage,
                  })
                }
                groups.get(key).cells.push(cell)
              } else {
                let cellNoneUrlGroupKey = getNoneUrlGroupKey(cell)
                if (!groups.hasKey(cellNoneUrlGroupKey)) {
                  groups.set(cellNoneUrlGroupKey, {
                    groupKey: cellNoneUrlGroupKey,
                    cells: [],
                    defaultConfirmMessageBeforeChange: cell.defaultConfirmMessageBeforeChange,
                    defaultNotAllowChangeMessage: cell.defaultNotAllowChangeMessage,
                    defaultCheckErrorMessage: cell.defaultCheckErrorMessage,
                  })
                }
                groups.get(cellNoneUrlGroupKey).cells.push(cell)
              }
            })
            return groups.values()
          }

          let updateBillData = (data, over_, checkCouldChange, changedCellsForCall) => {
            let over = () => {
              over_ && over_()
            }
            let { changedCells, extraObject, nextUrlInfo, nextUrlConfirmMessage, nextUrlParams } = data
            if (changedCells && changedCells.length > 0) {
              // 设置新单元格的数据
              changedCells.forEach(cell => {
                checkCouldChange && checkCouldChange(cell)
                _setNewCellData(cell)
              })
              // 更新单元格数据
              updateCellsValue(changedCells)
              let {
                getCellInitialized,
                getCellSaved,
                getCellRow,
                getCellCol,
              } = getTransCellDataConfig(getConfig())

              // 初始化单元格信息、设置单元格保存信息
              changedCells.forEach(cell => {
                let cellInitialized = getCellInitialized(cell)
                let cellSaved = getCellSaved(cell)
                let row = getCellRow(cell)
                let col = getCellCol(cell)
                if (cellSaved !== true) {
                  if (cellInitialized !== false) {
                    cellChangedManager.setCellInitialized(row, col)
                  }
                } else {
                  cellChangedManager.setCellsSaved([getCellId(cell)])
                  cellChangedManager.setCellInitialized(row, col)
                }
              })

              // 如果存在变更单元格则清空undo历史
              if (changedCells.length > 0) {
                getSpread().undoManager().clear()
              }
            }
            // 如果存在额外信息则更新
            if (extraObject) {
              updateExtraObject(extraObject)
            }

            if (nextUrlInfo && nextUrlInfo.url) {
              if (nextUrlInfo.urlType == BillCellTriggerUrlTypeEnum.FUNCTION) {
                if (nextUrlConfirmMessage) {
                  confirmMessage(nextUrlConfirmMessage).then(() => {
                    return startOneFunction(changedCellsForCall || changedCells, nextUrlInfo, nextUrlParams, checkCouldChange)
                  }).finally(over)
                } else {
                  startOneFunction(changedCellsForCall || changedCells, nextUrlInfo, nextUrlParams, checkCouldChange).finally(over)
                }
              } else if (nextUrlInfo.urlType == BillCellTriggerUrlTypeEnum.PAGE) {
                if (nextUrlConfirmMessage) {
                  confirmMessage(nextUrlConfirmMessage).then(() => {
                    openPage(nextUrlInfo.url, nextUrlParams)
                  }).finally(over)
                } else {
                  openPage(nextUrlInfo.url, nextUrlParams)
                  over()
                }
              } else {
                over()
                console.error("错误的urlType", nextUrlInfo)
              }
            } else {
              over()
            }
          }

          let startOneFunction = (changedCellsForCall, urlInfo, urlParams, checkCouldChange, checkAllowChange) => {
            let billData = getBillData()
            let getKey = (row, col) => row + "," + col
            let {
              setCellValue,
              setCellCode,
              setCellChangeType,
            } = getTransCellDataConfig(getConfig())
            let cellMap = commonUtil.listToMap(billData.cells, v => getKey(getCellRow(v), getCellCol(v)))
            let triggerCells = changedCellsForCall.map(cell => {
              let key = getKey(cell.row, cell.col)
              let cellData = cellMap[key]
              let billCellChangeInfo = {
                beforeChangeCellInfo: (() => {
                  let copy = copyDataSimple(cellData)
                  let value = cell.beforeValue
                  let code = cell.beforeCode
                  let changeType = cell.beforeChangeType
                  setCellValue(copy, value)
                  if (code !== undefined) {
                    setCellCode(copy, code)
                  }
                  setCellChangeType(copy, changeType)
                  return copy
                })(),
                afterChangeCellInfo: (() => {
                  let copy = copyDataSimple(cellData)
                  let value = cell.afterValue
                  let code = cell.afterCode
                  let changeType = cell.afterChangeType
                  setCellValue(copy, value)
                  if (code !== undefined) {
                    setCellCode(copy, code)
                  }
                  setCellChangeType(copy, changeType)
                  return copy
                })()
              }
              return billCellChangeInfo
            })

            let sendData = {
              triggerCells,
              cells: billData.cells,
              areas: billData.areas,
              extraObject: billData.extraObject
            }

            return new Promise((resolve, reject) => {
              let loading = getConfig().windowLoading()
              getAxiosInstance().post(urlInfo.url, sendData, {
                params: urlParams
              }).then((data) => {
                loading.close()
                if (!checkAllowChange || (checkAllowChange && data.allowChange === true)) {
                  updateBillData(data, () => {
                    resolve(data)
                  }, checkCouldChange, changedCellsForCall)
                } else {
                  resolve(data)
                }
              }).catch((error) => {
                loading.close()
                reject(error)
              })
            })
          }

          let doAfterConfirm = async (triggerUrlConfirmMessage, fn) => {
            let cancelTriggerUrl = false
            if (triggerUrlConfirmMessage) {
              try {
                await confirmMessage(triggerUrlConfirmMessage)
              } catch (error) {
                cancelTriggerUrl = true
              }
            }
            if (!cancelTriggerUrl) {
              await fn()
            }
          }

          let getCellNames = (cells) => {
            let sheet = getSpread().getActiveSheet()
            return cells.map(v => commonUtil.getCellName(sheet, v.row, v.col)).join("、")
          }

          let doInSuspendPaint = (fn) => {
            let spread = getSpread()
            spread.suspendPaint()
            fn()
            spread.resumePaint()
          }

          //row, col, beforeValue, beforeCode, beforeChangeType, afterValue, afterCode, afterChangeType, doChange(allowChange), defaultConfirmMessageBeforeChange, defaultNotAllowChangeMessage, defaultCheckErrorMessage
          let startTriggerUrl = async (changingCells) => {
            let cellUrlGroups = getCellUrlGroups(changingCells)

            let {
              setCellId,
              getCellId,
              setCellValue,
              setCellCode,
            } = getTransCellDataConfig(getConfig())
            let cellUrlGroupCellMaps = cellUrlGroups.map(group => {
              return commonUtil.listToMap(group.cells, v => cellIdManager.getCellId(v.row, v.col))
            })
            let notAllowChangeError = "_notAllowChangeError_"
            for (let i = 0; i < cellUrlGroups.length; i++) {
              const cellUrlGroup = cellUrlGroups[i]
              if (cellUrlGroup.urlInfo) {
                let checkCouldChange = (cellData) => {
                  let id = getCellId(cellData)
                  if (cellUrlGroupCellMaps.slice(i + 1).some(map => id in map)) {
                    throw notAllowChangeError
                  }
                }
                try {
                  let { cells, timing, urlInfo, triggerUrlConfirmMessage, defaultConfirmMessageBeforeChange, defaultNotAllowChangeMessage } = cellUrlGroup
                  cells = cells.filter(cell => !cell.checkCouldChangeRuntime || cell.checkCouldChangeRuntime())
                  if (cells.length > 0) {
                    if (timing === BillCellTriggerTimingEnum.BEFORE_CHANGE && urlInfo.urlType === BillCellTriggerUrlTypeEnum.FUNCTION) {
                      // 如果是在变更前，triggerUrlConfirmMessage和defaultConfirmMessageBeforeChange二选其一即可，不需要二次确认
                      let curConfirmMessage = !isBlank(triggerUrlConfirmMessage) ? triggerUrlConfirmMessage : defaultConfirmMessageBeforeChange
                      await doAfterConfirm(curConfirmMessage, async () => {
                        let { allowChange, operationResultMessage } = await startOneFunction(cells, urlInfo, undefined, checkCouldChange, true)
                        // 调用url获得结果后，根据调用情况对单元格进行变更设置
                        doInSuspendPaint(() => {
                          cells.forEach(v => v.doChange(allowChange))
                        })
                        // 如果返回了false并且没有设置撤销信息，则设置默认撤销编辑
                        if (!allowChange && !operationResultMessage) {
                          operationResultMessage = defaultNotAllowChangeMessage ? defaultNotAllowChangeMessage : `根据业务规则，单元格操作被撤销：${getCellNames(cells)}`
                        }
                        if (operationResultMessage) {
                          alertMessage(operationResultMessage)
                        }
                      })
                    } else {
                      // 如果是在变更后调用，分别进行变更前的确认和变更后的确认
                      await doAfterConfirm(defaultConfirmMessageBeforeChange, async () => {
                        // 执行单元格变更方法
                        doInSuspendPaint(() => {
                          cells.forEach(v => v.doChange(true))
                        })
                        // 确认后触发url
                        await doAfterConfirm(triggerUrlConfirmMessage, async () => {
                          if (urlInfo.urlType === BillCellTriggerUrlTypeEnum.FUNCTION) {
                            let { operationResultMessage } = await startOneFunction(cells, urlInfo, undefined, checkCouldChange)
                            if (operationResultMessage) {
                              alertMessage(operationResultMessage)
                            }
                          } else if (urlInfo.urlType === BillCellTriggerUrlTypeEnum.PAGE) {
                            openPage(urlInfo.url)
                          }
                        })
                      })
                    }
                  }
                } catch (error) {
                  let { cells, timing, urlInfo, defaultCheckErrorMessage } = cellUrlGroup
                  let msg = null
                  if (timing === BillCellTriggerTimingEnum.BEFORE_CHANGE && urlInfo.urlType === BillCellTriggerUrlTypeEnum.FUNCTION) {
                    msg = defaultCheckErrorMessage ? defaultCheckErrorMessage : `单元格变更校验请求调用失败：${getCellNames(cells)}`
                  } else {
                    msg = `单元格变更触发请求调用失败：${getCellNames(cells)}`
                  }

                  if (error == notAllowChangeError) {
                    msg += "：服务变更了意料之外的单元格"
                    updateCellsValue(cells.map(cell => {
                      let { row, col, beforeValue, beforeCode, beforeChangeType } = cell
                      let cellData = {}
                      let cellId = cellIdManager.getCellId(row, col)
                      setCellId(cellData, cellId)
                      setCellValue(cellData, beforeValue)
                      if (beforeCode !== undefined) {
                        setCellCode(cellData, beforeCode)
                      }
                      if (beforeChangeType === BillEntityChangeTypeEnum.NO_CHANGE) {
                        cellChangedManager.setCellsSaved([cellId])
                      }
                      return cellData
                    }))
                    getSpread().undoManager().clear()
                  } else {
                    console.error(msg, error)
                  }
                  if (!error || !error.normal) {
                    alertMessage(msg, "error")
                  }
                }
              } else {
                let { cells, defaultConfirmMessageBeforeChange } = cellUrlGroup
                cells = cells.filter(cell => !cell.checkCouldChangeRuntime || cell.checkCouldChangeRuntime())
                await doAfterConfirm(defaultConfirmMessageBeforeChange, async () => {
                  doInSuspendPaint(() => {
                    cells.forEach(v => v.doChange(true))
                  })
                })
              }
            }
          }

          let iframeWrapper
          if (dataMapUtil.hasData("iframeWrapper")) {
            iframeWrapper = dataMapUtil.getData("iframeWrapper")
          } else {
            iframeWrapper = new Vue({
              components: {
                CustomButton: () => Promise.resolve(getConfig().buttonComponentGetter())
              },
              template: `
                <div v-if="show" style="position: fixed;width: 100vw;height: 100vh;left: 0;top: 0;z-index: 10000;background: white;display: flex;flex-direction: column;align-items: flex-end;">
                  <custom-button @click.native="close">关闭</custom-button>
                  <iframe :src="url" style="
                    width: 100%;
                    flex-grow: 1;
                    border: none;
                    border-top: 2px solid lightgray;
                  "></iframe>
                </div>
              `,
              data: {
                url: null
              },
              computed: {
                show() {
                  return !!this.url
                }
              },
              methods: {
                close() {
                  this.url = null
                }
              },
              watch: {
                show(value) {
                  if (value) {
                    document.body.append(this.$el)
                  } else {
                    this.$el.remove()
                  }
                }
              }
            })
            dataMapUtil.setData("iframeWrapper", iframeWrapper)
            // setTimeout(() => {
            //   iframeWrapper.$mount()
            //   iframeWrapper.url = "/asd/asd1"
            // }, 1000)
          }

          let openPage = (url, urlParams) => {
            if (!iframeWrapper.$el) {
              iframeWrapper.$mount()
            }
            let urlParamsObj = new URLSearchParams(urlParams)
            let urlParamsStr = urlParamsObj.toString()
            let openUrl = url + (urlParamsStr.length > 0 ? "?" + urlParamsStr : "")
            iframeWrapper.url = openUrl
            // let targetWindow = window.top.open(openUrl)
            // if (targetWindow == null) {
            //   confirmMessage("即将进入新页面").then(() => {
            //     let tw = window.top.open(openUrl)
            //     if (tw == null) {
            //       alert("打开新页面失败")
            //     }
            //   })
            // }
          }
          return {
            // setNewCellData,
            getMergedCellData,
            getNewCellData,
            hasTriggerUrl(row, col) {
              let cellId = cellIdManager.getCellId(row, col)
              if (cellId) {
                let mergedCellData = getMergedCellData(cellId)
                let triggerUrlInfo = mergedCellData[con.triggerUrlInfo]
                if (triggerUrlInfo && triggerUrlInfo.url) {
                  return true
                }
              }
              return false
            },
            checkCellNeedTriggerUrl(row, col, value) {
              if (this.hasTriggerUrl(row, col)) {
                return checkCellTriggerConditionOption(row, col, value)
              }
              return false
            },
            checkCellNeedCancelEditing(row, col) {
              return getCellTriggerTiming(row, col) == BillCellTriggerTimingEnum.BEFORE_CHANGE
            },
            isPageUrl(row, col) {
              return getCellTriggerUrlInfo(row, col).urlType == BillCellTriggerUrlTypeEnum.PAGE
            },
            isFunctionUrl(row, col) {
              return getCellTriggerUrlInfo(row, col).urlType == BillCellTriggerUrlTypeEnum.FUNCTION
            },
            goPage(row, col) {
              let urlInfo = getCellTriggerUrlInfo(row, col)
              openPage(urlInfo.url)
            },
            clearData() {
              newCellDataVueCore.map = {}
            },
            startTriggerUrl,
            getCellTriggerUrlInfo,
            getCellTriggerTiming,
            getCellTriggerUrlConfirmMessage,
            getAllNewCellData,
            updateBillData
          }
        })(
          {
            getOriCellData: (oriCellId) => {
              return vueCore.oriCellDataMap[oriCellId]
            },
            getBillData: () => vueCore.getBillData(),
            getConfig: () => vueCore.getConfig(),
            updateExtraObject: (data => {
              vueCore.$set(vueCore.originalData, "extraObjectString", JSON.stringify(data))
            }),
            setValue: (row, col, value, sheet) => {
              vueCore.setValue(row, col, value, sheet)
            }
          }
        )

        let getAttributeOriOperationalByMode = getGetterForAttributeOriOperationalByMode({
          getCellPosition: (innerCellId) => innerCellIdManager.getCellPosition(innerCellId),
          getInnerCellId: (row, col) => innerCellIdManager.getInnerCellId(row, col),
          getCellId: (row, col) => cellIdManager.getCellId(row, col),
          startTriggerUrl: (changingCells) => cellUrlTriggerUtil.startTriggerUrl(changingCells),
          getNewCellData: (cellId) => cellUrlTriggerUtil.getNewCellData(cellId),
          getCellChangeType: (cellId) => cellChangedManager.getCellChangeType(cellId),
          getCheckCellPreposedControlUtil
        })

        let vueCore = new Vue({
          data: {
            config: {},
            tabStripRatio: 0,
            originalData: {
              ssjsonDataString: null,
              cellsString: null,
              areasString: null,
              extraObjectString: null,
              propertyTreeString: null,
              propertyOptionListString: null
            }, // 原始数据

            areaData: [], // 内部使用的区域数据 x,y,width,height,id

            controlData: [], // 内部使用的控制数据
            isShowMode: false,
            curSelections: [],// 当前选择的单元格区域

            userData: {},// 用于存储用户数据

            isInSelectMode: false,// 是否处于选择模式

            readOnlyCellIdMapByPreposedControl: {},// 回填时，因回填顺序而被临时禁用的单元格id(使用innerCellId)
            highlightCellsInBackFillMode: [],// 回填时，需要高亮的单元格信息
          },
          computed: {
            cellPropertyData: {// 内部使用的单元格属性数据
              get() {
                return cellPropertyDataManager.getAllCellData()
              },
              set(value) {
                cellPropertyDataManager.setAllCellData(value)
              }
            },
            defaultSsjson() {
              return `{"version":"${spreadVersion}","tabStripRatio":${this.tabStripRatio},"customList":[],"sheets":{"Sheet1":{"name":"Sheet1","rowCount":200,"columnCount":200,"isSelected":true,"activeRow":0,"activeCol":0,"theme":"Office","data":{"defaultDataNode":{"style":{"themeFont":"Body"}},"dataTable":{}},"rowHeaderData":{"defaultDataNode":{"style":{"themeFont":"Body"}}},"colHeaderData":{"defaultDataNode":{"style":{"themeFont":"Body"}}},"leftCellIndex":0,"topCellIndex":0,"selections":{"0":{"row":0,"rowCount":1,"col":0,"colCount":1},"length":1},"cellStates":{},"outlineColumnOptions":{},"autoMergeRangeInfos":[],"printInfo":{"paperSize":{"width":850,"height":1100,"kind":1}},"index":0}}}`
            },
            propertyTree() {
              if (this.originalData && this.originalData.propertyTreeString) {
                return JSON.parse(this.originalData.propertyTreeString)
              } else {
                return []
              }
            },
            // 属性Map, 只存留叶子节点
            propertyMap() {
              let map = {}
              let {
                propertyTreeConfig: {
                  getCode,
                  getName,
                  getChildren
                }
              } = this.getConfig()
              let setOne = v => {
                let children = getChildren(v)
                if (children) {
                  children.forEach(setOne)
                } else {
                  map[getCode(v)] = v
                }
              }
              this.propertyTree.forEach(setOne)
              return map
            },
            propertyOptionList() {
              if (this.originalData && this.originalData.propertyOptionListString) {
                return JSON.parse(this.originalData.propertyOptionListString)
              } else {
                return []
              }
            },
            propertyOptionMap() {
              let map = {}
              let oriOptionList = this.propertyOptionList
              if (oriOptionList) {
                let {
                  propertyOptionListConfig: {
                    getOptionValue,
                    getAttributeCode
                  }
                } = this.getConfig()
                oriOptionList.forEach(option => {
                  let attributeCode = getAttributeCode(option)
                  if (!map[attributeCode]) {
                    map[attributeCode] = {}
                  }
                  let optionValue = getOptionValue(option)
                  if (!map[attributeCode][optionValue]) {
                    map[attributeCode][optionValue] = option
                  } else {
                    console.error(`属性值重复, 属性code: ${attributeCode}, 值: ${optionValue}`)
                  }
                })
              }
              return map
            },
            propertyOptionValueListMap() {
              let map = {}
              let oriOptionList = this.propertyOptionList
              if (oriOptionList) {
                let {
                  propertyOptionListConfig: {
                    getOptionValue,
                    getAttributeCode
                  }
                } = this.getConfig()
                oriOptionList.forEach(option => {
                  let attributeCode = getAttributeCode(option)
                  if (!map[attributeCode]) {
                    map[attributeCode] = []
                  }
                  let optionValue = getOptionValue(option)
                  if (!map[attributeCode].find(v => v == optionValue)) {
                    map[attributeCode].push(optionValue)
                  }
                })
              }
              return map
            },
            oriCellDataMap() {
              let map = {}
              let {
                getCellId
              } = getTransCellDataConfig(this.getConfig())
              if (this.originalData && this.originalData.cellsString) {
                let cells = JSON.parse(this.originalData.cellsString)
                cells.forEach(cellData => {
                  map[getCellId(cellData)] = cellData
                })
              }
              return map
            },
            oriAreaMap() {
              let map = {}
              if (this.originalData && this.originalData.areasString) {
                let areas = JSON.parse(this.originalData.areasString)
                areas.forEach(area => {
                  // 使用id作为key
                  map[area.id] = area
                })
              }
              return map
            },
            // 与接口数据合并过的属性注册信息
            operationalAttributesMap() {
              let {
                propertyTreeConfig: {
                  getCode,
                  getName,
                  getModeCode
                }
              } = this.getConfig()

              let map = {}
              let getExObj = (propertyData) => {
                return {
                  readOnly: this._isPropertyReadOnly(propertyData),
                  valueRequired: this._isPropertyValueRequired(propertyData),
                  name: getName(propertyData),
                  code: getCode(propertyData),
                  modeCode: getModeCode(propertyData)
                }
              }
              let wrap = regAttrWrapperAfterDataInited
              for (const property in this.propertyMap) {
                let propertyData = this.propertyMap[property]
                let modeCode = getModeCode(propertyData)
                let oriObj = getAttributeOriOperationalByMode(this, modeCode)
                let attrObj = null
                if (oriObj) {
                  attrObj = {
                    ...oriObj,
                    ...getExObj(propertyData)
                  }
                } else {
                  attrObj = getExObj(propertyData)
                }
                wrap(attrObj, this, (innerCellId) => innerCellIdManager.getCellPosition(innerCellId))
                map[property] = attrObj
              }
              return map
            },
            areaMap() {
              return commonUtil.listToMap(this.areaData, v => v.id)
            },
            areaNumberMap() {
              let map = {}
              this.areaData.forEach((area, index) => {
                map[area.id] = index + 1
              })
              return map
            },
            areaTitleMap() {
              let map = {}
              commonUtil.getEnumConstants(AreaTypeEnum).forEach(areaType => {
                this.areaData.filter(area => area.type == areaType).forEach((area, index) => {
                  map[area.id] = `${AreaTypeEnum.properties[areaType].titlePrefix}${index + 1}`
                })
              })
              return map
            },
            titleCellKeyAreaMap() {
              let map = {}
              this.areaData.forEach(area => {
                // 固定左上角为区域标题的位置
                let titleCellKey = getCellKey(area.y, area.x)
                if (!map[titleCellKey]) {
                  map[titleCellKey] = []
                }
                map[titleCellKey].push(area)
              })
              return map
            },
            // 单元格控制信息
            cellControlsMap() {
              let map = {}
              this.controlData.forEach((control, index) => {
                let { sourceRow, sourceCol, type, targetRow, targetCol, targetAreaId } = control
                let cellKey = getCellKey(sourceRow, sourceCol)
                if (!map[cellKey]) {
                  map[cellKey] = []
                }
                map[cellKey].push({
                  ...control,
                  index: index
                })
              })
              return map
            },
            // 前置单元格控制信息
            cellPreposedCellControlsMap() {
              let map = {}
              for (const key in this.cellControlsMap) {
                const ctrls = this.cellControlsMap[key]
                let filteredCtrls = ctrls.filter(v => {
                  return v.type == ControlTypeEnum.PREMISS_CTRL && !isBlank(v.targetRow)
                })
                if (filteredCtrls.length > 0) {
                  map[key] = copyDataSimple(filteredCtrls)
                }
              }
              return map
            },
            // 单元格控制信息
            cellPreposedCellControlsReverseMap() {
              let map = {}
              for (const key in this.cellPreposedCellControlsMap) {
                const ctrls = this.cellPreposedCellControlsMap[key]
                ctrls.forEach((ctrl) => {
                  let { targetRow, targetCol } = ctrl
                  let innerCellId = innerCellIdManager.getInnerCellId(targetRow, targetCol)
                  if (!map[innerCellId]) {
                    map[innerCellId] = []
                  }
                  map[innerCellId].push(copyDataSimple(ctrl))
                })
              }
              return map
            },
            // 前置区域控制信息，根据单元格id查询依赖此单元格的单元格控制信息
            cellPreposedAreaControlsMap() {
              let map = {}
              for (const key in this.cellControlsMap) {
                const ctrls = this.cellControlsMap[key]
                let filteredCtrls = ctrls.filter(v => {
                  return v.type == ControlTypeEnum.PREMISS_CTRL && !isBlank(v.targetAreaId)
                })
                if (filteredCtrls.length > 0) {
                  map[key] = copyDataSimple(filteredCtrls)
                }
              }
              return map
            },
            // 区域控制信息，根据区域id查询依赖此区域的单元格控制信息
            cellPreposedAreaControlsReverseMap() {
              let map = {}
              for (const key in this.cellPreposedAreaControlsMap) {
                const ctrls = this.cellPreposedAreaControlsMap[key]
                ctrls.forEach((ctrl) => {
                  let { targetAreaId } = ctrl
                  if (!map[targetAreaId]) {
                    map[targetAreaId] = []
                  }
                  map[targetAreaId].push(copyDataSimple(ctrl))
                })
              }
              return map
            },
            // 完全回填控制信息
            cellEntirelyFillControlMap() {
              let map = {}
              for (const key in this.cellControlsMap) {
                const ctrls = this.cellControlsMap[key]
                let filteredCtrls = ctrls.filter(v => {
                  return v.type == ControlTypeEnum.ENTIRELY_FILL_CTRL
                })
                if (filteredCtrls.length > 0) {
                  map[key] = copyDataSimple(filteredCtrls[0])
                }
              }
              return map
            },
            // 单元格默认值设置控制信息
            settingDefaultValueCellControlsMap() {
              let map = {}
              for (const key in this.cellControlsMap) {
                const ctrls = this.cellControlsMap[key]
                let filteredCtrls = ctrls.filter(v => {
                  return v.type == ControlTypeEnum.DEFAULT_SETTING_CTRL && !isBlank(v.targetRow)
                })
                if (filteredCtrls.length > 0) {
                  map[key] = copyDataSimple(filteredCtrls)
                }
              }
              return map
            },
            // 区域默认值设置控制信息
            settingDefaultValueAreaControlsMap() {
              let map = {}
              for (const key in this.cellControlsMap) {
                const ctrls = this.cellControlsMap[key]
                let filteredCtrls = ctrls.filter(v => {
                  return v.type == ControlTypeEnum.DEFAULT_SETTING_CTRL && !isBlank(v.targetAreaId)
                })
                if (filteredCtrls.length > 0) {
                  map[key] = filteredCtrls
                }
              }
              return map
            },
            highlightAreas() {// 临时高亮区域
              let areaIds = []
              let selections = this.curSelections
              // 选择模式、回填模式的非只读状态暂无区域高亮情况，高亮目前均开始于单独选择了单元格
              if (this.isInSelectMode || !this.getConfig().configMode || !commonUtil.checkSingleCellSelection(selections, getSpread().getActiveSheet())) {
                return areaIds
              }
              let [{ row, col }] = selections
              let cellControls = this.getAllCellControls(row, col)
              cellControls && cellControls.forEach(({ targetAreaId }) => {
                if (!isBlank(targetAreaId)) {
                  areaIds.push(targetAreaId)
                }
              })
              return areaIds
            },
            highlightCells() {// 临时高亮单元格
              let cellInfoList = []
              let selections = this.curSelections
              // 选择模式暂无单元格高亮情况，高亮目前均开始于单独选择了单元格
              if (this.isInSelectMode || !commonUtil.checkSingleCellSelection(selections, getSpread().getActiveSheet())) {
                return cellInfoList
              }

              if (this.getConfig().configMode) {
                let [{ row, col }] = selections
                let cellControls = this.getAllCellControls(row, col)
                cellControls && cellControls.forEach(({ targetRow, targetCol }) => {
                  if (!isBlank(targetRow) && !isBlank(targetCol)) {
                    let id = innerCellIdManager.getInnerCellId(targetRow, targetCol)
                    cellInfoList.push([id])
                  }
                })
              } else {
                this.highlightCellsInBackFillMode.forEach(({ id, color }) => {
                  let info = [id]
                  if (color) {
                    info.push(color)
                  }
                  cellInfoList.push(info)
                })
              }
              return cellInfoList
            },
            highlightAreasMap() {
              return commonUtil.listToMap(this.highlightAreas, areaId => areaId)
            },
            highlightCellsMap() {
              return commonUtil.listToMap(this.highlightCells, ([id]) => {
                return id
              })
            },
            roleNameMap() {
              let map = {}
              if (this.userData && this.userData.roles) {
                this.userData.roles.forEach(v => {
                  map[v.code] = v.name
                })
              }
              return map
            },
          },
          methods: {
            getConfig() {
              return this.config
            },
            setConfig(config) {
              let vueCoreConfig = mergeConfigs({}, config)
              this.config = vueCoreConfig
            },
            getUndoManager() {
              return getUndoManager()
            },

            _isPropertyReadOnly(propertyData) {
              return propertyData.readOnly == "1"// ["0", "2"].some(v => v == propertyData.isChange)
            },
            _isPropertyValueRequired(propertyData) {
              return propertyData.backFillVerify == "1" // ["2", "3"].some(v => v == propertyData.isChange)
            },

            getPropertyKey(rowIndex, columnIndex) {
              return cellPropertyDataManager.getPropertyKey(rowIndex, columnIndex)
            },
            getExtraData(rowIndex, columnIndex) {
              return cellPropertyDataManager.getExtraData(rowIndex, columnIndex)
            },
            setExtraData(rowIndex, columnIndex, extraData) {
              cellPropertyDataManager.setExtraData(rowIndex, columnIndex, extraData)
            },
            getProperty(rowIndex, columnIndex) {
              let propertyKey = this.getPropertyKey(rowIndex, columnIndex)
              return propertyKey ? this.propertyMap[propertyKey] : null
            },
            isSignAttribute(attributeCode) { // 是否为签字属性(具有额外的code值)
              let regAttr = this.operationalAttributesMap[attributeCode]
              return !!regAttr && regAttr.modeCode === AttributeModeEnum.SIGN
            },
            isReadOnlyAttribute(attributeCode) {
              let regAttr = this.operationalAttributesMap[attributeCode]
              return regAttr.readOnly
            },
            isValueRequiredAttribute(attributeCode) {
              let regAttr = this.operationalAttributesMap[attributeCode]
              return regAttr.valueRequired
            },
            isAttributeRegistered(attributeCode) {
              return !!this.operationalAttributesMap[attributeCode]
            },
            isAttributeValueRequiredActually(attributeCode) {// 判断属性是否为必填属性
              let { configMode } = this.getConfig()
              if (configMode) {// 此方法只在回填时生效
                return false
              } else {
                let registeredAttr = this.operationalAttributesMap[attributeCode]
                if (registeredAttr) {
                  return !!registeredAttr.valueRequired
                } else {
                  return true
                }
              }
            },
            getOriCellInfo(row, col) {
              let cellId = cellIdManager.getCellId(row, col)
              if (cellId) {
                return cellUrlTriggerUtil.getMergedCellData(cellId)
              }
            },
            updateBillData(data) {
              cellUrlTriggerUtil.updateBillData(data)
            },
            isCellDisabled(row, col) {// 被禁用的单元格禁止属性相关的影响
              let {
                configMode
              } = this.getConfig()
              // 回填时，如果发现单元格临时被禁用
              if (!configMode) {
                let cellId = cellIdManager.getCellId(row, col)
                if (cellId) {
                  let cellData = cellUrlTriggerUtil.getMergedCellData(cellId)
                  if (cellData && cellData.readOnly) {
                    return true
                  }
                }
              }
              return false
            },
            couldCellEdited(innerCellId) {
              let {
                readOnly,
                configMode
              } = this.getConfig()
              // 如果配置只读，则任何单元格禁止编辑
              if (readOnly) {
                return false
              }
              let [row, col] = innerCellIdManager.getCellPosition(innerCellId)

              // 如果单元格被禁用，则单元格禁止编辑
              if (this.isCellDisabled(row, col)) {
                return false
              }

              let attributeCode = this.getPropertyKey(row, col)
              let operationalAttributesMap = this.operationalAttributesMap
              if (configMode) {
                // 编辑、新增模板时，只有具有readOnlyOnEdit属性的单元格禁止编辑
                let registeredAttr = operationalAttributesMap[attributeCode]
                if (registeredAttr && !!registeredAttr.readOnlyOnEdit) {
                  return false
                }
              } else {
                // 单据回填时，如果单元格没有属性或者其属性为只读，则单元格只读
                let registeredAttr = operationalAttributesMap[attributeCode]
                if (!registeredAttr || registeredAttr.readOnly) {
                  return false
                }
              }
              return true
            },
            isCellReadOnlyFinal(innerCellId) { // 判断单元格是否最终是只读的
              if (!this.couldCellEdited(innerCellId)) {
                return true
              }
              let [row, col] = innerCellIdManager.getCellPosition(innerCellId)
              // 如果单元格因回填顺序设置被禁用，则单元格只读
              if (this.readOnlyCellIdMapByPreposedControl[innerCellId]) {
                return true
              }
              // 签字属性的单元格只读
              if (this.isSignAttribute(this.getPropertyKey(row, col))) {
                return true
              }
              return false
            },
            getDefaultValue(row, col) {// 获取指定单元格的默认值
              let propertyObj = this.getProperty(row, col)
              if (propertyObj) {
                let {
                  propertyTreeConfig: {
                    getCode,
                    getName,
                    getModeCode
                  },
                  propertyOptionListConfig: {
                    getIsDefault
                  }
                } = this.getConfig()
                let modeCode = getModeCode(propertyObj)
                let code = getCode(propertyObj)

                let getOption = (optionValue) => {
                  return this.propertyOptionMap[code][optionValue]
                }

                let modeEnumProperty = AttributeModeEnum.properties[modeCode]
                if (modeEnumProperty && modeEnumProperty.hasDefaultValue) {
                  let optionValues = this.propertyOptionValueListMap[code]
                  if (optionValues && optionValues.length > 0) {
                    if (modeEnumProperty.onlyDefault) {
                      return optionValues[0]
                    } else {
                      return optionValues.find(v => getIsDefault(getOption(v)))
                    }
                  }
                }

              }
              return null
            },
            // 遍历所有存在属性的单元格
            everyCellWithProperty(callback) {
              cellPropertyDataManager.forEachCellData((row, col, propertyData) => {
                callback(row, col)
              })
            },
            // 遍历每一个拥有区域或者拥有属性的单元格
            everyCellWithPropertyOrArea(callback, areaData) {
              if (!areaData) {
                areaData = this.areaData
              }
              let uniqueCall = commonUtil.getUniqueCallFunction()

              areaData.forEach((areaInfo) => {
                let {
                  x,
                  y,
                  width,
                  height
                } = areaInfo
                for (let col = x; col < x + width; col++) {
                  for (let row = y; row < y + height; row++) {
                    uniqueCall(() => {
                      callback && callback({
                        row,
                        col
                      }, areaInfo)
                    }, getCellKey(row, col))
                  }
                }
              })
              let noneArea = {
                x: -1,
                y: -1,
                width: 0,
                height: 0,
              }
              // 遍历区域之外的属性
              this.everyCellWithProperty((row, col) => {
                uniqueCall(() => {
                  callback && callback({
                    row,
                    col
                  }, noneArea)
                }, getCellKey(row, col))
              })
            },
            everyCellWithPropertyOrWithControls(callback) {
              let uniqueCall = commonUtil.getUniqueCallFunction()
              // 遍历有属性的单元格
              this.everyCellWithProperty((row, col) => {
                uniqueCall(() => {
                  callback && callback(row, col)
                }, getCellKey(row, col))
              })
              // 遍历有control的单元格
              for (let key in this.cellControlsMap) {
                let { sourceRow: row, sourceCol: col } = this.cellControlsMap[key][0]
                uniqueCall(() => {
                  callback && callback(row, col)
                }, getCellKey(row, col))
              }
            },
            initCellPropertyData(cells, setAttributeCode, setExtraData) {
              let config = this.getConfig()
              let {
                configMode,
              } = config
              let {
                getCellAttributeCode,
                getCellCode
              } = getTransCellDataConfig(config)
              cells.forEach(cell => {
                let attributeCode = getCellAttributeCode(cell)
                if (attributeCode) {
                  let [row, col] = getCellPosFromCellData(cell, this.getConfig())
                  setAttributeCode(row, col, attributeCode)
                  if (!configMode) {
                    setExtraData(row, col, getCellCode(cell))
                  }
                }
              })
            },
            _getAreaData(areas) {
              return areas.map(({
                leftUp,
                rightDown,
                type,
                number,
                id
              }) => {
                let [leftX, upY] = leftUp.split(",").map(v => parseInt(v))
                let [rightX, downY] = rightDown.split(",").map(v => parseInt(v))
                setExistAreaId(number)
                return {
                  x: leftX,
                  y: upY,
                  width: rightX - leftX + 1,
                  height: downY - upY + 1,
                  type: type,
                  id: number,
                  originId: id
                }
              })
            },
            getEditConfigModeProtectionOptions() {
              return {
                allowDragInsertRows: true,
                allowDragInsertColumns: true,
                allowInsertRows: true,
                allowInsertColumns: true,
                allowDeleteRows: true,
                allowDeleteColumns: true,
                allowSelectLockedCells: true,
                allowSelectUnlockedCells: true,
                allowSort: true,
                allowFilter: true,
                allowEditObjects: true,
                allowResizeRows: true,
                allowResizeColumns: true,
              }
            },
            // 统一管理所有配置
            getDynamicSpreadSheetOptionsConfigs() {
              let getReadOnly = () => this.getConfig().readOnly
              let getConfigMode = () => this.getConfig().configMode
              let getAllowExtendOperation = () => this.getConfig().allowExtendOperation
              let getHeaderVisible = () => getConfigMode() || this.getConfig().headerVisible
              return {
                sheet: {
                  protectionOptions: {
                    predicate: () => getConfigMode() && !getReadOnly(),
                    trueValue: this.getEditConfigModeProtectionOptions(),
                    falseValue: {}
                  },
                  clipBoardOptions: {
                    predicate: () => getConfigMode(),
                    trueValue: Sheets.ClipboardPasteOptions.all,
                    falseValue: Sheets.ClipboardPasteOptions.values
                  },
                  rowHeaderVisible: {
                    predicate: () => getHeaderVisible(),
                    trueValue: true,
                    falseValue: false
                  },
                  colHeaderVisible: {
                    predicate: () => getHeaderVisible(),
                    trueValue: true,
                    falseValue: false
                  }
                },
                spread: {
                  defaultDragFillType: {
                    predicate: () => getConfigMode(),
                    trueValue: Sheets.Fill.AutoFillType.auto,
                    falseValue: Sheets.Fill.AutoFillType.fillWithoutFormatting
                  },
                  showDragFillSmartTag: {
                    predicate: () => getConfigMode(),
                    trueValue: true,
                    falseValue: false
                  },
                  allowUserDragFill: {
                    predicate: () => getConfigMode() || getAllowExtendOperation(),
                    trueValue: true,
                    falseValue: false
                  }
                },
              }
            },
            initSpreadSheetOptionsBinder(sheet) {
              let unbinds = []
              let spread = sheet.getParent()

              let spreadSheetOptionsConfigs = this.getDynamicSpreadSheetOptionsConfigs()

              for (const optionName in spreadSheetOptionsConfigs.spread) {
                const config = spreadSheetOptionsConfigs.spread[optionName]
                unbinds.unshift(this.$watch(() => {
                  return config.predicate()
                }, (value) => {
                  spread.options = {
                    ...spread.options,
                    [optionName]: value ? config.trueValue : config.falseValue
                  }
                }))
              }

              for (const optionName in spreadSheetOptionsConfigs.sheet) {
                const config = spreadSheetOptionsConfigs.sheet[optionName]
                unbinds.unshift(this.$watch(() => {
                  return config.predicate()
                }, (value) => {
                  sheet.options = {
                    ...sheet.options,
                    [optionName]: value ? config.trueValue : config.falseValue
                  }
                }))
              }
              return () => {
                unbinds.forEach(v => v())
              }
            },
            initSsjsonData(ssjsonData) {
              let unbinds = []

              let sheets = ssjsonData.sheets
              ssjsonData.tabStripRatio = this.tabStripRatio
              let firstSheet = ssjsonUtil.getFirstSheetFromJsonObj(ssjsonData)

              if (ssjsonData.activeSheetIndex != null) {
                ssjsonData.activeSheetIndex = 1
              }
              ssjsonData.sheetCount = 1
              Object.keys(sheets).forEach(k => {
                if (sheets[k].index != 0) {
                  delete sheets[k]
                }
              })
              ssjsonData.tabStripRatio = this.tabStripRatio

              firstSheet.isProtected = true
              unbinds.unshift((ssjsonData) => {
                // 清理单元格锁定信息
                ssjsonUtil.clearCellLock(ssjsonData)
              })

              let setSsjsonOption = (obj_, optionName, value) => {
                let oriValue = obj_[optionName]
                obj_[optionName] = value
                return (obj) => {
                  if (oriValue == null) {
                    delete obj[optionName]
                  } else {
                    obj[optionName] = oriValue
                  }
                }
              }

              let setSheetOption = (optionName, value) => {
                let unbind = setSsjsonOption(firstSheet, optionName, value)
                unbinds.unshift((ssjsonData) => {
                  unbind(ssjsonUtil.getFirstSheetFromJsonObj(ssjsonData))
                })
              }

              let setSpreadOption = (optionName, value) => {
                let unbind = setSsjsonOption(ssjsonData, optionName, value)
                unbinds.unshift((ssjsonData) => {
                  unbind(ssjsonData)
                })
              }

              let spreadSheetOptionsConfigs = this.getDynamicSpreadSheetOptionsConfigs()

              for (const optionName in spreadSheetOptionsConfigs.spread) {
                const config = spreadSheetOptionsConfigs.spread[optionName]
                setSpreadOption(optionName, config.predicate() ? config.trueValue : config.falseValue)
              }

              for (const optionName in spreadSheetOptionsConfigs.sheet) {
                const config = spreadSheetOptionsConfigs.sheet[optionName]
                setSheetOption(optionName, config.predicate() ? config.trueValue : config.falseValue)
              }
              // 固定禁止用户拖拽单元格
              setSpreadOption("allowUserDragDrop", false)

              return (ssjsonData) => {
                unbinds.forEach(v => v(ssjsonData))
              }
            },
            initAllCellsByConfig() { // 初始化单元格只读，执行属性的清理方法
              let spread = getSpread()
              let sheet = spread.getActiveSheet()
              let rowCount = sheet.getRowCount()
              let columnCount = sheet.getColumnCount()
              spread.suspendPaint()
              commonUtil.everyCell(0, 0, rowCount, columnCount, (row, col) => {
                let innerCellId = innerCellIdManager.getInnerCellId(row, col)
                sheet.getCell(row, col).locked(this.isCellReadOnlyFinal(innerCellId))
                let attrCode = this.getPropertyKey(row, col)
                if (attrCode) {
                  try {
                    let attrObj = this.operationalAttributesMap[attrCode]
                    if (attrObj && attrObj.clear) {
                      attrObj.clear(innerCellId, sheet)
                    }
                  } catch (error) {
                    console.error(error)
                  }
                }
              })
              spread.resumePaint()
            },
            // 根据所有属性数据初始化当前表格
            initCellsWhichHasProperty(disableWarningMessage) {
              let spread = getSpread()
              let sheet = spread.getActiveSheet()
              let uniqueCall = commonUtil.getUniqueCallFunction()
              this.everyCellWithProperty((row, col) => {
                let propertyKey = this.getPropertyKey(row, col)
                let registeredAttr = this.operationalAttributesMap[propertyKey]
                let unregisteredPropertyKeys = []
                if (registeredAttr) {
                  if (registeredAttr.initCellAttribute) {
                    let innerCellId = innerCellIdManager.getInnerCellId(row, col)
                    revertAttributeEffectManager.setAttributeEffect(innerCellId, () => {
                      return registeredAttr.initCellAttribute(innerCellId, sheet)
                    })
                  }
                } else {
                  if (!disableWarningMessage) {
                    uniqueCall(() => {
                      unregisteredPropertyKeys.push(propertyKey)
                    }, propertyKey)
                  }
                }
                if (unregisteredPropertyKeys.length > 0) {
                  this.getConfig().alertMessage(
                    `未注册的属性：${unregisteredPropertyKeys.join("、")}，请检查属性数据与单据属性并修改，以免影响使用`,
                    "warning"
                  )
                }
              })
            },
            setPropertyData({
              propertyTree,
              propertyOptionList
            }) {
              // 设置属性原始数据
              this.$set(this.originalData, "propertyTreeString", JSON.stringify(propertyTree))
              this.$set(this.originalData, "propertyOptionListString", JSON.stringify(propertyOptionList))
            },
            setUserData(userData) {
              if (userData) {
                // id, name, roles:[{code, name}...]}
                if (!userData.id) {
                  throw "用户信息缺少id"
                }
                if (!userData.name) {
                  throw "用户信息缺少名称(name)"
                }
                if (!userData.roles) {
                  throw "用户信息缺少角色列表(roles)"
                }
                if (userData.roles.some(obj => {
                  if (!obj.code || !obj.name) {
                    return true
                  } else {
                    return false
                  }
                })) {
                  throw "用户信息的roles的属性存在问题，请检查数据，列表内对象需要属性code和name"
                }
                this.userData = copyDataSimple(userData)
              }
            },
            getUserId() {
              return this.userData.id
            },
            getUserName() {
              return this.userData.name
            },
            hasRole(role) {// 判断当前用户是否为该角色
              return role in this.roleNameMap
            },
            // 设置单元格值，会根据属性设置
            setValue(row, col, value, sheet) {
              let propertyKey = this.getPropertyKey(row, col)
              let registeredAttr = this.operationalAttributesMap[propertyKey]
              if (registeredAttr && registeredAttr.setValue) {
                registeredAttr.setValue(row, col, value, sheet)
              } else {
                sheet.setValue(row, col, value)
              }
            },
            setTemplateData({
              ssjsonDataString,
              billData: {
                cells,
                areas,
                extraObject
              }
            }, localUse) {
              if (!this.originalData.propertyTreeString) {
                throw "请先设置属性数据"
              }
              if (isBlank(ssjsonDataString)) {
                ssjsonDataString = this.defaultSsjson
              }
              if (cells == null) {
                cells = []
              }
              if (areas == null) {
                areas = []
              }
              let spread = getSpread()
              // 暂停渲染
              spread.suspendPaint()

              // 恢复所有属性单元格的额外影响（主要目的是取消监听）
              revertAttributeEffectManager.revertAllCell()
              // 清除旧有可撤销的数据
              this.clearInnerData()
              // 清除额外单元格属性信息
              cellUrlTriggerUtil.clearData()

              let config = this.getConfig()

              let transCellDataConfig = getTransCellDataConfig(config)

              let {
                getCellId,
                setCellId
              } = transCellDataConfig

              // 初始化id位置数据（要优先初始化id位置数据，因为后面判断单元格只读情况依赖此id位置数据)
              cells.forEach(cell => {
                let cellId = getCellId(cell)
                let [row, col] = getCellPosFromCellData(cell, config)
                if (!cellId) {
                  cellId = innerCellIdManager.getInnerCellId(row, col)
                  console.warn("单元格id为空：" + JSON.stringify(cell))
                  setCellId(cell, cellId)
                }
                cellIdManager.setCellId(row, col, cellId)
              })

              // 设置原始数据
              this.$set(this.originalData, "ssjsonDataString", ssjsonDataString)
              this.$set(this.originalData, "cellsString", JSON.stringify(cells))
              this.$set(this.originalData, "areasString", JSON.stringify(areas))
              if (!config.configMode) {
                if (extraObject == null) {
                  extraObject = {}
                }
                this.$set(this.originalData, "extraObjectString", JSON.stringify(extraObject))
              }



              // 初始化ssjson数据
              let ssjsonData = JSON.parse(ssjsonDataString)
              let revertSsjson = this.initSsjsonData(ssjsonData)
              this._revertSsjson = revertSsjson
              spread.fromJSON(ssjsonData)

              // 初始化区域数据
              this.areaData = this._getAreaData(areas)

              // 初始化控制数据
              let {
                getControls,
              } = transCellDataConfig

              let controlData = this.controlData
              cells.forEach(cell => {
                let [row, col] = getCellPosFromCellData(cell, config)
                let cellControls = getControls(cell)
                cellControls && cellControls.forEach(control => {
                  let { type, rowId, colId, areaNumber } = control
                  if (isBlank(areaNumber) || this.areaMap[areaNumber]) {
                    controlData.push({
                      type,
                      sourceRow: row,
                      sourceCol: col,
                      targetRow: commonUtil.isBlank(rowId) ? null : parseInt(rowId),
                      targetCol: commonUtil.isBlank(colId) ? null : parseInt(colId),
                      targetAreaId: areaNumber
                    })
                  } else {
                    let msg = "排除异常控制数据，区域number不存在：" + JSON.stringify(control)
                    console.error(msg)
                    window.alert(msg)
                  }
                })
              })

              // 初始化单元格属性数据
              this.initCellPropertyData(cells, (row, col, propertyKey) => {
                return cellPropertyDataManager.setPropertyKey(row, col, propertyKey)
              }, (row, col, extraData) => {
                return cellPropertyDataManager.setExtraData(row, col, extraData)
              })

              let sheet = spread.getActiveSheet()

              // 设置不存在属性的单元格的是否只读，执行属性的清理方法
              // 由于清理方法会清理属性的痕迹，所以必须要先于属性的初始化方法执行
              this.initAllCellsByConfig()
              // 执行属性的初始化方法
              this.initCellsWhichHasProperty(localUse)

              let {
                getCellValue,
                getCellInitialized,
                getCellSaved,
                getCellNeedInitializeByAttributeDefaultValue,
              } = transCellDataConfig
              // 初始化单元格数据
              cells.forEach(cell => {
                let [row, col] = getCellPosFromCellData(cell, config)
                let value = getCellValue(cell)
                //getCellNeedInitializeByAttributeDefaultValue
                if (!config.configMode) {
                  let needInitializeByAttributeDefaultValue = getCellNeedInitializeByAttributeDefaultValue(cell)
                  if (isBlank(value) && needInitializeByAttributeDefaultValue === true) {
                    value = this.getDefaultValue(row, col)
                  }
                }
                this.setValue(row, col, value, sheet)
                let propertyKey = this.getPropertyKey(row, col)
                let registeredAttr = this.operationalAttributesMap[propertyKey]
                if (registeredAttr && registeredAttr.afterValueInit) {
                  registeredAttr.afterValueInit(row, col, sheet)
                }

                if (!config.configMode) {
                  let cellInitialized = getCellInitialized(cell)
                  let cellSaved = getCellSaved(cell)
                  // 如果单元格一上来就是未保存状态，则需要设置单元格具有原始的变化历史
                  if (cellSaved === false) {
                    // 如果没有初始化，则表示应该此单元格应该被设置INSERT，只要不初始化，就是INSERT
                    if (cellInitialized !== false) {
                      cellChangedManager.setCellInitialized(row, col)
                      cellChangedManager.setCellHasOuterChanged(getCellId(cell), value)
                    }
                  } else {
                    cellChangedManager.setCellInitialized(row, col)
                  }
                }
              })

              // 恢复渲染
              spread.resumePaint()
            },
            tempRevertAllCell() {
              revertAttributeEffectManager.revertAllCell()
              return () => {
                this.initCellsWhichHasProperty()
              }
            },
            getBillData(localUse) {
              let spread = getSpread()

              let sheet = spread.getActiveSheet()

              let cells = []
              let config = this.getConfig()
              let {
                setCellId,
                setCellValue,
                setCellAttributeCode,
                setCellRow,
                setCellCol,

                setCellCode,
                setCellChangeType,

                setLinkCells,
                setLinkCellId,

                setControls,
              } = getTransCellDataConfig(config)
              let getAttr = (row, col) => {
                return this.getPropertyKey(row, col)
              }

              let isHideBySpan = (row, col) => {
                return commonUtil.isHideBySpan(row, col, sheet)
              }
              this.everyCellWithPropertyOrWithControls((row, col) => {
                let cellData = {}
                if (isHideBySpan(row, col)) {
                  return
                }

                let attributeCode = getAttr(row, col)
                let cellId = cellIdManager.getCellId(row, col)
                if (cellId) {
                  setCellId(cellData, cellId)
                } else {
                  setCellId(cellData, innerCellIdManager.getInnerCellId(row, col))
                }

                let registeredAttr = attributeCode && this.operationalAttributesMap[attributeCode]
                if (registeredAttr && registeredAttr.getValue) {
                  setCellValue(cellData, registeredAttr.getValue(row, col, sheet))
                } else {
                  setCellValue(cellData, sheet.getValue(row, col))
                }
                setCellAttributeCode(cellData, attributeCode)
                setCellRow(cellData, row)
                setCellCol(cellData, col)

                if (!config.configMode) {
                  let extraData = cellPropertyDataManager.getExtraData(row, col)
                  setCellCode(cellData, extraData != null ? extraData : "")
                  setCellChangeType(cellData, cellChangedManager.getCellChangeType(cellId))
                }

                let cellKey = getCellKey(row, col)

                let controls = []
                if (this.cellControlsMap[cellKey]) {
                  this.cellControlsMap[cellKey].forEach(control => {
                    let targetControl = {
                      type: control.type,
                      rowId: control.targetRow,
                      colId: control.targetCol,
                      areaNumber: this.areaNumberMap[control.targetAreaId]
                    }
                    if (!isHideBySpan(targetControl.rowId, targetControl.colId)) {
                      controls.push(targetControl)
                    }
                  })
                }

                setControls(cellData, controls)

                if (!localUse && attributeCode) {// 本地使用不用设置关联单元格
                  setLinkCells(cellData, this.getLinkedCells(row, col, sheet).map(({
                    row: lRow,
                    col: lCol
                  }) => {
                    let linkCellData = {}
                    let cellId = cellIdManager.getCellId(lRow, lCol)
                    if (cellId) {
                      setLinkCellId(linkCellData, cellId)
                    } else {
                      setLinkCellId(linkCellData, innerCellIdManager.getInnerCellId(row, col))
                    }
                    return linkCellData
                  }))
                }
                // 增加原始数据的补充
                let oriCellData = cellUrlTriggerUtil.getMergedCellData(cellId)
                if (cellId && oriCellData) {
                  cellData = {
                    ...oriCellData,
                    ...cellData
                  }
                }
                cells.push(cellData)
              })

              let areas = this._getAreaList()

              return {
                cells,
                areas,
                extraObject: config.configMode ? undefined : JSON.parse(this.originalData.extraObjectString)
              }
            },
            getTemplateData(localUse) {
              let spread = getSpread()
              // 暂停渲染
              spread.suspendPaint()
              let endDoInNormalStyle = this.doInNormalStyle()

              let endTempRevertAllCell = null
              if (!localUse) {
                // 正常情况下需要临时还原属性的额外影响
                endTempRevertAllCell = this.tempRevertAllCell()
              }

              let ssjsonData = spread.toJSON()

              if (this._revertSsjson) {
                this._revertSsjson(ssjsonData)
              } else {
                // 清理保护信息
                ssjsonUtil.clearCellLock(ssjsonData)
              }
              let ssjsonDataString = JSON.stringify(ssjsonData)

              if (endTempRevertAllCell) {
                endTempRevertAllCell()
              }
              endDoInNormalStyle()
              // 恢复渲染
              spread.resumePaint()
              return {
                ssjsonDataString,
                billData: this.getBillData(localUse)
              }
            },
            _getAreaList() {
              let areaEq = (a, b) => {
                return a.leftUp == b.leftUp && a.rightDown == b.rightDown
              }
              let configMode = this.getConfig().configMode
              let cache = {}
              let areas = this.areaData.map(({
                x,
                y,
                width,
                height,
                type,
                id,
                originId
              }) => {
                let area = {
                  leftUp: `${x},${y}`,
                  leftDown: `${x},${y + height - 1}`,
                  rightUp: `${x + width - 1},${y}`,
                  rightDown: `${x + width - 1},${y + height - 1}`,
                  type,
                  number: this.areaNumberMap[id]
                }
                // 继承区域原始数据
                if (originId && this.oriAreaMap[originId]) {
                  area = {
                    ...this.oriAreaMap[originId],
                    ...area
                  }
                }
                if (!configMode) {
                  if (originId && this.oriAreaMap[originId]) {
                    cache[originId] = true
                    area.changeType = areaEq(area, this.oriAreaMap[originId]) ? BillEntityChangeTypeEnum.NO_CHANGE : BillEntityChangeTypeEnum.UPDATE
                  } else {
                    area.changeType = BillEntityChangeTypeEnum.INSERT
                  }
                }
                return area
              })
              if (!configMode) {
                for (const areaOriId in this.oriAreaMap) {
                  if (!cache[areaOriId]) {
                    let oriArea = copyDataSimple(this.oriAreaMap[areaOriId])
                    oriArea.changeType = BillEntityChangeTypeEnum.DELETE
                    areas.push(oriArea)
                  }
                }
              }
              return areas
            },
            exportExcelBlobBySsjson(ssjsonData) {
              return new Promise((resolve, reject) => {
                const excelIo = new GC.Spread.Excel.IO()
                excelIo.save(ssjsonData, (data) => {
                  let mimeType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
                  resolve(new Blob([data], { type: mimeType }))
                }, (error) => {
                  reject(error)
                })
              })
            },
            exportPdfBlobBySsjson(ssjsonData, printInfo) {
              return new Promise((resolve, reject) => {
                const tempSpread = new Sheets.Workbook(document.createElement("div"))
                let revertFn = this.initSsjsonData(ssjsonData)
                tempSpread.fromJSON(ssjsonData)
                revertFn(ssjsonData)
                if (printInfo) {
                  tempSpread.getSheet(0).printInfo(printInfo)
                }
                tempSpread.savePDF(function (blob) {
                  resolve(blob)
                }, function (ex) {
                  reject(ex)
                }, undefined, 0)
              })
            },
            setExportingStyle(sheet) {
              let revertFns = []
              this.everyCellWithProperty((row, col) => {
                let propertyKey = this.getPropertyKey(row, col)
                let registeredAttr = this.operationalAttributesMap[propertyKey]
                if (registeredAttr && registeredAttr.setExportingStyle) {
                  revertFns.unshift(registeredAttr.setExportingStyle(innerCellIdManager.getInnerCellId(row, col), sheet))
                }
              })
              return () => {
                revertFns.forEach(v => v())
              }
            },
            async exportExcelBlob() {
              if (GC.Spread.Excel != null && GC.Spread.Excel.IO != null) {
                let ssjsonData = this.getExportJSON()
                return await this.exportExcelBlobBySsjson(ssjsonData)
              } else {
                console.error(`如果想使用导出excel功能，请引入gc.spread.excelio.${spreadVersion}.min.js`)
              }
            },
            async exportPdfBlob(printInfo) {
              if (getSpread().savePDF == null || getSpread().print == null) {
                console.error(`如果想使用导出pdf功能，请引入gc.spread.sheets.print.${spreadVersion}.min.js和gc.spread.sheets.pdf.${spreadVersion}.min.js`)
                return
              }
              let ssjsonData = this.getExportJSON()
              return await this.exportPdfBlobBySsjson(ssjsonData, printInfo)
            },
            getExportJSON(options) {
              let spread = getSpread()
              // 暂停渲染
              spread.suspendPaint()
              let revertFns = []
              revertFns.unshift(this.doInNormalStyle())
              revertFns.unshift(this.tempRevertAllCell())
              revertFns.unshift(this.setExportingStyle(spread.getActiveSheet()))
              try {
                let ssjsonData = spread.toJSON(options || {})
                if (this._revertSsjson) {
                  this._revertSsjson(ssjsonData)
                } else {
                  // 清理保护信息
                  ssjsonUtil.clearCellLock(ssjsonData)
                }
                return ssjsonData
              } finally {
                revertFns.forEach(v => v())
                // 恢复渲染
                spread.resumePaint()
              }
            },
            isLinkCell(rowIndex, columnIndex) {
              let attr = this.getProperty(rowIndex, columnIndex)
              return !!attr && attr.linkAttr == "1"
            },
            // 获取关联单元格
            getLinkedCells(rowIndex, columnIndex, sheet) {
              // if (this.isLinkCell(rowIndex, columnIndex)) {
              //   return []
              // }
              // 查找隔离区域
              let areaInfo = this.findAreaInfo(rowIndex, columnIndex, 1, 1, false, AreaTypeEnum.SEGREGATE)
              // 没有则使用工作簿整个范围作为默认隔离区域
              if (!areaInfo) {
                areaInfo = {
                  x: 0,
                  y: 0,
                  width: sheet.getColumnCount(),
                  height: sheet.getRowCount()
                }
              }
              let {
                x,
                y,
                width,
                height
              } = areaInfo
              let rowLinkCells = []
              let tempMap = {}
              let checkAndPush = (row, col) => {
                let key = getCellKey(row, col)
                if (!tempMap[key]) {
                  tempMap[key] = true
                  if (this.isLinkCell(row, col)) {
                    rowLinkCells.push({
                      row,
                      col
                    })
                  }
                }
              }
              let getRealPosition = (row, col) => {
                let spanInfo = sheet.getSpan(row, col)
                if (spanInfo) {
                  return [spanInfo.row, spanInfo.col]
                } else {
                  return [row, col]
                }
              }
              for (let col = x; col <= x + width; col++) {
                checkAndPush(...getRealPosition(rowIndex, col))
              }
              let columnLinkCells = []
              for (let row = y; row < y + height; row++) {
                checkAndPush(...getRealPosition(row, columnIndex))
              }
              return [].concat(rowLinkCells, columnLinkCells)
            },
            clearInnerData() {
              this.cellPropertyData = []
              this.areaData = []
              this.controlData = []
              innerCellIdManager.clearAllData()
              cellChangedManager.clearAllData()
              cellIdManager.clearAllData()
            },
            getInnerData() {
              return [
                this.cellPropertyData,
                this.areaData,
                this.controlData,
                innerCellIdManager.getAllData(),
                cellChangedManager.getAllData(),
                cellIdManager.getAllData()
              ]
            },
            setInnerData(data) {
              this.cellPropertyData = data[0]
              this.areaData = data[1]
              this.controlData = data[2]
              innerCellIdManager.setAllData(data[3])
              cellChangedManager.setAllData(data[4])
              cellIdManager.setAllData(data[5])
            },
            installExtraShowStyle(sheet) {
              // 设置区域单元格的边框样式
              let setAreaBorder = (() => {
                let markName = "_bill_module_"
                let newBorderStyle = (color) => {
                  let lineStyle = new Sheets.LineBorder(color, Sheets.LineStyle.thick)
                  lineStyle[markName] = true
                  return lineStyle
                }
                let addBorderStyle = (cell, borderName, colorObj) => {
                  let border = cell[borderName]()
                  if (border == null || !border[markName]) {
                    cell[borderName](newBorderStyle(colorObj.originColor ? colorObj.originColor : commonUtil.colorObjToString(colorObj)))
                    return () => {
                      cell[borderName](border)
                    }
                  } else {
                    let mergedColorObj = commonUtil.mergeColors([commonUtil.parseRgbString(border.color), colorObj])
                    border.color = commonUtil.toRgbaString(mergedColorObj)
                    cell[borderName](border)
                  }
                }
                return (areas, colorObj) => {
                  let recovers = []
                  areas.forEach(({ x, y, width, height }) => {
                    commonUtil.everyCell(y, x, 1, width, (row, col) => {
                      let endAdd = addBorderStyle(sheet.getCell(row, col), "borderTop", colorObj)
                      if (endAdd) {
                        recovers.unshift(endAdd)
                      }
                    })
                    commonUtil.everyCell(y, x + width - 1, height, 1, (row, col) => {
                      let endAdd = addBorderStyle(sheet.getCell(row, col), "borderRight", colorObj)
                      if (endAdd) {
                        recovers.unshift(endAdd)
                      }
                    })
                    commonUtil.everyCell(y + height - 1, x, 1, width, (row, col) => {
                      let endAdd = addBorderStyle(sheet.getCell(row, col), "borderBottom", colorObj)
                      if (endAdd) {
                        recovers.unshift(endAdd)
                      }
                    })
                    commonUtil.everyCell(y, x, height, 1, (row, col) => {
                      let endAdd = addBorderStyle(sheet.getCell(row, col), "borderLeft", colorObj)
                      if (endAdd) {
                        recovers.unshift(endAdd)
                      }
                    })
                  })
                  return () => {
                    recovers.forEach(v => v())
                  }
                }
              })()

              let cachedParseRgbString = commonUtil.getCachedFn(commonUtil.parseRgbString)
              let setNormalAreaBorder = () => {
                let unbinds = []
                let normalAreas = this.areaData.filter(v => !this.highlightAreasMap[v.id])
                commonUtil.getEnumConstants(AreaTypeEnum).forEach(areaType => {
                  unbinds.unshift(
                    setAreaBorder(
                      normalAreas.filter(v => v.type == areaType),
                      cachedParseRgbString(AreaTypeEnum.properties[areaType].borderColor)
                    )
                  )
                })

                return () => {
                  unbinds.forEach(v => v())
                }
              }

              let setHighlightAreaBorder = () => {
                let highlightAreaBorderColor = "rgba(128, 0, 128, 0.7)"
                let highlightAreaBorderColorObj = commonUtil.parseRgbString(highlightAreaBorderColor)
                return setAreaBorder(this.areaData.filter(v => this.highlightAreasMap[v.id]), highlightAreaBorderColorObj)
              }

              let showMode = () => this.isShowMode && this.getConfig().configMode

              let endSetHighlightAreaBorder = null
              let endSetNormalAreaBorder = null

              // 清除所有区域背景色设置
              let clearAreaBorder = () => {
                if (endSetNormalAreaBorder) {
                  endSetNormalAreaBorder()
                  endSetNormalAreaBorder = null
                }
                if (endSetHighlightAreaBorder) {
                  endSetHighlightAreaBorder()
                  endSetHighlightAreaBorder = null
                }
              }
              // 重置所有区域背景色
              let resetAreaBorder = () => {
                clearAreaBorder()
                if (showMode()) {
                  endSetNormalAreaBorder = setNormalAreaBorder()
                }
                endSetHighlightAreaBorder = setHighlightAreaBorder()
              }

              // 设置查看模式下单元格的背景色
              let setShowModeBackColor = () => {
                let layerName = "_show_mode_"
                let propertyBackColorObj = commonUtil.parseRgbString("rgba(0,0,255,0.3)")
                let ctrlBackColorObjA = commonUtil.parseRgbString("rgba(128,255,0,0.3)")
                let ctrlBackColorObjB = commonUtil.parseRgbString("rgba(0,128,255,0.3)")
                let addBackColors = () => {
                  // 设置存在顺序设置的单元格的背景颜色
                  for (const key in this.cellControlsMap) {
                    const { sourceRow, sourceCol } = this.cellControlsMap[key][0]
                    if (this.getAllCellOrderControls(sourceRow, sourceCol)) {
                      backColorLayerManager.unshiftRgbaBackColor(sourceRow, sourceCol, layerName, ctrlBackColorObjA)
                    }
                    if (this.getAllSettingDefaultValueControls(sourceRow, sourceCol)) {
                      backColorLayerManager.unshiftRgbaBackColor(sourceRow, sourceCol, layerName, ctrlBackColorObjB)
                    }
                  }

                  // 设置属性单元格的背景颜色
                  this.everyCellWithProperty((row, col) => {
                    backColorLayerManager.unshiftRgbaBackColor(row, col, layerName, propertyBackColorObj)
                  })
                }
                addBackColors()
                backColorLayerManager.setBackColorToSheet(sheet)
                return {
                  refreshColorLayerData: () => {// 此方法仅用于刷新数据，不会讲数据更新到页面上，因为更新数据的方式因具体情况而异
                    backColorLayerManager.removeAllRgbaBackColor(layerName)
                    addBackColors()
                  },
                  endSetShowModeBackColor: () => {
                    backColorLayerManager.removeAllRgbaBackColor(layerName)
                    backColorLayerManager.setBackColorToSheet(sheet)
                  }
                }
              }
              let showModeBackColorManager = null
              let clearShowModeBackColor = () => {
                if (showModeBackColorManager) {
                  showModeBackColorManager.endSetShowModeBackColor()
                  showModeBackColorManager = null
                }
              }
              let resetShowModeBackColor = () => {
                clearShowModeBackColor()
                if (showMode()) {
                  showModeBackColorManager = setShowModeBackColor()
                }
              }

              // 设置高亮单元格的背景色
              let setHighlightCellBackColor = () => {
                let layerName = "_highlight_cell_"
                let highlightCellBackColorObj = commonUtil.parseRgbString("rgba(255,128,0,0.6)")
                let cachedParseRgbString = commonUtil.getCachedFn(commonUtil.parseRgbString)

                let addBackColors = () => {
                  this.highlightCells && this.highlightCells.forEach(([id, color]) => {
                    let [row, col] = innerCellIdManager.getCellPosition(id)
                    let colorObj = color ? cachedParseRgbString(color) : highlightCellBackColorObj
                    backColorLayerManager.pushRgbaBackColor(row, col, layerName, colorObj)
                  })
                }
                addBackColors()
                backColorLayerManager.setBackColorToSheet(sheet)
                return {
                  refreshColorLayerData: () => {// 此方法仅用于刷新数据，不会讲数据更新到页面上，因为更新数据的方式因具体情况而异
                    backColorLayerManager.removeAllRgbaBackColor(layerName)
                    addBackColors()
                  },
                  endSetHighlightCellBackColor: () => {
                    backColorLayerManager.removeAllRgbaBackColor(layerName)
                    backColorLayerManager.setBackColorToSheet(sheet)
                  }
                }
              }

              sheet.suspendPaint()
              resetAreaBorder()
              resetShowModeBackColor()
              sheet.resumePaint()
              let highlightCellBackColorManager = setHighlightCellBackColor()

              let endWatchShowMode = this.$watch(() => showMode(), () => {
                sheet.suspendPaint()
                resetAreaBorder()
                resetShowModeBackColor()
                highlightCellBackColorManager.endSetHighlightCellBackColor()
                backColorLayerManager.setBackColorToSheet(sheet)
                sheet.resumePaint()
              })

              let isInTemporaryRemoveAllExtraShowStyle = false

              this.extraShowStyleManager_ = {
                refresh() {
                  sheet.suspendPaint()

                  // 刷新查看模式背景色数据
                  if (showModeBackColorManager) {
                    showModeBackColorManager.refreshColorLayerData()
                  }
                  // 刷新高亮单元格背景色数据
                  highlightCellBackColorManager.refreshColorLayerData()
                  // 如果没有处于临时移除所有额外样式的状态，将更新的颜色数据设置到表格中
                  if (!isInTemporaryRemoveAllExtraShowStyle) {
                    backColorLayerManager.setBackColorToSheet(sheet)
                    resetAreaBorder()
                  }

                  sheet.resumePaint()
                },
                temporaryRemoveAllExtraShowStyle(refresh) {
                  if (isInTemporaryRemoveAllExtraShowStyle) {
                    return () => { }
                  } else {
                    isInTemporaryRemoveAllExtraShowStyle = true
                  }
                  sheet.suspendPaint()
                  // 单元格背景颜色的还原由背景颜色管理器统一还原，这样外部设置的额外背景色也能还原
                  let doBack = backColorLayerManager.tempToOriBackColor(sheet)
                  // 还原区域边框颜色
                  clearAreaBorder()
                  sheet.resumePaint()
                  return () => {
                    sheet.suspendPaint()
                    if (refresh) {
                      // 刷新查看模式背景色数据
                      if (showModeBackColorManager) {
                        showModeBackColorManager.refreshColorLayerData()
                      }
                      // 刷新高亮单元格背景色数据
                      highlightCellBackColorManager.refreshColorLayerData()
                    }
                    // 恢复单元格背景颜色
                    doBack()
                    // 恢复区域边框颜色
                    resetAreaBorder()
                    sheet.resumePaint()
                    isInTemporaryRemoveAllExtraShowStyle = false
                  }
                }
              }

              return () => {
                endWatchShowMode()
                clearAreaBorder()
                clearShowModeBackColor()
                delete this.extraShowStyleManager_
              }
            },
            refreshExtraShowStyle() {
              this.extraShowStyleManager_ && this.extraShowStyleManager_.refresh()
            },
            doInNormalStyle(refresh) {
              let endNormalStyle = null
              if (this.extraShowStyleManager_) {
                endNormalStyle = this.extraShowStyleManager_.temporaryRemoveAllExtraShowStyle(refresh)
              }
              return () => {
                if (endNormalStyle) {
                  endNormalStyle()
                }
              }
            },
            updateSheetFromChangedPropertyData(sheet) { // 根据变化的属性更新表格内容
              // 根据属性变化处理属性对表格的额外操作
              getPropChangeManager(sheet).everyChange((
                innerCellId,
                {
                  oldProperty: oldAttr,
                  newProperty: newAttr,
                }
              ) => {
                let oldRegisteredAttr = this.operationalAttributesMap[oldAttr]
                let newRegisteredAttr = this.operationalAttributesMap[newAttr]

                let needRemoveOldAttrEffect = !!(oldRegisteredAttr)
                let needAddNewAttrEffect = !!(newRegisteredAttr && newRegisteredAttr.initCellAttribute)

                let formatCellValue = (innerCellId, sheet, formatter) => {
                  let [row, col] = innerCellIdManager.getCellPosition(innerCellId)
                  let cell = sheet.getCell(row, col)
                  let curValue = cell.value()
                  let formattedValue = formatter(curValue, innerCellId, sheet)
                  if (curValue !== formattedValue) {
                    cell.value(formattedValue)
                  }
                }

                if (needRemoveOldAttrEffect) {
                  revertAttributeEffectManager.revertAttributeEffect(innerCellId, () => {
                    let removeCellAttribute = oldRegisteredAttr.initCellAttribute(innerCellId, sheet)
                    if (oldRegisteredAttr.formatter) {
                      formatCellValue(innerCellId, sheet, (...args) => oldRegisteredAttr.formatter(...args))
                    }
                    return removeCellAttribute
                  })
                }
                if (needAddNewAttrEffect) {
                  revertAttributeEffectManager.setAttributeEffect(innerCellId, () => {
                    let removeCellAttribute = newRegisteredAttr.initCellAttribute(innerCellId, sheet)
                    if (newRegisteredAttr.formatter) {
                      formatCellValue(innerCellId, sheet, (...args) => newRegisteredAttr.formatter(...args))
                    }
                    return removeCellAttribute
                  })
                }
              })
            },
            listenEditCellForFormat() {
              let spread = getSpread()
              let setText = (el, text) => {
                let lastElementChild = el
                while (lastElementChild.lastElementChild) {
                  lastElementChild = lastElementChild.lastElementChild
                }
                lastElementChild.innerText = text
              }
              let innerEditEnd = false
              let endEdit = (ignoreValueChange) => {
                innerEditEnd = true
                spread.getActiveSheet().endEdit(ignoreValueChange)
              }
              // 如果不是内部调用结束才进一步执行判断
              let checkInnerEditEndAndDo = (callback) => {
                if (!innerEditEnd) {
                  callback()
                } else {
                  // 这个值仅生效一次，每次内部结束都要重新设置
                  innerEditEnd = false
                }
              }

              let editEndingListener = (e, args) => {
                this.checkEditEndingCellReadOnly(args)
                if (args.cancel) {
                  return
                }
                let { editingText, row, col, sheet } = args
                checkInnerEditEndAndDo(() => {
                  if (editingText) {
                    let propKey = this.getPropertyKey(row, col)
                    if (propKey) {
                      let regAttr = this.operationalAttributesMap[propKey]
                      if (regAttr && regAttr.formatter && !this.isCellDisabled(row, col)) {
                        let innerCellId = innerCellIdManager.getInnerCellId(row, col)
                        let formattedValue = regAttr.formatter(editingText, innerCellId, sheet)
                        if (!formattedValue || editingText !== formattedValue.toString()) {
                          args.editingText = formattedValue ? formattedValue.toString() : formattedValue
                          let { editor } = args
                          setText(editor, args.editingText)
                          args.cancel = true
                          setTimeout(() => {
                            endEdit()
                          }, 0)
                        }
                      }
                    }
                  }
                })

              }
              spread.bind(Sheets.Events.EditEnding, editEndingListener)
              return () => {
                spread.unbind(Sheets.Events.EditEnding, editEndingListener)
              }
            },
            listenSelectionChangedForShowHighlightCellsAndAreas() {
              let spread = getSpread()

              let preHighlightCells = null
              let preHighlightAreas = null

              let innerChange = false

              let endWatch = this.$watch(() => {
                return JSON.stringify(this.highlightCells) + "_" + JSON.stringify(this.highlightAreas)
              }, () => {
                if (!innerChange) {
                  preHighlightCells = null
                  preHighlightAreas = null
                }
              })

              let listener = getLogErrorFn((e, info) => {
                setTimeout(() => {
                  innerChange = true
                  this.curSelections = copyDataSimple(info.newSelections)
                  let curHighlightCells = JSON.stringify(this.highlightCells)
                  let curHighlightAreas = JSON.stringify(this.highlightAreas)
                  if (preHighlightCells != curHighlightCells || preHighlightAreas != curHighlightAreas) {
                    this.refreshExtraShowStyle()
                  }
                  preHighlightCells = curHighlightCells
                  preHighlightAreas = curHighlightAreas
                  innerChange = false
                }, 0)
              })
              spread.bind(Sheets.Events.SelectionChanged, listener)
              return () => {
                spread.unbind(Sheets.Events.SelectionChanged, listener)
                endWatch()
              }
            },
            repairBasicCommand() {
              let spread = getSpread()
              let unbinds = []

              // 用于存储上一个命令的名字
              let lastCmd = null
              // 粘贴时, 用于判断是否从复制开始, 然后修正由于自动样式设置而消失的虚框
              let copyStart = false

              // 表示当前正在执行的命令(标记过的命令，不是所有)数量，
              // 一可以防止异步执行的命令破坏代码执行顺序，
              // 二可以用于表示当前命令是否为标记命令
              let executingCommandCount = 0

              let getSheet = (context, options) => {
                let sheetName = commonUtil.getSheetNameFromCommandOptions(options)
                return sheetName ? context.getSheetFromName(sheetName) : context.getActiveSheet()
              }

              let backStyle = (context, options, isUndo, cmd) => {
                executingCommandCount++
                let endDoInNormalStyle = null
                if (executingCommandCount == 1) {
                  // 暂停渲染
                  context.suspendPaint()
                  endDoInNormalStyle = this.doInNormalStyle(true)
                }
                return () => {
                  if (executingCommandCount == 1) {
                    endDoInNormalStyle()
                    // 恢复渲染
                    context.resumePaint()
                  }
                  executingCommandCount--
                }
              }

              // 修正因为样式修改导致的复制后指示框消失
              // WARN 可能会因为更换版本导致bug
              let showCutCopyIndicator = (context, options) => {
                let sheet = getSheet(context, options)
                try {
                  // VERSION_CHECK 按照复制内容重绘
                  sheet.at.BJ(true)
                } catch (error) {
                  console.error(error)
                  console.error(VERSION_CHECK_ERROR)
                }
              }

              // 处理粘贴命令的异步问题与ctrl+v的按键
              let listeners = []
              let exeAndRemoveListeners = () => {
                for (let i = 0; i < listeners.length; i++) {
                  const fn = listeners[i]
                  fn()
                  listeners.splice(i, 1)
                  i--
                }
              }

              let el = spread.getHost()
              el.addEventListener("paste", exeAndRemoveListeners)

              let isCtrlV = false
              let keydownListener = (e) => {
                if (e.keyCode === 86 && e.ctrlKey) {
                  isCtrlV = true
                  // 这里只是简单恢复了一下，没有考虑一直按着的情况，因为长时间按逻辑上不会出bug，所以暂时不处理
                  setTimeout(() => {
                    isCtrlV = false
                  }, 100)
                }
              }
              el.addEventListener("keydown", keydownListener)

              unbinds.unshift(() => {
                el.removeEventListener("paste", exeAndRemoveListeners)
                el.removeEventListener("keydown", keydownListener)
              })

              // 包装一些原生命令处理样式bug和属性的复制等操作
              let fixCommands = [
                {
                  cmd: "clearValues",
                  checkCommandNeedCancel: (context, options) => {
                    let cancelCommand = false
                    let { ranges } = options
                    commonUtil.executeFnUseForceEnd((forceEnd) => {
                      ranges.forEach(range => {
                        commonUtil.everyCell(range.row, range.col, range.rowCount, range.colCount, (row, col) => {
                          if (cellUrlTriggerUtil.hasTriggerUrl(row, col)) {
                            cancelCommand = true
                            forceEnd()
                          }
                        })
                      })
                    })
                    return cancelCommand
                  }
                },
                {
                  cmd: "editCell"
                },
                {
                  cmd: "fill",
                  extraEffectInCommand: (() => {
                    const {
                      down,
                      left,
                      right,
                      up
                    } = Sheets.Fill.FillDirection
                    const {
                      auto,
                      copyCells
                    } = Sheets.Fill.AutoFillType

                    // 获取作为像刷子一样的复制源
                    let getBrushRange = (fillDirection, startRange) => {
                      let {
                        row: startRowIndex,
                        col: startColIndex,
                        rowCount: startRowCount,
                        colCount: startColCount
                      } = startRange
                      if (fillDirection == down) {
                        return {
                          row: startRowIndex + startRowCount - 1,
                          col: startColIndex,
                          rowCount: 1,
                          colCount: startColCount
                        }
                      } else if (fillDirection == up) {
                        return {
                          row: startRowIndex,
                          col: startColIndex,
                          rowCount: 1,
                          colCount: startColCount
                        }
                      } else if (fillDirection == right) {
                        return {
                          row: startRowIndex,
                          col: startColIndex + startColCount - 1,
                          rowCount: startRowCount,
                          colCount: 1
                        }
                      } else if (fillDirection == left) {
                        return {
                          row: startRowIndex,
                          col: startColIndex,
                          rowCount: startRowCount,
                          colCount: 1
                        }
                      }
                    }
                    // 将复制源复制按某个方向连续复制到目标单元格内
                    let fillProp = (range, fillDirection, fillRange, sheet) => {
                      let {
                        row: fillRowIndex,
                        col: fillColIndex,
                        rowCount: fillRowCount,
                        colCount: fillColCount
                      } = fillRange
                      let tempPropClipboard = customGetPropClipboard()
                      tempPropClipboard.copyProp([range])
                      if (fillDirection == down || fillDirection == up) {
                        for (let i = fillRowIndex; i < fillRowIndex + fillRowCount; i++) {
                          let targetRange = {
                            row: i,
                            col: fillColIndex,
                            rowCount: 1,
                            colCount: fillColCount
                          }
                          tempPropClipboard.pasteProp([targetRange], sheet)
                        }
                      } else {
                        for (let i = fillColIndex; i < fillColIndex + fillColCount; i++) {
                          let targetRange = {
                            row: fillRowIndex,
                            col: i,
                            rowCount: fillRowCount,
                            colCount: 1
                          }
                          tempPropClipboard.pasteProp([targetRange], sheet)
                        }
                      }
                      tempPropClipboard.clear()
                    }

                    return (context, options, isUndo) => {
                      let {
                        startRange,
                        fillRange,
                        fillDirection,
                        autoFillType
                      } = options
                      if (autoFillType == auto || autoFillType == copyCells) {
                        let sheet = getSheet(context, options)
                        // 获取作为像刷子一样的复制源
                        let range = getBrushRange(fillDirection, startRange)
                        // 将复制源复制按某个方向连续复制到目标单元格内
                        fillProp(range, fillDirection, fillRange, sheet)
                      }
                    }
                  })(),
                },
                // 因技术原因及时间原因（增加了control的操作）allowUserDragDrop已禁用，所以暂时注释以下代码
                // {
                //   cmd: "dragDrop",
                //   extraEffectInCommand: (context, options, isUndo) => {
                //     let {
                //       toRow,
                //       toColumn,
                //       fromRow,
                //       fromColumn,
                //       rowCount,
                //       columnCount
                //     } = options
                //     let fromRange = {
                //       row: fromRow,
                //       col: fromColumn,
                //       rowCount: rowCount,
                //       colCount: columnCount
                //     }
                //     let toRange = {
                //       row: toRow,
                //       col: toColumn,
                //       rowCount: rowCount,
                //       colCount: columnCount
                //     }
                //     let sheet = getSheet(context, options)
                //     let tempPropClipboard = customGetPropClipboard()
                //     tempPropClipboard.cutProp([fromRange])
                //     tempPropClipboard.pasteProp([toRange], sheet)
                //   },
                // },
                {
                  cmd: "copy",
                  afterCommand: (context, options) => {
                    propClipboard.copyProp(getSheet(context, options).getSelections())
                  },
                  afterResetStyle: (context, options) => {
                    showCutCopyIndicator(context, options)
                  }
                },
                {
                  cmd: "cut",
                  // 如果选择范围内存在需要调用接口的单元格则禁止剪切
                  checkCommandNeedCancel: (context, options) => {
                    let cancelCommand = false
                    let sheet = getSheet(context, options)
                    let ranges = sheet.getSelections()
                    commonUtil.executeFnUseForceEnd((forceEnd) => {
                      ranges.forEach(range => {
                        commonUtil.everyCell(range.row, range.col, range.rowCount, range.colCount, (row, col) => {
                          if (cellUrlTriggerUtil.hasTriggerUrl(row, col)) {
                            cancelCommand = true
                            forceEnd()
                          }
                        })
                      })
                    })
                    return cancelCommand
                  },
                  afterCommand: (context, options) => {
                    propClipboard.cutProp(getSheet(context, options).getSelections())
                  },
                  afterResetStyle: (context, options) => {
                    showCutCopyIndicator(context, options)
                  }
                },
                {
                  cmd: "clipboardPaste",
                  checkCommandNeedCancel: (context, options) => {
                    let cancelCommand = false
                    let sheet = getSheet(context, options)
                    let ranges = options.pastedRanges ? options.pastedRanges : sheet.getSelections()
                    commonUtil.executeFnUseForceEnd((forceEnd) => {
                      ranges.forEach(range => {
                        commonUtil.everyCell(range.row, range.col, range.rowCount, range.colCount, (row, col) => {
                          if (cellUrlTriggerUtil.hasTriggerUrl(row, col)) {
                            cancelCommand = true
                            forceEnd()
                          }
                        })
                      })
                    })
                    return cancelCommand
                  },
                  extraEffectInCommand: (context, options, isUndo) => {
                    // 当剪切板被外界复制更新后，单元格属性剪贴板要清空
                    if ((!options.fromRanges || !options.fromSheet) && !options.clipboardHtml) {
                      propClipboard.clear()
                    } else if (options.pasteOption == Sheets.ClipboardPasteOptions.all) {
                      let sheet = getSheet(context, options)
                      let pastedRanges = options.pastedRanges ? options.pastedRanges : sheet.getSelections()
                      if (!options.propClipboardInfo) {
                        options.propClipboardInfo = propClipboard.getPropClipboardInfo()
                      }

                      customGetPropClipboard(options.propClipboardInfo).pasteProp(pastedRanges, sheet)
                    }
                  },
                  afterResetStyle(context, options, isUndo) {
                    if (lastCmd == "copy") { // 如果上一个命令是复制，则开始给虚框续命
                      copyStart = true
                    } else if (lastCmd != "clipboardPaste" || isUndo) { // 如果上一个命令不是粘贴，则结束给虚框续命
                      copyStart = false
                    }
                    lastCmd = "clipboardPaste"
                    if (copyStart) {
                      showCutCopyIndicator(context, options, isUndo)
                    }
                  },
                  // 如果是粘贴命令则需要延迟执行恢复样式动作
                  customFinalExecute(finalExe, [context, options, isUndo]) {
                    if (!isUndo) {
                      listeners.push(finalExe)
                      // 如果不是键盘ctrl+v粘贴，则100毫秒后执行
                      if (!isCtrlV) {
                        setTimeout(() => {
                          exeAndRemoveListeners()
                        }, 100)
                      }
                    } else {
                      finalExe()
                    }
                  }
                }
              ]
              let commandManager = spread.commandManager()

              let findCommandObj = (regCommand) => {
                for (const k1 in regCommand) {
                  const v1 = regCommand[k1]
                  if (typeof v1 == "object" && Object.keys(v1).some(key => key == "execute")) {
                    return v1
                  }
                }
              }
              fixCommands.forEach(({
                cmd,
                checkCommandNeedCancel,
                extraEffectInCommand,
                afterCommand,
                afterResetStyle,
                customFinalExecute
              }) => {
                let commandObj = findCommandObj(commandManager[cmd])
                if (commandObj) {
                  let revertExecute = wrapFn(commandObj, "execute", function (oriFn, args) {
                    var Commands = Sheets.Commands
                    let [context, options, isUndo] = args
                    let resetStyle = null
                    let cancelCommand = false
                    try {
                      let rs = null
                      if (window.debug) {
                        console.log(cmd, args)
                      }
                      args.push(cmd)
                      if (commandObj.canUndo) {
                        if (!isUndo) {
                          if (checkCommandNeedCancel) {
                            cancelCommand = checkCommandNeedCancel(context, options)
                          }

                          if (!cancelCommand) {
                            // 暂停渲染
                            context.suspendPaint()
                            // startTransaction已经包含了backStyle的功能
                            Commands.startTransaction(context, options, () => {
                              // if (beforeCommand) {
                              //   beforeCommand(context, options)
                              // }
                            })
                            try {
                              rs = oriFn()
                              if (extraEffectInCommand) {
                                extraEffectInCommand(...args)
                              }
                            } catch (error) {
                              throw error
                            } finally {
                              Commands.endTransaction(context, options, () => {
                                if (afterCommand) {
                                  afterCommand(context, options)
                                }
                              })
                              // 恢复渲染
                              context.resumePaint()
                            }
                          }
                        } else {
                          Commands.undoTransaction(context, options, () => {
                            // if (beforeCommand) {
                            //   beforeCommand(context, options)
                            // }
                          }, () => {
                            if (afterCommand) {
                              afterCommand(context, options)
                            }
                          })
                          rs = true
                        }
                      } else {
                        if (checkCommandNeedCancel) {
                          cancelCommand = checkCommandNeedCancel(context, options)
                        }

                        if (!cancelCommand) {
                          if (getUndoManager().getCommandDeep(context.getSheetFromName(commonUtil.getSheetNameFromCommandOptions(options))) === 0) {
                            resetStyle = backStyle(...args)
                          }
                          rs = oriFn()

                          if (afterCommand) {
                            afterCommand(...args)
                          }
                        }
                      }
                      return rs
                    } catch (error) {
                      console.error(error)
                      throw error
                    } finally {
                      let finalExe = () => {
                        if (commandObj.canUndo) {
                          if (isUndo || !cancelCommand) {
                            if (afterResetStyle) {
                              afterResetStyle(...args)
                            }
                          }
                        } else {
                          if (!cancelCommand) {
                            if (getUndoManager().getCommandDeep(context.getSheetFromName(commonUtil.getSheetNameFromCommandOptions(options))) === 0) {
                              resetStyle(...args)
                              if (afterResetStyle) {
                                afterResetStyle(...args)
                              }
                            } else {
                              // 如果当前命令被包含在事务中，则设置一个稍后执行的方法
                              if (afterResetStyle) {
                                options.__afterResetStyle = () => {
                                  afterResetStyle(...args)
                                }
                              }
                            }
                          }
                        }
                      }
                      if (customFinalExecute) {
                        customFinalExecute(finalExe, args)
                      } else {
                        finalExe()
                      }
                    }
                  })
                  unbinds.unshift(() => {
                    revertExecute()
                  })
                }
              })

              let commandListener = (...args) => {
                if (args[0] && args[0].command) {
                  let command = args[0].command
                  if (command.cmd) {
                    //因为粘贴命令为非同步操作，此处lastCmd交由其他地方处理
                    if ("clipboardPaste" != command.cmd) {
                      lastCmd = command.cmd
                    }
                  } else if (command.copyData) {
                    lastCmd = "copy"
                  } else if (command.cutData) {
                    lastCmd = "cut"
                  } else if (Object.keys(command).length != 1) {
                    lastCmd = null
                  }
                }
                if (window.debug) {
                  console.log(args)
                }
              }
              commandManager.addListener("__", commandListener)
              unbinds.unshift(() => {
                commandManager.removeListener("__", commandListener)
              })

              return () => {
                unbinds.forEach(v => v())
              }
            },
            // 用于包装静态方法，多次初始化只会绑定一次， initWrapFunction会传入一个findThis方法来找到当前spread绑定的vue实例，
            // initWrapFunction内部禁止使用getSpread方法，因为getSpread永远返回的是整个程序运行过程中第一次绑定的spread
            // 除getSpread方法外，各种方法、对象也必须通过findThis到的vue实例对象间接取得
            // 如果不能把握到以上注释说明的目的，禁止使用wrapStaticFunction方法
            wrapStaticFunction(key, initWrapFunction) {
              let getWrapData = () => {
                return dataMapUtil.getData(key)
              }
              let hasWrapData = () => {
                return dataMapUtil.hasData(key)
              }
              let setWrapData = wrapData => {
                dataMapUtil.setData(key, wrapData)
              }
              let deleteWrapData = () => {
                dataMapUtil.deleteData(key)
              }
              let unwrapStaticFunction = () => {
                let wrapData = getWrapData()
                wrapData.wrapCount--
                wrapData.moduleMap.splice(wrapData.moduleMap.findIndex(v => {
                  return v[1] === this
                }), 1)
                if (wrapData.wrapCount == 0) {
                  wrapData.unwrapStaticFunction()
                  deleteWrapData()
                }
              }
              if (hasWrapData()) {
                let wrapData = getWrapData()
                wrapData.wrapCount++
                if (wrapData.moduleMap.findIndex(v => {
                  return v[0] === getSpread()
                }) != -1) {
                  throw "同一WorkBook实例禁止绑定多个BillSpreadModule"
                }
                wrapData.moduleMap.push([getSpread(), this])
                return unwrapStaticFunction
              }

              let findThis = context => {
                let pair = getWrapData().moduleMap.find(v => {
                  return v[0] === context
                })
                return pair && pair[1]
              }

              setWrapData({
                wrapped: true,
                wrapCount: 1,
                unwrapStaticFunction: initWrapFunction(findThis),
                moduleMap: [
                  [getSpread(), this]
                ]
              })

              return unwrapStaticFunction
            },
            getRevertAttributeEffectManager() {
              return revertAttributeEffectManager
            },
            // 包装表格内部事务
            wrapSheetTransaction(sheet) {
              // VERSION_CHECK
              let transactionManager = sheet.ITa
              if (!transactionManager) {
                console.error(VERSION_CHECK_ERROR)
                return
              }
              let this_ = this
              let dataForUndo = null
              let customPropertyName = "_custom_undo_data_"
              let revertStartTransaction = wrapFn(transactionManager, "startTransaction", (doOri, [extraBeforeCommand]) => {
                if (this_.getUndoManager().getCommandDeep(sheet) === 0) {
                  if (!getData(this_).endDoInNormalStyle) {
                    getData(this_).endDoInNormalStyle = this_.doInNormalStyle(true)
                  }
                  // 装载命令执行前的属性数据
                  getPropChangeManager(sheet).setOldData(this_.getInnerData()[0])
                  dataForUndo = {}
                }
                if (extraBeforeCommand) {
                  extraBeforeCommand()
                }

                // 备份内部数据，用于支持undo功能
                this_.getUndoManager().beforeCommandInSheet(sheet, dataForUndo)
                return doOri()
              })
              let revertEndTransaction = wrapFn(transactionManager, "endTransaction", (doOri, [extraAfterCommand]) => {
                if (this_.getUndoManager().getCommandDeep(sheet) === 1) {
                  sheet.suspendPaint()
                  // 装载命令执行后的属性数据
                  getPropChangeManager(sheet).setNewData(this_.getInnerData()[0])
                  // 根据属性更新表格内容
                  this_.updateSheetFromChangedPropertyData(sheet)
                }
                let events = doOri()
                this_.getUndoManager().afterCommandInSheet(sheet, dataForUndo)
                if (this_.getUndoManager().getCommandDeep(sheet) === 0) {
                  // 清理propChangeManage属性数据
                  getPropChangeManager(sheet).clear()

                  if (getData(this_).endDoInNormalStyle) {
                    getData(this_).endDoInNormalStyle()
                    delete getData(this_).endDoInNormalStyle
                  }
                  events[customPropertyName] = dataForUndo
                  dataForUndo = null
                  sheet.resumePaint()
                }
                if (extraAfterCommand) {
                  extraAfterCommand()
                }
                return events
              })
              let revertUndo = wrapFn(transactionManager, "undo", (doOri, [events, extraBeforeCommand, extraAfterCommand]) => {
                sheet.suspendPaint()
                let endDoInNormalStyle = null
                endDoInNormalStyle = this_.doInNormalStyle(true)
                if (extraBeforeCommand) {
                  extraBeforeCommand()
                }
                doOri()
                if (events[customPropertyName]) {
                  this_.getUndoManager().undoInSheet(sheet, events[customPropertyName])
                  delete events[customPropertyName]
                }
                if (extraAfterCommand) {
                  extraAfterCommand()
                }
                endDoInNormalStyle()
                sheet.resumePaint()
              })
              return () => {
                revertStartTransaction()
                revertEndTransaction()
                revertUndo()
              }
            },
            // 包装spread外部事务
            wrapSpreadTransaction() {
              return this.wrapStaticFunction("wrapSpreadTransaction", (findThis) => {
                let getSheet = (context, options) => {
                  let sheetName = commonUtil.getSheetNameFromCommandOptions(options)
                  return sheetName ? context.getSheetFromName(sheetName) : context.getActiveSheet()
                }
                let revertStartTransaction = wrapFn(Sheets.Commands, "startTransaction", (doOri, [context, options, extraBeforeCommand]) => {
                  let this_ = findThis(context)
                  let sheetName = commonUtil.getSheetNameFromCommandOptions(options)
                  // 目前仅支持一个工作簿
                  let canWrap = this_ && (context.getSheetIndex(sheetName) === 0 || !sheetName)
                  let isStart = false
                  let sheet = getSheet(context, options)
                  if (canWrap) {
                    if (this_.getUndoManager().getCommandDeep(sheet) === 0) {
                      isStart = true
                      if (!getData(this_).endDoInNormalStyle) {
                        getData(this_).endDoInNormalStyle = this_.doInNormalStyle(true)
                      }

                      everySheetName(context, options.sheetName, sheetName => {
                        // 装载命令执行前的属性数据
                        getPropChangeManager(context.getSheetFromName(sheetName)).setOldData(this_.getInnerData()[0])
                      })
                    }

                    if (extraBeforeCommand) {
                      extraBeforeCommand()
                    }

                    // 备份内部数据，用于支持undo功能
                    this_.getUndoManager().beforeCommand(context, options)
                    // 记录属性影响，用来实现属性影响监听的undo功能
                    this_.getRevertAttributeEffectManager().startTransaction(context, options)
                  }
                  doOri()
                  if (isStart) {
                    getData(this_).startedCommandDeep = this_.getUndoManager().getCommandDeep(sheet)
                  }
                })

                let revertEndTransaction = wrapFn(Sheets.Commands, "endTransaction", (doOri, [context, options, extraAfterCommand]) => {

                  let this_ = findThis(context)
                  let sheetName = commonUtil.getSheetNameFromCommandOptions(options)
                  // 目前仅支持一个工作簿
                  let canWrap = this_ && (context.getSheetIndex(sheetName) === 0 || !sheetName)
                  let sheet = getSheet(context, options)
                  let isBeforeFinalEnd = () => this_.getUndoManager().getCommandDeep(sheet) === getData(this_).startedCommandDeep
                  let isFinalEnd = () => this_.getUndoManager().getCommandDeep(sheet) === 0
                  if (canWrap) {
                    context.suspendPaint()
                    if (isBeforeFinalEnd()) {
                      everySheetName(context, options.sheetName, sheetName => {
                        // 装载命令执行后的属性数据
                        getPropChangeManager(context.getSheetFromName(sheetName)).setNewData(this_.getInnerData()[0])
                        // 根据属性更新表格内容
                        this_.updateSheetFromChangedPropertyData(sheet)
                      })
                    }
                  }
                  doOri()
                  if (canWrap) {
                    this_.getUndoManager().afterCommand(context, options)
                    this_.getRevertAttributeEffectManager().endTransaction(context, options)
                    if (isFinalEnd()) {
                      everySheetName(context, options.sheetName, sheetName => {
                        // 清理propChangeManage属性数据
                        getPropChangeManager(context.getSheetFromName(sheetName)).clear()
                      })
                    }
                    if (extraAfterCommand) {
                      extraAfterCommand()
                    }
                    if (isFinalEnd()) {
                      if (getData(this_).endDoInNormalStyle) {
                        getData(this_).endDoInNormalStyle()
                        delete getData(this_).endDoInNormalStyle
                      }
                      delete getData(this_).startedCommandDeep

                      if (options.__afterResetStyle) {
                        options.__afterResetStyle()
                        delete options.__afterResetStyle
                      }
                    }

                    context.resumePaint()
                  }
                })

                let revertUndoTransaction = wrapFn(Sheets.Commands, "undoTransaction", (doOri, [context, options, extraBeforeCommand, extraAfterCommand]) => {
                  let this_ = findThis(context)
                  let sheetName = commonUtil.getSheetNameFromCommandOptions(options)
                  // 目前仅支持一个工作簿
                  let canWrap = this_ && (context.getSheetIndex(sheetName) === 0 || !sheetName)
                  let endDoInNormalStyle = null
                  if (canWrap) {
                    context.suspendPaint()
                    endDoInNormalStyle = this_.doInNormalStyle(true)
                    if (extraBeforeCommand) {
                      extraBeforeCommand()
                    }
                  }
                  doOri()
                  if (canWrap) {
                    this_.getUndoManager().undo(context, options)
                    this_.getRevertAttributeEffectManager().undoTransaction(context, options)
                    if (extraAfterCommand) {
                      extraAfterCommand()
                    }
                    endDoInNormalStyle()
                    context.resumePaint()
                  }
                })
                return () => {
                  revertStartTransaction()
                  revertEndTransaction()
                  revertUndoTransaction()
                }
              })
            },
            // 包装单元格绘制
            wrapCellPaint() {
              let highlightTextColor = "rgba(128,0,128,0.7)"
              return this.wrapStaticFunction("wrapCellPaint", (findThis) => {
                let revertCellPaint = wrapFn(Sheets.CellTypes.Base.prototype, "paint", (doOri, [ctx, value, x, y, w, h, style, options]) => {
                  doOri()
                  let this_ = findThis(options.sheet.getParent())
                  if (this_) {
                    let showMode = this_.isShowMode && this_.getConfig().configMode
                    let titleAreas = this_.getTitleAreas(options.row, options.col)
                    let isHighlightArea = (area) => {
                      return this_.highlightAreasMap[area.id] != null
                    }
                    if (!showMode && titleAreas) {
                      // 非展示模式下，跳过非高亮的区域
                      titleAreas = titleAreas.filter(area => isHighlightArea(area))
                    }
                    if (titleAreas && titleAreas.length > 0) {
                      // console.log(ctx.textAlign, [ctx, value, x, y, w, h, style, options])
                      ctx.save()
                      // ctx.font = "19px serif";
                      ctx.textAlign = "left"
                      ctx.textBaseline = "top"

                      filledText = ""
                      let hasDataArea = titleAreas.some(area => area.type === AreaTypeEnum.DATA)

                      for (let i = 0; i < titleAreas.length; i++) {
                        const area = titleAreas[i]
                        let isHighlight = isHighlightArea(area)
                        if (isHighlight) {
                          ctx.fillStyle = highlightTextColor
                        } else {
                          ctx.fillStyle = AreaTypeEnum.properties[area.type].titleTextColor
                        }

                        let title = this_.getAreaTitle(area.id)
                        ctx.fillText(title, x + 1 + ctx.measureText(filledText).width, y + 1)
                        filledText = filledText + title
                        if (i != titleAreas.length - 1) {
                          if (showMode) {
                            ctx.fillStyle = AreaTypeEnum.properties[hasDataArea ? AreaTypeEnum.DATA : titleAreas[0].type].titleTextColor
                          } else {
                            ctx.fillStyle = highlightTextColor
                          }
                          ctx.fillText(",", x + 1 + ctx.measureText(filledText).width, y + 1)
                          filledText = filledText + ","
                        }
                      }
                      ctx.restore()
                    }
                  }
                })
                return () => {
                  revertCellPaint()
                }
              })
            },
            // 包装状态栏展示
            wrapStatusBar() {
              let allowStatusItems = ["cellMode", "zoomSlider", "zoomPanel"]
              return this.wrapStaticFunction("wrapStatusBar", (findThis) => {
                let revertStatusBarBind = wrapFn(Sheets.StatusBar.StatusBar.prototype, "bind", function (doOri, [spread]) {
                  doOri()
                  if (findThis(spread)) {
                    let this_ = findThis(spread)
                    // 如果是回填模式，则移除不必要的状态栏，添加自定义状态栏显示
                    if (!this_.getConfig().configMode) {
                      this.all().filter(v => !allowStatusItems.find(item => item == v.name)).forEach((v) => {
                        this.remove(v.name)
                      })
                      this.add(new RangeSelectorItem("rangeSelector", {
                        menuContent: "选择区域显示",
                        align: "left",
                      }))
                    }
                  }
                })
                return () => {
                  revertStatusBarBind()
                }
              })
            },
            installAllProperty() {
              let uninstalls = []
              let operationalAttributesMap = this.operationalAttributesMap
              for (const key in operationalAttributesMap) {
                const attr = operationalAttributesMap[key]
                if (attr.install) {
                  uninstalls.unshift(attr.install(getSpread()))
                }
              }
              return () => {
                uninstalls.forEach(v => v())
              }
            },
            installCustomSheetFns(sheet) {
              let unbinds = []
              let fnNames = ["addRows", "addColumns", "deleteRows", "deleteColumns"]

              fnNames.forEach(fnName => {
                unbinds.unshift(wrapFn(sheet, fnName, (doOri, args) => {
                  let afterExe = this[fnName](...[sheet, ...args])
                  doOri()
                  afterExe && afterExe()
                }))
              })

              return () => {
                unbinds.forEach(v => v())
              }
            },
            // 合并单元格跨区域限制
            addCheckSheetAddSpan(sheet) {
              return wrapFn(sheet, "addSpan", (doOri, [row, column, rowCount, colCount, sheetArea]) => {
                if (row != null && column != null && rowCount != null && colCount != null && (!sheetArea || sheetArea === Sheets.SheetArea.viewport)) {
                  if (rowCount > 1 || colCount > 1) {
                    let spanArea = {
                      x: column,
                      width: colCount,
                      y: row,
                      height: rowCount
                    }
                    if (this.areaData.every(area => {
                      if (commonUtil.hasAreaIntersection(area, spanArea)) {
                        return commonUtil.isAreaAContainAreaB(area, spanArea)
                      } else {
                        return true
                      }
                    })) {
                      doOri()
                    } else {
                      commonUtil.uniqueAlertMessage("addSpan", (onClose) => {
                        let ctrlObj = this.getConfig().alertMessage("合并单元格不能跨越区域", "warning", onClose)
                        return () => ctrlObj.close()
                      })
                      return false
                    }
                  } else {
                    doOri()
                  }
                } else {
                  doOri()
                }
              })
            },
            // 安装设置默认值功能
            installSettingDefaultValueControl(sheet) {
              let {
                checkCellPreposedControl
              } = getCheckCellPreposedControlUtil(() => sheet, this)

              let posEq = ([rowA, colA], [rowB, colB]) => {
                return rowA == rowB && colA == colB
              }
              let checkCouldSetDefaultValue = (sheet, targetRow, targetCol) => {
                let innerCellId = innerCellIdManager.getInnerCellId(targetRow, targetCol)
                if (this.couldCellEdited(innerCellId)) {
                  let oriValue = sheet.getValue(targetRow, targetCol)
                  if (isBlank(oriValue)) {
                    let defaultValue = getDefaultValue(targetRow, targetCol)
                    if (!isBlank(defaultValue)) {
                      return true
                    }
                  }
                }
                return false
              }
              let getDefaultValueAndCodeFromAttribute = (row, col) => {
                let propertyKey = this.getPropertyKey(row, col)
                if (propertyKey) {
                  let registeredAttr = this.operationalAttributesMap[propertyKey]
                  if (registeredAttr && registeredAttr.getDefaultValueAndCode) {
                    let rs = registeredAttr.getDefaultValueAndCode(row, col, sheet)
                    if (rs) {
                      return rs
                    } else {
                      return {}
                    }
                  }
                }
                return null
              }
              let getDefaultValue = (targetRow, targetCol) => {
                let rs = getDefaultValueAndCodeFromAttribute(targetRow, targetCol)
                if (rs) {
                  return rs.value
                } else {
                  return this.getDefaultValue(targetRow, targetCol)
                }
              }
              let getDefaultCode = (targetRow, targetCol) => {
                let rs = getDefaultValueAndCodeFromAttribute(targetRow, targetCol)
                if (rs) {
                  return rs.code
                } else {
                  return null
                }
              }
              let enableBackfill = () => {
                return !this.getConfig().configMode && !this.getConfig().readOnly
              }
              let getChangeTypeInfo = (targetRow, targetCol) => {
                let beforeChangeType = cellChangedManager.getCellChangeType(cellIdManager.getCellId(targetRow, targetCol))
                let afterChangeType = beforeChangeType === BillEntityChangeTypeEnum.NO_CHANGE ? BillEntityChangeTypeEnum.UPDATE : beforeChangeType
                return {
                  beforeChangeType,
                  afterChangeType
                }
              }

              let isADependingOnB = ([rowA, colA], [rowB, colB]) => {
                let isDepending = false
                let cellPreposedCellControls = this.getCellPreposedCellControls(rowA, colA)
                commonUtil.executeFnUseForceEnd((forceEnd) => {
                  cellPreposedCellControls && cellPreposedCellControls.forEach(({ targetRow, targetCol }) => {
                    if (posEq([targetRow, targetCol], [rowB, colB])) {
                      isDepending = true
                      forceEnd()
                    }
                  })
                })
                if (!isDepending) {
                  let cellPreposedAreaControls = this.getCellPreposedAreaControls(rowA, colA)
                  commonUtil.executeFnUseForceEnd((forceEnd) => {
                    cellPreposedAreaControls && cellPreposedAreaControls.forEach(({ targetAreaId }) => {
                      let { x, y, width, height } = this.areaMap[targetAreaId]
                      commonUtil.everyCell(y, x, height, width, (areaCellRow, areaCellCol) => {
                        if (posEq([areaCellRow, areaCellCol], [rowB, colB])) {
                          isDepending = true
                          forceEnd()
                        }
                      })
                    })
                  })
                }
                return isDepending
              }

              let cellClickListener = (e, { row, col, sheet, sheetArea }) => {
                if (enableBackfill() && sheetArea === Sheets.SheetArea.viewport && commonUtil.checkSingleCellSelection(sheet.getSelections(), sheet)) {
                  let changingCells = []
                  let checkAndPush = (targetRow, targetCol) => {
                    if (checkCouldSetDefaultValue(sheet, targetRow, targetCol)) {
                      let { beforeChangeType, afterChangeType } = getChangeTypeInfo(targetRow, targetCol)
                      let afterValue = getDefaultValue(targetRow, targetCol)
                      let afterCode = getDefaultCode(targetRow, targetCol)
                      changingCells.push({
                        row: targetRow,
                        col: targetCol,
                        beforeValue: sheet.getValue(targetRow, targetCol),
                        beforeCode: this.getExtraData(targetRow, targetCol),
                        beforeChangeType,
                        afterValue,
                        afterCode,
                        afterChangeType,
                        doChange: (allowChange) => {
                          if (allowChange) {
                            this.setValue(targetRow, targetCol, afterValue, sheet)
                            if (!isBlank(afterCode)) {
                              this.setExtraData(targetRow, targetCol, afterCode)
                            }
                          }
                        },
                        checkCouldChangeRuntime() {
                          //实时进行前置单元格校验，以支持多阶次依赖同时设置单元格默认内容
                          return checkCellPreposedControl(targetRow, targetCol, true)
                        }
                        // defaultConfirmMessageBeforeChange,
                        // defaultNotAllowChangeMessage,
                        // defaultCheckErrorMessage
                      })
                    }
                  }

                  let allCellPositionsMap = commonUtil.getLinkedMap()

                  let cellControls = this.getSettingDefaultValueCellControls(row, col)
                  if (cellControls) {
                    cellControls.forEach(({ targetRow, targetCol }) => {
                      let k = getCellKey(targetRow, targetCol)
                      if (!allCellPositionsMap.hasKey(k)) {
                        allCellPositionsMap.set(k, [targetRow, targetCol])
                      }
                    })
                  }

                  let areaControls = this.getSettingDefaultValueAreaControls(row, col)
                  if (areaControls) {
                    areaControls.forEach(({ targetAreaId: areaId }) => {
                      let areaInfo = this.areaMap[areaId]
                      if (areaInfo) {
                        let { x, y, width, height } = areaInfo
                        commonUtil.everyCell(y, x, height, width, (targetRow, targetCol) => {
                          if (!commonUtil.isHideBySpan(targetRow, targetCol, sheet)) {
                            let k = getCellKey(targetRow, targetCol)
                            if (!allCellPositionsMap.hasKey(k)) {
                              allCellPositionsMap.set(k, [targetRow, targetCol])
                            }
                            // checkAndPush(targetRow, targetCol)
                          }
                        })
                      }
                    })
                  }

                  let treeMap = commonUtil.getLinkedMap()
                  let getId = pos => innerCellIdManager.getInnerCellId(...pos)
                  let allCellPositions = allCellPositionsMap.values()
                  allCellPositions.forEach(v => {
                    let id = getId(v)
                    treeMap.set(id, {
                      id: id,
                      children: [],
                      parents: [],
                      position: v
                    })
                  })
                  allCellPositions.forEach((prePosition, index) => {
                    // 只与自己之后的进行比较，防止重复比较
                    allCellPositions.slice(index + 1).forEach(afterPosition => {
                      let preId = getId(prePosition)
                      let afterId = getId(afterPosition)
                      if (isADependingOnB(afterPosition, prePosition)) {
                        treeMap.get(preId).children.push(afterId)
                        treeMap.get(afterId).parents.push(preId)
                      } else if (isADependingOnB(prePosition, afterPosition)) {
                        treeMap.get(preId).parents.push(afterId)
                        treeMap.get(afterId).children.push(preId)
                      }
                    })
                  })
                  let cache = {}
                  let newPositions = []
                  let checkAdd = (posNode, checkingList) => {
                    if (checkingList == null) {
                      checkingList = []
                    }
                    if (checkingList.includes(posNode.id)) {
                      return posNode.id
                    }
                    if (posNode.parents.length > 0) {
                      for (let i = 0; i < posNode.parents.length; i++) {
                        let pId = posNode.parents[i]
                        let rs = checkAdd(treeMap.get(pId), [...checkingList, posNode.id])
                        if (rs != null && rs != posNode.id) {
                          return posNode.id
                        }
                      }
                    }
                    if (!cache[posNode.id]) {
                      newPositions.push(posNode.position)
                      cache[posNode.id] = true
                    }
                  }
                  treeMap.forEach((key, value, index) => {
                    checkAdd(value)
                  })
                  newPositions.forEach(v => checkAndPush(...v))

                  this.settingDefaultValue_ = true
                  cellUrlTriggerUtil.startTriggerUrl(changingCells).finally(() => {
                    this.settingDefaultValue_ = false
                  })
                }
              }
              sheet.bind(Sheets.Events.CellClick, cellClickListener)
              return () => {
                sheet.unbind(Sheets.Events.CellClick, cellClickListener)
              }
            },
            isSettingDefaultValue() {
              return this.settingDefaultValue_ != null && this.settingDefaultValue_
            },
            // 安装前置单元格控制功能（排除签字情况）
            installPreposedControl(sheet) {

              let {
                checkCellPreposedControl,
                hideInfo
              } = getCheckCellPreposedControlUtil(() => sheet, this)

              this.curSelections = sheet.getSelections()
              this.curSelections.forEach(({ row, col, rowCount, colCount }) => {
                commonUtil.everyCell(row, col, rowCount, colCount, (cellRow, cellCol) => {
                  if (!commonUtil.isHideBySpan(cellRow, cellCol, sheet)) {
                    checkCellPreposedControl(cellRow, cellCol, true)
                  }
                })
              })

              let enableBackfill = () => {
                return !this.getConfig().configMode && !this.getConfig().readOnly
              }
              let selectionUniqueCall = null
              let selectionChangingListener = getLogErrorFn((e, info) => {
                if (!selectionUniqueCall) {
                  selectionUniqueCall = commonUtil.getUniqueCallFunction()
                }
                if (enableBackfill()) {
                  let [{ row, col, rowCount, colCount }] = info.newSelections
                  commonUtil.everyCell(row, col, rowCount, colCount, (cellRow, cellCol) => {
                    if (!commonUtil.isHideBySpan(cellRow, cellCol, sheet)) {
                      selectionUniqueCall(() => {
                        checkCellPreposedControl(cellRow, cellCol, true)
                      }, cellRow + "," + cellCol)
                    }
                  })
                  hideInfo()
                }
              })
              let selectionChangedListener = getLogErrorFn((e, info) => {
                selectionUniqueCall = null
                if (enableBackfill()) {
                  if (commonUtil.checkSingleCellSelection(info.newSelections, sheet)) {
                    let [{ row, col }] = info.newSelections
                    if (!this.isSignAttribute(this.getPropertyKey(row, col))) {
                      checkCellPreposedControl(row, col)
                    }
                  } else {
                    hideInfo()
                  }
                }
              })
              sheet.bind(Sheets.Events.SelectionChanging, selectionChangingListener)
              sheet.bind(Sheets.Events.SelectionChanged, selectionChangedListener)


              let needToExecuteAfterEditEnded = null

              let revertFn = listenMouseEnterCell(sheet.parent, ({ row, col }) => {
                // 未选择时启用浮动校验
                if (!selectionUniqueCall) {
                  if (!sheet.isEditing()) {
                    checkCellPreposedControl(row, col, true)
                  } else {
                    if (needToExecuteAfterEditEnded) {
                      needToExecuteAfterEditEnded()
                      needToExecuteAfterEditEnded = () => {
                        checkCellPreposedControl(row, col, true)
                      }
                    } else {
                      needToExecuteAfterEditEnded = () => {
                        checkCellPreposedControl(row, col, true)
                      }
                      let editEndedListener = () => {
                        // 参考 ISSUES 1
                        setTimeout(() => {
                          sheet.unbind(Sheets.Events.EditEnded, editEndedListener)
                        }, 0)
                        if (needToExecuteAfterEditEnded) {
                          needToExecuteAfterEditEnded()
                          needToExecuteAfterEditEnded = null
                        }
                      }
                      sheet.bind(Sheets.Events.EditEnded, editEndedListener)
                    }
                  }
                }
              })
              // let revertFn = this.wrapStaticFunction("Sheets.CellTypes.Base.prototype.processMouseEnter", (findThis) => {
              //   return wrapFn(Sheets.CellTypes.Base.prototype, "processMouseEnter", function (doOri, args) {
              //     let checked = true
              //     if (!selectionUniqueCall) {
              //       checked = checkCellPreposedControl(args[0].row, args[0].col, true)
              //     }
              //     if (checked) {
              //       return doOri()
              //     }
              //     // else {
              //     //   this.processMouseLeave({
              //     //     ...args[0],
              //     //     x: args[0].x + 20,
              //     //     y: args[0].y + 20,
              //     //   })
              //     // }
              //   })
              // })
              return () => {
                sheet.unbind(Sheets.Events.SelectionChanging, selectionChangingListener)
                sheet.unbind(Sheets.Events.SelectionChanged, selectionChangedListener)
                revertFn()
              }
            },
            // 初始化单元格只读的管理，这样就可使用vue监听的方式动态设置单元格的只读情况
            // 实现难点：1、监听本身是动态变化的，因为单元格可能会增减；2、监听的结束方法必须保留，且要支持撤销操作
            installCellReadOnlyBinder(sheet) {
              let unbinds = []

              let customCellPositionManager = getCellDataManagerGroup().getCellDataManager()
              let initCellPosition = (innerCellId) => {
                let [row, col] = innerCellIdManager.getCellPosition(innerCellId)
                let oldInnerCellId = customCellPositionManager.getCellData(row, col)
                if (oldInnerCellId != null && oldInnerCellId != innerCellId) {
                  console.error({
                    msg: "installCellReadOnlyBinder：内部单元格id设置与之前不一致，请检查程序",
                    row,
                    col,
                    innerCellId,
                    oldInnerCellId
                  })
                }
                customCellPositionManager.setCellData(row, col, innerCellId)
              }

              let cellReadOnlyWatchers = {}

              let proxyDo = (() => {
                let n = null
                let fns = []
                return (fn) => {
                  if (n != null) {
                    clearTimeout(n)
                  }
                  fns.push(fn)
                  n = setTimeout(() => {
                    sheet.suspendPaint()
                    fns.forEach(fn => fn())
                    fns = []
                    sheet.resumePaint()
                    n = null
                  }, 0)
                }
              })()

              let setLock = (innerCellId, locked) => {
                let [row, col] = innerCellIdManager.getCellPosition(innerCellId)
                let cell = sheet.getCell(row, col)
                locked = !!locked
                if (cell.locked() !== locked) {
                  cell.locked(locked)
                }
              }
              let initCellWatcher = (innerCellId) => {
                if (!cellReadOnlyWatchers[innerCellId]) {
                  cellReadOnlyWatchers[innerCellId] = this.$watch(() => {
                    return this.isCellReadOnlyFinal(innerCellId)
                  }, (readOnly) => {
                    // 如果只读则设置单元格锁定
                    proxyDo(() => {
                      setLock(innerCellId, readOnly)
                    })
                  })
                }
                initCellPosition(innerCellId)
              }

              let destroyCellWatcher = (innerCellId) => {
                if (cellReadOnlyWatchers[innerCellId]) {
                  cellReadOnlyWatchers[innerCellId]()
                  delete cellReadOnlyWatchers[innerCellId]
                }
              }

              // let initWatchByRange = (startRow, startColumn, rowCount, columnCount) => {
              //   commonUtil.everyCell(startRow, startColumn, rowCount, columnCount, (row, col) => {
              //     let innerCellId = innerCellIdManager.getInnerCellId(row, col)
              //     initCellWatcher(innerCellId)
              //   })
              // }

              let cancelWatchByRange = (startRow, startColumn, rowCount, columnCount) => {
                commonUtil.everyCell(startRow, startColumn, rowCount, columnCount, (row, col) => {
                  let innerCellId = customCellPositionManager.getCellData(row, col)
                  destroyCellWatcher(innerCellId)
                })
              }

              // let addRows = (rowIndex, rowCount) => {
              //   // 扩充区域
              //   customCellPositionManager.addRows(rowIndex, rowCount)
              //   let sheetColumnCount = sheet.getColumnCount()
              //   // 监听并设置内部单元格id
              //   initWatchByRange(rowIndex, 0, rowCount, sheetColumnCount)
              // }

              let undoAddRows = (rowIndex, rowCount) => {
                deleteRows(rowIndex, rowCount)
              }

              let deleteRows = (rowIndex, rowCount) => {
                let sheetColumnCount = sheet.getColumnCount()
                // 取消监听
                cancelWatchByRange(rowIndex, 0, rowCount, sheetColumnCount)
                // 删除区域
                customCellPositionManager.deleteRows(rowIndex, rowCount)
              }

              // // 在命令完全执行完毕后执行
              // let undoDeleteRows = (rowIndex, rowCount) => {
              //   addRows(rowIndex, rowCount)
              // }


              // let addColumns = (colIndex, columnCount) => {
              //   // 扩充区域
              //   customCellPositionManager.addColumns(colIndex, columnCount)
              //   let sheetRowCount = sheet.getRowCount()
              //   // 设置监听并初始化内部单元格id
              //   initWatchByRange(0, colIndex, sheetRowCount, columnCount)
              // }

              let undoAddColumns = (colIndex, columnCount) => {
                deleteColumns(colIndex, columnCount)
              }

              let deleteColumns = (colIndex, columnCount) => {
                let sheetRowCount = sheet.getRowCount()
                // 取消监听
                cancelWatchByRange(0, colIndex, sheetRowCount, columnCount)
                // 删除区域
                customCellPositionManager.deleteColumns(colIndex, columnCount)
              }

              // // 在命令完全执行完毕后执行
              // let undoDeleteColumns = (colIndex, columnCount) => {
              //   addColumns(colIndex, columnCount)
              // }

              let rowChangedListener = (e, info) => {
                if (!info.isUndo) {
                  if (info.propertyName == "addRows") {
                    // addRows(info.row, info.newValue)
                  } else if (info.propertyName == "deleteRows") {
                    deleteRows(info.row, info.newValue)
                  }
                } else {
                  if (info.propertyName == "addRows") {
                    undoAddRows(info.row, info.oldValue)
                  } else if (info.propertyName == "deleteRows") {
                    // // 因为需要被undo的单元格内部id数据，而单元格内部id数据是通过重写endTransaction等方法实现的，此时实际id数据未更改，所以等到下个执行帧执行以保证需要的数据存在
                    // // TODO 待优化，应该使用监听方式调用，这种方式存在风险
                    // setTimeout(() => {
                    //   undoDeleteRows(info.row, info.oldValue)
                    // }, 0)
                  }
                }
              }
              let columnChangedListener = (e, info) => {
                if (!info.isUndo) {
                  if (info.propertyName == "addColumns") {
                    // addColumns(info.col, info.newValue)
                  } else if (info.propertyName == "deleteColumns") {
                    deleteColumns(info.col, info.newValue)
                  }
                } else {
                  if (info.propertyName == "addColumns") {
                    undoAddColumns(info.col, info.oldValue)
                  } else if (info.propertyName == "deleteColumns") {
                    // setTimeout(() => {
                    //   undoDeleteColumns(info.col, info.oldValue)
                    // }, 0)
                  }
                }
              }

              // initWatchByRange(0, 0, sheet.getRowCount(), sheet.getColumnCount())

              sheet.bind(Sheets.Events.RowChanged, rowChangedListener)
              unbinds.unshift(() => {
                sheet.unbind(Sheets.Events.RowChanged, rowChangedListener)
              })
              sheet.bind(Sheets.Events.ColumnChanged, columnChangedListener)
              unbinds.unshift(() => {
                sheet.unbind(Sheets.Events.ColumnChanged, columnChangedListener)
              })

              let propertySetListener = (row, col, propertyKey) => {
                setTimeout(() => {
                  let innerCellId = innerCellIdManager.getInnerCellId(row, col)
                  initCellWatcher(innerCellId)
                }, 0)
              }

              cellPropertyDataManager.forEachCellData((row, col, propertyData) => {
                let innerCellId = innerCellIdManager.getInnerCellId(row, col)
                initCellWatcher(innerCellId)
              })

              cellPropertyDataManager.addPropertySetListener(propertySetListener)
              unbinds.unshift(() => {
                cellPropertyDataManager.removePropertySetListener(propertySetListener)
              })


              let initPreposedCellControlsReadOnlyWatcher = () => {
                for (const innerCellId in this.cellPreposedCellControlsMap) {
                  initCellWatcher(innerCellId)
                }
              }
              if (!this.getConfig().configMode) {
                initPreposedCellControlsReadOnlyWatcher()
              }

              let endWatch = this.$watch(() => {
                return this.getConfig().configMode
              }, (value) => {
                if (!value) {// 进入回填模式时要重新监听存在前置单元格要求的单元格
                  initPreposedCellControlsReadOnlyWatcher()
                }
              })
              unbinds.unshift(endWatch)

              return () => {
                for (const innerCellId in cellReadOnlyWatchers) {
                  cellReadOnlyWatchers[innerCellId]()
                }
                unbinds.forEach(v => v())
              }
            },
            checkEditEndingCellReadOnly: (() => {
              let innerEditEnd = false
              return function (args) {
                if (!innerEditEnd) {
                  if (!args.cancel) {
                    let { row, col } = args
                    if (this.isCellReadOnlyFinal(innerCellIdManager.getInnerCellId(row, col))) {
                      args.cancel = true
                      setTimeout(() => {
                        innerEditEnd = true
                        getSpread().getActiveSheet().endEdit(true)
                        innerEditEnd = false
                      }, 0)
                    }
                  }
                }
              }
            })(),
            initMaxTextLengthChecker() {
              let spread = getSpread()
              let editEndingListener = (e, args) => {
                this.checkEditEndingCellReadOnly(args)
                if (args.cancel) {
                  return
                }
                let { editingText } = args
                if (editingText && editingText.length > this.getConfig().maxTextLength) {
                  args.cancel = true
                  commonUtil.uniqueAlertMessage("overMaxTextLength", (onClose) => {
                    let ctrlObj = this.getConfig().alertMessage("字数超出限制：" + this.getConfig().maxTextLength, "warning", onClose)
                    return () => ctrlObj.close()
                  })
                }
              }
              spread.bind(Sheets.Events.EditEnding, editEndingListener)
              return () => {
                spread.unbind(Sheets.Events.EditEnding, editEndingListener)
              }
            },
            installFastInputModule() {
              let spread = getSpread()
              // let curCell = [-1, -1]

              // let posEq = (posA, posB) => {
              //   return posA[0] === posB[0] && posA[1] === posB[1]
              // }

              // let editEndedListener = (e, args) => {
              //   if (!this.getConfig().configMode) {
              //     let curCell_ = curCell
              //     setTimeout(() => {
              //       let { row, col, sheet } = args
              //       if (!posEq(curCell_, [row, col])) {
              //         let targetValue = sheet.getValue(...curCell_)
              //         if ((targetValue == null || targetValue == "") && sheet.getCellType(...curCell_) instanceof Sheets.CellTypes.Text) {
              //           sheet.setActiveCell(...curCell_)
              //           sheet.startEdit()
              //         }
              //       }
              //     }, 0)
              //   }
              // }

              // let endListen = listenMouseEnterCell(spread, ({ row, col }) => {
              //   curCell = [row, col]
              // })
              // spread.bind(Sheets.Events.EditEnded, editEndedListener)
              // return () => {
              //   spread.unbind(Sheets.Events.EditEnded, editEndedListener)
              //   endListen()
              // }
              let customCheckCellPreposedControl = (() => {
                let sheet_ = null
                let {
                  checkCellPreposedControl
                } = getCheckCellPreposedControlUtil(() => sheet_, this)
                return (sheet, ...args) => {
                  sheet_ = sheet
                  return checkCellPreposedControl(...args)
                }
              })()

              let allowCellTypes = [
                Sheets.CellTypes.Text,
                DateTimeCellType,
                CustomComboBox
              ]

              let cellClickListener = (e, args) => {
                if (this.getConfig().configMode) {
                  return
                }
                let { row, col, sheet } = args
                if (commonUtil.checkSingleCellSelection(sheet.getSelections(), sheet)) {
                  let targetValue = sheet.getValue(row, col)
                  if (isBlank(targetValue) && allowCellTypes.some(type => sheet.getCellType(row, col) instanceof type)) {
                    if (!this.isSignAttribute(this.getPropertyKey(row, col))) {
                      let doFastEdit = () => {
                        // 由于设置活跃单元格操作不会触发单元格选择变更事件，在这里进行额外校验
                        sheet.setActiveCell(row, col)
                        if (customCheckCellPreposedControl(sheet, row, col)) {
                          setTimeout(() => {
                            sheet.startEdit()
                          }, 0)
                        }
                        this.refreshExtraShowStyle()
                      }
                      if (!sheet.isEditing()) {
                        doFastEdit()
                      } else {
                        let editEndedListener = () => {
                          // 参考 ISSUES 1
                          setTimeout(() => {
                            sheet.unbind(Sheets.Events.EditEnded, editEndedListener)
                          }, 0)
                          doFastEdit()
                        }
                        sheet.bind(Sheets.Events.EditEnded, editEndedListener)
                      }
                    }
                  }
                }

              }

              spread.bind(Sheets.Events.CellClick, cellClickListener)
              return () => {
                spread.unbind(Sheets.Events.CellClick, cellClickListener)
              }
            },

            // 安装单元格接口触发功能（仅适用于可编辑的单元格，签字、radio选择这类除外）
            installCellUrlTrigger(sheet) {
              let unbinds = []
              let editing = false
              let checkingAllowChange = false
              let editingCellOldValue = null
              let editStartingListener = (e, args) => {
                editing = true
                editingCellOldValue = sheet.getValue(args.row, args.col)
              }

              let editEndFns = []
              let editEndedListener = (e, args) => {
                editing = false
                if (editEndFns.length > 0) {
                  editEndFns.forEach(v => v())
                  editEndFns = []
                }
                editingCellOldValue = null
              }

              let innerEditEnd = false
              let endEdit = (ignoreValueChange) => {
                innerEditEnd = true
                sheet.endEdit(ignoreValueChange)
                innerEditEnd = false
              }
              // 如果不是内部调用结束才进一步执行判断
              let checkInnerEditEndAndDo = (callback) => {
                if (!innerEditEnd) {
                  callback()
                }
              }

              let checkValueChanged = (editingText) => {
                return !isValueEqual(editingCellOldValue, editingText)
              }

              let getTempSheet = (() => {
                let tempSheet = null
                return () => {
                  if (!tempSheet) {
                    tempSheet = new Sheets.Worksheet()
                    setTimeout(() => {
                      tempSheet = null
                    }, 0)
                  }
                  return tempSheet
                }
              })()

              let editEndingListener = (e, args) => {
                this.checkEditEndingCellReadOnly(args)
                if (args.cancel) {
                  return
                }
                checkInnerEditEndAndDo(() => {
                  // 如果正在检查是否允许更改，则取消结束编辑并直接返回
                  if (checkingAllowChange) {
                    args.cancel = true
                    return
                  }
                  // 如果编辑的单元格的数据变化了，则判断是否需要调用单元格
                  if (checkValueChanged(args.editingText)) {
                    args.cancel = true
                    checkingAllowChange = true

                    let { row, col, editingText, sheet } = args
                    let beforeValue = sheet.getValue(row, col)
                    // 处理时间类型值的变更检测
                    if (beforeValue instanceof Date) {
                      beforeValue = sheet.getText(row, col)
                      if (editingText instanceof Date) {
                        let tempSheet = getTempSheet()
                        tempSheet.setStyle(0, 0, sheet.getStyle(row, col).clone())
                        tempSheet.setValue(0, 0, editingText)
                        editingText = tempSheet.getText(0, 0)
                      }
                    }
                    let beforeChangeType = cellChangedManager.getCellChangeType(cellIdManager.getCellId(row, col))
                    let afterValue = editingText
                    let afterChangeType = beforeChangeType === BillEntityChangeTypeEnum.NO_CHANGE ? BillEntityChangeTypeEnum.UPDATE : beforeChangeType
                    let changingCells = [{
                      row,
                      col,
                      beforeValue,
                      beforeChangeType,
                      afterValue,
                      afterChangeType,
                      doChange: (allowChange) => {
                        endEdit(!allowChange)
                      },
                      // defaultConfirmMessageBeforeChange,
                      defaultNotAllowChangeMessage: "根据业务规则，当前单元格编辑被撤销",
                      defaultCheckErrorMessage: "变更检测校验请求调用失败"
                    }]
                    setTimeout(() => {
                      cellUrlTriggerUtil.startTriggerUrl(changingCells).finally(() => {
                        checkingAllowChange = false
                      })
                    }, 0)
                  }
                })
              }

              sheet.bind(Sheets.Events.EditStarting, editStartingListener)
              unbinds.unshift(() => {
                sheet.unbind(Sheets.Events.EditStarting, editStartingListener)
              })

              sheet.bind(Sheets.Events.EditEnding, editEndingListener)
              unbinds.unshift(() => {
                sheet.unbind(Sheets.Events.EditEnding, editEndingListener)
              })

              sheet.bind(Sheets.Events.EditEnded, editEndedListener)
              unbinds.unshift(() => {
                sheet.unbind(Sheets.Events.EditEnded, editEndedListener)
              })

              let dragFillBlockListener = (e, args) => {
                let startRangeCellMap = {}
                let getKey = (row, col) => row + "," + col
                args.sheet.getSelections().forEach(selection => {
                  commonUtil.everyCell(selection.row, selection.col, selection.rowCount, selection.colCount, (row, col) => {
                    startRangeCellMap[getKey(row, col)] = true
                  })
                })
                let fillRange = args.fillRange
                commonUtil.executeFnUseForceEnd((forceEnd) => {
                  commonUtil.everyCell(fillRange.row, fillRange.col, fillRange.rowCount, fillRange.colCount, (row, col) => {
                    if (!startRangeCellMap[getKey(row, col)]) {
                      if (cellUrlTriggerUtil.hasTriggerUrl(row, col)) {
                        args.cancel = true
                        // this.getConfig().alertMessage("填充区域存在需要调用服务端接口的单元格，禁止填充操作")
                        forceEnd()
                      }
                    }
                  })
                })
              }
              // let dragFillBlockCompletedListener = (e, args) => {
              // }

              sheet.bind(Sheets.Events.DragFillBlock, dragFillBlockListener)
              unbinds.unshift(() => {
                sheet.unbind(Sheets.Events.DragFillBlock, dragFillBlockListener)
              })

              // sheet.bind(Sheets.Events.DragFillBlockCompleted, dragFillBlockCompletedListener)
              // unbinds.unshift(() => {
              //   sheet.unbind(Sheets.Events.DragFillBlockCompleted, dragFillBlockCompletedListener)
              // })
              return () => {
                unbinds.forEach(v => v())
              }
            },
            // 修复编辑单元格时文字重叠的问题。
            // 背景色为半透明所致，通过编辑前提前变更背景色为不透明颜色解决
            repairCellEditingStyle() {
              let spread = getSpread()
              let whiteColor = commonUtil.parseRgbString("rgba(255,255,255,1)")
              return this.wrapStaticFunction("innerStartEdit", (findThis) => {
                // VERSION_CHECK
                let revertInnerStartEditBind = wrapFn(Sheets.Worksheet.prototype, "EF", function (doOri, [canvas, row, col]) {
                  if (findThis(spread)) {
                    // let this_ = findThis(spread)
                    let sheet = this
                    let backColor = sheet.getCell(row, col).backColor()
                    if (backColor && backColor.startsWith("rgba")) {
                      let repairedBackgroundColor = commonUtil.toRgbString(commonUtil.mergeColors([whiteColor, commonUtil.parseRgbString(backColor)]))
                      sheet.getCell(row, col).backColor(repairedBackgroundColor)
                      // 下一个执行帧即可恢复背景色
                      setTimeout(() => {
                        // 如果颜色变化则取消恢复背景颜色，因为当前颜色可能是临时的颜色
                        if (sheet.getCell(row, col).backColor() === repairedBackgroundColor) {
                          sheet.getCell(row, col).backColor(backColor)
                        }
                      }, 0)
                    }
                  }
                  doOri()
                })
                return () => {
                  revertInnerStartEditBind()
                }
              })
            },
            // 用于禁用部分额外操作的设置（剪切、粘贴、撤销、重做）
            installExtendOperationBinder() {
              let keyEnum = GC.Spread.Commands.Key
              let getConfigMode = () => this.getConfig().configMode
              let getAllowExtendOperation = () => this.getConfig().allowExtendOperation
              let checkDisableExtendOperation = () => !getConfigMode() && !getAllowExtendOperation()

              let spread = getSpread()
              let tempRemovedShortcutKeys = [
                {
                  cmd: "cut",
                  key: keyEnum.x,
                  ctrl: true
                },
                {
                  cmd: "paste",
                  key: keyEnum.v,
                  ctrl: true
                },
                {
                  cmd: "undo",
                  key: keyEnum.z,
                  ctrl: true
                },
                {
                  cmd: "redo",
                  key: keyEnum.y,
                  ctrl: true
                },
              ]
              let disableExtendOperation = () => {
                // 通过禁用快捷键来禁用用户的扩展操作
                tempRemovedShortcutKeys.forEach((obj) => {
                  spread.commandManager().setShortcutKey(obj.cmd, null, null)
                })
              }
              let enableExtendOperation = () => {
                tempRemovedShortcutKeys.forEach((obj) => {
                  spread.commandManager().setShortcutKey(obj.cmd, obj.key, obj.ctrl)
                })
              }
              if (checkDisableExtendOperation()) {
                disableExtendOperation()
              }
              let endWatch = this.$watch(() => {
                return checkDisableExtendOperation()
              }, () => {
                if (checkDisableExtendOperation()) {
                  disableExtendOperation()
                } else {
                  enableExtendOperation()
                }
              })
              // // 恢复渲染
              // sheet.resumePaint()
              return () => {
                // // 暂停渲染
                // sheet.suspendPaint()
                endWatch()
                enableExtendOperation()
                // // 恢复渲染
                // sheet.resumePaint()
              }
            },
            // 在属性数据初始化后调用，返回解绑函数
            installSpreadAfterSetPropertyData() {
              let uninstallAllProperty = this.installAllProperty()
              return () => {
                uninstallAllProperty()
              }
            },
            // 在单元格数据初始化后调用，返回解绑函数
            installSpreadAfterSetTemplateData() {
              let sheet = getSpread().getActiveSheet()
              let unbinds = []
              unbinds.unshift(this.wrapSheetTransaction(sheet))
              unbinds.unshift(this.installCustomSheetFns(sheet))
              unbinds.unshift(this.addCheckSheetAddSpan(sheet))
              unbinds.unshift(this.installExtraShowStyle(sheet))
              unbinds.unshift(cellChangedManager.initSheetListener(sheet))
              unbinds.unshift(this.installCellReadOnlyBinder(sheet))
              unbinds.unshift(this.installSettingDefaultValueControl(sheet))
              unbinds.unshift(this.installPreposedControl(sheet))
              unbinds.unshift(this.installCellUrlTrigger(sheet))
              unbinds.unshift(this.initSpreadSheetOptionsBinder(sheet))
              return () => {
                unbinds.forEach(v => v())
              }
            },
            // 初始化spread
            initSpread() {
              let spread = getSpread()
              let enableContextMenu = null
              let disableContextMenu = () => wrapFn(spread.contextMenu, "onOpenMenu", function (doOri, [menuData, itemsDataForShown, hitInfo, workbook]) {
                itemsDataForShown.splice(0, itemsDataForShown.length)
              })
              // 非配置模式下，初始化背景并且禁用右键菜单
              if (!this.getConfig().configMode) {
                spread.fromJSON(JSON.parse(this.defaultSsjson))
                enableContextMenu = disableContextMenu()
              }
              this.$watch(() => {
                return this.getConfig().configMode
              }, (configMode) => {
                if (enableContextMenu) {
                  enableContextMenu()
                  enableContextMenu = null
                }
                if (!configMode) {
                  enableContextMenu = disableContextMenu()
                }
              })
              // 初始化底部滚动条样式
              let removeTabStripStyle = initTabStripStyle(spread.getHost())
              return () => {
                if (enableContextMenu) {
                  enableContextMenu()
                  enableContextMenu = null
                }
                removeTabStripStyle()
              }
            },
            // 在spread初始化时调用绑定，返回解绑函数
            installSpread() {
              let unbinds = []
              unbinds.unshift(this.initSpread())
              unbinds.unshift(this.wrapSpreadTransaction())
              unbinds.unshift(this.wrapCellPaint())
              unbinds.unshift(this.wrapStatusBar())
              unbinds.unshift(this.repairBasicCommand())
              unbinds.unshift(setSpreadMouseHoverTitle(getSpread(), this))
              unbinds.unshift(this.listenSelectionChangedForShowHighlightCellsAndAreas())
              unbinds.unshift(this.installExtendOperationBinder())
              unbinds.unshift(this.repairCellEditingStyle())

              unbinds.unshift(this.listenEditCellForFormat())
              unbinds.unshift(this.initMaxTextLengthChecker())
              unbinds.unshift(this.installFastInputModule())
              return () => {
                unbinds.forEach(v => v())
              }
            },

            // 初始化新增的单元格区域范围(比如插入的行或者列)
            initNewRanges(ranges, sheet) {
              ranges.forEach(({ col: columnIndex, colCount, row: rowIndex, rowCount }) => {
                if (columnIndex == -1) {
                  columnIndex = 0
                }
                if (rowIndex == -1) {
                  rowIndex = 0
                }
                commonUtil.everyCell(rowIndex, columnIndex, rowCount, colCount, (row, col) => {
                  sheet.getCell(row, col).locked(false)
                })
              })
            },
            addRows(sheet, rowIndex, rowCount, sourceRowIndex) {
              let afterExecutors = []
              if (sourceRowIndex == null) {
                sourceRowIndex = rowIndex
              }
              currentCellDataManagerGroup.addRows(rowIndex, rowCount, { sourceRowIndex })

              let copyAreaData = copyDataSimple(this.areaData)
              let areaChanged = false
              copyAreaData.forEach(areaInfo => {
                let { x, y, width, height } = areaInfo
                if (sourceRowIndex >= y && sourceRowIndex <= y + height - 1) {
                  areaInfo.height += rowCount
                  areaChanged = true
                } else if (sourceRowIndex < y) {
                  this.deleteAreaTag(areaInfo)
                  areaInfo.y += rowCount
                  afterExecutors.push(() => {
                    this.addAreaTag(areaInfo, sheet)
                  })
                  areaChanged = true
                }
              })
              if (areaChanged) {
                this.areaData = copyAreaData
              }

              let copyControlData = copyDataSimple(this.controlData)
              let controlChanged = false
              copyControlData.forEach(control => {
                let { sourceRow, targetRow } = control
                if (sourceRow >= rowIndex) {
                  control.sourceRow += rowCount
                  controlChanged = true
                }
                if (!isBlank(targetRow) && targetRow >= rowIndex) {
                  control.targetRow += rowCount
                  controlChanged = true
                }
              })
              if (controlChanged) {
                this.controlData = copyControlData
              }
              return () => {
                this.initNewRanges([{
                  col: -1,
                  colCount: sheet.getColumnCount(),
                  row: rowIndex,
                  rowCount: rowCount
                }], sheet)
                afterExecutors.forEach(v => v())
              }
            },
            addColumns(sheet, columnIndex, columnCount, sourceColumnIndex) {
              let afterExecutors = []
              if (sourceColumnIndex == null) {
                sourceColumnIndex = columnIndex
              }
              currentCellDataManagerGroup.addColumns(columnIndex, columnCount, { sourceColumnIndex })

              let copyAreaData = copyDataSimple(this.areaData)
              let areaChanged = false
              copyAreaData.forEach(areaInfo => {
                let { x, y, width, height } = areaInfo

                if (sourceColumnIndex >= x && sourceColumnIndex <= x + width - 1) {
                  areaInfo.width += columnCount
                  areaChanged = true
                } else if (sourceColumnIndex < x) {
                  this.deleteAreaTag(areaInfo, sheet)
                  areaInfo.x += columnCount
                  afterExecutors.push(() => {
                    this.addAreaTag(areaInfo, sheet)
                  })
                  areaChanged = true
                }
              })
              if (areaChanged) {
                this.areaData = copyAreaData
              }

              let copyControlData = copyDataSimple(this.controlData)
              let controlChanged = false
              copyControlData.forEach(control => {
                let { sourceCol, targetCol } = control
                if (sourceCol >= columnIndex) {
                  control.sourceCol += columnCount
                  controlChanged = true
                }
                if (!isBlank(targetCol) && targetCol >= columnIndex) {
                  control.targetCol += columnCount
                  controlChanged = true
                }
              })
              if (controlChanged) {
                this.controlData = copyControlData
              }

              return () => {
                this.initNewRanges([{
                  col: columnIndex,
                  colCount: columnCount,
                  row: -1,
                  rowCount: sheet.getRowCount()
                }], sheet)
                afterExecutors.forEach(v => v())
              }
            },
            deleteRows(sheet, rowIndex, rowCount) {
              let afterExecutors = []

              currentCellDataManagerGroup.deleteRows(rowIndex, rowCount)

              let endRowIndex = rowIndex + rowCount - 1
              commonUtil.forEach(this.areaData, (areaInfo, index, splice) => {
                let { x, y, width, height } = areaInfo
                let endY = y + height - 1
                if (rowIndex <= endY && endRowIndex >= y) {
                  areaInfo.height -= Math.min(endRowIndex, endY) - Math.max(rowIndex, y) + 1
                }
                if (rowIndex < y) {
                  this.deleteAreaTag(areaInfo, sheet)
                  if (endRowIndex >= y) {
                    areaInfo.y = rowIndex
                  } else {
                    areaInfo.y -= rowCount
                  }
                  afterExecutors.push(() => {
                    this.addAreaTag(areaInfo, sheet)
                  })
                }
                if (areaInfo.height === 0) {
                  splice(index, 1)
                }
              })


              let isBetween = (v, a, b) => {
                return !(v < a) && !(v > b)
              }
              commonUtil.forEach(this.controlData, (control, i, splice) => {
                let { sourceRow, targetRow, targetAreaId } = control
                if (isBetween(sourceRow, rowIndex, endRowIndex)) {
                  splice(i, 1)
                } else if ((!isBlank(targetRow) && isBetween(targetRow, rowIndex, endRowIndex)) ||
                  (!isBlank(targetAreaId) && !this.areaMap[targetAreaId])) {
                  splice(i, 1)
                  this.resetControlTag(control.sourceRow, control.sourceCol, sheet)
                } else {
                  if (sourceRow > endRowIndex) {
                    control.sourceRow -= rowCount
                  }
                  if (!isBlank(targetRow) && targetRow > endRowIndex) {
                    control.targetRow -= rowCount
                    afterExecutors.push(() => {
                      this.resetControlTag(control.sourceRow, control.sourceCol, sheet)
                    })
                  }
                }
              })
              return () => {
                afterExecutors.forEach(v => v())
              }
            },
            deleteColumns(sheet, columnIndex, columnCount) {
              let afterExecutors = []

              currentCellDataManagerGroup.deleteColumns(columnIndex, columnCount)

              let endColumnIndex = columnIndex + columnCount - 1
              commonUtil.forEach(this.areaData, (areaInfo, index, splice) => {
                let { x, y, width, height } = areaInfo
                let endX = x + width - 1
                if (columnIndex <= endX && endColumnIndex >= x) {
                  areaInfo.width -= Math.min(endColumnIndex, endX) - Math.max(columnIndex, x) + 1
                }
                if (columnIndex < x) {
                  this.deleteAreaTag(areaInfo, sheet)
                  if (endColumnIndex >= x) {
                    areaInfo.x = columnIndex
                  } else {
                    areaInfo.x -= columnCount
                  }
                  afterExecutors.push(() => {
                    this.addAreaTag(areaInfo, sheet)
                  })
                }
                if (areaInfo.width === 0) {
                  splice(index, 1)
                }
              })

              let isBetween = (v, a, b) => {
                return !(v < a) && !(v > b)
              }
              commonUtil.forEach(this.controlData, (control, i, splice) => {
                let { sourceCol, targetCol, targetAreaId } = control
                if (isBetween(sourceCol, columnIndex, endColumnIndex)) {
                  splice(i, 1)
                } else if ((!isBlank(targetCol) && isBetween(targetCol, columnIndex, endColumnIndex)) ||
                  (!isBlank(targetAreaId) && !this.areaMap[targetAreaId])) {
                  splice(i, 1)
                  this.resetControlTag(control.sourceRow, control.sourceCol, sheet)
                } else {
                  if (sourceCol > endColumnIndex) {
                    control.sourceCol -= columnCount
                  }
                  if (!isBlank(targetCol) && targetCol > endColumnIndex) {
                    control.targetCol -= columnCounts
                    afterExecutors.push(() => {
                      this.resetControlTag(control.sourceRow, control.sourceCol, sheet)
                    })
                  }
                }
              })

              return () => {
                afterExecutors.forEach(v => v())
              }
            },


            hasProperty(rowIndex, columnIndex, rowCount, columnCount) {
              if (rowCount == null) {
                rowCount = 1
              }
              if (columnCount == null) {
                columnCount = 1
              }
              if (rowIndex == -1) {
                rowIndex = 0
              }
              if (columnIndex == -1) {
                columnIndex = 0
              }
              for (let row = rowIndex; row < rowIndex + rowCount; row++) {
                for (let col = columnIndex; col < columnIndex + columnCount; col++) {
                  if (cellPropertyDataManager.getCellData(row, col)) {
                    return true
                  }
                }
              }
              return false
            },
            setProperty({ rowIndex, columnIndex, rowCount, columnCount, propertyKey, sheet }) {
              if (rowIndex == -1) {
                rowIndex = 0
              }
              if (columnIndex == -1) {
                columnIndex = 0
              }
              if (rowCount == null) {
                rowCount = 1
              }
              if (columnCount == null) {
                columnCount = 1
              }
              if (sheet == null) {
                sheet = getSpread().getActiveSheet()
              }

              for (let row = rowIndex; row < rowIndex + rowCount; row++) {
                for (let col = columnIndex; col < columnIndex + columnCount; col++) {
                  // 过滤掉因为跨行跨列而被隐藏的单元格
                  if (!commonUtil.isHideBySpan(row, col, sheet)) {
                    cellPropertyDataManager.setPropertyKey(row, col, propertyKey)
                  }
                }
              }
            },
            deleteProperty({ rowIndex, columnIndex, rowCount, columnCount }) {
              if (rowIndex == -1) {
                rowIndex = 0
              }
              if (columnIndex == -1) {
                columnIndex = 0
              }
              if (rowCount == null) {
                rowCount = 1
              }
              if (columnCount == null) {
                columnCount = 1
              }
              for (let i = rowIndex; i < rowIndex + rowCount; i++) {
                for (let j = columnIndex; j < columnIndex + columnCount; j++) {
                  cellPropertyDataManager.deleteCellData(i, j)
                }
              }
            },
            // 查找相关区域
            findAreaInfo(rowIndex, columnIndex, rowCount, columnCount, isContain, areaType) {
              if (rowCount == null) {
                rowCount = 1
              }
              if (columnCount == null) {
                columnCount = 1
              }
              let curArea = {
                x: columnIndex,
                width: columnCount,
                y: rowIndex,
                height: rowCount
              }
              if (isContain == null || isContain === false) {
                for (let i = 0; i < this.areaData.length; i++) {
                  let { type } = this.areaData[i]
                  if (areaType != type) {
                    continue
                  }
                  if (commonUtil.hasAreaIntersection(this.areaData[i], curArea)) {
                    return this.areaData[i]
                  }
                }
              } else if (isContain === true) {
                for (let i = 0; i < this.areaData.length; i++) {
                  let { type } = this.areaData[i]
                  if (areaType != type) {
                    continue
                  }
                  if (commonUtil.isAreaAContainAreaB(this.areaData[i], curArea)) {
                    return this.areaData[i]
                  }
                }
              }
              return null
            },
            setArea(rowIndex, columnIndex, rowCount, columnCount, areaType) {
              if (rowIndex == -1) {
                rowIndex = 0
              }
              if (columnIndex == -1) {
                columnIndex = 0
              }
              if (areaType == null) {
                throw "请传递区域类型"
              }
              if (AreaTypeEnum.properties[areaType].allowOverlap) {
                if (this.findAreaIndex(rowIndex, columnIndex, rowCount, columnCount, areaType) != -1) {
                  let msg = "已经设置过的" + AreaTypeEnum.properties[areaType].description + "无法再次设置"
                  console.error(msg)
                  throw msg
                }
              } else {
                if (this.findAreaInfo(rowIndex, columnIndex, rowCount, columnCount, false, areaType)) {
                  let msg = AreaTypeEnum.properties[areaType].description + "禁止重叠"
                  console.error(msg)
                  throw msg
                }
              }

              let id = getNewAreaId()
              let area = {
                x: columnIndex,
                y: rowIndex,
                width: columnCount,
                height: rowCount,
                id: id,
                type: areaType
              }
              this.areaData.push(area)
              this.addAreaTag(area)
            },
            addAreaTag(area, sheet) {
              let startCell = (sheet ? sheet : getSpread().getActiveSheet()).getCell(area.y, area.x)
              let oldAreas = getCellTagAttr(startCell, "areas") || []
              if (oldAreas.findIndex(v => v == area.id) == -1) {
                oldAreas.push(area.id)
                setCellTagAttr(startCell, "areas", oldAreas)
              }
            },

            innerDeleteArea(rowIndex, columnIndex, rowCount, columnCount, areaType, checkAreaFn) {
              if (rowIndex == -1) {
                rowIndex = 0
              }
              if (columnIndex == -1) {
                columnIndex = 0
              }
              let index = this.findAreaIndex(rowIndex, columnIndex, rowCount, columnCount, areaType)
              if (index != -1) {
                if (checkAreaFn) {
                  checkAreaFn(this.areaData[index])
                }
                let [deletedArea] = this.areaData.splice(index, 1)
                this.deleteAreaTag(deletedArea)
              } else {
                throw "未找到区域，删除失败。注：删除区域仅支持准确对应"
              }
            },
            // 删除区域
            deleteArea(rowIndex, columnIndex, rowCount, columnCount, areaType) {
              if (areaType == null) {
                throw "请传递区域类型"
              }
              if (areaType == AreaTypeEnum.DATA) {
                this.innerDeleteArea(rowIndex, columnIndex, rowCount, columnCount, areaType, (areaInfo) => {
                  let findControl = this.controlData.find(v => v.targetAreaId == areaInfo.id)
                  if (findControl) {
                    throw "请先移除" + [findControl.sourceRow, findControl.sourceCol] + "单元格的针对当前区域的配置"
                  }
                })
              } else {
                this.innerDeleteArea(rowIndex, columnIndex, rowCount, columnCount, areaType)
              }
            },
            deleteAreaTag(deletedArea, sheet) {
              let startCell = (sheet ? sheet : getSpread().getActiveSheet()).getCell(deletedArea.y, deletedArea.x)
              let oldAreas = getCellTagAttr(startCell, "areas") || []
              oldAreas.splice(oldAreas.findIndex(v => v == deletedArea.id), 1)
              if (oldAreas.length > 0) {
                setCellTagAttr(startCell, "areas", oldAreas)
              } else {
                deleteCellTagAttr(startCell, "areas")
              }
            },
            findAreaIndex(rowIndex, columnIndex, rowCount, columnCount, areaType) {
              if (rowIndex == -1) {
                rowIndex = 0
              }
              if (columnIndex == -1) {
                columnIndex = 0
              }
              let targetArea = {
                x: columnIndex,
                y: rowIndex,
                width: columnCount,
                height: rowCount
              }
              return this.areaData.findIndex((area) => {
                if (commonUtil.isAreaRangeEquals(targetArea, area) && area.type == areaType) {
                  return true
                } else {
                  return false
                }
              })
            },
            getArea(rowIndex, columnIndex, rowCount, columnCount, areaType) {
              let index = this.findAreaIndex(rowIndex, columnIndex, rowCount, columnCount, areaType)
              if (index == -1) {
                return null
              } else {
                return copyDataSimple(this.areaData[index])
              }
            },
            getAllAreaOfAreaType(areaType) {
              return copyDataSimple(this.areaData.filter(v => v.type == areaType))
            },
            // 获取以当前单元格为标题显示单元格的区域
            getTitleAreas(rowIndex, columnIndex) {
              return this.titleCellKeyAreaMap[getCellKey(rowIndex, columnIndex)]
            },
            getAreaTitle(areaId) {
              return this.areaTitleMap[areaId]
            },

            _deleteCellControls(cellControlsWithIndex) {
              if (cellControlsWithIndex) {
                let indexes = cellControlsWithIndex.map(v => v.index)
                let ranges = commonUtil.integersToRange(indexes)
                let delNum = 0
                ranges.forEach(([startIndex, endIndex]) => {
                  let length = endIndex - startIndex + 1
                  this.controlData.splice(startIndex - delNum, length)
                  delNum += length
                })
              }
            },

            // 根据目标区域查找控制信息
            findControlsByTargetAreaId(targetAreaId) {
              return this.controlData.filter(v => v.targetAreaId == targetAreaId)
            },

            getAllCellControls(row, col) {
              return this.cellControlsMap[getCellKey(row, col)]
            },
            deleteAllCellControls(row, col) {
              let cellControls = this.getAllCellControls(row, col)
              this._deleteCellControls(cellControls)
              this.resetControlTag(row, col)
            },

            getAllCellOrderControls(row, col) {
              let cellControls = []
              let cellPreposedCellControls = this.getCellPreposedCellControls(row, col)

              if (cellPreposedCellControls) {
                cellControls.splice(0, 0, ...cellPreposedCellControls)
              }

              let cellPreposedAreaControls = this.getCellPreposedAreaControls(row, col)
              if (cellPreposedAreaControls) {
                cellControls.splice(0, 0, ...cellPreposedAreaControls)
              }

              let cellEntirelyFillControl = this.getCellEntirelyFillControl(row, col)
              if (cellEntirelyFillControl) {
                cellControls.splice(0, 0, cellEntirelyFillControl)
              }

              return cellControls.length > 0 ? cellControls : null
            },
            deleteAllCellOrderControls(row, col) {
              let cellControls = this.getAllCellOrderControls(row, col)
              this._deleteCellControls(cellControls)
              this.resetControlTag(row, col)
            },

            getAllSettingDefaultValueControls(row, col) {
              let cellControls = []
              let settingDefaultValueCellControls = this.getSettingDefaultValueCellControls(row, col)

              if (settingDefaultValueCellControls) {
                cellControls.splice(0, 0, ...settingDefaultValueCellControls)
              }

              let settingDefaultValueAreaControls = this.getSettingDefaultValueAreaControls(row, col)
              if (settingDefaultValueAreaControls) {
                cellControls.splice(0, 0, ...settingDefaultValueAreaControls)
              }

              return cellControls.length > 0 ? cellControls : null
            },
            deleteAllSettingDefaultValueControls(row, col) {
              let cellControls = this.getAllCellOrderControls(row, col)
              this._deleteCellControls(cellControls)
              this.resetControlTag(row, col)
            },

            getCellPreposedCellControls(row, col) {
              return this.cellPreposedCellControlsMap[getCellKey(row, col)]
            },
            deleteCellPreposedCellControls(row, col) {
              let cellControls = this.getCellPreposedCellControls(row, col)
              this._deleteCellControls(cellControls)
              this.resetControlTag(row, col)
            },
            setCellPreposedCellControls(row, col, cellPositions) {
              if (this.getCellPreposedCellControls(row, col)) {
                this.deleteCellPreposedCellControls(row, col)
              }
              cellPositions.forEach(([targetRow, targetCol]) => {
                this.controlData.push({
                  sourceRow: row,
                  sourceCol: col,
                  targetRow,
                  targetCol,
                  type: ControlTypeEnum.PREMISS_CTRL// 1--完全回填  2--回填条件  3--默认回填值
                })
              })
              this.resetControlTag(row, col)
            },

            getCellPreposedAreaControls(row, col) {
              return this.cellPreposedAreaControlsMap[getCellKey(row, col)]
            },
            deleteCellPreposedAreaControls(row, col) {
              let cellControls = this.getCellPreposedAreaControls(row, col)
              this._deleteCellControls(cellControls)
              this.resetControlTag(row, col)
            },
            setCellPreposedAreaControls(row, col, areaIds) {
              if (this.getCellPreposedAreaControls(row, col)) {
                this.deleteCellPreposedAreaControls(row, col)
              }
              areaIds.forEach(areaId => {
                if (this.areaMap[areaId]) {
                  this.controlData.push({
                    sourceRow: row,
                    sourceCol: col,
                    targetAreaId: areaId,
                    type: ControlTypeEnum.PREMISS_CTRL// 1--完全回填  2--回填条件  3--默认回填值
                  })
                }
              })
              this.resetControlTag(row, col)
            },

            getCellEntirelyFillControl(row, col) {
              return this.cellEntirelyFillControlMap[getCellKey(row, col)]
            },
            deleteCellEntirelyFillControl(row, col) {
              let cellControl = this.getCellEntirelyFillControl(row, col)
              if (cellControl) {
                this._deleteCellControls([cellControl])
                this.resetControlTag(row, col)
              }
            },
            canSetCellEntirelyFillControl(row, col) {
              let attr = this.getPropertyKey(row, col)
              return !(attr && this.isAttributeRegistered(attr) && this.isValueRequiredAttribute(attr))
            },
            setCellEntirelyFillControl(row, col) {
              if (this.getCellEntirelyFillControl(row, col)) {
                this.deleteCellEntirelyFillControl(row, col)
              }
              if (this.canSetCellEntirelyFillControl(row, col)) {
                this.controlData.push({
                  sourceRow: row,
                  sourceCol: col,
                  type: ControlTypeEnum.ENTIRELY_FILL_CTRL// 1--完全回填  2--回填条件  3--默认回填值
                })
                this.resetControlTag(row, col)
              } else {
                let attr = this.getPropertyKey(row, col)
                console.error(`属性：${attr}为必填属性，不可设置完全回填后可填写`)
              }
            },

            getSettingDefaultValueCellControls(row, col) {
              return this.settingDefaultValueCellControlsMap[getCellKey(row, col)]
            },
            deleteSettingDefaultValueCellControls(row, col) {
              let cellControls = this.getSettingDefaultValueCellControls(row, col)
              this._deleteCellControls(cellControls)
              this.resetControlTag(row, col)
            },
            setSettingDefaultValueCellControls(row, col, cellPositions) {
              if (this.getSettingDefaultValueCellControls(row, col)) {
                this.deleteSettingDefaultValueCellControls(row, col)
              }
              cellPositions.forEach(([targetRow, targetCol]) => {
                this.controlData.push({
                  sourceRow: row,
                  sourceCol: col,
                  targetRow,
                  targetCol,
                  type: ControlTypeEnum.DEFAULT_SETTING_CTRL// 1--完全回填  2--回填条件  3--默认回填值
                })
              })
              this.resetControlTag(row, col)
            },

            getSettingDefaultValueAreaControls(row, col) {
              return this.settingDefaultValueAreaControlsMap[getCellKey(row, col)]
            },
            deleteSettingDefaultValueAreaControls(row, col) {
              let cellControls = this.getSettingDefaultValueAreaControls(row, col)
              this._deleteCellControls(cellControls)
              this.resetControlTag(row, col)
            },
            setSettingDefaultValueAreaControls(row, col, areaIds) {
              if (this.getSettingDefaultValueAreaControls(row, col)) {
                this.deleteSettingDefaultValueAreaControls(row, col)
              }
              areaIds.forEach(areaId => {
                if (this.areaMap[areaId]) {
                  this.controlData.push({
                    sourceRow: row,
                    sourceCol: col,
                    targetAreaId: areaId,
                    type: ControlTypeEnum.DEFAULT_SETTING_CTRL// 1--完全回填  2--回填条件  3--默认回填值
                  })
                }
              })
              this.resetControlTag(row, col)
            },

            getTagObjs(ctrls) {
              return ctrls.map((ctrl) => {
                let tagObj = {}
                if (!isBlank(ctrl.targetRow) && !isBlank(ctrl.targetCol)) {
                  tagObj.row = ctrl.targetRow
                  tagObj.col = ctrl.targetCol
                } else if (!isBlank(ctrl.targetAreaId)) {
                  tagObj.areaId = ctrl.targetAreaId
                }
                tagObj.t = ctrl.type
                return tagObj
              })
            },
            resetControlTag(row, col, sheet) {
              let cell = (sheet ? sheet : getSpread().getActiveSheet()).getCell(row, col)
              let ctrls = this.getAllCellControls(row, col)
              if (ctrls && ctrls.length > 0) {
                setCellTagAttr(cell, "controls", this.getTagObjs(ctrls))
              } else {
                deleteCellTagAttr(cell, "controls")
              }
            },

            doInAllCellReadOnly(sheet) {
              // 暂停渲染
              sheet.suspendPaint()
              let sheetRowCount = sheet.getRowCount()
              let sheetColCount = sheet.getColumnCount()
              let lockedCells = []
              // 锁定所有单元格
              commonUtil.everyCell(0, 0, sheetRowCount, sheetColCount, (row, col) => {
                let style = sheet.getStyle(row, col)
                let locked = style.locked
                if (locked != null && !locked) {
                  lockedCells.push([row, col])
                  style.locked = true
                  sheet.setStyle(row, col, style)
                }
              })
              let spread = sheet.getParent()
              let keyEnum = GC.Spread.Commands.Key
              let tempRemovedShortcutKeys = [
                {
                  cmd: "copy",
                  key: keyEnum.c,
                  ctrl: true
                },
                {
                  cmd: "cut",
                  key: keyEnum.x,
                  ctrl: true
                },
                {
                  cmd: "paste",
                  key: keyEnum.v,
                  ctrl: true
                },
                {
                  cmd: "undo",
                  key: keyEnum.z,
                  ctrl: true
                },
                {
                  cmd: "redo",
                  key: keyEnum.y,
                  ctrl: true
                },
              ]
              // 禁用所有能够直接操作单元格的快捷键
              tempRemovedShortcutKeys.forEach((obj) => {
                spread.commandManager().setShortcutKey(obj.cmd, null, null)
              })
              // 恢复渲染
              sheet.resumePaint()
              return () => {
                // 暂停渲染
                sheet.suspendPaint()
                // 取消锁定单元格
                lockedCells.forEach(([row, col]) => {
                  let style = sheet.getStyle(row, col)
                  style.locked = false
                  sheet.setStyle(row, col, style)
                })
                // 取消锁定所有单元格
                tempRemovedShortcutKeys.forEach((obj) => {
                  spread.commandManager().setShortcutKey(obj.cmd, obj.key, obj.ctrl)
                })
                // 恢复渲染
                sheet.resumePaint()
              }
            },
            getIsInSelectMode() {
              return this.isInSelectMode
            },
            selectCellsInSelectMode(sheet, initCellPositions) {
              if (this.isInSelectMode) {
                throw "请先结束正在进行的选择模式"
              }
              // 暂停渲染
              sheet.suspendPaint()

              this.isInSelectMode = true
              let selectedCellBackColor = commonUtil.parseRgbString("rgba(255,255,0,0.3)")
              let layerName = "_select_mode_"
              let refreshBackColors = (cellPositions) => {
                backColorLayerManager.removeAllRgbaBackColor(layerName)
                cellPositions.forEach(([row, col]) => {
                  backColorLayerManager.pushRgbaBackColor(row, col, layerName, selectedCellBackColor)
                })
                if (!backColorLayerManager.isInTempToOriBackColor()) {
                  backColorLayerManager.setBackColorToSheet(sheet)
                }
              }
              initCellPositions && refreshBackColors(initCellPositions)

              let vueObj = new Vue({
                data() {
                  return {
                    cellPositionMap: commonUtil.listToMap(initCellPositions ? copyDataSimple(initCellPositions) : [], ([row, col]) => {
                      return getCellKey(row, col)
                    })
                  }
                },
                computed: {
                  cellPositions() {
                    let cellPositions = []
                    for (const cellKey in this.cellPositionMap) {
                      cellPositions.push(this.cellPositionMap[cellKey])
                    }
                    return cellPositions
                  },
                }
              })

              let endReadOnly = this.doInAllCellReadOnly(sheet)
              // 恢复渲染
              sheet.resumePaint()
              return {
                endSelectMode: () => {
                  backColorLayerManager.removeAllRgbaBackColor(layerName)
                  if (!backColorLayerManager.isInTempToOriBackColor()) {
                    backColorLayerManager.setBackColorToSheet(sheet)
                  }
                  endReadOnly()
                  this.isInSelectMode = false
                  return copyDataSimple(vueObj.cellPositions)
                },
                selectRanges: (ranges, cellFilter) => {
                  ranges.forEach(({
                    row: rangeRow,
                    rowCount: rangeRowCount,
                    col: rangeCol,
                    colCount: rangeColCount,
                  }) => {
                    commonUtil.everyCell(rangeRow, rangeCol, rangeRowCount, rangeColCount, (row, col) => {
                      if (!commonUtil.isHideBySpan(rangeRow, rangeCol, sheet)) {
                        let cellKey = getCellKey(row, col)
                        if ((!cellFilter || cellFilter(row, col)) && !vueObj.cellPositionMap[cellKey]) {
                          vueObj.$set(vueObj.cellPositionMap, cellKey, [row, col])
                        }
                      }
                    })
                  })
                  refreshBackColors(vueObj.cellPositions)
                },
                unselectRanges: (ranges, cellFilter) => {
                  ranges.forEach(({
                    row: rangeRow,
                    rowCount: rangeRowCount,
                    col: rangeCol,
                    colCount: rangeColCount,
                  }) => {
                    commonUtil.everyCell(rangeRow, rangeCol, rangeRowCount, rangeColCount, (row, col) => {
                      let cellKey = getCellKey(row, col)
                      if (vueObj.cellPositionMap[cellKey] && (!cellFilter || cellFilter(row, col))) {
                        vueObj.$delete(vueObj.cellPositionMap, cellKey)
                      }
                    })
                  })
                  refreshBackColors(vueObj.cellPositions)
                },
                getSelectedCellPositions() {
                  return copyDataSimple(vueObj.cellPositions)
                }
              }
            },
            getBackColorLayerManager() {
              return backColorLayerManager
            },

            checkBillBackfillIsOver() {// TODO 判断是否完成了回填，此方法未使用
              let sheet = getSpread().getActiveSheet()
              let findedErrorCellProp = cellPropertyDataManager.findCellData((row, col, cellData) => {
                if (cellData) {
                  // 过滤掉因为跨行跨列而被隐藏的单元格
                  if (!commonUtil.isHideBySpan(row, col, sheet)) {
                    let propKey = cellPropertyDataManager.getPropertyKeyFromCellData(cellData)
                    let regAttrObj = this.operationalAttributesMap[propKey]
                    if (regAttrObj && regAttrObj.validateCell) {

                      let rs = regAttrObj.validateCell(innerCellIdManager.getInnerCellId(row, col), sheet)
                      if (!rs.isValidated()) {
                        return true
                      }
                    }
                  }
                }
                return false
              })
              if (findedErrorCellProp) {
                return false
              } else {
                return true
              }
            },
            validateTemplate() {// 由于需要使用sheet内部方法，请在暂停渲染后调用此方法
              let sheet = getSpread().getActiveSheet()
              let pass = true
              let msg = null
              // try {
              //   if (this.areaData.length == 0) {
              //     pass = false
              //     msg = "存在没有回填区域的单据"
              //     throw "_"
              //   }
              //   this.everyCellWithPropertyOrArea(({ row, col }) => {
              //     let spanInfo = sheet.getSpan(row, col)
              //     if (spanInfo) {
              //       row = spanInfo.row
              //       col = spanInfo.col
              //     }
              //     if (this.getProperty(row, col) == null) {
              //       pass = false
              //       msg = "回填区域内存在未设置属性的单元格"
              //       throw "_"
              //     }
              //   })
              // } catch (error) {
              // }
              return {
                pass,
                msg
              }
            },

          },
          watch: {
          }
        })

        setUndoManager(new UndoManager({
          setData(data) {
            vueCore.setInnerData(data)
          },
          getData() {
            return vueCore.getInnerData()
          }
        }))

        return vueCore
      }
    })(getCellDataManagerGroup, getPropClipboard)

    var UndoManager = (function () {
      function UndoManager({
        getData,
        setData
      }) {
        this.getData = getData
        this.setData = setData
        this.commandDeepMap = new Map()
      }
      let getSheetId = (() => {
        let sheetIdMap = new Map()
        let n = 1
        return (sheet) => {
          if (!sheetIdMap.has(sheet)) {
            sheetIdMap.set(sheet, n++)
          }
          return sheetIdMap.get(sheet)
        }
      })()

      UndoManager.prototype.commandDeepMap = null

      UndoManager.prototype.getCommandDeep = function (sheet) {
        if (this.commandDeepMap.has(sheet)) {
          return this.commandDeepMap.get(sheet)
        } else {
          return 0
        }
      }

      UndoManager.prototype.setCommandDeep = function (sheet, commandDeep) {
        this.commandDeepMap.set(sheet, commandDeep)
      }

      UndoManager.prototype.extraOptionPropertyNameBase = "_bill_property_data_"

      UndoManager.prototype.getExtraOptionPropertyName = function (sheet) {
        return this.extraOptionPropertyNameBase + getSheetId(sheet)
      }

      UndoManager.prototype.beforeCommand = function (context, options) {
        everySheetName(context, options.sheetName, (sheetName) => {
          this.beforeCommandInSheet(context.getSheetFromName(sheetName), options)
        })
      }

      UndoManager.prototype.beforeCommandInSheet = function (sheet, scope) {
        if (this.getCommandDeep(sheet) === 0) {
          scope[this.getExtraOptionPropertyName(sheet)] = JSON.stringify(this.getData())
        }
        this.setCommandDeep(sheet, this.getCommandDeep(sheet) + 1)
      }

      UndoManager.prototype.afterCommand = function (context, options) {
        everySheetName(context, options.sheetName, (sheetName) => {
          this.afterCommandInSheet(context.getSheetFromName(sheetName), options)
        })
      }

      UndoManager.prototype.afterCommandInSheet = function (sheet, scope) {
        this.setCommandDeep(sheet, this.getCommandDeep(sheet) - 1)
        if (this.getCommandDeep(sheet) === 0 && !scope[this.getExtraOptionPropertyName(sheet)]) {
          throw "程序异常"
        }
      }

      // TODO 实际多表格会报错，因为实际执行的setData不区分
      UndoManager.prototype.undo = function (context, options) {
        everySheetName(context, options.sheetName, (sheetName) => {
          this.undoInSheet(context.getSheetFromName(sheetName), options)
        })
      }

      UndoManager.prototype.undoInSheet = function (sheet, scope) {
        let extraOptionPropertyName = this.getExtraOptionPropertyName(sheet)
        if (scope[extraOptionPropertyName]) {
          this.setData(JSON.parse(scope[extraOptionPropertyName]))
        }
      }
      return UndoManager
    })()

    function BillSpreadModule(config) {
      getData(this).vueCore = getVueCoreObj({
        getSpread: () => {
          let spread = this.getSpread()
          if (!spread) {
            throw "请先初始化spread"
          } else {
            return spread
          }
        },
        getUndoManager: () => {
          return getData(this).undoManager
        },
        setUndoManager: undoManager => {
          getData(this).undoManager = undoManager
        }
      })
      this.setConfig(config == null ? getDefaultConfig() : mergeConfigs(getDefaultConfig(), config))
    }

    BillSpreadModule.prototype.getConfig = function () {
      let config = getData(this).config
      return config ? mergeConfigs({}, config) : {}
    }

    BillSpreadModule.prototype.setConfig = function (config) {
      if (config != null) {
        let mergedConfig = mergeConfigs(this.getConfig(), config)
        getData(this).config = mergedConfig
        getData(this).vueCore.setConfig(mergedConfig)
      }
    }

    BillSpreadModule.prototype.getDefaultConfig = function () {
      return getDefaultConfig()
    }

    BillSpreadModule.prototype.mountSpread = function (el) {
      if (el instanceof HTMLDivElement) {
        if (this.getSpread()) {
          throw "spread工作簿已挂载过，禁止重复挂载"
        }
        if (dataMapUtil.hasData(el)) {
          throw "当前元素已经挂载过，禁止重复挂载，如果想重新挂载请先执行之前挂载此元素的BillSpreadModule对象的destroy方法来对其进行销毁"
        }
        getData(this).spread = new Sheets.Workbook(el)
        dataMapUtil.setData(el, this)
        getData(this).uninstallSpread = getData(this).vueCore.installSpread()
      } else {
        throw "请传入div元素对象"
      }
    }

    BillSpreadModule.prototype.setSpread = function (spread) {
      if (this.getSpread()) {
        throw "spread工作簿已挂载过，禁止重复挂载"
      }
      if (spread instanceof Sheets.Workbook) {
        dataMapUtil.setData(spread.getHost(), this)
        getData(this).spread = spread
        getData(this).uninstallSpread = getData(this).vueCore.installSpread()
      } else {
        throw "请传入GC.Spread.Sheets.Workbook类型对象"
      }
    }

    BillSpreadModule.prototype.getSpread = function () {
      return getData(this).spread
    }

    BillSpreadModule.prototype.destroy = function () {
      if (getData(this).uninstallSpreadAfterSetTemplateData) {
        getData(this).uninstallSpreadAfterSetTemplateData()
        getData(this).uninstallSpreadAfterSetTemplateData = null
      }
      if (getData(this).uninstallSpreadAfterSetPropertyData) {
        getData(this).uninstallSpreadAfterSetPropertyData()
        getData(this).uninstallSpreadAfterSetPropertyData = null
      }
      if (getData(this).uninstallSpread) {
        getData(this).uninstallSpread()
        getData(this).uninstallSpread = null
      }
      let spread = this.getSpread()
      spread.destroy()
      dataMapUtil.deleteData(spread.getHost())
      getData(this).vueCore.$destroy()
      dataMapUtil.deleteData(this)
    }

    BillSpreadModule.prototype.setIsShowMode = function (isShowMode) {
      getData(this).vueCore.isShowMode = !!isShowMode
    }

    BillSpreadModule.prototype.getIsShowMode = function () {
      return getData(this).vueCore.isShowMode
    }

    // 初始化所有数据
    BillSpreadModule.prototype.initAllData = function ({
      ssjsonDataString,
      billData,
      propertyTree,
      propertyOptionList,
      userData
    }) {

      // 进行全部数据初始化
      // 设置属性数据
      this.setPropertyData({ propertyTree, propertyOptionList })
      // 设置模板数据
      this.setTemplateData({
        ssjsonDataString,
        billData
      })
      this.setUserData(userData)
    }

    // 设置用户数据 {id, name, roleInfoList:[{code, name}...]}
    BillSpreadModule.prototype.setUserData = function (userData) {
      getData(this).vueCore.setUserData(userData)
    }

    // 设置属性数据
    BillSpreadModule.prototype.setPropertyData = function ({
      propertyTree,
      propertyOptionList
    }) {
      // 如果之前有绑定过则先按照旧数据进行解绑
      if (getData(this).uninstallSpreadAfterSetPropertyData) {
        getData(this).uninstallSpreadAfterSetPropertyData()
        getData(this).uninstallSpreadAfterSetPropertyData = null
      }

      getData(this).vueCore.setPropertyData({
        propertyTree,
        propertyOptionList
      })

      // 进行需要初始化数据之后的spread绑定，留存解绑函数
      getData(this).uninstallSpreadAfterSetPropertyData = getData(this).vueCore.installSpreadAfterSetPropertyData()
    }

    // 获取模板数据
    BillSpreadModule.prototype.getTemplateData = function (localUse) {
      return getData(this).vueCore.getTemplateData(localUse) //{ ssjsonDataString, billData: {cells, areas, extraObject} }
    }

    // 获取记录单数据
    BillSpreadModule.prototype.getBillData = function (localUse) {
      return getData(this).vueCore.getBillData(localUse) //{cells, areas, extraObject}
    }

    // 设置模板数据
    BillSpreadModule.prototype.setTemplateData = function ({
      ssjsonDataString,
      billData: {
        cells,
        areas,
        extraObject
      }
    }, localUse) {
      // 如果之前有绑定过则先按照旧数据进行解绑
      if (getData(this).uninstallSpreadAfterSetTemplateData) {
        getData(this).uninstallSpreadAfterSetTemplateData()
        getData(this).uninstallSpreadAfterSetTemplateData = null
      }

      getData(this).vueCore.setTemplateData({
        ssjsonDataString,
        billData: {
          cells,
          areas,
          extraObject
        }
      }, localUse)
      // 进行需要初始化单元格数据之后的spread绑定，留存解绑函数
      getData(this).uninstallSpreadAfterSetTemplateData = getData(this).vueCore.installSpreadAfterSetTemplateData()
    }

    // 清空模板数据
    BillSpreadModule.prototype.clearTemplateData = function () {
      this.setTemplateData({
        ssjsonDataString: null,
        billData: {
          cells: [],
          areas: [],
          extraObject: {}
        }
      })
    }

    BillSpreadModule.exportExcelBlobFromData = async function (allData) {
      // 创建临时BillSpreadModule对象
      let tempBillSpreadModuleObj = new BillSpreadModule()
      // 挂载一个空div元素
      tempBillSpreadModuleObj.mountSpread(document.createElement("div"))
      // 初始化数据
      tempBillSpreadModuleObj.initAllData(allData)
      // 执行导出
      let blob = await tempBillSpreadModuleObj.exportExcelBlob()
      // 销毁临时创建的BillSpreadModule对象
      tempBillSpreadModuleObj.destroy()
      return blob
    }

    BillSpreadModule.exportPdfBlobFromData = async function (allData, printInfo) {
      // 创建临时BillSpreadModule对象
      let tempBillSpreadModuleObj = new BillSpreadModule()
      // 挂载一个空div元素
      tempBillSpreadModuleObj.mountSpread(document.createElement("div"))
      // 初始化数据
      tempBillSpreadModuleObj.initAllData(allData)
      // 执行导出
      let blob = await tempBillSpreadModuleObj.exportPdfBlob(printInfo)
      // 销毁临时创建的BillSpreadModule对象
      tempBillSpreadModuleObj.destroy()
      return blob
    }

    let simpleMapMethods = [
      "checkBillBackfillIsOver", // 检测是否回填完毕
      "validateTemplate",
      "isSignAttribute",
      "isValueRequiredAttribute",
      "isReadOnlyAttribute",
      // "getProperty",
      "getPropertyKey",
      "hasProperty",
      "setProperty",
      "deleteProperty",

      "setArea",
      "deleteArea",
      "getArea",
      "getAllAreaOfAreaType",
      "getAreaTitle",
      "findAreaInfo",

      "findControlsByTargetAreaId",
      "getAllCellControls",
      "deleteAllCellControls",

      "getAllCellOrderControls",
      "deleteAllCellOrderControls",

      "getAllSettingDefaultValueControls",
      "deleteAllSettingDefaultValueControls",

      "getCellPreposedCellControls",
      "setCellPreposedCellControls",
      "deleteCellPreposedCellControls",

      "getCellPreposedAreaControls",
      "setCellPreposedAreaControls",
      "deleteCellPreposedAreaControls",

      "getCellEntirelyFillControl",
      "setCellEntirelyFillControl",
      "deleteCellEntirelyFillControl",
      "canSetCellEntirelyFillControl",

      "getSettingDefaultValueCellControls",
      "setSettingDefaultValueCellControls",
      "deleteSettingDefaultValueCellControls",

      "getSettingDefaultValueAreaControls",
      "setSettingDefaultValueAreaControls",
      "deleteSettingDefaultValueAreaControls",

      "selectCellsInSelectMode",
      "getIsInSelectMode",
      "getBackColorLayerManager",

      "exportExcelBlob",
      "exportPdfBlob",
      "getExportJSON",
      "updateBillData",

    ]

    simpleMapMethods.forEach(methodName => {
      BillSpreadModule.prototype[methodName] = function (...args) {
        return getData(this).vueCore[methodName](...args)
      }
    })

    BillSpreadModule.prototype.vueCore = function () {
      return getData(this).vueCore
    }
    BillSpreadModule.commonUtil = commonUtil
    BillSpreadModule.AreaTypeEnum = AreaTypeEnum

    return BillSpreadModule
  })()

  Vue.component('bill-spread-module', {
    name: 'BillSpreadModule',
    template: `
      <div :style="outerWrapperStyle">
        <div :style="innerWrapperStyle">
          <div ref="spreadHost" :style="spreadHostStyle"></div>
          <div 
            ref="statusBar" 
            :class="statusBarClassName"
            :style="statusBarStyle"
          ></div>
        </div>
      </div>
    `,
    props: {
      mainId: {// 仅初始化时生效
        type: String,
        default: null,
      },
      config: {
        type: Object,
        default: null,
      },
      borderWidth: {// 边框宽度，单位: px
        type: Number,
        default: 3
      },
      statusBarClassName: {
        type: String,
        default: null,
      },
      statusBarHeight: {// 状态栏高度，单位: px
        type: Number,
        default: 22
      }
    },
    data() {
      return {
        billSpreadModule: null,// 组件对象实例
        statusBar: null,// 状态栏实例
      }
    },
    computed: {
      outerWrapperStyle() {
        return {
          width: `100%`,
          height: `100%`,
          position: "relative"
        }
      },
      innerWrapperStyle() {
        return {
          border: `black ${this.borderWidth}px solid`,
          width: `calc(100% - ${this.borderWidth * 2 + 1}px)`,
          height: `calc(100% - ${this.borderWidth * 2 + 1}px)`,
          position: "absolute"
        }
      },
      spreadHostStyle() {
        return {
          width: `100%`,
          height: `calc(100% - ${this.statusBarHeight}px)`,
          border: "none"
        }
      },
      statusBarStyle() {
        return {
          height: this.statusBarHeight + "px",
          backgroundColor: "#217346"
        }
      },
    },
    methods: {
      getModuleInstance() {// 获取组件实例
        if (this.billSpreadModule) {
          return this.billSpreadModule
        } else {
          throw "BillSpreadModule实例尚未初始化完毕"
        }
      },
      refreshPaint() {
        this.$nextTick(() => {
          this.getSpread().refresh()
        })
      },
      ...(() => {
        let obj = {}
        let hiddenFns = [
          "setConfig",
          "mountSpread",
          "setSpread",
          "destroy"
        ]
        for (const fnName in context.BillSpreadModule.prototype) {
          if (hiddenFns.every(v => v != fnName)) {
            if (fnName == "getConfig") {
              obj.getFinalConfig = function () {
                return this.getModuleInstance().getConfig()
              }
            } else {
              obj[fnName] = function (...args) {
                return this.getModuleInstance()[fnName](...args)
              }
            }
          }
        }
        return obj
      })()
    },
    mounted() {
      if (this.mainId) {
        this.$refs.spreadHost.id = this.mainId
      }
      // 初始化
      this.billSpreadModule = new context.BillSpreadModule(this.config)
      this.billSpreadModule.mountSpread(this.$refs.spreadHost)

      this.statusBar = new GC.Spread.Sheets.StatusBar.StatusBar(this.$refs.statusBar)
      this.statusBar.bind(this.billSpreadModule.getSpread())
    },
    watch: {
      config: {
        deep: true,
        handler(v) {
          let commonUtil = context.BillSpreadModule.commonUtil
          let defaultConfig = this.getModuleInstance().getDefaultConfig()
          this.getModuleInstance().setConfig(commonUtil.mergeConfigs(defaultConfig, v))
        }
      },
      mainId() {
        console.error("BillSpreadModule：mainId仅初始化时生效，不可中途变更")
      },
      spreadHostStyle: "refreshPaint",
      innerWrapperStyle: "refreshPaint"
    },
    beforeDestroy() {
      this.statusBar.unbind(this.billSpreadModule.getSpread())
      this.statusBar = null
      this.billSpreadModule.destroy()
      this.billSpreadModule = null
    }
  })

  function validateSpreadDependencies(v, context) {
    let baseErrorMsg = "BillSpreadModule初始化失败"
    if (!context.GC || !context.GC.Spread || !context.GC.Spread.Sheets) {
      throw `${baseErrorMsg}，缺少依赖：gc.spread.sheets.all.${v}.min.js`
    }
    if (context.GC.Spread.Sheets.productInfo.productVersion != spreadVersion) {
      throw `${baseErrorMsg}，spread版本错误，期望版本：${v}`
    }
  }

  function validateOtherDependencies(context) {
    let baseErrorMsg = "BillSpreadModule初始化失败"
    if (!context.Vue) {
      throw `${baseErrorMsg}，缺少依赖：vue.min.js`
    }
  }

})(window)