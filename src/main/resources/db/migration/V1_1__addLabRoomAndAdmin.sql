insert into asset (max_rental_days, name, total_quantity, rentable_quantity, is_available, dtype,
                   reservation_count_per_day)
values (1, 'hanul', 16, 16, true, 'lab_room', 16);
insert into asset (max_rental_days, name, total_quantity, rentable_quantity, is_available, dtype,
                   reservation_count_per_day)
values (1, 'hwado', 10, 10, true, 'lab_room', 3);

insert into member (birth_date, email, member_number, name, password, phone_number, role)
values ('000000', 'admin@admin.com', 'admin', 'admin', '$2a$10$.x6pa9PIb14vW6AOP9tfi.MzFDHfCcGDilAnp8P1DPLg0wqKKzkqS',
        '000000000', 'ADMIN');