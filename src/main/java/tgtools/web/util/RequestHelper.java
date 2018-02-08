package tgtools.web.util;

import tgtools.exceptions.APPErrorException;
import tgtools.json.JSONException;
import tgtools.json.JSONObject;
import tgtools.util.StringUtil;
import tgtools.web.rests.entity.ResposeEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * 名  称：处理请求并将如果参数中有method 则映射对应方法处理并返回
 * 使用范围 Servlet jsp 等。
 * 如：jsp使用
 * <% RequestHelper.invoke(this,request,response); %>
 * <%! public String getName(){return "1";}; %>
 * 编写者：田径
 * 功  能：
 * 时  间：14:49
 */
public class RequestHelper {
    private static final String KEY_METHOD = "method";

    private static boolean hasMethod(JSONObject p_Params) {
        try {
            return p_Params.has(KEY_METHOD) && !StringUtil.isNullOrEmpty(p_Params.getString(KEY_METHOD));
        } catch (JSONException e) {
            return false;
        }
    }

    /**
     * 根据请求映射方法
     *
     * @param p_Object
     * @param p_Param
     * @return
     * @throws APPErrorException
     */
    private static String invokeMethod(Object p_Object, JSONObject p_Param) throws APPErrorException {
        JSONObject json = p_Param;
        try {
            String methodname = json.getString(KEY_METHOD);
            Object obj = tgtools.util.ReflectionUtil.invokeMethod(methodname, p_Object, new Class[]{JSONObject.class}, new Object[]{p_Param});
            return null == obj ? "返回值：null" : obj.toString();
        } catch (Exception e) {
            throw new APPErrorException("处理出错：" + e.getMessage(), e);
        }
    }

    /**
     * 根据请求判断参数输入类型 目前只支持 常规和json
     *
     * @param p_Request
     * @return
     */
    private static String getInputType(HttpServletRequest p_Request) {
        if (p_Request.getContentType().indexOf("json") >= 0) {
            return "json";
        } else {
            return "html";
        }
    }

    /**
     * 将输入的参数组装程JSON
     *
     * @param p_Request
     * @return
     * @throws Exception
     */
    private static tgtools.json.JSONObject getInputParameter(HttpServletRequest p_Request)
            throws APPErrorException {
        if (p_Request.getParameterMap().size() < 1) {
            try {
                java.io.InputStream dd = p_Request.getInputStream();
                String res = tgtools.util.StringUtil.parseInputStream(dd, "utf-8");
                return new tgtools.json.JSONObject(res);
            } catch (Exception e) {
                throw new APPErrorException("获取输入流出错；原因：" + e.getMessage(), e);
            }
        }
        JSONObject json = new JSONObject();
        Enumeration keys = p_Request.getParameterNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            json.put(key, p_Request.getParameter(key));
        }
        return json;
    }

    /**
     * 将请求的参数转换程json
     *
     * @param request
     * @return
     * @throws APPErrorException
     */
    public static JSONObject parseRequest(HttpServletRequest request) throws APPErrorException {
        return getInputParameter(request);
    }

    /**
     * 根据请求 调用指定的方法 并返回结果 不做相应
     *
     * @param p_Object
     * @param request
     * @throws APPErrorException
     */
    public static Object invoke(Object p_Object, HttpServletRequest request) throws APPErrorException {
        JSONObject json = getInputParameter(request);
        if (!hasMethod(json)) {
            return null;
        }
        return invokeMethod(p_Object, json);
    }

    /**
     * 根据请求 调用指定的方法 并直接输出
     *
     * @param p_Object
     * @param request
     * @param response
     * @param p_ResponseNeedRest Response 是否需要 reset ；true:方法内进行reset
     * @throws APPErrorException
     */
    public static void invoke(Object p_Object, HttpServletRequest request, HttpServletResponse response,boolean p_ResponseNeedRest) throws APPErrorException {
        String inputtype = getInputType(request);
        String result = StringUtil.EMPTY_STRING;
        String error = StringUtil.EMPTY_STRING;
        try {
            Object obj = invoke(p_Object, request);
            if (null == obj) {
                return;
            }
            result = obj.toString();
        } catch (Exception e) {

            error = e.getMessage();
            e.printStackTrace();
        }

        if ("json".equals(inputtype)) {

            ResposeEntity entity = new ResposeEntity();
            entity.Success = StringUtil.isNullOrEmpty(error);
            entity.Data = result;
            entity.Error = error;
            result = tgtools.util.JsonParseHelper.parseToJson(entity, true);
            if(p_ResponseNeedRest) {
                response.reset();
            }
            response.setContentType(request.getContentType());
        }
        try {
            response.getWriter().write(result);
        } catch (Exception e) {
            throw new APPErrorException("响应出错；原因：" + e.getMessage(), e);
        }
    }
    /**
     * 根据请求 调用指定的方法 并直接输出
     *
     * @param p_Object
     * @param request
     * @param response
     * @throws APPErrorException
     */
    public static void invoke(Object p_Object, HttpServletRequest request, HttpServletResponse response) throws APPErrorException {
        invoke(p_Object,request,response,true);
    }
}