package plus.gaga.monitor.sdk.push;

import plus.gaga.monitor.sdk.model.LogMessage;

/**
 * 接口IPush定义了日志消息推送的通用方法
 * 它提供了开启连接和发送日志消息的功能
 */
public interface IPush {

    /**
     * 开启与远程服务器的连接
     *
     * @param host 远程服务器的主机名或IP地址
     * @param port 远程服务器的端口号
     */
    void open(String host, int port);

    /**
     * 发送日志消息到远程服务器
     *
     * @param logMessage 要发送的日志消息对象，包含日志的相关信息
     */
    void send(LogMessage logMessage);

}
