package com.shildon.knight.util;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Shildon on 2017/4/12.
 */
public class TestReflectUtil {

    @Test
    public void testInstantiateBean() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        TestReflectUtilDemo trud = (TestReflectUtilDemo) ReflectUtil.instantiateBean(TestReflectUtilDemo.class);
        trud.say();
    }

}

class TestReflectUtilDemo {

    String str;

    public TestReflectUtilDemo() {
        str = "hello";
    }

    void say() {
        System.out.println(str);
    }

}
