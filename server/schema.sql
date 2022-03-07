drop database if exists defaultdb;

-- Create a database call todos
create database defaultdb;

use defaultdb;

drop table if exists favProduct;
drop table if exists user;

create table user (
    username varchar(64) not null,
    passcode varchar(512) not null,
    email varchar(512) not null,
    is_verified boolean
        default FALSE,
    signupDate timestamp
        default current_timestamp,
    primary key(username)
);

create table favProduct (
    productStoreUrl varchar(256) not null,
    productName varchar(128) not null,
    productImageUrl varchar(256),
    supermarketStore enum('Fairprice', 'Giant') not null,
    productCurrentPrice decimal(8,2) not null,
    productPreviousPrice decimal(8,2),
    productDiscountCondition varchar(256),
    productPercentageDiscount tinyint,
    username varchar(64) not null,
    log_time timestamp
        default current_timestamp,
    primary key(productStoreUrl),
    constraint fk_username
        foreign key(username)
        references user(username)
);


insert into user (username, passcode, email, is_verified) values ('admin', sha1('OneWifi&isgood!'), 'lobangphilosophy@gmail.com', TRUE);