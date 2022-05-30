let copyData = (data) => JSON.parse(JSON.stringify(data));
window.main = new Vue({
  el: '#keyWorkConfig',
  data: {
    unitCode: '',
    currentSection: '',
    unitList: [],
    headerFormData: {
      unitCode: '',
      content: '',
    },
    headerFormRules: {
      unitCode: [],
      content: [],
    },
    loading: true,
    specialColumnKeys: Object.freeze(['FUNCTION_CLASS']),
    columns: [],
    columnListMap: {},
    mainTableHeight: 0,
    mainTableData: [],
    mainTablePageNum: 1,
    mainTablePageSize: 20,
    mainTablePageTotal: 20,
    trainTypeList: [],
    cascaderProps: {
      // expandTrigger:'hover',
      value: 'code',
      label: 'name',
      checkStrictly: true,
      emitPath: false,
      lazy: true,
    },
    configDialog: {
      visible: false,
      formData: {
        title: '',
        unitCode: '',
        content: '',
        trainModel: [],
      },
      formRules: {
        unitCode: [
          {
            required: true,
            message: '请选择运用所',
            trigger: 'change',
          },
        ],
        content: [
          {
            required: true,
            message: '请输入作业内容',
            trigger: 'blur',
          },
        ],
        trainModel: [
          {
            required: true,
            message: '请选择车型',
            trigger: 'change',
          },
        ],
      },
    },
    keyWorkTypeConfigDialog: {
      visible: false,
      formData: {
        type: '',
      },
      formRules: {
        type: [
          {
            required: true,
            message: '请输入类型名称',
            trigger: 'blur',
          },
        ],
      },
      tableData: [],
      oriTableData: [],
      editable: {},
      typeObj: {},
      deleteTypeList: [],
    },
  },
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('关键作业配置'),
    columnMap({ columns }) {
      return columns.reduce((prev, item) => {
        prev[item.property] = item;
        return prev;
      }, {});
    },
    carList({ columnListMap }) {
      if (columnListMap.CARList) {
        return columnListMap.CARList.map((item) => {
          return {
            id: item.value,
            label: item.label,
            value: item.value,
          };
        });
      } else {
        return [];
      }
    },

    lastIndex({ keyWorkTypeConfigDialog }) {
      let dataTotal = keyWorkTypeConfigDialog.tableData.length;
      if (dataTotal === 0) return 0;
      return dataTotal - 1;
    },
  },
  watch: {
    'keyWorkTypeConfigDialog.tableData'() {
      this.resetEditableStatusAndData();
    },
  },
  async created() {
    await this.getUnitCode();
    await this.isCenter();
    await this.getUnitList();
    this.getKeyWorkConfigList();
    this.getKeyWorkExtraColumnList();
  },
  mounted() {
    this.mainTableHeightChange();
    window.addEventListener('resize', this.mainTableHeightChange);
    this.$once('hook:beforeDestroy', () => {
      window.removeEventListener('resize', this.mainTableHeightChange);
    });
  },
  methods: {
    async isCenter() {
      let res = await isCenter();
      this.currentSection = res;
    },

    // 获取运用所编码
    async getUnitCode() {
      let data = await getUnitCode();
      this.unitCode = data;
    },

    mainTableHeightChange() {
      this.$nextTick(() => {
        this.mainTableHeight = 'calc(100vh - 145px)';
        this.$refs.elTable.doLayout();
      });
    },
    async getUnitList() {
      this.unitList = await getUnitList();
      if (!this.currentSection) {
        this.headerFormData.unitCode = this.unitList[0].unitCode;
      }
    },
    async getKeyWorkConfigList() {
      const { records, total } = await getKeyWorkConfigList({
        ...this.headerFormData,
        pageNum: this.mainTablePageNum,
        pageSize: this.mainTablePageSize,
      });
      this.mainTableData = records;
      this.mainTablePageTotal = total;
    },
    async getKeyWorkExtraColumnList() {
      this.columns = await getKeyWorkExtraColumnList();
      this.columns.forEach(async (column) => {
        if (['options', 'specialOptions'].includes(column.type)) {
          // 配置框表单
          let list = [];
          if (this.specialColumnKeys.includes(column.key)) {
            if (column.key === 'FUNCTION_CLASS') {
              list = await getFunctionClass();
              this.$set(this.columnListMap, `${column.key}FlatList`, this.tranListToFlatData(list));
              this.addListLeaf(list);
            }
          } else {
            if (column.key !== 'CAR') {
              list = await getKeyWorkExtraColumnValueList({
                columnKey: column.key,
              });
            }
          }

          this.$set(this.columnListMap, `${column.key}List`, list);
        }
      });

      this.loading = false;
    },
    addListLeaf(list) {
      list.forEach((item) => {
        if (item.children && item.children.length > 0) {
          item.leaf = false;
          this.addListLeaf(item.children);
        } else {
          item.leaf = true;
        }
      });
    },
    tranListToFlatData(list) {
      let arr = [];
      list.forEach((item) => {
        arr.push({
          id: item.id,
          code: item.code,
          disabled: item.disabled,
          name: item.name,
        });
        if (item.children && item.children.length > 0) {
          arr = [...arr, ...this.tranListToFlatData(item.children)];
        }
      });
      return arr;
    },
    getColumnLabel(row, { key, property }) {
      if (key === 'FUNCTION_CLASS') {
        return (
          this.columnListMap[`${key}FlatList`] &&
          this.columnListMap[`${key}FlatList`].find((item) => item.code == row[property]) &&
          this.columnListMap[`${key}FlatList`].find((item) => item.code == row[property]).name
        );
      } else {
        return (
          this.columnListMap[`${key}List`] &&
          this.columnListMap[`${key}List`].find((item) => item.value == row[property]) &&
          this.columnListMap[`${key}List`].find((item) => item.value == row[property]).label
        );
      }
    },
    mainTablePageSizeChange(pageSize) {
      this.mainTablePageSize = pageSize;
      this.getKeyWorkConfigList();
    },
    mainTablePageNumChange(pageNum) {
      this.mainTablePageNum = pageNum;
      this.getKeyWorkConfigList();
    },
    delKeyWorkConfig(row) {
      this.$confirm('确认删除？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          try {
            await delKeyWorkConfig(row);
            this.$message.success('删除成功');
            this.getKeyWorkConfigList();
          } catch (error) {
            this.$message.error(error.message);
          }
        })
        .catch(() => {
        });
    },
    async getTrainTypeList() {
      try {
        const { data } = await getTrainTypeList();
        this.trainTypeList = data.map((item) => ({
          id: item,
          label: item,
          value: item,
        }));
      } catch (error) {
        this.$message.error('车型数据异常');
      }
    },
    async openConfigDialog(row) {
      this.configDialog.formData.title = row ? '关键作业配置-编辑' : '关键作业配置-快速录入配置'
      // 初始化表单额外列
      this.columns.forEach(async (column) => {
        if (column.type == 'multipleChoice') {
          this.$set(this.configDialog.formData, column.property, []);
        } else {
          this.$set(this.configDialog.formData, column.property, '');
        }
        if (column.required) {
          this.$set(this.configDialog.formRules, column.property, [
            {
              required: true,
              message: '请选择辆序',
              trigger: ['change', 'blur'],
            },
          ]);
        } else {
          this.$set(this.configDialog.formRules, column.property, []);
        }
      });

      if (row) {
        for (const key in row) {
          this.$set(this.configDialog.formData, key, copyData(row[key]));
        }
      }

      if (this.currentSection) {
        this.configDialog.formData.unitCode = '';
      } else {
        this.configDialog.formData.unitCode = this.unitList[0].unitCode;
      }

      this.getTrainTypeList();
      await this.formItemFocus({ key: 'CAR' });

      this.$nextTick().then(async () => {
        await this.$refs.configDialogForm.clearValidate();
      });

      if (row && row.carNoList.length == 0) {
        this.$set(
          this.configDialog.formData,
          'carNoList',
          this.columnListMap.CARList.map((item) => item.value)
        );
      }
      this.configDialog.visible = true;
    },
    async formItemFocus({ key, property }) {
      // 辆序
      if (key === 'CAR') {
        let res = [];
        if (Array.isArray(this.configDialog.formData.trainModel)) {
          if (this.configDialog.formData.trainModel.length === 0) return;
          let requestList = this.configDialog.formData.trainModel.map((item) =>
            getKeyWorkExtraColumnValueList({ columnKey: key, trainModel: item })
          );
          const result = await axios.all(requestList);
          result.forEach((item) => {
            if (res.length === 0) {
              res = item;
            } else if (res.length < item.length) {
              res = item;
            }
          });
        } else {
          if (!this.configDialog.formData.trainModel) return;
          res = await getKeyWorkExtraColumnValueList({
            columnKey: key,
            trainModel: this.configDialog.formData.trainModel,
          });
        }
        this.$set(this.columnListMap, `${key}List`, res);
      }
      // }
    },
    async trainModelChange(val) {
      if (!val.toString()) {
        this.$set(this.configDialog.formData, 'carNoList', []);
        this.$set(this.columnListMap, 'CARList', []);
      } else {
        await this.formItemFocus({ key: 'CAR' });
        // if (this.configDialog.formData.carNoList.length === 0) {
        this.configDialog.formData.carNoList = this.columnListMap.CARList.map((item) => item.value);
        // }
      }
      this.$refs.configDialogForm.validateField('trainModel');
    },
    carNoListChange() {
      this.$refs.configDialogForm.validateField('carNoList');
    },
    async setKeyWorkConfig() {
      this.$refs.configDialogForm.validate(async (valid) => {
        if (valid) {
          let carNoObj = {};
          let trainModel = this.configDialog.formData.trainModel;
          if (Array.isArray(trainModel)) {
            if (trainModel.length === 0) return;
            for (let i = 0; i < trainModel.length; i++) {
              let res = await getKeyWorkExtraColumnValueList({ columnKey: 'CAR', trainModel: trainModel[i] });
              res.forEach((carNo) => {
                if (!carNoObj[trainModel[i]]) {
                  carNoObj[trainModel[i]] = [];
                }
                carNoObj[trainModel[i]].push(carNo.value);
              });
            }
          }

          let voidCarNoList = {};
          Object.keys(carNoObj).forEach((key) => {
            this.configDialog.formData.carNoList.forEach((carNo) => {
              if (carNoObj[key].indexOf(carNo) == -1) {
                if (!voidCarNoList[key]) {
                  voidCarNoList[key] = [];
                }
                voidCarNoList[key].push(carNo);
              }
            });
          });

          let dataObj = {};
          if (trainModel.length > 1) {
            Object.keys(voidCarNoList).forEach((key) => {
              let keys = voidCarNoList[key].join(',');
              if (!dataObj[keys]) {
                dataObj[keys] = [];
              }
              dataObj[voidCarNoList[key]].push(key);
            });
          }

          let contentList = [];
          Object.keys(dataObj).forEach((key) => {
            dataObj[key] = Array.from(dataObj[key]).join(',');
            contentList.push(`<div>${dataObj[key]}车型没有${key}辆序，该车型没有的辆序的数据将无法保存。</div>`);
          });

          let content = contentList.join('');
          if (Array.isArray(trainModel) && Object.keys(dataObj).length > 0) {
            this.$confirm(content, {
              title: '提示',
              dangerouslyUseHTMLString: true,
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            })
              .then(async () => {
                try {
                  let data = copyData(this.configDialog.formData);
                  data.trainModel = data.trainModel.toString();
                  // 全列传空
                  let carNoList = [];
                  if (this.columnListMap.CARList.length === this.configDialog.formData.carNoList.length) {
                    carNoList = [];
                  } else {
                    carNoList = this.configDialog.formData.carNoList;
                  }

                  await setKeyWorkConfig({ ...data, carNoList });
                  this.$message.success('保存成功');
                  this.configDialog.visible = false;
                  this.getKeyWorkConfigList();
                } catch (error) {}
              })
              .catch(() => {
                this.$message({
                  type: 'info',
                  message: '已取消保存',
                });
              });
          } else {
            try {
              let data = copyData(this.configDialog.formData);
              data.trainModel = data.trainModel.toString();
              // 全列传空
              let carNoList = [];
              if (this.columnListMap.CARList.length === this.configDialog.formData.carNoList.length) {
                carNoList = [];
              } else {
                carNoList = this.configDialog.formData.carNoList;
              }

              await setKeyWorkConfig({ ...data, carNoList });
              this.$message.success('保存成功');
              this.configDialog.visible = false;
              this.getKeyWorkConfigList();
            } catch (error) {}
          }
        }
      });
    },
    configDialogClose() {
      this.$refs.configDialogForm.resetFields();
      this.configDialog.formData = {
        unitCode: '',
        content: '',
        trainModel: [],
      };
      this.$set(this.columnListMap, 'CARList', []);
    },
    async getKeyWorkTypeList() {
      this.keyWorkTypeConfigDialog.tableData = await getKeyWorkTypeList({
        unitCode: this.unitCode,
      });
      this.keyWorkTypeConfigDialog.tableData.forEach((item, index) => {
        this.$set(this.keyWorkTypeConfigDialog.tableData[index], 'code', this.randomRangeId(26));
      });
      this.keyWorkTypeConfigDialog.deleteTypeList = [];
      this.keyWorkTypeConfigDialog.oriTableData = JSON.parse(JSON.stringify(this.keyWorkTypeConfigDialog.tableData));
      this.keyWorkTypeConfigDialog.visible = true;
    },
    resetEditableStatusAndData() {
      this.keyWorkTypeConfigDialog.tableData.forEach((item) => {
        this.$set(this.keyWorkTypeConfigDialog.editable, item.id, false);
        this.$set(this.keyWorkTypeConfigDialog.typeObj, item.id, item.name);
      });
    },
    // validateTypeName(name) {
    //   return this.keyWorkTypeConfigDialog.tableData.find((item) => item.name == name);
    // },
    async addType() {
      if(this.keyWorkTypeConfigDialog.tableData.find((item) => item.name == this.keyWorkTypeConfigDialog.formData.type)) {
        this.$message.error(`${this.keyWorkTypeConfigDialog.formData.type}  出现重复的类型配置!`);
      } else {
        try {
          await this.$refs.keyWorkTypeConfigDialogForm.validate();
          // if (this.validateTypeName(this.keyWorkTypeConfigDialog.formData.type)) {
          //   throw new Error('类型名称不能重复');
          // }
  
          this.keyWorkTypeConfigDialog.tableData.push({
            name: this.keyWorkTypeConfigDialog.formData.type,
            unitCode: this.unitCode,
            code: this.randomRangeId(26),
          });
          this.$refs.keyWorkTypeConfigDialogForm.resetFields();
        } catch (error) {
          if (!error.showed) {
            this.$message.error(error.message);
          }
        }
      }
    },
    editType(row, column, cell, e) {
      if (!column.showOverflowTooltip) return;
      if (this.keyWorkTypeConfigDialog.editable[row.id]) return;
      this.resetEditableStatusAndData();
      this.$set(this.keyWorkTypeConfigDialog.editable, row.id, true);
    },
    async confirmEditType(id, index) {
      if(
        this.keyWorkTypeConfigDialog.tableData.find((item) => item.name == this.keyWorkTypeConfigDialog.typeObj[id])
        &&
        this.keyWorkTypeConfigDialog.tableData[index].name != this.keyWorkTypeConfigDialog.typeObj[id]
        ) {
        this.$message.error(`${this.keyWorkTypeConfigDialog.typeObj[id]}  出现重复的类型配置!`);
      } else {
        // 请求修改数据
        try {
          if (this.keyWorkTypeConfigDialog.typeObj[id] == '') return;
          // if (this.validateTypeName(this.keyWorkTypeConfigDialog.typeObj[id])) {
          //   throw new Error('类型名称不能重复');
          // }
          let typeList = copyData(this.keyWorkTypeConfigDialog.tableData);
          typeList[index].name = this.keyWorkTypeConfigDialog.typeObj[id];
          this.keyWorkTypeConfigDialog.tableData = copyData(typeList);
        } catch (error) {
          if (!error.showed) {
            this.$message.error(error.message);
          }
        }
      }
    },
    cancelEditType(id) {
      // 关闭input
      this.$set(this.keyWorkTypeConfigDialog.editable, id, false);
    },
    async deleteType(id, index) {
      try {
        this.$set(this.keyWorkTypeConfigDialog.editable, id, false);
        let typeList = copyData(this.keyWorkTypeConfigDialog.tableData);
        let deleteType = typeList.splice(index, 1);
        if (id) {
          this.keyWorkTypeConfigDialog.deleteTypeList.push({
            id,
            name: deleteType[0].name,
            deleted: '1',
            unitCode: this.unitCode,
          });
        }
        this.keyWorkTypeConfigDialog.tableData = copyData(typeList);
      } catch (error) {}
    },
    keyWorkTypeConfigDialogClose() {
      this.$refs.keyWorkTypeConfigDialogForm.resetFields();
      this.getKeyWorkExtraColumnList();
    },
    // 类型配置排序
    typeSortChange(upOrDown, code) {
      const targetTypetIndex = this.keyWorkTypeConfigDialog.tableData.findIndex((item) => item.code === code);
      let typeList = copyData(this.keyWorkTypeConfigDialog.tableData);
      let typeSort = typeList[targetTypetIndex];
      if (upOrDown === 'up') {
        typeList[targetTypetIndex] = typeList[targetTypetIndex - 1];
        typeList[targetTypetIndex - 1] = typeSort;
      } else {
        typeList[targetTypetIndex] = typeList[targetTypetIndex + 1];
        typeList[targetTypetIndex + 1] = typeSort;
      }
      this.keyWorkTypeConfigDialog.tableData = copyData(typeList);
    },

    async setTypeList() {
      let params = [...this.keyWorkTypeConfigDialog.tableData, ...this.keyWorkTypeConfigDialog.deleteTypeList];
      let {data} = await setKeyWorkType(params);
      if (data.code == 1) {
        this.$message.success('保存成功!');
      } else {
        this.$message.error(data.msg);
      }
      this.keyWorkTypeConfigDialog.visible = false;
    },

    cancelSetTypeList() {
      if (
        JSON.stringify(this.keyWorkTypeConfigDialog.tableData) ===
        JSON.stringify(this.keyWorkTypeConfigDialog.oriTableData)
      ) {
        this.keyWorkTypeConfigDialog.visible = false;
      } else {
        this.$confirm('您的关键作业类型已变更，是否确定!', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }).then(() => {
          this.keyWorkTypeConfigDialog.visible = false;
        });
      }
    },

    randomRangeId(num) {
      var returnStr = '',
        charStr = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
      for (var i = 0; i < num; i++) {
        var index = Math.round(Math.random() * (charStr.length - 1));
        returnStr += charStr.substring(index, index + 1);
      }
      return returnStr;
    },
  },

  filters: {
    filterCarNoList(val) {
      if (val.length == 0) {
        return '全列';
      } else {
        return val.toString();
      }
    },
  },
});
