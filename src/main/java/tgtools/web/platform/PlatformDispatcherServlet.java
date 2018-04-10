package tgtools.web.platform;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;
import tgtools.exceptions.APPErrorException;
import tgtools.interfaces.IDispose;
import tgtools.json.JSONObject;
import tgtools.message.Message;
import tgtools.util.LogHelper;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：9:22
 */
@Controller
public class PlatformDispatcherServlet extends DispatcherServlet {

    private AbstractRefreshableWebApplicationContext mContext;
    private org.springframework.beans.factory.support.DefaultListableBeanFactory mBeanFactory;
    private org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping mMapper;
    private SimpleUrlHandlerMapping mSimpleUrlHandlerMapping;

    /**
     * 验证
     *
     * @throws APPErrorException
     */
    private void Valid() throws APPErrorException {
        if (null == mContext || null == mBeanFactory || null == mMapper) {
            throw new APPErrorException("无法获取RestBeanFactory，请检查web.xml RestServlet是否配置为tgtools.web.platform.PlatformDispatcherServlet");
        }
    }


    /**
     * 添加 URL 映射
     *
     * @param p_BeanName
     * @param p_Class
     *
     * @throws APPErrorException
     */
    public void addRest(String p_BeanName, Class<?> p_Class) throws APPErrorException {
        Valid();
        try {

            if (!mBeanFactory.containsSingleton(p_BeanName)) {
                LogHelper.info("", "正在添加RestBean：" + p_BeanName + ";;class:" + p_Class.getSimpleName(), "addRest");
                mBeanFactory.registerSingleton(p_BeanName, p_Class.newInstance());//.registerBeanDefinition(p_BeanName, p_BeanDefinition);
                Method method = this.mMapper.getClass().getSuperclass().getSuperclass().getDeclaredMethod("detectHandlerMethods", Object.class);
                method.setAccessible(true);
                method.invoke(mMapper, p_BeanName);
            }
        } catch (Exception e) {
            throw new APPErrorException("获取bean失败", e);
        }
    }

    /**
     * 移除 URL 映射
     *
     * @param p_BeanName
     *
     * @throws APPErrorException
     */
    public void removeRest(String p_BeanName) throws APPErrorException {
        Valid();
        mBeanFactory.destroySingleton(p_BeanName);
        removeUrl(p_BeanName);
    }

    /**
     * @return
     */
    private LinkedHashMap<RequestMappingInfo, HandlerMethod> getHandlerMethods() {
        Field field = null;
        try {
            field = mMapper.getClass().getSuperclass().getSuperclass().getDeclaredField("handlerMethods");
            field.setAccessible(true);
            return (LinkedHashMap) field.get(mMapper);

        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 获取 Mapping
     *
     * @return
     */
    private LinkedMultiValueMap<String, RequestMappingInfo> getUrlMap() {
        try {
            Field field1 = mMapper.getClass().getSuperclass().getSuperclass().getDeclaredField("urlMap");
            field1.setAccessible(true);
            return (LinkedMultiValueMap) field1.get(mMapper);
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * @return
     */
    private LinkedMultiValueMap<String, HandlerMethod> getNameMap() {
        try {
            Field field2 = mMapper.getClass().getSuperclass().getSuperclass().getDeclaredField("nameMap");
            field2.setAccessible(true);
            return (LinkedMultiValueMap) field2.get(mMapper);
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * @param p_BeanName
     */
    private void removeUrl(String p_BeanName) {
        LinkedHashMap<RequestMappingInfo, HandlerMethod> handles = getHandlerMethods();
        LinkedMultiValueMap<String, RequestMappingInfo> urlMap = getUrlMap();
        LinkedMultiValueMap<String, HandlerMethod> nameMap = getNameMap();
        ArrayList<RequestMappingInfo> list = new ArrayList<RequestMappingInfo>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> item : handles.entrySet()) {
            if (item.getValue().getBean().equals(p_BeanName)) {
                list.add(item.getKey());
                removeUrlMap(urlMap, item.getKey());
                removeNamelMap(nameMap, item.getValue());
            }
        }
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                handles.remove(list.get(i));
            }
        }
    }

    /**
     * @param p_NameMap
     * @param p_Method
     */
    private void removeNamelMap(LinkedMultiValueMap<String, HandlerMethod> p_NameMap, HandlerMethod p_Method) {
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<String, List<HandlerMethod>> item : p_NameMap.entrySet()) {
            if (item.getValue().contains(p_Method)) {
                list.add(item.getKey());
            }
        }
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                List<HandlerMethod> methods = p_NameMap.get(list.get(i));
                if (methods.size() > 1) {
                    methods.remove(p_Method);
                } else {
                    p_NameMap.remove(list.get(i));
                }
            }
        }
    }

