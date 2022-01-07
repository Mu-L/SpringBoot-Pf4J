/*
 * Copyright (C) 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pf4j.demo.order;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.pf4j.PluginWrapper;
import org.pf4j.RuntimeMode;
import org.pf4j.demo.api.Const;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.demo.api.OrderGenerate;

import java.util.*;

/**
 * @author Decebal Suiu
 */
public class StandardOrderPlugin extends Plugin {

    public StandardOrderPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("StandardOrder.start()");
        // for testing the development mode
        if (RuntimeMode.DEVELOPMENT.equals(wrapper.getRuntimeMode())) {
            System.out.println("StandardOrder".toUpperCase(Locale.ROOT));
        }
    }

    @Override
    public void stop() {
        System.out.println("StandardOrder.stop()");
    }

    @Extension
    public static class StandardOrderOperation implements OrderGenerate {

        @Override
        public void validate(ObjectNode orderInfo) throws Exception {
            if (orderInfo.path(Const.PRICE).asDouble() < 0) {
                throw new Exception("价格不能小于 0");
            }
            if (orderInfo.path(Const.COUNT).asInt() < 0) {
                throw new Exception("数量不能小于 0");
            }
            if (orderInfo.path(Const.BUYER).asDouble() < 0) {
                throw new Exception("买家id");
            }
            String s = orderInfo.path(Const.ADDRESS).asText();
            if (s == null || s.isEmpty()) {
                throw new Exception("收货地址不能为空");
            }
            if (s.length() < 6) {
                throw new Exception("收货地址长度不能小于6");
            }
        }

        @Override
        public ObjectNode fix(ObjectNode orderInfo) {
            orderInfo.put(Const.DATE_CREATED, System.currentTimeMillis());
            return orderInfo;
        }

        @Override
        public List<String> persistence(ObjectNode orderInfo) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("INSERT INTO ORDER (")
                    .append(" '").append(Const.PRICE.toUpperCase(Locale.ROOT)).append("', ")
                    .append("'").append(Const.COUNT.toUpperCase(Locale.ROOT)).append("', ")
                    .append("'").append(Const.BUYER.toUpperCase(Locale.ROOT)).append("', ")
                    .append("'").append(Const.ADDRESS.toUpperCase(Locale.ROOT)).append("', ")
                    .append("'").append(Const.DATE_CREATED.toUpperCase(Locale.ROOT)).append("'")
                    .append(" ) VALUE (")
                    .append(orderInfo.path(Const.PRICE).asDouble()).append(" ,")
                    .append(orderInfo.path(Const.COUNT).asInt()).append(" ,")
                    .append(orderInfo.path(Const.BUYER).asInt()).append(" ,")
                    .append("'").append(orderInfo.path(Const.ADDRESS).asText()).append("' ,")
                    .append(orderInfo.path(Const.DATE_CREATED.toUpperCase(Locale.ROOT)).asLong()).append(" )");
            return Collections.singletonList(stringBuilder.toString());
        }
    }

}
