(function () {
  var designer = GC.Spread.Sheets.Designer
  designer.loader.customReady(function () {
    let dataMapping = (obj) => {
      let computedObj = {}
      for (const key in obj) {
        if (typeof obj[key] !== 'function') {
          computedObj[key] = {
            get() {
              return obj[key]
            },
            set(v) {
              obj[key] = v
            }
          }
        }
      }
      return computedObj
    }
    window.billTemplateSetting = new Vue({
      data: {
        // billCount: 1,
        // curBillNum: 1
      },
      computed: {
        ...dataMapping(app.core.billConfigSetting)
      },
      el: "#billTemplateSettingGroup",
      methods: {
        close() {
          app.core.billConfigSetting.close()
        }
      },
      render(createElement) {
        let React = { createElement }
        return (
          <div
            class={['bilConfigContainer']}
          >
            {[
              <div>
                {[
                  "单据数量：",
                  <input
                    attrs={{ type: 'radio' }}
                    domProps={{ checked: this.billCount == 1, disabled: this.billCountRadioDisabled }}
                    on={{
                      change: (e) => {
                        if (e.target.checked) {
                          this.billCount = 1
                        }
                      }
                    }}
                  />,
                  "1",
                  <input
                    attrs={{ type: 'radio' }}
                    domProps={{ checked: this.billCount == 2, disabled: this.billCountRadioDisabled }}
                    on={{
                      change: (e) => {
                        if (e.target.checked) {
                          this.billCount = 2
                        }
                      }
                    }}
                  />,
                  "2"
                ]}
              </div>,
              (this.billCount == 2) ? (<div>
                {[
                  <button
                    style={{ marginRight: "10px" }}
                    attrs={{ disabled: this.curBillNum == 1 }}
                    on={{
                      click: (e) => {
                        this.curBillNum = 1
                      }
                    }}
                  >
                    上一张
                  </button>,
                  <button
                    attrs={{ disabled: this.curBillNum == 2 }}
                    on={{
                      click: (e) => {
                        this.curBillNum = 2
                      }
                    }}
                  >
                    下一张
                  </button>
                ]}
              </div>) : null,
              <div>
                {[
                  <label attrs={{ for: "styleShowModeCheckbox" }}>
                    查看模式：
                  </label>,
                  <input
                    attrs={{ type: 'checkbox', id: "styleShowModeCheckbox" }}
                    domProps={{ checked: this.isShowMode }}
                    on={{
                      change: (e) => {
                        this.isShowMode = e.target.checked
                      }
                    }}
                  />
                ]}
              </div>,
              <div class={['btnGroup']}>
                {(() => {
                  let buttons = []
                  if (app.core.canEditConfig) {
                    // TODO 暂时禁用复制功能
                    // buttons.push(<button
                    //   attrs={{ disabled: this.saveBtnDisabled }}
                    //   on={{
                    //     click: (e) => {
                    //       app.core.copyBillConfig()
                    //     }
                    //   }}
                    //  >复制记录单</button>)
                    if (this.showReleaseBtn) {
                      buttons.push(
                        <button
                          on={{
                            click: (e) => {
                              app.core.publishBillConfig()
                            }
                          }}
                        >发布</button>
                      )
                    }
                    buttons.push(
                      <button
                        attrs={{ disabled: this.saveBtnDisabled }}
                        on={{
                          click: (e) => {
                            app.core.saveBillConfig()
                          }
                        }}
                      >保存</button>
                    )
                  }
                  if (this.showCloseBtn) {
                    buttons.push(
                      <button
                        on={{
                          click: (e) => {
                            this.close()
                          }
                        }}
                      >关闭</button>
                    )
                  }
                  return buttons
                })()}
              </div>,
            ]}
          </div>
        )
      }
    })
  })

})()
