ALTER TABLE public."transaction" DROP COLUMN ip_address;

ALTER TABLE public."bill" ADD ip_address varchar(32) NULL;


COMMIT;