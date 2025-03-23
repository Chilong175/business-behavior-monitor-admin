package plus.gaga.monitor.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import plus.gaga.monitor.infrastructure.po.MonitorDataMapNode;

import java.util.List;

/**
 * 这个 DAO 负责管理监控数据中的采集节点，就像 TagDao 负责管理文章的标签。
 */
@Mapper
public interface IMonitorDataMapNodeDao {
    // 查询监控节点
    List<MonitorDataMapNode> queryMonitoryDataMapNodeList(MonitorDataMapNode monitorDataMapNodeReq);
    // 查询某个监控 ID 的节点配置
    List<MonitorDataMapNode> queryMonitorNodeConfigByMonitorId(String monitorId);
    // 更新节点配置
    void updateNodeConfig(MonitorDataMapNode monitorDataMapNodeReq);

}
