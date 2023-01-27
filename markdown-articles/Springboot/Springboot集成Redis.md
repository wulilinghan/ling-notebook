---
title: Springboot集成Redis
url: https://www.yuque.com/tangsanghegedan/ilyegg/uyfdmp
---

<a name="D7hsX"></a>

## 1.1 Redis相关介绍

Redis 是一种非关系型数据库（NoSQL），NoSQL 是以 key-value 的形式存储的。
Redis 的 key 可以是字符串、哈希、链表、集合和有序集合。value 类型很多，包括 String、list、set、
zset。这些数据类型都支持 push/pop、add/remove、取交集和并集以及更多更丰富的操作，Redis 也
支持各种不同方式的排序。为了保证效率，数据都是在缓存在内存中，它也可以周期性的把更新的数据
写入磁盘或者把修改操作写入追加的记录文件中。 <a name="FfX2W"></a>

## 1.2 Springboot集成Redis

<a name="NPIHo"></a>

### 1.2.1 依赖导入

```java
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<!--阿里巴巴fastjson -->
<dependency>
	<groupId>com.alibaba</groupId>
	<artifactId>fastjson</artifactId>
	<version>1.2.35</version>
</dependency>
```

fastjson 是为了在后面我们要存一个实体，为了方便把实体转换成 json 字符串存进去。 <a name="ZMYMi"></a>

### 1.2.2 Redis 配置

application.yml 文件里配置 redis：

```yaml
server:
	port: 8080
spring:
	#redis相关配置
	redis:
		database: 5
		# 配置redis的主机地址，需要修改成自己的
		host: 192.168.48.190
		port: 6379
		password: 123456
		timeout: 5000
		jedis:
			pool:
				# 连接池中的最大空闲连接，默认值也是8。
				max-idle: 500
				# 连接池中的最小空闲连接，默认值也是0。
				min-idle: 50
				# 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)
				max-active: 1000
				# 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException
				max-wait: 2000
```

<a name="2zadK"></a>

### 1.2.3 Redistemplate配置类

```java
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Ling
 * @description: redis config
 * @date: 2020-11-27
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 设置序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        RedisSerializer<?> stringSerializer = new StringRedisSerializer();
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 默认序列化
        redisTemplate.setDefaultSerializer(jackson2JsonRedisSerializer);
        // 设置键（key）的序列化采用StringRedisSerializer。
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        // value值的序列化采用 fastJsonRedisSerializer
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 启用默认序列化方式
//        redisTemplate.setEnableDefaultSerializer(false);
//        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
```

<a name="1hQJS"></a>

## 1.3 常用 api 介绍

RedisTemplate 和 StringRedisTemplate。
RedisTemplate 提供给我们操作对象，操作对象的时候，我们通常是以 json 格式存储，但在存储的时候，会使用 Redis 默认的内部序列化器；导致我们存进里面的是乱码之类的东西。当然了，我们可以自己定义序列化，但是比较麻烦，所以使用 StringRedisTemplate 模板。StringRedisTemplate 主要给我们提供字符串操作，我们可以将实体类等转成 json 字符串即可，在取出来后，也可以转成相应的对象，这就是上面我导入了阿里 fastjson 的原因。 <a name="8b0ZH"></a>

### 1.3.1 redis:string 类型 `opsForValue()`

新建一个 RedisService，注入 StringRedisTemplate，使用`stringRedisTemplate.opsForValue()`，可以获取 `ValueOperations<String, String>` 对象，通过该对象即可读写 redis 数据库。

```java
public class RedisService {
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	/**
	 * set redis: string类型
	 * @param key key
     * @param value value
	 */
	public void setString(String key, String value){
		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
		valueOperations.set(key, value);
	}
	 /**
	  * get redis: string类型
	  * @param key key
	  * @return
	  */
	public String getString(String key){
		return stringRedisTemplate.opsForValue().get(key);
	}
```

该对象操作的是 string，我们也可以存实体类，只需要将实体类转换成 json 字符串即可。

```java
	//测试redis的string类型
	redisService.setString("weichat","程序员私房菜");
	logger.info("我的微信公众号为：{}", redisService.getString("weichat"));
	// 如果是个实体，我们可以使用json工具转成json字符串，
	User user = new User("CSDN", "123456");
	redisService.setString("userInfo", JSON.toJSONString(user));
	logger.info("用户信息：{}", redisService.getString("userInfo"));
```

观察控制台打印的日志如下：

```java
我的微信公众号为：程序员私房菜
用户信息：{"password":"123456","username":"CSDN"}
```

<a name="lEalL"></a>

### 1.3.2 redis:hash 类型 `opsForHash()`

hash 类型其实原理和 string 一样的，但是有两个 key，使用 `stringRedisTemplate.opsForHash()`可以获取 `HashOperations<String, Object, Object>` 对象。比如我们要存储订单信息，所有订单信息都放在 order 下，针对不同用户的订单实体，可以通过用户的 id 来区分，这就相当于两个 key了。

```java
@Service
public class RedisService {
	@Resource
	private StringRedisTemplate stringRedisTemplate;
    /**
	 * set redis: hash类型
	 * @param key key
	 * @param filedKey filedkey
	 * @param value value
	 */
	public void setHash(String key, String filedKey, String value){
		HashOperations<String, Object, Object> hashOperations =stringRedisTemplate.opsForHash();
		hashOperations.put(key,filedKey, value);
	}
	/**
	* get redis: hash类型
	* @param key key
	* @param filedkey filedkey
	* @return
	*/
	public String getHash(String key, String filedkey){
		return (String) stringRedisTemplate.opsForHash().get(key, filedkey);
	}
}
```

<a name="HxIjI"></a>

### 1.3.3 redis:list 类型 `opsForList() `

使用`stringRedisTemplate.opsForList()`可以获取 `ListOperations<String, String> listOperations` redis 列表对象，该列表是个简单的字符串列表，可以支持从**左侧添加**，也可以支持从**右侧添加**，一个列表最多包含 2 ^ 32 -1 个元素。

```java
@Service
public class RedisService {
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	/**
	 * set redis:list类型
	 * @param key key
	 * @param value value
	 * @return
	 */
	public long setList(String key, String value){
		ListOperations<String, String> listOperations = stringRedisTemplate.opsForList();
		return listOperations.leftPush(key, value);
	}
	/**
	 * get redis:list类型
	 * @param key key
	 * @param start start
	 * @param end end
	 * @return
	 */
	public List<String> getList(String key, long start, long end){
		return stringRedisTemplate.opsForList().range(key, start, end);
	}
}
```

测试：

```java
	//测试redis的list类型
	redisService.setList("list", "football");
	redisService.setList("list", "basketball");
	List<String> valList = redisService.getList("list",0,-1);
	for(String value :valList){
		logger.info("list中有：{}", value);
	}
```

在实际项目中，通常都用 redis 作为缓存，在查询数据库的时候，会先从 redis 中查找，如果有信息，则从 redis
中取；如果没有，则从数据库中查，并且同步到 redis 中，下次 redis 中就有了。更新和删除也是如此，都需要同步到 redis。
