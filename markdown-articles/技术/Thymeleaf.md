```
tittle: Thymeleaf 用法
```



# 字符串判空

> Springboot Thymeleaf 检查String是否为 null 或空白字符串

使用 springboot 项目的 thymeleaf 前端模版，判断一个字符串是否为空（String 为 null、空字符或者只包含空白符）的几种方法。

##使用 String 的 trim() 方法

```markup
<div th:if="${str != null && str.trim() != ''}" th:text="${str}"/>
```

## 使用 Strings 的 isEmpty() 方法

Thymeleaf 的 Strings 类，包含类多个常用的判断，可以参考 Strings 的 api [org.thymeleaf.expression.Strings](https://www.thymeleaf.org/apidocs/thymeleaf/2.1.4.RELEASE/org/thymeleaf/expression/Strings.html)，还有一些字符串拼接、集合 List，Set 的操作等，非常有用的一个工具类。

`isEmpty()` 的源代码如下。空对象，或者只包含了空白字符的，表明就是空的，返回 true。

```java
public Boolean isEmpty(final Object target) {
    return Boolean.valueOf(target == null || StringUtils.isEmptyOrWhitespace(target.toString()));
}
```

以下3种写法都是一样的效果，使用类`th:if`和`th:unless`条件判断。

```markup
<div th:if="!${#strings.isEmpty(str)}" th:text="${str}"/>
<div th:if="${!#strings.isEmpty(str)}" th:text="${str}"/>
<div th:unless="${#strings.isEmpty(str)}" th:text="${str}"/>
```

## 使用 isEmpty() 方法

这一方法与上一步的方法一致。

```markup
<div th:unless="${str.isEmpty()}" th:text="${str}"/>
```

# 字符串常见api

```
判断是不是为空:null:
<span th:if="${name} != null">不为空</span>
<span th:if="${name1} == null">为空</span>

判断是不是为空字符串: “”
<span th:if="${#strings.isEmpty(name1)}">空的</span>

判断是否相同：
<span th:if="${name} eq 'jack'">相同于jack,</span>
<span th:if="${name} eq 'ywj'">相同于ywj,</span>
<span th:if="${name} ne 'jack'">不相同于jack,</span>

不存在设置默认值：
<span th:text="${name2} ?: '默认值'"></span>

是否包含(分大小写):
<span th:if="${#strings.contains(name,'ez')}">包ez</span>
<span th:if="${#strings.contains(name,'y')}">包j</span>

是否包含（不分大小写）
<span th:if="${#strings.containsIgnoreCase(name,'y')}">包j</span>
同理。。。下面的和JAVA的String基本一样。。。。不笔记解释，官网有

${#strings.startsWith(name,'o')}
${#strings.endsWith(name, 'o')}
${#strings.indexOf(name,frag)}// 下标
${#strings.substring(name,3,5)}// 截取
${#strings.substringAfter(name,prefix)}// 从 prefix之后的一位开始截取到最后,比如 (ywj,y) = wj, 如果是(abccdefg,c) = cdefg//里面有2个c,取的是第一个c
${#strings.substringBefore(name,suffix)}// 同上，不过是往前截取
${#strings.replace(name,'las','ler')}// 替换
${#strings.prepend(str,prefix)}// 拼字字符串在str前面
${#strings.append(str,suffix)}// 和上面相反，接在后面
${#strings.toUpperCase(name)}
${#strings.toLowerCase(name)}
${#strings.trim(str)}
${#strings.length(str)}
${#strings.abbreviate(str,10)}// 我的理解是 str截取0-10位，后面的全部用…这个点代替，注意，最小是3位
```

# 条件判断语句（if else）

### th:if  

**th:if 针对取值有以下规则判断：**

- 值不为 null 的情况下：    

  - 布尔类型且为 true;      

  - 非0数字;    

  - 非0字符;      

  - 非“false”, “off” or “no”字符串;      

  - 判断值不为布尔值，数字，字符，字符串     

结果都为true

- 值为 null，结果为 false

表达式

```
eq：equal（等于）==
ne：not equal（不等于）!=

gt：great than（大于）>
ge：great equal（大于等于）>=

lt：less than（小于）<
le：less equal（小于等于）<=

案例
 <div th:if="${score gt 60}"> 及格了 </div>
 <div th:if="${score >= 60}"> 及格了 </div>

 <div th:if="${score lt60}" style="color: red"> 不及格</div>
 <div th:if="${score < 60}" style="color: red"> 不及格</div>
```
多个判断可以用 and 或者 or 来连接，不能用 && 或者 || 这些

```
<div th:if="${webSiteConfig.getPluginPlayer() and webSiteConfig.getPluginPlayerType() eq 'neteasePlayer'}" 

用 && 会报错
<div th:if="${webSiteConfig.getPluginPlayer() && webSiteConfig.getPluginPlayerType() eq 'neteasePlayer'}" 
```


### th:unless 

Thymeleaf 没有else，可以使用 **unless** 配合 **if** 来完成 if else 的操作

**unless** 也可以理解为在表达式前加了个**非**
```
<!--/* word不为空时显示 */-->
<div th:if="${word}" th:text="${word}"></div>

<!--/* word为空时显示（除非word不为空才不显示） */-->
<div th:unless="${word}" th:text="${word}"></div>
```



