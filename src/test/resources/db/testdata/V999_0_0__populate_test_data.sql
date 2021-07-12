-- Create test users
INSERT INTO users (id, card_id, first_name, last_name, email, birthday, studprog, is_member, created_by, created_date,
                   changed_by, changed_date)
VALUES (1, 12346, 'Kari', 'Nordmann', 'karnor@stud.ntnu.no', '1994-04-06 14:00:00.000000', 'BMAT', true, 'Migration',
        '2016-10-15 00:24:03.000000', null, '2021-07-12 18:39:20.039756');
INSERT INTO users (id, card_id, first_name, last_name, email, birthday, studprog, is_member, created_by, created_date,
                   changed_by, changed_date)
VALUES (2, 12345, 'Ola', 'Nordmann', 'olanor@stud.ntnu.no', '1990-04-23 14:00:00.000000', 'BFY', true, 'Migration',
        '2013-10-31 14:55:53.000000', null, '2021-07-12 18:39:20.052869');
INSERT INTO users (id, card_id, first_name, last_name, email, birthday, studprog, is_member, created_by, created_date,
                   changed_by, changed_date)
VALUES (3, 10, 'Erich Christian', 'Dahl', 'ecdahls@stud.ntnu.no', '2014-02-12 13:00:00.000000', 'Dahls', true,
        'Migration', '1970-01-01 01:00:00.000000', null, '2021-07-12 18:39:20.057486');

-- Create test user properties
INSERT INTO user_properties (id, type, value, created_by, created_date, changed_by, changed_date)
VALUES (1, 'CREDIT', '1', 'Migration', '2021-07-12 18:39:20.011844', null, '2021-07-12 8:39:20.011778');
INSERT INTO user_properties (id, type, value, created_by, created_date, changed_by, changed_date)
VALUES (2, 'COMMENT', 'Fyllesvin', 'Migration', '2021-07-12 18:39:20.020889', null, '201-07-12 18:39:20.020826');
INSERT INTO user_properties (id, type, value, created_by, created_date, changed_by, changed_date)
VALUES (3, 'CREDIT', '5', 'Migration', '2021-07-12 18:39:20.024118', null, '2021-07-12 18:39:20.024057');
INSERT INTO user_properties (id, type, value, created_by, created_date, changed_by, changed_date)
VALUES (4, 'TITLE', 'Drunkard', 'Migration', '2021-07-12 18:39:20.031276', null, '2021-07-12 18:39:20.031212');

-- Link test user properties
INSERT INTO users_user_properties (users_id, user_properties_id)
VALUES (1, 1);
INSERT INTO users_user_properties (users_id, user_properties_id)
VALUES (2, 2);
INSERT INTO users_user_properties (users_id, user_properties_id)
VALUES (2, 3);
INSERT INTO users_user_properties (users_id, user_properties_id)
VALUES (3, 4);

-- Create test user wallets
INSERT INTO wallets (id, user_id, cash_balance, total_spent, latest_transaction_id)
VALUES (1, 1, -100, 40285, null);
INSERT INTO wallets (id, user_id, cash_balance, total_spent, latest_transaction_id)
VALUES (2, 2, 0, 2825, null);

-- Create transactions
INSERT INTO transactions (id, wallet_id, previous_balance, balance_change, previous_transaction_id, hash,
                          transaction_date)
VALUES (1, 1, 0, -100, null, 0, '2021-07-12 18:39:50.525136');
INSERT INTO transactions (id, wallet_id, previous_balance, balance_change, previous_transaction_id, hash,
                          transaction_date)
VALUES (2, 2, 0, 0, null, 0, '2021-07-12 18:39:59.148185');

-- Link transactions to wallets
UPDATE wallets
set latest_transaction_id = 1
where id = 1;
UPDATE wallets
set latest_transaction_id = 2
where id = 2;

-- Jump forward in id generation sequences
alter sequence user_id_generator restart with 1000;
alter sequence user_property_id_generator restart with 1000;
alter sequence item_id_generator restart with 1000;
alter sequence wallet_id_generator restart with 1000;
alter sequence borrowed_item_id_generator restart with 1000;
alter sequence transaction_id_generator restart with 1000;
