package com.mmc.dubbo.doe.classloader;

import com.google.common.collect.Maps;

import java.util.Map;

public class ClassLoaderProxy {

    private static Map<String,StandardExecutorClassLoader> data = Maps.newHashMap();

    public static StandardExecutorClassLoader getClassLoader(String version) {
        if(data.containsKey(version)){
            return data.get(version);
        }else{
            data.put(version, new StandardExecutorClassLoader());
            return data.get(version);
        }
    }

}
