Vue.component('column-draggable', {
  name: 'column-draggable',
  template: `
  <!--使用draggable组件-->
  <div class="drag-container clearFix">
      <div class="drag-wrapper">
          <div class="wrapper-title">隐藏列</div>
          <draggable tag="ul" v-model="leftArr" group="site" animation="300" dragClass="dragClass" chosenClass="chosenClass" @start="onStart" @end="onEnd">
              <transition-group>
                  <li class="drag-item" v-for="item in leftArr" :key="item.key">{{item.label}}</li>
              </transition-group>
          </draggable>
      </div>
      <div class="drag-wrapper">
          <div class="wrapper-title">显示列</div>
          <draggable tag="ul"  v-model="rightArr" group="site" animation="100" dragClass="dragClass" chosenClass="chosenClass" @start="onStart" @end="onEnd">
              <transition-group>
                  <li class="drag-item" v-for="item in rightArr" :key="item.key">{{item.label}}</li>
              </transition-group>
          </draggable>
      </div>
  </div>
  `,
  props: {
    leftList: {
      type: Array,
      default: () => [],
    },
    rightList: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      drag: false,
      leftArr: [],
      rightArr:[],
    };
  },
  computed: {},
  watch: {
    leftList: {
      handler(val) {
        this.leftArr = val;
      },
      immediate: true,
    },
    rightList: {
      handler(val) {
        this.rightArr = val;
      },
      immediate: true,
    },
  },
  methods: {
    onStart() {
      this.drag = true;
    },
    onEnd() {
      this.drag = false;
      this.$emit('column-change', { left: this.leftArr, right: this.rightArr });
    },
  },
});
