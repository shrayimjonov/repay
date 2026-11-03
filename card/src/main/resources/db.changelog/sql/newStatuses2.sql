INSERT INTO public.status
(id, created_by, created_date, init_time, modified_by, update_time, code, definition, definition_uz, processing, "type")
VALUES(nextval('card_stat_seq'), 'system', '2024-03-07', '2024-03-07 10:22:26.821', 'system', '2024-03-07 10:22:26.833', '1105', 'CARD_DELETED', 'O''CHIRIB YUBORILGAN', 'UZCARD', 'CARD');
INSERT INTO public.status
(id, created_by, created_date, init_time, modified_by, update_time, code, definition, definition_uz, processing, "type")
VALUES(nextval('card_stat_seq'), 'system', '2024-03-07', '2024-03-07 10:22:29.856', 'system', '2024-03-07 10:22:29.857', '1105', 'CARD_DELETED', 'O''CHIRIB YUBORILGAN', 'HUMO', 'CARD');
COMMIT;