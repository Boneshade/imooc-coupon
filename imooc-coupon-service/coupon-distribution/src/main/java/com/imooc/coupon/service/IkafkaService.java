package com.imooc.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author xubr 2020/12/30
 */
public interface IkafkaService {

    /**
     * <h1>消费优惠券 Kafka 消息<h1/>
     * @param record{@link ConsumerRecord}
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record);

}
