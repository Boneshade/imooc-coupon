# imooc-coupon
## Redis Pipeline详解

### 一.pipeline出现的背景

redis客户端执行一条命令分为4个过程:

发送命令->命令排队->命令执行->返回结果

这个过程称为Round trip time(简称RTP,往返时间),mget,mset有效节约了RTT,但大部分命令(如hgetall,并没有mhgettall)不支持批量操作,需要消耗N次RTT,这个时候需要pipeline来解决这个问题

 Pipeline 指的是管道技术,指的是客户端允许将多个请求依次发送给服务器,过程中而不需要等待请求的回复,在最后一并读取结果即可。

