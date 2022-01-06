package org.pf4j.demo.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.pf4j.ExtensionPoint;

import java.util.List;

/**
 * 处理订单相关操作
 *
 * @author zzq
 * */
public interface OrderGenerate extends ExtensionPoint {

    /**
     * 校验订单
     *
     * @param orderInfo 订单信息
     * @throws Exception 订单信息不通过应该抛出异常
     */
    void validate(ObjectNode orderInfo) throws Exception;

    /**
     * 修正订单数据
     *
     * @param orderInfo 订单信息
     * @return 修正后的订单信息
     */
    ObjectNode fix(ObjectNode orderInfo);

    /**
     * 持久化到数据库的操作
     *
     * @param orderInfo 订单信息
     * @return 插件不应该获取数据库连接，这里返回sql代表
     * */
    List<String> persistence(ObjectNode orderInfo);
}
