package plus.gaga.monitor.trigger.listener;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Component;
import plus.gaga.monitor.domain.service.ILogAnalyticalService;
import plus.gaga.monitor.sdk.model.LogMessage;

import javax.annotation.Resource;

/**
 * 监控日志监听器
 * 用于监听监控日志消息，并进行解析和存储
 */
@Slf4j
@Component
public class MonitorLogListener implements MessageListener<LogMessage> {

    /**
     * 日志解析服务
     * 用于处理日志消息的解析和存储
     */
    @Resource
    private ILogAnalyticalService logAnalyticalService;

    /**
     * 当接收到日志消息时调用的方法
     *
     * @param charSequence 消息的字符序列
     * @param logMessage 日志消息对象，包含系统名、类名、方法名和日志列表
     */
    @Override
    public void onMessage(CharSequence charSequence, LogMessage logMessage) {
        try {
            // 监听监控日志消息，进行解析和存储
            log.info("监听监控日志消息，解析存储: {}", JSON.toJSONString(logMessage));
            logAnalyticalService.doAnalytical(logMessage.getSystemName(), logMessage.getClassName(), logMessage.getMethodName(), logMessage.getLogList());
        } catch (Exception e) {
            // 日志消息解析失败时记录错误日志
            log.error("监听监控日志消息，解析失败: {}", JSON.toJSONString(logMessage), e);
        }
    }

}
