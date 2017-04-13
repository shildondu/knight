package com.shildon.knight.ioc;

import com.shildon.knight.ioc.support.DefaultBeanFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Shildon on 2017/4/13.
 */
public class TestIoc {

    @Test
    public void testBeanFactory() {
        BeanFactory bf = new DefaultBeanFactory();
        Apple a = bf.getBean(Apple.class);
        Assert.assertNotNull(a);
        AppleTree at = bf.getBean(AppleTree.class);
        Assert.assertNotNull(at.getFruit());
    }

}
