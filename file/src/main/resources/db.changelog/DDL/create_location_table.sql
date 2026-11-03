create table location (
                          id bigint not null,
                          description varchar(255),
                          latitude varchar(255),
                          longitude varchar(255),
                          loan_ext_id varchar(61),
                          created_by varchar(61),
                          created_date date,
                          init_time timestamp(6),
                          modified_by varchar(61),
                          update_time timestamp(6),
                          primary key (id)
);

create index location_loan_ext_id_ind
    on location (loan_ext_id);

create sequence location_seq start with 1 increment by 1;

COMMIT;