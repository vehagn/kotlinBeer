create sequence if not exists hibernate_sequence start with 1 increment by 1;
create sequence if not exists user_id_generator start with 1 increment by 50;

create table borrowed_items
(
    id             bigint not null,
    item_id        bigint,
    borrower_id    bigint,
    comment        varchar(255),
    borrowed_date  timestamp,
    return_by_date timestamp,
    returned_date  timestamp,
    primary key (id)
);

create table items
(
    id          bigint not null,
    name        varchar(255),
    description varchar(255),
    created     timestamp,
    changed     timestamp,
    primary key (id)
);

create table users
(
    id                    bigint  not null,
    card_id               bigint  not null,
    first_name            varchar(255),
    last_name             varchar(255),
    studprog              varchar(255),
    is_member             boolean not null,
    userlevel             tinyint not null,
    title                 varchar(255),
    comment               varchar(255),
    username              varchar(255),
    password              varchar(255),
    cash_balance          integer not null,
    total_spent           integer not null,
    tab                   tinyint not null,
    latest_transaction_id bigint,
    changed               timestamp,
    created               timestamp,
    primary key (id),
);

create table transactions
(
    id                      bigint  not null,
    user_id                 bigint  not null,
    previous_balance        integer not null,
    balance_change          integer not null,
    current_balance         integer not null,
    previous_transaction_id bigint,
    transaction_date        timestamp,
    hash                    integer not null,
    primary key (id),
);