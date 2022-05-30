let copyData = (data) => JSON.parse(JSON.stringify(data));

Vue.component('track-wrapper', {
  name: 'track-wrapper',
  props: {
    trackAreaName: {
      type: String,
      default: '',
    },
    isConnect: {
      type: Boolean,
      default: false,
    },
    trackInfo: {
      type: Object,
      default: () => ({}),
    },
    powerInfo: {
      type: Object,
      default: () => ({}),
    },
    isActive: {
      type: Boolean,
      default: false,
    },
    trackPowerChangedState:{
      type: Boolean,
      default: false,
    },
    unitCode: {
      type: String,
      default: '',
    },
    unitName: {
      type: String,
      default: '',
    }

  },
  data() {
    return {};
  },
  computed: {},
  created() {},
  methods: {},
  render(createElement) {
    let React = { createElement };

    return (
      <div
        {...{
          class: {
            'track-wrapper': true,
            'wrapper-shadow': this.isActive,
          },
        }}
      >
        {[
          (() => {
            if (this.trackAreaName) {
              return <div class="track-name">{this.trackAreaName}</div>;
            }
          })(),
          this.trackInfo.lstTrackPositionInfo.map((positionItem, positionIndex) => {
            return [
              <div
                {...{
                  class: {
                    'position-title': true,
                    'left-position': positionIndex == 0,
                    'right-position': positionIndex == 1,
                  },
                }}
              >
                {[
                  <div class="track-position-name">
                    {this.trackInfo.trackName + '-' + positionItem.trackPostionName}
                  </div>,
                  <div
                    class="power-state"
                    {...{
                      class: {
                        'power-state': true,
                        danger: this.powerInfo[positionItem.trackPositionCode]&&this.powerInfo[positionItem.trackPositionCode].state == '1',
                      },
                      on: {
                        click: () => {
                          let data = [];
                          if(this.trackPowerChangedState){
                            // 单独
                            if(this.powerInfo[positionItem.trackPositionCode]){
                              let powerInfo = copyData(this.powerInfo[positionItem.trackPositionCode])
                              powerInfo.state = powerInfo.state == '1' ? '2' : '1';
                              data.push(powerInfo)
                            }else{
                              data.push({
                                dataSource: '',
                                recordTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
                                state: '2',
                                time: dayjs().format('YYYY-MM-DD HH:mm:ss'),
                                trackCode: ''+this.trackInfo.trackCode,
                                trackName: this.trackInfo.trackName,
                                trackPlaCode: ''+positionItem.trackPositionCode,
                                trackPlaName: positionItem.trackPostionName,
                                unitCode: this.unitCode,
                                unitName: this.unitName,
                              })
                            }
                          }else{
                            // 统一
                            this.trackInfo.lstTrackPositionInfo.forEach(position=>{
                              if(this.powerInfo[position.trackPositionCode]){
                                let powerInfo = copyData(this.powerInfo[position.trackPositionCode])
                                powerInfo.state = powerInfo.state == '1' ? '2' : '1';
                                data.push(powerInfo)
                              }else{
                                data.push({
                                  dataSource: '',
                                  recordTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
                                  state: '2',
                                  time: dayjs().format('YYYY-MM-DD HH:mm:ss'),
                                  trackCode: ''+this.trackInfo.trackCode,
                                  trackName: this.trackInfo.trackName,
                                  trackPlaCode: ''+position.trackPositionCode,
                                  trackPlaName: position.trackPostionName,
                                  unitCode: this.unitCode,
                                  unitName: this.unitName,
                                })
                              }
                            })

                            const targetState = data.find(item=>item.trackPlaCode==positionItem.trackPositionCode).state
                            data.forEach(item=>{
                              item.state = targetState
                            })
                          }

                          // console.log(data);
                          // return
                          this.$emit('track-power-change', data);
                        },
                      },
                    }}
                  ></div>,
                ]}
              </div>,
            ];
          }),
          <div
            {...{
              class: {
                'connect-icon': true,
                'is-block': this.isConnect,
              },
              on: {
                click: () => {
                  this.$emit('train-position-exchange', this.trackInfo.trackCode);
                },
              },
            }}
          ></div>,
          this.$slots.default,
          
        ]}
      </div>
    );
  },
});
