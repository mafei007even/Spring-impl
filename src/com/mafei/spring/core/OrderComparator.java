package com.mafei.spring.core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author mafei007
 * @date 2022/7/9 01:47
 */
public class OrderComparator implements Comparator<Object> {

    public static final OrderComparator INSTANCE = new OrderComparator();

    public static void sort(List<?> list) {
        if (list.size() > 1) {
            list.sort(INSTANCE);
        }
    }

    public static void sort(Object[] array) {
        if (array.length > 1) {
            Arrays.sort(array, INSTANCE);
        }
    }

    @Override
    public int compare(Object o1, Object o2) {
        boolean p1 = o1 instanceof PriorityOrdered;
        boolean p2 = o2 instanceof PriorityOrdered;
        if (p1 && !p2) {
            return -1;
        } else if (!p1 && p2) {
            return 1;
        }
        int order1 = getOrder(o1);
        int order2 = getOrder(o2);
        // 不要直接写 order1 - order2， 会照成数值溢出问题
        // 使用 Integer.compare 避免溢出
        return Integer.compare(order1, order2);
    }

    private int getOrder(Object obj) {
        if (obj == null) {
            return Ordered.LOWEST_PRECEDENCE;
        }
        if (obj instanceof Ordered) {
            return ((Ordered) obj).getOrder();
        } else {
            return Ordered.LOWEST_PRECEDENCE;
        }
    }
}
