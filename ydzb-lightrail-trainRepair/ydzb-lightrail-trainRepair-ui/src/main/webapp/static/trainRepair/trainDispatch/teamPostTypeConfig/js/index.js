const copyData = (data)=>JSON.parse(JSON.stringify(data))
const teamPostTypeConfig = new Vue({
  el: '#teamPostTypeConfig',
  data: {
    loading:false,
    teamFormData: {
      teamType: '',
      teamPost: '',
    },
    teamTableHeight: 0,
    teamTableData: [],
    teamTablePageNum: 1,
    teamTablePageSize: 20,
    teamTablePageTotal: 0,
    pageStatus: '',
    teamPostTypeDialogVisible: false,
    editTeamPostTypeFormData: {
      postName: '',
      postId: '',
    },
    editTeamPostTypeFormRules: {
      postName: [{ required: false }],
      postId: [{ required: false }],
    },
    // 是否可编辑
    teamPostTypeConfigEdit: teamPostTypeConfigEdit
  },
  created() {
    this.getPostList();
    this.teamTableHeight = window.innerHeight - 150 + 'px';
  },
  computed: {
    pageTitle: GeneralUtil.getObservablePageTitleGetter('岗位配置'),
    postDialogTitle({ pageStatus }) {
      if (pageStatus === 'add') {
        return '班组岗位类型配置-新增';
      } else if (pageStatus === 'edit') {
        return '班组岗位类型配置-编辑';
      }
    },
    lastIndex({teamTablePageSize,teamTablePageTotal}){
      if(teamTablePageTotal===0)return 0
      return teamTablePageTotal>teamTablePageSize?teamTablePageSize - 1:teamTablePageTotal - 1
    }
  },
  watch: {},
  methods: {
    async getPostList() {
      const {
        data: { count, postList },
      } = await getPostList({
        pageNum: this.teamTablePageNum,
        pageSize: this.teamTablePageSize,
      });
      this.teamTableData = postList;
      this.teamTablePageTotal = count;
    },
    validatePost(data) {
      const { postName } = data;
      return !this.teamTableData.some((item) => item.postName === postName);
    },
    async addPost(data) {
      await addPost(data);
    },
    async updPost(data) {
      await updPost(data);
    },
    async delPost(data) {
      await delPost(data);
    },
    addPostBtn() {
      this.pageStatus = 'add';
      this.teamPostTypeDialogVisible = true;
      this.editTeamPostTypeFormData.postId = '';
    },
    editTeamPostType(row) {
      this.pageStatus = 'edit';
      this.teamPostTypeDialogVisible = true;
      this.$nextTick(() => {
        this.editTeamPostTypeFormData.postName = row.postName;
        this.editTeamPostTypeFormData.postId = row.postId;
      });
    },
    teamPostTypeDialogClose() {
      this.$refs.editTeamPostTypeFormRef.resetFields();
    },
    async confirmPostBtn() {
      let postName = this.editTeamPostTypeFormData.postName
      if (postName && postName.split(' ').join('').length != 0) {
        if (this.validatePost(this.editTeamPostTypeFormData)) {
          if (this.pageStatus === 'add') {
            await this.addPost(this.editTeamPostTypeFormData);
          }
          if (this.pageStatus === 'edit') {
            await this.updPost([this.editTeamPostTypeFormData]);
          }
          this.teamPostTypeDialogVisible = false;
          this.$message.success('保存成功');
          this.getPostList();
        } else {
          this.$message.error('不能添加相同的岗位类型');
        }
      } else {
        this.$message.error('岗位类型不能为空');
      }
    },
    cancelPostBtn() {
      this.teamPostTypeDialogVisible = false;
    },
    async deleteTeamPostType(row) {
      let res = await this.$confirm('此操作将永久删除该岗位，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).catch(() => {
        this.$message.info('已取消删除');
      });
      if (res == 'confirm') {
        await this.delPost({ postId: row.postId, postName: row.postName });
        this.$message.success('删除成功');
        this.getPostList();
      }
    },

    teamTablePageSizeChange(pageSize) {
      this.teamTablePageSize = pageSize;
      this.getPostList();
    },
    teamTablePageNumChange(pageNum) {
      this.teamTablePageNum = pageNum;
      this.getPostList();
    },
    // 岗位排序
    postSortChange(type,postId){
      const targetPostIndex = this.teamTableData.findIndex(item=>item.postId === postId)
      let posts
      if(type==='up'){
        posts = copyData(this.teamTableData.slice(targetPostIndex - 1,targetPostIndex+1))
      }else{
        posts = copyData(this.teamTableData.slice(targetPostIndex,targetPostIndex+2))
      }
      const middleWareSort = posts[0].sort
      posts[0].sort =  posts[1].sort
      posts[1].sort = middleWareSort

      this.sortChange(posts)
    },
    // 请求排序
    async sortChange(posts){
      try {
        this.loading = true
        await updPost(posts);
        await this.getPostList();
      } catch (error) {
          console.log(error);
      }finally{
        this.loading = false
      }
    },
  },
});
