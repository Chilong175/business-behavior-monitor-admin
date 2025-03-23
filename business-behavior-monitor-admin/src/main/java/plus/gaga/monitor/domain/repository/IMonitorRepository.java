package plus.gaga.monitor.domain.repository;

import plus.gaga.monitor.domain.model.entity.MonitorDataEntity;
import plus.gaga.monitor.domain.model.entity.MonitorDataMapEntity;
import plus.gaga.monitor.domain.model.entity.MonitorFlowDesignerEntity;
import plus.gaga.monitor.domain.model.valobj.GatherNodeExpressionVO;
import plus.gaga.monitor.domain.model.valobj.MonitorTreeConfigVO;

import java.util.List;

/**
 * 监控数据存储接口
 * 提供监控数据的增删查改功能
 */
public interface IMonitorRepository {

    /**
     * 查询采集节点表达式信息
     *
     * @param systemName 系统名称
     * @param className 类名
     * @param methodName 方法名
     * @return 采集节点表达式列表
     */
    List<GatherNodeExpressionVO> queryGatherNodeExpressionVO(String systemName, String className, String methodName);

    /**
     * 根据监控ID查询监控名称
     *
     * @param monitorId 监控ID
     * @return 监控名称
     */
    String queryMonitoryNameByMonitoryId(String monitorId);

    /**
     * 保存监控数据
     *
     * @param monitorDataEntity 监控数据实体
     */
    void saveMonitoryData(MonitorDataEntity monitorDataEntity);

    /**
     * 查询监控数据映射实体列表
     *
     * @return 监控数据映射实体列表
     */
    List<MonitorDataMapEntity> queryMonitorDataMapEntityList();

    /**
     * 根据监控ID查询监控流程数据
     *
     * @param monitorId 监控ID
     * @return 监控树配置VO
     */
    MonitorTreeConfigVO queryMonitorFlowData(String monitorId);

    /**
     * 查询监控数据实体列表
     *
     * @param monitorDataEntity 监控数据实体条件
     * @return 监控数据实体列表
     */
    List<MonitorDataEntity> queryMonitorDataEntityList(MonitorDataEntity monitorDataEntity);

    /**
     * 更新监控流程设计器数据
     *
     * @param monitorFlowDesignerEntity 监控流程设计器实体
     */
    void updateMonitorFlowDesigner(MonitorFlowDesignerEntity monitorFlowDesignerEntity);

}
