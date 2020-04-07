# spring-cloud
一、搭建一个父级工程
1)File——>New——>Projcet...
2)选择Spring Initializr,选择对应的JDK，Choose Initializr Server URL 选择 default——>Next
3)输入项目组Group:com.xxx；组件名称Artifact:xxxx；Type:选择Maven Project；修改自动生成的Package——>Next
4）选择你需要的Spring Boot版本，其他的先不选——>Next
5)Project Name工程名称，和组件名称Artifact一样；Project location:设置项目文件存放目录——>Finish
6)父级工程创建完成后删除src文件夹
7)修改pom文件（打包方式一定要改成pom）

二、搭建eureka server（注册中心）
　服务注册和发现模块
1.euraka-server创建：
　　1）File—>New—>Module...
　　2)选择Spring Initializr,选择对应的JDK，Choose Initializr Server URL 选择 default——>Next
　　3)输入项目组Group:com.xxx；组件名称Artifact:xxxx；Type:选择Maven Project；修改自动生成的Package——>Next
　　4) Dpendencies选择Spring Cloud Discovery—>Eureka Server,选择你需要的Spring Boot版本——>Next
 　 5）Project Name工程名称一般不做修改，和组件名称Artifact一样；Content root、Module file location 均按自动生成，不做修改——>Finish
 　 6）向EurekaServerApplication添加注解@EnableEurekaServer表明是一个eureka服务
    7）将application.properties修改为application.yml（重命名快捷键Shift+F6），修改完成后加入以下配置：
　　8）修改pom文件
          使用父工程spring-cloud的spring boot依赖
　　9）打开http://localhost：8761，即eureka server的网址，可以看见注册的服务
2.eureka-client=01创建同上
　　1）EurekaClient01Application中加入以下两个注解
　　　　@EnableEurekaClient
　　　　@RestController
　　2)application.yml配置
　　3）在启动类里面随便写一个接口
　　4）打开http://localhost：8762/hi?name=forezp,返回接口访问内容（上一个eureka server服务不要关闭）
 
三、ribbon+restTemplate-服务消费者
1.在微服务架构中，业务都会被拆分成一个独立的服务，服务与服务的通讯是基于http restful的。 Spring cloud有两种服务调用方式，一种是ribbon+restTemplate，另一种是feign。
2.ribbon是一个负载均衡客户端，可以很好的控制htt和tcp的一些行为。Feign默认集成了ribbon。
　　1）基于以上项目复制eureka-client-01创建eureka-client-02并修改yml中端口为8763并启动　
　　　　spring.application.name不修改，仍为eureka-client-01： 由于服务之间是根据此名称进行相互调用，所以此时表示 8763,8762对外提供一个服务，服务名为eureka-client-01=====>等同于一个小的集群
　　2)创建一个服务消费者：service-ribbon（创建步骤和之前一样这里就不截图了）
　　　　1.创建步骤：
　　　　　　a.File—>New—>Module...
　　　　　　b.选择Spring Initializr,选择对应的JDK，Choose Initializr Server URL 选择 default——>Next
　　　　　　c.输入项目组Group:com.xxx；组件名称Artifact:xxxx；Type:选择Maven Project；修改自动生成的Package——>Next
　　　　　　d.Dpendencies选择Spring Cloud Discovery—>Eureka Server,选择你需要的Spring Boot版本——>Next
　　　　　　e.Project Name工程名称一般不做修改，和组件名称Artifact一样；Content root、Module file location 均按自动生成，不做修改——>Finish
　　　　2.启动类中添加注解和方法
　　　　　　a.@EnableDiscoveryClient
　　　　　　b.通过LoadBalanced注解表明这个restRemplate开启负载均衡的功能
　　　　　　c.编写测试类controller和service，主要调用是因为service中的RestTemplate
　　　　　　d.浏览器上多次访问http://localhost:8764/hi?name=forezp，浏览器交替显示：“hi forezp,i am from port:8762”，“hi forezp,i am from port:8763”；这说明我们通过调用restTemplate.getForObject("http://SERVICE-HI/hi?name="name,String.class")方法时，已经做了负载均衡，访问了不同端口的服务实例。

