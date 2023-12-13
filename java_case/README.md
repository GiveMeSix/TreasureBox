# java_project 项目

记录日常遇到的亿点点小问题

## java_project模块

### 三目运算符NullPointException

#### 代码路径

cn.ding.java.project.ConditionOperatorTestNPE.java

#### 现象

~~~ 
Task task = map.get("xx"); // task为null
long SeqNo = task == null ? -1L : task.getSeqNo();
// Do Something
~~~

**在执行"long SeqNo = task == null ? -1L : task.getSeqNo();"语句时，抛出NPE。**

#### 分析

由于三目运算符的自动拆箱，导致了执行task.getSetNo().longValue()，从而抛出NPE。

引用出处：[《新版阿里巴巴Java开发手册》提到的三目运算符的空指针问题到底是个怎么回事](https://www.cnblogs.com/hollischuang/p/12841150.html)

关于为什么编辑器会在代码编译阶段对于三目运算符中的表达式进行自动拆箱，其实在《The Java Language Specification》（后文简称JLS）的第15.25章节中是有相关介绍的。

在不同版本的JLS中，关于这部分描述虽然不尽相同，尤其在Java 8中有了大幅度的更新，但是其核心内容和原理是不变的。我们直接看Java SE 1.7 JLS中关于这部分的描述（因为1.7的表述更加简洁一些）：
The type of a conditional expression is determined as follows: • If the second and third operands have the same type (which may be the null type),then that is the type of the conditional expression. • If one of the second and third operands is of primitive type T, and the type of the other is the result of applying boxing conversion (§5.1.7) to T, then the type of the conditional expression is T.
简单的来说就是：当第二位和第三位操作数的类型相同时，则三目运算符表达式的结果和这两位操作数的类型相同。当第二，第三位操作数分别为基本类型和该基本类型对应的包装类型时，那么该表达式的结果的类型要求是基本类型。

为了满足以上规定，又避免程序员过度感知这个规则，所以在编译过程中编译器如果发现三目操作符的第二位和第三位操作数的类型分别是基本数据类型(如boolean)以及该基本类型对应的包装类型（如Boolean）时，并且需要返回表达式为包装类型，那么就需要对该包装类进行自动拆箱。

在Java SE 1.8 JLS中，关于这部分描述又做了一些细分，再次把表达式区分成布尔型条件表达式（Boolean Conditional Expressions）、数值型条件表达式（Numeric Conditional Expressions）和引用类型条件表达式（Reference Conditional Expressions）。
并且通过表格的形式明确的列举了第二位和第三位分别是不同类型时得到的表达式结果值应该是什么，感兴趣的大家可以去翻阅一下。

其实简单总结下，就是：当第二位和第三位表达式都是包装类型的时候，该表达式的结果才是该包装类型，否则，只要有一个表达式的类型是基本数据类型，则表达式得到的结果都是基本数据类型。如果结果不符合预期，那么编译器就会进行自动拆箱。（即Java开发手册中总结的：只要表达式1和表达式2的类型有一个是基本类型，就会做触发类型对齐的拆箱操作，只不过如果都是基本类型也就不需要拆箱了。）

~~~ java 
boolean flag = true;
boolean simpleBoolean = false;
Boolean objectBoolean = Boolean.FALSE;

//当第二位和第三位表达式都是对象时，表达式返回值也为对象；
Boolean x1 = flag ? objectBoolean : objectBoolean; 
//反编译后代码为：Boolean x1 = flag ? objectBoolean : objectBoolean; 
//因为x1的类型是对象，所以不需要做任何特殊操作。

//当第二位和第三位表达式都为基本类型时，表达式返回值也为基本类型；
boolean x2 = flag ? simpleBoolean : simpleBoolean; 
//反编译后代码为：boolean x2 = flag ? simpleBoolean : simpleBoolean;
//因为x2的类型也是基本类型，所以不需要做任何特殊操作。

//当第二位和第三位表达式中有一个为基本类型时，表达式返回值也为基本类型；
boolean x3 = flag ? objectBoolean : simpleBoolean; 
//反编译后代码为：boolean x3 = flag ? objectBoolean.booleanValue() : simpleBoolean;
//因为x3的类型是基本类型，所以需要对其中的包装类进行拆箱。
~~~

因为我们熟知三目运算符的规则，所以我们就会按照以上方式去定义x1、x2和x3的类型。

但是，并不是所有人都熟知这个规则，所以在实际应用中，还会出现以下三种定义方式：

~~~ java
//当第二位和第三位表达式都是对象时，表达式返回值也为对象；
boolean x4 = flag ? objectBoolean : objectBoolean; 
//反编译后代码为：boolean x4 = (flag ? objectBoolean : objectBoolean).booleanValue();
//因为x4的类型是基本类型，所以需要对表达式结果进行自动拆箱。

//当第二位和第三位表达式都为基本类型时，表达式返回值也为基本类型；
Boolean x5 = flag ? simpleBoolean : simpleBoolean; 
//反编译后代码为：Boolean x5 = Boolean.valueOf(flag ? simpleBoolean : simpleBoolean);
//因为x5的类型是对象类型，所以需要对表达式结果进行自动装箱。

//当第二位和第三位表达式中有一个为基本类型时，表达式返回值也为基本类型；
Boolean x6 = flag ? objectBoolean : simpleBoolean;  
//反编译后代码为：Boolean x6 = Boolean.valueOf(flag ? objectBoolean.booleanValue() : simpleBoolean);
//因为x6的类型是对象类型，所以需要对表达式结果进行自动装箱。
~~~

#### 测试

~~~ java
boolean flag = true; //设置成true，保证条件表达式的表达式二一定可以执行
boolean simpleBoolean = false; //定义一个基本数据类型的boolean变量
Boolean nullBoolean = null;//定义一个包装类对象类型的Boolean变量，值为null
boolean x = flag ? nullBoolean : simpleBoolean; //使用三目运算符并给x变量赋值 // 此处NPE
return x;
~~~

反编译之后的代码为：

~~~ java

boolean flag = true;
boolean simpleBoolean = false;
Boolean nullBoolean = null;
boolean x = flag ? nullBoolean.booleanValue() : simpleBoolean;
~~~

反编译后的代码的最后一行，编译器帮我们做了一次自动拆箱，而就是因为这次自动拆箱，导致代码出现对于一个null对象（nullBoolean.booleanValue()）的调用，导致了NPE。

## java_fastjson模块

### rCode字段JSON转换赋值失败

在fastjson-1.1.33版本（特定版本），对象定义的rCode字段，反序列化时未被赋值。

~~~ java
public class Main {
    public static void main(String[] args) {
        String str = "{\"r_code\":\"code\"}";
        Bean bean = JSONObject.parseObject(str, Bean.class);
        System.out.println(bean.getRCode());
        /**
         * fastjson-1.1.27 输出内容：
         * method:setRCode()
         * code
         *
         * fastjson-1.1.33 输出内容：
         * null
         */
    }
}

public class Bean implements Serializable {

    @JSONField(name = "r_code")
    private String rCode;

    public String getRCode() {
        return rCode;
    }

    public void setRCode(String rCode) {
        System.out.println("method:setRCode()");
        this.rCode = rCode;
    }

    public void setrCode(String rCode) {
        System.out.println("method:setrCode()");
        this.rCode = rCode;
    }

    public void setrcode(String rCode) {
        System.out.println("method:setRCode()");
        this.rCode = rCode;
    }

    public void setRcode(String rCode) {
        System.out.println("method:setRCode()");
        this.rCode = rCode;
    }
}
~~~

#### 代码路径

cn.ding.fastjson.Main.java

#### 分析

- 在1.1.27版本的fastjson中，反序列化时，调用了setRCode()对rCode进行赋值。
- 在1.1.33版本的fastjson中，反序列化时，rCode未null，未进行赋值。

在1.1.33版本的fastjson中，遍历Class的方法时

- 如果第4位为大写，且第5位是大写，则认为字段名是第4位开始的值
  - methodName.substring(3); // 此时为RCode
- 如果第4位为大写，且第5位是小写，则认为字段名是第4位小写开始的值
  - Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4); // 此时为rcode