    /**
     * removeUrlMap
     *
     * @param p_UrlMap
     * @param p_Info
     */
    private void removeUrlMap(LinkedMultiValueMap<String, RequestMappingInfo> p_UrlMap, RequestMappingInfo p_Info) {
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<String, List<RequestMappingInfo>> item : p_UrlMap.entrySet()) {
            if (item.getValue().contains(p_Info)) {
                list.add(item.getKey());
            }
        }
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                List<RequestMappingInfo> infos = p_UrlMap.get(list.get(i));
                if (infos.size() > 1) {
                    infos.remove(p_Info);
                } else {
                    p_UrlMap.remove(list.get(i));
                }
            }
        }
    }

    /**
     * 添加websocket处理器
     *
     * @param pUrlMap url
     * @param pHandle 处理器
     *
     * @throws APPErrorException
     */
    public void addWebsocket(String pUrlMap, WebSocketHttpRequestHandler pHandle) throws APPErrorException {
        if (null == mSimpleUrlHandlerMapping) {
            throw new APPErrorException("Spring Websocket没启用");
        }
        try {

            Method method = SimpleUrlHandlerMapping.class.getSuperclass().getDeclaredMethod("registerHandler", String.class, Object.class);
            if (null == method) {
                throw new APPErrorException("SimpleUrlHandlerMapping 无效无法注册Websocket");
            }
            method.setAccessible(true);
            method.invoke(mSimpleUrlHandlerMapping, pUrlMap, pHandle);
        } catch (Exception e) {
            if (e instanceof APPErrorException) {
                throw (APPErrorException) e;
            }
            throw new APPErrorException("添加失败；原因：" + e.getMessage(), e);
        }
    }

    /**
     * 移除websocket处理器
     *
     * @param pUrlMap url
     *
     * @throws APPErrorException
     */
    public void removeWebsocket(String pUrlMap) throws APPErrorException {
        if (null == mSimpleUrlHandlerMapping) {
            throw new APPErrorException("Spring Websocket没启用");
        }

        try {
            Field field = SimpleUrlHandlerMapping.class.getSuperclass().getDeclaredField("handlerMap");
            if (null == field) {
                throw new APPErrorException("SimpleUrlHandlerMapping 无效无法注销Websocket");
            }
            field.setAccessible(true);
            Map<String, Object> map = (Map<String, Object>) field.get(mSimpleUrlHandlerMapping);
            Object obj=map.get(pUrlMap);
            if(obj instanceof Closeable)
            {
                try{((Closeable)obj).close();}
                catch (Exception e){}
            }
            else if(obj instanceof IDispose)
            {
                ((IDispose)obj).Dispose();
            }
            obj=null;
            map.remove(pUrlMap);
        } catch (Exception e) {
            if (e instanceof APPErrorException) {
                throw (APPErrorException) e;
            }
            throw new APPErrorException("删除失败；原因：" + e.getMessage(), e);
        }
    }

    /**
     * 获取 BeanFactory
     *
     * @param context
     */
    @Override
    protected void initStrategies(ApplicationContext context) {
        mContext = (AbstractRefreshableWebApplicationContext) context;
        mBeanFactory = (org.springframework.beans.factory.support.DefaultListableBeanFactory) mContext.getBeanFactory();
        mMapper = (org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping) mBeanFactory.getBean(org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping.class);
        super.initStrategies(context);
        PlatformDispatcherServletFactory.addDispatchers(this.getServletName(), this);
        if(mBeanFactory.containsBeanDefinition("webSocketHandlerMapping")) {
            mSimpleUrlHandlerMapping = (SimpleUrlHandlerMapping) mBeanFactory.getBean("webSocketHandlerMapping");
        }
        else if(tgtools.web.platform.Platform.getBeanFactory().containsBeanDefinition("webSocketHandlerMapping"))
        {
            mSimpleUrlHandlerMapping = (SimpleUrlHandlerMapping) tgtools.web.platform.Platform.getBeanFactory().getBean("webSocketHandlerMapping");
        }
        sendMessage();
    }

    private void sendMessage() {
        try {
            JSONObject json = new JSONObject();
            json.put("ApplicationName", mContext.getApplicationName());
            json.put("DisplayName", mContext.getDisplayName());
            json.put("ServletName", mContext.getServletConfig().getServletName());
            Message message = new Message();
            message.setEvent("addDispatcherServlet");
            message.setContent(json.toString());
            message.setSender("PlatformDispatcherServlet");

            tgtools.message.MessageFactory.sendMessage(message);
        } catch (APPErrorException e) {
            LogHelper.error("系统", "发送消息失败！原因：" + e.getMessage(), "PlatformDispatcherServlet", e);
        }

    }
}
