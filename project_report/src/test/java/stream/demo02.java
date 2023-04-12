package stream;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class demo02 {

    /**
     * 五种生成流的方式：
     *      集合、数组、值、文件、函数
     */
    @Test
    void test01() throws IOException {
        //集合
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5, 6);
        Stream<Integer> stream = integerList.stream();

        //数组
        int[] arr = {1, 2, 3, 4, 5, 6};
        IntStream intStream = Arrays.stream(arr);

        //值
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5);

        //文件
        Stream<String> lines = Files.lines(Paths.get("result.xlsx"), Charset.defaultCharset());

        //函数,第一个参数为初始化值，第二个为进行的函数操作
        Stream<Integer> integerStream1 = Stream.iterate(0, n -> n + 2).limit(5);
        Stream<Double> doubleStream = Stream.generate(Math::random).limit(5);
    }

    /**
     * 流的中间操作
     */
    @Test
    void test02() {
        //filter筛选
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5, 6);
        integerList.stream().filter(i -> i > 3).forEach((Integer i) -> {
            System.out.println(i);
        });
    }

    @Test
    void test03() {
        //distinct去重
        Arrays.asList(1,2,2,3,4,4,5,5,6,7,8).stream().distinct().forEach((Integer i) -> {
            System.out.println(i);
        });
    }

    @Test
    void test04() {
        //limit返回指定个数
        Arrays.asList(1,2,3,4,5,6,7,8,9,0).stream().limit(5).forEach((Integer s) -> {
            System.out.println(s);
        });
    }

    @Test
    void test05() {
        //skip跳过流中的元素
        Arrays.asList(1,2,3,4,5,6,7,4).stream().skip(5).forEach((Integer i) -> {
            System.out.println(i);
        });
    }

    @Test
    void test06() {
        //map流映射,将接收的元素映射成另外一个元素
        List<Integer> list = Arrays.asList("java 8", "靓仔", "lambda", "Action")
                .stream()
                .map(String::length)
                .collect(Collectors.toList());

        Set<Integer> set = Arrays.asList("java 8", "靓仔", "lambda", "Action")
                .stream()
                .map(String::length)
                .collect(Collectors.toSet());
    }

    @Test
    void test07() {
        //flatMap,将每一个流中的值都转换为另外一个流
        List<String> stringList = Arrays.asList("java 8", "靓仔", "lambda", "Action")
                .stream()
                .map(w -> w.split(" "))
                .flatMap(Arrays::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    @Test
    void test08() {
        //allMatch匹配所有元素
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        if (integerList.stream().allMatch(integer -> integer > 3)) {
            System.out.println("所有元素都大于3");
        } else {
            System.out.println("不是所有元素都大于3");
        }
    }

    @Test
    void test09() {
        //anyMatch匹配其中一个
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 4, 5, 6);
        if (integerList.stream().anyMatch(integer -> integer > 3)) {
            System.out.println("存在大于3的元素");
        } else {
            System.out.println("不存在大于3的元素");
        }
    }

    @Test
    void test10() {
        //noneMatch全部不匹配
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        if (integerList.stream().noneMatch(integer -> integer > 6)) {
            System.out.println("值都大于6");
        } else {
            System.out.println("值不都大于6");
        }
    }
}
