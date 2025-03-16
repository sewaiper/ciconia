--liquibase formatted sql
--changeset sewaiper@bk.ru:v1_0_1_create_user_table

create table if not exists users (
    id bigint ,
    username text not null ,
    first_name text ,
    last_name text ,

    command text ,
    chat_id bigint not null ,

    check(length(command) < 512) ,

    primary key (id)
);
