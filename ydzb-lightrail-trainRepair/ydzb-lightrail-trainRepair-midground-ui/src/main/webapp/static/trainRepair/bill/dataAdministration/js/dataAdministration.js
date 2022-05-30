var app = new Vue({
  el: '#app',
  filters: {
    yesAndNo(val) {
      return val == 1 ? '是' : '否';
    },
    toNumberFilter(val) {
      if (val == '0') {
        return '整数';
      } else {
        return val;
      }
    },
    signersFilter(val, signers) {
      let list = [];
      val = val.split(',');
      val.forEach((item) => {
        signers.forEach((value) => {
          if (item === value.id) {
            list.push(value.text);
          }
        });
      });

      return list.join(',');
    },
  },

  data() {
    return {
      // 新增(用户)
      appendFormData: {
        //新增弹窗
        // 属性编码
        attriButeCode: '',
        // 选中属性类型
        newSelectValueType: '',
        // 新增属性名称
        newAttributeName: '',
        // 新增属性说明
        attributeState: '',
        // 属性模式下拉选项
        seletAttributeModel: [],
        // 选中属性模式
        attributeModel: '',
        // 文本取值范围
        textInput: '',
        // 编辑-文本取值范围id
        textInputId: '',
        // 属性模式数字-小数位
        signature: [
          { index: '0', value: '整数' },
          { index: '1', value: '1' },
          { index: '2', value: '2' },
          { index: '3', value: '3' },
        ],
        // 数字取值范围
        numScope: '',
        // 编辑-数字取值范围ID
        numScopeId: '',
        // 下拉取值范围
        selectScope: '',
        // 签字角色数据列表
        signers: [],
        // 签字角色模糊查询
        search: '',
        // 复选框签字取值范围
        checkList: [],
        // 展现/请求的签字取值范围
        signatureScope: '',
        // 按钮取值范围
        btnInput: '',
        // 编辑-按钮取值范围ID
        btnInputId: '',
        // 是否关联属性
        myAttribute: '0',
        // 是否只读
        myRead: '0',
        // 是否必填
        myRequired: '0',
        controlType: '',
      },
      // 新增弹框的验证
      formRules: {
        attriButeCode: [{ required: true, message: '请输入属性编码', trigger: 'blur' }],
        newSelectValueType: [{ required: true, message: '请输入属性类型', trigger: 'blur' }],
        newAttributeName: [
          { required: true, message: '请输入属性名称', trigger: 'blur' },
          { max: 20, message: '属性名称不能超过20个字符', trigger: 'blur' },
        ],
        attributeState: [{ max: 200, message: '属性说明最大支持200个字符', trigger: 'blur' }],
        attributeModel: [{ required: true, message: '请输入属性模式', trigger: 'blur' }],
      },

      // 打开页面获取权限
      jurisdiction: '',

      // 属性类型
      valueType: '',
      // 选中的属性类型
      selectValueType: '',
      // 属性名称
      valueInput: '',
      // 类别
      category: '',
      // 类别列表
      categoryList: [
        { code: 1, name: '系统' },
        { code: 0, name: '自定义' },
        { code: '', name: '全部' }
      ],

      // 签字下拉打开关闭控制
      signatureDown: false,

      // 列表
      valueList: [],
      // 列表信息总条数
      count: 0,
      // 每页显示条数
      pageSize: 20,
      // 当前页
      pageNum: 1,

      // 新增属性模式下拉-编辑按钮
      // 取值范围-输入框
      scope: '',
      // 编辑中的下拉取值范围列表
      editingDropDownList: [],
      // 最终要提交的下拉取值范围列表
      currentDropDownList: [],

      // 新增弹框是否弹出
      serial: false,

      // 判断弹窗是从新增按钮进入还是编辑按钮进入
      newAndEdit: false,
      // 储存编辑时的Id
      editId: '',
      // 新增属性模式下拉-编辑按钮弹出
      valueRangeEdit: false,
      value: '',
      relationAttributeTips:
        '关联属性：关联属性在对表单的行和列进行关联时使用，比如一级修检修记录单中的检查项目和辆序交叉点的单元格。举例：比如我们在配置一级修检修记录单时配置了一个属性叫“受电弓碳滑板高度”，在是否关联属性中选择了“是”以后，系统以后就可以识别这是哪一个辆序的碳滑板高度值。',
    };
  },

  created() {
    this.jurisdiction = this.getUrlKey('type');
    if (this.jurisdiction == 2) {
      this.category = '';
    }
    this.getTemplateattributeList();
    this.attributeCate();
    this.getRoles();
    this.seltemplateattriutemodeLis();
  },

  computed: {
    // 每个值对象的isDefault组成的对象
    editingDropDownListDefaultMap() {
      let this_ = this;
      return this.editingDropDownList.reduce((prev, item) => {
        Object.defineProperty(prev, item.code, {
          get() {
            return item.isDefault || "0";
          },
          set(v) {
            if (v == "1") {
              this_.editingDropDownList.forEach((curItem) => {
                this_.$set(curItem, "isDefault", curItem.code == item.code ? "1" : "0");
              });
            }
          },
          enumerable: true
        });
        return prev;
      }, {});
    },
    pageTitle: GeneralUtil.getObservablePageTitleGetter('单据属性配置'),
    // 取值范围名称
    valueName() {
      let value = this.appendFormData.seletAttributeModel.filter((item) => {
        return item.attributeModeCode === this.appendFormData.attributeModel;
      });

      return value[0] && value[0].note;
    },

    signerList() {
      if (this.appendFormData.search == '') {
        return this.appendFormData.signers;
      } else {
        return this.appendFormData.signers.filter((value) => {
          return value.text.indexOf(this.appendFormData.search) != -1;
        });
      }
    }
  },
  watch: {
    editingDropDownList: {
      deep: true,
      handler(list) {
        if (list.length > 0 && list.every(item => item.isDefault != "1")) {
          list[0].isDefault = "1"
        }
      }
    }
  },

  methods: {
    getFormItemStyleByLabel(label) {
      if (label) {
        return {
          marginLeft: (4 - label.length) + 'rem'
        };
      } else {
        return null;
      }
    },
    // 控制属性模式-签字的选择角色下拉的弹出
    openSignatureDown() {
      this.signatureDown = !this.signatureDown;
      this.appendFormData.search = '';
      this.$nextTick(() => {
        this.$refs.optionScrolltop.scrollTop = 0;
      });
    },
    // 获取url信息
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
    tableRowClassName({ row, rowIndex }) {
      if (rowIndex % 2 == 1) {
        return 'warning-row';
      } else {
        return 'success-row';
      }
    },

    // 按条件查询
    find() {
      this.pageNum = 1;
      this.getTemplateattributeList();
    },

    // 重置
    reset() {
      this.selectValueType = '';
      this.valueInput = '';
      if (this.jurisdiction == 2) {
        this.category = '';
      }
      this.find();
    },

    // 属性类型
    async attributeCate() {
      let {
        data: { data },
      } = await axios({
        method: 'get',
        url: '/apiTrainRepairMidGround/templateAttribute/getTemplateAttributeTypeList',
      });
      this.valueType = data;
    },

    // 数据列表
    async getTemplateattributeList() {
      let sysType;
      if (this.jurisdiction == 1) {
        sysType = 1;
      } else {
        sysType = this.category;
      }
      let {
        data: { data },
      } = await axios({
        method: 'post',
        url: '/apiTrainRepairMidGround/templateAttribute/getTemplateAttributeList',
        data: {
          attributeCode: '',
          attributeTypeCode: this.selectValueType,
          attributeName: this.valueInput,
          pageNum: this.pageNum,
          sysType,
          pageSize: this.pageSize,
        },
      });

      this.valueList = data.templateAttributeList;
      this.count = data.count;
    },

    // 删除数据
    async removeValue(id) {
      let { data } = await axios({
        method: 'get',
        url: '/apiTrainRepairMidGround/templateAttribute/delTemplateAttribute',
        params: {
          id,
        },
      });

      if (data.code == 1) {
        this.$message.success(data.msg);
        this.getTemplateattributeList();
      } else {
        this.$message.error(data.msg);
        this.getTemplateattributeList();
      }
    },

    // 点击删除按钮
    deleteValue(id) {
      this.$confirm('您确定要删除该属性么？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        this.removeValue(id);
      });
    },

    // 点击编辑按钮
    async compile(id) {
      this.serial = true;
      this.newAndEdit = true;
      this.editId = id;
      await this.getTemplateAttributeWithDetail(id);

      this.$nextTick().then(async () => {
        await this.$refs.addForm.clearValidate();
      });
    },

    // 获取属性详情
    async getTemplateAttributeWithDetail(id) {
      let {
        data: { data },
      } = await axios.get('/apiTrainRepairMidGround/templateAttribute/getTemplateAttributeWithDetail', {
        params: {
          id,
        },
      });
      // 获取详情信息进行回填
      this.appendFormData.attriButeCode = data.attributeCode;
      this.appendFormData.newSelectValueType = data.attributeTypeCode;
      this.appendFormData.newAttributeName = data.attributeName;
      this.appendFormData.attributeState = data.attributeNote;
      this.appendFormData.attributeModel = data.attributeModeCode;
      this.appendFormData.myAttribute = data.linkAttr;
      this.appendFormData.myRead = data.readOnly;
      this.appendFormData.myRequired = data.backFillVerify;

      await this.attributeModelStyle();
      let controlType = this.appendFormData.controlType;

      if (controlType == 'Text') {
        this.appendFormData.textInput = data.templateValueList[0].attributeRangeValue;
        this.appendFormData.textInputId = data.templateValueList[0].id;
      } else if (controlType == 'DropDownList') {
        let list = [];
        data.templateValueList.forEach((item) => {
          this.$set(item, 'code', item.id);
          list.push(item.attributeRangeValue);
        });
        this.appendFormData.selectScope = list.join(',');
        this.currentDropDownList = data.templateValueList;
      } else if (controlType == 'NumberDropDownList') {
        this.appendFormData.numScope = data.templateValueList[0].attributeRangeValue;
        this.appendFormData.numScopeId = data.templateValueList[0].id;
      } else if (controlType == 'SignDropDownList') {
        let list = [];
        data.templateValueList.forEach((item) => {
          list.push(item.attributeRangeValue);
        });
        this.selectSigners(list);
        this.appendFormData.checkList = list;
        this.appendFormData.checkListMap = data.templateValueList;
      }
    },

    // 每页显示条数
    handleSizeChange(val) {
      this.pageSize = val;
      this.getTemplateattributeList();
    },

    // 当前页
    handleCurrentChange(val) {
      this.pageNum = val;
      this.getTemplateattributeList();
    },

    // 新增弹框
    newlyAdd() {
      this.serial = true;
      this.newAndEdit = false;
      this.appendFormData.attributeModel = 'TextControl';
      this.attributeModelStyle();

      this.$nextTick().then(async () => {
        await this.$refs.addForm.clearValidate();
      });
    },

    // 获取属性模式
    async seltemplateattriutemodeLis() {
      let {
        data: { data },
      } = await axios({
        method: 'get',
        url: '/apiTrainRepairMidGround/templateAttribute/getTemplateAttributeModeList',
      });
      this.appendFormData.seletAttributeModel = data;
    },

    // 获取签字角色数据
    async getRoles() {
      let {
        data: { data },
      } = await axios({
        method: 'get',
        url: '/apiTrainRepairMidGround/templateAttribute/getRoles',
      });

      this.appendFormData.signers = data;
    },

    attributeModelStyle() {
      this.appendFormData.seletAttributeModel.forEach((item) => {
        if (this.appendFormData.attributeModel == item.attributeModeCode) {
          this.appendFormData.controlType = item.controlType;
        }
      });
    },

    // 打开取值范围弹窗
    openCompile() {
      this.valueRangeEdit = true;
      this.editingDropDownList = [...new Set(this.currentDropDownList)];
    },

    // 取值范围弹窗新增
    newScope() {
      if (this.scope == null || this.scope.length == 0) {
        this.$message.error('取值范围不能为空');
      } else {
        this.editingDropDownList.push({
          attributeCode: '',
          attributeRangeValue: this.scope,
          createTime: null,
          createUserCode: '',
          createUserName: '',
          delTime: null,
          delUserCode: '',
          delUserName: '',
          flag: '',
          iSortId: '',
          id: '',
          isDefault: '0',
          synDate: null,
          synFlag: '',
          code: new Date().valueOf(),
        });

        this.scope = '';
      }
    },

    // 签字人员选择
    selectSigners(val) {
      let list = [];
      val.forEach((item) => {
        this.appendFormData.signers.forEach((value) => {
          if (item === value.id) {
            list.push(value.text);
          }
        });
      });
      list = list.join(',');
      this.appendFormData.signatureScope = list;
    },

    // 取值范围弹窗删除
    removeScope(index) {
      this.editingDropDownList.splice(index, 1);
    },

    // 取值范围-确定
    determine() {
      this.currentDropDownList = [...new Set(this.editingDropDownList)];
      this.appendFormData.selectScope = this.editingDropDownList.map(item => item.attributeRangeValue).join(',');
      this.valueRangeEdit = false;
    },

    onValueRangeEditClosed() {
      this.editingDropDownList = [];
      this.scope = '';
    },
    // 取值范围-取消
    cancel() {
      this.valueRangeEdit = false;
    },

    // 新增确认
    addition(addForm) {
      this.$refs[addForm].validate((valid) => {
        if (valid) {
          this.addTemplateattribute();
          this.serial = false;
        }
      });
    },

    // 清空弹出框列表内信息
    empty() {
      this.appendFormData.attriButeCode = '';
      this.appendFormData.newAttributeName = '';
      this.appendFormData.attributeState = '';
      this.appendFormData.textInput = '';
      this.appendFormData.btnInput = '';
      this.appendFormData.numScope = '';
      this.appendFormData.signatureScope = '';
      this.appendFormData.selectScope = '';
      this.currentDropDownList = [];
      this.appendFormData.newSelectValueType = '';
      this.appendFormData.search = '';
      this.appendFormData.myRequired = '0';
      this.appendFormData.myAttribute = '0';
      this.appendFormData.myRead = '0';
      this.appendFormData.checkList = [];
      this.appendFormData.controlType = '';
    },

    // 关闭弹出框时清空数据
    onClosed() {
      this.empty();
    },

    // 添加/修改动作
    async addTemplateattribute() {
      let templateValueList = [];

      // 根据属性模式判断
      if (this.appendFormData.controlType == 'Text') {
        templateValueList.push({
          attributeCode: '',
          attributeRangeValue: this.appendFormData.textInput,
          createTime: null,
          createUserCode: '',
          createUserName: '',
          delTime: null,
          delUserCode: '',
          delUserName: '',
          flag: '',
          iSortId: '',
          id: '',
          isDefault: '',
          synDate: null,
          synFlag: '',
        });
      } else if (this.appendFormData.controlType == 'NumberDropDownList') {
        templateValueList.push({
          attributeCode: '',
          attributeRangeValue: this.appendFormData.numScope,
          createTime: null,
          createUserCode: '',
          createUserName: '',
          delTime: null,
          delUserCode: '',
          delUserName: '',
          flag: '',
          iSortId: '',
          id: '',
          isDefault: '',
          synDate: null,
          synFlag: '',
        });
      } else if (this.appendFormData.controlType == 'DropDownList') {
        templateValueList = this.currentDropDownList;
      } else if (this.appendFormData.controlType == 'SignDropDownList') {
        templateValueList = [];
        this.appendFormData.checkList.forEach((item) => {
          templateValueList.push({
            attributeCode: '',
            attributeRangeValue: item,
            createTime: null,
            createUserCode: '',
            createUserName: '',
            delTime: null,
            delUserCode: '',
            delUserName: '',
            flag: '',
            iSortId: '',
            id: '',
            isDefault: '',
            synDate: null,
            synFlag: '',
          });
        });
      }
      if (!this.newAndEdit) {
        let res = await axios({
          method: 'post',
          url: '/apiTrainRepairMidGround/templateAttribute/addTemplateAttribute',
          data: {
            attributeCode: this.appendFormData.attriButeCode,
            attributeModeCode: this.appendFormData.attributeModel,
            attributeName: this.appendFormData.newAttributeName,
            attributeNote: this.appendFormData.attributeState,
            templateValueList,
            attributeTypeCode: this.appendFormData.newSelectValueType,
            backFillVerify: this.appendFormData.myRequired,
            linkAttr: this.appendFormData.myAttribute,
            readOnly: this.appendFormData.myRead,
            sysType: this.jurisdiction == 1 ? 1 : 0,
          },
        });
        if (res.data.code == 1) {
          this.$message({
            message: '添加成功',
            type: 'success',
          });
          this.find();
        } else {
          this.$message.error(res.data.msg);
        }
      } else {
        let res = await axios({
          method: 'post',
          url: '/apiTrainRepairMidGround/templateAttribute/updateTemplateAttribute',
          data: {
            id: this.editId,
            attributeCode: this.appendFormData.attriButeCode,
            attributeModeCode: this.appendFormData.attributeModel,
            attributeName: this.appendFormData.newAttributeName,
            attributeNote: this.appendFormData.attributeState,
            templateValueList,
            attributeTypeCode: this.appendFormData.newSelectValueType,
            backFillVerify: this.appendFormData.myRequired,
            linkAttr: this.appendFormData.myAttribute,
            readOnly: this.appendFormData.myRead,
          },
        });
        if (res.data.code == 1) {
          this.$message({
            message: '修改成功',
            type: 'success',
          });
          this.editId = '';
          this.find();
        } else {
          this.$message.error('修改失败');
        }
      }

      this.empty();
    },

    // 关闭新增弹窗
    closePop() {
      this.serial = false;
      this.editId = '';
    },
  },
});
