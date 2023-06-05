create table if not exists lab_room_daily_ban
(
    id          bigint auto_increment,
    ban_date    date   not null,
    lab_room_id bigint not null,
    primary key (id)
)