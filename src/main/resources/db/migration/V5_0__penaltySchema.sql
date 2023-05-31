create table if not exists penalty
(
    id
    bigint
    auto_increment,
    member_id
    bigint
    not
    null,
    end_date
    date
    not
    null,
    start_date
    date
    not
    null,
    reason
    varchar
(
    20
) not null,
    rental_spec_id bigint not null,
    reservation_id bigint not null,
    reservation_spec_id bigint not null,
    primary key
(
    id
)
    );