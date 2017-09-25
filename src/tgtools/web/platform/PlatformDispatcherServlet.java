package tgtools.web.platform;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import tgtools.exceptions.APPErrorException;

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
    public static void Valid() throws APPErrorException
    {
        if(null==m_context||null==m_BeanFactory||null==m_Mapper)
        {
            throw new APPErrorException("无法获取RestBeanFactory，请检查web.xml RestServlet是否配置为tgtools.web.platform.PlatformDispatcherServlet");
        }
    }
    public static void addUrlMapping(String p_BeanName, BeanDefinition p_BeanDefinition) throws APPErrorException {
        Valid();
        m_BeanFactory.registerBeanDefinition(p_BeanName, p_BeanDefinition);
        m_Mapper.afterPropertiesSet();
    }
    public static void addUrlMapping(String p_BeanName, Class<?> p_Class) throws APPErrorException {
        Valid();
        BeanDefinitionBuilder dataSourceBuider = BeanDefinitionBuilder.genericBeanDefinition(p_Class);
        m_BeanFactory.registerBeanDefinition(p_BeanName, dataSourceBuider.getBeanDefinition());
        m_Mapper.afterPropertiesSet();
    }
    public static void removeUrlMapping(String p_BeanName) throws APPErrorException {
        Valid();
        m_BeanFactory.removeBeanDefinition(p_BeanName);
        m_Mapper.afterPropertiesSet();
    }
    @Override
    protected void initStrategies(ApplicationContext context) {
        m_context = (org.springframework.web.context.support.XmlWebApplicationContext) context;
        m_BeanFactory = (org.springframework.beans.factory.support.DefaultListableBeanFactory) m_context.getBeanFactory();
        m_Mapper = (org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping) m_BeanFactory.getBean("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#0");
        super.initStrategies(context);
    }


}
