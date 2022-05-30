package com.ydzbinfo.emis.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ydzbinfo.emis.guns.config.HighLevelRepairProperties;
import com.ydzbinfo.emis.guns.config.RecheckTaskProperties;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.JsonRootBean;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.HighLevelRepairInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.axiom.om.*;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/23 11:08
 **/
@Slf4j
public class HttpUtil {
    @Resource
    private RecheckTaskProperties recheckTaskProperties;

    private static RequestConfig requestConfig;
    private static final String DEFAULT_ENCODING = "UTF-8";

    public static JsonRootBean getToken(RecheckTaskProperties recheckTaskProperties) {
        String result = null;
        String secretKey = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        Map<String, Object> params = new HashMap<>();
        params.put("Account", DESUtil.encode(secretKey, recheckTaskProperties.getAccount()));
        params.put("Password", DESUtil.encode(secretKey, recheckTaskProperties.getPassword()));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(recheckTaskProperties.getGetTokenUrl());
        CloseableHttpResponse response = null;
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("RequestId", DESUtil.encode(secretKey, requestId));
        httpPost.setHeader("SecretKey", RSACryption.encryptData(secretKey, recheckTaskProperties.getXmlPublicKey()));

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                // 过滤空值
                if (entry.getValue() == null || entry.getValue().equals("")) {
                    continue;
                }
                Object objVal = entry.getValue();
                String val = getValue(objVal);

                NameValuePair pair = new BasicNameValuePair(entry.getKey(), val);
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName(DEFAULT_ENCODING)));

            response = httpClient.execute(httpPost);
            // System.out.p//System.out.onse.toString());
            HttpEntity entity = response.getEntity();

            result = EntityUtils.toString(entity, DEFAULT_ENCODING);

        } catch (IOException e) {

        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                }
            }
        }
        log.info("getToken账号密码入参:" + params + "返回未解密的数据:" + result + ",,,secretKey为:" + secretKey);
        for (Header allHeader : httpPost.getAllHeaders()) {
            log.info("getTokenHeaders入参:" + allHeader.getName() + "," + allHeader.getValue());
        }
        String decodeResult = DESUtil.decode(secretKey, result);
        log.info("getToken返回数据解密后结果" + decodeResult);
        JsonRootBean jsonRootBean = JSONObject.parseObject(decodeResult, JsonRootBean.class);
        return jsonRootBean;
    }

    public static JSONObject doPost(String url, Map<String, Object> params, RecheckTaskProperties recheckTaskProperties, JsonRootBean jsonRootBean) {
        String result = null;
        String secretKey = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("SecretKey", RSACryption.encryptData(secretKey, recheckTaskProperties.getXmlPublicKey()));
        httpPost.setHeader("RequestId", DESUtil.encode(secretKey, requestId));
        JSONObject json = JSONObject.parseObject(JSON.toJSONString(jsonRootBean.getData().getAccessToken(), SerializerFeature.WriteMapNullValue));
        // JSONObject jsonObject=JSONObject.parseObject(JSONObject.toJSONString(jsonRootBean.getData().getAccessToken()));
        httpPost.setHeader("Authorization", DESUtil.encode(secretKey, json.toString()));

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                // 过滤空值
                if (entry.getValue() == null || entry.getValue().equals("")) {
                    continue;
                }
                Object objVal = entry.getValue();
                //String val = getValue(objVal);
                String val = DESUtil.encode(secretKey, getValue(objVal.toString()));

                NameValuePair pair = new BasicNameValuePair(entry.getKey(), val);
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName(DEFAULT_ENCODING)));

            response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();

            result = EntityUtils.toString(entity, DEFAULT_ENCODING);

        } catch (IOException e) {

        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                }
            }
        }
        log.info("获取解密前的复核任务:{}", result);
        result = DESUtil.decode(secretKey, result);
        log.info("获取解密后的复核任务:{}", result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject;
    }

    public static List<HighLevelRepairInfo> getHighLevelRepairInfo(HighLevelRepairProperties highLevelRepairProperties, String unitCode) throws AxisFault {
        String url = highLevelRepairProperties.getWsdlUrl();
        String tns = highLevelRepairProperties.getXmlnsUrl();
        String methodName = highLevelRepairProperties.getMethodName();
        Options options = new Options();
        EndpointReference targetEPR = new EndpointReference(url);
        options.setTo(targetEPR);
        //需要调用的接口方法
        options.setAction(tns + methodName);
        //设置超时时间

        long timeOutInMilliSeconds = Long.parseLong("10000");
        options.setTimeOutInMilliSeconds(timeOutInMilliSeconds);
        ServiceClient sender = new ServiceClient();
        sender.setOptions(options);
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace(tns, "");

        OMElement method = fac.createOMElement(methodName, omNs);
        OMElement deptCodePar = fac.createOMElement("deptCode", omNs);
        deptCodePar.addChild(fac.createOMText(deptCodePar, unitCode));

        method.addChild(deptCodePar);
        method.build();
        OMElement result = sender.sendReceive(method);
        Iterator iterator = result.getChildElements();
        String resultNodeName = methodName + "Result";
        while (iterator.hasNext()) {
            OMNode omNode = (OMNode) iterator.next();
            if (omNode.getType() == OMNode.ELEMENT_NODE) {
                OMElement omElement = (OMElement) omNode;
                if (resultNodeName.equals(omElement.getLocalName())) {
                    String temp = omElement.getText().trim();
                    JSONObject jsonobject = JSON.parseObject(temp);
                    List<HighLevelRepairInfo> highLevelRepairInfoList = JSONArray.parseArray(jsonobject.getJSONArray("data").toJSONString(), HighLevelRepairInfo.class);
                    return highLevelRepairInfoList;
                }
            }
        }
        return new ArrayList<>();
    }


    /**
     * 值类型转化
     *
     * @param objVal
     * @return
     */
    private static String getValue(Object objVal) {
        String val = null;

        if (objVal instanceof String) {
            val = String.valueOf(objVal);
        } else if (objVal instanceof String[]) {
            val = ((String[]) objVal)[0];
        } else {
            throw new IllegalArgumentException("map type is eroor, please use Map<String,String> or Map<String, String[])");
        }
        return val;
    }


}
