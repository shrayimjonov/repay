create table reconciliation
(
    id                      bigint not null,
    credits_reversal_amount bigint,
    credits_reversal_number integer,
    debits_amount           bigint,
    debits_number           integer,
    payment_id              varchar(255),
    ref_num                 varchar(255),
    response                jsonb,
    status                  varchar(64),
    terminal_id             varchar(255),
    primary key (id)
);


create index reco_terminal_ind
    on reconciliation (terminal_id);

create sequence reco_seq start with 1 increment by 1;



COMMIT;