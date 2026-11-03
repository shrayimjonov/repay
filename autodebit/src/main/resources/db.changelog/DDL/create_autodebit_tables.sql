CREATE TABLE IF NOT EXISTS debit_cycle (
    cycle_id       VARCHAR(48)  PRIMARY KEY,
    started_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deadline       TIMESTAMPTZ  NOT NULL,
    expected_count INT          NOT NULL DEFAULT 0,
    status         VARCHAR(16)  NOT NULL DEFAULT 'RUNNING'
);
CREATE INDEX IF NOT EXISTS idx_debit_cycle_status ON debit_cycle (status, deadline);

CREATE TABLE IF NOT EXISTS debit_attempt (
    id             BIGSERIAL    PRIMARY KEY,
    loan_ext_id    VARCHAR(64)  NOT NULL,
    cycle_id       VARCHAR(48)  NOT NULL,
    branch_id      VARCHAR(32),
    debit_date     DATE         NOT NULL DEFAULT CURRENT_DATE,
    status         VARCHAR(24)  NOT NULL DEFAULT 'PROCESSING',
    failure_reason VARCHAR(256),
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ,
    CONSTRAINT uq_debit_attempt_loan_cycle UNIQUE (loan_ext_id, cycle_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_debit_attempt_active
    ON debit_attempt (loan_ext_id)
    WHERE status = 'PROCESSING';


CREATE INDEX IF NOT EXISTS idx_debit_attempt_cycle   ON debit_attempt (cycle_id, status);
CREATE INDEX IF NOT EXISTS idx_debit_attempt_loan    ON debit_attempt (loan_ext_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_debit_attempt_created ON debit_attempt (created_at);