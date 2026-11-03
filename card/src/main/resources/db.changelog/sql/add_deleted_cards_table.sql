create table deleted_cards (
                               id bigint not null,
                               action varchar(32) check (action in ('APPROVED','DELETED')),
                               card_id bigint,
                               loan_id bigint,
                               ip_address varchar(64),
                               created_by varchar(255),
                               created_date date,
                               init_time timestamp(6),
                               modified_by varchar(255),
                               update_time timestamp(6),
                               primary key (id)
);

CREATE SEQUENCE public.del_card_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;

create index del_card_card_id_ind
    on deleted_cards (card_id);

create index del_card_loan_id_ind
    on deleted_cards (loan_id);

COMMIT;