Vue.component('context-menu', {
  label: 'context-menu',
  template: `
    <ul class="contextmenu">
      <li
          v-for="item in menu"
          class="contextmenu__item"
          :key="item.value||item.label"
          :id="item.value||item.label"
          @mouseenter="contextmenuWrapStyle(item, $event)"
        >
          <div @click.stop="fnHandler(item)" class="button" :class="{'is-disabled':item.disabled}">
            <i v-if="iconShow" :class="item.icon"></i>
            <i class="el-icon-check" v-if="item.multiple&&posts.find(post=>post.value==item.value)"></i>
            <el-tooltip class="item" effect="dark" :content="item.label" :disabled="item.label.length < 10 || !Boolean(showTooltip)" placement="right">
              <span>{{item.label}}</span>
            </el-tooltip>
            <i
              class="el-icon-arrow-right"
              v-if="item.children && item.children.length>0"
            ></i>
            <context-menu
              v-if="item.children && item.children.length>0"
              :style="positionStyle"
              :iconShow="iconShow"
              :menu="item.children"
              :resolve="resolve"
              :posts="posts"
              :showTooltip="true"
              @select="onSubmenuSelect"
            ></context-menu>
          </div>
        </li>
    </ul>
  `,
  props: {
    iconShow: {
      // 是否显示icon
      type: Boolean,
      default: true,
    },
    menu: {
      // 最重要的列表,没有直接不显示
      type: Array,
      default() {
        return [];
      },
    },
    resolve: {
      // 点击menu按钮时执行的方法
      type: Function,
      default: function () {},
    },
    posts: {
      type: Array,
      default() {
        return [];
      },
    },
    showTooltip: {
      // 是否显示icon
      type: Boolean,
      default: false,
    }
  },

  data() {
    return {
      positionStyle: {},
    };
  },

  computed: {},

  methods: {
    contextmenuWrapStyle(item, e) {
      if (!item.children) return;
      let y = e.y;
      // 判断menu距离浏览器可视窗口底部距离，一面距离底部太近的时候，会导致menu被遮挡
      let menuHeight = 160; // 不能用scrollHeight，获取到的menu是上一次的menu宽高

      this.positionStyle = {
        top: (document.body.clientHeight - y < menuHeight ? document.body.clientHeight - y - menuHeight : 0) + 'px',
      };
    },

    onSubmenuSelect(item) {
      if (item.disabled) return;

      if (item.action == 'choosePostion') {
        if (this.posts.find((post) => post.value == item.value)) {
          this.$emit(
            'post-change',
            this.posts.filter((post) => post.value != item.value)
          );
        } else {
          this.$emit('post-change', [...this.posts, item]);
        }
      }

      this.$emit('select', item);
    },

    fnHandler(item) {
      if (item.children && item.children.length > 0) {
        return false;
      }
      if (item.disabled) return;

      this.$emit('select', item);
    },
  },
});
