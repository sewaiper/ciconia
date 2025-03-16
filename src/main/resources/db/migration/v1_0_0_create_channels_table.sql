--liquibase formatted sql
--changeset sewaiper@bk.ru:v1_0_0_create_channels_table

create table if not exists channels (
    id bigint ,
    supergroup_id bigint not null ,
    name text not null ,
    title text not null ,

    unique (name) ,

    constraint name_len_chk check (length(name) < 1024) ,
    constraint title_len_chk check (length(title) < 2048) ,

    primary key (id)
);
