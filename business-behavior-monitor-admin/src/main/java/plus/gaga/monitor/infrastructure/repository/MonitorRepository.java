package plus.gaga.monitor.infrastructure.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import plus.gaga.monitor.domain.model.entity.MonitorDataEntity;
import plus.gaga.monitor.domain.model.entity.MonitorDataMapEntity;
import plus.gaga.monitor.domain.model.entity.MonitorFlowDesignerEntity;
import plus.gaga.monitor.domain.model.valobj.GatherNodeExpressionVO;
import plus.gaga.monitor.domain.model.valobj.MonitorTreeConfigVO;
import plus.gaga.monitor.domain.repository.IMonitorRepository;
import plus.gaga.monitor.infrastructure.dao.*;
import plus.gaga.monitor.infrastructure.po.*;
import plus.gaga.monitor.infrastructure.redis.IRedisService;
import plus.gaga.monitor.types.Constants;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * 监控数据仓库实现
 * queryGatherNodeExpressionVO	查询文章的所有标签
 * queryMonitoryNameByMonitoryId	通过文章ID查文章标题
 * saveMonitoryData	新增文章并增加浏览量
 * queryMonitorFlowData	获取文章的推荐关系
 * updateMonitorFlowDesigner	调整文章分类
 */
@Repository
public class MonitorRepository implements IMonitorRepository {
    @Resource
    private IMonitorDataDao monitorDataDao;
    @Resource
    private IMonitorDataMapDao monitorDataMapDao;
    @Resource
    private IMonitorDataMapNodeDao monitorDataMapNodeDao;
    @Resource
    private IMonitorDataMapNodeFieldDao monitorDataMapNodeFieldDao;
    @Resource
    private IMonitorDataMapNodeLinkDao monitorDataMapNodeLinkDao;
    @Resource
    private IRedisService redisService;
    @Resource
    private TransactionTemplate transactionTemplate;
    /**
     * 查询采集节点表达式信息
     *
     * 博客场景类比： 就像是**“查找某篇文章的所有标签”**：
     * systemName = 文章所属的分类
     * className = 文章的标题
     * methodName = 文章的作者
     * GatherNodeExpressionVO = 这篇文章的标签列表
     * 实现逻辑：
     * 先去 monitorDataMapNodeDao 查询符合条件的监控节点（文章）。
     * 再去 monitorDataMapNodeFieldDao 查询这些节点对应的字段信息（标签）。
     * 把所有查到的信息封装成 GatherNodeExpressionVO 返回。
     */
    @Override
    public List<GatherNodeExpressionVO> queryGatherNodeExpressionVO(String systemName, String className, String methodName) {

        // 创建一个MonitorDataMapNode对象，用于后续查询监测数据映射节点列表
        MonitorDataMapNode monitorDataMapNodeReq = new MonitorDataMapNode();
        // 设置需要查询的系统名称
        monitorDataMapNodeReq.setGatherSystemName(systemName);
        // 设置需要查询的类名称
        monitorDataMapNodeReq.setGatherClazzName(className);
        // 设置需要查询的方法名称
        monitorDataMapNodeReq.setGatherMethodName(methodName);
        // 查询符合要求的监测数据映射节点列表
        List<MonitorDataMapNode> monitorDataMapNodes = monitorDataMapNodeDao.queryMonitoryDataMapNodeList(monitorDataMapNodeReq);
        // 如果查询结果为空，则直接返回null
        if (monitorDataMapNodes.isEmpty()) return null;

        // 创建一个列表，用于存储采集节点表达式视图对象
        List<GatherNodeExpressionVO> gatherNodeExpressionVOS = new ArrayList<>();
        // 遍历查询到的每一个监测数据映射节点
        for (MonitorDataMapNode monitorDataMapNodeRes : monitorDataMapNodes) {
           
            // 创建一个MonitorDataMapNodeField对象，用于查询监测数据映射节点字段列表
            MonitorDataMapNodeField monitorDataMapNodeFieldReq = new MonitorDataMapNodeField();
            // 设置监测ID
            monitorDataMapNodeFieldReq.setMonitorId(monitorDataMapNodeRes.getMonitorId());
            // 设置监测节点ID
            monitorDataMapNodeFieldReq.setMonitorNodeId(monitorDataMapNodeRes.getMonitorNodeId());
            // 查询符合要求的监测数据映射节点字段列表
            List<MonitorDataMapNodeField> monitorDataMapNodeFieldList = monitorDataMapNodeFieldDao.queryMonitorDataMapNodeList(monitorDataMapNodeFieldReq);

            // 创建一个列表，用于存储采集节点表达式视图对象的字段信息
            List<GatherNodeExpressionVO.Filed> fileds = new ArrayList<>();
            // 遍历每一个监测数据映射节点字段
            for (MonitorDataMapNodeField monitorDataMapNodeField : monitorDataMapNodeFieldList) {
                // 将字段信息转换为采集节点表达式视图对象的字段，并添加到列表中
                fileds.add(GatherNodeExpressionVO.Filed.builder()
                        .logName(monitorDataMapNodeField.getLogName())
                        .logIndex(monitorDataMapNodeField.getLogIndex())
                        .logType(monitorDataMapNodeField.getLogType())
                        .attributeField(monitorDataMapNodeField.getAttributeField())
                        .attributeName(monitorDataMapNodeField.getAttributeName())
                        .attributeOgnl(monitorDataMapNodeField.getAttributeOgnl())
                        .build());
            }

            // 将监测数据映射节点及其字段信息转换为采集节点表达式视图对象，并添加到列表中
            gatherNodeExpressionVOS.add(GatherNodeExpressionVO.builder()
                    .monitorId(monitorDataMapNodeRes.getMonitorId())
                    .monitorNodeId(monitorDataMapNodeRes.getMonitorNodeId())
                    .gatherSystemName(monitorDataMapNodeRes.getGatherSystemName())
                    .gatherClazzName(monitorDataMapNodeRes.getGatherClazzName())
                    .gatherMethodName(monitorDataMapNodeRes.getGatherMethodName())
                    .fileds(fileds)
                    .build());
        }
        // 返回采集节点表达式视图对象列表
        return gatherNodeExpressionVOS;
    }

