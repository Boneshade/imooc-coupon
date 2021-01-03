# imooc-coupon
## Redis Pipeline详解

### 一.pipeline出现的背景

redis客户端执行一条命令分为4个过程:

发送命令->命令排队->命令执行->返回结果

这个过程称为Round trip time(简称RTP,往返时间),mget,mset有效节约了RTT,但大部分命令(如hgetall,并没有mhgettall)不支持批量操作,需要消耗N次RTT,这个时候需要pipeline来解决这个问题

 Pipeline 指的是管道技术,指的是客户端允许将多个请求依次发送给服务器,过程中而不需要等待请求的回复,在最后一并读取结果即可。

# Ribbon和Feign

**微服务之间的服务调用**

***Ribbon 包括了两个部分:负载均衡算法+app_name 转具体的ip:port***

**Feign:定义接口,并在接口上添加注解,消费者通过调用接口的形式进行服务消费**

**Hystrix**:**服务雪崩是熔断器解决的最核心的问题**

**Hystrix的三个特性:断路器机制,FallBack,资源隔离**

**断路器机制:当Hystrix Command 请求后端服务失败数量超过一个阀值比例,断路器会切换到开路状态**

**FallBack:降级回滚策略**

**资源隔离:不同的微服务调用使用不同的线程池来管理**

