const { Message } = this.ELEMENT;
const instance = axios.create({});
instance.interceptors.response.use(
  (res) => {
    const { code, data, msg } = res.data;
    if (code == 0) {
      return data;
    } else {
      Message.error(msg);
      return Promise.reject(new Error(msg));
    }
  },
  (err) => {
    Message.error(err.message);
    return Promise.reject(err);
  }
);
