create table guarantor
(
    id           bigint not null,
    first_name   varchar(255),
    surname      varchar(255),
    patronymic   varchar(255),
    birthday     timestamp(6),
    gender       varchar(255) check (gender in ('MALE', 'FEMALE')),
    language     varchar(255),
    passport     jsonb,
    person_code  varchar(255),
    phone        varchar(255),
    created_by   varchar(255),
    created_date date,
    init_time    timestamp(6),
    modified_by  varchar(255),
    update_time  timestamp(6),
    primary key (id)
);

create table loan_guarantors
(
    guarantor_id bigint not null,
    loan_id      bigint not null,
    ip_address   varchar(255),
    is_deleted   boolean,
    created_by   varchar(255),
    created_date date,
    init_time    timestamp(6),
    modified_by  varchar(255),
    update_time  timestamp(6),
    primary key (guarantor_id, loan_id)
);

create index guarantor_person_code_ind
    on guarantor (person_code);

alter table if exists guarantor
    add constraint guarantor_person_code_un unique (person_code);

create index loan_guarantor_ind
    on loan_guarantors (loan_id, guarantor_id);

create sequence guarantor_seq start with 1 increment by 50;

alter table if exists loan_guarantors
    add constraint FKt7lmky5vmuov5fcdrvvcr46l0
        foreign key (guarantor_id)
            references guarantor;

alter table if exists loan_guarantors
    add constraint FK7fa729tsb3j3xie1rw1gqy032
        foreign key (loan_id)
            references loan;