    /**
     * 根据监控ID查询监控名称
     *
     *博客场景类比： 就像是 “通过文章ID查文章标题”：
     * monitorId = 文章ID
     * monitorDataMapDao.queryMonitorNameByMonitoryId(monitorId) = 数据库查询文章标题
     * 实现逻辑：
     * 你输入 monitorId。
     * 直接调用 monitorDataMapDao 去数据库查名字，返回即可。
     */
    @Override
    public String queryMonitoryNameByMonitoryId(String monitorId) {
        return monitorDataMapDao.queryMonitorNameByMonitoryId(monitorId);
    }

    /**
     * 把监控数据存到数据库，同时更新 Redis 里的计数。
     *
     *博客场景类比： 就像是 “发布一篇文章，并增加它的浏览量”：
     * monitorDataDao.insert(monitorDataReq); = 存文章
     * redisService.incr(cacheKey); = 增加文章浏览量
     * 实现逻辑：
     * 把 monitorDataEntity 里的数据转换成 MonitorData，存到数据库。
     * 计算 Redis 的 cacheKey，然后 incr() 增加计数。
     */
    @Override
    public void saveMonitoryData(MonitorDataEntity monitorDataEntity) {
        // 创建一个MonitorData对象来准备插入数据库的新监控数据
        MonitorData monitorDataReq = new MonitorData();

        // 设置监控数据的监控ID
        monitorDataReq.setMonitorId(monitorDataEntity.getMonitorId());
        // 设置监控数据的监控名称
        monitorDataReq.setMonitorName(monitorDataEntity.getMonitorName());
        // 设置监控数据的监控节点ID
        monitorDataReq.setMonitorNodeId(monitorDataEntity.getMonitorNodeId());
        // 设置监控数据的系统名称
        monitorDataReq.setSystemName(monitorDataEntity.getSystemName());
        // 设置监控数据的类名称
        monitorDataReq.setClazzName(monitorDataEntity.getClazzName());
        // 设置监控数据的方法名称
        monitorDataReq.setMethodName(monitorDataEntity.getMethodName());
        // 设置监控数据的属性名称
        monitorDataReq.setAttributeName(monitorDataEntity.getAttributeName());
        // 设置监控数据的属性字段
        monitorDataReq.setAttributeField(monitorDataEntity.getAttributeField());
        // 设置监控数据的属性值
        monitorDataReq.setAttributeValue(monitorDataEntity.getAttributeValue());

        // 将准备好的监控数据插入到数据库中
        monitorDataDao.insert(monitorDataReq);

        // 构造缓存键，用于统计特定监控ID和监控节点ID组合的数据计数
        String cacheKey = Constants.RedisKey.monitor_node_data_count_key + monitorDataEntity.getMonitorId() + Constants.UNDERLINE + monitorDataEntity.getMonitorNodeId();

        // 使用Redis服务对缓存键的值进行递增操作，以实现数据计数
        redisService.incr(cacheKey);
    }
    /**
     * 查询监控数据映射实体列表（ = 查询文章分类）
     *
     * 本方法通过访问数据库，获取监控数据映射，并将其转换为实体对象列表返回
     * 主要涉及数据访问和对象转换操作
     *
     * @return 返回一个MonitorDataMapEntity对象列表，包含从数据库中获取的所有监控数据映射信息
     */
    @Override
    public List<MonitorDataMapEntity> queryMonitorDataMapEntityList() {
        // 从数据库中查询监控数据映射列表
        List<MonitorDataMap> monitorDataList = monitorDataMapDao.queryMonitorDataMapEntityList();

        // 初始化一个空的MonitorDataMapEntity列表，用于存放转换后的实体对象
        List<MonitorDataMapEntity> monitorDataMapEntities = new ArrayList<>();

        // 遍历查询到的监控数据映射列表
        for (MonitorDataMap monitorDataMap : monitorDataList) {
            // 将每个监控数据映射转换为MonitorDataMapEntity对象，并添加到实体列表中
            monitorDataMapEntities.add(MonitorDataMapEntity.builder()
                    .monitorId(monitorDataMap.getMonitorId())
                    .monitorName(monitorDataMap.getMonitorName())
                    .build());
        }

        // 返回转换后的MonitorDataMapEntity对象列表
        return monitorDataMapEntities;
    }

