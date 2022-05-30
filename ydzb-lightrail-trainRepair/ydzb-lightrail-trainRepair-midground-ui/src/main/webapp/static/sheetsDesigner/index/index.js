(function() {
  'use strict'

  var designer = GC.Spread.Sheets.Designer
  var needSuspend
  var tipWidth = designer.res.ribbon.accessBar.tipWidth || 600

  function updateLayout() {
    $(".content").css('height', $(".content").children('.fill-spread-content').height())
    $(".header").css("width", $(window).width() + "px")

    if ($(".ribbon-bar").data("gcui-gcuiribbon")) {
      $(".ribbon-bar").data("gcui-gcuiribbon").updateRibbonSize()
    }

    // 张天可
    // 因为和经常要暂停的操作冲突，所以注释起来了，目前看不影响正常使用
    // var spread = designer.wrapper.spread

    // if (spread && spread.isPaintSuspended()) {
    //   spread.resumePaint()
    //   spread.refresh()
    //   needSuspend = true
    // }
  }

  var _windowResizeTimer = null

  function _doWindowResize() {
    if (_windowResizeTimer) {
      window.clearTimeout(_windowResizeTimer)
    }

    _windowResizeTimer = window.setTimeout(function() {
      updateLayout()
      _windowResizeTimer = null

      if (needSuspend) {
        needSuspend = false
        window.setTimeout(function() {
          designer.wrapper.spread.suspendPaint()
        }, 300)
      }
    }, 100) //now delay 100ms to resize designer
  }

  $(document).ready(function() {
    app.core.init().then(function() {
      designer.loader.loadContent()
      $(window).resize(_doWindowResize)
      $(window).resize()
      $("#verticalSplitter").draggable({
        axis: "y",
        containment: ".container",
        scroll: false,
        zIndex: 100,
        stop: function stop(event, ui) {
          var $this = $(this),
            top = $this.offset().top,
            offset = top - $(".header").height(),
            $content = $(".content .fill-spread-content")
          // adjust size of related items

          var oldHeight = $("#formulaBarText").height()
          var newHeight = oldHeight + offset
          var ORIGINAL_FORMULABARTEXT_HEIGHT = 20
          var MAX_FORMULABAETEXT_HEIGHT = 100 // 100: max height of formulaBarText

          if (newHeight < ORIGINAL_FORMULABARTEXT_HEIGHT) { // 20: original height of formulaBarText
            // reset to default
            $("#formulaBarText").css({
              height: ORIGINAL_FORMULABARTEXT_HEIGHT
            })
            top = top + ORIGINAL_FORMULABARTEXT_HEIGHT - newHeight
          } else if (newHeight > MAX_FORMULABAETEXT_HEIGHT) {
            $("#formulaBarText").css({
              height: MAX_FORMULABAETEXT_HEIGHT
            })
            top = top + MAX_FORMULABAETEXT_HEIGHT - newHeight
          } else {
            $("#formulaBarText").css({
              height: newHeight
            })
          }

          // 张天可
          $content.css({
            top: top + 20
          }) // 10: height of the space for verticalSplitter

          $content.parent().css({
            height: $content.height()
          })
          $(".header").css({
            height: top
          })
          $this.css({
            top: 0
          })
          designer.wrapper.spread.refresh()
        }
      })

      function disableDragDrop(dataTransfer) {
        if (dataTransfer) {
          dataTransfer.effectAllowed = "none"
          dataTransfer.dropEffect = "none"
        }
      }

      window.addEventListener("dragenter", function(e) {
        e = e || event
        e.preventDefault()
        disableDragDrop(e.dataTransfer)
      }, false)
      window.addEventListener("dragover", function(e) {
        e = e || event
        e.preventDefault()
        disableDragDrop(e.dataTransfer)
      }, false)
      window.addEventListener("drop", function(e) {
        e = e || event
        e.preventDefault()
        disableDragDrop(e.dataTransfer)
      }, false)
    })
  })
  designer.loader.ready(function() {
    //To Fix the designer resize performance issues.
    $(window).unbind("resize.gcuiribbon")
    $("#verticalSplitter").show()
    updateLayout()
  })

  // 张天可
  // 初始化

  designer.loader.customReady(function() {
    var spread = designer.wrapper.spread



    window.onbeforeunload = function() {
      if (app.core.changed) {
        // TODO
        // return "__"
      }
    }
  })
})()