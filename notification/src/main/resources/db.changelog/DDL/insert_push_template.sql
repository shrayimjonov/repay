INSERT INTO public.sms_template (id, sms_type, text_values, created_by, created_date, init_time, modified_by,
                                 update_time)
VALUES (-1, 'PUSH', '{
  "EN": {
    "text": "According to the loan # linked to you, from card # was withdrawn # sum.",
    "valueType": {
      "1": "LOAN",
      "2": "CARD",
      "3": "AMOUNT"
    },
    "valuesCount": 3
  },
  "RU": {
    "text": "По анкете #, привязанной к Вам, с карты # было снято # сум.",
    "valueType": {
      "1": "LOAN",
      "2": "CARD",
      "3": "AMOUNT"
    },
    "valuesCount": 3
  },
  "UZ": {
    "text": "Sizga bog''langan # raqamli kredit anketasi uchun # kartadan # sum yechib olindi.",
    "valueType": {
      "1": "LOAN",
      "2": "CARD",
      "3": "AMOUNT"
    },
    "valuesCount": 3
  }
}'::jsonb, 'system', '1996-08-29', '1996-08-29 00:00:00.000', 'system', '1996-08-29 00:00:00.000');

