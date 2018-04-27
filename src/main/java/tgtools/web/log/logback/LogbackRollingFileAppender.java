package tgtools.web.log.logback;

import tgtools.util.LogHelper;
import tgtools.web.platform.Platform;

/**
 * 名  称：logback 文件输出类 方便 不用定义 LOG_HOME
 * 编写者：田径
 * 功  能：
 * 时  间：12:03
 */
public class LogbackRollingFileAppender extends ch.qos.logback.core.rolling.RollingFileAppender {
    /**
     * 获取文件路径
     * @return
     */
    @Override
    public String getFile() {
        LogHelper.info("", "日志地址开始：" + super.fileName, "LogbackRollingFileAppender");
        String path = Platform.getServerPath();
        return path+super.getFile();
    }


}
