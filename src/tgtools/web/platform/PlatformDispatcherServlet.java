package tgtools.web.platform;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import tgtools.exceptions.APPErrorException;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：9:22
 */
@Controller
public class PlatformDispatcherServlet extends DispatcherServlet {

    private static org.springframework.web.context.support.XmlWebApplicationContext m_context;
    private static org.springframework.beans.factory.support.DefaultListableBeanFactory m_BeanFactory;
    private static org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping m_Mapper;

    /**
     * 验证
     * @throws APPErrorException
     */
    private static void Valid() throws APPErrorException {
        if (null == m_context || null == m_BeanFactory || null == m_Mapper) {
            throw new APPErrorException("无法获取RestBeanFactory，请检查web.xml RestServlet是否配置为tgtools.web.platform.PlatformDispatcherServlet");
        }
    }

    /**
     * 添加 URL 映射
     * @param p_BeanName
     * @param p_BeanDefinition
     * @throws APPErrorException
     */
    public static void addRest(String p_BeanName, BeanDefinition p_BeanDefinition) throws APPErrorException {
        Valid();
        m_BeanFactory.registerBeanDefinition(p_BeanName, p_BeanDefinition);
        m_Mapper.afterPropertiesSet();
    }

    /**
     * 添加 URL 映射
     * @param p_BeanName
     * @param p_Class
     * @throws APPErrorException
     */
    public static void addRest(String p_BeanName, Class<?> p_Class) throws APPErrorException {
        Valid();
        BeanDefinitionBuilder dataSourceBuider = BeanDefinitionBuilder.genericBeanDefinition(p_Class);
        m_BeanFactory.registerBeanDefinition(p_BeanName, dataSourceBuider.getBeanDefinition());
        m_Mapper.afterPropertiesSet();
    }

    /**
     * 移除 URL 映射
     * @param p_BeanName
     * @throws APPErrorException
     */
    public static void removeRest(String p_BeanName) throws APPErrorException {
        Valid();
        m_BeanFactory.removeBeanDefinition(p_BeanName);
        removeUrl(p_BeanName);
        //m_Mapper.afterPropertiesSet();
    }

    /**
     *
     * @return
     */
    private static LinkedHashMap<RequestMappingInfo, HandlerMethod> getHandlerMethods() {
        Field field = null;
        try {
            field = m_Mapper.getClass().getSuperclass().getSuperclass().getDeclaredField("handlerMethods");
            field.setAccessible(true);
            return (LinkedHashMap) field.get(m_Mapper);

        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 获取 Mapping
     * @return
     */
    private static LinkedMultiValueMap<String, RequestMappingInfo> getUrlMap() {
        try {
            Field field1 = m_Mapper.getClass().getSuperclass().getSuperclass().getDeclaredField("urlMap");
            field1.setAccessible(true);
            return (LinkedMultiValueMap) field1.get(m_Mapper);
        } catch (Exception e) {

        }
        return null;
    }

    /**
     *
     * @return
     */
    private static LinkedMultiValueMap<String, HandlerMethod> getNameMap() {
        try {
            Field field2 = m_Mapper.getClass().getSuperclass().getSuperclass().getDeclaredField("nameMap");
            field2.setAccessible(true);
            return (LinkedMultiValueMap) field2.get(m_Mapper);
        } catch (Exception e) {

        }
        return null;
    }

    /**
     *
     * @param p_BeanName
     */
    private static void removeUrl(String p_BeanName) {
        LinkedHashMap<RequestMappingInfo, HandlerMethod> handles=getHandlerMethods();
        LinkedMultiValueMap<String, RequestMappingInfo> urlMap =getUrlMap();
        LinkedMultiValueMap<String, HandlerMethod> nameMap=getNameMap();
        ArrayList<RequestMappingInfo> list =new ArrayList<RequestMappingInfo>();
        for(Map.Entry<RequestMappingInfo, HandlerMethod> item : handles.entrySet())
        {
            if( item.getValue().getBean().equals(p_BeanName))
            {
                list.add(item.getKey());
                removeUrlMap(urlMap,item.getKey());
                removeNamelMap(nameMap,item.getValue());
            }
        }
        if(list.size()>0)
        {
            for(int i=0;i<list.size();i++)
            {
                handles.remove(list.get(i));
            }
        }
    }

    /**
     *
     * @param p_NameMap
     * @param p_Method
     */
    private static void removeNamelMap(LinkedMultiValueMap<String, HandlerMethod> p_NameMap,HandlerMethod p_Method)
    {
        ArrayList<String> list =new ArrayList<String>();
        for(Map.Entry<String, List<HandlerMethod>> item : p_NameMap.entrySet())
        {
            if(item.getValue().contains(p_Method))
            {
                list.add(item.getKey());
            }
        }
        if(list.size()>0)
        {
            for(int i=0;i<list.size();i++)
            {
                List<HandlerMethod> methods= p_NameMap.get(list.get(i));
                if(methods.size()>1)
                {methods.remove(p_Method);}
                else
                {
                    p_NameMap.remove(list.get(i));
                }
            }
        }
    }

    /**
     * removeUrlMap
     * @param p_UrlMap
     * @param p_Info
     */
    private static void removeUrlMap(LinkedMultiValueMap<String, RequestMappingInfo> p_UrlMap,RequestMappingInfo p_Info) {
        ArrayList<String> list =new ArrayList<String>();
        for(Map.Entry<String, List<RequestMappingInfo>> item : p_UrlMap.entrySet())
        {
            if(item.getValue().contains(p_Info))
            {
                list.add(item.getKey());
            }
        }
        if(list.size()>0)
        {
            for(int i=0;i<list.size();i++)
            {
                List<RequestMappingInfo> infos= p_UrlMap.get(list.get(i));
                if(infos.size()>1)
                {infos.remove(p_Info);}
                else
                {
                    p_UrlMap.remove(list.get(i));
                }
            }
        }




    }

    /**
     * 获取 BeanFactory
     * @param context
     */
    @Override
    protected void initStrategies(ApplicationContext context) {
        m_context = (org.springframework.web.context.support.XmlWebApplicationContext) context;
        m_BeanFactory = (org.springframework.beans.factory.support.DefaultListableBeanFactory) m_context.getBeanFactory();
        m_Mapper = (org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping) m_BeanFactory.getBean("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#0");
        super.initStrategies(context);
    }


}
