package tgtools.web.platform;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：9:02
 */
public class PlatformDispatcherServletFactory {

    private static LinkedHashMap<String,PlatformDispatcherServlet> m_Dispatchers =new LinkedHashMap<String, PlatformDispatcherServlet>();

    /**
     * 添加rest容器
     * @param p_ServletName 配置文件中 ServletName
     * @param p_PlatformDispatcherServlet
     */
    static void addDispatchers(String p_ServletName,PlatformDispatcherServlet p_PlatformDispatcherServlet)
    {
        if(!m_Dispatchers.containsKey(p_ServletName))
        {
            m_Dispatchers.put(p_ServletName,p_PlatformDispatcherServlet);
        }
    }

    /**
     * 获取默认的rest容器（第一个）
     * @return
     */
    public static PlatformDispatcherServlet getDefaultDispatcher()
    {
        for(Map.Entry<String,PlatformDispatcherServlet> item:m_Dispatchers.entrySet())
        {
            if(null!=item.getValue())
            return item.getValue();
        }

        return null;
    }

    /**
     * 根据名称获取rest容器
     * @param p_ServletName 配置文件中 ServletName
     * @return
     */
    public static PlatformDispatcherServlet getDispatcher(String p_ServletName)
    {
        if(m_Dispatchers.containsKey(p_ServletName))
        {
            return m_Dispatchers.get(p_ServletName);
        }
        return null;
    }

}
