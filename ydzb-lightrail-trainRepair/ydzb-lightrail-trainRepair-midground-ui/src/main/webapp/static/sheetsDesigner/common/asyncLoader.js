(function (loader) {
  'use strict'

  var designer = GC.Spread.Sheets.Designer
  var loader = {}
  loader.rootFolder = '../'
  // 张天可
  loader.isReady = false
  var delayActions = []
  var bindingApplied

  function onReady(root) {
    if (!bindingApplied) {
      ko.applyBindings({ res: designer.res }, document.body)
      ko.applyBindings({ res: designer.res }, document.head)
    }

    for (var i = 0; i < delayActions.length; i++) {
      delayActions[i]()
    }
    delayActions = []

    if (!bindingApplied) {
      setTimeout(function () {
        root.find('.loading-placeholder').addClass('hidden')
        app.notifyReady()
      }, 0)
    } else {
      root.find('.loading-placeholder').addClass('hidden')
      app.notifyReady()
    }
    bindingApplied = true
    // 张天可
    loader.isReady = true
  }

  function ready(func) {
    delayActions.push(func)
  }

  loader.ready = ready

  // 张天可
  loader.customReady = function (func) {
    if (loader.isReady) {
      func()
    } else {
      delayActions.push(func)
    }
  }

  function getRootFolder() {
    if (!loader.rootFolder) {
      return './'
    }

    var folder = loader.rootFolder.trim()
    if (folder.slice(-1) !== '/') {
      folder = folder + '/'
    }
    return folder
  }

  function loadContent(root) {
    if (root === undefined) {
      root = $(document)
    }

    var folder = getRootFolder()

    var includes = root.find('[data-include]'), tasks = [], scripts = []

    includes.each(function () {
      var $this = $(this), include = $this.data('include')
      var fileName = designer.util.resolveHtmlPath(folder + include, include + '.html')
      tasks.push(designer.util.loadHtml(fileName, '.' + include, $this))
    })

    return $.when.apply($, tasks).then(function () {
      designer.ribbon.addLocalFonts()
      if (scripts.length !== 0) {
        var scriptTasks = scripts.map(function (s) {
          return designer.util.loadScript(s + '.js')
        })
        $.when.apply($, scriptTasks).then(function () {
          onReady(root)
        })
      } else {
        onReady(root)
      }
    })
  }

  loader.loadContent = loadContent
  designer.loader = loader
})()
