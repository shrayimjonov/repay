ALTER TABLE public.featured_loans
    DROP CONSTRAINT featured_loans_pkey;

ALTER TABLE public.featured_loans
    ADD CONSTRAINT featured_loans_pkey PRIMARY KEY (loan_id, id);

COMMIT;