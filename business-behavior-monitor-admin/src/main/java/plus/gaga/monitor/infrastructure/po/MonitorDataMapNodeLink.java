package plus.gaga.monitor.infrastructure.po;

import lombok.Data;

import java.util.Date;

/**
 * MonitorDataMapNodeLink 代表监控数据的流转关系，就像 ArticleRecommendation 代表文章之间的推荐关系。
 */

@Data
public class MonitorDataMapNodeLink {

    // 自增ID
    private Long id;
    // 监控ID
    private String monitorId;
    // from 监控ID
    private String fromMonitorNodeId;
    // to 监控ID
    private String toMonitorNodeId;
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;

}
