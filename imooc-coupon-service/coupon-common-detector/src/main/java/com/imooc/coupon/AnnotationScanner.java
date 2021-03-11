package com.imooc.coupon;

import com.imooc.coupon.annotation.IgnorePermission;
import com.imooc.coupon.annotation.ImoocCouponPermission;
import com.imooc.coupon.vo.PermissionInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author xubr
 * @title: AnnotationScanner
 * @projectName imooc-coupon
 * @description: <h1>接口权限信息扫描</h1>
 * @date 2021/3/1114:43
 */
@Slf4j
public class AnnotationScanner {

    private String pathPrefix;

    private static final String IMOOC_COUPON_PKG = "com.imooc.coupon";

    /**
     * <h2>构造所有 Controller 的权限信息<h2/>
     * @param mappingMap
     * @return
     */
    List<PermissionInfo> scanPermission(Map<RequestMappingInfo, HandlerMethod> mappingMap) {
        List<PermissionInfo> result = new ArrayList<>();
        mappingMap.forEach((mapInfo, method) ->
                result.addAll(buildPermission(mapInfo, method)));

        return result;
    }

    /**
     * <h2>构造 Controller 的权限信息<h2/>
     * @param handlerMethod handlerMethod {@link HandlerMethod} @RequestMapping 对应方法的详情,包括方法,类,参数
     * @Param mapInfo {@link org.springframework.web.bind.annotation.RequestMapping} @RequestMapping 对应的信息
     * Springmvc会在启动时扫描所有的@RequestMapping注解并封装成对应的RequestMappingInfo 是String数组
     */
    private List<PermissionInfo> buildPermission(RequestMappingInfo mapInfo, HandlerMethod handlerMethod) {

        Method javaMethod = handlerMethod.getMethod();

        //获得方法所在类的声明
        Class baseClass = javaMethod.getDeclaringClass();

        //如果该类不是com.imooc.coupon 包下的类直接忽略
        if (!isImoocCouponPackage(baseClass.getName())) {
            log.debug("ignore method: {}", javaMethod.getName());
            return Collections.emptyList();
        }

        //判断是否需要忽略此方法
        IgnorePermission ignorePermission = javaMethod.getAnnotation(
                IgnorePermission.class
        );

        //如果当前方法上没有该注解,则忽略权限校验
        if (null != ignorePermission) {
            log.debug("ignore method: {}", javaMethod.getName());
            return Collections.emptyList();
        }

        //取出权限注解
        ImoocCouponPermission couponPermission = javaMethod.getAnnotation(
                ImoocCouponPermission.class
        );

        if (null == couponPermission) {
            log.error("lack @ImoocCouponPermission -> {}#{}",
                    javaMethod.getDeclaringClass().getName(),
                    javaMethod.getName());
            return Collections.emptyList();
        }


        //这个是获取RequestMapping所有的Url
        Set<String> urlSet = mapInfo.getPatternsCondition().getPatterns();

        //获取所有的method
        boolean isAllMethods = false;
        Set<RequestMethod> methodSet = mapInfo.getMethodsCondition().getMethods();
        //如果
        if (CollectionUtils.isEmpty(methodSet)) {
            isAllMethods = true;
        }

        List<PermissionInfo> infoList = new ArrayList<>();

        for (String url : urlSet) {
            if (isAllMethods) {
                PermissionInfo info = buildPermissionInfo(
                        HttpMethodEnum.ALL.name(),
                        javaMethod.getName(),
                        this.pathPrefix + url,
                        couponPermission.readOnly(),
                        couponPermission.description(),
                        couponPermission.extra()
                );
                infoList.add(info);
                continue;
            }

            //支持部分的 http method
            for (RequestMethod method : methodSet) {
                PermissionInfo info = buildPermissionInfo(
                        method.name(),
                        javaMethod.getName(),
                        this.pathPrefix + url,
                        couponPermission.readOnly(),
                        couponPermission.description(),
                        couponPermission.extra()
                );
                infoList.add(info);
                log.info("permission detected: {}", info);
            }

        }
        return infoList;
    }

    private PermissionInfo buildPermissionInfo(String reqMethod, String javaMethod, String path,
                                               boolean readOnly, String description, String extra) {
        PermissionInfo info = new PermissionInfo();
        info.setMethod(reqMethod);
        info.setUrl(path);
        info.setIsRead(readOnly);
        info.setDescription(
                // 如果注解中没有描述, 则使用方法名称
                StringUtils.isEmpty(description) ? javaMethod : description
        );
        info.setExtra(extra);
        return info;
    }

    /**
     * <h2>判断当前类是否在我们定义的包中<h2/>
     * @param className
     * @return
     */
    private boolean isImoocCouponPackage(String className) {
        return className.startsWith(IMOOC_COUPON_PKG);
    }


}
