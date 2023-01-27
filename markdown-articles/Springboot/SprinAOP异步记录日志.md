---
title: SprinAOP异步记录日志
url: https://www.yuque.com/tangsanghegedan/ilyegg/tkhr50
---

<a name="a4HlC"></a>

## 1. 配置文件开启切面

```java
#切面启用
spring.aop.proxy-target-class=true
```

<a name="MxCJS"></a>

## 2. 开启异步操作

```java
@SpringBootApplication
@EnableAsync // 开启异步调用
public class Application {
	public static void main(String[] args) {
    	SpringApplication.run(Neo4jV2Application.class, args);
    }
}
```

<a name="A2BXa"></a>

## 3. 创建线程池

```java

@Configuration
public class ExecutorConfig {
 
    @Bean
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(5);
        //配置最大线程数
        executor.setMaxPoolSize(10);
        //配置队列大小
        executor.setQueueCapacity(400);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("thread-");
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
}
```

<a name="N7wtW"></a>

## 4. 在插入方法加上@Async

```java
package poc.application.systemManage;

import org.springframework.stereotype.Service;
import poc.domain.systemManage.model.LogQueryRequestSource;
import poc.domain.systemManage.model.LogSource;
import poc.domain.systemManage.model.Page;
import poc.domain.systemManage.model.QueryResultSource;
import poc.domain.systemManage.repository.LogRepository;
import poc.infrastructure.systemManage.po.Log;

import java.util.*;

@Service
public class LogApplicationService {
    private final LogRepository repository;

    public LogApplicationService(LogRepository logRepository) {
        this.repository = logRepository;
    }

    @Async(value = "asyncServiceExecutor")
    public int insertSystemLog(LogSource logSource) {
        return repository.insert(logSource);
    }


}

```

<a name="xu1Gw"></a>

## 5. 自定义日志注解

```java
/**
 * Title: SystemControllerLog
 *
 * @date 2020年7月1日
 * Description:  自定义注解，拦截controller
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})//作用在参数和方法上
@Retention(RetentionPolicy.RUNTIME)//运行时注解
@Documented//表明这个注解应该被 javadoc工具记录
public @interface SystemControllerLog {
    String description() default "";
}
```

<a name="QciBg"></a>

## 6. 建立切面

