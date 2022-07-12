package com.mafei.spring.core;

/**
 * Extension of the {@link Ordered} interface, expressing a <em>priority</em>
 * ordering: {@code PriorityOrdered} objects are always applied before
 * <em>plain</em> {@link Ordered} objects regardless of their order values.
 *
 * <p>When sorting a set of {@code Ordered} objects, {@code PriorityOrdered}
 * objects and <em>plain</em> {@code Ordered} objects are effectively treated as
 * two separate subsets, PriorityOrdered 对象集位于纯 Ordered 对象集之前,并在这些子集内应用相对排序。
 */
public interface PriorityOrdered extends Ordered {
}