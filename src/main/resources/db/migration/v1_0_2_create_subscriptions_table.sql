--liquibase formatted sql
--changeset sewaiper@bk.ru:v1_0_2_create_subscriptions_table

create table if not exists subscriptions (
    user_id bigint not null ,
    channel_id bigint not null ,

    primary key (user_id, channel_id) ,
    foreign key (user_id) references users(id) ,
    foreign key (channel_id) references channels(id)
);
