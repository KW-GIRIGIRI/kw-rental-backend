create table if not exists asset
(
    id              bigint auto_increment,
    dtype           varchar(20) not null,
    max_rental_days integer     not null,
    name            varchar(50) not null,
    total_quantity  integer     not null,
    category        varchar(15),
    components      varchar(100),
    description     varchar(100),
    img_url         varchar(255),
    maker           varchar(20),
    purpose         varchar(100),
    rental_place    varchar(20),
    is_available    boolean,
    primary key (id)
);

alter table inventory
    rename column equipment_id TO asset_id;

alter table item rename column equipment_id TO asset_id;

alter table reservation_spec
    rename column equipment_id TO asset_id;

alter table asset
    add constraint UK_ASSET_NAME unique (name);

alter table item
    add constraint UK_ITEM_PROPETY_NUMBER unique (property_number);

alter table inventory
    add constraint FK_INVENTORY_ASSET foreign key (asset_id) references asset (id);

alter table reservation_spec
    add constraint FK_RESERVATION_SPEC_ASSET foreign key (asset_id) references asset (id);

alter table inventory
    drop constraint FK_INVENTORY_EQUIPMENT;

alter table reservation_spec
    drop constraint FK_RESERVATION_SPEC_EQUIPMENT;


