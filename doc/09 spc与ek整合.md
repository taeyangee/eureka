# spc eureka & eureka

可能是ek的server写的太渣，spc并没有秉承简单封装的哲学，spc ek server从ek那边copy了很多代码过来，ek client倒是复用了不少

但是ekserver实际上写的还可以
- eureka-client-1.9.25.jar：Netflix原生ek client
- eureka-core-1.9.25.jar: netflix远程ek core
- spring-cloud-netflix-eureka-client-2.2.5.RELEASE.jar：
- spring-cloud-netflix-eureka-server-2.2.5.RELEASE.jar：
- spring-cloud-starter-netflix-eureka-client-2.2.5.RELEASE.jar
- spring-cloud-starter-netflix-eureka-server-2.2.5.RELEASE.jar

# spc ek client
有eureka、ribbon.eureka，只关注前面
- config：跳过
- http：spc基于webclient、resttemplate实现了EurekaHttpClient
- loadbalancer：可能和ribbon有关，再说
- metadata：跳过
- reactive：跳过
- serviceregistry：需要了解spc的server registry体系，详见后

# spc的server registry体系
- 参考： [Spring Cloud：服务注册与发现机制、源码分析](https://blog.csdn.net/rambogototravel/article/details/106579811) 

#### spc的服务注册
通路一：@EnableEurekaClient 初始化client。 参考
- RefreshableEurekaClientConfiguration懒加载CloudEurekaClient 
- CloudEurekaClient  extends DiscoveryClient impl EurekaClient

通路二： @EnableDiscoveryClient import了AutoServiceRegistrationConfiguration
- AutoServiceRegistrationAutoConfiguration， 由spring-cloud-commons-2.2.5.RELEASE.jar自动注册
    - import了AutoServiceRegistrationConfiguration: 只是实例化了配置 AutoServiceRegistrationProperties
    - 要求装配AutoServiceRegistration、AutoServiceRegistrationProperties，看来主要还是 AutoServiceRegistration

AutoServiceRegistration
- 标记接口
- 但是 [EurekaAutoServiceRegistration](pic/EurekaAutoServiceRegistration.uml) 实现了该接口： 
    - 持有EurekaServiceRegistry、registration
    - start方法，整合在spring的生命周期中
        - 做初始化`this.serviceRegistry.register(this.registration);`
        - 发送 spc事件 InstanceRegisteredEvent
    - 监听 WebServerInitializedEvent， 也会调用 start
    - EurekaServiceRegistry#register -> reg.getApplicationInfoManager().setInstanceStatus更新本地实例信息为up    
    - DiscoveryClient#statusChangeListener 今天到状态变化 --》 instanceInfoReplicator.onDemandUpdate();  --> discoveryClient.register();
#### spc的服务发现
DiscoveryClient：spc的服务发现抽象
  - DiscoveryClient的使用者：spring gateway、org.springframework.cloud.loadbalancer.core子类的
  - EurekaDiscoveryClient实现了该接口：委托EurekaClient实现所有接口

