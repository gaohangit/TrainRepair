var REQUEST_TIME = 100000;
/**
 * 通用封装
 * by miaoxx
 * */
(function ($) {
    $.extend({
        table: {
            _option: {},
            _params: {},
            //初始化表参数
            init: function (id, options) {
                $.table._option = options;
                $.table._params = $.common.isEmpty(options.queryParams) ? $.table.queryParams : options.queryParams;
                $('#' + id).bootstrapTable('destroy');
                $('#' + id).bootstrapTable({
                    url: options.url,																// 地址
                    method: $.common.isEmpty(options.method) ? 'get' : options.method,				//
                    idField: $.common.isEmpty(options.idField) ? '' : options.idField,
                    columns: options.columns,														// 列信息
                    pagination: $.common.isEmpty(options.pagination) ? true : options.pagination, 	// 是否开启分页
                    pageSize: $.common.isEmpty(options.pageSize) ? '' : options.pageSize, 													// 页面数据条数
                    pageNumber: $.common.isEmpty(options.pageNumber) ? '' : options.pageNumber, 												// 首页页码
                    pageList: $.common.isEmpty(options.pageList) ? '' : options.pageList, 													// 设置页面可以显示的数据条数
                    sortName: $.common.isEmpty(options.sortName) ? undefined : options.sortName, 		// 要排序的字段
                    height: $.common.isEmpty(options.height) ? undefined : options.height, 			// 表格高度
                    width: $.common.isEmpty(options.width) ? undefined : options.width, 			// 表格高度
                    queryParams: $.table._params,													// 传参
                    contentType: $.common.isEmpty(options.contentType) ? "application/x-www-form-urlencoded" : options.contentType,								// 发送到服务端的数据编码类型
                    cache: true, 																	// 设置为 false 禁用 AJAX 数据缓存， 默认为true
                    striped: false,  																// 表格显示条纹，默认为false
                    sortable: true,
                    sortOrder: $.common.isEmpty(options.sortOrder) ? 'desc' : options.sortOrder, 			// 排序规则
                    search: false,																	// 开启搜索栏

                    sidePagination: $.common.isEmpty(options.sidePagination) ? 'server' : options.sidePagination,		// 设置为服务器端分页
                    dataType: "json",																// 服务器返回的数据类型
                    paginationLoop: $.common.isEmpty(options.paginationLoop) ? false : options.paginationLoop,// 默认是true,是否设置分页无限循环
                    undefinedText: '',
                    onlyInfoPagination: $.common.isEmpty(options.onlyInfoPagination) ? false : options.onlyInfoPagination,														// 只显示条数不显示分页
                    uniqueId: $.common.isEmpty(options.uniqueId) ? '' : options.uniqueId,
                    fixedNumber: $.common.isEmpty(options.fixedNumber) ? '' : options.fixedNumber,
                    fixedColumns: $.common.isEmpty(options.fixedColumns) ? false : options.fixedColumns,
                    rowAttributes: $.common.isEmpty(options.rowAttributes) ? function (row, index) {												// 自定义隔行换色

                    } : options.rowAttributes,
                    detailView:$.common.isEmpty(options.detailView) ? false : options.detailView,
                    onExpandRow: $.common.isEmpty(options.onExpandRow) ? function (index,row,$detail) {  //加载成功时执行

                    } : options.onExpandRow,
                    onLoadSuccess: $.common.isEmpty(options.onLoadSuccess) ? function (value) {  //加载成功时执行
                        //console.info("加载成功");
                    } : options.onLoadSuccess,
                    onLoadError: $.common.isEmpty(options.onLoadError) ? function () {  //加载失败时执行
                        //console.info("加载数据失败");
                    } : options.onLoadError,
                    onClickRow: $.common.isEmpty(options.onClickRow) ? function (value, row, index) {
                        //console.info("点击");
                    } : options.onClickRow,
                    onPostHeader: $.common.isEmpty(options.onPostHeader) ? function () {  //加载失败时执行
                        //console.info("加载数据失败");
                    } : options.onPostHeader,
                    onSort: $.common.isEmpty(options.onSort) ? function (name, order) {  //加载失败时执行
                        //console.info("加载数据失败");
                        showLayer();
                    } : options.onSort,
                    onPageChange: $.common.isEmpty(options.onPageChange) ? function () {  //加载失败时执行
                        //console.info("加载数据失败");
                        showLayer();
                    } : options.onPageChange,
                });
            },
            /*
             * 向后台传参
             */
            queryParams: function (params) {
                return {
                    limit: params.limit, // 每页要显示的数据条数
                    offset: params.offset / params.limit + 1 // 每页显示数据的开始行号
                }
            }
        },
        //公共方法
        common: {
            /*
             * 判断空字符
             */
            isEmpty: function (val) {
                if (val == null || this.trim(val) == '') {
                    return true;
                }
                return false;
            },
            /*
             * 去除首位空格
             */
            trim: function (val) {
                if (val == null) {
                    return "";
                }
                return val.toString().replace(/(^\s*)|(\s*$)|\r|\n/g, "")
            }
        }
    });
})(jQuery);

/*
 * 表格内容悬停显示title
 */
function paramsMatter(value, row, index) {
    if (value) {
        var values = value.toString().replace(/\s+/g, '&nbsp;');
        return "<span title=" + values + ">" + value + "</span>"
    }
}

/**
 * 获取地址栏传输数据
 * @param {要获取的数据名称} name
 */
function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return decodeURI(r[2]);
    return null;
}

/**
 * 个位数补零
 * @param {要补零的数据} iNum
 */
function toDou(iNum) {
    return iNum < 10 ? '0' + iNum : '' + iNum;
}

/**
 * 导航栏鼠标移入事件
 * @param {jq的标签ID/class} obj
 */
function mouseover(obj) {
    obj.find('dl').addClass('layui-show');
    obj.find('span').addClass('layui-nav-mored');
}

