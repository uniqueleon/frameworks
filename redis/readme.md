# 双捷云 REDIS 使用教程



## 第一步 引入依赖  

项目POM文件中只需要加入以下依赖:  

    <!-- 加入core依赖  -->
    <dependency>
        <groupId>com.sjsc</groupId>
        <artifactId>sj-cloud-core</artifactId>
        <version>${framework-version}</version>
    </dependency>
    <!-- 加入redis依赖  -->
    <dependency>
        <groupId>com.sjsc</groupId>
        <artifactId>sj-cloud-redis</artifactId>
        <version>${framework-version}</version>
    </dependency>
    

## 配置disconf
项目需要使用disconf来读取配置

    是否用户远程配置  
    enable.remote.conf=true  
    配置服务器的实际url地址  
    conf_server_host=http://disconf.bsmdev.itsjsc.com:8898/  
    当前使用的配置版本号  
    version=1_0_0_0  
    当前的APP的名字(类似于spring.app.name)  
    app=ELASTIC_JOB_DEMO  
    运行环境(类似于spring.app.profiles)  
    env=rd  
    是否开启debug模式  
    debug=true  
    是否忽略某些配置  
    ignore=  
    当配置读取失败时，配置服务器重试次数  
    conf\_server\_url\_retry\_times=1  
    配置服务器重试休眼时间  
    conf\_server\_url\_retry\_sleep\_seconds=1  

本地运行的过程中，可能会出现java.net.UnkownHostException : zk-host 异常。这是因为disconf默认会从远端配置服务器中拉取zookeeper服务器的地址进行配置项的动态更新，而目前这部分的连接信息是直接配置在docker镜像中，如果项目运行在docker容器环境中，这不是什么问题。如果不是运行在docker中，就需要在本机的hosts文件中配置zk-host。  

例如把zk-host配置为localhost，然后再在本地起一个nginx,在nginx作如下配置


    stream {  
      server {  
        listen       2181;  
        proxy_pass   zookeeper;  
      }  
      upstream zookeeper {  
         server zookeeper.bsmdev.itsjsc.com:8897;  
      }  
    }  

就可以把本地2181端口指向远端服务器的8897端口（该端口可访问到远端redis容器的2181端口）。  

## 连接Redis（哨兵集群 + redis数据分片集群）  

目前redis 的部署拓朴架构为 ： 哨兵集群 + 多个分片组 + 分片集群（一主多从）  

disconf 服务器上有redis.conf配置  

    redis_open=true   #是否开启redis
    sentinel_url=120.79.60.70:26390   #哨兵服务器地址，多个用逗号隔开
    masters=mymaster  #分片组
    password=redis123456  #登陆密码
    statlog_open=false  #是否开启日志
    debug_open=true  #是否开启调试模式
    localcache_refreshtime=900000  #本地缓存（内存）自动刷新时间
    localcache_open=true  #是否开启本地缓存
    localcache_refresh_open=true  #本地缓存自动刷新是否开启
    localcache_waitqueue_timeout=60000  #本地缓存等待队列超时时间
    localcache_waitqueue_checktime=20000  #本地缓存等待队列唤醒间隔

当代码连上哨兵机，哨兵机会返回实际后端的分片组以及组内Master ,slaves 机的情况。在Docker容器内，这些机器的IP均为docker虚拟的IP，所以如果目标应用在容器外部署时（可在application.yml中配置sjsc.framework.redis.docker_env=false，默认为false），框架会自动将不可达IP，按如下规则进行转译为主机名。

MASTER : {分组名}\_M\_{序号，从0开始}  
SLAVES : {分组名}\_S\_{序号，从0开始}  

因此，目标应用宿主机必须在hosts文件或所在的DNS服务器上面，配置 这些虚拟主机的IP。
如在hosts中添加

    120.79.60.70 mymaster_M_0
    120.79.60.70 mymaster_S_0
    
指向真实的IP地址

## 使用RedisManager 操作Redis  

    RedisManage redisManager = RedisManage.getInstance();
    ... // 操作redis，详情请参看RedisManage 接口

## 高级用法

未完待续
