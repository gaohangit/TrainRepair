axios.interceptors.request.use(
    function (req) {
        showLayer();
        return req;
    },
    function (err) {
        console.log(err);
    }
);
axios.interceptors.response.use(function (res) {
    hideLayer();
    return res;
}, function (err) {
    console.log(err);
});