/**
 * 导航栏鼠标移出事件
 * @param {jq的标签ID/class} obj
 */
function mouseleave(obj) {
    obj.find('dl').removeClass('layui-show');
    obj.find('span').removeClass('layui-nav-mored');
}

/**
 * 查找数组中某个值
 */
Array.prototype.indexOf = function (val) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == val) return i;
    }
    return -1;
}
/**
 * 数组删除指定元素
 */
Array.prototype.remove = function (val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
}
/**
 * 数组去重
 */
Array.prototype.uniq = function (arr) {
    var temp = [];
    for (var i = 0; i < arr.length; i++) {
        if (temp.indexOf(arr[i]) == -1) {
            temp.push(arr[i])
        }
    }
    return temp;
}
//数组排序 升序
function asCompare(name,minor){
    return function(o,p){
        var a,b;
        if(o&&p&&typeof o==='object'&&typeof p==='object'){
            a=parseFloat(o[name]);
            b=parseFloat(p[name]);
            if(a===b){
                return 0
            }
            return a<b?-1:1;
        }else{
            console.error('error')
        }
    }
}
/**
 * 查询数组中的最大值
 * @returns {number}
 */
Array.prototype.max = function () {
    return Math.max.apply({}, this);
}

/**
 * 查询数组中的最小值
 * @returns {number}
 */
Array.prototype.min = function () {
    return Math.min.apply({}, this);
}

/**
 * 模态框垂直居中
 * @param {模态框id} id
 */
function centerDialog(id) {
    $('#' + id).on('shown.bs.modal', function (e) {
        $(this).css('display', 'block');
        var modalHeight = $(window).height() / 2 - $('#' + id).find('.modal-dialog').height() / 2;
        $(this).find('.modal-dialog').css({
            'margin-top': modalHeight
        })
    })
}

/**
 * 显示等待遮罩层
 */
function showLayer() {
    $('.layer').show();
    $('.layerImg').show();
}

/**
 * 隐藏等待遮罩层
 */
function hideLayer() {
    $('.layer').hide();
    $('.layerImg').hide();
}

window.onresize = window.onscroll = function () {
    $('.container-fluid').css('height', $(document).height())
}
$(function () {
    $('.container-fluid').css('height', $(document).height());
})

//时间插件(有开始结束)
function dateUnit(formDate, toDate) {
    layui.use(['laydate', 'layer'], function () {
        var layuiDate = layui.laydate;
        var nowTime = new Date();

        var dateFromTime = nowTime.getFullYear() + '-' + toDou(nowTime.getMonth() + 1) + '-' + toDou(nowTime.getDate())
        var dateToTime = nowTime.getFullYear() + '-' + toDou(nowTime.getMonth() + 1) + '-' + toDou(nowTime.getDate())

        $('#' + formDate).val(dateFromTime)
        $('#' + toDate).val(dateToTime)
        var startTime = layuiDate.render({
            elem: '#' + formDate,
            format: 'yyyy-MM-dd',
            done: function (value, date, endDate) {
                nowTime = endTime.config.min = {
                    year: date.year,
                    month: date.month - 1,
                    date: date.date
                }
            },
            showBottom: false,
            theme: '#393d49'
        });
        var endTime = layuiDate.render({
            elem: '#' + toDate,
            format: 'yyyy-MM-dd',
            done: function (value, date, endDate) {
                //console.log(date)
            },
            showBottom: false,
            theme: '#393d49'
        });
    })
}

function isBig(formDate, toDate) {
    var toVal1 = $('#' + formDate).val();
    var oDate1 = new Date(toVal1.split('-')[0], toVal1.split('-')[1] - 1, toVal1.split('-')[2]);
    var toVal2 = $('#' + toDate).val();
    var oDate2 = new Date(toVal2.split('-')[0], toVal2.split('-')[1] - 1, toVal2.split('-')[2]);
    var isBig = oDate1 - oDate2;
    if (isBig > 0) {
        return true;
    } else {
        return false;
    }
}

//单个日期/时间选择
/**
 *
 * @param {String} id 文本框ID
 * @param {Boolean} showBottom    是否显示底部工具栏
 * @param {String} type type类型:
 year---只提供年列表
 month--提供年,月列表
 date---可选择年月日
 time---只提供时分秒
 datetime--可选择年月日时分秒
 */
function choose_sigleTime(id, showBottom, type) {
    layui.use(['laydate'], function () {
        var layuiDate = layui.laydate;
        //时间插件调用
        layuiDate.render({
            elem: '#' + id,
            type: type,
            showBottom: showBottom,
            theme: '#393d49'
        });
    })
}

//单个日期/时间选择/带有默认值（当前时间）
/**
 *
 * @param {String} id 文本框ID
 * @param {Boolean} showBottom    是否显示底部工具栏
 * @param {String} type type类型:
 year---只提供年列表
 month--提供年,月列表
 date---可选择年月日
 time---只提供时分秒
 datetime--可选择年月日时分秒
 */
function setInputTime(id, showBottom, type) {
    layui.use(['laydate'], function () {
        var layuiDate = layui.laydate;
        //时间插件调用
        layuiDate.render({
            elem: '#' + id,
            type: type,
            value: new Date(),
            showBottom: showBottom,
            theme: '#393d49'
        });
    })
}

//设置距离当前日期的某个时间点
function setFallTime(id, showBottom, type, day, hour, min, sec) {
    var date = new Date();
    date.setDate(date.getDate() - day)
    date.setHours(hour)
    date.setMinutes(min)
    date.setSeconds(sec)
    layui.use(['laydate'], function () {
        var layuiDate = layui.laydate;
        //时间插件调用
        layuiDate.render({
            elem: '#' + id,
            type: type,
            value: date,
            showBottom: showBottom,
            theme: '#393d49'
        });
    })
}

function showErrorWarning(id, msg) {
    $('#' + id).addClass('errorWarning');
    $('#' + id).on('focus', function () {
        $('#' + id).removeClass('errorWarning');
    })
    layui.use(['layer'], function () {
        var layer = layui.layer;
        layer.msg(msg);
    })
}

