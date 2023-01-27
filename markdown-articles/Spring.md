Spring事务失效场景

- 非public修饰

Spring源码中已经写死了只能是public，源码位置AbstractFallbackTransactionAttributeSource 的 computeTransactionAttribute 方法的第一个 if 就直接写死了

如果把这个类修改覆盖后protected是否可以实现代理的？外部通过反射的方式调用，能否走代理实现事务？（待验证）

- final修饰也会失效, 动态代理需要重写方法才能生效事务, final修饰则无法重写
如果走JDK代理是否可行？（待验证）

- static修饰也会失效

- 代码catch了异常，没有抛给Spring

- 抛了错误的异常, catch住手动抛Exception则不会回滚, 因为Spirng事务只会处理RuntimeException和Error