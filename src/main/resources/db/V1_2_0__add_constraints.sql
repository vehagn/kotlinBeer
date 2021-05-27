alter table BORROWED_ITEMS
    add constraint BORROWER foreign key (borrower_card_id) references persons(card_id);
alter table BORROWED_ITEMS
    add constraint ITEM_ID foreign key (item_id) references items(id);

alter table PERSONS
    add constraint LATEST_TRANSACTION foreign key (latest_transaction_id) references transactions(id);

alter table TRANSACTIONS
    add constraint CARD_ID foreign key (person_card_id) references persons(card_id);
alter table TRANSACTIONS
    add constraint PREVIOUS_TRANSACTION foreign key (previous_transaction_id) references transactions(id);