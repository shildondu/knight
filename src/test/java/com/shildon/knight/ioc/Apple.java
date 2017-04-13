package com.shildon.knight.ioc;

import com.shildon.knight.ioc.annotation.Bean;

/**
 * Created by Shildon on 2017/4/13.
 */
@Bean
public class Apple implements Fruit {

    private String taste;

    @Override
    public String getTaste() {
        return taste;
    }

    @Override
    public void setTaste(String taste) {
        this.taste = taste;
    }

}