//初始化-计划年月
function initYearAmonth() {
    var oDate = new Date();
    var year = oDate.getFullYear();
    var month = toDou(oDate.getMonth() + 1);
    var yOption = '', mOption = '';
    var initYear = 2007;
    for (var i = initYear; i < year + 2; i++) {
        if (year == i) {
            yOption += '<option value="' + i + '" selected="true">' + i + '</option>'
        } else {
            yOption += '<option value="' + i + '">' + i + '</option>'
        }
    }

    for (var i = 1; i < 13; i++) {
        var num = toDou(i);
        if (Number(month) == num) {
            mOption += '<option value="' + num + '" selected="true">' + num + '</option>'
        } else {
            mOption += '<option value="' + num + '">' + num + '</option>'
        }
    }

    return {"year": yOption, "month": mOption}
}

/**
 *
 * @param {表格数据} data
 * @param {要合并的列名} fieldName
 * @param {合并的行} colspan
 * @param {表名称} target
 * @param {合并的列} rowspan
 */
function mergeCells(data, fieldName, colspan, target, rowspan) {
    for (var i = 0; i < data.length; i++) {
        $('#' + target).bootstrapTable('mergeCells', {index: i, field: fieldName, colspan: colspan, rowspan: rowspan})
        i += (rowspan - 1);
    }
}

//带有年月日的日期
function nomalDate() {
    var oDate = new Date();
    var dateHtml = oDate.getFullYear() + '年' + toDou(oDate.getMonth() + 1) + '月' + toDou(oDate.getDate()) + '日';
    return dateHtml;
}

//格式化日期为年月日时分秒
function FormatDate(dateStr) {
    var oDate = new Date(dateStr);
    var dateHtml = oDate.getFullYear() + '年' + toDou(oDate.getMonth() + 1) + '月' + toDou(oDate.getDate()) + '日 ' + oDate.getHours() + '时' + oDate.getMinutes() + '分';
    return dateHtml;
}

//日期用横杠'-'间隔
function newDate() {
    var oDate = new Date();
    var dateHtml = oDate.getFullYear() + '-' + toDou(oDate.getMonth() + 1) + '-' + toDou(oDate.getDate());
    return dateHtml;
}

//格式化日期为yyyy-MM-dd
function fomDate(oDate) {
    var dateHtml = oDate.getFullYear() + '-' + toDou(oDate.getMonth() + 1) + '-' + toDou(oDate.getDate());
    return dateHtml;
}

/**
 * 合并列单元格
 * @param id  表格ID
 * @param tdclass 要合并的列td的class
 */
function mergeRowspan(id, tdclass) {
    var fTd = '', cTd = '', num = 0, obj = $('#' + id).find('.' + tdclass);
    if ($('#' + id).find('.' + tdclass).html() != '') {
        obj.each(function (i) {
            if (i == 0) {
                fTd = $(this);
                num = 1;
            } else {
                cTd = $(this);
                if (fTd.text() == cTd.text()) {
                    num++;
                    cTd.hide();
                    fTd.attr('rowSpan', num);
                } else {
                    fTd = $(this);
                    num = 1;
                }
            }
        })
    }
}

/**
 * 合并行单元格
 * @param id  表格ID
 * @param tdclass 要合并的列td的class
 */
function mergeColspan(id, tdclass) {
    var fTd = '', cTd = '', num = 0, obj = $('#' + id).find('.' + tdclass);
    if ($('#' + id).find('.' + tdclass).html() != '') {
        obj.each(function (i) {
            if (i == 0) {
                fTd = $(this);
                num = 1;
            } else {
                cTd = $(this);
                if (fTd.text() == cTd.text()) {
                    num++;
                    cTd.hide();
                    fTd.attr('colSpan', num);
                } else {
                    fTd = $(this);
                    num = 1;
                }
            }
        })
    }
}

/**
 * 获取路局公共方法
 * @param id    路局选择框ID
 */
