ALTER TABLE public."transaction" ADD currency varchar(4) NULL;


ALTER TABLE public."bill" ADD currency varchar(4) NULL;

COMMIT;
