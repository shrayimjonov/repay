ALTER TABLE public.loan ADD "language" varchar(3) NULL;

UPDATE loan SET language ='UZ' WHERE language IS NULL;

COMMIT;