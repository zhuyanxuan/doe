package com.mmc.dubbo.doe.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public class StandardExecutorClassLoader extends URLClassLoader {

    public StandardExecutorClassLoader() {
        // 将 Parent 设置为 null
        super(new URL[] {}, null);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // 测试时可打印看一下
        System.out.println("Class loader: " + name);
        return super.loadClass(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch(ClassNotFoundException e) {
            return StandardExecutorClassLoader.class.getClassLoader().loadClass(name);
        }
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }

}
