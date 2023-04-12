package demo;


import java.util.regex.Pattern;

public class demo03 {

    public static final String regex = "a[^b-d]e";


    public void Regex() {
        System.out.println("正则表达式：" + regex);
        check("ade");
        check("abe");
        check("ame");
        check("are");
    }

    private static void check(String input) {
        boolean result = Pattern.matches(regex, input);
        System.out.println(input + ": " + result);
    }

    void test01() {
        int row = 10;
        for (int i = 0; i < row; i++) {
            int number = 1;
            //打印空格字符串
            System.out.format("%" + (row - i) * 2 + "s", "");
            for (int j = 0; j <= i; j++) {
                System.out.format("%4d",number);
                number = number * (i - j) / (j + 1);
            }
            System.out.println();
        }
    }
}
