package tgtools.web.clock;


import tgtools.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * 名  称：数据库时钟
 * 编写者：田径
 * 功  能：利用缓存 减少sql 方式 获取数据库时间
 * 时  间：16:58
 */
public abstract class AbstractClock {

    /**
     * 获取当前时间对象
     * @return
     */
    public Date getCurrentTime() {
        try {
            return getCurrentDate();
        }
        catch (Exception ex)
        {
            return new Date();
        }
    }

    /**
     * 获取当前日期对象
     * @return
     */
    public Calendar getCurrentCalendar() {
        try {
            Calendar dd = new GregorianCalendar();
            dd.setTime(getCurrentTime());
            return dd;
        }catch (Exception ex)
        {
            return new GregorianCalendar();
        }
    }

    /**
     * 获取当前时区的日期对象
     * @param timeZone
     * @return
     */
    public Calendar getCurrentCalendar(TimeZone timeZone) {
        return convertToTimeZone(getCurrentCalendar(), timeZone);
    }

    /**
     * 获取当前时区
     * @return
     */
    public TimeZone getCurrentTimeZone() {
        return getCurrentCalendar().getTimeZone();
    }

    /**
     * 获取当前时间
     * @return
     */
    protected Date getCurrentDate()
    {
        return getDBDate();
    }

    /**
     * 通过数据库和缓存获取当前时间
     * @return
     */
    protected abstract Date getDBDate() ;

    public static Calendar convertToTimeZone(Calendar time, TimeZone timeZone) {
        Calendar foreignTime = new GregorianCalendar(timeZone);
        foreignTime.setTimeInMillis(time.getTimeInMillis());

        return foreignTime;
    }
}
