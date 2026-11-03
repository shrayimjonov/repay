ALTER TABLE public.debt_loan ADD is_insolvent bool NULL;

UPDATE debt_loan set is_insolvent = false where is_insolvent is null;

COMMIT;