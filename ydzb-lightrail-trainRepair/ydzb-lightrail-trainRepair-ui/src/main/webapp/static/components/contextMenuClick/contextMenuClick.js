Vue.component('right-menu', {
  name: 'right-menu',
  props: {
    list: {
      type: Object,
      default: () => ({
        title: '转线线区',
        level: 0,
        menu: [
          {
            icon: 'el-icon-circle-plus-outline',
            label: '插入上一个节点',
            value: 'addPrevNode',
            lastLevel: false,
          },
          {
            icon: 'el-icon-circle-plus-outline',
            label: '插入下一个节点',
            value: 'addNextNode',
            lastLevel: true,
          },
        ],
        children: {
          title: '转线股道列位',
          level: 1,
          menu: [
            {
              icon: 'el-icon-circle-plus-outline',
              label: '插入上一个节点',
              value: 'addPrevNode',
              lastLevel: true,
            },
            {
              icon: 'el-icon-circle-plus-outline',
              label: '插入下一个节点',
              value: 'addNextNode',
              lastLevel: true,
            },
          ],
        },
      }),
    },
    visible: {
      type: Boolean,
      default: false,
    },
    isShowIcon: {
      type: Boolean,
      default: false,
    },
    action: {
      type: Array,
      default: () => [],
    },
    active: {
      type: Object,
      default: () => ({}),
    },
    childVisible: {
      type: Object,
      default: () => ({}),
    },
    width: {
      type: Number,
      default: 180,
    },
    height: {
      type: Number,
      default: 250,
    },
    left: {
      type: Number,
      default: 0,
    },
    top: {
      type: Number,
      default: 0,
    },
    scrollHeight: {
      type: Number,
      default: 207,
    },

    trackAreaCodeTrackNameMap: {
      type: Object,
      default: () => ({}),
    },
    rightMenuType: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      showChild: false,
      // active: {},
      yGap: 0,
      searchWord: '',
      baseImgPath: ctxPath + '/static/trainRepair/trainMonitor/img/',
    };
  },
  computed: {
    childStyle() {
      return {
        left: this.left + this.width,
        top: this.top + this.yGap,
      };
    },
    menu() {
      if (this.list.menu) {
        if (this.$parent.searchWord) {
          if (this.list.menu.filter((item) => item.label.includes(this.$parent.searchWord)).length > 0) {
            return this.list.menu.filter((item) => item.label.includes(this.$parent.searchWord));
          } else {
            return this.list.menu;
          }
        } else {
          if (this.list.filterable) {
            if (this.list.menu.filter((item) => item.label.includes(this.searchWord)).length > 0) {
              return this.list.menu.filter((item) => item.label.includes(this.searchWord));
            } else {
              return this.list.menu.filter((item) =>
                this.trackAreaCodeTrackNameMap[item.value].some((itm) => String(itm).includes(this.searchWord))
              );
            }
          } else {
            return this.list.menu;
          }
        }
      } else {
        return [];
      }
    },
    realScrollHeight() {
      return this.list.filterable ? 170 : this.scrollHeight;
    },
  },
  watch: {
    left: {
      handler(val) {
        if (val + this.width > document.body.clientWidth) {
          // 改变最外层的left值
          this.$emit('root-left-change', document.body.clientWidth - this.width * (this.list.level + 1));
        }
      },
      immediate: true,
    },
    top: {
      handler(val) {
        let y = val + this.height - document.body.clientHeight;
        if (y > 0) {
          // 改变最外层的top值
          this.$emit('root-top-change', y);
        }
      },
      immediate: true,
    },

    // menu: {
    //   handler(menu) {
    //     if (menu) {
    //       menu.forEach((item, index) => {
    //         this.$emit('set-active', { key: this.list.level + '' + index, value: false });
    //       });
    //     }
    //   },
    //   immediate: true,
    // },
  },

  created() {},
  updated() {},
  render(createElement) {
    let React = { createElement };
    return (
      <div
        {...{
          class: {
            'contextmenu-wrap': true,
            'is-none': !this.visible,
          },
          style: {
            width: this.width + 'px',
            height: this.height + 'px',
            left: this.left + 'px',
            top: this.top + 'px',
          },
        }}
      >
        {[
          <div class="contextmenu-title">{this.list.title}</div>,
          (() => {
            if (this.list.filterable) {
              return (
                <div
                  class="menu-input"
                  {...{
                    style: {
                      padding: '0 10px',
                    },
                  }}
                >
                  {[
                    <el-input
                      {...{
                        props: {
                          size: 'mini',
                          placeholder: '请输入关键词',
                          value: this.searchWord,
                        },
                        style: {
                          'margin-bottom': '10px',
                        },
                        on: {
                          input: (inputValue) => {
                            this.searchWord = inputValue;
                            this.$emit('reset-active');
                            this.$emit('reset-child-visible', this.list.level);
                          },
                        },
                      }}
                    >
                      {[
                        <img
                          {...{
                            slot: 'suffix',
                            attrs: {
                              src: this.baseImgPath + '/ico_search.png',
                            },
                            style: {
                              cursor: 'pointer',
                              position: 'absolute',
                              right: '0',
                              top: '50%',
                              transform: 'translateY(-50%)',
                            },
                          }}
                        />,
                      ]}
                    </el-input>,
                  ]}
                </div>
              );
            } else {
              return [];
            }
          })(),
          <el-scrollbar
            {...{
              style: {
                height: this.realScrollHeight + 'px',
              },
            }}
          >
            {[
              <ul>
                {[
                  this.menu.map((item, index) => {
                    return (
                      <li>
                        {[
                          <el-tooltip
                            {...{
                              attrs: {
                                placement: 'right-start',
                              },
                              props: {
                                disabled: !item.disabled,
                                content: item.tooltipContent,
                              },
                            }}
                          >
                            {[
                              <div
                                {...{
                                  class: {
                                    'menu-button': true,
                                    'is-active': this.active[this.list.level + '' + index],
                                    'is-disabled': item.disabled,
                                  },
                                  on: {
                                    click: (e) => {
                                      if (item.disabled) return;
                                      // 颜色
                                      // 重置同级颜色
                                      this.$emit('reset-active');

                                      // 改变当前选择项颜色
                                      this.$emit('set-active', { key: this.list.level + '' + index, value: true });
                                      this.$emit('set-action', { level: this.list.level, value: item.value });
                                      if (item.lastLevel) {
                                        // 重置
                                        // 所有颜色
                                        this.$emit('reset-active');
                                        this.$emit('reset-child-visible');
                                        this.$emit('menu-select', this.list.level);
                                      } else {
                                        this.yGap = e.y - this.top;
                                        // 下级菜单触发
                                        this.$emit('children-change', { action: this.action, level: this.list.level });
                                        // 显示下一级
                                        this.$emit('set-child-visible', { key: this.list.level, value: true });
                                      }
                                    },
                                  },
                                }}
                              >
                                {[
                                  <i
                                    {...{
                                      class: {
                                        [item.icon]: this.isShowIcon,
                                      },
                                    }}
                                  ></i>,
                                  <span>{item.label}</span>,
                                  <i
                                    {...{
                                      class: {
                                        'el-icon-caret-right': true,
                                        'is-hidden': item.lastLevel,
                                      },
                                    }}
                                  ></i>,
                                ]}
                              </div>,
                            ]}
                          </el-tooltip>,
                        ]}
                      </li>
                    );
                  }),
                ]}
              </ul>,
            ]}
          </el-scrollbar>,

          (() => {
            if (this.list.children && Object.keys(this.list.children).length > 0) {
              return (
                <right-menu
                  {...{
                    props: {
                      list: this.list.children,
                      visible: !!this.childVisible[this.list.level],
                      left: this.childStyle.left,
                      top: this.childStyle.top,
                      action: this.action,
                      active: this.active,
                      trackAreaCodeTrackNameMap: this.trackAreaCodeTrackNameMap,
                      'child-visible': this.childVisible,
                    },
                    on: {
                      'root-left-change': (x) => {
                        this.$emit('root-left-change', x);
                      },
                      'root-top-change': (y) => {
                        this.$emit('root-top-change', y);
                      },
                      'set-active': ({ key, value }) => {
                        this.$emit('set-active', { key, value });
                      },
                      'reset-active': () => {
                        this.$emit('reset-active');
                      },
                      'set-child-visible': ({ key, value }) => {
                        this.$emit('set-child-visible', { key, value });
                      },
                      'reset-child-visible': () => {
                        this.$emit('reset-child-visible');
                      },
                      'set-action': ({ level, value }) => {
                        this.$emit('set-action', { level, value });
                      },
                      'menu-select': (level) => {
                        this.$emit('menu-select', level);
                      },
                      'children-change': ({ action, level }) => {
                        this.$emit('children-change', { action, level });
                      },
                    },
                  }}
                ></right-menu>
              );
            }
          })(),
        ]}
      </div>
    );
  },
});
