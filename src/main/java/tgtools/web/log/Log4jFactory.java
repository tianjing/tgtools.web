package tgtools.web.log;

import org.springframework.util.Log4jConfigurer;
import tgtools.util.StringUtil;
import tgtools.web.platform.Platform;

import java.io.FileNotFoundException;

/**
 * log4j 配置加载
 */
public class Log4jFactory {

    /**
     * 根据文件路径加载配置
     * @param pPath
     */
    public static void start(String pPath) {
        if (!StringUtil.isNullOrEmpty(pPath)) {
            try {
                if(tgtools.log.LoggerFactory.getDefault() instanceof tgtools.log.DefaultLoger)
                {
                    tgtools.log.LoggerFactory.getDefault().info("当前可能不是log4j日志，加载log4j配置可能无效请注意。");
                }
                Log4jConfigurer.initLogging(pPath);
            } catch (FileNotFoundException e) {
                System.out.println("日志启动失败；原因：" + e.getMessage());
            }
        }
    }

    /**
     * 加载默认配置
     */
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
