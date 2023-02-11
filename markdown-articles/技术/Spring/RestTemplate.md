### 注册RestTemplate

```Java

@Component
public class BeanRegister {

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        factory.setReadTimeout(5000);
//        factory.setConnectTimeout(15000);
        // 设置代理
        //factory.setProxy(null);
        return factory;
    }

    /**
     * org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException: The field file exceeds its maximum permitted size of 1048576 bytes., code=400
     * 设置tomcat上传文件大小限制
     *
     * @return /
     */

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //允许上传的文件最大值 (//KB,MB)
        factory.setMaxFileSize(DataSize.parse("50MB"));
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.parse("100MB"));
        return factory.createMultipartConfig();
    }
}
```

### GET请求, 并设置Cookie和Header

```Java

    /**
     * // 1. 无参,不指定Header调用
     * //        ResponseEntity<R> forEntity = restTemplate.getForEntity(URL_USER_LIST, R.class);
     * <p>
     * // 2. 无参,指定Header调用 get请求需要使用exchange()
     */
    @Test
    public void testGetUserList() {
        // 1. 无参,不指定Header调用
//        ResponseEntity<R> forEntity = restTemplate.getForEntity(URL_USER_LIST, R.class);
        // 2. 无参,指定Header调用 get请求需要使用exchange()
        // 封装请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.COOKIE, "JSESSIONID=" + JSESSIONID);
        httpHeaders.set("token", TOKEN);
        HttpEntity<String> httpEntity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<R> forEntity1 = restTemplate.exchange(URL_GET_USER_LIST, HttpMethod.GET, httpEntity, R.class);
        System.out.println("forEntity1.getBody() = " + forEntity1.getBody());
    }

```

### GET请求传参

```Java

private static final String URL_GET_USER_ID = "http://124.71.56.21:8081/get/user/id?uid={uid}";

    /**
     * 带参,并设置请求头,使用map封装参数
     */
    @Test
    public void testGetUserBYId() {
        // 带参,指定Header调用 get请求需要使用exchange()
        List<String> cookies = new ArrayList<>();
        cookies.add("JSESSIONID=" + JSESSIONID);

        // 封装请求头
        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.put(HttpHeaders.COOKIE, cookies);
        httpHeaders.set(HttpHeaders.COOKIE, "JSESSIONID=" + JSESSIONID);
        httpHeaders.set("token", TOKEN);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        // 封装参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("uid", "1");


        // 请求url要使用占位符
        ResponseEntity<R> forEntity1 = restTemplate.exchange(URL_GET_USER_ID, HttpMethod.GET, httpEntity, R.class, paramMap);
        System.out.println("forEntity1.getBody() = " + forEntity1.getBody());
    }
```

### POST请求

1. 表单提交

```Java

    /**
     * 表单提交, 要使用MultiValueMap
     */
    @Test
    public void testPostUserByForm() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set(HttpHeaders.COOKIE, "JSESSIONID=" + JSESSIONID);
        headers.set("token", TOKEN);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("uid", 4);
        map.add("username", "嘻嘻");
        map.add("age", 24);
        map.add("address", "潮汕");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);

        ResponseEntity<R> response = restTemplate.postForEntity(URL_POST_USER_BY_FORM, request, R.class);
        System.out.println("response = " + response.getBody());
    }

```

2. JSON提交

```Java

    /**
     * JSON提交
     */

    @Test
    public void testPostUserByJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.COOKIE, "JSESSIONID=" + JSESSIONID);
        headers.set("token", TOKEN);


        User user = new User().setUid(5).setUsername("haha").setAge(66).setAddress("龙岗");
        String jsonString = JSON.toJSONString(user);
        System.out.println("jsonString = " + jsonString);
//        JSONObject jsonString = new JSONObject();
//        jsonString.put("uid", 5);
//        jsonString.put("username", "haha");
//        jsonString.put("age", 77);
//        jsonString.put("address", "龙岗");

        HttpEntity<String> request = new HttpEntity<>(jsonString, headers);
        ResponseEntity<R> response = restTemplate.postForEntity(URL_POST_USER_BY_JSON, request, R.class);
        System.out.println("response = " + response.getBody());
    }
```

### 上传文件

1. 设置Tomcat上传限制

```Java

    /**
     * org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException: The field file exceeds its maximum permitted size of 1048576 bytes., code=400
     * 设置tomcat上传文件大小限制
     *
     * @return /
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //允许上传的文件最大值 (//KB,MB)
        factory.setMaxFileSize(DataSize.parse("50MB"));
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.parse("100MB"));
        return factory.createMultipartConfig();
    }
```

案例:

```Java

    /**
     * 上传文件
     */
    @Test
    public void testUploadFile() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.set(HttpHeaders.COOKIE, "JSESSIONID=" + JSESSIONID);
        httpHeaders.set("token", TOKEN);


        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("file", new ClassPathResource("yuque_diagram.jpg"));
        paramMap.add("file", new ClassPathResource("线程类图.png"));
        paramMap.add("file", new ClassPathResource("acane_madder - 庭園にて。.mp3"));
        paramMap.add("isErr", "");

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(paramMap, httpHeaders);

        ResponseEntity<R> response = restTemplate.postForEntity(URL_POST_FILE_UP1, httpEntity, R.class);
        System.out.println("response = " + response.getBody());
        System.out.println("response.getStatusCode() = " + response.getStatusCode());
        System.out.println("response.getStatusCode().is2xxSuccessful() = " + response.getStatusCode().is2xxSuccessful());
    }

```



> [代码](https://github.com/tanglinghan/demo-collection/tree/main/http-demo)