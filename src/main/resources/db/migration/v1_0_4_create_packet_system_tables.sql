--liquibase formatted sql
--changeset sewaiper@bk.ru:v1_0_5_create_packet_system_tables

create table if not exists packet_config (
    id bigint ,
    max_size int not null ,
    ttl int not null ,

    primary key (id) ,
    foreign key (id) references users(id)
);

create table if not exists packets (
    id int generated always as identity ,
    user_id bigint not null ,
    created_at bigint not null ,

    primary key (id) ,
    foreign key (user_id) references users(id)
);

create table if not exists packets_content (
    packet_id int not null ,
    message_id bigint not null ,

    primary key (packet_id, message_id) ,
    foreign key (packet_id) references packets(id) ,
    foreign key (message_id) references messages(id)
);
