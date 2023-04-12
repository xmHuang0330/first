package com.xm;


public class MyLinkList {
    /**
     * 链表节点
     */
    private static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
        }
    }
    private Node head;
    private Node last;
    private int size;


    /**
     * 链表查找元素
     * @param index 查找的位置
     * @return
     */
    public Node get(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("超出链表节点范围");
        }
        Node temp = head;
        for (int i = 0; i < index; i++) {
            temp = temp.next;
        }
        return temp;
    }
    /**
     * 链表插入元素
     * @param data
     * @param index
     * @throws Exception
     */
    public void insert(int data,int index) throws Exception {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("超出链表节点范围");
        }
        Node insertNode = new Node(data);
        if (size == 0) {
            head = insertNode;
            last = insertNode;
        } else if (index == 0) {
            insertNode.next = head;
            head = insertNode;
        } else if (size == index) {
            last.next = insertNode;
            last = insertNode;
        } else {
            Node preNode = get(index - 1);
            insertNode.next = preNode.next;
            preNode.next = insertNode;
        }
        size++;
    }

    /**
     * 链表删除元素
     * @param index
     * @return
     * @throws Exception
     */
    public Node remove(int index) throws Exception {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("超出链表节点范围");
        }
        Node removeNode = null;
        if (index == 0) {
            removeNode = head;
            head = head.next;
        } else if (index == size - 1) {
            Node preNode = get(index - 1);
            removeNode = preNode.next;
            preNode.next = null;
            last = preNode;
        } else {
            Node preNode = get(index - 1);
            Node nextNode = preNode.next.next;
            removeNode = preNode.next;
            preNode.next = nextNode;
        }
        size--;
        return removeNode;
    }

    /**
     * 输出链表
     */
    public void output() {
        Node temp = head;
        while (temp != null) {
            System.out.print(temp.data + " ");
            temp = temp.next;
        }
    }

    public static void main(String[] args) throws Exception {
        MyLinkList test = new MyLinkList();
        test.insert(3,0);
        test.insert(7,1);
        test.insert(9,2);
        test.insert(5,3);
        test.insert(6,1);
        test.insert(8,4);
        test.remove(0);
        test.output();
    }
}
