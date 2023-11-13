package com.girigiri.kwrental.item.repository;

import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.testsupport.RepositoryTest;
import com.girigiri.kwrental.testsupport.fixture.ItemFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class ItemRepositoryTest {

    @Mock
    private ItemJpaRepository itemJpaRepository;
    @Mock
    private ItemConstraintPolicy itemConstraintPolicy;
    @InjectMocks
    private ItemRepositoryImpl itemRepositoryImpl;

    @Test
    @DisplayName("자산번호 고유 제약 조건을 지켜야 품목들을 등록할 수 있다.")
    void saveAll() {
        // given
        final Item item = ItemFixture.create();
        doNothing().when(itemConstraintPolicy).validateNotDeletedPropertyNumberIsUnique(any());

        // when, then
        assertThatCode(() -> itemRepositoryImpl.saveAll(List.of(item)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("자산번호 고유 제약 조건을 지켜야 단일 품목을 등록할 수 있다.")
    void save() {
        // given
        final Item item = ItemFixture.create();
        doNothing().when(itemConstraintPolicy).validateNotDeletedPropertyNumberIsUnique(any());

        // when, then
        assertThatCode(() -> itemRepositoryImpl.save(item))
                .doesNotThrowAnyException();
    }
}
