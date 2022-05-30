var trackDetails = Vue.component('track-details', {
  name: 'track-details',
  data() {
    return {
      imgURL: `${ctxPath}/static/trainRepair/projection/workRepairmonitor/img/`,
      imgObj: {},
      imageTypeObj: {},
      standbyUrl: `${ctxPath}/static/components/track/img/train/new-`,
      errorTrainTypeUrlInfo: {},
    };
  },
  props: {
    jurisdiction: {
      type: String,
    },
    fullscreen: {
      type: Boolean,
    },
    trackDetail: {
      type: Object,
      default: () => ({}),
    },
    showTask: {
      type: String,
    },
  },
  template: `
    <div>
      <div class="title" v-if="trackDetail.queryProcessMonitorPlaList[0] && trackDetail.queryProcessMonitorPlaList[0].isLong=='1'">
        <div class="title_Left">
          <div>{{trackDetail.trackName +'-'+ trackDetail.queryProcessMonitorPlaList[0].headDirectionPla}}</div>
          <div :class="trackDetail.queryProcessMonitorPlaList[0].powerState=='1' ? 'isElectric' : 'noElectric'"></div>
          <div class="chu" v-if="false">出</div>
        </div>
        <div class="title_Right">
          <div class="chu" v-if="false">出</div>
          <div :class="trackDetail.queryProcessMonitorPlaList[0].powerState=='1' ? 'isElectric' : 'noElectric'"></div>
          <div>{{trackDetail.trackName +'-'+ trackDetail.queryProcessMonitorPlaList[0].tailDirectionPla}}</div>
        </div>
      </div>
      <div class="title" v-else>
        <div :class="{title_Left:index==0,title_Right:index==1}" v-for="(item,index) in trackDetail.queryProcessMonitorPlaList" :key="item.trackPlaCode">
          <div class="chu" v-if="false">出</div>
          <div :class="item.powerState=='1' ? 'isElectric' : 'noElectric'" v-if="index==1"></div>
          <div>{{trackDetail.trackName +'-'+ item.trackPlaName}}</div>
          <div :class="item.powerState=='1' ? 'isElectric' : 'noElectric'" v-if="index==0"></div>
          <div class="chu" v-if="false">出</div>
        </div>
      </div>
      <div class="content">
        <ul class="contentBox">
          <li :class="{trainlength: item.isLong == 1, left: idx == 0 && item.isLong == 0, right: idx == 1 && item.isLong == 0}" v-for="(item, idx) in trackDetail.queryProcessMonitorPlaList" :key="item.trackPlaCode">
            <div style="width: 100%;" v-if="item.trainsetId">
              <div class="repairProject">
                <ul>
                  <li>{{item.itemInfos}}</li>
                </ul>
                <img class="one" v-if="jurisdiction != 2 && item.itemInfos" \@click="trainRepairCut(idx, item.trackPlaCode, trackDetail.trackCode, '1','iconOneUrl')" :src="imgObj[item.trackPlaCode].iconOneUrl" alt="">
                <img class="two" v-if="jurisdiction != 1 && item.itemInfos" \@click="trainRepairCut(idx, item.trackPlaCode, trackDetail.trackCode, '2','iconTwoUrl')" :src="imgObj[item.trackPlaCode].iconTwoUrl" alt="">
              </div>
              <div class="train">
                <div style="width: 100%;">
                  <img class="left" :src="item.headDirection == '01' ? workNameURL.one : workNameURL.zero" alt="">
                  <img class="right" :src="item.headDirection == '01' ? workNameURL.zero : workNameURL.one" alt="">
                  <div class="car">
                    <div class="trainsetName">
                      <span>{{item.trainsetName}}</span>
                      <span v-if="item.nextInTime">　计划离开时间：</span>
                      <span v-if="item.nextInTime">{{getMoveTrainPlanTime(item)}}</span>
                      <span v-if="item.nextTrackName">　{{item.nextTrackName + ' (' + item.nextTrackPositionName + ')'}}</span>
                    </div>
                    <img :src="url" :class="'trainUrl_' + (index + 1)" @error="setErrorUrl(item.trainsetType)" :style="{width:trainBody(index, item.isLong)}" v-for="(url, index) in getTrainUrlList(item.trainsetType)" alt="">
                  </div>
                </div>
              </div>
              <div class="flow">
                <div v-if="item.showHeadLine">
                  <img src="${ctxPath}/static/trainRepair/projection/workRepairmonitor/img/line1.png" alt="">
                </div>
                <ul v-if="item.nodeShowList&&item.nodeShowList.length > 0">
                  <li v-for="(node, nodeIndex) in item.nodeShowList" :key="node.id">
                    <el-tooltip class="item" effect="dark" :content="node.name" placement="top">
                      <div class="flowName" :class="{'state_1':state_1(node),'state_2':state_2(node),'state_3':state_3(node),'state_4':state_4(node), not_started: !item.flowId}" \@click="nodeClick(item.trainsetId)"><span>{{node.name}}</span></div>
                    </el-tooltip>
                    <div>
                      <img src="${ctxPath}/static/trainRepair/projection/workRepairmonitor/img/line2.png" alt="">
                    </div>
                  </li>
                </ul>
                <div v-if="item.showFootLine">
                  <img src="${ctxPath}/static/trainRepair/projection/workRepairmonitor/img/line3.png" alt="">
                </div>
              </div>
              <div class="details">
                <div class="fault" :class="{twoFault:jurisdiction==2}">
                  <div class="faultName">故障:</div>
                  <ul class="faultPosition">
                    <li v-for="val in item.faultData.faultStateList" :key="val.carNo">
                      <div \@click="faultList(item.trainsetId, val.carNo)">{{val.carNo}}</div>
                      <img :src="val.faultState | malfunction" alt="">
                    </li>
                  </ul>
                  <div class="faultComplete">
                    <span \@click="faultList(item.trainsetId, '')">
                      总计：{{item.faultData.faultDealCount}} / {{item.faultData.faultTotalCount}}
                    </span>
                  </div>
                </div>
                <div class="task">
                  <div class="left">
                    <div v-if="item.oneWorkerList">
                      <ul class="firstRepair">
                        <li class="people"  v-for="val in item.oneWorkerList" :key="val.workerId">
                          <div class="PeopleName">
                            <span>{{val.workerName}}</span>
                          </div>
                          <ul class="work">
                            <li class="workDetails" v-for="items in val.oneItemList" :key="items.oneItemCode">
                              <div class="workName">
                                <img :src="workNameURL.icoPackage" alt="">
                                <span :class="{overTime: items.oneItemState == 1, over: items.oneItemState == 2}">{{items.oneItemName}}</span>
                              </div>
                              <ul class="quantity">
                                <li :class="{completed: obj.carNoState == 2, under: obj.carNoState == 1}" v-for="obj in items.oneItemCarNoStateList" :key="obj.carNo">{{obj.carNo}}</li>
                              </ul>
                            </li>
                          </ul>
                        </li>
                      </ul>
                    </div>
                    <div v-if="item.workPacketList">
                      <ul class="secondRepair">
                        <li class="work twoWorkPacket" v-for="val in item.workPacketList" :key="val.packetCode" \@click="taskDetailsList(item.trainsetName, item.trainsetId,val.packetCode, val.packetName)">
                          <div class="workName">{{val.packetName}}</div>
                          <div class="percent">{{getWidth(val.packetEndCount,val.packetTotalCount)}}</div>
                          <div class="progress" id="progress"><div id="percent" :style="{width:getWidth(val.packetEndCount,val.packetTotalCount)}"><span></span></div></div>
                          <div class="percentage">{{val.packetEndCount}} / {{val.packetTotalCount}}</div>
                        </li>
                      </ul>
                      <div class="personnel">
                        <ul v-if="false">
                          <li>张三</li>
                          <li>李四</li>
                          <li>王五</li>
                          <li>赵六</li>
                          <li>张三</li>
                          <li>李四</li>
                          <li>王五</li>
                          <li>赵六</li>
                        </ul>
                      </div>
                    </div>
                  </div>
                  <div class="right">
                    <ul>
                      <li v-for="val in item.integrationList" :key="val.integrationName">
                        <span>{{val.integrationName}}</span>
                        <div :class="{finish: val.integrationState==1}"></div>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  `,
  created() {
    this.initImageUrl();
  },
  mounted() {},
  watch: {
    fullscreen() {
      this.trackDetail.queryProcessMonitorPlaList.forEach((train) => {
        this.imageChange(train.trackPlaCode, this.imageTypeObj[train.trackPlaCode]);
      });
    },
    showTask() {
      this.trackDetail.queryProcessMonitorPlaList.forEach((train) => {
        this.imageChange(train.trackPlaCode, this.imageTypeObj[train.trackPlaCode]);
      });
    },
    // trackDetail: {
    //   handler(val) {
    //     this.dateObj = val
    //   },
    //   immediate: true
    // }
  },
  computed: {
    workNameURL() {
      let imgURL = `${ctxPath}/static/trainRepair/projection/workRepairmonitor/img/`;
      if (this.fullscreen) {
        return {
          // 车头/车尾
          zero: imgURL + '0-2.png',
          one: imgURL + '1-2.png',

          icoPackage: imgURL + 'ico-package.png',
        };
      } else {
        return {
          // 车头/车尾
          zero: imgURL + '0-3.png',
          one: imgURL + '1-3.png',
          icoPackage: imgURL + 'ico-package-1.png',
        };
      }
    },

    state_1() {
      return (node) => {
        return node.finished && node.couldDisposeRoleIds.length == 0;
      };
    },
    state_2() {
      return (node) => {
        return node.finished && node.couldDisposeRoleIds.length != 0;
      };
    },
    state_3() {
      return (node, index) => {
        return !node.finished && node.couldDisposeRoleIds.length > 0;
      };
    },
    state_4() {
      return (node) => {
        return !node.finished && node.couldDisposeRoleIds.length == 0;
      };
    },
  },
  methods: {
    setErrorUrl(trainType) {
      this.errorTrainTypeUrlInfo[trainType] = true;
    },
    // 列车图片
    getTrainUrlList(trainType) {
      if (this.errorTrainTypeUrlInfo[trainType]) {
        return [this.standbyUrl + '1.png', this.standbyUrl + '2.png', this.standbyUrl + '3.png'];
      } else {
        return getTrainUrls(trainType);
      }
    },

    // 根据是否为长编控制车体宽度
    trainBody(index, isLong) {
      if (index == 1) {
        if (isLong == 1) {
          return '60vh';
        } else {
          return '30vh';
        }
      }
    },

    swichImageUrl() {
      Object.keys(this.imageTypeObj).forEach((item) => {
        this.$set(this.imageTypeObj, item, this.imageTypeObj[item] == 'iconOneUrl' ? 'iconTwoUrl' : 'iconOneUrl');
      });
      this.trackDetail.queryProcessMonitorPlaList.forEach((train) => {
        this.imageChange(train.trackPlaCode, this.imageTypeObj[train.trackPlaCode]);
      });
    },

    initImageUrl() {
      this.trackDetail.queryProcessMonitorPlaList.forEach((train) => {
        if (this.jurisdiction == 1) {
          this.setImageUrl(train.trackPlaCode, this.imgURL + 'one1.png', '', 'iconOneUrl');
        } else if (this.jurisdiction == 2) {
          this.setImageUrl(train.trackPlaCode, '', this.imgURL + 'two1.png', 'iconTwoUrl');
        } else if (this.jurisdiction == 'all') {
          if (this.showTask == 1) {
            this.setImageUrl(train.trackPlaCode, this.imgURL + 'one1.png', this.imgURL + 'two1-1.png', 'iconOneUrl');
          } else if (this.showTask == 2) {
            this.setImageUrl(train.trackPlaCode, this.imgURL + 'one1-1.png', this.imgURL + 'two1.png', 'iconTwoUrl');
          } else if (this.showTask == 3) {
            if (this.fullscreen) {
              this.setImageUrl(train.trackPlaCode, this.imgURL + 'one.png', this.imgURL + 'two-1.png', 'iconOneUrl');
            } else {
              this.setImageUrl(train.trackPlaCode, this.imgURL + 'one1.png', this.imgURL + 'two1-1.png', 'iconOneUrl');
            }
          }
        }
      });
    },

    setImageUrl(trackPlaCode, iconOneUrl, iconTwoUrl, imageType) {
      this.$set(this.imgObj, trackPlaCode, {});
      this.$set(this.imgObj[trackPlaCode], 'iconOneUrl', iconOneUrl);
      this.$set(this.imgObj[trackPlaCode], 'iconTwoUrl', iconTwoUrl);

      this.$set(this.imageTypeObj, trackPlaCode, imageType);
    },

    // 二级修进度条宽度
    getWidth(complented, sum) {
      let percent = Math.round((complented / sum) * 100);
      return percent + '%';
    },

    imageChange(trackPlaCode, imageType) {
      let imgURL = `${ctxPath}/static/trainRepair/projection/workRepairmonitor/img/`;
      if (this.jurisdiction == 'all') {
        if (this.fullscreen) {
          if (imageType && imageType == 'iconOneUrl') {
            this.$set(this.imgObj[trackPlaCode], 'iconOneUrl', imgURL + 'one.png');
            this.$set(this.imgObj[trackPlaCode], 'iconTwoUrl', imgURL + 'two-1.png');
          }
          if (imageType && imageType == 'iconTwoUrl') {
            this.$set(this.imgObj[trackPlaCode], 'iconOneUrl', imgURL + 'one-1.png');
            this.$set(this.imgObj[trackPlaCode], 'iconTwoUrl', imgURL + 'two.png');
          }
        } else {
          if (imageType && imageType == 'iconOneUrl') {
            this.$set(this.imgObj[trackPlaCode], 'iconOneUrl', imgURL + 'one1.png');
            this.$set(this.imgObj[trackPlaCode], 'iconTwoUrl', imgURL + 'two1-1.png');
          }
          if (imageType && imageType == 'iconTwoUrl') {
            this.$set(this.imgObj[trackPlaCode], 'iconOneUrl', imgURL + 'one1-1.png');
            this.$set(this.imgObj[trackPlaCode], 'iconTwoUrl', imgURL + 'two1.png');
          }
        }
      }
    },

    // 点击列车图片上方一/二级修图片
    trainRepairCut(index, trackPlaCode, trackCode, num, imageType) {
      if (this.jurisdiction == 'all') {
        this.imageTypeObj[trackPlaCode] = imageType;
        this.imageChange(trackPlaCode, this.imageTypeObj[trackPlaCode]);
        let obj = {
          index,
          trackPlaCode,
          trackCode,
          num,
        };

        this.$emit('train-repair-cut', obj);
      }
    },

    // 点击故障总数
    faultList(trainsetId, carNo) {
      let obj = {
        trainsetId,
        carNo,
      };
      this.$emit('fault-list', obj);
    },

    // 点击作业包总数
    taskDetailsList(trainsetName, trainsetId, packetCode, packetName) {
      var obj = {
        trainsetName,
        trainsetId,
        packetCode,
        packetName,
      };
      this.$emit('task-details-list', obj);
    },

    nodeClick(trainsetId) {
      this.$emit('node-click', trainsetId);
    },

    // 获取调车计划信息
    getMoveTrainPlanTime(item) {
      let timearr = ''
      if (item.nextInTime) {
        timearr = item.nextInTime.replace(' ', ':').replace(/\:/g, '-').split('-');
        timestr = timearr[3] + ':' + timearr[4] + ':' + timearr[5];
      }
      return timestr;
    },
  },
  filters: {
    // 车图片
    train(val) {
      let imgURL = `${ctxPath}/static/trainRepair/projection/workRepairmonitor/img/`;
      if (val == 1) {
        return imgURL + 'train1-1.png';
      } else {
        return imgURL + 'train1.png';
      }
    },

    // 故障背景图
    malfunction(val) {
      let imgURL = `${ctxPath}/static/trainRepair/projection/workRepairmonitor/img/`;
      if (val == 2) {
        return imgURL + 'error1-1.png';
      } else {
        return imgURL + 'error1.png';
      }
    },
  },
  beforeDestroy() {},
});
