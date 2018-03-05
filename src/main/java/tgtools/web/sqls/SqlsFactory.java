package tgtools.web.sqls;


import tgtools.db.IDataAccess;
import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.PropertiesObject;
import tgtools.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：8:38
 */
public class SqlsFactory {

    public static <T> T getSQLs(T p_T) {
        String name = StringUtil.replace(p_T.getClass().getName(), ".", "/");
        name = StringUtil.replace(name, p_T.getClass().getSimpleName(), "");
        LogHelper.info("", "name:" + name, "SqlsFactory.getSQLs");
        return getSQLs(tgtools.db.DataBaseFactory.getDefault().getDataBaseType(),p_T, name);
    }
    public static <T> T getSQLs(IDataAccess p_DataAccess, T p_T) {
        String name = StringUtil.replace(p_T.getClass().getName(), ".", "/");
        name = StringUtil.replace(name, p_T.getClass().getSimpleName(), "");
        LogHelper.info("", "name:" + name, "SqlsFactory.getSQLs");
        return getSQLs(p_DataAccess.getDataBaseType(),p_T, name);
    }

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

    private static PropertiesObject loadSqlFile(String p_DBType, String p_ResUrl) throws APPErrorException {
        PropertiesObject properties = new PropertiesObject();

        String defaultfile = "sql-base.xml";
        loadFile(properties, p_ResUrl + defaultfile);
        if(!StringUtil.isNullOrEmpty(p_DBType)) {
            String dbfile = "sql-" + p_DBType + ".xml";
            loadFile(properties, p_ResUrl + dbfile);
        }
        return properties;
    }

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

    private static InputStream getResource(String p_Url) {

        return SqlsFactory.class.getClassLoader().getResourceAsStream(p_Url);

    }

    public static void main(String[] args) {
        String name = StringUtil.replace(SqlsFactory.class.getName(), ".", "/");
        name = StringUtil.replace(name, SqlsFactory.class.getSimpleName(), "");
        System.out.println(name);

    }
}
