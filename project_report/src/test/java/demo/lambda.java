package demo;

import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.junit.jupiter.api.Test;

public class lambda {

    interface MathOperation{
        int operation(int a, int b);
    }

    interface GreetingService{
        void sayMesage(String message);
    }

    private int operate(int a, int b, MathOperation mathOperation) {
        return mathOperation.operation(a, b);
    }


    @Test
    void Lambda() {
        lambda lambda = new lambda();

        //类型声明
        MathOperation addtion = (int a,int b) -> a + b;

        //不用类型声明
        MathOperation subtraction = (a, b) -> a - b;

        //大括号中的返回语句
        MathOperation multiplication = (int a,int b) -> {return a * b;};

        //没有大括号及返回语句
        MathOperation division = (int a, int b) -> a / b;

        System.out.println("10 + 5 = " + lambda.operate(10, 5, addtion));
        System.out.println("10 - 5 = " + lambda.operate(10, 5, subtraction));
        System.out.println("10 * 5 = " + lambda.operate(10, 5, multiplication));
        System.out.println("10 / 5 = " + lambda.operate(10,5,division));

        System.out.println("================================================");

        //不用括号
        GreetingService greetingService = message -> System.out.println("hello " + message);

        //用括号
        GreetingService greetingService1 = message -> System.out.println("hello " + message);

        greetingService1.sayMesage("靓仔");
        greetingService1.sayMesage("靓女");
    }

    @Test
    void test() {
        int x = 236;
        System.out.println("x / 10 = " + x / 10);
        System.out.println("x % 10 = " + x % 10);
        System.out.println("x % 100 = " + x % 100);

    }

}
