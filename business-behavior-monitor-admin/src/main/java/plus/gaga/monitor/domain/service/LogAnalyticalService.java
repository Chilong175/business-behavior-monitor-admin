package plus.gaga.monitor.domain.service;

import com.alibaba.fastjson.JSONObject;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.springframework.stereotype.Service;
import plus.gaga.monitor.domain.model.entity.MonitorDataEntity;
import plus.gaga.monitor.domain.model.entity.MonitorDataMapEntity;
import plus.gaga.monitor.domain.model.entity.MonitorFlowDesignerEntity;
import plus.gaga.monitor.domain.model.valobj.GatherNodeExpressionVO;
import plus.gaga.monitor.domain.model.valobj.MonitorTreeConfigVO;
import plus.gaga.monitor.domain.repository.IMonitorRepository;
import plus.gaga.monitor.types.Constants;

import javax.annotation.Resource;
import java.util.List;

/**
 * LogAnalyticalService 是 日志分析服务实现类，它的作用是 解析日志数据，并将关键数据存储到监控系统。
 */
@Service
public class LogAnalyticalService implements ILogAnalyticalService {

    @Resource
    private IMonitorRepository repository;

    /**
     * 执行日志分析
     *
     * @param systemName 系统名称
     * @param className 类名称
     * @param methodName 方法名称
     * @param logList 日志列表
     * @throws OgnlException 如果OGNL表达式解析失败
     */
    @Override
    public void doAnalytical(String systemName, String className, String methodName, List<String> logList) throws OgnlException {

        // 查询符合条件的监控节点表达式信息
        List<GatherNodeExpressionVO> gatherNodeExpressionVOs = repository.queryGatherNodeExpressionVO(systemName, className, methodName);
        // 如果查询结果为空，则直接返回
        if (null == gatherNodeExpressionVOs || gatherNodeExpressionVOs.isEmpty()) return;

        // 遍历查询到的监控节点表达式信息
        for (GatherNodeExpressionVO gatherNodeExpressionVO : gatherNodeExpressionVOs) {
            // 根据监控ID查询监控名称
            String monitoryName = repository.queryMonitoryNameByMonitoryId(gatherNodeExpressionVO.getMonitorId());

            // 获取当前监控节点的所有字段信息
            List<GatherNodeExpressionVO.Filed> fileds = gatherNodeExpressionVO.getFileds();
            // 遍历每个字段，处理日志信息以提取所需的监控数据
            for (GatherNodeExpressionVO.Filed filed : fileds) {
                Integer logIndex = filed.getLogIndex();

                // 获取日志名称，并检查是否与当前字段的日志名称匹配
                String logName = logList.get(0);
                if (!logName.equals(filed.getLogName())) {
                    continue;
                }

                // 初始化属性值
                String attributeValue = "";
                // 获取当前日志字符串
                String logStr = logList.get(logIndex);
                // 根据日志类型处理日志字符串以提取属性值
                if ("Object".equals(filed.getLogType())) {
                    // 使用Ognl解析JSON对象类型日志：

                    // 创建OgnlContext实例，用于解析OGNL表达式
                    OgnlContext context = new OgnlContext();
                    // 将日志字符串解析为JSONObject，并设置为上下文的根对象
                    context.setRoot(JSONObject.parseObject(logStr));
                    // 获取上下文的根对象
                    Object root = context.getRoot();
                    // 根据OGNL表达式获取属性值
                    // attributeValue变量用于存储获取的属性值，通过OGNL表达式解析和获取
                    attributeValue = String.valueOf(Ognl.getValue(filed.getAttributeOgnl(), context, root));
                } else {
                    // 对于非对象类型日志，直接提取或进一步处理属性值
                    attributeValue = logStr.trim();
                    if (attributeValue.contains(Constants.COLON)) {
                        attributeValue = attributeValue.split(Constants.COLON)[1].trim();
                    }
                }

                // 构建监控数据实体对象
                MonitorDataEntity monitorDataEntity = MonitorDataEntity.builder()
                        .monitorId(gatherNodeExpressionVO.getMonitorId())
                        .monitorName(monitoryName)
                        .monitorNodeId(gatherNodeExpressionVO.getMonitorNodeId())
                        .systemName(gatherNodeExpressionVO.getGatherSystemName())
                        .clazzName(gatherNodeExpressionVO.getGatherClazzName())
                        .methodName(gatherNodeExpressionVO.getGatherMethodName())
                        .attributeName(filed.getAttributeName())
                        .attributeField(filed.getAttributeField())
                        .attributeValue(attributeValue)
                        .build();

                // 保存监控数据到数据库
                repository.saveMonitoryData(monitorDataEntity);
            }

        }

    }

    /**
     * 查询监控数据映射实体列表（ = 查询文章分类）
     *
     * @return 监控数据映射实体列表
     */
    @Override
    public List<MonitorDataMapEntity> queryMonitorDataMapEntityList() {
        return repository.queryMonitorDataMapEntityList();
    }

    /**
     * 查询监控流程数据（ = 查询文章推荐关系）
     *
     * @param monitorId 监控ID
     * @return 监控树配置VO
     */
    @Override
    public MonitorTreeConfigVO queryMonitorFlowData(String monitorId) {
        return repository.queryMonitorFlowData(monitorId);
    }

    /**
     * 查询监控数据实体列表
     *
     * @param monitorDataEntity 监控数据实体条件
     * @return 监控数据实体列表
     */
    @Override
    public List<MonitorDataEntity> queryMonitorDataEntityList(MonitorDataEntity monitorDataEntity) {
        return repository.queryMonitorDataEntityList(monitorDataEntity);
    }

    /**
     * 更新监控流程设计器实体（ = 调整文章分类）
     *
     * @param monitorFlowDesignerEntity 监控流程设计器实体
     */
    @Override
    public void updateMonitorFlowDesigner(MonitorFlowDesignerEntity monitorFlowDesignerEntity) {
        repository.updateMonitorFlowDesigner(monitorFlowDesignerEntity);
    }

}