    /**
     * 查询监控流程数据，获取监控节点之间的流转关系。
     *
     *博客场景类比： 就像是 “获取文章的推荐关系”：
     * monitorDataMapNodeDao.queryMonitorNodeConfigByMonitorId(monitorId) = 查询 文章推荐列表
     * monitorDataMapNodeLinkDao.queryMonitorNodeLinkConfigByMonitorId(monitorId) = 查询 推荐关系
     * 实现逻辑：
     * 查询监控节点（文章）。
     * 查询监控节点之间的连接（推荐关系）。
     * 计算 Redis 里的流量数据，得到监控数据流动情况。
     */
    @Override
    public MonitorTreeConfigVO queryMonitorFlowData(String monitorId) {

        // 查询指定监控ID下的所有监控节点配置
        List<MonitorDataMapNode> monitorDataMapNodes = monitorDataMapNodeDao.queryMonitorNodeConfigByMonitorId(monitorId);
        // 查询指定监控ID下的所有监控节点链接配置
        List<MonitorDataMapNodeLink> monitorDataMapNodeLinks = monitorDataMapNodeLinkDao.queryMonitorNodeLinkConfigByMonitorId(monitorId);

        // 构建从监控节点ID到其所有目标节点ID的映射
        Map<String, List<String>> fromMonitorNodeIdToNodeIds = monitorDataMapNodeLinks.stream()
                .collect(Collectors.groupingBy(MonitorDataMapNodeLink::getFromMonitorNodeId,
                        Collectors.mapping(MonitorDataMapNodeLink::getToMonitorNodeId,
                                Collectors.toList())));

        // 初始化监控树配置中的节点列表
        List<MonitorTreeConfigVO.Node> nodeList = new ArrayList<>();
        // 遍历所有监控节点，构建监控树配置中的节点对象
        for (MonitorDataMapNode monitorDataMapNode : monitorDataMapNodes) {
            // 构建Redis缓存键，用于获取节点的数据计数
            String cacheKey = Constants.RedisKey.monitor_node_data_count_key + monitorId + Constants.UNDERLINE + monitorDataMapNode.getMonitorNodeId();
            // 从Redis中获取节点的数据计数
            Long count = redisService.getAtomicLong(cacheKey);

            // 构建并添加监控节点对象到节点列表
            nodeList.add(MonitorTreeConfigVO.Node.builder()
                    .monitorNodeId(monitorDataMapNode.getMonitorNodeId())
                    .monitorNodeName(monitorDataMapNode.getMonitorNodeName())
                    .loc(monitorDataMapNode.getLoc())
                    .color(monitorDataMapNode.getColor())
                    .monitorNodeValue(null == count ? "0" : String.valueOf(count))
                    .build());
        }

        // 初始化监控树配置中的链接列表
        List<MonitorTreeConfigVO.Link> linkList = new ArrayList<>();
        // 遍历所有监控节点链接，构建监控树配置中的链接对象
        for (MonitorDataMapNodeLink monitorDataMapNodeLink : monitorDataMapNodeLinks) {
            // 构建从节点的Redis缓存键，用于获取从节点的数据计数
            String fromCacheKey = Constants.RedisKey.monitor_node_data_count_key + monitorId + Constants.UNDERLINE + monitorDataMapNodeLink.getFromMonitorNodeId();
            // 从Redis中获取从节点的数据计数
            Long fromCacheCount = redisService.getAtomicLong(fromCacheKey);
            // 初始化到节点的数据计数
            Long toCacheCount = 0L;

            // 获取当前从节点的所有目标节点ID
            List<String> toNodeIds = fromMonitorNodeIdToNodeIds.get(monitorDataMapNodeLink.getFromMonitorNodeId());
            // 遍历所有目标节点，累加它们的数据计数
            for (String toNodeId : toNodeIds) {
                // 构建到节点的Redis缓存键，用于获取到节点的数据计数
                String toCacheKey = Constants.RedisKey.monitor_node_data_count_key + monitorId + Constants.UNDERLINE + toNodeId;
                toCacheCount += redisService.getAtomicLong(toCacheKey);
            }

            // 计算从节点到目标节点的数据差值
            long differenceValue = (null == fromCacheCount ? 0L : fromCacheCount) - toCacheCount;

            // 构建并添加监控链接对象到链接列表
            linkList.add(MonitorTreeConfigVO.Link.builder()
                    .fromMonitorNodeId(monitorDataMapNodeLink.getFromMonitorNodeId())
                    .toMonitorNodeId(monitorDataMapNodeLink.getToMonitorNodeId())
                    .linkKey(String.valueOf(monitorDataMapNodeLink.getId()))
                    .linkValue(String.valueOf(differenceValue <= 0 ? 0 : differenceValue))
                    .build());
        }

        // 构建并返回监控树配置对象
        return MonitorTreeConfigVO.builder()
                .monitorId(monitorId)
                .nodeList(nodeList)
                .linkList(linkList)
                .build();
    }
    /**
     * 查询监控数据实体列表
     *
     * @param monitorDataEntity 监控数据实体
     * @return 监控数据实体列表
     */
    @Override
    public List<MonitorDataEntity> queryMonitorDataEntityList(MonitorDataEntity monitorDataEntity) {
        MonitorData monitorDataReq = new MonitorData();
        monitorDataReq.setMonitorId(monitorDataEntity.getMonitorId());
        monitorDataReq.setMonitorName(monitorDataEntity.getMonitorName());
        monitorDataReq.setMonitorNodeId(monitorDataEntity.getMonitorNodeId());
        List<MonitorData> monitorDataList = monitorDataDao.queryMonitorDataList(monitorDataReq);
        List<MonitorDataEntity> monitorDataEntities = new ArrayList<>();
        for (MonitorData monitorData : monitorDataList) {
            MonitorDataEntity monitorDataEntityRes = new MonitorDataEntity();
            monitorDataEntityRes.setMonitorId(monitorData.getMonitorId());
            monitorDataEntityRes.setMonitorName(monitorData.getMonitorName());
            monitorDataEntityRes.setMonitorNodeId(monitorData.getMonitorNodeId());
            monitorDataEntityRes.setSystemName(monitorData.getSystemName());
            monitorDataEntityRes.setClazzName(monitorData.getClazzName());
            monitorDataEntityRes.setMethodName(monitorData.getMethodName());
            monitorDataEntityRes.setAttributeName(monitorData.getAttributeName());
            monitorDataEntityRes.setAttributeField(monitorData.getAttributeField());
            monitorDataEntityRes.setAttributeValue(monitorData.getAttributeValue());
            monitorDataEntities.add(monitorDataEntityRes);
        }
        return monitorDataEntities;
    }
    /**
     * 更新监控流程设计器数据，修改监控节点的流程图结构。
     *
     *博客场景类比： 就像是 “调整文章分类”：
     * monitorDataMapNodeDao.updateNodeConfig(monitorDataMapNodeReq); = 修改文章分类
     * monitorDataMapNodeLinkDao.insert(monitorDataMapNodeLinkReq); = 更新分类层级结构
     * 实现逻辑：
     * 先更新监控节点的配置信息（修改文章的分类）。
     * 再更新监控节点的连接关系（修改分类的父子关系）。
     * 事务控制 确保数据不会一半成功一半失败。
     */
    @Override
    public void updateMonitorFlowDesigner(MonitorFlowDesignerEntity monitorFlowDesignerEntity) {
        // 执行事务操作，确保数据一致性
        transactionTemplate.execute(status -> {
            try {
                // 获取并遍历节点列表，更新每个节点的配置
                List<MonitorFlowDesignerEntity.Node> nodeList = monitorFlowDesignerEntity.getNodeList();
                for (MonitorFlowDesignerEntity.Node node : nodeList) {
                    // 创建并设置节点请求对象
                    MonitorDataMapNode monitorDataMapNodeReq = new MonitorDataMapNode();
                    monitorDataMapNodeReq.setMonitorId(monitorFlowDesignerEntity.getMonitorId());
                    monitorDataMapNodeReq.setMonitorNodeId(node.getMonitorNodeId());
                    monitorDataMapNodeReq.setLoc(node.getLoc());
                    // 更新节点配置
                    monitorDataMapNodeDao.updateNodeConfig(monitorDataMapNodeReq);
                }

                // 获取链接列表，先删除现有的链接，再插入新的链接
                List<MonitorFlowDesignerEntity.Link> linkList = monitorFlowDesignerEntity.getLinkList();
                // 删除监控ID相关的所有链接，为后续重新插入做准备
                monitorDataMapNodeLinkDao.deleteLinkFromByMonitorId(monitorFlowDesignerEntity.getMonitorId());
                for (MonitorFlowDesignerEntity.Link link : linkList) {
                    // 创建并设置链接请求对象
                    MonitorDataMapNodeLink monitorDataMapNodeLinkReq = new MonitorDataMapNodeLink();
                    monitorDataMapNodeLinkReq.setMonitorId(monitorFlowDesignerEntity.getMonitorId());
                    monitorDataMapNodeLinkReq.setFromMonitorNodeId(link.getFrom());
                    monitorDataMapNodeLinkReq.setToMonitorNodeId(link.getTo());
                    // 插入新的链接
                    monitorDataMapNodeLinkDao.insert(monitorDataMapNodeLinkReq);
                }
                // 事务成功完成，返回1表示成功
                return 1;
            } catch (Exception e) {
                // 发生异常时，设置事务回滚
                status.setRollbackOnly();
                // 将异常继续抛出，以便上层处理
                throw e;
            }
        });

    }
}
