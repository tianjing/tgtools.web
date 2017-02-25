package tgtools.web.platform;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import tgtools.cache.CacheFactory;
import tgtools.db.DataBaseFactory;
import tgtools.log.LoggerFactory;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;
import tgtools.web.rests.entity.SettingsEntity;

import java.net.MalformedURLException;
import java.net.URL;

public class Platform {
    private static ApplicationContext m_ApplicationContext;
    private static SettingsEntity m_Settings;
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

            try {
                Object objsetting = p_Context.getBean("SettingsEntity");
                if (null != objsetting && objsetting instanceof SettingsEntity) {
                    m_Settings = (SettingsEntity) objsetting;
                    LogHelper.info("", "配置信息加载成功", "startup");
                }
            } catch (Exception e) {
                LogHelper.info("", "配置信息加载失败，可能影响运行", "startup");
            }

        } catch (Exception e) {

            LogHelper.error("数据源加载失败", "", "", e);
        }

    }

    public static ApplicationContext getApplicationContext() {
        return m_ApplicationContext;
    }

    public static DefaultListableBeanFactory getBeanFactory() {
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
        if (getBeanFactory().containsBean(p_Name)) {
            getBeanFactory().removeBeanDefinition(p_Name);
        }
        BeanDefinitionBuilder dataSourceBuider = BeanDefinitionBuilder.genericBeanDefinition(p_Class);
        dataSourceBuider.setScope(p_Scope);
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
     * 获取设置
     *
     * @param p_Key
     * @return
     */
    public static String getSetting(String p_Key) {

        if (null != m_Settings && m_Settings.getConfigs().containsKey(p_Key)) {
            return m_Settings.getConfigs().get(p_Key);
        }
        return StringUtil.EMPTY_STRING;
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

    public static void main(String[] args) {
        System.out.println("1111:" + getServerPath());
    }
}
