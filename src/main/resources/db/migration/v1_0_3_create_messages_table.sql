--liquibase formatted sql
--changeset sewaiper@bk.ru:v1_0_3_create_messages_table

create table if not exists messages (
    id bigint ,
    channel_id bigint ,
    link text not null ,
    brief text  ,
    "text" text ,
    tag int ,

    primary key (id) ,
    foreign key (channel_id) references channels(id)
);
