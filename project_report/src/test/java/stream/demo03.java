package stream;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class demo03 {

    /**
     * 终端操作
     */
    @Test
    void test01() {
        //count统计流中的元素个数
        long count = Arrays.asList(1, 2, 3, 4, 5, 6, 7).stream().count();
        System.out.println(count);
    }

    @Test
    void test02() {
        //findFirst查找第一个
        Optional<Integer> first = Arrays.asList(1, 2, 3, 4, 5, 6).stream().findFirst();
        System.out.println(first.orElse(-1));
    }

    @Test
    void test03() {
        //findAny随机查找一个
        Optional<Integer> integer1 = Arrays.asList(1, 2, 3, 4, 5, 6).stream().filter(integer -> integer > 3).findAny();
        System.out.println(integer1.orElse(-1));
    }

    @Test
    void test04() {
        //reduce将流中的元素组合
        Integer reduce = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8).stream().reduce(0, Integer::sum);
        System.out.println(reduce);

        List<String> strings = Arrays.asList("java 8", "lambdas", "In", "Action");
        Optional<Integer> min = strings.stream().map(String::length).reduce(Integer::min);
        System.out.println("最小值" + min.orElse(-1));
    }

    @Test
    void test05() {
        //max/min求最大值最小值
        //最大值
        Optional<Integer> max = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8).stream().max(Integer::compareTo);
        System.out.println(max.orElse(-1));

        //最小值
        System.out.println(Arrays.asList(1,2,3,4,5,6,7,8,9,0).stream().min(Integer::compareTo).orElse(-1));
    }

    @Test
    void test06() {
        //求平均值
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

}
