package plus.gaga.monitor.domain.service;

import ognl.OgnlException;
import plus.gaga.monitor.domain.model.entity.MonitorDataEntity;
import plus.gaga.monitor.domain.model.entity.MonitorDataMapEntity;
import plus.gaga.monitor.domain.model.entity.MonitorFlowDesignerEntity;
import plus.gaga.monitor.domain.model.valobj.MonitorTreeConfigVO;

import java.util.List;

/**
 * 日志分析服务接口，定义了日志数据解析和监控数据查询相关的操作
 */
public interface ILogAnalyticalService {

    /**
     * 解析日志并执行监控操作
     *
     * @param systemName 系统名称，用于标识日志来源系统
     * @param className 类名，用于标识日志相关的类
     * @param methodName 方法名，用于标识日志相关的具体方法
     * @param logList 日志列表，包含待解析的日志数据
     * @throws OgnlException 如果日志解析过程中发生错误
     */
    void doAnalytical(String systemName, String className, String methodName, List<String> logList) throws OgnlException;

    /**
     * 查询监控数据映射实体列表
     *
     * @return 监控数据映射实体列表，包含所有监控数据的映射信息
     */
    List<MonitorDataMapEntity> queryMonitorDataMapEntityList();

    /**
     * 根据监控ID查询监控流程数据
     *
     * @param monitorId 监控ID，用于标识特定的监控流程
     * @return 监控树配置VO对象，包含监控流程的配置数据
     */
    MonitorTreeConfigVO queryMonitorFlowData(String monitorId);

    /**
     * 查询监控数据实体列表
     *
     * @param monitorDataEntity 监控数据实体对象，包含查询条件
     * @return 监控数据实体列表，满足查询条件的监控数据
     */
    List<MonitorDataEntity> queryMonitorDataEntityList(MonitorDataEntity monitorDataEntity);

    /**
     * 更新监控流程设计器实体
     *
     * @param monitorFlowDesignerEntity 监控流程设计器实体对象，包含更新后的监控流程信息
     */
    void updateMonitorFlowDesigner(MonitorFlowDesignerEntity monitorFlowDesignerEntity);

}
