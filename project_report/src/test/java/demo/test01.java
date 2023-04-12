package demo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class test01 {

    @Test
    public void method01() {
        ArrayList<Integer> arrayList = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < 21; i++) {
            arrayList.add(random.nextInt(100));
        }
        System.out.println(arrayList);

        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i) < 50) {
                list.add(arrayList.get(i));
            }
        }
        System.out.println(list);

    }
}
