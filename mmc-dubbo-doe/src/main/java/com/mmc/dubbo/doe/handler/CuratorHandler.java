/*
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Founder. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the agreements
 * you entered into with Founder.
 *
 */
package com.mmc.dubbo.doe.handler;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.registry.zookeeper.ZookeeperRegistry;
import com.alibaba.dubbo.remoting.zookeeper.ZookeeperClient;
import com.alibaba.dubbo.remoting.zookeeper.curator.CuratorZookeeperTransporter;
import com.mmc.dubbo.doe.cache.MethodCaches;
import com.mmc.dubbo.doe.cache.UrlCaches;
import com.mmc.dubbo.doe.dto.MethodModelDTO;
import com.mmc.dubbo.doe.model.ServiceModel;
import com.mmc.dubbo.doe.model.UrlModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Joey
 * @date 2018/6/15 10:21
 */
public class CuratorHandler {

    private final String protocol;
    private final String host;
    private final int port;
    private ZookeeperClient zkClient;
    private ZookeeperRegistry registry;
    private String root = "/dubbo";

    public CuratorHandler(String protocol, String host, int port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

    public void doConnect() throws NoSuchFieldException, IllegalAccessException, InterruptedException {

        CuratorZookeeperTransporter zookeeperTransporter = new CuratorZookeeperTransporter();
        URL url = new URL(protocol, host, port);

        registry = new ZookeeperRegistry(url, zookeeperTransporter);

        Thread.sleep(10000);
        Field field = registry.getClass().getDeclaredField("zkClient");
        field.setAccessible(true);
        zkClient = (ZookeeperClient) field.get(registry);

    }

    public List<ServiceModel> getInterfaces() {
        List<ServiceModel> ret = new ArrayList<>();
        List<String> list = zkClient.getChildren(root);
        for (int i = 0; i < list.size(); i++) {
            ServiceModel model = new ServiceModel();
            model.setServiceName(list.get(i));
            ret.add(model);
        }

        return ret;
    }

    public List<UrlModel> getProviders(String interfaceName) {

        Map<String, String> map = new HashMap<>();
        map.put(Constants.INTERFACE_KEY, interfaceName);
        URL url = new URL(protocol, host, port, map);
        List<URL> list = registry.lookup(url);

        return UrlCaches.cache(interfaceName, list);
    }

    public List<MethodModelDTO> getMethods(String interfaceName,String version) throws ClassNotFoundException {

        Class<?> clazz = Class.forName(interfaceName);
        Method[] methods = clazz.getMethods();
        // 缓存一份，方便下次调用
        return MethodCaches.cache(version+interfaceName, methods);

    }

    public void close() {
        registry.destroy();
    }

    public boolean isAvailable() {
        return registry.isAvailable();
    }
}
