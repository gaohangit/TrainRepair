Vue.component('wang-editor', {
  name: 'wangEditor',
  template: `
    <div class="editor">
      <div ref="editorForm"/>
    </div>
  `,
  props: {
    // 传递过来的编辑器内容参数，用于设置编辑器内容
    content: {
      type: String,
      default: '',
    },
    able:{
      type: Boolean,
      default: true,
    }
  },
  data() {
    return {
      // 真正的编辑器里的内容
      editorContent: '',
      // 编辑器对象
      editor: null
    };
  },
  watch: {
    //  watch 表示监听  当父组件的内容变化时需要更新编辑器的内容
    content() {
      this.editor.txt.html(this.content);
    },
  },
  /**
   * 这里使用了 Vue 的 mounted 函数钩子，这属于 Vue 生命周期的一个阶段
   */
  mounted() {
    // 初始化
    this.editor = new wangEditor(this.$refs.editorForm);
    // 配置上传图片

    this.editor.config.customUploadImg = async(resultFiles,insertImgFn)=>{
      let form = new FormData()
      resultFiles.forEach(file=>{
        form.append(file.name,file)
      })
      const res = await axios({
        url:'/apiTrainRepair/taskFlowConfig/uploadFile',
        method:'post',
        data:form,
        headers:{
          "Content-Type":'multipart/form-data'
        }
      })
      if(res.data.code==0){
        console.log('0000');
        return this.$message.error(res.data.msg)
      }
      let fileRes = res.data.data

      fileRes.forEach((file)=> {
        insertImgFn(`/storageTrainRepair/${file.relativeUrl}`)
      })
    }

    this.editor.config.showLinkImg = false

    this.editor.config.menus = [
      'head',
      'bold',
      'fontSize',
      'fontName',
      'italic',
      'underline',
      'strikeThrough',
      'foreColor',
      'backColor',
      'link',
      'list',
      'justify',
      'quote',
      'image',
      'table',
      'code',
      'undo',
      'redo',
      'fullscreen'
    ]

    this.editor.create();
    if(!this.able){
      this.editor.disable();
    }
    this.editor.txt.html(this.content);
  },
  methods: {
    setContent(val) {
      this.content = val
    }
  }
});
