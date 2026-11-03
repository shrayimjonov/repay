ALTER TABLE public.card DROP COLUMN ip_address;

ALTER TABLE public.loan_cards ADD ip_address varchar(32) NULL;

COMMIT;