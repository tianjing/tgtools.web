package tgtools.web.log;

import org.springframework.util.Log4jConfigurer;
import tgtools.util.StringUtil;
import tgtools.web.platform.Platform;

import java.io.FileNotFoundException;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：13:09
 */
public class Log4jFactory {

    public static void start(String pPath) {
        if (!StringUtil.isNullOrEmpty(pPath)) {
            try {
                Log4jConfigurer.initLogging(pPath);
            } catch (FileNotFoundException e) {
                System.out.println("日志启动失败；原因：" + e.getMessage());
            }
        }
    }

    public static void start() {
        String location = Platform.getServerPath();
        if (!StringUtil.isNullOrEmpty(location)) {
            location += "WEB-INF/log4j.xml";
            start(location);
        }
    }

    public static void shutdown() {
        try {
            Log4jConfigurer.shutdownLogging();
        } catch (Exception ex) {
        }
    }
}
