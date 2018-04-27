package tgtools.web.log.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;
import tgtools.util.StringUtil;

import java.io.File;
import java.net.URL;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：13:09
 */
public class LogbackFactory {

    /**
     * 加载配置
     * @param pPath
     */
    public static void start(String pPath) {
        if (!StringUtil.isNullOrEmpty(pPath)) {
            try {
               load(new File(pPath));
            } catch (Exception e) {
                System.out.println("日志启动失败；原因：" + e.getMessage());
            }
        }
    }

    /**
     * 加载配置
     * @param pPath
     */
    public static void start(URL pPath) {
        if (null!=pPath) {
            try {
                load(pPath);
            } catch (Exception e) {
                System.out.println("日志启动失败；原因：" + e.getMessage());
            }
        }
    }

    /**
     * 停止
     */
    public static void shutdown() {
        try {
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            lc.stop();
        } catch (Exception ex) {
        }
    }



    /**
     * 加载外部的logback配置文件
     *
     * @param pFile 配置文件路径
     *
     * @throws JoranException
     */
    private static void load(File pFile) throws JoranException {
        if(tgtools.log.LoggerFactory.getDefault() instanceof tgtools.log.Log4jLoger)
        {
            tgtools.log.LoggerFactory.getDefault().info("当前为log4j日志，加载logback配置可能无效请注意。");
        }
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        configurator.doConfigure(pFile);
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
    }

    /**
     * 通过URL加载
     * @param pURL
     * @throws JoranException
     */
    private static void load(URL pURL) throws JoranException {
        if(tgtools.log.LoggerFactory.getDefault() instanceof tgtools.log.Log4jLoger)
        {
            tgtools.log.LoggerFactory.getDefault().info("当前为log4j日志，加载logback配置可能无效请注意。");
        }
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        configurator.doConfigure(pURL);
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
    }
}
