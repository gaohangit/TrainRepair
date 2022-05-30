(function () {
  let res = null;
  async function getTrainsetListReceivedInfo() {
    const result = await getTrainsetListReceived();
    if (Array.isArray(result)) {
      res = result;
    } else {
      res = result.data;
    }
  }
  if (!res) getTrainsetListReceivedInfo();

  Vue.component('train-wrapper', {
    name: 'train-wrapper',
    props: {
      trainsetInfo: {
        type: Object,
        default: () => ({}),
      },
      showMoveTrainPlan: {
        type: Boolean,
        default: true,
      },
      isMoveTrainPlanWarning: {
        type: Boolean,
        default: true,
      },
      moveTrainPlanWarningTime: {
        type: String,
        default: '',
      },
      warningConfig: {
        type: Object,
        default: () => ({}),
      },
      focusId: {
        type: String,
        default: '',
      },
      hoverActive: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        baseImgPath: ctxPath + '/static/trainRepair/trainMonitor/img',
        trainsetId: this.trainsetInfo.trainsetId,
        urls: [],
        trainsetListReceived: res || [],
        standbyUrl: [
          `${ctxPath}/static/components/track/img/train/new-1.png`,
          `${ctxPath}/static/components/track/img/train/new-2.png`,
          `${ctxPath}/static/components/track/img/train/new-3.png`,
        ],
      };
    },
    computed: {
      trainsetIdTypeMap({ trainsetListReceived }) {
        return trainsetListReceived.reduce((prev, item) => {
          prev[item.trainsetid] = item.traintype;
          return prev;
        }, {});
      },
      trainType({ trainsetIdTypeMap, trainsetInfo }) {
        return trainsetIdTypeMap[trainsetInfo.trainsetId];
      },
      nextInTime({ trainsetInfo }) {
        return trainsetInfo.nextInTime ? dayjs(new Date(trainsetInfo.nextInTime)).format('HH:mm') : '';
      },
      tooltipContent({ trainsetInfo, nextInTime, showMoveTrainPlan }) {
        let timeText = '';
        if (nextInTime) {
          timeText = ' 计划离开时间：' + nextInTime;
        }
        let nextText = '';
        if (trainsetInfo.nextTrackPositionCode) {
          nextText =
            ' ' +
            this.trainsetInfo.nextTrackName +
            ' (' +
            this.trainsetInfo.nextTrackPositionName +
            ')';
        }

        if (!showMoveTrainPlan) {
          return trainsetInfo.trainsetName + timeText + nextText;
        } else {
          return trainsetInfo.trainsetName;
        }
      },
    },
    created() {
      // const trainTypeList = ['CR400AF', 'CR400BF', 'CRH1', 'CRH2', 'CRH3', 'CRH5', 'CRH380A', 'CRH380B', 'CRH380C'];
      // let trainType = trainTypeList.includes(this.trainType) ? this.trainType : 'CRH1';
      let trainType = this.trainsetInfo.trainsetName.split('-')
      trainType = trainType.splice(0,trainType.length-1).join('-')
      this.urls = getTrainUrls(trainType);
    },
    methods: {
      // 离开预警，文字闪烁
      isStar(time) {
        if (this.warningConfig[this.trainsetInfo.trainsetId] === false) return;
        if (!this.isMoveTrainPlanWarning) {
          let nowTime = new Date().valueOf();
          let warningTime = new Date(Number(time)).valueOf();
          let prevWarningTime =
            Number(this.moveTrainPlanWarningTime) * 60 * 1000;

          if (nowTime + prevWarningTime - warningTime > 0) {
            return true;
          } else {
            return false;
          }
        } else {
          return false;
        }
      },
    },
    render(createElement) {
      let React = { createElement };

      return (
        <div
          {...{
            class: {
              'trainset-wrapper': true,
              'long-wrapper': this.trainsetInfo.isLong == '1',
            },
          }}
        >
          {[
            this.$scopedSlots['train-header'] &&
              this.$scopedSlots['train-header'](),
            <div
              {...{
                class: {
                  'trainset-body': true,
                  'train-hover': this.hoverActive,
                  trainFilter: this.focusId == this.trainsetId,
                },
                on: {
                  contextmenu: (e) => {
                    this.$emit('train-contextmenu', {
                      trainsetInfo: this.trainsetInfo,
                      e,
                    });
                    e.preventDefault();
                    e.stopPropagation();
                  },
                  click: (e) => {
                    this.$emit('train-click', {
                      trainsetInfo: this.trainsetInfo,
                      e,
                    });
                    e.preventDefault();
                    e.stopPropagation();
                  },
                  dragstart: () => {
                    this.$emit('drag-start');
                  }
                },
                domProps: {
                  draggable: 'true'
                }
              }}
            >
              {[
                <div
                  class="train-body-1"
                  {...{
                    style: {
                      'background-image': `url(${this.urls[0]}),url(${this.standbyUrl[0]})`,
                    },
                  }}
                >
                  {[
                    <img
                      {...{
                        class: {
                          handImg: true,
                          'is-block': this.focusId == this.trainsetId,
                        },
                        attrs: {
                          width: '25px',
                          height: '25px',
                          src: this.baseImgPath + '/hand.png',
                        },
                      }}
                    />,
                  ]}
                </div>,
                <div
                  class="train-body-2"
                  {...{
                    style: {
                      'background-image': `url(${this.urls[1]}),url(${this.standbyUrl[1]})`,
                    },
                  }}
                >
                  {[
                    <el-tooltip {...{ attrs: { placement: 'right' } }}>
                      {[
                        <div slot="content">{this.tooltipContent}</div>,
                        <p class="body-content">
                          {[
                            <span class="body-name">
                              {this.trainsetInfo.trainsetName}
                            </span>,
                            <span
                              {...{
                                class: {
                                  'is-none':
                                    !this.nextInTime || this.showMoveTrainPlan,
                                },
                              }}
                            >
                              计划离开时间：
                            </span>,
                            <span
                              {...{
                                class: {
                                  'is-none':
                                    !this.nextInTime || this.showMoveTrainPlan,
                                  star: this.isStar(this.trainsetInfo.nextInTime),
                                },
                              }}
                            >
                              {this.nextInTime}
                            </span>,
                            <span
                              {...{
                                class: {
                                  'plan-postion': true,
                                  'is-none':
                                    !this.trainsetInfo.nextTrackPositionCode ||
                                    this.showMoveTrainPlan,
                                },
                              }}
                            >
                              {this.trainsetInfo.nextTrackName +
                                ' (' +
                                this.trainsetInfo.nextTrackPositionName +
                                ')'}
                            </span>,
                          ]}
                        </p>,
                      ]}
                    </el-tooltip>,
                  ]}
                </div>,
                <div
                  class="train-body-3"
                  {...{
                    style: {
                      'background-image': `url(${this.urls[2]}),url(${this.standbyUrl[2]})`,
                    },
                  }}
                ></div>,

                <div
                  {...{
                    class: {
                      'trainset-head': true,
                      'right-head': this.trainsetInfo.headDirection == '00',
                    },
                    on: {
                      click: () => {
                        this.$emit('train-head-change', this.trainsetInfo);
                      },
                    },
                  }}
                ></div>,
              ]}
            </div>,
            this.$scopedSlots['train-footer'] &&
              this.$scopedSlots['train-footer'](),

            <svg
              {...{
                style: {
                  height: '0px',
                  width: '0px',
                  position: 'fixed',
                  overflow: 'visible',
                },
              }}
            >
              {[
                <defs>
                  {[
                    <filter
                      {...{
                        attrs: {
                          id: 'mySvgFilter',
                          x: '-50%',
                          y: '-50%',
                          width: '200%',
                          height: '200%',
                        },
                      }}
                    >
                      {[
                        <feOffset
                          {...{
                            attrs: {
                              in: 'SourceGrahic',
                              result: 'sourceOut',
                              dx: '0',
                              dy: '0',
                            },
                          }}
                        />,
                        <feOffset
                          {...{
                            attrs: {
                              in: 'SourceAlpha',
                              result: 'offOut',
                              dx: '0',
                              dy: '0',
                            },
                          }}
                        />,
                        <feMorphology
                          {...{
                            attrs: {
                              in: 'offOut',
                              result: 'mOut',
                              radius: '2.5',
                              operator: 'dilate',
                            },
                          }}
                        />,
                        <feGaussianBlur
                          {...{
                            attrs: {
                              in: 'mOut',
                              result: 'blurOut',
                              stdDeviation: '4',
                            },
                          }}
                        />,
                        <feColorMatrix
                          {...{
                            attrs: {
                              in: 'blurOut',
                              result: 'finalOut',
                              values: '0 0 0 0 0 0 0 0 0 1 0 0 0 0 1 0 0 0 1 0',
                            },
                          }}
                        />,
                        <feMerge>
                          {[
                            <feMergeNode
                              {...{
                                attrs: {
                                  in: 'finalOut',
                                },
                              }}
                            />,
                            <feMergeNode
                              {...{
                                attrs: {
                                  in: 'sourceOut',
                                },
                              }}
                            />,
                          ]}
                        </feMerge>,
                      ]}
                    </filter>,
                  ]}
                </defs>,
              ]}
            </svg>,
          ]}
        </div>
      );
    },
  });
})();
