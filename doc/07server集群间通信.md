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

#####  http任务批处理
![](pic/http%20任务执行.png )
不同于一般情况下，任务提交了立即同步或异步执行，任务的执行拆分了三层队列：

第一层，接收队列( acceptorQueue )，重新处理队列( reprocessQueue )。
- 蓝线：分发器在收到任务执行请求后，提交到接收队列，任务实际未执行。
- 黄线：执行器的工作线程处理任务失败，将符合条件( 见 「3. 任务处理器」 )的失败任务提交到重新执行队列。

第二层，待执行队列( processingOrder )
- 粉线：接收线程( Runner )将重新执行队列，接收队列提交到待执行队列。

第三层，工作队列( workQueue )
- 粉线：接收线程( Runner )将待执行队列的任务根据参数( maxBatchingSize )将任务合并成批量任务，调度( 提交 )到工作队列。
- 黄线：执行器的工作线程池，一个工作线程可以拉取一个批量任务进行执行。

三层队列的好处：
- 接收队列，避免处理任务的阻塞等待。
- 接收线程( Runner )合并任务，将相同任务编号( 是的，任务是带有编号的 )的任务合并，只执行一次。
- Eureka-Server 为集群同步提供批量操作多个应用实例的接口，一个批量任务可以一次调度接口完成，避免多次调用的开销。当然，这样做的前提是合并任务，这也导致 Eureka-Server 集群之间对应用实例的注册和下线带来更大的延迟。毕竟，Eureka 是在 CAP 之间，选择了
