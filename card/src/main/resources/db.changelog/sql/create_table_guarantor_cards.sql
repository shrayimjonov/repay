create table guarantor_cards
(
    card_id      bigint not null,
    guarantor_id bigint not null,
    added_type   varchar(255),
    ip_address   varchar(255),
    is_deleted   boolean,
    payer        varchar(255),
    created_by   varchar(255),
    created_date date,
    init_time    timestamp(6),
    modified_by  varchar(255),
    update_time  timestamp(6),
    primary key (card_id, guarantor_id)
);

create index guarantor_cards_guarantor_ind
    on guarantor_cards (guarantor_id);

create index guarantor_cards_card_ind
    on guarantor_cards (card_id);

CREATE INDEX guarantor_cards_loan_and_card_idx
    ON guarantor_cards (card_id, guarantor_id);


alter table if exists guarantor_cards
    add constraint FK27tu6myi5bptpdlap0em48vw8
        foreign key (card_id)
            references card;

alter table if exists guarantor_cards
    add constraint FKq51mmkhxcp8r2cqqqtxt6gjoh
        foreign key (guarantor_id)
            references guarantor;


COMMIT;

