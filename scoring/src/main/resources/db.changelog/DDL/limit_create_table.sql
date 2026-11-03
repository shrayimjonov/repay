CREATE TABLE limit_type (
                            id BIGSERIAL PRIMARY KEY,
                            type VARCHAR NOT NULL,
                            description VARCHAR,
                            pc VARCHAR,
                            init_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            created_date DATE DEFAULT CURRENT_DATE,
                            created_by VARCHAR,
                            update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            modified_by VARCHAR,
                            CONSTRAINT scoring_limit_type_pc_un UNIQUE (type, description)
);

CREATE INDEX scoring_limit_type_idx ON limit_type (type);

CREATE SEQUENCE limit_type_seq START 1 INCREMENT 1;