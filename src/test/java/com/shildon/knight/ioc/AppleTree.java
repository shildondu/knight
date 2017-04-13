package com.shildon.knight.ioc;

import com.shildon.knight.ioc.annotation.Bean;
import com.shildon.knight.ioc.annotation.Inject;

/**
 * Created by Shildon on 2017/4/13.
 */
@Bean
public class AppleTree {
    @Inject
    private Fruit fruit;

    public Fruit getFruit() {
        return fruit;
    }

    public void setFruit(Fruit fruit) {
        this.fruit = fruit;
    }
}
