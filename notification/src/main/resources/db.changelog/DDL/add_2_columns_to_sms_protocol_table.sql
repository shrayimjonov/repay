ALTER TABLE public.sms_protocol ADD loan_ext_id varchar(32) NULL;
ALTER TABLE public.sms_protocol ADD ip_address varchar(32) NULL;

COMMIT;