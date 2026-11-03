ALTER TABLE public.files ADD is_internal bool NULL;

UPDATE public.files set is_internal = false where is_internal is null;

COMMIT;