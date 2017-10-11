package tgtools.web.log;

import org.apache.log4j.DailyRollingFileAppender;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;
import tgtools.web.platform.Platform;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：12:03
 */
public class MyDailyRollingFileAppender extends DailyRollingFileAppender {
    public MyDailyRollingFileAppender()
    {
    }
    @Override
    public void setFile(String file)
    {
        LogHelper.info("","设置日志地址："+file,"MyDailyRollingFileAppender");
        super.setFile(file);
        initFile();
    }
    private Boolean m_InitFile=false;
    public void initFile()
    {
        if(m_InitFile)
        {
            return ;
        }
        LogHelper.info("","日志地址开始："+super.fileName,"MyDailyRollingFileAppender");
        String path=Platform.getServerPath();
        if(StringUtil.isNullOrEmpty(path))
        {
            return ;

        }
        if(super.fileName.indexOf(path)>=0)
        {
            return ;
        }
        super.fileName=path+super.fileName;
        LogHelper.info("","日志地址结束："+super.fileName,"MyDailyRollingFileAppender");
        m_InitFile=true;
    }

}
