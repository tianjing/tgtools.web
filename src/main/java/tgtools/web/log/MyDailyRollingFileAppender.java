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
    private String mFile="";
    @Override
    public void setFile(String file)
    {
        if(StringUtil.isNullOrEmpty(mFile)) {
            mFile = file;
        }
        LogHelper.info("","设置日志地址："+file,"MyDailyRollingFileAppender");
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
        if(StringUtil.isNullOrEmpty(mFile)||mFile.indexOf(path)>=0)
        {
            return ;
        }

        if(mFile.indexOf("/")==0||mFile.indexOf("\\")==0)
        {
            mFile=mFile.substring(1);
        }
        path =path+mFile;
        path=StringUtil.replace(path,"\\","/");
        path=StringUtil.replace(path,"//","/");
        super.fileName=path;
        super.setFile(super.fileName);
        LogHelper.info("","日志地址结束："+super.fileName,"MyDailyRollingFileAppender");
        m_InitFile=true;
    }

}
