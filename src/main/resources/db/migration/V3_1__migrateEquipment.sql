INSERT INTO asset (dtype, id, max_rental_days, name, total_quantity, category, components, description, img_url, maker,
                   purpose, rental_place, is_available)
SELECT 'equipment',
       id,
       max_rental_days,
       model_name,
       total_quantity,
       category,
       components,
       description,
       img_url,
       maker,
       purpose,
       rental_place,
       NULL
FROM equipment;

SET
@max_id = (SELECT MAX(id) + 1 FROM equipment);
SET
@sql = CONCAT('ALTER TABLE asset AUTO_INCREMENT = ', @max_id);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;