package com.shildon.knight.aop;

import com.shildon.knight.aop.support.AbstractMethodInterceptor;
import com.shildon.knight.aop.support.CglibProxyFactory;
import com.shildon.knight.aop.support.ProxyMethod;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Shildon on 2017/4/12.
 */
public class TestAOP {

    public List<MethodInvocator> createInterceptors() throws NoSuchMethodException {
        List<MethodInvocator> ami = new LinkedList<>();
        Class<TestAOPProxyMethod> clazz = TestAOPProxyMethod.class;
        Method method = clazz.getMethod("pre");
        ProxyMethod proxyMethod = new ProxyMethod("say", method);
        ami.add(new AbstractMethodInterceptor(proxyMethod) {
            @Override
            public void beforeMethod(Method method, Object targetObject, Object[] targetParams) {
                try {
                    proxyMethod.getMethod().invoke(new TestAOPProxyMethod());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
        return ami;
    }

    @Test
    public void test() throws NoSuchMethodException {
        long start = System.currentTimeMillis();
        ProxyFactory cglib = new CglibProxyFactory(TestAOPDemo.class, createInterceptors());
        TestAOPDemo tad = cglib.getProxy();
        tad.say();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

}
