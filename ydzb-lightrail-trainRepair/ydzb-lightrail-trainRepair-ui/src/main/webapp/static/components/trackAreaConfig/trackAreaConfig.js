Vue.component('track-area-config', {
  name: 'track-area-config',
  template: `
  <div class="track-area-config">
    <div class="dialog-title-text">线区股道配置<span v-if="localConfig">( 只对当前终端有效 )</span></div>
    <el-table :data="lineTableData" border header-row-class-name="table-header" class="base-table" :height="lineTableHeight" \@cell-click="cellClick">
      <el-table-column prop="trackAreaName" label="线区" align="center">
        <template slot-scope="scope">
          <div style="display: flex;">
            <span><el-checkbox :value="isShowAlltrackArea[scope.$index]" \@change="setTrackAreaShow($event, scope.$index)"></el-checkbox></span>
            <span style="flex: 1; text-align: center;">{{scope.row.trackAreaName}}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="股道" align="center">
        <template slot-scope="scope">
          <ul>
            <li v-for="item in scope.row.trackNameList">{{item}}</li>
          </ul>
        </template>
      </el-table-column>
      <el-table-column align="center">
        <template slot="header" slot-scope="scope">
          <el-checkbox v-model="isShowAllTrack" class="all-track-checkbox"></el-checkbox>
          是否显示
        </template>
        <template slot-scope="scope">
          <ul>
            <li v-for="(trackCode,index) in scope.row.trackCodeList">
              <el-checkbox
                :value="getTrackShow(trackCode)"
                \@change="setTrackShow(trackCode, $event)"
              ></el-checkbox>
            </li>
          </ul>
        </template>
      </el-table-column>
    </el-table>
  </div>
  `,
  props: {
    localConfig: {
      type: Boolean,
      default: false,
    },
    lineTableData: {
      type: Array,
      default: () => [],
    },
    lineTableHeight: {
      type: [Number, String],
      default: 200,
    },
    allTrackAreaList: {
      type: Array,
      default: () => [],
    },
    trackShowConfig: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      // isShowAlltrackArea: [],
    };
  },
  computed: {
    isShowAllTrack: {
      get() {
        return this.allTrackAreaList.every((trackArea) => {
          return trackArea.lstTrackInfo.every((track) => {
            return this.getTrackShow(track.trackCode);
          });
        });
      },
      set(value) {
        this.allTrackAreaList.forEach((trackArea) => {
          trackArea.lstTrackInfo.forEach((track) => {
            this.setTrackShow(track.trackCode, value);
          });
        });
      },
    },

    isShowAlltrackArea: {
      get() {
        return this.allTrackAreaList.map((trackArea) => {
          return trackArea.lstTrackInfo.every((track) => {
            return this.getTrackShow(track.trackCode);
          });
        });
      },
      set(value) {
        console.log(value)
      },
    },
  },
  watch: {},
  created() {
  },
  methods: {
    isShow(flag) {
      this.isShowAlltrackArea = this.allTrackAreaList.map((trackArea) => {
        return trackArea.lstTrackInfo.every((track) => {
          return this.getTrackShow(track.trackCode);
        });
      });
    },
    getTrackShow(trackCode) {
      if (trackCode in this.trackShowConfig) {
        return this.trackShowConfig[trackCode];
      } else {
        return true;
      }
    },
    setTrackShow(trackCode, value) {
      this.$emit('set-track-show-config', { trackCode, value });
        // this.isShow();
    },
    cellClick(row, column, cell, event) {
      // if (column.label === '线区') {
      //   const { trackCodeList } = row;
      //   trackCodeList.forEach((track) => {
      //     this.setTrackShow(track, true);
      //   });
      // }
    },
    setTrackAreaShow(value, index) {
      this.allTrackAreaList[index].lstTrackInfo.forEach((track) => {
        this.setTrackShow(track.trackCode, value);
      });
      // this.isShow();
    },
  },
});
