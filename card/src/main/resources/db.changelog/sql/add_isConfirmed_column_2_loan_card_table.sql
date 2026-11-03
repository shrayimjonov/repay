ALTER TABLE public.loan_cards ADD is_confirmed bool NULL;
ALTER TABLE public.guarantor_cards ADD is_confirmed bool NULL;

update public.loan_cards set is_confirmed = true where is_confirmed is null;
update public.guarantor_cards set is_confirmed = true where is_confirmed is null;

COMMIT;