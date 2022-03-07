drop database if exists defaultdb;

-- Create a database call todos
create database defaultdb;

use data_app;

drop table if exists watchlist;
drop table if exists user;
drop table if exists product;
drop table if exists watchrecord;

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

create table product (
    productStoreUrl varchar(256) not null,
    productName varchar(128) not null,
    productImageUrl varchar(256),
    supermarketStore enum('Fairprice', 'Giant') not null,
    last_update timestamp
        default current_timestamp
        on update current_timestamp,
    primary key(productStoreUrl)
);

create table watchlist (
    watchid int auto_increment not null,
    username varchar(64) not null,
    productStoreUrl varchar(256) not null,
    primary key(watchid),
    constraint fk_username
        foreign key(username)
        references user(username),
    constraint fk_productStoreUrl
        foreign key(productStoreUrl)
        references product(productStoreUrl)
);

create table watchrecord (
    recordDate date not null,
    watchid int auto_increment not null,
    username varchar(64) not null,
    productStoreUrl varchar(256) not null,
    productCurrentPrice decimal(8,2) not null,
    productPreviousPrice decimal(8,2),
    productDiscountCondition varchar(256),
    productPercentageDiscount tinyint,
    primary key(watchid, recordDate)
);

insert into user (username, passcode, email, is_verified) values ('admin', sha1('OneWifi&isgood!'), 'adminfromgdmb@gmail.com', TRUE);