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