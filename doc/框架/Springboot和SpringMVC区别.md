---
title: Springboot和SpringMVC区别
url: https://www.yuque.com/tangsanghegedan/ilyegg/ipuq09
---

spring boot只是一个配置工具,整合工具,辅助工具.
springmvc是框架,项目中实际运行的代码
 
**Spring 框架**就像一个家族，有众多衍生产品例如 boot、security、jpa等等。但他们的基础都是Spring 的ioc和 aop，ioc 提供了依赖注入的容器， aop解决了面向横切面的编程，然后在此两者的基础上实现了其他延伸产品的高级功能。
**Spring MVC提供了一种轻度耦合的方式来开发web应用。**它是Spring的一个模块，是一个web框架。通过Dispatcher Servlet, ModelAndView 和 View Resolver，开发web应用变得很容易。解决的问题领域是网站应用程序或者服务开发——URL路由、Session、模板引擎、静态Web资源等等。
**Spring Boot实现了自动配置，降低了项目搭建的复杂度**。它主要是为了解决使用Spring框架需要进行大量的配置太麻烦的问题，所以它并不是用来替代Spring的解决方案，而是和Spring框架紧密结合用于提升Spring开发者体验的工具。同时它集成了大量常用的第三方库配置(例如Jackson, JDBC, Mongo, Redis, Mail等等)，Spring Boot应用中这些第三方库几乎可以零配置的开箱即用(out-of-the-box)。
Spring Boot只是承载者，辅助你简化项目搭建过程的。如果承载的是WEB项目，使用Spring MVC作为MVC框架，那么工作流程和你上面描述的是完全一样的，因为这部分工作是Spring MVC做的而不是Spring Boot。
对使用者来说，换用Spring Boot以后，项目初始化方法变了，配置文件变了，另外就是不需要单独安装Tomcat这类容器服务器了，maven打出jar包直接跑起来就是个网站，但你最核心的业务逻辑实现与业务流程实现没有任何变化。
 
**总结：**Spring 最初利用“工厂模式”（DI）和“代理模式”（AOP）解耦应用组件。大家觉得挺好用，于是按照这种模式搞了一个 MVC框架（一些用Spring 解耦的组件），用开发 web 应用（ SpringMVC ）。然后发现每次开发都写很多样板代码，为了简化工作流程，于是开发出了一些“懒人整合包”（starter），这套就是 Spring Boot。
 
所以，用最简练的语言概括就是：
Spring 是一个“引擎”；
Spring MVC 是基于Spring的一个 MVC 框架；
Spring Boot 是基于Spring4的条件注册的一套快速开发整合包。
