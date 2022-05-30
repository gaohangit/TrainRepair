Vue.component('flow-item', {
  name: 'flow-item',
  template: `
  <div class="flow-item">
    <div class="arrow-btn" :class="{'is-show':node.children.length>0}">
      <div :class="{'is-open':isOpen}" \@click="isOpen = !isOpen"></div>
    </div>
    <div class="arrow" :class="{'is-show':isArrowShow && !state_4}"></div>
    <el-tooltip placement="top" v-if="nodeNetalis">
      <div slot="content" v-html="nodeNetalis"></div>
      <div class="flow-content" :class="{'state_1':state_1,'state_2':state_2,'state_3':state_3,'state_4':state_4,'is-focus':isFocus}" \@click="nodeClick">
        <p>{{node.name}}</p>
        <div class="flow-icon is-gear" :class="{'is-focus':isFocus}"></div>
      </div>
    </el-tooltip>
    <div class="flow-content" v-if="!nodeNetalis" :class="{'state_1':state_1,'state_2':state_2,'state_3':state_3,'state_4':state_4,'is-focus':isFocus}" \@click="nodeClick">
      <p>{{node.name}}</p>
      <div class="flow-icon is-gear" :class="{'is-focus':isFocus}"></div>
    </div>
    <slot :data="isOpen"></slot>
  </div>
  `,
  props: {
    node: {
      type: Object,
      default: () => ({}),
    },
    isArrowShow: {
      type: Boolean,
      default: true,
    },
    isFocus: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      isOpen: false,
    };
  },
  computed: {
    disposeSubflow({ node }) {
      return Boolean(node.disposeSubflow);
    },
    couldDisposeRoleIds({ node }) {
      return node.couldDisposeRoleIds;
    },
    finished({ node }) {
      return node.finished;
    },
    latestChild({ node }) {
      let copyArr = copyData(node.children);
      copyArr.reverse();
      return copyArr.find((item) => !(!item.finished && !item.couldDisposeRoleIds.length > 0));
    },
    state_1({ disposeSubflow, latestChild, finished, couldDisposeRoleIds }) {
      return disposeSubflow ? latestChild.finished && !latestChild.couldDisposeRoleIds.length  > 0 : finished && !couldDisposeRoleIds.length > 0;
    },
    state_2({ disposeSubflow, latestChild, finished, couldDisposeRoleIds }) {
      return disposeSubflow ? latestChild.finished && latestChild.couldDisposeRoleIds.length  > 0 : finished && couldDisposeRoleIds.length > 0;
    },
    state_3({ disposeSubflow, latestChild, finished, couldDisposeRoleIds }) {
      return disposeSubflow ? !latestChild.finished && latestChild.couldDisposeRoleIds.length  > 0 : !finished && couldDisposeRoleIds.length > 0;
    },
    state_4({ disposeSubflow, latestChild, finished, couldDisposeRoleIds }) {
      return disposeSubflow ? !latestChild.finished && !latestChild.couldDisposeRoleIds.length  > 0 : !finished && !couldDisposeRoleIds.length  > 0;
    },
    nodeNetalis({ node }) {
      return node.nodeRecords
        ? node.nodeRecords
            .map((record) => {
              return `打卡者：${record.workerName} 打卡角色：${record.roleName} 打卡时间：${record.recordTime},<br/>`;
            })
            .join('')
        : '';
    },
  },
  created() {
    this.isOpen = false;
  },
  methods: {
    nodeClick() {
      this.$emit('node-click', this.node.id);
    },
  },
});
