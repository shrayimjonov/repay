ALTER TABLE public."transaction" ADD terminal_id varchar(32) NULL;
ALTER TABLE public."transaction" ADD ip_address varchar(32) NULL;

COMMIT;