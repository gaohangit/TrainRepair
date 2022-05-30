window.app = new Vue({
  el: '#app',
  data() {
    return {
      // 权限信息
      // 通过路由打开页面携带参数
      // 当前修程('1'—查看一级修, '2'—查看二级修, 'all'—一二级修都可查看)
      jurisdiction: '',
      // 第三方调用携带参数
      // 所编码
      unitCode: '',
      // 所名称
      unitName: '',
      // 日计划
      dayPlanId: '',
      // 车组名称
      trainsetName: '',
      // 当前修程('1'—查看一级修, '2'—查看二级修,)
      status: '',
      // 第三方调用时通过车组名称获取到的股道code
      trainCode: '',
      // 流程部分是否显示
      processShow: '',
      // 当前状态是否全屏
      fullscreen: false,
      // 当前屏幕是获取信息是一/二级修(true为一级修，false为二级修)
      currentScreenState: true,
      // 股道详情信息(包含车组、作业、故障等信息)
      trackDetails: [],
      // 股道股道code对应的股道信息
      TrackCodeDataMap: {},
      // 流程运行信息
      flowRunInfoMap: {},
      // 一级修班组作业信息页码
      oneItemInfosInfo: {},
      // 二级修班组作业信息页码
      twoItemInfosInfo: {},
      // 一级修页码信息
      oneWorkPageNumInfo: {},
      // 二级修页码信息
      twoWorkPageNumInfo: {},
      // 一级修外协任务页码信息
      oneOutsourceWorkPageNumInfo: {},
      // 二级修外协任务页码信息
      twoOutsourceWorkPageNumInfo: {},
      // 时间定时器
      timer: null,
      // 右上角时间
      time: '',
      // 当前页
      currentPage: 1,
      // 系统默认进程刷新-定时器
      dataRefreshed: null,
      // 任务刷新-定时器
      taskRefreh: null,
      // 股道刷新-定时器
      trackRefreh: null,
      // 当前刷新股道所在位置
      trackIndex: 0,
      // 配置弹出框
      dialog: {
        // 弹出框展示/隐藏
        dialogconfig: false,
        // 日计划
        dayplayid: '',
        // 获取到的配置信息——用于修改
        configMessage: [],
        // 显示任务
        showTask: '',
        // 显示任务列表
        showTaskList: [
          {
            name: '一级修',
            id: '1',
          },
          {
            name: '二级修',
            id: '2',
          },
          {
            name: '全部',
            id: '3',
          },
        ],
        // 股道轮播时间
        trackWarningTime: '',
        // 任务轮播时间
        taskWarningTime: '',
        // 是否全屏显示
        whetherFull: false,
        // 用于是否全屏显示重置
        oldWhetherFull: false,
        // 股道位置切换
        TrackPositionChange: false,
        // 股道位置切换重置
        oldTrackPositionChange: false,
        // 是否显示无车组股道
        ShowNoTrainsetTrack: false,
        // 用于是否显示无车组股道重置
        oldShowNoTrainsetTrack: false,
        // 过滤后的股道
        filterTrackList: [],
        // 配置选中的股道
        trackList: [],
        // 股道列位信息
        trackAreas: [],
      },
      // 故障弹出窗
      fault: {
        // 加载loading效果
        loading: true,
        // 弹出窗开关
        FaultProject: false,
        // 故障列表
        FaultList: [],
      },
      // 二级修作业详情
      TaskDetails: {
        // 加载loading效果
        loading: true,
        // 弹出窗开关
        TaskDetailsProject: false,
        // 二级修作业详情列表
        TaskDetailsList: [],
      },
      roleList: [],
      nodeList: [],
      // 节点处理情况查看
      lookupNodeDealInfoDialog: {
        // 弹窗开关
        visible: false,
      },
      // 节点详细处理记录
      nodeDealDetailDialog: {
        // 弹窗开关
        visible: false,
      },
      workerPicMap: {},
      workerDetail: {},
      imgUrl: '',
      prePics: [],

      paginationKey: 1,
      loading: true
    };
  },

  created() {
    // 获取url信息
    // 第三方调用携带参数
    // 日计划
    this.dayPlanId = this.getUrlKey('dayPlanId');
    // 车组名称
    this.trainsetName = this.getUrlKey('trainsetName');
    // 当前修程('1'—查看一级修, '2'—查看二级修)
    this.status = this.getUrlKey('status');
    // 流程模块是否显示
    this.processShow = this.getUrlKey('processShow');
    // 通过路由打开页面携带参数
    // 当前修程('1'—查看一级修, '2'—查看二级修, 'all'—一二级修都可查看)
    this.jurisdiction = this.getUrlKey('type');
  },

  async mounted() {
    await this.getUnitCode();
    await this.getUnitName();
    if (this.dayPlanId && this.trainsetName && this.status) {
      await this.getTrainsetPostIon();

      // 获取大屏信息
      await this.getLargeScreenInfo();
      this.loading = false
    } else if (this.jurisdiction) {
      await this.getAllTrackArea();
      await this.getWorkProcessMonitorConfigList();
      await this.getOneWorkProcessMonitorConfig();
      await this.getDay();
      await this.getPostRoleList();

      // 监听全屏
      window.addEventListener('resize', this.resize);

      // 时间
      if (this.$refs.time) {
        this.timer = setInterval(() => {
          this.$refs.time.innerText = dayjs().format('YYYY-MM-DD HH:mm:ss');
        }, 1000);
      }

      // 获取大屏信息
      await this.getLargeScreenInfo(this.showTrackList.join(','));
      this.loading = false
    }

    // 刷新进程
    await this.dataPeriodicallyRefreh();
  },

  computed: {
    pagedTrackDetails() {
      let trackDetails = JSON.parse(JSON.stringify(this.trackDetails));
      for (let i = 0; i < trackDetails.length; i++) {
        let track = trackDetails[i];
        if (track.queryProcessMonitorPlaList) {
          for (let j = 0; j < track.queryProcessMonitorPlaList.length; j++) {
            let train = track.queryProcessMonitorPlaList[j];
            if (!train.powerState) {
              train.powerState = 2;
            }

            if (train.oneWorkerList) {
              let itemInfo = this.getItemInfosInfo(train.itemInfos);
              train.itemInfos = itemInfo.getPage(this.oneItemInfosInfo[train.trainsetId]);

              let pageInfo = this.getOneWorkPageInfo(train.oneWorkerList);
              train.oneWorkerList = pageInfo.getPage(this.oneWorkPageNumInfo[train.trainsetId]);
              let obj = {};
              train.oneWorkerList.forEach((item) => {
                if (!obj[item.oneWorker.workerId]) {
                  obj[item.oneWorker.workerId] = {
                    workerId: item.oneWorker.workerId,
                    workerName: item.oneWorker.workerName,
                    oneItemList: [],
                  };
                }

                obj[item.oneWorker.workerId].oneItemList.push(item.oneItem);
              });

              let list = [];
              Object.keys(obj).forEach((item) => {
                list.push(obj[item]);
              });
              train.oneWorkerList = list;

              let integration = this.getOutsourceWorkPageInfo(train.integrationList);
              train.integrationList = integration.getPage(this.oneOutsourceWorkPageNumInfo[train.trainsetId]);
            } else if (train.workPacketList) {
              let itemInfo = this.getItemInfosInfo(train.itemInfos);
              train.itemInfos = itemInfo.getPage(this.twoItemInfosInfo[train.trainsetId]);

              let pageInfo = this.getTwoWorkPageInfo(train.workPacketList);
              train.workPacketList = pageInfo.getPage(this.twoWorkPageNumInfo[train.trainsetId]);

              let integration = this.getOutsourceWorkPageInfo(train.integrationList);
              train.integrationList = integration.getPage(this.twoOutsourceWorkPageNumInfo[train.trainsetId]);
            }

            let flowRunInfo = this.flowRunInfoMap[train.trainsetId];
            if (flowRunInfo) {
              train.nodeList = flowRunInfo.nodeList;
              train.nodeShowList = flowRunInfo.nodeShowList;
              train.showHeadLine = flowRunInfo.showHeadLine;
              train.showFootLine = flowRunInfo.showFootLine;
              train.flowId = flowRunInfo.flowId;
            }
          }
        }
      }

      if (this.dialog.oldTrackPositionChange) {
        trackDetails.forEach((item) => {
          if (item.queryProcessMonitorPlaList.length > 1) {
            item.queryProcessMonitorPlaList.reverse();
          } else {
            item.queryProcessMonitorPlaList.forEach((train) => {
              let name = train.headDirectionPla;
              train.headDirectionPla = train.tailDirectionPla;
              train.tailDirectionPla = name;
            });
          }
          item.queryProcessMonitorPlaList.forEach((train) => {
            let carNo = train.headDirection;
            train.headDirection = train.tailDirection;
            train.tailDirection = carNo;
          });
        });
      }

      if (this.jurisdiction) {
        trackDetails.forEach((item) => {
          if (this.TrackCodeDataMap[item.trackCode]) {
            this.TrackCodeDataMap[item.trackCode] = {};
          }
          this.TrackCodeDataMap[item.trackCode] = item;
        });

        let trackDetailList = [];

        if (this.selectedTrack) {
          let trackList = this.selectedTrack.split(',');

          trackList.forEach((track) => {
            if (this.TrackCodeDataMap[track]) {
              trackDetailList.push(this.TrackCodeDataMap[track]);
            }
          });
        }

        return trackDetailList;
      } else {
        return trackDetails;
      }
    },

    // 对获取到的配置信息进行处理
    trackNameConfigInfo({ dialog }) {
      return dialog.configMessage.reduce((prev, item) => {
        if (item.parmName == 'TrackCode') {
          prev[item.parmValue] = item;
        }
        return prev;
      }, {});
    },

    isShowAlltrack: {
      get() {
        let trackList = [];
        this.dialog.trackAreas.forEach((item) => {
          item.lstTrackInfo.forEach((track) => {
            trackList.push(track.trackCode);
          });
        });

        if (this.dialog.trackList.length == trackList.length) {
          return true;
        } else {
          return false;
        }
      },
      set(value) {
        if (value) {
          let trackList = [];
          this.dialog.trackAreas.forEach((item) => {
            item.lstTrackInfo.forEach((track) => {
              trackList.push(track.trackCode);
            });
          });

          this.dialog.trackList = trackList;
        } else {
          this.dialog.trackList = [];
        }
      },
    },

    isShowAlltrackArea: {
      get() {
        let list = [];
        this.dialog.trackAreas.forEach((trackArea) => {
          let trackCheck = trackArea.lstTrackInfo.every((track) => {
            return this.dialog.trackList.includes(track.trackCode);
          });

          list.push(trackCheck);
        });
        return list;
      },
    },

    showTrackList() {
      let List;
      if (this.dialog.oldShowNoTrainsetTrack) {
        List = this.dialog.trackList;
      } else {
        List = this.dialog.filterTrackList;
      }

      let arr = [];
      for (let i = 0; i < this.dialog.trackAreas.length; i++) {
        for (let j = 0; j < this.dialog.trackAreas[i].lstTrackInfo.length; j++) {
          for (let z = 0; z < List.length; z++) {
            if (this.dialog.trackAreas[i].lstTrackInfo[j].trackCode == List[z]) {
              arr.push(this.dialog.trackAreas[i].lstTrackInfo[j]);
            }
          }
        }
      }

      return arr.sort(this.compare('sort')).reduce((prev, item) => {
        prev.push(item.trackCode);
        return prev;
      }, []);
    },

    // 要展示的股道进行字符串拼接
    selectedTrack() {
      if (this.showTrackList.length <= this.trackIndex) {
        this.trackIndex = 0;
      }

      return this.showTrackList.slice(this.trackIndex, this.trackIndex + 2).join(',');
    },

    // 股道列位code对应的股道code
    trainsetNameTrackCodeMap({ trackDetails }) {
      return trackDetails.reduce((prev, item) => {
        item.queryProcessMonitorPlaList.forEach((train) => {
          prev[train.trackPlaCode] = item.trackCode;
        });
        return prev;
      }, {});
    },

    // 股道索引
    trackIndexMap({ trackDetails }) {
      return trackDetails.reduce((prev, item, index) => {
        prev[item.trackCode] = index;
        return prev;
      }, {});
    },

    // 列为索引
    trainIndexMap({ trackDetails }) {
      return trackDetails.reduce((prev, item) => {
        item.queryProcessMonitorPlaList.forEach((train, index) => {
          prev[train.trackPlaCode] = index;
        });
        return prev;
      }, {});
    },

    roleCodeNameMap({ roleList }) {
      return roleList.reduce((prev, item) => {
        prev[item.code] = item.name;
        return prev;
      }, {});
    },

    nodeColumns({ nodeList, roleCodeNameMap }) {
      const roleIds = [
        ...new Set(
          nodeList
            .map((node) => {
              return node.roleConfigs.map((role) => {
                return role.roleId;
              });
            })
            .flat()
        ),
      ];
      return roleIds.map((id) => {
        return {
          id: id,
          label: roleCodeNameMap[id],
          prop: id,
        };
      });
    },

    nodeDealData({ nodeList }) {
      if (nodeList) {
        return nodeList.map((node) => {
          const record = node.nodeRecords.reduce((prev, item) => {
            if (prev[item.roleId]) {
              prev[item.roleId] = prev[item.roleId] + ',' + item.workerName;
            } else {
              prev[item.roleId] = item.workerName;
            }

            // prev[item.roleId] = `${item.workerName}在 ${item.recordTime} 打了卡`;
            return prev;
          }, {});
          const roleWorkerPicUrls = node.nodeRecords.reduce((prev, item) => {
            if (!prev[item.roleId]) {
              prev[item.roleId] = {};
            }
            prev[item.roleId] = node.nodeRecords.reduce((prev, item) => {
              if (!prev[item.workerId]) {
                prev[item.workerId] = [];
              }
              prev[item.workerId] = [
                ...prev[item.workerId],
                ...item.pictureUrls.map((item) => {
                  let url = item.relativeUrl.split('/');
                  return {
                    name: url[url.length - 1],
                    url: `/storageTrainRepair/${item.relativeUrl}`,
                  };
                }),
              ];

              return prev;
            }, {});
            return prev;
          }, {});

          const roleWorkerDetails = node.nodeRecords.reduce((prev, item) => {
            if (!prev[item.roleId]) {
              prev[item.roleId] = {};
            }
            prev[item.roleId] = node.nodeRecords.reduce((prev, item) => {
              if (!prev[item.workerId]) {
                prev[item.workerId] = [];
              }
              prev[item.workerId] = `${item.workerName}在 ${item.recordTime} 打卡`;

              return prev;
            }, {});
            return prev;
          }, {});

          return {
            id: node.id,
            node: node.name,
            ...record,
            roleWorkerPicUrls,
            roleWorkerDetails,
          };
        });
      }
    },
  },

  watch: {},

  methods: {
    // 获取运用所编码
    async getUnitCode() {
      let { data } = await axios.get('/apiTrainRepair/common/getUnitCode');
      if (data.code == 1) {
        this.unitCode = data.data;
      } else {
        this.$message.error('获取登录人信息失败！');
      }
    },

    // 获取运用所名称
    async getUnitName() {
      let { data } = await axios.get('/apiTrainRepair/common/getUnitName');
      if (data.code == 1) {
        this.unitName = data.data;
      } else {
        this.$message.error('获取登录人信息失败！');
      }
    },

    getRightPageNum(pageInfo, pageNum) {
      if (pageInfo.pages < pageNum) {
        pageNum = pageInfo.pages;
      } else if (pageNum < 1 || isNaN(pageNum)) {
        pageNum = 1;
      }
      return pageNum;
    },

    // 作业班组作业包分页控制
    getItemInfosInfo(item) {
      let getPage = (pageInfo, pageNum) => {
        return pageInfo.pageData[this.getRightPageNum(pageInfo, pageNum) - 1];
      };
      let getNextPageNum = (pageInfo, pageNum) => {
        pageNum = this.getRightPageNum(pageInfo, pageNum);
        let nextPageNum = pageNum + 1;
        if (pageInfo.pages < nextPageNum) {
          nextPageNum = 1;
        }
        return nextPageNum;
      };

      item = JSON.parse(JSON.stringify(item));

      if (item.length == 0) {
        return {
          pageData: [],
          pages: 1,
          getPage(pageNum) {
            return getPage(this, pageNum);
          },
          getNextPageNum(pageNum) {
            return getNextPageNum(this, pageNum);
          },
        };
      }

      return {
        pages: item.length,
        pageData: item,
        getPage(pageNum) {
          return getPage(this, pageNum);
        },
        getNextPageNum(pageNum) {
          return getNextPageNum(this, pageNum);
        },
      };
    },

    // 二级修作业人员分页控制
    getTwoWorkPageInfo(twoWorkerList) {
      let getPage = (pageInfo, pageNum) => {
        return pageInfo.pageData[this.getRightPageNum(pageInfo, pageNum) - 1];
      };
      let getNextPageNum = (pageInfo, pageNum) => {
        pageNum = this.getRightPageNum(pageInfo, pageNum);
        let nextPageNum = pageNum + 1;
        if (pageInfo.pages < nextPageNum) {
          nextPageNum = 1;
        }
        return nextPageNum;
      };

      twoWorkerList = JSON.parse(JSON.stringify(twoWorkerList));
      let pageLines = 5;

      if (twoWorkerList.length == 0) {
        return {
          pageData: [[]],
          pages: 1,
          getPage(pageNum) {
            return getPage(this, pageNum);
          },
          getNextPageNum(pageNum) {
            return getNextPageNum(this, pageNum);
          },
        };
      }
      let pageData = [];
      let pages = 1;
      let a = Math.trunc(twoWorkerList.length / pageLines);
      let b = twoWorkerList.length % pageLines;
      if (b == 0) {
        pages = a;
      } else {
        pages = a + 1;
      }
      for (let pageIndex = 0; pageIndex < pages; pageIndex++) {
        pageData[pageIndex] = twoWorkerList.slice(pageIndex * pageLines, (pageIndex + 1) * pageLines);
      }
      return {
        pages,
        pageData,
        getPage(pageNum) {
          return getPage(this, pageNum);
        },
        getNextPageNum(pageNum) {
          return getNextPageNum(this, pageNum);
        },
      };
    },

    // 一级修作业人员分页控制
    getOneWorkPageInfo(oneWorkerList) {
      let getPage = (pageInfo, pageNum) => {
        return pageInfo.pageData[this.getRightPageNum(pageInfo, pageNum) - 1];
      };
      let getNextPageNum = (pageInfo, pageNum) => {
        pageNum = this.getRightPageNum(pageInfo, pageNum);
        let nextPageNum = pageNum + 1;
        if (pageInfo.pages < nextPageNum) {
          nextPageNum = 1;
        }
        return nextPageNum;
      };

      oneWorkerList = JSON.parse(JSON.stringify(oneWorkerList));
      let pageLines = 8;
      let oneItemWithWorkerList = [];

      oneWorkerList.forEach((oneWorker) => {
        oneWorker.oneItemList.forEach((oneItem) => {
          oneItemWithWorkerList.push({
            oneItem: oneItem,
            oneWorker: oneWorker,
          });
        });
      });
      if (oneItemWithWorkerList.length == 0) {
        return {
          pageData: [[]],
          pages: 1,
          getPage(pageNum) {
            return getPage(this, pageNum);
          },
          getNextPageNum(pageNum) {
            return getNextPageNum(this, pageNum);
          },
        };
      }
      let pageData = [];
      let pages = 1;
      let a = Math.trunc(oneItemWithWorkerList.length / pageLines);
      let b = oneItemWithWorkerList.length % pageLines;
      if (b == 0) {
        pages = a;
      } else {
        pages = a + 1;
      }
      for (let pageIndex = 0; pageIndex < pages; pageIndex++) {
        pageData[pageIndex] = oneItemWithWorkerList.slice(pageIndex * pageLines, (pageIndex + 1) * pageLines);
      }

      return {
        pages,
        pageData,
        getPage(pageNum) {
          return getPage(this, pageNum);
        },
        getNextPageNum(pageNum) {
          return getNextPageNum(this, pageNum);
        },
      };
    },

    // 外协任务分页控制
    getOutsourceWorkPageInfo(integrationList) {
      let getPage = (pageInfo, pageNum) => {
        return pageInfo.pageData[this.getRightPageNum(pageInfo, pageNum) - 1];
      };
      let getNextPageNum = (pageInfo, pageNum) => {
        pageNum = this.getRightPageNum(pageInfo, pageNum);
        let nextPageNum = pageNum + 1;
        if (pageInfo.pages < nextPageNum) {
          nextPageNum = 1;
        }
        return nextPageNum;
      };

      integrationList = JSON.parse(JSON.stringify(integrationList));
      let pageLines = 5;

      if (integrationList.length == 0) {
        return {
          pageData: [[]],
          pages: 1,
          getPage(pageNum) {
            return getPage(this, pageNum);
          },
          getNextPageNum(pageNum) {
            return getNextPageNum(this, pageNum);
          },
        };
      }
      let pageData = [];
      let pages = 1;
      let a = Math.trunc(integrationList.length / pageLines);
      let b = integrationList.length % pageLines;
      if (b == 0) {
        pages = a;
      } else {
        pages = a + 1;
      }
      for (let pageIndex = 0; pageIndex < pages; pageIndex++) {
        pageData[pageIndex] = integrationList.slice(pageIndex * pageLines, (pageIndex + 1) * pageLines);
      }

      return {
        pages,
        pageData,
        getPage(pageNum) {
          return getPage(this, pageNum);
        },
        getNextPageNum(pageNum) {
          return getNextPageNum(this, pageNum);
        },
      };
    },

    // 控制故障列表表格颜色
    tableRowClassName({ row, rowIndex }) {
      if (rowIndex % 2 == 1) {
        return 'warning-row';
      } else {
        return 'success-row';
      }
    },

    // 获取url信息
    getUrlKey(name) {
      var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
      var r = window.location.search.substr(1).match(reg);
      if (r != null) {
        return r[2];
      }
      return null;
    },

    // 右键菜单
    openMemu(ev) {
      if (this.jurisdiction) {
        //************获取元素***********
        var listBox = document.getElementById('list'); //获取自定义右键菜单
        //兼容性写法示例：
        var ev = ev || event; //或（||）书写顺序有讲究，不能随意换
        //阻止默认行为
        ev.preventDefault();
        //记录当前的坐标(x轴和y轴)
        var x = ev.clientX;
        var y = ev.clientY;
        listBox.style.display = 'block'; //右键点击时显示菜单框
        listBox.style.left = x + 'px'; //左
        listBox.style.top = y + 'px'; //右
        //关闭右键
        document.onclick = function () {
          listBox.style.display = 'none'; //再次点击时隐藏菜单框
        };
      }
    },

    // 右键菜单-配置按钮
    configuration() {
      this.dialog.dialogconfig = true;
    },

    // 页面窗口变化
    resize() {
      if (this.isFullScreen()) {
        this.dialog.whetherFull = true;
        this.dialog.oldWhetherFull = this.dialog.whetherFull;
        this.fullscreen = true;
      } else {
        this.dialog.whetherFull = false;
        this.dialog.oldWhetherFull = this.dialog.whetherFull;
        this.fullscreen = false;
      }
    },

    // 判断是否在全屏模式下
    isFullScreen() {
      return (
        document.fullscreenElement ||
        document.webkitFullscreenElement ||
        document.mozFullscreenElement ||
        document.msFullscreenElement
      );
    },

    // 进入全屏
    screen() {
      var appBackground = document.getElementById('app');
      let element = document.documentElement;
      if (this.isFullScreen()) {
        if (document.exitFullscreen) {
          document.exitFullscreen();
        } else if (document.webkitCancelFullscreen) {
          document.webkitCancelFullscreen();
        } else if (document.mozCancelFullScreen) {
          document.mozCancelFullScreen();
        } else if (document.msExitFullScreen) {
          document.msExitFullScreen();
        }
      } else {
        if (element.requestFullscreen) {
          element.requestFullscreen();
        } else if (element.webkitRequestFullScreen) {
          element.webkitRequestFullScreen();
        } else if (element.mozRequestFullScreen) {
          element.mozRequestFullScreen();
        } else if (element.msRequestFullscreen) {
          element.msRequestFullscreen();
        }
      }
    },

    // 对股道排序
    compare(property) {
      return function (a, b) {
        let sort1 = a[property];
        let sort2 = b[property];
        return sort1 - sort2;
      };
    },

    // 获取股道列位信息
    async getAllTrackArea() {
      let res = await axios.get('/apiTrainRepair/trackPowerStateCur/getAllTrackArea');
      if (res.data.code == 1) {
        this.dialog.trackAreas = res.data.data.trackAreas;

        this.dialog.trackAreas.sort(this.compare('sort'));

        this.dialog.trackAreas.forEach((item) => {
          item.lstTrackInfo.sort(this.compare('sort'));
        });
      } else {
        this.$message({
          message: '获取股道信息失败',
          type: 'warning',
        });
      }
    },

    // 获取作业过程个性化配置
    async getWorkProcessMonitorConfigList() {
      let res = await axios.get('/apiTrainRepair/processUserConfig/getWorkProcessMonitorConfigList', {
        params: {
          unitCode: this.unitCode,
        },
      });
      if (res.data.code == 1) {
        this.dialog.configMessage = res.data.data;
        this.dialog.trackList = [];
        res.data.data.forEach((item) => {
          if (item.parmName == 'TrackCode') {
            // 选中的股道
            this.dialog.trackList.push(Number(item.parmValue));
          } else if (item.parmName == 'TrackRefreshTime') {
            // 股道轮播时间
            this.dialog.trackWarningTime = item.parmValue;
          } else if (item.parmName == 'TaskRefreshTime') {
            // 任务轮播时间
            this.dialog.taskWarningTime = item.parmValue;
          } else if (item.parmName == 'ShowTaskType') {
            // 显示任务
            this.dialog.showTask = item.parmValue;
          }
        });
      } else {
        this.$message({
          message: '获取配置信息失败',
          type: 'warning',
        });
      }
    },

    // 获取过滤后的股道
    async getOneWorkProcessMonitorConfig() {
      this.dialog.filterTrackList = [];
      if (this.dialog.trackList.length == 0) return;
      let { data } = await axios.post(
        '/apiTrainRepair/processMonitor/getOneWorkProcessMonitorConfig',
        this.dialog.trackList
      );

      if (data.code == 1) {
        this.dialog.filterTrackList = data.data.map((item) => {
          return item.trackCode;
        });
      }
    },

    // 修改作业过程个性化配置
    async updateWorkProcessMonitorConfigList() {
      if (this.unitCode) {
        let trackConfig = [];
        // 选中要进行显示的股道
        trackConfig = this.dialog.trackList.map((check) => {
          if (this.trackNameConfigInfo[check]) {
            return this.trackNameConfigInfo[check];
          } else {
            return {
              configId: '',
              iSortId: '',
              parmName: 'TrackCode',
              parmValue: check,
              remark: '',
              staffId: '',
              unitCode: this.unitCode,
            };
          }
        });
        this.dialog.configMessage.forEach((item) => {
          if (item.parmName == 'TrackRefreshTime') {
            // 股道轮播时间
            item.parmValue = this.dialog.trackWarningTime;
            trackConfig.push(item);
          } else if (item.parmName == 'TaskRefreshTime') {
            // 任务轮播时间
            item.parmValue = this.dialog.taskWarningTime;
            trackConfig.push(item);
          } else if (item.parmName == 'ShowTaskType') {
            // 显示任务
            item.parmValue = this.dialog.showTask;
            trackConfig.push(item);
          }
        });

        this.dialog.configMessage = trackConfig;

        await axios
          .post('/apiTrainRepair/processUserConfig/updateWorkProcessMonitorConfigList', {
            processUserConfigList: trackConfig,
          })
          .then(async (res) => {
            this.dialog.dialogconfig = false;
            let element = document.documentElement;
            if (this.dialog.whetherFull) {
              if (element.requestFullscreen) {
                element.requestFullscreen();
              } else if (element.webkitRequestFullScreen) {
                element.webkitRequestFullScreen();
              } else if (element.mozRequestFullScreen) {
                element.mozRequestFullScreen();
              } else if (element.msRequestFullscreen) {
                element.msRequestFullscreen();
              }
            } else {
              if (this.isFullScreen()) {
                if (document.exitFullscreen) {
                  document.exitFullscreen();
                } else if (document.webkitCancelFullscreen) {
                  document.webkitCancelFullscreen();
                } else if (document.mozCancelFullScreen) {
                  document.mozCancelFullScreen();
                } else if (document.msExitFullScreen) {
                  document.msExitFullScreen();
                }
              }
            }
            this.dialog.oldWhetherFull = this.dialog.whetherFull;
            this.dialog.oldShowNoTrainsetTrack = this.dialog.ShowNoTrainsetTrack;
            this.dialog.oldTrackPositionChange = this.dialog.TrackPositionChange;
            this.trackIndex = 0;
            this.currentPage = 1;
            // 获取配置信息
            await this.getWorkProcessMonitorConfigList();
            await this.getOneWorkProcessMonitorConfig();

            const childComponents = findComponentsDownward(this, 'track-details');
            childComponents.forEach((child) => {
              child.initImageUrl();
            });

            // 获取大屏信息
            await this.getLargeScreenInfo();
            // 刷新进程
            this.dataPeriodicallyRefreh();

            this.paginationKey += 1;
          })
          .catch(() => {
            this.$message({
              message: '修改失败',
              type: 'warning',
            });
          });
      } else {
        this.$message({
          message: '运用所不能为空',
          type: 'warning',
        });
      }
    },

    // 取消修改作业过程个性化配置
    cancel() {
      let that = this;
      that.dialog.dialogconfig = false;
      that.dialog.whetherFull = that.dialog.oldWhetherFull;
      that.dialog.ShowNoTrainsetTrack = that.dialog.oldShowNoTrainsetTrack;
      this.dialog.TrackPositionChange = this.dialog.oldTrackPositionChange;
      that.getWorkProcessMonitorConfigList();
    },

    //弹出层-获取作业班次(日计划)
    async getDay() {
      var that = this;
      var url_path = '/apiTrainRepair/common/getDay';
      let res = await axios.get(url_path);
      var data = res.data;
      that.dialog.dayplayid = data.dayPlanId;
    },

    async setflowRunInfoMap(trainsetId, isLong) {
      let {
        data: {
          data: { flowRunInfoGroups },
        },
      } = await this.getFlowRunInfos(trainsetId);

      if (flowRunInfoGroups.length == 0) {
        this.flowRunInfoMap[trainsetId] = [];
      } else if (flowRunInfoGroups[0].flowRunInfoForSimpleShows.length == 0) {
        this.flowRunInfoMap[trainsetId] = [];
      } else {
        let nodeList = flowRunInfoGroups[0].flowRunInfoForSimpleShows[0].nodeList;

        let runingNode = this.runingNode(nodeList);
        let runingNodeIdx = this.runingNodeIdx(nodeList, runingNode);
        let flowState = this.flowState(nodeList, runingNodeIdx);
        let headNodeList = this.headNodeList(runingNodeIdx, nodeList, isLong, flowState);
        let footNodeList = this.footNodeList(runingNodeIdx, nodeList, isLong);
        let nodeShowList = this.nodeShowList(nodeList, headNodeList, footNodeList, runingNode, runingNodeIdx, isLong);
        let showHeadLine = this.showHeadLine(nodeShowList, nodeList);
        let showFootLine = this.showFootLine(nodeShowList, nodeList);
        this.flowRunInfoMap[trainsetId] = {
          nodeList,
          nodeShowList,
          showHeadLine,
          showFootLine,
          flowId: flowRunInfoGroups[0].flowRunInfoForSimpleShows[0].id,
        };
      }
    },

    // 一级修大屏
    async getOneWorkProcessMonitorList(trackList, trainsetName, dayPlanId) {
      let trainsetNameStr;
      if (trainsetName) {
        trainsetNameStr = trainsetName;
      } else {
        trainsetNameStr = '';
      }

      let res = await axios.post('/apiTrainRepair/processMonitor/getOneWorkProcessMonitorList', {
        trackCodesJsonStr: trackList, // 股道集合
        unitCode: this.unitCode, // 运用所编码
        dayPlanId: dayPlanId ? dayPlanId : this.dialog.dayplayid, // 日计划ID
        // dayPlanId: '2021-12-09-00', // 日计划ID
        trainsetNameStr, // 车组名称集合
      });

      if (res.data.code == 1) {
        if (this.jurisdiction && trainsetNameStr && res.data.data.length < 2) {
          let trainRepairImg = res.data.data;
          trainRepairImg.forEach((track) => {
            track.queryProcessMonitorPlaList &&
              track.queryProcessMonitorPlaList.forEach(async (train) => {
                await this.setflowRunInfoMap(train.trainsetId, train.isLong);

                if (train.oneWorkerList) {
                  // 班组分页
                  let itemInfo = this.getItemInfosInfo(train.itemInfos);
                  let itemPageNum = this.oneItemInfosInfo[train.trainsetId];
                  this.$set(
                    this.oneItemInfosInfo,
                    train.trainsetId,
                    itemPageNum == null ? 1 : itemInfo.getNextPageNum(itemPageNum)
                  );

                  // 任务分页
                  let pageInfo = this.getOneWorkPageInfo(train.oneWorkerList);
                  let curPageNum = this.oneWorkPageNumInfo[train.trainsetId];
                  this.$set(
                    this.oneWorkPageNumInfo,
                    train.trainsetId,
                    curPageNum == null ? 1 : pageInfo.getNextPageNum(curPageNum)
                  );

                  // 外协任务分页
                  let outsourceInfo = this.getOutsourceWorkPageInfo(train.integrationList);
                  let outsourcePageNum = this.oneOutsourceWorkPageNumInfo[train.trainsetId];
                  this.$set(
                    this.oneOutsourceWorkPageNumInfo,
                    train.trainsetId,
                    outsourcePageNum == null ? 1 : outsourceInfo.getNextPageNum(outsourcePageNum)
                  );
                }
              });
          });
          return trainRepairImg;
        } else {
          this.trackDetails = res.data.data;

          this.trackDetails.forEach((track) => {
            track.queryProcessMonitorPlaList &&
              track.queryProcessMonitorPlaList.forEach(async (train) => {
                await this.setflowRunInfoMap(train.trainsetId, train.isLong);

                if (train.oneWorkerList) {
                  // 班组分页
                  let itemInfo = this.getItemInfosInfo(train.itemInfos);
                  let itemPageNum = this.oneItemInfosInfo[train.trainsetId];
                  this.$set(
                    this.oneItemInfosInfo,
                    train.trainsetId,
                    itemPageNum == null ? 1 : itemInfo.getNextPageNum(itemPageNum)
                  );

                  // 任务分页
                  let pageInfo = this.getOneWorkPageInfo(train.oneWorkerList);
                  let curPageNum = this.oneWorkPageNumInfo[train.trainsetId];
                  this.$set(
                    this.oneWorkPageNumInfo,
                    train.trainsetId,
                    curPageNum == null ? 1 : pageInfo.getNextPageNum(curPageNum)
                  );

                  let outsourceInfo = this.getOutsourceWorkPageInfo(train.integrationList);
                  let outsourcePageNum = this.oneOutsourceWorkPageNumInfo[train.trainsetId];
                  this.$set(
                    this.oneOutsourceWorkPageNumInfo,
                    train.trainsetId,
                    outsourcePageNum == null ? 1 : outsourceInfo.getNextPageNum(outsourcePageNum)
                  );
                }
              });
          });
        }
      } else {
        this.$message({
          message: '获取一级修信息失败',
          type: 'warning',
        });
      }
    },

    // 二级修大屏
    async getTwoWorkProcessMonitorList(trackList, trainsetName, dayPlanId) {
      let trainsetNameStr;
      if (trainsetName) {
        trainsetNameStr = trainsetName;
      } else {
        trainsetNameStr = '';
      }

      let res = await axios.post('/apiTrainRepair/processMonitor/getTwoWorkProcessMonitorList', {
        trackCodesJsonStr: trackList, // 股道集合
        unitCode: this.unitCode, // 运用所编码
        dayPlanId: dayPlanId ? dayPlanId : this.dialog.dayplayid, // 日计划ID
        // dayPlanId: '2021-05-17-00', // 日计划ID
        trainsetNameStr, // 车组名称集合
      });

      if (res.data.code == 1) {
        if (this.jurisdiction && trainsetNameStr && res.data.data.length < 2) {
          let trainRepairImg = res.data.data;
          trainRepairImg.forEach((track) => {
            track.queryProcessMonitorPlaList &&
              track.queryProcessMonitorPlaList.forEach(async (train) => {
                await this.setflowRunInfoMap(train.trainsetId, train.isLong);

                if (train.workPacketList) {
                  let itemInfo = this.getItemInfosInfo(train.itemInfos);
                  let itemPageNum = this.twoItemInfosInfo[train.trainsetId];
                  this.$set(
                    this.twoItemInfosInfo,
                    train.trainsetId,
                    itemPageNum == null ? 1 : itemInfo.getNextPageNum(itemPageNum)
                  );

                  let pageInfo = this.getTwoWorkPageInfo(train.workPacketList);
                  let curPageNum = this.twoWorkPageNumInfo[train.trainsetId];
                  this.$set(
                    this.twoWorkPageNumInfo,
                    train.trainsetId,
                    curPageNum == null ? 1 : pageInfo.getNextPageNum(curPageNum)
                  );

                  // 外协任务分页
                  let outsourceInfo = this.getOutsourceWorkPageInfo(train.integrationList);
                  let outsourcePageNum = this.twoOutsourceWorkPageNumInfo[train.trainsetId];
                  this.$set(
                    this.twoOutsourceWorkPageNumInfo,
                    train.trainsetId,
                    outsourcePageNum == null ? 1 : outsourceInfo.getNextPageNum(outsourcePageNum)
                  );
                }
              });
          });
          return trainRepairImg;
        } else {
          this.trackDetails = res.data.data;
          this.trackDetails.forEach((track) => {
            track.queryProcessMonitorPlaList &&
              track.queryProcessMonitorPlaList.forEach(async (train) => {
                await this.setflowRunInfoMap(train.trainsetId, train.isLong);

                if (train.workPacketList) {
                  let itemInfo = this.getItemInfosInfo(train.itemInfos);
                  let itemPageNum = this.twoItemInfosInfo[train.trainsetId];
                  this.$set(
                    this.twoItemInfosInfo,
                    train.trainsetId,
                    itemPageNum == null ? 1 : itemInfo.getNextPageNum(itemPageNum)
                  );

                  let pageInfo = this.getTwoWorkPageInfo(train.workPacketList);
                  let curPageNum = this.twoWorkPageNumInfo[train.trainsetId];
                  this.$set(
                    this.twoWorkPageNumInfo,
                    train.trainsetId,
                    curPageNum == null ? 1 : pageInfo.getNextPageNum(curPageNum)
                  );

                  // 外协任务分页
                  let outsourceInfo = this.getOutsourceWorkPageInfo(train.integrationList);
                  let outsourcePageNum = this.twoOutsourceWorkPageNumInfo[train.trainsetId];
                  this.$set(
                    this.twoOutsourceWorkPageNumInfo,
                    train.trainsetId,
                    outsourcePageNum == null ? 1 : outsourceInfo.getNextPageNum(outsourcePageNum)
                  );
                }
              });
          });
        }
      } else {
        this.$message({
          message: '获取二级修信息失败',
          type: 'warning',
        });
      }
    },

    // 获取所有车的一二级状态
    getWorkType() {
      const childComponents = findComponentsDownward(this, 'track-details');
      return childComponents.reduce((prev, item) => {
        prev = { ...prev, ...item.imageTypeObj };
        return prev;
      }, {});
    },

    // 获取大屏初始化信息
    async getLargeScreenInfo(selectedTrack) {
      let track;
      if (selectedTrack) {
        track = selectedTrack;
      } else {
        track = this.selectedTrack;
      }

      if (this.dayPlanId && this.trainsetName && this.status) {
        if (this.status == '1') {
          await this.getOneWorkProcessMonitorList(this.trainCode, this.trainsetName, this.dayPlanId);
        } else if (this.status == '2') {
          await this.getTwoWorkProcessMonitorList(this.trainCode, this.trainsetName, this.dayPlanId);
        }
      } else if (this.jurisdiction) {
        if (this.jurisdiction == '1') {
          await this.getOneWorkProcessMonitorList(track);
        } else if (this.jurisdiction == '2') {
          await this.getTwoWorkProcessMonitorList(track);
        } else if (this.jurisdiction == 'all') {
          if (this.dialog.showTask == '1') {
            await this.getOneWorkProcessMonitorList(track);
          } else if (this.dialog.showTask == '2') {
            await this.getTwoWorkProcessMonitorList(track);
          } else if (this.dialog.showTask == '3') {
            await this.getOneWorkProcessMonitorList(track);
          }
        }
      }
    },

    // 获取对应条件的股道信息
    async getLargeScreenMessage() {
      const workType = this.getWorkType();
      if (this.jurisdiction == 1) {
        await this.getOneWorkProcessMonitorList(this.selectedTrack);
      } else if (this.jurisdiction == 2) {
        await this.getTwoWorkProcessMonitorList(this.selectedTrack);
      } else if (this.jurisdiction == 'all') {
        let getDataPormiseGetters = [];
        let getedDataThenExecutes = [];

        Object.keys(workType).forEach((typeItem) => {
          let trackPlaCode;
          this.pagedTrackDetails.forEach((item) => {
            item.queryProcessMonitorPlaList.forEach((val) => {
              if (val.trackPlaCode == typeItem) {
                trackPlaCode = val.trainsetName;
              }
            });
          });

          if (trackPlaCode) {
            let trackCode = this.trainsetNameTrackCodeMap[typeItem];
            let queryProcessMonitorPlaList = this.trackDetails[this.trackIndexMap[trackCode]]
              .queryProcessMonitorPlaList;
            let index = this.trainIndexMap[typeItem];
            let trainRepairImg = null;
            if (workType[typeItem] == 'iconOneUrl') {
              getDataPormiseGetters.push(() =>
                this.getOneWorkProcessMonitorList(trackCode, trackPlaCode).then((v) => {
                  trainRepairImg = v;
                })
              );
            } else if (workType[typeItem] == 'iconTwoUrl') {
              getDataPormiseGetters.push(() =>
                this.getTwoWorkProcessMonitorList(trackCode, trackPlaCode).then((v) => {
                  trainRepairImg = v;
                })
              );
            } else {
              return;
            }

            getedDataThenExecutes.push(() => {
              this.$set(queryProcessMonitorPlaList, index, trainRepairImg[0].queryProcessMonitorPlaList[0]);
            });
          }
        });

        for (let i = 0; i < getDataPormiseGetters.length; i++) {
          await getDataPormiseGetters[i]();
        }

        for (let i = 0; i < getedDataThenExecutes.length; i++) {
          getedDataThenExecutes[i]();
        }
      }
    },

    // 系统默认进程刷新
    dataPeriodicallyRefreh() {
      let num = 0;
      if (this.dataRefreh != null) {
        clearInterval(this.dataRefreh);
      }

      this.dataRefreh = setInterval(async () => {
        num++;
        // 任务刷新时间
        let taskTime = Number(this.dialog.taskWarningTime);
        // 股道刷新时间
        let trackTime = Number(this.dialog.trackWarningTime);
        if (trackTime && num % (trackTime * 3) == 0) {
          this.trackIndex += 2;
          this.currentPage = Math.ceil((this.trackIndex + 2) / 2);
          let pages = Math.ceil(this.showTrackList.length / 2);
          if (this.currentPage > pages) {
            this.currentPage = 1;
          }
          // 获取大屏信息
          const childComponents = findComponentsDownward(this, 'track-details');
          childComponents.forEach((child) => {
            child.initImageUrl();
          });
          await this.getLargeScreenInfo();

          console.log('股道刷新');
        } else if (taskTime && num % (taskTime * 3) == 0) {
          if (this.dialog.showTask == 3) {
            this.currentScreenState = !this.currentScreenState;
            console.log('任务刷新');

            const childComponents = findComponentsDownward(this, 'track-details');
            console.log(childComponents);
            childComponents.forEach((child) => {
              child.swichImageUrl();
            });

            await this.getLargeScreenMessage();
          } else {
            console.log('系统刷新');
            await this.getLargeScreenMessage();
          }
        } else {
          console.log('系统刷新');
          if (this.dayPlanId && this.trainsetName && this.status) {
            await this.getLargeScreenInfo();
          } else {
            await this.getLargeScreenMessage();
          }
        }
      }, 20000);
    },

    // 切换车组一/二级修显示数据
    async trainRepairCut(index, val) {
      let trackPlaCode;
      this.pagedTrackDetails.forEach((item) => {
        item.queryProcessMonitorPlaList.forEach((v) => {
          if (v.trackPlaCode == val.trackPlaCode) {
            trackPlaCode = v.trainsetName;
          }
        });
      });

      if (val.num == 1) {
        let trainRepairImg = await this.getOneWorkProcessMonitorList(val.trackCode, trackPlaCode);
        let queryProcessMonitorPlaList = this.trackDetails[index].queryProcessMonitorPlaList;
        this.$set(queryProcessMonitorPlaList, val.index, trainRepairImg[0].queryProcessMonitorPlaList[0]);
      } else if (val.num == 2) {
        let trainRepairImg = await this.getTwoWorkProcessMonitorList(val.trackCode, trackPlaCode);
        let queryProcessMonitorPlaList = this.trackDetails[index].queryProcessMonitorPlaList;
        this.$set(queryProcessMonitorPlaList, val.index, trainRepairImg[0].queryProcessMonitorPlaList[0]);
      }
    },

    // 打开故障列表弹窗
    openFaultList(val) {
      this.fault.FaultProject = true;
      this.fault.FaultList = [];
      this.fault.loading = true;
      this.getFaultList(val);
    },

    // 获取故障列表
    getFaultList(val) {
      let carNo;
      if (val.carNo) {
        carNo = val.carNo;
      } else {
        carNo = '';
      }
      axios
        .get('/apiTrainRepair/processMonitor/getFaultList', {
          params: {
            unitCode: this.unitCode, // 运用所编码
            dayPlanId: this.dialog.dayplayid ? this.dialog.dayplayid : this.dayPlanId, // 日计划ID
            // dayPlanId: '2021-06-02-00', // 日计划ID
            trainsetId: val.trainsetId, // 车组ID
            carNo: carNo, // 辆序
          },
        })
        .then((res) => {
          this.fault.FaultList = res.data.data;
          this.fault.loading = false;
        })
        .catch(() => {
          this.fault.loading = false;
          this.$message({
            message: '获取故障列表失败',
            type: 'warning',
          });
        });
    },

    // 打开二级修作业详情弹窗
    openTaskDetailsList(val) {
      this.TaskDetails.TaskDetailsProject = true;
      this.TaskDetails.TaskDetailsList = [];
      this.TaskDetails.loading = true;
      this.getTwoItemList(val);
    },

    // 获取二级修作业详情
    getTwoItemList(val) {
      axios
        .post('/apiTrainRepair/processMonitor/getTwoItemList', {
          unitCode: this.unitCode, // 运用所编码
          dayPlanId: this.dialog.dayplayid ? this.dialog.dayplayid : this.dayPlanId, // 日计划ID
          // dayPlanId: '2021-06-02-00', // 日计划ID
          trainsetId: val.trainsetId, // 车组ID
          packetCode: val.packetCode, // 作业包编码
          packetName: val.packetName, // 作业包名称
        })
        .then((res) => {
          this.TaskDetails.TaskDetailsList = res.data.data;
          this.TaskDetails.TaskDetailsList.forEach((item) => {
            if (!item.trainsetName) {
              item.trainsetName = '';
            }
            item.trainsetName = val.trainsetName;
          });
        })
        .catch(() => {
          this.$message({
            message: '获取作业详情失败',
            type: 'warning',
          });
        })
        .finally(() => {
          this.TaskDetails.loading = false;
        })
    },

    // 通过车组名称获取股道信息
    async getTrainsetPostIon() {
      let res = await axios.get(
        `/apiTrainRepairMidGround/iTrainsetPostIonCur/getTrainsetPostIon?unitCode=${this.unitCode}&trackCodesJsonStr=&trainsetNameStr=${this.trainsetName}&pageNum=0&pageSize=10000`
      );

      this.trainCode = res.data.data.trainsetPostIonCurs[0].trackCode;
    },

    // 切换分页
    async handleCurrentChange(val) {
      this.currentPage = val;
      let trackArr = this.showTrackList.slice(val * 2 - 2, val * 2).join(',');
      this.trackIndex = val * 2 - 2;
      // 获取大屏信息
      await this.getLargeScreenInfo(trackArr);

      // 刷新进程
      await this.dataPeriodicallyRefreh();
    },

    // 获取流程节点
    async getFlowRunInfos(trainsetId) {
      let res = await axios.get('/apiTrainRepair/flowRun/getFlowRunInfosByTrainMonitor', {
        params: {
          flowPageCode: 'TRAIN_MONITOR',
          unitCode: this.unitCode,
          dayPlanId: this.dayPlanId ? this.dayPlanId : this.dialog.dayplayid,
          trainsetId,
        },
      });
      return res;
    },

    // 流程状态
    flowState(nodeList, runingNodeIdx) {
      if (nodeList.length === 0) return -1;
      if (runingNodeIdx === -1) {
        if (nodeList[nodeList.length - 1].finished) {
          return 1;
        } else {
          return -1;
        }
      } else {
        return 0;
      }
    },

    // 最后一个已完成的流程节点
    runingNode(nodeList) {
      let copyNodeList = JSON.parse(JSON.stringify(nodeList));
      copyNodeList.reverse();
      return copyNodeList.find((item) => item.finished) || {};
    },

    // 最后一个已完成的流程节点的下标
    runingNodeIdx(nodeList, runingNode) {
      return nodeList.findIndex((node) => node.id == runingNode.id);
    },

    headNodeList(runingNodeIdx, nodeList, isLong, flowState) {
      if (isLong == 1) {
        // 长编
        if (runingNodeIdx == -1) {
          if (flowState === -1) {
            return nodeList.slice(0, 6);
          }
          if (flowState === 1) {
            return nodeList.slice(-6);
          }
        } else {
          let startIdx = runingNodeIdx - 2;
          if (startIdx < 0) {
            startIdx = 0;
          }
          return nodeList.slice(startIdx, runingNodeIdx);
        }
      } else {
        // 短编
        if (runingNodeIdx == -1) {
          if (flowState === -1) {
            return nodeList.slice(0, 4);
          }
          if (flowState === 1) {
            return nodeList.slice(-4);
          }
        } else {
          return [];
          // let startIdx = runingNodeIdx - 1;
          // if (startIdx < 0) {
          //   startIdx = 0;
          // }
          // return nodeList.slice(startIdx, runingNodeIdx);
        }
      }
    },

    footNodeList(runingNodeIdx, nodeList, isLong) {
      if (isLong != '0') {
        // 长编
        if (runingNodeIdx == -1) {
          return [];
        } else {
          return nodeList.slice(runingNodeIdx + 1, runingNodeIdx + 4);
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
    // 长编（6）（前二后三）
    nodeShowList(nodeList, headNodeList, footNodeList, runingNode, runingNodeIdx, isLong) {
      if (runingNodeIdx == -1) {
        return headNodeList;
      } else {
        if (isLong != '0') {
          if (headNodeList.length < 2) {
            return nodeList.slice(0, 6);
          } else if (footNodeList.length < 3) {
            return nodeList.slice(-6);
          } else {
            return [...headNodeList, nodeList[runingNodeIdx], ...footNodeList];
          }
        } else {
          if (runingNodeIdx == -1) {
            return nodeList.slice(0, 4);
          } else if (footNodeList.length < 3) {
            return nodeList.slice(-4);
          } else {
            return [...headNodeList, nodeList[runingNodeIdx], ...footNodeList];
          }
        }
      }
    },

    showHeadLine(nodeShowList, nodeList) {
      if (nodeList.length == 0 || nodeShowList.length == 0) return false;
      if (nodeShowList[0].id == nodeList[0].id) {
        return false;
      } else {
        return true;
      }
    },

    showFootLine(nodeShowList, nodeList) {
      if (nodeList.length == 0 || nodeShowList.length == 0) return false;

      if (nodeShowList[nodeShowList.length - 1].id == nodeList[nodeList.length - 1].id) {
        return false;
      } else {
        return true;
      }
    },

    // 点解流程节点
    async nodeClick(val) {
      this.lookupNodeDealInfoDialog.visible = true;
      this.nodeList = this.flowRunInfoMap[val].nodeList;
    },

    showIframe(type, trainsetId) {
      if (type == 'one') {
        this.iframeSrc = `${iframeBaseUrlMap[type]}?dayPlanId=${this.dayPlanId}&trainsetName=${this.trainsetIdNameMap[trainsetId]}&status=1`;
      } else if (type == 'two') {
        this.iframeSrc = `${iframeBaseUrlMap[type]}?dayPlanId=${this.dayPlanId}&trainsetName=${this.trainsetIdNameMap[trainsetId]}&status=2`;
      } else {
        this.iframeSrc = `${iframeBaseUrlMap[type]}?trainsetId=${trainsetId}&flowId=${this.trainsetIdTaskMap[trainsetId].planLessKeyFlowId}`;
      }
      this.iframeVisible = true;
    },

    async getPostRoleList() {
      let url = '/apiTrainRepair/common/getPostRoleList';
      const {
        data: { code, data },
      } = await axios.get(url);
      if (code == 1) {
        this.roleList = data;
      }
    },

    cellStyle({ row, column, rowIndex, columnIndex }) {
      if (columnIndex == 0) return '';
      this.nodeColumns[columnIndex - 1].prop;
      if (row[this.nodeColumns[columnIndex - 1].prop]) {
        return 'background:#82c343 !important;';
      } else {
        return '';
      }
    },

    getDetail(info) {
      if (typeof info == 'object') {
        return Object.values(info).toString();
      } else {
        return '';
      }
    },

    cellClick(row, property) {
      if (!row.roleWorkerPicUrls[property]) return;
      this.workerPicMap = row.roleWorkerPicUrls[property];
      this.workerDetail = row.roleWorkerDetails[property];
      this.nodeDealDetailDialog.visible = true;
    },

    previewPicture(file, fileList) {
      this.imgUrl = file.url;

      this.prePics = fileList.map((item) => item.url);

      this.$refs.myImg.showViewer = true;
    },

    setTrackAreaShow(value, index) {
      if (value) {
        this.dialog.trackAreas[index].lstTrackInfo.forEach((item) => {
          this.dialog.trackList.push(item.trackCode);
        });
        this.dialog.trackList = [...new Set(this.dialog.trackList)];
      } else {
        this.dialog.trackAreas[index].lstTrackInfo.forEach((item) => {
          let indexOf = this.dialog.trackList.indexOf(item.trackCode);
          if (indexOf != -1) {
            this.dialog.trackList.splice(indexOf, 1);
          }
        });
      }
    },
  },
});