由于对象中定义的字段名为rCode，该版本通过方法获取的字段名和定义的字段名不一致，故而导致rCode赋值失败。

1.1.33版本代码：

~~~ java
/**
 * fastjson-1.1.33.jar
 * DeserializeBeanInfo.java
 */
public static DeserializeBeanInfo computeSetters(Class<?> clazz, Type type) {
    for (Method method : clazz.getMethods()) {
        String propertyName;
        if (Character.isUpperCase(c3)) {
            if (methodName.length() > 4 && Character.isUpperCase(methodName.charAt(4))) {
                propertyName = methodName.substring(3); // RCode
            } else {
                propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
            }
        } else if (c3 == '_') {
            propertyName = methodName.substring(4);
        } else if (c3 == 'f') {
            propertyName = methodName.substring(3);
        } else {
            continue;
        }
        Field field = getField(clazz, propertyName); // rCode
        if (field != null) {

        }
    }
}
~~~

在1.1.27版本的fastjson中，遍历Class的方法时

- 寻找以set开头的方法，若第4位大写，则认为字段名第4位小写开始的值
  - String propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4); // 此时为rCode

从而调用setRCode()方法对rCode进行赋值。

1.1.27版本代码：

~~~ java
/**
 * fastjson-1.1.27.jar
 * DeserializeBeanInfo.java
 */
public static DeserializeBeanInfo computeSetters(Class<?> clazz, Type type) {
    for (Method method : clazz.getMethods()) {
        if (methodName.startsWith("set") && Character.isUpperCase(methodName.charAt(3))) {
            String propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);

            Field field = getField(clazz, propertyName);
            if (field != null) {

                JSONField fieldAnnotation = field.getAnnotation(JSONField.class);

                if (fieldAnnotation != null && fieldAnnotation.name().length() != 0) {
                    propertyName = fieldAnnotation.name();

                    beanInfo.add(new FieldInfo(propertyName, method, field, clazz, type));
                    continue;
                }
            }

            beanInfo.add(new FieldInfo(propertyName, method, null, clazz, type));
            method.setAccessible(true);
        }
    }
}
~~~

fastjson 1.1.27和1.1.33版本，反序列化时，对于获取方法映射的字段名时实现方式不相同，导致了特定情况(rCode)下，出现字段未被赋值的情况。
该bug（姑且称之为bug）仅在1.1.33版本中出现，在之前的1.1.27，最新的2.0.32的版本中均未出现。