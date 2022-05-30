/**
 * 本js用于修复elementUI table组件的一个bug。
 * bug内容: table组件的父元素如果为flex布局，则会在flame页面从display:none到显示时到时动态计算出错误的高度。
 * bug表现: 从其他flame页面切换到本页面时，组件高度可能会经历一个从过高到正常的过程
 *
 * 20220114 更新：支持flex布局下，el-table高度的动态适应
 * @author 张天可
 */
(function () {
  if (!ELEMENT) {
    throw "请先引入elementUI再引入本js！"
  }
  var elTable = ELEMENT.Table
  var wrapFn = function (obj, name, fn) {
    var ori = obj[name]
    obj[name] = function () {
      var args = arguments
      var this_ = this
      return fn.call(this, function () {
        return ori.apply(this_, args)
      }, args)
    }
  }
  var stateMap = new Map()
  wrapFn(elTable, "data", function (ori) {
    var data = ori()
    wrapFn(data.layout, "updateElsHeight", function (ori) {
      var curState = stateMap.get(this.table)
      var this_ = this
      var rs
      // 如果处于隐藏状态则进入修复过的逻辑
      if (curState) {
        if (curState.hidden === true) {
          if (!curState.cancel) {// 如果是隐藏到显示的初次设置高度，令其失效(人为设置一个小值)
            rs = ori()
            if (this.bodyHeight !== 0) {
              var lastBodyHeight = this.bodyHeight
              // 临时保存当前高度，如果超时则重新设置这个高度
              curState.lastBodyHeight = lastBodyHeight
              // 设置0不会生效，所以设置的1
              this.bodyHeight = 1
              var cancelKey = setTimeout(function () {
                this_.bodyHeight = curState.lastBodyHeight
                curState.cancel()
              }, 100)
              curState.cancel = function () {
                clearTimeout(cancelKey)
                curState.cancel = null
                curState.hidden = false
                curState.lastBodyHeight = null
              }
            }
          } else {// 保留从隐藏到显示的第二次设置高度
            rs = ori()
            curState.cancel()
          }
        } else {
          rs = ori()
          // 如果为0则表示table被隐藏了
          if (this.bodyHeight === 0) {
            curState.hidden = true
          }
        }
      } else {
        rs = ori()
      }
      return rs
    })
    stateMap.set(this, {
      hidden: false
    })
    return data
  })
  // 嵌入一个absolute元素，这样内部元素就不会影响flex布局时的高度判断
  wrapFn(elTable, "render", function (ori, args) {
    var createElement = args[0]
    var v_node = ori()
    var refs = ["hiddenColumns", "headerWrapper", "bodyWrapper"]
    var getIsChildEqRef = function (ref) {
      return function (child) {
        return child.data.ref === ref
      }
    }
    // 记录原始位置与子元素
    var targetChildrenPair = refs.map(function (ref) {
      var i = v_node.children.findIndex(getIsChildEqRef(ref))
      return [i, v_node.children[i]]
    })
    // 删除原本子元素
    refs.forEach(function (ref) {
      var i = v_node.children.findIndex(getIsChildEqRef(ref))
      if (i != -1) {
        v_node.children.splice(i, 1)
      }
    })
    targetChildrenPair.sort(function (v1, v2) {
      return v1[0] - v2[0]
    })
    var targetChildren = targetChildrenPair.map(function (v) {
      return v[1]
    })
    var startIndex = targetChildrenPair[0][0]
    v_node.children.splice(startIndex, 0, createElement(
      "div",
      {
        staticStyle: {
          position: "absolute",
          width: "100%"
        }
      },
      targetChildren
    ))
    return v_node
  })
  wrapFn(elTable, "destroyed", function (ori) {
    ori()
    stateMap.delete(this)
  })
  // 重新注册组件
  delete elTable._Ctor
  delete elTable._compiled
  Vue.component(elTable.name, elTable)
})()