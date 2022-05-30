(function () {
  var designer = GC.Spread.Sheets.Designer
  designer.loader.customReady(function () {
    // let dataMapping = (obj) => {
    //   let computedObj = {}
    //   for (const key in obj) {
    //     computedObj[key] = {
    //       get() {
    //         return obj[key]
    //       },
    //       set(v) {
    //         obj[key] = v
    //       }
    //     }
    //   }
    //   return computedObj
    // }
    const TemplateQueryEnum = {
      UNIT: "Unit",
      TRAINSET_TYPE: "TrainsetType",
      TRAINSET: "Trainset",
      BATCH: "Batch",
      ITEM: "Item",
      MARSHAL_COUNT: "MarshalCount",
      properties: {
        Unit: {
          description: "运用所",
        },
        TrainsetType: {
          description: "车型",
        },
        Trainset: {
          description: "车组",
        },
        Batch: {
          description: "批次",
        },
        Item: {
          description: "项目",
        },
        MarshalCount: {
          description: "编组数量",
        },
      },
    }

    window.billBasicInfo = new Vue({
      data: {},
      computed: {
        templateBasicData: {
          get() {
            return app.core.templateBasicData
          },
        },
        templateTypeInfo() {
          return app.core.templateTypeInfo
        },
        fixedErrorMessage() {
          return app.core.fixedErrorMessage
        },
      },
      el: "#billBasicInfo",
      render(createElement) {
        let React = { createElement }
        let billBasicInfo = this.templateBasicData || {}
        let valueGetterMap = {
          [TemplateQueryEnum.UNIT]: () => billBasicInfo.unitName,
          [TemplateQueryEnum.TRAINSET_TYPE]: () => billBasicInfo.trainsetType,
          [TemplateQueryEnum.BATCH]: () => billBasicInfo.batch,
          // [TemplateQueryEnum.TRAINSET]: () => billBasicInfo.trainsetName,
          [TemplateQueryEnum.MARSHAL_COUNT]: () => billBasicInfo.marshalCount,
          [TemplateQueryEnum.ITEM]: () => billBasicInfo.itemName,
        }
        let arrForSort = [
          TemplateQueryEnum.UNIT,
          TemplateQueryEnum.TRAINSET_TYPE,
          TemplateQueryEnum.BATCH,
          TemplateQueryEnum.MARSHAL_COUNT,
          TemplateQueryEnum.ITEM,
        ]
        return (
          <div class={["billBasicInfoContainer"]}>
            {(() => {
              let list = []
              if (this.templateTypeInfo) {
                list.push(
                  <div>{[`单据类型：`, this.templateTypeInfo.templateTypeName]}</div>
                )
                if (this.templateTypeInfo.linkQueries) {
                  let linkQueryMap = this.templateTypeInfo.linkQueries.reduce((pre, cur) => {
                    pre[cur.queryCode] = cur
                    return pre
                  }, {})
                  list = [
                    ...list,
                    arrForSort
                      .filter((v) => linkQueryMap[v] && valueGetterMap[v]())
                      .map((queryCode) => {
                        let query = linkQueryMap[queryCode]
                        return <div>{[`${query.queryName}：`, valueGetterMap[queryCode]()]}</div>
                      }),
                  ]
                }
              }
              if (this.fixedErrorMessage) {
                list.push(
                  <div
                    {...{
                      style: {
                        color: "red",
                      },
                    }}
                  >
                    {this.fixedErrorMessage}
                  </div>
                )
              }
              return list
            })()}
          </div>
        )
      },
    })
  })
})()
