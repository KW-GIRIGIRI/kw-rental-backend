package com.girigiri.kwrental.util;


import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class QueryDSLUtils {

    public static Predicate isContains(final String input, StringPath path) {
        return input == null ? null : path.contains(input);
    }

    public static <T> Predicate isEqualTo(final T t, final SimpleExpression<T> expression) {
        return t == null ? null : expression.eq(t);
    }

    public static <T> JPAQuery<T> setPageable(final JPAQuery<T> query, final Path<?> path, final Pageable pageable) {
        return query
                .orderBy(getOrderSpecFrom(pageable.getSort(), path))
                .limit(pageable.getPageSize())
                .offset(getOffsetFrom(pageable));
    }

    private static OrderSpecifier<?>[] getOrderSpecFrom(final Sort sort, final Path<?> path) {
        return sort.stream()
                .map(order -> mapToOrderSpec(order, path))
                .toArray(OrderSpecifier[]::new);
    }

    private static OrderSpecifier<?> mapToOrderSpec(final Order order, final Path<?> path) {
        com.querydsl.core.types.Order direction = order.isAscending() ? ASC : DESC;
        SimplePath<Object> filedPath = Expressions.path(Object.class, path, order.getProperty());
        return new OrderSpecifier(direction, filedPath);
    }

    private static long getOffsetFrom(final Pageable pageable) {
        final int pageNumber = pageable.getPageNumber();
        return pageNumber == 0 ? 0 : (long) pageNumber * pageable.getPageSize();
    }
}
