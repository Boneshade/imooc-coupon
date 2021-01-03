package com.imooc.coupon.service.Impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.dao.CouponDao;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.service.IkafkaService;
import com.imooc.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * <h1>Kafka 相关的服务接口实现</h1>
 * 核心思想:是将 Cache中的coupon 的状态变化同步到 DB中
 *
 * @author xubr 2021/1/3
 */
@Slf4j
@Component
public class KafkaServiceImpl implements IkafkaService {

    private final CouponDao couponDao;

    @Autowired
    public KafkaServiceImpl(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    /**
     * <h2>消费优惠券 kafka 消息<h2/>
     *
     * @param record{@link ConsumerRecord}
     */
    @Override
    @KafkaListener(topics = {Constant.TOPIC}, groupId = "imooc-coupon-1")
    @SuppressWarnings("all")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {

        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            CouponKafkaMessage couponInfo = JSON.parseObject(
                    message.toString(),
                    CouponKafkaMessage.class
            );

            log.info("Receive CouponKafkaMessage: {}", message.toString());
            CouponStatus status = CouponStatus.of(couponInfo.getStatus());
            //USABLE是需要先生成id保存到数据库,所以不需要让kafka异步回写,而expired的优惠券是需要重新生成id的所以需要kafka异步回写到数据库
            switch (status) {
                case USABLE:
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponInfo, status);
                    break;
                case USED:
                    processUsedCoupons(couponInfo, status);
                    break;
            }
        }
    }


    /**
     * <h2>处理已使用的用户优惠券<h2/>
     *
     * @param kafkaMessage
     * @param status
     */
    private void processUsedCoupons(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        //todo 给用户发送短信
        processCouponByStatus(kafkaMessage, status);
    }

    /**
     * <h2>处理过期的用户优惠券<h2/>
     *
     * @param kafkaMessage
     * @param status
     */
    private void processExpiredCoupons(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        //todo 给用户发送推送
        processCouponByStatus(kafkaMessage, status);
    }


    /**
     * <h2>根据状态处理优惠券信息<h2/>
     *
     * @param kafkaMessage
     * @param status
     */
    private void processCouponByStatus(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        List<Coupon> coupons = couponDao.findAllById(
                kafkaMessage.getIds()
        );

        if (CollectionUtils.isEmpty(coupons)
                || coupons.size() != kafkaMessage.getIds().size()) {
            log.error("Can Not Find Right Coupon Info:{}",
                    JSON.toJSONString(kafkaMessage));
            return;
        }
        coupons.forEach(c -> c.setStatus(status));
        log.info("CouponKafkaMessage Op Coupon Count: {}", couponDao.saveAll(coupons).size());

    }
}
