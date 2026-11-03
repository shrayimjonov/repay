SELECT
    loan_ext_id as id, created_date AS transaction_date,
    amount / 100.0 AS amount,
    processing
FROM transaction
WHERE status = 'SUCCESS'
  AND created_date >= CURRENT_DATE
  AND created_date < CURRENT_DATE + INTERVAL '1 day'
ORDER BY created_date DESC;