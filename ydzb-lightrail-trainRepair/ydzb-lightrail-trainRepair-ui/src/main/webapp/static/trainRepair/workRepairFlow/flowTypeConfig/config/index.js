const instance = axios.create({});
instance.interceptors.response.use((res) => {
  if (res.data.code != 1) {
    main.$message.error(res.data.msg);
  }
  return res.data;
});
