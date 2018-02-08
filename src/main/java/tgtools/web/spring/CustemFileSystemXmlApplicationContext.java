package tgtools.web.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：15:27
 */
public class CustemFileSystemXmlApplicationContext  extends FileSystemXmlApplicationContext{

    private CustemFileSystemXmlApplicationContext(){}

    protected DefaultListableBeanFactory _getBeanFactory()
    {
        return (DefaultListableBeanFactory)this.getBeanFactory();
    }

    /**
     * 初始化
     * @param p_FilePath
     * @param p_ClassLoader
     */
    public void init(String p_FilePath,ClassLoader p_ClassLoader)
    {
        this.setConfigLocation(p_FilePath);
        this.setClassLoader(p_ClassLoader);
        this.refresh();
    }

    /**
     * 初始化
     * @param p_FilePath
     */
    public void init(String p_FilePath) {
        init(p_FilePath,CustemFileSystemXmlApplicationContext.class.getClassLoader());
    }

    /**
     * 添加bean
     * @param p_BeanName
     * @param p_BeanDefinition
     */
    public void addBean(String p_BeanName,BeanDefinition p_BeanDefinition)
    {
        if(!_getBeanFactory().containsBeanDefinition(p_BeanName)) {
            _getBeanFactory().registerBeanDefinition(p_BeanName, p_BeanDefinition);
        }
    }

    /**
     * 添加bean
     * @param p_BeanName
     * @param p_Class
     */
    public void addBean(String p_BeanName,Class<?> p_Class)
    {
        BeanDefinitionBuilder beanDefinitionBuilder=BeanDefinitionBuilder.genericBeanDefinition(p_Class);
        this.addBean(p_BeanName,beanDefinitionBuilder.getBeanDefinition());
    }

    /**
     * 移除已注册的bean
     * @param p_BeanName
     */
    public void removeBean(String p_BeanName)
    {
        if( _getBeanFactory().containsBeanDefinition(p_BeanName)) {
            _getBeanFactory().removeBeanDefinition(p_BeanName);
        }
    }

    /**
     * 创建 spring 容器
     * @param p_FilePath 配置文件全路径（如：C:/Myfile/spring.xml)
     * @param p_ClassLoader
     * @return
     */
    public static CustemFileSystemXmlApplicationContext create(String p_FilePath,ClassLoader p_ClassLoader)
    {
        CustemFileSystemXmlApplicationContext context =new CustemFileSystemXmlApplicationContext();
        context.init(p_FilePath,p_ClassLoader);

        return context;
    }

    /**
     * 创建 spring 容器
     * @param p_FilePath 配置文件全路径（如：C:/Myfile/spring.xml)
     * @return
     */
    public static CustemFileSystemXmlApplicationContext create(String p_FilePath)
    {
        CustemFileSystemXmlApplicationContext context =new CustemFileSystemXmlApplicationContext();
        context.init(p_FilePath);
        return context;
    }
}
