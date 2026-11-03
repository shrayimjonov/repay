create table chambers_application
(
    id                 uuid         not null,
    ext_id             varchar(255),
    ip_address         varchar(255),
    loan_ext_id        varchar(255) not null,
    file_ids           jsonb,
    request            jsonb,
    response           jsonb,
    responsible_person varchar(255),
    status             varchar(255),
    created_by         varchar(255),
    created_date       date,
    init_time          timestamp(6),
    modified_by        varchar(255),
    update_time        timestamp(6),
    primary key (id)
);

create index chambers_loanExtId_idx
    on chambers_application (loan_ext_id);

create index chambers_createdDate_idx
    on chambers_application (created_date);
