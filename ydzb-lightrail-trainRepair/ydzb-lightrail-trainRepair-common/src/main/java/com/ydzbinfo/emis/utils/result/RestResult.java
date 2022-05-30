package com.ydzbinfo.emis.utils.result;


import com.ydzbinfo.emis.utils.result.base.RestResponseCodeEnum;
import com.ydzbinfo.emis.utils.result.base.RestResponseUtil;
import com.ydzbinfo.emis.utils.result.base.RestResultGenericBase;
import org.slf4j.Logger;

/**
 * 通用返回结果实体
 *
 * @author 张天可
 */
public class RestResult extends RestResultGenericBase<Object, RestResult> {

    private static final RestResponseUtil<RestResult> restResponseUtilInstance;

    static {
        restResponseUtilInstance = new RestResponseUtil<RestResult>() {
            @Override
            public RestResult getNewResponseObject(Integer code) {
                return new RestResult(RestResponseCodeEnum.from(code));
            }

            @Override
            public void setMessage(RestResult responseObject, String message) {
                responseObject.setMsg(message);
            }
        };
    }

    RestResult(RestResponseCodeEnum success) {
        super(success);
    }

    /**
     * 获取成功结果对象
     */
    public static RestResult success() {
        return new RestResult(RestResponseCodeEnum.SUCCESS);
    }

    /**
     * 根据异常生成返回对象，如果是意料之外的异常，会使用logger进行异常日志记录
     */
    public static RestResult fromException(Exception e, Logger logger, String defaultErrorMessage) {
        return restResponseUtilInstance.getResultFromException(e, RESULT_CODE_STYLE, logger, defaultErrorMessage);
    }
}
