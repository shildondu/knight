package com.shildon.knight.ioc;

import com.shildon.knight.ioc.annotation.Bean;

/**
 * Created by Shildon on 2017/4/13.
 */
@Bean
public interface Fruit {

    String getTaste();

    void setTaste(String taste);

}
