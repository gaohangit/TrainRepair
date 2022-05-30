Vue.component('track-container', {
  name: 'track-container',
  props: {
    height:{
      type:String,
      default:'100%'
    }
  },
  computed: {},
  created() {},
  render(createElement) {
    let React = { createElement };
    return <div class="track-container" {...{
      style:{
        height:this.height
      }
    }}>{[this.$slots.default]}</div>;
  },
});