function getburea(id) {
    $.ajax({
        url: ctxPath + "/bBureaDict/list",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {},
        dataType: "json",
        async: false,
        timeout: REQUEST_TIME,
        success: function (res) {
            var option = '<option value="ALL">所有</option>'
            var data = res.rows;
            var len = data.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + data[i].cBureacode + '">' + data[i].sBureanameabbr1 + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            // layui.use(['layer'], function(){
            //     var layer = layui.layer;
            //     if(res.msg){
            //         layer.alert(res.msg);
            //     }else{
            //         layer.alert('查询失败');
            //     }
            // })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
}

/**
 *  获取段公共方法
 * @param obj 路局标签(this)
 * @param id  段所选择框ID
 */
function getBdept(obj, id, type) {
    var data = false;
    $.ajax({
        url: ctxPath + "/bDeptDict/getBDeptDictsByBureaCode",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {
            bureaCode: $(obj).val()
        },
        async: false,
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            data = res;
            var option = '<option value="ALL">所有</option>';
            var len = res.length;
            if (type == 's') {
                for (var i = 0; i < len; i++) {
                    option += '<option value="' + res[i].sDeptcode + '">' + res[i].sDeptname + '</option>'
                }
            } else if (type == 'd') {
                var tmp = [];
                for (var i = 0; i < len; i++) {
                    for (var j = i + 1; j < len; j++) {
                        if (res[i].sFatherdeptcode === res[j].sFatherdeptcode) {
                            j = ++i
                        }
                    }
                    tmp.push(res[i]);
                }
                for (var i = 0; i < tmp.length; i++) {
                    option += '<option value="' + tmp[i].sFatherdeptcode + '">' + tmp[i].S_SEGMENTABBR + '</option>'
                }
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            // layui.use(['layer'], function(){
            //     var layer = layui.layer;
            //     if(res.msg){
            //         layer.alert(res.msg);
            //     }else{
            //         layer.alert('查询失败');
            //     }
            // })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
    return data;
}

/**
 * 获取车系公共方法
 * @param id    车系下拉框ID
 */
function getTrainsetSeries(id) {
    $.ajax({
        url: ctxPath + "/bTrainsetTypeDict/list",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {},
        async: false,
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var option = '<option value="ALL">所有</option>';
            var data = res.data;
            var len = data.length;
            var array = [];
            for (var i = 0; i < len; i++) {
                if (array.indexOf(data[i].sTrainsettype) == -1) {
                    array.push(data[i].sTrainsettype);
                }
            }
            for (var i = 0; i < array.length; i++) {
                option += '<option value="' + array[i] + '">' + array[i] + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            // layui.use(['layer'], function(){
            //     var layer = layui.layer;
            //     if(res.msg){
            //         layer.alert(res.msg);
            //     }else{
            //         layer.alert('查询失败');
            //     }
            // })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
}

/**
 *  获取段所公共方法
 * @param obj 段或路局
 * @param id 所
 */
function getBDeptDicts(type, jId, dId, sId) {
    var bCode = '';
    var pCode = '';
    if (type == 'j') {
        bCode = $('#' + jId).val();
        pCode = 'ALL';

    } else if (type == 'd') {
        bCode = jId == '' ? '' : $('#' + jId).val();
        if (dId == '') {
            pCode = '';
        } else {
            pCode = $('#' + dId).val();
        }
    }
    $.ajax({
        url: ctxPath + "/bDeptDict/getBDeptDictsByParentCode",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {
            bureaCode: bCode,
            parentCode: pCode
        },
        dataType: "json",
        async: false,
        timeout: REQUEST_TIME,
        success: function (res) {
            var d_option = '<option value="ALL">所有</option>';
            var s_option = '<option value="ALL">所有</option>';
            var rows = res.rows;
            var len = rows.length;
            for (var i = 0; i < len; i++) {
                if (rows[i].cDepttypecode == '03') {
                    //段
                    d_option += '<option value="' + rows[i].sDeptcode + '">' + rows[i].sDeptabbr + '</option>'
                } else if (rows[i].cDepttypecode == '04') {
                    //所
                    s_option += '<option value="' + rows[i].sDeptcode + '">' + rows[i].sDeptabbr + '</option>'
                }
            }
            if (type == 'j') {
                if (dId == '') {
                    $('#' + sId).html(s_option);
                } else {
                    $('#' + dId).html(d_option);
                    $('#' + sId).html(s_option);
                }
            } else if (type == 'd') {
                $('#' + sId).html(s_option);
            } else if (type == '') {
                //type传入空值
                if (dId == '') {
                    //不传入段ID,只给所赋值
                    $('#' + sId).html(s_option);
                } else {
                    //传入段ID , 段所都赋值
                    $('#' + dId).html(d_option);
                    $('#' + sId).html(s_option);
                }
            }
        },
        error: function (res, req) {
            // layui.use(['layer'], function(){
            //     var layer = layui.layer;
            //     if(res.msg){
            //         layer.alert(res.msg);
            //     }else{
            //         layer.alert('查询失败');
            //     }
            // })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
}

/**
 * 获取车型公共方法
 * @param id    车型下拉框ID
 */
function getTrainset(id) {
    $.ajax({
        url: ctxPath + "/bTrainsetTypeDict/list",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {},
        async: false,
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var option = '<option value="ALL">所有</option>';
            var data = res.data;
            var len = data.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + data[i].sTrainsetname + '">' + data[i].sTrainsetname + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            // layui.use(['layer'], function(){
            //     var layer = layui.layer;
            //     if(res.msg){
            //         layer.alert(res.msg);
            //     }else{
            //         layer.alert('查询失败');
            //     }
            // })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
}

/**
 * 获取车组号公共方法
 * @param obj   车型标签
 * @param id    车组号下拉ID
 */
function getTrainGroup(obj, id) {
    $('#traingroupNum').val('');
    $('#traingroupNum').attr('data-id', '')
    /*$.ajax({
        url: ctxPath + "/pdBTrainsetDict/list/" + $(obj).val(),
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {},
        async: false,
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var option = '<option value="ALL">所有</option>';
            var data = res.data;
            var len = data.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + data[i].sTrainsetid + '" trainsetno="' + data[i].sTrainsetno + '">' + data[i].sTrainsetno + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            // layui.use(['layer'], function(){
            //     var layer = layui.layer;
            //     if(res.msg){
            //         layer.alert(res.msg);
            //     }else{
            //         layer.alert('查询失败');
            //     }
            // })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })*/

}

function getTrainGroup1(obj, id) {
    $.ajax({
        url: ctxPath + "/pdBTrainsetDict/list/" + $(obj).val(),
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {},
        async: false,
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var option = '<option value="ALL">所有</option>';
            var data = res.data;
            var len = data.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + data[i].sTrainsetid + '" trainsetno="' + data[i].sTrainsetno + '">' + data[i].sTrainsetno + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            // layui.use(['layer'], function(){
            //     var layer = layui.layer;
            //     if(res.msg){
            //         layer.alert(res.msg);
            //     }else{
            //         layer.alert('查询失败');
            //     }
            // })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })

}

/**
 * 获取检修单位
 * @param id
 * @returns
 */
function getJxcomp(id) {
    $.ajax({
        url: ctxPath + "/bDeptDict/queryBRepairDeptDict",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {},
        async: false,
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var option = '<option value="ALL">所有</option>';
            var data = res.data;
            var len = data.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + data[i].deptCode + '">' + data[i].deptAbbr + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
}

/********************************************大部件start*************************************************************/
/**
 * 根据车组号获取车号
 * @param obj 车组号标签
 * @param id  车号id
 */
function getTrainNo(obj, id) {
    var trainsetId = $(obj).val();
    $.ajax({
        url: ctxPath + "/QueryKeyPartInfo/getCarNo",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {
            TRAINSETID: trainsetId
        },
        async: false,
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var option = '<option value="ALL">所有</option>';
            var data = res.rows;
            var len = data.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + data[i].ID + '">' + data[i].NAME + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
}

/**
 * 根据车型获取车辆代号
 * @param obj 车型标签
 * @param id  车辆代号id
 */
function getTrainCodeName(obj, id) {
    var trainsetId = $(obj).val();
    $.ajax({
        url: ctxPath + "/QueryKeyPartInfo/getCarSymbol",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {
            cheXing: trainsetId
        },
        async: false,
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var option = '<option value="ALL">所有</option>';
            var data = res.rows;
            var len = data.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + data[i].ID + '">' + data[i].NAME + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
}

/**
 * 获取供应商
 * @param trainType 车型
 * @param id 供应商id
 * @returns
 */
function getSupplier(trainType, id) {
    var trainSetType = $(trainType).val();
    var arr = '';
    $.ajax({
        url: ctxPath + "/QueryKeyPartInfo/getGongYingShang",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {
            keyPartID: arr,
            trainSetType: trainSetType
        },
        async: false,
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var option = '<option value="ALL">所有</option>';
            var data = res.rows;
            var len = data.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + data[i].ID + '">' + data[i].NAME + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
}

/**
 * 获取配件名称
 * @param id 供应商id
 */
function getSupplier(id) {
    var trainSetType = $(trainType).val();
    var arr = '';
    $.ajax({
        url: ctxPath + "/QueryKeyPartInfo/getPartClassList",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {
            keyPartID: arr,
            trainSetType: trainSetType
        },
        async: false,
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var option = '<option value="ALL">所有</option>';
            var data = res.rows;
            var len = data.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + data[i].ID + '">' + data[i].NAME + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
}

/**********************************************大部件end***********************************************************/
//获取配属段
function getdrpDuan(id, bureaCode) {
    $.ajax({
        url: ctxPath + "/pdBTrainsetDict/list/",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {
            bureaCode: bureaCode
        },
        async: false,
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var option = '<option value="ALL">所有</option>';
            var data = res.data;
            var len = data.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + data[i].sTrainsetname + '">' + data[i].sTrainsetno + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
}

function showAssignedHistory(name, code, id) {
    var querytype = '';
    //车组
    if (name == 'trainGroup') {
        querytype = 'TRAINSET'
        $('#assignedHistory-dialog .titleText').html('');
    }
    //部发配属电报
    else if (name == 'telegraph') {
        querytype = 'EQUIP'
        $('#assignedHistory-dialog .titleText').html(code);
    }
    $.ajax({
        url: ctxPath + "/equipHisQuery/getHisEquipDetail",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {
            querytype: querytype,
            telegramCode: code,
            trainsetId: id
        },
        async: false,
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            $('#assignedHistory-dialog').modal('show');
            if (res.code == 0) {
                var tbody = '';
                var data = res.data;
                var len = data.length;
                if (len == '0') {
                    tbody = '<tr><td colspan="13">暂无数据!</td></tr>'
                } else {
                    for (var i = 0; i < len; i++) {
                        tbody += '<tr>' +
                            '	<td>' + (i + 1) + '</td>' +
                            '	<td>' + data[i].s_TRAINTYPENAME + '</td>' +
                            '	<td>' + data[i].s_TRAINSETID1 + '</td>' +
                            '	<td>' + data[i].s_ALLPERSON + '</td>' +
                            '	<td>' + data[i].s_BUREANAMEABBR1 + '</td>' +
                            '	<td>' + data[i].s_DEPLOYDEPOTNAME + '</td>' +
                            '	<td>' + data[i].s_DEPOTABBR + '</td>' +
                            '	<td><a href="javascript:;" onclick="getTelegraphDetail(\'' + data[i].s_EQUIPCODE + '\')">' + data[i].s_EQUIPCODE + '</a></td>' +
                            '	<td>' + data[i].s_EQUIPTIME + '</td>' +
                            '	<td>' + data[i].c_BORROWFLAG + '</td>' +
                            '	<td>' + data[i].s_ACTBUREANAMEABBR1 + '</td>' +
                            '	<td>' + data[i].s_PREDEPOTNAME + '</td>' +
                            '	<td>' + data[i].s_ACTDEPOTABBR + '</td>' +
                            '</tr>'
                    }
                }
                $('#assignedHistory-dialog #detailTable_his').find('tbody').html(tbody);

                $('#assignedHistory-dialog #detailTable_his').fixedHeaderTable({
                    autoShow: true
                });
                $('#assignedHistory-dialog .fht-tbody').css('height', '300px');
                $('#assignedHistory-dialog .fht-tbody .detailTable').css({
                    'width': '100%',
                    'margin-top': '-37px'
                });
                if (len >= 11) {
                    $('#assignedHistory-dialog .fht-thead').css({
                        'width': 'calc(100% - 17px)'
                    })
                } else {
                    $('#assignedHistory-dialog .fht-thead').css({
                        'width': '100%'
                    })
                }
                hideLayer();
            } else {
                hideLayer();
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert(res.msg);
                })
            }
        },
        error: function (res, req) {
            hideLayer();
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
}

function createGuid() {
    var d = new Date().getTime();
    var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
    return 'g-' + uuid;
}


/**
 * 技术履历模块-大部件-转向架维修记录-上报单位下拉框
 * @param obj (this)
 * @param id  选择框ID
 */
function getDeptDictForBogieRepairHisQry(id) {
    $.ajax({
        url: ctxPath + "/bDeptDict/getDeptDictForBogieRepairHisQry",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {},
        async: false,
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var option = '<option value="ALL">所有</option>'
            var len = res.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + res[i].deptCode + '">' + res[i].deptAbbr + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        }
    })

}

//系统功能故障分类(id：下拉框id，type：功能故障类型 1功能)
function getFaultClass(id, type) {
    var data = false;
    $.ajax({
        url: ctxPath + "/Dictionary/getFaultClass",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {},
        async: false,
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var option = '<option value="ALL">所有</option>';
            data = res.data;
            var len = data.length;
            for (var i = 0; i < len; i++) {
                if (data[i].iNodelevel == type) {
                    option += '<option value="' + data[i].sNodeid + '">' + data[i].sNodetext + '</option>'
                }
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
    return data;
}

//责任单位字典(id：下拉框id)
function getDutyDept(id) {
    var data = false;
    $.ajax({
        url: ctxPath + "/Dictionary/getDutyDept",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {},
        dataType: "json",
        async: false,
        timeout: REQUEST_TIME,
        success: function (res) {
            var option = '';
            data = res.data;
            var len = data.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + data[i].s_Id + '">' + data[i].s_Name + '</option>'
            }
            if (option == '') {
                option = '<option value="noData">暂无数据</option>';
            }
            $('#' + id).html(option);
            $('#' + id).selectpicker('refresh');
            $('#' + id).selectpicker('render');
        },
        error: function (res, req) {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
    return data;
}

//系统功能结构()
function getDictSysclass() {
    var data = false;
    $.ajax({
        url: ctxPath + "/Dictionary/getDictSysclass",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {},
        dataType: "json",
        async: false,
        timeout: REQUEST_TIME,
        success: function (res) {
            data = res.data;
        },
        error: function (res, req) {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
    return data;
}

//惯性原因字典表
function getJcBFaultCauseDict(id, trainSetId) {
    var data = false;
    $.ajax({
        url: ctxPath + "/Dictionary/getJcBFaultCauseDict",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {'trainSetId': trainSetId},
        dataType: "json",
        async: false,
        timeout: REQUEST_TIME,
        success: function (res) {
            data = res.data;
            var option = '<option value="ALL">所有</option>'
            var len = data.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + data[i].sId + '">' + data[i].sFaultcausename + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
    return data;
}

//惯性原因字典表(全部 为了表格显示)
function getAllJcBFaultCauseDict() {
    var data = false;
    $.ajax({
        url: ctxPath + "/Dictionary/getAllJcBFaultCauseDict",
        contentType: "application/json;charset=utf-8",
        type: 'post',
        data: {},
        dataType: "json",
        async: false,
        timeout: REQUEST_TIME,
        success: function (res) {
            data = res.data;
        },
        error: function (res, req) {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
    return data;
}

//供应商字典表
function getJcBRunFaultSupplier(id) {
    var data = false;
    $.ajax({
        url: ctxPath + "/Dictionary/getJcBRunFaultSupplier",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {},
        dataType: "json",
        async: false,
        timeout: REQUEST_TIME,
        success: function (res) {
            data = res.data;
            var option = '<option value="ALL">所有</option>'
            var len = data.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + data[i].sSupplierid + '">' + data[i].sSuppliername + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
    return data;
}

//主体责任字典表
function getBrunFaultCauseType(id, codeList) {
    var data = false;
    if (codeList.length == 0) {
        var option = '<option value="ALL">所有</option>'
        $('#' + id).html(option);
        return;
    }
    $.ajax({
        url: ctxPath + "/Dictionary/getBrunFaultCauseType",
        contentType: "application/json;charset=utf-8",
        type: 'post',
        data: JSON.stringify(codeList),
        dataType: "json",
        async: false,
        timeout: REQUEST_TIME,
        success: function (res) {
            data = res.data;
            var option = '<option value="ALL">所有</option>'
            var len = data.length;
            for (var i = 0; i < len; i++) {
                option += '<option value="' + data[i].sid + '">' + data[i].sTypeName + '</option>'
            }
            $('#' + id).html(option);
        },
        error: function (res, req) {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                if (res.msg) {
                    layer.alert(res.msg);
                } else {
                    layer.alert('查询失败');
                }
            })
        },
        complete: function (XMLHttpRequest, status) {
            if (status == 'timeout') {
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    layer.alert('请求超时');
                })
            }
        }
    })
    return data;
}

function fltThousand(val) {
    if (val && val != 0) {
        val = val.toString();
        return val == "" ? val : val.replace(/\d{1,3}(?=(\d{3})+$)/g, '$&,');
    } else {
        return val
    }
}

/**
 * 车组号模糊查询
 * @param id 输入框id
 * @returns
 */
function fuzzyQuery(id) {
    $('#' + id).on('keyup', function () {
        var list = [];
        var num = $(this).val();
        var timer = setTimeout(function () {
            $.ajax({
                url: ctxPath + "/pdBTrainsetDict/list/",
                contentType: "application/json;charset=utf-8",
                type: 'get',
                data: {},
                dataType: "json",
                async: false,
                timeout: REQUEST_TIME,
                success: function (res) {
                    var data = res.data;
                    for (var i = 0; i < data.length; i++) {
                        if (data[i].sTrainsetno.indexOf(num) != -1) {
                            list.push(data[i].sTrainsetno)
                        }
                    }
                    var sLi = '';
                    for (var i = 0; i < list.length; i++) {
                        sLi += '<li>' + list[i] + '</li>'
                    }
                    $('#fuzzyQbox').show().html(sLi);
                    $('#fuzzyQbox li').each(function () {
                        $(this).on('click', function () {
                            $('#' + id).val($(this).text());
                            $('#fuzzyQbox').hide().html('');
                        });
                    });
                },
                complete: function (XMLHttpRequest, status) {
                    if (status == 'timeout') {
                        layui.use(['layer'], function () {
                            var layer = layui.layer;
                            layer.alert('请求超时');
                        })
                    }
                }
            })
        }, 500);
    });
}

/**
 * 限制查询日期范围
 * @param startId 开始日期文本框id
 * @param endId     结束日期文本框id
 * @returns
 */
function limitDate(startId, endId) {
    var s = new Date($('#' + startId).val());
    var e = new Date($('#' + endId).val());
    var countTime = e.getTime() - s.getTime();
    var r = parseInt(countTime / 1000);
    var d = parseInt(r / (24 * 60 * 60)); //天数
    if (d > 30) {
        layui.use(['layer'], function () {
            var layer = layui.layer;
            layer.alert('查询日期范围不能大于30天');
            $('#' + endId).addClass('errorWarning');
            $('#' + endId).on('focus', function () {
                $('#' + endId).removeClass('errorWarning');
            })
        })
    }
}

function limitMonthDate(startId, endId) {
    var s = new Date($('#' + startId).val());
    var e = new Date($('#' + endId).val());
    var countTime = e.getTime() - s.getTime();
    var r = parseInt(countTime / 1000);
    var d = parseInt(r / (24 * 60 * 60)); //天数
    if (d > 30) {
        return false;
    } else {
        return true;
    }
}

/**
 * 日期格式转中文
 * @param str 日期(YYYY-MM-DD)
 * @returns
 */
function cnDate(str) {
    var cnArr = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十'];
    var newStr = str.replace(/[^0-9]+/g, '');
    var year = cnArr[newStr[0]] + cnArr[newStr[1]] + cnArr[newStr[2]] + cnArr[newStr[3]];
    var month = newStr[4] === '0' ? cnArr[newStr[5]] : cnArr[10] + cnArr[newStr[5]];
    var day = newStr[6] === '0' ? cnArr[newStr[7]] : cnArr[10] + cnArr[newStr[7]];
    return year + '年' + month + '月' + day + '日';
}

function getUser() {
    var user = false;
    $.ajax({
        url: ctxPath + "/getUser/getUser",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {},
        dataType: "json",
        async: false,
        timeout: REQUEST_TIME,
        success: function (res) {
            if (res.code == 0) {
                user = res.user;
            } else {
                layer.alert('获取用户失败');
            }
        },
        error: function () {
            layer.alert('获取用户失败');
        }
    })
    return user;
}

function getPreMonth() {
    var iYear = parseInt(this.selYear);
    var iMonth = parseInt(this.selMonth);
    if (iMonth - 1 == 0) {
        iMonth = 12;
        iYear = iYear - 1;
    }
    else {
        iMonth = iMonth - 1;
    }
    var preYM = iYear + "-" + (iMonth < 10 ? "0" + iMonth : iMonth);
    return preYM;
}

function limitchart(obj) {
    var char = new RegExp("[`~!@#$%^&*()=_·|{}+\':;,\\[\\].<>/?~！@#￥&*()|\\\【】\‘’“”：？；\"]", 'g');
    $(obj).val($(obj).val().replace(char, '').replace(/\s+/g, ''));
}

function limitLength(obj, length) {
    var value = $(obj).val();
    if (value.length > length) {
        showTip("长度限制：" + length);
        $(obj).val(value.substring(0, length));
    }
}

function trimSpace(str) {
    return str.replace(/(^\s*)|(\s*$)/g, "");
}

/**
 * 通过车组号/车组号id 获取车组号name
 * @param id  输入框id
 * @param trainsetType 车型val
 * @returns html
 */
function getTrainsetIdByNo(id, trainsetType) {
    $('#' + id).removeAttr('data-id');
    var trainsetType = trainsetType || '';
    var trainsetNo = $('#' + id).val().toUpperCase();
    $.ajax({
        url: ctxPath + "/pdBTrainsetDict/list",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {
            trainset: trainsetNo,
            trainsetType: trainsetType
        },
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var rows = res.data;
            if (rows) {
                if (rows.length != 0) {
                    $('#traingroupNum').css('border', '1px solid #ccc');
                    var html = '';
                    if (rows.length == 1) {
                        $('#' + id).val(rows[0].sTrainsetname).attr({
                            'data-id': rows[0].sTrainsetid,
                            'data-no': rows[0].sTrainsetno
                        });
                        $('#' + id).parent().find('.fuzzyQbox').html('').hide();
                    } else if (rows.length >= 2) {
                        for (var i = 0; i < rows.length; i++) {
                            html += '<li data-id="' + rows[i].sTrainsetid + '" onclick="c_textVal(\'' + id + '\',\'' + rows[i].sTrainsetid + '\',\'' + rows[i].sTrainsetname + '\',\'' + rows[i].sTrainsetno + '\')">' + rows[i].sTrainsetname + '</li>'
                        }
                        $('#' + id).parent().find('.fuzzyQbox').html(html).show();
                    }
                } else {
                    $('#traingroupNum').val('').css('border', '1px solid red');
                    layui.use(['layer'], function () {
                        var layer = layui.layer;
                        layer.msg('请输入正确车组号!');
                    })
                    $('#' + id).parent().find('.fuzzyQbox').html('').hide();
                }
            }
        }
    })
}

//获取车型分类
function getTrainTypeClass(domId, bureaCode, depotCode, deptCode, trainType) {
    $.ajax({
        url: ctxPath + "/pdBTrainsetDict/getTrainTypeClass",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {
            bureaCode: bureaCode,
            depotCode: depotCode,
            deptCode: deptCode,
            trainType: trainType,
        },
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var rows = res.data;
            var len = rows.length;
            var option = '<option value="ALL">所有</option>';

            for (var i = 0; i < len; i++) {
                option += '<option value="' + rows[i].trainsetType + '">' + rows[i].trainsetTypeName + '</option>'
            }
            $('#' + domId).html(option);
        }
    });
}

//获取车型分类
function getTrainTypeClass(domId, bureaCode, depotCode, deptCode, trainType) {
    $.ajax({
        url: ctxPath + "/pdBTrainsetDict/getTrainTypeClass",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {
            bureaCode: bureaCode,
            depotCode: depotCode,
            deptCode: deptCode,
            trainType: trainType,
        },
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var rows = res.data;
            var len = rows.length;
            var option = '<option value="ALL">所有</option>';

            for (var i = 0; i < len; i++) {
                option += '<option value="' + rows[i].trainsetType + '">' + rows[i].trainsetTypeName + '</option>'
            }
            $('#' + domId).html(option);
        }
    });
}


//获取车型
function getTrainTypeInfo(domId, bureaCode, depotCode, deptCode, trainType) {
    $.ajax({
        url: ctxPath + "/pdBTrainsetDict/getTrainType",
        contentType: "application/json;charset=utf-8",
        type: 'get',
        data: {
            bureaCode: bureaCode,
            depotCode: depotCode,
            deptCode: deptCode,
            trainType: trainType,
        },
        dataType: "json",
        timeout: REQUEST_TIME,
        success: function (res) {
            var rows = res.data;
            var len = rows.length;
            var option = '<option value="ALL">所有</option>';

            for (var i = 0; i < len; i++) {
                option += '<option value="' + rows[i].trainType + '">' + rows[i].trainTypeName + '</option>'
            }
            $('#' + domId).html(option);
        }
    });
}


function c_textVal(id, sTrainsetid, sTrainsetName, sTrainsetno) {
    $('#' + id).parent().find('.fuzzyQbox').html('').hide();
    $('#' + id).val(sTrainsetName).attr({'data-id': sTrainsetid, 'data-no': sTrainsetno});
    $('#' + id).attr('value', sTrainsetName);
}

function limitNum(id) {
    $('#' + id).on('keyup', function () {
        if ($(this).val().length == 1) {
            $(this).val($(this).val().replace(/[^1-9]/g, ''))
        } else {
            $(this).val($(this).val().replace(/\D/g, ''))
        }
    });
}

function showTip(msg) {
    layui.use(['layer'], function () {
        var layer = layui.layer;
        layer.msg(msg);
    })
}


//时间插件(有开始结束)
function dateUnitFault(formDate, toDate, fYear, fMonth, fDay, tYear, tMonth, tDay) {
    layui.use(['laydate', 'layer'], function () {
        var layuiDate = layui.laydate;
        var nowTime = new Date();
        nowTime.setYear(nowTime.getFullYear() + fYear);
        nowTime.setMonth(nowTime.getMonth() + fMonth)
        nowTime.setDate(nowTime.getDate() + fDay);
        var dateFromTime = nowTime.getFullYear() + '-' + toDou(nowTime.getMonth() + 1) + '-' + toDou(nowTime.getDate())
        nowTime = new Date();
        nowTime.setYear(nowTime.getFullYear() + tYear);
        nowTime.setMonth(nowTime.getMonth() + tMonth)
        nowTime.setDate(nowTime.getDate() + tDay);
        var dateToTime = nowTime.getFullYear() + '-' + toDou(nowTime.getMonth() + 1) + '-' + toDou(nowTime.getDate())
        $('#' + formDate).val(dateFromTime);
        $('#' + toDate).val(dateToTime);
        var startTime = layuiDate.render({
            elem: '#' + formDate,
            format: 'yyyy-MM-dd',
            done: function (value, date, endDate) {

            },
            showBottom: false,
            theme: '#393d49'
        });
        var endTime = layuiDate.render({
            elem: '#' + toDate,
            format: 'yyyy-MM-dd',
            done: function (value, date, endDate) {
                //console.log(date)
            },
            showBottom: false,
            theme: '#393d49'
        });
    })
}

//千位分隔符
function handleThousand(val) {
    return val == "" ? val : val.replace(/\d{1,3}(?=(\d{3})+$)/g, '$&,');
}


//获取上个月的今天
function getLastMonth() {
    var now = new Date();
    var year = now.getFullYear();
    var month = now.getMonth() + 1;
    var day = now.getDate();
    var dateObj = {};
    if (parseInt(month) < 10) {
        month = "0" + month
    }
    if (parseInt(day) < 10) {
        day = "0" + day
    }
    dateObj.now = year + '-' + month + '-' + day;
    if (parseInt(month) == 1) {
        dateObj.last = (parseInt(year) - 1) + '-12-' + day;
        return dateObj;
    }
    var preSize = new Date(year, parseInt(month) - 1, 0).getDate();
    if (preSize < parseInt(day)) {
        dateObj.last = year + '-' + month + '01';
        return dateObj;
    }
    if (parseInt(month) < 10) {
        dateObj.last = year + '-0' + (parseInt(month) - 1) + '-' + day;
        return dateObj;
    } else {
        dateObj.last = year + '-' + (parseInt(month) - 1) + '-' + day;
        return dateObj;
    }
}

/**
 * 获取month月份的第一天
 * @param month
 * @returns {string}
 */
function getCurrentMonthFirstDay(month) {
    var date = new Date()
    date.setDate(1)
    var day = date.getDate()
    if (month < 10) {
        month = '0' + month
    }
    if (day < 10) {
        day = '0' + day
    }
    return date.getFullYear() + '-' + month + '-' + day
}

/**
 * 获取month月份的最后一天
 * @param month
 * @returns {string}
 */
function getCurrentMonthLastDay(month) {
    var date = new Date()
    var year = date.getFullYear()
    month = month < 10 ? '0' + month : month
    var day = new Date(year, month, 0)
    return year + '-' + month + '-' + day.getDate()
}



//获取上月第一天和最后一天
function getLStartAend() {
    var obj = {};
    var nowdays = new Date();
    var year = nowdays.getFullYear();
    var month = nowdays.getMonth();
    if (month == 0) {
        month = 12;
        year = year - 1;
    }
    if (month < 10) {
        month = "0" + month;
    }
    obj.firstDay = year + "-" + month + "-" + "01";//上个月的第一天
    var myDate = new Date(year, month, 0);  //上个月最后一天
    obj.lastDay = year + "-" + month + "-" + myDate.getDate();//上个月的最后一天
    return obj;
}

//两个日期之间相差天数
function getDaysBetween(stD, enD) {
    var startDate = Date.parse(stD);
    var endDate = Date.parse(enD);
    var days = (endDate - startDate) / (1 * 24 * 60 * 60 * 1000);
    return days;
}
