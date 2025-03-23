package plus.gaga.monitor.sdk.model;

import java.util.List;

/**
 * 日志消息类
 * 用于封装日志相关信息，包括系统名称、类名、方法名及日志列表
 * 主要用途是用于日志的收集、处理和传输
 *
 */
public class LogMessage {

    // 系统名称，标识日志来源的系统
    private String systemName;

    // 类名，标识日志来源的类
    private String className;

    // 方法名，标识日志来源的方法
    private String methodName;

    // 日志列表，存储具体的日志信息条目
    private List<String> logList;

    // 默认构造函数
    public LogMessage() {
    }

    /**
     * 带参数的构造函数
     *
     * @param systemName 系统名称
     * @param className  类名
     * @param methodName 方法名
     * @param logList    日志列表
     */
    public LogMessage(String systemName, String className, String methodName, List<String> logList) {
        this.systemName = systemName;
        this.className = className;
        this.methodName = methodName;
        this.logList = logList;
    }

    // 获取系统名称
    public String getSystemName() {
        return systemName;
    }

    // 获取类名
    public String getClassName() {
        return className;
    }

    // 获取方法名
    public String getMethodName() {
        return methodName;
    }

    // 获取日志列表
    public List<String> getLogList() {
        return logList;
    }

}
