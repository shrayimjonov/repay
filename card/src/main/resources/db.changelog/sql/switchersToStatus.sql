


INSERT INTO public.status
(id, created_by, created_date, init_time, modified_by, update_time, code, definition, definition_uz, processing, "type")
VALUES(nextval('card_stat_seq'), 'system', '2024-01-12', '2024-01-12 15:19:38.342', 'system', '2024-01-12 15:19:38.343', '1101', 'Can''t catch card from PROCESSING', 'KARTA ISHLAMAYDI', 'HUMO', 'CARD');
INSERT INTO public.status
(id, created_by, created_date, init_time, modified_by, update_time, code, definition, definition_uz, processing, "type")
VALUES(nextval('card_stat_seq'), 'system', '2024-01-12', '2024-01-12 15:19:38.342', 'system', '2024-01-12 15:19:38.343', '1101', 'Can''t catch card from PROCESSING', 'KARTA ISHLAMAYDI', 'UZCARD', 'CARD');



INSERT INTO public.status
(id, created_by, created_date, init_time, modified_by, update_time, code, definition, definition_uz, processing, "type")
VALUES(nextval('card_stat_seq'), 'system', '2024-01-12', '2024-01-12 15:19:38.342', 'system', '2024-01-12 15:19:38.343', '1102', 'Switcher is turned off', 'UZCARDDA MUAMMO', 'UZCARD', 'CARD');
INSERT INTO public.status
(id, created_by, created_date, init_time, modified_by, update_time, code, definition, definition_uz, processing, "type")
VALUES(nextval('card_stat_seq'), 'system', '2024-01-12', '2024-01-12 15:19:38.342', 'system', '2024-01-12 15:19:38.343', '1102', 'Switcher is turned off', 'HUMODA MUAMMO', 'HUMO', 'CARD');


COMMIT;