```java
import org.apache.servicecomb.core.Invocation;
import org.apache.servicecomb.foundation.vertx.http.HttpServletRequestEx;
import org.apache.servicecomb.swagger.invocation.context.ContextUtils;
import org.apache.servicecomb.swagger.invocation.context.InvocationContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.weaver.ast.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import poc.application.systemManage.LogApplicationService;
import poc.domain.systemManage.model.LogSource;
import poc.infrastructure.systemManage.util.IdWorker;
import poc.representation.Token;
import poc.representation.jwtshiro.util.JWTUtil;
import poc.representation.logaop.annotation.SystemControllerLog;

import java.lang.reflect.Method;

/** 
 * 切点类 
 * @author 蛮吉 
 * @date 2020年7月1日
 */ 
@Aspect
@Component
@SuppressWarnings("all")
public class SystemLogAspect {
    
    @Autowired
    private final LogApplicationService logService;

    public SystemLogAspect(LogApplicationService applicationService) {
        this.logService = applicationService;
    }

    //本地异常日志记录对象
    private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);


    //Controller层切点
    @Pointcut("@annotation(poc.representation.logaop.annotation.SystemControllerLog)")
    public void controllerAspect() {
    }

/*    @Pointcut("execution( * poc.representation.systemManage.*Controller.*(..))")
    public void controllerLog() {
    }*/

    /**
     * @Description 前置通知  用于拦截Controller层记录用户的操作
     * @date 2020年7月1日
     */
//    @Around("controllerAspect() && controllerLog()")
    @Around("controllerAspect()")
    public Object doBefore(ProceedingJoinPoint pjp) {
        InvocationContext invocationContext = ContextUtils.getInvocationContext();
        Invocation context = (Invocation) invocationContext;
        HttpServletRequestEx request = context.getRequestEx();
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String jwt = request.getHeader("Authorization");
        if (jwt == null) {
            jwt = "";
        }
        Token token = JWTUtil.getToken(jwt);
        String userId = token.getUserId();
        String loginName = token.getLoginName();
        String userName = token.getUserName();
        String ip = IpUtils.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");// 获取系统和浏览器版本
        String requestMethod = request.getMethod();
        String actionUrl = request.getRequestURI();
        String actionMethod = pjp.getTarget().getClass().getName() + "." + pjp.getSignature().getName();
        String actionMethodName = pjp.getSignature().getName();
        //获取用户请求方法的参数并序列化为JSON格式字符串
        String params = "";
        // 参数名称
        String[] parameterNames = ((MethodSignature) pjp.getSignature()).getParameterNames();
        // 参数值
        Object[] args = pjp.getArgs();
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                params += JsonUtils.objectToJson(parameterNames[i] + ":" + args[i]) + ";";
            }
        }
        //*========控制台输出=========*//
        System.out.println("==============通知开始==============");
        System.out.println("请求方式：" + requestMethod); // GET
        System.out.println("请求路径：" + actionUrl); //gz/hj/sxyd/sxyd-xtgl/user/queryUserList
        System.out.println("请求方法：" + actionMethod);// poc.representation.systemManage.UserController.queryUserLists
        System.out.println("请求人Id：" + userId);
        System.out.println("请求ip：" + ip);
        System.out.println("请求参数:" + params);
        System.out.println("浏览器/系统版本:" + userAgent);
        System.out.println("==============通知结束==============");
        //*========数据库日志=========*//
        // 日志Id
        long nextId = new IdWorker().nextId();
        String logId = String.valueOf(nextId);
        // 日志对象
        LogSource log = new LogSource();
        log.setLogId(logId);
        log.setServiceName("系统管理服务");
        if ("login".equals(actionMethodName) | "login2".equals(actionMethodName) | "login3".equals(actionMethodName) | "login4".equals(actionMethodName)) {
            log.setLogType("02"); //日志类型 00-系统运行日志，01-用户操作日志，02-用户登录日志
        } else {
            log.setLogType("01");
        }
        log.setRequestMethod(requestMethod); //请求方式
        log.setActionUrl(actionUrl); //请求路径
        log.setActionMethod(actionMethod); //请求方法
        log.setBrowser(userAgent); //浏览器/系统版本
        log.setActionParams(params); //请求参数
        log.setLogIp(ip); // 登录IP
        log.setUserId(userId); // 操作用户ID
        log.setUserName(userName);
        log.setLoginName(loginName);
        Object result = null;
        try {
            System.out.println("方法描述：" + this.getControllerMethodDescription(pjp));
            String description = this.getControllerMethodDescription(pjp);
            log.setOperate(description); //操作项(请求方法描述)
            String[] split = description.split("--");
            if (split.length >= 3) {
                String moduleName = split[1];
                log.setModuleName(moduleName);
            }
            log.setOperateStatus("操作成功");
            result = pjp.proceed();
            return result;
        } catch (Throwable e) {
            //记录本地异常日志
            logger.error("====通知异常====");
            logger.error("异常信息：{}", e.getMessage());
            log.setOperateStatus("操作失败");
            throw new RuntimeException(e);
        } finally {
            try {
                //保存数据库
                int i = logService.insertSystemLog(log);
                logger.info("logInsertResult = " + (i > 0 ? true : false));
            } catch (Exception e) {

            }
        }
    }


    /**
     * @Description 获取注解中对方法的描述信息 用于Controller层注解
     * @date 2020年7月1日
     */
    public static String getControllerMethodDescription(JoinPoint joinPoint) throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();//目标方法名
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(SystemControllerLog.class).description();
                    break;
                }
            }
        }
        return description;
    }
}
```

<a name="6f1df"></a>

## 7. 注解使用

```java
package poc.representation.systemManage;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import poc.application.systemManage.UserApplicationService;

import poc.representation.logaop.annotation.SystemControllerLog;
import poc.representation.systemManage.mapper.UserRequestMapper;
import poc.representation.systemManage.request.UserRequest;
import poc.representation.response.Response;


@Api(value = "系统用户管理")
@RestSchema(schemaId = "sxydSystemUser")
@RequestMapping("/user")
public class UserController {

    private final UserApplicationService userService;

    public UserController(UserApplicationService applicationService) {
        this.userService = applicationService;
    }

    @SystemControllerLog(description = "新增用户")
    @PostMapping("/insertUser")
    @ApiOperation(value = "新增用户")
    public Response insertUserById(UserRequest userRequest) {
        Long aLong = userService.insertUser(UserRequestMapper.MAPPER.requestToSource(userRequest));
        if (aLong > 0) {
            return Response.ok("新增成功!");
        }
        return Response.error("新增失败,请稍后重试");
    }

}
```

<a name="gdAeO"></a>

### 注意事项:

1. 调用被@Async标记的方法的调用者不能和被调用的方法在同一类中, 不然不会起作用
2. @Async时要求是不能有返回值的不然会报错的 因为异步要求是不关心结果的
   1. 这个注解是可以有返回值的，就像是callable Future线程一样，这里可以返回Future对象。
   2. 以通过Future返回值判断任务是否执行完成
