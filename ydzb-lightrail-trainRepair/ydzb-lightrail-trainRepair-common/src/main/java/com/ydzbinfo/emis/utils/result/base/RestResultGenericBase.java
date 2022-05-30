package com.ydzbinfo.emis.utils.result.base;


import com.ydzbinfo.emis.utils.entity.ToStringUtil;

/**
 * 带泛型的通用返回结果实体(基础)
 *
 * @author 张天可
 */
@SuppressWarnings("unchecked")
public abstract class RestResultGenericBase<R, T extends RestResultGenericBase<R, T>> {
    public static final ResultStyleEnum RESULT_CODE_STYLE = ResultStyleEnum.STYLE_ENUM_10;
    protected RestResponseCodeEnum code;
    protected String msg;
    protected R data;

    protected RestResultGenericBase(RestResponseCodeEnum code) {
        this.code = code;
    }

    public RestResponseCodeEnum getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T setMsg(String msg) {
        this.msg = msg;
        return (T) this;
    }

    public R getData() {
        return data;
    }

    public T setData(R data) {
        this.data = data;
        return (T) this;
    }

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
