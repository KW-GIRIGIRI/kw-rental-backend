package com.girigiri.kwrental.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.web.util.UriComponentsBuilder;

class LinkUtilsTest {

    private final LinkUtils linkUtils = new LinkUtils();
    private final UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/example");

    @Test
    @DisplayName("다음 페이지가 존재하는 경우 링크를 생성한다.")
    void createIfNextExists() {
        // given
        final SliceImpl<Integer> slice = new SliceImpl<>(List.of(1, 2, 3),
                PageRequest.of(0, 3, Sort.by("value").descending()), true);

        // when
        final String expect = linkUtils.createIfNextExists(slice, builder);

        // then
        assertThat(expect).isNotNull()
                .contains("?size=3&page=1&sort=value,DESC");
    }

    @Test
    @DisplayName("이전 페이지가 존재하는 경우 링크를 생성한다.")
    void createIfPreviousExists() {
        // given
        final SliceImpl<Integer> slice = new SliceImpl<>(List.of(1, 2, 3),
                PageRequest.of(1, 3, Sort.by("value").descending()), true);

        // when
        final String expect = linkUtils.createIfPreviousExists(slice, builder);

        // then
        assertThat(expect).isNotNull()
                .contains("?size=3&page=0&sort=value,DESC");
    }
}
