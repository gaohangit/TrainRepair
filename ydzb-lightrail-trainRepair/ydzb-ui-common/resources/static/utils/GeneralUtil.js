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
          throw "bindTitle?????????????????????function"
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
    
            // ????????????????????????
            for (let i = index + 1; i < vueInstances.length; i++) {
              const afterPropertyVueInstance = vueInstances[i]
              afterPropertyVueInstance.value = null
              if (debug) {
                console.log("setNull:" + afterPropertyVueInstance.propertyOption.name)
              }
            }
            // ??????????????????????????????????????????index????????????????????????????????????????????????????????????
            let startNeedResetOptionsPropertyIndex = index + 1
    
            // ?????????????????????????????????????????????????????????????????????
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
                    // ?????????????????????????????????????????????
                    startNeedResetOptionsPropertyIndex = i
                  }
                }
              }
            }
    
            // ??????????????????
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
            // elementUI????????????null???????????????????????????????????????????????????
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
                throw "FamilyPropertyDefinitions??????????????????????????????????????????????????????"
              }
            }
            if (this.debug) {
              console.log(stackChanges.slice())
            }
            // ????????????
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
            // ??????1?????????setTimeout????????????????????????
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
            // ??????????????????
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
