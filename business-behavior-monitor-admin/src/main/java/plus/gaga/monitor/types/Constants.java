package plus.gaga.monitor.types;

/**
 * 常量类，用于定义项目中使用的各种常量
 */
public class Constants {

    /**
     * 逗号分隔符常量
     */
    public final static String SPLIT = ",";

    /**
     * 冒号常量
     */
    public final static String COLON = ":";

    /**
     * 空格常量
     */
    public final static String SPACE = " ";

    /**
     * 下划线常量
     */
    public final static String UNDERLINE = "_";

    /**
     * Redis键常量内部类，用于定义在Redis中使用的键
     */
    public static class RedisKey {
        /**
         * 监控节点数据计数的Redis键，用于存储监控节点的数据信息
         */
        public static String monitor_node_data_count_key = "monitor_node_data_key_";
    }
}

