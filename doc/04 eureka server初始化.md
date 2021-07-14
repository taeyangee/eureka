# server配置体系
- EurekaServerConfig：Configuration information required by the eureka server to operate.
  - 请求认证相关
  - 请求限流相关
  - 获取注册信息请求相关:详见应用实例注册流程
  - 自我保护机制相关
  - 注册的应用实例的租约过期相关
  - Eureka-Server 远程节点( 非集群 )读取相关
  - Eureka-Server 集群同步相关
- DefaultEurekaServerConfig
  - 默认读取eureka-server.properties
  - 基于配置文件的EurekaServerConfig实现

# eureka-server module
- build.gradle: 打包war，EurekaClientServerRestIntegrationTest中配合启动jetty。这里改一下，直接走source启动netty
- web.xml: EurekaBootStrap做服务拉起
```xml
 <!-- eureka服务拉起 -->
  <listener>
    <listener-class>com.netflix.eureka.EurekaBootStrap</listener-class>
  </listener>

  <!-- 检测 eureka服务是否可堆外提供服务 -->
  <filter>
    <filter-name>statusFilter</filter-name>
    <filter-class>com.netflix.eureka.StatusFilter</filter-class>
  </filter>

  <!-- 简单http head检查 -->
  <filter>
    <filter-name>requestAuthFilter</filter-name>
    <filter-class>com.netflix.eureka.ServerRequestAuthFilter</filter-class>
  </filter>
  <!-- 限流 -->
  <filter>
    <filter-name>rateLimitingFilter</filter-name>
    <filter-class>com.netflix.eureka.RateLimitingFilter</filter-class>
  </filter>
```
 
# EurekaBootStrap初始化流程（主要流程）
启动类：EurekaBootStrap
- EurekaBootStrap#initEurekaEnvironment：主要是set环境变懒
- EurekaBootStrap#initEurekaServerContext：
  - 创建server配置 EurekaServerConfig
  - 创建Eureka-Client: Eureka-Server 内嵌 Eureka-Client，用于和 Eureka-Server 集群里其他节点通信交互。
  - 创建PeerAwareInstanceRegistry：具备server集群感知能力的注册表, [PeerAwareInstanceRegistry体系](pic/InstanceRegistry.uml)
  - 创建 Eureka-Server 集群节点集合: PeerEurekaNodes
  - 根据以上各种组件，创建 Eureka-Server上下文（DefaultEurekaServerContext），并set到EurekaServerContextHolder中
  - EurekaServerContext做初始化：让集群准备信息（PeerEurekaNodes#start）, 用于初始化本地注册个表（PeerAwareInstanceRegistry#init）
  - 从其他 Eureka-Server 拉取注册信息（PeerAwareInstanceRegistry#syncup）
  - 注册server到监控（EurekaMonitors.registerAllStats();）
# Q
租约：租什么
region:是什么
registry: 注册信息