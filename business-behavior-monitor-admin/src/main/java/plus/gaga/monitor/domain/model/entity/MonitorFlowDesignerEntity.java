package plus.gaga.monitor.domain.model.entity;

import lombok.*;

import java.util.List;

/**
 * 监控流程设计器实体类
 * 用于保存监控流程图中的节点和连线信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonitorFlowDesignerEntity {

    /**
     * 监控ID
     * 用于唯一标识一个监控流程
     */
    private String monitorId;

    /**
     * 节点列表
     * 包含监控流程图中的所有节点信息
     */
    private List<Node> nodeList;

    /**
     * 连线列表
     * 包含监控流程图中所有节点之间的连线信息
     */
    private List<Link> linkList;

    /**
     * 节点类
     * 用于表示监控流程图中的一个节点
     */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Node {
        /**
         * 节点ID
         * 用于唯一标识一个节点
         */
        private String monitorNodeId;

        /**
         * 节点坐标
         * 表示节点在流程图中的位置
         */
        private String loc;
    }

    /**
     * 连线类
     * 用于表示监控流程图中两个节点之间的一条连线
     */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Link {
        /**
         * 连线的起点节点ID
         */
        private String from;

        /**
         * 连线的终点节点ID
         */
        private String to;

        /**
         * 连线的键值
         * 可能用于标识或描述连线的特定属性
         */
        private String key;

        /**
         * 连线上的文本
         * 可能用于在流程图中显示连线的标签或描述
         */
        private String text;
    }
}
