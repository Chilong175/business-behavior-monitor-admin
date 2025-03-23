package plus.gaga.monitor.sdk.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import plus.gaga.monitor.sdk.model.LogMessage;
import plus.gaga.monitor.sdk.push.IPush;
import plus.gaga.monitor.sdk.push.impl.RedisPush;

import java.util.Arrays;

/**
 * 自定义日志采集类，继承自AppenderBase，用于将日志事件追加到自定义的位置或处理逻辑中
 * 主要功能是将特定系统和组的日志推送到Redis中
 */
public class CustomAppender<E> extends AppenderBase<E> {

    // 系统名称，用于标识日志来源系统
    private String systemName;
    // 只采集指定范围的日志，通过组ID进行筛选
    private String groupId;
    // redis 连接地址
    private String host;
    // redis 连接端口
    private int port;

    // 使用Redis进行日志推送
    private final IPush push = new RedisPush();

    /**
     * 实现日志事件的追加逻辑
     * 通过判断日志事件的来源是否符合预设的组ID来决定是否进行日志的推送
     * @param eventObject 日志事件对象，包含日志的相关信息
     */
    @Override
    protected void append(E eventObject) {
        // 开启推送
        push.open(host, port);
        //获取日志
        // 检查事件对象是否为ILoggingEvent类型
        if (eventObject instanceof ILoggingEvent) {
            ILoggingEvent event = (ILoggingEvent) eventObject;

            String methodName = "unknown";
            String className = "unknown";

            // 获取事件的调用者数据数组
            StackTraceElement[] callerDataArray = event.getCallerData();
            // 如果调用者数据数组非空且长度大于0，获取第一个调用者数据
            if (null != callerDataArray && callerDataArray.length > 0) {
                StackTraceElement callerData = callerDataArray[0];
                methodName = callerData.getMethodName();
                className = callerData.getClassName();
            }

            // 如果类名不以groupId开头，直接返回
            if (!className.startsWith(groupId)){
                return;
            }

            // 构建日志
            LogMessage logMessage = new LogMessage(systemName, className, methodName, Arrays.asList(event.getFormattedMessage().split(" ")));
            // 推送日志
            push.send(logMessage);
        }
    }

    // 下面是getter和setter方法，用于获取和设置自定义Appender的属性值

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
