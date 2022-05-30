const { Message } = this.ELEMENT;
const instance = axios.create({});
instance.interceptors.response.use(
  (res) => {
    const { code, data, msg } = res.data;
    if (code == 1) {
      return data;
    } else {
      Message.error(msg);
      let error = new Error(msg)
      error.showed = true
      return Promise.reject(error);
    }
  },
  (err) => {
    Message.error(err.message);
    err.showed = true
    return Promise.reject(err);
  }
);
