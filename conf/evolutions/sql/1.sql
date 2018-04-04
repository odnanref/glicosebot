# User schema

# --- !Ups

create table `executions` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
       `name` VARCHAR(254) NOT NULL,
        `execution` VARCHAR(254) NOT NULL
    );

create table `commands` (`id` BIGINT AUTO_INCREMENT PRIMARY KEY,
`name` VARCHAR(254) NOT NULL,
`description` VARCHAR(254) NOT NULL,
`execution_id` BIGINT,
        CONSTRAINT `fk_execution_id`
        FOREIGN KEY (`execution_id`)
        REFERENCES executions(`id`)
                ON DELETE CASCADE
                ON UPDATE RESTRICT
);

ALTER TABLE executions ADD FULLTEXT(`name`);
ALTER TABLE commands ADD FULLTEXT(`name`);

create table `user_profile` ( `id` BIGINT UNIQUE,
    first_name VARCHAR(254),
    last_name VARCHAR(254),
    profile_pic VARCHAR(254),
    locale VARCHAR(54),
    timezone int,
    gender VARCHAR(254)
    );

create table glicose (`id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    inputed varchar(4),
    datein DATETIME,
    user_profile_id BIGINT,
    food VARCHAR(254),
    CONSTRAINT `fk_glicose_user_profile`
        FOREIGN KEY (`user_profile_id`)
        REFERENCES user_profile(`id`)
            ON DELETE CASCADE
            ON UPDATE RESTRICT
 );

# --- !Downs
drop table `commands`;
drop table `executions`;
drop table `user_profile`;
