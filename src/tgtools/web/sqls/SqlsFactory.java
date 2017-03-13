package tgtools.web.sqls;


import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.PropertiesObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：8:38
 */
public class SqlsFactory {

    public static <T>T getSQLs(T p_T) {
        String dbtype = tgtools.db.DataBaseFactory.getDefault().getDataBaseType();
        LogHelper.info("", "dbtype:" + dbtype, "SqlsFactory.getSQLs");
        try {
            PropertiesObject properties = loadSqlFile(dbtype,p_T.getClass());
            LogHelper.info("", "读取sql文件成功", "SqlsFactory.getSQLs");
            return properties.convert(p_T);
        } catch (APPErrorException e) {
            LogHelper.error("", "转换BaseViewSqls失败：" + e.getMessage(), "SqlsFactory.getSQLs", e);
            return null;
        }

    }

    public static PropertiesObject loadSqlFile(String p_DBType,Class p_Class) throws APPErrorException {
        PropertiesObject properties = new PropertiesObject();

        String defaultfile = "/spring-sql-base.xml";
        String dbfile = "/spring-sql-" + p_DBType + ".xml";
        loadFile(properties, defaultfile,p_Class);
        loadFile(properties, dbfile,p_Class);

        return properties;
    }

    private static void loadFile(PropertiesObject properties, String p_File,Class p_Class) throws APPErrorException {
        try {
            InputStream defaultStream = getResource(p_File,p_Class);
            if (null != defaultStream) {
                properties.loadFromXML(defaultStream);
                defaultStream.close();
            } else {
                LogHelper.info("", "读取sql文件失败：" + p_File, "SqlsFactory.getSQLs");
            }

        } catch (IOException e) {
            throw new APPErrorException("获取资源文件出错：" + p_File + "  :" + e.getMessage(), e);
        }
    }

    private static InputStream getResource(String p_Url,Class p_Class) {
        return p_Class.getResourceAsStream(p_Url);
    }




}
