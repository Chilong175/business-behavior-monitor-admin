package plus.gaga.monitor.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import plus.gaga.monitor.infrastructure.po.MonitorDataMap;

import java.util.List;

/**
 * 这个 DAO 负责管理监控数据的分类，就像 CategoryDao 负责文章分类。
 */
@Mapper
public interface IMonitorDataMapDao {
    // 根据 ID 查监控名称
    String queryMonitorNameByMonitoryId(String monitorId);
    // 获取所有监控映射
    List<MonitorDataMap> queryMonitorDataMapEntityList();

}
