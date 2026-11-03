
ALTER TABLE public.loan ADD resident bool NULL;

ALTER TABLE public.loan ADD subject varchar(2) NULL;

COMMIT;