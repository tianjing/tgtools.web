package tgtools.web.platform;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;
import tgtools.exceptions.APPErrorException;
import tgtools.log.LoggerFactory;
import tgtools.util.StringUtil;
import tgtools.web.log.Log4jFactory;
import tgtools.web.services.ServicesBll;

@SuppressWarnings("rawtypes")
@Component("BeanDefineConfigue")
public class BeanDefineConfigue implements ApplicationListener {
    @Override
    /**
     * 加载容易初始化事件
     * @param event
     */
    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof ContextRefreshedEvent) {
            ContextRefreshedEvent e = (ContextRefreshedEvent) event;
            if (e.getApplicationContext().getParent() == null) {
                onLoad(e);
            }
        } else if (event instanceof ContextStartedEvent) {
            onLoaded();
        } else if (event instanceof ContextClosedEvent) {
            onClosed((ContextClosedEvent) event);
        } else if (event instanceof ContextStoppedEvent) {
            onStoped((ContextStoppedEvent) event);
        }
    }

    /**
     * 加载配置服务
     *
     * @param event
     */
    protected void onLoad(ContextRefreshedEvent event) {
        Platform.startup(event.getApplicationContext());
        LoggerFactory.getDefault().info("Platform 初始化完毕========");
        loadService();

    }

    protected void onLoaded() {
    }

    /**
     * 加载服务
     */
    protected void loadService() {
        LoggerFactory.getDefault().info("表配置的服务 初始化开始========");

        tgtools.threads.ThreadPoolFactory.addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ServicesBll.laodAllServices();
                    LoggerFactory.getDefault().info("表配置的服务 初始化完毕========");
                } catch (APPErrorException e) {
                    if (StringUtil.contains(e.getMessage(), "sql执行失败")) {
                        LoggerFactory.getDefault().info("表配置的服务 初始化失败！不影响项目使用。");
                    } else {
                        LoggerFactory.getDefault().error("表配置的服务 初始化失败！不影响项目使用。", e);
                    }
                }
            }
        });


    }

    protected void onStoped(ContextStoppedEvent event) {
        LoggerFactory.getDefault().info("项目已停止");
    }

    protected void onClosed(ContextClosedEvent event) {
        tgtools.service.ServiceFactory.stop();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LoggerFactory.getDefault().info("项目已关闭");
        Log4jFactory.shutdown();
    }
}
