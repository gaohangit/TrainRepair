window.app = new Vue({
  el: '#app',
  filters: {
    flagFilter(val) {
      return val == 1 ? '生效' : '失效';
    },
  },
  data() {
    return {
      // 新增弹出框
      addFormData: {
        // 单据类型列表
        dataDocumentsList: [],
        // 选中的单据类型
        dataDocuments: '',
        // 单据子类型列表
        dataSonDocumentsList: [],
        // 使用此数组发送请求对单据子类型列表进行增
        addDataSonDocumentsList: [],
        // 使用此数组发送请求对单据子类型列表进行删
        removeDataSonDocumentsList: [],
        // 选中的单据子类型
        dataSonDocuments: '',
        // 单据名称列表
        billsNameList: [],
        // 使用此数组发送请求对单据名称列表进行增
        addBillsNameList: [],
        // 使用此数组发送请求对单据名称列表进行删
        removeBillsNameList: [],
        // 选中的单据名称
        billsName: '',
        // 关联条件下拉列表
        cruxConditionList: [],
        // 关联属性签字人员下拉列表
        signers: [],
        // 选中关联条件
        checkList: [],
        // 展示关联条件
        jionCondition: '',
        // 关联属性
        attributeRelationship: '',
        // 是否支持多张单据
        moreForm: '0',
        // 备注
        remark: '',

        // 系统新增多出

        // 项目类型编码
        projectCoding: '',
        // 单据是否可以编辑
        whetherEdit: '0',

        // 关联属性查看
        // 属性类型
        attributeTypeName: '',
        // 属性名称
        newAttributeName: '',
        // 属性说明
        attributeState: '',
        // 属性模式
        attributeModel: '',
        // 取值范围
        // 属性模式列表
        seletAttributeModel: [],
        // 下拉
        selectScope: '',
        // 数字
        numScope: '',
        // 签字
        signatureScope: '',
        // 是否关联属性
        myAttribute: '',
        // 是否只读
        myRead: '',
        // 书否必填
        myRequired: '',
      },

      // 新增弹框的验证
      formRules: {
        dataDocuments: [{ required: true, message: '请选择单据类型', trigger: 'change' }],
        dataSonDocuments: [{ required: true, message: '请选择单据子类型', trigger: 'change' }],
        billsName: [{ required: true, message: '请选择单据名称', trigger: 'change' }],
        attributeRelationship: [{ required: true, message: '请选择关联属性', trigger: 'change' }],
      },

      // 搜索部分
      // 单据类型列表
      dataDocumentsList: [],
      // 选中单据类型
      dataDocuments: '',
      // 单据子类型列表
      dataSonDocumentsList: [],
      // 选中单据子类型
      dataSonDocuments: '',
      // 单据名称
      billsName: '',
      // 状态
      state: [
        { index: '', value: '全部' },
        { index: '1', value: '生效' },
        { index: '0', value: '失效' },
      ],
      // 选中的状态
      seletState: '1',
      // 类型配置信息列表
      typeList: [],

      // 类别
      category: '',
      // 类别列表
      categoryList: [
        { code: 1, name: '系统' },
        { code: 0, name: '自定义' },
        { code: '', name: '全部' }
      ],

      // 控制新增/编辑弹框打开关闭
      append: false,
      // 确认是新增还是编辑
      newAndEdit: '',
      // 通过查看打开弹窗
      prohibit: false,

      // 新增弹框单据子类型编辑按钮的弹窗
      editDataSonDocumentsPopup: false,
      // 是否禁用单据子类型编辑的确定按钮
      determineDataSonDocumentsBtnisDisabled: true,
      // 单据子类型名称
      newDataSonDocuments: '',
      // 单据子类型编码
      billsSonCoding: '',
      dataSonDocumentsTableList: [],

      // 新增弹框单据名称编辑按钮的弹窗
      editBillsNamePopup: false,
      // 单据名称
      newBillsName: '',
      // 单据名称编码
      billsNameCoding: '',
      billsNameTableList: [],

      // 新增关联属性编辑按钮的弹窗
      editAttributeChoice: false,

      // 新增-关联条件下拉框弹出收起控制
      signatureDown: false,

      // 关联属性列表-未选中的
      currentRelationAttributeList: [],
      // 关联属性列表-选中的
      currentSelectRelationAttributeList: [],
      // 正在编辑中-关联属性列表-未选中的
      editingRelationAttributeList: [],
      // 正在编辑中-关联属性列表-选中的
      editingSelectRelationAttributeList: [],
      // 关联属性添加的中间件
      multipleSelection: [],
      // 关联属性移除的中间件
      removeSelection: [],
      // 未选中关联属性列表模糊查询
      unCheckedQuery: '',
      // 选中关联属性列表模糊查询
      checkedQuery: '',
      // 打开页面获取权限
      jurisdiction: '',
      // 关联属性查看功能
      serial: false,

      // 分页部分
      // 总条数
      count: 0,
      // 当前页条数
      pageSize: 20,
      // 当前所在页
      pageNum: 1,
      relationConditionTips:
        '关联条件：选择某一关联条件后，该类型单据会和该关联条件绑定；举例：如果关联条件选择了“车型”，则该类型单据配置时会要求按照车型进行配置，可简单理解为每个车型需要配置一张单据。',
    };
  },
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('单据类型配置'),
    // 取值范围名称
    valueName() {
      let value = this.addFormData.seletAttributeModel.filter((item) => {
        return item.attributeModeCode === this.addFormData.attributeModel;
      });

      return value[0] && value[0].note;
    },

    // 模糊查询
    // 未选中
    filteredUncheckedAttributeList() {
      if (this.unCheckedQuery == '') {
        return this.editingRelationAttributeList;
      } else {
        return this.editingRelationAttributeList.filter((value) => {
          return value.attributeName.toLowerCase().includes(this.unCheckedQuery.toLowerCase());
        });
      }
    },

    // 选中
    filteredCheckedAttributeList() {
      if (this.checkedQuery == '') {
        return this.editingSelectRelationAttributeList;
      } else {
        return this.editingSelectRelationAttributeList.filter((value) => {
          return value.attributeName.toLowerCase().includes(this.checkedQuery.toLowerCase());
        });
      }
    },

    getDataDocuments() {
      let data;
      this.dataDocumentsList.forEach((item) => {
        if (item.templateTypeCode == this.addFormData.dataDocuments) {
          data = item.templateTypeName;
        }
      });

      return data;
    },

    getDataSonDocuments() {
      let data;
      this.addFormData.dataSonDocumentsList.forEach((item) => {
        if (item.templateTypeCode == this.addFormData.dataSonDocuments) {
          data = item.templateTypeName;
        }
      });

      return data;
    },

    allExistsSonDocumentsList() {
      return (row) => {
        let exists = this.typeList.some((item) => {
          return item.fatherTypeCode == row.templateTypeCode && item.templateLinkAttrNames;
        });

        if (this.jurisdiction == '1') {
          if (!exists) {
            return true;
          } else {
            return false;
          }
        } else if (this.jurisdiction == '2' && row.sysType == '0' && !exists) {
          return true;
        } else {
          return false;
        }
      };
    },

    billsNameList() {
      if (this.newAndEdit == 'edit') {
        return this.addFormData.billsNameList;
      } else if (this.addFormData.dataSonDocuments && this.addFormData.billsNameList.length > 0) {
        return this.addFormData.billsNameList.filter((item) => {
          let exists = this.typeList.some((type) => {
            return type.templateTypeCode == item.templateTypeCode && type.templateLinkAttrNames;
          });

          if (!exists) {
            return item;
          }
        });
      } else {
        return [];
      }
    },

    existsBillsName() {
      return (row) => {
        let exists = this.typeList.some((item) => {
          return item.templateTypeCode == row.templateTypeCode && item.templateLinkAttrNames;
        });

        if (this.jurisdiction == '1') {
          if (!exists) {
            return true;
          } else {
            return false;
          }
        } else if (this.jurisdiction == '2' && row.sysType == '0' && !exists) {
          return true;
        } else {
          return false;
        }
      };
    },

    rowKey(row) {
      return row.id;
    },
  },

  created() {
    this.jurisdiction = this.getUrlKey('type');
    if (this.jurisdiction == '2') {
      this.category = '';
    }
    this.getTemplateType('1', '', '');
    this.getTemplateTypeList();
    this.getRoles();
    this.getQueryList();
    this.seltemplateattriutemodeLis();
  },
  watch: {
    'addFormData.addDataSonDocumentsList'(val) {
      let dataSonDocumentsList = [
        ...this.addFormData.addDataSonDocumentsList,
        ...this.addFormData.removeDataSonDocumentsList,
      ];
      console.log(dataSonDocumentsList)
      if(dataSonDocumentsList.length == 0) {
        this.determineDataSonDocumentsBtnisDisabled = true
      } else {
        this.determineDataSonDocumentsBtnisDisabled = false
      }
    },
    'addFormData.removeDataSonDocumentsList'(val) {
      let dataSonDocumentsList = [
        ...this.addFormData.addDataSonDocumentsList,
        ...this.addFormData.removeDataSonDocumentsList,
      ];
      console.log(dataSonDocumentsList)
      if(dataSonDocumentsList.length == 0) {
        this.determineDataSonDocumentsBtnisDisabled = true
      } else {
        this.determineDataSonDocumentsBtnisDisabled = false
      }
    }
  },
  methods: {
    // 获取属性模式
    async seltemplateattriutemodeLis() {
      let {
        data: { data },
      } = await axios({
        method: 'get',
        url: '/apiTrainRepairMidGround/templateAttribute/getTemplateAttributeModeList',
      });
      this.addFormData.seletAttributeModel = data;
    },

    // 控制属性模式-签字的选择角色下拉的弹出
    openSignatureDown() {
      this.signatureDown = !this.signatureDown;
    },

    // 获取权限信息
    getUrlKey(name) {
      return (
        decodeURIComponent(
          (new RegExp('[?|@]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ''])[1].replace(
            /\+/g,
            '%20'
          )
        ) || null
      );
    },

    // 展示列表斑马纹样式
    tableRowClassName({ row, rowIndex }) {
      if (rowIndex % 2 == 1) {
        return 'warning-row';
      } else {
        return 'success-row';
      }
    },

    // 获取单据类型/单据子类型/单据名称列表
    async getTemplateType(type, fatherCode, cFlag = '1') {
      let sysType;
      if (type == 1) {
        sysType = '';
      } else {
        if (this.jurisdiction == '1') {
          sysType = 1;
        } else {
          sysType = 0;
        }
      }

      let {
        data: { data },
      } = await axios({
        method: 'get',
        url: '/apiTrainRepairMidGround/templateType/getTemplateType',
        params: {
          fatherCode,
          type,
          sysType,
          cFlag
        },
      });

      if (type == 1) {
        this.addFormData.dataDocumentsList = data;
        this.dataDocumentsList = data;
      } else if (type == 2) {
        this.addFormData.dataSonDocumentsList = data;
        if(!this.addFormData.dataSonDocumentsList.map((value) => {
          return value.templateTypeCode
        }).includes(this.addFormData.dataSonDocuments)) {
          this.addFormData.dataSonDocuments = ''
        }
      } else {
        this.addFormData.billsNameList = data;
      }
    },

    // 获取关联条件下拉列表
    async getQueryList() {
      let {
        data: { data },
      } = await axios({
        method: 'get',
        url: '/apiTrainRepairMidGround/templateType/getQueryList',
      });
      this.addFormData.cruxConditionList = data;
    },

    // 获取签字角色数据
    async getRoles() {
      let {
        data: { data },
      } = await axios({
        method: 'get',
        url: '/apiTrainRepairMidGround/templateAttribute/getRoles',
      });

      this.addFormData.signers = data;
    },

    /*
     *按需搜索部分
     **/
    // 切换单据类型
    async cutDataDocuments() {
      this.dataSonDocuments = '';

      let {
        data: { data },
      } = await axios({
        method: 'get',
        url: '/apiTrainRepairMidGround/templateType/getTemplateType',
        params: {
          fatherCode: this.dataDocuments,
          type: '2',
          sysType: '',
          cFlag: ''
        },
      });
      this.dataSonDocumentsList = data;
    },

    // 点击查询按钮
    inquiry() {
      this.pageNum = 1;
      this.getTemplateTypeList();
    },

    // 点击重置
    reset() {
      this.dataDocuments = '';
      this.dataSonDocuments = '';
      this.dataSonDocumentsList = [];
      this.billsName = '';
      this.seletState = '1';
      if (this.jurisdiction == '2') {
        this.category = '';
      }
    },

    // 获取表单列表
    async getTemplateTypeList() {
      let loading = this.$loading();
      let sysType;
      if (this.jurisdiction == '1') {
        sysType = 1;
      } else if (this.jurisdiction == '2') {
        sysType = this.category;
      }
      let { data } = await axios({
        method: 'post',
        url: '/apiTrainRepairMidGround/templateType/getTemplateTypeList',
        data: {
          oneTypeCode: this.dataDocuments,
          fatherTypeCode: this.dataSonDocuments,
          templateTypeName: this.billsName,
          sysType,
          flag: this.seletState,
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        },
      });
      if (data.code == 1) {
        this.typeList = data.data.templateAttributeList;
        this.count = data.data.count;
        loading.close();
      } else {
        loading.close();
      }
    },

    /*
     *新增弹框
     **/
    // 切换单据类型
    newCutDataDocuments() {
      this.addFormData.dataSonDocuments = '';
      this.addFormData.dataSonDocumentsList = [];
      this.addFormData.billsName = '';
      this.addFormData.billsNameList = [];
      if (this.addFormData.dataDocuments) {
        this.getTemplateType('2', this.addFormData.dataDocuments);
      }
    },

    // 切换单据子类型
    cutDataSonDocuments() {
      this.addFormData.billsName = '';
      this.addFormData.billsNameList = [];
      if (this.addFormData.dataSonDocuments) {
        this.getTemplateType('3', this.addFormData.dataSonDocuments);
      }
    },


    // 点击新增按钮弹出弹框获取单据类型
    newlyAdd() {
      this.append = true;
      this.newAndEdit = 'add';
      this.getTemplateattributeList();

      this.$nextTick().then(async () => {
        await this.$refs.addForm.clearValidate();
      });
    },

    // 点击单据子类型编辑按钮弹出页面
    openDataSonDocumentsEdit() {
      if (this.addFormData.dataDocuments) {
        this.editDataSonDocumentsPopup = true;
        this.determineDataSonDocumentsBtnisDisabled = true;
        this.dataSonDocumentsTableList = JSON.parse(JSON.stringify(this.addFormData.dataSonDocumentsList));
      } else {
        this.$message.error('请先选择单据类型');
      }
    },

    // 点击单据子类型编辑弹出框添加按钮
    newDataSonDocumentsList() {
      let newDataSonDocuments = this.dataSonDocumentsTableList.findIndex((item) => {
        return item.templateTypeName == this.newDataSonDocuments;
      });

      let billsSonCoding = this.dataSonDocumentsTableList.findIndex((item) => {
        return item.templateTypeCode == this.billsSonCoding;
      });

      if (this.jurisdiction == 1 && !this.billsSonCoding) {
        return this.$message.error('请输入单据编码!');
      }

      if (newDataSonDocuments != -1) {
        this.$message.error('单据子类型已存在，不能重复添加!');
      } else if (this.newDataSonDocuments == null || this.newDataSonDocuments.length == 0) {
        this.$message.error('单据子类型不能为空!');
      } else {
        if (this.jurisdiction == 1 && billsSonCoding != -1) {
          this.$message.error('单据编码已存在，不能重复添加!');
        } else {
          let templateTypeCode, templateTypeName, fatherTypeCode, sysType, sysTemplate, type, operationType;
          templateTypeName = this.newDataSonDocuments;
          fatherTypeCode = this.addFormData.dataDocuments;
          type = '2';
          operationType = '1';
          if (this.jurisdiction == '2') {
            templateTypeCode = '';
            sysType = '0';
            sysTemplate = '1';
          } else if (this.jurisdiction == '1') {
            templateTypeCode = this.billsSonCoding;
            sysType = '1';
            sysTemplate = '0';
          }
          let obj = {
            templateTypeCode,
            templateTypeName,
            fatherTypeCode,
            sysType,
            sysTemplate,
            type,
            operationType,
          };
          this.dataSonDocumentsTableList.unshift(obj);
          this.addFormData.addDataSonDocumentsList.unshift(obj);
          this.newDataSonDocuments = '';
          this.billsSonCoding = '';
        }
      }
    },

    // 点击单据子类型编辑弹出框删除按钮
    removeScope(index) {
      this.$confirm('删除单据子类型,则单据子类型下的单据名称都会被删除', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          let templateTypeName = this.dataSonDocumentsTableList[index].templateTypeName;
          let list = this.addFormData.addDataSonDocumentsList.filter((item) => {
            return item.templateTypeName == templateTypeName;
          });

          if (!list.length > 0) {
            this.dataSonDocumentsTableList[index].operationType = '0';
            this.addFormData.removeDataSonDocumentsList.push({
              ...this.dataSonDocumentsTableList[index],
            });
          } else {
            for (let i = 0; i < this.addFormData.addDataSonDocumentsList.length; i++) {
              if (this.addFormData.addDataSonDocumentsList[i].templateTypeName === templateTypeName) {
                this.addFormData.addDataSonDocumentsList.splice(i, 1);
              }
            }
          }
          this.dataSonDocumentsTableList.splice(index, 1);
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除',
          });
        });
    },

    // 添加/删除单据子类型(单据名称)列表
    async addTemplateTypes(list, num, code) {
      await axios({
        method: 'post',
        url: '/apiTrainRepairMidGround/templateType/addTemplateTypes',
        data: {
          templateTypeList: list,
        },
      })
        .then((res) => {
          if(res.data.code == 1) {
            this.$message({
              message: res.data.msg,
              type: 'success',
            });
          } else {
            this.$message({
              message: res.data.msg,
              type: 'error',
            });
          }
          this.getTemplateType(num, code);
        })
        .catch((err) => {
          this.$message.error('修改失败');
        });
    },

    // 单据子类型编辑-确定
    determineDataSonDocuments() {
      let dataSonDocumentsList = [
        ...this.addFormData.addDataSonDocumentsList,
        ...this.addFormData.removeDataSonDocumentsList,
      ];
      if(dataSonDocumentsList.length == 0) return
      this.editDataSonDocumentsPopup = false;
      this.addTemplateTypes(dataSonDocumentsList, '2', this.addFormData.dataDocuments);
    },

    // 编辑框关闭时，清空数据
    onEditDataSonDocumentsClosed(){
      this.newDataSonDocuments = '';
      this.billsSonCoding = '';
      this.addFormData.addDataSonDocumentsList = [];
      this.addFormData.removeDataSonDocumentsList = [];
    },
    // 单据子类型编辑-取消
    cancelDataSonDocuments() {
      this.editDataSonDocumentsPopup = false;
    },

    // 点击单据名称编辑按钮弹出页面
    openbillsNameEdit() {
      if (this.addFormData.dataSonDocuments) {
        this.editBillsNamePopup = true;
        this.billsNameTableList = JSON.parse(JSON.stringify(this.addFormData.billsNameList));
      } else {
        this.$message.error('请先选择单据子类型!');
      }
    },

    // 点击单据名称辑弹出框添加按钮
    newbillsNameList() {
      let newBillsName = this.addFormData.billsNameList.findIndex((item) => {
        return item.templateTypeName == this.newBillsName;
      });

      let billsNameCoding = this.addFormData.billsNameList.findIndex((item) => {
        return item.templateTypeCode == this.billsNameCoding;
      });

      if (this.jurisdiction == 1 && !this.billsNameCoding) {
        return this.$message.error('请输入单据名称编码!');
      }

      if (newBillsName != -1) {
        this.$message.error('单据名称已存在，不能重复添加!');
      } else if (this.newBillsName == null || this.newBillsName.length == 0) {
        this.$message.error('单据名称不能为空!');
      } else {
        if (this.jurisdiction == 1 && billsNameCoding != -1) {
          this.$message.error('单据名称编码已存在，不能重复添加!');
        } else {
          let templateTypeCode, templateTypeName, fatherTypeCode, sysType, sysTemplate, type, operationType;
          templateTypeName = this.newBillsName;
          fatherTypeCode = this.addFormData.dataSonDocuments;
          type = '3';
          operationType = '1';
          if (this.jurisdiction == '2') {
            templateTypeCode = '';
            sysType = '0';
            sysTemplate = '1';
          } else if (this.jurisdiction == '1') {
            templateTypeCode = this.billsNameCoding;
            sysType = '1';
            sysTemplate = '0';
          }

          let obj = {
            templateTypeCode,
            templateTypeName,
            fatherTypeCode,
            sysType,
            sysTemplate,
            type,
            operationType,
          };

          this.billsNameTableList.unshift(obj);
          this.addFormData.addBillsNameList.unshift(obj);

          this.newBillsName = '';
          this.billsNameCoding = '';
        }
      }
    },

    // 点击单据子类型编辑弹出框删除按钮
    newRemoveScope(index) {
      let templateTypeName = this.billsNameTableList[index].templateTypeName;
      let list = this.addFormData.addBillsNameList.filter((item) => {
        return item.templateTypeName == templateTypeName;
      });
      if (list == false) {
        this.billsNameTableList[index].operationType = '0';
        this.addFormData.removeBillsNameList.push({
          ...this.billsNameTableList[index],
        });
      } else {
        for (let i = 0; i < this.addFormData.addBillsNameList.length; i++) {
          if (this.addFormData.addBillsNameList[i].templateTypeName === templateTypeName) {
            this.addFormData.addBillsNameList.splice(i, 1);
          }
        }
      }
      this.billsNameTableList.splice(index, 1);
    },

    // 单据名称编辑-确定
    determineBillsName() {
      this.editBillsNamePopup = false;
      let dataSonDocumentsList = [...this.addFormData.addBillsNameList, ...this.addFormData.removeBillsNameList];
      this.addTemplateTypes(dataSonDocumentsList, '3', this.addFormData.dataSonDocuments);
    },
    // 关闭单据名称编辑框时清空数据
    editBillsNameDialogClosed(){
      this.newBillsName = '';
      this.billsNameCoding = '';
      this.addFormData.addBillsNameList = [];
      this.addFormData.removeBillsNameList = [];
    },
    // 单据名称编辑-取消
    cancelBillsName() {
      this.editBillsNamePopup = false;
    },

    // 点击关联属性编辑按钮弹出页面
    openRelationEdit() {
      this.editingRelationAttributeList = JSON.parse(JSON.stringify(this.currentRelationAttributeList));
      this.editingSelectRelationAttributeList = JSON.parse(JSON.stringify(this.currentSelectRelationAttributeList));
      this.editAttributeChoice = true;
    },

    // 获取全部属性列表
    async getTemplateattributeList() {
      this.currentRelationAttributeList = [];
      this.currentSelectRelationAttributeList = [];

      let {
        data: {
          data: { templateAttributeList },
        },
      } = await axios({
        method: 'post',
        url: '/apiTrainRepairMidGround/templateAttribute/getTemplateAttributeList',
        data: {
          attributeCode: '',
          attributeTypeCode: '',
          attributeName: '',
          pageNum: 1,
          pageSize: -1,
        },
      });
      // 默认全部都是未选中
      this.currentRelationAttributeList = templateAttributeList;
    },

    notConfig(val) {
      this.multipleSelection = val;
    },

    // 添加
    addTo() {
      if (this.multipleSelection.length > 0) {
        this.editingSelectRelationAttributeList.push(...this.multipleSelection);
        this.multipleSelection.forEach((item) => {
          let index = this.editingRelationAttributeList.indexOf(item);
          this.editingRelationAttributeList.splice(index, 1);
        });
      } else {
        this.$message({
          message: '请选择属性！',
          type: 'warning',
        });
      }
    },

    yesConfig(val) {
      this.removeSelection = val;
    },

    // 移除
    removeTo() {
      if (this.removeSelection.length > 0) {
        this.editingRelationAttributeList.push(...this.removeSelection);
        this.removeSelection.forEach((item) => {
          let index = this.editingSelectRelationAttributeList.indexOf(item);
          this.editingSelectRelationAttributeList.splice(index, 1);
        });
      } else {
        this.$message({
          message: '请选择属性！',
          type: 'warning',
        });
      }
    },

    // 点击查看按钮
    seeValue(index, num) {
      this.serial = true;
      if (num == '1') {
        let editingRelationAttributeList = this.editingRelationAttributeList[index];
        // 获取详情信息进行回填
        this.addFormData.attributeTypeName = editingRelationAttributeList.attributeTypeName;
        this.addFormData.newAttributeName = editingRelationAttributeList.attributeName;
        this.addFormData.attributeState = editingRelationAttributeList.attributeNote;
        this.addFormData.attributeModel = editingRelationAttributeList.attributeModeCode;
        this.addFormData.myAttribute = editingRelationAttributeList.linkAttr;
        this.addFormData.myRead = editingRelationAttributeList.readOnly;
        this.addFormData.myRequired = editingRelationAttributeList.backFillVerify;

        this.addFormData.selectScope = editingRelationAttributeList.templateValues;
        if (editingRelationAttributeList.attributeModeCode == 'NumberControl') {
          if (editingRelationAttributeList.templateValues == '0') {
            this.addFormData.selectScope = '整数';
          } else {
            this.addFormData.selectScope = editingRelationAttributeList.templateValues;
          }
        } else if (editingRelationAttributeList.attributeModeCode == 'SignControl') {
          let templateValues = editingRelationAttributeList.templateValues.split(',');
          let list = [];
          templateValues.forEach((item) => {
            this.addFormData.signers.forEach((value) => {
              if (item === value.id) {
                list.push(value.text);
              }
            });
          });
          this.addFormData.selectScope = list.join(',');
        }
      } else if (num == '2') {
        let editingSelectRelationAttributeList = this.editingSelectRelationAttributeList[index];
        // 获取详情信息进行回填
        this.addFormData.attributeTypeName = editingSelectRelationAttributeList.attributeTypeName;
        this.addFormData.newAttributeName = editingSelectRelationAttributeList.attributeName;
        this.addFormData.attributeState = editingSelectRelationAttributeList.attributeNote;
        this.addFormData.attributeModel = editingSelectRelationAttributeList.attributeModeCode;
        this.addFormData.myAttribute = editingSelectRelationAttributeList.linkAttr;
        this.addFormData.myRead = editingSelectRelationAttributeList.readOnly;
        this.addFormData.myRequired = editingSelectRelationAttributeList.backFillVerify;

        this.addFormData.selectScope = editingSelectRelationAttributeList.templateValues;
        if (editingSelectRelationAttributeList.attributeModeCode == 'NumberControl') {
          if (editingSelectRelationAttributeList.templateValues == '0') {
            this.addFormData.selectScope = '整数';
          } else {
            this.addFormData.selectScope = editingSelectRelationAttributeList.templateValues;
          }
        } else if (editingSelectRelationAttributeList.attributeModeCode == 'SignControl') {
          let templateValues = editingSelectRelationAttributeList.templateValues.split(',');
          let list = [];
          templateValues.forEach((item) => {
            this.addFormData.signers.forEach((value) => {
              if (item === value.id) {
                list.push(value.text);
              }
            });
          });
          this.addFormData.selectScope = list.join(',');
        }
      }
    },

    // 关联属性框关闭时，重置其数据
    onPropertiesDialogClosed(){
      this.editingRelationAttributeList = [];
      this.editingSelectRelationAttributeList = [];
      this.unCheckedQuery = '';
      this.checkedQuery = '';
    },

    // 关联属性-确认
    confirmProperties() {
      this.currentRelationAttributeList = JSON.parse(JSON.stringify(this.editingRelationAttributeList));
      this.currentSelectRelationAttributeList = JSON.parse(JSON.stringify(this.editingSelectRelationAttributeList));
      let num = [];
      this.editingSelectRelationAttributeList.forEach((item) => {
        num.push(item.attributeName);
      });
      this.addFormData.attributeRelationship = num.join(',');
      this.editAttributeChoice = false;
    },

    // 关联属性-取消
    cancelProperties() {
      this.editAttributeChoice = false;
    },

    // 关联条件选择
    selectSigners(val) {
      let list = [];
      val.forEach((item) => {
        this.addFormData.cruxConditionList.forEach((value) => {
          if (item === value.queryCode) {
            list.push(value.queryName);
          }
        });
      });
      list = list.join(',');
      this.addFormData.jionCondition = list;
    },

    // 表单数据项新增或修改
    async updataTemplateType() {
      let itemType, sysType;
      if (this.jurisdiction == 1) {
        sysType = 1;
        itemType = this.addFormData.projectCoding;
      } else {
        sysType = 0;
        itemType = '';
      }

      let num = [];
      this.currentSelectRelationAttributeList.forEach((item) => {
        num.push(item.attributeCode);
      });
      let templateLinkAttrs = num.join(',');

      let res = await axios({
        method: 'post',
        url: '/apiTrainRepairMidGround/templateType/updateTemplateType',
        data: {
          templateTypeCode: this.addFormData.billsName,
          moreCellFlag: this.addFormData.moreForm,
          remark: this.addFormData.remark,
          queryCodes: this.addFormData.checkList.join(','),
          templateLinkAttrs,
          itemType,
          sysTemplate: this.jurisdiction == '1' ? this.addFormData.whetherEdit : 1,
          sysType,
        },
      });

      if (this.newAndEdit == 'add') {
        if (res.data.code == 1) {
          this.$message({
            message: res.data.msg,
            type: 'success',
          });
          this.getTemplateTypeList();
        } else {
          this.$message.error(res.data.msg);
        }
      } else if (this.newAndEdit == 'edit') {
        if (res.data.code == 1) {
          this.$message({
            message: res.data.msg,
            type: 'success',
          });
          this.getTemplateTypeList();
        } else {
          this.$message.error(res.data.msg);
        }
      }
      this.inquiry();
      this.empty();
    },

    // 新增确认
    addition(addForm) {
      this.$refs[addForm].validate((valid) => {
        if (valid) {
          this.updataTemplateType();
          this.closePop();
        }
      });
    },

    // 关闭弹框时重置数据
    onPopUp() {
      this.prohibit = false;
      this.empty();
    },

    // 关闭新增弹窗
    closePop() {
      this.append = false;
    },

    // 清空表单
    empty() {
      this.addFormData.dataDocuments = '';
      this.addFormData.dataSonDocumentsList = [];
      this.addFormData.dataSonDocuments = '';
      this.addFormData.billsNameList = [];
      this.addFormData.billsName = '';
      this.addFormData.checkList = [];
      this.addFormData.jionCondition = '';
      this.addFormData.attributeRelationship = '';
      this.addFormData.moreForm = '0';
      this.addFormData.remark = '';
      this.addFormData.whetherEdit = '0';
      this.addFormData.projectCoding = '';
    },

    // 点击删除列表项
    delteValue(id) {
      this.$confirm('此操作将永久作废该类型，是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        this.delTemplateAttribute(id);
        this.getTemplateTypeList();
      });
    },

    // 删除列表项
    async delTemplateAttribute(templateTypeCode) {
      await axios({
        method: 'get',
        url: '/apiTrainRepairMidGround/templateType/delTemplateType',
        params: {
          templateTypeCode,
        },
      })
        .then(() => {
          this.$message({
            message: '单据类型已作废',
            type: 'success',
          });
          this.getTemplateTypeList();
        })
        .catch(() => {
          this.$message.error('删除失败');
        });
    },

    // 点击编辑按钮
    newEdit(index) {
      this.append = true;
      this.newAndEdit = 'edit';
      this.getTemplateattributeList().then((res) => {
        this.assignment(index);
      });

      this.$nextTick().then(async () => {
        await this.$refs.addForm.clearValidate();
      });
    },

    // 对列表项进行回填，即将查询到的数据整理到表单上
    async assignment(index, cFlag) {
      let typeList = this.typeList[index];
      // 进行表单赋值
      this.addFormData.dataDocuments = typeList.oneTypeCode;
      await this.getTemplateType('2', typeList.oneTypeCode, '');
      this.addFormData.dataSonDocuments = typeList.fatherTypeCode;
      this.addFormData.billsName = typeList.templateTypeCode;
      await this.getTemplateType('3', typeList.fatherTypeCode, cFlag);
      this.addFormData.checkList = typeList.queryCodes.split(',');

      let list = [];
      this.addFormData.checkList.forEach((item) => {
        this.addFormData.cruxConditionList.forEach((value) => {
          if (item === value.queryCode) {
            list.push(value.queryName);
          }
        });
      });
      list = list.join(',');
      this.addFormData.jionCondition = list;

      this.addFormData.attributeRelationship = typeList.templateLinkAttrNames;
      typeList.templateLinkAttrs.split(',').forEach((item) => {
        let a = this.currentRelationAttributeList.filter((value) => {
          return value.attributeCode == item;
        });

        this.currentSelectRelationAttributeList.push(...a);
      });

      this.currentSelectRelationAttributeList.forEach((item) => {
        let index = this.currentRelationAttributeList.indexOf(item);

        this.currentRelationAttributeList.splice(index, 1);
      });

      this.addFormData.projectCoding = typeList.itemType;
      this.addFormData.moreForm = typeList.moreCellFlag;
      this.addFormData.remark = typeList.remark;
      this.addFormData.whetherEdit = typeList.sysTemplate;
    },

    // 点击查看按钮
    see(index, cFlag) {
      this.prohibit = true;
      this.append = true;
      this.newAndEdit = 'edit';
      this.getTemplateattributeList().then((res) => {
        this.assignment(index, cFlag);
      });

      this.$nextTick().then(async () => {
        await this.$refs.addForm.clearValidate();
      });
    },

    /**
     *  分页部分
     * */
    // 每页显示条数
    handleSizeChange(val) {
      this.pageSize = val;
      this.getTemplateTypeList();
    },

    // 当前页
    handleCurrentChange(val) {
      this.pageNum = val;
      this.getTemplateTypeList();
    },
    getTemplateTypeName(val) {
      let templateTypeName
      this.billsNameList.forEach(billsName => {
        if(billsName.templateTypeCode === val) {
          templateTypeName = billsName.templateTypeName
        }
      });
      return templateTypeName
    },
    getSonTemplateTypeName(val) {
      let templateTypeName
      this.addFormData.dataSonDocumentsList.forEach(billsName => {
        if(billsName.templateTypeCode === val) {
          templateTypeName = billsName.templateTypeName
        }
      });
      return templateTypeName
    }
  },
});
