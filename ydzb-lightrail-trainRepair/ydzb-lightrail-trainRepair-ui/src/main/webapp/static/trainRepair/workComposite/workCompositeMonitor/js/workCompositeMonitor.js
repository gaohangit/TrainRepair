let copyData = (data) => JSON.parse(JSON.stringify(data));

window.main = new Vue({
  el: '#main',

  data() {
    var checkRefreshTime = (rule, value, callback) => {
      let msg;
      if (rule.field == 'taskRefreshTime') {
        msg = '任务';
      } else {
        msg = '股道';
      }
      if (!value) {
        return callback(new Error(msg + '切换时间不能为空'));
      }
      setTimeout(() => {
        if (!Number.isInteger(Number(value))) {
          callback(new Error('请输入数字值'));
        } else {
          if (value < 30) {
            callback(new Error(msg + '切换时间最小为30秒'));
          } else {
            callback();
          }
        }
      }, 1000);
    };

    return {
      // 所编码
      unitCode: '',
      // 所名称
      unitName: '',
      // 班次
      dayPlanId: '',
      dayPlanIds: [],
      // 时间定时器
      timer: null,
      // 右上角时间
      time: '',
      baseImgPath: ctxPath + '/static/trainRepair/workRepairFlow/keyWorkFlowMonitor/img',
      isFullScreen: false,
      // 股道信息
      allTrackAreaList: [],
      // 所有在股道上车组信息
      allTrainsetList: [],
      flowRunInfoMap: {},
      hostLingFlowRunInfoMap: {},
      oneWorkerList: {},
      workPacketList: {},
      keyWorkFlowRunInfoWithTrainsetList: [],
      planlessKeyList: [],
      temporaryList: [],
      foucsNodeId: '',
      trainsetIdRepairTaskList: {},
      integrationList: {},
      nodeRecord: {},

      dataRefreh: null,
      // 当前任务页
      taskPageNum: 1,
      // 当前页
      pageNum: 1,

      localConfig: {
        showMode: '1',
        trackShowConfig: {},
        trackPostionShowConfig: {},
        showContentConfig: [],
        showContentType: {},
        taskRefreshTime: undefined, // 任务切换时间
        trackRefreshTime: undefined, // 股道切换时间
        ShowNoTrainsetTrack: false, // 是否显示无车组的股道信息
        ShowLastRepairTask: false, // 是否显示上一班次任务

        keyWorkFlowConfig: {},
      },
      staticColumns: [
        {
          key: 'TRAINSETID',
          label: '车组',
          property: 'trainsetId',
          fixed: false,
          type: 'static',
          width: '150',
        },
        {
          key: 'CONTENT',
          label: '作业内容',
          property: 'content',
          fixed: false,
          type: 'static',
          width: '150',
        },
        {
          key: 'STARTWORKERNAME',
          label: '录入人',
          property: 'startWorkerName',
          fixed: false,
          type: 'static',
          width: '120',
        },
        {
          key: 'STARTTIME',
          label: '录入时间',
          property: 'startTime',
          fixed: false,
          type: 'static',
          width: '160',
        },
        {
          key: 'STATE',
          label: '流程状态',
          property: 'state',
          fixed: false,
          type: 'static',
          width: '120',
        },
      ],
      columns: [],
      columnListMap: {},

      trackDetails: {},

      monitorConfig: {
        visible: false,
        showMode: '1',
        trackShowConfig: {},
        trackPostionShowConfig: {},
        showContentConfig: [],
        showContentType: {},
        taskRefreshTime: undefined, // 任务切换时间
        trackRefreshTime: undefined, // 股道切换时间
        ShowNoTrainsetTrack: false, // 是否显示无车组的股道信息
        ShowLastRepairTask: false, // 是否显示上一班次任务

        keyWorkFlowConfig: {},
      },

      monitorConfigRules: {
        taskRefreshTime: [{ validator: checkRefreshTime, trigger: 'blur' }],
        trackRefreshTime: [{ validator: checkRefreshTime, trigger: 'blur' }],
      },

      nodeList: [],
      roleList: [],
      lookupNodeDealInfoDialog: {
        visible: false,
      },

      keyWorkMonitorDialog: {
        visible: false,
        flowId: '',
      },
    };
  },

  async created() {
    await this.getUnitCode();
    await this.getUnitName();
    await this.getDay();
    await this.getPostRoleList();
    await this.getTrainsetPostIon();
    await this.getAllTrackArea();
    await this.getTaskFlowConfigList();
    await this.getKeyWorkExtraColumnList();
    await this.getConfigToLocal();

    await this.dataInfo();
  },

  mounted() {
    this.$nextTick(() => {
      // 时间
      if (this.$refs.time) {
        this.timer = setInterval(() => {
          this.$refs.time.innerText = dayjs().format('YYYY-MM-DD HH:mm:ss');
        }, 1000);
      }

      this.dataPeriodicallyRefreh();

      // 监听全屏
      window.addEventListener('resize', this.resize);
    });
  },

  computed: {
    workNameURL() {
      let imgURL = `${ctxPath}/static/trainRepair/projection/workRepairmonitor/img/`;
      if (this.fullscreen) {
        return {
          // 车头/车尾
          zero: imgURL + '0-2.png',
          one: imgURL + '1-2.png',

          icoPackage: imgURL + 'ico-package.png',
        };
      } else {
        return {
          // 车头/车尾
          zero: imgURL + '0-3.png',
          one: imgURL + '1-3.png',
          icoPackage: imgURL + 'ico-package-1.png',
        };
      }
    },

    allTrackAreaSortList({ allTrackAreaList }) {
      let data = copyData(allTrackAreaList);

      data.forEach((trackArea) => {
        trackArea.lstTrackInfo.sort((a, b) => a.sort - b.sort);
      });
      data.sort((a, b) => a.sort - b.sort);
      return data;
    },

    lineTableData() {
      let list = [];
      this.allTrackAreaSortList.forEach((item) => {
        item.lstTrackInfo.forEach((Track) => {
          if (this.monitorConfig.showMode == 1) {
            let obj = {};
            obj.trackAreaName = item.trackAreaName;
            obj.trackAreaCode = item.trackAreaCode;
            obj.trackName = Track.trackName;
            obj.trackCode = Track.trackCode;
            list.push(obj);
          } else {
            Track.lstTrackPositionInfo.forEach((TrackPosition) => {
              let obj = {};
              obj.trackAreaName = item.trackAreaName;
              obj.trackAreaCode = item.trackAreaCode;
              obj.trackName = Track.trackName;
              obj.trackCode = Track.trackCode;
              obj.trackPostionName = TrackPosition.trackPostionName;
              obj.trackPositionCode = TrackPosition.trackPositionCode;
              list.push(obj);
            });
          }
        });
      });

      return list;
    },

    trackList() {
      let trackList = this.allTrackAreaSortList.map((item) => item.lstTrackInfo).flat();
      return trackList;
    },

    trackCodeList() {
      let trackList = this.allTrackAreaSortList.map((item) => item.lstTrackInfo).flat();
      return trackList.map((track) => track.trackCode);
    },

    trackPositionList() {
      return this.trackList.map((track) => track.lstTrackPositionInfo).flat();
    },

    allTrackPositionList({ trackPositionTraiSetMap, trackList, trackPositionList, localConfig, trackHasTrainList }) {
      let getTrackList = copyData(trackList);
      let getTrackPositionList = copyData(trackPositionList);

      let list;
      // 勾选的股道/列位
      if (localConfig.showMode == 1) {
        list = getTrackList.filter((item) => localConfig.trackShowConfig[item.trackCode]);
        list = list.map((track) => track.lstTrackPositionInfo).flat();
      } else {
        list = getTrackPositionList.filter((item) => localConfig.trackPostionShowConfig[item.trackPositionCode]);
      }

      // 是否显示无车组的股道
      if (localConfig.ShowNoTrainsetTrack) {
        list = list.filter((item) => trackHasTrainList.includes(String(item.trackPositionCode)));
      }

      Object.keys(trackPositionTraiSetMap).forEach((key) => {
        if (trackPositionTraiSetMap[key].isLong == 1) {
          let index = list.findIndex((item) => {
            return item.trackPositionCode == trackPositionTraiSetMap[key].tailDirectionPlaCode;
          });

          if (index !== -1) {
            list.splice(index, 1);
          }
        }
      });

      return list;
    },

    // 列位对应的股道
    trackPositionTrackMap({ trackList, allTrackPositionList }) {
      return allTrackPositionList.reduce((prev, item, index) => {
        trackList.forEach((track) => {
          track.lstTrackPositionInfo.forEach((trackPosition) => {
            if (item.trackPositionCode == trackPosition.trackPositionCode) {
              prev[item.trackPositionCode] = track.trackCode;
            }
          });
        });
        return prev;
      }, {});
    },

    trackHasTrainList({ allTrainsetList }) {
      return [...new Set(allTrainsetList.map((item) => item.tailDirectionPlaCode))];
    },

    // 根据展示条数分组
    groupAllTrackPositionMap({ allTrackPositionList, localConfig }) {
      let pageSize;
      if (localConfig.showMode == 1) {
        pageSize = 1;
      } else {
        pageSize = 2;
      }
      return allTrackPositionList.reduce((prev, item, index) => {
        let pageNum = parseInt(index / pageSize) + 1;
        if (!prev[pageNum]) {
          prev[pageNum] = [];
        }
        prev[pageNum].push(item);
        return prev;
      }, {});
    },

    // 当前页所有的列位
    nowPageTrackPositions({ groupAllTrackPositionMap, pageNum }) {
      if (groupAllTrackPositionMap[pageNum]) {
        return groupAllTrackPositionMap[pageNum].map((item) => item.trackPositionCode);
      } else {
        return [];
      }
    },

    // 当前页所有的股道
    nowPageTracks({ trackList, nowPageTrackPositions }) {
      let list = [];
      nowPageTrackPositions.forEach((trackPosition) => {
        trackList.forEach((track) => {
          track.lstTrackPositionInfo.forEach((item) => {
            if (item.trackPositionCode == trackPosition) {
              list.push(track.trackCode);
            }
          });
        });
      });

      return Array.from(new Set(list));
    },

    // 列位对应的车组
    trackPositionTraiSetMap({ allTrainsetList }) {
      return allTrainsetList.reduce((pre, cur) => {
        pre[cur.headDirectionPlaCode] = cur;
        return pre;
      }, {});
    },

    // 列位对应的分页
    trackPositionPageMap({ allTrackPositionList }) {
      return allTrackPositionList.reduce((pre, cur) => {
        pre[cur.trackPositionCode] = {
          integrationPage: 0,
          oneWorkerPage: 0,
          workPacketPage: 0,
        };
        return pre;
      }, {});
    },

    trainsetIdNameMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        prev[item.trainsetId] = item.trainsetName;
        return prev;
      }, {});
    },

    allTrainsetNameList({ allTrainsetList }) {
      return allTrainsetList.map((item) => {
        return item.trainsetName;
      });
    },

    // 当前页的车组
    nowPageTrainIds({ nowPageTrackPositions, allTrainsetList }) {
      return allTrainsetList
        .filter((train) => nowPageTrackPositions.includes(Number(train.headDirectionPlaCode)))
        .map((train) => train.trainsetId);
    },

    spanArr() {
      let firstColumnPos, secondColumnPos;
      let arr = [];
      this.lineTableData.forEach((item, index) => {
        if (index === 0) {
          arr[0] = [];
          arr[0].push(1);
          if (this.localConfig.showMode == 2) {
            arr[1] = [];
            arr[1].push(1);
          }
          firstColumnPos = 0;
          secondColumnPos = 0;
        } else {
          if (item.trackAreaCode == this.lineTableData[index - 1].trackAreaCode) {
            arr[0][firstColumnPos] += 1;
            arr[0].push(0);
          } else {
            firstColumnPos = index;
            arr[0].push(1);
          }

          if (this.localConfig.showMode == 2) {
            if (item.trackCode == this.lineTableData[index - 1].trackCode) {
              arr[1][secondColumnPos] += 1;
              arr[1].push(0);
            } else {
              secondColumnPos = index;
              arr[1].push(1);
            }
          }
        }
      });

      return arr;
    },

    isShowAllTrack: {
      get() {
        return this.allTrackAreaList.every((trackArea) => {
          return trackArea.lstTrackInfo.every((track) => {
            if (this.monitorConfig.showMode == 1) {
              return this.getTrackShow(track.trackCode);
            } else {
              return track.lstTrackPositionInfo.every((trackPostion) => {
                return this.getTrackPostionShow(trackPostion.trackPositionCode);
              });
            }
          });
        });
      },
      set(value) {
        this.allTrackAreaList.forEach((trackArea) => {
          trackArea.lstTrackInfo.forEach((track) => {
            if (this.monitorConfig.showMode == 1) {
              this.setTrackShow(track.trackCode, value);
            } else {
              track.lstTrackPositionInfo.forEach((trackPostion) => {
                this.setTrackPostionShow(trackPostion.trackPositionCode, value);
              });
            }
          });
        });
      },
    },

    isShowAlltrackArea: {
      get() {
        let list = [];
        this.allTrackAreaList.forEach((trackArea) => {
          if (this.monitorConfig.showMode == 1) {
            let trackCheck = trackArea.lstTrackInfo.every((track) => {
              return this.getTrackShow(track.trackCode);
            });

            return trackArea.lstTrackInfo.map((item) => {
              if (trackCheck) {
                list.push(true);
              } else {
                list.push(false);
              }
            });
          } else {
            let trackCheck = trackArea.lstTrackInfo.every((track) => {
              return track.lstTrackPositionInfo.every((trackPostion) => {
                return this.getTrackPostionShow(trackPostion.trackPositionCode);
              });
            });

            return trackArea.lstTrackInfo.map((track) => {
              return track.lstTrackPositionInfo.every((trackPostion) => {
                if (trackCheck) {
                  list.push(true);
                } else {
                  list.push(false);
                }
              });
            });
          }
        });
        return list;
      },
    },

    getConfig() {
      if (this.monitorConfig.visible) {
        return this.monitorConfig;
      } else {
        return this.localConfig;
      }
    },

    showContentConfigData() {
      return this.getConfig.showContentConfig.sort((a, b) => {
        return a['sort'] - b['sort'];
      });
    },

    lastIndex() {
      return this.localConfig.showContentConfig.length - 1;
    },

    taskRefreshTimeShow() {
      let showList = this.showContentConfigData.filter((item) => {
        return item.code != 'oneOrTwoFlow' && item.isShow;
      });
      if (this.getConfig.showMode == 1) {
        if (showList.length > 1) {
          return true;
        } else {
          return false;
        }
      } else {
        if (showList.length > 2) {
          return true;
        } else {
          return false;
        }
      }
    },

    trackRefreshTimeShow() {
      let showList = [];
      if (this.getConfig.showMode == 1) {
        Object.keys(this.getConfig.trackShowConfig).forEach((item) => {
          if (this.getConfig.trackShowConfig[item]) {
            showList.push(this.getConfig.trackShowConfig[item]);
          }
        });
      } else {
        Object.keys(this.getConfig.trackPostionShowConfig).forEach((item) => {
          if (this.getConfig.trackPostionShowConfig[item]) {
            showList.push(this.getConfig.trackPostionShowConfig[item]);
          }
        });
      }
      if (showList.length > 1) {
        return true;
      } else {
        return;
      }
    },

    showFlowFault({ localConfig }) {
      return localConfig.showContentConfig.filter((item) => {
        return item.code == 'oneOrTwoFlow' || item.code == 'fault';
      });
    },

    showContent({ localConfig }) {
      return localConfig.showContentConfig.filter((item) => {
        return item.code !== 'oneOrTwoFlow' && item.code !== 'fault' && item.isShow;
      });
    },

    showPageContent({ showContent, localConfig }) {
      let pageSize;
      if (localConfig.showMode == 1) {
        pageSize = 2;
      } else {
        pageSize = 1;
      }
      return showContent.reduce((prev, item, index) => {
        let pageNum = parseInt(index / pageSize) + 1;
        if (!prev[pageNum]) {
          prev[pageNum] = [];
        }
        prev[pageNum].push(item);
        return prev;
      }, {});
    },

    showNextPageContent({ showPageContent, taskPageNum }) {
      if (showPageContent[taskPageNum]) {
        return showPageContent[taskPageNum].map((item) => item);
      } else {
        return [];
      }
    },

    showFlowRunList() {
      return (trainsetId) => {
        if (this.flowRunInfoMap[trainsetId]) {
          return this.flowRunInfoMap[trainsetId];
        } else if (this.hostLingFlowRunInfoMap[trainsetId]) {
          return this.hostLingFlowRunInfoMap[trainsetId];
        } else {
          return [];
        }
      };
    },

    state_1() {
      return (node) => {
        return node.finished && node.couldDisposeRoleIds.length == 0;
      };
    },
    state_2() {
      return (node) => {
        return node.finished && node.couldDisposeRoleIds.length != 0;
      };
    },
    state_3() {
      return (node) => {
        if (!this.nodeListId) {
          return false;
        } else {
          return !node.finished && node.couldDisposeRoleIds.length != 0;
        }
      };
    },
    state_4() {
      return (node) => {
        return !node.finished && node.couldDisposeRoleIds.length == 0;
      };
    },

    trainsetIdFlowIdDataMap({ keyWorkFlowRunInfoWithTrainsetList, planlessKeyIdNodesMap }) {
      return keyWorkFlowRunInfoWithTrainsetList.reduce((prev, item) => {
        prev[item.trainsetId] = item.keyWorkFlowRunInfos.reduce((pre, itm) => {
          if (planlessKeyIdNodesMap[itm.flowId]) {
            if (!pre[itm.flowId]) {
              pre[itm.flowId] = [];
            }
            pre[itm.flowId].push(itm);
          }

          return pre;
        }, {});

        return prev;
      }, {});
    },

    planlessKeyIdNodesMap({ planlessKeyList }) {
      return planlessKeyList.reduce((prev, item) => {
        prev[item.id] = item.nodes;
        return prev;
      }, {});
    },

    // 所有节点的nodeRecord
    flowRunNodeIdRecordsMap({ keyWorkFlowRunInfoWithTrainsetList }) {
      let obj = {};
      keyWorkFlowRunInfoWithTrainsetList.forEach((train) => {
        train.keyWorkFlowRunInfos.forEach((flowRun) => {
          const nodeRecords = flowRun.nodeWithRecords.reduce((prev, node) => {
            let record;
            if (node.nodeRecords.length !== 1) {
              record = node.nodeRecords
                .map((item) => {
                  return item.workerName;
                })
                .toString();
            } else {
              record = `${node.nodeRecords[0].workerName}<br>${node.nodeRecords[0].recordTime}`;
            }

            if (flowRun.flowRunRecordInfo && flowRun.flowRunRecordInfo.nodeId === node.id) {
              record = `<span style="color: red;">驳回: ${flowRun.flowRunRecordInfo.workerName}<br>${dayjs(
                flowRun.flowRunRecordInfo.recordTime
              ).format('YYYY-MM-DD HH:mm:ss')}</span>`;
            }

            prev[node.id] = record;
            return prev;
          }, {});

          obj[flowRun.id] = nodeRecords;
        });
      });
      return obj;
    },

    trainsetIdTemporaryMap({ temporaryList }) {
      return temporaryList.reduce((prev, item) => {
        prev[item.trainsetId] = item.nodeInfoForSimpleShows;
        return prev;
      }, {});
    },

    flowMap({ temporaryList }) {
      return temporaryList.reduce((prev, temporary) => {
        temporary.nodeInfoForSimpleShows.forEach((item) => {
          if (item.remark) {
            prev[item.id] = item.remark;
          } else {
            prev[item.id] = '无';
          }
        });
        return prev;
      }, {});
    },

    nodeRecordList() {
      return (temporaryList) => {
        if (temporaryList) {
          return temporaryList
            .map((node) => {
              return node.nodeRecords;
            })
            .flat()
            .reverse();
        }
      };
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

    roleCodeNameMap({ roleList }) {
      return roleList.reduce((prev, item) => {
        prev[item.code] = item.name;
        return prev;
      }, {});
    },

    nodeIdNameMap({ trainsetIdTemporaryMap }) {
      let obj = {};
      Object.keys(trainsetIdTemporaryMap).forEach((trainsetId) => {
        let flowList = trainsetIdTemporaryMap[trainsetId];
        let nameMap = flowList.reduce((prev, item) => {
          prev[item.id] = item.name;
          item.children.forEach((subNode) => {
            prev[subNode.id] = subNode.name;
          });
          return prev;
        }, {});
        obj[trainsetId] = nameMap;
      });
      return obj;
    },

    nodeColumns({ nodeList, roleCodeNameMap }) {
      if (nodeList) {
        let roleIds = [
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

        roleIds = roleIds.filter((item) => roleCodeNameMap[item]);

        return roleIds.map((id) => {
          return {
            id: id,
            label: roleCodeNameMap[id],
            prop: id,
          };
        });
      }
    },
  },

  watch: {
    async taskPageNum() {
      this.showNextPageContent.forEach(async (item) => {
        if (item.code == 'oneWork') {
          await this.getOneWorkProcessMonitorList(this.nowPageTracks.join(','), '');
        } else if (item.code == 'twoWork') {
          await this.getTwoWorkProcessMonitorList(this.nowPageTracks.join(','), '');
        } else if (item.code == 'keyWork') {
          await this.getKeyWorkFlowRunInfoWithTrainsetList();
        } else if (item.code == 'temporary') {
          await this.getTemporaryFlowRunInfoWithTrainsetList();
        }
      });

      for (let i = 0; i < this.nowPageTrackPositions.length; i++) {
        let train = this.trackPositionTraiSetMap[this.nowPageTrackPositions[i]];
        if (train) {
          await this.getRepairTask(train.trainsetId);
          await this.setFlowRunInfoMap(train.trainsetId, train.isLong);
          await this.setHostLingFlowRunInfos(train.trainsetId, train.isLong);
        }
      }
    },

    pageNum() {
      this.dataInfo();
    },

    planlessKeyList(list) {
      list.forEach((item) => {
        this.$set(this.localConfig.keyWorkFlowConfig, item.id, {});
      });
    },
  },

  methods: {
    // 页面窗口变化
    resize() {
      if (
        document.fullscreenElement ||
        document.webkitFullscreenElement ||
        document.mozFullscreenElement ||
        document.msFullscreenElement
      ) {
        this.isFullScreen = true;
      } else {
        this.isFullScreen = false;
      }
    },

    // 进入全屏
    requestFullscreen() {
      let element = document.documentElement;
      if (element.requestFullscreen) {
        element.requestFullscreen();
      } else if (element.webkitRequestFullScreen) {
        element.webkitRequestFullScreen();
      } else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
      } else if (element.msRequestFullscreen) {
        // IE11
        element.msRequestFullscreen();
      }
    },

    // 退出全屏
    exitFullscreen() {
      if (document.exitFullscreen) {
        document.exitFullscreen();
      } else if (document.webkitCancelFullScreen) {
        document.webkitCancelFullScreen();
      } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
      } else if (document.msExitFullscreen) {
        document.msExitFullscreen();
      }
    },

    // 右上角的全屏点击
    fullscreenClick() {
      if (!this.isFullScreen) {
        this.requestFullscreen();
      } else {
        this.exitFullscreen();
      }
    },

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

    //获取作业班次(日计划)
    async getDay() {
      let {
        data: { dayPlanId },
      } = await axios.get('/apiTrainRepair/common/getDay');
      this.dayPlanId = dayPlanId;

      let dayPlanArr = dayPlanId.split('-');
      const datType = dayPlanArr.pop();
      const time = dayPlanArr.join('-');
      if (datType == '00') {
        this.dayPlanIds = [`${dayjs(time).subtract(1, 'day').format('YYYY-MM-DD')}-01`, dayPlanId];
      } else {
        this.dayPlanIds = [`${dayjs(time).format('YYYY-MM-DD')}-00`, dayPlanId];
      }
    },

    // 获取显示股道
    async getAllTrackArea() {
      const { data } = await axios.get('/apiTrainRepair/trackPowerStateCur/getAllTrackArea');
      this.allTrackAreaList = data.data.trackAreas || [];
    },

    setTrackAreaShow(value, index) {
      this.allTrackAreaList.forEach((trackArea) => {
        if (trackArea.trackAreaCode == this.lineTableData[index].trackAreaCode) {
          trackArea.lstTrackInfo.forEach((track) => {
            if (this.monitorConfig.showMode == 1) {
              this.setTrackShow(track.trackCode, value);
            } else {
              track.lstTrackPositionInfo.forEach((trackPostion) => {
                this.setTrackPostionShow(trackPostion.trackPositionCode, value);
              });
            }
          });
        }
      });
    },

    // 获取配置信息
    getConfigToLocal() {
      const workCompositeConfigStr = localStorage.getItem('workCompositeConfig');
      if (workCompositeConfigStr) {
        this.localConfig = JSON.parse(workCompositeConfigStr);

        // 更新修复keyWorkFlowConfig
        let keyWorkFlowConfig = {};
        for (const flowId in this.planlessKeyIdNodesMap) {
          if (this.localConfig.keyWorkFlowConfig[flowId]) {
            keyWorkFlowConfig[flowId] = this.localConfig.keyWorkFlowConfig[flowId];
          } else {
            keyWorkFlowConfig[flowId] = {};
            keyWorkFlowConfig[flowId].leftList = [];
            keyWorkFlowConfig[flowId].rightList = [
              ...this.staticColumns,
              ...this.columns,
              ...this.planlessKeyIdNodesMap[flowId].map((node) => {
                return {
                  key: node.id,
                  label: node.name,
                  property: node.id,
                  type: 'node',
                };
              }),
            ];
          }
        }
        this.$set(this.localConfig, 'keyWorkFlowConfig', keyWorkFlowConfig);
      } else {
        this.localConfig.showMode = '1';

        this.trackList.forEach((track) => {
          this.$set(this.localConfig.trackShowConfig, track.trackCode, true);
          track.lstTrackPositionInfo.forEach((trackPostion) => {
            this.$set(this.localConfig.trackPostionShowConfig, trackPostion.trackPositionCode, true);
          });
        });

        this.localConfig.showContentConfig = [
          {
            code: 'oneOrTwoFlow',
            name: '一、二级修作业流程',
            sort: 1,
            isShow: true,
          },
          {
            code: 'fault',
            name: '故障信息',
            sort: 2,
            isShow: true,
          },
          {
            code: 'keyWork',
            name: '关键作业',
            sort: 3,
            isShow: true,
          },
          {
            code: 'oneWork',
            name: '一级修作业过程',
            sort: 4,
            isShow: true,
          },
          {
            code: 'twoWork',
            name: '二级修作业过程',
            sort: 5,
            isShow: true,
          },
          {
            code: 'temporary',
            name: '临修作业流程',
            sort: 6,
            isShow: true,
          },
        ];

        this.localConfig.showContentConfig.forEach((item) => {
          this.localConfig.showContentType[item.code] = '1';
        });

        this.localConfig.taskRefreshTime = '30';
        this.localConfig.trackRefreshTime = '30';
        this.localConfig.ShowNoTrainsetTrack = false;
        this.localConfig.ShowLastRepairTask = false;

        for (const key in this.localConfig.keyWorkFlowConfig) {
          this.$set(this.localConfig.keyWorkFlowConfig[key], 'leftList', []);
          this.$set(this.localConfig.keyWorkFlowConfig[key], 'rightList', [
            ...this.staticColumns,
            ...this.columns,
            ...this.planlessKeyIdNodesMap[key].map((node) => {
              return {
                key: node.id,
                label: node.name,
                property: node.id,
                type: 'node',
              };
            }),
          ]);
        }
      }
    },

    // 修改配置信息
    setConfigToLocal() {
      const workCompositeConfigStr = JSON.stringify({
        showMode: this.monitorConfig.showMode,
        trackShowConfig: this.monitorConfig.trackShowConfig,
        trackPostionShowConfig: this.monitorConfig.trackPostionShowConfig,
        showContentConfig: this.monitorConfig.showContentConfig,
        showContentType: this.monitorConfig.showContentType,
        taskRefreshTime: this.monitorConfig.taskRefreshTime,
        trackRefreshTime: this.monitorConfig.trackRefreshTime,
        ShowNoTrainsetTrack: this.monitorConfig.ShowNoTrainsetTrack,
        ShowLastRepairTask: this.monitorConfig.ShowLastRepairTask,
        keyWorkFlowConfig: this.monitorConfig.keyWorkFlowConfig,
      });
      localStorage.setItem('workCompositeConfig', workCompositeConfigStr);

      this.taskPageNum = 1;
      this.pageNum = 1;
    },

    // 打开配置弹窗
    openMonitorDialog() {
      this.monitorConfig.showMode = copyData(this.localConfig.showMode);
      this.monitorConfig.trackShowConfig = copyData(this.localConfig.trackShowConfig);
      this.monitorConfig.trackPostionShowConfig = copyData(this.localConfig.trackPostionShowConfig);
      this.monitorConfig.showContentConfig = copyData(this.localConfig.showContentConfig);
      this.monitorConfig.showContentType = copyData(this.localConfig.showContentType);
      this.monitorConfig.taskRefreshTime = copyData(this.localConfig.taskRefreshTime);
      this.monitorConfig.trackRefreshTime = copyData(this.localConfig.trackRefreshTime);
      this.monitorConfig.ShowNoTrainsetTrack = copyData(this.localConfig.ShowNoTrainsetTrack);
      this.monitorConfig.ShowLastRepairTask = copyData(this.localConfig.ShowLastRepairTask);
      this.monitorConfig.keyWorkFlowConfig = copyData(this.localConfig.keyWorkFlowConfig);
      this.monitorConfig.visible = true;
    },

    // 合并方法
    spanMethod({ row, column, rowIndex, columnIndex }) {
      if (this.spanArr[columnIndex]) {
        let _row = this.spanArr[columnIndex][rowIndex];
        let _col = _row > 0 ? 1 : 0;
        return {
          rowspan: _row,
          colspan: _col,
        };
      }
    },

    // 显示股道
    getTrackShow(trackCode) {
      if (trackCode in this.monitorConfig.trackShowConfig) {
        return this.monitorConfig.trackShowConfig[trackCode];
      } else {
        return false;
      }
    },

    // 显示列位
    getTrackPostionShow(trackPositionCode) {
      if (trackPositionCode in this.monitorConfig.trackPostionShowConfig) {
        return this.monitorConfig.trackPostionShowConfig[trackPositionCode];
      } else {
        return false;
      }
    },

    // 修改显示股道
    setTrackShow(trackCode, value) {
      this.$set(this.monitorConfig.trackShowConfig, trackCode, value);
    },

    // 修改显示列位
    setTrackPostionShow(trackPositionCode, value) {
      this.$set(this.monitorConfig.trackPostionShowConfig, trackPositionCode, value);
    },

    changeIsShow(code, value) {
      this.monitorConfig.showContentConfig.forEach((item) => {
        if (item.code == code) {
          item.isShow = value;
        }
      });
    },

    getShowContentImg(code) {
      console.log(code);
    },

    sortChange(type, code) {
      let showContentConfig = this.monitorConfig.showContentConfig;
      let showContentIndex = showContentConfig.findIndex((item) => item.code === code);
      if (type === 'up') {
        const showContentSort = showContentConfig[showContentIndex].sort;
        showContentConfig[showContentIndex].sort = showContentConfig[showContentIndex - 1].sort;
        showContentConfig[showContentIndex - 1].sort = showContentSort;
      } else {
        const showContentSort = showContentConfig[showContentIndex].sort;
        showContentConfig[showContentIndex].sort = showContentConfig[showContentIndex + 1].sort;
        showContentConfig[showContentIndex + 1].sort = showContentSort;
      }
    },

    updMonitorConfigBtn(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          if (this.taskRefreshTimeShow && this.trackRefreshTimeShow) {
            if (this.monitorConfig.taskRefreshTime <= this.monitorConfig.trackRefreshTime) {
              this.monitorConfig.visible = false;
              this.setConfigToLocal();
              this.getConfigToLocal();

              await this.dataInfo();
              this.dataPeriodicallyRefreh();
            } else {
              this.$message({
                message: '股道切换时间不能小于任务切换时间!',
                type: 'warning',
              });
            }
          } else {
            this.monitorConfig.visible = false;
            this.setConfigToLocal();
            this.getConfigToLocal();

            await this.dataInfo();
            this.dataPeriodicallyRefreh();
          }
        }
      });
    },

    updMonitorCancel(formName) {
      this.$refs[formName].resetFields();
      this.monitorConfig.visible = false;
    },

    /**
     * 根据运用所和股道集合获取车组位置信息
     * unitCode 单位编码 trackCodesJsonStr股道编码 trainsetNameStr车组名称
     */
    async getTrainsetPostIon(trackCodesJsonStr, trainsetNameStr) {
      let { data } = await axios.get('/apiTrainRepair/trainsetPostIonCur/getTrainsetPostIon', {
        params: {
          unitCode: this.unitCode,
          trackCodesJsonStr: trackCodesJsonStr,
          trainsetNameStr: trainsetNameStr,
        },
      });

      this.allTrainsetList = data.data;
    },

    async setFlowRunInfoMap(trainsetId, isLong) {
      let {
        data: {
          data: { flowRunInfoGroups },
        },
      } = await this.getFlowRunInfos(trainsetId);

      if (flowRunInfoGroups.length == 0) {
        this.flowRunInfoMap[trainsetId] = {};
      } else if (flowRunInfoGroups[0].flowRunInfoForSimpleShows.length == 0) {
        this.flowRunInfoMap[trainsetId] = {};
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

    async setHostLingFlowRunInfos(trainsetId, isLong) {
      let {
        data: {
          data: { flowRunInfoGroups },
        },
      } = await this.getHostLingFlowRunInfos(trainsetId);

      if (flowRunInfoGroups.length == 0) {
        this.hostLingFlowRunInfoMap[trainsetId] = {};
      } else if (flowRunInfoGroups[0].flowRunInfoForSimpleShows.length == 0) {
        this.hostLingFlowRunInfoMap[trainsetId] = {};
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
        this.hostLingFlowRunInfoMap[trainsetId] = {
          nodeList,
          nodeShowList,
          showHeadLine,
          showFootLine,
          flowId: flowRunInfoGroups[0].flowRunInfoForSimpleShows[0].id,
        };
      }
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

    // 正在进行的流程节点
    runingNode(nodeList) {
      let copyNodeList = JSON.parse(JSON.stringify(nodeList));
      copyNodeList.reverse();
      return copyNodeList.find((item) => item.finished) || {};
    },

    // 正在进行的流程节点的下标
    runingNodeIdx(nodeList, runingNode) {
      return nodeList.findIndex((node) => node.id == runingNode.id);
    },

    headNodeList(runingNodeIdx, nodeList, isLong, flowState) {
      if (isLong == 1) {
        // 长编
        if (runingNodeIdx == -1) {
          if (flowState === -1) {
            return nodeList.slice(0, 10);
          }
          if (flowState === 1) {
            return nodeList.slice(-10);
          }
        } else {
          let startIdx = runingNodeIdx - 3;
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
          return nodeList.slice(runingNodeIdx + 1, runingNodeIdx + 7);
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

    // 短编（4）（前一后二）
    // 长编（10）（前四后五）
    nodeShowList(nodeList, headNodeList, footNodeList, runingNode, runingNodeIdx, isLong) {
      if (runingNodeIdx == -1) {
        return headNodeList;
      } else {
        if (isLong != '0') {
          if (headNodeList.length < 3) {
            return nodeList.slice(0, 10);
          } else if (footNodeList.length < 6) {
            return nodeList.slice(-10);
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
    async nodeClick(trainsetId) {
      // this.lookupNodeDealInfoDialog.visible = true;
      // this.nodeList = this.flowRunInfoMap[trainsetId].nodeList;
    },

    // 获取一二级修流程节点
    async getFlowRunInfos(trainsetId) {
      let res = await axios.get('/apiTrainRepair/flowRun/getFlowRunInfosByTrainMonitor', {
        params: {
          flowPageCode: 'TRAIN_MONITOR',
          unitCode: this.unitCode,
          dayPlanId: this.dayPlanId,
          trainsetId,
          showDayRepairTask: this.localConfig.ShowLastRepairTask,
        },
      });
      return res;
    },

    // 获取整备修流程节点
    async getHostLingFlowRunInfos(trainsetId) {
      let res = await axios.get('/apiTrainRepair/flowRun/getFlowRunInfosByTrainMonitor', {
        params: {
          flowPageCode: 'HOSTLING',
          unitCode: this.unitCode,
          dayPlanId: this.dayPlanId,
          trainsetId,
          showDayRepairTask: this.localConfig.ShowLastRepairTask,
        },
      });
      return res;
    },

    async dataInfo() {
      if (this.pageNum > Object.keys(this.groupAllTrackPositionMap).length) {
        return (this.pageNum = 1);
      }
      if (this.taskPageNum > Object.keys(this.showPageContent).length) {
        this.taskPageNum == 1;
      }
      await this.getOneWorkProcessMonitorList();
      await this.getTwoWorkProcessMonitorList();
      await this.getKeyWorkFlowRunInfoWithTrainsetList();
      await this.getTemporaryFlowRunInfoWithTrainsetList();
      for (let i = 0; i < this.nowPageTrackPositions.length; i++) {
        let train = this.trackPositionTraiSetMap[this.nowPageTrackPositions[i]];
        if (train) {
          await this.getRepairTask(train.trainsetId);
          await this.setFlowRunInfoMap(train.trainsetId, train.isLong);
          await this.setHostLingFlowRunInfos(train.trainsetId, train.isLong);
        }
      }
    },

    async getOneWorkProcessMonitorList() {
      let {
        data: { data },
      } = await axios.post('/apiTrainRepair/processMonitor/getOneWorkProcessMonitorList', {
        trackCodesJsonStr: this.nowPageTracks.join(','), // 股道集合
        unitCode: this.unitCode, // 运用所编码
        dayPlanId: this.dayPlanId, // 日计划ID
        trainsetNameStr: '', // 车组名称集合
      });

      for (let i = 0; i < data.length; i++) {
        for (let j = 0; j < data[i].queryProcessMonitorPlaList.length; j++) {
          let queryProcessMonitorPla = copyData(data[i].queryProcessMonitorPlaList[j]);
          let pages = Math.trunc(queryProcessMonitorPla.integrationList.length / 5);
          let a = queryProcessMonitorPla.integrationList.length % 5;

          if (a != 0) {
            pages++;
          }
          let list = [];
          for (let pageIndex = 0; pageIndex < pages; pageIndex++) {
            list.push(queryProcessMonitorPla.integrationList.slice(pageIndex * 5, (pageIndex + 1) * 5));
          }
          this.$set(this.integrationList, queryProcessMonitorPla.trackPlaCode, copyData(list));

          this.$set(this.trackDetails, queryProcessMonitorPla.trackPlaCode, {
            queryProcessMonitorPlaList: [copyData(queryProcessMonitorPla)],
            trackCode: data[i].trackCode,
            trackName: data[i].trackName,
          });

          let oneWorkerList = JSON.parse(JSON.stringify(queryProcessMonitorPla.oneWorkerList));
          let pageLines = 8;
          let oneItemWithWorkerList = [];

          oneWorkerList.forEach((oneWorker) => {
            oneWorker.oneItemList.forEach((oneItem) => {
              oneItemWithWorkerList.push({
                oneItem: oneItem,
                workerId: oneWorker.workerId,
                workerName: oneWorker.workerName,
              });
            });
          });

          let pagesData = [];
          let page = 1;
          let b = Math.trunc(oneItemWithWorkerList.length / pageLines);
          let c = oneItemWithWorkerList.length % pageLines;
          if (c == 0) {
            page = b;
          } else {
            page = b + 1;
          }
          for (let pageIndex = 0; pageIndex < page; pageIndex++) {
            let nextPageData = oneItemWithWorkerList.slice(pageIndex * pageLines, (pageIndex + 1) * pageLines);
            let obj = {};
            nextPageData.forEach((item) => {
              if (!obj[item.workerId]) {
                obj[item.workerId] = {
                  workerId: item.workerId,
                  workerName: item.workerName,
                  oneItemList: [],
                };
              }

              obj[item.workerId].oneItemList.push(item.oneItem);
            });
            let list = [];
            Object.keys(obj).forEach((item) => {
              list.push(obj[item]);
            });
            pagesData.push(list);
          }

          this.$set(this.oneWorkerList, queryProcessMonitorPla.trackPlaCode, copyData(pagesData));
        }
      }
    },

    async getTwoWorkProcessMonitorList() {
      let {
        data: { data },
      } = await axios.post('/apiTrainRepair/processMonitor/getTwoWorkProcessMonitorList', {
        trackCodesJsonStr: this.nowPageTracks.join(','), // 股道集合
        unitCode: this.unitCode, // 运用所编码
        dayPlanId: this.dayPlanId, // 日计划ID
        trainsetNameStr: '', // 车组名称集合
      });

      for (let i = 0; i < data.length; i++) {
        for (let j = 0; j < data[i].queryProcessMonitorPlaList.length; j++) {
          let queryProcessMonitorPla = copyData(data[i].queryProcessMonitorPlaList[j]);
          let pages = Math.trunc(queryProcessMonitorPla.workPacketList.length / 5);
          let a = queryProcessMonitorPla.workPacketList.length % 5;

          if (a != 0) {
            pages++;
          }
          let list = [];
          for (let pageIndex = 0; pageIndex < pages; pageIndex++) {
            list.push(queryProcessMonitorPla.workPacketList.slice(pageIndex * 5, (pageIndex + 1) * 5));
          }
          this.$set(this.workPacketList, queryProcessMonitorPla.trackPlaCode, copyData(list));
        }
      }
    },

    // 二级修进度条宽度
    getWidth(complented, sum) {
      let percent = Math.round((complented / sum) * 100);
      return percent + '%';
    },

    // 获取车组信息(包含表格数据)
    async getKeyWorkFlowRunInfoWithTrainsetList() {
      let {
        data: { data },
      } = await axios.get('/apiTrainRepair/flowRun/getKeyWorkFlowRunInfoWithTrainsetList', {
        params: {
          dayPlanIds: this.dayPlanIds.join(','),
          unitCode: this.unitCode,
        },
      });
      this.keyWorkFlowRunInfoWithTrainsetList = data;
    },

    /**
     * 查询关键作业额外列
     */
    async getKeyWorkExtraColumnList() {
      let {
        data: { data },
      } = await axios.get('/apiTrainRepair/KeyWorkConfig/getKeyWorkExtraColumnList');
      this.columns = data;
    },

    // 获取所有关键作业流程
    async getTaskFlowConfigList() {
      let {
        data: { data },
      } = await axios.get('/apiTrainRepair/taskFlowConfig/getTaskFlowConfigList', {
        params: {
          flowTypeCode: 'PLANLESS_KEY',
        },
      });

      this.planlessKeyList = data;
    },

    getTableHeight(flowInfo) {
      const flowList = Object.keys(flowInfo);
      const num = flowList.length;
      return 100 / num + '%';
    },

    tableRowClassName({ row, rowIndex }) {
      if (rowIndex % 2 === 1) {
        return 'odd';
      } else {
        return 'even';
      }
    },

    getColumnLabel(row, { key, property }) {
      if (key === 'FUNCTION_CLASS') {
        return (
          this.columnListMap[`${key}FlatList`] &&
          this.columnListMap[`${key}FlatList`].find((item) => item.code == row[property]) &&
          this.columnListMap[`${key}FlatList`].find((item) => item.code == row[property]).name
        );
      } else if (key === 'BATCH_BOM_NODE_CODE') {
        return (
          this.columnListMap[`${row.trainsetId}${key}FlatList`] &&
          this.columnListMap[`${row.trainsetId}${key}FlatList`].find((item) => item.code == row[property]) &&
          this.columnListMap[`${row.trainsetId}${key}FlatList`].find((item) => item.code == row[property]).name
        );
      } else {
        return (
          this.columnListMap[`${key}List`] &&
          this.columnListMap[`${key}List`].find((item) => item.value == row[property]) &&
          this.columnListMap[`${key}List`].find((item) => item.value == row[property]).label
        );
      }
    },

    /**
     * 临修作业流程监控
     *
     */
    async getTemporaryFlowRunInfoWithTrainsetList() {
      let {
        data: { data },
      } = await axios.get('/apiTrainRepair/flowRun/getTemporaryFlowRunInfoWithTrainsetList', {
        params: {
          dayPlanIds: this.dayPlanIds.join(','),
          unitCode: this.unitCode,
        },
      });

      this.temporaryList = data;

      this.temporaryList.forEach((item) => {
        this.nodeRecord[item.trainsetId] = item.nodeInfoForSimpleShows && item.nodeInfoForSimpleShows[0].remark;
      });
    },

    nodeChange(trainsetId, id) {
      this.foucsNodeId = id;
      this.nodeRecord[trainsetId] = this.flowMap[id] || '无';
    },

    // 系统默认进程刷新
    dataPeriodicallyRefreh() {
      let num = 0;
      if (this.dataRefreh != null) {
        clearInterval(this.dataRefreh);
      }

      this.dataRefreh = setInterval(() => {
        if (num >= 15) {
          if (this.taskRefreshTimeShow && num % this.localConfig.trackRefreshTime == 0) {
            this.pageNum++;
          } else if (this.trackRefreshTimeShow && num % this.localConfig.taskRefreshTime == 0) {
            this.taskPageNum++;
          } else if (num % 15 == 0) {
            this.nowPageTrackPositions.forEach((item) => {
              this.$set(
                this.trackPositionPageMap[item],
                'oneWorkerPage',
                this.trackPositionPageMap[item].oneWorkerPage + 1
              );

              if (
                this.oneWorkerList[item] &&
                this.oneWorkerList[item].length &&
                this.trackPositionPageMap[item].oneWorkerPage > this.oneWorkerList[item].length - 1
              ) {
                this.$set(this.trackPositionPageMap[item], 'oneWorkerPage', 0);
              }

              this.$set(
                this.trackPositionPageMap[item],
                'workPacketPage',
                this.trackPositionPageMap[item].workPacketPage + 1
              );
              if (
                this.workPacketList[item] &&
                this.workPacketList[item].length &&
                this.trackPositionPageMap[item].workPacketPage > this.workPacketList[item].length - 1
              ) {
                this.$set(this.trackPositionPageMap[item], 'workPacketPage', 0);
              }

              this.$set(
                this.trackPositionPageMap[item],
                'integrationPage',
                this.trackPositionPageMap[item].integrationPage + 1
              );
              if (
                this.integrationList[item] &&
                this.integrationList[item].length &&
                this.trackPositionPageMap[item].integrationPage > this.integrationList[item].length - 1
              ) {
                this.$set(this.trackPositionPageMap[item], 'integrationPage', 0);
              }
            });

            this.$forceUpdate();
          }
        }
        num++;
      }, 1000);
    },

    /**
     * 获取修程任务
     *
     */
    async getRepairTask(trainsetIdStr) {
      let {
        data: { data },
      } = await axios.get('/apiTrainRepair/monitorCommon/getRepairTask', {
        params: {
          dayPlanId: this.dayPlanId,
          trainsetIdStr,
          unitCode: this.unitCode,
          showDayRepairTask: this.monitorConfig.ShowLastRepairTask,
        },
      });

      data.forEach((item) => {
        this.trainsetIdRepairTaskList[item.trainsetId] = item;
      });
    },

    lookupNodeDealInfoBtn(nodeList) {
      this.nodeList = nodeList ? nodeList : [];

      this.lookupNodeDealInfoDialog.visible = true;
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

    /**
     * 获取角色
     *
     */
    async getPostRoleList() {
      let {
        data: { data },
      } = await axios.get('/apiTrainRepair/common/getPostRoleList');

      this.roleList = data;
    },

    columnChange({ left, right }) {
      this.$set(this.monitorConfig.keyWorkFlowConfig[this.keyWorkMonitorDialog.flowId], 'leftList', left);
      this.$set(this.monitorConfig.keyWorkFlowConfig[this.keyWorkMonitorDialog.flowId], 'rightList', right);
    },

    openKeyWorkMonitorDialog() {
      this.keyWorkMonitorDialog.flowId = this.planlessKeyList[0] && this.planlessKeyList[0].id;

      this.keyWorkMonitorDialog.visible = true;
    },

    keyWorkConfigBtn() {
      const workCompositeConfigStr = JSON.stringify({
        showMode: this.localConfig.showMode,
        trackShowConfig: this.localConfig.trackShowConfig,
        trackPostionShowConfig: this.localConfig.trackPostionShowConfig,
        showContentConfig: this.localConfig.showContentConfig,
        showContentType: this.localConfig.showContentType,
        taskRefreshTime: this.localConfig.taskRefreshTime,
        trackRefreshTime: this.localConfig.trackRefreshTime,
        ShowNoTrainsetTrack: this.localConfig.ShowNoTrainsetTrack,
        ShowLastRepairTask: this.localConfig.ShowLastRepairTask,
        keyWorkFlowConfig: this.monitorConfig.keyWorkFlowConfig,
      });

      localStorage.setItem('workCompositeConfig', workCompositeConfigStr);
      this.getConfigToLocal();
      this.keyWorkMonitorDialog.visible = false;
    },

    keyWorkConfigCancel() {
      this.keyWorkMonitorDialog.visible = false;
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
  },

  filters: {
    // 故障背景图
    malfunction(val) {
      let imgURL = `${ctxPath}/static/trainRepair/projection/workRepairmonitor/img/`;
      if (val == 2) {
        return imgURL + 'error1-1.png';
      } else {
        return imgURL + 'error1.png';
      }
    },
  },
});
