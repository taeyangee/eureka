# Q
- 集群间如何发现
- 如何同步注册信息：初始化拉取、实时同步

# 集群中通信
- Eureka-Server集群不区分主从节点，所有节点相同角色( 也就是没有角色 )，完全对等。
- Eureka-Client 可以向任意 Eureka-Client 发起任意读写操作，Eureka-Server 将操作复制到另外的 Eureka-Server 以达到最终一致性。 CAP中选择了AP

#### 关键抽象 PeerEurekaNodes
- 功能：负责管理所有集群节点的生命周期， 生命周期函数： start、stop

- taskExecutor:定时更新集群节点， 默认周期10min
- 核心函数：updatePeerEurekaNodes

#### 关键抽象 PeerEurekaNode
- 功能：负责将本地server接收到 注册、续约、下线、过钱、状态变更 都通给 一个特定的集群节点
- 组件：
    - serviceUrl; /* 实例url */
    - targetHost; /* 解析后的 实例url*/
    - replicationClient; /* 负责该targetHost的http client */
    - batchingDispatcher; /* 设计：请求批量发送 */
    - nonBatchingDispatcher; /* 请求单发*/
#### 初始化集群节点信息 PeerEurekaNodes
- 入口：DefaultEurekaServerContext#initialize --> PeerEurekaNodes#start

#### 初始化时，从任一PeerEurekaNodes拉取注册信息
EurekaBootStrap#initEurekaServerContext
PeerAwareInstanceRegistryImpl#syncUp()
#### 处理client请求，并将请求结果同步给PeerEurekaNodes

#### 