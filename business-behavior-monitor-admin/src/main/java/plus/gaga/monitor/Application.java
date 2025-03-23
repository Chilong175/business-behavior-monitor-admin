package plus.gaga.monitor;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import plus.gaga.monitor.sdk.model.LogMessage;
import plus.gaga.monitor.trigger.listener.MonitorLogListener;

/**
 * Spring Boot 启动监控服务
 */
@SpringBootApplication
@Configurable
public class Application {

    /**
     * 主程序入口
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    /**
     * Redis 客户端配置类
     */
    @Configuration
    /**
     * Redis 客户端配置类
     * 使用 @EnableConfigurationProperties 注解开启 RedisClientConfigProperties 配置属性的支持
     * 这个内部类负责基于应用配置创建和配置 RedissonClient 实例
     */
    @EnableConfigurationProperties(RedisClientConfigProperties.class)
    public static class RedisClientConfig {

        /**
         * 创建 RedissonClient 实例，建立Redis 连接，让 Spring Boot 应用可以 使用 Redis 存储监控数据。
         *
         * @param applicationContext Spring 应用上下文，用于访问应用配置和环境信息
         * @param properties Redis 客户端配置属性，包含 Redis 连接和池配置
         * @return RedissonClient 实例，用于与 Redis 服务器进行交互
         */
        @Bean("redissonClient")
        public RedissonClient redissonClient(ConfigurableApplicationContext applicationContext, RedisClientConfigProperties properties) {
            Config config = new Config();
            // 设置编解码器，这里使用 JsonJacksonCodec 实例
            // 编解码器用于在 Java 对象和 Redis 数据之间进行转换
            config.setCodec(JsonJacksonCodec.INSTANCE);

            // 连接单个 Redis 服务器（也可以配置集群）
            config.useSingleServer()
                    // 连接 Redis 地址
                    .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                    // 设置连接池大小
                    .setConnectionPoolSize(properties.getPoolSize())
                    // 设置最小空闲连接数
                    .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                    // 设置空闲连接超时时间
                    .setIdleConnectionTimeout(properties.getIdleTimeout())
                    // 设置连接超时时间
                    .setConnectTimeout(properties.getConnectTimeout())
                    // 设置重试次数
                    .setRetryAttempts(properties.getRetryAttempts())
                    // 设置重试间隔
                    .setRetryInterval(properties.getRetryInterval())
                    // 设置连接验证间隔
                    .setPingConnectionInterval(properties.getPingInterval())
                    // 设置是否保持连接
                    .setKeepAlive(properties.isKeepAlive())
            ;

            // 返回 Redisson 客户端
            return Redisson.create(config);
        }


    }

    /**
     * 创建业务行为监控主题
     *
     * @param redissonClient Redis 客户端
     * @param monitorLogListener 监控日志监听器
     * @return RTopic 实例
     */
    @Bean("businessBehaviorMonitorTopic")
    public RTopic businessBehaviorMonitorTopic(RedissonClient redissonClient, MonitorLogListener monitorLogListener) {
        RTopic topic = redissonClient.getTopic("business-behavior-monitor-sdk-topic");
        topic.addListener(LogMessage.class, monitorLogListener);
        return topic;
    }

    /**
     * Redis 客户端配置属性类
     */
    @Data
    @ConfigurationProperties(prefix = "redis.sdk.config", ignoreInvalidFields = true)
    public static class RedisClientConfigProperties {
        /**
         * host:ip
         */
        private String host;
        /**
         * 端口
         */
        private int port;
        /**
         * 账密
         */
        private String password;
        /**
         * 设置连接池的大小，默认为64
         */
        private int poolSize = 64;
        /**
         * 设置连接池的最小空闲连接数，默认为10
         */
        private int minIdleSize = 10;
        /**
         * 设置连接的最大空闲时间（单位：毫秒），超过该时间的空闲连接将被关闭，默认为10000
         */
        private int idleTimeout = 10000;
        /**
         * 设置连接超时时间（单位：毫秒），默认为10000
         */
        private int connectTimeout = 10000;
        /**
         * 设置连接重试次数，默认为3
         */
        private int retryAttempts = 3;
        /**
         * 设置连接重试的间隔时间（单位：毫秒），默认为1000
         */
        private int retryInterval = 1000;
        /**
         * 设置定期检查连接是否可用的时间间隔（单位：毫秒），默认为0，表示不进行定期检查
         */
        private int pingInterval = 0;
        /**
         * 设置是否保持长连接，默认为true
         */
        private boolean keepAlive = true;
    }

}
