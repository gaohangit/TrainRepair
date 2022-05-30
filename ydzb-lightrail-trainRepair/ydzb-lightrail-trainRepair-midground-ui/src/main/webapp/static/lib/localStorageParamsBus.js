var localStorageParamsBus = (function (params) {
  var getAllParamsData = function () {
    var data = localStorage.getItem("__paramData__")
    if (data) {
      return JSON.parse(data)
    } else {
      return {}
    }
  }
  var setAllParamData = function (data) {
    localStorage.setItem("__paramData__", JSON.stringify(data))
  }
  var setParamData = function (pageCode, param, data) {
    var allData = getAllParamsData()
    if (!allData[pageCode]) {
      allData[pageCode] = {}
    }
    allData[pageCode][param] = data
    setAllParamData(allData)
  }
  var getParamData = function (pageCode, paramName) {
    var allData = getAllParamsData()
    if (allData[pageCode] && allData[pageCode][paramName]) {
      return allData[pageCode][paramName]
    } else {
      return null
    }
  }
  var removeParamData = function (pageCode, paramName) {
    var allData = getAllParamsData()
    if (allData[pageCode] && allData[pageCode][paramName]) {
      delete allData[pageCode][paramName]
      setAllParamData(allData)
    }
  }
  var listeners = {}
  setInterval(function () {
    for (var pageCode in listeners) {
      var p = listeners[pageCode]
      for (const paramName in p) {
        var list = p[paramName]
        if (list && list.length > 0) {
          var paramData = getParamData(pageCode, paramName)
          if (paramData) {
            removeParamData(pageCode, paramName)
            list.forEach(function (v) {
              try {
                v(paramData)
              } catch (error) {
                console.error(error)
              }
            })
          }
        }
      }
    }
  }, 40)
  return {
    clearData(pageCode, paramName) {
      removeParamData(pageCode, paramName)
    },
    addParamsListener: function (pageCode, paramName, listener) {
      if (typeof pageCode != "string" || typeof paramName != "string" || typeof listener != "function") {
        throw "参数错误"
      }
      if (!listeners[pageCode]) {
        listeners[pageCode] = {}
      }
      if (!listeners[pageCode][paramName]) {
        listeners[pageCode][paramName] = []
      }
      listeners[pageCode][paramName].push(listener)
    },
    removeParamsListener: function (pageCode, paramName, listener) {
      if (typeof pageCode != "string" || typeof paramName != "string" || typeof listener != "function") {
        throw "参数错误"
      }
      if (listeners[pageCode] && listeners[pageCode][paramName]) {
        var index = listeners[pageCode][paramName].indexOf(listener)
        if (index != -1) {
          listeners[pageCode][paramName].splice(index, 1)
        }
      }
    },
    sendParams: function (pageCode, paramName, data) {
      setParamData(pageCode, paramName, data)
    }
  }
})()
