create table autopay (
                         id bigint not null,
                         created_by varchar(255),
                         created_date date,
                         init_time timestamp(6),
                         modified_by varchar(255),
                         update_time timestamp(6),
                         loan_ext_id varchar(255),
                         person_code varchar(255),
                         request jsonb,
                         terminal_id varchar(255),
                         primary key (id)
);
create table autopay_cards (
                               id bigint not null,
                               created_by varchar(255),
                               created_date date,
                               init_time timestamp(6),
                               modified_by varchar(255),
                               update_time timestamp(6),
                               holder varchar(255),
                               pan varchar(255),
                               token varchar(255),
                               autopay_id bigint,
                               primary key (id)
);
create index autopay_pinfl_ind
    on autopay (person_code);
create index autopay_loan_ind
    on autopay (loan_ext_id);
create index autopay_cards_token_ind
    on autopay_cards (token);
create sequence autopay_seq start with 1 increment by 1;

alter table if exists autopay_cards
    add constraint FKdtrs8umvqcem54ii0qefgd7qx
        foreign key (autopay_id)
            references autopay;