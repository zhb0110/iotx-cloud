# iotx-cloud

## 平台简介
微服务版本，优先维护，参照ruoyi
参照若依微服务模板建设的模板
不要大而全，要简单，快速。

* 采用前后端分离的模式，微服务版本前端，
* 后端采用Spring Boot/Spring Cloud & Alibaba
* 注册中心、配置中心选型Nacos，权限认证使用Redis
* 流程控制框架选型Sentinel，分布式事务选型Seata


## 系统模块

~~~
com.iotx     
├── iotx-ui              // 前端框架 [80]
├── iotx-gateway         // 网关模块 [8080]
├── iotx-auth            // 认证中心 [9200]
├── iotx-api             // 服务间调用契约（调用方/提供方共同依赖）。  
│       └── iotx-api-system                          // 系统接口
├── iotx-common          // 无状态工具（所有模块依赖），  封装层：在 common 下建子模块（如 common-rabbitmq），处理配置、模板类。  
│       └── iotx-common-core                         // 核心模块
│       └── iotx-common-datascope                    // 权限范围
│       └── iotx-common-datasource                   // 多数据源
│       └── iotx-common-log                          // 日志记录，可接入Elasticsearch，异步日志写入(需搭配mq)
│       └── iotx-common-redis                        // 缓存服务
│       └── iotx-common-seata                        // 分布式事务
│       └── iotx-common-security                     // 安全模块
│       └── iotx-common-sensitive                    // 数据脱敏
│       └── iotx-common-swagger                      // 系统接口
│       └── iotx-common-mq                           // 新增：消息队列，接入rabbitmq
│       └── iotx-common-mqtt                         // 新增：mqtt，接入rabbitmq的mqtt
│       └── iotx-common-influxdb                     // 新增：influxdb，接入influxdb数据库
├── iotx-modules         // 独立业务服务（包含业务逻辑 + 数据库访问）。    业务层：在 modules 中具体使用，保持业务与技术解耦。
│       └── iotx-system                              // 系统模块 [9201]
│       └── iotx-gen                                 // 代码生成 [9202]
│       └── iotx-job                                 // 定时任务 [9203]
│       └── iotx-file                                // 文件服务 [9300]
│       └── iotx-modules-iot                         // 新增：物联网模块 [9204]，可使用iotx-common-mqtt模块
│       └── iotx-modules-search                      // 新增：搜索模块 [9205]，可直接集成ES客户端，避免放在common-log中（职责不同）
├── iotx-visual          // 图形化管理模块，监控类服务（可选部署）。
│       └── iotx-visual-monitor                      // 监控中心 [9100]，可支持写入 influxdb数据
├──pom.xml                // 公共依赖

1. iotx-mqtt-adapter微服务：![img.png](img.png)
    废弃，改为以下：
    ● 客户端封装：在 ruoyi-common 下新建 ruoyi-common-mqtt，封装连接、发布/订阅逻辑。  
    ● 业务使用：物联网相关业务模块（如 ruoyi-modules-iot）依赖此模块，实现具体消息处理。
2. iotx-timeseries时序数据微服务：![img_1.png](img_1.png)
    废弃，改为以下：
    ● 写入端：监控数据采集 → 放入 ruoyi-visual-monitor（扩展其监控能力）。  
    ● 查询端：若需业务查询（如报表），在对应业务模块引入 InfluxDB 客户端。  
    ● 公共配置：InfluxDB 连接池 → 放入 ruoyi-common-core 或新建 ruoyi-common-influxdb。
3. iotx-logging日志记录微服务：![img_2.png](img_2.png)
    废弃，改为以下：
    ● 日志存储：扩展 ruoyi-common-log，将日志异步写入 ES（需搭配 MQ）。  
    ● 业务搜索：在业务模块（如 ruoyi-modules-search）直接集成 ES 客户端，避免放在 common-log（职责不同）。
4. docker微服务内部+compose：![img_3.png](img_3.png)
   5. 代码提交，自动构建镜像，测试环境，通过，生产环境滚动更新
   6. 添加nginx演示
~~~

## 架构疑问
疑问1：file 为何放在业务模块而非通用模块？
    ● 正确设计：file 是一个 独立业务服务（需部署、有数据库、提供 REST API），而通用模块 (common) 仅包含 无状态工具（JAR 包形式）。  
    ● 若将文件功能抽成通用工具，可将其代码放入 ruoyi-common-core，但服务实现仍属于业务模块。
疑问2：接口模块（api）为何独立存在？
    ● 核心价值：解耦服务调用方和提供方。  
    ● 若合并到 visual 或业务模块：  
        ○ 会导致 循环依赖（如监控中心依赖业务模块，业务模块又需调用监控接口）。  
        ○ 破坏模块职责单一性（visual 应专注监控，而非管理接口契约）。
    ● 最佳实践：接口模块作为 轻量级契约包，被调用方（业务模块）和调用方（网关/其他业务）共同依赖。

## 内置功能

~~1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  通知公告：系统通知公告信息发布维护。
9.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 在线用户：当前系统中活跃用户状态监控。
12. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
13. 代码生成：前后端代码的生成（java、html、xml、sql）支持CRUD下载 。
14. 系统接口：根据业务代码自动生成相关的api接口文档。
15. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
16. 在线构建器：拖动表单元素生成相应的HTML代码。
17. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。~~

## 在线体验

- admin/admin123
- 陆陆续续收到一些打赏，为了更好的体验已用于演示服务器升级。谢谢各位小伙伴。

~~演示地址：http://ruoyi.vip  
文档地址：http://doc.ruoyi.vip~~

## 演示图


## iotx-cloud微服务交流群
无