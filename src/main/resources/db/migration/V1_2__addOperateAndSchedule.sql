CREATE TABLE if not exists schedule
(
    `id`          bigint      NOT NULL AUTO_INCREMENT,
    `day_of_week` varchar(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_DAY_OF_WEEK` (`day_of_week`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 33
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE if not exists entire_operation
(
    `id`         bigint     NOT NULL AUTO_INCREMENT,
    `is_running` tinyint(1) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 33
  DEFAULT CHARSET = utf8mb3;

insert into entire_operation (is_running) value (true);
insert into schedule (day_of_week) value ('TUESDAY');
insert into schedule (day_of_week) value ('WEDNESDAY');
insert into schedule (day_of_week) value ('THURSDAY');
insert into schedule (day_of_week) value ('FRIDAY');