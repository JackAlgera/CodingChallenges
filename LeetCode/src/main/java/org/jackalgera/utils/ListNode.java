package org.jackalgera.utils;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
public class ListNode {
    public int val;
    public ListNode next;

    public ListNode() {
        this.next = null;
    }

    public ListNode(int val) {
        this.val = val;
        this.next = null;
    }

    public ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }

    public ListNode(int[] ints) {
        this.next = null;

        if (ints.length == 0) {
            return;
        }

        this.val = ints[0];
        if (ints.length == 1) {
            return;
        }

        ListNode current = new ListNode(ints[1]);
        this.next = current;

        for (int i = 2; i < ints.length; i++) {
            current.next = new ListNode(ints[i]);
            current = current.next;
        }
    }

    @Override
    public String toString() {
        String output = "[" + val;
        ListNode current = this.next;
        while (current != null) {
            output = output + "," + current.val;
            current = current.next;
        }
        return output + "]";
    }
}
