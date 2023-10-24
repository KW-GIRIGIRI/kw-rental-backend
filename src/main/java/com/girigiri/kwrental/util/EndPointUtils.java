package com.girigiri.kwrental.util;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class EndPointUtils {

    public List<String> createAllPageEndPoints(final Page<?> page) {
        final ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();
        if (page == null || page.isEmpty()) {
            return Collections.emptyList();
        }
        final int totalPages = page.getTotalPages();
        return IntStream.range(0, totalPages)
            .mapToObj(pageCount -> mapToEndPoint(builder, page.getPageable(), pageCount))
            .toList();
    }

    private String mapToEndPoint(final UriComponentsBuilder builder, final Pageable pageable,
        final int pageNumber) {
        final UriComponentsBuilder clonedBuilder = builder.cloneBuilder();
        final UriComponentsBuilder builderWithParams = setPageableParams(clonedBuilder, pageable, pageNumber);
        return buildEndpoint(builderWithParams);
    }

    private UriComponentsBuilder setPageableParams(final UriComponentsBuilder clonedBuilder,
        final Pageable pageable, final int pageNumber) {
        return clonedBuilder
            .replaceQueryParam("size", pageable.getPageSize())
            .replaceQueryParam("page", pageNumber)
            .replaceQueryParam("sort", sortToString(pageable.getSort()));
    }

    private String buildEndpoint(final UriComponentsBuilder builder) {
        final UriComponents uriComponents = builder.build();
        final String endPoint = uriComponents.getPath() + "?" + uriComponents.getQuery();
        return endPoint.replaceFirst("/\\?", "?");
    }

    private String sortToString(final Sort sort) {
        if (sort.isEmpty())
            return "";
        StringBuilder builder = new StringBuilder();

        for (Order order : sort) {
            builder.append("&sort=").append(order.getProperty())
                .append(",").append(order.getDirection().name());
        }
        builder.delete(0, 6);
        return builder.toString();
    }
}
