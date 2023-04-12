package test;

import lombok.Data;

public class functionalTest {


    public static void main(String[] args) {
        Person person1 = new Person(new Head());

        System.out.println(person1.getHead().getEye());

        Head head = person1.getHead();

        Person person2 = new Person();
        person2.setHead(head);
        person2.getHead().setEye("Black");

        System.out.println(person1.getHead().getEye());
        System.out.println(person2.getHead().getEye());
    }


}
@Data
class Person{
    private Head head;

    Person(Head head){
        this.head = head;
    }

    Person(){}
}
@Data
class Head{
    private String eye = "blue";
    private String mouth = "moon";

    public Head(String eye, String mouth) {
        this.eye = eye;
        this.mouth = mouth;
    }

    public Head() {
    }
}
