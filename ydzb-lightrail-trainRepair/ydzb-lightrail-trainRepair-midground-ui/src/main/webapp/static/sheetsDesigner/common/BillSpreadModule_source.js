/*!
 *
 * BillSpreadModule 1.0
 *
 * by 张天可
 *
 */
(function (context) {
  // 版本升级需要重新变更的标记: VERSION_CHECK
  const VERSION_CHECK_ERROR = "spread依赖版本(打包批次)变更错误"
  var spreadVersion = "13.2.0" // 请勿修改此版本号来避免报错，如果想使用新版本spread请联系组件开发者
  validateSpreadDependencies(spreadVersion, context)
  validateOtherDependencies(context)

  let isObject = (value) => {
    return Object.prototype.toString.call(value) === "[object Object]"
  }

  let copyDataSimple = (data) => JSON.parse(JSON.stringify(data))

  let wrapFn = (obj, name, fn) => {
    // 以包装的方式扩展对象方法
    let ori = obj[name]
    obj[name] = function (...args) {
      return fn.call(this, () => {
        return ori && ori.apply(this, args)
      }, args)
    }
    return ori
  }

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
    let Sheets = GC.Spread.Sheets
    let getDefaultConfig = () => {
      return {
        readOnly: false, // 是否只读
        configMode: false, // 是否为模板编辑模式
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
            if (!config.configMode) {
              return cell.code
            }
          },
          setCellCode(cell, code, config) {
            if (!config.configMode) {
              cell.code = code
            }
          },
          // 关联单元格只写不读
          setLinkCells(cell, linkCells, config) {
            cell.linkCells = linkCells
          },
          // 单元格是否保存只读不写
          getCellSaved(cell, config) {
            if (!config.configMode) {
              return cell.saved
            }
          },

          getLinkCellId(cell, config) {
            return cell.linkCellId
          },
          setLinkCellId(cell, cellId, config) {
            cell.linkCellId = value
          },
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
            config[key] = value
          }
        }
      }
      return config
    }

    let getCellPosFromCellData = (cell, config) => {
      let row = null
      let col = null
      let {
        getCellRow,
        getCellCol
      } = config.cellDataConfig
      row = getCellRow(cell, config)
      col = getCellCol(cell, config)
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
  height: ${height} !important;
}`
          tabStripStyleEl.id = tabStripStyleId
          document.head.append(tabStripStyleEl)
        }
      }
    })()


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

      var listener = function (e) {
        var x = e.offsetX
        var y = e.offsetY
        var rs = spread.hitTest(x, y)

        if (!rs || !rs.worksheetHitInfo) {
          this.title = ""
          return
        }

        var hitInfo = rs.worksheetHitInfo
        var rowViewportIndex = hitInfo.rowViewportIndex
        var colViewportIndex = hitInfo.colViewportIndex

        if (rowViewportIndex === 1 && colViewportIndex === 1) {
          if ("configMode" in vueCore.getConfig()) {
            let {
              row,
              col
            } = hitInfo
            let {
              configMode,
              propertyTreeConfig: {
                getName
              }
            } = vueCore.getConfig()
            let cellKey = row + "," + col
            if (configMode) {
              var property = vueCore.getProperty(row, col)
              setTitle(property ? getName(property) : "", cellKey)
            } else {
              var value = spread.getActiveSheet().getValue(row, col)
              setTitle(value ? value.toString() : "", cellKey)
            }
          }
        } else {
          this.title = ""
        }
      }

      hostEl.addEventListener("mousemove", listener)
      return () => {
        hostEl.removeEventListener("mousemove", listener)
      }
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

    const BillCellChangeTypeEnum = (() => {
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
        SIGN: "SignControl",
        OTHER: "OtherControl",
        BUTTON: "ButtonControl",
        EDITABLE_SELECT: "DropDownUpdateControl",
        RADIO: "SingleChoiceControl",
        DATE_TIME: 8,
        DATE: 9,
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
          "SignControl": {
            description: "签字",
            hasDefaultValue: false,
          },
          "OtherControl": {
            description: "其他",
            hasDefaultValue: false,
          },
          "ButtonControl": {
            description: "按钮",
            hasDefaultValue: false,
          },
          "DropDownUpdateControl": {
            description: "下拉选项（可编辑）",
            hasDefaultValue: true,
            onlyDefault: false
          },
          "SingleChoiceControl": {
            description: "单选",
            hasDefaultValue: false,
          },
          8: {
            description: "时间控件",
            hasDefaultValue: false,
          },
          8: {
            description: "日期控件",
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
      let mergeAlpha = (alpha1, alpha2) => {
        return alpha1 + alpha2 - alpha1 * alpha2
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
        everyCell(rowIndex, columnIndex, rowCount, columnCount, callback) {
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
        mergeColors(colors) {
          let finalColor = colors[0]
          for (let index = 1; index < colors.length; index++) {
            const color = colors[index]
            finalColor = mergeTwoColors(finalColor, color)
          }
          return finalColor
        },
        parseRgbString(rgb) {
          if (typeof rgb == "string") {
            var result = /^rgb[a]?\([ \n]*([\d]+)[ \n]*,[ \n]*([\d]+)[ \n]*,[ \n]*([\d]+)[ \n]*,?[ \n]*([.\d]+)?[ \n]*\)$/i.exec(rgb)
            var rgbObj = {}
            rgbObj.r = parseInt(result[1])
            rgbObj.g = parseInt(result[2])
            rgbObj.b = parseInt(result[3])
            if (result.length > 4 && result[4] != null) {
              rgbObj.a = parseFloat(result[4])
            }
            rgbObj.originColor = rgb
            return rgbObj
          } else {
            return null
          }
        },
        toRgbaString,
        toRgbString,
        colorObjToString(colorObj) {
          if (colorObj.a != null) {
            return toRgbaString(colorObj)
          } else {
            return toRgbString(colorObj)
          }
        },
        listToMap(list, getKey) {
          var map = {}
          list.forEach(v => {
            var key = getKey(v)
            if (key != null) {
              map[key] = v
            }
          })
          return map
        },
        integersToRange(integers) {
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
        isBlank(str) {
          if (str == null) {
            return true
          } else {
            if (str.toString().trim() == "") {
              return true
            } else {
              return false
            }
          }
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
        }
      }
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
      clearProtectInfo(ssjsonObj) {
        let namedStylesMap = this.getNamedStylesMap(ssjsonObj)
        this.everySheet(ssjsonObj, (sheet) => {
          let sheetTable = sheet.data.dataTable
          delete sheet.isProtected
          if (sheet.protectionOptions) {
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

    // TODO 内部属性尚未处理, 比如用户信息等
    let getOriOperationalAttributes = (vueCore) => {

      return [
        {
          code: "ATTR_NUMBERVALUE",
          formatter(value) {
            let fixNumber = v => {
              return parseFloat(v.toFixed(2))
            }
            if (typeof value == "number") {
              return fixNumber(value)
            }
            if (typeof value == "string") {
              try {
                let v = parseFloat(value)
                if (isNaN(v)) {
                  return null
                } else {
                  return fixNumber(v)
                }
              } catch (error) { }
            }
            return null
          },
          clear(row, col, sheet) {
            let cell = sheet.getCell(row, col)
            cell.formatter(null)
          }
        },
        {
          code: "ATTR_NA",
          onAttributeAdd(row, col, sheet) {
            let cell = sheet.getCell(row, col)
            cell.value("N/A")
          },
          onAttributeRemove(row, col, sheet) {
            sheet.setValue(row, col, "")
          },
          readOnlyOnEdit: true
        },
        {
          code: "ATTR_REPAIR_RESULT",

        },
        ...(() => {
          let signAttrs = [{
            code: "ATTR_FOREMAN_SIGN", // 具有检修工长角色的人可以点击，点击后写入当前登录人的姓名，如果已经存在该人员姓名，则提示是否撤销签字，如果选择是，取消本人签字；如果已经存在其他人名，人名之间用逗号间隔；
            role: "4",
            isNeedFillAll: true, // 检修工长签字必须要求完全回填
            signRequestConfig: {
              url: "/apiTrainRepairMidGround/billBack/batchSigns",
              method: "post",
              data: (billBacks, row, col, workerId, workerName, roleId) => {
                return {
                  ids: billBacks.map(v => v.checkListSummaryId).join(","),
                  backStatus: "1",
                  roleId, // : userInfo && userInfo.roles ? userInfo.roles.join(",") : ""
                  workerId,
                  workerName,
                  roleFlag: "ATTR_FOREMAN_SIGN",
                  x: row, // x行
                  y: col, // y列
                }
              }
            },
          },
          {
            code: "ATTR_REPAIR_PERSON", // 具有地勤机械师角色的人可以点击，点击后写入当前登录人的姓名，如果已经存在该人员姓名，则提示是否撤销签字，如果选择是，取消本人签字；如果已经存在其他人名，人名之间用逗号间隔；
            role: "6",
          },
          {
            code: "ATTR_QUALITY_SIGN", // 具有质检员角色的人可以点击，点击后写入当前登录人的姓名，如果已经存在该人员姓名，则提示是否撤销签字，如果选择是，取消本人签字；如果已经存在其他人名，人名之间用逗号间隔；
            role: "8",
            isNeedFillAll: true, // 检修工长签字必须要求完全回填
            signRequestConfig: {
              url: "/apiTrainRepairMidGround/billBack/batchSigns",
              method: "post",
              data: (billBacks, row, col, workerId, workerName, roleId) => {
                return {
                  ids: billBacks.map(v => v.checkListSummaryId).join(","),
                  backStatus: "1",
                  roleId,
                  workerId,
                  workerName,
                  roleFlag: "ATTR_QUALITY_SIGN",
                  x: row, // x行
                  y: col, // y列
                }
              }
            },
          }
          ]
          return signAttrs.map(attr => ({
            ...attr,
            readOnlyOnEdit: true,
            isSign: true,
            onAttributeAdd(row, col, sheet) {
              let cell = sheet.getCell(row, col)
              let value = cell.value()
              let fValue = this.formatter(value, row, col, sheet)
              if (!fValue) {
                cell.value(null)
              }
            },
            onAttributeRemove(row, col, sheet) {
              sheet.setValue(row, col, "")
            },
            formatter(value, row, col, sheet) {
              if (sheet) {
                let userCodeData = vueCore.cellPropertyData[row][col].cellCode// TODO cellPropertyDataManager.getExtraDataFromCellData
                let userCodes = userCodeData ? userCodeData.split(",") : []
                return userCodes.map(v => vueCore.userNameMap[v]).join(",")
              } else {
                return value
              }
            },
            install(spread) {
              let signRequest = (signRequestConfig, row, col) => {
                return new Promise((resolve, reject) => {
                  let config = {
                    ...signRequestConfig
                  }
                  if (typeof config.data == "function") {
                    config.data = config.data(
                      [vueCore.templateBasicData],
                      row,
                      col,
                      vueCore.userCode,
                      vueCore.userName,
                      vueCore.userData.roles ? vueCore.userData.roles.join(",") : ""
                    )
                  }
                  let loading = vueCore.$loading()
                  axios(config).then(resolve).catch(reject).finally(() => {
                    loading.close()
                  })
                })
              }
              this.listener = (sender, args) => {
                if (!vueCore.getConfig().configMode && !vueCore.isReadOnly && args.sheetArea === GC.Spread.Sheets.SheetArea.viewport) {
                  let {
                    row,
                    col,
                    sheet
                  } = args
                  let p = vueCore.getProperty(row, col)
                  if (p && p.attributeCode == this.code) {
                    // TODO 
                    if (!this.role || vueCore.isRole(this.role)) {
                      let doSign = () => {
                        let userCodeData = vueCore.cellPropertyData[row][col].cellCode// TODO cellPropertyDataManager.getExtraDataFromCellData
                        let codes = userCodeData ? userCodeData.split(",") : []
                        let userNameData = spread.getActiveSheet().getValue(row, col)
                        let names = userNameData ? userNameData.split(",") : []
                        let curUserIndex = codes.findIndex(v => v == vueCore.userCode)
                        let doSignInner = () => {
                          vueCore.cellPropertyData[row][col].cellCode = codes.join(",")// TODO cellPropertyDataManager.getExtraDataFromCellData
                          spread.getActiveSheet().setValue(row, col, names.join(","))
                        }
                        if (curUserIndex == -1) {
                          let localSign = () => {
                            // TODO
                            codes.push(vueCore.userCode)
                            names.push(vueCore.userName)
                            doSignInner()
                          }
                          if (this.signRequestConfig) {
                            signRequest(this.signRequestConfig, row, col).then(() => {
                              localSign()
                              vueCore.$message({
                                type: "success",
                                message: "签字成功",
                                showClose: true
                              })
                              localStorageParamsBus.sendParams(vueCore.pageCode, "refreshBackList", true)
                            })
                          } else {
                            localSign()
                          }
                        } else {
                          if (!this.signRequestConfig) {
                            app.showConfirm("是否撤销签字？").then(() => {
                              codes.splice(curUserIndex, 1)
                              names.splice(curUserIndex, 1)
                              doSignInner()
                            })
                          } else {
                            app.showWarn("已经签字！")
                          }
                        }

                      }
                      if (this.isNeedFillAll) {
                        if (vueCore.checkBillBackfillIsOver()) {
                          vueCore.afterSaveBillBack(null, doSign)
                        } else {
                          // vueMain.highlightInvalidData = true
                          app.showWarn("请填写完整后再进行" + this.name + "签字！")
                        }
                      } else {
                        doSign()
                      }
                    } else {
                      app.showWarn("您没有权限进行" + this.name + "签字！")
                    }

                  }
                }
              }
              spread.bind(GC.Spread.Sheets.Events.CellClick, this.listener)
            },
            uninstall(spread) {
              spread.unbind(GC.Spread.Sheets.Events.CellClick, this.listener)
            },
          }))
        })(),
      ]
    }
    let getAttributeOriOperationalByMode = (() => {
      let getCachedData = (vueCore, getInnerCellId) => {
        return new Vue({
          computed: {
            isDisableOptions() {// 是否禁用下拉列表，仅在回填只读模式下生效
              let config = vueCore.getConfig()
              return config.readOnly && !config.configMode
            },
            extraOptionValueListMap() {
              let cellDataListString = vueCore.originalData.cellDataListString
              if (cellDataListString) {
                let map = {}
                let config = vueCore.getConfig()
                let {
                  getCellRow,
                  getCellCol
                } = config.cellDataConfig
                let cellDataList = JSON.parse(cellDataListString)
                cellDataList.forEach(cellData => {
                  let cellId = getInnerCellId(
                    getCellRow(cellData, config),
                    getCellCol(cellData, config)
                  )
                  if (!map[cellId]) {
                    let extraOptionValues = cellData.extraOptions
                    if (extraOptionValues && extraOptionValues.length > 0) {
                      map[cellId] = extraOptionValues
                    }
                  } else {
                    console.error("单元格数据重复", cellData)
                  }
                })
                return map
              } else {
                return {}
              }
            }
          }
        })
      }
      let getOptionsAttribute = (editable, vueCore, getCellPosition, getInnerCellId) => {
        let cachedData = getCachedData(vueCore, getInnerCellId)
        let obj = {
          onAttributeAdd(innerCellId, sheet) {
            let endFns = []
            let onAttributeRemove = () => {
              let [row, col] = getCellPosition(innerCellId)
              if (!cachedData.isDisableOptions()) {
                sheet.setCellType(row, col, null)
              }
              let fn = null
              while (fn = endFns.shift()) {
                fn()
              }
            }
            let onAttributeAdd = () => {
              let [row, col] = getCellPosition(innerCellId)
              if (!cachedData.isDisableOptions()) {
                let comboBoxCellType = new GC.Spread.Sheets.CellTypes.ComboBox()
                let propertyOptionValues = vueCore.propertyOptionValueListMap[this.code] || []
                let extraOptionValues = cachedData.extraOptionValueListMap[innerCellId] || []
                let optionValues = [].concat(propertyOptionValues, extraOptionValues)
                comboBoxCellType.editable(editable)
                comboBoxCellType.items(optionValues)
                sheet.setCellType(row, col, comboBoxCellType)

                let endWatchCellExtraOptions = cachedData.$watch(() => {
                  return JSON.stringify(cachedData.extraOptionValueListMap[innerCellId] || [])
                }, () => {
                  onAttributeRemove()
                  onAttributeAdd()
                })
                endFns.unshift(() => endWatchCellExtraOptions())
              }
              let endWatchIsDisableOptions = cachedData.$watch("isDisableOptions", () => {
                onAttributeRemove()
                onAttributeAdd()
              })
              endFns.unshift(() => endWatchIsDisableOptions())
            }
            onAttributeAdd()
            return onAttributeRemove
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
                return new ValidateResult(false, "请选择列表之内的数据")
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
      let map = {
        [AttributeModeEnum.SELECT]: (vueCore, getCellPosition, getInnerCellId) => {
          return getOptionsAttribute(false, vueCore, getCellPosition, getInnerCellId)
        },
        [AttributeModeEnum.EDITABLE_SELECT]: (vueCore, getCellPosition, getInnerCellId) => {
          return getOptionsAttribute(false, vueCore, getCellPosition, getInnerCellId)
        }
      }


      return (vueCore, getCellPosition, getInnerCellId, modeCode) => {
        if (map[modeCode]) {
          return map[modeCode](vueCore, getCellPosition, getInnerCellId)
        } else {
          return null
        }
      }
    })()

    let formatSetWrapper = (attr, _vueCore, getCellPosition) => {
      let formatCellValue = (innerCellId, sheet) => {
        let [row, col] = getCellPosition(innerCellId)
        let cell = sheet.getCell(row, col)
        let curValue = cell.value()
        let formattedValue = attr.formatter(curValue, innerCellId, sheet)
        if (curValue !== formattedValue) {
          cell.value(formattedValue)
        }
      }

      if (attr.formatter) {
        wrapFn(attr, "onAttributeAdd", (doOri, args) => {
          let onAttributeRemove = doOri()
          formatCellValue(...args)
          return onAttributeRemove
        })
      }
    }

    let disableSetWrapper = (attr, vueCore, getCellPosition) => {
      let isCellDisabled = (innerCellId) => {
        return vueCore.isCellDisabled(innerCellId)
      }

      wrapFn(attr, "onAttributeAdd", function (doOri, args) {
        let unbinds = []
        let onAttributeRemove = null

        let [innerCellId, sheet] = args
        // 如果单元格没有被禁用，初始化单元格属性影响
        if (!isCellDisabled(innerCellId)) {
          onAttributeRemove = doOri()
        }
        unbinds.unshift(() => {
          if (onAttributeRemove) {
            onAttributeRemove()
          }
        })

        // 通过vue监听，根据单元格的禁用情况动态初始化、结束单元格属性影响
        let endWatchCellDisabled = vueCore.$watch(() => {
          return isCellDisabled(innerCellId)
        }, (cellDisabled) => {
          if (cellDisabled) {
            if (onAttributeRemove) {
              onAttributeRemove()
              onAttributeRemove = null
            }
          } else {
            onAttributeRemove = doOri()
          }
        })
        unbinds.unshift(endWatchCellDisabled)

        return () => {
          unbinds.forEach(v => v())
        }
      })
    }

    // 定义校验结果对象
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

    let validateCellWrapper = (attr, vueCore, getCellPosition) => {
      let isAttributeValueRequiredActually = (attrObj) => {
        return vueCore.isAttributeValueRequiredActually(attrObj.code)
      }
      let isCellDisabled = (innerCellId) => {
        return vueCore.isCellDisabled(innerCellId)
      }
      let validateCellValueRequired = (attrObj, innerCellId, source) => {
        if (isAttributeValueRequiredActually(attrObj)) {
          let [row, col] = getCellPosition(innerCellId)
          let val = source.getValue(row, col)
          if (val != null && !(typeof val == "string" && val.trim() === "")) {
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
        if (isCellDisabled(innerCellId)) { // 跳过被禁用的单元格
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

    // 将属性信息标记在单元格的tag数据里，防止设置属性时因没有任何表格数据的改变而导致无法undo
    let basicAttrTagWrapper = (attr, vueCore, getCellPosition) => {
      wrapFn(attr, "onAttributeAdd", function (doOri, args) {
        let onAttributeRemove = doOri()
        let [innerCellId, sheet] = args
        let [row, col] = getCellPosition(innerCellId)
        setCellTagAttr(sheet.getCell(row, col), "attr", this.code)
        return () => {
          let [row, col] = getCellPosition(innerCellId)
          if (onAttributeRemove) {
            onAttributeRemove()
          }
          deleteCellTagAttr(sheet.getCell(row, col), "attr")
        }
      })
    }

    // 获取数据之后包装属性配置
    let regAttrWrapperAfterDataInited = (attr, vueCore, getCellPosition) => {
      formatSetWrapper(attr, vueCore, getCellPosition)
      basicAttrTagWrapper(attr, vueCore, getCellPosition)
      validateCellWrapper(attr, vueCore, getCellPosition)
      disableSetWrapper(attr, vueCore, getCellPosition)
    }

    function getPropClipboard(setProperty, deleteProperty, getAllCellPropertyData, propClipboardInfo) {
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
              let propKey = Object.keys(cellPropData)[0]
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
                copyCellPropTo(fromCellRow, fromCellCol, toCellRow, toCellCol, fromCopyData)
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
                  let toRow = fromRow + (startRow - copyStartRow)
                  let toCol = fromCol + (startCol - copyStartCol)
                  copyCellPropTo(fromRow, fromCol, toRow, toCol, fromCopyData)
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

      let addCopyRows = (manager, rowCount, rowData) => {
        let allCellData = manager.getAllCellData()
        let rowDataList = commonUtil.getArrayFillBy(rowCount, rowData)
        if (allCellData.length <= rowIndex) {
          manager.$set(allCellData, rowIndex, null)
        }
        allCellData.splice(rowIndex, 0, ...rowDataList)
      }

      let addCopyColumns = (manager, columnCount, getColumnData) => {
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
                if (!extraArgs || !extraArgs.sourceRowIndex) {
                  throw "getAddRowsData参数错误"
                }
                let allCellData = manager.getAllCellData()
                let sourceRowIndex = extraArgs.sourceRowIndex
                let rowData = allCellData[sourceRowIndex] || null
                addCopyRows(manager, rowCount, rowData)
              },
              setAddColumnsData(manager, columnIndex, columnCount, extraArgs) {
                if (!extraArgs || !extraArgs.sourceColumnIndex) {
                  throw "setAddColumnsData参数错误"
                }
                let sourceColumnIndex = extraArgs.sourceColumnIndex
                addCopyColumns(manager, columnCount, (rowData) => {
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

      let managerGroup = {
        modesForAddCells,
        getCellDataManager: (config) => {
          let { modeForAddCell, onDeleteCell } = config ? config : {}
          let manager = new Vue({
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
                  this.$delete(allCellData[row], col)
                  if (allCellData[row].length === 0) {
                    this.$delete(allCellData, row)
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
                  addCopyRows(this, rowCount, null)
                }
              },
              addColumns: (columnIndex, columnCount, extraArgs) => {
                let modeObj = modesForAddCells.properties[modeForAddCell]
                if (modeObj && modeObj.setAddColumnsData) {
                  modeObj.setAddColumnsData(this, columnIndex, columnCount, extraArgs)
                } else {
                  addCopyColumns(this, columnCount, () => null)
                }
              },
              deleteRows: (rowIndex, rowCount) => {
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
              deleteColumns: (columnIndex, columnCount) => {
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
          })
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

    let getVueCoreObj = ((getCellDataManagerGroup_, getPropClipboard_) => {
      return ({
        getSpread,
        getUndoManager,
        setUndoManager
      }) => {

        let currentCellDataManagerGroup = getCellDataManagerGroup_()

        let innerCellIdManager = ((currentCellDataManagerGroup_) => {
          let dataManager = currentCellDataManagerGroup_.getCellDataManager()
          let innerVueCore = new Vue({
            computed: {
              cellPositionMap() {
                let map = {}
                dataManager.forEachCellData((row, col, id) => {
                  map[id] = [row, col]
                })
                return map
              }
            }
          })
          let n = 1
          return {
            getInnerCellId(row, col) {
              let innerCellId = dataManager.getCellData(row, col)
              if (innerCellId != null) {
                return innerCellId
              } else {
                innerCellId = n++
                dataManager.setCellData(row, col, innerCellId)
                return innerCellId
              }
            },
            getCellPosition(innerCellId) {
              return innerVueCore.cellPositionMap[innerCellId] || [-1, -1]
            },
            getAllIdData() {
              return dataManager.getAllCellData()
            },
            setAllIdData(allIdData) {
              dataManager.setAllCellData(allIdData)
            },
            clearIdData() {
              dataManager.clearCellData()
            }
          }
        })(currentCellDataManagerGroup)

        let getAttributeOriOperational = (vueCore, modeCode) => {
          return getAttributeOriOperationalByMode(
            vueCore,
            (innerCellId) => innerCellIdManager.getCellPosition(innerCellId),
            (row, col) => innerCellIdManager.getInnerCellId(row, col),
            modeCode,
          )
        }

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
              if (iId > n) {
                n = iId
              }
            }
          }
        })()


        let revertAttributeEffectManager = (() => {
          let fnData = {};

          let revertCell = (innerCellId) => {
            let fn = fnData[innerCellId]
            if (fn) {
              let resetFn = fn()
              delete fnData[innerCellId]
              if (fnsForUndo) {
                fnsForUndo.unshift(() => {
                  fnData[innerCellId] = resetFn()
                })
              }
            }
          }

          let setRevertFunction = (innerCellId, fn) => {
            let oldFn = fnData[innerCellId]
            let oldResetFn = null
            if (oldFn) {
              oldResetFn = oldFn()
            }
            fnData[innerCellId] = fn
            if (fnsForUndo) {
              fnsForUndo.unshift(() => {
                fnData[innerCellId]()
                if (oldResetFn) {
                  fnData[innerCellId] = oldResetFn()
                } else {
                  delete fnData[innerCellId]
                }
              })
            }
          }

          let fnsForUndo = null;
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
            revertCell,
            setRevertFunction,
            startTransaction,
            endTransaction,
            undoTransaction,
            revertAllCell() {
              for (const innerCellId in fnData) {
                revertCell(innerCellId)
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
          manager.setExtraDataFromCellData = (cellData, extraData) => {
            return cellData.extraData = extraData
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
            propClipboardInfo
          )
        }

        let propClipboard = customGetPropClipboard()

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
          let computedDataManager = new Vue({
            computed: {
              cellIdPositionMap() {
                let map = {}
                let arr = cellChangedDataManager.getAllCellData()
                if (arr) {
                  arr.forEach((rowData, row) => {
                    if (rowData) {
                      rowData.forEach((cellData, col) => {
                        if (cellData && cellData.id != null) {
                          map[cellData.id] = [row, col]
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
                console.error(msg);
                throw msg
              }
              let lastValue = cellData.history[cellData.history.length - 1]
              if (lastValue != value) {
                let msg = "运行异常，请检查程序"
                console.error(msg);
                throw msg
              }
              cellData.history.pop()
            }
          }
          const Sheets = GC.Spread.Sheets
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
            setCellId(row, col, cellId) {
              let cellData = cellChangedDataManager.getCellData(row, col)
              if (!cellData) {
                cellChangedDataManager.setCellData(row, col, { id: cellId })
              } else {
                cellChangedDataManager.$set(cellData, "id", cellId)
              }
            },
            getCellId(row, col) {
              let cellData = cellChangedDataManager.getCellData(row, col)
              if (cellData) {
                return cellData.id
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
            setCellSaved(cellIds) {
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
        let vueCore = new Vue({
          data: {
            config: {},
            tabStripRatio: 0,
            originalData: {
              ssjsonDataString: null,
              cellDataListString: null,
              areaListString: null,
              propertyTreeString: null,
              propertyOptionListString: null
            }, // 原始数据
            areaData: [], // 内部使用的区域数据
            controlData: [], // 内部使用的控制数据
            isShowMode: false,
            changed: false, // 用于标记当前单据是否发生改变
            curSelections: [],// 当前选择的单元格区域

            userData: {},// 用于存储用户数据

            isInSelectMode: false,// 是否处于选择模式

            readOnlyCellIdMapByPreposedControl: {},// 回填时，因回填顺序而被临时禁用的单元格id(使用innerCellId)
            highlightCellIdsInBackFillMode: [],// 回填时，需要高亮的单元格id列表
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
              if (this.originalData && this.originalData.cellDataListString) {
                let cellDataList = JSON.parse(this.originalData.cellDataListString)
                cellDataList.forEach(cellData => {
                  map[getCellKey(...getCellPosFromCellData(cellData, this.getConfig()))] = cellData
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
                let oriObj = getAttributeOriOperational(this, modeCode)
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
            cellPreposedCellControlsMap() {
              let map = {}
              for (const key in this.cellControlsMap) {
                const ctrls = this.cellControlsMap[key]
                let filteredCtrls = ctrls.filter(v => {
                  return v.type == "2" && v.targetRow != null
                })
                if (filteredCtrls.length > 0) {
                  map[key] = copyDataSimple(filteredCtrls)
                }
              }
              return map
            },
            cellPreposedAreaControlsMap() {
              let map = {}
              for (const key in this.cellControlsMap) {
                const ctrls = this.cellControlsMap[key]
                let filteredCtrls = ctrls.filter(v => {
                  return v.type == "2" && v.targetAreaId != null
                })
                if (filteredCtrls.length > 0) {
                  map[key] = copyDataSimple(filteredCtrls)
                }
              }
              return map
            },
            cellEntirelyFillControlMap() {
              let map = {}
              for (const key in this.cellControlsMap) {
                const ctrls = this.cellControlsMap[key]
                let filteredCtrls = ctrls.filter(v => {
                  return v.type == "1"
                })
                if (filteredCtrls.length > 0) {
                  map[key] = copyDataSimple(filteredCtrls[0])
                }
              }
              return map
            },
            settingDefaultValueCellControlsMap() {
              let map = {}
              for (const key in this.cellControlsMap) {
                const ctrls = this.cellControlsMap[key]
                let filteredCtrls = ctrls.filter(v => {
                  return v.type == "3" && v.targetRow != null
                })
                if (filteredCtrls.length > 0) {
                  map[key] = copyDataSimple(filteredCtrls)
                }
              }
              return map
            },
            settingDefaultValueAreaControlsMap() {
              let map = {}
              for (const key in this.cellControlsMap) {
                const ctrls = this.cellControlsMap[key]
                let filteredCtrls = ctrls.filter(v => {
                  return v.type == "3" && v.targetAreaId != null
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
              // 选择模式、回填模式暂无区域高亮情况，高亮目前均开始于单独选择了单元格
              if (this.isInSelectMode || !this.getConfig().configMode || !this.checkSingleCellSelection(selections)) {
                return areaIds
              }
              let [{ row, col }] = selections
              let cellControls = this.getAllCellControls(row, col)
              cellControls && cellControls.forEach(({ targetAreaId }) => {
                if (targetAreaId != null) {
                  areaIds.push(targetAreaId)
                }
              })
              return areaIds
            },
            highlightCells() {// 临时高亮单元格
              let cellPositions = []
              let selections = this.curSelections
              // 选择模式暂无单元格高亮情况，高亮目前均开始于单独选择了单元格
              if (this.isInSelectMode || !this.checkSingleCellSelection(selections)) {
                return cellPositions
              }

              let [{ row, col }] = selections
              if (this.getConfig().configMode) {
                let cellControls = this.getAllCellControls(row, col)
                cellControls && cellControls.forEach(({ targetRow, targetCol }) => {
                  if (targetRow != null && targetCol != null) {
                    cellPositions.push([targetRow, targetCol])
                  }
                })
              } else {
                this.highlightCellIdsInBackFillMode.forEach((innerCellId) => {
                  cellPositions.push(innerCellIdManager.getCellPosition(innerCellId))
                })
              }
              return cellPositions
            },
            highlightAreasMap() {
              return commonUtil.listToMap(this.highlightAreas, areaId => areaId)
            },
            highlightCellsMap() {
              return commonUtil.listToMap(this.highlightCells, ([row, col]) => {
                return getCellKey(row, col)
              })
            }
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
              let cellData = cellPropertyDataManager.getCellData(rowIndex, columnIndex)
              if (cellData) {
                return cellPropertyDataManager.getPropertyKeyFromCellData(cellData)
              } else {
                return null
              }
            },
            getProperty(rowIndex, columnIndex) {
              let propertyKey = this.getPropertyKey(rowIndex, columnIndex)
              return propertyKey ? this.propertyMap[propertyKey] : null
            },
            isSignAttribute(attributeCode) { // 是否为签字属性(具有额外的code值)
              let regAttr = this.operationalAttributesMap[attributeCode]
              return !!regAttr && !!regAttr.isSign
            },
            isReadOnlyAttribute(attributeCode) {
              let regAttr = this.operationalAttributesMap[attributeCode]
              return regAttr.readOnly
            },
            isValueRequiredAttribute(attributeCode) {
              let regAttr = this.operationalAttributesMap[attributeCode]
              return regAttr.valueRequired
            },
            isAttributeReadOnlyActually(attributeCode) { // 判断属性是否为只读属性
              let {
                readOnly,
                configMode
              } = this.getConfig()
              let operationalAttributesMap = this.operationalAttributesMap
              if (!readOnly) {
                if (configMode) {
                  // 编辑、新增模板时，只有具有readOnlyOnEdit属性的单元格是只读的
                  let registeredAttr = operationalAttributesMap[attributeCode]
                  if (registeredAttr) {
                    return !!registeredAttr.readOnlyOnEdit
                  } else {
                    return false
                  }
                } else {
                  // 单据回填时，只有具有可编辑的属性的单元格是可编辑的
                  let registeredAttr = operationalAttributesMap[attributeCode]
                  if (registeredAttr) {
                    if (!registeredAttr.readOnly) {
                      return false
                    }
                  }
                }
              }
              // 默认都是只读的
              return true
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
            isCellDisabled(innerCellId) {// 被禁用的单元格禁止属性相关的影响
              let {
                configMode
              } = this.getConfig()
              // 回填时，如果发现单元格临时被禁用
              if (!configMode) {
                let cellData = this.oriCellDataMap[innerCellId]
                if (cellData && cellData.readOnly) {
                  return true
                }
              }
              return false
            },
            isCellReadOnly(innerCellId) { // 判断单元格是否临时被禁用，临时被禁用的单元格为只读并且没有原本属性的特征
              // 如果单元格被禁用，则单元格只读
              if (this.isCellDisabled(innerCellId)) {
                return true
              }
              // 如果单元格的属性只读，则单元格只读
              let [row, col] = innerCellIdManager.getCellPosition(innerCellId)
              let attributeCode = this.getPropertyKey(row, col)
              if (this.isAttributeReadOnlyActually(attributeCode)) {
                return true
              }
              // 如果单元格因回填顺序设置被禁用，则单元格只读
              if (this.readOnlyCellIdMapByPreposedControl[innerCellId]) {
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
            initCellPropertyData(cellDataList, setCellData, setKey, setExtraData) {
              let config = this.getConfig()
              let {
                configMode,
                cellDataConfig: {
                  getCellAttributeCode,
                }
              } = config
              cellDataList.forEach(cell => {
                let [row, col] = getCellPosFromCellData(cell, this.getConfig())
                let attributeCode = getCellAttributeCode(cell, config)
                if (attributeCode) {
                  row = parseInt(row)
                  col = parseInt(col)
                  let cellData = {}
                  setKey(cellData, attributeCode)
                  if (!configMode) {
                    setExtraData(cellData, getCellCode(cell, config))
                  }
                  setCellData(row, col, cellData)
                }
              })
            },
            _getAreaData(areaList) {
              return areaList.map(({
                leftUp,
                rightDown,
                number
              }) => {
                let [leftX, upY] = leftUp.split(",").map(v => parseInt(v))
                let [rightX, downY] = rightDown.split(",").map(v => parseInt(v))
                setExistAreaId(number)
                return {
                  x: leftX,
                  y: upY,
                  width: rightX - leftX + 1,
                  height: downY - upY + 1,
                  id: number
                }
              })
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

              let {
                readOnly,
                configMode
              } = this.getConfig()
              firstSheet.isProtected = true
              if (!readOnly && configMode) {
                firstSheet.protectionOptions = {
                  ...firstSheet.protectionOptions,
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
              }
              unbinds.unshift((ssjsonData) => {
                // 清理保护信息
                ssjsonUtil.clearProtectInfo(ssjsonData)
              })

              let ori = ssjsonData.allowUserDragDrop
              ssjsonData.allowUserDragDrop = false
              unbinds.unshift((ssjsonData) => {
                if (ori == null) {
                  delete ssjsonData.allowUserDragDrop
                } else {
                  ssjsonData.allowUserDragDrop = ori
                }
              })
              return (ssjsonData) => {
                unbinds.forEach(v => v(ssjsonData))
              }
            },
            initAllCellsByConfig() { // 设置不存在属性的单元格的只读，执行属性的清理方法
              let spread = getSpread()
              let sheet = spread.getActiveSheet()
              let rowCount = sheet.getRowCount()
              let columnCount = sheet.getColumnCount()
              spread.suspendPaint()
              commonUtil.everyCell(0, 0, rowCount, columnCount, (row, col) => {
                let attrCode = this.getPropertyKey(row, col)
                if (!attrCode) {
                  sheet.getCell(row, col).locked(this.isAttributeReadOnlyActually(null))
                } else {
                  try {
                    let attrObj = this.operationalAttributesMap[attrCode]
                    if (attrObj && attrObj.clear) {
                      attrObj.clear(row, col, sheet)
                    }
                  } catch (error) {
                    console.log(error)
                  }
                }
              })
              spread.resumePaint()
            },
            // 根据所有属性数据初始化当前表格
            initCellsWhichHasProperty() {
              let spread = getSpread()
              let sheet = spread.getActiveSheet()
              this.everyCellWithProperty((row, col) => {
                let propertyKey = this.getPropertyKey(row, col)
                let registeredAttr = this.operationalAttributesMap[propertyKey]
                if (registeredAttr) {
                  if (registeredAttr.onAttributeAdd) {
                    let innerCellId = innerCellIdManager.getInnerCellId(row, col)
                    let revertFn = registeredAttr.onAttributeAdd(innerCellId, sheet)
                    revertAttributeEffectManager.setRevertFunction(innerCellId, revertFn)
                  }
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
                // staffId, name, roleInfoList:[{code, name}...]}
                if (!userData.staffId) {
                  throw "用户信息缺少staffId"
                }
                if (!userData.name) {
                  throw "用户信息缺少name"
                }
                if (!userData.roleInfoList) {
                  throw "用户信息缺少roleInfoList"
                }
                if (userData.roleInfoList.some(obj => {
                  if (!obj.code || !obj.name) {
                    return true
                  } else {
                    return false
                  }
                })) {
                  throw "用户信息的roleInfoList的属性存在问题，请检查数据，列表内对象需要属性code和name"
                }
                this.userData = copyDataSimple(userData)
              }
            },
            setTemplateData({
              ssjsonDataString,
              cellDataList,
              areaList
            }) {
              if (!this.originalData.propertyTreeString) {
                throw "请先设置属性数据"
              }
              if (ssjsonDataString == null) {
                ssjsonDataString = this.defaultSsjson
              }
              if (cellDataList == null) {
                cellDataList = []
              }
              if (areaList == null) {
                areaList = []
              }
              let spread = getSpread()
              // 暂停渲染
              spread.suspendPaint()

              let config = this.getConfig()

              // 恢复所有属性单元格的额外影响（主要目的是取消监听）
              revertAttributeEffectManager.revertAllCell()

              // 设置原始数据
              this.$set(this.originalData, "ssjsonDataString", ssjsonDataString)
              this.$set(this.originalData, "cellDataListString", JSON.stringify(cellDataList))
              this.$set(this.originalData, "areaListString", JSON.stringify(areaList))

              // 初始化ssjson数据
              let ssjsonData = JSON.parse(ssjsonDataString)
              let revertSsjson = this.initSsjsonData(ssjsonData)
              this._revertSsjson = revertSsjson
              spread.fromJSON(ssjsonData)

              // 清除旧有数据
              this.clearInnerData()

              // 初始化区域数据
              this.areaData = this._getAreaData(areaList)

              // 初始化控制数据
              let controlData = this.controlData
              cellDataList.forEach(cell => {
                let [row, col] = getCellPosFromCellData(cell, config)
                let cellControls = cell.controls
                cellControls && cellControls.forEach(control => {
                  let { type, rowId, colId, areaNumber } = control
                  if (areaNumber == null || this.areaMap[areaNumber]) {
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
              this.initCellPropertyData(cellDataList, (row, col, propertyData) => {
                cellPropertyDataManager.setCellData(row, col, propertyData)
              }, (cellData, propertyKey) => {
                return cellPropertyDataManager.setPropertyKeyToCellData(cellData, propertyKey)
              }, (cellData, extraData) => {
                return cellPropertyDataManager.setExtraDataFromCellData(cellData, extraData)
              })

              let sheet = spread.getActiveSheet()

              let {
                cellDataConfig: {
                  getCellId,
                  getCellValue,
                  getCellSaved,
                }
              } = config
              cellDataList.forEach(cell => {
                let [row, col] = getCellPosFromCellData(cell, config)
                let value = getCellValue(cell, config)
                sheet.setValue(row, col, value)
                let cellId = getCellId(cell, config)
                cellChangedManager.setCellId(row, col, cellId)
                if (!config.configMode) {
                  let cellSaved = getCellSaved(cell, config)
                  // 如果单元格一上来就是未保存状态，则需要设置单元格具有原始的变化历史
                  if (cellSaved === false) {
                    cellChangedManager.setCellHasOuterChanged(cellId, value)
                  }
                }
              })
              // 设置不存在属性的单元格的是否只读，执行属性的清理方法
              // 由于清理方法会清理属性的痕迹，所以必须要先于属性的初始化方法执行
              this.initAllCellsByConfig()
              // 执行属性的初始化方法
              this.initCellsWhichHasProperty()

              // 恢复渲染
              spread.resumePaint()
            },
            tempRevertAllCell() {
              revertAttributeEffectManager.revertAllCell()
              return () => {
                this.initCellsWhichHasProperty()
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
                ssjsonUtil.clearProtectInfo(ssjsonData)
              }
              let ssjsonDataString = JSON.stringify(ssjsonData)

              let sheet = spread.getActiveSheet()

              let cellDataList = []
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
              } = config.cellDataConfig
              let getAttr = (row, col) => {
                return this.getPropertyKey(row, col)
              }
              this.everyCellWithPropertyOrWithControls((row, col) => {
                let cellData = {}
                let spanInfo = sheet.getSpan(row, col)
                if (spanInfo && !(spanInfo.row == row && spanInfo.col == col)) {
                  return
                }

                let attributeCode = getAttr(row, col)
                let cellId = cellChangedManager.getCellId(row, col)
                setCellId(cellData, cellId, config)
                setCellValue(cellData, sheet.getValue(row, col), config)
                if (attributeCode) {
                  setCellAttributeCode(cellData, attributeCode, config)
                }
                setCellRow(cellData, row, config)
                setCellCol(cellData, col, config)

                if (!config.configMode) {
                  setCellCode(cellData, cellPropertyDataManager.getExtraDataFromCellData(cellPropertyDataManager.getCellData(row, col)), config)
                  let cellChanged = cellChangedManager.getCellChanged(cellId)
                  let cellDeleted = cellChangedManager.getCellDeleted(cellId)

                  if (cellChanged != null) {
                    setCellChangeType(cellChanged ? BillCellChangeTypeEnum.UPDATE : BillCellChangeTypeEnum.NO_CHANGE)
                  } else {
                    setCellChangeType(cellDeleted ? BillCellChangeTypeEnum.DELETE : BillCellChangeTypeEnum.INSERT)
                  }
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
                    controls.push(targetControl)
                  })
                }
                cellData.controls = controls

                if (!localUse && attributeCode) {// 本地使用不用设置关联单元格
                  setLinkCells(cellData, this.getLinkedCells(row, col, sheet).map(({
                    row: lRow,
                    col: lCol
                  }) => {
                    let linkCellData = {}
                    let cellId = cellChangedManager.getCellId(lRow, lCol)
                    setLinkCellId(linkCellData, cellId, config)
                    return linkCellData
                  }), config)
                }

                cellDataList.push(cellData)
              })

              let areaList = this.areaData.map(({
                x,
                y,
                width,
                height,
                id
              }) => ({
                leftUp: `${x},${y}`,
                leftDown: `${x},${y + height - 1}`,
                rightUp: `${x + width - 1},${y}`,
                rightDown: `${x + width - 1},${y + height - 1}`,
                type: "1",
                number: this.areaNumberMap[id]
              }))

              if (endTempRevertAllCell) {
                endTempRevertAllCell()
              }
              endDoInNormalStyle()

              // 恢复渲染
              spread.resumePaint()
              return {
                ssjsonDataString,
                cellDataList,
                areaList
              }
            },
            exportExcelBlobBySsjson(ssjsonData) {
              return new Promise((resolve, reject) => {
                var excelIo = new GC.Spread.Excel.IO()
                excelIo.save(ssjsonData, (data) => {
                  let mimeType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
                  resolve(new Blob([data], { type: mimeType }))
                }, (error) => {
                  reject(error)
                })
              })
            },
            async exportExcelBlob() {
              if (GC.Spread.Excel != null && GC.Spread.Excel.IO != null) {
                let spread = getSpread()
                // 暂停渲染
                spread.suspendPaint()
                let endDoInNormalStyle = this.doInNormalStyle()
                let endTempRevertAllCell = this.tempRevertAllCell()
                try {
                  let ssjsonData = spread.toJSON()

                  if (this._revertSsjson) {
                    this._revertSsjson(ssjsonData)
                  } else {
                    // 清理保护信息
                    ssjsonUtil.clearProtectInfo(ssjsonData)
                  }
                  return await this.exportExcelBlobBySsjson(ssjsonData)
                } finally {
                  endTempRevertAllCell()
                  endDoInNormalStyle()
                  // 恢复渲染
                  spread.resumePaint()
                }
              } else {
                console.error(`如果想使用导出功能，请引入gc.spread.excelio.${spreadVersion}.min.js`)
              }
            },
            isLinkCell(rowIndex, columnIndex) {
              let attr = this.getProperty(rowIndex, columnIndex)
              return !!attr && attr.linkAttr == "1"
            },
            // 获取关联单元格 TODO 关联单元格使用的areaInfo必须是专门用来提供框定范围的
            getLinkedCells(rowIndex, columnIndex, sheet) {
              if (this.isLinkCell(rowIndex, columnIndex)) {
                return []
              }
              let areaInfo = this.findAreaInfo(rowIndex, columnIndex)
              if (areaInfo) {
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
                for (let col = x; col <= x + width; col++) {
                  let spanInfo = sheet.getSpan(rowIndex, col)
                  if (spanInfo) {
                    checkAndPush(spanInfo.row, spanInfo.col)
                  } else {
                    checkAndPush(rowIndex, col)
                  }
                }
                let columnLinkCells = []
                for (let row = y; row < y + height; row++) {
                  let spanInfo = sheet.getSpan(row, columnIndex)
                  if (spanInfo) {
                    checkAndPush(spanInfo.row, spanInfo.col)
                  } else {
                    checkAndPush(row, columnIndex)
                  }
                }
                return [].concat(rowLinkCells, columnLinkCells)
              } else {
                return []
              }
            },
            clearInnerData() {
              this.cellPropertyData = []
              this.areaData = []
              this.controlData = []
              innerCellIdManager.clearIdData()
              cellChangedManager.clearAllData()
            },
            getInnerData() {
              return [
                this.cellPropertyData,
                this.areaData,
                this.controlData,
                innerCellIdManager.getAllIdData(),
                cellChangedManager.getAllData()
              ]
            },
            setInnerData(data) {
              this.cellPropertyData = data[0]
              this.areaData = data[1]
              this.controlData = data[2]
              innerCellIdManager.setAllIdData(data[3])
              cellChangedManager.setAllData(data[4])
            },
            checkSingleCellSelection(selections) {
              if (selections.length != 1 || selections[0].colCount != 1 || selections[0].rowCount != 1) {
                return false
              }
              return true
            },
            installExtraShowStyle(sheet) {
              // 设置区域单元格的边框样式
              let setAreaBorder = (() => {
                let markName = "_bill_module_"
                let newBorderStyle = (color) => {
                  let lineStyle = new GC.Spread.Sheets.LineBorder(color, GC.Spread.Sheets.LineStyle.thick)
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

              let setNormalAreaBorder = () => {
                let areaBorderColor = "rgba(255, 0, 0, 0.3)"
                let areaBorderColorObj = commonUtil.parseRgbString(areaBorderColor)
                return setAreaBorder(this.areaData.filter(v => !this.highlightAreasMap[v.id]), areaBorderColorObj)
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
                let addBackColors = () => {
                  this.highlightCells && this.highlightCells.forEach(([row, col]) => {
                    backColorLayerManager.pushRgbaBackColor(row, col, layerName, highlightCellBackColorObj)
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
                  // 将更新的颜色数据设置到表格中
                  backColorLayerManager.setBackColorToSheet(sheet)

                  resetAreaBorder()

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
                let needAddNewAttrEffect = !!(newRegisteredAttr && newRegisteredAttr.onAttributeAdd)

                if (needRemoveOldAttrEffect) {
                  revertAttributeEffectManager.revertCell(innerCellId)
                }
                if (needAddNewAttrEffect) {
                  revertAttributeEffectManager.setRevertFunction(innerCellId, newRegisteredAttr.onAttributeAdd(innerCellId, sheet))
                }
              })
            },
            listenEditCellForFormat() {
              let spread = getSpread()
              let commandManager = spread.commandManager()

              commandManager.addListener("editCellForFormat", (...args) => {
                if (args[0] && args[0].command) {
                  let command = args[0].command
                  if (command.cmd == "editCell") {
                    let {
                      row,
                      col
                    } = command
                    let propKey = this.getPropertyKey(row, col)
                    if (propKey) {
                      let regAttr = this.operationalAttributesMap[propKey]
                      if (regAttr && regAttr.formatter && !this.isCellDisabled(innerCellIdManager.getInnerCellId(row, col))) {
                        let sheet = spread.getActiveSheet()
                        let newValue = sheet.getValue(row, col)
                        let formattedValue = regAttr.formatter(newValue, row, col, sheet)
                        if (formattedValue !== newValue) {
                          sheet.setValue(row, col, formattedValue)
                        }
                      }
                    }
                  }
                }
              })
              return () => {
                commandManager.removeListener("editCellForFormat")
              }
            },
            listenSelectionChangedForShowHighlightCellsAndAreas() {
              let spread = getSpread()

              let listener = (e, info) => {
                let selections = info.newSelections
                let preHighlightCells = JSON.stringify(this.highlightCells)
                let preHighlightAreas = JSON.stringify(this.highlightAreas)
                this.curSelections = copyDataSimple(selections)
                let curHighlightCells = JSON.stringify(this.highlightCells)
                let curHighlightAreas = JSON.stringify(this.highlightAreas)
                if (preHighlightCells != curHighlightCells || preHighlightAreas != curHighlightAreas) {
                  this.refreshExtraShowStyle()
                }
              }
              spread.bind(GC.Spread.Sheets.Events.SelectionChanged, listener)
              return () => {
                spread.unbind(GC.Spread.Sheets.Events.SelectionChanged, listener)
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
              let fixCommands = [{
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
                  } = GC.Spread.Sheets.Fill.FillDirection
                  const {
                    auto,
                    copyCells
                  } = GC.Spread.Sheets.Fill.AutoFillType

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
              // 因技术原因及时间原因（增加了control的操作）allowUserDragDrop已禁用，所以暂时注释以下代码暂时禁用
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
                afterCommand: (context, options) => {
                  propClipboard.cutProp(getSheet(context, options).getSelections())
                },
                afterResetStyle: (context, options) => {
                  showCutCopyIndicator(context, options)
                }
              },
              {
                cmd: "clipboardPaste",
                extraEffectInCommand: (context, options, isUndo) => {
                  // 当剪切板被外界复制更新后，单元格属性剪贴板要清空
                  if ((!options.fromRanges || !options.fromSheet) && !options.clipboardHtml) {
                    propClipboard.clear()
                  } else if (options.pasteOption == GC.Spread.Sheets.ClipboardPasteOptions.all) {
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
                // 如果是粘贴命令则需要延迟执行粘贴动作
                customFinalExecute(finalExe, [context, options, isUndo]) {
                  listeners.push(finalExe)
                  // 如果不是键盘ctrl+v粘贴，则100毫秒后执行
                  if (!isCtrlV) {
                    setTimeout(() => {
                      exeAndRemoveListeners()
                    }, 100)
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
                beforeCommand,
                extraEffectInCommand,
                afterCommand,
                afterResetStyle,
                customFinalExecute
              }) => {
                let commandObj = findCommandObj(commandManager[cmd])
                if (commandObj) {
                  let oriExecute = wrapFn(commandObj, "execute", function (oriFn, args) {
                    var Commands = GC.Spread.Sheets.Commands
                    let [context, options, isUndo] = args
                    let resetStyle = null
                    try {
                      let rs = null
                      if (window.debug) {
                        console.log(cmd, args)
                      }
                      args.push(cmd)
                      if (commandObj.canUndo) {
                        if (!isUndo) {
                          // 暂停渲染
                          context.suspendPaint()
                          // startTransaction已经包含了backStyle的功能
                          Commands.startTransaction(context, options, () => {
                            if (beforeCommand) {
                              beforeCommand(context, options)
                            }
                          })
                        }
                      } else {
                        if (getUndoManager().getCommandDeep(context.getSheetFromName(commonUtil.getSheetNameFromCommandOptions(options))) === 0) {
                          resetStyle = backStyle(...args)
                        }

                        if (beforeCommand) {
                          beforeCommand(context, options)
                        }
                      }
                      rs = oriFn()
                      return rs
                    } catch (error) {
                      throw error
                    } finally {
                      let finalExe = () => {
                        if (commandObj.canUndo) {
                          if (!isUndo) {
                            if (extraEffectInCommand) {
                              extraEffectInCommand(...args)
                            }
                            Commands.endTransaction(context, options, () => {
                              if (afterCommand) {
                                afterCommand(context, options)
                              }
                            })
                            // 恢复渲染
                            context.resumePaint()
                          } else {
                            Commands.undoTransaction(context, options, () => {
                              if (beforeCommand) {
                                beforeCommand(context, options)
                              }
                            }, () => {
                              if (afterCommand) {
                                afterCommand(context, options)
                              }
                            })
                          }
                          if (afterResetStyle) {
                            afterResetStyle(...args)
                          }
                        } else {
                          if (afterCommand) {
                            afterCommand(...args)
                          }
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
                      if (customFinalExecute) {
                        customFinalExecute(finalExe, args)
                      } else {
                        finalExe()
                      }
                    }
                  })
                  unbinds.unshift(() => {
                    commandObj.execute = oriExecute
                  })
                }
              })

              let commandListener = (...args) => {
                this.changed = true
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
              if (hasWrapData() && getWrapData().isWrapped) {
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
              let startTransactionOri = wrapFn(transactionManager, "startTransaction", (doOri) => {
                if (this_.getUndoManager().getCommandDeep(sheet) === 0) {
                  if (!getData(this_).endDoInNormalStyle) {
                    getData(this_).endDoInNormalStyle = this_.doInNormalStyle(true)
                  }
                  // 装载命令执行前的属性数据
                  getPropChangeManager(sheet).setOldData(this_.getInnerData()[0])
                  dataForUndo = {}
                }

                // 备份内部数据，用于支持undo功能
                this_.getUndoManager().beforeCommandInSheet(sheet, dataForUndo)
                doOri()
              })
              let endTransactionOri = wrapFn(transactionManager, "endTransaction", (doOri) => {
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
                return events
              })
              let undoOri = wrapFn(transactionManager, "undo", (doOri, [events]) => {
                sheet.suspendPaint()
                let endDoInNormalStyle = null
                endDoInNormalStyle = this_.doInNormalStyle(true)
                doOri()
                if (events[customPropertyName]) {
                  this_.getUndoManager().undoInSheet(sheet, events[customPropertyName])
                  delete events[customPropertyName]
                }
                endDoInNormalStyle()
                sheet.resumePaint()
              })
              return () => {
                transactionManager.startTransaction = startTransactionOri
                transactionManager.endTransaction = endTransactionOri
                transactionManager.undo = undoOri
              }
            },
            // 包装spread外部事务
            wrapSpreadTransaction() {
              return this.wrapStaticFunction("wrapSpreadTransaction", (findThis) => {
                let getSheet = (context, options) => {
                  let sheetName = commonUtil.getSheetNameFromCommandOptions(options)
                  return sheetName ? context.getSheetFromName(sheetName) : context.getActiveSheet()
                }
                let startTransactionOri = wrapFn(Sheets.Commands, "startTransaction", (doOri, [context, options, extraBeforeCommand]) => {
                  let this_ = findThis(context)
                  let sheetName = commonUtil.getSheetNameFromCommandOptions(options)
                  // 目前仅支持一个工作簿
                  if (this_ && (context.getSheetIndex(sheetName) === 0 || !sheetName)) {
                    let sheet = getSheet(context, options)
                    if (this_.getUndoManager().getCommandDeep(sheet.name()) === 0) {
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
                })

                let endTransactionOri = wrapFn(Sheets.Commands, "endTransaction", (doOri, [context, options, extraAfterCommand]) => {

                  let this_ = findThis(context)
                  let sheetName = commonUtil.getSheetNameFromCommandOptions(options)
                  // 目前仅支持一个工作簿
                  let canWrap = this_ && (context.getSheetIndex(sheetName) === 0 || !sheetName)
                  let sheet = getSheet(context, options)
                  if (canWrap) {
                    context.suspendPaint()
                    if (this_.getUndoManager().getCommandDeep(sheet.name()) === 1) {
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
                    if (this_.getUndoManager().getCommandDeep(sheet.name()) === 0) {
                      everySheetName(context, options.sheetName, sheetName => {
                        // 清理propChangeManage属性数据
                        getPropChangeManager(context.getSheetFromName(sheetName)).clear()
                      })

                      if (getData(this_).endDoInNormalStyle) {
                        getData(this_).endDoInNormalStyle()
                        delete getData(this_).endDoInNormalStyle
                      }
                      if (options.__afterResetStyle) {
                        options.__afterResetStyle()
                        delete options.__afterResetStyle
                      }
                    }
                    if (extraAfterCommand) {
                      extraAfterCommand()
                    }
                    context.resumePaint()
                  }
                })

                let undoTransactionOri = wrapFn(Sheets.Commands, "undoTransaction", (doOri, [context, options, extraBeforeCommand, extraAfterCommand]) => {
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
                  Sheets.Commands.startTransaction = startTransactionOri
                  Sheets.Commands.endTransaction = endTransactionOri
                  Sheets.Commands.undoTransaction = undoTransactionOri
                }
              })
            },
            // 包装单元格绘制
            wrapCellPaint() {
              return this.wrapStaticFunction("wrapCellPaint", (findThis) => {
                let oriCellPaint = wrapFn(Sheets.CellTypes.Base.prototype, "paint", (doOri, [ctx, value, x, y, w, h, style, options]) => {
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
                    let highlightTextColor = "rgba(128,0,128,0.7)"
                    let showModeTextColor = "rgba(255,0,0,0.7)"
                    if (titleAreas && titleAreas.length > 0) {
                      // console.log(ctx.textAlign, [ctx, value, x, y, w, h, style, options])
                      ctx.save()
                      // ctx.font = "19px serif";
                      ctx.textAlign = "left"
                      ctx.textBaseline = "top"

                      filledText = ""

                      for (let i = 0; i < titleAreas.length; i++) {
                        const area = titleAreas[i]
                        let isHighlight = isHighlightArea(area)
                        if (!isHighlight) {
                          ctx.fillStyle = showModeTextColor
                        } else {
                          ctx.fillStyle = highlightTextColor
                        }
                        let title = this_.getAreaTitle(area.id)
                        ctx.fillText(title, x + 1 + ctx.measureText(filledText).width, y + 1)
                        filledText = filledText + title
                        if (i != titleAreas.length - 1) {
                          if (showMode) {
                            ctx.fillStyle = showModeTextColor
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
                  Sheets.CellTypes.Base.prototype = oriCellPaint
                }
              })
            },
            installAllProperty() {
              let operationalAttributesMap = this.operationalAttributesMap
              for (const key in operationalAttributesMap) {
                const attr = operationalAttributesMap[key]
                if (attr.install) {
                  attr.install(getSpread())
                }
              }
              return () => {
                for (const key in operationalAttributesMap) {
                  const attr = operationalAttributesMap[key]
                  if (attr.uninstall) {
                    attr.uninstall(getSpread())
                  }
                }
              }
            },
            installCustomSheetFns(sheet) {
              let unbinds = []
              let fnNames = ["addRows", "addColumns", "deleteRows", "deleteColumns"]

              fnNames.forEach(fnName => {
                let oriFn = wrapFn(sheet, fnName, (doOri, args) => {
                  let afterExe = this[fnName](...[sheet, ...args])
                  doOri()
                  afterExe && afterExe()
                })
                unbinds.unshift(() => {
                  sheet[fnName] = oriFn
                })
              })

              return () => {
                unbinds.forEach(v => v())
              }
            },
            // 安装设置默认值功能
            installSettingDefaultValueControl(sheet) {
              const Sheets = GC.Spread.Sheets
              let setDefaultValue = (sheet, targetRow, targetCol) => {
                let oriValue = sheet.getValue(targetRow, targetCol)
                if (oriValue != null && oriValue != "") {
                  let defaultValue = this.getDefaultValue(targetRow, targetCol)
                  if (defaultValue != null) {
                    sheet.setValue(defaultValue)
                  }
                }
              }
              let cellClickListener = (e, args) => {
                if (args.sheetArea === Sheets.SheetArea.viewport) {
                  let { row, col, sheet } = args
                  let cellControls = this.getSettingDefaultValueCellControls(row, col)

                  if (cellControls) {
                    cellControls.forEach(({ targetRow, targetCol }) => {
                      setDefaultValue(sheet, targetRow, targetCol)
                    })
                  }

                  let areaControls = this.getSettingDefaultValueAreaControls(row, col)
                  if (areaControls) {
                    areaControls.forEach(({ targetAreaId: areaId }) => {
                      let areaInfo = this.areaMap[areaId]
                      if (areaInfo) {
                        let { x, y, width, height } = areaInfo
                        commonUtil.everyCell(y, x, height, width, (targetRow, targetCol) => {
                          setDefaultValue(sheet, targetRow, targetCol)
                        })
                      }
                    })
                  }
                }
              }
              sheet.bind(Sheets.Events.CellClick, cellClickListener)
              return () => {
                sheet.unbind(Sheets.Events.CellClick, cellClickListener)
              }
            },
            installPreposedControl(sheet) {
              const Sheets = GC.Spread.Sheets
              //readOnlyCellIdMapByPreposedControl
              //highlightCellIdsInBackFillMode

              // TODO 1、要把当前单元格的前置单元格遍、前置区域历一遍，判断当前单元格是否可编辑
              // 2、如果可编辑，则需要把当前单元格从readOnlyCellIdMapByPreposedControl中去除，清空高亮信息
              // 3、如果不可编辑，则需要把前置区域、前置单元格中未通过校验的单元格进行高亮

              // TODO 高亮单元格并弹出说明框(spreadJs中的inputMessage待验证)
              let checkCellPreposedControl = (row, col) => {
                var dv = new GC.Spread.Sheets.DataValidation.DefaultDataValidator()
                dv.inputMessage("1111")
                dv.inputTitle("aaa")
                sheet.setDataValidator(row, col, 1, 1, dv);
                this.$set(this.readOnlyCellIdMapByPreposedControl, innerCellIdManager.getInnerCellId(row, col), true)
                // this.readOnlyCellIdMapByPreposedControl = {}
              }
              let noneMatched = () => {
                this.highlightCellIdsInBackFillMode = []
              }

              let selectionChangedListener = (e, info) => {
                let [{ row, col }] = info.newSelections
                checkCellPreposedControl(row, col)
                // TODO 暂时注释
                // if (!this.getConfig().configMode) {
                //   if (this.checkSingleCellSelection(info.newSelections)) {
                //     let [{ row, col }] = info.newSelections
                //     checkCellPreposedControl(row, col)
                //   } else {
                //     noneMatched()
                //   }
                // }
              }
              sheet.bind(Sheets.Events.SelectionChanging, selectionChangedListener);
              // sheet.bind(Sheets.Events.SelectionChanged, selectionChangedListener);
              return () => {
                sheet.bind(Sheets.Events.SelectionChanging, selectionChangedListener);
                // sheet.unbind(Sheets.Events.SelectionChanged, selectionChangedListener)
              }
            },
            // TODO 
            // 2、处理监听方法与实际单元格的一致性
            installCellReadOnlyBinder(sheet) {
              let setLock = (innerCellId, locked) => {
                let [row, col] = innerCellIdManager.getCellPosition(innerCellId)
                let cell = sheet.getCell(row, col)
                locked = !!locked
                if (cell.locked() !== locked) {
                  cell.locked(locked)
                }
              }

              let customCellPositionManager = getCellDataManagerGroup().getCellDataManager()
              let initCellPosition = (innerCellId) => {
                let [row, col] = innerCellIdManager.getCellPosition(innerCellId)
                let oldInnerCellId = customCellPositionManager.getCellData(row, col)
                if (oldInnerCellId != null && oldInnerCellId != innerCellId) {
                  console.log({
                    msg: "installCellReadOnlyBinder：内部单元格id设置与之前不一致，请检查程序",
                    row,
                    col,
                    innerCellId,
                    oldInnerCellId
                  });
                }
                customCellPositionManager.setCellData(row, col, innerCellId)
              }

              let cellReadOnlyWatchers = {}
              let initCellWatcher = (innerCellId) => {
                if (!cellReadOnlyWatchers[innerCellId]) {
                  cellReadOnlyWatchers[innerCellId] = this.$watch(() => {
                    return this.isCellReadOnly(innerCellId)
                  }, (readOnly) => {
                    // 如果只读则设置单元格锁定
                    setLock(innerCellId, readOnly)
                  })
                }
              }

              let destroyCellWatcher = (innerCellId) => {
                if (cellReadOnlyWatchers[innerCellId]) {
                  cellReadOnlyWatchers[innerCellId]()
                  delete cellReadOnlyWatchers[innerCellId]
                }
              }

              let initWatchByRange = (startRow, startColumn, rowCount, columnCount) => {
                commonUtil.everyCell(startRow, startColumn, rowCount, columnCount, (row, col) => {
                  let innerCellId = innerCellIdManager.getInnerCellId(row, col)
                  initCellWatcher(innerCellId)
                  initCellPosition(innerCellId)
                })
              }
              let cancelWatchByRange = (startRow, startColumn, rowCount, columnCount) => {
                commonUtil.everyCell(startRow, startColumn, rowCount, columnCount, (row, col) => {
                  let innerCellId = customCellPositionManager.getCellData(row, col)
                  destroyCellWatcher(innerCellId)
                })
              }

              initWatchByRange(0, 0, sheet.getRowCount(), sheet.getColumnCount())

              let addRows = (rowIndex, rowCount) => {
                // 扩充区域
                customCellPositionManager.addRows(rowIndex, rowCount)
                let sheetColumnCount = sheet.getColumnCount()
                // 监听并设置内部单元格id
                initWatchByRange(rowIndex, 0, rowCount, sheetColumnCount)
              }

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

              // 在命令完全执行完毕后执行
              let undoDeleteRows = (rowIndex, rowCount) => {
                addRows(rowIndex, rowCount)
              }


              let addColumns = (colIndex, columnCount) => {
                // 扩充区域
                customCellPositionManager.addColumns(colIndex, columnCount)
                let sheetRowCount = sheet.getRowCount()
                // 设置监听并初始化内部单元格id
                initWatchByRange(0, colIndex, sheetRowCount, columnCount)
              }

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

              // 在命令完全执行完毕后执行
              let undoDeleteColumns = (colIndex, columnCount) => {
                addColumns(colIndex, columnCount)
              }

              let rowChangedListener = (e, info) => {
                if (!info.isUndo) {
                  if (info.propertyName == "addRows") {
                    addRows(info.row, info.newValue)
                  } else if (info.propertyName == "deleteRows") {
                    deleteRows(info.row, info.newValue)
                  }
                } else {
                  if (info.propertyName == "addRows") {
                    undoAddRows(info.row, info.oldValue)
                  } else if (info.propertyName == "deleteRows") {

                    // 因为需要被undo的单元格内部id数据，而单元格内部id数据是通过重写endTransaction等方法实现的，此时实际id数据未更改，所以等到下个执行帧执行以保证需要的数据存在
                    // TODO 待优化，应该使用监听方式调用，这种方式存在风险
                    setTimeout(() => {
                      undoDeleteRows(info.row, info.oldValue)
                    }, 0);
                  }
                }
              }
              let columnChangedListener = (e, info) => {
                if (!info.isUndo) {
                  if (info.propertyName == "addColumns") {
                    addColumns(info.col, info.newValue)
                  } else if (info.propertyName == "deleteColumns") {
                    deleteColumns(info.col, info.newValue)
                  }
                } else {
                  if (info.propertyName == "addColumns") {
                    undoAddColumns(info.col, info.oldValue)
                  } else if (info.propertyName == "deleteColumns") {
                    setTimeout(() => {
                      undoDeleteColumns(info.col, info.oldValue)
                    }, 0);
                  }
                }
              }


              sheet.bind(Sheets.Events.RowChanged, rowChangedListener)
              sheet.bind(Sheets.Events.ColumnChanged, columnChangedListener)
              return () => {
                for (const innerCellId in cellReadOnlyWatchers) {
                  cellReadOnlyWatchers[innerCellId]()
                }
                sheet.unbind(Sheets.Events.RowChanged, rowChangedListener)
                sheet.unbind(Sheets.Events.ColumnChanged, columnChangedListener)
              }




              let unbinds = []

              let isCellDisabled = (innerCellId) => {
                return vueCore.isCellDisabled(innerCellId)
              }
              let isCellReadOnly = (innerCellId) => {
                return vueCore.isCellReadOnly(innerCellId)
              }



              wrapFn(attr, "onAttributeAdd", function (doOri, args) {
                let unbinds = []
                let onAttributeRemove = null

                let [innerCellId, sheet] = args
                // 如果单元格没有被禁用，初始化单元格属性影响
                if (!isCellDisabled(innerCellId)) {
                  onAttributeRemove = doOri()
                }
                unbinds.unshift(() => {
                  if (onAttributeRemove) {
                    onAttributeRemove()
                  }
                })

                // 通过vue监听，根据单元格的禁用情况动态初始化、结束单元格属性影响
                let endWatchCellDisabled = vueCore.$watch(() => {
                  return isCellDisabled(innerCellId)
                }, (cellDisabled) => {
                  if (cellDisabled) {
                    if (onAttributeRemove) {
                      onAttributeRemove()
                      onAttributeRemove = null
                    }
                  } else {
                    onAttributeRemove = doOri()
                  }
                })
                unbinds.unshift(endWatchCellDisabled)

                // 初始化单元格只读状态
                setLock(innerCellId, sheet, isCellReadOnly(innerCellId))
                unbinds.unshift(() => {
                  setLock(innerCellId, sheet, isCellReadOnly(innerCellId))
                })

                // 通过vue监听，动态设置单元格的只读情况
                let endWatchCellReadOnly = vueCore.$watch(() => {
                  return isCellReadOnly(innerCellId)
                }, (readOnly) => {
                  setLock(innerCellId, sheet, readOnly)
                })
                unbinds.unshift(endWatchCellReadOnly)

                return () => {
                  unbinds.forEach(v => v())
                }
              })
            },
            // TODO 
            // 1、单元格控制：监听点击设置默认值？是的，只要点击单元格就会设置默认值
            // 2、单元格控制：前置控制禁止输入？禁用单元格，当点选中此单元格时，显示前置单元格、区域，并弹出说明
            // 3、内容输入监听，调用可能的接口(需要实现更改单元格前调用接口)
            // 4、设置用户数据
            // initSheet

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
              unbinds.unshift(this.installExtraShowStyle(sheet))
              unbinds.unshift(cellChangedManager.initSheetListener(sheet))
              unbinds.unshift(this.installSettingDefaultValueControl(sheet))
              unbinds.unshift(this.installPreposedControl(sheet))
              return () => {
                unbinds.forEach(v => v())
              }
            },
            // 在spread初始化时调用绑定，返回解绑函数
            installSpread() {
              if(!this.getConfig().configMode){
                getSpread().fromJSON(this.defaultSsjson)
              }
              let unbinds = []
              unbinds.unshift(this.wrapSpreadTransaction())
              unbinds.unshift(this.wrapCellPaint())
              unbinds.unshift(this.repairBasicCommand())
              unbinds.unshift(setSpreadMouseHoverTitle(getSpread(), this))
              unbinds.unshift(this.listenEditCellForFormat())
              unbinds.unshift(this.listenSelectionChangedForShowHighlightCellsAndAreas())
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

              this.areaData.forEach(areaInfo => {
                let { x, y, width, height } = areaInfo
                if (sourceRowIndex >= y && sourceRowIndex <= y + height - 1) {
                  areaInfo.height += rowCount
                } else if (sourceRowIndex < y) {
                  this.deleteAreaTag(areaInfo)
                  areaInfo.y += rowCount
                  afterExecutors.push(() => {
                    this.addAreaTag(areaInfo, sheet)
                  })
                }
              })
              this.controlData.forEach(control => {
                let { sourceRow, targetRow } = control
                if (sourceRow >= rowIndex) {
                  control.sourceRow += rowCount
                }
                if (targetRow != null && targetRow >= rowIndex) {
                  control.targetRow += rowCount
                }
              })
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

              this.areaData.forEach(areaInfo => {
                let { x, y, width, height } = areaInfo

                if (sourceColumnIndex >= x && sourceColumnIndex <= x + width - 1) {
                  areaInfo.width += columnCount
                } else if (sourceColumnIndex < x) {
                  this.deleteAreaTag(areaInfo, sheet)
                  areaInfo.x += columnCount
                  afterExecutors.push(() => {
                    this.addAreaTag(areaInfo, sheet)
                  })
                }
              })

              this.controlData.forEach(control => {
                let { sourceCol, targetCol } = control
                if (sourceCol >= columnIndex) {
                  control.sourceCol += columnCount
                }
                if (targetCol != null && targetCol >= columnIndex) {
                  control.targetCol += columnCount
                }
              })

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
                } else if ((targetRow != null && isBetween(targetRow, rowIndex, endRowIndex)) ||
                  (targetAreaId != null && !this.areaMap[targetAreaId])) {
                  splice(i, 1)
                  this.resetControlTag(control.sourceRow, control.sourceCol, sheet)
                } else {
                  if (sourceRow > endRowIndex) {
                    control.sourceRow -= rowCount
                  }
                  if (targetRow != null && targetRow > endRowIndex) {
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
                } else if ((targetCol != null && isBetween(targetCol, columnIndex, endColumnIndex)) ||
                  (targetAreaId != null && !this.areaMap[targetAreaId])) {
                  splice(i, 1)
                  this.resetControlTag(control.sourceRow, control.sourceCol, sheet)
                } else {
                  if (sourceCol > endColumnIndex) {
                    control.sourceCol -= columnCount
                  }
                  if (targetCol != null && targetCol > endColumnIndex) {
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
                  // 判断跨行跨列
                  let spanInfo = sheet.getSpan(row, col)
                  if (!spanInfo || (spanInfo.row == row && spanInfo.col == col)) {
                    let cellData = {}
                    cellPropertyDataManager.setPropertyKeyToCellData(cellData, propertyKey)
                    cellPropertyDataManager.setCellData(row, col, cellData)
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
            findAreaInfo(rowIndex, columnIndex, rowCount, columnCount, isContain) {
              if (rowCount == null) {
                rowCount = 1
              }
              if (columnCount == null) {
                columnCount = 1
              }
              if (isContain == null || isContain === false) {
                for (let i = 0; i < this.areaData.length; i++) {
                  let { x, y, width, height } = this.areaData[i]
                  if (columnIndex + columnCount - 1 >= x && columnIndex <= x + width - 1 && rowIndex + rowCount - 1 >= y && rowIndex <= y + height - 1) {
                    return this.areaData[i]
                  }
                }
              } else if (isContain === true) {
                for (let i = 0; i < this.areaData.length; i++) {
                  let { x, y, width, height } = this.areaData[i]
                  if (columnIndex >= x && columnIndex + columnCount - 1 <= x + width - 1 && rowIndex >= y && rowIndex + rowCount - 1 <= y + height - 1) {
                    return this.areaData[i]
                  }
                }
              }
              return null
            },
            // 设置数据区域
            setArea(rowIndex, columnIndex, rowCount, columnCount) {
              if (rowIndex == -1) {
                rowIndex = 0
              }
              if (columnIndex == -1) {
                columnIndex = 0
              }
              if (this.findAreaIndex(rowIndex, columnIndex, rowCount, columnCount) != -1) {
                let msg = "已经设置过的区域无法再次设置"
                console.error(msg)
                throw msg
              }

              let id = getNewAreaId()
              let area = {
                x: columnIndex,
                y: rowIndex,
                width: columnCount,
                height: rowCount,
                id: id
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
            // 删除区域 TODO 有关联设置时需要禁止删除
            deleteArea(rowIndex, columnIndex, rowCount, columnCount) {
              if (rowIndex == -1) {
                rowIndex = 0
              }
              if (columnIndex == -1) {
                columnIndex = 0
              }
              let index = this.findAreaIndex(rowIndex, columnIndex, rowCount, columnCount)
              if (index != -1) {
                let [deletedArea] = this.areaData.splice(index, 1)
                this.deleteAreaTag(deletedArea)
              } else {
                console.error("删除区域仅支持准确对应")
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
            findAreaIndex(rowIndex, columnIndex, rowCount, columnCount) {
              if (rowIndex == -1) {
                rowIndex = 0
              }
              if (columnIndex == -1) {
                columnIndex = 0
              }
              return this.areaData.findIndex(({ x, y, width, height }) => {
                if (x == columnIndex && y == rowIndex && width == columnCount && height == rowCount) {
                  return true
                } else {
                  return false
                }
              })
            },
            findArea(rowIndex, columnIndex, rowCount, columnCount) {
              let index = this.findAreaIndex(rowIndex, columnIndex, rowCount, columnCount)
              if (index == -1) {
                return null
              } else {
                return copyDataSimple(this.areaData[index])
              }
            },
            getAllArea() {
              return copyDataSimple(this.areaData)
            },
            // 获取以当前单元格为标题显示单元格的区域
            getTitleAreas(rowIndex, columnIndex) {
              return this.titleCellKeyAreaMap[getCellKey(rowIndex, columnIndex)]
            },
            getAreaTitle(areaId) {
              return this.areaNumberMap[areaId]
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
                  type: "2"// 1--完全回填  2--回填条件  3--默认回填值
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
                    type: "2"// 1--完全回填  2--回填条件  3--默认回填值
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
              if (!attr) {
                return true
              } else {
                return !this.isValueRequiredAttribute(attr)
              }
            },
            setCellEntirelyFillControl(row, col) {
              if (this.getCellEntirelyFillControl(row, col)) {
                this.deleteCellEntirelyFillControl(row, col)
              }
              let attr = this.getPropertyKey(row, col)
              if (!attr || !this.isValueRequiredAttribute(attr)) {
                this.controlData.push({
                  sourceRow: row,
                  sourceCol: col,
                  type: "1"// 1--完全回填  2--回填条件  3--默认回填值
                })
                this.resetControlTag(row, col)
              } else {
                console.error(`属性：${attr}为必填属性，不可设置完全回填后设置`)
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
                  type: "3"// 1--完全回填  2--回填条件  3--默认回填值
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
                    type: "3"// 1--完全回填  2--回填条件  3--默认回填值
                  })
                }
              })
              this.resetControlTag(row, col)
            },

            getTagObjs(ctrls) {
              return ctrls.map((ctrl) => {
                let tagObj = {}
                if (ctrl.targetRow != null && ctrl.targetCol != null) {
                  tagObj.row = ctrl.targetRow
                  tagObj.col = ctrl.targetCol
                } else if (ctrl.targetAreaId != null) {
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
                selectRanges: (ranges) => {
                  ranges.forEach(({
                    row: rangeRow,
                    rowCount: rangeRowCount,
                    col: rangeCol,
                    colCount: rangeColCount,
                  }) => {
                    commonUtil.everyCell(rangeRow, rangeCol, rangeRowCount, rangeColCount, (row, col) => {
                      let cellKey = getCellKey(row, col)
                      if (!vueObj.cellPositionMap[cellKey]) {
                        vueObj.$set(vueObj.cellPositionMap, cellKey, [row, col])
                      }
                    })
                  })
                  refreshBackColors(vueObj.cellPositions)
                },
                unselectRanges: (ranges) => {
                  ranges.forEach(({
                    row: rangeRow,
                    rowCount: rangeRowCount,
                    col: rangeCol,
                    colCount: rangeColCount,
                  }) => {
                    commonUtil.everyCell(rangeRow, rangeCol, rangeRowCount, rangeColCount, (row, col) => {
                      let cellKey = getCellKey(row, col)
                      if (vueObj.cellPositionMap[cellKey]) {
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

            checkBillBackfillIsOver() {// TODO 判断是否完成了回填
              let sheet = getSpread().getActiveSheet()
              let findedErrorCellProp = cellPropertyDataManager.findCellData((row, col, cellData) => {
                if (cellData) {
                  let spanInfo = sheet.getSpan(row, col)
                  if ((!spanInfo || (spanInfo.row == row && spanInfo.col == col))) {
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
        getData(this).spread = new Sheets.Workbook(el)
        getData(this).uninstallSpread = getData(this).vueCore.installSpread()
        initTabStripStyle(el)
      } else {
        throw "请传入div元素对象"
      }
    }

    BillSpreadModule.prototype.setSpread = function (spread) {
      if (this.getSpread()) {
        throw "spread工作簿已挂载过，禁止重复挂载"
      }
      if (spread instanceof Sheets.Workbook) {
        getData(this).spread = spread
        getData(this).uninstallSpread = getData(this).vueCore.installSpread()
        initTabStripStyle(spread.getHost())
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
      getData(this).vueCore.$destroy()
      return dataMapUtil.deleteData(this)
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
      cellDataList,
      areaList,
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
        cellDataList,
        areaList
      })
      this.setUserData(userData)
    }

    // 设置用户数据 {staffId, name, roleInfoList:[{code, name}...]}
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
      return getData(this).vueCore.getTemplateData(localUse) //{ ssjsonDataString, cellDataList, areaList }
    }

    // 设置模板数据
    BillSpreadModule.prototype.setTemplateData = function ({
      ssjsonDataString,
      cellDataList,
      areaList
    }) {
      // 如果之前有绑定过则先按照旧数据进行解绑
      if (getData(this).uninstallSpreadAfterSetTemplateData) {
        getData(this).uninstallSpreadAfterSetTemplateData()
        getData(this).uninstallSpreadAfterSetTemplateData = null
      }

      getData(this).vueCore.setTemplateData({
        ssjsonDataString,
        cellDataList,
        areaList
      })
      // 进行需要初始化单元格数据之后的spread绑定，留存解绑函数
      getData(this).uninstallSpreadAfterSetTemplateData = getData(this).vueCore.installSpreadAfterSetTemplateData()
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
      "findAreaInfo",
      "findArea",
      "getAllArea",
      "getAreaTitle",

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

    return BillSpreadModule
  })()

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