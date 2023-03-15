package com.girigiri.kwrental.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class EndPointUtilsTest {

    @Test
    @DisplayName("가능한 모든 페이지의 링크를 생성한다.")
    void createAllPageLinks() {
        // given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/mock");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        final PageImpl<Integer> page = new PageImpl<>(List.of(1, 2, 3),
                PageRequest.of(0, 3, Sort.by("value").descending()), 10);

        // when
        final List<String> links = EndPointUtils.createAllPageEndPoints(page);

        // then
        assertThat(links).hasSize(4)
                .containsExactly(
                        "/mock?size=3&page=0&sort=value,DESC",
                        "/mock?size=3&page=1&sort=value,DESC",
                        "/mock?size=3&page=2&sort=value,DESC",
                        "/mock?size=3&page=3&sort=value,DESC"
                );
    }
}
