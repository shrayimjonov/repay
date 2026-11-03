create table loan_temp (
                           id bigint not null,
                           loan_ext_id varchar(255),
                           primary key (id)
);

create sequence temp_loan_seq start with 1 increment by 1;

ALTER TABLE public.loan_temp ADD CONSTRAINT loan_temp_loan_ext_id_un UNIQUE (loan_ext_id);