场景总结：
　　1.一个服务注册中心，eureka-server端口为8761
　　2.向服务注册中心eureka-server注册两个客户端服务：名称为eureka-client-01包含服务：8762/8763
　　3.向服务之策中心eureka-server注册消费者服务：service-ribbon端口：8764
　　4.当service-ribbon通过restTemplate调用eureka-client-01的hi接口时，因为用了ribbon负载均衡，会轮流的调用eureka-client-01：8762和8763两个端口的hi接口；
四、Feign-服务消费者
1.Feign是一个声明式的伪Http客户端，它使得写Http客户端变得更简单。
　　使用Feign，只需要创建一个接口并注解。它具有可插拔的注解特性，可使用Feign 注解和JAX-RS注解。
　　Feign支持可插拔的编码器和解码器。Feign默认集成了Ribbon，并和Eureka结合，默认实现了负载均衡的效果
2.简单理解：
　　·Feign 采用的是基于接口的注解
　　·Feign 整合了ribbon
　　1）创建一个服务消费者service-feign
　　　　a.application.yml指定文件程序名为service-feign，端口号为8765
　　　　b.pom文件中加入feign依赖入
　　　　c.启动类中加入@EnableFeignClients注解开启Feign的功能(@EnableDiscoveryClient和@EnableFeignClients)
　　　　d.编写测试类service和controller，feign基于接口——创建serviceTest
　　　　e.浏览器上多次访问http://localhost:8765/hi?name=Feign，浏览器交替显示：“hi ,i am from port:8762”，“hi Feign,i am from port:8763”；
五、断路器（Hystrix）
1.微服务架构中，根据业务分析来拆分一个个的服务，服务与服务之间可以相互调用(RPC)在Spring Cloud可以用RestTemplate+Ribbon和Feign来调用。为了保证其高可用，单个服务通常会集群部署。由于网络原因或者自身的原因，服务并不能保证100%可用，如果单个服务出现问题，调用这个服务就会出现线程阻塞，此时若有大量的请求涌入，Servlet容器的线程资源会被消耗完毕，导致服务瘫痪。服务与服务之间的依赖性，故障会传播，会对整个微服务系统造成灾难性的严重后果，这就是服务故障的“雪崩”效应。为了解决这个问题，业界提出了断路器模型。
2.Netflix开源了Hystrix组件，实现了断路模式，SpringCloud对这一组件进行了整合。
　　在微服务架构中，一个请求需要调用多个服务是非常常见的,叫底层的服务如果出现故障，会导致连锁故障。当对特定的服务的调用的不可用达到一个阈值（Hystrix是5秒20次断路器将会被打开。断路打开后，可用避免连锁故障，fallback方法可以直接返回一个固定值）。
3.Ribbon使用断路器：
　　1）添加断路器依赖:spring-cloud-starter-hystrix如果springboot是2.0及以上的新版本还需要加入一个依赖
　　2）启动类加注解@EnableHystrix开启Hystrix（@EnableDiscoveryClient和@EnableHystrix）
　　3)改造HelloRibbonService类，在hiService方法上加上@HystrixCommand注解
　　　　注意： 该注解对该方法创建了熔断器的功能，并指定了fallbackMethod熔断方法， 熔断方法直接返回了一个字符串，字符串为“hi，”+name+“，sorry,error!”，
　　4)断路测试：
      ·启动ribben访问
      ·关闭eureka-client-01，再访问，会得到短路由返回值
      ·eureka-client-01工程不可用的时候，service-ribbon调用 service-hi的API接口时，
       会执行快速失败，直接返回一组字符串，而不是等待响应超时，这很好的控制了容器的线程阻塞
　4.Feign中使用断路器：　　
　　1)Feign是自带断路器的，在D版本的Spring Cloud中，它没有默认打开。需要在配置文件中配置打开：feign.hystrix.enabled=true
　　2)在@FeignClient接口的注解中加上fallback的指定类HelloFeignServiceImpl
　　3)HelloFeignServiceImpl需要实现HelloFeignService接口，并注入到Ioc容器中（组件注解）
　　4)断路测试-同上
1.Hystrix Dashboard (断路器：Hystrix 仪表盘)：
　--仪表盘添加+ribbon和feign相同
　　--以ribbon为例：
　　1)添加依赖
　　2)主程序启动类中：
　　　·加入@EnableHystrixDashboard注解，开启hystrixDashboard
　　　·在spring版本2.0以上需要注入一个servlet（启动类中）
　　3)访问http://localhost:8764/hystrix
　　  ·第一个文本框输入http://localhost:8764/hystrix.stream 
　　4)点击monitor stream，进入下一个界面
　　  ·显示加载中load.... 
　　5)访问：http://localhost:8764/hi?name=forezp，查看监控界面
六、路由网关-zuul
1.在Spring Cloud微服务系统中，一种常见的负载均衡方式是：
1.在Spring Cloud微服务系统中，一种常见的负载均衡方式是：
　　1)客户端请求-->负载均衡（zuul、Ngnix）-->
　　2)服务网关（zuul集群）-->具体的服务器 -->
　　3)服务统一注册到高可用的服务注册中心集群
　　4)服务的所有的配置文件由配置服务管理, 配置服务的配置文件放在git仓库，方便开发人员随时改配置
2.Zuul的主要功能是路由转发和过滤器。路由功能是微服务的一部分， 比如／api/user转发到到user服务，/api/shop转发到到shop服务。 zuul默认和Ribbon结合实现了负载均衡的功能
3.创建service-zuul工程:
　　1)pom依赖：spring-cloud-starter-netflix-zuul
　　2)applicaton类
　　　　·加上注解@EnableZuulProxy，开启zuul的功能
　　　　·加上注解@EnableEurekaClient，注册进eureka服务
　　3)yml配置文件
    4)测试：
