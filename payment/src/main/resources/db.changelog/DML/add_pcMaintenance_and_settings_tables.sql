create table abs_setting
(
    id             bigint not null,
    created_by     varchar(255),
    created_date   date,
    init_time      timestamp(6),
    modified_by    varchar(255),
    update_time    timestamp(6),
    account_credit varchar(255),
    account_debit  varchar(255),
    branch_credit  varchar(255),
    branch_debit   varchar(255),
    currency       varchar(255),
    doc_type       varchar(255),
    order_queue    integer,
    pay_type       varchar(255),
    purpose        varchar(255),
    purpose_code   varchar(255),
    pc_id          bigint,
    primary key (id)
);


create table processing_maintenance
(
    id           bigint not null,
    created_by   varchar(255),
    created_date date,
    init_time    timestamp(6),
    modified_by  varchar(255),
    update_time  timestamp(6),
    processing   varchar(255),
    primary key (id)
);


create table processing_maintenance_abs_settings
(
    pcmaintenance_id bigint not null,
    abs_settings_id  bigint not null
);

alter table if exists abs_setting
    drop constraint if exists onePConeOrderNum;

alter table if exists abs_setting
    add constraint onePConeOrderNum unique (pc_id, order_queue);

alter table if exists processing_maintenance
    drop constraint if exists onePcMaintenance;

alter table if exists processing_maintenance
    add constraint onePcMaintenance unique (processing);

alter table if exists processing_maintenance_abs_settings
    drop constraint if exists UK_d7241n5nuj6qtb6c4tohf0qs7;

alter table if exists processing_maintenance_abs_settings
    add constraint UK_d7241n5nuj6qtb6c4tohf0qs7 unique (abs_settings_id);

create sequence abs_setting_seq start with 1 increment by 1;

create sequence pc_main_seq start with 1 increment by 1;

alter table if exists abs_setting
    add constraint FKodclhq951si2aqj42o6f5ui2c
        foreign key (pc_id)
            references processing_maintenance;


alter table if exists processing_maintenance_abs_settings
    add constraint FKiqwghrirv0tecr8tl7d3ltkr9
        foreign key (abs_settings_id)
            references abs_setting;


alter table if exists processing_maintenance_abs_settings
    add constraint FK85q7qtn5hw4qefwse0183g3i9
        foreign key (pcmaintenance_id)
            references processing_maintenance;