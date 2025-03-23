package plus.gaga.monitor.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import plus.gaga.monitor.infrastructure.po.MonitorDataMapNodeField;

import java.util.List;

/**
 * 这个 DAO 负责管理监控数据中的字段信息，就像 ArticleContentDao 负责文章的内容块。
 */
@Mapper
public interface IMonitorDataMapNodeFieldDao {
    List<MonitorDataMapNodeField> queryMonitorDataMapNodeList(MonitorDataMapNodeField monitorDataMapNodeFieldReq);

}
