drop table if exists legacy_users;

create table legacy_users
(
    card_id       bigint       not null primary key,
    last_name     varchar(64)  null,
    first_name    varchar(64)  null,
    username      varchar(16)  null,
    birthday      varchar(8)   null,
    studprog      varchar(10)  null,
    membership    int          null,
    userlevel     int          null,
    password      varchar(64)  null,
    tab           int          null,
    cash          int          null,
    spent         int          null,
    borrowed      varchar(128) null,
    comment       varchar(128) null,
    misc          varchar(128) null,
    creation_date int          null
);

INSERT INTO legacy_users (card_id, last_name, first_name, username, birthday, studprog, membership, userlevel, password,
                          tab, cash, spent, borrowed, comment, misc, creation_date)
VALUES (12345, 'Nordmann', 'Ola', 'olanor', '230490', 'BFY', 1, 9, '', 5, 0, 2825, '', '', 'Fyllesvin', 1383227753);

INSERT INTO legacy_users (card_id, last_name, first_name, username, birthday, studprog, membership, userlevel, password,
                          tab, cash, spent, borrowed, comment, misc, creation_date)
VALUES (12346, 'Nordmann', 'Kari', 'karnor', '060494', 'BMAT', 1, 9, '', 1, -100, 40285, '', '', '', 1476483843);

INSERT INTO legacy_users (card_id, last_name, first_name, username, birthday, studprog, membership, userlevel, password,
                          tab, cash, spent, borrowed, comment, misc, creation_date)
VALUES (10, 'Dahl', 'Erich Christian', 'ecdahls', '120214', 'Dahls', 1, 0, '', 0, 105, 35948, '', 'Drunkard', '', 0);
