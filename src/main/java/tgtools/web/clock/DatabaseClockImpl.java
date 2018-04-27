package tgtools.web.clock;

import tgtools.data.DataTable;
import tgtools.db.IDataAccess;
import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;

import java.util.Date;

/**
 * 数据库时间对象
 * 如果数据库访问对象不存在或取值失败则使用当前系统时间
 * @author 田径
 * @Title
 * @Description
 * @date 8:57
 */
public class DatabaseClockImpl extends AbstractClock {
    private IDataAccess mIDataAccess;

    public IDataAccess getIDataAccess() {
        return mIDataAccess;
    }

    public void setIDataAccess(IDataAccess pIDataAccess) {
        mIDataAccess = pIDataAccess;
    }

    @Override
    protected Date getDBDate() {
        DataTable dt=null;
        if(null!=mIDataAccess) {
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
