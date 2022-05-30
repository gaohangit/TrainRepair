let SvgMask = (() => {
  if (document.getElementById("mask-svg-style") == null) {
    let maskSvgStyleStr = `.mask-svg {
      width: 100vw;
      height: 100vh;
      position: fixed;
      left: 0;
      top: 0;
      pointer-events: none;
      z-index: 10000;
    }

    .mask-svg path {
      pointer-events: all;
    }`
    let style = document.createElement("style")
    style.innerHTML = maskSvgStyleStr
    style.id = "mask-svg-style"
    document.head.append(style)
  }
  let getPaths = ({ x, y, width, height }) => {
    return [
      `M${x} ${y}`,
      `L${x} ${y + height}`,
      `L${x + width} ${y + height}`,
      `L${x + width} ${y}`,
      `L${x} ${y}`,
    ]
  }

  let getPathEl = (elements, maskColor) => {
    let windowHeight = window.innerHeight
    let windowWidth = window.innerWidth
    let paths = [
      `M0 0`,
      `L${windowWidth} 0`,
      `L${windowWidth} ${windowHeight}`,
      `L0 ${windowHeight}`,
      `L0 0`,
    ]
    elements && elements.forEach(element => {
      paths.splice(paths.length - 1, 0, ...getPaths(element.getBoundingClientRect()))
    })
    let path = document.createElement("path")
    path.setAttribute("d", paths.join(" "))
    path.setAttribute("fill", maskColor)
    return path
  }

  function SvgMask({ elements, maskColor }) {
    this.elements = elements
    this.maskColor = maskColor || "rgba(0,0,0,0.5)"
  }

  SvgMask.prototype.getSvgElement = function() {
    if (this.el != null) {
      return this.el
    } else {
      let svg = document.createElement("svg")
      svg.className = "mask-svg"
      let path = getPathEl(this.elements, this.maskColor)
      svg.appendChild(path)
      this.el = svg
      return svg
    }
  }

  SvgMask.prototype.refresh = function() {
    if (this.el) {
      this.el.innerHTML = ""
      let path = getPathEl(this.elements, this.maskColor)
      this.el.appendChild(path)
      if (this.el.parentElement) {
        this.el.outerHTML = this.el.outerHTML
        this.el = document.querySelector("svg.mask-svg")
        this.el.querySelector("path").addEventListener("contextmenu", (e) => {
          e.stopPropagation()
          e.preventDefault()
        })
      }
    }
  }

  SvgMask.prototype.setElements = function(elements) {
    this.elements = elements
    this.refresh()
  }

  SvgMask.prototype.appendToBody = function() {
    if (!this.el || !this.el.parentElement) {
      let el = this.getSvgElement()
      document.body.appendChild(el)
      el.outerHTML = el.outerHTML
      this.el = document.querySelector("svg.mask-svg")
      this.el.querySelector("path").addEventListener("contextmenu", (e) => {
        e.stopPropagation()
        e.preventDefault()
      })

    }
  }

  SvgMask.prototype.removeFromBody = function() {
    if (this.el && this.el.parentElement) {
      document.body.removeChild(this.el)
    }
  }

  return SvgMask
})()
