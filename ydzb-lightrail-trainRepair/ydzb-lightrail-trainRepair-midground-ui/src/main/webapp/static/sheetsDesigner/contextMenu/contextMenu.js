(function () {
  'use strict'

  var designer = GC.Spread.Sheets.Designer
  var chartHelper = designer.util.chartHelper
  var contextMenu = {}

  var SEPARATOR = "separator", MENUITEM_NAME_PREFIX = "designer.", ICON_CLASS_PREFIX = "gc-spread-",
    SPREAD_NAME_PREFIX = "gc.spread.", SPREAD_COMMAND_PREFIX = "gc.spread.contextMenu."
  var REMOVE_SLICER = "removeSlicer", TOGGLE_COMMENT = "toggleComment", DELETE_COMMENT = "deleteComment",
    EDIT_COMMENT = "editComment",
    UNHIDE_SHEET = "unhideSheet", HIDE_SHEET = "hideSheet", UNHIDE_ROWS = 'unhideRows',
    UNHIDE_COLS = "unhideColumns", SORT_ASCEND = "sortAscend",
    SORT_DESCEND = "sortDescend", SORT = "sort", FILTER = "filter", INSERT_COMMENT = "insertComment",
    DELETE_SHEET = "deleteSheet",
    INSERT_SHEET = "insertSheet", CLEAR_CONTENT = "clearContents", PASTE = "paste", PASTE_OPTIONS = "pasteOptions",
    PASTE_FORMATTING = "pasteFormatting", PASTE_VALUES = "pasteValues", PASTE_FORMULA = "pasteFormula",
    PASTE_VALUES_FORMATTING = "pasteValuesFormatting", PASTE_FORMULA_FORMATTING = "pasteFormulaFormatting",
    PASTE_ALL = "pasteAll", CUT = "cut", COPY = "copy",
    GROUP_HEADER = "groupHeader", PROTECT_SHEET = "protectSheet", CUSTOM_SORT = 'customSort',
    FORMAT_CELLS = 'formatCells', EDIT_CELLTYPE = 'editCellType', RICH_TEXT = 'richText', DEFINE_NAME = 'defineName', CELL_TAG = 'cellTag', ROW_HEIGHT = 'rowHeight',
    COL_WIDTH = 'columnWidth', EDIT_CELL_DROPDOWNS = 'editCellDropdowns',
    HYPERLINK_FORMULA = "HYPERLINK", LINK = 'link', EDIT_HYPERLINK = 'editHyperlink', OPEN_HYPERLINK = 'openHyperlink', REMOVE_HYPERLINK = 'removeHyperlink', REMOVE_HYPERLINKS = 'removeHyperlinks',
    SHEET_TAG = 'sheetTag', ROW_TAG = 'rowTag', COL_TAG = 'colTag', HIDE_ROWS = "hideRows",
    HIDE_COLUMNS = "hideColumns", TAB_COLOR = "tabColor", SHOW_TAB_COLOR = "showTabColor",
    FORMAT_COMMENT = "formatComment", SLICER_CUT = 'slicerCut', SLICER_COPY = 'slicerCopy',
    SLICER_PASTE_OPTIONS = 'slicerPasteOptions', SLICER_PASTE = 'slicerPaste',
    SLICER_SORT_ASCEND = 'slicerSortAscend',
    SLICER_SORT_DESCEND = 'slicerSortDescend', SLICER_PROPERTY = 'slicerProperty', SLICER_SETTING = 'slicerSetting',
    INSERT_COLUMNS = 'insertColumns', INSERT_ROWS = 'insertRows',
    DELETE_COLUMNS = 'deleteColumns', DELETE_ROWS = 'deleteRows', INSERT_DIALOG = 'insert_dialog',
    DELETE_DIALOG = 'delete_dialog', COLUMN_HEADERS = "columnHeaders", OUTLINE_COLUMN = "outlineColumn ", ROW_HEADERS = 'rowHeaders',
    CHANGE_CHART_TYPE_DIALOG = 'changeChartTypeDialog', SELECT_CHART_DATA_DIALOG = 'selectChartDataDialog',
    FORMAT_CHART = 'formatChart', MOVE_CHART_DIALOG = 'moveChartDialog',
    FLOATING_OBJECT_CUT = 'floatingObjectCut', FLOATING_OBJECT_COPY = 'floatingObjectCopy',
    RESET_CHART_COLOR = 'resetChartColor', SHAPE = 'shape', CUT_SHAPE = 'cutShapes', COPY_SHAPE = 'copyShapes',
    PASTE_SHAPE = 'pasteShapes', GROUP = 'group', GROUP_SHAPE = 'groupShapes', UNGROUP_SHAPE = 'ungroupShapes',
    TOTAL_ROW = "Totals Row", TO_RANGE = "tableToRange", FORMAT_SHAPES = 'formatShapes',
    TABLE_INSERT = "tableInsert", TABLE_DELETE = "tableDelete",
    TABLE_INSERT_ROWS_ABOVE = "tableInsertRowsAbove", TABLE_INSERT_ROWS_BELOW = "tableInsertRowsBelow",
    TABLE_INSERT_COLUMNS_LEFT = "tableInsertColumnsLeft", TABLE_INSERT_COLUMNS_RIGHT = "tableInsertColumnsRight",
    TABLE_DELETE_ROWS = "tableDeleteRows", TABLE_DELETE_COLUMNS = "tableDeleteColumns",
    INSERT_COPY_CELLS = "insertCopiedCells", INSERT_CUT_CELLS = "insertCutCells", SHIFT_CELLS_DOWN = "shiftCellsDown",
    SHIFT_CELLS_RIGHT = "shiftCellsRight"
  var TABLE_INSERT_ROWS_ABOVE_FOR_CONTEXT_MENU = 'tableInsertRowsAboveForContextMenu',
    TABLE_INSERT_ROWS_BELOW_FOR_CONTEXT_MENU = 'tableInsertRowsBelowForContextMenu',
    TABLE_DELETE_ROWS_FOR_CONTEXT_MENU = 'tableDeleteRowsForContextMenu',
    TABLE_INSERT_COLUMNS_LEFT_FOR_CONTEXT_MENU = 'tableInsertColumnsLeftForContextMenu',
    TABLE_INSERT_COLUMNS_RIGHT_FOR_CONTEXT_MENU = 'tableInsertColumnsRightForContextMenu',
    TABLE_DELETE_COLUMNS_FOR_CONTEXT_MENU = 'tableDeleteColumnsForContextMenu'

  // 张天可
  var ZSJCJYW_COMMAND_PREFIX = app.core.ZSJCJYW_COMMAND_PREFIX
  var CUSTOM_INSERT_ROWS = 'customInsertRows'
  var INSERT_LAST_ROWS = 'insertLastRows'
  var INSERT_NEXT_ROWS = 'insertNextRows'
  var CUSTOM_INSERT_COLUMNS = 'customInsertColumns'
  var INSERT_LAST_COLUMNS = 'insertLastColumns'
  var INSERT_NEXT_COLUMNS = 'insertNextColumns'

  var VIEWPORT = "viewport", COLHEADER = "colHeader", ROWHEADER = "rowHeader", CORNER = 'corner', SLICER = "slicer",
    SHEETTAB = "sheetTab", CHART = 'chart', TABLE = "table", VIEWPORT_WITHOUT_TABLE = "vpWithoutTb"

  function isNullOrUndefined(value) {
    return value === undefined || value === null
  }

  function iteratorMenuDataByName(menuData, name, callback) {
    var menuDataCount = menuData.length, menuItemDataIndex = 0
    for (; menuItemDataIndex < menuDataCount; menuItemDataIndex++) {
      if (isNullOrUndefined(menuData[menuItemDataIndex])) {
        continue
      }
      if (name === menuData[menuItemDataIndex].name) {
        callback(menuData, menuItemDataIndex)
        return
      }
      if (menuData[menuItemDataIndex].subMenu) {
        iteratorMenuDataByName(menuData[menuItemDataIndex].subMenu, name, callback)
      }
    }
  }

  function containsLockedCellsInSelection(sheet, row, column, rowCount, columnCount) {
    row = row < 0 ? 0 : row
    column = column < 0 ? 0 : column
    for (var i = row; i < row + rowCount; i++) {
      for (var j = column; j < column + columnCount; j++) {
        if (sheet.getCell(i, j).locked()) {
          return true
        }
      }
    }
  }
  function containLockedCellsInSelections(sheet) {
    var selections = sheet.getSelections()
    var containLockedCellInSelections = false
    for (var i = 0; i < selections.length; i++) {
      if (containsLockedCellsInSelection(sheet, selections[i].row, selections[i].col, selections[i].rowCount, selections[i].colCount)) {
        containLockedCellInSelections = true
      }
    }
    return containLockedCellInSelections
  }
  function isFullRow(sheet) {
    var selections = sheet.getSelections()
    for (var i = 0; i < selections.length; i++) {
      if (selections[i].col !== -1) {
        return false
      }
    }
    return true
  }
  function isFullCol(sheet) {
    var selections = sheet.getSelections()
    for (var i = 0; i < selections.length; i++) {
      if (selections[i].row !== -1) {
        return false
      }
    }
    return true
  }
  function getTable(sheet, selection) {
    if (!selection) {
      return null
    }
    for (var row = selection.row; row < selection.row + selection.rowCount; row++) {
      for (var col = selection.col; col < selection.col + selection.colCount; col++) {
        if (sheet.tables) {
          var table = sheet.tables.find(row, col)
          if (table) {
            return table
          }
        }
      }
    }
    return null
  }
  function showInsertCellCopyCut(sheet) {
    var acRow = sheet.getActiveRowIndex(), acCol = sheet.getActiveColumnIndex()
    var clipboardHelper = sheet.getClipboardHelperObj()
    if (!clipboardHelper.showInsertCopyPasteCells) {
      return false
    }
    if (sheet.tables) {
      if (sheet.tables.find(acRow, acCol)) {
        return false
      }
    }
    var spans = sheet.getSpans()
    for (var i = 0; i < spans.length; i++) {
      var span = spans[i]
      if (acRow >= span.row && acRow < (span.row + span.rowCount) && acCol >= span.col && acCol < (span.col + span.colCount)) {
        return false
      }
    }
    return true
  }
  function isShowLinkItem(sheet) {
    var ranges = sheet.getSelections()
    if (!ranges || ranges.length > 1) {
      return false
    }
    var range = ranges[0]
    var row = range.row, col = range.col, rowCount = range.rowCount, colCount = range.colCount
    if (rowCount === 1 && colCount === 1) {
      if (!isNullOrUndefined(sheet.getHyperlink(row, col))) {
        return false
      }
      var formula = sheet.getFormula(row, col)
      if (formula && formula.indexOf(HYPERLINK_FORMULA) >= -1) {
        return false
      }
    }
    return true
  }
  function isShowEditLinkItem(sheet) {
    var ranges = sheet.getSelections()
    if (!ranges || ranges.length > 1) {
      return false
    }
    var range = ranges[0]
    if (range) {
      var row = range.row, col = range.col, rowCount = range.rowCount, colCount = range.colCount
      if (rowCount === 1 && colCount === 1) {
        if (!isNullOrUndefined(sheet.getHyperlink(row, col))) {
          var formula = sheet.getFormula(row, col)
          if (!formula || formula.indexOf(HYPERLINK_FORMULA) === -1) {
            return true
          }
        }
      }
    }
    return false
  }
  function isShowOpenLinkItem(sheet) {
    var ranges = sheet.getSelections()
    if (!ranges || ranges.length > 1) {
      return false
    }
    var range = ranges[0]
    if (range) {
      var row = range.row, col = range.col, rowCount = range.rowCount, colCount = range.colCount
      if (rowCount === 1 && colCount === 1) {
        if (!isNullOrUndefined(sheet.getHyperlink(row, col))) {
          return true
        }
      }
    }
    return false
  }
  function isShowRemoveLinkItem(sheet) {
    var ranges = sheet.getSelections()
    if (!ranges || ranges.length > 1) {
      return false
    }
    var range = ranges[0]
    if (range) {
      var row = range.row, col = range.col, rowCount = range.rowCount, colCount = range.colCount
      if (rowCount === 1 && colCount === 1) {
        if (!isNullOrUndefined(sheet.getHyperlink(row, col))) {
          var formula = sheet.getFormula(row, col)
          if (!formula || formula.indexOf(HYPERLINK_FORMULA) === -1) {
            return true
          }
        }
      }
    }
    return false
  }
  function isShowRemoveLinksItem(sheet) {
    var ranges = sheet.getSelections()
    if (ranges.length === 1) {
      var range = ranges[0]
      var rowCount = range.rowCount, colCount = range.colCount
      if (rowCount === 1 && colCount === 1) {
        return false
      }
    }
    for (var i = 0, len = ranges.length; i < len; i++) {
      var range = ranges[i]
      var row = range.row, col = range.col, rowCount = range.rowCount, colCount = range.colCount
      if (row === -1) {
        row = 0
      }
      if (col === -1) {
        col = 0
      }
      for (var r = 0; r < rowCount; r++) {
        for (var c = 0; c < colCount; c++) {
          if (!isNullOrUndefined(sheet.getHyperlink(row + r, col + c))) {
            var formula = sheet.getFormula(row + r, col + c)
            if (!formula || formula.indexOf(HYPERLINK_FORMULA) === -1) {
              return true
            }
          }
        }
      }
    }
    return false
  }
  function initContextMenu() {
    var spread = designer.wrapper.spread

    function DesignerContextMenu() {
    }

    function checkWorkArea(hitInfo, workbook, menuItem) {
      return new RegExp(SHAPE, "i").test(menuItem.workArea) && hitInfo.worksheetHitInfo && hitInfo.worksheetHitInfo.shapeHitInfo
    }
    DesignerContextMenu.prototype = new GC.Spread.Sheets.ContextMenu.ContextMenu()
    DesignerContextMenu.prototype.onOpenMenu = function (menuData, itemsDataForShown, hitInfo, workbook) {
      var self = this
      var i
      var sheet = workbook.getActiveSheet()
      if (hitInfo.tabStripHitInfo) {
        var sheets = workbook.sheets
        for (i = 0; i < sheets.length; i++) {
          if (!sheets[i].visible()) {
            iteratorMenuDataByName(itemsDataForShown, MENUITEM_NAME_PREFIX + UNHIDE_SHEET, function (menuItemsArray, menuItemIndex) {
              menuItemsArray[menuItemIndex].disable = false
            })
            return
          }
        }
        iteratorMenuDataByName(itemsDataForShown, MENUITEM_NAME_PREFIX + UNHIDE_SHEET, function (menuItemsArray, menuItemIndex) {
          menuItemsArray[menuItemIndex].disable = true
        })
      } else if (hitInfo.worksheetHitInfo && hitInfo.worksheetHitInfo.shapeHitInfo) {
        itemsDataForShown.length = 0
        for (i = 0; i < menuData.length; i++) {
          if (checkWorkArea(hitInfo, workbook, menuData[i])) {
            itemsDataForShown.push(menuData[i])
          }
        }
        var shapes = sheet.shapes.all()
        var isGroupShapeBeSelected = false
        var selectedShapeCount = 0
        for (i = 0; i < shapes.length; i++) {
          if (shapes[i].isSelected()) {
            selectedShapeCount++
            if (shapes[i] instanceof GC.Spread.Sheets.Shapes.GroupShape) {
              isGroupShapeBeSelected = true
            }
          }
        }
        if (selectedShapeCount < 2) { // only when multiple select shape, group menu item is enable
          iteratorMenuDataByName(itemsDataForShown, MENUITEM_NAME_PREFIX + GROUP_SHAPE, function (menuItemsArray, menuItemIndex) {
            menuItemsArray[menuItemIndex].disable = true
          })
        } else {
          iteratorMenuDataByName(itemsDataForShown, MENUITEM_NAME_PREFIX + GROUP_SHAPE, function (menuItemsArray, menuItemIndex) {
            menuItemsArray[menuItemIndex].disable = false
          })
        }
        if (!isGroupShapeBeSelected) { // only selected shapes contain a group shape, ungroup menu item is enable
          iteratorMenuDataByName(itemsDataForShown, MENUITEM_NAME_PREFIX + UNGROUP_SHAPE, function (menuItemsArray, menuItemIndex) {
            menuItemsArray[menuItemIndex].disable = true
          })
        } else {
          iteratorMenuDataByName(itemsDataForShown, MENUITEM_NAME_PREFIX + UNGROUP_SHAPE, function (menuItemsArray, menuItemIndex) {
            menuItemsArray[menuItemIndex].disable = false
          })
        }
      } else {
        var row = sheet.getActiveRowIndex()
        var col = sheet.getActiveColumnIndex()
        var table = sheet.tables.find(row, col)
        if (!table) {
          iteratorMenuDataByName(itemsDataForShown, MENUITEM_NAME_PREFIX + TABLE, function (menuItemsArray, menuItemIndex) {
            menuItemsArray.splice(menuItemIndex, 1)
          })
        }
        var comment = sheet.comments.get(row, col)
        if (!comment) {
          iteratorMenuDataByName(itemsDataForShown, MENUITEM_NAME_PREFIX + FORMAT_COMMENT, function (menuItemsArray, menuItemIndex) {
            menuItemsArray.splice(menuItemIndex, 1)
          })
        }
        var style = sheet.getStyle(row, col)
        var hasCellType = style && style.cellType && style.cellType.typeName !== "1" /*Text*/
        var hasCellButtons = style && style.cellButtons && style.cellButtons.length > 0
        if (!hasCellType) {
          iteratorMenuDataByName(itemsDataForShown, MENUITEM_NAME_PREFIX + EDIT_CELLTYPE, function (menuItemsArray, menuItemIndex) {
            menuItemsArray.splice(menuItemIndex, 1)
          })
        }
        if (!hasCellButtons) {
          iteratorMenuDataByName(itemsDataForShown, MENUITEM_NAME_PREFIX + EDIT_CELL_DROPDOWNS, function (menuItemsArray, menuItemIndex) {
            menuItemsArray.splice(menuItemIndex, 1)
          })
        }
      }
      var workSheetHitInfo = hitInfo.worksheetHitInfo
      if (workSheetHitInfo) {
        var chart = workSheetHitInfo.floatingObjectHitInfo && workSheetHitInfo.floatingObjectHitInfo.floatingObject
        if (chart instanceof GC.Spread.Sheets.Charts.Chart) {
          var chartHitInfo = chart.hitTest(hitInfo.x, hitInfo.y)
          if (chartHitInfo) {
            var chartElementType = chartHelper.chartElementType
            iteratorMenuDataByName(itemsDataForShown, MENUITEM_NAME_PREFIX + FORMAT_CHART, function (menuItemsArray, menuItemIndex) {
              menuItemsArray[menuItemIndex].text = designer.res.contextMenu.formatChart[chartElementType[chartHitInfo.elementType]]
              menuItemsArray[menuItemIndex].options = chartHitInfo
            })
          }
        }
      }
      self.insertCopyCutCells(itemsDataForShown, workbook)
      self.checkAvailableByCurrentSelections(itemsDataForShown, workbook)
      self.checkAvailableByProtectionSettings(itemsDataForShown, workbook)
      self.filterTableMenuDataBySelections(itemsDataForShown, workbook)
      self.trimNullObject(itemsDataForShown)
      self.trimSeparatorLine(itemsDataForShown)
      self.filterInsertCellsAndInsert(itemsDataForShown)
    }
    DesignerContextMenu.prototype.filterInsertCellsAndInsert = function (itemsDataForShown) {
      var insertIndex = 0, haveInsertCellsItem = false
      for (var i = 0; i < itemsDataForShown.length; i++) {
        var itemName = itemsDataForShown[i].name
        if (itemName === MENUITEM_NAME_PREFIX + ROWHEADER + INSERT_COPY_CELLS
          || itemName === MENUITEM_NAME_PREFIX + ROWHEADER + INSERT_CUT_CELLS ||
          itemName === MENUITEM_NAME_PREFIX + COLHEADER + INSERT_COPY_CELLS
          || itemName === MENUITEM_NAME_PREFIX + COLHEADER + INSERT_CUT_CELLS) {
          haveInsertCellsItem = true
        }
        if (itemName === MENUITEM_NAME_PREFIX + INSERT_ROWS || itemName === MENUITEM_NAME_PREFIX + INSERT_COLUMNS) {
          insertIndex = i
          if (haveInsertCellsItem) {
            break
          }
        }
      }
      haveInsertCellsItem && itemsDataForShown.splice(insertIndex, 1)
    }
    DesignerContextMenu.prototype.insertCopyCutCells = function (itemsDataForShown, workbook) {
      var sheet = workbook.getActiveSheet(), clipboardHelper = sheet.getClipboardHelperObj()
      if (showInsertCellCopyCut(sheet)) {
        for (var i = itemsDataForShown.length - 1; i >= 0; i--) {
          if (!itemsDataForShown[i]) {
            continue
          }
          if (itemsDataForShown[i].name === MENUITEM_NAME_PREFIX + INSERT_DIALOG) {
            itemsDataForShown.splice(i, 1)
            break
          }
          var mulitSelect = sheet.getSelections().length === 1 ? false : true
          switch (itemsDataForShown[i].name) {
            case MENUITEM_NAME_PREFIX + ROWHEADER + INSERT_COPY_CELLS:
              if (!(!clipboardHelper.isCutting && clipboardHelper.ranges && clipboardHelper.ranges.length === 1 && sheet.getColumnCount() % clipboardHelper.ranges[0].colCount === 0 && !mulitSelect)) {
                itemsDataForShown.splice(i, 1)
              }
              break
            case MENUITEM_NAME_PREFIX + ROWHEADER + INSERT_CUT_CELLS:
              if (!(clipboardHelper.isCutting && clipboardHelper.ranges && clipboardHelper.ranges.length === 1 && sheet.getColumnCount() % clipboardHelper.ranges[0].colCount === 0 && !mulitSelect)) {
                itemsDataForShown.splice(i, 1)
              }
              break
            case MENUITEM_NAME_PREFIX + COLHEADER + INSERT_COPY_CELLS:
              if (!(!clipboardHelper.isCutting && clipboardHelper.ranges && clipboardHelper.ranges.length === 1 && sheet.getRowCount() % clipboardHelper.ranges[0].rowCount === 0 && !mulitSelect)) {
                itemsDataForShown.splice(i, 1)
              }
              break
            case MENUITEM_NAME_PREFIX + COLHEADER + INSERT_CUT_CELLS:
              if (!(clipboardHelper.isCutting && clipboardHelper.ranges && clipboardHelper.ranges.length === 1 && sheet.getRowCount() % clipboardHelper.ranges[0].rowCount === 0 && !mulitSelect)) {
                itemsDataForShown.splice(i, 1)
              }
              break
            case MENUITEM_NAME_PREFIX + INSERT_COPY_CELLS:
              if (!(!clipboardHelper.isCutting && clipboardHelper.ranges && clipboardHelper.ranges.length === 1 && !mulitSelect)) {
                itemsDataForShown.splice(i, 1)
              }
              break
            case MENUITEM_NAME_PREFIX + INSERT_CUT_CELLS:
              if (!(clipboardHelper.isCutting && clipboardHelper.ranges && clipboardHelper.ranges.length === 1 && !mulitSelect)) {
                itemsDataForShown.splice(i, 1)
              }
              break
          }
        }
      } else {
        for (var i = itemsDataForShown.length - 1; i >= 0; i--) {
          if (!itemsDataForShown[i]) {
            continue
          }
          switch (itemsDataForShown[i].name) {
            case MENUITEM_NAME_PREFIX + ROWHEADER + INSERT_COPY_CELLS:
              itemsDataForShown.splice(i, 1)
              break
            case MENUITEM_NAME_PREFIX + ROWHEADER + INSERT_CUT_CELLS:
              itemsDataForShown.splice(i, 1)
              break
            case MENUITEM_NAME_PREFIX + COLHEADER + INSERT_COPY_CELLS:
              itemsDataForShown.splice(i, 1)
              break
            case MENUITEM_NAME_PREFIX + COLHEADER + INSERT_CUT_CELLS:
              itemsDataForShown.splice(i, 1)
              break
            case MENUITEM_NAME_PREFIX + INSERT_COPY_CELLS:
              itemsDataForShown.splice(i, 1)
              break
            case MENUITEM_NAME_PREFIX + INSERT_CUT_CELLS:
              itemsDataForShown.splice(i, 1)
              break
          }
        }
      }
    }

    DesignerContextMenu.prototype.checkAvailableByCurrentSelections = function (itemsDataForShown, workbook) {
      var self = this
      var sheet = workbook.getActiveSheet(), tables = sheet.tables
      for (var i = 0; i < itemsDataForShown.length; i++) {
        if (!itemsDataForShown[i]) {
          continue
        }
        switch (itemsDataForShown[i].name) {
          case MENUITEM_NAME_PREFIX + INSERT_ROWS:
            // 张天可
            // if (!isFullRow(sheet)) {
            //   itemsDataForShown[i].disable = true
            // }
            break
          case MENUITEM_NAME_PREFIX + DELETE_ROWS:
            // 张天可
            // if (!isFullRow(sheet) || tables && tables.containTableHeaderTotalRow()) {
            //   itemsDataForShown[i].disable = true
            // }
            break
          case MENUITEM_NAME_PREFIX + INSERT_COLUMNS:
          case MENUITEM_NAME_PREFIX + DELETE_COLUMNS:
            // 张天可
            // if (!isFullCol(sheet)) {
            //   itemsDataForShown[i].disable = true
            // }
            break
          case MENUITEM_NAME_PREFIX + RICH_TEXT:
            if (sheet.getFormula(sheet.getActiveRowIndex(), sheet.getActiveColumnIndex())) {
              itemsDataForShown[i].disable = true
            }
            break
          case MENUITEM_NAME_PREFIX + TABLE_INSERT_ROWS_ABOVE:
            if (tables && (tables.containTableHeader() || tables.noRoomToInsertRow())) {
              itemsDataForShown[i].disable = true
            }
            break
          case MENUITEM_NAME_PREFIX + TABLE_INSERT_ROWS_BELOW:
            if (tables && tables.noRoomToInsertRow()) {
              itemsDataForShown[i].disable = true
            }
            break
          case MENUITEM_NAME_PREFIX + TABLE_DELETE_ROWS:
            if (tables && (tables.checkTableRow() || tables.containTableHeaderTotalRow(true/* ignoreRangeContains */))) {
              itemsDataForShown[i].disable = true
            }
            break
          case MENUITEM_NAME_PREFIX + TABLE_INSERT_COLUMNS_LEFT:
          case MENUITEM_NAME_PREFIX + TABLE_INSERT_COLUMNS_RIGHT:
            if (tables && tables.noRoomToInsertColumn()) {
              itemsDataForShown[i].disable = true
            }
            break
          case MENUITEM_NAME_PREFIX + TABLE_DELETE_COLUMNS:
            if (tables && tables.checkTableCol()) {
              itemsDataForShown[i].disable = true
            }
            break
          case MENUITEM_NAME_PREFIX + LINK:
            if (!isShowLinkItem(sheet)) {
              itemsDataForShown.splice(i, 1)
              i--
            }
            break
          case MENUITEM_NAME_PREFIX + EDIT_HYPERLINK:
            if (!isShowEditLinkItem(sheet)) {
              itemsDataForShown.splice(i, 1)
              i--
            }
            break
          case MENUITEM_NAME_PREFIX + OPEN_HYPERLINK:
            if (!isShowOpenLinkItem(sheet)) {
              itemsDataForShown.splice(i, 1)
              i--
            }
            break
          case MENUITEM_NAME_PREFIX + REMOVE_HYPERLINK:
            if (!isShowRemoveLinkItem(sheet)) {
              itemsDataForShown.splice(i, 1)
              i--
            }
            break
          case MENUITEM_NAME_PREFIX + REMOVE_HYPERLINKS:
            if (!isShowRemoveLinksItem(sheet)) {
              itemsDataForShown.splice(i, 1)
              i--
            }
            break
          default:
            break
        }
        if (itemsDataForShown[i] && itemsDataForShown[i].subMenu) {
          self.checkAvailableByCurrentSelections(itemsDataForShown[i].subMenu, spread)
        }
      }
    }

    DesignerContextMenu.prototype.checkAvailableByProtectionSettings = function (itemsDataForShown, workbook) {
      var self = this
      var sheet = workbook.getActiveSheet(), isProtected = sheet.options.isProtected,
        protectionOptions = sheet.options.protectionOptions
      if (!isProtected) {
        return
      }
      if (protectionOptions) {
        for (var i = 0; i < itemsDataForShown.length; i++) {
          if (!itemsDataForShown[i]) {
            continue
          }
          switch (itemsDataForShown[i].name) {
            case MENUITEM_NAME_PREFIX + FLOATING_OBJECT_CUT:
            case MENUITEM_NAME_PREFIX + FLOATING_OBJECT_COPY:
            case MENUITEM_NAME_PREFIX + CUT_SHAPE:
            case MENUITEM_NAME_PREFIX + RESET_CHART_COLOR:
            case MENUITEM_NAME_PREFIX + CHANGE_CHART_TYPE_DIALOG:
            case MENUITEM_NAME_PREFIX + SELECT_CHART_DATA_DIALOG:
            case MENUITEM_NAME_PREFIX + MOVE_CHART_DIALOG:
            case MENUITEM_NAME_PREFIX + FORMAT_CHART:
            case MENUITEM_NAME_PREFIX + SLICER_CUT:
            case MENUITEM_NAME_PREFIX + SLICER_COPY:
            case MENUITEM_NAME_PREFIX + SLICER_PASTE:
            case MENUITEM_NAME_PREFIX + SLICER_SORT_ASCEND:
            case MENUITEM_NAME_PREFIX + SLICER_SORT_DESCEND:
            case MENUITEM_NAME_PREFIX + REMOVE_SLICER:
            case MENUITEM_NAME_PREFIX + SLICER_PROPERTY:
            case MENUITEM_NAME_PREFIX + SLICER_SETTING:
              if (!protectionOptions.allowEditObjects) {
                itemsDataForShown.splice(0, itemsDataForShown.length)
              }
              break
            case MENUITEM_NAME_PREFIX + FORMAT_COMMENT:
              if (!protectionOptions.allowEditObjects) {
                itemsDataForShown[i] = null
              }
              break
            case MENUITEM_NAME_PREFIX + ROW_HEIGHT:
              if (!protectionOptions.allowResizeRows) {
                itemsDataForShown[i].disable = true
              }
              break
            case MENUITEM_NAME_PREFIX + COL_WIDTH:
              if (!protectionOptions.allowResizeColumns) {
                itemsDataForShown[i].disable = true
              }
              break
            case MENUITEM_NAME_PREFIX + INSERT_DIALOG:
            case MENUITEM_NAME_PREFIX + DELETE_DIALOG:
            case MENUITEM_NAME_PREFIX + FORMAT_CELLS:
            case MENUITEM_NAME_PREFIX + RICH_TEXT:
            case MENUITEM_NAME_PREFIX + DEFINE_NAME:
            case MENUITEM_NAME_PREFIX + CELL_TAG:
            case MENUITEM_NAME_PREFIX + ROW_TAG:
            case MENUITEM_NAME_PREFIX + COL_TAG:
            case MENUITEM_NAME_PREFIX + HIDE_ROWS:
            case MENUITEM_NAME_PREFIX + UNHIDE_ROWS:
            case MENUITEM_NAME_PREFIX + HIDE_COLUMNS:
            case MENUITEM_NAME_PREFIX + UNHIDE_COLS:
            case MENUITEM_NAME_PREFIX + COLUMN_HEADERS:
            case MENUITEM_NAME_PREFIX + ROW_HEADERS:
              // 张天可
              if (!app.core.canEditConfig) {
                itemsDataForShown[i].disable = true
              }
              break
            case MENUITEM_NAME_PREFIX + INSERT_ROWS:
              if (!protectionOptions.allowInsertRows) {
                itemsDataForShown[i].disable = true
              }
              break
            case MENUITEM_NAME_PREFIX + INSERT_COLUMNS:
              if (!protectionOptions.allowInsertColumns) {
                itemsDataForShown[i].disable = true
              }
              break
            case MENUITEM_NAME_PREFIX + DELETE_COLUMNS:
              if (!protectionOptions.allowDeleteColumns) {
                itemsDataForShown[i].disable = true
              }
              break
            case MENUITEM_NAME_PREFIX + DELETE_ROWS:
              if (!protectionOptions.allowDeleteRows) {
                itemsDataForShown[i].disable = true
              }
              break
            case MENUITEM_NAME_PREFIX + FILTER:
              if (!protectionOptions.allowFilter) {
                itemsDataForShown[i].disable = true
              }
              break
            case MENUITEM_NAME_PREFIX + SORT_ASCEND:
            case MENUITEM_NAME_PREFIX + SORT_DESCEND:
            case MENUITEM_NAME_PREFIX + CUSTOM_SORT:
              if (!protectionOptions.allowSort
                || containLockedCellsInSelections(sheet)) {
                itemsDataForShown[i].disable = true
              }
              break
            case MENUITEM_NAME_PREFIX + TABLE_INSERT_ROWS_ABOVE:
            case MENUITEM_NAME_PREFIX + TABLE_INSERT_ROWS_BELOW:
            case MENUITEM_NAME_PREFIX + TABLE_INSERT_COLUMNS_LEFT:
            case MENUITEM_NAME_PREFIX + TABLE_INSERT_COLUMNS_RIGHT:
            case MENUITEM_NAME_PREFIX + TABLE_DELETE_ROWS:
            case MENUITEM_NAME_PREFIX + TABLE_DELETE_COLUMNS:
            case MENUITEM_NAME_PREFIX + TOTAL_ROW:
            case MENUITEM_NAME_PREFIX + TO_RANGE:
              if (!app.core.canEditConfig) {
                itemsDataForShown[i].disable = true
              }
              break
            default:
              break
          }
          if (itemsDataForShown[i] && itemsDataForShown[i].subMenu) {
            self.checkAvailableByProtectionSettings(itemsDataForShown[i].subMenu, workbook)
          }
        }
      }
    }

    DesignerContextMenu.prototype.filterTableMenuDataBySelections = function (itemsDataForShown, spread) {
      //张天可
      // 如果不可编辑则禁用右键菜单
      if (app.core.isReadOnly) {
        itemsDataForShown.splice(0, itemsDataForShown.length)
        return
      }

      var sheet = spread.getActiveSheet()
      var selections = sheet.getSelections()

      //张天可
      if (app.isInSelectMode) {
        //  选择 取消选择 结束选择
        var spliceArgs = [
          0,
          itemsDataForShown.length,
        ]
        var row = app.core.selectModeUtil.row
        var col = app.core.selectModeUtil.col
        var selectedCellPositions = app.core.selectModeUtil.getSelectedCellPositions()

        var isCoreCell = function (row_, col_) {
          return row_ == row && col_ == col
        }
        var selected = function (row_, col_) {
          return selectedCellPositions.some(function (pos) {
            return pos[0] == row_ && pos[1] == col_
          })
        }
        // 除自身以外，只要存在尚未选择的单元格就可以选择
        if (
          selections.some(function (selection) {
            var matched = false
            app.commonUtil.executeFnUseForceEnd(function (forceEnd) {
              app.commonUtil.everyCell(selection.row, selection.col, selection.rowCount, selection.colCount, function (row, col) {
                if (
                  !app.commonUtil.isHideBySpan(row, col, sheet) &&
                  !isCoreCell(row, col) &&
                  !selected(row, col)
                ) {
                  matched = true
                  forceEnd()
                }
              })
            })
            return matched
          })
        ) {
          spliceArgs.push({
            name: "selectCellInSelectMode",
            text: "选择",
            command: app.core.setCellPreposedCellControlsCommandName,
            workArea: VIEWPORT,
            options: { functionType: "select" }
          })
        }
        if (selectedCellPositions.some(function (pos) {
          return selections.some(function (selection) {
            return selection.contains(pos[0], pos[1], 1, 1)
          })
        })) {
          spliceArgs.push({
            name: "unselectCellInSelectMode",
            text: "取消选择",
            command: app.core.setCellPreposedCellControlsCommandName,
            workArea: VIEWPORT,
            options: { functionType: "unselect" }
          })
        }
        if (spliceArgs.length > 2) {
          spliceArgs.push({ "type": SEPARATOR })
        }
        spliceArgs.push({
          name: "endSelectCellInSelectMode",
          text: "结束选择",
          command: app.core.setCellPreposedCellControlsCommandName,
          workArea: VIEWPORT,
          options: { functionType: "endSelect" }
        });
        [].splice.apply(itemsDataForShown, spliceArgs)
      } else {
        if (selections && selections.length > 0) {
          var table = getTable(sheet, selections[0]), row = sheet.getActiveRowIndex(), col = sheet.getActiveColumnIndex()
          if (table) {
            $.each(itemsDataForShown, function (menuItemIndex, menuItemData) {
              if (menuItemData.name === MENUITEM_NAME_PREFIX + TABLE_INSERT) {
                var subMenuItem = menuItemData.subMenu
                if (table.checkTableLastRow(row)) { // only last row show insert below, and not header row
                  subMenuItem.splice(3, 1)
                }
                if (table.checkTableLastCol(col)) { // only last column show insert to right
                  subMenuItem.splice(1, 1)
                }
              }
            })
          }

          // 张天可
          var setExtraDynamicChange = function (menuItemsData) {
            app.commonUtil.forEach(menuItemsData, function (menuItemData, menuItemIndex, splice) {
              var thisRemoved = false
              var removeThis = function () {
                splice(menuItemIndex, 1)
                thisRemoved = true
              }
              if (menuItemData.extraDynamicChangeOnShow) {
                let beforeSubMenuExist = !!menuItemData.subMenu
                menuItemData.extraDynamicChangeOnShow(menuItemData, spread, removeThis)
                if (!thisRemoved && beforeSubMenuExist && menuItemData.subMenu) {
                  setExtraDynamicChange(menuItemData.subMenu)
                }
              }
            })
          }
          setExtraDynamicChange(itemsDataForShown)
        }
      }

    }
    DesignerContextMenu.prototype.trimNullObject = function (itemsData) {
      var self = this
      var i, itemsCount = itemsData.length
      for (i = itemsCount - 1; i > 0; i--) {
        if (!itemsData[i]) {
          itemsData.splice(i, 1)
        } else if (itemsData[i].subMenu) {
          self.trimNullObject(itemsData[i].subMenu)
        }
      }
      return itemsData
    }

    DesignerContextMenu.prototype.trimSeparatorLine = function (itemsData) {
      var itemsDataForShown = []
      var i, itemsCount = itemsData.length
      for (i = 0; i < itemsCount; i++) {
        if (itemsData[i] && itemsData[i].type === SEPARATOR && itemsData[i + 1] && itemsData[i + 1].type === SEPARATOR) {
          continue
        }
        itemsDataForShown.push(itemsData[i])
      }
      itemsCount = itemsDataForShown.length
      var start = 0, end = 0, calcStart = true, calcEnd = true
      for (i = 0; i < itemsCount; i++) {
        if (calcStart && itemsDataForShown[i].type === SEPARATOR) {
          start++
        } else {
          calcStart = false
        }
        if (calcEnd && itemsDataForShown[itemsCount - 1 - i].type === SEPARATOR) {
          end++
        } else {
          calcEnd = false
        }
        if (!calcStart && !calcEnd) {
          break
        }
      }
      itemsDataForShown.splice(itemsCount - end, end)
      itemsDataForShown.splice(0, start)
      itemsData.splice(0, itemsData.length)
      for (i = 0; i < itemsDataForShown.length; i++) {
        itemsData.push(itemsDataForShown[i])
      }
      return itemsData
    }

    var setAreaExtraDynamicChangeOnShow = function (thisItem, spread, removeThis, areaType) {
      var selections = spread.getActiveSheet().getSelections()
      const AreaTypeEnum = BillSpreadModule.AreaTypeEnum
      if (selections.length != 1) {
        removeThis()
      } else {
        let billCore = app.core.getBillModule()
        let needRemove
        // 允许重叠的区域只要不完全一致就ok
        if (AreaTypeEnum.properties[areaType].allowOverlap) {
          needRemove = billCore.getArea(selections[0].row, selections[0].col, selections[0].rowCount, selections[0].colCount, areaType) != null
        } else {
          needRemove = billCore.findAreaInfo(selections[0].row, selections[0].col, selections[0].rowCount, selections[0].colCount, null, areaType) != null
        }
        if (!needRemove && app.core.businessType !== app.BusinessTypeEnum.单据基础模板配置) {
          needRemove = [AreaTypeEnum.ALLOW_EXPAND, AreaTypeEnum.ALLOW_ATTRIBUTE_EDIT].includes(areaType)
        }
        if (needRemove) {
          removeThis()
        } else {
          thisItem.options = {
            areaType
          }
        }
      }
    }

    var deleteAreaExtraDynamicChangeOnShow = function (thisItem, spread, removeThis, areaType) {
      var selections = spread.getActiveSheet().getSelections()
      if (selections.length != 1) {
        removeThis()
      } else {
        var areaInfo = app.core.getBillModule().getArea(selections[0].row, selections[0].col, selections[0].rowCount, selections[0].colCount, areaType)
        if (areaInfo == null) {
          removeThis()
        } else {
          thisItem.text += ("[" + app.core.getBillModule().getAreaTitle(areaInfo.id) + "]")
          thisItem.options = {
            areaType,
            areaId: areaInfo.id
          }
        }
      }
    }

    spread.contextMenu = new DesignerContextMenu()
    var contextMenuRes = designer.res.contextMenu
    spread.contextMenu.menuData = [
      {
        text: contextMenuRes.cut,
        name: MENUITEM_NAME_PREFIX + CUT,
        command: MENUITEM_NAME_PREFIX + CUT,
        iconClass: ICON_CLASS_PREFIX + CUT,
        workArea: VIEWPORT + COLHEADER + ROWHEADER + CORNER
      },
      {
        name: MENUITEM_NAME_PREFIX + COPY,
        text: contextMenuRes.copy,
        command: MENUITEM_NAME_PREFIX + COPY,
        iconClass: ICON_CLASS_PREFIX + COPY,
        workArea: VIEWPORT + COLHEADER + ROWHEADER + CORNER
      },
      {
        text: contextMenuRes.paste,
        name: MENUITEM_NAME_PREFIX + PASTE,
        iconClass: ICON_CLASS_PREFIX + PASTE_OPTIONS,
        type: GROUP_HEADER,
        workArea: VIEWPORT + COLHEADER + ROWHEADER + CORNER
      },
      {
        command: MENUITEM_NAME_PREFIX + PASTE_ALL,
        name: MENUITEM_NAME_PREFIX + PASTE_ALL,
        iconClass: ICON_CLASS_PREFIX + PASTE_ALL,
        group: MENUITEM_NAME_PREFIX + PASTE,
        text: contextMenuRes.pasteAll,
        workArea: VIEWPORT + COLHEADER + ROWHEADER + CORNER
      },
      {
        command: MENUITEM_NAME_PREFIX + PASTE_FORMULA,
        name: MENUITEM_NAME_PREFIX + PASTE_FORMULA,
        iconClass: ICON_CLASS_PREFIX + PASTE_FORMULA,
        group: MENUITEM_NAME_PREFIX + PASTE,
        text: contextMenuRes.pasteFormula,
        workArea: VIEWPORT + COLHEADER + ROWHEADER + CORNER
      },
      {
        command: MENUITEM_NAME_PREFIX + PASTE_VALUES,
        name: MENUITEM_NAME_PREFIX + PASTE_VALUES,
        iconClass: ICON_CLASS_PREFIX + PASTE_VALUES,
        group: MENUITEM_NAME_PREFIX + PASTE,
        text: contextMenuRes.pasteValue,
        workArea: VIEWPORT + COLHEADER + ROWHEADER + CORNER
      },
      {
        command: MENUITEM_NAME_PREFIX + PASTE_FORMATTING,
        name: MENUITEM_NAME_PREFIX + PASTE_FORMATTING,
        iconClass: ICON_CLASS_PREFIX + PASTE_FORMATTING,
        group: MENUITEM_NAME_PREFIX + PASTE,
        text: contextMenuRes.pasteFormatting,
        workArea: VIEWPORT + COLHEADER + ROWHEADER + CORNER
      },
      {
        command: MENUITEM_NAME_PREFIX + PASTE_VALUES_FORMATTING,
        name: MENUITEM_NAME_PREFIX + PASTE_VALUES_FORMATTING,
        iconClass: ICON_CLASS_PREFIX + PASTE_VALUES_FORMATTING,
        group: MENUITEM_NAME_PREFIX + PASTE,
        text: contextMenuRes.pasteValuesFormatting,
        workArea: VIEWPORT + COLHEADER + ROWHEADER + CORNER
      },
      {
        command: MENUITEM_NAME_PREFIX + PASTE_FORMULA_FORMATTING,
        name: MENUITEM_NAME_PREFIX + PASTE_FORMULA_FORMATTING,
        iconClass: ICON_CLASS_PREFIX + PASTE_FORMULA_FORMATTING,
        group: MENUITEM_NAME_PREFIX + PASTE,
        text: contextMenuRes.pasteFormulaFormatting,
        workArea: VIEWPORT + COLHEADER + ROWHEADER + CORNER
      },
      {
        text: contextMenuRes.cut,
        name: MENUITEM_NAME_PREFIX + FLOATING_OBJECT_CUT,
        command: MENUITEM_NAME_PREFIX + FLOATING_OBJECT_CUT,
        iconClass: ICON_CLASS_PREFIX + CUT,
        workArea: CHART
      },
      {
        name: MENUITEM_NAME_PREFIX + FLOATING_OBJECT_COPY,
        text: contextMenuRes.copy,
        command: MENUITEM_NAME_PREFIX + FLOATING_OBJECT_COPY,
        iconClass: ICON_CLASS_PREFIX + COPY,
        workArea: CHART
      },
      {
        text: contextMenuRes.cut,
        name: MENUITEM_NAME_PREFIX + CUT_SHAPE,
        command: MENUITEM_NAME_PREFIX + CUT_SHAPE,
        workArea: SHAPE
      },
      {
        text: contextMenuRes.copy,
        name: MENUITEM_NAME_PREFIX + COPY_SHAPE,
        command: MENUITEM_NAME_PREFIX + COPY_SHAPE,
        workArea: SHAPE
      },
      {
        text: contextMenuRes.pasteShape,
        name: MENUITEM_NAME_PREFIX + PASTE_SHAPE,
        command: MENUITEM_NAME_PREFIX + PASTE_SHAPE,
        workArea: SHAPE
      },
      { "type": SEPARATOR },
      // 张天可
      {
        name: "setProperty",
        text: "设置业务属性",
        workArea: VIEWPORT,// + COLHEADER + ROWHEADER + CORNER
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          thisItem.subMenu = app.core.getPropertyMenuItems()
        }
      },
      {
        name: "deleteProperty",
        text: "删除业务属性",
        command: app.core.deletePropertyCommandName,
        workArea: VIEWPORT,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          var selections = spread.getActiveSheet().getSelections()
          if (selections.every(function (selection) {
            return !app.core.getBillModule().hasProperty(selection.row, selection.col, selection.rowCount, selection.colCount)
          })) {
            removeThis()
          }
        }
      },
      { "type": SEPARATOR },
      {
        name: "setDataArea",
        text: "设置" + BillSpreadModule.AreaTypeEnum.properties[BillSpreadModule.AreaTypeEnum.DATA].description,
        command: app.core.setAreaCommandName,
        workArea: VIEWPORT,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          setAreaExtraDynamicChangeOnShow(thisItem, spread, removeThis, BillSpreadModule.AreaTypeEnum.DATA)
        }
      },
      {
        name: "deleteDataArea",
        text: "删除" + BillSpreadModule.AreaTypeEnum.properties[BillSpreadModule.AreaTypeEnum.DATA].description,
        command: app.core.deleteAreaCommandName,
        workArea: VIEWPORT,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          deleteAreaExtraDynamicChangeOnShow(thisItem, spread, removeThis, BillSpreadModule.AreaTypeEnum.DATA)
        }
      },
      {
        name: "setSegregateArea",
        text: "设置" + BillSpreadModule.AreaTypeEnum.properties[BillSpreadModule.AreaTypeEnum.SEGREGATE].description,
        command: app.core.setAreaCommandName,
        workArea: VIEWPORT,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          setAreaExtraDynamicChangeOnShow(thisItem, spread, removeThis, BillSpreadModule.AreaTypeEnum.SEGREGATE)
        }
      },
      {
        name: "deleteSegregateArea",
        text: "删除" + BillSpreadModule.AreaTypeEnum.properties[BillSpreadModule.AreaTypeEnum.SEGREGATE].description,
        command: app.core.deleteAreaCommandName,
        workArea: VIEWPORT,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          deleteAreaExtraDynamicChangeOnShow(thisItem, spread, removeThis, BillSpreadModule.AreaTypeEnum.SEGREGATE)
        }
      },
      { "type": SEPARATOR },
      {
        name: "setAllowExpandArea",
        text: "设置" + BillSpreadModule.AreaTypeEnum.properties[BillSpreadModule.AreaTypeEnum.ALLOW_EXPAND].description,
        command: app.core.setAreaCommandName,
        workArea: VIEWPORT,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          setAreaExtraDynamicChangeOnShow(thisItem, spread, removeThis, BillSpreadModule.AreaTypeEnum.ALLOW_EXPAND)
        }
      },
      {
        name: "deleteAllowExpandArea",
        text: "删除" + BillSpreadModule.AreaTypeEnum.properties[BillSpreadModule.AreaTypeEnum.ALLOW_EXPAND].description,
        command: app.core.deleteAreaCommandName,
        workArea: VIEWPORT,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          deleteAreaExtraDynamicChangeOnShow(thisItem, spread, removeThis, BillSpreadModule.AreaTypeEnum.ALLOW_EXPAND)
        }
      },
      {
        name: "setAllowAttributeEditArea",
        text: "设置" + BillSpreadModule.AreaTypeEnum.properties[BillSpreadModule.AreaTypeEnum.ALLOW_ATTRIBUTE_EDIT].description,
        command: app.core.setAreaCommandName,
        workArea: VIEWPORT,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          setAreaExtraDynamicChangeOnShow(thisItem, spread, removeThis, BillSpreadModule.AreaTypeEnum.ALLOW_ATTRIBUTE_EDIT)
        }
      },
      {
        name: "deleteAllowAttributeEditArea",
        text: "删除" + BillSpreadModule.AreaTypeEnum.properties[BillSpreadModule.AreaTypeEnum.ALLOW_ATTRIBUTE_EDIT].description,
        command: app.core.deleteAreaCommandName,
        workArea: VIEWPORT,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          deleteAreaExtraDynamicChangeOnShow(thisItem, spread, removeThis, BillSpreadModule.AreaTypeEnum.ALLOW_ATTRIBUTE_EDIT)
        }
      },
      { "type": SEPARATOR },
      {
        name: "setCellOrderControls",
        text: "设置回填顺序",
        workArea: VIEWPORT,// + COLHEADER + ROWHEADER + CORNER
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          var sheet = spread.getActiveSheet()
          var selections = sheet.getSelections()
          if (!app.commonUtil.checkSingleCellSelection(selections, sheet)) {
            removeThis()
          }
        },
        subMenu: [
          {
            name: "setCellPreposedCellControls",
            text: "选择前置单元格",
            command: app.core.setCellPreposedCellControlsCommandName,
            workArea: VIEWPORT,
            extraDynamicChangeOnShow(thisItem, spread, removeThis) {
              var selections = spread.getActiveSheet().getSelections()
              var row = selections[0].row
              var col = selections[0].col
              thisItem.options = {
                row: row,
                col: col,
              }
            }
          },
          {
            name: "setCellPreposedAreaControls",
            text: "选择前置区域",
            workArea: VIEWPORT,
            extraDynamicChangeOnShow(thisItem, spread, removeThis) {
              var selections = spread.getActiveSheet().getSelections()
              var row = selections[0].row
              var col = selections[0].col
              var core = app.core.getBillModule()
              var curCellArea = core.findAreaInfo(row, col, 1, 1, false, BillSpreadModule.AreaTypeEnum.DATA)
              // 排除当前单元格所在区域
              var areaList = core.getAllAreaOfAreaType(BillSpreadModule.AreaTypeEnum.DATA).filter(function (area) {
                return !curCellArea || curCellArea.id != area.id
              })

              if (areaList.length > 0) {// 如果存在区域则设置选择区域的子菜单
                thisItem.subMenu = areaList.map(function (area) {
                  var areaTitle = core.getAreaTitle(area.id)
                  return {
                    name: thisItem.name + "." + area.id,
                    text: BillSpreadModule.AreaTypeEnum.properties[BillSpreadModule.AreaTypeEnum.DATA].description + "[" + areaTitle + "]",
                    command: app.core.setCellPreposedAreaControlsCommandName,
                    options: {
                      areaId: area.id,
                      row: row,
                      col: col,
                    }
                  }
                })
              } else {// 如果没有区域则删除设置前置区域子菜单
                removeThis()
              }

            }
          },
          {
            name: "setCellEntirelyFillControl",
            text: "完全回填后执行",
            command: app.core.setCellEntirelyFillControlCommandName,
            workArea: VIEWPORT,
            extraDynamicChangeOnShow(thisItem, spread, removeThis) {
              var selections = spread.getActiveSheet().getSelections()
              var row = selections[0].row
              var col = selections[0].col
              // 如果不能设置完全回填后执行，则删除此子菜单
              if (!app.core.getBillModule().canSetCellEntirelyFillControl(row, col)) {
                removeThis()
              } else {
                thisItem.options = {
                  row: row,
                  col: col,
                }
              }
            }
          },
        ]
      },
      {
        name: "deleteCellOrderControls",
        text: "删除回填顺序设置",
        workArea: VIEWPORT,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          var sheet = spread.getActiveSheet()
          var selections = sheet.getSelections()
          if (!app.commonUtil.checkSingleCellSelection(selections, sheet)) {
            removeThis()
          } else {
            var row = selections[0].row
            var col = selections[0].col
            var core = app.core.getBillModule()
            var controls = core.getAllCellOrderControls(row, col)
            if (!controls || controls.length == 0) {
              removeThis()
            }
          }
        },
        subMenu: [
          {
            name: "deleteAllCellOrderControls",
            text: "删除全部",
            command: app.core.deleteCellOrderControlsCommandName,
            workArea: VIEWPORT,
            extraDynamicChangeOnShow(thisItem, spread, removeThis) {
              var selections = spread.getActiveSheet().getSelections()
              var row = selections[0].row
              var col = selections[0].col
              thisItem.options = {
                functionName: thisItem.name,
                row: row,
                col: col,
              }
            }
          },
          {
            name: "deleteCellPreposedCellControls",
            text: "删除前置单元格",
            command: app.core.deleteCellOrderControlsCommandName,
            workArea: VIEWPORT,
            extraDynamicChangeOnShow(thisItem, spread, removeThis) {
              var selections = spread.getActiveSheet().getSelections()
              var row = selections[0].row
              var col = selections[0].col
              var core = app.core.getBillModule()
              if (!core.getCellPreposedCellControls(row, col)) {
                removeThis()
              } else {
                thisItem.options = {
                  functionName: thisItem.name,
                  row: row,
                  col: col,
                }
              }
            }
          },
          {
            name: "deleteCellPreposedAreaControls",
            text: "删除前置区域",
            command: app.core.deleteCellOrderControlsCommandName,
            workArea: VIEWPORT,
            extraDynamicChangeOnShow(thisItem, spread, removeThis) {
              var selections = spread.getActiveSheet().getSelections()
              var row = selections[0].row
              var col = selections[0].col
              var core = app.core.getBillModule()
              if (!core.getCellPreposedAreaControls(row, col)) {
                removeThis()
              } else {
                thisItem.options = {
                  functionName: thisItem.name,
                  row: row,
                  col: col,
                }
              }
            }
          },
          {
            name: "deleteCellEntirelyFillControl",
            text: "删除完全回填后执行",
            command: app.core.deleteCellOrderControlsCommandName,
            workArea: VIEWPORT,
            extraDynamicChangeOnShow(thisItem, spread, removeThis) {
              var selections = spread.getActiveSheet().getSelections()
              var row = selections[0].row
              var col = selections[0].col
              var core = app.core.getBillModule()
              if (!core.getCellEntirelyFillControl(row, col)) {
                removeThis()
              } else {
                thisItem.options = {
                  functionName: thisItem.name,
                  row: row,
                  col: col,
                }
              }
            }
          },
        ]
      },
      { "type": SEPARATOR },
      {
        name: "setSettingDefaultValueControls",
        text: "设置关联回填默认值",
        workArea: VIEWPORT,// + COLHEADER + ROWHEADER + CORNER
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          var sheet = spread.getActiveSheet()
          var selections = sheet.getSelections()
          if (!app.commonUtil.checkSingleCellSelection(selections, sheet)) {
            removeThis()
          }
        },
        subMenu: [
          {
            name: "setSettingDefaultValueCellControls",
            text: "选择关联单元格",
            command: app.core.setSettingDefaultValueCellControlsCommandName,
            workArea: VIEWPORT,
            extraDynamicChangeOnShow(thisItem, spread, removeThis) {
              var selections = spread.getActiveSheet().getSelections()
              var row = selections[0].row
              var col = selections[0].col
              thisItem.options = {
                row: row,
                col: col,
              }
            }
          },
          {
            name: "setSettingDefaultValueAreaControls",
            text: "选择关联区域",
            workArea: VIEWPORT,
            extraDynamicChangeOnShow(thisItem, spread, removeThis) {
              var selections = spread.getActiveSheet().getSelections()
              var row = selections[0].row
              var col = selections[0].col
              var core = app.core.getBillModule()
              // 找到当前单元格所在区域
              var curCellArea = core.findAreaInfo(row, col, 1, 1, false, BillSpreadModule.AreaTypeEnum.DATA)
              // 排除当前单元格所在区域之后剩下的区域
              var areaList = core.getAllAreaOfAreaType(BillSpreadModule.AreaTypeEnum.DATA).filter(function (area) {
                return !curCellArea || curCellArea.id != area.id
              })
              if (areaList.length > 0) {// 如果存在区域则设置选择区域的子菜单
                thisItem.subMenu = areaList.map(function (area) {
                  var areaTitle = core.getAreaTitle(area.id)
                  return {
                    name: thisItem.name + "." + area.id,
                    text: BillSpreadModule.AreaTypeEnum.properties[BillSpreadModule.AreaTypeEnum.DATA].description + "[" + areaTitle + "]",
                    command: app.core.setSettingDefaultValueAreaControlsCommandName,
                    options: {
                      areaId: area.id,
                      row: row,
                      col: col,
                    }
                  }
                })
              } else {// 如果没有区域则删除设置关联区域子菜单
                removeThis()
              }
            }
          },
        ]
      },
      {
        name: "deleteSettingDefaultValueControls",
        text: "删除关联回填默认值设置",
        workArea: VIEWPORT,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          var selections = spread.getActiveSheet().getSelections()
          var row = selections[0].row
          var col = selections[0].col
          var core = app.core.getBillModule()
          var controls = core.getAllSettingDefaultValueControls(row, col)
          if (!controls || controls.length == 0) {
            removeThis()
          }
        },
        subMenu: [
          {
            name: "deleteAllSettingDefaultValueControls",
            text: "删除全部关联",
            command: app.core.deleteSettingDefaultValueControlsCommandName,
            workArea: VIEWPORT,
            extraDynamicChangeOnShow(thisItem, spread, removeThis) {
              var selections = spread.getActiveSheet().getSelections()
              var row = selections[0].row
              var col = selections[0].col
              thisItem.options = {
                functionName: thisItem.name,
                row: row,
                col: col,
              }
            }
          },
          {
            name: "deleteSettingDefaultValueCellControls",
            text: "删除单元格关联",
            command: app.core.deleteSettingDefaultValueControlsCommandName,
            workArea: VIEWPORT,
            extraDynamicChangeOnShow(thisItem, spread, removeThis) {
              var selections = spread.getActiveSheet().getSelections()
              var row = selections[0].row
              var col = selections[0].col
              var core = app.core.getBillModule()
              if (!core.getSettingDefaultValueCellControls(row, col)) {
                removeThis()
              } else {
                thisItem.options = {
                  functionName: thisItem.name,
                  row: row,
                  col: col,
                }
              }
            }
          },
          {
            name: "deleteSettingDefaultValueAreaControls",
            text: "删除区域关联",
            command: app.core.deleteSettingDefaultValueControlsCommandName,
            workArea: VIEWPORT,
            extraDynamicChangeOnShow(thisItem, spread, removeThis) {
              var selections = spread.getActiveSheet().getSelections()
              var row = selections[0].row
              var col = selections[0].col
              var core = app.core.getBillModule()
              if (!core.getSettingDefaultValueAreaControls(row, col)) {
                removeThis()
              } else {
                thisItem.options = {
                  functionName: thisItem.name,
                  row: row,
                  col: col,
                }
              }
            }
          },
        ]
      },
      { "type": SEPARATOR },
      {
        text: contextMenuRes.resetChartColor,
        name: MENUITEM_NAME_PREFIX + RESET_CHART_COLOR,
        iconClass: ICON_CLASS_PREFIX + RESET_CHART_COLOR,
        command: MENUITEM_NAME_PREFIX + RESET_CHART_COLOR,
        workArea: CHART
      },
      {
        text: contextMenuRes.changeChartType,
        name: MENUITEM_NAME_PREFIX + CHANGE_CHART_TYPE_DIALOG,
        iconClass: ICON_CLASS_PREFIX + CHANGE_CHART_TYPE_DIALOG,
        command: MENUITEM_NAME_PREFIX + CHANGE_CHART_TYPE_DIALOG,
        workArea: CHART
      },
      {
        text: contextMenuRes.selectData,
        name: MENUITEM_NAME_PREFIX + SELECT_CHART_DATA_DIALOG,
        iconClass: ICON_CLASS_PREFIX + SELECT_CHART_DATA_DIALOG,
        command: MENUITEM_NAME_PREFIX + SELECT_CHART_DATA_DIALOG,
        workArea: CHART
      },
      {
        text: contextMenuRes.moveChart,
        name: MENUITEM_NAME_PREFIX + MOVE_CHART_DIALOG,
        iconClass: ICON_CLASS_PREFIX + MOVE_CHART_DIALOG,
        command: MENUITEM_NAME_PREFIX + MOVE_CHART_DIALOG,
        workArea: CHART
      },
      {
        text: contextMenuRes.formatChart.chartArea,
        name: MENUITEM_NAME_PREFIX + FORMAT_CHART,
        iconClass: ICON_CLASS_PREFIX + FORMAT_CHART,
        command: MENUITEM_NAME_PREFIX + FORMAT_CHART,
        workArea: CHART
      },
      { "type": SEPARATOR },
      // 张天可
      // {
      //   text: contextMenuRes.insertDialog,
      //   name: MENUITEM_NAME_PREFIX + INSERT_DIALOG,
      //   command: MENUITEM_NAME_PREFIX + INSERT_DIALOG,
      //   workArea: VIEWPORT_WITHOUT_TABLE + CORNER
      // },
      // {
      //   text: contextMenuRes.deleteDialog,
      //   name: MENUITEM_NAME_PREFIX + DELETE_DIALOG,
      //   command: MENUITEM_NAME_PREFIX + DELETE_DIALOG,
      //   workArea: VIEWPORT_WITHOUT_TABLE + CORNER
      // },
      // {
      //   text: contextMenuRes.insert,
      //   name: MENUITEM_NAME_PREFIX + INSERT_COLUMNS,
      //   command: SPREAD_COMMAND_PREFIX + INSERT_COLUMNS,
      //   workArea: COLHEADER
      // },
      // 张天可
      {
        text: contextMenuRes.insert,
        name: MENUITEM_NAME_PREFIX + CUSTOM_INSERT_COLUMNS,
        subMenu: [
          {
            text: "上一列",
            name: MENUITEM_NAME_PREFIX + INSERT_LAST_COLUMNS,
            command: ZSJCJYW_COMMAND_PREFIX + INSERT_LAST_COLUMNS
          },
          {
            text: "下一列",
            name: MENUITEM_NAME_PREFIX + INSERT_NEXT_COLUMNS,
            command: ZSJCJYW_COMMAND_PREFIX + INSERT_NEXT_COLUMNS
          }
        ],
        workArea: COLHEADER + VIEWPORT_WITHOUT_TABLE,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          var selections = spread.getActiveSheet().getSelections()
          if (selections.length != 1 || selections[0].colCount != 1) {
            removeThis()
          } else if (selections[0].row >= 0) {
            thisItem.text += "列"
          }
        }
      },
      {
        text: contextMenuRes.delete,
        name: MENUITEM_NAME_PREFIX + DELETE_COLUMNS,
        command: ZSJCJYW_COMMAND_PREFIX + DELETE_COLUMNS,
        workArea: COLHEADER + VIEWPORT_WITHOUT_TABLE,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          var selections = spread.getActiveSheet().getSelections()
          if (selections.length != 1) {
            removeThis()
          } else if (selections[0].row >= 0) {
            thisItem.text += "列"
          }
        }
      },
      // 张天可
      {
        text: contextMenuRes.insert,
        name: MENUITEM_NAME_PREFIX + CUSTOM_INSERT_ROWS,
        subMenu: [
          {
            text: "上一行",
            name: MENUITEM_NAME_PREFIX + INSERT_LAST_ROWS,
            command: ZSJCJYW_COMMAND_PREFIX + INSERT_LAST_ROWS
          },
          {
            text: "下一行",
            name: MENUITEM_NAME_PREFIX + INSERT_NEXT_ROWS,
            command: ZSJCJYW_COMMAND_PREFIX + INSERT_NEXT_ROWS
          }
        ],
        workArea: ROWHEADER + VIEWPORT_WITHOUT_TABLE,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          var selections = spread.getActiveSheet().getSelections()
          if (selections.length != 1 || selections[0].rowCount != 1) {
            removeThis()
          } else if (selections[0].col >= 0) {
            thisItem.text += "行"
          }
        }
      },
      {
        text: contextMenuRes.delete,
        name: MENUITEM_NAME_PREFIX + DELETE_ROWS,
        command: ZSJCJYW_COMMAND_PREFIX + DELETE_ROWS,
        workArea: ROWHEADER + VIEWPORT_WITHOUT_TABLE,
        extraDynamicChangeOnShow(thisItem, spread, removeThis) {
          var selections = spread.getActiveSheet().getSelections()
          if (selections.length != 1) {
            removeThis()
          } else if (selections[0].col >= 0) {
            thisItem.text += "行"
          }
        }
      },
      {
        text: contextMenuRes.tableInsert,
        name: MENUITEM_NAME_PREFIX + TABLE_INSERT,
        subMenu: [
          {
            text: contextMenuRes.tableInsertColumnsLeft,
            name: MENUITEM_NAME_PREFIX + TABLE_INSERT_COLUMNS_LEFT,
            command: TABLE_INSERT_COLUMNS_LEFT_FOR_CONTEXT_MENU,
            iconClass: ICON_CLASS_PREFIX + TABLE_INSERT_COLUMNS_LEFT
          },
          {
            text: contextMenuRes.tableInsertColumnsRight,
            name: MENUITEM_NAME_PREFIX + TABLE_INSERT_COLUMNS_RIGHT,
            command: TABLE_INSERT_COLUMNS_RIGHT_FOR_CONTEXT_MENU,
            iconClass: ICON_CLASS_PREFIX + TABLE_INSERT_COLUMNS_RIGHT
          },
          {
            text: contextMenuRes.tableInsertRowsAbove,
            name: MENUITEM_NAME_PREFIX + TABLE_INSERT_ROWS_ABOVE,
            command: TABLE_INSERT_ROWS_ABOVE_FOR_CONTEXT_MENU,
            iconClass: ICON_CLASS_PREFIX + TABLE_INSERT_ROWS_ABOVE
          },
          {
            text: contextMenuRes.tableInsertRowsBelow,
            name: MENUITEM_NAME_PREFIX + TABLE_INSERT_ROWS_BELOW,
            command: TABLE_INSERT_ROWS_BELOW_FOR_CONTEXT_MENU,
            iconClass: ICON_CLASS_PREFIX + TABLE_INSERT_ROWS_BELOW
          }
        ],
        workArea: TABLE
      },
      {
        text: contextMenuRes.tableDelete,
        name: MENUITEM_NAME_PREFIX + TABLE_DELETE,
        subMenu: [
          {
            text: contextMenuRes.tableDeleteColumns,
            name: MENUITEM_NAME_PREFIX + TABLE_DELETE_COLUMNS,
            command: TABLE_DELETE_COLUMNS_FOR_CONTEXT_MENU,
            iconClass: ICON_CLASS_PREFIX + TABLE_DELETE_COLUMNS
          },
          {
            text: contextMenuRes.tableDeleteRows,
            name: MENUITEM_NAME_PREFIX + TABLE_DELETE_ROWS,
            command: TABLE_DELETE_ROWS_FOR_CONTEXT_MENU,
            iconClass: ICON_CLASS_PREFIX + TABLE_DELETE_ROWS
          }
        ],
        workArea: TABLE
      },
      {
        text: contextMenuRes.clearcontents,
        command: SPREAD_COMMAND_PREFIX + CLEAR_CONTENT,
        name: MENUITEM_NAME_PREFIX + CLEAR_CONTENT,
        workArea: VIEWPORT + COLHEADER + ROWHEADER + CORNER
      },
      { "type": SEPARATOR },
      {
        text: contextMenuRes.headerInsertCopiedCells,
        name: MENUITEM_NAME_PREFIX + ROWHEADER + INSERT_COPY_CELLS,
        command: SPREAD_COMMAND_PREFIX + ROWHEADER + INSERT_COPY_CELLS,
        workArea: ROWHEADER
      },
      {
        text: contextMenuRes.headerInsertCutCells,
        name: MENUITEM_NAME_PREFIX + ROWHEADER + INSERT_CUT_CELLS,
        command: SPREAD_COMMAND_PREFIX + ROWHEADER + INSERT_CUT_CELLS,
        workArea: ROWHEADER
      },
      {
        text: contextMenuRes.headerInsertCopiedCells,
        name: MENUITEM_NAME_PREFIX + COLHEADER + INSERT_COPY_CELLS,
        command: SPREAD_COMMAND_PREFIX + COLHEADER + INSERT_COPY_CELLS,
        workArea: COLHEADER
      },
      {
        text: contextMenuRes.headerInsertCutCells,
        name: MENUITEM_NAME_PREFIX + COLHEADER + INSERT_CUT_CELLS,
        command: SPREAD_COMMAND_PREFIX + COLHEADER + INSERT_CUT_CELLS,
        workArea: COLHEADER
      },
      {
        text: contextMenuRes.insertCopiedCells,
        name: MENUITEM_NAME_PREFIX + INSERT_COPY_CELLS,
        subMenu: [
          {
            text: contextMenuRes.shiftCellsRight,
            name: MENUITEM_NAME_PREFIX + INSERT_COPY_CELLS + SHIFT_CELLS_RIGHT,
            command: SPREAD_COMMAND_PREFIX + INSERT_COPY_CELLS + SHIFT_CELLS_RIGHT,
            iconClass: ICON_CLASS_PREFIX + SHIFT_CELLS_RIGHT
          },
          {
            text: contextMenuRes.shiftCellsDown,
            name: MENUITEM_NAME_PREFIX + INSERT_COPY_CELLS + SHIFT_CELLS_DOWN,
            command: SPREAD_COMMAND_PREFIX + INSERT_COPY_CELLS + SHIFT_CELLS_DOWN,
            iconClass: ICON_CLASS_PREFIX + SHIFT_CELLS_DOWN
          }
        ],
        workArea: VIEWPORT
      },
      {
        text: contextMenuRes.insertCutCells,
        name: MENUITEM_NAME_PREFIX + INSERT_CUT_CELLS,
        subMenu: [
          {
            text: contextMenuRes.shiftCellsRight,
            name: MENUITEM_NAME_PREFIX + INSERT_CUT_CELLS + SHIFT_CELLS_RIGHT,
            command: SPREAD_COMMAND_PREFIX + INSERT_CUT_CELLS + SHIFT_CELLS_RIGHT,
            iconClass: ICON_CLASS_PREFIX + SHIFT_CELLS_RIGHT
          },
          {
            text: contextMenuRes.shiftCellsDown,
            name: MENUITEM_NAME_PREFIX + INSERT_CUT_CELLS + SHIFT_CELLS_DOWN,
            command: SPREAD_COMMAND_PREFIX + INSERT_CUT_CELLS + SHIFT_CELLS_DOWN,
            iconClass: ICON_CLASS_PREFIX + SHIFT_CELLS_DOWN
          }
        ],
        workArea: VIEWPORT
      },
      {
        text: contextMenuRes.insert,
        name: MENUITEM_NAME_PREFIX + INSERT_SHEET,
        command: SPREAD_COMMAND_PREFIX + INSERT_SHEET,
        workArea: SHEETTAB
      },
      {
        text: contextMenuRes.delete,
        name: MENUITEM_NAME_PREFIX + DELETE_SHEET,
        command: SPREAD_COMMAND_PREFIX + DELETE_SHEET,
        workArea: SHEETTAB
      },
      {
        text: contextMenuRes.protectsheet,
        name: MENUITEM_NAME_PREFIX + PROTECT_SHEET,
        command: MENUITEM_NAME_PREFIX + PROTECT_SHEET,
        iconClass: ICON_CLASS_PREFIX + PROTECT_SHEET,
        workArea: SHEETTAB
      },
      // 张天可
      // { "type": SEPARATOR },
      // {
      //   text: contextMenuRes.filter,
      //   name: MENUITEM_NAME_PREFIX + FILTER,
      //   command: SPREAD_COMMAND_PREFIX + FILTER,
      //   workArea: VIEWPORT + CORNER
      // },
      // {
      //   text: contextMenuRes.sort,
      //   name: MENUITEM_NAME_PREFIX + SORT,
      //   subMenu: [
      //     {
      //       text: contextMenuRes.sortAToZ,
      //       name: MENUITEM_NAME_PREFIX + SORT_ASCEND,
      //       command: SPREAD_COMMAND_PREFIX + SORT_ASCEND,
      //       iconClass: ICON_CLASS_PREFIX + SORT_ASCEND
      //     },
      //     {
      //       text: contextMenuRes.sortZToA,
      //       name: MENUITEM_NAME_PREFIX + SORT_DESCEND,
      //       command: SPREAD_COMMAND_PREFIX + SORT_DESCEND,
      //       iconClass: ICON_CLASS_PREFIX + SORT_DESCEND
      //     },
      //     {
      //       text: contextMenuRes.customSort,
      //       name: MENUITEM_NAME_PREFIX + CUSTOM_SORT,
      //       command: MENUITEM_NAME_PREFIX + CUSTOM_SORT,
      //       iconClass: ICON_CLASS_PREFIX + CUSTOM_SORT
      //     }
      //   ],
      //   workArea: VIEWPORT + CORNER
      // },
      // {
      //   text: contextMenuRes.table,
      //   name: MENUITEM_NAME_PREFIX + TABLE,
      //   subMenu: [
      //     {
      //       text: contextMenuRes.totalRow,
      //       name: MENUITEM_NAME_PREFIX + TOTAL_ROW,
      //       command: MENUITEM_NAME_PREFIX + TOTAL_ROW,
      //       iconClass: ICON_CLASS_PREFIX + TOTAL_ROW
      //     },
      //     {
      //       text: contextMenuRes.toRange,
      //       name: MENUITEM_NAME_PREFIX + TO_RANGE,
      //       command: MENUITEM_NAME_PREFIX + TO_RANGE,
      //       iconClass: ICON_CLASS_PREFIX + TO_RANGE
      //     }
      //   ],
      //   workArea: VIEWPORT + CORNER
      // },
      // { "type": SEPARATOR },
      // {
      //   text: contextMenuRes.insertComment,
      //   name: SPREAD_NAME_PREFIX + INSERT_COMMENT,
      //   command: SPREAD_COMMAND_PREFIX + INSERT_COMMENT,
      //   iconClass: ICON_CLASS_PREFIX + INSERT_COMMENT,
      //   workArea: VIEWPORT + CORNER
      // },
      // {
      //   text: contextMenuRes.editComment,
      //   name: SPREAD_NAME_PREFIX + EDIT_COMMENT,
      //   command: SPREAD_COMMAND_PREFIX + EDIT_COMMENT,
      //   iconClass: ICON_CLASS_PREFIX + EDIT_COMMENT,
      //   workArea: VIEWPORT + CORNER
      // },
      // {
      //   text: contextMenuRes.deleteComment,
      //   name: SPREAD_NAME_PREFIX + DELETE_COMMENT,
      //   command: SPREAD_COMMAND_PREFIX + DELETE_COMMENT,
      //   iconClass: ICON_CLASS_PREFIX + DELETE_COMMENT,
      //   workArea: VIEWPORT + CORNER
      // },
      // {
      //   text: contextMenuRes.unHideComment,
      //   name: SPREAD_NAME_PREFIX + TOGGLE_COMMENT,
      //   command: SPREAD_COMMAND_PREFIX + TOGGLE_COMMENT,
      //   workArea: VIEWPORT + CORNER
      // },
      // {
      //   text: contextMenuRes.formatComment,
      //   name: MENUITEM_NAME_PREFIX + FORMAT_COMMENT,
      //   command: MENUITEM_NAME_PREFIX + FORMAT_COMMENT,
      //   iconClass: ICON_CLASS_PREFIX + FORMAT_COMMENT,
      //   workArea: VIEWPORT + CORNER
      // },
      // { "type": SEPARATOR },
      {
        text: contextMenuRes.formatCells,
        name: MENUITEM_NAME_PREFIX + FORMAT_CELLS,
        command: MENUITEM_NAME_PREFIX + FORMAT_CELLS,
        iconClass: ICON_CLASS_PREFIX + FORMAT_CELLS,
        workArea: VIEWPORT + COLHEADER + ROWHEADER + CORNER
      },
      {
        text: contextMenuRes.editCellType,
        name: MENUITEM_NAME_PREFIX + EDIT_CELLTYPE,
        command: MENUITEM_NAME_PREFIX + EDIT_CELLTYPE,
        iconClass: ICON_CLASS_PREFIX + EDIT_CELLTYPE,
        workArea: VIEWPORT
      },
      {
        text: contextMenuRes.editCellDropdows,
        name: MENUITEM_NAME_PREFIX + EDIT_CELL_DROPDOWNS,
        command: MENUITEM_NAME_PREFIX + EDIT_CELL_DROPDOWNS,
        iconClass: ICON_CLASS_PREFIX + EDIT_CELL_DROPDOWNS,
        workArea: VIEWPORT
      },
      // 张天可
      // {
      //   text: contextMenuRes.link,
      //   name: MENUITEM_NAME_PREFIX + LINK,
      //   command: MENUITEM_NAME_PREFIX + LINK,
      //   iconClass: ICON_CLASS_PREFIX + LINK,
      //   workArea: VIEWPORT + CORNER
      // },
      // {
      //   text: contextMenuRes.editHyperlink,
      //   name: MENUITEM_NAME_PREFIX + EDIT_HYPERLINK,
      //   command: MENUITEM_NAME_PREFIX + EDIT_HYPERLINK,
      //   iconClass: ICON_CLASS_PREFIX + LINK,
      //   workArea: VIEWPORT
      // },
      // {
      //   text: contextMenuRes.openHyperlink,
      //   name: MENUITEM_NAME_PREFIX + OPEN_HYPERLINK,
      //   command: MENUITEM_NAME_PREFIX + OPEN_HYPERLINK,
      //   workArea: VIEWPORT
      // },
      // {
      //   text: contextMenuRes.removeHyperlink,
      //   name: MENUITEM_NAME_PREFIX + REMOVE_HYPERLINK,
      //   command: MENUITEM_NAME_PREFIX + REMOVE_HYPERLINK,
      //   iconClass: ICON_CLASS_PREFIX + REMOVE_HYPERLINK,
      //   workArea: VIEWPORT
      // },
      // {
      //   text: contextMenuRes.removeHyperlinks,
      //   name: MENUITEM_NAME_PREFIX + REMOVE_HYPERLINKS,
      //   command: MENUITEM_NAME_PREFIX + REMOVE_HYPERLINKS,
      //   iconClass: ICON_CLASS_PREFIX + REMOVE_HYPERLINK,
      //   workArea: COLHEADER + ROWHEADER + CORNER + VIEWPORT
      // },
      { "type": SEPARATOR },
      {
        text: contextMenuRes.richText,
        name: MENUITEM_NAME_PREFIX + RICH_TEXT,
        command: MENUITEM_NAME_PREFIX + RICH_TEXT,
        iconClass: ICON_CLASS_PREFIX + RICH_TEXT,
        workArea: VIEWPORT
      },
      // 张天可
      // {
      //   text: contextMenuRes.defineName,
      //   name: MENUITEM_NAME_PREFIX + DEFINE_NAME,
      //   command: MENUITEM_NAME_PREFIX + DEFINE_NAME,
      //   workArea: VIEWPORT + CORNER
      // },
      // {
      //   text: contextMenuRes.tag,
      //   name: MENUITEM_NAME_PREFIX + CELL_TAG,
      //   command: MENUITEM_NAME_PREFIX + CELL_TAG,
      //   workArea: VIEWPORT + CORNER
      // },
      // {
      //   text: contextMenuRes.tag,
      //   name: MENUITEM_NAME_PREFIX + ROW_TAG,
      //   command: MENUITEM_NAME_PREFIX + ROW_TAG,
      //   workArea: ROWHEADER
      // },
      // {
      //   text: contextMenuRes.tag,
      //   name: MENUITEM_NAME_PREFIX + COL_TAG,
      //   command: MENUITEM_NAME_PREFIX + COL_TAG,
      //   workArea: COLHEADER
      // },
      {
        text: contextMenuRes.rowHeight,
        name: MENUITEM_NAME_PREFIX + ROW_HEIGHT,
        command: MENUITEM_NAME_PREFIX + ROW_HEIGHT,
        workArea: ROWHEADER
      },
      {
        text: contextMenuRes.columnWidth,
        name: MENUITEM_NAME_PREFIX + COL_WIDTH,
        command: MENUITEM_NAME_PREFIX + COL_WIDTH,
        workArea: COLHEADER
      },
      {
        text: contextMenuRes.hide,
        name: MENUITEM_NAME_PREFIX + HIDE_ROWS,
        command: SPREAD_COMMAND_PREFIX + HIDE_ROWS,
        workArea: ROWHEADER
      },
      {
        text: contextMenuRes.hide,
        name: MENUITEM_NAME_PREFIX + HIDE_COLUMNS,
        command: SPREAD_COMMAND_PREFIX + HIDE_COLUMNS,
        workArea: COLHEADER
      },
      {
        text: contextMenuRes.unhide,
        name: MENUITEM_NAME_PREFIX + UNHIDE_COLS,
        command: SPREAD_COMMAND_PREFIX + UNHIDE_COLS,
        workArea: COLHEADER
      },
      {
        text: contextMenuRes.unhide,
        name: MENUITEM_NAME_PREFIX + UNHIDE_ROWS,
        command: SPREAD_COMMAND_PREFIX + UNHIDE_ROWS,
        workArea: ROWHEADER
      },
      // 张天可
      // {
      //   text: contextMenuRes.headers,
      //   name: MENUITEM_NAME_PREFIX + COLUMN_HEADERS,
      //   command: MENUITEM_NAME_PREFIX + COLUMN_HEADERS,
      //   workArea: COLHEADER
      // },
      // {
      //   text: contextMenuRes.headers,
      //   name: MENUITEM_NAME_PREFIX + ROW_HEADERS,
      //   command: MENUITEM_NAME_PREFIX + ROW_HEADERS,
      //   workArea: ROWHEADER
      // },
      // {
      //   text: contextMenuRes.outlineColumn,
      //   name: MENUITEM_NAME_PREFIX + OUTLINE_COLUMN,
      //   command: MENUITEM_NAME_PREFIX + OUTLINE_COLUMN,
      //   workArea: COLHEADER
      // },
      { "type": SEPARATOR },
      {
        text: contextMenuRes.sheetTabColor,
        name: MENUITEM_NAME_PREFIX + SHOW_TAB_COLOR,
        subMenu: [{
          name: MENUITEM_NAME_PREFIX + TAB_COLOR,
          command: MENUITEM_NAME_PREFIX + TAB_COLOR
        }],
        workArea: SHEETTAB
      },
      {
        text: contextMenuRes.hide,
        name: MENUITEM_NAME_PREFIX + HIDE_SHEET,
        command: SPREAD_COMMAND_PREFIX + HIDE_SHEET,
        workArea: SHEETTAB
      },
      {
        text: contextMenuRes.unhide,
        name: MENUITEM_NAME_PREFIX + UNHIDE_SHEET,
        command: SPREAD_COMMAND_PREFIX + UNHIDE_SHEET,
        workArea: SHEETTAB
      },
      // 张天可
      // {
      //   text: contextMenuRes.tag,
      //   name: MENUITEM_NAME_PREFIX + SHEET_TAG,
      //   command: MENUITEM_NAME_PREFIX + SHEET_TAG,
      //   workArea: SHEETTAB
      // },
      { "type": SEPARATOR },
      {
        text: contextMenuRes.cut,
        name: MENUITEM_NAME_PREFIX + SLICER_CUT,
        iconClass: ICON_CLASS_PREFIX + CUT,
        command: SPREAD_COMMAND_PREFIX + CUT,
        workArea: SLICER
      },
      {
        text: contextMenuRes.copy,
        name: MENUITEM_NAME_PREFIX + SLICER_COPY,
        iconClass: ICON_CLASS_PREFIX + COPY,
        command: SPREAD_COMMAND_PREFIX + COPY,
        workArea: SLICER
      },
      {
        text: contextMenuRes.paste,
        name: MENUITEM_NAME_PREFIX + SLICER_PASTE_OPTIONS,
        iconClass: ICON_CLASS_PREFIX + PASTE_OPTIONS,
        type: GROUP_HEADER,
        workArea: SLICER
      },
      {
        command: SPREAD_COMMAND_PREFIX + PASTE_ALL,
        name: MENUITEM_NAME_PREFIX + SLICER_PASTE,
        iconClass: ICON_CLASS_PREFIX + PASTE_ALL,
        group: MENUITEM_NAME_PREFIX + SLICER_PASTE_OPTIONS,
        text: contextMenuRes.pasteAll,
        workArea: SLICER
      },
      {
        text: contextMenuRes.sortAToZ,
        name: MENUITEM_NAME_PREFIX + SLICER_SORT_ASCEND,
        iconClass: ICON_CLASS_PREFIX + SORT_ASCEND,
        command: SPREAD_COMMAND_PREFIX + SLICER_SORT_ASCEND,
        workArea: SLICER
      },
      {
        text: contextMenuRes.sortZToA,
        name: MENUITEM_NAME_PREFIX + SLICER_SORT_DESCEND,
        iconClass: ICON_CLASS_PREFIX + SORT_DESCEND,
        command: SPREAD_COMMAND_PREFIX + SLICER_SORT_DESCEND,
        workArea: SLICER
      },
      {
        text: contextMenuRes.remove,
        name: MENUITEM_NAME_PREFIX + REMOVE_SLICER,
        command: SPREAD_COMMAND_PREFIX + REMOVE_SLICER,
        workArea: SLICER
      },
      {
        text: contextMenuRes.slicerProperty,
        name: MENUITEM_NAME_PREFIX + SLICER_PROPERTY,
        command: MENUITEM_NAME_PREFIX + SLICER_PROPERTY,
        iconClass: ICON_CLASS_PREFIX + SLICER_PROPERTY,
        workArea: SLICER
      },
      {
        text: contextMenuRes.slicerSetting,
        name: MENUITEM_NAME_PREFIX + SLICER_SETTING,
        command: MENUITEM_NAME_PREFIX + SLICER_SETTING,
        iconClass: ICON_CLASS_PREFIX + SLICER_SETTING,
        workArea: SLICER
      },
      { "type": SEPARATOR },
      {
        text: contextMenuRes.groupShapes,
        iconClass: ICON_CLASS_PREFIX + GROUP_SHAPE,
        name: MENUITEM_NAME_PREFIX + GROUP,
        subMenu: [
          {
            text: contextMenuRes.groupShapes,
            iconClass: ICON_CLASS_PREFIX + GROUP_SHAPE,
            name: MENUITEM_NAME_PREFIX + GROUP_SHAPE,
            command: MENUITEM_NAME_PREFIX + GROUP_SHAPE,
          },
          {
            text: contextMenuRes.ungroupShapes,
            iconClass: ICON_CLASS_PREFIX + UNGROUP_SHAPE,
            name: MENUITEM_NAME_PREFIX + UNGROUP_SHAPE,
            command: MENUITEM_NAME_PREFIX + UNGROUP_SHAPE,
          }
        ],
        workArea: SHAPE
      },
      {
        text: contextMenuRes.formatShapes,
        name: MENUITEM_NAME_PREFIX + FORMAT_SHAPES,
        command: MENUITEM_NAME_PREFIX + FORMAT_SHAPES,
        workArea: SHAPE
      },
    ]

    function DesignerMenuView() {
    }

    DesignerMenuView.prototype = new GC.Spread.Sheets.ContextMenu.MenuView()
    DesignerMenuView.prototype.createMenuItemElement = function (menuItemData) {
      var self = this
      var menuItemView
      if (menuItemData.name === MENUITEM_NAME_PREFIX + TAB_COLOR) {
        var supMenuItemContainer = GC.Spread.Sheets.ContextMenu.MenuView.prototype.createMenuItemElement.call(self, menuItemData)
        supMenuItemContainer.empty()
        $(supMenuItemContainer[0]).colorpicker(
          {
            valueChanged: function (e, value) {
              if (!value.isSetColorDirectly) {
                $(supMenuItemContainer[0]).data("selectedColor", value)
              } else {
                designer.actions.doAction("setSheetTabColor", designer.wrapper.spread, value.color)
              }
            }
          }
        )
        $(supMenuItemContainer[0]).colorpicker("option", "themeColors", designer.wrapper.getThemeColors())
        supMenuItemContainer.css({ "margin": "0", "padding": "0" })
        supMenuItemContainer.css("background", "white")
        return supMenuItemContainer
      } else if (menuItemData.name === MENUITEM_NAME_PREFIX + PROTECT_SHEET) {
        var activeSheet = designer.wrapper.spread.getActiveSheet()
        if (activeSheet.options.isProtected) {
          menuItemData.text = contextMenuRes.unprotectsheet
        } else {
          menuItemData.text = contextMenuRes.protectsheet
        }
        menuItemView = GC.Spread.Sheets.ContextMenu.MenuView.prototype.createMenuItemElement.call(self, menuItemData)
        return menuItemView
      } else {
        menuItemView = GC.Spread.Sheets.ContextMenu.MenuView.prototype.createMenuItemElement.call(self, menuItemData)
        // 张天可
        // 超出最大长度隐藏的选项设置悬浮内容
        menuItemView[0].addEventListener("mousemove", function (event) {
          let targetEl = event.target
          let contentEl
          if ($(targetEl).is(".gc-ui-contextmenu-menuitem-content")) {
            contentEl = targetEl
          } else {
            contentEl = $(targetEl).parent(".gc-ui-contextmenu-menuitem-content")[0]
          }
          if (contentEl) {
            let textEl = contentEl.querySelector("span.gc-ui-contextmenu-text")
            if (textEl && textEl.scrollWidth > textEl.offsetWidth) {
              contentEl.title = textEl.innerText
            } else {
              contentEl.title = ""
            }
          }
        })
        return menuItemView
      }
    }
    DesignerMenuView.prototype.getCommandOptions = function (menuItemData, host, event) {
      if (menuItemData.name === MENUITEM_NAME_PREFIX + TAB_COLOR) {
        return {
          value: $(host).children().data("selectedColor")
        }
      } else if (menuItemData.name === MENUITEM_NAME_PREFIX + FORMAT_CHART) {
        return menuItemData.options
      } else if (menuItemData.options) {
        return menuItemData.options
      }
    }
    spread.contextMenu.menuView = new DesignerMenuView()
    designer.spreadActions.initDesignerCommands(spread.commandManager())
  }

  contextMenu.initContextMenu = initContextMenu

  //endregion
  //#region Loader.Ready
  designer.loader.ready(function () {
    if (!designer.wrapper.spread.notWorking) {
      initContextMenu()
    }
  })
  designer.contextMenu = contextMenu
})()
