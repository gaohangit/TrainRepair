package com.ydzbinfo.emis.utils;

import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.utils.result.RestResult;

/**
 *
 * @author 吴跃常
 */
public class RestResultUtils {
    public static JSONObject RestResultChangeJSONObject(RestResult restResult) {
        JSONObject result = new JSONObject();
        result.put("code", restResult.getCode());
        result.put("data", restResult.getData());
        result.put("msg", restResult.getMsg());
        return result;
    }
}
