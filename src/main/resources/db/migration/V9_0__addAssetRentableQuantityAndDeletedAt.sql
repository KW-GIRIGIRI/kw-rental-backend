alter table asset
    add column rentable_quantity integer not null;

alter table asset
    add column deleted_at date;

alter table item
    add column deleted_at date;