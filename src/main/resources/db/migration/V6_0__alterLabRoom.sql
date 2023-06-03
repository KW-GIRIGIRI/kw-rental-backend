ALTER TABLE asset
    ADD COLUMN reservation_count_per_day integer;

UPDATE asset
set reservation_count_per_day = 16
where name = 'hanul';
UPDATE asset
set reservation_count_per_day = 1
where name = 'hwado';
UPDATE asset
set total_quantity = 10
where name = 'hwado';
