-- Sequences
create sequence if not exists user_id_generator start with 1 increment by 50;
create sequence if not exists user_property_id_generator start with 1 increment by 50;
create sequence if not exists item_id_generator start with 1 increment by 50;
create sequence if not exists borrowed_item_id_generator start with 1 increment by 50;
create sequence if not exists transaction_id_generator start with 1 increment by 50;

-- Types
create type user_property as enum ('TITLE', 'COMMENT', 'OTHER');

-- Tables
create table users
(
    id                    int8 primary key,
    card_id               int8 unique not null,
    first_name            varchar(63),
    last_name             varchar(63),
    username              varchar(31),
    birthday              timestamp,
    studprog              varchar(31),
    is_member             boolean     not null,
    tab                   int2,
    cash_balance          int4        not null,
    total_spent           int4        not null,
    latest_transaction_id int8,
    created               timestamp,
    changed               timestamp
);

create table user_properties
(
    id       int8 primary key,
    property user_property not null,
    value    varchar(255),
    created  timestamp,
    changed  timestamp
);

create table users_user_properties
(
    users_id           int8        not null,
    user_properties_id int8 unique not null
);

create table transactions
(
    id                      int8 primary key,
    user_id                 int8,
    previous_balance        int4 not null,
    balance_change          int4 not null,
    previous_transaction_id int8,
    hash                    int8 not null,
    transaction_date        timestamp
);

create table items
(
    id          int8 primary key,
    name        varchar(63),
    description varchar(255),
    created     timestamp,
    changed     timestamp
);

create table borrowed_items
(
    id             int8 primary key,
    item_id        int8,
    borrower_id    int8,
    comment        varchar(255),
    borrowed_date  timestamp,
    return_by_date timestamp,
    returned_date  timestamp
);

-- Relations
alter table users
    add constraint latest_transaction foreign key (latest_transaction_id) references transactions;

alter table users_user_properties
    add constraint user_property_id foreign key (user_properties_id) references user_properties;
alter table users_user_properties
    add constraint user_id foreign key (users_id) references users;

alter table borrowed_items
    add constraint borrower_id foreign key (borrower_id) references users;
alter table borrowed_items
    add constraint item_id foreign key (item_id) references items;

alter table transactions
    add constraint previous_transaction_id foreign key (previous_transaction_id) references transactions;
alter table transactions
    add constraint user_id foreign key (user_id) references users;
