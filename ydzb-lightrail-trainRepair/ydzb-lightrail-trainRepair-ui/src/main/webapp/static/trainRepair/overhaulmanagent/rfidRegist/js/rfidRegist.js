var vueMain;
$(function () {
  var showOneMessage = (function () {
    var messageInstanceMap = {};
    return (key, showMessage) => {
      if (messageInstanceMap[key]) {
        messageInstanceMap[key].close();
        messageInstanceMap[key] = null;
      }
      messageInstanceMap[key] = showMessage();
    };
  })();

  var executeCurrentPromiseIfLastOver = (() => {
    var promiseInstanceMap = {};
    return (key, getPromise) => {
      if (!promiseInstanceMap[key]) {
        let promise = getPromise();
        promise &&
          promise.finally(() => {
            promiseInstanceMap[key] = null;
          });
        promiseInstanceMap[key] = promise;
      }
    };
  })();

  var checkResponse10 = (res) => {
    return new Promise((resolve, reject) => {
      if (res.data.code === 1) {
        resolve(res.data.data);
      } else {
        showOneMessage('req', () => vueMain.$message.warning(res.data.msg));
        reject();
      }
    });
  };
  var checkResponse200Wrap = (silent) => {
    return (res) => {
      return new Promise((resolve, reject) => {
        if (res && res.data) {
          if (res.data.status === 200) {
            resolve(res.data.data);
          } else {
            if (!silent) {
              showOneMessage('req', () => vueMain.$message.warning(res.data.message));
            }
            reject();
          }
        } else if (res && res.message) {
          if (!silent) {
            let str;
            if (res.message) {
              str = res.message;
            } else {
              str = '请求错误!';
            }
            showOneMessage('req', () => vueMain.$message.warning(str));
          }
          reject();
        } else {
          reject(res);
        }
      });
    };
  };

  var propMaps = [
    ['sTrackname', 'trackName'],
    ['sTrackcode', 'trackCode'],
    ['sRepairplacename', 'repairPlaceName'],
    ['sRepairplacecode', 'repairPlaceCode'],
    ['sPillarname', 'pillarName'],
    ['sTid', 'tId'],
    ['sId', 'id'],
    ['sUnitcode', 'unitCode'],
    ['sUnitname', 'unitName'],
  ];
  var transPropDataMap = (() => {
    var a = {};
    var b = {};
    propMaps.forEach((pair) => {
      a[pair[0]] = pair[1];
      b[pair[1]] = pair[0];
    });
    return (v) => {
      let v2 = {};
      for (const key in v) {
        const element = v[key];
        if (a[key]) {
          v2[a[key]] = element;
        } else if (b[key]) {
          v2[b[key]] = element;
        } else {
          v2[key] = element;
        }
      }
      return v2;
    };
  })();

  var promiseQueueExe = (arr, getPromise) => {
    var curIndex = 0;
    var isBreak = false;
    return new Promise((resolve, reject) => {
      var next = () => {
        if (curIndex < arr.length && !isBreak) {
          getPromise(arr[curIndex++], () => {
            isBreak = true;
          })
            .then(next)
            .catch(reject);
        } else {
          resolve();
        }
      };
      next();
    });
  };

  var fatalError = 'fatal';

  window.uhfUtils = new Vue({
    data: function () {
      return {
        rightCom: null,
        // resolvedComs: []
      };
    },
    computed: {},
    methods: {
      readTIdOnce(hideNormalError) {
        if (this.rightCom) {
          return new Promise((resolve, reject) => {
            this.getComTId(this.rightCom, hideNormalError).then(resolve).catch(reject);
            // .catch((error) => {
            //   this.rightCom = null;
            //   this.readTIdOnce().then(resolve).catch(reject);
            // });
          });
        } else {
          return new Promise((resolveMain, rejectMain) => {
            let hasFatalError = false;
            this.getComs()
              .then((coms) => {
                // var filteredComs = coms.filter(v => this.resolvedComs.indexOf(v) === -1)
                if (coms.length > 0) {
                  return Promise.resolve(coms);
                } else {
                  showOneMessage('readTidOnce', () => this.$message.warning('请检查设备是否连接'));
                  hasFatalError = true;
                  return Promise.reject();
                }
              })
              .catch((error) => {
                if (error) {
                  console.error(error);
                  showOneMessage('readTidOnce', () => this.$message.warning('请检查标签驱动服务是否开启'));
                  hasFatalError = true;
                }
                return Promise.reject();
              })
              .then((coms) => {
                var tId = null;
                return promiseQueueExe(coms, (com, breakExe) => {
                  return new Promise((resolve, reject) => {
                    this.getComTId(com, true)
                      .then((curtId) => {
                        breakExe();
                        tId = curtId;
                        this.rightCom = com;
                      })
                      .finally(resolve);
                  });
                }).then(() => {
                  // this.resolvedComs = this.resolvedComs.concat(coms)
                  if (tId) {
                    resolveMain(tId);
                  } else {
                    if (hideNormalError !== true) {
                      showOneMessage('readTidOnce', () =>
                        this.$message.warning('请检查设备是否连接，或者将标签置于设备之上')
                      );
                    }
                    rejectMain();
                  }
                });
              })
              .catch(() => {
                if (hasFatalError) {
                  rejectMain(fatalError);
                } else {
                  rejectMain();
                }
              });
          });
        }
      },
      getComTId(com, silent) {
        return new Promise((resolve, reject) => {
          var logFatal = () => {
            showOneMessage('abnormal', () => this.$message.warning('流程执行异常中止，请刷新页面后重试'));
          };
          this.connectRFModel(com)
            .then(() => this.tidReadSync(silent))
            .then((tId) => {
              this.closeRFModel()
                .then(() => {
                  resolve(tId);
                })
                .catch((error) => {
                  console.error('closeRFModel失败', error);
                  reject(error);
                  logFatal();
                });
            })
            .catch((error) => {
              console.error('connectRFModel失败', error);
              this.closeRFModel()
                .then(() => {
                  reject(error);
                })
                .catch((error2) => {
                  console.error('closeRFModel失败', error2);
                  reject([error, error2]);
                  logFatal();
                });
            });
        });
      },
      //读取com端口号设备
      getComs() {
        return new Promise((resolve, reject) => {
          axios
            .get(uhfBaseUrl + '/getCom')
            .then(checkResponse200Wrap())
            // .catch(checkResponse200Wrap())
            .then(resolve)
            .catch(reject);
        });
      },
      //连接端口
      connectRFModel(com) {
        return new Promise((resolve, reject) => {
          axios
            .get(uhfBaseUrl + '/connectRFModel', {
              params: { serial: com, BaudRate: '115200' },
            })
            .then(checkResponse200Wrap())
            // .catch(checkResponse200Wrap())
            .then(resolve)
            .catch(reject);
        });
      },
      //关闭端口
      closeRFModel() {
        return new Promise((resolve, reject) => {
          axios
            .get(uhfBaseUrl + '/closeRFModel')
            .then(checkResponse200Wrap())
            // .catch(checkResponse200Wrap())
            .then(resolve)
            .catch(reject);
        });
      },
      //调用读TID
      tidReadSync(silent) {
        return new Promise((resolve, reject) => {
          axios
            .get(uhfBaseUrl + '/tidReadSync', {
              params: { tidReadLength: '6', tidStartAddress: '0' },
            })
            .then(checkResponse200Wrap(silent))
            // .catch(checkResponse200Wrap(silent))
            .then(resolve)
            .catch(reject);
        });
      },
    },
  });

  var setIntervalUtil = (function () {
    let setIntervalKey = null;
    return {
      start(...args) {
        if (setIntervalKey) {
          this.end();
        }
        setIntervalKey = setInterval(...args);
      },
      end() {
        clearInterval(setIntervalKey);
        setIntervalKey = null;
      },
    };
  })();

  var getBaseEmptyData = () => {
    return {
      trackCode: null, // 股道编码
      place: null, // 列位
      repairPlaceCode: null, // 作业位置编码
      pillarName: undefined, // 位置编号
      tId: null, // 标签编号
    };
  };
  vueMain = new Vue({
    el: '#main',
    data() {
      var columns = [
        {
          property: 'sTrackname',
          label: '股道',
          width: null,
        },
        {
          property: 'sRepairplacename',
          label: '标签载体',
          width: null,
        },
        {
          property: 'sPillarname',
          label: '位置编号',
          // className: "num-col",
          width: null,
        },
        {
          property: 'sTid',
          label: '标签编号',
          width: null,
        },
      ];

      columns.forEach((v) => {
        if (v.property == 'sPillarname') {
          v.align = 'right';
        } else {
          v.align = 'center';
        }
      });

      return {
        searchData: getBaseEmptyData(),
        // 选择当前查询新增模式
        stateRadio: 1,
        // 列位列表
        placeList: [],
        // 编组数
        gandUpNum: '',
        // 编组数组
        gandUpNumList: [4, 8, 16, 17],
        // 车型
        trainType: '',
        // 批次
        PatchListByTraintype: '',
        // 车组列表
        allTrainsetList: [],
        columns: columns,
        rfidList: '',
        rfidSelection: [], // 选择的行
        pageInfo: {
          pageSize: 10,
          pageNum: 1,
          total: 0,
        },
        userData: {
          unitName: '',
          unitCode: '',
        },
        originalPlaceTypes: [], // 作业位置列表
        originalTrackAreas: [], // 股道线区列表（包含股道列位信息）

        dialog: new Vue({
          data() {
            return {
              dialogVisible: false,
              dialogTitle: 'RFID标签注册',
              formData: getBaseEmptyData(),
              tempRfidList: [],
              singleOrMultiple: 1, //新增——单一注册/连续注册

              preFixes: undefined, // 字冠
              newPillarName: undefined, // 当前位置编号
              oldPillarName: undefined, // 结束位置编号
              intervalPillarName: undefined, // 位置间隔
              continuityCreate: false, //是否开始连续创造标记

              // RFID位置注册
              RFIDLocation: false,
              // 编组数
              gandUpNum: [],
              // 股道
              trackCode: '',
              // 股道列表
              trackAll: [],
              // 列位
              placeCode: '',
              // 列位列表
              placeAll: [],
              // 车头方向
              trainHead: '1列位',
              // 辆序
              trainHeadSequenc: '',
              // 车尾方向
              trainTail: '1列位',
              // 辆序
              trainTailSequenc: '',

              // RFID标签和作业标准关系
              RFIDRelation: false,
              // 车型
              trainType: '',
              // 批次
              batch: '',
              // 作业标准
              sItemname: '',
              // 作业标准列表
              sItemnameList: [],
              // 标签载体
              repairPlaceCode: '',

              // 所有车组
              allTrainsetList: [],
            };
          },
          watch: {
            continuityCreate(value) {
              if (value) {
                setIntervalUtil.start(() => {
                  executeCurrentPromiseIfLastOver('continuityCreateNextOne', () => {
                    return this.continuityCreateNextOne();
                  });
                }, 5000);
              } else {
                setIntervalUtil.end();
                vueMain.getRfidList(1);
              }
            },
            singleOrMultiple() {
              this.newPillarName = undefined;
              this.oldPillarName = undefined;
              this.intervalPillarName = undefined;
              if (this.singleOrMultiple == 1) {
                this.preFixes = undefined;
              } else {
                let positionNumber = localStorage.getItem('positionNumber');
                positionNumber = JSON.parse(positionNumber);
                this.preFixes = positionNumber && positionNumber.preFixes;
                this.newPillarName = positionNumber.newPillarName;
              }
            },
          },
          computed: {
            getColumns() {
              return (
                vueMain &&
                vueMain.columns.map((v) => {
                  var copy = { ...v };
                  if (copy.property === 'sTid') {
                    copy.width = '300px';
                  }
                  return copy;
                })
              );
            },

            formDataForSend() {
              var data = {
                ...this.formData,
              };
              data.sPlaceCode = this.formData.place.trackPositionCode;
              data.sPlaceName = this.formData.place.trackPostionName;
              data.trackName = vueMain && vueMain.tracksLabelMap[data.trackCode];
              data.repairPlaceName = vueMain && vueMain.placeTypesLabelMap[data.repairPlaceCode];
              data.unitCode = vueMain && vueMain.unitCode;
              data.unitName = vueMain && vueMain.unitName;
              return transPropDataMap(data);
            },

            trainTypeList({ allTrainsetList }) {
              if (allTrainsetList.length) {
                // 去重+组合
                const arr = allTrainsetList.reduce((prev, item) => {
                  prev.push(item.traintype);
                  return prev;
                }, []);
                return new Set(arr);
              }
            },

            trainTempList({ allTrainsetList, trainType }) {
              if (allTrainsetList.length) {
                // 去重+组合
                const map = allTrainsetList.reduce((prev, item) => {
                  if (item.traintempid && !prev.has(item.traintempid)) {
                    prev.set(item.traintempid, {
                      traintempid: item.traintempid,
                      traintype: item.traintype,
                    });
                  }
                  return prev;
                }, new Map());
                let arr = [...map.values()];
                // 过滤条件
                if (trainType) {
                  arr = arr.filter((item) => item.traintype == trainType);
                }

                return arr;
              }
            },
          },
          methods: {
            // 股道切换
            async trackCodeChange(val) {
              this.formData.place = '';
              await vueMain.getTrackPositionByTrackCode(val);
              vueMain.placeList.forEach((item) => {
                // if (item.directionCode == 0) {
                //   this.trainHead = item.trackPostionName + '列位'
                // } else if (item.directionCode == 1) {
                //   this.trainTail = item.trackPostionName + '列位'
                // }
              });
            },

            onDialogConfirm() {
              this.validateForm();
              if (this.validateForm()) {
                return vueMain.wrapLoading(
                  vueMain
                    .addRfId(this.formDataForSend, this.preFixes)
                    .then((data) => {
                      return vueMain.getRfidList(1).then(() => {
                        this.tempRfidList.push(data);
                      });
                    })
                    .then(() => {
                      showOneMessage('rfidRegist', () => this.$message.success('注册成功'));
                    })
                );
              }
            },

            // 连续注册开始
            newOnDialogConfirm() {
              this.validateForm();
              if (this.validateForm()) {
                if (this.newPillarName == null || this.oldPillarName == null) {
                  showOneMessage('warning', () => this.$message.warning('当前位置编号或结束位置编号不能为空'));
                } else if (this.intervalPillarName == null) {
                  showOneMessage('warning', () => this.$message.warning('位置间隔不能为空'));
                } else {
                  if (this.newPillarName >= this.oldPillarName) {
                    showOneMessage('warning', () => this.$message.warning('当前位置编号不能等于大于结束位置编号'));
                  } else {
                    this.continuityCreate = true;
                    showOneMessage('success', () => this.$message.success('连续注册开始'));
                  }
                }
              }
            },
            // 校验数据
            validateForm() {
              // trackCode: null, // 股道编码
              // repairPlaceCode: null, // 作业位置编码
              // pillarName: null, // 位置编号
              // tId: null, // 标签编号
              var requiredPropConfig = {
                trackCode: {
                  name: '股道',
                  type: 'select',
                },
                place: {
                  name: '列位',
                  type: 'select',
                },
                repairPlaceCode: {
                  name: '标签载体',
                  type: 'select',
                },
                pillarName: {
                  name: '位置编号',
                  type: 'input',
                },
                tId: {
                  name: '标签',
                  type: 'input',
                },
              };
              if (this.singleOrMultiple == 2) {
                requiredPropConfig = {
                  trackCode: {
                    name: '股道',
                    type: 'select',
                  },
                  place: {
                    name: '列位',
                    type: 'select',
                  },
                  repairPlaceCode: {
                    name: '标签载体',
                    type: 'select',
                  },
                };
              }
              var getMsgMap = {
                select(propName) {
                  return '请选择' + propName + '！';
                },
                input(propName) {
                  return '请输入' + propName + '！';
                },
              };
              for (const prop in this.formData) {
                if (requiredPropConfig[prop]) {
                  let config = requiredPropConfig[prop];
                  if (!this.formData[prop]) {
                    showOneMessage('dialogConfirmError', () =>
                      this.$message.warning(getMsgMap[config.type](config.name))
                    );
                    return false;
                  }
                }
              }
              return true;
            },

            // 定时读取设备信息、注册
            continuityCreateNextOne() {
              let newPillarName = this.newPillarName; // 当前位置编号
              let oldPillarName = this.oldPillarName; // 结束位置编号
              let intervalPillarName = this.intervalPillarName; // 位置间隔
              if (newPillarName < oldPillarName) {
                return vueMain.wrapLoading(
                  uhfUtils
                    .readTIdOnce(true)
                    .then((tId) => {
                      this.formData.tId = tId;
                      this.formData.pillarName = newPillarName;
                      return vueMain
                        .addRfId(this.formDataForSend, this.preFixes)
                        .then((data) => {
                          this.tempRfidList.push(data);
                        })
                        .then(() => {
                          let positionNumber = {
                            preFixes: this.preFixes,
                            newPillarName: parseInt(newPillarName) + 1,
                          };
                          localStorage.setItem('positionNumber', JSON.stringify(positionNumber));
                          newPillarName += intervalPillarName;
                          this.newPillarName = newPillarName;
                          showOneMessage('rfidRegist', () => this.$message.success('注册成功'));
                        })
                        .catch((error) => {
                          this.continuityCreate = false;
                        });
                    })
                    .catch((error) => {
                      if (error == 'fatal') {
                        this.continuityCreate = false;
                        showOneMessage('warning', () => this.$message.warning('注册结束'));
                      }
                    })
                );
              } else {
                this.continuityCreate = false;
                showOneMessage('success', () => this.$message.success('注册结束'));
              }
            },

            // 连续注册结束
            oldOnDialogConfirm() {
              this.continuityCreate = false;
              showOneMessage('success', () => this.$message.success('连续注册结束'));
            },

            onDialogCancel() {
              if (!this.continuityCreate) {
                this.dialogVisible = false;
                this.RFIDLocation = false;
                this.RFIDRelation = false;
                this.formData = getBaseEmptyData();
                this.tempRfidList = [];
                this.placeList = [];
                this.singleOrMultiple = 1;
                this.newPillarName = undefined;
                this.oldPillarName = undefined;
                this.intervalPillarName = undefined;
                this.gandUpNum = [];
                // this.trainHead = ''
                this.trainHeadSequenc = '';
                this.trackCode = '';
                this.trackAll = [];
                this.placeCode = '';
                this.placeAll = [];
                // this.trainTail = ''
                this.trainTailSequenc = '';
                this.trainType = '';
                this.batch = '';
                this.sItemname = '';
                this.sItemnameList = [];
                this.repairPlaceCode = '';
                this.placeTypes = [];
              } else {
                showOneMessage('warning', () => this.$message.warning('标签注册中！请先结束注册，再退出注册弹窗'));
              }
            },
            onReadTId() {
              vueMain.wrapLoading(
                uhfUtils.readTIdOnce().then((tId) => {
                  this.formData.tId = tId;
                  if (this.RFIDLocation) {
                    this.getTeackMsg(1, 1000, tId);
                  }
                })
              );
            },

            // RFID位置注册——通过标签编码获取股道列位
            async getTeackMsg(pageNum, pageSize, sTid) {
              let res = await axios.get('/apiTrainRepair/rfidRegist/list', {
                params: {
                  pageNum,
                  pageSize,
                  sTid,
                },
              });

              this.trackAll = [
                {
                  trackCode: res.data.data.records && res.data.data.records[0].sTrackcode,
                  trackName: res.data.data.records && res.data.data.records[0].sTrackname,
                },
              ];
              this.trackCode = res.data.data.records && res.data.data.records[0].sTrackcode;

              this.placeAll = [
                {
                  trackPositionCode: res.data.data.records && res.data.data.records[0].sPlaceCode,
                  trackPostionName: res.data.data.records && res.data.data.records[0].sPlaceName,
                },
              ];
              this.placeCode = res.data.data.records[0].sPlaceCode;

              this.trackCodeChange(this.trackCode);
            },

            showDialog() {
              if (vueMain.stateRadio == 1) {
                this.formData.tId = '';
                this.dialogVisible = true;
              } else if (vueMain.stateRadio == 2) {
                this.RFIDLocation = true;
              } else if (vueMain.stateRadio == 3) {
                this.getTrainsetList();
                this.RFIDRelation = true;
              }
            },

            // 弹出框-验证 作业辆序/确认辆序 输入是否为正整数
            changeInpit(modelValue) {
              var pattern = /^[0-9][0-9]*$/; // 正整数正则表达式
              // 当不是正整数时
              if (!this.gandUpNum) {
                showOneMessage('warning', () => this.$message.warning('请先选择编组数!'));
                this[modelValue] = '';
              } else if (!pattern.test(this[modelValue])) {
                this[modelValue] = '';
              } else if (this[modelValue] > this.gandUpNum) {
                showOneMessage('warning', () => this.$message.warning('辆序不能大于编组数!'));
                this[modelValue] = '';
              }
            },

            // RFID位置注册弹窗点击新增
            RFIDLocationAdd() {
              if (!this.formData.tId) {
                showOneMessage('warning', () => this.$message.warning('标签编号不能为空!'));
              } else if (this.gandUpNum.length == 0) {
                showOneMessage('warning', () => this.$message.warning('编组数不能为空!'));
              } else if (!this.trackCode) {
                showOneMessage('warning', () => this.$message.warning('股道不能为空!'));
              } else if (!this.placeCode) {
                showOneMessage('warning', () => this.$message.warning('列位不能为空!'));
              } else if (!this.trainHeadSequenc) {
                showOneMessage('warning', () => this.$message.warning('车头辆序号不能为空!'));
              } else if (this.trainHeadSequenc.length < 2) {
                showOneMessage('warning', () => this.$message.warning('车头辆序号格式为两位数字组成!'));
              } else if (!this.trainTailSequenc) {
                showOneMessage('warning', () => this.$message.warning('车尾辆序号不能为空!'));
              } else if (this.trainTailSequenc.length < 2) {
                showOneMessage('warning', () => this.$message.warning('车尾辆序号格式为两位数字组成!'));
              } else {
                this.addPosition();
              }
            },

            // RFID位置注册新增
            async addPosition() {
              let res = await axios.post('/apiTrainRepair/rfidRegist/addPosition', {
                // 车头辆序号
                headDirectionCarNo: this.trainHeadSequenc,
                // 股道编号
                trackCode: this.trackCode,
                // 标签编号
                tId: this.formData.tId,
                // 列位编号
                placeCode: this.placeCode,
                // 车尾辆序号
                tailDirectionCarNo: this.trainTailSequenc,
                // 编组数量集合列表
                carCounts: this.gandUpNum,
              });

              if (res.data.code == 1) {
                showOneMessage('success', () => this.$message.warning('添加成功!'));
                await vueMain.getRfidList(1);
                this.$confirm('是否继续新增？', '', {
                  confirmButtonText: '确定',
                  cancelButtonText: '取消',
                })
                  .then(() => {
                    this.onDialogCancel();
                    this.RFIDLocation = true;
                  })
                  .catch(() => {
                    this.RFIDLocation = false;
                  });
              } else {
                showOneMessage('warning', () => this.$message.warning(res.data.msg));
              }
            },

            // 切换车型
            changTrainType(val) {
              this.batch = '';
              this.sItemname = '';
              this.sItemnameList = [];
            },

            /**
             * 获取所有车组
             */
            async getTrainsetList() {
              let { data } = await axios.get('/apiTrainRepair/common/getTrainsetList');
              if (data.code == 1) {
                this.allTrainsetList = data.data;
              }
            },

            // 切换批次
            batchChange(val) {
              this.sItemname = '';
              if (val) {
                this.trainType = this.trainTempList.find((item) => item.traintempid == val).traintype;
              }
              this.getWorkcritertionList();
            },

            // 获取作业标准
            async getWorkcritertionList() {
              let res = await axios.post('/apiTrainRepair/xzyCWorkcritertion/getWorkcritertionList', {
                trainsetType: this.trainType, // 车型 ,
                trainsetSubType: this.batch, //批次
                cyc: 1, //修程 ,
                itemName: '', // 作业项目
                pageNum: 1, // 页码
                pageSize: 10000, // 每页显示数据条数
              });

              if (res.data.code == 1) {
                this.sItemnameList = res.data.data.xzyCWorkcritertionList;
              } else {
                showOneMessage('warning', () => this.$message.warning('获取作业标准失败!'));
              }
            },

            //  RFID标签和作业标准关系新增按钮
            async relationAdd() {
              if (!this.trainType) {
                showOneMessage('warning', () => this.$message.warning('车型不能为空!'));
              } else if (!this.batch) {
                showOneMessage('warning', () => this.$message.warning('批次不能为空!'));
              } else if (!this.sItemname) {
                showOneMessage('warning', () => this.$message.warning('作业标准不能为空!'));
              } else if (!this.repairPlaceCode) {
                showOneMessage('warning', () => this.$message.warning('标签载体不能为空!'));
              } else {
                await this.addCritertion();
              }
            },

            // 新增
            async addCritertion() {
              let res = await axios.post('/apiTrainRepair/rfidRegist/addCritertion', {
                // 标准id
                critertionId: this.sItemname,
                // 作业位置CODE
                repairPlaceCode: this.repairPlaceCode,
              });

              if (res.data.code == 1) {
                showOneMessage('success', () => this.$message.warning('添加成功!'));
                await vueMain.getRfidList(1);
                this.$confirm('是否继续新增？', '', {
                  confirmButtonText: '确定',
                  cancelButtonText: '取消',
                })
                  .then(() => {
                    this.onDialogCancel();
                    this.RFIDRelation = true;
                  })
                  .catch(() => {
                    this.RFIDRelation = false;
                  });
              } else {
                showOneMessage('warning', () => this.$message.warning(res.data.msg));
              }
            },
          },
        }),
      };
    },
    async created() {
      await this.getUnitCode();
      this.wrapLoading(
        Promise.all([
          this.getTrackAreas(this.unitCode),
          this.getUnitName(),
          this.getPlaceTypes(),
          this.getRfidList(1, this.pageInfo.pageSize),
        ])
      );
      // this.getTraintypeList();
      this.getTrainsetList();
    },

    methods: {
      querySearch(queryString, cb) {
        let rfidList = this.rfidList;

        let list = [];
        if (queryString) {
          rfidList.forEach((item) => {
            if (item.sPillarname.toLowerCase().indexOf(queryString.toLowerCase()) === 0) {
              list.push({ value: item.sPillarname, address: item.sPillarname });
            }
          });
        } else {
          rfidList.forEach((item) => {
            list.push({ value: item.sPillarname, address: item.sPillarname });
          });
        }
        // 调用 callback 返回建议列表的数据
        cb(list);
      },

      /**
       * 获取所有车组
       */
      async getTrainsetList() {
        let { data } = await axios.get('/apiTrainRepair/common/getTrainsetList');
        if (data.code == 1) {
          this.allTrainsetList = data.data;
        }
      },

      // 切换批次
      batchChange(val) {
        this.sItemname = '';
        if (val) {
          this.trainType = this.trainTempList.find((item) => item.traintempid == val).traintype;
        }
      },

      //搜索栏-获取车型
      async getTraintypeList() {
        var trainType_url_path = '/apiTrainResume/resume/getTraintypeList';
        let res = await axios.get(trainType_url_path);

        if (res.status == 200) {
          this.trainTypeList = res.data.data;
        } else {
          showOneMessage('warning', () => this.$message.warning('获取车型失败!'));
        }
      },

      // 切换车型
      changTrainType(val) {
        this.PatchListByTraintype = '';
      },

      // 获取批次
      async getPatchListByTraintype() {
        let res = await axios.get('/apiTrainResume/resume/getPatchListByTraintype', {
          params: {
            traintype: this.trainType,
          },
        });

        if (res.status == 200) {
          this.PatchListByTraintypeList = res.data.data;
        } else {
          showOneMessage('warning', () => this.$message.warning('获取批次失败!'));
        }
      },

      wrapLoading(prom) {
        let loading = this.$loading();
        prom.finally(() => {
          loading.close();
        });
      },

      // 股道切换
      allTracksChange(val) {
        this.searchData.place = '';
        this.getTrackPositionByTrackCode(val);
      },

      // 选择当前查询模式
      stateRadioChange(val) {
        this.searchData.trackCode = '';
        this.searchData.place = '';
        this.placeList = [];
        this.gandUpNum = '';
        this.searchData.repairPlaceCode = '';
        this.searchData.pillarName = undefined;
        this.searchData.tId = '';
        this.getRfidList(1, this.pageInfo.pageSize);
      },

      // 获取列位信息
      async getTrackPositionByTrackCode(trackCode) {
        let res = await axios.get('/apiTrainUse/yard/getTrackPositionByTrackCode', {
          params: {
            trackCode,
            deptCode: this.unitCode,
          },
        });
        vueMain.placeList = res.data;
      },

      async getUnitCode() {
        let { data } = await axios.get('/apiTrainRepair/common/getUnitCode');
        if (data.code == 1) {
          this.userData.unitCode = data.data;
        } else {
          showOneMessage('warning', () => this.$message.warning('获取登录人信息失败!'));
        }
      },

      getUnitName() {
        axios.get('/apiTrainRepair/common/getUnitName').then((res) => {
          if (res.data.code == 1) {
            this.userData.unitName = res.data.data;
          } else {
            showOneMessage('warning', () => this.$message.warning('获取登录人信息失败!'));
          }
        });
      },

      getRfidList(pageNum, pageSize) {
        return new Promise((resolve, reject) => {
          if (!pageSize) {
            pageSize = this.pageInfo.pageSize;
          }

          let URL, params;
          if (this.stateRadio == 1) {
            URL = '/apiTrainRepair/rfidRegist/list';
            params = {
              pageNum,
              pageSize,
              ...transPropDataMap(this.searchData),
            };
          } else if (this.stateRadio == 2) {
            URL = '/apiTrainRepair/rfidRegist/positionList';
            params = {
              pageNum,
              pageSize,
              // TID
              tid: this.searchData.tId,
              // 股道code
              trackCode: this.searchData.trackCode,
              // 列位code
              placeCode: this.searchData.place,
              // 编组数
              carCount: this.gandUpNum,
            };
          } else if (this.stateRadio == 3) {
            URL = '/apiTrainRepair/rfidRegist/critertionList';
            params = {
              pageNum,
              pageSize,
              trainsetType: this.trainType,
              trainsetSubType: this.PatchListByTraintype,
              repairPlaceCode: this.searchData.repairPlaceCode,
            };
          }

          axios
            .get(URL, {
              params,
            })
            .then(checkResponse10)
            .then((page) => {
              this.rfidList = page.records;
              this.pageInfo.pageNum = page.current;
              this.pageInfo.pageSize = page.size;
              this.pageInfo.total = page.total;
              this.$refs.mainTable && this.$refs.mainTable.clearSelection();
              resolve();
            })
            .catch(reject);
        });
      },

      addRfId(data, header) {
        data.sPillarname = data.sPillarname;
        return new Promise((resolve, reject) => {
          axios.post('/apiTrainRepair/rfidRegist/add', data).then(checkResponse10).then(resolve).catch(reject);
        });
      },

      getPlaceTypes() {
        return new Promise((resolve, reject) => {
          axios
            .get('/apiTrainRepair/rfidRegist/getPlaceTypes')
            .then(checkResponse10)
            .then((data) => {
              this.originalPlaceTypes = data;
              resolve(data);
            })
            .catch(reject);
        });
      },

      getTrackAreas(deptCode) {
        return new Promise((resolve, reject) => {
          axios
            .get('/apiTrainRepair/rfidRegist/getTrackAreas', {
              params: {
                deptCode,
              },
            })
            .then(checkResponse10)
            .then((data) => {
              this.originalTrackAreas = data;
              resolve(data);
            })
            .catch(reject);
        });
      },

      onSelectionChange(selection) {
        this.rfidSelection = selection;
      },
      handleSizeChange: function (val) {
        this.wrapLoading(this.getRfidList(1, val));
      },
      handleCurrentChange: function (val) {
        this.wrapLoading(this.getRfidList(val));
      },

      stopScroll(e) {
        e = e || window.event;
        if (e.preventDefault) {
          e.preventDefault();
          e.stopPropagation();
        } else {
          e.cancelBubble = true;
          e.returnValue = false;
        }
        return false;
      },

      onReadNumber() {
        vueMain.wrapLoading(
          uhfUtils.readTIdOnce().then((tId) => {
            this.searchData.tId = tId;
          })
        );
      },

      onSearch() {
        this.pageInfo.pageNum = 1;
        this.wrapLoading(this.getRfidList(1));
      },

      onReset() {
        this.searchData.trackCode = '';
        this.searchData.place = '';
        this.gandUpNum = '';
        this.trainType = '';
        this.PatchListByTraintype = '';
        this.searchData.repairPlaceCode = '';
        this.searchData.pillarName = '';
        this.searchData.tId = '';
        this.searchData.placeCode = '';
        this.placeList = [];
        this.onSearch();
      },

      elConfirm(msg, title) {
        return new Promise((resolve, reject) => {
          this.$confirm(msg, title || '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            closeOnClickModal: false,
            closeOnPressEscape: false,
            type: 'warning',
          })
            .then(resolve)
            .catch(reject);
        });
      },
      onDelete() {
        if (this.rfidSelection.length == 0) {
          showOneMessage('deleteError', () => this.$message.warning('请选择要删除的注册信息！'));
        } else {
          this.elConfirm('是否确认删除？').then(() => {
            let url, data;
            if (this.stateRadio == 1) {
              url = '/apiTrainRepair/rfidRegist/delRfid';
              data = {
                rfidRegistIds: this.rfidSelection.map((v) => v.sId),
              };
            } else if (this.stateRadio == 2) {
              url = '/apiTrainRepair/rfidRegist/delPosition';
              data = this.rfidSelection.map((v) => v.ruleId);
            } else if (this.stateRadio == 3) {
              url = '/apiTrainRepair/rfidRegist/delCritertion';
              data = this.rfidSelection.map((v) => v.id);
            }

            let prom = new Promise((resolve, reject) => {
              axios
                .post(url, data)
                .then(checkResponse10)
                .then(() => this.getRfidList(1))
                .then(resolve)
                .catch(reject);
            });
            this.wrapLoading(prom);
            prom
              .then(() => {
                showOneMessage('success', () => this.$message.success('删除成功!'));
              })
              .catch((error) => {
                if (error) {
                  showOneMessage('warning', () => this.$message.warning('删除失败!'));
                }
              });
          });
        }
      },
      onAdd() {
        this.dialog.showDialog();
      },
    },
    computed: {
      pageTitle: GeneralUtil.getObservablePageTitleGetter('RFID标签配置'),
      rfidListShow() {
        if (this.stateRadio == 1) {
          return [
            {
              prop: 'sTrackname',
              lable: '股道',
            },
            {
              prop: 'sPlaceName',
              lable: '列位',
            },
            {
              prop: 'sRepairplacename',
              lable: '标签载体',
            },
            {
              prop: 'sPillarname',
              lable: '位置编号',
            },
            {
              prop: 'sTid',
              lable: '标签编号',
            },
          ];
        } else if (this.stateRadio == 2) {
          return [
            {
              prop: 'trackName',
              lable: '股道',
            },
            {
              prop: 'placeName',
              lable: '列位',
            },
            {
              prop: 'tId',
              lable: '标签编号',
            },
            {
              prop: 'carCount',
              lable: '编组数',
            },
            {
              prop: 'headDirectionName',
              lable: '车头方向',
            },
            {
              prop: 'headDirectionCarNo',
              lable: '辆序',
            },
            {
              prop: 'tailDirectionName',
              lable: '车尾方向',
            },
            {
              prop: 'tailDirectionCarNo',
              lable: '辆序',
            },
          ];
        } else if (this.stateRadio == 3) {
          return [
            {
              prop: 'trainsetType',
              lable: '车型',
            },
            {
              prop: 'trainsetSubtype',
              lable: '批次',
            },
            {
              prop: 'itemName',
              lable: '标准',
            },
            {
              prop: 'repairPlaceName',
              lable: '标签载体',
            },
          ];
        }
      },

      placeTypesLabelMap() {
        if (this.placeTypes.length) {
          return this.placeTypes.reduce((p, c) => {
            p[c.repairPlaceCode] = c.repairPlaceName;
            return p;
          }, {});
        }
      },
      placeTypes() {
        return this.originalPlaceTypes ? this.originalPlaceTypes.map(transPropDataMap) : [];
      },
      // 当前人运用所编码
      unitCode() {
        return this.userData && this.userData.unitCode;
      },
      // 当前人运用所名称
      unitName() {
        return this.userData && this.userData.unitName;
      },

      allTracks() {
        // 所有股道
        var tracks = [];
        var cache = {};
        this.originalTrackAreas.forEach((trackArea) => {
          trackArea.lstTrackInfo.forEach((track) => {
            if (!cache[track.trackCode]) {
              tracks.push(track);
              cache[track.trackCode] = true;
            }
          });
        });
        tracks.sort((a, b) => {
          return a.trackName.localeCompare(b.trackName);
        });
        return tracks;
      },

      tracksLabelMap() {
        if (this.allTracks.length) {
          return this.allTracks.reduce((p, c) => {
            p[c.trackCode] = c.trackName;
            return p;
          }, {});
        }
      },

      trainTypeList({ allTrainsetList }) {
        // 去重+组合
        if (allTrainsetList.length) {
          const arr = allTrainsetList.reduce((prev, item) => {
            prev.push(item.traintype);
            return prev;
          }, []);
          return new Set(arr);
        }
      },

      trainTempList({ allTrainsetList, trainType }) {
        if (allTrainsetList.length) {
          // 去重+组合
          const map = allTrainsetList.reduce((prev, item) => {
            if (item.traintempid && !prev.has(item.traintempid)) {
              prev.set(item.traintempid, {
                traintempid: item.traintempid,
                traintype: item.traintype,
              });
            }
            return prev;
          }, new Map());
          let arr = [...map.values()];
          // 过滤条件
          if (trainType) {
            arr = arr.filter((item) => item.traintype == trainType);
          }

          return arr;
        }
      },
    },
  });
});

//调用读
// function readEPC() {
//   $.ajax({
//     url: uhfBaseUrl + "/userReadSync",
//     method: "get",
//     data: { userReadLength: "6", userStartAddress: "0" },
//     success: function (res) {
//       console.log(res);
//       if (res) {
//         $("#readTxt").val(res.data);
//         // console.log(res);
//       }
//     },
//   });
// }

//写
// function writeEPC() {
//   var writeTxt = $("#writeTxt").val();
//   $.ajax({
//     url: uhfBaseUrl + "/userWriteSync",
//     method: "get",
//     data: {
//       userWriteData: writeTxt,
//       userReadLength: "4",
//       userStartAddress: "0",
//     },
//     success: function (res) {
//       if (res) {
//         console.log(res);
//       }
//     },
//   });
// }
