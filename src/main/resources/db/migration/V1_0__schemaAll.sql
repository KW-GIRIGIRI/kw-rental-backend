CREATE TABLE if not exists `asset`
(
    `id`                        bigint      NOT NULL AUTO_INCREMENT,
    `dtype`                     varchar(20) NOT NULL,
    `max_rental_days`           int         NOT NULL,
    `name`                      varchar(50) NOT NULL,
    `total_quantity`            int         NOT NULL,
    `rentable_quantity`         int         NOT NULL,
    `category`                  varchar(15)  DEFAULT NULL,
    `components`                varchar(100) DEFAULT NULL,
    `description`               varchar(100) DEFAULT NULL,
    `img_url`                   varchar(255) DEFAULT NULL,
    `maker`                     varchar(20)  DEFAULT NULL,
    `purpose`                   varchar(100) DEFAULT NULL,
    `rental_place`              varchar(20)  DEFAULT NULL,
    `is_available`              tinyint(1)   DEFAULT NULL,
    `reservation_count_per_day` int          DEFAULT NULL,
    `notice`                    text         DEFAULT NULL,
    `deleted_at`                date         DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_ASSET_NAME` (`name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 19
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE if not exists `inventory`
(
    `id`                bigint NOT NULL AUTO_INCREMENT,
    `member_id`         bigint NOT NULL,
    `amount`            int    NOT NULL,
    `rental_end_date`   date   NOT NULL,
    `rental_start_date` date   NOT NULL,
    `asset_id`          bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_INVENTORY_ASSET` (`asset_id`),
    CONSTRAINT `FK_INVENTORY_ASSET` FOREIGN KEY (`asset_id`) REFERENCES `asset` (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 73
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `item`
(
    `id`              bigint      NOT NULL AUTO_INCREMENT,
    `available`       tinyint(1)  NOT NULL DEFAULT '1',
    `asset_id`        bigint      NOT NULL,
    `property_number` varchar(20) NOT NULL,
    `deleted_at`      date                 DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `property_number` (`property_number`),
    UNIQUE KEY `UK_ITEM_PROPERTY_NUMBER` (`property_number`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 33
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `member`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `birth_date`    varchar(10)  NOT NULL,
    `email`         varchar(30)  NOT NULL,
    `member_number` varchar(15)  NOT NULL,
    `name`          varchar(10)  NOT NULL,
    `password`      varchar(255) NOT NULL,
    `phone_number`  varchar(15)  NOT NULL,
    `role`          varchar(10) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `penalty`
(
    `id`                  bigint      NOT NULL AUTO_INCREMENT,
    `member_id`           bigint      NOT NULL,
    `end_date`            date        NOT NULL,
    `start_date`          date        NOT NULL,
    `reason`              varchar(20) NOT NULL,
    `rental_spec_id`      bigint      NOT NULL,
    `reservation_id`      bigint      NOT NULL,
    `reservation_spec_id` bigint      NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 36
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `rental_spec`
(
    `id`                  bigint       NOT NULL AUTO_INCREMENT,
    `accept_date_time`    timestamp(6) NULL DEFAULT NULL,
    `property_number`     varchar(20)       DEFAULT NULL,
    `reservation_id`      bigint       NOT NULL,
    `reservation_spec_id` bigint       NOT NULL,
    `return_date_time`    timestamp(6) NULL DEFAULT NULL,
    `status`              varchar(20)  NOT NULL,
    `dtype`               varchar(20)  NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 56
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `reservation`
(
    `id`               bigint       NOT NULL AUTO_INCREMENT,
    `accept_date_time` timestamp(6) NULL DEFAULT NULL,
    `email`            varchar(30)  NOT NULL,
    `member_id`        bigint       NOT NULL,
    `name`             varchar(10)  NOT NULL,
    `phone_number`     varchar(15)  NOT NULL,
    `purpose`          varchar(50)  NOT NULL,
    `is_terminated`    tinyint(1)   NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 108
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `reservation_spec`
(
    `id`                bigint      NOT NULL AUTO_INCREMENT,
    `amount`            int         NOT NULL,
    `rental_end_date`   date        NOT NULL,
    `rental_start_date` date        NOT NULL,
    `status`            varchar(20) NOT NULL,
    `asset_id`          bigint      NOT NULL,
    `reservation_id`    bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_RESERVATION_SPEC_RESERVATION` (`reservation_id`),
    KEY `FK_RESERVATION_SPEC_ASSET` (`asset_id`),
    CONSTRAINT `FK_RESERVATION_SPEC_ASSET` FOREIGN KEY (`asset_id`) REFERENCES `asset` (`id`),
    CONSTRAINT `FK_RESERVATION_SPEC_RESERVATION` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 121
  DEFAULT CHARSET = utf8mb3;

CREATE TABLE `lab_room_daily_ban`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `ban_date`    date   NOT NULL,
    `lab_room_id` bigint NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb3;