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