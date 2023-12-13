### SpringBoot

#### @Value

~~~ java
// 取name的值。如果没有，赋默认值Ding
@Value("${name:Ding}")
~~~

#### SpringBoot使用Mybatis

- @Alies 需要搭配mybatis：type-aliases-package: cn.ding.job.domain使用
-  @MapperScan
-  @Mapper