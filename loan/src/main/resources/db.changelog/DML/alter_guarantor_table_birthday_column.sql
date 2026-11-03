ALTER TABLE public.guarantor ALTER COLUMN birthday TYPE date USING birthday::date;

COMMIT;