package com.girigiri.kwrental.item.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.girigiri.kwrental.item.domain.Item;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemJdbcRepositoryCustomImpl implements ItemJdbcRepositoryCustom {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public int saveAll(final List<Item> items) {
        final String query = "INSERT INTO item (property_number, available, asset_id) values (?, ?, ?)";
        final int[][] affectedRows = jdbcTemplate.batchUpdate(query, items, items.size(), (ps, arg) -> {
            ps.setString(1, arg.getPropertyNumber());
            ps.setBoolean(2, arg.isAvailable());
            ps.setLong(3, arg.getAssetId());
        });
        return sum(affectedRows);
    }

    private int sum(final int[][] array) {
        int sum = 0;
        for (int[] row : array) {
            for (int num : row) {
                sum += num;
            }
        }
        return sum;
    }
}
