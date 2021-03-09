package com.imooc.coupon.service;

import com.imooc.coupon.dao.PathRepository;
import com.imooc.coupon.entity.Path;
import com.imooc.coupon.vo.CreatePathRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xubr
 * @title: PathService
 * @projectName imooc-coupon
 * @description: <h1>路径相关功能实现</h1>
 * @date 2021/3/916:19
 */
@Slf4j
@Service
public class PathService {

    /**
     * Path Repository
     */
    private final PathRepository pathRepository;

    @Autowired
    public PathService(PathRepository pathRepository) {
        this.pathRepository = pathRepository;
    }

    /**
     * <h2>添加新的 path 到数据库表中<h2/>
     * @param request {@link CreatePathRequest}
     * @return Path 数据记录的主键
     */
    public List<Integer> createPath(CreatePathRequest request) {

        //路径创建请求对象的定义
        List<CreatePathRequest.PathInfo> pathInfos = request.getPathInfos();
        List<CreatePathRequest.PathInfo> validRequests =
                new ArrayList<>(request.getPathInfos().size());
        //所有创建路径的请求都属于同一模块下的
        List<Path> currentPaths = pathRepository.
                findAllByServiceName(pathInfos.get(0).getServiceName());

        if (!CollectionUtils.isEmpty(currentPaths)) {
            for (CreatePathRequest.PathInfo pathInfo : pathInfos) {
                boolean isValid = true;

                for (Path currentPath : currentPaths) {
                    if (currentPath.getPathPattern().equals(pathInfo.getPathPattern())
                            && currentPath.getHttpMethod().equals(pathInfo.getHttpMethod())) {
                        isValid = false;
                        break;
                    }
                }
                if (isValid) {
                    validRequests.add(pathInfo);
                }
            }
        } else {
            validRequests = pathInfos;
        }

        List<Path> paths = new ArrayList<>(validRequests.size());
        validRequests.forEach(p -> paths.add(new Path(
                p.getPathPattern(),
                p.getHttpMethod(),
                p.getPathName(),
                p.getServiceName(),
                p.getOpMode()
        )));

        return pathRepository.saveAll(paths)
                .stream().map(Path::getId).collect(Collectors.toList());
    }


}
