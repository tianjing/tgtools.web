package tgtools.web.util;

import tgtools.data.DataTable;
import tgtools.db.IDataAccess;
import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;
import tgtools.web.entity.BSGridDataEntity;
import tgtools.web.sqls.BaseViewSqls;
import tgtools.web.sqls.SqlsFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分页工具类 支持 多数据库类型 通用,db2,dm7,dm6,h2,mysql
 */
public class PageSqlUtil {

    private static Map<String, BaseViewSqls> mSqls = new ConcurrentHashMap<String, BaseViewSqls>();

    public static BaseViewSqls getPageSqls(String pType) {
        if (!mSqls.containsKey(pType)) {
            mSqls.put(pType, SqlsFactory.getSQLs(pType, new BaseViewSqls()));
        }
        return mSqls.get(pType);
    }

    /**
     * 根据数据sql 获取分页sql
     *
     * @param p_SQL      数据sql
     * @param p_CurrPage 当前页
     * @param p_PageSize 每页多少数据
     * @return
     */
    public static String getPageDataLimitSQL(String p_SQL, String p_CurrPage,
                                             String p_PageSize) {
        return getPageDataLimitSQL(tgtools.db.DataBaseFactory.getDefault(), p_SQL, p_CurrPage, p_PageSize);
    }

    /**
     * 根据数据源获取不同的sql
     *
     * @param p_DataAccess
     * @return
     */
    public static String getPageDataLimitSQL(IDataAccess p_DataAccess, String p_SQL, String p_CurrPage,
                                             String p_PageSize) {
        return getPageDataLimitSQL(p_DataAccess.getDataBaseType(), p_SQL, p_CurrPage, p_PageSize);
    }

    /**
     * 根据数据源获取不同的sql
     *
     * @param pType
     * @return
     */
    public static String getPageDataLimitSQL(String pType, String p_SQL, String p_CurrPage,
                                             String p_PageSize) {
        BaseViewSqls sqls = getPageSqls(pType);
        String sql = sqls.Page_GetPageData_Limit_SQL;
        sql = StringUtil.replace(sql, "${sql}", p_SQL);

        int pageIndex = Integer.valueOf(p_CurrPage);
        int pageSize = Integer.valueOf(p_PageSize);
        if (pageIndex < 1) {
            pageIndex = 1;
        }
        pageIndex = (pageIndex - 1) * pageSize;

        sql = StringUtil.replace(sql, "{currParge}", String.valueOf(pageIndex));
        sql = StringUtil.replace(sql, "{pargeSize}", p_PageSize);
        return sql;
    }


    /**
     * 根据数据sql 获取分页sql
     *
     * @param p_SQL      数据sql
     * @param p_CurrPage 当前页
     * @param p_PageSize 每页多少数据
     * @return
     */
    public static String getPageDataSQL(String p_SQL, String p_CurrPage,
                                        String p_PageSize) {
        return getPageDataSQL(tgtools.db.DataBaseFactory.getDefault(), p_SQL, p_CurrPage, p_PageSize);
    }

    /**
     * 根据数据源获取不同的sql
     *
     * @param pType
     * @return
     */
    public static String getPageDataSQL(String pType, String p_SQL, String p_CurrPage,
                                        String p_PageSize) {
        BaseViewSqls sqls = getPageSqls(pType);
        String sql = sqls.Page_GetPageData_SQL;
        sql = StringUtil.replace(sql, "${sql}", p_SQL);

        sql = StringUtil.replace(sql, "{currParge}", "0" == p_CurrPage ? p_CurrPage : String.valueOf(Integer.valueOf(p_CurrPage)));
        sql = StringUtil.replace(sql, "{pargeSize}", p_PageSize);
        return sql;
    }

    /**
     * 根据数据源获取不同的sql
     *
     * @param p_DataAccess
     * @return
     */
    public static String getPageDataSQL(IDataAccess p_DataAccess, String p_SQL, String p_CurrPage,
                                        String p_PageSize) {
        return getPageDataSQL(p_DataAccess.getDataBaseType(), p_SQL, p_CurrPage, p_PageSize);
    }

    /**
     * 根据数据sql 获取分页数据 没有NUM列
     *
     * @param p_SQL      数据sql
     * @param p_CurrPage 当前页
     * @param p_PageSize 每页多少数据
     * @return
     * @throws APPErrorException
     */
    public static DataTable getNoNumPageData(String p_SQL, String p_CurrPage,
                                             String p_PageSize) throws APPErrorException {
        DataTable dt = getPageData(p_SQL, p_CurrPage, p_PageSize);
        dt.removeColumn("NUM");
        return dt;
    }

    /**
     * 根据数据sql 获取分页数据 没有NUM列
     *
     * @param p_DataAccess 数据源
     * @param p_SQL        p_SQL 数据sql
     * @param p_CurrPage   当前页
     * @param p_PageSize   每页多少数据
     * @return
     * @throws APPErrorException
     */
    public static DataTable getNoNumPageData(IDataAccess p_DataAccess, String p_SQL, String p_CurrPage,
                                             String p_PageSize) throws APPErrorException {
        DataTable dt = getPageData(p_DataAccess, p_SQL, p_CurrPage, p_PageSize);
        dt.removeColumn("NUM");
        return dt;
    }

