# Knight
## 简介
简单实现一个Web框架。

## 设计原则
* 惯例优于配置
* 基于Java注解

## 已实现简单功能
### IoC容器
1. 将注解`@bean`注在类上，表明该类的实例化由框架管理。用到以下所有功能的类都要加上该注解。
2. 将注解`@inject`注在成员变量上，表明该成员变量会根据类型进行自动注入。

### 基于Jdk和Cglib动态代理的AOP
1. 约定拦截器包名为**intercepter**。
2. 在代理类上注上`@proxy`注解，表示该类被代理。
3. 在**intercepter**包中的类的方法上使用`@BeforeMethod`，`@AfterMethod`，`@AfterException`实现代理的位置以及增强的功能。

### 定时任务
1. 约定定时任务报名为**schedule**。
2. 在**schedule**包中的类的方法上使用`@Scheduled`表明该方法为定时任务。

### 基于Cglib动态代理的声明式事务管理
1. 在需要声明式事务管理的类加上`@proxy`注解。
2. 在需要声明式事务管理的类的方法加上`@Transaction`注解。

### 基于前端控制器的Web前后端交互层
1. 约定控制器的包名为**controller**。
2. 在控制器的方法上用`@requestMapping`实现url映射匹配控制器。
