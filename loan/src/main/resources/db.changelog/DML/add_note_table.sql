create table note
(
    id           bigint not null,
    loan_ext_id  varchar(255),
    note         TEXT,
    created_by   varchar(255),
    created_date date,
    init_time    timestamp(6),
    modified_by  varchar(255),
    update_time  timestamp(6),
    primary key (id)
);

create index note_loan_ext_id_ind
    on note (loan_ext_id);

create sequence note_seq start with 1 increment by 1;

COMMIT;