create table if not exists equipment
(
    id              bigint auto_increment,
    category        varchar(15)  not null,
    components      varchar(100),
    description     varchar(100) not null,
    img_url         varchar(255) not null,
    maker           varchar(20)  not null,
    max_rental_days integer      not null,
    model_name      varchar(50)  not null unique,
    purpose         varchar(100),
    rental_place    varchar(20)  not null,
    total_quantity  integer      not null,
    primary key (id)
);

create table if not exists inventory
(
    id                bigint auto_increment,
    member_id         bigint  not null,
    amount            integer not null,
    rental_end_date   date    not null,
    rental_start_date date    not null,
    equipment_id      bigint  not null,
    primary key (id)
);

create table if not exists item
(
    id              bigint auto_increment,
    available       boolean     not null,
    equipment_id    bigint      not null,
    property_number varchar(20) not null unique,
    primary key (id)
);

create table if not exists member
(
    id            bigint auto_increment,
    birth_date    varchar(10)  not null,
    email         varchar(30)  not null,
    member_number varchar(15)  not null,
    name          varchar(10)  not null,
    password      varchar(255) not null,
    phone_number  varchar(15)  not null,
    role          varchar(10),
    primary key (id)
);

create table if not exists rental_spec
(
    id                  bigint auto_increment,
    accept_date_time    timestamp(6),
    property_number     varchar(20) not null,
    reservation_id      bigint      not null,
    reservation_spec_id bigint      not null,
    return_date_time    timestamp(6),
    status              varchar(20) not null,
    primary key (id)
);

create table if not exists reservation
(
    id               bigint auto_increment,
    accept_date_time timestamp(6),
    email            varchar(30) not null,
    member_id        bigint      not null,
    name             varchar(10) not null,
    phone_number     varchar(15) not null,
    purpose          varchar(50) not null,
    `terminated`     boolean     not null,
    primary key (id)
);

create table if not exists reservation_spec
(
    id                bigint auto_increment,
    amount            integer     not null,
    rental_end_date   date        not null,
    rental_start_date date        not null,
    status            varchar(20) not null,
    equipment_id      bigint      not null,
    reservation_id    bigint,
    primary key (id)
);

alter table inventory
    add constraint FK_INVENTORY_EQUIPMENT foreign key (equipment_id) references equipment (id);
alter table reservation_spec
    add constraint FK_RESERVATION_SPEC_EQUIPMENT foreign key (equipment_id) references equipment (id);
alter table reservation_spec
    add constraint FK_RESERVATION_SPEC_RESERVATION foreign key (reservation_id) references reservation (id);