var trainDetails = Vue.component('train-details', {
  name: 'train-details',
  data() {
    return {
      imgURL: `${ctxPath}/static/trainRepair/projection/workRepairmonitor/img/`,
      standbyUrl: '${ctxPath}/static/components/track/img/train/new-',
      errorTrainTypeUrlInfo: {},
    };
  },
  props: {
    status: {
      type: String,
      default: '1',
    },

    trackDetail: {
      type: Object,
      default: () => ({}),
    },

    processShow: {
      type: String,
      default: 'true',
    },
  },
  template: `
    <div>
      <div class="title" v-if="trackDetail&&trackDetail.queryProcessMonitorPlaList">
        <div class="title_Left">
          <div>{{trackDetail.trackName +'-'+ (trackDetail.queryProcessMonitorPlaList[0].headDirectionPla || trackDetail.queryProcessMonitorPlaList[0].trackPlaName)}}</div>
          <div :class="trackDetail.queryProcessMonitorPlaList[0].powerState=='1' ? 'isElectric' : 'noElectric'"></div>
          <div class="chu" v-if="false">出</div>
        </div>
        <div class="title_Right">
          <div class="chu" v-if="false">出</div>
          <div :class="trackDetail.queryProcessMonitorPlaList[0].powerState=='1' ? 'isElectric' : 'noElectric'"></div>
          <div>{{trackDetail.trackName +'-'+ (trackDetail.queryProcessMonitorPlaList[0].tailDirectionPla || trackDetail.queryProcessMonitorPlaList[0].trackPlaName)}}</div>
        </div>
      </div>

      <div class="content">
        <ul class="contentUl">
          <li class="contentLi" v-for="(item, idx) in trackDetail.queryProcessMonitorPlaList" :key="item.trackPlaCode">
            <div class="main" style="width: 100%; height: 100%;" v-if="item.trainsetId">
              <slot name="header">
                <div class="repairProject">
                  <ul>
                    <li>{{item.itemInfos}}</li>
                  </ul>
                  <img class="one" v-if="status && status != 2 && item.itemInfos" :src="imgURL + 'one.png'" alt="">
                  <img class="two" v-if="status && status != 1 && item.itemInfos" :src="imgURL + 'two.png'" alt="">
                </div>
              </slot>
              <div class="train">
                <div style="width: 85%;">
                  <img class="left" :src="item.headDirection == '01' ? workNameURL.one : workNameURL.zero" alt="">
                  <img class="right" :src="item.headDirection == '01' ? workNameURL.zero : workNameURL.one" alt="">
                  <div class="car">
                    <div class="trainsetName"><span>{{item.trainsetName}}</span><span v-if="item.outTime"> / </span><span v-if="item.outTime">{{item.outTime | timeStr}}</span></div>
                    <img :src="url" :class="'trainUrl_' + (index + 1)" @error="setErrorUrl(item.trainsetType)" v-for="(url, index) in getTrainUrlList(item.trainsetType)" alt="">
                  </div>
                </div>
              </div>
              <slot name="flow">
                <div class="flow">
                  <div v-if="item.showHeadLine">
                    <img src="${ctxPath}/static/trainRepair/projection/workRepairmonitor/img/line1.png" alt="">
                  </div>
                  <ul v-if="item.nodeShowList&&item.nodeShowList.length > 0">
                    <li v-for="node in item.nodeShowList" :key="node.id">
                      <el-tooltip class="item" effect="dark" :content="node.name" placement="top">
                        <div class="flowName" :class="{'state_1':state_1(node),'state_2':state_2(node),'state_3':state_3(node),'state_4':state_4(node), not_started: !item.flowId}" v-if="processShow == 'true'" \@click="nodeClick(item.trainsetId)"><span>{{node.name}}</span></div>
                        <div class="flowName" :class="{'state_1':state_1(node),'state_2':state_2(node),'state_3':state_3(node),'state_4':state_4(node), not_started: !item.flowId}" v-else><span>{{node.name}}</span></div>
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
              </slot>
              <div class="details">
                <slot name="fault">
                  <div class="fault" :class="{twoFault:status==2}">
                    <div class="faultName">故障:</div>
                    <ul class="faultPosition">
                      <li v-for="val in item.faultData.faultStateList" :key="val.carNo">
                        <div :class="{isLong: item.isLong == '1'}" \@click="faultList(item.trainsetId, val.carNo)">{{val.carNo}}</div>
                        <img :src="val.faultState | malfunction" alt="">
                      </li>
                    </ul>
                    <div class="faultComplete">
                      <span \@click="faultList(item.trainsetId, '')">
                        总计：{{item.faultData.faultDealCount}} / {{item.faultData.faultTotalCount}}
                      </span>
                    </div>
                  </div>
                </slot>
                <div class="task">
                  <slot name="content">
                    <div class="left" v-if="item.oneWorkerList">
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
                    <div class="left" v-if="item.workPacketList">
                      <ul class="secondRepair">
                        <li class="work" v-for="val in item.workPacketList" :key="val.packetCode">
                          <div class="workName">{{val.packetName}}</div>
                          <div class="percent">{{getWidth(val.packetEndCount,val.packetTotalCount)}}</div>
                          <div class="progress" id="progress"><div id="percent" :style="{width:getWidth(val.packetEndCount,val.packetTotalCount)}"><span></span></div></div>
                          <div class="percentage" \@click="taskDetailsList(item.trainsetName, item.trainsetId,val.packetCode, val.packetName)">{{val.packetEndCount}} / {{val.packetTotalCount}}</div>
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
                  </slot>

                  <slot name="right">
                    <div class="right">
                      <ul>
                        <li v-for="val in item.integrationList" :key="val.integrationName">
                          <span>{{val.integrationName}}</span>
                          <div :class="{finish: val.integrationState==1}"></div>
                        </li>
                      </ul>
                    </div>
                  </slot>
                </div>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  `,
  created() {},
  mounted() {},
  watch: {},
  computed: {
    workNameURL() {
      let imgURL = `${ctxPath}/static/trainRepair/projection/workRepairmonitor/img/`;
      return {
        // 车头/车尾
        zero: imgURL + '0-2.png',
        one: imgURL + '1-2.png',

        icoPackage: imgURL + 'ico-package.png',
      };
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
      return (node) => {
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

    // 二级修进度条宽度
    getWidth(complented, sum) {
      let percent = Math.round((complented / sum) * 100);
      return percent + '%';
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
  },
  filters: {
    // 截取列车离开时间(时分秒)
    timeStr(times) {
      var timearr = times.replace(' ', ':').replace(/\:/g, '-').split('-');
      var timestr = '' + timearr[3] + ':' + timearr[4] + ':' + timearr[5];
      return timestr;
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
