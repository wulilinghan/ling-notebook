[【JAVA】判断对象是否为null,包括对象中的属性为null,可自定义判断的属性或值](https://blog.csdn.net/qq_35566813/article/details/90914062) 

## 前言

- java中当对象需要判空的时候，大体都会直接 if(Object != null) ，而当我们的对象是 new Object()的时候，往往这种判断不会起作用
  因为此时对象已经被实例化，所以在项目中通常会用反射获取Field从而判断该属性值是否为null，也就是常说的判断对象中所有属性不为null，本文讲讲我在项目中利用反射来判断遇到的问题和一些坑。
- 给返回对象中字段值为null的，返回指定的的值

```java
    /**
     * description:判断当前对象是否为空,并设置值（包括所有属性为空）
     **/
    public static boolean objCheckIsNull(Object object) {
        if (object == null) {
            return true;
        }
        // 得到类对象
        Class clazz = object.getClass();
        // 得到所有属性
        Field[] fields = clazz.getDeclaredFields();
        //定义返回结果，默认为true
        boolean flag = true;
        for (Field field : fields) {
            //暴力反射
            field.setAccessible(true);
            Object fieldValue = null;
            String fieldName = null;
            try {
                //得到属性值
                fieldValue = field.get(object);
                //得到属性类型 (ex: java.util.Date)
                Type fieldType = field.getGenericType();
                //得到属性名
                fieldName = field.getName();
//                System.out.println("属性类型：" + fieldType + ",属性名：" + fieldName + ",属性值：" + fieldValue);
                String s = fieldType.toString();            
                boolean date = s.contains("Date");
                // 给对象中字段值为null且不是日期类型的字段设置值为"/"
                if (fieldValue == null && !date) {
                    field.set(object,"/");
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }
```

