Vue.component('work-repair-pair-node-connect', {
  name: 'WorkRepairPairNodeConnect',
  props: ['fromNodeId', 'toNodeId', 'context', 'offsetX'],
  computed: {
    contextMain() {
      return this.context.main
    },
    fromNodeInstance() {
      if (this.contextMain) {
        return this.contextMain.nodeInstanceMap[this.fromNodeId]
      } else {
        return null
      }
    },
    toNodeInstance() {
      if (this.contextMain) {
        return this.contextMain.nodeInstanceMap[this.toNodeId]
      } else {
        return null
      }
    },
    points() {
      if (!this.fromNodeInstance || !this.toNodeInstance) {
        return []
      } else {
        let getCenterY = (nodeInstance) => {
          return nodeInstance.top + nodeInstance.finalHeight / 2
        }
        let start = {
          x: this.fromNodeInstance.left,
          y: getCenterY(this.fromNodeInstance)
        }
        let end = {
          x: this.toNodeInstance.left,
          y: getCenterY(this.toNodeInstance)
        }
        let finalLeft = start.x - this.offsetX
        let secondPoint = {
          x: finalLeft,
          y: start.y
        }
        let thirdPoint = {
          x: finalLeft,
          y: end.y,
        }
        let points = [start, secondPoint, thirdPoint, end]
        return points
      }
    },
    path() {
      if (this.points.length === 0) return ''
      return 'M ' + this.points.map((v) => `${v.x} ${v.y}`).join(' L ')
    },

  },
  created() {
    // console.log(this.fromNodeId,this.toNodeId);
  },
  methods: {
  },

  render(createElement) {
    let React = { createElement }
    return (
      <g>
        {[
          <path
            {...{
              attrs: {
                d: this.path,
                fill: 'none',
                stroke: 'rgb(78,134,255)',
                'stroke-width': '4',
              },
            }}
          ></path>,
        ]}
      </g>
    )
  },
})
