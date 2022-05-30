// <input type="text" placeholder="请输入关键词进行过滤" class="lp-input__inner filter-input" v-model="filterValue"  />
Vue.component('lp-select', {
  name: 'lp-select',
  template: `
  <div ref="lpSelect" class="lp-select" :class="{'is-focus':isFocus,'is-disabled':disabled}">
    <div  @click.stop="inputFocus" @mouseover="mouseover" @mouseout="mouseout">
      <input :title="checkValueShow" type="text" disabled="disabled" placeholder="请选择" class="lp-input__inner" :value="checkValueShow" readonly="true" />
      <span class="lp-input__suffix">
        <i
          class="lp-icon el-icon-circle-close"
          v-if="clearable&&!disabled&&edit"
          @click.stop="clearValue"
        ></i>
        <i
          class="lp-icon el-icon-arrow-down"
          :class="{'is-reverse':isReverse}"
          v-else
        ></i>
      </span>
    </div>
    <transition name="el-zoom-in-top">
      <div class="lp-select-dropdown" v-show="dropdownShow">
        <el-input v-if="list.length>0" class="filter-input" v-model.trim="filterValue" placeholder="请输入关键词进行过滤" :clearable="true"></el-input>
        <el-scrollbar class="lp-scrollbar"  v-if="finalList.length>0">
          <ul v-if="edit">
            <li>
              <label>
                <input type="checkbox" class="lp-checkbox" v-model="selectAll">
                <span>{{selectAllText}}</span>
              </label>
            </li>
            
            <li v-for="item in finalList" :key="item[id]">
              <label>
                <input name="lp" type="checkbox" class="lp-checkbox" :value="value ? item[value] : item" v-model="checkValue" \@change="checkboxChange">
                <span>{{item[label]}}</span>
              </label>
              <input type="number" v-if="hasnum" :class="{'is-disabled':isNumDisabled(value ? item[value] : item)}" :disabled="isNumDisabled(value ? item[value] : item)" class="sub-input" :value="getRoleNum(value ? item[value] : item)" \@input="setRoleNum(value ? item[value] : item, $event)">
            </li>
          </ul>
          <ul v-else>
          <li>
          <li v-for="item in finalList" :key="item[id]">
            <label style="cursor: default">
              <span>{{item[label]}}</span>
            </label>
          </li>
        </ul>

        </el-scrollbar>
        <p v-else class="lp-select-dropdown__empty">
          无数据
        </p>
        <div class="lp_popper__arrow"></div>
      </div>
    </transition>
  </div>
  `,
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
    inputValue: {
      type: Array,
      default: () => [],
    },
    list: {
      type: Array,
      default: () => [],
    },
    id: {
      type: String,
      default: 'id',
    },
    label: {
      type: String,
      default: 'label',
    },
    value: {
      type: String,
      default: '',
    },
    mutiple: {
      type: Boolean,
      default: false,
    },
    rolenum: {
      type: Object,
      default: () => {},
    },
    hasnum: {
      type: Boolean,
      default: false,
    },
    flag: {
      type: String,
      default: '',
    },
    showText: {
      type: String,
      default: '',
    },
    edit: {
      type: Boolean,
      default: true,
    }
  },
  model: {
    prop: 'inputValue',
    event: 'lpChange',
  },
  data() {
    return {
      isReverse: false,
      isFocus: false,
      dropdownShow: false,
      checkValue: [],
      clearable: false,
      filterValue: '',
    };
  },
  mounted() {
    document.addEventListener(
      'click',
      (e) => {
        if (this.$refs.lpSelect && this.$refs.lpSelect.contains(e.target)) return;
        this.isFocus = false;
      },
      true
    );
  },
  methods: {
    checkboxChange() {
      // console.log(this.checkValue)
    },
    inputFocus() {
      if (this.disabled) return;
      this.isFocus = !this.isFocus;
      if (this.isFocus) {
        this.$emit('focus');
      }
    },
    getRoleNum(key) {
      if (key in this.rolenum) {
        return this.rolenum[key];
      } else {
        return 0;
      }
    },
    setRoleNum(key, e) {
      if (e.target.value > 100) {
        this.$message.warning('角色最小打卡人数不能超过100');
        e.target.value = 100;
      }
      const num = Number(e.target.value);
      this.$emit('rolenum-change', { key, num: num < 0 ? 0 : num });
    },
    isNumDisabled(value) {
      return !this.checkValue.includes(value);
    },
    mouseover() {
      if (this.checkValue.length > 0) {
        this.clearable = true;
      } else {
        this.clearable = false;
      }
    },
    mouseout() {
      this.clearable = false;
    },
    clearValue() {
      this.checkValue = [];
      this.dropdownShow = false;
    },
  },
  computed: {
    finalList() {
      let list = JSON.parse(JSON.stringify(this.list));
      if (!this.filterValue) {
        return list;
      } else {
        return list.filter((v) => {
          return v[this.label] && v[this.label].includes(this.filterValue);
        });
      }
    },
    selectAll: {
      get() {
        if (this.finalList.length === this.checkValue.length) {
          return true;
        } else {
          return false;
        }
      },
      set(val) {
        if (val) {
          this.checkValue = this.finalList.map((item) => this.value ? item[this.value] : item);
        } else {
          this.checkValue = [];
        }
      },
    },
    checkValueShow({ list, checkValue, flag }) {
      // 当做辆序组件时,全部选择时，显示全列
      if (flag === 'CAR' && list.length > 0 && list.length === checkValue.length) {
        return '全列';
      } else {
        let checkRoleList = [];
        let checkList = list
          .map((item) => {
            if (checkValue.includes(item[this.id])) {
              if (this.hasnum) {
                let items = this.value ? item[this.value] : item
                if (items) {
                  checkRoleList.push(items);
                }
                let num = this.rolenum[items] ? this.rolenum[items] : 0;
                return item[this.label] + '(' + (num == 0 ? '不限制' : num) + ')';
              } else {
                return item[this.label];
              }
            }

            let result = false
            result = checkValue.some(val => {
              if (val[this.showText] == item[this.showText]) {
                return true
              }
            })
            if (result) {
              return item[this.showText];
            }
          })
          .filter((item) => item);
        if (this.hasnum) {
          this.checkValue = checkRoleList;
        }
        return checkList.toString();
      }
    },
    selectAllText({ flag }) {
      if (flag == 'CAR') {
        if (this.selectAll) {
          return '全不选';
        } else {
          return '全列';
        }
      } else {
        if (this.selectAll) {
          return '全不选';
        } else {
          return '全选';
        }
      }
    },
  },
  watch: {
    inputValue: {
      handler(val) {
        this.checkValue = val;
      },
      immediate: true,
    },

    isFocus(val) {
      if (val) {
        this.isReverse = true;
        this.dropdownShow = true;
      } else {
        this.isReverse = false;
        this.dropdownShow = false;
      }
    },
    dropdownShow(val) {
      if (!val) {
        this.filterValue = '';
      }
    },
    checkValue(newVal, oldVal) {
      if (this.hasnum) {
        let arr = oldVal
          .map((item) => {
            return !newVal.includes(item) && item;
          })
          .filter((item) => item);
        arr.forEach((item) => {
          this.$set(this.rolenum, item, 0);
        });
      }

      this.$emit('lpChange', newVal);
      this.$emit('change', newVal);
    },
  },
});
