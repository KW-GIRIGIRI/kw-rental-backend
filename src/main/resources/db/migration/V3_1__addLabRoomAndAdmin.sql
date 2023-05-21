insert into asset (max_rental_days, name, total_quantity, is_available, dtype)
values (1, 'hanul', 16, true, 'lab_room');
insert into asset (max_rental_days, name, total_quantity, is_available, dtype)
values (1, 'hwado', 1, true, 'lab_room');

insert into member (birth_date, email, member_number, name, password, phone_number, role)
values ('000000', 'admin@admin.com', 'admin', 'admin', '$2a$10$.x6pa9PIb14vW6AOP9tfi.MzFDHfCcGDilAnp8P1DPLg0wqKKzkqS',
        '000000000', 'ADMIN');