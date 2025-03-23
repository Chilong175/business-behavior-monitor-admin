package plus.gaga.monitor.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import plus.gaga.monitor.infrastructure.po.MonitorData;

import java.util.List;

/**
 * 这个 DAO 负责存储具体的监控数据，就像 ArticleDao 存储文章数据。
 */
@Mapper
public interface IMonitorDataDao {
    // 查询监控数据
    List<MonitorData> queryMonitorDataList(MonitorData monitorDataReq);
    // 插入监控数据
    void insert(MonitorData monitorDataReq);

}
