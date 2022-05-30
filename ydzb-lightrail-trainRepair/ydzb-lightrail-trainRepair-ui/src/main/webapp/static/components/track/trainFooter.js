let copyData = (data) => JSON.parse(JSON.stringify(data));

Vue.component('train-footer', {
  name: 'train-footer',
  template: `
  <div class="train-footer" >
    <div class="train-footer-title" v-if="nodeShowList.length>0">作业进度:</div>
    <img
      :src="baseImgPath+'/line1.png'"
      alt=""
      height="4px"
      style="margin-right: 5px"
      v-if="showHeadLine"
    />
    <div class="node-list" :class="{'is-last':index+1==nodeShowList.length}" v-for="(node,index) in nodeShowList" :key="node.id">
      <el-tooltip placement="top">
        <span slot="content">
          {{node.name}}
        </span>
        <div \@click="lookupNodeDealInfoBtn" class="train-word"  :class="{'state_1':state_1(node),'state_2':state_2(node),'state_3':state_3(node,index),'state_4':state_4(node), not_started: !nodeListId}">
          {{node.name | textFilter(fullScreen)}}
        </div>
      </el-tooltip>

      <img
        :src="baseImgPath+'/line3.png'"
        alt=""
        height="4px"
      />
    </div>

    <img
      :src="baseImgPath+'/line2.png'"
      alt=""
      height="4px"
      v-if="showFootLine"
    />
  </div> 
  `,
  props: {
    nodeList: {
      type: Array,
      default: () => [],
    },
    nodeListId: {
      type: String,
      default: '',
    },
    nodeId: {
      type: String,
      default: '',
    },
    isLong: {
      type: Boolean,
      default: false,
    },
    fullScreen: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      baseImgPath: ctxPath + '/static/trainRepair/trainMonitor/img',
    };
  },
  computed: {
    // 最后一个已完成的流程节点
    runingNode({ nodeList }) {
      let copyNodeList = copyData(nodeList);
      copyNodeList.reverse();
      return copyNodeList.find((item) => item.finished) || {};
    },
    // 最后一个已完成的流程节点的下标
    runingNodeIdx({ nodeList, runingNode }) {
      return nodeList.findIndex((node) => node.id == runingNode.id);
    },
    headNodeList({ runingNodeIdx, nodeList, isLong }) {
      if (isLong) {
        // 长编
        if (runingNodeIdx == -1) {
          return nodeList.slice(0, 10);
        } else {
          let startIdx = runingNodeIdx - 3;
          if (startIdx < 0) {
            startIdx = 0;
          }
          return nodeList.slice(startIdx, runingNodeIdx);
        }
      } else {
        // 短编
        if (runingNodeIdx == -1) {
          return nodeList.slice(0, 4);
        } else {
          return []
          // let startIdx = runingNodeIdx - 1;
          // if (startIdx < 0) {
          //   startIdx = 0;
          // }
          // return nodeList.slice(startIdx, runingNodeIdx);
        }
      }
    },
    footNodeList({ runingNodeIdx, nodeList, isLong }) {
      if (isLong) {
        // 长编
        if (runingNodeIdx == -1) {
          return [];
        } else {
          return nodeList.slice(runingNodeIdx + 1, runingNodeIdx + 7);
        }
      } else {
        // 短编
        if (runingNodeIdx == -1) {
          return [];
        } else {
          return nodeList.slice(runingNodeIdx + 1, runingNodeIdx + 4);
        }
      }
    },
    // 短编（4）（前零后三）
    // 长编（10）（前三后六）
    nodeShowList({ nodeList, headNodeList, footNodeList, runingNode, runingNodeIdx, isLong }) {
      if (runingNodeIdx == -1) {
        let undoneNode = nodeList.some((node) => {
          return !node.finished && node.finished == false;
        });
        // 判断是否有未打卡节点
        if (undoneNode) {
          // 都未打卡显示开头
          return headNodeList;
        } else {
          // 都打完卡显示结尾
          if (isLong) {
            return nodeList.slice(-10, nodeList.lenth);
          } else {
            return nodeList.slice(-4, nodeList.lenth);
          }
        }
      } else {
        if (isLong) {
          if (headNodeList.length < 3) {
            return nodeList.slice(0, 10);
          } else if (footNodeList.length < 6) {
            return nodeList.slice(-10);
          } else {
            return [...headNodeList, nodeList[runingNodeIdx], ...footNodeList];
          }
        } else {
          if (runingNodeIdx == -1) {
            return nodeList.slice(0, 4);
          } else if (footNodeList.length < 3) {
            return nodeList.slice(-4);
          } else {
            console.log(headNodeList, nodeList[runingNodeIdx], footNodeList)
            return [...headNodeList, nodeList[runingNodeIdx], ...footNodeList];
          }
        }
      }
    },

    showHeadLine({ nodeShowList, nodeList }) {
      if (nodeList.length == 0 || nodeShowList.length == 0) return false;
      if (nodeShowList[0].id == nodeList[0].id) {
        return false;
      } else {
        return true;
      }
    },

    showFootLine({ nodeShowList, nodeList }) {
      if (nodeList.length == 0 || nodeShowList.length == 0) return false;

      if (nodeShowList[nodeShowList.length - 1].id == nodeList[nodeList.length - 1].id) {
        return false;
      } else {
        return true;
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
        if (!this.nodeListId) {
          return false;
        } else {
          return !node.finished && node.couldDisposeRoleIds.length != 0;
        }
      };
    },
    state_4() {
      return (node) => {
        return !node.finished && node.couldDisposeRoleIds.length == 0;
      };
    },
  },
  watch: {
    // nodeList(val) {
    //   console.log('nodeList',val);
    // },
    // isLong(val){
    //   console.log('isLong',val);
    // },
    // nodeShowList(val) {
    // console.log(
    //   'nodeShowList',
    //   val
    // );
    // },
  },
  created() {},
  methods: {
    lookupNodeDealInfoBtn() {
      this.$emit('lookup-node-deal-info-btn');
    },
  },
  filters: {
    textFilter(text, fullScreen) {
      const textLimit = fullScreen ? 6 : 4;
      if (text.length > textLimit) {
        return text.trim().slice(0, textLimit) + '...';
      } else {
        return text;
      }
    },
  },
});
