create table log_protocol
(
    id           bigint not null,
    loan_ext_id  varchar(31),
    request      jsonb,
    type         varchar(31),
    created_by   varchar(63),
    created_date date,
    init_time    timestamp(6),
    modified_by  varchar(63),
    update_time  timestamp(6),
    primary key (id)
);

create sequence log_protocol_seq start with 1 increment by 1;

COMMIT;