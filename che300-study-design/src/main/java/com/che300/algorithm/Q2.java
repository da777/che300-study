package com.che300.algorithm;

public class Q2 {

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode root = new ListNode();
        ListNode index = root;
        while (l1 != null || l2 != null) {
            int val1 = 0, val2 = 0;
            if (l1 != null) val1 = l1.val;
            if (l2 != null) val2 = l2.val;
            if (l1 != null) l1 = l1.next;
            if (l2 != null) l2 = l2.next;
            index.val = val1 + val2;
            if (l1 != null || l2 != null) {
                index.next = new ListNode();
                index = index.next;
            }
        }

        index = root;
        while (root != null) {
            int temp = root.val;
            if (temp >= 10) {
                root.val = temp % 10;
                if (root.next == null) {
                    root.next = new ListNode(1);
                } else {
                    root.next.val++;
                }
            }
            root = root.next;
        }
        return index;
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}
