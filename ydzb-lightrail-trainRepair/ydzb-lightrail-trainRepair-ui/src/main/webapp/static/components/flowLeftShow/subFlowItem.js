Vue.component('subflow-item', {
  name: 'subflow-item',
  template: `
  <div class="subflow-item">
    <div class="arrow" :class="{'is-show':isArrowShow && !state_4}"></div>
    <div class="subflow-content" :class="{'state_1':state_1,'state_2':state_2,'state_3':state_3,'state_4':state_4,'is-focus':isFocus}" \@click="nodeClick">
      <p>{{subNode.name}}</p>
      <div class="flow-icon is-gear" :class="{'is-focus':isFocus}"></div>
    </div>
  </div>
  `,
  props: {
    subNode: {
      type: Object,
      default: () => ({}),
    },
    isFocus: {
      type: Boolean,
      default: false,
    },
    isArrowShow: {
      type: Boolean,
      default: true,
    },
  },
  computed: {
    couldDisposeRoleIds({ subNode }) {
      return subNode.couldDisposeRoleIds.lenth > 0;
    },
    finished({ subNode }) {
      return subNode.finish;
    },
    state_1({ finished, couldDisposeRoleIds }) {
      return finished && !couldDisposeRoleIds.lenth > 0;
    },
    state_2({ finished, couldDisposeRoleIds }) {
      return finished && couldDisposeRoleIds.lenth > 0;
    },
    state_3({ finished, couldDisposeRoleIds }) {
      return !finished && couldDisposeRoleIds.lenth > 0;
    },
    state_4({ finished, couldDisposeRoleIds }) {
      return !finished && !couldDisposeRoleIds.lenth > 0;
    },
  },
  created() {
    
  },
  methods: {
    nodeClick() {
      this.$emit('node-click', this.subNode.id);
    },
  },
});
