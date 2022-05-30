Vue.component('position-wrapper', {
  name: 'position-wrapper',
  props: {
    positionInfo: {
      type: Object,
      default: () => ({}),
    },
    isActive: {
      type: Boolean,
      default: false,
    },
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
            'position-wrapper': true,
            'trainset-dragover': this.isActive,
          },
          on: {
            dragenter: (e) => {
              // 改变当前选择项颜色
              this.$emit('set-train-wrapper-active', {
                key: this.positionInfo.trackCode + '-' + this.positionInfo.trackPositionCode,
                active: true,
              });

              // 父背景
              this.$emit('set-train-wrapper-active', { key: this.positionInfo.trackCode, active: true });
            },
            dragleave: (e) => {
              // 重置所有
              this.$emit('reset-train-wrapper-active');
            },
            dragover: (e) => {
              e.preventDefault();
            },
            drop: (e) => {
              // 重置所有
              this.$emit('reset-train-wrapper-active');

              this.$emit('drop', {
                trackCode: this.positionInfo.trackCode,
                trackPositionCode: this.positionInfo.trackPositionCode,
              });
              e.preventDefault();
            },
            contextmenu: (e) => {
              this.$emit('empty-track-click', {
                targetTrackPostionInfo: {
                  trackCode: this.positionInfo.trackCode,
                  trackPositionCode: this.positionInfo.trackPositionCode,
                },
                e,
              });
              e.preventDefault();
            },
          },
        }}
      >
        {[this.$slots.default]}
      </div>
    );
  },
});
