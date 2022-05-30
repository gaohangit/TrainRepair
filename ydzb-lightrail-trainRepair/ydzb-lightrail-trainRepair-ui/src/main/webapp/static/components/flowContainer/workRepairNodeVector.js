Vue.component('work-repair-node-vector', {
  name: 'WorkRepairNodeVector',
  props: ['fromNodeId', 'toNodeId', 'context', 'isHighlight'],

  computed: {
    contextMain() {
      return this.context.main;
    },
    fromNodeInstance() {
      if (this.contextMain) {
        return this.contextMain.nodeInstanceMap[this.fromNodeId];
      } else {
        return null;
      }
    },
    toNodeInstance() {
      if (this.contextMain) {
        return this.contextMain.nodeInstanceMap[this.toNodeId];
      } else {
        return null;
      }
    },
    points() {
      let points = [];
      if (this.fromNodeInstance && this.toNodeInstance) {
        let start = {
          x: this.fromNodeInstance.left + this.fromNodeInstance.finalWidth / 2,
          y: this.fromNodeInstance.top + this.fromNodeInstance.finalHeight,
        };
        let end = {
          x: this.toNodeInstance.left + this.toNodeInstance.finalWidth / 2,
          y: this.toNodeInstance.top - 3,
        };
        if (start.x == end.x) {
          points.push(start);
          points.push(end);
        } else {
          let baseYGap = this.context.nodeShowConfig.yGap;

          let secondPoint = {
            x: start.x,
            y:
              start.x < end.x
                ? end.y - (baseYGap * 1) / 3
                : start.y + (baseYGap * 1) / 3 + this.fromNodeInstance.extraBottomGap,
            // y:end.y - (baseYGap * 1) / 3
          };
          let thirdPoint = {
            x: end.x,
            y: secondPoint.y,
          };

          points = [start, secondPoint, thirdPoint, end];
        }
      }
      return points;
    },
    path() {
      return 'M ' + this.points.map((v) => `${v.x} ${v.y}`).join(' L ');
    },
    endPoint() {
      return this.points[this.points.length - 1];
    },
    trianglePath() {
      const end = this.endPoint;
      return `M ${end.x + 0.5} ${end.y + 1} L ${end.x - 5}  ${end.y - 5} L ${end.x + 6}  ${end.y - 5}`;
    },
    highLightTrianglePath() {
      const end = this.endPoint;
      return `M ${end.x + 1} ${end.y + 1} L ${end.x - 5}  ${end.y - 5} L ${end.x + 7}  ${end.y - 5}`;
    },
  },
  watch:{
    context(val){
      console.log(val);
    }
  },
  created() {},
  render(createElement) {
    let React = { createElement };

    return (
      <g>
        {[
          ...(() => {
            if (this.points.length > 0) {
              return [
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
                <path
                  {...{
                    attrs: {
                      d: this.trianglePath,
                      fill: 'rgb(78,134,255)',
                      stroke: 'rgb(78,134,255)',
                      'stroke-width': '2',
                    },
                  }}
                ></path>,
                ...(() => {
                  if (this.isHighlight) {
                    return [
                      <path
                        {...{
                          attrs: {
                            d: this.path,
                            fill: 'none',
                            stroke: '#0be2ff',
                            'stroke-width': '5',
                          },
                        }}
                      ></path>,
                      <path
                        {...{
                          attrs: {
                            d: this.highLightTrianglePath,
                            fill: '#0be2ff',
                            stroke: '#0be2ff',
                            'stroke-width': '3',
                          },
                        }}
                      ></path>,
                    ];
                  } else {
                    return [];
                  }
                })(),
                <path
                  class="transparent"
                  {...{
                    attrs: {
                      d: this.path,
                      fill: 'transparent',
                      stroke: 'transparent',
                      'stroke-width': '10',
                    },
                    on: {
                      mouseenter: (e) => {
                        this.$emit('mouseenter');
                      },
                      mouseleave: (e) => {
                        this.$emit('mouseleave');
                      },
                    },
                  }}
                ></path>,
              ];
            } else {
              return [];
            }
          })(),
        ]}
      </g>
    );
  },
});