　　　　http://localhost:8769/api-a/hi?name=forezp 浏览器显示：hi forezp,i am from port:8762
　　　　http://localhost:8769/api-b/hi?name=forezp 浏览器显示：hi forezp,i am from port:8762
 4.zuul不仅只是路由，并且还能过滤，做一些安全验证,详见filter
 5.测试filter：
　　http://localhost:8769/api-a/hi?name=forezp 网页显示： token is empty
　　http://localhost:8769/api-a/hi?name=forezp&token=22 网页显示： hi forezp,i am from port:8762
七、分布式配置中心(Spring Cloud Config)
1.在分布式系统中，由于服务数量居多，为了方便服务配置文件统一管理，实时更新，所以需要分布式配置中心组件。在Spring Cloud中，有分布式配置中心组件spring cloud config，它支持配置服务放在配置服务的内存中（即本地），也支持放在远程Git仓库中。在spring cloud config 组件中，分两个角色，一是config server，二是config client。
2.创建config-server工程：
1）pom.xml添加依赖：spring-cloud-config-server
2）Application类加上@EnableA上@EnableConfigServer注解开启配置服务器的功能
3）配置文件修改指向git——文件为：config-client-dev.properties
4）测试
　　a）访问http://localhost:8888/config-client-dev.properties
　　　　展示其内容：foo: foo version 2
　　b）具体的映射关系测试请查看其配置文件
　　　　http请求地址和资源文件映射如下:
　　　　　　/{application}/{profile}[/{label}]
　　　　　　/{application}-{profile}.yml
　　　　　　/{label}/{application}-{profile}.yml
　　　　　　/{application}-{profile}.properties
　　　　　　/{label}/{application}-{profile}.properties
　　　　　　#{application}映射客户端的"spring.application.name"
　　　　　　#{profile}映射客户端的"spring.profiles.active"（逗号分隔列表）
3.创建一个config-client工程
　　1)pom.xml添加依赖.xml添加依赖：spring-cloud-starter-config
　　2)配置文件bootstrap.properties
　　3)程序的入口类，写一个API接口“／hi”，返回从配置中心读取的foo变量的值
　　4)测试
　　　　打开网址访问：http://localhost:8881/hi，网页显示：dev|å¼åèæ¨¡å¼éç½®
　　　　 这就说明，config-client从config-server获取了foo的属性，而config-server是从git仓库读取的
八、高可用的分布式配置中心(Spring Cloud Config)
1.当config-client很多时，可以将config-server做成集群，达成高可用
2.创建一个config-eureka-server工程，用作服务注册中心
　　1)eureka依赖
　　2)yml上，指定服务端口为8889，加上作为服务注册中心的基本配置
　　3)入口类： @EnableEurekaServer
3.改造config-server
　　1)pom.xml文件加上EurekaClient的起步依赖
　　2)配置文件application.yml，指定服务注册地址为http://localhost:8889/eureka/
　　3)最后需要在程序的启动类Application加上@EnableEurekaClient的注解
4.改造config-client
　　1）将其注册微到服务注册中心，作为Eureka客户端，需要pom文件加上起步依赖
　　2）配置文件bootstrap.properties，注意是bootstrap
　　　　加上服务注册地址为http://localhost：8889/eureka
　　　　spring.cloud.config.discovery.enabled是从配置中心读取文件。
　　　　spring.cloud.config.discovery.serviceId配置中心的serviceId，即服务名。
5.测试
　　这时发现，在读取配置文件不再写ip地址，而是服务名，这时如果配置服务部署多份，通过负载均衡，从而高可用。
　　1.依次启动eureka-server，config-server，config-client
　　2.访问网址：http://localhost：8889/
　　3.访问http://localhost：8881/hi,浏览器显示：dev|å¼åèæ¨¡å¼éç½®
