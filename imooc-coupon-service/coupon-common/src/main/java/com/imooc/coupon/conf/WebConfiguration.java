package com.imooc.coupon.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * <h1>http消息转换器<h1/>
 * @author xubr 2020/12/6
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        /**将java对象转成Http**/
        converters.clear();
        converters.add(new MappingJackson2HttpMessageConverter());


    }
}
