package tgtools.web.clock;


import org.springframework.cache.Cache;
import tgtools.util.DateUtil;

import java.util.Date;

/**
 * 名  称：数据库时钟
 * 编写者：田径
 * 功  能：利用缓存 减少sql 方式 获取数据库时间
 * 时  间：16:58
 */
public abstract class AbstractCacheDatabaseClock extends AbstractClock {
    protected static final String CACHE_KEY="ClockDBDate";

    /**
     * 获取缓存
     * @return
     */
    public abstract Cache getCache();


    protected Date getCacheDBDate()
    {
        if(null==getCache())
        {
            return getDBDate();
        }
        Date cacheDate=getCache().get(CACHE_KEY,Date.class);
        if(null==cacheDate)
        {
            try {
                Date date = getDBDate();
                getCache().put(CACHE_KEY, date);
                calculateDvalue(date);
            }catch (Exception ex)
            {
                return null;
            }
        }
        return getCache().get(CACHE_KEY,Date.class);
    }
    /**
     * 计算时间差
     * @param p_Date
     */
    protected void calculateDvalue(Date p_Date)
    {
        if(null!=p_Date)
        {
            m_Dvalue= p_Date.getTime()- DateUtil.getCurrentDate().getTime();
        }
    }

    /**
     * 获取当前时间并计算当前时间
     * @return
     */
    @Override
    protected Date getCurrentDate()
    {
        Date date =getCacheDBDate();
        if(null!=date)
        {
            return new Date(DateUtil.getCurrentDate().getTime()+m_Dvalue);
        }
        return DateUtil.getCurrentDate();
    }
    protected long m_Dvalue=0;
}
