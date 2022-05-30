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
Vue.component('single-train', {
  name: 'single-train',
  template: `
    <div class="train-container">
      <div class="position-title left-position">
        <div class="track-position-name">{{train.trackName +'-'+ train.headDirectionPla}}</div>
        <div class="power-state" :class="{danger:powerInfo[train.headDirectionPlaCode]&&powerInfo[train.headDirectionPlaCode].state == '1'}"></div>
      </div>
      <div class="position-title right-position">
        <div class="track-position-name">{{train.trackName +'-'+ train.tailDirectionPla}}</div>
        <div class="power-state" :class="{danger:powerInfo[train.tailDirectionPlaCode]&&powerInfo[train.tailDirectionPlaCode].state == '1'}"></div>
      </div>
      <div class="trainset-body">
        <div class="train-body-1" :style="{'background-image':'url('+urls[0]+'),url('+standbyUrl[0]+')'}"></div>
        <div class="train-body-2" :style="{'background-image':'url('+urls[1]+'),url('+standbyUrl[1]+')'}">
          <p class="body-content">
            <span class="body-name">{{train.trainsetName}}</span>
          </p>
        </div>
        <div class="train-body-3" :style="{'background-image':'url('+urls[2]+'),url('+standbyUrl[2]+')'}"></div>
        <div class="trainset-head" :class="{'right-head':train.headDirection=='00'}"></div>
      </div>
      <slot></slot>
    </div>
  `,
  props: {
    train:{
      type:Object,
      default:()=>({})
    },
    powerInfo:{
      type:Object,
      default:()=>({})
    }
  },
  data() {
    return {
      baseImgPath: ctxPath + '/static/trainRepair/trainMonitor/img',
      urls: [],
      trainsetListReceived: res || [],
      standbyUrl: [
        `${ctxPath}/static/components/track/img/train/new-1.png`,
        `${ctxPath}/static/components/track/img/train/new-2.png`,
        `${ctxPath}/static/components/track/img/train/new-3.png`,
      ],
    };
  },
  computed:{
    trainsetIdTypeMap({ trainsetListReceived }) {
      return trainsetListReceived.reduce((prev, item) => {
        prev[item.trainsetid] = item.traintype;
        return prev;
      }, {});
    },
    trainType({ trainsetIdTypeMap, train }) {
      return trainsetIdTypeMap[train.trainsetId];
    },
  },
  watch: {},
  created(){
      this.urls = getTrainUrls(this.trainType);
  },
  methods: {},
});

})();