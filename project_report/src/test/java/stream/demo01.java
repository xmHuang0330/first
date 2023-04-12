package stream;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class demo01 {

    /**
     * stream流引入
     */
    @Test
    void test01() {
        ArrayList<String> list = new ArrayList<>();
        list.add("张老三");
        list.add("张小三");
        list.add("李四");
        list.add("赵武");
        list.add("张六");
        list.add("王八");

        ArrayList<String> list1 = new ArrayList<>();
        for (String name :
                list) {
            if (name.startsWith("张") && name.length() == 3) {
                list1.add(name);
            }
        }
        System.out.println(list1);
        System.out.println("=================================");

        //stream流
        list.stream().filter((String name)
                -> name.startsWith("张")).filter((String name)
                -> name.length() == 3).forEach((String name)
                ->{
                    System.out.println("符合条件的姓名：" + name);
        });
    }

    /**
     * 根据collection获取流
     * 1、根据list获取流
     * 2、根据set获取流
     * 3、根据map获取流
     * 3.1、根据map集合的键获取流
     * 3.2、根据map集合的值获取流
     * 3.3、根据map集合的键值对对象获取流
     * 4、根据数组获取流
     */

    /**
     * 方法：
     * count：统计流中的数据，返回long类型数据（长度）
     * Filter：过滤出满足条件的元素
     * foreach：逐一处理流中的书数据
     * limit：取前几个元素
     * map：将字符串类型转换成int类型
     * skip：跳过前几个，打印剩下的，如果超出长度，则不执行后面的代码
     * concat：将两个流合并在一起
     * collect（collectors）：将流收集到单例中
     */

    @Test
    void test02() {
        //根据list集合获取流
        ArrayList<String> list = new ArrayList<>();
        list.add("张老三");
        list.add("张小三");
        list.add("李四");
        list.add("赵武");
        list.add("张六");
        list.add("王八");
        Stream<String> stream = list.stream();
        long c = list.stream().count();

//        stream.filter((String name) -> {
//            return name.startsWith("张");
//        }).forEach((String name) ->{
//            System.out.println("流中的元素：" + name);
//        });

//        stream.limit(3).forEach((String name) -> {
//            System.out.println("foreach：" + name);
//        });

        Stream<String> stringStream = stream.filter((String name) -> {
            return name.startsWith("张");
        }).filter((String name) -> {
            return name.length() == 3;
        });

        //stream收集到单例中
//        List<String> list1 = stringStream.collect(Collectors.toList());
//        System.out.println("stream收集到单例中toList：" + list1);

        Set<String> stringSet = stringStream.collect(Collectors.toSet());
        System.out.println("stream收集到单例中toSet：" + stringSet);


        //根据set集合获取流
        Set<String> set = new HashSet<>();
        set.add("张老三");
        set.add("张小三");
        set.add("李四");
        set.add("赵武");
        set.add("张六");
        set.add("王八");
        Stream<String> stream1 = set.stream();
    }

    @Test
    void test03() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "张老三");
        map.put(2, "张小三");
        map.put(3, "李四");
        map.put(4, "赵武");
        map.put(5, "张六");
        map.put(6, "王八");

        //根据map集合的键获取流
        Set<Integer> key = map.keySet();
        Stream<Integer> stream = key.stream();
        long count = key.stream().count();
        System.out.println(count);

        //根据map集合的值获取流
        Collection<String> values = map.values();
        Stream<String> stream1 = values.stream();
        long count1 = values.stream().count();

//        stream1.filter((String name) -> {
//            return name.startsWith("张");
//        }).forEach((String name) ->{
//            System.out.println("流中的元素：" + name);
//        });
        stream1.skip(4).forEach((String name) -> {
            System.out.println("跳过前3个，打印剩下的：" + name);
        });
        System.out.println("===================================");

        //根据map集合的键值对对对象获取流
        Set<Map.Entry<Integer, String>> entries = map.entrySet();
        long count2 = entries.stream().count();
        System.out.println("entries: " + count2);
        Stream<Map.Entry<Integer, String>> stream2 = entries.stream();
        map.forEach((k, v) -> System.out.println(k + "---->" + v));


        //根据数组获取流
        String[] arr = {"张杰", "张国荣", "张曼玉", "袁咏仪"};
        Stream<String> stream3 = Stream.of(arr);
        long count3 = Arrays.stream(arr).count();
        System.out.println("arr: " + count3);

//        stream3.filter((String name) -> {
//            return name.startsWith("张");
//        }).forEach((String name) ->{
//            System.out.println("流中的元素：" + name);
//        });
        stream3.forEach((String name) -> {
            System.out.println("foreach：" + name);
        });
    }

    @Test
    void test04() {
        Stream<String> stream = Stream.of("11", "22", "33", "44", "55");
        //把stream中的元素转换成int类型
//        stream.map((String s) -> {
//            return Integer.parseInt(s);
//        }).forEach((Integer i) -> {
//            System.out.println(i);
//        });
        Stream<String> stream1 = Stream.of("张杰", "张国荣", "张曼玉", "袁咏仪");
        Stream<String> concat = Stream.concat(stream, stream1);
        concat.forEach((String name) -> {
            System.out.print(name);
        });
    }
}
