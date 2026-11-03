create table featured_loans
(
    loan_id      bigint not null,
    created_by   varchar(255),
    created_date date,
    init_time    timestamp(6),
    modified_by  varchar(255),
    update_time  timestamp(6),
    primary key (loan_id)
);

create index feat_loan_user_ind
    on featured_loans (created_by);


alter table if exists featured_loans
    add constraint feat_oneUserOneLoan_un unique (loan_id, created_by);


alter table if exists featured_loans
    add constraint FK5pt2mvwvw1w63dmo8cjh5622q
        foreign key (loan_id)
            references loan;

COMMIT;
