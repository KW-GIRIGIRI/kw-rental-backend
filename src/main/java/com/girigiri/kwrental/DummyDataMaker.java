package com.girigiri.kwrental;

import com.girigiri.kwrental.equipment.domain.Category;
import com.girigiri.kwrental.equipment.domain.Equipment;
import com.girigiri.kwrental.equipment.repository.EquipmentRepository;
import com.girigiri.kwrental.item.domain.Item;
import com.girigiri.kwrental.item.repository.ItemRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("release")
@Component
public class DummyDataMaker {

    private final EquipmentRepository equipmentRepository;
    private final ItemRepository itemRepository;

    public DummyDataMaker(final EquipmentRepository equipmentRepository, final ItemRepository itemRepository) {
        this.equipmentRepository = equipmentRepository;
        this.itemRepository = itemRepository;
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 50; i++) {
            final Equipment equipment = equipmentRepository.save(create(i));
            itemRepository.save(new Item(null, "12345678", true, equipment.getId()));
            itemRepository.save(new Item(null, null, false, equipment.getId()));
        }
    }

    private Equipment create(int i) {
        return Equipment.builder()
                .modelName("모델이름" + i)
                .totalQuantity(i + 1)
                .maker("메이커")
                .category(Category.values()[i % 5])
                .purpose("더미 데이터라구")
                .imgUrl("https://img.danawa.com/prod_img/500000/240/891/img/17891240_1.jpg?shrink=130:130&_v=20230201140548")
                .rentalPlace("양동주 자택")
                .description("더미데이터의 안내사항")
                .components("이영현, 박다은, 김효리")
                .build();
    }
}
