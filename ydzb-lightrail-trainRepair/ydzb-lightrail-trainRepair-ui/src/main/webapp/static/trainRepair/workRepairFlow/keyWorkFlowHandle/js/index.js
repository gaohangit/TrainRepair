let copyDataSimple = (data) => JSON.parse(JSON.stringify(data));
var valiWorkTeam = (rule, value, callback) => {
  if (Object.keys(value).length == 0) {
    return callback(new Error('请选择打卡班组'));
  } else {
    callback();
  }
};

window.main = new Vue({
  el: '#keyWorkFlowHandle',
  components: {
    'my-form': {
      name: 'my-form',
      template: `
      <el-form
        ref="addKeyWorkForm"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        size="medium"
        class="scroll-form"
        :disabled="!!formData.id"
      >
        <div class="flex-form">
          <el-form-item prop="trainType" label="驳回理由：" v-if="!!formData.id&&!!formData.flowRunRecordInfo">
            <el-input
              v-model.trim="formData.flowRunRecordInfo.remark"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item prop="trainType" label="车型：">
            <el-select
              v-model="formData.trainType"
              filterable
              clearable
              \@change="addFormTrainTypeChange"
            >
              <el-option
                v-for="item in addTrainTypeList"
                :key="item.traintype"
                :value="item.traintype"
                :label="item.traintype"
              ></el-option>
            </el-select>
          </el-form-item>

          <el-form-item prop="trainTemplate" label="批次：">
            <el-select
              v-model="formData.trainTemplate"
              filterable
              clearable
              \@change="addFormTrainTemplateChange"
            >
              <el-option
                v-for="item in addTrainTempList"
                :key="item.traintempid"
                :value="item.traintempid"
                :label="item.traintempid"
              ></el-option>
            </el-select>
          </el-form-item>

          <el-form-item prop="trainsetId" label="车组：">
            <el-select
              v-model="formData.trainsetId"
              filterable
              clearable
              \@change="addFormTrainsetIdChange"
            >
              <el-option
                v-for="item in addTrainsetList"
                :key="item.trainsetid"
                :value="item.trainsetid"
                :label="item.trainsetname"
                :disabled="!item.hasTrack"
              ></el-option>
            </el-select>
          </el-form-item>

          <el-form-item
            v-for="column in columns"
            :key="column.key"
            :label="column.label+'：'"
            :prop="column.property"
          >
            <el-select
              v-if="column.type=='options'"
              v-model="formData[column.property]"
              \@focus="formItemFocus(column)"
              value="value"
              filterable
              :clearable="column.key!='WORK_ENV'"
            >
              <el-option
                v-for="item in columnListMap[column.key+'List']"
                v-show="columnListMap['KEY_WORK_TYPEList'] &&  !item.isDelete ? true : false "
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
            <lp-select
              v-else-if="column.type=='multipleChoice'"
              v-model="formData.carNoList"
              \@change="carNoListChange"
              :list="carList"
              mutiple
              value="value"
              :disabled="!!formData.id"
              flag="CAR"
              \@focus="formItemFocus(column)"
            ></lp-select>
            <template v-else-if="column.type=='specialOptions'">
              <el-cascader
                :placeholder="column.key=='BATCH_BOM_NODE_CODE' ? batchBomNodeCodeTips : '请选择'"
                v-model="formData[column.property]"
                :options="columnListMap[column.key+'List']"
                :props="cascaderProps"
                \@focus="formItemFocus(column)"
                :show-all-levels="false"
                popper-class="funcNodeCodePopper"
                filterable
                clearable
                \@visible-change="cascaderChange"
                :disabled="column.key=='BATCH_BOM_NODE_CODE'&&batchBomDisable"
              >
              </el-cascader>
            </template>
            
            <el-input
              v-else-if="column.property === 'position'"
              :maxlength="50" 
              show-word-limit
              v-model.trim="formData[column.property]"
              clearable
              class="fix-count-out"
            ></el-input>

            <el-input
              v-else
              :maxlength="50" 
              show-word-limit
              v-model="formData[column.property]"
              clearable
              class="fix-count-out"
            ></el-input>
          </el-form-item>
           
          <el-form-item prop="workTeam" label="作业班组：">
            <el-select
              v-model="formData.workTeam"
              filterable
              value-key="teamCode"
            >
              <el-option
                v-for="item in workTeamList"
                :key="item.teamCode"
                :value="item"
                :label="item.teamName"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>

        <el-row>
          <el-form-item prop="content" label="作业内容：">
            <el-autocomplete
              :maxlength="200"
              show-word-limit
              v-model.trim="formData.content"
              placeholder="请输入"
              :fetch-suggestions="querySearch"
              clearable
              \@select="handleSelect"
              class="fix-count-out"
              v-show="formData.trainType ? true : false"
            >
              <template v-slot="{item}">
                {{item.value = item.content}}
              </template>
            </el-autocomplete>
            <el-input
              :maxlength="200"
              v-model.trim="formData.content"
              placeholder="请输入"
              clearable
              class="fix-count-out"
              show-word-limit
              v-show="formData.trainType ? false : true"
              v-popover:popover
            >
              <template v-slot="{item}">
                {{item.value = item.content}}
              </template>
            </el-input>
          </el-form-item>
          <el-popover
            ref="popover"
            placement="bottom"
            width="200"
            trigger="click"
            content="没有符合条件的数据">
          </el-popover>
        </el-row>

        <el-row>
          <el-form-item prop="remark" label="备注：">
            <el-input
              type="textarea"
              :rows="4"
              v-model.trim="formData.remark"
              :maxlength="500"
              show-word-limit
              class="fix-count-out"
            ></el-input>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item
            prop="images"
            label="图片上传:"
            class="picture-title"
            :class="{'disable-upload':!!formData.id}"
          > 
            <div style="position: absolute; right: 0; top: -37px;" v-if="proposeImgNumber != 0">{{fileList.length + ' / ' + proposeImgNumber}}</div>
            <el-upload
              action="#"
              :http-request="uploadFiles"
              multiple
              list-type="picture-card"
              :file-list="fileList"
              :before-upload="beforeAvatarUploadPdf"
              :on-success="fileSuccess"
            >
              <i class="el-icon-plus"></i>
              <template v-slot:file="{file}">
                <img
                  class="el-upload-list__item-thumbnail"
                  :src="file.url"
                  alt=""
                />
                <span class="el-upload-list__item-actions">
                  <span
                    class="el-upload-list__item-preview"
                    \@click="previewPicture(file,fileList)"
                  >
                    <i class="el-icon-zoom-in"></i>
                  </span>
                  <span
                    v-if="!formData.id"
                    class="el-upload-list__item-delete"
                    \@click="removePicture(file)"
                  >
                    <i class="el-icon-delete"></i>
                  </span>
                </span>
              </template>
            </el-upload>
          </el-form-item>
        </el-row>
        <el-row v-if="!formData.id">
          <el-col class="text-c">
            <el-button type="primary" size="small" \@click="setKeyWorkFlowRun"
              >确定</el-button
            >
            <el-button
              type="primary"
              size="small"
              \@click="onCancel"
              >取消</el-button
            >
          </el-col>
        </el-row>
      </el-form>
      `,
      props: {
        proposeImgNumber: {
          type: [String, Number],
          default: 0,
        },
        trainsetList: {
          type: Array,
          default: () => [],
        },
        columns: {
          type: Array,
          default: () => [],
        },
        columnListMap: {
          type: Object,
          default: () => ({}),
        },
        // uploadLimit: {
        //   type: [String, Number],
        //   default: 0,
        // },
        unitCode: {
          type: String,
          default: '',
        },
        workTeamList: {
          type: Array,
          default: () => [],
        },
      },
      data() {
        return {
          formData: {
            trainType: '',
            trainTemplate: '',
            trainsetId: '',
            workTeam: {},
            content: '',
            remark: '',
          },
          formRules: {
            trainType: [],
            trainTemplate: [],
            trainsetId: [
              {
                required: true,
                message: '请选择车组',
                trigger: 'change',
              },
            ],
            workTeam: [
              {
                required: true,
                validator: valiWorkTeam,
                trigger: 'select',
              },
            ],
            content: [
              {
                required: true,
                message: '请输入作业内容',
                trigger: ['change', 'trigger'],
              },
            ],
            remark: [],
          },
          batchBomNodeCodeTips: '',
          fileList: [],
          contentList: [],
          batchBomDisable: false,
          cascaderProps: {
            value: 'code',
            label: 'name',
            checkStrictly: true,
            emitPath: false,
            lazy: true,
          },
          currentUploaded: [],
          uploadedFileInfos: [],
        };
      },
      filters: {},
      computed: {
        addTrainTypeList({ trainsetList }) {
          // 去重+组合
          const map = trainsetList.reduce((prev, item) => {
            if (!prev.has(item.traintype)) {
              prev.set(item.traintype, {
                traintype: item.traintype,
              });
            }
            return prev;
          }, new Map());
          let arr = [...map.values()];

          return arr;
        },
        addTrainTempList({ trainsetList, formData }) {
          // 去重+组合
          const map = trainsetList.reduce((prev, item) => {
            if (!prev.has(item.traintempid)) {
              prev.set(item.traintempid, {
                traintempid: item.traintempid,
                traintype: item.traintype,
              });
            }
            return prev;
          }, new Map());
          let arr = [...map.values()];
          // 过滤条件
          if (formData.trainType) {
            arr = arr.filter((item) => item.traintype == formData.trainType);
          }

          return arr;
        },
        addTrainsetList({ trainsetList, formData }) {
          let list = copyDataSimple(trainsetList)
          let arr = [...list];
          // 过滤条件
          if (formData.trainType) {
            arr = arr.filter((item) => item.traintype == formData.trainType);
          }
          if (formData.trainTemplate) {
            arr = arr.filter((item) => item.traintempid == formData.trainTemplate);
          }

          let hasTrackList = []
          let noneTrackList = []
          arr.forEach((item) => {
            if (!item.hasTrack) {
              item.trainsetname = item.trainsetname + '(未在股道)';
              hasTrackList.push(item)
            } else {
              noneTrackList.push(item)
            }
          });
          let newArr = noneTrackList.concat(hasTrackList)
          return newArr;
        },
        // 一级部件信息
        fstLevelBomCodes({ columnListMap }) {
          return columnListMap.BATCH_BOM_NODE_CODEList.map((item) => item.code);
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

        columnPropertys({ columns }) {
          return columns.map((item) => item.property);
        },
      },
      watch: {},
      methods: {
        async addFormTrainTypeChange(val) {
          this.formData.trainTemplate = '';
          this.formData.trainsetId = '';
          this.formData.carNoList = [];

          this.$emit('column-list-map-change', 'CARList', []);
          this.contentList = [];
          if (val) {
            await this.formItemFocus({ key: 'CAR' });
            // this.formData.carNoList = this.columnListMap.CARList.map((item) => item.value);
            this.formData.carNoList = [];
          }
        },
        async addFormTrainTemplateChange(val) {
          if (val) {
            this.formData.trainType = this.addTrainTempList.find((item) => item.traintempid == val).traintype;
            await this.formItemFocus({ key: 'CAR' });
            if (this.formData.carNoList.length === 0) {
              // this.formData.carNoList = this.columnListMap.CARList.map((item) => item.value);
              this.formData.carNoList = [];
            }
          }

          this.formData.trainsetId = '';
        },
        async addFormTrainsetIdChange(val) {
          if (val) {
            await this.$emit('add-form-work-teams', val);
            this.formData.trainType = this.addTrainsetList.find((item) => item.trainsetid == val).traintype;
            await this.formItemFocus({ key: 'CAR' });
            if (this.formData.carNoList.length === 0) {
              // this.formData.carNoList = this.columnListMap.CARList.map((item) => item.value);
              this.formData.carNoList = [];
            }
            this.formData.trainTemplate = this.addTrainsetList.find((item) => item.trainsetid == val).traintempid;
          }
        },

        async formItemFocus({ key }) {
          let res = [];
          // 辆序
          if (key === 'CAR') {
            if (Array.isArray(this.formData.trainType)) {
              if (this.formData.trainType.length === 0) return;
              let requestList = this.formData.trainType.map((item) =>
                getKeyWorkExtraColumnValueList({
                  columnKey: key,
                  trainModel: item,
                })
              );
              const result = await axios.all(requestList);
              result.forEach((item) => {
                if (res.length === 0) {
                  res = item;
                } else if (res.length > item.length) {
                  res = item;
                }
              });
            } else {
              if (!this.formData.trainType) return;
              res = await getKeyWorkExtraColumnValueList({
                columnKey: key,
                trainModel: this.formData.trainType,
              });
            }
            this.$emit('column-list-map-change', 'CARList', res);
          } else if (key === 'BATCH_BOM_NODE_CODE') {
            if (!this.formData.trainsetId || this.formData.carNoList.length == 0) {
              return this.$message.info('可通过选择车组和辆序获得部件信息');
            }

            res = await getBatchBomNodeCode(this.formData.trainsetId, this.formData.carNoList.toString());
            if (res !== null) {
              this.addListLeaf(res);
            }
            this.$emit('column-list-map-change', `${key}List`, res ? res : []);
          }
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
        carNoListChange(val) {
          if (val.length > 1) {
            this.batchBomDisable = true;
            this.formData.batchBomNodeCode = '';
            this.batchBomNodeCodeTips = '多选辆序暂不支持部件选择'
            this.$refs.addKeyWorkForm.validateField('carNoList');
          } else {
            this.batchBomDisable = false;
            this.batchBomNodeCodeTips = '请选择'
          }
        },
        cascaderChange(val) {
          if (!val) {
            if (this.fstLevelBomCodes.includes(this.formData.batchBomNodeCode)) {
              this.formData.batchBomNodeCode = '';
              this.$message.info('至少选择二级以上的部件信息');
            }
          }
        },
        async querySearch(queryString, cb) {
          if (!this.formData.trainType) return;
          this.getKeyWorkExtraColumnValueListKeyWorkType()
          this.contentList = await getKeyWorkConfigListByTrainModel(this.unitCode, this.formData.trainType);

          let res = queryString
            ? this.contentList.filter((item) => item.content.toLowerCase().includes(queryString.toLowerCase()))
            : this.contentList;
          cb(res);
        },
        handleSelect(row) {
          this.columnPropertys.forEach((property) => {
            if (property == 'carNoList' && row[property].length == 0) {
              let carNoList = copyDataSimple(this.columnListMap.CARList);
              carNoList = carNoList.map((item) => {
                return item.value;
              });
              this.$set(this.formData, property, carNoList);
            } else {
              this.$set(this.formData, property, row[property]);
            }
          });

          if (this.formData.trainType) {
            this.formItemFocus({ key: 'CAR' });
          }
        },
        async getKeyWorkExtraColumnValueListKeyWorkType() {
          const columnKey = 'KEY_WORK_TYPE'
          let list = []
          list = await getKeyWorkExtraColumnValueList({   
            columnKey
          });
          this.$set(this.columnListMap, 'KEY_WORK_TYPEList', list);
        },
        // 判断上传图片格式
        beforeAvatarUploadPdf(file) {
          var testmsg = file.name.substring(file.name.lastIndexOf('.') + 1);
          const extension = testmsg === 'jpg' || testmsg === 'JPG';
          const extension2 = testmsg === 'png' || testmsg === 'PNG';
          const extension3 = testmsg === 'jpeg' || testmsg === 'JPEG';
          const extension4 = testmsg === 'bmp' || testmsg === 'BMP';
          if (!extension && !extension2 && !extension3 && !extension4) {
            this.$message({
              message: '上传文件只能是 JPG、JPEG、BMP、PNG 格式!',
              type: 'warning',
            });
            return false;
          }
          return extension || extension2 || extension3 || extension4;
        },

        async uploadFiles({ file }) {
          this.uploadedFileInfos = [...this.uploadedFileInfos, file];
        },
        onExceed() {
          // this.$message.error(`最大上传图片数量不能超过${this.uploadLimit}张`);
        },
        previewPicture(file, fileList) {
          this.$emit('preview-picture', file, fileList);
        },
        fileListChange(file, fileList) {
          this.fileList = fileList;
        },

        fileSuccess(response, file, fileList) {
          this.fileList = fileList.map((item) => item);
        },

        removePicture(file) {
          this.fileList = this.fileList.filter((item) => item.uid != file.uid);
          this.uploadedFileInfos = this.uploadedFileInfos.filter((item) => item.uid != file.uid);
        },
        async setKeyWorkFlowRun() {
          let loading = this.$loading();
          try {
            // let fileFormData = new FormData();
            // this.uploadedFileInfos.forEach((item) => {
            //   fileFormData.append(item.uid, item);
            // });
            // const res = await uploadedFileInfo(fileFormData);
            await this.$refs.addKeyWorkForm.validate();
            // let currentUploaded = [];
            for (let i = 0; i < this.uploadedFileInfos.length; i++) {
              let file = this.uploadedFileInfos[i];
              if (file instanceof File) {
                // 避免重复上传图片
                if(this.currentUploaded.length == 0) {
                  let fileFormData = new FormData();
                  fileFormData.append(file.uid, file);
                  let res = await uploadedFileInfo(fileFormData);
                  this.currentUploaded.push(...res);
                } else {
                  if(!this.currentUploaded.some((item) => {return item.paramName == file.uid})) {
                    let fileFormData = new FormData();
                    fileFormData.append(file.uid, file);
                    let res = await uploadedFileInfo(fileFormData);
                    this.currentUploaded.push(...res);
                  }
                }
              } else {
                this.currentUploaded.push(file);
              }
              // if (!this.uploadedFileInfos[i + 1]) {
              //   this.uploadedFileInfos = [];
              // }
            }

            const {
              data: { dayPlanId },
            } = await getDay();
            // const { teamCode, teamName } = await getWorkTeam();
            // 全列传空
            let carNoList = [];
            if (this.columnListMap.CARList.length === this.formData.carNoList.length) {
              carNoList = [];
            } else {
              carNoList = this.formData.carNoList;
            }

            const KeyWorkParam = JSON.stringify({
              batchBomNodeCode: this.formData.batchBomNodeCode,
              content: this.formData.content,
              functionClass: this.formData.functionClass,
              images: this.formData.images,
              keyWorkType: this.formData.keyWorkType,
              position: this.formData.position,
              remark: this.formData.remark,
              trainTemplate: this.formData.trainTemplate,
              trainType: this.formData.trainType,
              trainsetId: this.formData.trainsetId,
              workEnv: this.formData.workEnv,
              carNoList,
              teamCode: this.formData.workTeam.teamCode,
              teamName: this.formData.workTeam.teamName,
              dayPlanId,
              uploadedFileInfos: this.currentUploaded,
            });
            let formData = new FormData();
            formData.append('KeyWorkParam', KeyWorkParam);

            if (this.fileList.length < this.proposeImgNumber) {
              loading.close()
              this.$confirm(`上传图片数量小于建议上传图片数量${this.proposeImgNumber}张, 是否继续？`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                closeOnClickModal: false,
                closeOnPressEscape: false,
              }).then(async () => {
                await setKeyWorkFlowRun(formData);
                this.$message.success('录入成功');

                this.$emit('on-success');
                this.formData.workTeam = {};
              });
            } else {
              await setKeyWorkFlowRun(formData);
              this.$message.success('录入成功');

              this.$emit('on-success');
              this.formData.workTeam = {};
            }
          } catch (error) {
            loading.close()
            console.log(error);
          }
        },
        initData() {
          this.$set(this.columnListMap, 'BATCH_BOM_NODE_CODEList', []);
          this.$set(this.columnListMap, 'CARList', []);

          // 初始化表单额外列
          this.columns.forEach(async (column) => {
            if (column.type == 'multipleChoice') {
              this.$set(this.formData, column.property, []);
            } else {
              this.$set(this.formData, column.property, '');
            }
            if (column.required) {
              this.$set(this.formRules, column.property, [
                {
                  required: true,
                  message: '请选择辆序',
                  trigger: ['change', 'blur'],
                },
              ]);
            } else {
              this.$set(this.formRules, column.property, []);
            }
          });
        },
        resetData() {
          this.$refs.addKeyWorkForm.resetFields();

          this.formData = {
            trainType: '',
            trainTemplate: '',
            trainsetId: '',
            content: '',
            remark: '',
          };

          this.$set(this.columnListMap, 'CARList', []);

          this.contentList = [];
          this.fileList = [];
          this.uploadedFileInfos = [];
          this.currentUploaded = [];
        },
        async setValue(row) {
          // 初始化表单额外列
          this.columns.forEach(async (column) => {
            if (column.type == 'multipleChoice') {
              this.$set(this.formData, column.property, []);
            } else {
              this.$set(this.formData, column.property, '');
            }
            if (column.required) {
              this.$set(this.formRules, column.property, [
                {
                  required: true,
                  message: '请选择辆序',
                  trigger: ['change', 'blur'],
                },
              ]);
            } else {
              this.$set(this.formRules, column.property, []);
            }
          });

          this.formData = copyDataSimple(row);
          await this.addFormTrainsetIdChange(this.formData.trainsetId);
          if (this.formData.carNoList.length === 0) {
            this.formData.carNoList = this.columnListMap.CARList.map((item) => item.value);
          }

          this.$set(this.formData, 'workTeam', {
            teamName: row.teamName,
            teamCode: row.teamCode,
          });
          // this.formData = {
          //   ...row,
          //   workTeam: {
          //     teamName: row.teamName,
          //     teamCode: row.teamCode
          //   },
          // };
          this.uploadedFileInfos = row.uploadedFilePathInfos;
          this.fileList = row.uploadedFilePathInfos
            .map((item) => {
              let url = item.relativeUrl.split('/');
              return {
                name: url[url.length - 1],
                url: `/storageTrainRepair/${item.relativeUrl}`,
              };
            })
            .flat();
          await this.formItemFocus({ key: 'BATCH_BOM_NODE_CODE' });
        },

        setWorkTeam(val) {
          this.formData.workTeam = val[0];
        },

        onCancel() {
          this.$emit('on-cancel');
          this.formData.workTeam = {};
        },
      },
      created() {
        this.initData();
      },
    },
  },
  data: {
    unitCode: '',
    dayPlanId: '',
    // uploadLimit: '',
    proposeImgNumber: '', // 推建图片上传数量
    flowState: Object.freeze({
      '-1': '未开始',
      0: '正在进行',
      1: '已完成',
      2: '已驳回',
      3: '已撤回',
    }),
    stateList: Object.freeze([
      { id: 1, label: '正在进行', value: 0 },
      { id: 2, label: '已完成', value: 1 },
      { id: 3, label: '已驳回', value: 2 },
      { id: 4, label: '已撤回', value: 3 },
    ]),
    pickerOptions: {
      disabledDate(time) {
        let timeOptionRange = main.timeOptionRange;
        let secondNum = 60 * 60 * 24 * 30 * 1000;
        if (timeOptionRange) {
          return (
            time.getTime() > timeOptionRange.getTime() + secondNum ||
            time.getTime() < timeOptionRange.getTime() - secondNum
          );
        }
      },
      onPick({ minDate, maxDate }) {
        if (minDate && !maxDate) {
          main.timeOptionRange = minDate;
        }
        if (maxDate) {
          main.timeOptionRange = '';
        }
      },
    },
    timeOptionRange: '',
    baseImgPath: ctxPath + '/static/trainRepair/workRepairFlow/keyWorkFlowHandle/img/',
    allTrainsetList: [],
    trainsetList: [],
    planlessKeyList: [],
    headerFormData: {
      trainType: '',
      trainTemplate: '',
      trainsetId: '',
      time: [],
      flowName: '',
      flowRunState: 0,
      content: '',
      expireFlowShow: false,
    },
    headerFormRules: {
      trainType: [],
      trainTemplate: [],
      trainsetId: [],
      time: [
        {
          required: true,
          message: '请选择时间范围',
          trigger: 'change',
        },
      ],
      flowName: [],
      flowRunState: [],
      content: [],
    },
    nowFlow: {},
    contentList: [],

    specialColumnKeys: Object.freeze(['FUNCTION_CLASS', 'BATCH_BOM_NODE_CODE']),
    columns: [],
    columnListMap: {},

    mainTable: {
      data: [],
      pageNum: 1,
      pageSize: 20,
      total: 20,
    },
    addKeyWorkDialog: {
      visible: false,
      formData: {
        trainType: '',
        trainTemplate: '',
        trainsetId: '',
        content: '',
        remark: '',
      },
      formRules: {
        trainType: [],
        trainTemplate: [],
        trainsetId: [
          {
            required: true,
            message: '请选择车组',
            trigger: 'change',
          },
        ],
        content: [
          {
            required: true,
            message: '请输入作业内容',
            trigger: ['change', 'trigger'],
          },
        ],
        remark: [],
      },
      fileList: [],
    },
    handleNodeDialog: {
      visible: false,
      formData: {
        type: '',
      },
      formRules: {
        type: [],
      },
      beforeFileList: [
        {
          name: 'db.png',
          url: `${ctxPath}/static/trainRepair/workRepairFlow/keyWorkFlowHandle/img/db.png`,
        },
        {
          name: 'hand.png',
          url: `${ctxPath}/static/trainRepair/workRepairFlow/keyWorkFlowHandle/img/hand.png`,
        },
      ],
      fileList: [],
      nodeRecords: [],
    },
    node: {},
    flowRun: {},
    userInfo: {},
    roleList: [],

    // 选择作业班组部分
    workTeamDialog: {
      visible: false,
      selectWorkTeamlist: [],
      workTeamFormData: {
        selectWorkTeam: {},
      },
      workTeamFormRules: {
        selectWorkTeam: [
          {
            validator: valiWorkTeam,
            trigger: 'select',
          },
        ],
      },
    },

    roleDialog: {
      visible: false,
      selectRolelist: [],
      roleFormData: {
        roleId: '',
      },
      roleFormRules: {
        roleId: [
          {
            required: true,
            message: '请选择打卡角色',
            trigger: 'select',
          },
        ],
      },
    },

    lookupNodeDealInfoDialog: {
      visible: false,
      flowData: {
        nodes: [],
        nodeVectors: [],
        parallelFlowConfigs: [],
        subflowInfoList: [],
        pairNodeInfo: [],
      },
    },

    nodeDealDetailDialog: {
      visible: false,
      formData: {
        remark: '',
      },
    },

    viewPictureDialog: {
      visible: false,
      detailInfo: {
        title: '',
        pics: [],
        prePics: [],
      },
    },

    // 驳回弹窗
    overruleFlowRun: {
      visible: false,
      formData: {
        remark: '',
      },
      formRules: {
        remark: { required: true, message: '请输入驳回理由', trigger: 'blur' },
      },
    },
    workerPicMap: {},
    workerDetail: {},
    batchBomDisable: false,
    imgUrl: '',
    prePics: [],
    addFormWorkTeams: [],
    oriFileUrl: {},
    uploadedFileInfos: [],
    currentUploaded: [],
  },
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('关键作业管理'),
    trainsetIdNameMap({ allTrainsetList }) {
      return allTrainsetList.reduce((prev, item) => {
        prev[item.trainsetid] = item.trainsetname;
        return prev;
      }, {});
    },

    queryTrainTypeList({ allTrainsetList }) {
      // 去重+组合
      const map = allTrainsetList.reduce((prev, item) => {
        if (!prev.has(item.traintype)) {
          prev.set(item.traintype, {
            traintype: item.traintype,
          });
        }
        return prev;
      }, new Map());
      let arr = [...map.values()];

      return arr;
    },
    queryTrainTempList({ allTrainsetList, headerFormData }) {
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
      if (headerFormData.trainType) {
        arr = arr.filter((item) => item.traintype == headerFormData.trainType);
      }

      return arr;
    },
    queryTrainsetList({ allTrainsetList, headerFormData }) {
      let arr = [...allTrainsetList];
      // 过滤条件
      if (headerFormData.trainType) {
        arr = arr.filter((item) => item.traintype == headerFormData.trainType);
      }
      if (headerFormData.trainTemplate) {
        arr = arr.filter((item) => item.traintempid == headerFormData.trainTemplate);
      }
      return arr;
    },
    // 需要请求部件的车组
    nowPageTrainsetIds({ mainTable }) {
      return [...new Set(mainTable.data.map((item) => item.trainsetId))];
    },
    idInfoMap({ mainTable }) {
      return mainTable.data.reduce((prev, item) => {
        prev[item.id] = item;
        return prev;
      }, {});
    },
    flowIdTableDataMap({ mainTable }) {
      return mainTable.data.reduce((prev, item) => {
        if (!prev[item.flowId]) {
          prev[item.flowId] = [];
        }
        prev[item.flowId].push(item);
        return prev;
      }, {});
    },
    flowIdNameMap({ mainTable }) {
      return mainTable.data.reduce((prev, item) => {
        prev[item.flowId] = item.flowName;
        return prev;
      }, {});
    },
    sortTableData({ flowIdTableDataMap }) {
      return Object.values(flowIdTableDataMap).flat();
    },
    groupSortTableData({ sortTableData, mainTable }) {
      let obj = {};
      let pageNum = 1;
      for (let i = 0; i < sortTableData.length; i += mainTable.pageSize) {
        obj[pageNum] = sortTableData.slice(i, i + mainTable.pageSize);
        pageNum++;
      }
      return obj;
    },
    currentPageGroupSortTableData({ groupSortTableData, mainTable }) {
      return groupSortTableData[mainTable.pageNum] || [];
    },
    flowIdNodeWithRecordsMap({ currentPageGroupSortTableData }) {
      return currentPageGroupSortTableData.reduce((prev, item) => {
        if (!prev[item.flowId]) {
          prev[item.flowId] = [];
        }
        prev[item.flowId].push(item.nodeWithRecords);
        return prev;
      }, {});
    },
    flowIdCurrentPageGroupSortTableDataMap({ currentPageGroupSortTableData }) {
      return currentPageGroupSortTableData.reduce((prev, item) => {
        if (!prev[item.flowId]) {
          prev[item.flowId] = [];
        }
        prev[item.flowId].push(item);
        return prev;
      }, {});
    },

    roleCodeInfoMap({ roleList }) {
      return roleList.reduce((prev, item) => {
        prev[item.code] = {
          roleId: item.code,
          roleName: item.name,
          type: item.type,
        };
        return prev;
      }, {});
    },
    roleCodeNameMap({ roleList }) {
      return roleList.reduce((prev, item) => {
        prev[item.code] = item.name;
        return prev;
      }, {});
    },

    nodeColumns({ nowFlow, roleCodeNameMap }) {
      if (!nowFlow.nodeWithRecords) return [];
      let roleIds = [
        ...new Set(
          nowFlow.nodeWithRecords
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
    },
    nodeDealData({ nowFlow }) {
      if (!nowFlow.nodeWithRecords) return [];
      return nowFlow.nodeWithRecords.map((node) => {
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
          // let obj2 = {};
          // for (const key in obj1) {
          //   const element = obj1[key];
          //   if (element.length > 0) {
          //     obj2[key] = element;
          //   }
          // }
          // prev[item.roleId] = obj2;
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
    },
    workerIdNameMap({ mainTable }) {
      return mainTable.data.reduce((prev, item) => {
        item.nodeWithRecords.forEach((node) => {
          node.nodeRecords.forEach((record) => {
            prev[record.workerId] = record.workerName;
          });
        });
        return prev;
      }, {});
    },
  },
  watch: {
    nowPageTrainsetIds: {
      async handler(trainsetIds) {
        try {
          for (const id of trainsetIds) {
            if (!this.columnListMap[`${id}BATCH_BOM_NODE_CODEFlatList`]) {
              let list = await getBatchBomNodeCode(id);
              if (list === null) continue;
              this.$set(this.columnListMap, `${id}BATCH_BOM_NODE_CODEFlatList`, this.tranListToFlatData(list));
            }
          }
        } catch (error) {
        } finally {
        }
      },
    },
  },
  async created() {
    await this.getUnitCode();
    await this.getDay();
    await this.getUser();
    await this.getConfig();
    this.getPostRoleList();
    this.headerFormData.time = [dayjs().subtract(30, 'day').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')];

    this.getKeyWorkExtraColumnList();
    this.getTrainsetList();
    this.getTrainsetListReceived();

    this.getFlowTypes();

    // this.getPictureUploadMax();
    this.$nextTick().then(() => {
      this.getKeyWorkFlowRunList();
    });
  },
  mounted() {
    this.getKeyWorkFlowRunList();
  },
  methods: {
    // 关键作业录入切换车组下拉列表
    async getAddFormWorkTeams(trainsetId) {
      let res = await this.getActuallyWorkTeams(trainsetId);
      this.addFormWorkTeams = res;
      if (res.length == 1) {
        this.$refs.myForm.setWorkTeam(res);
      }
    },

    // 获取当前登录人运用所编码
    async getUnitCode() {
      let data = await getUnitCode();
      this.unitCode = data;
    },

    // 获取当前登录人所在班组
    async getActuallyWorkTeams(trainsetIds) {
      return await getActuallyWorkTeams({
        unitCode: this.unitCode,
        dayPlanId: this.dayPlanId,
        trainsetIds,
        staffId: this.userInfo.workerId,
      });
    },

    getNodeCouldDispose(row, id) {
      let record = row.nodeWithRecords.find((record) => record.id == id);
      return record.couldDisposeRoleIds.length > 0 ? true : false;
    },
    getNodeFinished(row, id) {
      let record = row.nodeWithRecords.find((record) => record.id == id);
      return record.finished;
    },
    // 获取日计划编号
    async getDay() {
      const res = await axios({
        url: '/apiTrainRepair/common/getDay',
        params: {
          unitCode: this.unitCode,
        },
      });

      this.dayPlanId = res.data.dayPlanId;
    },

    async getUser() {
      const {
        data: {
          data: { staffId, name, workTeam },
        },
      } = await getUser();
      this.userInfo = {
        workerId: staffId,
        workerName: name,
        teamCode: workTeam && workTeam.teamCode ? workTeam.teamCode : '',
        teamName: workTeam && workTeam.teamName ? workTeam.teamName : '',
      };
    },
    // async getTaskPostList(trainsetId) {
    //   const userRoleList = await getTaskPostList(this.unitCode, this.dayPlanId, this.userInfo.workerId, trainsetId);
    //   this.userInfo.roles = userRoleList.map((item) => item.roleId);
    // },
    async getPostRoleList() {
      this.roleList = await getPostRoleList();
    },
    async getFlowTypes() {
      this.headerFormData.flowName = '';
      this.planlessKeyList = await getFlowTypes({
        unitCode: this.unitCode,
        queryPastFlow: this.headerFormData.expireFlowShow,
        flowTypeCode: 'PLANLESS_KEY',
        flowPageCode: 'PLANLESS_KEY',
      });
    },
    async getTrainsetList() {
      const data = await getTrainsetList();
      this.allTrainsetList = data;
    },
    async getTrainsetListReceived() {
      this.trainsetList = await getTrainsetListByTrack({ unitCode: this.unitCode });
    },
    headerFormTrainTypeChange() {
      this.headerFormData.trainTemplate = '';
      this.headerFormData.trainsetId = '';
    },
    headerFormTrainTemplateChange(val) {
      if (val) {
        this.headerFormData.trainType = this.queryTrainTempList.find((item) => item.traintempid == val).traintype;
      }
      this.headerFormData.trainsetId = '';
    },
    headerFormTrainsetIdChange(val) {
      if (val) {
        this.headerFormData.trainType = this.queryTrainsetList.find((item) => item.trainsetid == val).traintype;
        this.headerFormData.trainTemplate = this.queryTrainsetList.find((item) => item.trainsetid == val).traintempid;
      }
    },
    async getKeyWorkFlowRunList() {
      let loading = this.$loading();
      try {
        await this.$refs.headerForm.validate();
        const {
          trainType,
          trainTemplate,
          trainsetId,
          flowName,
          flowRunState,
          content,
          expireFlowShow,
        } = this.headerFormData;
        this.mainTable.data = await getKeyWorkFlowRunList({
          checkPower: hasPermission ? false : true,
          queryPastFlow: expireFlowShow,
          trainType,
          trainTemplate,
          trainsetId,
          flowName,
          startTime: Array.isArray(this.headerFormData.time) ? `${this.headerFormData.time[0]} 00:00:00` || '' : '',
          endTime: Array.isArray(this.headerFormData.time) ? `${this.headerFormData.time[1]} 23:59:59` || '' : '',
          unitCode: this.unitCode,
          flowRunState,
          content,
        });
        this.mainTable.total = this.mainTable.data.length;
        loading.close();
      } catch (error) {
        loading.close();
        console.log(error);
      }
    },
    resetHeaderForm() {
      this.$refs.headerForm.resetFields();
      this.headerFormData.time = [dayjs().subtract(30, 'day').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')];
      this.getKeyWorkFlowRunList();
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

    mainTablePageSizeChange(pageSize) {
      this.mainTable.pageSize = pageSize;
    },
    mainTablePageNumChange(pageNum) {
      this.mainTable.pageNum = pageNum;
    },

    openAddKeyWorkDialog() {
      this.$refs.myForm && this.$refs.myForm.initData();
      this.addKeyWorkDialog.visible = true;
      this.$nextTick(() => {
        this.getAddFormWorkTeams();
      });
    },

    async getPictureUploadMax() {
      // const { paramValue } = await getPictureUploadMax();
      // this.uploadLimit = Number(paramValue);
    },
    onExceed() {
      // this.$message.error(`最大上传图片数量不能超过${this.uploadLimit}张`);
    },
    previewPicture(file, fileList) {
      this.imgUrl = file.url;
      this.prePics = fileList.map((item) => item.url);
      this.$refs.myImg.showViewer = true;
    },

    // 判断上传图片格式
    beforeAvatarUploadPdf(file) {
      var testmsg = file.name.substring(file.name.lastIndexOf('.') + 1);
      const extension = testmsg === 'jpg' || testmsg === 'JPG';
      const extension2 = testmsg === 'png' || testmsg === 'PNG';
      const extension3 = testmsg === 'jpeg' || testmsg === 'JPEG';
      const extension4 = testmsg === 'bmp' || testmsg === 'BMP';
      if (!extension && !extension2 && !extension3 && !extension4) {
        this.$message({
          message: '上传文件只能是 JPG、JPEG、BMP、PNG 格式!',
          type: 'warning',
        });
        return false;
      }
      return extension || extension2 || extension3 || extension4;
    },

    async uploadFiles({ file }) {
      this.uploadedFileInfos = [...this.uploadedFileInfos, file];
    },

    handleRemove(file) {
      this.handleNodeDialog.fileList = this.handleNodeDialog.fileList.filter((item) => item.uid != file.uid);
      this.uploadedFileInfos = this.uploadedFileInfos.filter((item) => item.uid != file.uid);
    },

    onSuccess() {
      this.addKeyWorkDialog.visible = false;
      this.getKeyWorkFlowRunList();
    },
    onCancel() {
      this.addKeyWorkDialog.visible = false;
    },
    addKeyWorkDialogClose() {
      this.$refs.myForm.resetData();
      this.addKeyWorkDialog.visible = false;
    },

    columnListMapChange(key, val) {
      this.$set(this.columnListMap, key, val);
    },

    async openHandleNodeDialog(row, column) {
      // await this.getTaskPostList(row.trainsetId);
      this.columnListMap.BATCH_BOM_NODE_CODEList = [];
      this.node = copyDataSimple(column);
      this.node.overruleShow = this.node.couldDisposeRoleIds.length > 0 ? true : false;
      this.flowRun = row;
      const nodeName = column.name;
      const remark = column.remark || '无';
      this.handleNodeDialog.formData = copyDataSimple({
        ...row,
        nodeName,
        remark,
      });

      this.handleNodeDialog.beforeFileList = row.uploadedFilePathInfos
        .map((item) => {
          let url = item.relativeUrl.split('/');
          return {
            name: url[url.length - 1],
            url: `/storageTrainRepair/${item.relativeUrl}`,
          };
        })
        .flat();

      this.handleNodeDialog.visible = true;
      this.handleNodeDialog.fileList = [];
      this.handleNodeDialog.nodeRecords = [];
      this.roleDialog.roleFormData.roleId = '';

      this.roleDialog.selectRolelist = this.node.couldDisposeRoleIds.map(roleId => this.roleCodeInfoMap[roleId]);

      if (this.roleDialog.selectRolelist.length > 1) {
        this.roleDialog.visible = true
        this.$nextTick(() => {
          let isOk = false
          this.$refs.roleDialogRef.$once("close", () => {
            if (!isOk) {
              this.$refs.roleFormRef.resetFields();
              this.filterNodeRecords();
              this.roleDialog.visible = false;
            }
          })

          let onConfirm = async () => {
            try {
              await this.$refs.roleFormRef.validate();
              this.filterNodeRecords();
              isOk = true
              this.roleDialog.visible = false
            } catch(e){}
          }
          this.$refs.roleDialogConfirmBtnRef.$on("click", onConfirm)
          let unwatch = this.$watch("roleDialog.visible", (value) => {
            if(!value){
              this.$refs.roleDialogConfirmBtnRef.$off("click", onConfirm)
              unwatch()
            }
          })
        })
      } else {
        this.filterNodeRecords();
      }
    },

    filterNodeRecords() {
      let roleId;
      if (this.roleDialog.selectRolelist.length === 1) {
        this.roleDialog.roleFormData.roleId = this.roleDialog.selectRolelist[0].roleId;
        roleId = this.roleDialog.selectRolelist[0].roleId;
      } else {
        roleId = this.roleDialog.roleFormData.roleId;
      }

      let selfNodeRecords = this.node.nodeRecords.filter((node) => {
        return node.workerId == this.userInfo.workerId && roleId == node.roleId;
      });

      this.handleNodeDialog.fileList = selfNodeRecords
        .map((record) => {
          return record.pictureUrls.map((item) => {
            let url = item.relativeUrl.split('/');
            return {
              indexOfList: item.indexOfList,
              isOfList: item.isOfList,
              oldName: item.oldName,
              paramName: item.paramName,
              payload: item.payload,
              name: url[url.length - 1],
              relativePath: item.relativePath,
              url: `/storageTrainRepair/${item.relativeUrl}`,
            };
          });
        })
        .flat();

      this.oriFileUrl = copyDataSimple(this.handleNodeDialog.fileList);

      this.handleNodeDialog.nodeRecords = this.node.nodeRecords.filter((node) => {
        return node.workerId != this.userInfo.workerId || roleId != node.roleId;
      });
    },

    seeNodeRecordPhoto(row) {
      this.viewPictureDialog.visible = true;
      this.viewPictureDialog.detailInfo.title = `${row.workerName} 上传图片：`;
      this.viewPictureDialog.detailInfo.prePics = row.pictureUrls.map((item) => {
        return `/storageTrainRepair/${item.relativeUrl}`;
      });
      this.viewPictureDialog.detailInfo.pics = row.pictureUrls.map((item) => {
        let url = item.relativeUrl.split('/');
        return {
          name: url[url.length - 1],
          url: `/storageTrainRepair/${item.relativeUrl}`,
        };
      });
    },

    handleNodeDialogClose() {
      this.handleNodeDialog.fileList = [];
      this.uploadedFileInfos = [];
    },
    handleFileListChange(file, fileList) {
      this.handleNodeDialog.fileList = fileList;
    },

    fileSuccess(response, file, fileList) {
      this.handleNodeDialog.fileList = fileList;
    },

    async dealNodeBtn() {
      // if (!this.node.couldDispose) {
      //   await this.confirmDealNode();
      //   return;
      // }

      if (this.handleNodeDialog.fileList.length < Number(this.node.recommendedPicNum)) {
        const res = await this.$confirm('上传图像实际数量小于节点信息的建议上传图片数量', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          closeOnClickModal: false,
          closeOnPressEscape: false,
          type: 'warning',
        }).catch(() => {
          this.$message.info('已取消处理');
        });
        if (res != 'confirm') return;
      }

      let res = await this.getActuallyWorkTeams(this.flowRun.trainsetId);
      this.workTeamDialog.selectWorkTeamlist = res;

      if (this.workTeamDialog.selectWorkTeamlist.length !== 1) {
        this.workTeamDialog.workTeamFormData.selectWorkTeam = {};
        return (this.workTeamDialog.visible = true);
      } else {
        this.workTeamDialog.workTeamFormData.selectWorkTeam =
          this.workTeamDialog && this.workTeamDialog.selectWorkTeamlist[0];
        this.confirmDealNode();
      }
    },

    confirmWorkTeam() {
      this.$refs.workTeamFormRef.validate((valid) => {
        if (valid) {
          this.workTeamDialog.visible = false;
          this.confirmDealNode();
        }
      });
    },

    workTeamDialogClose() {
      // this.$refs.workTeamFormRef.resetFields();
      this.workTeamDialog.visible = false;
    },

    async confirmDealNode(skipFlag = false, confirmIgnoreMinIntervalRestrict = false, confirmCoverNodeRecord = false) {
      let roleInfo = {};
      if (this.roleDialog.selectRolelist.length == 1) {
        roleInfo = this.roleDialog.selectRolelist[0];
      } else {
        roleInfo = this.roleCodeInfoMap[this.roleDialog.roleFormData.roleId] || {};
      }

      for (let i = 0; i < this.uploadedFileInfos.length; i++) {
        let fileFormData = new FormData();
        let file = this.uploadedFileInfos[i];
        fileFormData.append(file.uid, file);
        let res = await uploadedFileInfo(fileFormData);
        this.currentUploaded.push(...res);
        if (!this.uploadedFileInfos[i + 1]) {
          this.uploadedFileInfos = [];
        }
      }

      let oldFileUrl = this.oriFileUrl
        .filter((file) => {
          return this.handleNodeDialog.fileList.some((item) => {
            return item.url == file.url;
          });
        })
        .map((v) => {
          return {
            indexOfList: v.indexOfList,
            isOfList: v.isOfList,
            oldName: v.oldName,
            paramName: v.paramName,
            payload: v.payload,
            relativePath: v.relativePath,
            relativeUrl: v.url.split('/').slice(2, v.url.split('/').length).join('/'),
          };
        });

      let nodeDisposeJson = {
        workerId: this.userInfo.workerId,
        workerName: this.userInfo.workerName,
        teamCode: this.workTeamDialog.workTeamFormData.selectWorkTeam.teamCode,
        teamName: this.workTeamDialog.workTeamFormData.selectWorkTeam.teamName,
        flowId: this.flowRun.flowId,
        roleInfo,
        nodeId: this.node.id,
        skipFlag,
        confirmIgnoreMinIntervalRestrict,
        confirmCoverNodeRecord,
        uploadedFileInfos: [...oldFileUrl, ...this.currentUploaded],
      };
      if (this.flowRun.id) {
        nodeDisposeJson = {
          ...nodeDisposeJson,
          flowRunId: this.flowRun.id,
        };
      } else {
        nodeDisposeJson = {
          ...nodeDisposeJson,
          flowRun: this.flowRun,
        };
      }
      const nodeDisposeJsonStr = JSON.stringify(nodeDisposeJson);
      let formData = new FormData();
      formData.append('nodeDisposeJsonStr', nodeDisposeJsonStr);
      let res;
      if (hasPermission) {
        res = await setNodeDisposeNotCheck(formData);
      } else {
        res = await setNodeDispose(formData);
      }

      if (res.data.code == 1) {
        if (
          res.data.data.needConfirmSkippedNodes ||
          res.data.data.needConfirmIgnoreMinIntervalRestrict ||
          res.data.data.needConfirmCoverNodeRecord
        ) {
          let confirm = (message) => {
            return this.$confirm(message, '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              closeOnClickModal: false,
              closeOnPressEscape: false,
              type: 'warning',
            });
          };
          let confirmAllInfo = async (res) => {
            let confirms = [];
            let confirmResult = {
              confirmedSkippedNodes: false,
              confirmedIgnoreMinIntervalRestrict: false,
              confirmedCoverNodeRecord: false,
            };

            if (res.needConfirmSkippedNodes) {
              let nodeStr = res.needSkippedNodes.map((node) => node.name).toString();
              confirms.push(() =>
                confirm(`当前流程将跳过 ${nodeStr} 节点，是否继续进行刷卡？`).then((v) => {
                  return (confirmResult.confirmedSkippedNodes = v === 'confirm');
                })
              );
            }

            if (res.needConfirmIgnoreMinIntervalRestrict) {
              confirms.push(() =>
                confirm(res.confirmIgnoreMinIntervalRestrictMessage).then((v) => {
                  return (confirmResult.confirmedIgnoreMinIntervalRestrict = v === 'confirm');
                })
              );
            }

            if (res.needConfirmCoverNodeRecord) {
              confirms.push(() =>
                confirm('是否覆盖上次打卡记录!').then((v) => {
                  return (confirmResult.confirmedCoverNodeRecord = v === 'confirm');
                })
              );
            }

            for (let i = 0; i < confirms.length; i++) {
              let rs = await confirms[i]();
              if (!rs) {
                throw '取消刷卡';
              }
            }
            return confirmResult;
          };

          return confirmAllInfo(res.data.data)
            .then((confirmResult) => {
              return this.confirmDealNode(
                confirmResult.confirmedSkippedNodes,
                confirmResult.confirmedIgnoreMinIntervalRestrict,
                confirmResult.confirmedCoverNodeRecord
              );
            })
            .catch(() => {
              this.$message.info('已取消处理');
            });
        } else {
          if (res.data.data.disposeAfterSkip) {
            this.$message.success('补卡成功');
          } else {
            this.$message.success('打卡成功');
          }
          this.currentUploaded = [];
          this.roleDialog.visible = false;
          this.handleNodeDialog.visible = false;
          return this.getKeyWorkFlowRunList();
        }
      } else {
        let messageList = res.data.msg.split('!');
        if (messageList.length > 1) {
          messageList.splice(messageList.length - 1, messageList.length);
        }
        let list = [];
        messageList.forEach((item) => {
          let reg = new RegExp('^[0-9]*$');
          let arrItem = item.split('');
          for (let i = 0; i < arrItem.length; i++) {
            if (reg.test(arrItem[i])) {
              arrItem[i] = `<span style="font-size: 16px;"> ${arrItem[i]} </span>`;
            }
          }
          let str = arrItem.join('');
          list.push(`<div>${str}!</div>`);
        });
        let message = list.join('');
        this.$message({
          dangerouslyUseHTMLString: true,
          message,
          type: 'warning',
          duration: '3000',
        });
      }
    },
    // 去除过期岗位信息
    postFilter(data) {
      let nodeList = data || [];
      nodeList.forEach((node) => {
        node.roleConfigs = node.roleConfigs.filter((role) => this.roleCodeNameMap[role.roleId]);
      });
    },
    async openRowDetailDialog(row) {
      this.nowFlow = JSON.parse(JSON.stringify(row));
      if (this.nowFlow.flowRunRecordInfo) {
        this.nowFlow.flowRunRecordInfo.recordTime = dayjs(this.nowFlow.flowRunRecordInfo.recordTime).format(
          'YYYY-MM-DD HH:mm:ss'
        );
      }
      const {
        flowInfo: { nodes, nodeVectors, pairNodeInfo, parallelSections, subflowInfoList },
      } = await getFlowDisposeGraph(this.nowFlow.id);
      this.postFilter(nodes);

      this.lookupNodeDealInfoDialog.flowData = {
        nodes,
        nodeVectors,
        pairNodeInfo,
        parallelFlowConfigs: parallelSections,
        subflowInfoList,
      };
      // let res = await this.getActuallyWorkTeams(row.trainsetId);
      this.addFormWorkTeams = [
        {
          teamName: row.teamName,
          teamCode: row.teamCode,
        },
      ];
      this.$nextTick().then(() => {
        this.$refs.myForms.setValue(row);
      });

      this.lookupNodeDealInfoDialog.visible = true;
    },
    getDetail(info) {
      if (typeof info == 'object') {
        return Object.values(info).toString();
      } else {
        return '';
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
    cellClick(row, property) {
      if (!row.roleWorkerPicUrls[property]) return;
      this.workerPicMap = row.roleWorkerPicUrls[property];
      this.workerDetail = row.roleWorkerDetails[property];

      this.nodeDealDetailDialog.visible = true;
    },

    // 驳回处理
    overruleKeyWorkFlowRun() {
      this.overruleFlowRun.visible = true;
    },

    // 驳回按钮
    async overruleFlowRunButton() {
      if (this.overruleFlowRun.formData.remark) {
        const res = await this.forceEndKeyWorkFlowRun({
          flowRunId: this.flowRun.id,
          nodeId: this.node.id,
          nodeName: this.node.name,
          remark: this.overruleFlowRun.formData.remark,
          workerId: this.userInfo.workerId,
          workerName: this.userInfo.workerName,
        });
        if (res.data.code) {
          this.$message.success('驳回成功!');
          await this.getKeyWorkFlowRunList();
          await this.overruleFlowRunClose();
          this.handleNodeDialog.visible = false;
        } else {
          this.$message.warning(res.data.msg);
        }
      } else {
        this.$message.warning('备注信息不能为空!');
      }
    },

    // 驳回请求
    async forceEndKeyWorkFlowRun(data) {
      return await axios({
        url: '/apiTrainRepair/flowRun/rejectEndKeyWorkFlowRun',
        method: 'post',
        data,
      });
    },

    // 关闭驳回弹出
    overruleFlowRunClose() {
      this.overruleFlowRun.formData.remark = '';
      this.overruleFlowRun.visible = false;
    },

    async getConfig() {
      let res = await axios({
        url: '/apiTrainRepair/config/getConfig',
        method: 'get',
        params: {
          paramName: 'KeyWorkImgUploadNumber',
        },
      });
      this.proposeImgNumber = res.data.data ? res.data.data.paramValue : false;
    },

    // 重新提交
    resubmit(row) {
      this.flowRun = row;
      this.flowRun.id = '';
      this.openAddKeyWorkDialog();
      this.$nextTick().then(() => {
        this.$refs.myForm.setValue(this.flowRun);
      });
    },

    // 撤回当前流程
    revokeWorkFlow(row) {
      this.$confirm('是否撤回当前流程？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        closeOnClickModal: false,
        closeOnPressEscape: false,
      })
        .then(() => {
          this.revokeKeyWorkFlowRun({
            flowRunId: row.id,
            workerId: this.userInfo.workerId,
            workerName: this.userInfo.workerName,
          })
            .then(async () => {
              await this.getKeyWorkFlowRunList();
              this.$message({
                type: 'success',
                message: '撤回成功',
              });
            })
            .catch(() => {
              this.$message({
                type: 'error',
                message: '撤回失败',
              });
            });
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '取消撤回',
          });
        });
    },

    async revokeKeyWorkFlowRun(data) {
      return await revokeKeyWorkFlowRun(data);
    },

    getKeyWorkType(val) {
      if (!this.columnListMap.KEY_WORK_TYPEList) return;
      let keyWorkType = this.columnListMap.KEY_WORK_TYPEList.filter((item) => {
        return item.value == val;
      });

      return keyWorkType[0] && keyWorkType[0].label;
    },
    sortTrainsetId(a, b) {
      const aNum = parseInt(a.trainsetName.split('-').join(''));
      const bNum = parseInt(b.trainsetName.split('-').join(''));
      return aNum - bNum;
    }
  },
  filters: {
    timeFilter(val) {
      if (val) {
        return dayjs(val).format('YYYY-MM-DD HH:mm');
      } else {
        return '';
      }
    },

    filterRejectDetails(val) {
      if (!val.flowRunRecordInfo) return;
      return `驳回：${val.flowRunRecordInfo.workerName} 驳回时间：${dayjs(val.flowRunRecordInfo.recordTime).format(
        'YYYY-MM-DD HH:mm'
      )}`;
    },
  },
});
