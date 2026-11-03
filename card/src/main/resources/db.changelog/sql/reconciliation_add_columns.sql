alter table if exists reconciliation
    add column created_by varchar(255);

alter table if exists reconciliation
    add column created_date date;

alter table if exists reconciliation
    add column init_time timestamp(6);

alter table if exists reconciliation
    add column modified_by varchar(255);

alter table if exists reconciliation
    add column update_time timestamp(6);

alter table if exists reconciliation
    add column session uuid;