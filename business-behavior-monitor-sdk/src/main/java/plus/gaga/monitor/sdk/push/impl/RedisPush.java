package plus.gaga.monitor.sdk.push.impl;

import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.gaga.monitor.sdk.model.LogMessage;
import plus.gaga.monitor.sdk.push.IPush;

/**
 * Redis消息推送实现类
 * 利用Redis的发布/订阅机制来推送消息
 */
public class RedisPush implements IPush {

    // 日志记录器
    private final Logger logger = LoggerFactory.getLogger(RedisPush.class);

    // Redisson客户端实例
    private RedissonClient redissonClient;

    /**
     * 开启Redisson客户端连接
     * 如果客户端已经开启，则直接返回，避免重复连接
     *
     * @param host Redis服务器主机名
     * @param port Redis服务器端口号
     */
    @Override
    public synchronized void open(String host, int port) {
        if (null != redissonClient && !redissonClient.isShutdown()) return;

        // 配置Redisson客户端
        Config config = new Config();
        config.setCodec(JsonJacksonCodec.INSTANCE); // 设置编解码器为JsonJacksonCodec
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port) // 设置Redis服务器地址
                .setConnectionPoolSize(64) // 连接池大小
                .setConnectionMinimumIdleSize(10) // 最小空闲连接数
                .setIdleConnectionTimeout(1000) // 空闲连接超时时间
                .setConnectTimeout(1000) // 连接超时时间
                .setRetryAttempts(3) // 重试次数
                .setRetryInterval(1000) // 重试间隔
                .setPingConnectionInterval(0) // Ping间隔
                .setKeepAlive(true); // 保持连接

        // 创建Redisson客户端实例
        this.redissonClient = Redisson.create(config);
    }

    /**
     * 发送日志消息到Redis主题
     * 如果发送过程中出现异常，将记录错误日志
     *
     * @param logMessage 要发送的日志消息
     */
    @Override
    public void send(LogMessage logMessage) {
        try {
            // 获取Redis主题
            RTopic topic = redissonClient.getTopic("business-behavior-monitor-sdk-topic");
            // 发布日志消息到主题
            topic.publish(logMessage);
        } catch (Exception e) {
            // 记录推送日志消息失败的错误信息
            logger.error("警告: 业务行为监控组件，推送日志消息失败", e);
        }

    }

}
