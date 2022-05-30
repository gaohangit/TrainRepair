const instance = axios.create({});
instance.interceptors.response.use((res) => {
  return res.data;
});
