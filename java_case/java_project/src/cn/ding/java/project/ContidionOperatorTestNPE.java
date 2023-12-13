package cn.ding.java.project;
public class ContidionOperatorTestNPE {

    public static void main(String[] args) {
        boolean target = false;
        new ContidionOperatorTestNPE().isMatch(target);
        /**
         * 输出结果：
         * Exception in thread "main" java.lang.NullPointerException: Cannot invoke "java.lang.Boolean.booleanValue()" because "nullBoolean" is null
         * 	at cn.ding.ContidionOperatorTestNPE.isMatch(ContidionOperatorTestNPE.java:14)
         * 	at cn.ding.ContidionOperatorTestNPE.main(ContidionOperatorTestNPE.java:7)
         */
    }

    boolean isMatch(boolean target) {
        boolean flag = true; //设置成true，保证条件表达式的表达式二一定可以执行
        boolean simpleBoolean = false; //定义一个基本数据类型的boolean变量
        Boolean nullBoolean = null;//定义一个包装类对象类型的Boolean变量，值为null
        boolean x = flag ? nullBoolean : simpleBoolean; //使用三目运算符并给x变量赋值
        return x;
    }

}
