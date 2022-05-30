var GeneralUtil = (function () {
  const isBlank = (value) => {
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
  return {
    getObservablePageTitleGetter: (function () {
      var getParentWindow = function () {
        if (window.parent != window) {
          return window.parent
        } else {
          return null
        }
      }
      var getCurrentPageTitle = function () {
        var parentWindow = getParentWindow()
        if (parentWindow) {
          var userIdInput = parentWindow.document.getElementById('userId')
          if (userIdInput) {
            var userId = userIdInput.value
            var curMenuKey = userId + 'curMenu'
            if (sessionStorage.getItem(curMenuKey)) {
              return JSON.parse(sessionStorage.getItem(curMenuKey)).title
            }
          }
        }
        return null
      }
      return function (defaultTitle) {
        var vueObj = new Vue({
          data: {
            title_: getCurrentPageTitle(),
            defaultTitle: defaultTitle
          },
          computed: {
            title() {
              return this.title_ || this.defaultTitle
            }
          }
        })
        var parentWindow = getParentWindow()
        var frameElement = window.frameElement
        if (parentWindow && frameElement && parentWindow.document) {
          var el = parentWindow.document.querySelector(".layui-tab-title")
          el && el.addEventListener("click", function () {
            setTimeout(() => {
              let clientRect = frameElement.getBoundingClientRect()
              if (clientRect.width != 0 && clientRect.height != 0) {
                vueObj.title_ = getCurrentPageTitle()
              }
            }, 0)
          })
        }
        return function () {
          return vueObj.title
        }
      }
    })(),
    bindTitle: (function () {
      var binder = new Vue()
      var endWatch = null
      var setTitle = (title) => {
        document.head.querySelector("title").innerText = title
      }
      return function (titleGetter) {
        if (typeof titleGetter != "function") {
          throw "bindTitle参数类型需要为function"
        }
        if (endWatch) {
          endWatch()
        }
        endWatch = binder.$watch(
          titleGetter,
          setTitle,
          {
            immediate: true
          }
        )
      }
    })(),
    isBlank,
    getFamilyPropertyDefinitions: (propertyOptions) => {
      let reverseFindFrom = (array, fromIndex, testFn) => {
        for (let i = fromIndex; i >= 0; i--) {
          const element = array[i]
          if (testFn(element)) {
            return element
          }
        }
        return null
      }
      let centerCtx = new Vue({
        data: {
          stackChanges: [],
          innerSettingValue: false,
          debug: false
        },
        methods: {
          triggerChange(index, fromValue, toValue) {
            if (!this.innerSettingValue) {
              this.stackChanges.push([index, fromValue, toValue])
            }
          },
          handleChange([index, fromValue, toValue], extraSetIndexes) {
            let debug = this.debug
    
            // 清空之后属性的值
            for (let i = index + 1; i < vueInstances.length; i++) {
              const afterPropertyVueInstance = vueInstances[i]
              afterPropertyVueInstance.value = null
              if (debug) {
                console.log("setNull:" + afterPropertyVueInstance.propertyOption.name)
              }
            }
            // 第一个需要刷新列表选项的属性index，此属性以及之后的属性均需要刷新列表选项
            let startNeedResetOptionsPropertyIndex = index + 1
    
            // 如果当前属性值不为空，则需要根据当前值设置其值
            if (isBlank(fromValue) && !isBlank(toValue)) {
              for (let i = index; i > 0; i--) {
                const currentPropertyVueInstance = vueInstances[i]
                const currentPropertyOption = currentPropertyVueInstance.propertyOption
                const previousPropertyVueInstance = vueInstances[i - 1]
                if (previousPropertyVueInstance && currentPropertyOption.getParentValue) {
                  let v = currentPropertyOption.getParentValue(currentPropertyVueInstance.options, currentPropertyVueInstance.value)
                  if (!isBlank(v) && (isBlank(previousPropertyVueInstance.value) || (extraSetIndexes && extraSetIndexes.includes(i - 1)))) {
                    previousPropertyVueInstance.value = v
                    if (debug) {
                      console.log("setValue:" + previousPropertyVueInstance.propertyOption.name)
                    }
                    // 设置值时，更新最小刷新起始位置
                    startNeedResetOptionsPropertyIndex = i
                  }
                }
              }
            }
    
            // 刷新列表选项
            for (let i = startNeedResetOptionsPropertyIndex; i < vueInstances.length; i++) {
              const afterPropertyVueInstance = vueInstances[i]
              afterPropertyVueInstance.resetOptions()
              if (debug) {
                console.log("resetOptions:" + afterPropertyVueInstance.propertyOption.name);
              }
            }
          },
          handleChanges(stackChanges) {
            this.innerSettingValue = true
            // elementUI会自动把null赋值为空字符串，需要把这部分过滤掉
            stackChanges = stackChanges.filter(([index, fromV, toV]) => !(isBlank(fromV) && isBlank(toV)))
            if (stackChanges.length == 1) {
              this.handleChange(stackChanges[0])
            } else if (stackChanges.length > 1) {
              if (stackChanges.every(([index, fromV, toV]) => !isBlank(fromV) && isBlank(toV))) {
                let copy = stackChanges.slice()
                copy.sort(([index1], [index2]) => index1 - index2)
                this.handleChange(copy[0])
              } else if (stackChanges.every(([index, fromV, toV]) => isBlank(fromV) && !isBlank(toV))) {
                let copy = stackChanges.slice()
                copy.sort(([index1], [index2]) => index1 - index2)
                this.handleChange(copy[copy.length - 1], copy.slice(0, copy.length - 1).map(v => v[0]))
              } else {
                throw "FamilyPropertyDefinitions同时变更多个值时仅支持多删除与多赋值"
              }
            }
            if (this.debug) {
              console.log(stackChanges.slice())
            }
            // 清空变更
            if (this.stackChanges.length > 0) {
              this.stackChanges.splice(0, this.stackChanges.length)
            }
            this.$nextTick(() => {
              this.innerSettingValue = false
            })
          }
        },
        watch: {
          stackChanges(stackChanges) {
            // 通过1毫秒的setTimeout合并紧密多次变化
            if (this.setTimeout_) {
              clearTimeout(this.setTimeout_)
              this.setTimeout_ = null
            }
            this.setTimeout_ = setTimeout(() => {
              this.setTimeout_ = null
              this.handleChanges(stackChanges)
            }, 1)
          }
        }
      })
      let vueInstances = propertyOptions.map((propertyOption, index) => {
        let isOuterValue = () => {
          return typeof propertyOption.getValue == "function" && typeof propertyOption.setValue == "function"
        }
        return new Vue({
          data() {
            let data = {
              options: [],
              innerSettingValue: false,
              innerValue: null
            }
            return data
          },
          computed: {
            propertyOption() {
              return propertyOption
            },
            value: {
              get() {
                if (isOuterValue()) {
                  return propertyOption.getValue()
                } else {
                  return this.innerValue
                }
              },
              set(value) {
                if (isOuterValue()) {
                  propertyOption.setValue(value)
                } else {
                  this.innerValue = value
                }
              }
            },
          },
          created() {
          },
          watch: {
            value(value, oldValue) {
              centerCtx.triggerChange(index, oldValue, value)
            }
          },
          methods: {
            // 重置选项列表
            resetOptions() {
              let result
              if (index > 0) {
                let hasValueParentVueInstance = reverseFindFrom(vueInstances, index - 1, (vueInstance) => {
                  return !isBlank(vueInstance.value)
                })
                hasValueParentVueInstance = hasValueParentVueInstance || vueInstances[0]
                let parentValue = hasValueParentVueInstance.value
                let parentName = hasValueParentVueInstance.propertyOption.name
                result = this.getOptions(parentValue, parentName)
              } else {
                result = this.getOptions()
              }
              if (result instanceof Promise) {
                result.then(v => {
                  this.options = v
                })
              } else {
                this.options = result
              }
            },
            getOptions(parentValue, parentName) {
              return propertyOption.getOptions(parentValue, parentName)
            },
          }
        })
      })
      return vueInstances.reduce((pre, cur) => {
        pre[cur.propertyOption.name] = cur
        return pre
      }, {})
    }
  }
})()
