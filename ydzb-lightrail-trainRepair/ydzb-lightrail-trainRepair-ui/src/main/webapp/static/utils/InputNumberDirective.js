function onInput(el, ele, binding, vnode, flag) {
  function handle() {
    setTimeout(function() {
      if (flag) {
        let val = ele.value
        val = val.replace(/-/g, '')
        val = val.replace(/[^\-\d.]/g, '')
        const idx = val.indexOf('.')
        const { integer } = binding.value
        if (!(idx === -1 || idx === val.length - 1)) {
          if (!isNaN(integer)) {
            const str = '^(\\d{' + integer + '}).*$'
            const reg = new RegExp(str)
            val = val.substr(0, idx).replace(reg, '$1') + '.' + val.substr(idx + 1).replace(/\./g, '')
          } else {
            val = val.substr(0, idx) + '.' + val.substr(idx + 1).replace(/\./g, '')
          }
        } else {
          const str = '^(\\d{' + integer + '}).*$'
          const reg = new RegExp(str)
          val = val.replace(reg, '$1')
          if (idx !== -1 && idx === val.length) {
            val = val + '.'
          }
        }
        if (typeof binding.value !== 'undefined') {
          let pointKeep = 0
          if (typeof binding.value === 'string' || typeof binding.value === 'number') {
            pointKeep = parseInt(binding.value)
          } else if (typeof binding.value === 'object') {
            pointKeep = binding.value.decimal
          }
          if (!isNaN(pointKeep)) {
            if (!Number.isInteger(pointKeep) || pointKeep < 0) {
              pointKeep = 0
            }
            const str = '^(\\d+)\\.(\\d{' + pointKeep + '}).*$'
            const reg = new RegExp(str)
            if (pointKeep === 0) {
              val = val.replace(reg, '$1')
            } else {
              val = val.replace(reg, '$1.$2')
            }
          }
        } else {
          val = ele.value.replace(/[^\d]/g, '')
        }
        if (val !== '' && typeof binding.value === 'object') {
          let { min } = binding.value
          min = parseFloat(min)
          if (!isNaN(min)) {
            if (min >= 0) {
              val = val.replace('-', '')
            }
            if (parseFloat(val) < min) {
              val = min
            }
          }
        }
        if (val !== '' && val !== '.') {
          const idx1 = val.indexOf('.')
          if (idx1 !== -1 && idx1 === val.length - 1) {
            const str1 = '^(\\d*).*$'
            const reg1 = new RegExp(str1)
            val = val.replace(reg1, '$1')
            val = parseFloat(val) + '.'
          } else {
            const pointKeep = binding.value.decimal
            if (!isNaN(pointKeep) && idx1 !== -1) {
              const str2 = '^(\\d+)\\.(\\d{' + pointKeep + '}).*$'
              const reg2 = new RegExp(str2)
              let length = 0
              if (val.replace(reg2, '$2') !== '') {
                length = val.replace(reg2, '$2').length
              }
              if (length === pointKeep) {
                val = parseFloat(val)
              }
            } else {
              val = parseFloat(val)
            }
          }
        }
        if (parseInt(idx) === 0) {
          val = '0' + val
        }
        // 给输入框绑定值
        ele.value = val
        // 调用输入框的input事件，使v-model生效
        ele.dispatchEvent(new Event("input"))
      }
    }, 0)
  }
  return handle
}
const numberInput = {
  bind(el, binding, vnode) {
    let flag = true
    el.addEventListener('compositionstart', (event) => {
      flag = false
    })
    el.addEventListener('compositionend', (event) => {
      flag = true
    })
    const ele = el.tagName === 'INPUT' ? el : el.querySelector('input')
    ele.addEventListener('input', onInput(el, ele, binding, vnode, flag), false)
  }
}
Vue.directive('number-input', numberInput)