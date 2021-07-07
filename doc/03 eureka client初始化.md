# client的大致初始化流程
![](pic/03Eureka-Client%20自身初始化的过程.png)
- 创建 EurekaInstanceConfig 对象
- 使用 EurekaInstanceConfig对象 创建 InstanceInfo对象
- 使用 EurekaInstanceConfig对象 + InstanceInfo对象 创建 ApplicationInfoManager对象
- 创建 EurekaClientConfig对象
- 使用 ApplicationInfoManager对象 + EurekaClientConfig对象 创建 EurekaClient对象

# eureka-client 配置
eureka习惯把config做成接口, 变动不大的前提下，语义更好
## eureka-client package
- com.netflix.appinfo: 每个应用只eureka中都被表征为一个appinfo。 xxxConfig就是静态配置、xxxInfo就是运行时信息
- com.netflix.discovery: DiscoveryClient 比较重要的就
## EurekaInstanceConfig 体系
- vs EurekaClientConfig
 - EurekaInstanceConfig，重在应用实例，例如，应用名、应用的端口等等。此处应用指的是，Application Consumer 和 Application Provider。
 - EurekaClientConfig，重在Eureka-Client，例如， 连接的 Eureka-Server 的地址、获取服务提供者列表的频率、注册自身为服务提供者的频率等等。
 - EurekaServerConfig， 重在Server，比如server集群构建、限流、请求认证、client续约、自我保护机制
- [EurekaInstanceConfig 体系图](pic/03EurekaInstanceConfig.uml)
## InstanceInfo
## ApplicationInfoManager
## EurekaClientConfig
大致在以下4个方面提供相关配置
- 使用 DNS 获取 Eureka-Server URL 相关
- 直接获取 Eureka-Server URL 相关
- 发现：从 Eureka-Server 获取注册信息相关
- 注册：向 Eureka-Server 注册自身服务
## EurekaClientConfig
- 传输层相关的配置 

# eureka-client 初始化流程 
- [EurekaClient体系](pic/EurekaClient.uml)
- EurekaClient初始化流程：入口在`DiscoveryClient#DiscoveryClient`，
  - 入参：ApplicationInfoManager、EurekaClientConfig、AbstractDiscoveryClientOptionalArgs（基本是null）
  - 顺序完成了以下动作
      - AbstractDiscoveryClientOptionalArgs中抽取配置（装配一下 健康检查处理器、实例注册前处理器）
      - 赋值 ApplicationInfoManager、EurekaClientConfig
      - 赋值 BackupRegistry ( fallback注册表， 猜：如果client同server同步注册表不成功， 就fallback顶上)
      - 初始化 InstanceInfoBasedUrlRandomizer （不知道干嘛的）
      - 初始化 Applications 在本地的缓存
      - 赋值 关注那些Region 集合的注册信息
      - 初始化拉取的监控、心跳的监控
      - 初始化线程池 （updating service urls、scheduling a TimedSuperVisorTask）
      - 初始化 heartbeatExecutor（心跳执行器）、cacheRefreshExecutor（刷新执行器）
      - 初始化 Eureka 网络通信相关（EurekaTransport）
      - 初始化 InstanceRegionChecker （不知道干嘛的）
      - 从Eureka-Server拉取注册信息，失败就走fetchRegistryFromBackup
      - 动作：执行Eureka-Server注册前处理器（注：初始化流程并没有注册动作？）
      - 动作：开启定时任务（开启cacheRefreshExecutor、heartbeatExecutor，创建instanceInfoReplicator、statusChangeListener）
      - 动作：向 Servo 注册监控 （Monitors.registerObject？）