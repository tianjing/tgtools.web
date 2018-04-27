package tgtools.web.clock;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import tgtools.cache.CacheFactory;
import tgtools.data.DataTable;
import tgtools.db.IDataAccess;
import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;

import java.util.Date;

/**
 * 数据库时间对象
 * 如果数据库访问对象不存在或取值失败则使用当前系统时间
 *
 * @author 田径
 * @Title
 * @Description
 * @date 8:57
 */
public class CacheDatabaseClockImpl extends AbstractCacheDatabaseClock {
    private IDataAccess mIDataAccess = null;
    private CacheManager mCacheManager = null;
    private Cache mCache = null;

    public IDataAccess getIDataAccess() {
        return mIDataAccess;
    }

    public void setIDataAccess(IDataAccess pIDataAccess) {
        mIDataAccess = pIDataAccess;
    }

    public CacheManager getCacheManager() {
        return mCacheManager;
    }

    public void setCacheManager(CacheManager pCacheManager) {
        mCacheManager = pCacheManager;
    }

    @Override
    public Cache getCache() {
        if (null == mCache) {
            if (null == getCacheManager()) {
                return null;
            }
            if (null == getCacheManager().getCache(CacheFactory.TimerCache)) {
                return null;
            }
            mCache = getCacheManager().getCache(CacheFactory.TimerCache);
        }

        return mCache;
    }

    @Override
    protected Date getDBDate()  {
        DataTable dt = null;
        if (null != mIDataAccess) {
            try {
                dt = mIDataAccess.Query("select SYSDATE FROM DUAL");
                if (DataTable.hasData(dt)) {
                    return (Date) DataTable.getFirstRow(dt).getValue(0);
                }
            } catch (APPErrorException e) {
                LogHelper.error("", "获取数据库日期出错", "DatabaseClockImpl.getCacheDBDate", e);
            }
        }
        return new Date();
    }
}