    /**
     * 根据数据sql 获取分页数据
     *
     * @param p_DataAccess 数据源
     * @param p_SQL        数据sql
     * @param p_CurrPage   当前页
     * @param p_PageSize   每页多少数据
     * @return
     */
    public static DataTable getPageData(IDataAccess p_DataAccess, String p_SQL, String p_CurrPage,
                                        String p_PageSize) throws APPErrorException {
        String sql = getPageDataSQL(p_DataAccess, p_SQL, p_CurrPage, p_PageSize);
        LogHelper.info("", sql, "");
        return p_DataAccess.Query(sql);
    }

    /**
     * 使用默认数据源 根据数据sql 获取分页数据
     *
     * @param p_SQL      数据sql
     * @param p_CurrPage 当前页
     * @param p_PageSize 每页多少数据
     * @return
     */
    public static DataTable getPageData(String p_SQL, String p_CurrPage,
                                        String p_PageSize) throws APPErrorException {
        return getPageData(tgtools.db.DataBaseFactory.getDefault(), p_SQL, p_CurrPage, p_PageSize);
    }


    /**
     * 根据数据sql 获取数据总数
     *
     * @param p_SQL 数据sql
     * @return
     */
    public static String getPageCountSQL(String p_SQL) {

        return getPageCountSQL(tgtools.db.DataBaseFactory.getDefault(), p_SQL);
    }

    /**
     * 根据数据sql 获取数据总数
     *
     * @param p_DataAccess 数据源
     * @param p_SQL        数据sql
     * @return
     */
    public static String getPageCountSQL(IDataAccess p_DataAccess, String p_SQL) {
        BaseViewSqls sqls = getPageSqls(p_DataAccess.getDataBaseType());
        String sql = sqls.Page_GetCountData_SQL;
        sql = StringUtil.replace(sql, "${sql}", p_SQL);
        return sql;
    }

    /**
     * 根据数据 sql 获取数据总数
     *
     * @param p_DataAccess 数据源
     * @param p_SQL        数据sql
     * @return
     * @throws APPErrorException
     */
    public static Integer getPageCount(IDataAccess p_DataAccess, String p_SQL) throws APPErrorException {
        String sql = getPageCountSQL(p_DataAccess, p_SQL);
        DataTable dt = p_DataAccess.Query(sql);
        if (DataTable.hasData(dt)) {
            return (Integer) dt.getRow(0).getValue("NUM");
        }
        return 0;
    }

    /**
     * 使用默认数据源 根据数据 sql 获取数据总数
     *
     * @param p_SQL 数据sql
     * @return
     * @throws APPErrorException
     */
    public static Integer getPageCount(String p_SQL) throws APPErrorException {
        return getPageCount(tgtools.db.DataBaseFactory.getDefault(), p_SQL);
    }

    /**
     * 根据指定的数据源 数据sql  获取分页的 BSGridDataEntity
     *
     * @param p_DataAccess 数据源
     * @param p_SQL        数据sql
     * @param p_CurrPage   当前页
     * @param p_PageSize   每页数据数量
     * @param p_OnlyCount  只获取数量
     * @return
     * @throws APPErrorException
     */
    public static BSGridDataEntity getPage(IDataAccess p_DataAccess, String p_SQL, String p_CurrPage,
                                           String p_PageSize, boolean p_OnlyCount) throws APPErrorException {
        BSGridDataEntity res = new BSGridDataEntity();
        try {
            res.setCurPage(Integer.parseInt(p_CurrPage));
            Integer count = getPageCount(p_DataAccess, p_SQL);
            res.setTotalRows(count);
            if (!p_OnlyCount) {
                DataTable dt = getPageData(p_DataAccess, p_SQL, p_CurrPage, p_PageSize);
                res.setData(dt);
            }
            res.setSussecc(true);
        } catch (Exception e) {
            res.setSussecc(false);
            res.setError(e.getMessage());
        }
        return res;
    }

    /**
     * 使用默认数据源 根据指定的数据源 数据sql  获取分页的 BSGridDataEntity
     *
     * @param p_SQL       数据sql
     * @param p_CurrPage  当前页
     * @param p_PageSize  每页数据数量
     * @param p_OnlyCount 只获取数量
     * @return
     * @throws APPErrorException
     */
    public static BSGridDataEntity getPage(String p_SQL, String p_CurrPage,
                                           String p_PageSize, boolean p_OnlyCount) throws APPErrorException {

        return getPage(tgtools.db.DataBaseFactory.getDefault(), p_SQL, p_CurrPage, p_PageSize, p_OnlyCount);
    }

    /**
     * 使用默认数据源 根据指定的数据源 数据sql  获取分页的 BSGridDataEntity
     *
     * @param p_SQL      数据sql
     * @param p_CurrPage 当前页
     * @param p_PageSize 每页数据数量
     * @return
     * @throws APPErrorException
     */
    public static BSGridDataEntity getPage(String p_SQL, String p_CurrPage,
                                           String p_PageSize) throws APPErrorException {
        return getPage(tgtools.db.DataBaseFactory.getDefault(), p_SQL, p_CurrPage, p_PageSize, false);
    }
}
