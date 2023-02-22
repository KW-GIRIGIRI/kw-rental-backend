package com.girigiri.kwrental.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class LinkUtils {

    public static String createIfNextExists(final Slice<?> slice, final UriComponentsBuilder builder) {
        if (slice.hasNext()) {
            return addPageableParameters(builder, slice.nextPageable());
        }
        return null;
    }

    public static String createIfPreviousExists(final Slice<?> slice, final UriComponentsBuilder builder) {
        if (slice.hasPrevious()) {
            return addPageableParameters(builder, slice.previousPageable());
        }
        return null;
    }

    private static String addPageableParameters(final UriComponentsBuilder builder, final Pageable pageable) {
        return builder
                .queryParam("size", pageable.getPageSize())
                .queryParam("page", pageable.getPageNumber())
                .queryParam("sort", sortToString(pageable.getSort()))
                .build().toUriString();
    }

    private static String sortToString(final Sort sort) {
        StringBuilder builder = new StringBuilder();

        for (Order order : sort) {
            builder.append("&sort=").append(order.getProperty())
                    .append(",").append(order.getDirection().name());
        }
        builder.delete(0, 6);
        return builder.toString();
    }
}
