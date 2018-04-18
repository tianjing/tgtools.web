package tgtools.web.sqls;


import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.PropertiesObject;
import tgtools.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * 名  称： 一个多数据类型兼容的方式
 * 将加载T 的包下sql 文件 先加载通用的，后加载差异的。
 * 编写者：田径
 * 功  能：
 * 时  间：8:38
 */
public class SqlsFactory {

    /**
     * 加载sql
     * @param p_T
     * @param <T>
     * @return
     */
    public static <T> T getSQLs(T p_T) {
        String name = StringUtil.replace(p_T.getClass().getName(), ".", "/");
        name = StringUtil.replace(name, p_T.getClass().getSimpleName(), "");
        LogHelper.info("", "name:" + name, "SqlsFactory.getSQLs");
        return getSQLs(tgtools.db.DataBaseFactory.getDefault().getDataBaseType(), p_T, name);
    }

    /**
     * 加载sql
     * @param p_DBType
     * @param p_T
     * @param <T>
     * @return
     */
    public static <T> T getSQLs(String p_DBType, T p_T) {
        String name = StringUtil.replace(p_T.getClass().getName(), ".", "/");
        name = StringUtil.replace(name, p_T.getClass().getSimpleName(), "");
        LogHelper.info("", "name:" + name, "SqlsFactory.getSQLs");
        return getSQLs(p_DBType, p_T, name);
    }

    /**
     * 加载sql
     * @param p_DBType
     * @param p_T
     * @param p_ResUrl
     * @param <T>
     * @return
     */
    private static <T> T getSQLs(String p_DBType, T p_T, String p_ResUrl) {
        LogHelper.info("", "dbtype:" + p_DBType, "SqlsFactory.getSQLs");
        try {
            PropertiesObject properties = loadSqlFile(p_DBType, p_ResUrl);
            return properties.convert(p_T);
        } catch (APPErrorException e) {
            LogHelper.error("", "转换BaseViewSqls失败：" + e.getMessage(), "SqlsFactory.getSQLs", e);
            return null;
        }

    }

    /**
     * 加载sql 文件 分析 差异的sql 文件名
     * @param p_DBType
     * @param p_ResUrl
     * @return
     * @throws APPErrorException
     */
    private static PropertiesObject loadSqlFile(String p_DBType, String p_ResUrl) throws APPErrorException {
        PropertiesObject properties = new PropertiesObject();

        String defaultfile = "sql-base.xml";
        loadFile(properties, p_ResUrl + defaultfile);
        if (!StringUtil.isNullOrEmpty(p_DBType)) {
            String dbfile = "sql-" + p_DBType + ".xml";
            loadFile(properties, p_ResUrl + dbfile);
        }
        return properties;
    }

    /**
     * 加载sql 文件
     * @param properties
     * @param p_File
     * @throws APPErrorException
     */
    private static void loadFile(PropertiesObject properties, String p_File) throws APPErrorException {
        try {
            InputStream defaultStream = getResource(p_File);
            if (null != defaultStream) {
                properties.loadFromXML(defaultStream);
                defaultStream.close();
                LogHelper.info("", "读取sql文件成功：文件：" + p_File, "SqlsFactory.getSQLs");
            } else {
                LogHelper.info("", "读取sql文件失败：" + p_File, "SqlsFactory.getSQLs");
            }

        } catch (IOException e) {
            throw new APPErrorException("获取资源文件出错：" + p_File + "  :" + e.getMessage(), e);
        }
    }

    /**
     * 获取sql xml 资源
     * @param p_Url
     * @return
     */
    private static InputStream getResource(String p_Url) {

        return SqlsFactory.class.getClassLoader().getResourceAsStream(p_Url);

    }

    public static void main(String[] args) {
        String name = StringUtil.replace(SqlsFactory.class.getName(), ".", "/");
        name = StringUtil.replace(name, SqlsFactory.class.getSimpleName(), "");
        System.out.println(name);

    }
}
