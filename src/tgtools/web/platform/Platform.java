package tgtools.web.platform;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import tgtools.cache.CacheFactory;
import tgtools.db.DataBaseFactory;
import tgtools.exceptions.APPErrorException;
import tgtools.log.LoggerFactory;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;
import tgtools.web.log.Log4jFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class Platform {
    private static ApplicationContext m_ApplicationContext;
    private static DefaultListableBeanFactory m_BeanFactory;
    private static String m_BasePath;
    private static String m_ContextPath;
    public  static final String Scope_Singleton="singleton";
    public  static final String Scope_Prototype="prototype";
    public  static final String Scope_Request="request";
    public  static final String Scope_Session="session";
    public  static final String Scope_GlobalSession="globalSession";

    /**
     * 平台启动
     *
     * @param p_Context
     */
    public static void startup(ApplicationContext p_Context) {
        if (m_ApplicationContext != p_Context) {
            m_ApplicationContext = p_Context;
            LogHelper.info("", "ApplicationContext:" + p_Context.getClass(), "");
        }//org.springframework.web.context.support.XmlWebApplicationContext
        try {
            setServerPath(p_Context);

            Log4jFactory.start();

            LoggerFactory.getDefault().info("DataBaseFactory load");
            String[] beannames = m_ApplicationContext.getBeanDefinitionNames();
            for (String name : beannames) {
                if (!StringUtil.isNullOrEmpty(name)
                        && name.toUpperCase().contains("DATASOURCE")) {
                    LoggerFactory.getDefault().info(
                            "DataBaseFactory load:" + name);
                    DataBaseFactory.add("Spring" + name, m_ApplicationContext, name);
                } else if (!StringUtil.isNullOrEmpty(name)
                        && name.toUpperCase().contains("RPCSOURCE")) {
                    LoggerFactory.getDefault().info(
                            "DataBaseFactory load:" + name);
                    DataBaseFactory.add(name, m_ApplicationContext.getBean(name));
                }
                else if (!StringUtil.isNullOrEmpty(name)
                        && name.toUpperCase().contains("JNDISOURCE")) {
                    LoggerFactory.getDefault().info(
                            "DataBaseFactory load:" + name);
                    DataBaseFactory.add(name, m_ApplicationContext.getBean(name));
                }
                else if (!StringUtil.isNullOrEmpty(name)
                        && name.toUpperCase().contains("DATAACCESS")) {
                    LoggerFactory.getDefault().info(
                            "DataBaseFactory load:" + name);
                    DataBaseFactory.add(name, m_ApplicationContext.getBean(name));
                }
            }
            LoggerFactory.getDefault().info("DataBaseFactory 初始化完毕========");
            getBeanFactory();
            LoggerFactory.getDefault().info("CacheFactory load");
            try {
                CacheFactory.init(new URL("file:" + getServerPath() + "WEB-INF/ehcache.xml"));
                LoggerFactory.getDefault().info("CacheFactory 初始化完毕========");
            } catch (MalformedURLException e1) {
                LoggerFactory.getDefault().error("CacheFactory 初始化失败========", e1);
            }

            String path = getServerPath() + "WEB-INF/Plugins/";
            LoggerFactory.getDefault().info("插件地址 ：" + path);
            tgtools.plugin.PluginFactory.startup(path);
            LoggerFactory.getDefault().info("plugin 初始化完毕========");

            tgtools.service.ServiceFactory.start();
            LoggerFactory.getDefault().info("services 初始化完毕========");

            tgtools.message.MessageFactory.start();
            LoggerFactory.getDefault().info("Message 初始化完毕========");


        } catch (Exception e) {

            LogHelper.error("数据源加载失败", "", "", e);
        }

    }

    public static ApplicationContext getApplicationContext() {
        return m_ApplicationContext;
    }

    public static DefaultListableBeanFactory getBeanFactory() {
        if(null==getApplicationContext())
        {
            org.springframework.context.support.GenericApplicationContext dd=new org.springframework.context.support.GenericApplicationContext();
            m_ApplicationContext=dd;

        }
        if (null == m_BeanFactory && null != getApplicationContext()) {
            if (getApplicationContext().getAutowireCapableBeanFactory() instanceof DefaultListableBeanFactory) {
                m_BeanFactory = (DefaultListableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory();
                LogHelper.info("", "BeanFactory:" + m_BeanFactory.getClass(), "Platform.getBeanFactory");
            }
        }
        return m_BeanFactory;
    }
    /**
     * 获取bean
     *
     * @param p_Name
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String p_Name) {
        if (null != getBeanFactory() && getBeanFactory().containsBean(p_Name)) {
            Object obj = getBeanFactory().getBean(p_Name);
            return null != obj ? (T) obj : null;
        }
        return null;
    }

    /**
     * 更新添加Bean，如果存在则替换
     *
     * @param p_Name
     * @param p_Class
     */
    public static void addBean(String p_Name, Class p_Class) {
        if (getBeanFactory().containsBean(p_Name)) {
            getBeanFactory().removeBeanDefinition(p_Name);
        }
        BeanDefinitionBuilder dataSourceBuider = BeanDefinitionBuilder.genericBeanDefinition(p_Class);
        getBeanFactory().registerBeanDefinition(p_Name, dataSourceBuider.getBeanDefinition());
    }
    /**
     * 更新添加Bean，如果存在则替换
     *
     * @param p_Name
     * @param p_Class
     * @param p_Scope 影响范围 参看 Platform。Scope_
     */
    public static void addBean(String p_Name, Class p_Class,String p_Scope) {
        addBean(p_Name,p_Class,p_Scope,null);
    }

    /**
     *  更新添加Bean，如果存在则替换
     * @param p_Name bean 名称
     * @param p_Class 类型
     * @param p_Scope 作用范围 参照：Platform。Scope_ 的常量
     * @param p_DestroyMethodName 销毁时方法
     */
    public static void addBean(String p_Name, Class p_Class,String p_Scope,String p_DestroyMethodName) {
        if (getBeanFactory().containsBean(p_Name)) {
            getBeanFactory().removeBeanDefinition(p_Name);
        }
        BeanDefinitionBuilder dataSourceBuider = BeanDefinitionBuilder.genericBeanDefinition(p_Class);
        dataSourceBuider.setScope(p_Scope);
        if(!StringUtil.isNullOrEmpty(p_DestroyMethodName)) {
            dataSourceBuider.setDestroyMethodName(p_DestroyMethodName);
        }
        getBeanFactory().registerBeanDefinition(p_Name, dataSourceBuider.getBeanDefinition());

    }
        /**
         * 更新添加Bean，如果存在则替换
         *
         * @param p_BeanDefinitionBuilder 配置信息
         */
    public static void addBean(String p_Name,BeanDefinitionBuilder p_BeanDefinitionBuilder) {
        if (getBeanFactory().containsBean(p_Name)) {
            getBeanFactory().removeBeanDefinition(p_Name);
        }
        getBeanFactory().registerBeanDefinition(p_Name, p_BeanDefinitionBuilder.getBeanDefinition());
    }

    /**
     * 更新添加Bean，如果存在则替换
     * @param p_Name
     * @param p_BeanDefinition
     */
    public static void addBean(String p_Name,BeanDefinition p_BeanDefinition) {
        if (getBeanFactory().containsBean(p_Name)) {
            getBeanFactory().removeBeanDefinition(p_Name);
        }
        getBeanFactory().registerBeanDefinition(p_Name,p_BeanDefinition);
    }

    /**
     * 获取服务路径
     *
     * @return
     */
    public static String getServerPath() {
        if (StringUtil.isNullOrEmpty(m_BasePath)) setServerPath(null);
        return m_BasePath;
    }

    /**
     * 获取项目名称 如 /EmptyProject
     * @return
     */
    public static String getContextPath() {
        if (StringUtil.isNullOrEmpty(m_ContextPath)) setServerPath(null);
        return m_ContextPath;
    }
    private static void setServerPath(ApplicationContext p_Context) {
        if (p_Context instanceof org.springframework.web.context.support.AbstractRefreshableWebApplicationContext) {
            m_BasePath = ((org.springframework.web.context.support.AbstractRefreshableWebApplicationContext) p_Context).getServletContext().getRealPath("/");
            m_ContextPath=((org.springframework.web.context.support.AbstractRefreshableWebApplicationContext) p_Context).getServletContext().getContextPath();
            if (!StringUtil.isNullOrEmpty(m_BasePath)) {
                m_BasePath += "/";
            }
            LogHelper.info("", m_BasePath, "getRealPath");
            LogHelper.info("", m_ContextPath, "m_ContextPath");
        } else {
            String path = Platform.class.getResource("").toString();

            if (path.indexOf("file:/") >= 0) {
                path = "/" + path.substring(path.indexOf("file:/") + 6);
            } else {
                path = path.substring(path.indexOf(":") + 1);
            }
            m_BasePath = path.substring(0, path.indexOf("/WEB-INF") + 1);
            LogHelper.info("", m_BasePath, "Platform.getServerPath");
        }
    }

    /**
     * 动态添加 rest URL  </br>
     * 添加的类 具有 @RequestMapping
     * @param p_BeanName bean的名称
     * @param p_BeanDefinition bean配置
     * @throws APPErrorException
     */
    public static void addRest(String p_BeanName, BeanDefinition p_BeanDefinition) throws APPErrorException {
        PlatformDispatcherServlet.addRest(p_BeanName,p_BeanDefinition);
    }

    /**
     * 动态添加 rest URL  </br>
     * 添加的类 具有 @RequestMapping
     * @param p_BeanName bean的名称
     * @param p_Class 类型
     * @throws APPErrorException
     */
    public static void addRest(String p_BeanName, Class<?> p_Class) throws APPErrorException {
        PlatformDispatcherServlet.addRest(p_BeanName,p_Class);
    }

    /**
     *  移除动态添加的rest
     * @param p_BeanName bean的名称
     * @throws APPErrorException
     */
    public static void removeRest(String p_BeanName) throws APPErrorException {
        PlatformDispatcherServlet.removeRest(p_BeanName);
    }





}
