package plus.gaga.monitor.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import plus.gaga.monitor.infrastructure.po.MonitorDataMapNodeLink;

import java.util.List;

/**
 * 这个 DAO 负责管理监控数据的流转关系，就像 ArticleRecommendationDao 负责文章之间的推荐关系。
 */
@Mapper
public interface IMonitorDataMapNodeLinkDao {
    // 查询监控数据流转
    List<MonitorDataMapNodeLink> queryMonitorNodeLinkConfigByMonitorId(String monitorId);
    // 删除某个监控 ID 相关的流转信息
    void deleteLinkFromByMonitorId(String monitorId);
    // 插入新的流转信息
    void insert(MonitorDataMapNodeLink monitorDataMapNodeLinkReq);

}
