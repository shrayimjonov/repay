ALTER TABLE public.loan ADD is_insolvent bool NULL;

UPDATE loan set is_insolvent = false where is_insolvent is null;

COMMIT;